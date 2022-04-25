package com.alinebatch.alinebatchwd.writers;


import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.Transaction;
import com.alinebatch.alinebatchwd.models.User;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.util.List;

@Slf4j
public class UserXmlItemWriter extends AbstractItemStreamItemWriter {

    private static UserCache userCache = new UserCache();

    @Override
    public void write(List list) throws Exception {


        XStream xs = new XStream();
        xs.alias("user", User.class);
        FileOutputStream fos = new FileOutputStream("/home/will/IdeaProjects/aline-batch-wd/src/main/resources/userOutput.xml",true);
        userCache.getAll().forEach((k,v) ->{
            if (!userCache.getSeen().contains(k))
            {
                synchronized (UserCache.class)
                {
                    if (!userCache.getSeen().contains(k))
                    {
                        xs.toXML(v,fos);
                        userCache.setSeen(k);
                    }
                }
            }
        });


    }
}
