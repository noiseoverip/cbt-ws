package com.cbt.ws.examples;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.model.AbstractResourceModelContext;
import com.sun.jersey.api.model.AbstractResourceModelListener;

/**
 * Provider which initiates microgrid resource collector executor and it's tasks
 * 
 * @author saulius alisauskas 2012-12-11 Initial version
 * 
 */
@Provider
public class CollectorProvider implements AbstractResourceModelListener {

	private static Logger logger = Logger.getLogger(CollectorProvider.class);

	/**
	 * 
	 */
	private ScheduledExecutorService executor;

	@Override
	public void onLoaded(AbstractResourceModelContext modelContext) {		
		logger.info("Initiating resource collector service");
		
		executor = Executors.newSingleThreadScheduledExecutor();
		
		executor.scheduleAtFixedRate(collector, 1, 2, TimeUnit.SECONDS);
	}

	/**
	 * Resource collector runnable to be executor on
	 */
	private Runnable collector = new Runnable() {

		@Override
		public void run() {			
			Client client = Client.create();
			WebResource webResource = client
					.resource("http://localhost:8081/hello/jsontest");
			ClientResponse response = webResource.accept("application/json")
					.get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = response.getEntity(String.class);

			logger.debug("Received from server:" + output);

		}
	};
}
