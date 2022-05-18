package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.*;
import com.alinebatch.alinebatchwd.analytics.postProcess.PostProcessAnalysis;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Null;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class MostTransactionsByZipCode extends SortedWriter<String, Integer> implements InTransitAnalysis<TransactionDTO> {

    public MostTransactionsByZipCode(int count)
    {
        super(count);
        for (int i = 100; i < 100000; i ++)
        {
            unsortedTally.put(i+".0",0);
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
        return "Zipcode";
    }

    @Override
    public String valueName() {
        return "Count";
    }

    @Override
    public String rootTag() {
        return "Most_Transactions_Grouped_By_ZipCode";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Most_Transactions_By_ZipCode.xml";
    }

    @Override
    public void process(TransactionDTO input) {

        if (!input.getMerchant_zip().equals(""))
        {
            String zip = input.getMerchant_zip();
            if (unsortedTally.get(zip) == null)
            {
                synchronized (unsortedTally)
                {
                    log.info("Creating new zip");
                    if (unsortedTally.get(zip) == null)
                    {
                        unsortedTally.put(zip,0);
                    }
                }
            }
            synchronized (unsortedTally)
            {
                try
                {
                    unsortedTally.put(zip, unsortedTally.get(zip) + 1);
                } catch (NullPointerException e)
                {
                    log.info(zip);
                    log.info(e.getMessage());
                }

            }

        }
    }

    @Override
    public ArrayList<String> sort(HashMap<String, Integer> toSort) {
        return QueryList.getTopX(unsortedTally,5);
    }
}
