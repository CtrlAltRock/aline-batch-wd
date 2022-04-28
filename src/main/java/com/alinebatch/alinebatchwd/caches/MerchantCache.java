package com.alinebatch.alinebatchwd.caches;


import com.alinebatch.alinebatchwd.models.Merchant;
import com.alinebatch.alinebatchwd.readers.MerchantCacheReader;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MerchantCache {

   public static MerchantCache instance = null;

   public static MerchantCache getInstance()
   {
       if (instance == null)
       {
           synchronized (MerchantCache.class)
           {
               if (instance == null)
               {
                   instance = new MerchantCache();
               }
           }
       }
       return instance;
   }

   private AbstractMap<String, Merchant> merchantMap = new ConcurrentHashMap<>();
   private HashMap<Long, String> merchantIndex = new HashMap<>();

   public Merchant get(String name)
   {
       return MerchantCache.getInstance().merchantMap.get(name);
   }

   public Merchant set(String name, Merchant merchant, Long id)
   {
       MerchantCache.getInstance().merchantMap.put(name, merchant);
       MerchantCache.getInstance().merchantIndex.put(id,name);
       return merchant;
   }

   public Merchant getById(Long id)
   {
       String name = MerchantCache.getInstance().merchantIndex.get(id);
       return MerchantCache.getInstance().merchantMap.get(name);
   }

   public AbstractMap<String, Merchant> getAll()
   {
       return MerchantCache.getInstance().merchantMap;
   }

}
