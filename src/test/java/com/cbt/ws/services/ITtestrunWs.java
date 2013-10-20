package com.cbt.ws.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.cbt.jooq.enums.TestrunTestrunStatus;
import com.cbt.ws.GuiceContextListener;
import com.cbt.ws.entity.TestRun;
import com.cbt.ws.testtools.ClientAuthFilter;
import com.cbt.ws.testtools.GetTestRunResponse;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

/**
 * Unit test for {@link DeviceJobsWs}
 *
 * @author SauliusAlisauskas 2013-03-24 Initial version
 */
public class ITtestrunWs extends JerseyTest {
   private static final String PATH_PREFIX = "rip";
   private final Logger logger = Logger.getLogger(ITtestrunWs.class);
   private final ClientAuthFilter authFilter = new ClientAuthFilter();

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

   // TODO: this is very bad tests, when this is executed there are unpredicted number of test runs already created so
   // must be shure to return large enough subset so that id:1 would be there since these are sorted by date
   @Test
   public void testGetUserTestRuns() throws ParseException {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      GetTestRunResponse response = getWebResource().path("testrun").queryParam("offset", "0").queryParam("max", "100")
            .type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE)
            .get(GetTestRunResponse.class);
      logger.info("Total test runs:" + response.getTotalRecords());
      logger.info(Arrays.toString(response.getResults()));

      // Verify fields
      assertTrue(response.getResults().length > 0);
      boolean found = false;
      for (int i = 0; i < response.getResults().length; i++) {
         logger.info("Comparing:" + response.getResults()[i]);
         if (response.getResults()[i].getId() == 1) {
            TestRun testRun = response.getResults()[i];
            assertEquals(testRun.getId(), Long.valueOf(1L));
            assertEquals(testRun.getUserId(), Long.valueOf(1L));
            assertEquals(testRun.getStatus(), TestrunTestrunStatus.WAITING);
            assertEquals(testRun.getId(), Long.valueOf(1L));
            assertEquals(testRun.getTestconfigId(), Long.valueOf(1L));
            assertNotNull(testRun.getTestconfig());
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
