package com.cbt.ws.entity;

import com.cbt.ws.utils.Utils;

/**
 * Entity class representing a Test package data (test scripts, test targets, other).
 * 
 * NOTE: this entity is not present in database.
 * 
 * @author SauliusAlisauskas 2013-03-05 Initial version
 * 
 */
public class TestPackage {
	private Long devicejobId;
	private String testScriptPath;
	private String testTargetPath;
	private String testScriptFileName;
	private String testTargetFileName;

	public String getTestScriptFileName() {
		return testScriptFileName;
	}

	public void setTestScriptFileName(String testScriptFileName) {
		this.testScriptFileName = testScriptFileName;
	}

	public String getTestTargetFileName() {
		return testTargetFileName;
	}

	public void setTestTargetFileName(String testTargetFileName) {
		this.testTargetFileName = testTargetFileName;
	}

	public Long getDevicejobId() {
		return devicejobId;
	}

	public void setDevicejobId(Long devicejobId) {
		this.devicejobId = devicejobId;
	}

	public String getTestScriptPath() {
		return testScriptPath;
	}

	public void setTestScriptPath(String testScriptPath) {
		this.testScriptPath = testScriptPath;
	}

	public String getTestTargetPath() {
		return testTargetPath;
	}

	public void setTestTargetPath(String testTargetPath) {
		this.testTargetPath = testTargetPath;
	}

	@Override
	public String toString() {
		return Utils.toString("TestPackage", "AppName", testTargetFileName, "ScriptName", testScriptFileName,
				"AppPath", testTargetPath, "ScriptPath", testScriptPath);
	}
}
