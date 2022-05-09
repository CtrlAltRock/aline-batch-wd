package com.alinebatch.alinebatchwd;

import com.alinebatch.alinebatchwd.config.BatchConfig;
import com.alinebatch.alinebatchwd.generators.GeneratorBean;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBatchTest
@Slf4j
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = BatchConfig.class)
@TestPropertySource(
		properties = {
				"inFile = /Users/willemduiker/Documents/test2.csv"
		}
)
class AlineBatchWdApplicationTests {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;

	@After
	public void cleanUp()
	{
		jobRepositoryTestUtils.removeJobExecutions();
	}

	private JobParameters defaultJobParameters() {
		JobParametersBuilder parametersBuilder = new JobParametersBuilder();
		return parametersBuilder.toJobParameters();
	}

	@Test
	public void jobExecutesOnFormattedData() throws Exception
	{
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		JobInstance actualJobInstance = jobExecution.getJobInstance();
		ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

		assertThat(actualJobInstance.getJobName(), is("transactionJob"));
		assertThat(actualJobExitStatus.getExitCode(), is("COMPLETED"));
	}

}
