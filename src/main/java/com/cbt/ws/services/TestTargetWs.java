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

import com.cbt.ws.dao.TestTargetDao;
import com.cbt.ws.entity.TestTarget;
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
@Path("/testtarget/")
@RequestScoped
public class TestTargetWs {

	private TestTargetDao mDao;

	private final Logger mLogger = Logger.getLogger(TestTargetWs.class);

	@Inject
	public TestTargetWs(TestTargetDao dao) {
		mDao = dao;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TestTarget[] get() {
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
	public TestTarget uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {

		// Encapsulate test package info
		TestTarget testTarget = new TestTarget();
		// TODO: implement authentication
		testTarget.setName(name);
		testTarget.setUserId(1L);

		try {
			mDao.storeTestTarget(testTarget, uploadedInputStream);
		} catch (IOException e) {
			mLogger.error("Error while save file", e);
		}		
		return testTarget;
	}
	
	@POST
	@Path("/add")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public String uploadFileReturnHtml(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {		
		TestTarget testTarget = uploadFile(uploadedInputStream, fileDetail, name);				
		return "<html><body>File uploaded successfully, new Id: " + testTarget.getId() +"</body></html>";
	}
	
}
