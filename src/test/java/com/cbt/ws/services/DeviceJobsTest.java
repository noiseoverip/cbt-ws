/**
 * 
 */
package com.cbt.ws.services;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.jooq.enums.DevicejobsStatus;
import com.cbt.ws.main.HelloGuiceServletConfig;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

//TODO: figure out how to slip in fake database...
/**
 * @author saulius
 * 
 */
public class DeviceJobsTest extends JerseyTest {

	private final Logger logger = Logger.getLogger(DeviceJobsTest.class);

	@Override
	protected AppDescriptor configure() {
		return new WebAppDescriptor.Builder().filterClass(GuiceFilter.class)
				.contextListenerClass(HelloGuiceServletConfig.class)
				.build();
	}

	@Test
	public void testWaitingJob() {
		WebResource webResource = resource();
		DeviceJob response = webResource.path("devicejobs/waiting").queryParam("deviceId", "1")
				.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE).get(DeviceJob.class);
		logger.info(response);
		// DeviceJob job = response.getEntity(new GenericType<DeviceJob>(){});
		assertEquals(Long.valueOf(1), response.getDeviceId());
	}

	@Test
	public void testJobsList() {
		WebResource webResource = resource();
		DeviceJob[] jobs = webResource.path("devicejobs").accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE).get(DeviceJob[].class);
		logger.info(jobs);
		// DeviceJob job = response.getEntity(new GenericType<DeviceJob>(){});
		assertEquals(Long.valueOf(1), jobs[0].getDeviceId());
	}
	
	/**
	 * Get on job with status WAITING, update it to CHECKEDOUT, verify response code, update it to WAITING, verify response code
	 */
	@Test
	public void testJobUpdate() {
		WebResource webResource = resource();
		DeviceJob deviceJob = webResource.path("devicejobs/waiting").queryParam("deviceId", "1")
				.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE).get(DeviceJob.class);
		
		deviceJob.setStatus(DevicejobsStatus.CHECKEDOUT);
		
		ClientResponse response = webResource.path("devicejobs").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, deviceJob);
		logger.info("Response fo change to CHECKEDOUT:" + response);
		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
		
		deviceJob.setStatus(DevicejobsStatus.WAITING);
		
		response = webResource.path("devicejobs").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, deviceJob);
		logger.info("Response of change to WAITING:" + response);
		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
	}

}
