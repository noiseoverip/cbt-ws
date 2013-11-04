package com.cbt.ws.services;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jooq.exception.DataAccessException;
import org.junit.Test;

import com.cbt.core.entity.User;
import com.cbt.ws.dao.UserDao;

/**
 * Unit test for {@link PublicWs}
 *
 * @author SauliusAlisauskas 2013-11-04 Initial version
 *
 */
public class TestPublicAccessWs {

   @Test
   public void testCreateUserOk() {
      UserDao userDao = mock(UserDao.class);
      PublicWs ws = new PublicWs(userDao, null);
      Map<String, String> details = new HashMap<String, String>();
      details.put("username", "saulius");
      details.put("password", "funikulierius");
      details.put("email", "jonas@petras.com");
      User user = new User();
      when(userDao.createNew(any(String.class), any(String.class), any(String.class))).thenReturn(user);
      assertEquals(user, ws.addUser(details));
   }

   @Test(expected = WebApplicationException.class)
   public void testCreateUserBadEmail() {
      PublicWs ws = new PublicWs(null, null);
      Map<String, String> details = new HashMap<String, String>();
      details.put("username", "saulius");
      details.put("password", "funikulierius");
      details.put("email", "jonas@petras");
      ws.addUser(details);
   }

   @Test(expected = WebApplicationException.class)
   public void testCreateUserBadUsername() {
      PublicWs ws = new PublicWs(null, null);
      Map<String, String> details = new HashMap<String, String>();
      details.put("username", "sa");
      details.put("password", "funikulierius");
      details.put("email", "jonas@petras.com");
      ws.addUser(details);
   }

   @Test(expected = WebApplicationException.class)
   public void testCreateUserBadPassword() {
      PublicWs ws = new PublicWs(null, null);
      Map<String, String> details = new HashMap<String, String>();
      details.put("username", "saulius");
      details.put("password", "funi");
      details.put("email", "jonas@petras.com");
      ws.addUser(details);
   }

   @Test
   public void testCreateExistingUser() {
      UserDao userDao = mock(UserDao.class);
      PublicWs ws = new PublicWs(userDao, null);
      Map<String, String> details = new HashMap<String, String>();
      details.put("username", "saulius");
      details.put("password", "funikulierius");
      details.put("email", "jonas@petras.com");
      when(userDao.createNew(any(String.class), any(String.class), any(String.class))).thenThrow(
            new DataAccessException("Duplicate entry for blabla"));
      // Make sure it returns status CONFLICT
      try {
         ws.addUser(details);
      } catch (WebApplicationException e) {
         assertEquals(Status.CONFLICT.getStatusCode(), e.getResponse().getStatus());
      }
   }
}
