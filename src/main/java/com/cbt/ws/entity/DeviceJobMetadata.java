package com.cbt.ws.entity;

import java.util.Arrays;

import com.cbt.ws.utils.Utils;


public class DeviceJobMetadata {
	private String[] testClasses;

	public String[] getTestClasses() {
		return testClasses;
	}

	public void setTestClasses(String[] testClasses) {
		this.testClasses = testClasses;
	}
	
	@Override
	public String toString() {		
		return Utils.toString("DeviceJobMetadata", "testclasses", Arrays.toString(testClasses));
	}

}
