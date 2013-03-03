package com.cbt.ws.services;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.TestRunDao;
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

	private final Logger mLogger = Logger.getLogger(TestRunWs.class);

	private TestRunDao mTestrunDao;
	private DevicejobDao mDevicejobDao;

	@Inject
	public TestRunWs(TestRunDao dao, DevicejobDao devicejobDao) {
		mTestrunDao = dao;
		mDevicejobDao = devicejobDao;
	}

	/**
	 * 
	 * Add new test run
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(TestRun testRun) {

		// TODO: implement authentication
		testRun.setUserId(1L);

		Long testRunId = mTestrunDao.add(testRun);

		// TODO: get device id's based on test profile device types
		
		
		// Add device id's to devicejobs table
		DeviceJob dj1 = new DeviceJob();
		dj1.setDeviceId(1L);
		dj1.setUserId(1L);
		dj1.setTestRunId(testRunId);		
		mDevicejobDao.add(dj1);
		
		DeviceJob dj2 = new DeviceJob();
		dj2.setDeviceId(2L);
		dj2.setUserId(1L);
		dj2.setTestRunId(testRunId);		
		mDevicejobDao.add(dj2);	

		return Response.status(200).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TestRun[] get() {
		return mTestrunDao.getAll();
	}
}
