package com.cbt.ws.dao;

import static com.cbt.jooq.tables.Testscript.TESTSCRIPT;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.tools.json.JSONArray;

import com.cbt.core.entity.TestScript;
import com.cbt.core.utils.Utils;
import com.cbt.jooq.tables.records.TestscriptRecord;
import com.cbt.ws.JooqDao;
import com.cbt.ws.utils.JarScanner;
import com.cbt.ws.utils.JarScannerException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

/**
 * Test package DAO
 * 
 * @author SauliusALisauskas 2013-03-03 Initial version
 */
public class TestScriptDao extends JooqDao {

	private static final String S3_BUCKET_NAME = "ts.cbt";
	private static final String FILE_EXTENSION = "jar";
	private final Logger mLogger = Logger.getLogger(TestScriptDao.class);
	private final AwsS3Dao s3Dao;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Inject
	public TestScriptDao(DataSource datasource, AwsS3Dao s3Dao) throws IOException {
		super(datasource);
		this.s3Dao = s3Dao;
	}

	/**
	 * Create new TestPackage record mainly to generate new id
	 * 
	 * @param userid
	 * @return
	 */
	private Long createNewTestScriptRecord(Long userid) {
		TestscriptRecord result = getDbContext().insertInto(TESTSCRIPT, TESTSCRIPT.TESTSCRIPT_USER_ID).values(userid)
				.returning(TESTSCRIPT.TESTSCRIPT_ID).fetchOne();
		return result.getTestscriptId();
	}

	/**
	 * Get all test packages
	 * 
	 * @return
	 */
	public TestScript[] getAll() {
		List<TestScript> packages = new ArrayList<TestScript>();
		Result<Record> result = getDbContext().select().from(TESTSCRIPT).fetch();
		for (Record r : result) {
			TestScript ts = r.into(TestScript.class);
			packages.add(ts);
		}
		return packages.toArray(new TestScript[packages.size()]);
	}

	/**
	 * Get by id
	 */
	public TestScript getById(Long testScriptId) {
		Record result = getDbContext().select().from(TESTSCRIPT).where(TESTSCRIPT.TESTSCRIPT_ID.eq(testScriptId))
				.fetchOne();
		TestScript testScript = result.into(TestScript.class);
		// Need to parse test classes separately since those are in JSON format
		String classesJson = result.getValue(TESTSCRIPT.TESTSCRIPT_CLASSES);
		testScript.setTestClasses(parseTestClasses(classesJson));
		return testScript;
	}

	/**
	 * Get list of test script by user id
	 * 
	 * @param userId
	 * @return
	 */
	public List<TestScript> getByUserId(Long userId) {
		return getDbContext().select().from(TESTSCRIPT).where(TESTSCRIPT.TESTSCRIPT_USER_ID.eq(userId))
				.fetch(testScriptRecordMapper);
	}

	/**
	 * Map result into {@link TestScript} class. Created since we have json string stored in database that needs to be
	 * parsed out
	 */
	private RecordMapper<Record, TestScript> testScriptRecordMapper = new RecordMapper<Record, TestScript>() {

		@Override
		public TestScript map(Record record) {
			TestScript testScript = record.into(TestScript.class);
			String classesJson = record.getValue(TESTSCRIPT.TESTSCRIPT_CLASSES);
			if (null != classesJson) {
				testScript.setTestClasses(parseTestClasses(classesJson));
			}
			return testScript;
		}

	};

	public String[] parseTestClasses(String classesJson) {
		String[] classes = null;
		if (null != classesJson) {
			try {
				classes = objectMapper.readValue(classesJson, String[].class);
			} catch (JsonParseException e) {
				mLogger.error(e);
			} catch (JsonMappingException e) {
				mLogger.error(e);
			} catch (IOException e) {
				mLogger.error(e);
			}
		}
		return classes;
	}

	/**
	 * Save test package
	 * 
	 * @param testScript
	 * @param uploadedInputStream
	 * @throws IOException
	 */
	public void storeTestScript(TestScript testScript, InputStream uploadedInputStream) throws IOException {
		// Create new test package record in db -> get it's id
		long newTestPackageId = createNewTestScriptRecord(testScript.getUserId());
		mLogger.debug("Generated new id for test package:" + newTestPackageId);

		// Create appropriate folder structure to store the file
		testScript.setId(newTestPackageId);
		File testScriptFile = FileUtils.getFile(Files.createTempDir(), newTestPackageId + "." + FILE_EXTENSION);

		// Store the file
		Utils.writeToFile(uploadedInputStream, testScriptFile);

		// Upload to S3
		try {
			s3Dao.uploadS3(S3_BUCKET_NAME, testScriptFile);
		} catch (InterruptedException e) {
			e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
		}

		// Parse test class names
		try {
			JarScanner jarScanner = new JarScanner(testScriptFile);
			testScript.setTestClasses(jarScanner.getTestClasseNames());
			testScript.setTestScriptType(jarScanner.getTestScriptType());
		} catch (JarScannerException e) {
			mLogger.error("Could not parse test class names from " + testScript.getFilePath());
		}

		testScriptFile.delete();

		// Update test package path and other info
		testScript.setFilePath(testScriptFile.getAbsolutePath());
		testScript.setFileName(testScriptFile.getName());
		updateTestScript(testScript);
	}

	public File getTestScript(long packageId) {
		File testScriptFile = null;
		try {
			testScriptFile = s3Dao.download(S3_BUCKET_NAME, String.valueOf(packageId), FILE_EXTENSION);
		} catch (InterruptedException e) {
			e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
		}
		return testScriptFile;
	}

	/**
	 * Update test package information
	 * 
	 * @param testScript
	 */
	private void updateTestScript(TestScript testScript) {
		if (getDbContext().update(TESTSCRIPT).set(TESTSCRIPT.TESTSCRIPT_FILE_PATH, testScript.getFilePath())
				.set(TESTSCRIPT.TESTSCRIPT_FILE_NAME, testScript.getFileName())
				.set(TESTSCRIPT.TESTSCRIPT_NAME, testScript.getName())
				.set(TESTSCRIPT.TESTSCRIPT_CLASSES, JSONArray.toJSONString(Arrays.asList(testScript.getTestClasses())))
				.set(TESTSCRIPT.TESTSCRIPT_TYPE, testScript.getTestScriptType())
				.where(TESTSCRIPT.TESTSCRIPT_ID.eq(testScript.getId())).execute() != 1) {
			mLogger.error("Failed to update package:" + testScript);
		} else {
			mLogger.debug("Test package updated:" + testScript);
		}
	}
}
