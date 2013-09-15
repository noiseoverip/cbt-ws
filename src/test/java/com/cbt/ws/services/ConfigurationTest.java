package com.cbt.ws.services;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.cbt.ws.GuiceConfigModule;
import com.cbt.ws.Configuration;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ConfigurationTest {
	private final Logger logger = Logger.getLogger(ConfigurationTest.class);
	
	@Test
	public void test1() {
		Injector injector = Guice.createInjector(new GuiceConfigModule());
		Configuration configuration = injector.getInstance(Configuration.class);
		logger.info(configuration.toString());
	}
}
