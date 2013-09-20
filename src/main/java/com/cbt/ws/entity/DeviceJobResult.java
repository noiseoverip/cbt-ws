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
	private String output;
	private Integer testsRun;
	private Integer testsFailed;
	private Integer testsErrors;
	private Long devicejobId;

//	/**
//	 * Constructor taking JooQ record as an argument
//	 * 
//	 * @param r
//	 * @return
//	 */
//	public static DeviceJobResult fromJooqRecord(DeviceJobResultRecord r) {
//		DeviceJobResult job = new DeviceJobResult();
//		job.setId(r.getDevicejobresultId());
//		job.setCreated(r.getCreated());
//		job.setDevicejobid(r.getDevicejobId());
//		job.setOutput(r.getOutput());
//		job.setTestserrors(r.getTestsErrors());
//		job.setTestsfailed(r.getTestsFailed());
//		job.setTestsrun(r.getTestsRun());
//		job.setState(State.valueOf(r.getState().toString()));
//		return job;
//	}

	public Long getDevicejobId() {
		return devicejobId;
	}

	public void setDevicejobid(Long devicejobId) {
		this.devicejobId = devicejobId;
	}

	private DeviceJobResultState state;

	public DeviceJobResultState getState() {
		return state;
	}

	public void setState(DeviceJobResultState state) {
		this.state = state;
	}

	public enum JunitTestSummary {
		TESTSRUN, FAILURES, ERRORS
	}
	
	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Integer getTestsRun() {
		return testsRun;
	}

	public void setTestsrun(Integer testsRun) {
		this.testsRun = testsRun;
	}

	public Integer getTestsFailed() {
		return testsFailed;
	}

	public void setTestsfailed(Integer testsFailed) {
		this.testsFailed = testsFailed;
	}

	public Integer getTestsErrors() {
		return testsErrors;
	}

	public void setTestserrors(Integer testsErrors) {
		this.testsErrors = testsErrors;
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
}
