package com.cbt.ws;

import com.cbt.ws.dao.UserDao;
import com.cbt.ws.security.CbtPrinciple;
import com.cbt.ws.security.CbtSecurityContext;
import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Map;

class AuthenticationFilter implements ContainerRequestFilter {

   private UserDao mUserDao;

   @Inject
   public AuthenticationFilter(UserDao userDao) {
      mUserDao = userDao;
   }

   //TODO: find a better, more declarative way to configure which pages need auth and which not !
   @Override
   public ContainerRequest filter(ContainerRequest request) {
      if (!"public".equals(request.getPathSegments().get(0).getPath())) {
         CbtPrinciple principal = authenticateByCookie(request);
         if (null == principal) {
            principal = authenticateWithHeaders(request);
         }

         if (null != principal) {
            request.setSecurityContext(new CbtSecurityContext(principal));
         } else {
            throw new WebApplicationException(Status.UNAUTHORIZED);
         }
      }
      return request;
   }

   private CbtPrinciple authenticateByCookie(ContainerRequest request) {
      Map<String, Cookie> cookies = request.getCookies();
      if (cookies.containsKey("auth")) {
         Cookie authCookie = cookies.get("auth");
         String[] up = authCookie.getValue().split(":");
         return CbtPrinciple.fromUser(mUserDao.authenticate(up[0], up[1]));
      }
      return null;
   }

   private CbtPrinciple authenticateWithHeaders(ContainerRequest request) {
      List<String> username = request.getRequestHeader("username");
      List<String> password = request.getRequestHeader("password");
      if (null != username && null != password) {
         return CbtPrinciple.fromUser(mUserDao.authenticate(username.get(0), password.get(0)));
      }
      return null;
   }
}