package com.alinebatch.alinebatchwd.processors;

import com.alinebatch.alinebatchwd.caches.CardCache;
import com.alinebatch.alinebatchwd.models.Card;
import com.alinebatch.alinebatchwd.models.User;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashMap;

public class CardCacheProcessor implements ItemProcessor<User, HashMap<Long, Card>> {

    @Override
    public HashMap<Long, Card> process(User user)
    {
        return CardCache.getInstance().getAll(user.getId());
    }
}