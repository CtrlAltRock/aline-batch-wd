package com.alinebatch.alinebatchwd.caches;


import com.alinebatch.alinebatchwd.models.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;

import javax.validation.constraints.Null;
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

   public HashMap<String, Merchant> merchantMap = new HashMap<>();

   public static Merchant get(String name)
   {
       return MerchantCache.getInstance().merchantMap.get(name);
   }

   public static void toggleIb(String name)
   {
       try {
           getInstance().merchantMap.get(name).setHadIb(true);
       } catch (NullPointerException e)
       {
           //thread was behind and the toggle was not able to complete.
           log.info("Caught Null Pointer, retrying in toggleIb()");
           toggleIb(name);
       }
   }

    public static void toggleErrors(String name)
    {
        try {

            getInstance().merchantMap.get(name).setHadErrors(true);
        } catch (NullPointerException e)
        {
            log.info("Caught Null Pointer, retrying in SetErrors()");
            toggleErrors(name);
        }
    }

    public static void toggleOnline(String name)
    {
        try
        {
            getInstance().merchantMap.get(name).setHadOnline(true);
        } catch (NullPointerException e)
        {
            log.info("Caught Null Pointer, retrying in toggleOnline()");
            toggleOnline(name);
        }
    }

   public static Merchant set(String name, Merchant merchant, Long id)
   {
       getInstance().merchantMap.put(name, merchant);
       return merchant;
   }

   public static int getTotal()
   {
        return getInstance().merchantMap.size();
   }

   public static AbstractMap<String, Merchant> getAll()
   {
       return getInstance().merchantMap;
   }

}
