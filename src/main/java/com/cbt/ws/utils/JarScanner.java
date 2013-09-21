package com.cbt.ws.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.DexFile;

/**
 * Helper class to scan UIAutomator JAR file and extract test classes
 * 
 * @author SauliusAlisauskas
 * 
 */
public class JarScanner {

	private final Logger logger = Logger.getLogger(JarScanner.class);
	private String filePath;
	private static final String TEST_CLASS_SUPER_TYPE_UIA = "Lcom/android/uiautomator/testrunner/UiAutomatorTestCase;";
   private static final String TEST_CLASS_SUPER_TYPE_INS = "Landroid/test/ActivityTestCase;";
   private static final String TEST_CLASS_SUPER_TYPE_INS2 = "Landroid/test/ActivityInstrumentationTestCase2;";

	public JarScanner(String filePath) {
		this.filePath = filePath;
	}

	public String[] getTestClasseNames() throws JarScannerException {
      List<String> classes = new ArrayList<String>();
		DexFile dxFile;
		try {
			dxFile = new DexFile(new File(filePath), false, false);
		} catch (IOException e) {
			throw new JarScannerException("Could not read DEX file:" + filePath, e);
		}
		for (ClassDefItem cds : dxFile.ClassDefsSection.getItems()) {
			// logger.info(">>" + cds.getSuperclass().getTypeDescriptor());
			if (TEST_CLASS_SUPER_TYPE_UIA.equals(cds.getSuperclass().getTypeDescriptor())
               || TEST_CLASS_SUPER_TYPE_INS.equals(cds.getSuperclass().getTypeDescriptor())
               || TEST_CLASS_SUPER_TYPE_INS2.equals(cds.getSuperclass().getTypeDescriptor())) {
				String testClassRaw = cds.getClassType().getTypeDescriptor();
				// Raw format is: Lcom/test/TestButton2;
				String testClass = testClassRaw.substring(1, testClassRaw.length() - 1).replaceAll("/", "\\.");
				logger.info("Found test class: " + testClass);
				classes.add(testClass);
			}
		}
		return classes.toArray(new String[classes.size()]);
	}
}