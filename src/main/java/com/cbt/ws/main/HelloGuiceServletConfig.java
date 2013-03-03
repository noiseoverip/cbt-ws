package com.cbt.ws.main;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.cbt.ws.dao.TestPackageDao;
import com.cbt.ws.dao.TestTargetDao;
import com.cbt.ws.services.TestPackageWs;
import com.cbt.ws.services.TestTargetWs;
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
				// Must configure at least one JAX-RS resource or the
				// server will fail to start.
				bind(TestResource.class);
				
				// Bind CBT services
				bind(TestPackageWs.class);
				bind(TestTargetWs.class);
				
				bind(TestPackageDao.class);
				bind(TestTargetDao.class);
				
				 // hook Jackson into Jersey as the POJO <-> JSON mapper
				bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);

				// Route all requests through GuiceContainer
				serve("/*").with(GuiceContainer.class);
			}
		}, new ConfigModule());
	}
}
