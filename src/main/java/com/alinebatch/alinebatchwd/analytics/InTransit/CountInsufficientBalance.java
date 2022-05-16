package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.alinebatch.alinebatchwd.models.UserDTO;

public class CountInsufficientBalance implements InTransitAnalysis<TransactionDTO> {

    @Override
    public void process(TransactionDTO input) {
        if (input.getErrors().toUpperCase().contains("BALANCE"))
        {
            UserCache.getInstance().get(input.getUser()).incrementIb();
        }
    }
}
