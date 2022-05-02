package com.alinebatch.alinebatchwd.processors;


import com.alinebatch.alinebatchwd.analytics.Analyzer;
import com.alinebatch.alinebatchwd.caches.StateCache;
import com.alinebatch.alinebatchwd.generators.GeneratorBean;
import com.alinebatch.alinebatchwd.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Null;

@Slf4j
public class TransactionProcessor implements ItemProcessor<TransactionDTO, TransactionDTO> {


    static GeneratorBean generatorBean = new GeneratorBean();

    static StateCache stateCache = new StateCache();

    Analyzer analyzer = new Analyzer();

    Long howMany = 0L;



    //Creates Caches for all objects
    @Override
    public TransactionDTO process(TransactionDTO transactionD) throws Exception {
        try {
            stateCache.getInstance().putZip(transactionD.getMerchant_state(),transactionD.getMerchant_zip());

        } catch (NullPointerException e)
        {
            //log.info("Attempted to write non US State");
        }
        analyzer.processTransaction(transactionD);
        long userId = transactionD.getUser();
        long cardId = transactionD.getCard();
        User u = generatorBean.getUser(userId);
        Card c = generatorBean.getCard(userId,cardId);
        Merchant m = generatorBean.getMerchant(transactionD.getMerchant_name(), transactionD);
        return transactionD;
    }
}
