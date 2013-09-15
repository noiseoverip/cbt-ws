package com.cbt.ws.dao;

import static com.cbt.ws.jooq.tables.Testscript.TESTSCRIPT;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.tools.json.JSONArray;

import com.cbt.core.utils.Utils;
import com.cbt.ws.Configuration;
import com.cbt.ws.JooqDao;
import com.cbt.ws.entity.TestScript;
import com.cbt.ws.jooq.tables.records.TestscriptRecord;
import com.cbt.ws.utils.JarScanner;
import com.cbt.ws.utils.JarScannerException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test package DAO
 * 
 * @author SauliusALisauskas 2013-03-03 Initial version
 * 
 */
public class TestScriptDao extends JooqDao {

	private final Logger mLogger = Logger.getLogger(TestScriptDao.class);
	private ObjectMapper objectMapper = new ObjectMapper();
	private Configuration mConfiguration;

	@Inject
	public TestScriptDao(Configuration configuration, DataSource datasource) {
		super(datasource);
		mConfiguration = configuration;
	}

	/**
	 * Create new TestPackage record mainly to generate new id
	 * 
	 * @param userid
	 * @return
	 */
	private Long createNewTestScriptRecord(Long userid) {
		TestscriptRecord result = getDbContext().insertInto(TESTSCRIPT, TESTSCRIPT.USER_ID).values(userid)
				.returning(TESTSCRIPT.ID).fetchOne();
		return result.getId();
	}

	/**
	 * Create appropriate folder structure for holding test package. e.g. /userid/testpackageid/
	 * 
	 * @param packagId
	 * @param userId
	 * @return
	 */
	private String createTestPackageFolder(Long packagId, Long userId) {
		// create user folder if not existing
		String path = mConfiguration.getWorkspace() + userId + "//ts-" + packagId;
		if (new File(path).mkdirs()) {
			mLogger.info("New folder created:" + path);
			return path;
		} else {
			mLogger.error("Could not create new folder:" + path);
		}
		return null;
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
		Record result = getDbContext().select().from(TESTSCRIPT).where(TESTSCRIPT.ID.eq(testScriptId)).fetchOne();
		TestScript testScript = result.into(TestScript.class);
		// Need to parse test classes separately since those are in JSON format

		String classesJson = result.getValue(TESTSCRIPT.CLASSES);
		if (null != classesJson) {
			try {
				testScript.setTestClasses(objectMapper.readValue(classesJson, String[].class));
			} catch (JsonParseException e) {
				mLogger.error(e);
			} catch (JsonMappingException e) {
				mLogger.error(e);
			} catch (IOException e) {
				mLogger.error(e);
			}
		}
		return testScript;
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
		Long newTestPackageId = createNewTestScriptRecord(testScript.getUserId());
		mLogger.debug("Generated new id for test package:" + newTestPackageId);

		// Create appropriate folder structure to store the file
		testScript.setId(newTestPackageId);
		String testPackagePath = createTestPackageFolder(newTestPackageId, testScript.getUserId());

		// Store the file
		// TODO: manage file names better
		String fileName = "uiautomator-" + testScript.getId() + ".jar";
		String filePath = testPackagePath + "//" + fileName;
		Utils.writeToFile(uploadedInputStream, filePath);

		// Parse test class names
		try {
			testScript.setTestClasses(new JarScanner(filePath).getTestClasseNames());
		} catch (JarScannerException e) {
			mLogger.error("Could not parse test class names from " + testScript.getPath());
		}

		// Update test package path and other info
		testScript.setPath(filePath);
		testScript.setFileName(fileName);
		updateTestScript(testScript);
	}

	/**
	 * Update test package information
	 * 
	 * @param testScript
	 */
	private void updateTestScript(TestScript testScript) {
		if (getDbContext().update(TESTSCRIPT).set(TESTSCRIPT.PATH, testScript.getPath())
				.set(TESTSCRIPT.FILE_NAME, testScript.getFileName()).set(TESTSCRIPT.NAME, testScript.getName())
				.set(TESTSCRIPT.CLASSES, JSONArray.toJSONString(Arrays.asList(testScript.getTestClasses())))
				.where(TESTSCRIPT.ID.eq(testScript.getId())).execute() != 1) {
			mLogger.error("Failed to update package:" + testScript);
		} else {
			mLogger.debug("Test package updated:" + testScript);
		}
	}
}
