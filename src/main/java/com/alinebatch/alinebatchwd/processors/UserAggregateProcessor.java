package com.alinebatch.alinebatchwd.processors;

import com.alinebatch.alinebatchwd.analytics.Analyzer;
import com.alinebatch.alinebatchwd.analytics.InTransit.UserInsufficientBalance;
import com.alinebatch.alinebatchwd.analytics.InTransit.UserInsufficientBalanceMore;
import com.alinebatch.alinebatchwd.models.UserDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class UserAggregateProcessor implements ItemProcessor<UserDTO, UserDTO> {

    Analyzer analyzer = new Analyzer();

    //analyzers
    UserInsufficientBalance userInsufficientBalance = new UserInsufficientBalance(new BigDecimal(0));
    UserInsufficientBalanceMore userInsufficientBalanceMore = new UserInsufficientBalanceMore(new BigDecimal(0));

    @Override
    public UserDTO process(UserDTO item) throws Exception {
        userInsufficientBalance.process(item);
        userInsufficientBalanceMore.process(item);
        return item;
    }
}
