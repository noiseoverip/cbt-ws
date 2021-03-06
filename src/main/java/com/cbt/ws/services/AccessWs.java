package com.cbt.ws.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.jooq.exception.DataAccessException;

import com.cbt.core.entity.Device;
import com.cbt.core.entity.DeviceJob;
import com.cbt.core.entity.DeviceJobResult;
import com.cbt.core.entity.DeviceSharing;
import com.cbt.core.entity.DeviceSharingGroup;
import com.cbt.core.entity.DeviceType;
import com.cbt.core.entity.TestConfig;
import com.cbt.core.entity.TestPackage;
import com.cbt.core.entity.TestProfile;
import com.cbt.core.entity.TestScript;
import com.cbt.core.entity.TestTarget;
import com.cbt.core.entity.User;
import com.cbt.core.entity.complex.TestConfigComplex;
import com.cbt.core.exceptions.CbtDaoException;
import com.cbt.core.utils.Utils;
import com.cbt.jooq.enums.DeviceDeviceState;
import com.cbt.jooq.enums.DeviceJobDeviceJobStatus;
import com.cbt.jooq.enums.DeviceJobResultState;
import com.cbt.jooq.enums.TestrunTestrunStatus;
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
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.exceptions.CbtNoDevicesException;
import com.cbt.ws.security.CbtPrinciple;
import com.cbt.ws.tools.TestRunProccessor;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Access web service. Provide authentication and user related API
 *
 * @author SauliusAlisauskas 2013-04-09 Initial version
 */
@Path("/")
public class AccessWs {

   private static final String QPARAM_USERID = "userId";
   private static final String QPARAM_ID = "id";
   private final Logger mLogger = Logger.getLogger(AccessWs.class);
   private final CheckoutDao mCheckoutDao;
   @Context
   private SecurityContext mContext;
   private final DeviceDao mDeviceDao;
   private final DevicejobDao mDeviceJobDao;
   private final DevicejobResultDao mDeviceJobResultDao;
   private final TestConfigDao mTestConfigDao;
   private final TestProfileDao mTestProfileDao;
   private final TestRunDao mTestRunDao;
   private final TestRunProccessor mTestRunProcessor;
   private final TestScriptDao mTestScriptDao;
   private final TestTargetDao mTestTargetDao;
   private final UserDao mUserDao;

   @Inject
   public AccessWs(UserDao dao, TestProfileDao testProfileDao, TestRunDao testRunDao, TestConfigDao testConfigDao,
         TestRunProccessor testRunProccessor, DevicejobDao deviceJobDao, DevicejobResultDao deviceJobResultDao,
         CheckoutDao checkoutDao, TestScriptDao testScriptDao, TestTargetDao testTargetDao, DeviceDao deviceDao) {
      mUserDao = dao;
      mTestProfileDao = testProfileDao;
      mTestRunDao = testRunDao;
      mTestConfigDao = testConfigDao;
      mTestRunProcessor = testRunProccessor;
      mDeviceJobDao = deviceJobDao;
      mDeviceJobResultDao = deviceJobResultDao;
      mTestScriptDao = testScriptDao;
      mTestTargetDao = testTargetDao;
      mDeviceDao = deviceDao;
      mCheckoutDao = checkoutDao;
   }

   /**
    * Delete device by device id
    *
    * @param deviceId
    * @throws CbtDaoException
    */
   @DELETE
   @Path("/device/{deviceId}")
   public void deleteDevice(@PathParam("deviceId") Long deviceId) throws CbtDaoException {
      mDeviceDao.deleteDevice(deviceId);
   }

   /**
    * Delete devicJob
    *
    * @param deviceJobId
    * @throws CbtDaoException
    */
   @DELETE
   @Path("/devicejob/{deviceJobId}")
   public void deleteDeviceJob(@PathParam("deviceJobId") Long deviceJobId) throws CbtDaoException {
      mDeviceJobDao.delete(deviceJobId);
      mDeviceJobResultDao.delete(deviceJobId);
   }

