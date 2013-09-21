package com.cbt.ws.entity;

import java.util.List;

import javax.persistence.Column;

import com.cbt.ws.jooq.enums.TestprofileTestprofileMode;
import com.google.common.base.Objects;

//TODO: need to add max number of devices to execute on
/**
 * Entity representing test profile data (mode, device types...)
 * 
 * @author Saulius Alisauskas 2013-03-24 Initial version
 * 
 */
public class TestProfile {	
	private List<Long> deviceTypes;
	private List<DeviceType> deviceTypesList; // TODO: fix this, shoud only be one list	
	private Long id;
	private TestprofileTestprofileMode mode;	
	private String name;
	private Long userId;
	
	public List<Long> getDeviceTypes() {
		return deviceTypes;
	}

	public List<DeviceType> getDeviceTypesList() {
		return deviceTypesList;
	}
	public Long getId() {
		return id;
	}
	public TestprofileTestprofileMode getMode() {
		return mode;
	}

	public String getName() {
		return name;
	}
	public Long getUserId() {
		return userId;
	};

	public void setDeviceTypes(List<Long> deviceTypes) {
		this.deviceTypes = deviceTypes;
	}

	public void setDeviceTypesList(List<DeviceType> deviceTypesList) {
		this.deviceTypesList = deviceTypesList;
	}

	@Column(name = "testprofile_id")
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "testprofile_mode")
	public void setMode(TestprofileTestprofileMode mode) {
		this.mode = mode;
	}

	@Column(name = "testprofile_name")
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "testprofile_user_id")
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", getId())
				.add("name", getName())
				.add("userId", getUserId())
				.add("mode", getMode()).toString();	
	}
}
