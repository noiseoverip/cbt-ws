package com.cbt.ws.dao;

import static com.cbt.ws.jooq.tables.Device.DEVICE;
import static com.cbt.ws.jooq.tables.DeviceSharing.DEVICE_SHARING;
import static com.cbt.ws.jooq.tables.DeviceType.DEVICE_TYPE;
import static com.cbt.ws.jooq.tables.User.USER;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.cbt.ws.JooqDao;
import com.cbt.ws.entity.Device;
import com.cbt.ws.entity.DeviceType;
import com.cbt.ws.exceptions.CbtDaoException;
import com.cbt.ws.jooq.enums.DeviceState;
import com.cbt.ws.jooq.tables.records.DeviceRecord;
import com.cbt.ws.jooq.tables.records.DeviceTypeRecord;
import com.google.inject.Inject;

/**
 * DAO for device related data operations
 * 
 * @author SauliusAlisauskas 2013-03-05 Initial version
 * 
 */
public class DeviceDao extends JooqDao {
	
	@Inject
	public DeviceDao(DataSource dataSource) {
		super(dataSource);		
	}

	private final Logger mLogger = Logger.getLogger(DeviceDao.class);

	/**
	 * Add new device
	 * 
	 * @param userid
	 * @return
	 */
	public Long add(Device device) {
		Long newDeviceId = getDbContext()
				.insertInto(DEVICE, DEVICE.OWNER_ID, DEVICE.SERIAL_NUMBER, DEVICE.DEVICE_UNIQUE_ID, DEVICE.DEVICE_TYPE_ID,
						DEVICE.DEVICE_OS_ID)
				.values(device.getOwnerId(), device.getSerialNumber(), device.getDeviceUniqueId(),
						device.getDeviceTypeId(), device.getDeviceOsId()).returning(DEVICE.ID).fetchOne().getId();
		return newDeviceId;
	}

	/**
	 * Delete specified device
	 * 
	 * @param device
	 * @throws CbtDaoException
	 */
	public void deleteDevice(Long deviceId) throws CbtDaoException {		
		int result = getDbContext().delete(DEVICE).where(DEVICE.ID.eq(deviceId)).execute();
		if (result != 1) {
			throw new CbtDaoException("Error while deleting device, result:" + result);
		}
	}

	/**
	 * Get one device record
	 * 
	 * @param deviceId
	 * @return
	 */
	public Device getDevice(Long deviceId) {
		DeviceRecord record = (DeviceRecord) getDbContext().select().from(DEVICE).where(DEVICE.ID.eq(deviceId)).fetchOne();
		return record.into(Device.class);
	}

	/**
	 * Get device by device unique id
	 * 
	 * @param device
	 * @return
	 */
	public Device getDeviceByUid(String uniqueId) {
		DeviceRecord record = (DeviceRecord) getDbContext().select().from(DEVICE).where(DEVICE.DEVICE_UNIQUE_ID.eq(uniqueId))
				.fetchOne();
		return record.into(Device.class);
	}

	/**
	 * Get devices of specified type
	 * 
	 * @param deviceType
	 * @return
	 */
	public List<Device> getDevicesOfType(Long deviceType, DeviceState state) {
		SelectJoinStep<Record> select = getDbContext().select().from(DEVICE);
		SelectConditionStep<Record> condition = select.where(DEVICE.DEVICE_TYPE_ID.eq(deviceType));
		if (null != state) {
			condition = condition.and(DEVICE.STATE.eq(state));
		}
		List<Device> devices = condition.fetch().map(new RecordMapper<Record, Device>() {
			@Override
			public Device map(Record record) {
				return record.into(Device.class);
			}
		});

		return devices;
	}

	public List<Device> getAllActive() {
		List<Device> devices = getDbContext().select().from(DEVICE).where(DEVICE.STATE.eq(DeviceState.ONLINE)).fetch()
				.map(new RecordMapper<Record, Device>() {
					@Override
					public Device map(Record record) {
						return record.into(Device.class);
					}
				});
		return devices;
	}

	/**
	 * Get devices by user id
	 * 
	 * @param userId
	 * @return Devices owned by user and devices shared with user
	 */
	public List<Device> getAllAvailableForUser(Long userId, Long deviceType, DeviceState state) {
		List<Device> ownedDevices = getOwnedByUser(userId, deviceType, state);
		List<Device> sharedDevices=getSharedWithUser(userId, deviceType, state);
		if (null != ownedDevices) {			
			if (null != sharedDevices) {
				ownedDevices.addAll(sharedDevices);
			}			
		} else {
			ownedDevices = sharedDevices;
		}
		return ownedDevices;
	}

