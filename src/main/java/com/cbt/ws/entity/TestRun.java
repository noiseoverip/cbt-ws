package com.cbt.ws.entity;

import java.util.List;

import com.cbt.ws.jooq.enums.TestrunStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

/**
 * Entity representing single test run information (configuration id, status...)
 * 
 * @author Saulius Alisauskas 2013-03-24 Initial version
 * 
 */
public class TestRun extends CbtEntity {
	public enum Status {FINISHED, RUNNING, WAITING}
	
	/**
	 * Devices to be used for this testrun, calculated and assigned by system
	 */
	private List<Device> devices;
	private TestrunStatus status;	
	private Long testConfigId;
	
	@JsonIgnore
	private TestConfig testconfig;	

	public TestConfig getTestConfig() {
		return testconfig;
	}

	public void setTestConfig(TestConfig testConfig) {
		this.testconfig = testConfig;
	}

	@Override
	public boolean equals(Object object) {
		if (null != object && object instanceof TestRun) {
			TestRun other = (TestRun) object;
			if (getId().equals(other.getId()) && getTestConfigId().equals(other.getTestConfigId())
					&& getStatus().equals(other.getStatus())) {
				return true;
			}
		}
		return false;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public TestrunStatus getStatus() {
		return status;
	}

	public Long getTestConfigId() {
		return testConfigId;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public void setStatus(TestrunStatus status) {
		this.status = status;
	}

	public void setTestConfigId(Long testConfigId) {
		this.testConfigId = testConfigId;
	}	
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", getId()).add("devices", devices)
				.add("status", status).add("testconfig", testconfig).toString();
	}
}
