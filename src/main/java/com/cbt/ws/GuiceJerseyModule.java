package com.cbt.ws;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

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
public class GuiceJerseyModule extends JerseyServletModule {

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
		bind(DataSource.class).toProvider(DatabaseConnectionProvider.class).in(Singleton.class);
		bind(Configuration.class).in(Singleton.class);

		// hook Jackson into Jersey as the POJO <-> JSON mapper
		// bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
		// bind(JacksonJaxbJsonProvider.class).in(Scopes.SINGLETON);

		// Set configuration parameters
		Map<String, String> paramsSecure = new HashMap<String, String>();
		paramsSecure.put("com.sun.jersey.config.property.packages", "com.cbt.ws.services;org.codehaus.jackson.jaxrs");		
		// params.put("com.sun.jersey.config.property.packages","com.cbt.ws.services");
		paramsSecure.put("com.sun.jersey.config.feature.Trace", Boolean.TRUE.toString()); // enable tracing
		// params.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE.toString());
		paramsSecure.put("com.sun.jersey.spi.container.ContainerRequestFilters", AuthenticationFilter.class.getName());

		Map<String, String> paramsPublic = new HashMap<String, String>();
		paramsPublic.put("com.sun.jersey.config.property.packages", "com.cbt.ws.services;org.codehaus.jackson.jaxrs");		
		// enable tracing
		paramsPublic.put("com.sun.jersey.config.feature.Trace", Boolean.TRUE.toString()); 

		// Route all requests through GuiceContainer
		serve("/rip/*").with(GuiceContainer.class, paramsSecure);
	}
}
