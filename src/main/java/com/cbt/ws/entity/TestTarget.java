package com.cbt.ws.entity;

import com.cbt.ws.utils.Utils;

/**
 * Entity representing test target (application) data (file name, path)
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 * 
 */
public class TestTarget extends CbtEntity {
	private String fileName;
	private String path;
	
	public TestTarget() {
		
	}
	
	public TestTarget(Long id, String name, String fileName, String path) {
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

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return Utils.toString("TestTarget", "id", getId(), "fileName", fileName, "path", path);
	}

}
