package com.alinebatch.alinebatchwd.generators;

import com.alinebatch.alinebatchwd.caches.CardCache;
import com.alinebatch.alinebatchwd.caches.MerchantCache;
import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

@Component
@Slf4j
public class GeneratorBean {

    private static UserCache userCache = new UserCache();

    private static CardCache cardCache = new CardCache();

    private static MerchantCache merchantCache = new MerchantCache();

    UserGenerator userGenerator = new UserGenerator();

    CardGenerator cardGenerator = new CardGenerator();

    MerchantGenerator merchantGenerator = new MerchantGenerator();

    private static int userCount = 0;
    private static int cardCount = 0;

    public UserDTO getUser(long id) throws Exception {
        //check if we need to generate more users
        if (id % 1000 == 0 && id >= userCache.count()) {
            synchronized (userCache)
            {
                if (id % 1000 == 0 && id >= userCache.count())
                {
                    log.info("Generating Block of Users");
                    for (long i = id; i < id + 1000; i ++)
                    {
                        UserDTO user = userGenerator.generateUser(i);
                        userCache.set(i,user);
                        for (long j = 0; j < 10; j ++)
                        {
                            CardDTO card = cardGenerator.generateCard(i,j);
                            cardCache.set(i,j,card);
                        }
                    }
                }
            }
        }
        userCache.checkLatest(id);
        return userCache.get(id);
    }

    public CardDTO getCard(long userId, long cardId) throws Exception {
        if (cardCache.get(userId, cardId) == null) {
            synchronized (cardCache) {
                if (cardCache.get(userId, cardId) == null) {
                        log.info("Generating new card");
                        CardDTO card = cardGenerator.generateCard(userId, cardId);
                        cardCache.set(userId, cardId, card);
                        return card;
                }
            }
        }
        return cardCache.get(userId, cardId);
    }

    public Merchant getMerchant(String name, TransactionDTO transaction) throws Exception
    {
        if (merchantCache.get(name) == null)
        {
            synchronized (merchantCache) {
                if (merchantCache.get(name) == null)
                {
                    Merchant m = merchantGenerator.generateMerchant((long)merchantCache.getTotal(),transaction.getMerchant_state()
                    ,transaction.getMerchant_city()
                    ,transaction.getMerchant_zip());
                    merchantCache.set(name, m, m.getId());
                }
            }
        }
        return merchantCache.get(name);
    }
}
