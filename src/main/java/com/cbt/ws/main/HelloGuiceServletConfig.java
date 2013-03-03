package com.cbt.ws.main;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.TestConfigDao;
import com.cbt.ws.dao.TestPackageDao;
import com.cbt.ws.dao.TestProfileDao;
import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.dao.TestTargetDao;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class HelloGuiceServletConfig extends GuiceServletContextListener {
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JerseyServletModule() {
			
			@Override
			protected void configureServlets() {				
				// Bind CBT services (will need this later for testing purposes)
				//bind(TestPackageWs.class);
				//bind(TestTargetWs.class);
				//bind(TestConfigWs.class);
				
				bind(TestPackageDao.class);
				bind(TestTargetDao.class);
				bind(TestProfileDao.class);
				bind(TestConfigDao.class);
				bind(TestRunDao.class);
				bind(DevicejobDao.class);
				
				 // hook Jackson into Jersey as the POJO <-> JSON mapper
				bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
				
				// Set configuration parameters
				Map<String, String> params = new HashMap<String, String>();
				params.put("com.sun.jersey.config.property.packages","com.cbt.ws.services"); // scan for resources
				params.put("com.sun.jersey.config.feature.Trace", "true"); // enable tracing
				
				// Route all requests through GuiceContainer
				serve("/*").with(GuiceContainer.class, params);				
			}
		}, new ConfigModule());
	}
}
