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
import org.springframework.batch.core.step.tasklet.TaskletStep;
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
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public Step CardCacheStep()
    {
        ThreadPoolTaskExecutor threadTask = new ThreadPoolTaskExecutor();
            threadTask.setMaxPoolSize(100);
            threadTask.setCorePoolSize(6);
            threadTask.afterPropertiesSet();

            return stepBuilderFactory.get("cardCacheStep")
                    .<User, HashMap<Long, Card>>chunk(10000)
                    .reader(new UserCacheReader<>(UserCache.getInstance().getAll()))
                    .processor(new CardCacheProcessor())
                    .writer(new CardItemWriter())
                    .taskExecutor(threadTask)
                    .build();
    }

    @Bean
    public Step multiThreadedStep()
    {

        ThreadPoolTaskExecutor threadTask = new ThreadPoolTaskExecutor();
                threadTask.setCorePoolSize(6);
                threadTask.setMaxPoolSize(100);
                threadTask.afterPropertiesSet();

                return stepBuilderFactory.get("multiThreadedStep")
                        .<TransactionDTO,Object>chunk(10000)
                        .reader(CsvReader())
                        .processor(new TransactionProcessor())
                        .writer(new UserXmlItemWriter())
                        .faultTolerant()
                        .skipLimit(100)
                        .skip(ParameterMappingException.class)
                        .skip(FlatFileFormatException.class)
                        .skip(FlatFileParseException.class)
                        .taskExecutor(threadTask)
                        .build();
    }

    @Bean
    public Step StateCacheStep()
    {
        ThreadPoolTaskExecutor threadTask = new ThreadPoolTaskExecutor();
        threadTask.setCorePoolSize(6);
        threadTask.setMaxPoolSize(250);
        threadTask.afterPropertiesSet();

        return stepBuilderFactory.get("stateCacheStep")
                .<State, Object>chunk(10000)
                .reader(new StateCacheReader<State>(StateCache.getInstance().getAll().values().iterator()))
                .processor(new StateCacheProcessor())
                .writer(new StateItemWriter())
                .taskExecutor(threadTask)
                .build();
    }

    @Bean
    public Step UserCacheStep()
    {
        ThreadPoolTaskExecutor threadTask = new ThreadPoolTaskExecutor();
        threadTask.setCorePoolSize(6);
        threadTask.setMaxPoolSize(250);
        threadTask.afterPropertiesSet();

        return stepBuilderFactory.get("userCacheStep")
                .<User, Object>chunk(10000)
                .reader(new UserCacheReader<>(UserCache.getInstance().getAll()))
                .processor(new UserCacheProcessor())
                .writer(new GeneralXmlWriter())
                .taskExecutor(threadTask)
                .build();
    }

    @Bean
    public Step MerchantCacheStep()
    {
        ThreadPoolTaskExecutor threadTask = new ThreadPoolTaskExecutor();
        threadTask.setCorePoolSize(6);
        threadTask.setMaxPoolSize(250);
        threadTask.afterPropertiesSet();

        return stepBuilderFactory.get("merchantCacheStep")
                .<Merchant, Merchant>chunk(10000)
                .reader(new MerchantCacheReader<Merchant>(new MerchantCache()))
                //.processor(new MerchantCacheProcessor())
                .writer(new MerchantCacheWriter())
                .taskExecutor(threadTask)
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
    public Job buildJob()
    {
        return jobBuilderFactory.get("transactionJob")
                .start(PrepStep())
                .next(multiThreadedStep())
                .next(MerchantCacheStep())
                .next(UserCacheStep())
                .next(CardCacheStep())
                .next(StateCacheStep())
                .next(CloseStep())
                .build();
    }
}
