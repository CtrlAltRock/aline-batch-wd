import com.alinebatch.alinebatchwd.TestInjectionLambda;
import com.alinebatch.alinebatchwd.analytics.*;
import com.alinebatch.alinebatchwd.analytics.InTransit.*;
import com.alinebatch.alinebatchwd.caches.MerchantCache;
import com.alinebatch.alinebatchwd.caches.UserCache;
import com.alinebatch.alinebatchwd.config.BatchConfig;
import com.alinebatch.alinebatchwd.models.TransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;

import javax.persistence.Basic;
import javax.swing.text.html.HTMLDocument;
import javax.validation.constraints.AssertTrue;
import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
@SpringBatchTest
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BatchConfig.class})
@EnableAutoConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BatchTestSuite {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private AnalysisContainer analysisContainer = new AnalysisContainer();

    @Test
    public void UsersCountsCorrectly()
    {
        assertTrue(UserCache.getInstance().latest == 6);
    }

    @Test
    public void MerchantsCountCorrectly()
    {
        Assertions.assertEquals(MerchantCache.getInstance().getAll().size(), 2172);
    }

    @Test
    public void TransactionsAreLinearlyScaled()
    {
        //need to reimplement




    }

    @Test
    public void SpecificTransactionsAreCorrect()
    {
        TestInjectionLambda specificTransactionsLogic = new TestInjectionLambda() {
            @Override
            public boolean injectedLogic(Map.Entry entry) {
                ArrayList<TransactionDTO> inputList = (ArrayList<TransactionDTO>)entry.getValue();
                AtomicBoolean isSolid = new AtomicBoolean(true);

                inputList.forEach((k) ->
                {
                    if (Double.parseDouble(k.getAmount().replace("$","")) < 100 ||
                            Integer.parseInt(k.getTime().split(":")[0]) < 20) isSolid.set(false);
                });
                return isSolid.get();
            }
        };
        assertTrue(AnalysisContainer.injectTest(specificTransactionsLogic, ProcessSpecificTransaction.class));
    }

    @Test
    public void AllTransactionTypesListed()
    {
        HashMap<String, Boolean> checker = new HashMap<>();
        checker.put("SwipeTransaction",false);
        checker.put("ChipTransaction", false);
        checker.put("OnlineTransaction", false);
        File file = new File("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Types_Of_Transactions.xml");
        try {
            Scanner scanner = new Scanner(file).useDelimiter("\n");
            while (scanner.hasNext())
            {
                String line = scanner.next();
                if (line.contains("Type"))
                {
                    line = line.replace("<Type>","").replace("</Type>","").replace(" ", "");
                    checker.put(line,true);
                }
            }

        } catch (Exception e)
        {
            log.info(e.getMessage());
        }
        checker.forEach((k,e) -> {
            assertTrue(e);
        });
    }

    @Test
    public void UserInsufficientBalanceTests()
    {
        BasicWriter<?> once = (UserInsufficientBalance)AnalysisContainer.grabBasic(UserInsufficientBalance.class);
        BasicWriter<?> more = (UserInsufficientBalanceMore)AnalysisContainer.grabBasic(UserInsufficientBalanceMore.class);
        assertEquals( "100", once.basicStat.toString());
        assertEquals( "100", more.basicStat.toString());
    }

    @Test
    public void ZipcodeTransactionsAreSorted()
    {
        ArrayList<Integer> checker = new ArrayList<>();
        File file = new File("/Users/willemduiker/IdeaProjects/aline-batch-wd/src/main/resources/analysis/Most_Transactions_By_ZipCode.xml");
        try {
            Scanner input = new Scanner(file);
            while (input.hasNext())
            {
                String inString = input.next();

                if (inString.contains("Count"))
                {
                    inString = inString.replace("<","").replace("/","").replace(">","").replace("Count","");
                    log.info(inString);
                    checker.add(Integer.parseInt(inString));

                }
            }
            for (int i = 0; i < checker.size() - 1; i ++)
            {
                int j = checker.get(i);
                int jup = checker.get(i+ 1);
                assertTrue(j > jup);
            }

        } catch (Exception e)
        {
            log.info(e.getMessage());
        }


    }

    @Test
    public void FraudByYearProducesCorrectNumbers()
    {
        AnalysisWrite<Integer, String> tester= (PercentageOfFraudByYear)AnalysisContainer.grabProcessor(PercentageOfFraudByYear.class);
        assertEquals(tester.analysisMap.get(2001), "%0.1103");
    }

    @Test
    public void DepositsWork()
    {
        DepositsByUser depositsByUser = (DepositsByUser)AnalysisContainer.grabProcessor(DepositsByUser.class);
        depositsByUser.analysisMap.forEach((k,v) -> {
            v.forEach((transaction) ->
            {
                assertTrue(transaction.getAmount().contains("-"));
            });
        });
    }


}
