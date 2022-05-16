package com.alinebatch.alinebatchwd.analytics.postProcess;

import com.alinebatch.alinebatchwd.analytics.BasicWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CountOfMerchants extends BasicWriter<Integer> {


    public CountOfMerchants(Integer input) {
        super(input);
    }

    @Override
    public Class getWritableClass() {
        return Integer.class;
    }

    @Override
    public String getValueName() {
        return "Count";
    }

    @Override
    public String rootTag() {
        return "Number_Of_Unique_Merchants";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/countOfMerchants.xml";
    }
}
