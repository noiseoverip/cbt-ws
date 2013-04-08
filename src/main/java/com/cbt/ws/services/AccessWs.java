package com.cbt.ws.services;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cbt.ws.dao.UserDao;
import com.google.inject.Inject;

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
}
