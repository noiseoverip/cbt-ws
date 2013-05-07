package com.cbt.ws.main;

import java.util.HashMap;
import java.util.Map;

import com.cbt.ws.dao.CheckoutDao;
import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.DevicejobResultDao;
import com.cbt.ws.dao.TestConfigDao;
import com.cbt.ws.dao.TestProfileDao;
import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.dao.TestScriptDao;
import com.cbt.ws.dao.TestTargetDao;
import com.cbt.ws.dao.UserDao;
import com.cbt.ws.workers.DeviceStateMonitor;
import com.google.inject.Singleton;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Main guice module for setting Dao binding as well as jersey configuration
 * 
 * @author saulius
 * 
 */
public class CbtJerseyServletModule extends JerseyServletModule {

	@Override
	protected void configureServlets() {
		// Bind CBT services (will need this later for testing purposes)
		// bind(TestPackageWs.class);
		// bind(TestTargetWs.class);
		// bind(TestConfigWs.class);

		bind(TestScriptDao.class);
		bind(TestTargetDao.class);
		bind(TestProfileDao.class);
		bind(TestConfigDao.class);
		bind(TestRunDao.class);
		bind(DevicejobDao.class);
		bind(DevicejobResultDao.class);
		bind(CheckoutDao.class);
		bind(DeviceDao.class);
		bind(UserDao.class);
		bind(WorkerManager.class).in(Singleton.class);
		bind(DeviceStateMonitor.class);
		// bind(AuthenticationFilter.class).in(Singleton.class);

		// hook Jackson into Jersey as the POJO <-> JSON mapper
		// bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
		// bind(JacksonJaxbJsonProvider.class).in(Scopes.SINGLETON);

		// Set configuration parameters
		Map<String, String> paramsSecure = new HashMap<String, String>();
		paramsSecure.put("com.sun.jersey.config.property.packages", "com.cbt.ws.services;org.codehaus.jackson.jaxrs"); // scan
																														// for
																														// resources
		// params.put("com.sun.jersey.config.property.packages","com.cbt.ws.services");
		paramsSecure.put("com.sun.jersey.config.feature.Trace", Boolean.TRUE.toString()); // enable tracing
		// params.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE.toString());
		paramsSecure.put("com.sun.jersey.spi.container.ContainerRequestFilters", AuthenticationFilter.class.getName());

		Map<String, String> paramsPublic = new HashMap<String, String>();
		paramsPublic.put("com.sun.jersey.config.property.packages", "com.cbt.ws.services;org.codehaus.jackson.jaxrs"); // scan
																														// for
																														// resources
		// params.put("com.sun.jersey.config.property.packages","com.cbt.ws.services");
		paramsPublic.put("com.sun.jersey.config.feature.Trace", Boolean.TRUE.toString()); // enable tracing

		// Route all requests through GuiceContainer
		serve("/*").with(GuiceContainer.class, paramsSecure);
	}
}
