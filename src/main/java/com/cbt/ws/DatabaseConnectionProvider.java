package com.cbt.ws;

import java.sql.Connection;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Guice provider for {@link Connection}
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: Ericsson
 * </p>
 * 
 * @author Saulius Alisauskas
 * 
 */
public class DatabaseConnectionProvider implements Provider<ComboPooledDataSource> {
	private ComboPooledDataSource mCpds;
	private final Logger mLogger = Logger.getLogger(DatabaseConnectionProvider.class);
	
	
	private Configuration mConfiguration;
	
	@Inject
	public DatabaseConnectionProvider(Configuration configuration) {
		mConfiguration = configuration;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboPooledDataSource get() {
		Properties props = mConfiguration.getProperties();
		
		//This is needed for Tomcat to work !----------------
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			mLogger.error("MySQL driver class not found !");
		}
		//----------------------------------------------------
		mCpds = new ComboPooledDataSource();
		
		String runMode = mConfiguration.getExecutionMode();
		if (null != runMode && runMode.equals("integration")) {
			mConfiguration.setInTestingMode(true);
			// Integration testing
			mCpds.setJdbcUrl(props.getProperty("db_test_jdbc_url"));
			mCpds.setUser(props.getProperty("db_test_user"));
			mCpds.setPassword(props.getProperty("db_test_password"));
		} else {
			// Production
			mConfiguration.setInTestingMode(false);
			mCpds.setJdbcUrl(props.getProperty("db_jdbc_url"));
			mCpds.setUser(props.getProperty("db_user"));
			mCpds.setPassword(props.getProperty("db_password"));
		}		
		
		mLogger.info("Connecting to database at:" + mCpds.getJdbcUrl());
		mCpds.setInitialPoolSize(new Integer(props.getProperty("initialPoolSize")));
		mCpds.setAcquireIncrement(new Integer(props.getProperty("acquireIncrement")));
		mCpds.setMaxPoolSize(new Integer(props.getProperty("maxPoolSize")));
		mCpds.setMinPoolSize(new Integer(props.getProperty("minPoolSize")));
		mCpds.setMaxStatements(new Integer(props.getProperty("maxStatements")));
		mCpds.setMaxIdleTime(new Integer(props.getProperty("maxIdleTime")));
		return mCpds;
	}
}
