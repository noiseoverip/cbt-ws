package com.cbt.ws.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.dao.DevicejobDao;
import com.cbt.ws.dao.TestRunDao;
import com.cbt.ws.dao.TestScriptDao;
import com.cbt.ws.entity.Device;
import com.cbt.ws.entity.DeviceJob;
import com.cbt.ws.entity.TestConfig;
import com.cbt.ws.entity.TestProfile;
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.entity.TestScript;
import com.cbt.ws.entity.complex.TestRunComplex;
import com.cbt.ws.jooq.enums.DeviceState;
import com.cbt.ws.jooq.enums.TestprofileMode;

public class TestRunWsTest {
	private static ArrayList<Long> DEVICETYPES = new ArrayList<Long>() {
		private static final long serialVersionUID = 2163048026629128105L;
		{
			add(1L);
		}
	};
	private final int deviceCountForType = 5;
	private final Logger logger = Logger.getLogger(TestRunWsTest.class);

	@Test
	public void addTestRun() {
		TestRunDao testRunDao = mock(TestRunDao.class);
		DevicejobDao devicejobDao = mock(DevicejobDao.class);
		DeviceDao deviceDao = mock(DeviceDao.class);
		TestScriptDao testScriptDao = mock(TestScriptDao.class);
		TestRunWs testRunWs = new TestRunWs(testRunDao, devicejobDao, deviceDao, testScriptDao);
		TestProfile testProfile = new TestProfile();
		testProfile.setMode(TestprofileMode.FAST);
		TestConfig testConfig = new TestConfig();
		TestRunComplex testRunComplex = new TestRunComplex();
		testRunComplex.setDeviceTypes(DEVICETYPES);
		testRunComplex.setTestProfile(testProfile);
		testRunComplex.setTestConfig(testConfig);
		TestScript testScript = new TestScript();
		testScript.setTestClasses(new String[] {"testClass1", "testClass2", "testClass3", "testClass4", "testClass5",
				"testClass6", "testClass7", "testClass8"});

		when(testRunDao.getTestRunComplex(anyLong())).thenReturn(testRunComplex);
		when(testScriptDao.getById(anyLong())).thenReturn(testScript);
		when(deviceDao.getDevicesOfType(anyLong(), any(DeviceState.class))).thenAnswer(new Answer<List<Device>>() {

			@Override
			public List<Device> answer(InvocationOnMock invocation) throws Throwable {
				//Long deviceId = Long.valueOf(invocation.getArguments()[0].toString());
				List<Device> devices = new ArrayList<Device>();
				for (int i =0; i<deviceCountForType; i++) {
					devices.add(new Device());
				}				
				return devices;
			}
		});

		when(devicejobDao.add(any(DeviceJob.class))).thenAnswer(new Answer<Long>() {

			@Override
			public Long answer(InvocationOnMock invocation) throws Throwable {
				logger.info("Called add devicejob:" + invocation.getArguments()[0]);
				return null;
			}
		});
		TestRun testRun = new TestRun();
		testRun.setId(1L);
		testRunWs.add(testRun);
	}
}
