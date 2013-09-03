package com.cbt.ws.entity;

import com.cbt.ws.utils.Utils;
import com.google.common.base.Objects;

/**
 * Entity representing test configuration data.
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 * 
 */
public class TestConfig extends CbtEntity {
	private Long testScriptId;
	private Long testTargetId;
	private Long testProfileId;
	private Long testConfigId;

	public Long getTestConfigId() {
		return testConfigId;
	}

	public void setTestConfigId(Long testConfigId) {
		this.testConfigId = testConfigId;
	}

	public Long getTestProfileId() {
		return testProfileId;
	}

	public void setTestProfileId(Long testProfileId) {
		this.testProfileId = testProfileId;
	}

	public Long getTestScriptId() {
		return testScriptId;
	}

	public void setTestScriptId(Long testScriptId) {
		this.testScriptId = testScriptId;
	}

	public Long getTestTargetId() {
		return testTargetId;
	}

	public void setTestTargetId(Long testTargetId) {
		this.testTargetId = testTargetId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestConfig) {
			TestConfig other = (TestConfig) obj;
			return this.getId().equals(other.getId()) && this.getName().equals(other.getName())
					&& this.getTestProfileId().equals(other.getTestProfileId())
					&& this.getTestScriptId().equals(other.getTestScriptId())
					&& this.getTestTargetId().equals(other.getTestTargetId());
		}
		return false;
	}

	@Override
	public String toString() {		
		return Objects.toStringHelper(this.getClass()).add("id", getTestConfigId()).add("testScriptId", testScriptId)
				.add("testTargetId", testTargetId).add("testProfileId", testProfileId).toString();
	}
}
