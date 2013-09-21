package com.cbt.ws.dao;

import static com.cbt.ws.jooq.tables.DeviceJob.DEVICE_JOB;
import static com.cbt.ws.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT;
import static com.cbt.ws.jooq.tables.Testconfig.TESTCONFIG;
import static com.cbt.ws.jooq.tables.Testrun.TESTRUN;
import static com.cbt.ws.jooq.tables.Testscript.TESTSCRIPT;
import static com.cbt.ws.jooq.tables.Testtarget.TESTTARGET;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;

import com.cbt.core.exceptions.CbtDaoException;
import com.cbt.ws.JooqDao;
import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.entity.DeviceJobMetadata;
import com.cbt.ws.entity.TestScript;
import com.cbt.ws.entity.TestTarget;
import com.cbt.ws.jooq.enums.DeviceJobDeviceJobStatus;
import com.cbt.ws.jooq.tables.records.DeviceJobRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

/**
 * Device job DAO
 * 
 * @author SauliusALisauskas 2013-03-03 Initial version
 * 
 */
public class DevicejobDao extends JooqDao {
	
	private TestScriptDao mTestScriptDao;
	
	@Inject
	public DevicejobDao(DataSource dataSource, TestScriptDao testScriptDao) {
		super(dataSource);
		mTestScriptDao = testScriptDao;
	}

	private final Logger mLogger = Logger.getLogger(DevicejobDao.class);
	// TODO: use one static instance for all project
	private final ObjectMapper mMapper = new ObjectMapper();

	/**
	 * Add new test configuration
	 * 
	 * @param userid
	 * @return
	 */
	public Long add(DeviceJob deviceJob) {
		mLogger.trace("Adding new device job");
		String metadata = null;
		try {
			metadata = mMapper.writeValueAsString(deviceJob.getMetadata());
		} catch (JsonProcessingException e) {
			mLogger.error("Could not convert to JSON:" + deviceJob.getMetadata());
		}
		Long deviceJobId = getDbContext()
				.insertInto(DEVICE_JOB, DEVICE_JOB.DEVICE_JOB_DEVICE_ID, DEVICE_JOB.DEVICE_JOB_TESTRUN_ID, DEVICE_JOB.DEVICE_JOB_CREATED,
						DEVICE_JOB.DEVICE_JOB_META)
				.values(deviceJob.getDeviceId(), deviceJob.getTestRunId(),
						new Timestamp(Calendar.getInstance().getTimeInMillis()), metadata).returning(DEVICE_JOB.DEVICE_JOB_ID)
				.fetchOne().getDeviceJobId();
		mLogger.trace("Added device job, new id:" + deviceJobId);
		return deviceJobId;
	}	
	
	/**
	 * Delete deviceJob
	 * 
	 * @param deviceJob
	 * @throws CbtDaoException
	 */
	public void delete(Long deviceJobId) throws CbtDaoException {
		mLogger.trace("Updating deviceJob");
		int count = getDbContext().delete(DEVICE_JOB).where(DEVICE_JOB.DEVICE_JOB_ID.eq(deviceJobId)).execute();

		if (count != 1) {
			String message = "Could delete deviceJobI:" + deviceJobId;
			mLogger.error(message);
			throw new CbtDaoException(message);
		}
		mLogger.trace("Deleted job, result: " + count);
	}
	

	/**
	 * Get device jobs
	 * 
	 * @return
	 */
	public DeviceJob[] getAll() {
		List<DeviceJob> testExecutions = new ArrayList<DeviceJob>();
		Result<Record> result = getDbContext().select().from(DEVICE_JOB).fetch();
		for (Record r : result) {
			DeviceJob tc = r.into(DeviceJob.class);
			testExecutions.add(tc);
			mLogger.debug(tc);
		}
		return testExecutions.toArray(new DeviceJob[testExecutions.size()]);
	}

	/**
	 * Get devicesjobs of specified test run
	 * 
	 * @param testRunId
	 * @return
	 */
	public DeviceJob[] getByTestRunId(Long testRunId) {
		List<DeviceJob> testExecutions = new ArrayList<DeviceJob>();
		Result<Record> result = getDbContext().select().from(DEVICE_JOB).where(DEVICE_JOB.DEVICE_JOB_TESTRUN_ID.eq(testRunId))
				.fetch();
		for (Record r : result) {
			DeviceJob tc = r.into(DeviceJob.class);
			testExecutions.add(tc);
			mLogger.debug(tc);
		}
		return testExecutions.toArray(new DeviceJob[testExecutions.size()]);
	}

