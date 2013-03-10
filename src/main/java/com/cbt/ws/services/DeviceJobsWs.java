package com.cbt.ws.services;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.exceptions.CbtDaoException;
import com.google.inject.servlet.RequestScoped;

/**
 * Device jobs web service
 * 
 * @author SauliusAlisauskas 2013-03-03 Initial version
 * 
 */
@Path("/devicejobs/")
@RequestScoped
public class DeviceJobsWs {

	//private final Logger mLogger = Logger.getLogger(DeviceJobsWs.class);

	private DevicejobDao mDao;

	@Inject
	public DeviceJobsWs(DevicejobDao dao) {
		mDao = dao;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceJob[] get() {
		return mDao.getAll();
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
		return Response.ok().build();
	}
}
