package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.AnalysisContainer;
import com.alinebatch.alinebatchwd.analytics.BasicWriter;
import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.analytics.postProcess.PostProcessAnalysis;
import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.UserDTO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

@Slf4j
public class UserInsufficientBalance extends BasicWriter<BigDecimal> implements InTransitAnalysis<UserDTO>, PostProcessAnalysis {

    double hasInsufficent = 0.0;

    public UserInsufficientBalance(BigDecimal input) {
        super(input);
        AnalysisContainer.addPost(this);
    }

    @Override
    public Class getWritableClass() {
        return BigDecimal.class;
    }

    @Override
    public String getValueName() {
        return "Percent";
    }

    @Override
    public String rootTag() {
        return "Percent_Of_Users_With_Insufficient_Balance";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Percent_Of_Users_With_Insufficient_Balance.xml";
    }

    @Override
    public void process(UserDTO input) {
        if (input.getIbCount() > 0)
        {
            hasInsufficent += 1;
        }
    }

    @Override
    public void postProcess() {
        this.basicStat = new BigDecimal((hasInsufficent/((double)UserCache.getInstance().count()))*100).round(new MathContext(4));
    }
}
