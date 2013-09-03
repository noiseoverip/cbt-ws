package com.cbt.ws.entity;

import java.util.List;

import com.cbt.ws.jooq.enums.TestprofileMode;
import com.cbt.ws.utils.Utils;

//TODO: need to add max number of devices to execute on
/**
 * Entity representing test profile data (mode, device types...)
 * 
 * @author Saulius Alisauskas 2013-03-24 Initial version
 * 
 */
public class TestProfile extends CbtEntity {	
	private List<Long> deviceTypes;

	private List<DeviceType> deviceTypesList; // TODO: fix this, shoud only be one list
	private TestprofileMode mode;;

	public List<Long> getDeviceTypes() {
		return deviceTypes;
	}

	public List<DeviceType> getDeviceTypesList() {
		return deviceTypesList;
	}

	public TestprofileMode getMode() {
		return mode;
	}

	public void setDeviceTypes(List<Long> deviceTypes) {
		this.deviceTypes = deviceTypes;
	}

	public void setDeviceTypesList(List<DeviceType> deviceTypesList) {
		this.deviceTypesList = deviceTypesList;
	}

	public void setMode(TestprofileMode mode) {
		this.mode = mode;
	}
	
	@Override
	public String toString() {
		return Utils.toString("TestProfile", "Mode", mode, "DeviceTypes", deviceTypes);
	}
}
