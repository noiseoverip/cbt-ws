package com.cbt.ws.tools;

import com.cbt.core.entity.Device;
import com.cbt.core.entity.DeviceJob;
import com.cbt.core.entity.TestScript;
import com.cbt.core.entity.complex.TestRunComplex;
import com.cbt.jooq.enums.DeviceDeviceState;
import com.cbt.jooq.enums.TestprofileTestprofileMode;
import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.dao.TestScriptDao;
import com.cbt.ws.exceptions.CbtNoDevicesException;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Test run logic handler
 *
 * @author Saulius Alisauskas
 */
public class TestRunProccessor {
   static final int MAX_DEVICE_PER_TEST = 3;
   private final Logger mLogger = Logger.getLogger(TestRunProccessor.class);
   private final TestRunDao mTestrunDao;
   private final DevicejobDao mDevicejobDao;
   private final DeviceDao mDeviceDao;
   private final TestScriptDao mTestScriptDao;

   @Inject
   public TestRunProccessor(TestRunDao testRunDao, DevicejobDao devicejobDao, DeviceDao deviceDao,
                            TestScriptDao testScripDao) {
      mTestrunDao = testRunDao;
      mDevicejobDao = devicejobDao;
      mDeviceDao = deviceDao;
      mTestScriptDao = testScripDao;
   }

   // TODO: need to redo this logic

   /**
    * Get devices available for specified user and specified testRunId
    *
    * @param testRunId
    * @param userId
    * @return
    * @throws CbtNoDevicesException
    */
   public List<Device> getDevices(Long testRunId, Long userId) throws CbtNoDevicesException {
      List<Device> devices = null;
      TestRunComplex testRunComplex = mTestrunDao.getTestRunComplex(testRunId);
      TestScript testScript = mTestScriptDao.getById(testRunComplex.getTestConfig().getTestScriptId());

      if (testRunComplex.getTestProfile().getMode().equals(TestprofileTestprofileMode.NORMAL)) {
         devices = handleNormalMode(testRunComplex, testScript, userId);
      } else {
         devices = handleFastMode(testRunComplex, testScript, userId);
      }

      return devices;
   }

   /**
    * Handle fast mode. Handles cases: when testClasses > devices when devices > testClases when testClasses = devices
    *
    * Workarounds: last device gets the rest of test cases
    *
    * @param testRunComplex
    * @throws CbtNoDevicesException
    */
   private List<Device> handleFastMode(TestRunComplex testRunComplex, TestScript testScript, Long userId)
         throws CbtNoDevicesException {
      mLogger.debug("Handling FAST mode ");
      List<DeviceJob> jobs = new ArrayList<DeviceJob>();
      List<Device> devicesAll = new ArrayList<Device>();
      List<Long> deviceTypes = testRunComplex.getDeviceTypes();
      for (Long deviceType : deviceTypes) {
         List<Device> devices = mDeviceDao.getAllAvailableForUser(userId, deviceType, DeviceDeviceState.ONLINE);
         if (null != devices && devices.size() > 0) {
            // Do this for each of device types
            int numberOfAvailableDevices = devices.size();
            String[] testClasses = testScript.getTestClasses();
            int numberOfTestCases = testClasses.length;
            mLogger.info("Found " + devices + " of type id:" + deviceType);
            mLogger.info("Will split test classes:" + Arrays.toString(testClasses));
            int testClassesPerDevice = (int) Math.floor(numberOfTestCases / numberOfAvailableDevices);
            if (testClassesPerDevice == 0) {
               testClassesPerDevice = 1;
            }
            mLogger.info("TestClassesPerDevice:" + testClassesPerDevice);

            int testClassIndex = 0;
            int deviceIndex = 0;
            for (Device device : devices) {
               DeviceJob job = new DeviceJob();
               job.setDeviceId(device.getId());
               job.setTestRunId(testRunComplex.getId());
               List<String> testClassesForDevice = new ArrayList<String>();
               for (int i = 0; i < testClassesPerDevice; i++) {
                  testClassesForDevice.add(testClasses[testClassIndex]);
                  testClassIndex++;
                  if (deviceIndex == (devices.size() - 1)) {
                     // Add all the rest of test classes if this is the last device that we have
                     while (testClassIndex <= (numberOfTestCases - 1)) {
                        testClassesForDevice.add(testClasses[testClassIndex]);
                        testClassIndex++;
                     }
                     break;
                  }
               }
               job.getMetadata().setTestClasses(
                     testClassesForDevice.toArray(new String[testClassesForDevice.size()]));
               Long deviceJobId = mDevicejobDao.add(job);
               if (null != deviceJobId) {
                  job.setId(deviceJobId);
                  jobs.add(job);
               } else {
                  mLogger.error("Could not created job:" + job);
               }
               devicesAll.add(device);
               if ((testClassIndex - 1) == (numberOfTestCases - 1)) {
                  // Stop looping through devices if we have distributed all the test cases
                  break;
               }
               deviceIndex++;
            }
         }
      }
      if (jobs.size() < 1) {
         mLogger.warn("Could not find any devices ONLINE of types:" + deviceTypes);
         throw new CbtNoDevicesException();
      }
      return devicesAll;
   }

   private List<Device> handleNormalMode(TestRunComplex testRunComplex, TestScript testScript, Long userId)
         throws CbtNoDevicesException {
      List<DeviceJob> jobs = new ArrayList<DeviceJob>();
      List<Device> devicesAll = new ArrayList<Device>();
      List<Long> deviceTypes = testRunComplex.getDeviceTypes();

      for (Long deviceType : deviceTypes) {
         List<Device> devices = mDeviceDao.getAllAvailableForUser(userId, deviceType, DeviceDeviceState.ONLINE);
         if (null != devices && devices.size() > 0) {
            Iterator<Device> it = devices.iterator();
            while (it.hasNext() && devicesAll.size() < MAX_DEVICE_PER_TEST) {
               Device device = it.next();
               DeviceJob job = new DeviceJob();
               job.setDeviceId(device.getId());
               job.setTestRunId(testRunComplex.getId());
               job.getMetadata().setTestClasses(testScript.getTestClasses());
               Long deviceJobId = mDevicejobDao.add(job);
               if (null != deviceJobId) {
                  job.setId(deviceJobId);
                  jobs.add(job);
               } else {
                  mLogger.error("Could not created job:" + job);
               }
               devicesAll.add(device);
            }
         }
      }
      if (jobs.size() < 1) {
         mLogger.warn("Could not find any devices ONLINE of types:" + deviceTypes);
         throw new CbtNoDevicesException();
      }
      return devicesAll;
   }
}
