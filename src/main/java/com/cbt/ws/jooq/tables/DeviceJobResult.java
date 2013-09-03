/**
 * This class is generated by jOOQ
 */
package com.cbt.ws.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DeviceJobResult extends org.jooq.impl.TableImpl<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord> {

	private static final long serialVersionUID = -759042303;

	/**
	 * The singleton instance of <code>cbt.device_job_result</code>
	 */
	public static final com.cbt.ws.jooq.tables.DeviceJobResult DEVICE_JOB_RESULT = new com.cbt.ws.jooq.tables.DeviceJobResult();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord> getRecordType() {
		return com.cbt.ws.jooq.tables.records.DeviceJobResultRecord.class;
	}

	/**
	 * The column <code>cbt.device_job_result.devicejobresult_id</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, java.lang.Long> DEVICEJOBRESULT_ID = createField("devicejobresult_id", org.jooq.impl.SQLDataType.BIGINT, this);

	/**
	 * The column <code>cbt.device_job_result.created</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, java.sql.Timestamp> CREATED = createField("created", org.jooq.impl.SQLDataType.TIMESTAMP, this);

	/**
	 * The column <code>cbt.device_job_result.devicejobid</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, java.lang.Long> DEVICEJOBID = createField("devicejobid", org.jooq.impl.SQLDataType.BIGINT, this);

	/**
	 * The column <code>cbt.device_job_result.state</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, com.cbt.ws.jooq.enums.DeviceJobResultState> STATE = createField("state", org.jooq.util.mysql.MySQLDataType.VARCHAR.asEnumDataType(com.cbt.ws.jooq.enums.DeviceJobResultState.class), this);

	/**
	 * The column <code>cbt.device_job_result.testsRun</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, java.lang.Integer> TESTSRUN = createField("testsRun", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>cbt.device_job_result.testsFailed</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, java.lang.Integer> TESTSFAILED = createField("testsFailed", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>cbt.device_job_result.testsErrors</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, java.lang.Integer> TESTSERRORS = createField("testsErrors", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>cbt.device_job_result.output</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, java.lang.String> OUTPUT = createField("output", org.jooq.impl.SQLDataType.CLOB.length(65535), this);

	/**
	 * Create a <code>cbt.device_job_result</code> table reference
	 */
	public DeviceJobResult() {
		super("device_job_result", com.cbt.ws.jooq.Cbt.CBT);
	}

	/**
	 * Create an aliased <code>cbt.device_job_result</code> table reference
	 */
	public DeviceJobResult(java.lang.String alias) {
		super(alias, com.cbt.ws.jooq.Cbt.CBT, com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord, java.lang.Long> getIdentity() {
		return com.cbt.ws.jooq.Keys.IDENTITY_DEVICE_JOB_RESULT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord> getPrimaryKey() {
		return com.cbt.ws.jooq.Keys.KEY_DEVICE_JOB_RESULT_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<com.cbt.ws.jooq.tables.records.DeviceJobResultRecord>>asList(com.cbt.ws.jooq.Keys.KEY_DEVICE_JOB_RESULT_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.cbt.ws.jooq.tables.DeviceJobResult as(java.lang.String alias) {
		return new com.cbt.ws.jooq.tables.DeviceJobResult(alias);
	}
}