package com.cbt.ws.services;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.validator.routines.EmailValidator;
import org.jooq.exception.DataAccessException;

import com.cbt.core.entity.User;
import com.cbt.core.utils.Utils;
import com.cbt.ws.dao.DeviceDao;
import com.cbt.ws.dao.UserDao;
import com.google.inject.Inject;

/**
 * @author SauliusAlisauskas 2013-04-09 Initial version
 */
@Path("/public")
public class PublicWs {

   private final UserDao mUserDao;
   private final DeviceDao mDeviceDao;
   private static final String userRegexp = "[a-zA-Z0-9\\._\\-]{3,}";

   @Inject
   public PublicWs(UserDao dao, DeviceDao deviceDao) {
      mUserDao = dao;
      mDeviceDao = deviceDao;
   }

   // TODO: add basic validation !
   @PUT
   @Path("/user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public User addUser(Map<String, String> details) {
      // Validate username
      String username = details.get("username");
      if (null == username || username.length() <= 2 || !username.matches(userRegexp)) {
         throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
               .entity(new String[] { "Username format error" }).build());
      }
      // Validate password
      String password = details.get("password");
      if (null == password || password.length() <= 5) {
         throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
               .entity(new String[] { "Password format error" }).build());
      }
      // Validate email
      String email = details.get("email");
      if (!EmailValidator.getInstance().isValid(email)) {
         throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
               .entity(new String[] { "Email format error" }).build());
      }

      // TODO: should probably check for unique emails as well
      try {
         return mUserDao.createNew(username, Utils.md5(password), email);
      } catch (DataAccessException e) {
         if (e.getMessage().contains("Duplicate entry")) {
            throw new WebApplicationException(Response.status(Status.CONFLICT)
                  .entity(new String[] { "User already exists" }).build());
         } else {
            throw new WebApplicationException(Response.serverError().build());
         }
      }
   }

   @GET
   @Path("/device-types")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Map<String, Object>> getDeviceByUserId(@QueryParam("userId") Long userId) {
      return mDeviceDao.getDeviceTypes();
   }

}