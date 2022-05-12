package com.alinebatch.alinebatchwd.caches;


import com.alinebatch.alinebatchwd.models.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MerchantCache {

   public static MerchantCache instance = null;


   public static MerchantCache getInstance()
   {
               if (instance == null)
               {
                   instance = new MerchantCache();
               }
       return instance;
   }

   public HashMap<String, Merchant> merchantMap = new HashMap<>();

   public Merchant get(String name)
   {
       return MerchantCache.getInstance().merchantMap.get(name);
   }

   public Merchant set(String name, Merchant merchant, Long id)
   {
       MerchantCache.getInstance().merchantMap.put(name, merchant);
       return merchant;
   }

   public AbstractMap<String, Merchant> getAll()
   {
       return MerchantCache.getInstance().merchantMap;
   }

}
