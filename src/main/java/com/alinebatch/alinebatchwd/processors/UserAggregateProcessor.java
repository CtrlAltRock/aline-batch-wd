package com.alinebatch.alinebatchwd.processors;

import com.alinebatch.alinebatchwd.analytics.Analyzer;
import com.alinebatch.alinebatchwd.models.UserDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAggregateProcessor implements ItemProcessor<UserDTO, UserDTO> {

    Analyzer analyzer = new Analyzer();

    @Override
    public UserDTO process(UserDTO item) throws Exception {
        analyzer.processUserAnalysis(item);
        return item;
    }
}
