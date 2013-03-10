package com.cbt.ws.services;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.entity.Device;
import com.cbt.ws.exceptions.CbtDaoException;
import com.google.inject.servlet.RequestScoped;

@Path("/device")
@RequestScoped
public class DeviceWs {

	private DeviceDao mDao;

	@Inject
	public DeviceWs(DeviceDao dao) {
		mDao = dao;
	}

	@GET
	@Path("/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Device getDevice(@PathParam("deviceId") Long deviceId) {
		return mDao.getDevice(deviceId);
	}
	
	@POST
	@Path("/{deviceId}")
	@Consumes(MediaType.APPLICATION_JSON)	
	public void updateDevice(@PathParam("deviceId") Long deviceId, Device device) throws CbtDaoException {
		mDao.updateDevice(device);		
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String updateDevice(Device device) throws CbtDaoException {
		return mDao.add(device).toString();
	}

}
