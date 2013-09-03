package com.cbt.ws.dao;

import static com.cbt.ws.jooq.tables.Testtarget.TESTTARGET;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;

import com.cbt.ws.JooqDao;
import com.cbt.ws.annotations.TestFileStorePath;
import com.cbt.ws.entity.TestTarget;
import com.cbt.ws.jooq.tables.records.TesttargetRecord;
import com.cbt.ws.utils.Utils;

/**
 * Test target(aplication) DAO
 * 
 * @author SauliusALisauskas 2013-03-03 Initial version
 * 
 */
public class TestTargetDao extends JooqDao{

	private String mFileStorePath;

	private final Logger mLogger = Logger.getLogger(TestTargetDao.class);

	@Inject
	public TestTargetDao(@TestFileStorePath String testFileStorePath, DataSource datasource) {
		super(datasource);
		mFileStorePath = testFileStorePath;
	}

	/**
	 * Create new dummy test target in database
	 * 
	 * @param userid
	 * @return
	 */
	private Long createNewTestPackageRecord(Long userid) {
		TesttargetRecord result = getDbContext().insertInto(TESTTARGET, TESTTARGET.USER_ID).values(userid)
				.returning(TESTTARGET.ID).fetchOne();
		return result.getId();
	}

	/**
	 * Create appropriate folder structure for holding test target. e.g. /userid/testtargetid/
	 * 
	 * @param targetId
	 * @param userId
	 * @return - path pointing to reated file
	 */
	private String createTestTargetFolder(Long targetId, Long userId) {
		// create user folder if not existing
		String path = mFileStorePath + userId + "//tt-" + targetId;
		if (new File(path).mkdirs()) {
			mLogger.info("New folder created:" + path);
			return path;
		}
		return null;
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
		String testPackagePath = createTestTargetFolder(newTestPackageId, testTarget.getUserId());

		// Store the file
		// TODO: manage file names better
		String fileName = "app-" + testTarget.getId() + ".apk";
		String filePath = testPackagePath + "//" + fileName;
		Utils.writeToFile(uploadedInputStream, filePath);

		// Update path and other info
		testTarget.setPath(filePath);
		testTarget.setFileName(fileName);
		updateTestTarget(testTarget);
	}

	/**
	 * Update test target information
	 * 
	 * @param testTarget
	 */
	private void updateTestTarget(TestTarget testTarget) {
		if (getDbContext().update(TESTTARGET).set(TESTTARGET.PATH, testTarget.getPath())
				.set(TESTTARGET.FILE_NAME, testTarget.getFileName()).set(TESTTARGET.NAME, testTarget.getName())
				.where(TESTTARGET.ID.eq(testTarget.getId())).execute() != 1) {
			mLogger.error("Failed to update package:" + testTarget);
		} else {
			mLogger.debug("Test package updated:" + testTarget);
		}
	}
}
