package com.alinebatch.alinebatchwd.analytics;

import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Map;

@Slf4j
public abstract class BasicWriter<T> {

    public T basicStat;

    public abstract Class getWritableClass();
    public abstract String getValueName();
    public abstract String rootTag();
    public abstract String filePath();

    public BasicWriter(T input)
    {
        basicStat = input;
        AnalysisContainer.addBasic(this);
    }

    protected void write() throws Exception
    {
        log.info("Writing for " + rootTag());
        File output = new File(filePath());
        FileWriter fw = new FileWriter(output);
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fw.write("<" + rootTag() + ">\n");
        fw.write(" <" + getValueName() + ">");
        fw.write("" + basicStat);
        fw.write("</" + getValueName() +">\n");
        fw.write("</" + rootTag() + ">\n");
        fw.close();
    }

}
