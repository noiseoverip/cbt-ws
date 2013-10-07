package com.cbt.ws.dao;

import com.cbt.core.entity.TestTarget;
import com.cbt.core.utils.Utils;
import com.cbt.jooq.tables.records.TesttargetRecord;
import com.cbt.ws.Configuration;
import com.cbt.ws.JooqDao;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.cbt.jooq.tables.Testtarget.TESTTARGET;

/**
 * Test target(application) DAO
 *
 * @author SauliusALisauskas 2013-03-03 Initial version
 */
public class TestTargetDao extends JooqDao {

   private static final String S3_BUCKET_NAME = "tt.cbt";
   private static final String FILE_EXTENSION = "apk";
   private final Logger mLogger = Logger.getLogger(TestTargetDao.class);
   private final AwsS3Dao s3Dao;
   private Configuration mConfiguration;

   @Inject
   public TestTargetDao(Configuration configuration, DataSource datasource, AwsS3Dao s3Dao) {
      super(datasource);
      mConfiguration = configuration;
      this.s3Dao = s3Dao;
   }

   /**
    * Create new dummy test target in database
    *
    * @param userid
    * @return
    */
   private Long createNewTestPackageRecord(Long userid) {
      TesttargetRecord result = getDbContext().insertInto(TESTTARGET, TESTTARGET.TESTTARGET_USER_ID).values(userid)
            .returning(TESTTARGET.TESTTARGET_ID).fetchOne();
      return result.getTesttargetId();
   }

   /**
    * Get all test targets
    *
    * @return
    */
   public TestTarget[] getAll() {
      List<TestTarget> applications = new ArrayList<TestTarget>();
      Result<Record> result = getDbContext().select().from(TESTTARGET).fetch();
      for (Record r : result) {
         TestTarget tp = r.into(TestTarget.class);
         applications.add(tp);
      }
      return applications.toArray(new TestTarget[applications.size()]);
   }

   /**
    * Save test target
    *
    * @param testTarget
    * @param uploadedInputStream
    * @throws IOException
    */
   public void storeTestTarget(TestTarget testTarget, InputStream uploadedInputStream) throws IOException {
      // Create new test package record in db -> get it's id
      Long newTestPackageId = createNewTestPackageRecord(testTarget.getUserId());
      mLogger.debug("Generated new id for test package:" + newTestPackageId);

      // Create appropriate folder structure to store the file
      testTarget.setId(newTestPackageId);

      File testTargetFile = FileUtils.getFile(Files.createTempDir(), newTestPackageId + "." + FILE_EXTENSION);

      // Store the file
      Utils.writeToFile(uploadedInputStream, testTargetFile);
      try {
         s3Dao.uploadS3(S3_BUCKET_NAME, testTargetFile);
      } catch (InterruptedException e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }

      // Update path and other info
      testTarget.setFilePath(testTargetFile.getAbsolutePath());
      testTarget.setFileName(testTargetFile.getName());
      updateTestTarget(testTarget);
   }

   public File getTestTarget(long packageId) {
      File testTargetFile = null;
      try {
         testTargetFile = s3Dao.download(S3_BUCKET_NAME, String.valueOf(packageId), FILE_EXTENSION);
      } catch (InterruptedException e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      return testTargetFile;
   }

   /**
    * Update test target information
    *
    * @param testTarget
    */
   private void updateTestTarget(TestTarget testTarget) {
      if (getDbContext().update(TESTTARGET).set(TESTTARGET.TESTTARGET_FILE_PATH, testTarget.getFilePath())
            .set(TESTTARGET.TESTTARGET_FILE_NAME, testTarget.getFileName()).set(TESTTARGET.TESTTARGET_NAME, testTarget.getName())
            .where(TESTTARGET.TESTTARGET_ID.eq(testTarget.getId())).execute() != 1) {
         mLogger.error("Failed to update package:" + testTarget);
      } else {
         mLogger.debug("Test package updated:" + testTarget);
      }
   }
}