   @DELETE
   @Path("/device/{deviceId}/share/user/{shareId}")
   public void deleteSharingUsers(@PathParam("deviceId") Long deviceId, @PathParam("shareId") Long shareId)
         throws CbtDaoException {
      Device device = mDeviceDao.getDevice(getUserId(), deviceId);
      if (null == device) {
         throw new WebApplicationException(Status.UNAUTHORIZED);
      }
      mDeviceDao.deleteSharingUser(shareId);
   }

   @DELETE
   @Path("/device/{deviceId}/share/group/{shareId}")
   public void deleteSharingGroups(@PathParam("deviceId") Long deviceId, @PathParam("shareId") Long shareId)
         throws CbtDaoException {
      Device device = mDeviceDao.getDevice(getUserId(), deviceId);
      if (null == device) {
         throw new WebApplicationException(Status.UNAUTHORIZED);
      }
      mDeviceDao.deleteSharingGroup(shareId);
   }

   @GET
   @Path("/user/all")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Map<String, Object>> getAllUsers() {
      return mUserDao.getAllUsers();
   }

   /**
    * Get device by device Id
    *
    * @param id - device id
    * @return
    */
   @GET
   @Path("/device/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Device getDevice(@PathParam(QPARAM_ID) Long id) {
      return mDeviceDao.getDevice(getUserId(), id);
   }

   // TODO: change to GET and use UID as parameter

   /**
    * Get device by UID
    *
    * @param device
    * @return
    */
   @POST
   @Path("/device")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Device getDeviceByUid(Device device) {
      String uniqueId = Utils.md5(Utils.buildContentForDeviceUniqueId(device));
      return mDeviceDao.getDeviceByUid(uniqueId);
   }

   /**
    * Get awayting device jobs
    *
    * @param deviceId
    * @return JSON string containing device job properties if any jobs available, 204 otherwise
    */
   @GET
   @Path("/devicejob")
   @Produces(MediaType.APPLICATION_JSON)
   public DeviceJob[] getDeviceJob(@QueryParam("deviceId") Long deviceId,
         @QueryParam(value = "testRunId") Long testRunId) {
      if (deviceId != null) {
         return mDeviceJobDao.getByDeviceId(deviceId);
      } else if (testRunId != null) {
         return mDeviceJobDao.getByTestRunId(testRunId);
      }
      return null;
   }

   /**
    * Get device job result
    *
    * @param deviceJobId
    * @return
    */
   @GET
   @Path("/devicejob/{deviceJobId}/result")
   @Produces(MediaType.APPLICATION_JSON)
   public DeviceJobResult[] getDeviceJobResults(@PathParam(value = "deviceJobId") Long deviceJobId) {
      return new DeviceJobResult[] { mDeviceJobResultDao.getByDeviceJobId(deviceJobId) };
   }

   /**
    * Get records of device share with users
    *
    * @param deviceId
    * @return
    */
   @GET
   @Path("/device/{deviceId}/share/user")
   @Produces(MediaType.APPLICATION_JSON)
   public List<DeviceSharing> getDeviceSharingUsers(@PathParam("deviceId") Long deviceId) {
      return mDeviceDao.getSharedWithUsers(deviceId);
   }

   /**
    * Get records of device share with groups
    *
    * @param deviceId
    * @return
    */
   @GET
   @Path("/device/{deviceId}/share/group")
   @Produces(MediaType.APPLICATION_JSON)
   public List<DeviceSharingGroup> getDeviceSharingGroups(@PathParam("deviceId") Long deviceId) {
      return mDeviceDao.getSharedWithGroups(deviceId);
   }

   // TODO: to be changed to use testConfigId once database is changed and test profile is removed
   @GET
   @Path("/testconfig/{testProfileId}/device-types")
   @Produces(MediaType.APPLICATION_JSON)
   public List<DeviceType> getTestConfigDeviceTypes(@PathParam("testProfileId") long testProfileId) {
      return mDeviceDao.getDeviceTypesByTestProfile(testProfileId);
   }

