package com.cbt.ws.services;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.entity.TestRun;
import com.google.inject.servlet.RequestScoped;

/**
 * Test configuration web service
 * 
 * @author SauliusAlisauskas 2013-03-03 Initial version
 * 
 */
@Path("/testrun/")
@RequestScoped
public class TestRunWs {

	private final Logger mLogger = Logger.getLogger(TestRunWs.class);

	private TestRunDao mDao;

	@Inject
	public TestRunWs(TestRunDao dao) {
		mDao = dao;
	}

	/**
	 * 
	 * Add new test run
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(TestRun testRun) {

		//TODO: implement authentication
		testRun.setUserId(1L);

		mDao.add(testRun);
		
		return Response.status(200).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TestRun[] get() {		
		return mDao.getAll();
	}
}
