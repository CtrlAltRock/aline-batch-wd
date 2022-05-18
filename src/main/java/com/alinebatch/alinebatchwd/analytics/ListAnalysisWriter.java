package com.alinebatch.alinebatchwd.analytics;

import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public abstract class ListAnalysisWriter<T> {

    public ArrayList<T> analysisList = new ArrayList<>();

    public ListAnalysisWriter()
    {
        Class loggingClass = this.getClass();
        log.info("In Constructor for " + loggingClass.getName());
        AnalysisContainer.addList(this);
    }

    public abstract Class getValueClass();

    public abstract String rootTag();
    public abstract String valueName();
    public abstract String filePath();

    protected void write() throws Exception
    {
        log.info("Writing for " + rootTag());
        File output = new File(filePath());
        FileWriter fw = new FileWriter(output);
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fw.close();
        XStream xs = new XStream();
        FileOutputStream fos = new FileOutputStream(output, true);
        xs.alias(valueName(), getValueClass());
        xs.alias(rootTag(), List.class);
        xs.toXML(analysisList, fos);
    }
}
