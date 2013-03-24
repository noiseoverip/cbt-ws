package com.cbt.ws.services;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.jooq.enums.DeviceJobStatus;
import com.cbt.ws.main.HelloGuiceServletConfig;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

//TODO: figure out how to slip in fake database...
/**
 * Unit test for {@link DeviceJobsWs}
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 * 
 */
public class DeviceJobsWsTest extends JerseyTest {

	private final Logger logger = Logger.getLogger(DeviceJobsWsTest.class);

	@Override
	protected AppDescriptor configure() {
		return new WebAppDescriptor.Builder().filterClass(GuiceFilter.class)
				.contextListenerClass(HelloGuiceServletConfig.class).build();
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

	// TODO: fix this
	/**
	 * Get on job with status WAITING, update it to CHECKEDOUT, verify response code, update it to WAITING, verify
	 * response code
	 */
	@Test
	public void testJobUpdate() {
		WebResource webResource = resource();
		Long deviceId = new Random().nextLong();

		DeviceJob deviceJob = webResource.path("devicejobs/waiting").queryParam("deviceId", deviceId.toString())
				.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE).get(DeviceJob.class);

		deviceJob.setStatus(DeviceJobStatus.CHECKEDOUT);

		ClientResponse response = webResource.path("devicejobs").type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, deviceJob);
		logger.info("Response fo change to CHECKEDOUT:" + response);
		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());

		deviceJob.setStatus(DeviceJobStatus.WAITING);

		response = webResource.path("devicejobs").type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, deviceJob);
		logger.info("Response of change to WAITING:" + response);
		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void testWaitingJobExisting() {
		WebResource webResource = resource();
		Long deviceId = new Random().nextLong();
		ClientResponse response = webResource.path("devicejobs/waiting").queryParam("deviceId", deviceId.toString())
				.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
		logger.info(response);
		assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
	}

}
