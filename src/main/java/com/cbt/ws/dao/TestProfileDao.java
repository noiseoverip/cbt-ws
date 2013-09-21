package com.cbt.ws.dao;

import static com.cbt.ws.jooq.tables.DeviceType.DEVICE_TYPE;
import static com.cbt.ws.jooq.tables.Testprofile.TESTPROFILE;
import static com.cbt.ws.jooq.tables.TestprofileDevices.TESTPROFILE_DEVICES;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;

import com.cbt.ws.JooqDao;
import com.cbt.ws.entity.DeviceType;
import com.cbt.ws.entity.TestProfile;
import com.cbt.ws.jooq.enums.TestprofileTestprofileMode;

/**
 * Test profile DAO
 * 
 * @author SauliusALisauskas 2013-03-03 Initial version
 * 
 */
public class TestProfileDao extends JooqDao {
	
	@Inject
	public TestProfileDao(DataSource dataSource) {
		super(dataSource);
	}

	private final Logger mLogger = Logger.getLogger(TestProfileDao.class);

	/**
	 * Add new test profile
	 * 
	 * @param testProfile
	 * @return
	 */
	public TestProfile add(TestProfile testProfile) {
		mLogger.trace("Adding new test configuration");
		
		DSLContext context = getDbContext();
		
		Long id = context
				.insertInto(TESTPROFILE, TESTPROFILE.TESTPROFILE_USER_ID, TESTPROFILE.TESTPROFILE_MODE, TESTPROFILE.TESTPROFILE_NAME)
				.values(testProfile.getUserId(), TestprofileTestprofileMode.valueOf(testProfile.getMode().toString()), testProfile.getName())
				.returning(TESTPROFILE.TESTPROFILE_ID).fetchOne().getTestprofileId();
		mLogger.trace("Added test configuration, enw id:" + id);
		mLogger.trace("Adding device list to test configuration:" + id);
		if (null != testProfile.getDeviceTypes() && testProfile.getDeviceTypes().size() > 0) {
			for (Long deviceTypeId : testProfile.getDeviceTypes()) {
				mLogger.trace("Addign device id");
				// TODO: improve performance here
				context.insertInto(TESTPROFILE_DEVICES, TESTPROFILE_DEVICES.TESTPROFILE_ID,
						TESTPROFILE_DEVICES.DEVICETYPE_ID).values(id, deviceTypeId).execute();
			}
		} else {
			mLogger.error("Devices were not provided for testprofile:" + id);
		}
		return getById(id);
	}
	
	/**
	 * Get device types of test profile
	 * 
	 * @param testProfileId
	 * @return
	 */
	public List<DeviceType> getDeviceTypesByTestProfile(Long testProfileId) {
		List<DeviceType> result = getDbContext().select().from(DEVICE_TYPE).join(TESTPROFILE_DEVICES)
				.on(TESTPROFILE_DEVICES.DEVICETYPE_ID.eq(DEVICE_TYPE.ID))
				.where(TESTPROFILE_DEVICES.TESTPROFILE_ID.eq(testProfileId))
				.fetch(new RecordMapper<Record, DeviceType>() {
					@Override
					public DeviceType map(Record record) {
						return record.into(DeviceType.class);
					}
				});
		return result;
	}
	
	/**
	 * Get all test profiles
	 * 
	 * @return
	 */
	public TestProfile[] getAll() {
		List<TestProfile> testProfiles = new ArrayList<TestProfile>();
		Result<Record> result = getDbContext().select().from(TESTPROFILE).orderBy(TESTPROFILE.TESTPROFILE_UPDATED.desc()).fetch();
		for (Record r : result) {
			TestProfile tp = new TestProfile();
			tp.setId(r.getValue(TESTPROFILE.TESTPROFILE_ID));
			tp.setName(r.getValue(TESTPROFILE.TESTPROFILE_NAME));
			tp.setUserId(r.getValue(TESTPROFILE.TESTPROFILE_USER_ID));
			tp.setMode(r.getValue(TESTPROFILE.TESTPROFILE_MODE));
			// Get devices of this test profile
			List<DeviceType> devices = getDeviceTypesByTestProfile(tp.getId());
			if (null != devices) {
				tp.setDeviceTypesList(devices);
			}
			testProfiles.add(tp);
			
			mLogger.debug(tp);
		}
		return testProfiles.toArray(new TestProfile[testProfiles.size()]);
	}	
	
	/**
	 * Get by user id
	 * 
	 * @param userId
	 * @return
	 */
	public TestProfile[] getByUserId(Long userId) {
		List<TestProfile> result = getDbContext().select().from(TESTPROFILE).where(TESTPROFILE.TESTPROFILE_USER_ID.eq(userId))
				.orderBy(TESTPROFILE.TESTPROFILE_UPDATED.desc()).fetch(new RecordMapper<Record, TestProfile>() {
					@Override
					public TestProfile map(Record record) {
						TestProfile tp = record.into(TestProfile.class);
						List<DeviceType> devices = getDeviceTypesByTestProfile(tp.getId());
						if (null != devices) {
							tp.setDeviceTypesList(devices);
						}
						return tp;
					}
				});
		return result.toArray(new TestProfile[result.size()]);
	}
	
	/**
	 * Get by id
	 * 
	 * @param testProfileId
	 * @return
	 */
	public TestProfile getById(Long testProfileId) {
		Record result = getDbContext().select().from(TESTPROFILE).where(TESTPROFILE.TESTPROFILE_ID.eq(testProfileId))
				.fetchOne();		
		return result.into(TestProfile.class);
	}
}
