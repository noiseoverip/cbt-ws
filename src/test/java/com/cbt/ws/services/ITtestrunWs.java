package com.cbt.ws.services;

import com.cbt.jooq.enums.TestrunTestrunStatus;
import com.cbt.ws.GuiceContextListener;
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.testtools.ClientAuthFilter;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link DeviceJobsWs}
 *
 * @author SauliusAlisauskas 2013-03-24 Initial version
 */
public class ITtestrunWs extends JerseyTest {
   private static final String PATH_PREFIX = "rip";
   private final Logger logger = Logger.getLogger(ITtestrunWs.class);
   private ClientAuthFilter authFilter = new ClientAuthFilter();

   @Override
   protected AppDescriptor configure() {
      return new WebAppDescriptor.Builder().filterClass(GuiceFilter.class)
            .contextListenerClass(GuiceContextListener.class).build();
   }

   @Override
   @Before
   public void setUp() throws Exception {
      super.setUp();
      client().addFilter(authFilter);
   }

   @Test
   public void testGetUserTestRuns() throws ParseException {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      TestRun[] testRuns = getWebResource().path("testrun").type(MediaType.APPLICATION_JSON_TYPE)
            .accept(MediaType.APPLICATION_JSON_TYPE).get(TestRun[].class);
      logger.info(Arrays.toString(testRuns));

      // Verify fields
      assertTrue(testRuns.length > 0);
      boolean found = false;
      for (int i = 0; i < testRuns.length; i++) {
         if (testRuns[i].getId() == 1) {
            TestRun testRun = testRuns[i];
            assertEquals(testRun.getId(), Long.valueOf(1L));
            assertEquals(testRun.getUserId(), Long.valueOf(1L));
            assertEquals(testRun.getStatus(), TestrunTestrunStatus.WAITING);
            assertEquals(testRun.getId(), Long.valueOf(1L));
            assertEquals(testRun.getTestconfigId(), Long.valueOf(1L));
            assertNull(testRun.getTestconfig());
            assertEquals(formatter.parse("2013-09-18 00:00:00"), testRun.getCreated());
            assertEquals(formatter.parse("2013-09-21 18:16:32"), testRun.getUpdated());
            assertEquals(testRun.getTestconfigId(), Long.valueOf(1L));
            found = true;
         }
      }
      assertTrue(found);
   }

   /**
    * Helper method to construct web resource base
    *
    * @return
    */
   private WebResource getWebResource() {
      return resource().path(PATH_PREFIX);
   }

}
