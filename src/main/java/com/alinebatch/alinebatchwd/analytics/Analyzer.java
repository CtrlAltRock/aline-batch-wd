package com.alinebatch.alinebatchwd.analytics;


import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import com.alinebatch.alinebatchwd.models.User;
import com.alinebatch.alinebatchwd.models.UserDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Component
public class Analyzer {

    //user analysis variables
    private int ibOnce = 0;

    private int ibMore = 0;

    private int userCount = 0;

    private double PercentIbOnce = 0;

    private double PercentIbMore = 0;

    private HashMap<String, ArrayList<TransactionDTO>> specificMap = new HashMap<>();

    private HashMap<String, Integer> zipMap = new HashMap<>();

    private List<String> topZips = new ArrayList<>();

    private ArrayList<UserDTO> withDeposits = new ArrayList<>();

    //transaction analysis
    private HashMap<String, Boolean> typeMap = new HashMap<>();

    private ArrayList<String> typeList = new ArrayList<>();

    private final int topTransactionCount = 10;

    private Long merchants = 0L;

    private int deposits = 0;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    //fraud percentage by year setup
    private HashMap<Integer, Integer> noFraudMapYear = new HashMap<>();

    private HashMap<Integer, Integer> fraudMapYear = new HashMap<>();

    //transactions by states with no fraud count
    private HashMap<String, Integer> noFraudMapState = new HashMap<>();

    @XStreamOmitField
    private HashMap<Integer, String> fraudByYear = new HashMap<>();

    private ArrayList<TransactionDTO> largestTransactions = new ArrayList<>();

    @XStreamOmitField
    private ConcurrentHashMap<String, Integer> errorMap = new ConcurrentHashMap<>();

    @XStreamOmitField
    private ConcurrentHashMap<Integer, ArrayList<TransactionDTO>> yearMap = new ConcurrentHashMap<>();

    public static Analyzer instance = null;

    //singleton creation
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

    //USER Analysis Section
    public static UserDTO tallyInsufficient(UserDTO userDTO, TransactionDTO transactionDTO)
    {
        if (transactionDTO.getErrors().contains("Insufficient Balance"))
        {
            userDTO.incrementIb();
        }

        return userDTO;
    }

    public void processUserAnalysis(UserDTO user)
    {
        getInstance().userCount++;
        if (user.getIbCount() != 0)
        {
            getInstance().ibOnce++;
            if (user.getIbCount() > 1)
            {
                getInstance().ibMore++;
            }
        }
        if (user.getDeposits().size() > 0)
        {
            getStaticInstance().withDeposits.add(user);
        }
    }

    public void calculateUserAnalysis()
    {
        log.info(ibOnce + "");
        log.info(ibMore + "");
        getInstance().PercentIbOnce = (double)getInstance().ibOnce/ (double)getInstance().userCount;
        getInstance().PercentIbMore = (double)getInstance().ibMore/ (double)getInstance().userCount;
    }

    //transaction analysis
    public void countNoFraudByState(TransactionDTO transaction)
    {
        Analyzer a = getInstance();
        if (transaction.getFraud().equals("No") && !transaction.getMerchant_zip().equals(""))
        {
            String state = transaction.getMerchant_state();
            if (a.noFraudMapState.get(state) == null)
            {
                synchronized (a.noFraudMapState)
                {
                    if (a.noFraudMapState.get(state) == null)
                    {
                        a.noFraudMapState.put(state,0);
                    }
                }
            }
            int cnt = a.noFraudMapState.get(state);
            cnt += 1;
            a.noFraudMapState.put(state,cnt);
        }
    }
    public void isDeposit(TransactionDTO transaction)
    {
        if (transaction.getAmount().contains("-"))
        {
            UserDTO user = UserCache.getInstance().get(transaction.getUser());
            user.getDeposits().add(transaction);
        }
    }
    //count no fraud/fraud by year
    public void countNoFraudByYear(TransactionDTO transaction)
    {
        Analyzer a = getInstance();
        int year = transaction.getYear();
        //set hashmap for percentages
        if (a.getFraudMapYear().get(year) == null)
        {
            synchronized (Analyzer.class)
            {
                if (a.getFraudMapYear().get(year) == null)
                {
                    a.getFraudMapYear().put(year,0);
                    a.getNoFraudMapYear().put(year,0);
                }
            }
        }
        if (transaction.getFraud().equals("Yes"))
        {
            synchronized (a.getFraudMapYear())
            {
                int cnt = a.getFraudMapYear().get(year);
                cnt += 1;
                a.getFraudMapYear().put(year,cnt);
            }
        } else {
            synchronized (a.getNoFraudMapYear())
            {
                int cnt = a.getNoFraudMapYear().get(year);
                cnt += 1;
                a.getNoFraudMapYear().put(year,cnt);
            }
        }

    }

