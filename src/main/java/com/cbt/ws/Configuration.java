package com.cbt.ws;

import com.google.common.base.Objects;
import com.google.inject.name.Named;

import javax.inject.Inject;

/**
 * Class for keeping all configuration options
 *
 * @author SauliusAlisauskas
 */
public class Configuration {
   private int dbAcquireIncrement;
   private String dbDriverClass;
   private int dbInitialPoolSize;
   private String dbJdbcUrl;
   private int dbMaxIdleTime;
   private int dbMaxPoolSize;
   private int dbMaxStatements;
   private int dbMinPoolSize;
   private String dbPassword;
   private String dbTestJdbcUrl;
   private String dbTestPassword;
   private String dbTestUser;
   private String dbUser;
   private boolean inTestingEnvironment = false;
   private String awsPropertiesPath;

   public Configuration() {
      // Override certain properties if provided as JVA arguments
      if ("integration".equals(System.getProperty("cbt.ws.environment"))) {
         inTestingEnvironment = true;
      }
   }

   public int getDbAcquireIncrement() {
      return dbAcquireIncrement;
   }

   public String getDbDriverClass() {
      return dbDriverClass;
   }

   public int getDbInitialPoolSize() {
      return dbInitialPoolSize;
   }

   public String getDbJdbcUrl() {
      return isInTestingMode() ? dbTestJdbcUrl : dbJdbcUrl;
   }

   public int getDbMaxIdleTime() {
      return dbMaxIdleTime;
   }

   public int getDbMaxPoolSize() {
      return dbMaxPoolSize;
   }

   public int getDbMaxStatements() {
      return dbMaxStatements;
   }

   public int getDbMinPoolSize() {
      return dbMinPoolSize;
   }

   public String getDbPassword() {
      return isInTestingMode() ? dbTestPassword : dbPassword;
   }

   public String getDbUser() {
      return isInTestingMode() ? dbTestUser : dbUser;
   }

   public String getAwsPropertiesPath() {
      return awsPropertiesPath;
   }

   public boolean isInTestingMode() {
      return inTestingEnvironment;
   }

   @Inject
   public void setDbAcquireIncrement(@Named("cbt.ws.db.acquireIncrement") int dbAcquireIncrement) {
      this.dbAcquireIncrement = dbAcquireIncrement;
   }

   @Inject
   public void setDbDriverClass(@Named("cbt.ws.db.driverClass") String dbDriverClass) {
      this.dbDriverClass = dbDriverClass;
   }

   @Inject
   public void setDbInitialPoolSize(@Named("cbt.ws.db.initialPoolSize") int dbInitialPoolSize) {
      this.dbInitialPoolSize = dbInitialPoolSize;
   }

   @Inject
   public void setDbJdbcUrl(@Named("cbt.ws.db.jdbc_url") String dbJdbcUrl) {
      this.dbJdbcUrl = dbJdbcUrl;
   }

   @Inject
   public void setDbMaxIdleTime(@Named("cbt.ws.db.maxIdleTime") int dbMaxIdleTime) {
      this.dbMaxIdleTime = dbMaxIdleTime;
   }

   @Inject
   public void setDbMaxPoolSize(@Named("cbt.ws.db.maxPoolSize") int dbMaxPoolSize) {
      this.dbMaxPoolSize = dbMaxPoolSize;
   }

   @Inject
   public void setDbMaxStatements(@Named("cbt.ws.db.maxStatements") int dbMaxStatements) {
      this.dbMaxStatements = dbMaxStatements;
   }

   @Inject
   public void setDbMinPoolSize(@Named("cbt.ws.db.minPoolSize") int dbMinPoolSize) {
      this.dbMinPoolSize = dbMinPoolSize;
   }

   @Inject
   public void setDbPassword(@Named("cbt.ws.db.password") String dbPassword) {
      this.dbPassword = dbPassword;
   }

   @Inject
   public void setDbTestJdbcUrl(@Named("cbt.ws.db.test.jdbc.url") String dbTestJdbcUrl) {
      this.dbTestJdbcUrl = dbTestJdbcUrl;
   }

   @Inject
   public void setDbTestPassword(@Named("cbt.ws.db.test.password") String dbTestPassword) {
      this.dbTestPassword = dbTestPassword;
   }

   @Inject
   public void setDbTestUser(@Named("cbt.ws.db.test.user") String dbTestUser) {
      this.dbTestUser = dbTestUser;
   }

   @Inject
   public void setDbUser(@Named("cbt.ws.db.user") String dbUser) {
      this.dbUser = dbUser;
   }

   @Inject
   public void setAwsPropertiesPath(@Named("cbt.aws.properties") String awsPropertiesPath) {
      this.awsPropertiesPath = awsPropertiesPath;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("cbt.ws.db.jdbc_url", getDbJdbcUrl())
            .add("cbt.ws.db.user", getDbUser()).toString();
   }

}
