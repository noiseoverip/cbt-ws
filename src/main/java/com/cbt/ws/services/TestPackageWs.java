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

import com.cbt.ws.dao.TestPackageDao;
import com.cbt.ws.entity.TestPackage;
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
@Path("/testpackage/")
@RequestScoped
public class TestPackageWs {

	private final Logger mLogger = Logger.getLogger(TestPackageWs.class);

	private TestPackageDao mDao;

	@Inject
	public TestPackageWs(TestPackageDao dao) {
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

		// Encapsulate test package info
		TestPackage testPackage = new TestPackage();
		//TODO: implement authentication
		testPackage.setUserId(1L);

		try {
			mDao.storeTestPackage(testPackage, uploadedInputStream);
		} catch (IOException e) {
			mLogger.error("Error while save file", e);
		}

		mLogger.info("User: " + username + " uploaded file:" + fileDetail);

		return Response.status(200).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TestPackage[] get() {
		TestPackage[] packages = null;
		packages = mDao.getPackagesAll();
		return packages;
	}
}
