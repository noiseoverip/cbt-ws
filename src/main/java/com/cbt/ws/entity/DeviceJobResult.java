package com.cbt.ws.entity;

import com.cbt.ws.jooq.enums.DeviceJobResultState;
import com.google.common.base.Objects;

/**
 * Device job result entity
 * 
 * @author SauliusAlisauskas 2013-04-07 Initial version
 *
 */
public class DeviceJobResult extends CbtEntity {
	public enum JunitTestSummary {
		ERRORS, FAILURES, TESTSRUN
	}
	private Long devicejobId;
	private String output;
	private DeviceJobResultState state;
	private Integer testsErrors;
	private Integer testsFailed;
	private Integer testsRun;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeviceJobResult) {
			DeviceJobResult other = (DeviceJobResult) obj;
			// TODO: improve comparison
			if (this.getId().equals(other.getId()) && this.getDevicejobId().equals(other.getDevicejobId())) {
				return true;
			}
		}
		return false;
	}

	public Long getDevicejobId() {
		return devicejobId;
	}

	public String getOutput() {
		return output;
	}

	public DeviceJobResultState getState() {
		return state;
	}
	
	public Integer getTestsErrors() {
		return testsErrors;
	}

	public Integer getTestsFailed() {
		return testsFailed;
	}

	public Integer getTestsRun() {
		return testsRun;
	}

	public void setDevicejobId(Long devicejobId) {
		this.devicejobId = devicejobId;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setState(DeviceJobResultState state) {
		this.state = state;
	}

	public void setTestsErrors(Integer testsErrors) {
		this.testsErrors = testsErrors;
	}

	public void setTestsFailed(Integer testsFailed) {
		this.testsFailed = testsFailed;
	}

	public void setTestsRun(Integer testsRun) {
		this.testsRun = testsRun;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", getId())
				.add("deviceJobId", devicejobId)
				.add("state", state)
				.add("failures" , testsFailed)
				.add("errors", testsErrors)
				.add("testsRun", testsRun).toString(); 
	}
}
