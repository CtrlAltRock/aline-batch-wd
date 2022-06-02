package com.alinebatch.alinebatchwd.analytics;

import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AnalysisWrite<T,U> {

    public ConcurrentHashMap<T,U> analysisMap = new ConcurrentHashMap<>();

    public AnalysisWrite()
    {
        Class loggingClass = this.getClass();
        log.info("In Constructor for " + loggingClass.getName());
        AnalysisContainer.add(this);
    }

    public abstract Class getKeyClass();
    public abstract Class getValueClass();

    public abstract String keyName();
    public abstract String valueName();
    public abstract String rootTag();
    public abstract String filePath();


    protected void write() throws Exception
    {
        XMLWriter.write(this);
        analysisMap = new ConcurrentHashMap<>();
    }
}
