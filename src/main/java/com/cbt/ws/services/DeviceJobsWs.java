package com.cbt.ws.services;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.DevicejobResultDao;
import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.entity.DeviceJobResult;
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.exceptions.CbtDaoException;
import com.cbt.ws.jooq.enums.DeviceJobStatus;
import com.cbt.ws.jooq.enums.TestrunStatus;
import com.google.inject.servlet.RequestScoped;

/**
 * Device jobs web service
 * 
 * @author SauliusAlisauskas 2013-03-03 Initial version
 * 
 */
@Path("/devicejob/")
@RequestScoped
public class DeviceJobsWs {

	// private final Logger mLogger = Logger.getLogger(DeviceJobsWs.class);

	private DevicejobDao mDao;
	private DevicejobResultDao mResultDao;
	private TestRunDao mTestRunDao;
	private final Logger mLogger = Logger.getLogger(DeviceJobsWs.class);

	@Inject
	public DeviceJobsWs(DevicejobDao dao, DevicejobResultDao resultDao, TestRunDao testRunDao) {
		mDao = dao;
		mResultDao = resultDao;
		mTestRunDao = testRunDao;
	}
	
	@GET
	@Path("/{deviceJobId}/result")
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceJobResult[] getAllResults(@PathParam(value = "deviceJobId") Long deviceJobId) {
		if (null == deviceJobId) {
			return mResultDao.getAll();
		} else {
			return new DeviceJobResult[]{mResultDao.getByDeviceJobId(deviceJobId)}; // must alway be one
		}
	}

	/**
	 * Update single {@link DeviceJob} entry
	 * 
	 * @param deviceJob
	 * @return
	 */
	@PUT
	@Path("/{deviceJobId}/result")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeviceJobResult add(DeviceJobResult deviceJobResult, @PathParam(value = "deviceJobId") Long deviceJobId) {
		mLogger.debug("Received:" + deviceJobResult);
		deviceJobResult.setDevicejobId(deviceJobId);
		Long id = mResultDao.add(deviceJobResult);
		deviceJobResult.setId(id);
		
		// Update devicejob status
		DeviceJob deviceJob = mDao.getById(deviceJobResult.getDevicejobId());
		deviceJob.setStatus(DeviceJobStatus.FINISHED);
		mLogger.debug("Updating devicejob to:" + deviceJob);
		try {
			mDao.update(deviceJob);
		} catch (CbtDaoException e1) {
			mLogger.error("Could not update DeviceJob status after receiving device job result");
		}
		
		// Update testrun status
		mLogger.debug("Checking if all jobs have finished");
		DeviceJob[] jobsIfThisTestRun = mDao.getByTestRunId(deviceJob.getTestRunId());
		boolean allFinished = true;
		for (DeviceJob job : jobsIfThisTestRun) {
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
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceJob[] get(@QueryParam(value = "testRunId") Long testRunId) {
		if (null == testRunId) {
			return mDao.getAll();
		} else {
			return mDao.getByTestRunId(testRunId);
		}		
	}	

	/**
	 * Get oldest waiting device job
	 * 
	 * @param deviceId
	 * @return JSON string containing device job properties if any jobs available, 204 otherwise
	 */
	@GET
	@Path("/waiting")
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceJob getWaitingJobs(@QueryParam("deviceId") Long deviceId) {
		return mDao.getOldestWaiting(deviceId);
	}

	/**
	 * Update single {@link DeviceJob} entry
	 * 
	 * @param deviceJob
	 * @return
	 */
	@POST
	public Response updateJob(DeviceJob deviceJob) {
		try {
			mDao.update(deviceJob);
		} catch (CbtDaoException e) {
			return Response.serverError().build();
		}

		// TODO: if status if finished, perform test run check logic to see if all test run has been finished

		return Response.ok().build();
	}
}
