package com.cbt.ws.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.cbt.ws.entity.Device;

/**
 * Utility class for common helper methods
 * 
 * @author SauliusAlisauskas 2013-03-24 Initial version
 *
 */
public final class Utils {
	
	private static final Logger mLogger = Logger.getLogger(Utils.class);
	
	/**
	 * Save file to specified location
	 * 
	 * @param uploadedInputStream
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeToFile(final InputStream uploadedInputStream, final String filePath) throws IOException {
		OutputStream out = new FileOutputStream(new File(filePath));
		int read = 0;
		byte[] bytes = new byte[1024];

		out = new FileOutputStream(new File(filePath));
		while ((read = uploadedInputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}

	public Date getCurrentDate() {
		return new Date();
	}

	/**
	 * Generate MD5 from provided string
	 * 
	 * @param string
	 * @return
	 */
	public static String Md5(String string) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(string.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {

		}
		return null;
	}

	/**
	 * Return string to be used for hashing with MD5
	 * 
	 * @param device
	 * @return
	 */
	public static String buildContentForDeviceUniqueId(Device device) {
		return device.getUserId() + device.getSerialNumber() + device.getDeviceTypeId() + device.getDeviceOsId();
	}

	/**
	 * Helper method to print entities in a consistent way
	 * 
	 * @param objectName
	 * @param vargs
	 * @return
	 */
	public static String toString(String objectName, Object... vargs) {
		final int relatedElements = 2;
		if (null != vargs && vargs.length > 0 && (vargs.length % relatedElements) == 0) {
			StringBuilder buff = new StringBuilder(objectName);
			for (int i = 0; i < vargs.length; i += relatedElements) {
				buff.append(" [" + vargs[i] + ":" + vargs[i + 1] + "]");
			}
			return buff.toString();
		}
		return "Should provide equal number of arguments for toString(), provided:" + vargs.length;
	}
	
	/**
	 * Extract specified ZIP archive into specified destination folder
	 * 
	 * @param pathToZipArchive
	 * @param destination
	 * @throws IOException
	 */
	public static void extractZipFiles(String pathToZipArchive, String destination) throws IOException {
		mLogger.debug("Extractting zip:" + pathToZipArchive + " to:" + destination);
		// Extract downloaded ZIP file
		ZipFile zipFile = new ZipFile(pathToZipArchive);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			File entryDestination = new File(destination, entry.getName());
			entryDestination.getParentFile().mkdirs();
			InputStream in = zipFile.getInputStream(entry);
			OutputStream out = new FileOutputStream(entryDestination);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			mLogger.debug("Extracted file:" + entry.getName() + " size:" + entry.getSize());
		}
		zipFile.close();
	}
}
