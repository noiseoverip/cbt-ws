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
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {

		// Encapsulate test package info
		TestScript testScript = new TestScript();
		// TODO: implement authentication
		testScript.setName(name);
		testScript.setUserId(1L);

		try {
			mDao.storeTestScript(testScript, uploadedInputStream);
		} catch (IOException e) {
			mLogger.error("Error while save file", e);
		}
		return testScript;

	}
	
	@POST
	@Path("/add")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public String uploadFileReturnHtml(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {	
		TestScript testScript = uploadFile(uploadedInputStream, fileDetail, name);
		return "<html><body>File uploaded successfully, new Id: " + testScript.getId() +"</body></html>";
	}
}