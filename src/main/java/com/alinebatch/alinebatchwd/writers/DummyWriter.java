package com.alinebatch.alinebatchwd.writers;

import com.alinebatch.alinebatchwd.models.Card;
import com.thoughtworks.xstream.XStream;
import org.hibernate.cfg.Environment;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

public class DummyWriter implements ItemWriter<Integer> {


    public void write(List list) throws Exception {

    }
}