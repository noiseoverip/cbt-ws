package com.cbt.ws.entity;

import java.util.Arrays;

import javax.persistence.Column;

import com.cbt.ws.jooq.enums.TestscriptTestscriptType;
import com.google.common.base.Objects;

/**
 * Enitity class representing test script data (file name, path...)
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 *
 */
public class TestScript {
	private String fileName;
	private String filePath;
	private Long id;
	private String name;
	private String[] testClasses;
	
	private TestscriptTestscriptType testScriptType;

	private Long userId;
	public TestScript() {
		
	}
	public TestScript(Long id, String name, String fileName, String path) {
		this.setName(name);
		this.setId(id);
		this.fileName = fileName;
		this.filePath = path;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String[] getTestClasses() {
		return testClasses;
	}

	public TestscriptTestscriptType getTestScriptType() {
		return testScriptType;
	}

	public Long getUserId() {
		return userId;
	}
	
	@Column(name = "testscript_file_name")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Column(name = "testscript_file_path")
	public void setFilePath(String path) {
		this.filePath = path;
	}

	@Column(name = "testscript_id")
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "testscript_name")
	public void setName(String name) {
		this.name = name;
	}	
	
	public void setTestClasses(String[] testClasses) {
		this.testClasses = testClasses;
	}
	
	@Column(name = "testscript_type")
	public void setTestScriptType(TestscriptTestscriptType testScripType) {
		this.testScriptType = testScripType;
	}	
	
	@Column(name = "testscript_user_id")
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", getId())
				.add("name", getName())
				.add("file-name", getFileName())
				.add("file-path", getFilePath())
				.add("testClasses", Arrays.toString(getTestClasses())).toString();
	}
}
