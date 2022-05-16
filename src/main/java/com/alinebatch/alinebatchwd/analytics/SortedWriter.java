package com.alinebatch.alinebatchwd.analytics;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public abstract class SortedWriter<T,U> extends AnalysisWrite<T,U>{

    @XStreamOmitField
    public HashMap<T,U> unsortedTally = new HashMap<>();

    private int count;

    public SortedWriter(int count)
    {

    }

    public abstract ArrayList<String> sort(HashMap<T,U> toSort);

    @Override
    public void write() throws Exception
    {
        ArrayList<String> sorted = sort(unsortedTally);
        log.info("Writing for " + rootTag());
        File output = new File(filePath());
        FileWriter fw = new FileWriter(output);
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fw.write("<" + rootTag() + ">\n");
        sorted.forEach((value) ->
        {
            String[] split = value.split(" ");
            String key = split[0];
            String v = split[1];
            try
            {
                fw.write(" <entry>\n");
                fw.write("  <" + keyName() + ">" + key + "</" + keyName()+">\n");
                fw.write("  <" + valueName() + ">" + v + "</" + valueName() + ">\n");
                fw.write(" </entry>\n");
            } catch (Exception e)
            {
                log.info("Write failed for value: " + value);
            }
        });
        fw.write("</" + rootTag() + ">");
        fw.close();
    }
}
