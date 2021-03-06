package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.analytics.QueryList;
import com.alinebatch.alinebatchwd.analytics.SortedWriter;
import com.alinebatch.alinebatchwd.models.TransactionDTO;

import java.util.ArrayList;
import java.util.HashMap;

public class TopCityByTransaction extends SortedWriter<String, Integer> implements InTransitAnalysis<TransactionDTO> {
    public TopCityByTransaction(int count) {
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
        return "City";
    }

    @Override
    public String valueName() {
        return "Count";
    }

    @Override
    public String rootTag() {
        return "Top_Transactions_Grouped_By_Cities";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/TopCityByTransaction.xml";
    }

    @Override
    public void process(TransactionDTO input) {
        String city = input.getMerchant_city().replace(" ","_");
        increment(city);
    }

    @Override
    public ArrayList<String> sort(HashMap<String, Integer> toSort) {
        return QueryList.getTopX(unsortedTally,5);
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }

    @Override
    public void increment(String key) {
        synchronized (unsortedTally)
        {
            int i = get(key);
            put(key, i + 1);
        }
    }
}
