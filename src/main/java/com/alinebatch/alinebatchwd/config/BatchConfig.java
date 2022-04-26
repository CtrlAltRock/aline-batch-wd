package com.alinebatch.alinebatchwd.config;


import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.Card;
import com.alinebatch.alinebatchwd.models.Transaction;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.alinebatch.alinebatchwd.models.User;
import com.alinebatch.alinebatchwd.processors.CardCacheProcessor;
import com.alinebatch.alinebatchwd.processors.TransactionProcessor;
import com.alinebatch.alinebatchwd.processors.UserCacheProcessor;
import com.alinebatch.alinebatchwd.readers.UserCacheReader;
import com.alinebatch.alinebatchwd.writers.CardItemWriter;
import com.alinebatch.alinebatchwd.writers.GeneralXmlWriter;
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

import java.util.HashMap;
import java.util.HashSet;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Bean
    public FlatFileItemReader<TransactionDTO> CsvReader()
    {

        return new FlatFileItemReaderBuilder<TransactionDTO>()
                .name("CsvItemReader")
                .resource(new FileSystemResource("/Users/willemduiker/Documents/card_transaction.v1.csv"))
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
                        .taskExecutor(threadTask)
                        .build();
    }

    @Bean
    public Step UserCacheStep()
    {
        ThreadPoolTaskExecutor threadTask = new ThreadPoolTaskExecutor();
        threadTask.setCorePoolSize(12);
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
    public Job buildJob()
    {
        return jobBuilderFactory.get("transactionJob")
                .start(multiThreadedStep())
                .next(UserCacheStep())
                .next(CardCacheStep())
                .build();
    }
}
