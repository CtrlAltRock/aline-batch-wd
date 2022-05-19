package com.alinebatch.alinebatchwd.writers;

import com.alinebatch.alinebatchwd.models.Card;
import com.alinebatch.alinebatchwd.models.CardDTO;
import com.alinebatch.alinebatchwd.models.User;
import com.thoughtworks.xstream.XStream;
import org.hibernate.cfg.Environment;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

public class CardItemWriter extends AbstractItemStreamItemWriter {

    @Autowired
    private Environment environment;

    @Value("${cardOut}")
    String cardOut;

    @Override
    public void write(List list) throws Exception {
        XStream xs = new XStream();
        xs.alias("card", CardDTO.class);
        FileOutputStream fos = new FileOutputStream("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/cardOutput.xml",true);
        list.forEach((t) -> {
            HashMap<Long, CardDTO> hm = (HashMap<Long, CardDTO>)t;
            hm.forEach((k, v) -> {
                xs.toXML(v,fos);
            });

        });
    }
}
