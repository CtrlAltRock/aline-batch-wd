package com.alinebatch.alinebatchwd.writers;

import com.alinebatch.alinebatchwd.models.CardDTO;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.alinebatch.alinebatchwd.models.User;
import com.alinebatch.alinebatchwd.models.UserDTO;
import com.thoughtworks.xstream.XStream;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.util.List;

public class GeneralXmlWriter extends AbstractItemStreamItemWriter {

    @Value("${userOut}")
    String userOut;

    @Override
    public void write(List list) throws Exception {
        XStream xs = new XStream();
        xs.alias("user", UserDTO.class);
        xs.omitField(User.class,"deposits");
        xs.alias("card", CardDTO.class);
        FileOutputStream fos = new FileOutputStream("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/userOutput.xml",true);
        list.forEach((t) -> {
            xs.toXML(t,fos);
        });
    }
}
