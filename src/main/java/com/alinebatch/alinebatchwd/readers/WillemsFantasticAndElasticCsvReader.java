package com.alinebatch.alinebatchwd.readers;


import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class WillemsFantasticAndElasticCsvReader<T> implements ItemReader<T> {

    public Class[] classList;

    public Integer skipLines;

    public void setSkipLines(Integer num)
    {
        this.skipLines = num;
    }

    public void setMetaDataFinder(Class[] classList)
    {
        this.classList = classList;
    }

    public void slantCompareString(String entry, String compareTo)
    {

    }

    public void setMetaDataLine()
    {}


    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return null;
    }
}
