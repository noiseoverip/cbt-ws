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

import org.apache.log4j.Logger;

import com.cbt.ws.dao.TestScriptDao;
import com.cbt.ws.entity.TestScript;
import com.google.inject.servlet.RequestScoped;
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
@Path("/testscript/")
@RequestScoped
public class TestScriptWs {

	private TestScriptDao mDao;

	private final Logger mLogger = Logger.getLogger(TestScriptWs.class);

	@Inject
	public TestScriptWs(TestScriptDao dao) {
		mDao = dao;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TestScript[] get() {
		return mDao.getAll();
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
	@Produces(MediaType.APPLICATION_JSON)
	public TestScript uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("username") String username) {

		// Encapsulate test package info
		TestScript testScript = new TestScript();
		// TODO: implement authentication
		testScript.setUserId(1L);

		try {
			mDao.storeTestScript(testScript, uploadedInputStream);
		} catch (IOException e) {
			mLogger.error("Error while save file", e);
		}

		mLogger.info("User: " + username + " uploaded file:" + fileDetail);

		return testScript;

	}
}