   @GET
   @Path("/testconfig")
   @Produces(MediaType.APPLICATION_JSON)
   public List<TestConfigComplex> getTestConfigs() {
      return mTestConfigDao.getByUserId(getUserId());
   }

   @GET
   @Path("/testconfig/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public TestConfig getTestConfig(@PathParam(QPARAM_ID) long id) {
      return mTestConfigDao.getById(id);
   }

   /**
    * Get test package object
    *
    * @param devicejobId
    * @return
    */
   @GET
   @Path("/testpackage")
   @Produces(MediaType.APPLICATION_JSON)
   public TestPackage getTestPackageInfo(@QueryParam("devicejobId") Long devicejobId) {
      return mCheckoutDao.getTestPackage(devicejobId);
   }

   /**
    * Get test package zip file
    *
    * @param devicejobId
    * @return
    * @throws FileNotFoundException
    */
   @Path("/testpackage.zip")
   @GET
   @Produces("application/x-zip-compressed")
   public InputStream getTestPackageZip(@QueryParam("devicejobId") Long devicejobId) throws FileNotFoundException {
      // build package using paths to files extracted previously
      TestPackage testPackage = mCheckoutDao.getTestPackage(devicejobId);

      File testScriptPath = mTestScriptDao.getTestScript(Long.valueOf(Files.getNameWithoutExtension(testPackage
            .getTestScriptFileName())));
      testPackage.setTestScriptPath(testScriptPath.getAbsolutePath());

      File testTargetPath = mTestTargetDao.getTestTarget(Long.valueOf(Files.getNameWithoutExtension(testPackage
            .getTestTargetFileName())));
      testPackage.setTestTargetPath(testTargetPath.getAbsolutePath());

      String[] paths = new String[] { testPackage.getTestScriptPath(), testPackage.getTestTargetPath() };
      FileInputStream result = new FileInputStream(new File(mCheckoutDao.buildZipPackage(paths)));
      testScriptPath.delete();
      testTargetPath.delete();
      return result;
   }

   @GET
   @Path("/testprofile")
   @Produces(MediaType.APPLICATION_JSON)
   public TestProfile[] getTestProfiles() {
      return mTestProfileDao.getByUserId(getUserId());
   }

   // TODO: max should probably be limited
   @GET
   @Path("/testrun")
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, Object> getTestRuns(@QueryParam("offset") int offset, @QueryParam("max") int to) {
      return mTestRunDao.getByUserIdFull(getUserId(), offset, to);
   }

   @GET
   @Path("/testrun/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public TestRun getTestRun(@PathParam(QPARAM_ID) long id) {
      return mTestRunDao.getTestRun(id);
   }

   /**
    * Get test script metadata
    *
    * @return
    */
   @GET
   @Path("/testscript")
   @Produces(MediaType.APPLICATION_JSON)
   public List<TestScript> getTestScriptMetadata() {
      return mTestScriptDao.getByUserId(getUserId());
   }

   /**
    * Get test target meta data
    *
    * @return
    */
   @GET
   @Path("/testtarget")
   @Produces(MediaType.APPLICATION_JSON)
   public TestTarget[] getTestTargets() {
      // TODO: should not return all
      return mTestTargetDao.getAll();
   }

   @GET
   @Path("/user/{userId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, Object> getUser(@PathParam(QPARAM_USERID) Long userId) {
      return mUserDao.getUserById(userId);
   }

