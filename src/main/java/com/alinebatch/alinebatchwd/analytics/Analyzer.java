package com.alinebatch.alinebatchwd.analytics;


import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
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

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public ArrayList<Object> transactions = new ArrayList<>();

    @XStreamAlias("Percent of users with insufficent balance")
    private Double percentOfUsersWithInsufficientBalance = 0.0;

    private HashMap<String, Integer> noFraudMapYear = new HashMap<>();

    private HashMap<String, Integer> fraudMapYear = new HashMap<>();

    private HashMap<String, Integer> noFraudMapState = new HashMap<>();

    @XStreamOmitField
    private HashMap<String, String> fraudByYear = new HashMap<>();

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
        fraudPercentages();

    }

    public void fraudPercentages()
    {
        Analyzer a = getInstance();
        a.fraudMapYear.forEach((k,v) ->
                {
                    int total = v + a.noFraudMapYear.get(k);
                    String percent = df.format(((double)v)/((double)total)* 100.0);
                    a.fraudByYear.put(k,"%" + percent);
                });
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
        String year = transaction.getYear() + "";

        //Check if year exists in both fraud maps
        if (a.noFraudMapYear.get(year) == null)
        {
            synchronized (a.noFraudMapYear)
            {
                if (a.noFraudMapYear.get(year) == null)
                {
                    a.noFraudMapYear.put(year,0);
                }
            }
        }
        int cnt = (isFraud ? 0 : 1) + a.noFraudMapYear.get(year);
        a.noFraudMapYear.put(year, cnt);

        if (a.fraudMapYear.get(year) == null)
        {
            synchronized (a.fraudMapYear)
            {
                if (a.fraudMapYear.get(year) == null)
                {
                    a.fraudMapYear.put(year,0);
                }
            }
        }
        cnt = (isFraud ? 1 : 0) + a.fraudMapYear.get(year);
        a.fraudMapYear.put(year, cnt);


        if (!isFraud && stored.getMerchant_state() != "")
        {
            if (a.noFraudMapState.get(stored.getMerchant_state()) == null)
            {
                synchronized (a.noFraudMapState)
                {
                    if (a.noFraudMapState.get(stored.getMerchant_state()) == null)
                    {
                        a.noFraudMapState.put(stored.getMerchant_state(), 0);
                    }

                }
            }
            cnt = a.noFraudMapState.get(stored.getMerchant_state());
            cnt += 1;
            a.noFraudMapState.put(stored.getMerchant_state(), cnt);
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
            synchronized (ArrayList.class)
            {
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
                cnt = a.getErrorMap().get(key);
                a.getErrorMap().put(key, ++cnt);
            }
        }
    }
}
