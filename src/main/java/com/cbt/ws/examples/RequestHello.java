package com.cbt.ws.examples;

/**
 * Entity for request Hello
 * 
 * @author Saulius Alisauskas 2012-10-05 Initial version
 * @author Saulius Alisauskas 2012-12-08 Remove parameter "ip", add javadocs
 *
 */
public class RequestHello {	
	/**
	 * Node serial number (sould be unique)
	 */
	private String sernum;	
	
	public RequestHello() {
		
	}
	
	/**
	 * Get {@link #sernum}
	 * 
	 * @return
	 */
	public String getSernum() {
		return sernum;
	}
	
	/**
	 * Set {@link #sernum}
	 * 
	 * @param sernum
	 */
	public void setSernum(String sernum) {
		this.sernum = sernum;
	}
	
	@Override
	public String toString() {
		return "HelloRequest [sernum=" + sernum + "]";
	}
}
