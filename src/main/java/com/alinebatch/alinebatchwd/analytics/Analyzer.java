package com.alinebatch.alinebatchwd.analytics;


import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Component
public class Analyzer {

    @Value("${TopTransactions}")
    private int topTransactionCount;

    @XStreamAlias("Number of Merchants")
    private Long merchants = 0L;

    @XStreamAlias("Number of Users")
    private int users = 0;

    @XStreamAlias("Number of deposits")
    private int deposits = 0;

    private int inLoop = 0;

    public ArrayList<Object> transactions = new ArrayList<>();

    @XStreamAlias("Percent of users with insufficent balance")
    private Double percentOfUsersWithInsufficientBalance = 0.0;

    private HashMap<String, Integer> noFraudMap = new HashMap<>();

    @XStreamOmitField
    private HashMap<Integer, Double> fraudByYear = new HashMap<>();

    @XStreamAlias("Percent of users with Insufficient Balance More Than Once")
    private Double percentOfUsersWithInsufficientBalanceMoreThanOnce = 0.0;

    private ArrayList<TransactionDTO> largestTransactions = new ArrayList<>();

    @XStreamOmitField
    private ConcurrentHashMap<String, Integer> errorMap = new ConcurrentHashMap<>();

    @XStreamOmitField
    private ConcurrentHashMap<Integer, ArrayList<TransactionDTO>> yearMap = new ConcurrentHashMap<>();

    @XStreamOmitField
    private ConcurrentHashMap<Long, Boolean> hadInsufficientBalance = new ConcurrentHashMap<>();

    public static Analyzer instance = null;

    public static Analyzer getInstance()
    {
        if (instance == null)
        {
            synchronized (Analyzer.class)
            {
                if (instance == null)
                {
                    instance = new Analyzer();
                    log.info(instance.topTransactionCount + "");
                    try
                    {
                        Thread.sleep(10000);
                    } catch (Exception e) {

                    }

                }
            }
        }
        return instance;
    }

    public void increaseUsers()
    {
        instance.users += 1;
    }

    public void calculatePercentages()
    {
        Analyzer a = getInstance();
        a.percentOfUsersWithInsufficientBalance = a.hadInsufficientBalance.size()/((double)a.users);
        int countTwice = 0;
        for (int i = 0; i < users; i ++)
        {
            if (hadInsufficientBalance.get((long)i) != null && hadInsufficientBalance.get((long)i))
            {
                countTwice += 1;
            }
        }
        a.percentOfUsersWithInsufficientBalanceMoreThanOnce =countTwice/((double)a.users);
    }

    public static Analyzer getStaticInstance()
    {
        if (instance == null)
        {
            synchronized (Analyzer.class)
            {
                if (instance == null)
                {
                    instance = new Analyzer();
                }
            }
        }
        return instance;
    }

    public void addMerchant()
    {
        //log.info(getInstance().merchants + "");
        getInstance().merchants += 1L;
    }


    public void processTransaction(TransactionDTO transaction)
    {
        Analyzer a = getInstance();
        int index = 0;
        TransactionDTO stored = transaction;
        stored.setAmount(stored.getAmount().replace("$",""));
        a.transactions.add(stored);


        //check fraud
        boolean isFraud = stored.getFraud().equals("Yes");
        if (!isFraud && stored.getMerchant_state() != "")
        {
            if (a.noFraudMap.get(stored.getMerchant_state()) == null)
            {
                synchronized (a.noFraudMap)
                {
                    log.info("Locked in noFraudMap");
                    if (a.noFraudMap.get(stored.getMerchant_state()) == null)
                    {
                        a.noFraudMap.put(stored.getMerchant_state(), 0);
                    }

                }
            }
            int cnt = a.noFraudMap.get(stored.getMerchant_state());
            cnt += 1;
            a.noFraudMap.put(stored.getMerchant_state(), cnt);
        }
        if (a.fraudByYear.get(stored.getYear()) == null)
        {
            synchronized (a.fraudByYear)
            {
                log.info("Locked in fraudByYear");
                if (a.fraudByYear.get(stored.getYear()) == null)
                {
                    a.fraudByYear.put(stored.getYear(), isFraud ? 1.0 : 0.0);
                }
            }
        } else {
            Double ans = a.fraudByYear.get(stored.getYear()) + (isFraud ? 1.0: 0.0);
            a.fraudByYear.put(stored.getYear(), ans/2);
        }

        double to = Double.parseDouble(transaction.getAmount().replace("$",""));
        if (to < 0)
        {
            deposits += 1;
        }
        //place transactions by year
        /*
        if (a.yearMap.get(transaction.getYear()) == null)
        {
            synchronized (a.yearMap)
            {
                log.info("Locked in yearMap");
                if (a.yearMap.get(transaction.getYear()) == null)
                {
                    a.yearMap.put(transaction.getYear(), new ArrayList<>());
                }
            }
        }

        a.yearMap.get(transaction.getYear()).add(transaction);
        */

        //10 Largest Transactions


        while (index < 10)
        {
            //discard anything that is smaller than the last indexed transaction
            if (a.largestTransactions.size() == 10)
            {
                double against = Double.parseDouble(a.largestTransactions.get(9).getAmount().replace("$",""));
                if (to < against) break;
            }
            //discard any that is smaller than t
            synchronized (ArrayList.class)
            {
                log.info("Looping");
                while (index < 10)
                {
                    a.inLoop++;
                    if (index == a.largestTransactions.size())
                    {
                        a.largestTransactions.add(stored);
                        break;
                    }
                    double queriedValue = Double.parseDouble(a.largestTransactions.get(index).getAmount().replace("$",""));
                    if (queriedValue < to)
                    {
                        TransactionDTO transfer = a.largestTransactions.get(index);
                        a.largestTransactions.set(index,stored);
                        stored = transfer;
                    }
                    index++;
                }
            }

        }

        //Store Errors in HashMap for analysis
        if (transaction.getErrors() != "")
        {
            String[] err = transaction.getErrors().split(",");
            for (int i = 0; i < err.length; i ++) {
                String key = err[i];

                if (a.getErrorMap().get(key) == null) {
                    synchronized (Analyzer.class)
                    {
                        if (a.getErrorMap().get(key) == null)
                        {
                            a.getErrorMap().put(key, 0);
                        }
                    }
                }
                //check for insufficient balance and add to map
                if (key.equals("Insufficient Balance"))
                {
                    if (a.hadInsufficientBalance.get(transaction.getUser()) == null)
                    {
                        a.hadInsufficientBalance.put(transaction.getUser(),false);
                    } else {
                        a.hadInsufficientBalance.put(transaction.getUser(),true);
                    }
                    a.percentOfUsersWithInsufficientBalance = a.hadInsufficientBalance.size()/((double)a.users);
                }
                int cnt = a.getErrorMap().get(key);
                a.getErrorMap().put(key, ++cnt);
            }
        }
    }
}
