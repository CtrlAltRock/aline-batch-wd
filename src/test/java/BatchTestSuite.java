import com.alinebatch.alinebatchwd.analytics.Analyzer;
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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.swing.text.html.HTMLDocument;
import javax.validation.constraints.AssertTrue;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = { BatchConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)

public class BatchTestSuite {

    @Test
    public void UsersCountsCorrectly()
    {
        UserCache userCache = new UserCache();
        Assertions.assertEquals(userCache.getAll().size(),6);
    }

    @Test
    public void AnalysisWorks()
    {
        Analyzer analyzer = new Analyzer();
        //user count is good
        Assertions.assertEquals(analyzer.getInstance().getUserCount(), 6);
        //merchant count is consistent
        Assertions.assertEquals(analyzer.getInstance().getMerchants(), 2172L);
        //make sure top transaction count is 10
        Assertions.assertEquals(analyzer.getInstance().getLargestTransactions().size(), 10);
    }

    @Test
    public void TransactionsAreLinearlyScaled()
    {
        Analyzer analyzer = new Analyzer();
        log.info(analyzer.getInstance().getLargestTransactions().toString());
        //check that all transactions are smaller than the preceeding transaction
        ArrayList<TransactionDTO> transactionList = analyzer.getInstance().getLargestTransactions();
        for (int i = 0; i < 9; i ++)
        {
            TransactionDTO t1 = transactionList.get(i);
            TransactionDTO t2 = transactionList.get(i+1);
            Double val1 = Double.parseDouble(t1.getAmount().replace("$",""));
            Double val2 = Double.parseDouble(t2.getAmount().replace("$",""));
            Assertions.assertTrue(val1 > val2);
            log.info((val1 > val2) ? "values are correctly sorted" : "Values are incorrectly sorted, you compared " + val1 + " with " + val2);
        }
    }

    @Test
    public void SpecificTransactionsAreCorrect()
    {
        Analyzer analyzer = new Analyzer();
        //
        ArrayList<TransactionDTO> transactionList = analyzer.getInstance().getSpecificMap().get("70809.0");

        transactionList.forEach((val) -> {
            Assertions.assertTrue(Double.parseDouble(val.getAmount().replace("$","")) > 100.00);
        });
    }

    @Test
    public void AllTransactionTypesListed()
    {
        ArrayList<String> typeCompare = new ArrayList<>();
        typeCompare.add("Swipe Transaction");
        typeCompare.add("Chip Transaction");
        typeCompare.add("Online Transaction");
        Analyzer analyzer = new Analyzer();
        Assertions.assertEquals(typeCompare, analyzer.getInstance().getTypeList());
    }

    @Test
    public void UserInsufficientBalanceTests()
    {
        Analyzer analyzer = new Analyzer();
        Assertions.assertEquals(analyzer.getInstance().getPercentIbMore(),1.0);
        Assertions.assertEquals(analyzer.getInstance().getPercentIbOnce(),1.0);
    }

    @Test
    public void ZipcodeTransactionsAreSorted()
    {
        Analyzer analyzer = new Analyzer();
        List<String> list = analyzer.getInstance().getTopZips();
        for (int i = 0; i < 4; i ++)
        {

            int val1 = Integer.parseInt(list.get(i).split(" ")[1]);
            int val2 = Integer.parseInt(list.get(i + 1).split(" ")[1]);
            Assertions.assertTrue(val1 > val2);
        }
    }

    @Test
    public void FraudByYearProducesCorrectNumbers()
    {
        Analyzer analyzer = new Analyzer();
        HashMap<Integer, String> fraudMap = analyzer.getInstance().getFraudByYear();
        Assertions.assertEquals(fraudMap.get(2016),"%0.18");
    }
}
