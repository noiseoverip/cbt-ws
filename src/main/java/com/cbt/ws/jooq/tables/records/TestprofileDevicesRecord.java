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
public class TestprofileDevicesRecord extends org.jooq.impl.UpdatableRecordImpl<com.cbt.ws.jooq.tables.records.TestprofileDevicesRecord> implements org.jooq.Record3<java.lang.Integer, java.lang.Long, java.lang.Long> {

	private static final long serialVersionUID = 298594647;

	/**
	 * Setter for <code>cbt.testprofile_devices.id</code>. 
	 */
	public void setId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>cbt.testprofile_devices.id</code>. 
	 */
	public java.lang.Integer getId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>cbt.testprofile_devices.testprofile_id</code>. 
	 */
	public void setTestprofileId(java.lang.Long value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>cbt.testprofile_devices.testprofile_id</code>. 
	 */
	public java.lang.Long getTestprofileId() {
		return (java.lang.Long) getValue(1);
	}

	/**
	 * Setter for <code>cbt.testprofile_devices.devicetype_id</code>. 
	 */
	public void setDevicetypeId(java.lang.Long value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>cbt.testprofile_devices.devicetype_id</code>. 
	 */
	public java.lang.Long getDevicetypeId() {
		return (java.lang.Long) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Integer> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<java.lang.Integer, java.lang.Long, java.lang.Long> fieldsRow() {
		return (org.jooq.Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<java.lang.Integer, java.lang.Long, java.lang.Long> valuesRow() {
		return (org.jooq.Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return com.cbt.ws.jooq.tables.TestprofileDevices.TESTPROFILE_DEVICES.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field2() {
		return com.cbt.ws.jooq.tables.TestprofileDevices.TESTPROFILE_DEVICES.TESTPROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field3() {
		return com.cbt.ws.jooq.tables.TestprofileDevices.TESTPROFILE_DEVICES.DEVICETYPE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value2() {
		return getTestprofileId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value3() {
		return getDevicetypeId();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TestprofileDevicesRecord
	 */
	public TestprofileDevicesRecord() {
		super(com.cbt.ws.jooq.tables.TestprofileDevices.TESTPROFILE_DEVICES);
	}
}
