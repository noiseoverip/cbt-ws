/**
 * This class is generated by jOOQ
 */
package com.cbt.ws.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DeviceJobResultRecord extends org.jooq.impl.UpdatableRecordImpl<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord> implements org.jooq.Record8<java.lang.Long, java.sql.Timestamp, java.lang.Long, com.cbt.ws.jooq.enums.DeviceJobResultState, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String> {

	private static final long serialVersionUID = -1573565862;

	/**
	 * Setter for <code>cbt.device_job_result.devicejobresult_id</code>. 
	 */
	public void setDevicejobresultId(java.lang.Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>cbt.device_job_result.devicejobresult_id</code>. 
	 */
	public java.lang.Long getDevicejobresultId() {
		return (java.lang.Long) getValue(0);
	}

	/**
	 * Setter for <code>cbt.device_job_result.created</code>. 
	 */
	public void setCreated(java.sql.Timestamp value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>cbt.device_job_result.created</code>. 
	 */
	public java.sql.Timestamp getCreated() {
		return (java.sql.Timestamp) getValue(1);
	}

	/**
	 * Setter for <code>cbt.device_job_result.devicejobid</code>. 
	 */
	public void setDevicejobid(java.lang.Long value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>cbt.device_job_result.devicejobid</code>. 
	 */
	public java.lang.Long getDevicejobid() {
		return (java.lang.Long) getValue(2);
	}

	/**
	 * Setter for <code>cbt.device_job_result.state</code>. 
	 */
	public void setState(com.cbt.ws.jooq.enums.DeviceJobResultState value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>cbt.device_job_result.state</code>. 
	 */
	public com.cbt.ws.jooq.enums.DeviceJobResultState getState() {
		return (com.cbt.ws.jooq.enums.DeviceJobResultState) getValue(3);
	}

	/**
	 * Setter for <code>cbt.device_job_result.testsRun</code>. 
	 */
	public void setTestsrun(java.lang.Integer value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>cbt.device_job_result.testsRun</code>. 
	 */
	public java.lang.Integer getTestsrun() {
		return (java.lang.Integer) getValue(4);
	}

	/**
	 * Setter for <code>cbt.device_job_result.testsFailed</code>. 
	 */
	public void setTestsfailed(java.lang.Integer value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>cbt.device_job_result.testsFailed</code>. 
	 */
	public java.lang.Integer getTestsfailed() {
		return (java.lang.Integer) getValue(5);
	}

	/**
	 * Setter for <code>cbt.device_job_result.testsErrors</code>. 
	 */
	public void setTestserrors(java.lang.Integer value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>cbt.device_job_result.testsErrors</code>. 
	 */
	public java.lang.Integer getTestserrors() {
		return (java.lang.Integer) getValue(6);
	}

	/**
	 * Setter for <code>cbt.device_job_result.output</code>. 
	 */
	public void setOutput(java.lang.String value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>cbt.device_job_result.output</code>. 
	 */
	public java.lang.String getOutput() {
		return (java.lang.String) getValue(7);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Long> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.lang.Long, java.sql.Timestamp, java.lang.Long, com.cbt.ws.jooq.enums.DeviceJobResultState, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String> fieldsRow() {
		return (org.jooq.Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.lang.Long, java.sql.Timestamp, java.lang.Long, com.cbt.ws.jooq.enums.DeviceJobResultState, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String> valuesRow() {
		return (org.jooq.Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field1() {
		return com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT.DEVICEJOBRESULT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field2() {
		return com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT.CREATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field3() {
		return com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT.DEVICEJOBID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<com.cbt.ws.jooq.enums.DeviceJobResultState> field4() {
		return com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT.STATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field5() {
		return com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT.TESTSRUN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field6() {
		return com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT.TESTSFAILED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field7() {
		return com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT.TESTSERRORS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field8() {
		return com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT.OUTPUT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value1() {
		return getDevicejobresultId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value2() {
		return getCreated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value3() {
		return getDevicejobid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.cbt.ws.jooq.enums.DeviceJobResultState value4() {
		return getState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value5() {
		return getTestsrun();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value6() {
		return getTestsfailed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value7() {
		return getTestserrors();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value8() {
		return getOutput();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached DeviceJobResultRecord
	 */
	public DeviceJobResultRecord() {
		super(com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT);
	}
}