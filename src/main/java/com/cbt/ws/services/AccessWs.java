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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.jooq.exception.DataAccessException;

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
import com.cbt.ws.entity.Device;
import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.entity.DeviceJobResult;
import com.cbt.ws.entity.DeviceType;
import com.cbt.ws.entity.TestConfig;
import com.cbt.ws.entity.TestPackage;
import com.cbt.ws.entity.TestProfile;
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.entity.TestScript;
import com.cbt.ws.entity.TestTarget;
import com.cbt.ws.entity.User;
import com.cbt.ws.entity.complex.TestConfigComplex;
import com.cbt.ws.exceptions.CbtDaoException;
import com.cbt.ws.exceptions.CbtNoDevicesException;
import com.cbt.ws.jooq.enums.DeviceJobStatus;
import com.cbt.ws.jooq.enums.TestrunStatus;
import com.cbt.ws.security.CbtPrinciple;
import com.cbt.ws.tools.TestRunProccessor;
import com.cbt.ws.utils.Utils;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Access web service. Provide authentication and user related API
 * 
 * @author SauliusAlisauskas 2013-04-09 Initial version
 * 
 */
@Path("/")
public class AccessWs {

	private static final String QPARAM_USERID = "userId";
	private CheckoutDao mCheckoutDao;
	@Context
	private SecurityContext mContext;
	private DeviceDao mDeviceDao;
	private DevicejobDao mDeviceJobDao;
	private DevicejobResultDao mDeviceJobResultDao;
	private final Logger mLogger = Logger.getLogger(AccessWs.class);
	private TestConfigDao mTestConfigDao;
	private TestProfileDao mTestProfileDao;
	private TestRunDao mTestRunDao;
	private TestRunProccessor mTestRunProcessor;
	private TestScriptDao mTestScriptDao;
	private TestTargetDao mTestTargetDao;
	private UserDao mUserDao;

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
	
	@GET
	@Path("/user/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getAllUsers() {
		return mUserDao.getAllUsers();
	}
	
	/**
	 * Get device by device Id
	 * 
	 * @param deviceId
	 * @return
	 */
	@GET
	@Path("/device/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Device getDevice(@PathParam("deviceId") Long deviceId) {
		return mDeviceDao.getDevice(deviceId);
	}
	
