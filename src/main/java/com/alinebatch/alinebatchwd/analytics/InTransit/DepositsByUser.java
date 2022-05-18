package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.AnalysisWrite;
import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.models.TransactionDTO;

import java.util.ArrayList;
import java.util.List;

public class DepositsByUser extends AnalysisWrite<Long, ArrayList<TransactionDTO>> implements InTransitAnalysis<TransactionDTO> {

    @Override
    public Class getKeyClass() {
        return Long.class;
    }

    @Override
    public Class getValueClass() {
        return List.class;
    }

    @Override
    public String keyName() {
        return "User_ID";
    }

    @Override
    public String valueName() {
        return "List_Of_Deposits";
    }

    @Override
    public String rootTag() {
        return "Deposits_Grouped_By_User";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Deposits_Grouped_By_User.xml";
    }

    @Override
    public void process(TransactionDTO input) {
        if (Double.parseDouble(input.getAmount().replace("$","")) < 0)
        {
            Long userId = input.getUser();
            if (analysisMap.get(userId) == null)
            {
                synchronized (analysisMap)
                {
                    if (analysisMap.get(userId) == null)
                    {
                        analysisMap.put(userId, new ArrayList<>());
                    }
                }
            }
            analysisMap.get(userId).add(input);
        }
    }
}
