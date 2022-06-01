package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.AnalysisWrite;
import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProcessSpecificTransaction extends AnalysisWrite<String, ArrayList<TransactionDTO>> implements InTransitAnalysis<TransactionDTO> {

    Integer count = 0;

    @Override
    public Class getKeyClass() {
        return String.class;
    }

    @Override
    public Class getValueClass() {
        return List.class;
    }

    @Override
    public String keyName() {
        return "ZipCode";
    }

    @Override
    public String valueName() {
        return "Transaction";
    }

    @Override
    public String rootTag() {
        return "All_Transactions_Over_100_Occuring_After_8PM_Grouped_By_Zipcode_Or_Online";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/All_Transactions_Over_100_Occuring_After_8PM_Grouped_By_Zipcode_Or_Online.xml";
    }

    @Override
    public void process(TransactionDTO input) {
        if (Double.parseDouble(input.getAmount().replace("$","")) > 100.00 &&
                Integer.parseInt(input.getTime().split(":")[0]) >= 20) {
            String index = input.getMerchant_zip();
            if (input.getMethod().equals("Online Transaction")) index = "Online";


            if (index.equals(""))
            {
                return;
            }

            if (analysisMap.get(index) == null)
            {
                synchronized (analysisMap)
                {
                    if (analysisMap.get(index) == null)
                    {
                        analysisMap.put(index, new ArrayList<>());
                    }
                }
            }
            analysisMap.get(index).add(input);
        }

    }
}