   @GET
   @Path("/user")
   @Produces(MediaType.APPLICATION_JSON)
   public User getUserByName(@QueryParam("name") String userName) {
      return mUserDao.getUserByName(userName);
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/device")
   public List<Device> getUserDevices(@QueryParam("state") DeviceDeviceState state) {
      return mDeviceDao.getAllAvailableForUser(getUserId(), null, state);
   }

   /**
    * Helper method to extract user id from security context
    *
    * @return
    */
   private Long getUserId() {
      return ((CbtPrinciple) mContext.getUserPrincipal()).getId();
   }

   @GET
   @Path("/{userId}/stats/hosted")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Map<String, Object>> getUserStatsHosted() {
      return mUserDao.getUserHostedTestStats(getUserId());
   }

   @GET
   @Path("/user/{userId}/stats/used")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Map<String, Object>> getUserStatsUsed() {
      return mUserDao.getUserRunTestStats(getUserId());
   }

   /**
    * Create new device
    *
    * @param device
    * @return
    * @throws CbtDaoException
    * @throws DataAccessException
    */
   @PUT
   @Path("/device")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_HTML)
   public Response putDevice(Device device) throws CbtDaoException, DataAccessException {
      // Generate device unique id
      String uniqueId = Utils.md5(Utils.buildContentForDeviceUniqueId(device));
      device.setDeviceUniqueId(uniqueId);
      Long response = null;
      try {
         response = mDeviceDao.add(device);
      } catch (DataAccessException e) {
         mLogger.warn("Cound not add device", e);
         if (e.getMessage().contains("Duplicate entry")) {
            Device duplicateDevice = mDeviceDao.getDeviceByUid(device.getDeviceUniqueId());
            return Response.status(Status.CONFLICT).entity(duplicateDevice).type(MediaType.APPLICATION_JSON).build();
         }
         return Response.serverError().build();
      }
      return Response.ok(response, MediaType.APPLICATION_JSON).build();
   }

   // TODO: remove logic from here

   /**
    * Update single {@link DeviceJob} entry
    *
    * @param deviceJobId
    * @param deviceJobResult
    * @return
    */
   @PUT
   @Path("/devicejob/{deviceJobId}/result")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public DeviceJobResult putDeviceJobResult(@PathParam("deviceJobId") Long deviceJobId, DeviceJobResult deviceJobResult) {
      mLogger.debug("Received:" + deviceJobResult);
      deviceJobResult.setDevicejobId(deviceJobId);
      Long id = mDeviceJobResultDao.add(deviceJobResult);
      deviceJobResult.setId(id);

      // Update device job status
      DeviceJob deviceJob = mDeviceJobDao.getById(deviceJobResult.getDevicejobId());
      deviceJob.setStatus(DeviceJobDeviceJobStatus.FINISHED);
      mLogger.debug("Updating devicejob to:" + deviceJob);
      try {
         mDeviceJobDao.update(deviceJob);
      } catch (CbtDaoException e1) {
         mLogger.error("Could not update DeviceJob status after receiving device job result");
      }

      // TODO: optimise by querying db for !FINISHED jobs
      // Check if all jobs have finished
      mLogger.debug("Checking if all jobs have finished");
      DeviceJob[] jobsOfThisTestRun = mDeviceJobDao.getByTestRunId(deviceJob.getTestRunId());
      boolean allFinished = true;
      for (DeviceJob job : jobsOfThisTestRun) {
         mLogger.debug(job);
         if (!job.getStatus().equals(DeviceJobDeviceJobStatus.FINISHED)) {
            allFinished = false;
         }
      }

      if (allFinished) {
         mLogger.debug("All jobs finished, checking jobs result verdicts");
         List<DeviceJobResult> jobsResults = mDeviceJobResultDao.getByTestRunId(deviceJob.getTestRunId());
         boolean allsuccess = true;
         for (DeviceJobResult result : jobsResults) {
            if (result.getState().equals(DeviceJobResultState.FAILED)) {
               allsuccess = false;
            }
         }

         TestRun testRun = mTestRunDao.getTestRun(deviceJob.getTestRunId());
         testRun.setStatus(allsuccess ? TestrunTestrunStatus.PASSED : TestrunTestrunStatus.FAILED);
         try {
            mTestRunDao.update(testRun);
         } catch (CbtDaoException e) {
            mLogger.error("Could not update TestRun status after receiveing device job result", e);
         }
      }
      return deviceJobResult;
   }

