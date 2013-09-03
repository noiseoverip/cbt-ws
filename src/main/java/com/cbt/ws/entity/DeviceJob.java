package com.cbt.ws.entity;

import com.cbt.ws.jooq.enums.DeviceJobStatus;
import com.google.common.base.Objects;

/**
 * Device job entity. Represents information describing a job(test) for specific device.
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 * 
 */
public class DeviceJob extends CbtEntity {

	// /**
	// * Constructor to construct entity from Jooq record
	// *
	// * @param record
	// * @return
	// */
	// public static DeviceJob fromJooqRecord(DeviceJobRecord record) {
	// if (null == record) {
	// return null;
	// }
	// DeviceJob job = new DeviceJob();
	// job.setId(record.getId());
	// job.setDeviceId(record.getDeviceId());
	// job.setTestRunId(record.getTestRunId());
	// job.setStatus(record.getStatus());
	// job.setCreated(record.getCreated());
	// job.setUpdated(record.getUpdated());
	// return job;
	// }	

	private Long deviceId;

	private DeviceJobMetadata metadata = new DeviceJobMetadata();
	private DeviceJobStatus status;

	private Long testRunId;;

	/**
	 * Default constructor
	 */
	public DeviceJob() {

	}

	public Long getDeviceId() {
		return deviceId;
	}

	public DeviceJobMetadata getMetadata() {
		return metadata;
	}

	public DeviceJobStatus getStatus() {
		return status;
	}

	public Long getTestRunId() {
		return testRunId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public void setMetadata(DeviceJobMetadata metadata) {
		this.metadata = metadata;
	}

	public void setStatus(DeviceJobStatus status) {
		this.status = status;
	}

	public void setTestRunId(Long testRunId) {
		this.testRunId = testRunId;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", getId()).add("deviceId", deviceId)
				.add("testRunId", testRunId).add("status", status).add("meta", getMetadata()).toString();
	}
}
