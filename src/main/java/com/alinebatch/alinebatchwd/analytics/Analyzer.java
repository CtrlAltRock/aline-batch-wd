package com.alinebatch.alinebatchwd.analytics;


import com.alinebatch.alinebatchwd.models.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Analyzer {

    private Long merchants = 0L;

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
}