   @PUT
   @Path("/device/{deviceId}/share/user")
   public void putNewDeviceShareForUser(@PathParam("deviceId") Long deviceId,
         @FormParam("username") String shareForUserName) {

      // Check if user is authorized to share this device and if device exists
      Device device = mDeviceDao.getDevice(getUserId(), deviceId);
      if (null == device) {
         throw new WebApplicationException(Response.status(Status.NOT_FOUND).type("text/plain")
               .entity("Specified device not found or un-authorized").build());
      }

      // Share with specified user
      User userToShareTo = mUserDao.getUserByName(shareForUserName);
      if (null != userToShareTo) {
         mDeviceDao.addSharing(deviceId, userToShareTo.getId());
      } else {
         throw new WebApplicationException(Response.status(Status.NOT_FOUND).type("text/plain")
               .entity("User " + shareForUserName + " was not found !").build());
      }
   }

   @PUT
   @Path("/device/{deviceId}/share/group")
   public void putNewDeviceShareForGroup(
         @PathParam("deviceId") Long deviceId,
         @FormParam("groupId") Long groupId) {

      // Check if user is authorized to share this device and if device exists
      Device device = mDeviceDao.getDevice(getUserId(), deviceId);
      if (null == device) {
         throw new WebApplicationException(Response.status(Status.NOT_FOUND).type("text/plain")
               .entity("Specified device not found or un-authorized").build());
      }

      mDeviceDao.addShareWithUserGroup(deviceId, groupId);

   }

   /**
    * Create new test configuration
    *
    * @param testConfig
    * @return
    */
   @PUT
   @Path("/testconfig")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public TestConfig putTestConfig(TestConfig testConfig) {
      testConfig.setUserId(getUserId());
      Long testConfigId = mTestConfigDao.add(testConfig);
      testConfig.setId(testConfigId);
      return testConfig;
   }

   /**
    * Create new test profile
    *
    * @param testProfile
    * @return
    */
   @PUT
   @Path("/testprofile")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public TestProfile putTestProfile(TestProfile testProfile) {
      testProfile.setUserId(getUserId());
      TestProfile testProfileNew = mTestProfileDao.add(testProfile);
      return testProfileNew;
   }

   // TODO: if there is no queue then there should be a way to check for available devices without creating a new test
   // run in db
   /**
    * Create new test run
    *
    * @param testRun
    * @return
    */
   @PUT
   @Path("/testrun")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public TestRun putTestRun(TestRun testRun) {
      testRun.setUserId(getUserId());
      Long testRunId = mTestRunDao.add(testRun);
      testRun.setId(testRunId);
      List<Device> devices = null;
      if (testRunId != null && testRunId > 0) {
         try {
            devices = mTestRunProcessor.getDevices(testRunId, testRun.getUserId());
         } catch (CbtNoDevicesException e) {
            mLogger.error(e);
            throw new WebApplicationException(Status.PRECONDITION_FAILED);
         } finally {
            if (null == devices) {
               // currently no queuing mechanism, therefore, remove created test run if not device found to service it
               try {
                  mTestRunDao.delete(testRun);
               } catch (CbtDaoException e) {
                  mLogger.error("Could not delete test run placeholder", e);
               }
            }
         }
         testRun.setDevices(devices);
         // Set to default state
         testRun.setStatus(TestrunTestrunStatus.WAITING);
      }
      return testRun;
   }

