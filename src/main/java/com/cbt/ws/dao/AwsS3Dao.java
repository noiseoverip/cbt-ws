package com.cbt.ws.dao;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.cbt.ws.Configuration;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class AwsS3Dao
 *
 * @author iljabobkevic 2013-10-07 initial version
 */
@Singleton
public class AwsS3Dao {

   private static final String AWS_PROPETIES_NAME = "aws.properties";
   private static final String DEFAULT_DOWNLOAD_FILE_EXT = "jar";
   private final TransferManager awsTx;
   private final Logger logger = Logger.getLogger(AwsS3Dao.class);

   @Inject
   public AwsS3Dao(Configuration configuration) {
      InputStream is;
      if (null != configuration.getAwsPropertiesPath() && !configuration.getAwsPropertiesPath().isEmpty()) {
         try {
            is = new FileInputStream(configuration.getAwsPropertiesPath());
         } catch (FileNotFoundException e) {
            logger.warn("Failed reading file: " + configuration.getAwsPropertiesPath());
            logger.debug(String.format("Trying to load %s from the class path", DEFAULT_DOWNLOAD_FILE_EXT));
            is = TestScriptDao.class.getResourceAsStream("/" + AWS_PROPETIES_NAME);
         }
      } else {
         is = TestScriptDao.class.getResourceAsStream("/" + AWS_PROPETIES_NAME);
      }

      PropertiesCredentials  credentials  = null;
      try {
         credentials = new PropertiesCredentials(is);
      } catch (IOException e) {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      awsTx = new TransferManager(credentials);
   }

   public void uploadS3(String bucketName, String objectName, InputStream is) throws IOException,
         InterruptedException {
      if (!awsTx.getAmazonS3Client().doesBucketExist(bucketName)) {
         awsTx.getAmazonS3Client().createBucket(bucketName);
      }

      byte[] contentBytes = IOUtils.toByteArray(is);
      Long contentLength = Long.valueOf(contentBytes.length);

      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(contentLength);

      PutObjectRequest request = new PutObjectRequest(bucketName, objectName, is, metadata);
      Upload upload = awsTx.upload(request);
      upload.waitForCompletion();
   }

   public void uploadS3(String bucketName, File file) throws IOException, InterruptedException {
      if (!awsTx.getAmazonS3Client().doesBucketExist(bucketName)) {
         awsTx.getAmazonS3Client().createBucket(bucketName);
      }

      PutObjectRequest request = new PutObjectRequest(bucketName, Files.getNameWithoutExtension(file.getName()), file);
      Upload upload = awsTx.upload(request);
      upload.waitForCompletion();
   }

   public File download(String bucketName, String objectName, String fileExtension) throws InterruptedException {
      GetObjectRequest request = new GetObjectRequest(bucketName, objectName);
      File testScriptFile = FileUtils.getFile(Files.createTempDir(), objectName + "." + fileExtension);
      Download download = awsTx.download(request, testScriptFile);
      download.waitForCompletion();
      return testScriptFile;
   }

   public File download(String bucketName, String objectName) throws InterruptedException {
      return download(bucketName, objectName, DEFAULT_DOWNLOAD_FILE_EXT);
   }
}
