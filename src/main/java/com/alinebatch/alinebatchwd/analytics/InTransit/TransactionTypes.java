package com.alinebatch.alinebatchwd.analytics.InTransit;


import com.alinebatch.alinebatchwd.analytics.AnalysisWrite;
import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.models.TransactionDTO;

public class TransactionTypes extends AnalysisWrite<String, Boolean> implements InTransitAnalysis<TransactionDTO> {


    @Override
    public Class getKeyClass() {
        return String.class;
    }

    @Override
    public Class getValueClass() {
        return Boolean.class;
    }

    @Override
    public String keyName() {
        return "Type";
    }

    @Override
    public String valueName() {
        return "Present";
    }

    @Override
    public String rootTag() {
        return "Transaction_Types";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Types_Of_Transactions.xml";
    }

    @Override
    public void process(TransactionDTO input) {
        String key = input.getMethod();
        if (analysisMap.get(key) == null)
        {
            synchronized (analysisMap)
            {
                if (analysisMap.get(key) == null)
                {
                    analysisMap.put(key,true);
                }
            }
        }
    }
}
