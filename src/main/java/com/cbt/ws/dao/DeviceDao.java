package com.cbt.ws.dao;

import static com.cbt.jooq.tables.Device.DEVICE;
import static com.cbt.jooq.tables.DeviceSharing.DEVICE_SHARING;
import static com.cbt.jooq.tables.DeviceType.DEVICE_TYPE;
import static com.cbt.jooq.tables.TestprofileDevices.TESTPROFILE_DEVICES;
import static com.cbt.jooq.tables.User.USER;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.cbt.core.entity.Device;
import com.cbt.core.entity.DeviceSharing;
import com.cbt.core.entity.DeviceType;
import com.cbt.core.entity.User;
import com.cbt.core.exceptions.CbtDaoException;
import com.cbt.jooq.enums.DeviceDeviceState;
import com.cbt.jooq.tables.records.DeviceRecord;
import com.cbt.jooq.tables.records.DeviceTypeRecord;
import com.cbt.ws.JooqDao;
import com.google.inject.Inject;

/**
 * DAO for device related data operations
 *
 * @author SauliusAlisauskas 2013-03-05 Initial version
 */
public class DeviceDao extends JooqDao {

   @Inject
   public DeviceDao(DataSource dataSource) {
      super(dataSource);
   }

   private final Logger mLogger = Logger.getLogger(DeviceDao.class);

   /**
    * Get device types of test profile
    *
    * @param testProfileId
    * @return
    */
   public List<DeviceType> getDeviceTypesByTestProfile(Long testProfileId) {
      List<DeviceType> result = getDbContext().select().from(DEVICE_TYPE).join(TESTPROFILE_DEVICES)
            .on(TESTPROFILE_DEVICES.DEVICETYPE_ID.eq(DEVICE_TYPE.DEVICE_TYPE_ID))
            .where(TESTPROFILE_DEVICES.TESTPROFILE_ID.eq(testProfileId)).fetch(new RecordMapper<Record, DeviceType>() {
               @Override
               public DeviceType map(Record record) {
                  return record.into(DeviceType.class);
               }
            });
      return result;
   }

   /**
    * Add new device
    *
    * @param device
    * @return
    */
   public Long add(Device device) {
      Long newDeviceId = getDbContext()
            .insertInto(DEVICE, DEVICE.DEVICE_OWNER_ID, DEVICE.DEVICE_SERIAL_NUMBER, DEVICE.DEVICE_UNIQUE_ID,
                  DEVICE.DEVICE_TYPE_ID,
                  DEVICE.DEVICE_OS_ID)
            .values(device.getOwnerId(), device.getSerialNumber(), device.getDeviceUniqueId(),
                  device.getDeviceTypeId(), device.getDeviceOsId()).returning(DEVICE.DEVICE_ID).fetchOne().getDeviceId();
      return newDeviceId;
   }

