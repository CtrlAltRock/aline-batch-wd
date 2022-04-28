package com.alinebatch.alinebatchwd.writers;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class XMLPrepper {

    public static void prep(String path, String rootName)
    {

        try {
            FileWriter xmlWriter = new FileWriter(path);
            xmlWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlWriter.append("<"+ rootName + ">\n");
            xmlWriter.close();
        } catch (IOException e)
        {
            log.info("Something went wrong, please check your environment and run again.");
        }
    }

    public static void close(String path, String rootName)
    {
        try {
            FileWriter xmlWriter = new FileWriter(path,true);
            xmlWriter.append("\n</" + rootName + ">");
            xmlWriter.close();
        } catch (IOException e)
        {
            log.info("Right at the moment of completion, failure has occurred. Inevitably, this disappointment will irreparably change us for the worst. Good luck.");
        }
    }

}
