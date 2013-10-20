package com.cbt.ws.dao;

import static com.cbt.jooq.tables.DeviceJob.DEVICE_JOB;
import static com.cbt.jooq.tables.DeviceJobResult.DEVICE_JOB_RESULT;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.RecordMapper;

import com.cbt.core.entity.DeviceJobResult;
import com.cbt.core.exceptions.CbtDaoException;
import com.cbt.jooq.enums.DeviceJobResultState;
import com.cbt.ws.JooqDao;
import com.google.inject.Inject;

/**
 * Device job result DAO
 *
 * @author SauliusAlisauskas 2013-04-07 Initial version
 */
public class DevicejobResultDao extends JooqDao {

   private final Logger mLogger = Logger.getLogger(DevicejobResultDao.class);

   /**
    * Map {@link Record} into {@link DeviceJobResult}
    */
   private final RecordMapper<Record, DeviceJobResult> resultMapper = new RecordMapper<Record, DeviceJobResult>() {

      @Override
      public DeviceJobResult map(Record record) {
         return record.into(DeviceJobResult.class);
      }
   };

   @Inject
   public DevicejobResultDao(DataSource dataSource) {
      super(dataSource);
   }

   /**
    * Add device job result
    *
    * @param deviceJobResult
    * @return
    */
   public Long add(DeviceJobResult deviceJobResult) {
      mLogger.trace("Adding new device job result");
      Long deviceJobResultId = getDbContext()
            .insertInto(DEVICE_JOB_RESULT, DEVICE_JOB_RESULT.DEVICEJOB_ID, DEVICE_JOB_RESULT.OUTPUT,
                  DEVICE_JOB_RESULT.STATE, DEVICE_JOB_RESULT.TESTS_RUN, DEVICE_JOB_RESULT.TESTS_FAILED,
                  DEVICE_JOB_RESULT.TESTS_ERRORS)
            .values(deviceJobResult.getDevicejobId(), deviceJobResult.getOutput(),
                  DeviceJobResultState.valueOf(deviceJobResult.getState().toString()), deviceJobResult.getTestsRun(),
                  deviceJobResult.getTestsFailed(), deviceJobResult.getTestsErrors())
            .returning(DEVICE_JOB_RESULT.DEVICEJOBRESULT_ID).fetchOne().getDevicejobresultId();
      mLogger.trace("Added device job result, new id:" + deviceJobResultId);
      return deviceJobResultId;
   }

   /**
    * Delete device job result record
    *
    * @param deviceJobId
    * @throws CbtDaoException
    */
   public void delete(long deviceJobId) throws CbtDaoException {
      mLogger.trace("Updating deviceJob");
      int count = getDbContext().delete(DEVICE_JOB_RESULT).where(DEVICE_JOB_RESULT.DEVICEJOB_ID.eq(deviceJobId))
            .execute();
      if (count != 1) {
         String message = "Could not delete results of devicejob id: " + deviceJobId;
         mLogger.error(message);
         throw new CbtDaoException(message);
      }
      mLogger.trace("Deleted results of devicejob id: " + deviceJobId);
   }

   /**
    * Get device job result by device job id
    *
    * @param deviceJobId
    * @return
    */
   public DeviceJobResult getByDeviceJobId(Long deviceJobId) {
      Record record = getDbContext().select().from(DEVICE_JOB_RESULT)
            .where(DEVICE_JOB_RESULT.DEVICEJOB_ID.eq(deviceJobId)).fetchOne();
      DeviceJobResult deviceJobRersult = record.into(DeviceJobResult.class);
      return deviceJobRersult;
   }

   // TODO: all deletes could be abstracted, error messages generated!

   /**
    * Get all device job result records
    *
    * @return
    */
   public List<DeviceJobResult> getByTestRunId(Long testRunId) {
      return getDbContext().select().from(DEVICE_JOB_RESULT).join(DEVICE_JOB)
            .on(DEVICE_JOB_RESULT.DEVICEJOB_ID.eq(DEVICE_JOB.DEVICE_JOB_ID))
            .where(DEVICE_JOB.DEVICE_JOB_TESTRUN_ID.eq(testRunId)).fetch(resultMapper);
   }

}