   /**
    * Delete specified device
    *
    * @param deviceId
    * @throws CbtDaoException
    */
   public void deleteDevice(Long deviceId) throws CbtDaoException {
      int result = getDbContext().delete(DEVICE).where(DEVICE.DEVICE_ID.eq(deviceId)).execute();
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
   public Device getDevice(Long userId, Long deviceId) {
      Record record = getDbContext().select().from(DEVICE).join(DEVICE_TYPE)
            .on(DEVICE.DEVICE_TYPE_ID.eq(DEVICE_TYPE.DEVICE_TYPE_ID)).join(USER).on(USER.ID.eq(DEVICE.DEVICE_OWNER_ID))
            .where(DEVICE.DEVICE_ID.eq(deviceId)).and(DEVICE.DEVICE_OWNER_ID.eq(userId)).fetchOne();
      Device device = null;
      if (null != record) {
         device = record.into(Device.class);
         device.setDeviceType(record.into(DeviceType.class));
         device.setOwner(record.into(User.class));
         if (userId == device.getOwnerId()) {
            device.setListerIsOwner(true);
         }
      }
      return device;
   }

   /**
    * Get device by device unique id
    *
    * @param uniqueId
    * @return
    */
   public Device getDeviceByUid(String uniqueId) {
      DeviceRecord record = (DeviceRecord) getDbContext().select().from(DEVICE)
            .where(DEVICE.DEVICE_UNIQUE_ID.eq(uniqueId)).fetchOne();
      return record.into(Device.class);
   }

   /**
    * Get devices of specified type
    *
    * @param deviceType
    * @return
    */
   public List<Device> getDevicesOfType(Long deviceType, DeviceDeviceState state) {
      SelectJoinStep<Record> select = getDbContext().select().from(DEVICE);
      SelectConditionStep<Record> condition = select.where(DEVICE.DEVICE_TYPE_ID.eq(deviceType));
      if (null != state) {
         condition = condition.and(DEVICE.DEVICE_STATE.eq(state));
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
      List<Device> devices = getDbContext().select().from(DEVICE)
            .where(DEVICE.DEVICE_STATE.eq(DeviceDeviceState.ONLINE))
            .fetch()
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
   public List<Device> getAllAvailableForUser(Long userId, Long deviceType, DeviceDeviceState state) {
      List<Device> ownedDevices = getOwnedByUser(userId, deviceType, state);
      List<Device> sharedDevices = getSharedWithUser(userId, deviceType, state);
      if (null != ownedDevices) {
         if (null != sharedDevices) {
            ownedDevices.addAll(sharedDevices);
         }
      } else {
         ownedDevices = sharedDevices;
      }
      return ownedDevices;
   }

   public List<Device> getOwnedByUser(Long userId, Long deviceType, DeviceDeviceState state) {
      SelectJoinStep<Record> select = getDbContext().select().from(DEVICE);
      SelectConditionStep<Record> condition = select.where(DEVICE.DEVICE_OWNER_ID.eq(userId));
      if (null != deviceType) {
         condition = condition.and(DEVICE.DEVICE_TYPE_ID.eq(deviceType));
      }
      if (null != state) {
         condition = condition.and(DEVICE.DEVICE_STATE.eq(state));
      }
      List<Device> devices = condition.fetch().map(new RecordMapper<Record, Device>() {
         @Override
         public Device map(Record record) {
            Device device = record.into(Device.class);
            device.setListerIsOwner(true);
            return device;
         }
      });
      return devices;
   }

   public List<Device> getSharedWithUser(Long userId, Long deviceType, DeviceDeviceState state) {
      SelectJoinStep<Record> select = getDbContext().select().from(DEVICE).join(DEVICE_SHARING)
            .on(DEVICE_SHARING.DEVICE_SHARING_DEVICE_ID.eq(DEVICE.DEVICE_ID));
      SelectConditionStep<Record> condition = select.where(DEVICE_SHARING.DEVICE_SHARING_USER_ID.eq(userId));
      if (null != deviceType) {
         condition = condition.and(DEVICE.DEVICE_TYPE_ID.eq(deviceType));
      }
      if (null != state) {
         condition = condition.and(DEVICE.DEVICE_STATE.eq(state));
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
   public List<DeviceSharing> getSharedWith(Long deviceId) {
      List<DeviceSharing> result = getDbContext().select().from(DEVICE_SHARING).join(USER)
            .on(USER.ID.eq(DEVICE_SHARING.DEVICE_SHARING_USER_ID))
            .where(DEVICE_SHARING.DEVICE_SHARING_DEVICE_ID.eq(deviceId))
            .fetch(new RecordMapper<Record, DeviceSharing>() {
               @Override
               public DeviceSharing map(Record record) {
                  DeviceSharing sharing = record.into(DeviceSharing.class);
                  sharing.setUser(record.into(User.class));
                  return sharing;
               }
            });
      return result;
   }

   /**
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
      Record result = getDbContext().select().from(DEVICE_TYPE)
            .where(DEVICE_TYPE.DEVICE_TYPE_MANUFACTURE.eq(manufacture)).and(DEVICE_TYPE.DEVICE_TYPE_MODEL.eq(model))
            .fetchOne();
      if (null == result) {
         DeviceTypeRecord record = getDbContext()
               .insertInto(DEVICE_TYPE, DEVICE_TYPE.DEVICE_TYPE_MANUFACTURE, DEVICE_TYPE.DEVICE_TYPE_MODEL)
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
            .set(DEVICE.DEVICE_OS_ID, device.getDeviceOsId())
            .set(DEVICE.DEVICE_STATE, DeviceDeviceState.valueOf(device.getState().toString()))
            .set(DEVICE.DEVICE_UPDATED, new Timestamp(Calendar.getInstance().getTimeInMillis()))
            .where(DEVICE.DEVICE_ID.eq(device.getId())).execute();
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
      getDbContext()
            .insertInto(DEVICE_SHARING, DEVICE_SHARING.DEVICE_SHARING_DEVICE_ID, DEVICE_SHARING.DEVICE_SHARING_USER_ID)
            .values(deviceId, userId).execute();
   }

   public void deleteSharing(Long shareId) {
      getDbContext().delete(DEVICE_SHARING).where(DEVICE_SHARING.DEVICE_SHARING_ID.eq(shareId)).execute();
   }
}
