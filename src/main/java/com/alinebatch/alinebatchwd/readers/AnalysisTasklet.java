package com.alinebatch.alinebatchwd.readers;

import com.alinebatch.alinebatchwd.analytics.Analyzer;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class AnalysisTasklet implements Tasklet {

    Analyzer analyzer = new Analyzer();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        analyzer.calculateUserAnalysis();
        analyzer.calculateTransactionAnalysis();
        return RepeatStatus.FINISHED;
    }
}
