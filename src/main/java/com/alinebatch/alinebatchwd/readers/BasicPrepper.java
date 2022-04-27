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

@Slf4j
public class BasicPrepper implements Tasklet{

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("Entering Prep Step");
        XMLPrepper.prep("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/cardOutput.xml","cards");
        XMLPrepper.prep("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/userOutput.xml", "users");
        return RepeatStatus.FINISHED;
    }
}
