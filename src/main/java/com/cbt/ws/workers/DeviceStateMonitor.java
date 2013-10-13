package com.cbt.ws.workers;

import com.cbt.core.entity.Device;
import com.cbt.core.exceptions.CbtDaoException;
import com.cbt.jooq.enums.DeviceState;
import com.cbt.ws.dao.DeviceDao;
import com.google.inject.Inject;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Runnable for checking device status updates and changing their state to OFFLINE if update was not received on time
 *
 * @author SauliusAlisauskas
 */
public class DeviceStateMonitor implements Runnable {
   private final Logger logger = Logger.getLogger(DeviceStateMonitor.class);
   private DeviceDao mDeviceDao;
   private long timeDeviceTimeout = 1 * 60 * 1000;

   @Inject
   public DeviceStateMonitor(DeviceDao deviceDao) {
      mDeviceDao = deviceDao;
   }

   @Override
   public void run() {
      logger.debug(this.getClass().getSimpleName() + " checking device states");
      List<Device> devices = mDeviceDao.getAllActive();
      long currentTime = System.currentTimeMillis();
      if (null != devices && devices.size() > 0) {
         for (Device device : devices) {
            logger.debug("Checking time for device:" + device);
            long timePassed = currentTime - device.getUpdated().getTime();
            if (timePassed >= timeDeviceTimeout) {
               logger.debug("Device id:" + device.getId() + " timeouted, last seen " + timePassed / 1000
                     + " seconds ago");
               device.setState(DeviceState.OFFLINE);
               try {
                  mDeviceDao.updateDevice(device);
               } catch (CbtDaoException e) {
                  logger.error("Could not change device state " + device);
               }
            }
         }
      }

   }

}
