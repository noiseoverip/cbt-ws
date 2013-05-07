package com.cbt.ws.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.dao.TestScriptDao;
import com.cbt.ws.entity.Device;
import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.entity.TestScript;
import com.cbt.ws.entity.complex.TestRunComplex;
import com.cbt.ws.jooq.enums.DeviceState;
import com.cbt.ws.jooq.enums.TestprofileMode;
import com.cbt.ws.main.CbtNoDevicesException;
import com.google.inject.servlet.RequestScoped;

/**
 * Test configuration web service
 * 
 * @author SauliusAlisauskas 2013-03-03 Initial version
 * 
 */
@Path("/testrun/")
@RequestScoped
public class TestRunWs {

	// private final Logger mLogger = Logger.getLogger(TestRunWs.class);

	private TestRunDao mTestrunDao;
	private DevicejobDao mDevicejobDao;
	private DeviceDao mDeviceDao;
	private final Logger mLogger = Logger.getLogger(TestRunWs.class);
	private TestScriptDao mTestScriptDao;

	@Inject
	public TestRunWs(TestRunDao testRunDao, DevicejobDao devicejobDao, DeviceDao deviceDao, TestScriptDao testScripDao) {
		mTestrunDao = testRunDao;
		mDevicejobDao = devicejobDao;
		mDeviceDao = deviceDao;
		mTestScriptDao = testScripDao;
	}

	/**
	 * 
	 * Add new test run
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TestRun add(TestRun testRun) {
		// TODO: move logic to dedicated class
		// TODO: implement authentication
		testRun.setUserId(1L);

		Long testRunId = mTestrunDao.add(testRun);
		testRun.setId(testRunId);

		TestRunComplex testRunComplex = mTestrunDao.getTestRunComplex(testRun.getId());
		TestScript testScript = mTestScriptDao.getById(testRunComplex.getTestConfig().getTestScriptId());
		try {
			if (testRunComplex.getTestProfile().getMode().equals(TestprofileMode.NORMAL)) {
				testRun.setDevices(handleNormalMode(testRunComplex, testScript));
			} else {
				testRun.setDevices(handleFastMode(testRunComplex, testScript));
			}
		} catch (CbtNoDevicesException e) {
			mLogger.error(e);
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
		}

		return testRun;
	}

	/**
	 * Handle fast mode. Handles cases: when testClasses > devices when devices > testClases when testClasses = devices
	 * 
	 * Workarounds: last device gets the rest of test cases
	 * 
	 * @param testRunComplex
	 * @throws CbtNoDevicesException
	 */
	private List<Device> handleFastMode(TestRunComplex testRunComplex, TestScript testScript)
			throws CbtNoDevicesException {
		mLogger.debug("Handling FAST mode ");
		List<DeviceJob> jobs = new ArrayList<DeviceJob>();
		List<Device> devicesAll = new ArrayList<Device>();
		List<Long> deviceTypes = testRunComplex.getDeviceTypes();
		for (Long deviceType : deviceTypes) {
			List<Device> devices = mDeviceDao.getDevicesOfType(deviceType, DeviceState.ONLINE);
			// Do this for each of device types
			int numberOfAvailableDevices = devices.size();
			String[] testClasses = testScript.getTestClasses();
			int numberOfTestCases = testClasses.length;
			mLogger.info("Found " + devices + " of type id:" + deviceType);
			mLogger.info("Will split test classes:" + Arrays.toString(testClasses));
			int testClassesPerDevice = (int) Math.floor(numberOfTestCases / numberOfAvailableDevices);
			if (testClassesPerDevice == 0) {
				testClassesPerDevice = 1;
			}
			mLogger.info("TestClassesPerDevice:" + testClassesPerDevice);
			if (null != devices) {
				int testClassIndex = 0;
				int deviceIndex = 0;
				for (Device device : devices) {
					DeviceJob job = new DeviceJob();
					job.setDeviceId(device.getId());
					job.setUserId(device.getUserId());
					job.setTestRunId(testRunComplex.getId());
					List<String> testClassesForDevice = new ArrayList<String>();
					for (int i = 0; i < testClassesPerDevice; i++) {
						testClassesForDevice.add(testClasses[testClassIndex]);
						testClassIndex++;
						if (deviceIndex == (devices.size() - 1)) {
							// Add all the rest of test classes if this is the last device that we have
							while (testClassIndex <= (numberOfTestCases - 1)) {
								testClassesForDevice.add(testClasses[testClassIndex]);
								testClassIndex++;
							}
						}
					}
					job.getMetadata().setTestClasses(
							testClassesForDevice.toArray(new String[testClassesForDevice.size()]));
					Long deviceJobId = mDevicejobDao.add(job);
					if (null != deviceJobId) {
						job.setId(deviceJobId);
						jobs.add(job);
					} else {
						mLogger.error("Could not created job:" + job);
					}
					devicesAll.add(device);
					if ((testClassIndex - 1) == (numberOfTestCases - 1)) {
						// Stop looping through devices if we have distributed all the test cases
						break;
					}
					deviceIndex++;
				}
			}
		}
		if (jobs.size() < 1) {
			mLogger.warn("Could not find any devices ONLINE of types:" + deviceTypes);
			throw new CbtNoDevicesException();
		}
		return devicesAll;
	}

	private List<Device> handleNormalMode(TestRunComplex testRunComplex, TestScript testScript)
			throws CbtNoDevicesException {
		List<DeviceJob> jobs = new ArrayList<DeviceJob>();
		List<Device> devicesAll = new ArrayList<Device>();
		List<Long> deviceTypes = testRunComplex.getDeviceTypes();
		for (Long deviceType : deviceTypes) {
			List<Device> devices = mDeviceDao.getDevicesOfType(deviceType, DeviceState.ONLINE);
			if (null != devices) {
				for (Device device : devices) {
					DeviceJob job = new DeviceJob();
					job.setDeviceId(device.getId());
					job.setUserId(device.getUserId());
					job.setTestRunId(testRunComplex.getId());
					job.getMetadata().setTestClasses(testScript.getTestClasses());
					Long deviceJobId = mDevicejobDao.add(job);
					if (null != deviceJobId) {
						job.setId(deviceJobId);
						jobs.add(job);
					} else {
						mLogger.error("Could not created job:" + job);
					}
					devicesAll.add(device);
				}
			}
		}
		if (jobs.size() < 1) {
			mLogger.warn("Could not find any devices ONLINE of types:" + deviceTypes);
			throw new CbtNoDevicesException();
		}
		return devicesAll;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TestRun[] get() {
		return mTestrunDao.getAll();
	}
}
