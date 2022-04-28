package com.alinebatch.alinebatchwd.readers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

import java.util.AbstractMap;
import java.util.Iterator;

@Slf4j
public class StateCacheReader<T> implements ItemReader<T> {

    Iterator<T> list;

    public StateCacheReader(Iterator<T> inputCache) {this.list = inputCache;}

    public T read() throws Exception
    {
        if (list.hasNext())
        {
            return list.next();
        }

        return null;
    }
}
