package com.cbt.ws.main;

import com.cbt.ws.annotations.TestFileStorePath;
import com.google.inject.AbstractModule;

public class ConfigModule extends AbstractModule {
	
	//private static final String TESTPACKAGE_STORE_PATH = "C://Dev//CBT//ws-store//";
	private static final String TESTPACKAGE_STORE_PATH = "/home/saulius/Documents/cbt/storage/";
	
	@Override
	protected void configure() {
		bind(String.class).annotatedWith(TestFileStorePath.class).toInstance(TESTPACKAGE_STORE_PATH);		
	}

}
