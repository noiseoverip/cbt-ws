package com.cbt.ws.services;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cbt.ws.dao.TestProfileDao;
import com.cbt.ws.dao.UserDao;
import com.cbt.ws.entity.TestProfile;
import com.cbt.ws.entity.User;
import com.google.inject.Inject;

/**
 * Access web service. Provide authentication and user related API
 * 
 * @author SauliusAlisauskas 2013-04-09 Initial version
 *
 */
@Path("/user")
public class AccessWs {
	
	private UserDao mUserDao;
	private TestProfileDao mTestProfileDao;
	
	@Inject
	public AccessWs(UserDao dao, TestProfileDao testProfileDao) {
		mUserDao = dao;
		mTestProfileDao = testProfileDao;
	}
	
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getUser(@PathParam("userId") Long userId) {
		return mUserDao.getUserById(userId);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserByName(@QueryParam("name") String userName) {
		return mUserDao.getUserByName(userName);
	}
	
	@GET
	@Path("/{userId}/stats/hosted")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getStatsHosted(@PathParam("userId") Long userId) {
		return mUserDao.getUserHostedTestStats(userId);
	}
	
	@GET
	@Path("/{userId}/stats/used")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getStatsUsed(@PathParam("userId") Long userId) {
		return mUserDao.getUserRunTestStats(userId);
	}
	
	@GET
	@Path("/{userId}/testprofile")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TestProfile> getTestProfiles(@PathParam("userId") Long userId) {
		return mTestProfileDao.getByUserId(userId);
	}
	
	@PUT
	@Path("/{userId}/testprofile")
	@Produces(MediaType.APPLICATION_JSON)
	public TestProfile putTestProfile(@PathParam("userId") Long userId, TestProfile testProfile) {
		return mTestProfileDao.add(testProfile);
	}
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<Map<String, Object>> getAllUsers() {
//		return mDao.getAllUsers();
//	}	
}
