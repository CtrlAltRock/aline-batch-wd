package com.alinebatch.alinebatchwd.writers;

import com.alinebatch.alinebatchwd.models.Merchant;
import com.alinebatch.alinebatchwd.models.State;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.util.List;

@Slf4j
public class MerchantCacheWriter extends AbstractItemStreamItemWriter {

    @Value("${merchantOut}")
    String merchantOut;

    @Override
    public void write(List list) throws Exception {
        XStream xs = new XStream();
        xs.alias("merchant", Merchant.class);
        FileOutputStream fos = new FileOutputStream("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/merchantOutput.xml",true);
        list.forEach((t) -> {
            synchronized (XStream.class)
            {
                xs.toXML(t, fos);
            }
        });
    }
}
