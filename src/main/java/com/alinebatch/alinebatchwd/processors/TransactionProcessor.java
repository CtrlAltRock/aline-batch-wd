package com.alinebatch.alinebatchwd.processors;


import com.alinebatch.alinebatchwd.generators.GeneratorBean;
import com.alinebatch.alinebatchwd.models.Card;
import com.alinebatch.alinebatchwd.models.Transaction;
import com.alinebatch.alinebatchwd.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class TransactionProcessor implements ItemProcessor<Transaction, Object> {


    static GeneratorBean generatorBean = new GeneratorBean();

    @Override
    public User process(Transaction transaction) throws Exception {
        long userId = transaction.getUser();
        long cardId = transaction.getCard();
        User u = generatorBean.getUser(userId);
        Card c = generatorBean.getCard(userId,cardId);
        return u;
    }
}
