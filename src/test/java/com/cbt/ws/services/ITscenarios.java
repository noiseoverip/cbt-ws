package com.cbt.ws.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.cbt.core.entity.Device;
import com.cbt.core.entity.DeviceJob;
import com.cbt.core.entity.DeviceJobResult;
import com.cbt.core.entity.TestConfig;
import com.cbt.core.entity.TestProfile;
import com.cbt.core.entity.complex.TestConfigComplex;
import com.cbt.jooq.enums.DeviceJobDeviceJobStatus;
import com.cbt.jooq.enums.DeviceJobResultState;
import com.cbt.jooq.enums.DeviceState;
import com.cbt.jooq.enums.TestprofileTestprofileMode;
import com.cbt.jooq.enums.TestrunTestrunStatus;
import com.cbt.ws.GuiceContextListener;
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.testtools.ClientAuthFilter;
import com.cbt.ws.testtools.GetTestRunResponse;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

/**
 * Unit test for {@link DeviceJobsWs}
 *
 * @author SauliusAlisauskas 2013-03-24 Initial version
 */
public class ITscenarios extends JerseyTest {
   private static final String PATH_PREFIX = "rip";
   private final Logger logger = Logger.getLogger(ITscenarios.class);
   private final ClientAuthFilter authFilter = new ClientAuthFilter();
   private final long testUser1Id = 1L;
   private final long testScriptId = 1L;
   private final long testTargetId = 1L;

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

   //TODO: move to separate IT
   @Test
   public void testAddGetUpdateGetByUidDevice() throws InterruptedException {
      // Create device object for testing
      Device device = new Device();
      device.setDeviceTypeId(1L);
      device.setDeviceOsId(1L);
      device.setSerialNumber(String.valueOf(new Random().nextLong()));
      device.setOwnerId(1L);

      // Add device
      logger.info("Adding new device");
      ClientResponse response = getWebResource().path("device").type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.TEXT_HTML).put(ClientResponse.class, device);
      logger.info(response);
      assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
      // Extract newly created device id
      Long deviceId = Long.valueOf(response.getEntity(String.class));
      device.setId(deviceId);

