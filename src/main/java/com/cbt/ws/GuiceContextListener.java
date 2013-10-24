package com.cbt.ws;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Guice servler context listener for creating injector and setting required modules
 *
 * @author SauliusAlisauskas
 */
public class GuiceContextListener extends GuiceServletContextListener {
   @Override
   protected Injector getInjector() {
      return Guice.createInjector(new GuiceJerseyModule(), new GuiceConfigModule());
   }
}
