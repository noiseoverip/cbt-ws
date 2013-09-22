package com.cbt.ws.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.cbt.ws.GuiceContextListener;
import com.cbt.ws.entity.DeviceJob;
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
public class ITdeviceJobsWs extends JerseyTest {
	private static final String PATH_PREFIX = "rip";
	private final Logger logger = Logger.getLogger(ITdeviceJobsWs.class);
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
	
	@Test
	public void testJobsList() {

		DeviceJob[] jobs = getWebResource().path("devicejob").queryParam("deviceId", "1").accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE).get(DeviceJob[].class);
		logger.info(Arrays.toString(jobs));
		
		assertTrue(jobs.length == 1);
		DeviceJob job = jobs[0];
		assertEquals(Long.valueOf(1L), job.getId());
		assertEquals(Long.valueOf(1L), job.getDeviceId());
		assertEquals(Long.valueOf(1L), job.getTestRunId());
		assertNotNull(job.getTestScript());
		assertNotNull(job.getTestTarget());
		
	}

	
	/**
	 * Helper method to construct web resource base
	 * 
	 * @return
	 */
	private WebResource getWebResource() {
		return resource().path(PATH_PREFIX);
	}

//	// TODO: fix this
//	/**
//	 * Get on job with status WAITING, update it to CHECKEDOUT, verify response code, update it to WAITING, verify
//	 * response code
//	 */
//	@Test
//	public void testJobUpdate() {
//		WebResource webResource = resource();
//		Long deviceId = new Random().nextLong();
//
//		DeviceJob deviceJob = webResource.path("devicejobs/waiting").queryParam("deviceId", deviceId.toString())
//				.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE).get(DeviceJob.class);
//
//		deviceJob.setStatus(DeviceJobStatus.CHECKEDOUT);
//
//		ClientResponse response = webResource.path("devicejobs").type(MediaType.APPLICATION_JSON_TYPE)
//				.post(ClientResponse.class, deviceJob);
//		logger.info("Response fo change to CHECKEDOUT:" + response);
//		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
//
//		deviceJob.setStatus(DeviceJobStatus.WAITING);
//
//		response = webResource.path("devicejobs").type(MediaType.APPLICATION_JSON_TYPE)
//				.post(ClientResponse.class, deviceJob);
//		logger.info("Response of change to WAITING:" + response);
//		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
//	}
//
//	@Test
//	public void testWaitingJobExisting() {
//		WebResource webResource = resource();
//		Long deviceId = new Random().nextLong();
//		ClientResponse response = webResource.path("devicejobs/waiting").queryParam("deviceId", deviceId.toString())
//				.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
//		logger.info(response);
//		assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
//	}

}
