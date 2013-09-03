package com.cbt.ws.dao;

import static com.cbt.ws.jooq.tables.DeviceJob.DEVICE_JOB;
import static com.cbt.ws.jooq.tables.Testconfig.TESTCONFIG;
import static com.cbt.ws.jooq.tables.Testprofile.TESTPROFILE;
import static com.cbt.ws.jooq.tables.TestprofileDevices.TESTPROFILE_DEVICES;
import static com.cbt.ws.jooq.tables.Testrun.TESTRUN;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;

import com.cbt.ws.JooqDao;
import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.entity.DeviceJobMetadata;
import com.cbt.ws.entity.TestConfig;
import com.cbt.ws.entity.TestProfile;
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.entity.complex.TestRunComplex;
import com.cbt.ws.exceptions.CbtDaoException;
import com.cbt.ws.jooq.enums.TestrunStatus;
import com.cbt.ws.jooq.tables.records.TestrunRecord;

/**
 * Test run DAO
 * 
 * @author Saulius Alisauskas 2013-03-03 Initial version
 * 
 */
public class TestRunDao extends JooqDao {

	@Inject
	public TestRunDao(DataSource dataSource) {
		super(dataSource);
	}

	private final Logger mLogger = Logger.getLogger(TestRunDao.class);

	/**
	 * Add new test run
	 * 
	 * @param userid
	 * @return
	 */
	public Long add(TestRun testRun) {
		mLogger.trace("Starting new test run");
		Long testRunId = getDbContext()
				.insertInto(TESTRUN, TESTRUN.USER_ID, TESTRUN.TEST_CONFIG_ID, TESTRUN.CREATED)
				.values(testRun.getUserId(), testRun.getTestConfigId(),
						new Timestamp(Calendar.getInstance().getTimeInMillis())).returning(TESTRUN.ID).fetchOne()
				.getId();
		mLogger.trace("Added test run, new id:" + testRunId);
		return testRunId;
	}

	/**
	 * Delete testRun
	 * 
	 * @param testRun
	 * @throws CbtDaoException
	 */
	public void delete(TestRun testRun) throws CbtDaoException {
		int result = getDbContext().delete(TESTRUN).where(TESTRUN.TEST_CONFIG_ID.eq(testRun.getId())).execute();
		if (result != 1) {
			throw new CbtDaoException("Error while deleting device, result:" + result);
		}
	}

	/**
	 * Get test runs
	 * 
	 * @return
	 */
	public TestRun[] getAll() {
		List<TestRun> testRuns = new ArrayList<TestRun>();
		Result<Record> result = getDbContext().select().from(TESTRUN).orderBy(TESTRUN.CREATED.desc()).fetch();
		for (Record r : result) {
			TestRun tr = new TestRun();
			tr.setId(r.getValue(TESTRUN.TEST_CONFIG_ID));
			tr.setTestConfigId(r.getValue(TESTRUN.TEST_CONFIG_ID));
			tr.setCreated(r.getValue(TESTRUN.CREATED));
			tr.setUpdated(r.getValue(TESTRUN.UPDATED));
			tr.setStatus(r.getValue(TESTRUN.STATUS));
			tr.setUserId(r.getValue(TESTRUN.USER_ID));
			testRuns.add(tr);
			mLogger.debug(tr);
		}
		return testRuns.toArray(new TestRun[testRuns.size()]);
	}

	/**
	 * Get test runs of specific user
	 * 
	 * @param userId
	 * @return
	 */
	public List<TestRun> getByUserId(Long userId) {
		List<TestRun> result = getDbContext().select().from(TESTRUN).where(TESTRUN.USER_ID.eq(userId))
				.orderBy(TESTRUN.UPDATED.desc()).fetch(new RecordMapper<Record, TestRun>() {
					@Override
					public TestRun map(Record record) {
						TestRun tp = record.into(TestRun.class);
						return tp;
					}
				});
		return result;
	}
	
	/**
	 * Get test runs of specific user
	 * 
	 * @param userId
	 * @return
	 */
	public TestRun[] getByUserIdFull(Long userId) {
		List<TestRun> list = getDbContext().select().from(TESTRUN).join(TESTCONFIG).onKey()
				.where(TESTRUN.USER_ID.eq(userId)).orderBy(TESTRUN.UPDATED.desc())
				.fetch(new RecordMapper<Record, TestRun>() {

					@Override
					public TestRun map(Record record) {
						TestRun testRun = record.into(TestRun.class);
						testRun.setTestConfig(record.into(TestConfig.class));
						return testRun;
					}
				});
		return list.toArray(new TestRun[list.size()]);
	}

	/**
	 * Get TestRun
	 * 
	 * @param testRunId
	 * @return
	 */
	public TestRun getTestRun(Long testRunId) {
		TestrunRecord record = (TestrunRecord) getDbContext().select().from(TESTRUN).where(TESTRUN.ID.eq(testRunId))
				.fetchOne();
		return record.into(TestRun.class);
	}

	/**
	 * Get Test more complex test run information
	 * 
	 * @param testRunId
	 * @return
	 */
	public TestRunComplex getTestRunComplex(Long testRunId) {
		Record result = getDbContext().select().from(TESTRUN).join(TESTCONFIG)
				.on(TESTCONFIG.TEST_CONFIG_ID.eq(TESTRUN.TEST_CONFIG_ID)).join(TESTPROFILE)
				.on(TESTPROFILE.ID.eq(TESTCONFIG.TEST_PROFILE_ID)).where(TESTRUN.ID.eq(testRunId)).fetchOne();

		TestRunComplex testRun = new TestRunComplex();
		testRun.setId(testRunId);
		testRun.setTestProfile(result.into(TestProfile.class));
		testRun.setTestConfig(result.into(TestConfig.class));

		// Construct device type list
		Result<Record> resultDeviceTypes = getDbContext().select().from(TESTPROFILE_DEVICES)
				.where(TESTPROFILE_DEVICES.TESTPROFILE_ID.eq(testRun.getTestProfile().getId())).fetch();
		List<Long> deviceTypes = new ArrayList<Long>(resultDeviceTypes.size());
		for (Record r : resultDeviceTypes) {
			deviceTypes.add(r.getValue(TESTPROFILE_DEVICES.DEVICETYPE_ID));
		}
		testRun.setDeviceTypes(deviceTypes);
		return testRun;
	}

	/**
	 * Update TestRun record
	 * 
	 * @param testRun
	 * @throws CbtDaoException
	 */
	public void update(TestRun testRun) throws CbtDaoException {
		int count = getDbContext().update(TESTRUN)
				.set(TESTRUN.STATUS, TestrunStatus.valueOf(testRun.getStatus().toString()))
				.where(TESTRUN.ID.eq(testRun.getId())).execute();

		if (count != 1) {
			throw new CbtDaoException("Could not update device");
		}
	}
}
