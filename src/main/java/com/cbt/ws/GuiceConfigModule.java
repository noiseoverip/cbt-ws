package com.cbt.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class GuiceConfigModule extends AbstractModule {
   private final Logger logger = Logger.getLogger(GuiceConfigModule.class);
   private final static String PROPERTIES_PATH = "cbt.properties";

   @Override
   protected void configure() {
      // Enable access to properties through @Named("propertyName")
      Names.bindProperties(binder(), getProperties());
      bind(Configuration.class).in(Singleton.class);
   }

   private Properties getProperties() {
      Properties mProperties = new Properties();
      // First try to load file from the same directory (to allow override file in resources)
      InputStream stream = null;
      try {
         logger.trace("Trying to load from: " + new File(PROPERTIES_PATH));
         stream = new FileInputStream(new File(PROPERTIES_PATH));
      } catch (FileNotFoundException e) {
         logger.warn("Could not load cbt.properties from current directory, trying class path");
         // Try file input stream (file should be in the *current* directory)
         stream = GuiceConfigModule.class.getResourceAsStream("/" + PROPERTIES_PATH);
      }

      try {
         mProperties.load(stream);
      } catch (IOException e) {
         logger.fatal("Could not read project properties !!!");
      }

      return mProperties;
   }
}
