//package com.cbt.ws;
//
//import org.eclipse.jetty.server.Handler;
//import org.eclipse.jetty.server.NCSARequestLog;
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.server.handler.HandlerList;
//import org.eclipse.jetty.server.handler.RequestLogHandler;
//import org.eclipse.jetty.server.handler.ResourceHandler;
//import org.eclipse.jetty.servlet.DefaultServlet;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//
//import com.google.inject.servlet.GuiceFilter;
//
//public class Main {
//
//	public static void main(String[] args) throws Exception {
//		// Create the server.
//		Server server = new Server(8081);
//
//		// Create a servlet context and add the jersey servlet.
//		ServletContextHandler sch = new ServletContextHandler(server, "/");
//
//		// Add our Guice listener that includes our bindings
//		sch.addEventListener(new GuiceContextListener());
//
//		// Then add GuiceFilter and configure the server to
//		// reroute all requests through this filter.
//		sch.addFilter(GuiceFilter.class, "/*", null);
//
//		// Must add DefaultServlet for embedded Jetty.
//		// Failing to do this will cause 404 errors.
//		sch.addServlet(DefaultServlet.class, "/");
//
//		// Enable serving static content
//		ResourceHandler resourceHandler = new ResourceHandler();
//		resourceHandler.setDirectoriesListed(true);
//		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
//		resourceHandler.setResourceBase("./src/main/resources/html");
//
//		// Setup request log handler
//		RequestLogHandler requestLogHandler = new RequestLogHandler();
//		NCSARequestLog requestLog = new NCSARequestLog("./target/jetty-yyyy_mm_dd.request.log");
//		requestLog.setRetainDays(90);
//		requestLog.setAppend(true);
//		requestLog.setExtended(false);
//		requestLog.setLogTimeZone("GMT");
//		requestLogHandler.setRequestLog(requestLog);
//		
//		// Attach handlers to server instance
//		HandlerList handlers = new HandlerList();
//		handlers.setHandlers(new Handler[] { requestLogHandler, resourceHandler, sch });
//		server.setHandler(handlers);
//
//		// Start the server
//		server.start();
//		server.join();
//	}
//}
