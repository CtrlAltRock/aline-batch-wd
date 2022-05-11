package com.alinebatch.alinebatchwd.writers;


import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.Transaction;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.alinebatch.alinebatchwd.models.User;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.util.List;

@Slf4j
public class UserXmlItemWriter extends AbstractItemStreamItemWriter {

    @Override
    public void write(List list) throws Exception {
            }
}
