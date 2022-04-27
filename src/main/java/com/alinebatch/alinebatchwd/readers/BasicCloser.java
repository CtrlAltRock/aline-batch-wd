package com.alinebatch.alinebatchwd.readers;

import com.alinebatch.alinebatchwd.writers.XMLPrepper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class BasicCloser implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("Entering Closer Step");
        XMLPrepper.close("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/cardOutput.xml","cards");
        XMLPrepper.close("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/userOutput.xml", "users");
        return RepeatStatus.FINISHED;
    }
}