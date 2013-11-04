package com.cbt.ws;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.UserDao;
import com.cbt.ws.security.CbtPrinciple;
import com.cbt.ws.security.CbtSecurityContext;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Class used for user authentication - receives intercepted request, performs authentication, returns UNAUTHORIZED http
 * status
 *
 * @author SauliusAlisauskas 2013-11-03 Initial version
 *
 */
class AuthenticationFilter implements ContainerRequestFilter {
   private final Logger mLogger = Logger.getLogger(AuthenticationFilter.class);
   private final UserDao mUserDao;

   @Inject
   public AuthenticationFilter(UserDao userDao) {
      mUserDao = userDao;
   }

   // TODO: find a better, more declarative way to configure which pages need auth and which not !
   @Override
   public ContainerRequest filter(ContainerRequest request) {
      if (!"public".equals(request.getPathSegments().get(0).getPath())) {
         CbtPrinciple principal = authenticateWithBasicAuthentication(request);
         if (null != principal) {
            request.setSecurityContext(new CbtSecurityContext(principal));
         } else {
            throw new WebApplicationException(Status.UNAUTHORIZED);
         }
      }
      return request;
   }

   /**
    * Authenticate user based on Basic authentication technique
    *
    * @param request
    * @return
    */
   private CbtPrinciple authenticateWithBasicAuthentication(ContainerRequest request) {
      final String base64 = request.getHeaderValue("Authorization");
      final String basicAuthIdentifier = "Basic";
      if (null != base64 && base64.startsWith(basicAuthIdentifier)) {
         String[] creds = new String(
               DatatypeConverter.parseBase64Binary(base64.substring(basicAuthIdentifier.length()))).split(":");
         Preconditions.checkArgument(creds.length == 2, "Malformed base64 contents provided");
         return CbtPrinciple.fromUser(mUserDao.authenticateMD5(creds[0], creds[1]));
      }
      mLogger.error("<Authorization> header was not found or malformed:" + base64);
      return null;
   }
}