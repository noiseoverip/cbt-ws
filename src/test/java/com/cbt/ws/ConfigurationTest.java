package com.cbt.ws;

import org.apache.log4j.Logger;

import com.cbt.ws.GuiceConfigModule;
import com.cbt.ws.Configuration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

public class ConfigurationTest {
	private final Logger logger = Logger.getLogger(ConfigurationTest.class);
	
	@Test
	public void test1() {
		Injector injector = Guice.createInjector(new GuiceConfigModule());
		Configuration configuration = injector.getInstance(Configuration.class);
		logger.info(configuration.toString());
		//TODO: add assert values not null
	}
}
