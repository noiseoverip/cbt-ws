/**
 * 
 */
package com.cbt.ws.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

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
 * @author saulius
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
	public void testAddGetDevice() {
		WebResource webResource = resource();
		Device device = new Device();
		device.setUserId(1L);
		device.setDeviceTypeId(1L);
		device.setDeviceOsId(1L);
		String uniqueId = UUID.randomUUID().toString();
		device.setDeviceUniqueId(uniqueId);

		ClientResponse response = webResource.path("device").type(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.TEXT_HTML).put(ClientResponse.class, device);
		logger.info(response);

		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
		Long deviceId = Long.valueOf(response.getEntity(String.class));
		device.setId(deviceId);

		Device fetchedDevice = webResource.path("device/" + deviceId).accept(MediaType.APPLICATION_JSON)
				.get(Device.class);

		assertEquals(device, fetchedDevice);
	}

	@Test
	public void updateDevice() {
		WebResource webResource = resource();
		Long deviceId = 1L;
		Device device = webResource.path("device/" + deviceId).accept(MediaType.APPLICATION_JSON).get(Device.class);
		assertNotNull(device);

		device.setState(DeviceState.ONLINE);

		ClientResponse response = webResource.path("device/" + deviceId).type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, device);		
		assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		
		Device fetchedDevice = webResource.path("device/" + deviceId).accept(MediaType.APPLICATION_JSON)
				.get(Device.class);
		
		assertEquals(device, fetchedDevice);
		
		//Reset state back
		device.setState(DeviceState.OFFLINE);
		
		ClientResponse response2 = webResource.path("device/" + deviceId).type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, device);		
		assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response2.getStatus());		
		
		Device fetchedDevice2 = webResource.path("device/" + deviceId).accept(MediaType.APPLICATION_JSON)
				.get(Device.class);
		
		assertEquals(device, fetchedDevice2);
	}
}