    public void calculateTransactionAnalysis()
    {
        Analyzer a = getInstance();
        //move hash map into list for better readability
        a.typeMap.forEach((key,value) ->
        {
            log.info(key);
            a.typeList.add(key);
        });
    }

    public void getTypes(TransactionDTO transaction)
    {
        String type = transaction.getMethod();
        Analyzer a = getInstance();
        a.typeMap.put(type,true);
    }

    public void processSpecific(TransactionDTO transaction)
    {
        Analyzer a = getInstance();
        if (Double.parseDouble(transaction.getAmount().replace("$","")) > 100.00 &&
        Integer.parseInt(transaction.getTime().split(":")[0]) >= 20)
        {
            String index = transaction.getMerchant_zip();
            if (transaction.getMethod().equals("Online Transaction"))
            {
                index = "ONLINE";
            }

            if (index.equals(""))
            {
                return;
            }

            if (a.specificMap.get(index) == null)
            {
                synchronized (a.specificMap)
                {
                    if (a.specificMap.get(index) == null)
                    {
                        a.specificMap.put(index, new ArrayList<TransactionDTO>());
                    }
                }
            }
            ArrayList<TransactionDTO> unfixed= a.specificMap.get(index);
            unfixed.add(transaction);
            a.specificMap.put(index,unfixed);
        }
    }

    public void processZip(TransactionDTO transaction)
    {
        Analyzer a = getInstance();
        String zip = transaction.getMerchant_zip();
        if (zip.equals("")) return;
        if (a.zipMap.get(zip) == null)
        {
            synchronized (a.zipMap)
            {
                if (a.zipMap.get(zip) == null)
                {
                    a.zipMap.put(zip, 0);
                }

            }
        }
        int cnt = a.zipMap.get(zip) + 1;
        a.zipMap.put(zip, cnt);
    }

    public void calculateTotals()
    {

    }

    public void calculatePercentages()
    {
        Analyzer a = getInstance();
        fraudPercentages();
        a.topZips = QueryList.getTopX(zipMap,5);
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

    public void calculateFraud(TransactionDTO transaction)
    {

    }

    public void processTopX(TransactionDTO transaction)
    {
        Analyzer a = getInstance();
        TransactionDTO stored = transaction;
        double to = Double.parseDouble(transaction.getAmount().replace("$",""));
        int index = 0;
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
    }


    public void processTransaction(TransactionDTO transaction)
    {

        Analyzer a = getInstance();
        log.info(Runtime.getRuntime().freeMemory() + "");
        int index = 0;
        TransactionDTO stored = transaction;
        stored.setAmount(stored.getAmount().replace("$",""));
        //a.transactions.add(stored);

        processZip(transaction);
        processSpecific(transaction);


        //check fraud
        boolean isFraud = stored.getFraud().equals("Yes");
        String year = transaction.getYear() + "";


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



        int cnt = 0;
        //Store Errors in HashMap for analysis
        if (transaction.getErrors() != "")
        {
            String[] err = getErrors(transaction);
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
                cnt = a.getErrorMap().get(key);
                a.getErrorMap().put(key, ++cnt);
            }
        }
    }

    //general analysis
    private static String[] getErrors(TransactionDTO transaction)
    {
        return transaction.getErrors().split(",");
    }
}
