package com.alinebatch.alinebatchwd.writers;

import com.alinebatch.alinebatchwd.analytics.Analyzer;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AnalyzerWriter implements Tasklet
{

    Analyzer analyzer = Analyzer.getStaticInstance();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        analyzer.calculatePercentages();
        XStream xs = new XStream();
        xs.alias("Analyzer",Analyzer.class);
        xs.omitField(Analyzer.class, "errorMap");
        xs.omitField(Analyzer.class,"hadInsufficientBalance");
        xs.omitField(Analyzer.class,"inLoop");
        xs.aliasField("Number_Of_Deposits" ,Analyzer.class,"deposits");
        xs.aliasField("Number_Of_Merchants" ,Analyzer.class,"merchants");
        xs.aliasField("Number_Of_Users" ,Analyzer.class,"users");
        xs.aliasField("Percent_of_Users_with_Insufficent_Balance" ,Analyzer.class,"percentOfUsersWithInsufficientBalance");
        xs.aliasField("Percent_of_Users_with_Insufficent_Balance_More_Than_Once" ,Analyzer.class,"percentOfUsersWithInsufficientBalanceMoreThanOnce");
        xs.aliasField("Top_10_Largest_Transactions" ,Analyzer.class,"largestTransactions");
        xs.alias("Transaction", TransactionDTO.class);

        FileOutputStream fos = new FileOutputStream("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis.xml",true);
        xs.toXML(analyzer, fos);
        return RepeatStatus.FINISHED;
    }
}
