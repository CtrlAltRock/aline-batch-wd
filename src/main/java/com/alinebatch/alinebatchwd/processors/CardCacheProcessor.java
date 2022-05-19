package com.alinebatch.alinebatchwd.processors;

import com.alinebatch.alinebatchwd.caches.CardCache;
import com.alinebatch.alinebatchwd.models.Card;
import com.alinebatch.alinebatchwd.models.CardDTO;
import com.alinebatch.alinebatchwd.models.UserDTO;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashMap;

public class CardCacheProcessor implements ItemProcessor<UserDTO, HashMap<Long, CardDTO>> {

    @Override
    public HashMap<Long, CardDTO> process(UserDTO user)
    {
        return CardCache.getInstance().getAll(user.getId());
    }
}
