package com.cbt.ws;

import com.cbt.ws.workers.DeviceStateMonitor;
import com.google.inject.Inject;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class for scheduling workers
 *
 * @author SauliusAlisauskas
 */
public class WorkerManager {
   private final Logger logger = Logger.getLogger(WorkerManager.class);
   private ScheduledExecutorService mDeviceMonitorExecutor;
   private DeviceStateMonitor mDeviceStateMonitor;
   private Configuration mConfiguration;

   @Inject
   public WorkerManager(DeviceStateMonitor deviceStateMonitor, Configuration configuration) {
      mDeviceStateMonitor = deviceStateMonitor;
      mConfiguration = configuration;
   }

   /**
    * Start worker manager
    */
   public void start() {
      logger.info("Starting up " + this.getClass().getSimpleName());

      // Print out configuration
      logger.info(mConfiguration.toString());

      mDeviceMonitorExecutor = Executors.newScheduledThreadPool(1);
      mDeviceMonitorExecutor.scheduleAtFixedRate(mDeviceStateMonitor, 1, 30, TimeUnit.SECONDS);
   }
}
