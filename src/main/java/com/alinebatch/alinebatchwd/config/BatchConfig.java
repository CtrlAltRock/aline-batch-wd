package com.alinebatch.alinebatchwd.config;


import com.alinebatch.alinebatchwd.caches.MerchantCache;
import com.alinebatch.alinebatchwd.caches.StateCache;
import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.*;
import com.alinebatch.alinebatchwd.processors.*;
import com.alinebatch.alinebatchwd.readers.*;
import com.alinebatch.alinebatchwd.writers.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.invoke.ParameterMappingException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.HashMap;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Value("${inFile}")
    String inFilePath;

    @Bean
    TaskExecutor multiThreadedExecutor()
    {
        ThreadPoolTaskExecutor threadTask = new ThreadPoolTaskExecutor();
        threadTask.setMaxPoolSize(16);
        threadTask.setCorePoolSize(8);
        threadTask.afterPropertiesSet();
        return threadTask;
    }

    @Bean
    TaskExecutor basicTaskExecutor()
    {
        return new SimpleAsyncTaskExecutor("SimpleAsyncTask");
    }

    //Multi Threaded Steps


    @Bean
    public ItemWriter<Card> cardWriter()
    {
        Resource exportFileResource = new FileSystemResource("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/cardOutput.xml");

        XStreamMarshaller cardMarshaller = new XStreamMarshaller();
        cardMarshaller.setAliases(Collections.singletonMap("card",Card.class));
        return new StaxEventItemWriterBuilder<Card>()
                .name("cardWriter")
                .resource(exportFileResource)
                .marshaller(cardMarshaller)
                .rootTagName("cards")
                .build();
    }

    @Bean
    public FlatFileItemReader<TransactionDTO> CsvReader()
    {

        return new FlatFileItemReaderBuilder<TransactionDTO>()
                .name("CsvItemReader")
                .resource(new FileSystemResource(inFilePath))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("user", "card", "year", "month", "day", "time", "amount", "method", "merchant_name", "merchant_city", "merchant_state", "merchant_zip", "mcc", "errors", "fraud")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<TransactionDTO>(){
                    {setTargetType(TransactionDTO.class);}
                })
                .build();
    }



    @Bean
    public Step multiThreadedStep()
    {
                return stepBuilderFactory.get("multiThreadedStep")
                        .<TransactionDTO,Object>chunk(10000)
                        .reader(CsvReader())
                        .processor(new TransactionProcessor())
                        .writer(new UserXmlItemWriter())
                        .faultTolerant()
                        .skipLimit(1)
                        .retry(Exception.class)
                        .retryLimit(1)
                        .skip(ParameterMappingException.class)
                        .skip(FlatFileFormatException.class)
                        .skip(FlatFileParseException.class)
                        .taskExecutor(multiThreadedExecutor())
                        .build();
    }

    //Flow Definition
    //writer flow
    @Bean
    public Flow aggregateFLow() {
        return new FlowBuilder<SimpleFlow>("aggregateFlow")
                .split(multiThreadedExecutor())
                .add(UserFlow(),CardFlow(),MerchantFlow(),StateFlow())
                .build();
    }

    @Bean
    public Flow UserFlow() {
        return new FlowBuilder<SimpleFlow>("UserFlow")
                .start(UserCacheStep())
                .build();
    }

    @Bean
    public Flow CardFlow() {
        return new FlowBuilder<SimpleFlow>("CardFlow")
                .start(CardCacheStep())
                .build();
    }

    @Bean
    public Flow MerchantFlow() {
        return new FlowBuilder<SimpleFlow>("MerchantFlow")
                .start(MerchantCacheStep())
                .build();
    }

    @Bean
    public Flow StateFlow() {
        return new FlowBuilder<SimpleFlow>("StateFlow")
                .start(StateCacheStep())
                .build();
    }


    //single threaded Steps
    @Bean
    public Step CardCacheStep()
    {
        return stepBuilderFactory.get("cardCacheStep")
                .<UserDTO, HashMap<Long, CardDTO>>chunk(1000)
                .reader(new UserCacheReader<>(UserCache.getInstance().getAll()))
                .processor(new CardCacheProcessor())
                .writer(new CardItemWriter())
                .taskExecutor(basicTaskExecutor())
                .build();
    }

    @Bean
    public Step StateCacheStep()
    {

        return stepBuilderFactory.get("stateCacheStep")
                .<State, Object>chunk(1000)
                .reader(new StateCacheReader<State>(StateCache.getInstance().getAll().values().iterator()))
                .processor(new StateCacheProcessor())
                .writer(new StateItemWriter())
                .taskExecutor(basicTaskExecutor())
                .build();
    }

    @Bean
    public Step UserCacheStep()
    {
        return stepBuilderFactory.get("userCacheStep")
                .<UserDTO, Object>chunk(1000)
                .reader(new UserCacheReader<>(UserCache.getInstance().getAll()))
                .processor(new UserAggregateProcessor())
                .writer(new GeneralXmlWriter())
                .taskExecutor(basicTaskExecutor())
                .build();
    }

    @Bean
    public Step MerchantCacheStep()
    {
        return stepBuilderFactory.get("merchantCacheStep")
                .tasklet(new MerchantTasklet())
                .build();
    }

    @Bean
    public Step PrepStep()
    {
        return stepBuilderFactory
                .get("prepareXML")
                .tasklet(basicPrepper())
                .build();
    }

    @Bean
    public Step analysisStep()
    {
        return stepBuilderFactory
                .get("calculate_analysis")
                .tasklet(new AnalysisTasklet())
                .build();
    }

    @Bean
    public Step analysisWrite()
    {
        return stepBuilderFactory
                .get("write_analysis")
                .tasklet(new AnalyzerWriter())
                .build();
    }



    @Bean
    public Step CloseStep()
    {
        return stepBuilderFactory
                .get("closeXML")
                .tasklet(basicCloser())
                .build();
    }

    @Bean
    public BasicPrepper basicPrepper()
    {
        return new BasicPrepper();
    }

    @Bean
    public BasicCloser basicCloser()
    {
        return new BasicCloser();
    }

    @Bean
    public AnalyzerWriter doAnalysis() { return new AnalyzerWriter();}

    @Bean
    public Job buildJob()
    {
        return jobBuilderFactory.get("transactionJob")
                .flow(PrepStep())
                .next(multiThreadedStep())
                .next(aggregateFLow())
                .next(analysisStep())
                .next(analysisWrite())
                .next(CloseStep())
                .end()
                .build();
    }
}