      // Add same device one more time, make sure we get response CONFLICT
      ClientResponse responseAd2 = getWebResource().path("device").type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.TEXT_HTML).put(ClientResponse.class, device);
      logger.info(responseAd2);
      assertEquals(ClientResponse.Status.CONFLICT.getStatusCode(), responseAd2.getStatus());

      // Get device
      Device fetchedDevice = getWebResource().path("device").path(deviceId.toString()).accept(MediaType.APPLICATION_JSON)
            .get(Device.class);
      assertEquals(device, fetchedDevice);

      // Get device by unique id
      Device deviceByUid = getWebResource().path("device").type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.APPLICATION_JSON).post(Device.class, device);
      assertEquals(device, deviceByUid);

      // Update device
      device.setState(DeviceState.ONLINE);
      ClientResponse responseUpdate = getWebResource().path("device/" + deviceId).type(MediaType.APPLICATION_JSON_TYPE)
            .post(ClientResponse.class, device);
      assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), responseUpdate.getStatus());
      Device deviceAfterUpdate = getWebResource().path("device/" + deviceId).accept(MediaType.APPLICATION_JSON)
            .get(Device.class);
      assertEquals(deviceAfterUpdate.getState(), DeviceState.ONLINE);

      // Delete device
      ClientResponse deleteResponse = getWebResource().path("device/" + deviceId).delete(ClientResponse.class);
      assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
   }

   /**
    * Test scenario 1:
    *
    * 1. Create a number of devices
    * 2. Create test profile
    * 2.1 Verify it is listed
    * 3. Create test configuration
    * 3.1 Verify it is listed
    * 4. Create testrun
    * 4.1 Verify that it was configured to run on our devices
    * 4.2 Verify correct device jobs have been created
    * 5. Post fake results of device jobs
    * 5.1 Verify that test run state was set to finished
    */
   @Test
   public void scenario1() {
      final int numberOfDevices = 5;
      final int numberOfTestRuns = 20;
      // Create devices for testing
      Device[] devices = getTestDevices(numberOfDevices);
      for (Device device : devices) {
         logger.info("Adding device:" + device.getSerialNumber());
         ClientResponse response = getWebResource().path("device").type(MediaType.APPLICATION_JSON_TYPE)
               .accept(MediaType.TEXT_HTML).put(ClientResponse.class, device);
         assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
         // Extract newly created device id
         Long deviceId = Long.valueOf(response.getEntity(String.class));
         assertNotNull(deviceId);
         assertTrue(deviceId > 0);
         device.setId(deviceId);
         // Update device state to ONLINE
         device.setState(DeviceState.ONLINE);
         ClientResponse deviceUpdateResponse = getWebResource().path("device").path(String.valueOf(device.getId()))
               .type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.TEXT_HTML)
               .post(ClientResponse.class, device);
         assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), deviceUpdateResponse.getStatus());
      }

      // Create test profile
      TestProfile testprofile = new TestProfile();
      testprofile.setDeviceTypes(Arrays.asList(new Long[]{1L}));
      testprofile.setMode(TestprofileTestprofileMode.NORMAL);
      testprofile.setName(UUID.randomUUID().toString());
      ClientResponse response = getWebResource().path("testprofile").type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, testprofile);
      assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
      // Verify that our test profile will be listed
      TestProfile[] userTestProfiles = getWebResource().path("testprofile").accept(MediaType.APPLICATION_JSON)
            .get(TestProfile[].class);
      boolean foundOurTestProfile = false;
      for (TestProfile testProfileTemp : userTestProfiles) {
         if (testProfileTemp.getName().equals(testprofile.getName())) {
            foundOurTestProfile = true;
            testprofile.setId(testProfileTemp.getId());
            break;
         }
      }
      assertTrue("Could not find created test profile", foundOurTestProfile);

      // Create test configuration
      TestConfig testConfiguration = new TestConfig();
      testConfiguration.setName(UUID.randomUUID().toString());
      testConfiguration.setTestProfileId(testprofile.getId());
      testConfiguration.setTestScriptId(testScriptId);
      testConfiguration.setTestTargetId(testTargetId);
      logger.info("Creating test configuration:" + testConfiguration);
      TestConfig newTestConfig = getWebResource().path("testconfig").type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.APPLICATION_JSON_TYPE).put(TestConfig.class, testConfiguration);
      assertTrue(newTestConfig.getId() > 0);
      logger.info("Created test configuration:" + newTestConfig);
      testConfiguration.setId(newTestConfig.getId());
      assertEquals(testConfiguration, newTestConfig);
      // Verify that our newly created test configuration will be listed
      // TODO: this doesn't work if testscript or testtarget do not exist !
      TestConfigComplex[] userTestConfigs = getWebResource().path("testconfig").accept(MediaType.APPLICATION_JSON)
            .get(TestConfigComplex[].class);
      boolean foundOurTestConfig = false;
      for (TestConfigComplex testConfigCom : userTestConfigs) {
         if (testConfigCom.getId().equals(testConfiguration.getId())) {
            // TODO: this is not right, should be no need to use TestConfigComplex object at all !
            assertEquals(testConfiguration.getName(), testConfigCom.getName());
            assertEquals(testConfiguration.getTestProfileId(), testConfigCom.getTestProfile().getId());
            assertEquals(testConfiguration.getTestScriptId(), testConfigCom.getTestScript().getId());
            assertEquals(testConfiguration.getTestTargetId(), testConfigCom.getTestTarget().getId());
            foundOurTestConfig = true;
         }
      }
      assertTrue(foundOurTestConfig);

      for (int i = 0; i < numberOfTestRuns; i++) {
         logger.info("Executing test run:" + i);
         List<DeviceJob> jobs = performCbtTestRun(numberOfDevices, testConfiguration.getId(), devices);

         // Remove device jobs, results should be removed by system
         for (DeviceJob deviceJob : jobs) {
            getWebResource().path("devicejob").path(String.valueOf(deviceJob.getId())).delete();
         }
      }

      // Remove devices
