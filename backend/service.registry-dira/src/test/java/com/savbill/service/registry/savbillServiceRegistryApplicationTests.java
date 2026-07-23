package com.savbill.service.registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class savbillServiceRegistryApplicationTests {

	private static Log logger = LogFactory.getLog(savbillServiceRegistryApplicationTests.class);
	@Test
	public void contextLoads() {
		logger.info("Context load successfully");
	}

}
