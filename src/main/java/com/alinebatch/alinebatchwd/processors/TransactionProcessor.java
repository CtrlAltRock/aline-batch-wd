package com.alinebatch.alinebatchwd.processors;


import com.alinebatch.alinebatchwd.analytics.AnalysisContainer;
import com.alinebatch.alinebatchwd.analytics.InTransit.*;
import com.alinebatch.alinebatchwd.analytics.InTransitAnalysis;
import com.alinebatch.alinebatchwd.caches.MerchantCache;
import com.alinebatch.alinebatchwd.caches.StateCache;
import com.alinebatch.alinebatchwd.generators.GeneratorBean;
import com.alinebatch.alinebatchwd.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class TransactionProcessor extends CompositeItemProcessor<TransactionDTO, TransactionDTO> {


    static GeneratorBean generatorBean = new GeneratorBean();

    private ArrayList<InTransitAnalysis<TransactionDTO>> processors = new ArrayList<>();

    static StateCache stateCache = new StateCache();

    private AnalysisContainer analysisContainer = new AnalysisContainer();


    Long howMany = 0L;

    //processors

    CountNoFraudByState countNoFraudByState = new CountNoFraudByState();

    PercentageOfFraudByYear percentageOfFraudByYear = new PercentageOfFraudByYear();

    CountInsufficientBalance countInsufficientBalance = new CountInsufficientBalance();

    MostTransactionsByZipCode mostTransactionsByZipCode = new MostTransactionsByZipCode(5);

    TransactionTypes transactionTypes = new TransactionTypes();

    TopTenTransactions topTenTransactions = new TopTenTransactions();

    TopFiveMerchantsNoErrorsNoIb topFiveMerchantsNoErrorsNoIb = new TopFiveMerchantsNoErrorsNoIb(5);

    TopCityByTransaction topCityByTransaction = new TopCityByTransaction(5);

    DepositsByUser depositsByUser = new DepositsByUser();

    BottomFiveMonths bottomFiveMonths = new BottomFiveMonths(5);

    TopCitiesByNumberOfOnlineMerchants topCitiesByNumberOfOnlineMerchants = new TopCitiesByNumberOfOnlineMerchants(10);

    ProcessSpecificTransaction processSpecificTransaction = new ProcessSpecificTransaction();

    @Override
    public TransactionDTO process(TransactionDTO transactionD) throws Exception {
        if (stateCache.getInstance().get(transactionD.getMerchant_state()) != null)
        {
            stateCache.getInstance().putZip(transactionD.getMerchant_state(),transactionD.getMerchant_zip());
        }
        long userId = transactionD.getUser();
        long cardId = transactionD.getCard();
        generatorBean.getUser(userId);
        generatorBean.getCard(userId,cardId);
        generatorBean.getMerchant(transactionD.getMerchant_name(), transactionD);
        //do analysis
        countNoFraudByState.process(transactionD);
        percentageOfFraudByYear.process(transactionD);
        countInsufficientBalance.process(transactionD);
        mostTransactionsByZipCode.process(transactionD);
        transactionTypes.process(transactionD);
        topTenTransactions.process(transactionD);
        depositsByUser.process(transactionD);
        topFiveMerchantsNoErrorsNoIb.process(transactionD);
        topCityByTransaction.process(transactionD);
        bottomFiveMonths.process(transactionD);
        topCitiesByNumberOfOnlineMerchants.process(transactionD);
        //processSpecificTransaction.process(transactionD);
        return transactionD;
    }
}