	//TODO: change to GET and use UID as parameter
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
		String uniqueId = Utils.Md5(Utils.buildContentForDeviceUniqueId(device));
		return mDeviceDao.getDeviceByUid(uniqueId);
	}

	/**
	 * Get oldest waiting device job
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
	 * @param userId
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
	 * Get device sharing information
	 * 
	 * @param deviceId
	 * @return
	 */
	@GET
	@Path("/device/{deviceId}/share")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getDeviceSharingInfo(@PathParam("deviceId") Long deviceId) {
		return mDeviceDao.getSharedWith(deviceId);
	}

	@GET
	@Path("/testconfig")
	@Produces(MediaType.APPLICATION_JSON)
	public TestConfigComplex[] getTestConfigs() {
		return mTestConfigDao.getByUserId(getUserId());
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
	public InputStream getTestPackageZip(@QueryParam("devicejob_id") Long devicejobId) throws FileNotFoundException {
		// build package using paths to files extracted previously
		TestPackage testPackage = mCheckoutDao.getTestPackage(devicejobId);
		String[] paths = new String[] { testPackage.getTestScriptPath(), testPackage.getTestTargetPath() };
		return new FileInputStream(new File(mCheckoutDao.buildZipPackage(paths)));
	}

	@GET
	@Path("/testprofile")
	@Produces(MediaType.APPLICATION_JSON)
	public TestProfile[] getTestProfiles() {
		return mTestProfileDao.getByUserId(getUserId());
	}

	@GET
	@Path("/testrun")
	@Produces(MediaType.APPLICATION_JSON)
	public TestRun[] getTestRuns() {
		return mTestRunDao.getByUserIdFull(getUserId());
	}

	// /**
	// * Update single {@link DeviceJob} entry
	// *
	// * @param deviceJob
	// * @return
	// * @throws CbtDaoException
	// */
	// @POST
	// @Path("/{userId}/devicejob")
	// public Response updateJob(@PathParam(QPARAM_USERID) Long userId, DeviceJob deviceJob) throws CbtDaoException {
	// mDeviceJobDao.update(deviceJob);
	// return Response.ok().build();
	// }

	/**
	 * Get test script metadata
	 * 
	 * @return
	 */
	@GET
	@Path("/testscript")
	@Produces(MediaType.APPLICATION_JSON)
	public TestScript[] getTestScriptMetadata() {
		// TODO: should not return all !!!
		return mTestScriptDao.getAll();
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
	@Path("/user/{userId}/device")
	public List<Device> getUserDevices(@PathParam("userId") Long userId) {
		return mDeviceDao.getAllAvailableForUser(userId, null, null);
	}

	/**
	 * Helper method to extract user id from security context
	 * 
	 * @param context
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
		String uniqueId = Utils.Md5(Utils.buildContentForDeviceUniqueId(device));
		device.setDeviceUniqueId(uniqueId);
		Long response = null;
		try {
			response = mDeviceDao.add(device);
		} catch (DataAccessException e) {
			mLogger.warn("Cound not add device", e);
			if (e.getMessage().contains("Duplicate entry")) {
				Device duplicateDevice = mDeviceDao.getDeviceByUid(device.getDeviceUniqueId());
				return Response.status(Status.CONFLICT).entity(duplicateDevice).type(MediaType.APPLICATION_JSON)
						.build();
			}
			return Response.serverError().build();
		}
		return Response.ok(response, MediaType.APPLICATION_JSON).build();
	}

	// TODO: remove logic from here
	/**
	 * Update single {@link DeviceJob} entry
	 * 
	 * @param deviceJob
	 * @return
	 */
	@PUT
	@Path("/devicejob/{deviceJobId}/result")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeviceJobResult putDeviceJobResult(@PathParam("deviceJobId") Long deviceJobId,
			DeviceJobResult deviceJobResult) {
		mLogger.debug("Received:" + deviceJobResult);
		deviceJobResult.setDevicejobId(deviceJobId);
		Long id = mDeviceJobResultDao.add(deviceJobResult);
		deviceJobResult.setId(id);

		// Update device job status
		DeviceJob deviceJob = mDeviceJobDao.getById(deviceJobResult.getDevicejobId());
		deviceJob.setStatus(DeviceJobStatus.FINISHED);
		mLogger.debug("Updating devicejob to:" + deviceJob);
		try {
			mDeviceJobDao.update(deviceJob);
		} catch (CbtDaoException e1) {
			mLogger.error("Could not update DeviceJob status after receiving device job result");
		}

		// Update test run status
		mLogger.debug("Checking if all jobs have finished");
		DeviceJob[] jobsOfThisTestRun = mDeviceJobDao.getByTestRunId(deviceJob.getTestRunId());
		boolean allFinished = true;
		for (DeviceJob job : jobsOfThisTestRun) {
			mLogger.debug(job);
			if (!job.getStatus().equals(DeviceJobStatus.FINISHED)) {
				allFinished = false;
			}
		}

		if (allFinished) {
			mLogger.debug("All jobs finished, updating TestRun status");
			TestRun testRun = mTestRunDao.getTestRun(deviceJob.getTestRunId());
			testRun.setStatus(TestrunStatus.FINISHED);
			try {
				mTestRunDao.update(testRun);
			} catch (CbtDaoException e) {
				mLogger.error("Could not update TestRun status after receiveing device job result", e);
			}
		}
		return deviceJobResult;
	}

	/**
	 * Share device with specified user
	 * 
	 * @param deviceId
	 * @param userId
	 */
	@PUT
	@Path("/device/{deviceId}/share/{userShareWithId}")
	public void putDeviceShare(@PathParam("deviceId") Long deviceId, @PathParam("userShareWithId") Long userShareWithId) {
		mDeviceDao.addSharing(deviceId, userShareWithId);
	}

	/**
	 * Create new device type
	 * 
	 * @param deviceType
	 * @return
	 */
	@PUT
	@Path("/device/type")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeviceType putDeviceType(DeviceType deviceType) {
		return mDeviceDao.getOrCreateDeviceType(deviceType.getManufacture(), deviceType.getModel());
	}

	/**
	 * Create new test configuration
	 * 
	 * @param testConfig
	 * @param context
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
	 * 
	 * Create new test profile
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
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

	/**
	 * Create new test run
	 * 
	 * @param userId
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
		List<Device> devices = null;
		if (testRunId != null && testRunId > 0) {
			try {
				devices = mTestRunProcessor.getDevices(testRunId, testRun.getUserId());
			} catch (CbtNoDevicesException e) {
				mLogger.error(e);
				// TODO: this is bad, need to change it to simply return a response code in stead of throwing an
				// exception
				throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
			}
		}
		testRun.setId(testRunId);
		testRun.setDevices(devices);
		// Set to default state
		testRun.setStatus(TestrunStatus.WAITING);
		return testRun;
	}

	/**
	 * 
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
	 * 
	 * Upload new test target file, return response in JSON
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
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
	@POST
	@Path("/testtarget")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public String putTestTargetHtml(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {
		TestTarget testTarget = putTestTarget(uploadedInputStream, fileDetail, name);
		return "<html><body>File uploaded successfully, new Id: " + testTarget.getId() + "</body></html>";
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
