package com.cbt.ws.dao;

import static com.cbt.jooq.tables.Device.DEVICE;
import static com.cbt.jooq.tables.DeviceJob.DEVICE_JOB;
import static com.cbt.jooq.tables.Testrun.TESTRUN;
import static com.cbt.jooq.tables.User.USER;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.cbt.core.entity.User;
import com.cbt.jooq.tables.records.UserRecord;
import com.cbt.ws.JooqDao;

/**
 * User Dao
 *
 * @author SauliusAlisauskas 2013-04-08 Initial version
 */
public class UserDao extends JooqDao {

   @Inject
   public UserDao(DataSource dataSource) {
      super(dataSource);
   }

   /**
    * Authenticate user
    *
    * @param username
    * @param password
    * @return
    */
   public User authenticate(String username, String password) {
      UserRecord record = (UserRecord) getDbContext().select().from(USER)
            .where(USER.NAME.eq(username).and(USER.PASSWORD.eq(password))).fetchOne();
      return record.into(User.class);
   }

   /**
    * Get user by name
    *
    * @param name
    * @return
    */
   public User getUserByName(String name) {
      return getDbContext().select().from(USER).where(USER.NAME.eq(name)).fetchOneInto(User.class);
   }

   /**
    * Create new user
    */
   public User createNew(String username, String password) {
      UserRecord user = getDbContext().newRecord(USER);
      user.setName(username);
      user.setPassword(password);
      if (user.insert() == 1) {
         return user.into(User.class);
      } else {
         return null;
      }
   }

   /**
    * Get user object
    *
    * @param userId
    * @return
    */
   public Map<String, Object> getUserById(Long userId) {
      UserRecord record = (UserRecord) getDbContext().select().from(USER).where(USER.ID.eq(userId)).fetchOne();
      return record.intoMap();
   }

   /**
    * Get test runs per device hosted by user
    *
    * @param userId
    * @return
    */
   public List<Map<String, Object>> getUserHostedTestStats(Long userId) {
      Result<Record2<Long, Integer>> result = getDbContext().select(DEVICE_JOB.DEVICE_JOB_DEVICE_ID, DSL.count().as("runs"))
.from(DEVICE_JOB).join(DEVICE)
            .on(DEVICE_JOB.DEVICE_JOB_DEVICE_ID.eq(DEVICE.DEVICE_ID)).where(DEVICE.DEVICE_OWNER_ID.eq(userId))
            .groupBy(DEVICE_JOB.DEVICE_JOB_DEVICE_ID).fetch();
      return result.intoMaps();
   }

   /**
    * Get statistics of services consumed by user
    *
    * @param userId
    * @return
    */
   public List<Map<String, Object>> getUserRunTestStats(Long userId) {
      Result<Record1<Integer>> result = getDbContext().select(DSL.count().as("runs")).from(DEVICE_JOB).join(TESTRUN)
            .on(DEVICE_JOB.DEVICE_JOB_TESTRUN_ID.eq(TESTRUN.TESTRUN_ID)).where(TESTRUN.TESTRUN_USER_ID.eq(userId)).fetch();
      return result.intoMaps();
   }

   /**
    * Get list of users
    *
    * @return
    */
   public List<Map<String, Object>> getAllUsers() {
      Result<Record2<Long, String>> result = getDbContext().select(USER.ID, USER.NAME).from(USER).fetch();
      return result.intoMaps();
   }
}
