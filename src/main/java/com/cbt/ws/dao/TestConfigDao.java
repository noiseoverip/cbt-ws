package com.cbt.ws.dao;

import static com.cbt.jooq.tables.Testconfig.TESTCONFIG;
import static com.cbt.jooq.tables.Testprofile.TESTPROFILE;
import static com.cbt.jooq.tables.Testscript.TESTSCRIPT;
import static com.cbt.jooq.tables.Testtarget.TESTTARGET;

import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.RecordMapper;

import com.cbt.core.entity.TestConfig;
import com.cbt.core.entity.TestProfile;
import com.cbt.core.entity.TestScript;
import com.cbt.core.entity.TestTarget;
import com.cbt.core.entity.complex.TestConfigComplex;
import com.cbt.ws.JooqDao;

/**
 * Test configuration DAO
 *
 * @author SauliusALisauskas 2013-03-03 Initial version
 */
public class TestConfigDao extends JooqDao {

   private final Logger mLogger = Logger.getLogger(TestConfigDao.class);
   /**
    * Mapper for building full TestConfig object
    */
   private final RecordMapper<Record, TestConfigComplex> testConfigMapper = new RecordMapper<Record, TestConfigComplex>() {

      @Override
      public TestConfigComplex map(Record record) {
         TestConfigComplex testConfig = record.into(TestConfigComplex.class);
         testConfig.setTestTarget(record.into(TestTarget.class));
         testConfig.setTestScript(record.into(TestScript.class));
         testConfig.setTestProfile(record.into(TestProfile.class));
         return testConfig;
      }

   };

   @Inject
   public TestConfigDao(DataSource dataSource) {
      super(dataSource);
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

   /**
    * Get test runs of specific user
    *
    * @param userId
    * @return
    */
   public List<TestConfigComplex> getByUserId(Long userId) {
      return getDbContext().select().from(TESTCONFIG)
            .join(TESTPROFILE).on(TESTPROFILE.TESTPROFILE_ID.eq(TESTCONFIG.TEST_PROFILE_ID))
            .join(TESTSCRIPT).on(TESTSCRIPT.TESTSCRIPT_ID.eq(TESTCONFIG.TEST_SCRIPT_ID))
            .join(TESTTARGET).on(TESTTARGET.TESTTARGET_ID.eq(TESTCONFIG.TEST_TARGET_ID))
            .where(TESTCONFIG.USER_ID.eq(userId))
            .orderBy(TESTCONFIG.UPDATED.desc()).fetch(testConfigMapper);
   }

   /**
    * Get test config by its id
    *
    * @param id - test config id
    * @return test config object
    */
   public TestConfig getById(Long id) {
      return getDbContext().select().from(TESTCONFIG).where(TESTCONFIG.TEST_CONFIG_ID.eq(id))
            .fetchOneInto(TestConfig.class);
   }
}
