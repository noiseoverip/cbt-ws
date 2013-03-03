package com.cbt.ws.services;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.TestTargetDao;
import com.cbt.ws.entity.TestTarget;
import com.cbt.ws.entity.User;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Test package web service
 * 
 * Test packages to be kept in: /username/testpackage/testpackage_id
 * 
 * @author SauliusAlisauskas 2013-02-25 Initial version
 * 
 */
@Path("/testtarget/")
public class TestTargetWs {

	private final Logger mLogger = Logger.getLogger(TestTargetWs.class);

	private TestTargetDao mDao;

	@Inject
	public TestTargetWs(TestTargetDao dao) {
		mDao = dao;
	}

	/**
	 * 
	 * Add new test package to the system
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@POST
	@Path("/add")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("username") String username) {

		// Load user info (should be taken care by interceptors, authentication...)
		User user = new User();
		user.setName(username);
		user.setId(1); //TODO: for now always use user id 1

		// Encapsulate test package info
		TestTarget tTarget = new TestTarget();
		tTarget.setOwner(user);

		try {
			mDao.storeTestTarget(tTarget, uploadedInputStream);
		} catch (IOException e) {
			mLogger.error("Error while save file", e);
		}

		mLogger.info("User: " + username + " uploaded file:" + fileDetail);

		return Response.status(200).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TestTarget[] get() {
		TestTarget[] apps = null;
		apps = mDao.getTestTargetAll();
		return apps;
	}
}
