package com.alinebatch.alinebatchwd.writers;

import com.alinebatch.alinebatchwd.models.State;
import com.alinebatch.alinebatchwd.models.User;
import com.thoughtworks.xstream.XStream;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.util.List;

public class StateItemWriter extends AbstractItemStreamItemWriter {
    //does not seem to be thread safe
    @Value("${stateOut}")
    String stateOut;

    @Override
    public void write(List list) throws Exception {
        XStream xs = new XStream();
        xs.alias("state", State.class);
        FileOutputStream fos = new FileOutputStream("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/stateOutput.xml",true);
        list.forEach((t) -> {
            synchronized (XStream.class)
            {
                xs.toXML(t, fos);
            }
        });
    }
}
