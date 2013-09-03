package com.cbt.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Class for keeping all configuration options
 * 
 * @author SauliusAlisauskas
 * 
 */
public class Configuration {

	private static Logger mLogger = Logger.getLogger(Configuration.class);
	private Properties mProperties;
	private boolean mInTestingMode = false;

	public Properties getProperties() {
		if (null == mProperties) {
			mProperties = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream stream = loader.getResourceAsStream("datasource.properties");
			try {
				mProperties.load(stream);
			} catch (IOException e) {
				mLogger.fatal("Could not read project properties !!!");
			}
		}
		return mProperties;
	}

	/**
	 * This system property is to be set during execution, used to differentiate integration testing from deployment
	 * 
	 * @return
	 */
	public String getExecutionMode() {
		return System.getProperty("cbt.run.mode");
	}

	public boolean isInTestingMode() {
		return mInTestingMode;
	}

	public void setInTestingMode(boolean inTestingMode) {
		mInTestingMode = inTestingMode;
	}
}
