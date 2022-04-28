package com.alinebatch.alinebatchwd.processors;


import com.alinebatch.alinebatchwd.models.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MerchantCacheProcessor implements ItemProcessor<Object, Object> {
    @Override
    public Object process(Object merchant) throws Exception {
        return merchant;
    }
}
