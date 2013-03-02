package com.cbt.ws.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;

public class Main {

	public static void main(String[] args) throws Exception {
		// Create the server.
		Server server = new Server(8080);
		
		// Create a servlet context and add the jersey servlet.
		ServletContextHandler sch = new ServletContextHandler(server, "/");

		// Add our Guice listener that includes our bindings
		sch.addEventListener(new HelloGuiceServletConfig());

		// Then add GuiceFilter and configure the server to
		// reroute all requests through this filter.
		sch.addFilter(GuiceFilter.class, "/*", null);

		// Must add DefaultServlet for embedded Jetty.
		// Failing to do this will cause 404 errors.		
		sch.addServlet(DefaultServlet.class, "/");
		
		
		// Enable serving static content
		ResourceHandler resource_handler = new ResourceHandler();
	    resource_handler.setDirectoriesListed(true);
	    resource_handler.setWelcomeFiles(new String[]{ "index.html" });
	    //resource_handler.setResourceBase("\\src\\main\\java\\com\\cbt\\ws\\html");
	    resource_handler.setResourceBase("./src/main/java/com/cbt/ws/html");

	    HandlerList handlers = new HandlerList();
	    handlers.setHandlers(new Handler[] { resource_handler, sch });
	    server.setHandler(handlers);
		//sch.setResourceBase("\\source\\main\\webapp\\static");
		
		// Start the server
		server.start();
		server.join();
	}

}
