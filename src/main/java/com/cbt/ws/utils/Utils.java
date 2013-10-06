package com.cbt.ws.utils;

import com.cbt.core.entity.Device;

/**
 * Utility class for common helper methods
 *
 * @author SauliusAlisauskas 2013-03-24 Initial version
 */
public final class Utils extends com.cbt.core.utils.Utils {

   //private static final Logger mLogger = Logger.getLogger(Utils.class);

   /**
    * Return string to be used for hashing with MD5
    *
    * @param device
    * @return
    */
   public static String buildContentForDeviceUniqueId(Device device) {
      return device.getUserId() + device.getSerialNumber() + device.getDeviceTypeId() + device.getDeviceOsId();
   }
}
