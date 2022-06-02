package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.analytics.QueryList;
import com.alinebatch.alinebatchwd.analytics.SortedWriter;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;


@Slf4j
public class BottomFiveMonths extends SortedWriter<String, Integer> implements InTransitAnalysis<TransactionDTO> {

    public BottomFiveMonths(int count) {
        super(count);
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
        return "Month";
    }

    @Override
    public String valueName() {
        return "Count";
    }

    @Override
    public String rootTag() {
        return "Bottom_5_Months_With_Number_Of_Online_Transactions";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Bottom_5_Months_With_Number_Of_Online_Transactions.xml";
    }

    @Override
    public void process(TransactionDTO input) {

        if (input.getMethod().contains("Online"))
        {
            String key = input.getMonth() + "";
            increment(key);


        }

    }

    @Override
    public ArrayList<String> sort(HashMap<String, Integer> toSort) {
        return QueryList.getBottomX(unsortedTally,5);
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }

    @Override
    public void increment(String key) {
        synchronized (unsortedTally)
        {
            put(key, get(key) + 1);
        }
    }
}
