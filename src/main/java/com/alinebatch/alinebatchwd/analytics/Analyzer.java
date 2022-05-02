package com.alinebatch.alinebatchwd.analytics;


import com.alinebatch.alinebatchwd.models.Transaction;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Analyzer {

    private Long merchants = 0L;

    private Long users = 0L;

    private ArrayList<TransactionDTO> largestTransactions = new ArrayList<>();

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

    public void process(TransactionDTO transactionDTO)
    {

    }

    public void addMerchant()
    {
        log.info(getInstance().merchants + "");
        getInstance().merchants += 1L;
    }

    public void processTransaction(TransactionDTO transaction)
    {
        Analyzer a = getInstance();
        int index = 0;
        TransactionDTO stored = transaction;
        while (index < 10)
        {
            synchronized (Analyzer.class)
            {
                while (index < 10)
                {
                    if (index == a.largestTransactions.size())
                    {
                        log.info("" + a.largestTransactions.size());
                        a.largestTransactions.add(stored);
                        break;
                    }
                    double against = Double.parseDouble(a.largestTransactions.get(index).getAmount().replace("$",""));
                    double to = Double.parseDouble(transaction.getAmount().replace("$",""));
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
    }
}
