package com.cbt.ws.services;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.dao.UserDao;
import com.cbt.ws.entity.User;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author SauliusAlisauskas 2013-04-09 Initial version
 *
 */
@Path("/public")
public class PublicWs {
	
	private UserDao mUserDao;
	private DeviceDao mDeviceDao;
	
	@Inject
	public PublicWs(UserDao dao, DeviceDao deviceDao) {
		mUserDao = dao;
		mDeviceDao = deviceDao;
	}	
	
	@PUT
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User addUser(Map<String, String> details) {
		return mUserDao.createNew(details.get("name"), details.get("password"));
	}
	
	@GET
	@Path("/device-types")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>>  getDeviceByUserId(@QueryParam("userId") Long userId) {
		return mDeviceDao.getDeviceTypes();
	}
}
