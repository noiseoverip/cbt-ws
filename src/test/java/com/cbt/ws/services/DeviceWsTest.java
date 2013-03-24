package com.cbt.ws.services;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.cbt.ws.entity.Device;
import com.cbt.ws.jooq.enums.DeviceState;
import com.cbt.ws.main.HelloGuiceServletConfig;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

//TODO: figure out how to slip in fake database...
/**
 * Unit test for {@link DeviceWs}
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 * 
 */
public class DeviceWsTest extends JerseyTest {

	private final Logger logger = Logger.getLogger(DeviceWsTest.class);

	@Override
	protected AppDescriptor configure() {
		return new WebAppDescriptor.Builder().filterClass(GuiceFilter.class)
				.contextListenerClass(HelloGuiceServletConfig.class).build();
	}

	@Test
	public void testAddGetUpdateGetByUidDevice() {
		WebResource webResource = resource();
		Device device = new Device();
		device.setUserId(1L);
		device.setDeviceTypeId(1L);
		device.setDeviceOsId(1L);
		device.setSerialNumber(String.valueOf(new Random().nextLong()));
		
		//Add device
		ClientResponse response = webResource.path("device").type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.TEXT_HTML).put(ClientResponse.class, device);
		logger.info(response);
		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
		Long deviceId = Long.valueOf(response.getEntity(String.class));
		device.setId(deviceId);
		
		//Add same devie one more time
		ClientResponse responseAd2 = webResource.path("device").type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.TEXT_HTML).put(ClientResponse.class, device);
		logger.info(responseAd2);
		assertEquals(ClientResponse.Status.CONFLICT.getStatusCode(), responseAd2.getStatus());

		
		// Get device
		Device fetchedDevice = webResource.path("device/" + deviceId).accept(MediaType.APPLICATION_JSON)
				.get(Device.class);
		assertEquals(device, fetchedDevice);
		
		// Get device by unique id
		Device deviceByUid = webResource.path("device").type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON)
				.post(Device.class, device);		
		assertEquals(device, deviceByUid);
		
		// Update device
		device.setState(DeviceState.ONLINE);
		ClientResponse responseUpdate = webResource.path("device/" + deviceId).type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, device);		
		assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), responseUpdate.getStatus());		
		Device deviceAfterUpdate = webResource.path("device/" + deviceId).accept(MediaType.APPLICATION_JSON)
				.get(Device.class);		
		assertEquals(deviceAfterUpdate.getState(), DeviceState.ONLINE);
		
		// Delete device
		ClientResponse deleteResponse = webResource.path("device/" + deviceId).delete(ClientResponse.class);
		assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
	}
}
