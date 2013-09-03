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
public class Testrun extends org.jooq.impl.TableImpl<com.cbt.ws.jooq.tables.records.TestrunRecord> {

	private static final long serialVersionUID = -423881266;

	/**
	 * The singleton instance of <code>cbt.testrun</code>
	 */
	public static final com.cbt.ws.jooq.tables.Testrun TESTRUN = new com.cbt.ws.jooq.tables.Testrun();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.cbt.ws.jooq.tables.records.TestrunRecord> getRecordType() {
		return com.cbt.ws.jooq.tables.records.TestrunRecord.class;
	}

	/**
	 * The column <code>cbt.testrun.id</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.TestrunRecord, java.lang.Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT, this);

	/**
	 * The column <code>cbt.testrun.user_id</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.TestrunRecord, java.lang.Long> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.BIGINT, this);

	/**
	 * The column <code>cbt.testrun.test_config_id</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.TestrunRecord, java.lang.Long> TEST_CONFIG_ID = createField("test_config_id", org.jooq.impl.SQLDataType.BIGINT, this);

	/**
	 * The column <code>cbt.testrun.created</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.TestrunRecord, java.sql.Timestamp> CREATED = createField("created", org.jooq.impl.SQLDataType.TIMESTAMP, this);

	/**
	 * The column <code>cbt.testrun.updated</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.TestrunRecord, java.sql.Timestamp> UPDATED = createField("updated", org.jooq.impl.SQLDataType.TIMESTAMP, this);

	/**
	 * The column <code>cbt.testrun.status</code>. 
	 */
	public final org.jooq.TableField<com.cbt.ws.jooq.tables.records.TestrunRecord, com.cbt.ws.jooq.enums.TestrunStatus> STATUS = createField("status", org.jooq.util.mysql.MySQLDataType.VARCHAR.asEnumDataType(com.cbt.ws.jooq.enums.TestrunStatus.class), this);

	/**
	 * Create a <code>cbt.testrun</code> table reference
	 */
	public Testrun() {
		super("testrun", com.cbt.ws.jooq.Cbt.CBT);
	}

	/**
	 * Create an aliased <code>cbt.testrun</code> table reference
	 */
	public Testrun(java.lang.String alias) {
		super(alias, com.cbt.ws.jooq.Cbt.CBT, com.cbt.ws.jooq.tables.Testrun.TESTRUN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<com.cbt.ws.jooq.tables.records.TestrunRecord, java.lang.Long> getIdentity() {
		return com.cbt.ws.jooq.Keys.IDENTITY_TESTRUN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<com.cbt.ws.jooq.tables.records.TestrunRecord> getPrimaryKey() {
		return com.cbt.ws.jooq.Keys.KEY_TESTRUN_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<com.cbt.ws.jooq.tables.records.TestrunRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<com.cbt.ws.jooq.tables.records.TestrunRecord>>asList(com.cbt.ws.jooq.Keys.KEY_TESTRUN_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<com.cbt.ws.jooq.tables.records.TestrunRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<com.cbt.ws.jooq.tables.records.TestrunRecord, ?>>asList(com.cbt.ws.jooq.Keys.TESTRUN_IBFK_1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.cbt.ws.jooq.tables.Testrun as(java.lang.String alias) {
		return new com.cbt.ws.jooq.tables.Testrun(alias);
	}
}
