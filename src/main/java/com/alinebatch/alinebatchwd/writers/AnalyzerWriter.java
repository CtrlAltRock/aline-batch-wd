package com.alinebatch.alinebatchwd.writers;

import com.alinebatch.alinebatchwd.analytics.Analyzer;
import com.alinebatch.alinebatchwd.analytics.Querier;
import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.alinebatch.alinebatchwd.models.User;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AnalyzerWriter implements Tasklet
{

    Analyzer analyzer = Analyzer.getStaticInstance();

    UserCache userCache = new UserCache();

    @Value("${TopTransactions}")
    int topTransactions;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        analyzer.calculatePercentages();
        XStream xs = new XStream();
        xs.alias("Analyzer",Analyzer.class);
        xs.omitField(Analyzer.class, "errorMap");
        xs.omitField(Analyzer.class,"hadInsufficientBalance");
        xs.omitField(Analyzer.class,"inLoop");
        xs.omitField(Analyzer.class,"yearMap");
        xs.omitField(Analyzer.class,"noFraud");
        xs.omitField(Analyzer.class,"transactions");
        xs.omitField(Analyzer.class, "noFraudMapYear");
        xs.omitField(Analyzer.class, "fraudMapYear");
        xs.omitField(Analyzer.class,"zipMap");
        xs.aliasField("Number_Of_Deposits" ,Analyzer.class,"deposits");
        xs.aliasField("Number_Of_Merchants" ,Analyzer.class,"merchants");
        xs.aliasField("Number_Of_Users" ,Analyzer.class,"users");
        xs.aliasField("Percent_of_Users_with_Insufficent_Balance" ,Analyzer.class,"percentOfUsersWithInsufficientBalance");
        xs.aliasField("Percent_of_Users_with_Insufficent_Balance_More_Than_Once" ,Analyzer.class,"percentOfUsersWithInsufficientBalanceMoreThanOnce");
        xs.aliasField("Top_" + topTransactions + "_Ordered_By_Value" ,Analyzer.class,"largestTransactions");
        xs.aliasField("Total_Transactions_Grouped_By_State_That_Had_No_Fraud", Analyzer.class,"noFraudMapState");
        xs.alias("Transaction", TransactionDTO.class);

        FileOutputStream fos = new FileOutputStream("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis.xml",true);
        xs.toXML(analyzer, fos);
        return RepeatStatus.FINISHED;
    }
}
