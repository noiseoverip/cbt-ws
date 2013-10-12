package com.cbt.ws.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.cbt.ws.GuiceContextListener;
import com.cbt.core.entity.complex.TestConfigComplex;
import com.cbt.ws.testtools.ClientAuthFilter;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

/**
 * Unit test for {@link DeviceJobsWs}
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 * 
 */
public class ITtestconfigWs extends JerseyTest {
	private static final String PATH_PREFIX = "rip";
	private final Logger logger = Logger.getLogger(ITtestconfigWs.class);
	private ClientAuthFilter authFilter = new ClientAuthFilter();

	@Override
	protected AppDescriptor configure() {
		return new WebAppDescriptor.Builder().filterClass(GuiceFilter.class)
				.contextListenerClass(GuiceContextListener.class).build();
	}
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		client().addFilter(authFilter);
	}
	
	//TODO: should test create/update/delete and not relay on test data
	@Test
	public void testGetTestConfigComplex() throws ParseException {		
		TestConfigComplex[] testConfigs = getWebResource().path("testconfig").type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON_TYPE).get(TestConfigComplex[].class);
		logger.info(Arrays.toString(testConfigs));			
				
		// Verify values, we only care about first test-config that must be of id = 1 (other test might have created other test configs...)
		assertTrue(testConfigs.length > 0);
		boolean found = false;
		for (int i=0; i< testConfigs.length; i++) {
			if (testConfigs[i].getId() == 1) {
				TestConfigComplex testConfig = testConfigs[i];		
				assertEquals(Long.valueOf(1L), testConfig.getId());
				assertEquals(Long.valueOf(1L), testConfig.getTestProfile().getId());
				assertEquals(Long.valueOf(1L), testConfig.getTestScript().getId());
				assertEquals(Long.valueOf(1L), testConfig.getTestTarget().getId());
				found = true;
			}
		}
		assertTrue(found);
	}
	
	/**
	 * Helper method to construct web resource base
	 * 
	 * @return
	 */
	private WebResource getWebResource() {
		return resource().path(PATH_PREFIX);
	}

}
