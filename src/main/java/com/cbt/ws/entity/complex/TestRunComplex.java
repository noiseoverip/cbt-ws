package com.cbt.ws.entity.complex;

import java.util.List;

import com.cbt.ws.entity.CbtEntity;
import com.cbt.ws.entity.TestConfig;
import com.cbt.ws.entity.TestProfile;
import com.cbt.ws.utils.Utils;

/**
 * Entity representing detailed test run information
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 *
 */
public class TestRunComplex extends CbtEntity {
	private List<Long> deviceTypes;
	private TestConfig testConfig;	
	private TestProfile testProfile;
	public List<Long> getDeviceTypes() {
		return deviceTypes;
	}

	public TestConfig getTestConfig() {
		return testConfig;
	}

	public TestProfile getTestProfile() {
		return testProfile;
	}	

	public void setDeviceTypes(List<Long> deviceTypes) {
		this.deviceTypes = deviceTypes;
	}

	public void setTestConfig(TestConfig testConfig) {
		this.testConfig = testConfig;
	}	

	public void setTestProfile(TestProfile testProfile) {
		this.testProfile = testProfile;
	}	
	
	@Override
	public String toString() {		
		return Utils.toString("TestRunComplex", "testConfig", testConfig, "testProfile", testProfile, "deviceTypes", deviceTypes);
	}	
}
