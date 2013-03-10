package com.cbt.ws.main;

import java.util.HashMap;
import java.util.Map;

import com.cbt.ws.dao.CheckoutDao;
import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.TestConfigDao;
import com.cbt.ws.dao.TestProfileDao;
import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.dao.TestScriptDao;
import com.cbt.ws.dao.TestTargetDao;
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
		//bind(TestPackageWs.class);
		//bind(TestTargetWs.class);
		//bind(TestConfigWs.class);
		
		bind(TestScriptDao.class);
		bind(TestTargetDao.class);
		bind(TestProfileDao.class);
		bind(TestConfigDao.class);
		bind(TestRunDao.class);
		bind(DevicejobDao.class);
		bind(CheckoutDao.class);
		
		 // hook Jackson into Jersey as the POJO <-> JSON mapper
		//bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
		//bind(JacksonJaxbJsonProvider.class).in(Scopes.SINGLETON);
		
		// Set configuration parameters
		Map<String, String> params = new HashMap<String, String>();
		params.put("com.sun.jersey.config.property.packages","com.cbt.ws.services;org.codehaus.jackson.jaxrs"); // scan for resources
		//params.put("com.sun.jersey.config.property.packages","com.cbt.ws.services");
		params.put("com.sun.jersey.config.feature.Trace", Boolean.TRUE.toString()); // enable tracing
		//params.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE.toString());
		
		// Route all requests through GuiceContainer
		serve("/*").with(GuiceContainer.class, params);
	}
}
