package com.cbt.ws.dao;

import com.cbt.core.entity.TestPackage;
import com.cbt.ws.Configuration;
import com.cbt.ws.JooqDao;
import org.apache.log4j.Logger;
import org.jooq.Record;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.cbt.jooq.tables.DeviceJob.DEVICE_JOB;
import static com.cbt.jooq.tables.Testconfig.TESTCONFIG;
import static com.cbt.jooq.tables.Testrun.TESTRUN;
import static com.cbt.jooq.tables.Testscript.TESTSCRIPT;
import static com.cbt.jooq.tables.Testtarget.TESTTARGET;

/**
 * DAO for checkout(downloading files) operations
 *
 * @author Saulius Alisauskas 2013-03-05 Initial version
 */
public class CheckoutDao extends JooqDao {

   private final Logger mLogger = Logger.getLogger(CheckoutDao.class);
   private String mFileStorePath;

   /**
    * Main constructor
    *
    * @param configuration
    * @param dataSource
    */
   @Inject
   public CheckoutDao(Configuration configuration, DataSource dataSource) {
      super(dataSource);
   }

   /**
    * Get TestPackage info
    *
    * @param devicejobId
    * @return
    */
   public TestPackage getTestPackage(Long devicejobId) {
      // TODO: improve size of returned data, we only need a couple of fields
      Record result = getDbContext().select().from(DEVICE_JOB)
            .join(TESTRUN).on(DEVICE_JOB.DEVICE_JOB_TESTRUN_ID.eq(TESTRUN.TESTRUN_ID))
            .join(TESTCONFIG).on(TESTCONFIG.TEST_CONFIG_ID.eq(TESTRUN.TESTRUN_TESTCONFIG_ID))
            .join(TESTSCRIPT).on(TESTSCRIPT.TESTSCRIPT_ID.eq(TESTCONFIG.TEST_SCRIPT_ID))
            .join(TESTTARGET).on(TESTTARGET.TESTTARGET_ID.eq(TESTCONFIG.TEST_TARGET_ID))
            .where(DEVICE_JOB.DEVICE_JOB_ID.eq(devicejobId))
            .fetchOne();

      TestPackage tp = new TestPackage();
      tp.setDevicejobId(devicejobId);
      tp.setTestScriptPath(result.getValue(TESTSCRIPT.TESTSCRIPT_FILE_PATH));
      tp.setTestTargetPath(result.getValue(TESTTARGET.TESTTARGET_FILE_PATH));
      tp.setTestScriptFileName(result.getValue(TESTSCRIPT.TESTSCRIPT_FILE_NAME));
      tp.setTestTargetFileName(result.getValue(TESTTARGET.TESTTARGET_FILE_NAME));
      return tp;
   }

   /**
    * Build a ZIP file containing file pointing to specified file paths
    *
    * @param paths
    * @return - path to built zip file
    */
   public String buildZipPackage(String[] paths) {

      String zipFile = mFileStorePath + UUID.randomUUID().toString();

      // create byte buffer
      byte[] buffer = new byte[1024];

      // create object of FileOutputStream
      FileOutputStream fout = null;
      try {
         fout = new FileOutputStream(zipFile);
      } catch (FileNotFoundException e) {
         mLogger.error("Could not create file output stream to" + zipFile);
      }

      // create object of ZipOutputStream from FileOutputStream
      ZipOutputStream zout = new ZipOutputStream(fout);

      for (int i = 0; i < paths.length; i++) {

         mLogger.info("Zipping file " + paths[i]);
         File file = new File(paths[i]);

         // create object of FileInputStream for source file
         FileInputStream fin = null;
         try {
            fin = new FileInputStream(paths[i]);
         } catch (FileNotFoundException e) {
            mLogger.error("Could not create file output stream to" + paths[i], e);
         }

         try {
            zout.putNextEntry(new ZipEntry(file.getName()));
         } catch (IOException e) {
            mLogger.error("Could not create new ZIP entry for file:" + paths[i], e);
         }

         int length;
         try {
            while ((length = fin.read(buffer)) > 0) {
               zout.write(buffer, 0, length);
            }
         } catch (IOException e) {
            mLogger.error("Error while copying file:" + paths[i], e);
         }

         try {
            zout.closeEntry();
         } catch (IOException e) {
            mLogger.error("Could not close ZIP entry for file" + paths[i], e);
         }

         // close the InputStream
         try {
            fin.close();
         } catch (IOException e) {
            mLogger.error("Could not close file input stream", e);
         }
      }

      try {
         zout.close();
      } catch (IOException e) {
         mLogger.error("Could not close zip file stream", e);
      }
      return zipFile;
   }
}
