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

@Slf4j
public class AnalyzerWriter implements Tasklet
{

    Analyzer analyzer = Analyzer.getStaticInstance();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        XStream xs = new XStream();
        xs.alias("Analyzer",Analyzer.class);
        xs.alias("Transaction", TransactionDTO.class);
        FileOutputStream fos = new FileOutputStream("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis.xml",true);
        xs.toXML(analyzer, fos);
        return RepeatStatus.FINISHED;
    }
}
