package com.alinebatch.alinebatchwd.generators;

import com.alinebatch.alinebatchwd.caches.CardCache;
import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.Card;
import com.alinebatch.alinebatchwd.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Component
@Slf4j
public class GeneratorBean {

    private static String baseUrl = "http://localhost:8085";

    private static UserCache userCache = new UserCache();

    private static CardCache cardCache = new CardCache();

    private static int userCount = 0;
    private static int cardCount = 0;

    public User getUser(long id) throws Exception {
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
                        User user = new ObjectMapper().readValue(input, User.class);
                        userCache.set(id, user);
                        log.info("User Count: " + (++userCount));
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
                        cardCache.set(userId, cardId, card);
                        log.info("Card Count:" + (++cardCount));
                        return card;
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }
            }
        }
        return cardCache.get(userId, cardId);
    }
}
