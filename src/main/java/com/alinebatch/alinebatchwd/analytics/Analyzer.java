package com.alinebatch.alinebatchwd.analytics;


import com.alinebatch.alinebatchwd.models.Transaction;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
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

    private Long merchants = 0L;

    private int users = 0;

    private int deposits = 0;

    private Double percentOfUsersWithInsufficientBalance = 0.0;

    private ArrayList<TransactionDTO> largestTransactions = new ArrayList<>();

    private ConcurrentHashMap<String, Integer> errorMap = new ConcurrentHashMap<>();

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
            synchronized (Analyzer.class)
            {
                while (index < 10)
                {
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
                    a.hadInsufficientBalance.put(transaction.getUser(),true);
                    a.percentOfUsersWithInsufficientBalance = a.hadInsufficientBalance.size()/((double)a.users);
                }
                int cnt = a.getErrorMap().get(key);
                a.getErrorMap().put(key, ++cnt);
            }
        }
    }
}
