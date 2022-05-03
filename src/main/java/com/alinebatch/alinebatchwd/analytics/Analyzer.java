package com.alinebatch.alinebatchwd.analytics;


import com.alinebatch.alinebatchwd.models.Transaction;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Analyzer {

    @XStreamAlias("Number of Merchants")
    private Long merchants = 0L;

    @XStreamAlias("Number of Users")
    private int users = 0;

    @XStreamAlias("Number of deposits")
    private int deposits = 0;

    private int inLoop = 0;

    @XStreamAlias("Percent of users with insufficent balance")
    private Double percentOfUsersWithInsufficientBalance = 0.0;

    @XStreamAlias("Percent of users with Insufficient Balance More Than Once")
    private Double percentOfUsersWithInsufficientBalanceMoreThanOnce = 0.0;

    private ArrayList<TransactionDTO> largestTransactions = new ArrayList<>();

    @XStreamOmitField
    private ConcurrentHashMap<String, Integer> errorMap = new ConcurrentHashMap<>();

    @XStreamOmitField
    private ConcurrentHashMap<Long, Boolean> hadInsufficientBalance = new ConcurrentHashMap<>();

    public static Analyzer instance = null;

    public Analyzer getInstance()
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

        double to = Double.parseDouble(transaction.getAmount().replace("$",""));
        if (to < 0)
        {
            deposits += 1;
        }


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
                while (index < 10)
                {
                    a.inLoop++;
                    if (index == a.largestTransactions.size())
                    {
                        a.largestTransactions.add(stored);
                        break;
                    }
                    double against = Double.parseDouble(a.largestTransactions.get(index).getAmount().replace("$",""));
                    if (against < to)
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
