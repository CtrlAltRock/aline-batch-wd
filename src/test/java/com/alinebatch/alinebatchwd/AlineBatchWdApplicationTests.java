package com.alinebatch.alinebatchwd;

import com.alinebatch.alinebatchwd.generators.GeneratorBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class AlineBatchWdApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void UserGenerates()
	{
		GeneratorBean gb = new GeneratorBean();
		try
		{
			gb.getUser(0);
		} catch (Exception e)
		{
			log.info(e.getMessage());
		}

	}

}
