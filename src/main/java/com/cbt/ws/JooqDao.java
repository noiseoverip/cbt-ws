package com.cbt.ws;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Super class for all JOOQ based DAO's
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: Ericsson
 * </p>
 *
 * @author esauali 2013-06-05 Initial version
 */
public class JooqDao {
   private DataSource mDataSource;
   private Configuration mConfiguration;

   /**
    * Main constructor
    *
    * @param connection
    */
   public JooqDao(DataSource dataSource) {
      mDataSource = dataSource;
   }

   /**
    * Get JOOQ executor
    *
    * @return
    */
   public DSLContext getDbContext() {
      // If in testing mode, we need to map CBT->CBTTESTING DB NAME since that is hardcoded in JOOQ generated classes
      if (mConfiguration.isInTestingMode()) {
         Settings settings = new Settings().withRenderMapping(new RenderMapping().withSchemata(new MappedSchema()
               .withInput("cbt").withOutput("cbttest")));
         return DSL.using(mDataSource, SQLDialect.MYSQL, settings);
      }
      return DSL.using(mDataSource, SQLDialect.MYSQL);
   }

   @Inject
   public void setConfiguration(Configuration config) {
      mConfiguration = config;
   }
}
