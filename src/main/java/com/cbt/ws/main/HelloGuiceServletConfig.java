package com.cbt.ws.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Guice servler context listener for creating injector and setting required modules
 * 
 * @author saulius
 *
 */
public class HelloGuiceServletConfig extends GuiceServletContextListener {
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new CbtJerseyServletModule(), new ConfigModule());
	}
}
