package com.alinebatch.alinebatchwd.caches;

import com.alinebatch.alinebatchwd.models.Card;
import com.alinebatch.alinebatchwd.models.CardDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.CatchClauseSignature;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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

    private HashMap<Long, HashMap<Long, CardDTO>> cacheMap = new HashMap<>();

    private HashSet<String> seenCache = new HashSet<>();

    public HashMap<Long, CardDTO> getAll(Long userId)
    {
        return CardCache.getInstance().cacheMap.get(userId);
    }

    public CardDTO get(Long userId, Long cardId) {
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
        return CardCache.getInstance().cacheMap.get(userId).get(cardId);
    }


    public CardDTO set(Long userId, Long cardId, CardDTO card)
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

    public AbstractMap<Long, HashMap<Long,CardDTO>> getAll() {
        return CardCache.getInstance().cacheMap;
    }
    public HashSet<String> getSeen() {return CardCache.getInstance().seenCache;}

    public void setSeen(Long userId, Long cardId) {
        CardCache.getInstance().seenCache.add(userId + "," + cardId);
    }
}
