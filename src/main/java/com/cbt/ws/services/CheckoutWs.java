package com.cbt.ws.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.CheckoutDao;
import com.google.inject.servlet.RequestScoped;

@Path("/checkout")
@RequestScoped
public class CheckoutWs {

	private final Logger mLogger = Logger.getLogger(CheckoutWs.class);
	private CheckoutDao mDao;
	
	@Inject
	public CheckoutWs(CheckoutDao dao) {
		mDao = dao;
	}
	
	@Path("/testpackage.zip")
	@GET
	@Produces("application/x-zip-compressed")
	public InputStream get() throws FileNotFoundException {		
		return new FileInputStream(new File(mDao.getPathToTestPackage()));
	}	
}
