package com.cbt.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class GuiceConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		// Enable access to properties through @Named("propertyName")
		Names.bindProperties(binder(), getProperties());
		bind(Configuration.class).in(Singleton.class);;
	}

	private Properties getProperties() {
		Properties mProperties = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("cbt.properties");
		try {
			mProperties.load(stream);
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).fatal("Could not read project properties !!!");
		}

		return mProperties;
	}
}
