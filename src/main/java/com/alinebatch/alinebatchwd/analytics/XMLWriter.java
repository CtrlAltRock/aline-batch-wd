package com.alinebatch.alinebatchwd.analytics;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.result.Output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class XMLWriter {

    public static void write(AnalysisWrite<?,?> input) throws Exception
    {

        log.info("Writing for " + input.rootTag());
        File output = new File(input.filePath());
        FileWriter fw = new FileWriter(output);
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fw.write("<" + input.rootTag() + ">\n");
        input.analysisMap.forEach((key, value) -> {
            try {
                fw.write(" <entry>\n");
                fw.write("  <" + input.keyName() + ">" + key + "</" + input.keyName() + ">\n");
                fw.write("  <" + input.valueName() + ">" + value + "</" + input.valueName() + ">\n");
                fw.write(" </entry>\n");
            } catch (IOException e)
            {
                log.info(e.getMessage());
            }
        });
        fw.close();
    }

    public static void write(ArrayList input, String fileName)
    {

    }
}
