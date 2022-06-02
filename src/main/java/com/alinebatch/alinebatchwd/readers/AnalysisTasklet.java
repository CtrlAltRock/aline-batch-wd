package com.alinebatch.alinebatchwd.readers;

import com.alinebatch.alinebatchwd.analytics.AnalysisContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AnalysisTasklet implements Tasklet {

    AnalysisContainer analysisContainer = new AnalysisContainer();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Is writing.....");
        AnalysisContainer.postProcess();
        AnalysisContainer.write();
        return RepeatStatus.FINISHED;
    }
}
