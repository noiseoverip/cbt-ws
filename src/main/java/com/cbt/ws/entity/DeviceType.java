package com.cbt.ws.entity;

public class DeviceType extends CbtEntity {
	private String model;
	private String manufacture;
	
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
