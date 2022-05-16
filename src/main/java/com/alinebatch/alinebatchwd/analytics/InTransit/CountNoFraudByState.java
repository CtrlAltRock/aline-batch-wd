package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.AnalysisWrite;
import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.models.TransactionDTO;


import java.util.HashMap;


public class CountNoFraudByState extends AnalysisWrite<String, Integer> implements InTransitAnalysis<TransactionDTO>  {

    private String keyName = "State";
    private String valueName = "Count";
    private String filePath = "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/NoFraudByState.xml";
    private String rootTag = "Count_Of_Transactions_With_No_Fraud_Grouped_By_State";
    @Override
    public void process(TransactionDTO input) {
        if (input.getFraud().equals("No") && !input.getMerchant_zip().equals(""))
        {
            String state = input.getMerchant_state();
            if (analysisMap.get(state) == null)
            {
                synchronized (analysisMap)
                {
                    if (analysisMap.get(state) == null)
                    {
                        analysisMap.put(state,0);
                    }
                }
            }
            int cnt = analysisMap.get(state) + 1;
            analysisMap.put(state, cnt);
        }
    }

    @Override
    public Class getKeyClass() {
        return String.class;
    }

    @Override
    public Class getValueClass() {
        return Integer.class;
    }

    @Override
    public String keyName() {
        return "State";
    }

    @Override
    public String valueName() {
        return "Count";
    }

    @Override
    public String rootTag() {
        return "Number_Of_Transactions_With_No_Fraud_Grouped_By_State";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/noFraudByState.xml";
    }
}
