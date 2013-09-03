package com.cbt.ws.dao;

import static com.cbt.ws.jooq.tables.Testconfig.TESTCONFIG;
import static com.cbt.ws.jooq.tables.Testscript.TESTSCRIPT;
import static com.cbt.ws.jooq.tables.Testtarget.TESTTARGET;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;

import com.cbt.ws.JooqDao;
import com.cbt.ws.entity.TestConfig;
import com.cbt.ws.entity.TestScript;
import com.cbt.ws.entity.TestTarget;
import com.cbt.ws.entity.complex.TestConfigComplex;

/**
 * Test configuration DAO
 * 
 * @author SauliusALisauskas 2013-03-03 Initial version
 * 
 */
public class TestConfigDao extends JooqDao {
	
	@Inject
	public TestConfigDao(DataSource dataSource) {
		super(dataSource);
	}

	private final Logger mLogger = Logger.getLogger(TestConfigDao.class);

	// TODO: remove, use only complex
	/**
	 * Get test configurations
	 * 
	 * @return
	 */
	public TestConfig[] getAll() {
		List<TestConfig> testExecutions = new ArrayList<TestConfig>();
		Result<Record> result = getDbContext().select().from(TESTCONFIG).orderBy(TESTCONFIG.UPDATED.desc()).fetch();
		for (Record r : result) {
			TestConfig tc = new TestConfig();
			tc.setId(r.getValue(TESTCONFIG.TEST_CONFIG_ID));
			tc.setTestTargetId(r.getValue(TESTCONFIG.TEST_TARGET_ID));
			tc.setTestScriptId(r.getValue(TESTCONFIG.TEST_SCRIPT_ID));
			tc.setTestProfileId(r.getValue(TESTCONFIG.TEST_PROFILE_ID));
			tc.setName(r.getValue(TESTCONFIG.TEST_CONFIG_NAME));
			tc.setUserId(r.getValue(TESTCONFIG.USER_ID));
			tc.setUpdated(r.getValue(TESTCONFIG.UPDATED));
			testExecutions.add(tc);
			mLogger.debug(tc);
		}
		return testExecutions.toArray(new TestConfig[testExecutions.size()]);
	}

	/**
	 * Get test configuration full objects
	 * 
	 * @return
	 */
	public TestConfigComplex[] getAllComplex() {
		List<TestConfigComplex> testExecutions = new ArrayList<TestConfigComplex>();
		Result<Record> result =  getDbContext().select().from(TESTCONFIG).join(TESTSCRIPT)
				.on(TESTSCRIPT.ID.eq(TESTCONFIG.TEST_SCRIPT_ID)).join(TESTTARGET)
				.on(TESTTARGET.ID.eq(TESTCONFIG.TEST_TARGET_ID)).orderBy(TESTCONFIG.UPDATED.desc()).fetch();

		for (Record r : result) {
			TestConfigComplex tc = new TestConfigComplex();
			tc.setId(r.getValue(TESTCONFIG.TEST_CONFIG_ID));
			tc.setTestTarget(r.into(TestTarget.class));
			tc.setTestScript(r.into(TestScript.class));
			tc.setTestProfileId(r.getValue(TESTCONFIG.TEST_PROFILE_ID));
			tc.setName(r.getValue(TESTCONFIG.TEST_CONFIG_NAME));
			tc.setUserId(r.getValue(TESTCONFIG.USER_ID));
			tc.setUpdated(r.getValue(TESTCONFIG.UPDATED));
			testExecutions.add(tc);
			mLogger.debug(tc);
		}
		return testExecutions.toArray(new TestConfigComplex[testExecutions.size()]);
	}

	/**
	 * Get test runs of specific user
	 * 
	 * @param userId
	 * @return
	 */
	public TestConfigComplex[] getByUserId(Long userId) {
		List<TestConfigComplex> testExecutions = new ArrayList<TestConfigComplex>();
		Result<Record> result =  getDbContext().select().from(TESTCONFIG).join(TESTSCRIPT)
				.on(TESTSCRIPT.ID.eq(TESTCONFIG.TEST_SCRIPT_ID)).join(TESTTARGET)
				.on(TESTTARGET.ID.eq(TESTCONFIG.TEST_TARGET_ID)).where(TESTCONFIG.USER_ID.eq(userId))
				.orderBy(TESTCONFIG.UPDATED.desc()).fetch();

		for (Record r : result) {
			TestConfigComplex tc = new TestConfigComplex();
			tc.setId(r.getValue(TESTCONFIG.TEST_CONFIG_ID));
			tc.setTestTarget(new TestTarget(r.getValue(TESTTARGET.ID), r.getValue(TESTTARGET.NAME), r.getValue(TESTTARGET.FILE_NAME), r.getValue(TESTTARGET.PATH)));
			tc.setTestScript((new TestScript(r.getValue(TESTSCRIPT.ID), r.getValue(TESTSCRIPT.NAME), r.getValue(TESTSCRIPT.FILE_NAME), r.getValue(TESTSCRIPT.PATH))));
			tc.setTestProfileId(r.getValue(TESTCONFIG.TEST_PROFILE_ID));
			tc.setName(r.getValue(TESTCONFIG.TEST_CONFIG_NAME));
			tc.setUserId(r.getValue(TESTCONFIG.USER_ID));
			tc.setUpdated(r.getValue(TESTCONFIG.UPDATED));
			testExecutions.add(tc);
		}
		return testExecutions.toArray(new TestConfigComplex[testExecutions.size()]);
	}

	/**
	 * Add new test configuration
	 * 
	 * @param userid
	 * @return
	 */
	public Long add(TestConfig testConfig) {
		mLogger.trace("Adding new test configuration");
		Long testConfigID =  getDbContext()
				.insertInto(TESTCONFIG, TESTCONFIG.USER_ID, TESTCONFIG.TEST_CONFIG_NAME, TESTCONFIG.TEST_SCRIPT_ID,
						TESTCONFIG.TEST_TARGET_ID, TESTCONFIG.TEST_PROFILE_ID)
				.values(testConfig.getUserId(), testConfig.getName(), testConfig.getTestScriptId(),
						testConfig.getTestTargetId(), testConfig.getTestProfileId())
				.returning(TESTCONFIG.TEST_CONFIG_ID).fetchOne().getTestConfigId();
		mLogger.trace("Added test configuration, new id:" + testConfigID);
		return testConfigID;
	}
}
