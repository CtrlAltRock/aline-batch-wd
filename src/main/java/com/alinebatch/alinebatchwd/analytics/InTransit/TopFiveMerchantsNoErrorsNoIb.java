package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.analytics.QueryList;
import com.alinebatch.alinebatchwd.analytics.SortedWriter;
import com.alinebatch.alinebatchwd.caches.MerchantCache;
import com.alinebatch.alinebatchwd.models.Merchant;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TopFiveMerchantsNoErrorsNoIb extends SortedWriter<String, Integer> implements InTransitAnalysis<TransactionDTO> {

    public TopFiveMerchantsNoErrorsNoIb(int count) {
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
        return "Merchant_Name";
    }

    @Override
    public String valueName() {
        return "Count";
    }

    @Override
    public String rootTag() {
        return "Top_5_Merchants_With_Insufficient_Balance_That_Had_No_Errors";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Top_5_Merchants_With_Insufficient_Balance_That_Had_No_Errors.xml";
    }

    @Override
    public void process(TransactionDTO input) {
        String merchant = input.getMerchant_name();
        //check if merchant exists in map
        if (unsortedTally.get(merchant) == null)
        {
            synchronized (analysisMap)
            {
                if (unsortedTally.get(merchant) == null) {
                    unsortedTally.put(merchant, 0);

                }
            }
        }
        if (!input.getErrors().equals(""))
        {
            //might have errors
            if (!input.getErrors().equals("") && !input.getErrors().equals("Insufficient Balance,"))
            {
                //has errors
                MerchantCache.toggleErrors(merchant);
            }
            if (input.getErrors().contains("Insufficient Balance"))
            {
                MerchantCache.getInstance().toggleIb(merchant);
            }
        }

        unsortedTally.put(merchant, unsortedTally.get(merchant)+1);
    }

    @Override
    public ArrayList<String> sort(HashMap<String, Integer> toSort) {
        log.info("Sorting, " + toSort.size());
        HashMap<String, Integer> unsorted = new HashMap<>();
        toSort.forEach((k,v) ->
        {
            Merchant merchant = MerchantCache.getInstance().get(k);
            if (!merchant.getHadErrors() && merchant.getHadIb())
            {
                unsorted.put(k,v);
            }
        });

        return QueryList.getTopX(unsorted,5);
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
