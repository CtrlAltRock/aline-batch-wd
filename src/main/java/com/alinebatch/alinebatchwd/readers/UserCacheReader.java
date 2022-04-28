package com.alinebatch.alinebatchwd.readers;

import com.alinebatch.alinebatchwd.caches.UserCache;

import com.alinebatch.alinebatchwd.models.User;
import org.springframework.batch.item.ItemReader;

import java.util.AbstractMap;


public class UserCacheReader<T> implements ItemReader<T> {

    AbstractMap<Long, T> list;
    Long currentIndex = 0L;
    private static final String CURRENT_INDEX = "current.index";

    public UserCacheReader(AbstractMap<Long,T> inputCache)
    {
        this.list = inputCache;
    }


    public T read() throws Exception
    {
        if (currentIndex < list.size())
        {
            return list.get(currentIndex++);

        }
        return null;
    }

}
