package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.analytics.ListAnalysisWriter;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TopTenTransactions extends ListAnalysisWriter<TransactionDTO> implements InTransitAnalysis<TransactionDTO>
{


    @Override
    public Class getValueClass() {
        return TransactionDTO.class;
    }

    @Override
    public String rootTag() {
        return "Top_Ten_Transactions_Sorted_By_Value";
    }

    @Override
    public String valueName() {
        return "Transaction";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Top_Ten_Transactions.xml";
    }

    @Override
    public void process(TransactionDTO input) {
        int i = 0;
        TransactionDTO stored = input;
        double to = parseValue(input);
        while (i < 10)
        {
            if (analysisList.size() == 10)
            {
                double against = parseValue(analysisList.get(9));
                if (to < against)
                {
                    break;
                }
            }
            synchronized (analysisList)
            {
                while (i < 10)
                {
                    if (i == analysisList.size())
                    {
                        analysisList.add(stored);
                        break;
                    }
                    double queriedValue = parseValue(analysisList.get(i));
                    if (queriedValue < to)
                    {
                        TransactionDTO transfer = analysisList.get(i);
                        analysisList.set(i, stored);
                        to = parseValue(transfer);
                        stored = transfer;
                    }
                    i++;
                }
            }

        }

    }

    public Double parseValue(TransactionDTO input)
    {
        return Double.parseDouble(input.getAmount().replace("$",""));
    }
}
