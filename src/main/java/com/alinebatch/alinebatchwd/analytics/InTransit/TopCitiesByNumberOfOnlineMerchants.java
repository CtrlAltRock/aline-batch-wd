package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.analytics.QueryList;
import com.alinebatch.alinebatchwd.analytics.SortedWriter;
import com.alinebatch.alinebatchwd.analytics.postProcess.PostProcessAnalysis;
import com.alinebatch.alinebatchwd.caches.MerchantCache;
import com.alinebatch.alinebatchwd.models.Merchant;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class TopCitiesByNumberOfOnlineMerchants extends SortedWriter<String, Integer> implements InTransitAnalysis<Merchant> {

    public TopCitiesByNumberOfOnlineMerchants(int count) {
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
        return "Top_10_Cities_Grouped_By_Number_Of_Merchants_That_Had_Online_Transactions";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Top_10_Cities_Grouped_By_Number_Of_Merchants_That_Had_Online_Transactions.xml";
    }

    @Override
    public void process(Merchant input) {

        if (input.getHadOnline())
        {
            String key = input.getCity();
            increment(key);

        }

    }

    public void process(TransactionDTO input)
    {
        if (input.getMethod().contains("Online"))
        {
            MerchantCache.toggleOnline(input.getMerchant_name());
        }
    }

    @Override
    public ArrayList<String> sort(HashMap<String, Integer> toSort) {
        return QueryList.getTopX(unsortedTally, 10);
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }

    @Override
    public void increment(String key) {
        synchronized (unsortedTally)
        {
            put(key, get(key + 1));
        }
    }

}
