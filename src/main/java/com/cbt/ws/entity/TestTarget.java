package com.cbt.ws.entity;

import javax.persistence.Column;

import com.google.common.base.Objects;

/**
 * Entity representing test target (application) data (file name, path)
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 * 
 */
public class TestTarget {
	private String fileName;
	private String filePath;
	private Long id;
	private String name;
	private Long userId;

	public TestTarget() {

	}

	public TestTarget(Long id, String name, String fileName, String path) {
		this.name = name;
		this.id = id;
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

	public Long getUserId() {
		return userId;
	}

	@Column(name = "testtarget_file_name")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "testtarget_file_path")
	public void setFilePath(String path) {
		this.filePath = path;
	}

	@Column(name = "testtarget_id")
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "testtarget_name")
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "testtarget_user_id")
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", getId())
				.add("name", getName())
				.add("file-name", getFileName())
				.add("file-path", getFilePath()).toString();
	}

}
