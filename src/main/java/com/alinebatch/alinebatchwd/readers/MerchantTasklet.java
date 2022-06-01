package com.alinebatch.alinebatchwd.readers;

import com.alinebatch.alinebatchwd.analytics.AnalysisContainer;
import com.alinebatch.alinebatchwd.analytics.InTransit.TopCitiesByNumberOfOnlineMerchants;
import com.alinebatch.alinebatchwd.caches.MerchantCache;
import com.alinebatch.alinebatchwd.models.Merchant;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.util.Map;

@Slf4j
public class MerchantTasklet implements Tasklet {

    MerchantCache merchantCache = new MerchantCache();
    @Value("${merchantOut}")
    String merchantOut;
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        TopCitiesByNumberOfOnlineMerchants topCitiesByNumberOfOnlineMerchants = (TopCitiesByNumberOfOnlineMerchants)AnalysisContainer.grabProcessor(TopCitiesByNumberOfOnlineMerchants.class);

        XStream xs = new XStream();
        xs.alias("merchant",Merchant.class);
        FileOutputStream fos = new FileOutputStream("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/merchantOutput.xml",true);
        merchantCache.getAll().entrySet().stream().forEach((value) ->
        {
            xs.toXML(value.getValue(),fos);
            topCitiesByNumberOfOnlineMerchants.process(value.getValue());
        });
        return RepeatStatus.FINISHED;
    }
}