//		for (Device device : devices) {
//			logger.info("Removing device:" + device);
//			ClientResponse deleteResponse = getWebResource().path("device").path(device.getId().toString()).delete(ClientResponse.class);
//			assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
//		}
   }

   /**
    * Helper method to create and verify test run
    *
    * @param numberOfDevices
    * @param testConfigId
    * @param devices
    * @return
    */
   private List<DeviceJob> performCbtTestRun(int numberOfDevices, Long testConfigId, Device[] devices) {
      // Create test run
      TestRun testRun = new TestRun();
      testRun.setName(UUID.randomUUID().toString());
      testRun.setTestconfigId(testConfigId);
      logger.info("Creating test run:" + testRun);
      TestRun newTestRun = getWebResource().path("testrun").type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.APPLICATION_JSON_TYPE).put(TestRun.class, testRun);
      assertTrue(newTestRun.getId() > 0);
      logger.info("Created test run:" + newTestRun);
      testRun.setId(newTestRun.getId());
      assertEquals(testRun.getName(), newTestRun.getName());
      assertEquals(testRun.getTestconfigId(), newTestRun.getTestconfigId());
      assertEquals(TestrunTestrunStatus.WAITING, newTestRun.getStatus());

      // Verify that test run will be performed our created devices
      assertEquals(devices.length, newTestRun.getDevices().size());
      List<DeviceJob> deviceJobs = new ArrayList<DeviceJob>(numberOfDevices);
      for (Device device : devices) {
         boolean found = false;
         for (Device deviceTemp : newTestRun.getDevices()) {
            if (device.getId().equals(deviceTemp.getId())) {
               found = true;
            }
         }
         assertTrue(found);
         // Verify device job has been created
         DeviceJob[] deviceJobsTemp = getWebResource().path("devicejob")
               .queryParam("deviceId", String.valueOf(device.getId()))
               .queryParam("testRunId", String.valueOf(testRun.getId())).accept(MediaType.APPLICATION_JSON)
               .get(DeviceJob[].class);
         assertNotNull(deviceJobsTemp);
         assertTrue(deviceJobsTemp.length == 1);
         assertEquals(deviceJobsTemp[0].getStatus(), DeviceJobDeviceJobStatus.WAITING);
         deviceJobs.add(deviceJobsTemp[0]);
      }

      // Post device job results
      for (DeviceJob deviceJob : deviceJobs) {
         DeviceJobResult result = new DeviceJobResult();
         result.setDevicejobId(deviceJob.getId());
         result.setOutput(UUID.randomUUID().toString());
         result.setState(DeviceJobResultState.PASSED);
         result.setTestsRun(1);
         result.setTestsErrors(1);
         result.setTestsFailed(1);
         DeviceJobResult newDeviceJobResult = getWebResource().path("devicejob").path(String.valueOf(deviceJob.getId()))
               .path("result").type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE)
               .put(DeviceJobResult.class, result);
         assertNotNull(newDeviceJobResult);
         assertEquals(result.getDevicejobId(), newDeviceJobResult.getDevicejobId());
         assertEquals(result.getState(), newDeviceJobResult.getState());
         assertEquals(result.getTestsErrors(), newDeviceJobResult.getTestsErrors());
         assertEquals(result.getTestsFailed(), newDeviceJobResult.getTestsFailed());
         assertEquals(result.getTestsRun(), newDeviceJobResult.getTestsRun());
      }

      // Verify that test run was set to finish and state passed after we have updated all jobs to finish
      GetTestRunResponse response = getWebResource().path("testrun").queryParam("offset", "0").queryParam("max", "10")
            .type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE)
            .get(GetTestRunResponse.class);
      boolean foundOurTestRun = false;
      for (TestRun testRunTemp : response.getResults()) {
         if (testRunTemp.getId().equals(testRun.getId())) {
            foundOurTestRun = true;
            assertTrue(testRunTemp.getStatus().equals(TestrunTestrunStatus.PASSED));
         }
      }
      assertTrue(foundOurTestRun);
      return deviceJobs;
   }

   private Device[] getTestDevices(int number) {
      Device[] devices = new Device[number];
      for (int i = 0; i < number; i++) {
         Device device = new Device();
         device.setDeviceTypeId(1L);
         device.setDeviceOsId(1L);
         device.setSerialNumber(UUID.randomUUID().toString());
         device.setOwnerId(testUser1Id);
         devices[i] = device;
      }
      return devices;
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