	public List<Device> getOwnedByUser(Long userId, Long deviceType, DeviceState state) {
		SelectJoinStep<Record> select = getDbContext().select().from(DEVICE);
		SelectConditionStep<Record> condition = select.where(DEVICE.OWNER_ID.eq(userId));
		if (null != deviceType) {
			condition = condition.and(DEVICE.DEVICE_TYPE_ID.eq(deviceType));
		}
		if (null != state) {
			condition = condition.and(DEVICE.STATE.eq(state));
		}
		List<Device> devices = condition.fetch().map(new RecordMapper<Record, Device>() {
			@Override
			public Device map(Record record) {
				return record.into(Device.class);
			}
		});
		return devices;
	}

	public List<Device> getSharedWithUser(Long userId, Long deviceType, DeviceState state) {
		SelectJoinStep<Record> select = getDbContext().select().from(DEVICE).join(DEVICE_SHARING)
				.on(DEVICE_SHARING.DEVICE_ID.eq(DEVICE.ID));
		SelectConditionStep<Record> condition = select.where(DEVICE_SHARING.USER_ID.eq(userId));
		if (null != deviceType) {
			condition = condition.and(DEVICE.DEVICE_TYPE_ID.eq(deviceType));
		}
		if (null != state) {
			condition = condition.and(DEVICE.STATE.eq(state));
		}
		List<Device> devices = condition.fetch().map(new RecordMapper<Record, Device>() {
			@Override
			public Device map(Record record) {
				return record.into(Device.class);
			}
		});
		return devices;
	}

	/**
	 * Get users which have access to device with specified id
	 * 
	 * @param deviceId
	 * @return
	 */
	public List<Map<String, Object>> getSharedWith(Long deviceId) {
		Result<Record2<Long, String>> result = getDbContext().select(USER.ID, USER.NAME).from(USER).join(DEVICE_SHARING)
				.on(DEVICE_SHARING.USER_ID.eq(USER.ID)).where(DEVICE_SHARING.DEVICE_ID.eq(deviceId)).fetch();
		return result.intoMaps();
	}

	/**
	 * 
	 * @param deviceId
	 * @return
	 */
	public List<Map<String, Object>> getDeviceTypes() {
		Result<Record> result = getDbContext().select().from(DEVICE_TYPE).fetch();
		return result.intoMaps();
	}

	/**
	 * Return existing device type based on provided properties, create if doesn't exist
	 * 
	 * @param manufacture
	 * @param model
	 * @return
	 */
	public DeviceType getOrCreateDeviceType(String manufacture, String model) {
		Record result = getDbContext().select().from(DEVICE_TYPE).where(DEVICE_TYPE.MANUFACTURE.eq(manufacture))
				.and(DEVICE_TYPE.MODEL.eq(model)).fetchOne();
		if (null == result) {
			DeviceTypeRecord record = getDbContext().insertInto(DEVICE_TYPE, DEVICE_TYPE.MANUFACTURE, DEVICE_TYPE.MODEL)
					.values(manufacture, model).returning(DEVICE_TYPE.fields()).fetchOne();
			if (null == record) {
				mLogger.error("Could not create new device type");
			}
			return record.into(DeviceType.class);
		}
		return result.into(DeviceType.class);
	}

	/**
	 * Update device (status, type)
	 * 
	 * @param device
	 * @throws CbtDaoException
	 */
	public void updateDevice(Device device) throws CbtDaoException {
		int count = getDbContext().update(DEVICE).set(DEVICE.DEVICE_TYPE_ID, device.getDeviceTypeId())
				.set(DEVICE.DEVICE_OS_ID, device.getDeviceOsId()).set(DEVICE.STATE, DeviceState.valueOf(device.getState().toString()))
				.set(DEVICE.UPDATED, new Timestamp(Calendar.getInstance().getTimeInMillis()))
				.where(DEVICE.ID.eq(device.getId())).execute();
		if (count != 1) {
			throw new CbtDaoException("Could not update device");
		}
	}

	/**
	 * Add device sharing record
	 * 
	 * @param deviceId
	 * @param userId
	 */
	public void addSharing(Long deviceId, Long userId) {
		getDbContext().insertInto(DEVICE_SHARING, DEVICE_SHARING.DEVICE_ID, DEVICE_SHARING.USER_ID).values(deviceId, userId)
				.execute();
	}
}
