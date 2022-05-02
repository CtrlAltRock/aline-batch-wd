package com.alinebatch.alinebatchwd.readers;

import com.alinebatch.alinebatchwd.writers.XMLPrepper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class BasicPrepper implements Tasklet{

    @Value("${merchantOut}")
    String merchantOut;

    @Value("${stateOut}")
    String stateOut;

    @Value("${cardOut}")
    String cardOut;

    @Value("${userOut}")
    String userOut;

    @Value("${analysisOut}")
    String analysisOut;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("Entering Prep Step");
        XMLPrepper.prep(cardOut,"cards");
        XMLPrepper.prep(userOut, "users");
        XMLPrepper.prep(stateOut, "states");
        XMLPrepper.prep(merchantOut, "merchants");
        XMLPrepper.prep(analysisOut, "analysis");
        return RepeatStatus.FINISHED;
    }
}
