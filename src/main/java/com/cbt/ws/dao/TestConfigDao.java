package com.cbt.ws.dao;

import com.cbt.core.entity.TestConfig;
import com.cbt.core.entity.TestScript;
import com.cbt.core.entity.TestTarget;
import com.cbt.core.entity.complex.TestConfigComplex;
import com.cbt.ws.JooqDao;
import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static com.cbt.jooq.tables.Testconfig.TESTCONFIG;
import static com.cbt.jooq.tables.Testscript.TESTSCRIPT;
import static com.cbt.jooq.tables.Testtarget.TESTTARGET;

/**
 * Test configuration DAO
 *
 * @author SauliusALisauskas 2013-03-03 Initial version
 */
public class TestConfigDao extends JooqDao {

   private final Logger mLogger = Logger.getLogger(TestConfigDao.class);

   @Inject
   public TestConfigDao(DataSource dataSource) {
      super(dataSource);
   }

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
      Result<Record> result = getDbContext().select().from(TESTCONFIG).join(TESTSCRIPT)
            .on(TESTSCRIPT.TESTSCRIPT_ID.eq(TESTCONFIG.TEST_SCRIPT_ID)).join(TESTTARGET)
            .on(TESTTARGET.TESTTARGET_ID.eq(TESTCONFIG.TEST_TARGET_ID)).orderBy(TESTCONFIG.UPDATED.desc()).fetch();

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
      Result<Record> result = getDbContext().select().from(TESTCONFIG).join(TESTSCRIPT)
            .on(TESTSCRIPT.TESTSCRIPT_ID.eq(TESTCONFIG.TEST_SCRIPT_ID)).join(TESTTARGET)
            .on(TESTTARGET.TESTTARGET_ID.eq(TESTCONFIG.TEST_TARGET_ID)).where(TESTCONFIG.USER_ID.eq(userId))
            .orderBy(TESTCONFIG.UPDATED.desc()).fetch();

      for (Record r : result) {
         TestConfigComplex tc = new TestConfigComplex();
         tc.setId(r.getValue(TESTCONFIG.TEST_CONFIG_ID));
         tc.setTestTarget(new TestTarget(r.getValue(TESTTARGET.TESTTARGET_ID), r.getValue(TESTTARGET.TESTTARGET_NAME), r.getValue(TESTTARGET.TESTTARGET_FILE_NAME), r.getValue(TESTTARGET.TESTTARGET_FILE_PATH)));
         tc.setTestScript((new TestScript(r.getValue(TESTSCRIPT.TESTSCRIPT_ID), r.getValue(TESTSCRIPT.TESTSCRIPT_NAME), r.getValue(TESTSCRIPT.TESTSCRIPT_FILE_NAME), r.getValue(TESTSCRIPT.TESTSCRIPT_FILE_PATH))));
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
    * @param testConfig
    * @return
    */
   public Long add(TestConfig testConfig) {
      mLogger.trace("Adding new test configuration");
      Long testConfigID = getDbContext()
            .insertInto(TESTCONFIG, TESTCONFIG.USER_ID, TESTCONFIG.TEST_CONFIG_NAME, TESTCONFIG.TEST_SCRIPT_ID,
                  TESTCONFIG.TEST_TARGET_ID, TESTCONFIG.TEST_PROFILE_ID)
            .values(testConfig.getUserId(), testConfig.getName(), testConfig.getTestScriptId(),
                  testConfig.getTestTargetId(), testConfig.getTestProfileId())
            .returning(TESTCONFIG.TEST_CONFIG_ID).fetchOne().getTestConfigId();
      mLogger.trace("Added test configuration, new id:" + testConfigID);
      return testConfigID;
   }
}
