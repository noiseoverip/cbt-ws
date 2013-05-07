package com.cbt.ws.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cbt.ws.workers.DeviceStateMonitor;
import com.google.inject.Inject;

/**
 * Class for scheduling worksers
 * 
 * @author SauliusAlisauskas
 *
 */
public class WorkerManager {
	private final Logger logger = Logger.getLogger(WorkerManager.class);
	private ScheduledExecutorService mDeviceMonitorExecutor;
	private DeviceStateMonitor mDeviceStateMonitor;
	
	@Inject
	public WorkerManager(DeviceStateMonitor deviceStateMonitor) {
		mDeviceStateMonitor = deviceStateMonitor;
	}
	
	/**
	 * Start worker manager
	 */
	public void start() {
		logger.info("Starting up " + this.getClass().getSimpleName());
		mDeviceMonitorExecutor = Executors.newScheduledThreadPool(1);
		mDeviceMonitorExecutor.scheduleAtFixedRate(mDeviceStateMonitor, 1, 10, TimeUnit.SECONDS);
	}
}
