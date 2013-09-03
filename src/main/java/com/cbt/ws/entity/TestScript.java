package com.cbt.ws.entity;

import java.util.Arrays;

import com.cbt.ws.utils.Utils;

/**
 * Enitity class representing test script data (file name, path...)
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 *
 */
public class TestScript extends CbtEntity {
	private String fileName;
	private String path;
	private String[] testClasses;
	
	public TestScript() {
		
	}
	
	public TestScript(Long id, String name, String fileName, String path) {
		this.setName(name);
		this.setId(id);
		this.fileName = fileName;
		this.path = path;
	}
	
	public String getFileName() {
		return fileName;
	}

	public String getPath() {
		return path;
	}
	
	public String[] getTestClasses() {
		return testClasses;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setPath(String path) {
		this.path = path;
	}	

	public void setTestClasses(String[] testClasses) {
		this.testClasses = testClasses;
	}

	@Override
	public String toString() {
		return Utils.toString("TestScript", "id", getId(),"fileName", fileName, "path", path, "testClasses", Arrays.toString(testClasses));
	}
}
