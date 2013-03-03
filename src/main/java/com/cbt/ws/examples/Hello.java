package com.cbt.ws.examples;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

/**
 * Simple service used for testing
 * 
 * @author Saulius Alisauskas 2012-10-05
 *
 */
@Path("/hello")
public class Hello {
	
	@Context
	HttpServletRequest request;
	
	Logger logger = Logger.getLogger(Hello.class);	
	
	@GET
	@Path("/test1")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {		
		return "Hello Jersey, request received from: " + request.getRemoteAddr();
	}
	
	@GET	
	@Path("/test2")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonTestObject getJsontest() {		
		return new JsonTestObject();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String sayPlainTextHelloPost(RequestHello r) {		
		return "Hello from Jersey";
	}	
}
