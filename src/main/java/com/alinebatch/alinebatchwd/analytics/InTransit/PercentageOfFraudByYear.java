package com.alinebatch.alinebatchwd.analytics.InTransit;

import com.alinebatch.alinebatchwd.analytics.AnalysisContainer;
import com.alinebatch.alinebatchwd.analytics.AnalysisWrite;
import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.analytics.postProcess.PostProcessAnalysis;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

public class PercentageOfFraudByYear extends AnalysisWrite<Integer, String> implements InTransitAnalysis<TransactionDTO>, PostProcessAnalysis {

    public PercentageOfFraudByYear()
    {
        super();
        AnalysisContainer.addPost(this);
    }

    @XStreamOmitField
    private HashMap<Integer, Integer> noFraudMap = new HashMap<>();

    @XStreamOmitField
    private HashMap<Integer, Integer> fraudMap = new HashMap<>();

    @Override
    public Class getKeyClass() {
        return Integer.class;
    }

    @Override
    public Class getValueClass() {
        return String.class;
    }

    @Override
    public String keyName() {
        return "Year";
    }

    @Override
    public String valueName() {
        return "Count";
    }

    @Override
    public String rootTag() {
        return "Percentage_Of_Fraud_By_Year";
    }

    @Override
    public String filePath() {
        return "/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Percentage_Of_Fraud_By_Year.xml";
    }

    @Override
    public void process(TransactionDTO input) {
        Integer year = input.getYear();
        Boolean fraud = input.getFraud().equals("Yes");
        if (noFraudMap.get(year) == null || fraudMap.get(year) == null)
        {
            synchronized (PercentageOfFraudByYear.class)
            {
                if (noFraudMap.get(year) == null || fraudMap.get(year) == null)
                {
                    noFraudMap.put(year,0);
                    fraudMap.put(year,0);
                }
            }
        }
        if (fraud)
        {
            fraudMap.put(year,fraudMap.get(year) + 1);
        } else {
            noFraudMap.put(year,noFraudMap.get(year) + 1);
        }
    }

    @Override
    public void postProcess() {
        noFraudMap.forEach((year, value) ->
        {
            double noFraud = (double)value;
            double fraud = fraudMap.get(year);
            BigDecimal percentage = BigDecimal.valueOf(100*(fraud/(fraud + noFraud))).round(new MathContext(4));
            analysisMap.put(year, "%" + percentage);
        });
    }
}
