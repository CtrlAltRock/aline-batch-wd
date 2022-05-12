package com.alinebatch.alinebatchwd.generators;

import com.alinebatch.alinebatchwd.analytics.Analyzer;
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

    private static String baseUrl = "http://localhost:8085";

    private static UserCache userCache = new UserCache();

    private static CardCache cardCache = new CardCache();

    private static MerchantCache merchantCache = new MerchantCache();

    private static int userCount = 0;
    private static int cardCount = 0;

    private Analyzer analyzer = new Analyzer();

    public UserDTO getUser(long id) throws Exception {
        if (userCache.get(id) == null) {
            synchronized (UserCache.class) {
                if (userCache.get(id) == null) {
                    try {
                        URL url = new URL(baseUrl + "/users/generate/user/" + id);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String input;
                        input = in.readLine();
                        UserDTO user = new ObjectMapper().readValue(input, UserDTO.class);
                        user.setIbCount(0);
                        userCache.set(id, user);
                        log.info(id + "");
                        return user;
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }
            }
        }
        return userCache.get(id);
    }

    public Card getCard(long userId, long cardId) throws Exception {
        if (cardCache.get(userId, cardId) == null) {
            synchronized (CardCache.class) {
                if (cardCache.get(userId, cardId) == null) {
                    try {
                        URL url = new URL(baseUrl + "/users/generate/card/" + userId + "/" + cardId);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String input;
                        input = in.readLine();
                        Card card = new ObjectMapper().readValue(input, Card.class);
                        card.setUserId(userId);
                        cardCache.set(userId, cardId, card);
                        return card;
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }
            }
        }
        return cardCache.get(userId, cardId);
    }

    public Merchant getMerchant(String name, TransactionDTO transaction) throws Exception
    {
        if (merchantCache.get(name) == null)
        {
            synchronized (MerchantCache.class) {
                if (merchantCache.get(name) == null)
                {
                    URL url = new URL(baseUrl + "/merchants/generate/merchant/" + name);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String input;
                    input = in.readLine();
                    Merchant m = new ObjectMapper().readValue(input, Merchant.class);
                    m.setCity(transaction.getMerchant_city());
                    m.setZip(transaction.getMerchant_zip());
                    m.setState(transaction.getMerchant_state());
                    merchantCache.set(name, m, m.getId());
                    analyzer.addMerchant();
                }
            }
        }
        return merchantCache.get(name);
    }
}
