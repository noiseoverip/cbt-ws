package com.cbt.ws.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.entity.Device;
import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.entity.TestRun;
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

	//private final Logger mLogger = Logger.getLogger(TestRunWs.class);

	private TestRunDao mTestrunDao;
	private DevicejobDao mDevicejobDao;
	private DeviceDao mDeviceDao;
	private final Logger mLogger = Logger.getLogger(TestRunWs.class);

	@Inject
	public TestRunWs(TestRunDao dao, DevicejobDao devicejobDao, DeviceDao deviceDao) {
		mTestrunDao = dao;
		mDevicejobDao = devicejobDao;
		mDeviceDao = deviceDao;
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

		// TODO: implement authentication
		testRun.setUserId(1L);

		Long testRunId = mTestrunDao.add(testRun);
		testRun.setId(testRunId);
		
		List<Long> deviceTypes = mTestrunDao.getTestRunComplex(testRun.getId()).getDeviceTypes();		
		List<DeviceJob> jobs = new ArrayList<DeviceJob>();		
		
		for (Long deviceType : deviceTypes) {
			List<Device> devices = mDeviceDao.getDevicesOfType(deviceType);
			if (null != devices) {
				for (Device device : devices) {
					DeviceJob job = new DeviceJob();
					job.setDeviceId(device.getId());
					job.setUserId(device.getUserId());
					job.setTestRunId(testRun.getId());	
					Long deviceJobId = mDevicejobDao.add(job);
					if (null != deviceJobId) {
						job.setId(deviceJobId);
						jobs.add(job);
					} else {
						mLogger.error("Could not created job:" + job);
					}					
				}
			}
		}
		
		return testRun;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TestRun[] get() {
		return mTestrunDao.getAll();
	}
}
