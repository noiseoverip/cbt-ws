package com.cbt.ws.services;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
public class Public {
	
	private UserDao mDao;
	
	@Inject
	public Public(UserDao dao) {
		mDao = dao;
	}	
	
	@PUT
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User addUser(Map<String, String> details) {
		return mDao.createNew(details.get("name"), details.get("password"));
	}	
}
