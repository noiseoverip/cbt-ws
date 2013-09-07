package com.cbt.ws.entity;

/**
 * Device type entity class
 * 
 * @author SauliusAlisauskas 2013-03-22 Initial version
 *
 */
public class DeviceType extends CbtEntity {
	private String model;
	private String manufacture;
	
	public DeviceType() {		
	}
	
	public DeviceType (String theManufacture, String theModel) {
		manufacture = theManufacture;
		model = theModel;
	}
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getManufacture() {
		return manufacture;
	}
	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}
}
