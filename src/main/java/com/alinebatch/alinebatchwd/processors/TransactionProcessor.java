package com.alinebatch.alinebatchwd.processors;


import com.alinebatch.alinebatchwd.analytics.AnalysisContainer;
import com.alinebatch.alinebatchwd.analytics.Analyzer;
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

    Analyzer analyzer = new Analyzer();

    Long howMany = 0L;

    //processors

    CountNoFraudByState countNoFraudByState = new CountNoFraudByState();

    ProcessSpecificTransaction processSpecificTransaction = new ProcessSpecificTransaction();

    PercentageOfFraudByYear percentageOfFraudByYear = new PercentageOfFraudByYear();

    CountInsufficientBalance countInsufficientBalance = new CountInsufficientBalance();

    MostTransactionsByZipCode mostTransactionsByZipCode = new MostTransactionsByZipCode(5);

    TransactionTypes transactionTypes = new TransactionTypes();

    TopTenTransactions topTenTransactions = new TopTenTransactions();

    TopFiveMerchantsNoErrorsNoIb topFiveMerchantsNoErrorsNoIb = new TopFiveMerchantsNoErrorsNoIb(5);

    TopCityByTransaction topCityByTransaction = new TopCityByTransaction(5);

    DepositsByUser depositsByUser = new DepositsByUser();
    //Creates Caches for all objects
    @Override
    public TransactionDTO process(TransactionDTO transactionD) throws Exception {
        if (stateCache.getInstance().get(transactionD.getMerchant_state()) != null)
        {
            stateCache.getInstance().putZip(transactionD.getMerchant_state(),transactionD.getMerchant_zip());
        }
        long userId = transactionD.getUser();
        long cardId = transactionD.getCard();
        UserDTO u = generatorBean.getUser(userId);
        CardDTO c = generatorBean.getCard(userId,cardId);
        Merchant m = generatorBean.getMerchant(transactionD.getMerchant_name(), transactionD);
        //do analysis
        countNoFraudByState.process(transactionD);
        processSpecificTransaction.process(transactionD);
        percentageOfFraudByYear.process(transactionD);
        countInsufficientBalance.process(transactionD);
        mostTransactionsByZipCode.process(transactionD);
        transactionTypes.process(transactionD);
        topTenTransactions.process(transactionD);
        depositsByUser.process(transactionD);
        topFiveMerchantsNoErrorsNoIb.process(transactionD);
        topCityByTransaction.process(transactionD);

        return transactionD;
    }
}
