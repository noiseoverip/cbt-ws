package com.cbt.ws.services;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.jooq.exception.DataAccessException;

import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.entity.Device;
import com.cbt.ws.entity.DeviceType;
import com.cbt.ws.exceptions.CbtDaoException;
import com.cbt.ws.utils.Utils;
import com.google.inject.servlet.RequestScoped;

@Path("/device")
@RequestScoped
public class DeviceWs {

	private DeviceDao mDao;

	private final Logger mLogger = Logger.getLogger(DeviceWs.class);

	@Inject
	public DeviceWs(DeviceDao dao) {
		mDao = dao;
	}
	
	@PUT
	@Path("/type")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeviceType getDeviceTypeId(DeviceType deviceType) {
		return mDao.getOrCreateDeviceType(deviceType.getManufacture(), deviceType.getModel());
	}	
	
	@GET
	@Path("/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Device getDevice(@PathParam("deviceId") Long deviceId) {
		return mDao.getDevice(deviceId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Device> getDeviceByUserId(@QueryParam("userId") Long userId) {
		return mDao.getAllAvailableForUser(userId, null, null);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Device getDeviceByUid(Device device) {
		String uniqueId = Utils.Md5(Utils.buildContentForDeviceUniqueId(device));
		return mDao.getDeviceByUid(uniqueId);
	}

	@POST
	@Path("/{deviceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDevice(@PathParam("deviceId") Long deviceId, Device device) throws CbtDaoException {
		mDao.updateDevice(device);
	}
	
	@PUT
	@Path("/{deviceId}/share/{userId}")
	public void addSharing(@PathParam("deviceId") Long deviceId, @PathParam("userId") Long userId) {
		mDao.addSharing(deviceId, userId);
	}
	
	@GET
	@Path("/{deviceId}/share")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> addSharing(@PathParam("deviceId") Long deviceId) {
		return mDao.getSharedWith(deviceId);
	}

	@DELETE
	@Path("/{deviceId}")
	public void deleteDevice(@PathParam("deviceId") Long deviceId) throws CbtDaoException {
		mDao.deleteDevice(deviceId);
	}

	// TODO: need to add check if all parameters are provided
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public Response updateDevice(Device device) throws CbtDaoException, DataAccessException {
		// Generate device unique id
		String uniqueId = Utils.Md5(Utils.buildContentForDeviceUniqueId(device));
		device.setDeviceUniqueId(uniqueId);
		Long response = null;
		try {
			response = mDao.add(device);
		} catch (DataAccessException e) {
			mLogger.warn("Cound not add device", e);
			if (e.getMessage().contains("Duplicate entry")) {
				Device duplicateDevice = mDao.getDeviceByUid(device.getDeviceUniqueId());
				return Response.status(Status.CONFLICT).entity(duplicateDevice).type(MediaType.APPLICATION_JSON)
						.build();
			}
			return Response.serverError().build();
		}
		return Response.ok(response, MediaType.APPLICATION_JSON).build();
	}
}
