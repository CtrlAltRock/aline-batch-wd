package com.alinebatch.alinebatchwd.caches;

import com.alinebatch.alinebatchwd.models.Card;
import org.aspectj.lang.reflect.CatchClauseSignature;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class CardCache {

    public static CardCache instance = null;


    public static CardCache getInstance()
    {
        if (instance == null)
        {
            instance = new CardCache();
        }
        return instance;
    }

    private AbstractMap<Long, HashMap<Long, Card>> cacheMap = new ConcurrentHashMap<>();

    private HashSet<String> seenCache = new HashSet<>();

    public HashMap<Long, Card> getAll(Long userId)
    {
        return CardCache.getInstance().cacheMap.get(userId);
    }

    public Card get(Long userId, Long cardId) {
        if (CardCache.getInstance().cacheMap.get(userId) == null)
        {
            synchronized (CardCache.class)
            {
                if (CardCache.getInstance().cacheMap.get(userId) == null)
                {
                    CardCache.getInstance().cacheMap.put(userId, new HashMap<>());
                }
            }
        }
        return CardCache.getInstance().cacheMap.get(userId).get(cardId);}

    public Card set(Long userId, Long cardId, Card card)
    {
        if (CardCache.getInstance().cacheMap.get(userId) == null)
        {
            synchronized (CardCache.class)
            {
                if (CardCache.getInstance().cacheMap.get(userId) == null)
                {
                    CardCache.getInstance().cacheMap.put(userId, new HashMap<>());
                }
            }
        }
        CardCache.getInstance().cacheMap.get(userId).put(cardId,card);
        return card;
    }

    public AbstractMap<Long, HashMap<Long,Card>> getAll() {
        return CardCache.getInstance().cacheMap;
    }
    public HashSet<String> getSeen() {return CardCache.getInstance().seenCache;}

    public void setSeen(Long userId, Long cardId) {
        CardCache.getInstance().seenCache.add(userId + "," + cardId);
    }
}
