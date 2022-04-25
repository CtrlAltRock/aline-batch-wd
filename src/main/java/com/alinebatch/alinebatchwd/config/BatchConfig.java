package com.alinebatch.alinebatchwd.config;


import com.alinebatch.alinebatchwd.models.Transaction;
import com.alinebatch.alinebatchwd.processors.TransactionProcessor;
import com.alinebatch.alinebatchwd.writers.UserXmlItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Bean
    public FlatFileItemReader<Transaction> CsvReader()
    {

        return new FlatFileItemReaderBuilder<Transaction>()
                .name("CsvItemReader")
                .resource(new FileSystemResource("/home/will/IdeaProjects/aline-batch-wd/src/main/resources/card_transaction.v1.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("user", "card", "year", "month", "day", "time", "amount", "method", "merchant_name", "merchant_city", "merchant_state", "merchant_zip", "mcc", "errors", "fraud")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>(){
                    {setTargetType(Transaction.class);}
                })
                .build();
    }

    @Bean
    public Step multiThreadedStep()
    {

        ThreadPoolTaskExecutor threadTask = new ThreadPoolTaskExecutor();
                threadTask.setCorePoolSize(6);
                threadTask.setMaxPoolSize(250);
                threadTask.afterPropertiesSet();

                return stepBuilderFactory.get("multiThreadedStep")
                        .<Transaction,Object>chunk(10000)
                        .reader(CsvReader())
                        .processor(new TransactionProcessor())
                        .writer(new UserXmlItemWriter())
                        .taskExecutor(threadTask)
                        .build();
    }

    @Bean
    public Job buildJob()
    {
        return jobBuilderFactory.get("transactionJob")
                .start(multiThreadedStep())
                .build();
    }
}
