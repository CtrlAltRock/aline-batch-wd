package com.alinebatch.alinebatchwd.readers;


import com.alinebatch.alinebatchwd.caches.MerchantCache;
import com.alinebatch.alinebatchwd.models.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MerchantCacheReader<T> implements ItemReader<T> {


    Long index = 0L;

    MerchantCache merchantCache;

    public MerchantCacheReader(MerchantCache merchantCache) {this.merchantCache = merchantCache;}

    public T read() throws Exception
    {
        while (index < merchantCache.getAll().size())
        {
            Merchant m = merchantCache.getById(index);
            index += 1L;
            return (T)m;
        }
        return null;
    }
}
