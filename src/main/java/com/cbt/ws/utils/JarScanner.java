package com.cbt.ws.utils;

import com.cbt.jooq.enums.TestscriptTestscriptType;
import org.apache.log4j.Logger;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.DexFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to scan UIAutomator JAR file and extract test classes
 *
 * @author SauliusAlisauskas
 */
public class JarScanner {

   private final Logger logger = Logger.getLogger(JarScanner.class);
   private final File file;
   private TestFramework testFramework = TestFramework.UNKNOWN;

   public JarScanner(File file) {
      this.file = file;
   }

   public String[] getTestClasseNames() throws JarScannerException {
      List<String> classes = new ArrayList<String>();
      DexFile dxFile;
      try {
         dxFile = new DexFile(file, false, false);
      } catch (IOException e) {
         throw new JarScannerException("Could not read DEX file:" + file, e);
      }
      for (ClassDefItem cds : dxFile.ClassDefsSection.getItems()) {
         // logger.info(">>" + cds.getSuperclass().getTypeDescriptor());
         TestFramework tmpTestFramework = getTestFramework(cds.getSuperclass().getTypeDescriptor());
         if (!TestFramework.UNKNOWN.equals(testFramework) && !TestFramework.UNKNOWN.equals(tmpTestFramework) && !testFramework.getTestScriptType().equals(tmpTestFramework.getTestScriptType())) {
            throw new IllegalStateException("Multiple test framework usage currently not supported!");
         }

         if (!TestFramework.UNKNOWN.equals(tmpTestFramework)) {
            testFramework = tmpTestFramework;
            String testClassRaw = cds.getClassType().getTypeDescriptor();
            // Raw format is: Lcom/test/TestButton2;
            String testClass = testClassRaw.substring(1, testClassRaw.length() - 1).replaceAll("/", "\\.");
            logger.info("Found test class: " + testClass);
            classes.add(testClass);
         }
      }
      return classes.toArray(new String[classes.size()]);
   }

   private TestFramework getTestFramework(String superClass) {
      if (null != superClass) {
         for (TestFramework testFramework : TestFramework.values()) {
            if (superClass.equals(testFramework.getClassName())) {
               return testFramework;
            }
         }
      }

      return TestFramework.UNKNOWN;
   }

   public TestscriptTestscriptType getTestScriptType() {
      return testFramework.getTestScriptType();
   }

   private enum TestFramework {
      UIAUTOMATOR(TestscriptTestscriptType.UIAUTOMATOR), INSTRUMENTATION1(TestscriptTestscriptType.INSTRUMENTATION), INSTRUMENTATION2(TestscriptTestscriptType.INSTRUMENTATION), UNKNOWN(null);

      private String className;
      private TestscriptTestscriptType testScriptType;

      private TestFramework(TestscriptTestscriptType testScriptType) {
         this.testScriptType = testScriptType;
      }

      static {
         UIAUTOMATOR.className = "Lcom/android/uiautomator/testrunner/UiAutomatorTestCase;";
         INSTRUMENTATION1.className = "Landroid/test/ActivityTestCase;";
         INSTRUMENTATION2.className = "Landroid/test/ActivityInstrumentationTestCase2;";
      }

      public TestscriptTestscriptType getTestScriptType() {
         return testScriptType;
      }

      public String getClassName() {
         return className;
      }
   }
}