   /**
    * Upload test target file, respond with JSON
    *
    * @param uploadedInputStream
    * @param fileDetail
    * @return
    */
   @POST
   @Path("/testscript")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.APPLICATION_JSON)
   @Deprecated
   public TestScript putTestScriptFile(@FormDataParam("file") InputStream uploadedInputStream,
         @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {
      // Encapsulate test package info
      TestScript testScript = new TestScript();
      testScript.setName(name);
      testScript.setUserId(getUserId());
      try {
         mTestScriptDao.storeTestScript(testScript, uploadedInputStream);
      } catch (IOException e) {
         mLogger.error("Error while save file", e);
      }
      return testScript;
   }

   /**
    * Upload test script file, respond with HTML
    *
    * @param uploadedInputStream
    * @param fileDetail
    * @param name
    * @return
    */
   @POST
   @Path("/testscript")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_HTML)
   @Deprecated
   public String putTestScriptFileHtml(@FormDataParam("file") InputStream uploadedInputStream,
         @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {
      TestScript testScript = putTestScriptFile(uploadedInputStream, fileDetail, name);
      StringBuilder sb = new StringBuilder("<html><body>File uploaded successfully, new Id: " + testScript.getId());
      sb.append("<br />Found test classes: <ul>");
      for (String testClass : testScript.getTestClasses()) {
         sb.append("<li>" + testClass + "</li>");
      }
      sb.append("<ul></body></html>");
      return sb.toString();
   }

   /**
    * Upload test script file
    *
    * @param uploadedInputStream
    * @return
    */
   @POST
   @Path("/v2/testscript")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.APPLICATION_JSON)
   public TestScript putTestScriptFile2(@FormDataParam("files[]") InputStream uploadedInputStream,
         @FormDataParam("files[]") FormDataContentDisposition fileDetail) {
      // Encapsulate test package info
      TestScript testScript = new TestScript();
      testScript.setName(fileDetail.getFileName());
      testScript.setUserId(getUserId());
      try {
         mTestScriptDao.storeTestScript(testScript, uploadedInputStream);
      } catch (IOException e) {
         mLogger.error("Error while save file", e);
      }
      return testScript;
   }

   /**
    * Upload new test target file, return response in JSON
    *
    * @param uploadedInputStream
    * @param fileDetail
    * @return
    */
   @Deprecated
   @POST
   @Path("/testtarget")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.APPLICATION_JSON)
   public TestTarget putTestTarget(@FormDataParam("file") InputStream uploadedInputStream,
         @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {
      // Encapsulate test package info
      TestTarget testTarget = new TestTarget();
      testTarget.setName(name);
      testTarget.setUserId(getUserId());
      try {
         mTestTargetDao.storeTestTarget(testTarget, uploadedInputStream);
      } catch (IOException e) {
         mLogger.error("Error while save file", e);
      }
      return testTarget;
   }

   /**
    * Upload new test target file, return response in HTML
    *
    * @param uploadedInputStream
    * @param fileDetail
    * @param name
    * @return
    */
   @Deprecated
   @POST
   @Path("/testtarget")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_HTML)
   public String putTestTargetHtml(@FormDataParam("file") InputStream uploadedInputStream,
         @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {
      TestTarget testTarget = putTestTarget(uploadedInputStream, fileDetail, name);
      return "<html><body>File uploaded successfully, new Id: " + testTarget.getId() + "</body></html>";
   }

   @POST
   @Path("/v2/testtarget")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.APPLICATION_JSON)
   public TestTarget putTestTarget2(@FormDataParam("files[]") InputStream uploadedInputStream,
         @FormDataParam("files[]") FormDataContentDisposition fileDetail) {
      // Encapsulate test package info
      TestTarget testTarget = new TestTarget();
      testTarget.setName(fileDetail.getFileName());
      testTarget.setUserId(getUserId());
      try {
         mTestTargetDao.storeTestTarget(testTarget, uploadedInputStream);
      } catch (IOException e) {
         mLogger.error("Error while save file", e);
      }
      return testTarget;
   }

   /**
    * Sync device type object
    *
    * @param deviceType
    * @return
    */
   @POST
   @Path("/device/type")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public DeviceType syncDeviceType(DeviceType deviceType) {
      return mDeviceDao.getOrCreateDeviceType(deviceType.getManufacture(), deviceType.getModel());
   }

   // TODO: should return something, since now it return 204 NO CONTENT

   /**
    * Update device
    *
    * @param deviceId
    * @param device
    * @throws CbtDaoException
    */
   @POST
   @Path("/device/{deviceId}")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updateDevice(@PathParam("deviceId") Long deviceId, Device device) throws CbtDaoException {
      mDeviceDao.updateDevice(device);
   }
}
