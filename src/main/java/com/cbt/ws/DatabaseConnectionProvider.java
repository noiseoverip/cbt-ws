package com.cbt.ws;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;

/**
 * Guice provider for {@link Connection}
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: Ericsson
 * </p>
 *
 * @author Saulius Alisauskas
 */
public class DatabaseConnectionProvider implements Provider<ComboPooledDataSource> {
   private ComboPooledDataSource mCpds;
   private final Logger mLogger = Logger.getLogger(DatabaseConnectionProvider.class);

   private Configuration mConfiguration;

   @Inject
   public DatabaseConnectionProvider(Configuration configuration) {
      mConfiguration = configuration;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ComboPooledDataSource get() {

      //This is needed for Tomcat to work !----------------
      try {
         Class.forName(mConfiguration.getDbDriverClass());
      } catch (ClassNotFoundException e) {
         mLogger.error("MySQL driver class not found !");
      }
      //----------------------------------------------------
      mCpds = new ComboPooledDataSource();
      mCpds.setJdbcUrl(mConfiguration.getDbJdbcUrl());
      mCpds.setUser(mConfiguration.getDbUser());
      mCpds.setPassword(mConfiguration.getDbPassword());
      mCpds.setInitialPoolSize(mConfiguration.getDbInitialPoolSize());
      mCpds.setAcquireIncrement(mConfiguration.getDbAcquireIncrement());
      mCpds.setMaxPoolSize(mConfiguration.getDbMaxPoolSize());
      mCpds.setMinPoolSize(mConfiguration.getDbMinPoolSize());
      mCpds.setMaxStatements(mConfiguration.getDbMaxStatements());
      mCpds.setMaxIdleTime(mConfiguration.getDbMaxIdleTime());
      return mCpds;
   }
}