	/**
	 * Get list of device jobs ordered by date, returns only those that are not FINISHED
	 * 
	 * @param deviceId
	 * @return
	 */
	public DeviceJob[] getByDeviceId(Long deviceId) {
		List<DeviceJob> jobs = getDbContext().select().from(DEVICE_JOB)
				.join(TESTRUN).on(DEVICE_JOB.DEVICE_JOB_TESTRUN_ID.eq(TESTRUN.TESTRUN_ID))
				.join(TESTCONFIG).on(TESTRUN.TESTRUN_TESTCONFIG_ID.eq(TESTCONFIG.TEST_CONFIG_ID))
				.join(TESTSCRIPT).on(TESTCONFIG.TEST_SCRIPT_ID.eq(TESTSCRIPT.TESTSCRIPT_ID))
				.join(TESTTARGET).on(TESTCONFIG.TEST_TARGET_ID.eq(TESTTARGET.TESTTARGET_ID))
				.where(DEVICE_JOB.DEVICE_JOB_DEVICE_ID.eq(deviceId).and(DEVICE_JOB.DEVICE_JOB_STATUS.notEqual(DeviceJobDeviceJobStatus.FINISHED)))				
				.orderBy(DEVICE_JOB.DEVICE_JOB_CREATED.asc()).fetch(deviceJobMapper);
		return jobs.toArray(new DeviceJob[jobs.size()]);
	}
	
	/**
	 * Mapper used for constructing lists of {@link DeviceJob} objects from Jooq Record
	 */
	private final RecordMapper<Record, DeviceJob> deviceJobMapper = new RecordMapper<Record, DeviceJob>() {

		@Override
		public DeviceJob map(Record record) {
			DeviceJob deviceJob = record.into(DeviceJob.class);
			DeviceJobMetadata medatada = null;
			if (record.getValue(DEVICE_JOB.DEVICE_JOB_META) != null) {
				try {
					medatada = mMapper.readValue(record.getValue(DEVICE_JOB.DEVICE_JOB_META), DeviceJobMetadata.class);
				} catch (Exception e) {
					mLogger.error("Could not map " + record.getValue(DEVICE_JOB.DEVICE_JOB_META) + " to "
							+ DeviceJobMetadata.class.getSimpleName());
				}
			}
			deviceJob.setMetadata(medatada);
			if (record.getValue(TESTSCRIPT.TESTSCRIPT_ID) != null) {
				TestScript testScript = record.into(TestScript.class);
				testScript.setTestClasses(mTestScriptDao.parseTestClasses(record.getValue(TESTSCRIPT.TESTSCRIPT_CLASSES)));
				deviceJob.setTestScript(testScript);
			}
			if (record.getValue(TESTTARGET.TESTTARGET_ID) != null) {
				deviceJob.setTestTarget(record.into(TestTarget.class));
			}
			return deviceJob;
		}
	};

	/**
	 * Update deviceJob, we should only need to update: state
	 * 
	 * @param deviceJob
	 * @throws CbtDaoException
	 */
	public void update(DeviceJob deviceJob) throws CbtDaoException {
		mLogger.trace("Updating deviceJob");
		int count = getDbContext().update(DEVICE_JOB)
				.set(DEVICE_JOB.DEVICE_JOB_STATUS, DeviceJobDeviceJobStatus.valueOf(deviceJob.getStatus().toString()))
				.where(DEVICE_JOB.DEVICE_JOB_ID.eq(deviceJob.getId())).execute();

		if (count != 1) {
			mLogger.error("Could not update deviceJob:" + deviceJob);
			throw new CbtDaoException("Could not update deviceJob");
		}
		mLogger.trace("Updated device job, result: " + count);
	}

	/**
	 * Get device job by id
	 * 
	 * @param id
	 * @return
	 */
	public DeviceJob getById(Long id) {
		DeviceJobRecord record = (DeviceJobRecord) getDbContext().select().from(DEVICE_JOB).where(DEVICE_JOB.DEVICE_JOB_ID.eq(id))
				.fetchOne();
		return record.into(DeviceJob.class);
	}

	/**
	 * Get device job by id, attach device job result
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getByIdWithResult(Long id) {
		DSLContext context = getDbContext();
		Map<String, Object> deviceJob = context.select().from(DEVICE_JOB).where(DEVICE_JOB.DEVICE_JOB_ID.eq(id)).fetchOne()
				.intoMap();
		Map<String, Object> result = context.select().from(DEVICE_JOB_RESULT)
				.where(DEVICE_JOB_RESULT.DEVICEJOB_ID.eq(id)).fetchOne().intoMap();
		deviceJob.put("result", result);
		return deviceJob;
	}
}
