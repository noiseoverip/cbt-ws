package com.cbt.ws.main;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Root resource, required for server startup
 * 
 * @author esauali
 *
 */
@Path("/hello")
public class TestResource {
	
	@Context
	HttpServletRequest request;
	
	@GET	
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello Jersey1 + Guice, request received from: " + request.getRemoteAddr();
	}
}
