package com.cbt.ws.services;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cbt.ws.dao.UserDao;
import com.google.inject.Inject;

/**
 * Access web service. Provide authentication and user related API
 * 
 * @author SauliusAlisauskas 2013-04-09 Initial version
 *
 */
@Path("/user")
public class AccessWs {
	
	private UserDao mDao;
	
	@Inject
	public AccessWs(UserDao dao) {
		mDao = dao;
	}
	
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getUser(@PathParam("userId") Long userId) {
		return mDao.getUserById(userId);
	}
	
	@GET
	@Path("/{userId}/stats/hosted")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getStatsHosted(@PathParam("userId") Long userId) {
		return mDao.getUserHostedTestStats(userId);
	}
	
	@GET
	@Path("/{userId}/stats/used")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getStatsUsed(@PathParam("userId") Long userId) {
		return mDao.getUserRunTestStats(userId);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getAllUsers() {
		return mDao.getAllUsers();
	}	
}
