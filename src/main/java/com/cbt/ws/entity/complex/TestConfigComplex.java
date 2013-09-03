package com.cbt.ws.entity.complex;

import com.cbt.ws.entity.CbtEntity;
import com.cbt.ws.entity.TestScript;
import com.cbt.ws.entity.TestTarget;
import com.cbt.ws.utils.Utils;

/**
 * Entity representing test configuration data.
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 * 
 */
public class TestConfigComplex extends CbtEntity {

	private Long testProfileId;
	private TestScript testScript;
	private TestTarget testTarget;

	public TestScript getTestScript() {
		return testScript;
	}

	public void setTestScript(TestScript testScript) {
		this.testScript = testScript;
	}

	public TestTarget getTestTarget() {
		return testTarget;
	}

	public void setTestTarget(TestTarget testTarget) {
		this.testTarget = testTarget;
	}

	public Long getTestProfileId() {
		return testProfileId;
	}

	public void setTestProfileId(Long testProfileId) {
		this.testProfileId = testProfileId;
	}

	@Override
	public String toString() {
		return Utils.toString("TestConfig", "id", getId(), "testScriptId", testScript.getId(), "testTargetId",
				testTarget.getId(), "testProfileId", testProfileId);
	}

}
