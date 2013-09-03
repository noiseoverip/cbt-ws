package com.cbt.ws.services;
//package com.cbt.ws.services;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Enumeration;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//
//import javax.ws.rs.core.MediaType;
//
//import junit.framework.Assert;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.log4j.Logger;
//import org.junit.Test;
//
//import com.cbt.ws.entity.DeviceJob;
//import com.cbt.ws.entity.TestConfig;
//import com.cbt.ws.entity.TestPackage;
//import com.cbt.ws.entity.TestRun;
//import com.cbt.ws.entity.TestScript;
//import com.cbt.ws.entity.TestTarget;
//import com.cbt.ws.main.HelloGuiceServletConfig;
//import com.google.inject.servlet.GuiceFilter;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.multipart.FormDataMultiPart;
//import com.sun.jersey.multipart.file.FileDataBodyPart;
//import com.sun.jersey.test.framework.AppDescriptor;
//import com.sun.jersey.test.framework.JerseyTest;
//import com.sun.jersey.test.framework.WebAppDescriptor;
//
///**
// * System test targeted add resource uploading and downloading
// * 
// * @author SauliusAlisauskas 2013-03-23 Initial version
// * 
// */
//public class UploadDownloadTest extends JerseyTest {
//
//	private static final Long mTestProfileId = 1L;
//	private static final Long mUserId = 1L;
//	private static final String mWorkspace = "/tmp/cbt/";
//	private final Logger mLogger = Logger.getLogger(UploadDownloadTest.class);
//
//	@Override
//	protected AppDescriptor configure() {
//		return new WebAppDescriptor.Builder().filterClass(GuiceFilter.class)
//				.contextListenerClass(HelloGuiceServletConfig.class).build();
//	}
//
//	private TestConfig createTestConfiguration(Long testScriptId, Long testAppId) {
//		TestConfig testConfig = new TestConfig();
//		testConfig.setUserId(mUserId);
//		testConfig.setTestScriptId(testScriptId);
//		testConfig.setTestTargetId(testAppId);
//		testConfig.setTestProfileId(mTestProfileId);
//		ClientResponse response = resource().path("testconfig").type(MediaType.APPLICATION_JSON)
//				.put(ClientResponse.class, testConfig);
//		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
//		return response.getEntity(TestConfig.class);
//	}
//
//	private TestRun createTestRun(TestConfig testConfig) {
//		TestRun testRun = new TestRun();
//		testRun.setTestConfigId(testConfig.getId());
//		testRun.setUserId(1L);
//		ClientResponse response = resource().path("testrun").type(MediaType.APPLICATION_JSON)
//				.put(ClientResponse.class, testRun);
//		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
//		return response.getEntity(TestRun.class);
//	}
//
//	private void downloadTestPackage(Long deviceJobId) throws IOException {
//		mLogger.info("Checking out files for job id:" + deviceJobId + " workspace:" + mWorkspace);
//		ClientResponse response = resource().path("checkout/testpackage.zip")
//				.queryParam("devicejob_id", deviceJobId.toString()).get(ClientResponse.class);
//		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
//		File s = response.getEntity(File.class);
//		new File(mWorkspace).mkdirs();
//		File ff = new File(mWorkspace + "testpackage.zip");
//		s.renameTo(ff);
//		FileWriter fr = new FileWriter(s);
//		fr.flush();
//		fr.close();
//
//		// Extract downloaded ZIP file
//		ZipFile zipFile = new ZipFile(ff.getAbsolutePath());
//		Enumeration<? extends ZipEntry> entries = zipFile.entries();
//		while (entries.hasMoreElements()) {
//			ZipEntry entry = entries.nextElement();
//			File entryDestination = new File(mWorkspace, entry.getName());
//			entryDestination.getParentFile().mkdirs();
//			InputStream in = zipFile.getInputStream(entry);
//			OutputStream out = new FileOutputStream(entryDestination);
//			IOUtils.copy(in, out);
//			IOUtils.closeQuietly(in);
//			IOUtils.closeQuietly(out);
//			mLogger.info("Extracted file:" + entry.getName() + " size:" + entry.getSize());
//		}
//		zipFile.close();
//
//		// Extract zip and compare file in our jar with the ones downloaded ones (MD5)
//	}
//
//	private DeviceJob[] getDeviceJobs(Long testRunId) {
//		DeviceJob[] response = resource().path("devicejobs").queryParam("testRunId", testRunId.toString())
//				.get(DeviceJob[].class);
//		return response;
//	}
//
//	private TestPackage getTestPackage(Long deviceJobId) {
//		TestPackage testPackage = resource().path("checkout/testpackage")
//				.queryParam("devicejob_id", deviceJobId.toString()).get(TestPackage.class);
//		Assert.assertNotNull(testPackage);
//		return testPackage;
//	}
//
//	@Test
//	public void testUpload() throws IOException {
//		TestScript script = uploadScript();
//		Assert.assertNotNull(script.getId());
//		mLogger.info(script);
//
//		TestTarget target = uploadTargetApp();
//		Assert.assertNotNull(target.getId());
//		mLogger.info(target);
//
//		TestConfig testConfig = createTestConfiguration(script.getId(), target.getId());
//		Assert.assertNotNull(testConfig.getId());
//		mLogger.info(testConfig);
//
//		TestRun testRun = createTestRun(testConfig);
//		Assert.assertNotNull(testRun.getId());
//		mLogger.info(testRun);
//
//		DeviceJob[] createdJobs = getDeviceJobs(testRun.getId());
//		mLogger.info("List of created jobs:" + createdJobs);
//
//		TestPackage testPackage = getTestPackage(createdJobs[0].getId());
//		mLogger.info(testPackage);
//
//		downloadTestPackage(createdJobs[0].getId());
//	}
//
//	// TODO: make one method for uploading files with arguments such as file path and web resource path
//	private TestScript uploadScript() {
//		String filePath = this.getClass().getClassLoader().getResource("testScript.jar").getPath();
//		FormDataMultiPart form = new FormDataMultiPart();
//		form.field("username", "saulius");
//		form.bodyPart(new FileDataBodyPart("file", new File(filePath), MediaType.MULTIPART_FORM_DATA_TYPE));
//		ClientResponse response = resource().path("testscript/add").type(MediaType.MULTIPART_FORM_DATA)
//				.post(ClientResponse.class, form);
//		mLogger.info("Response:" + response);
//		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
//		return response.getEntity(TestScript.class);
//	}
//
//	private TestTarget uploadTargetApp() {
//		String filePath = this.getClass().getClassLoader().getResource("testApp.apk").getPath();
//		FormDataMultiPart form = new FormDataMultiPart();
//		form.field("username", "saulius");
//		form.bodyPart(new FileDataBodyPart("file", new File(filePath), MediaType.MULTIPART_FORM_DATA_TYPE));
//		ClientResponse response = resource().path("testtarget/add").type(MediaType.MULTIPART_FORM_DATA)
//				.post(ClientResponse.class, form);
//		mLogger.info("Response:" + response);
//		assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
//		return response.getEntity(TestTarget.class);
//	}
//}
