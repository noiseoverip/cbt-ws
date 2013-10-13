package com.cbt.ws.testtools;

import com.cbt.core.utils.Utils;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

import javax.ws.rs.core.Cookie;
import java.util.ArrayList;
import java.util.List;

/**
 * Jersey Client filter for handling authentication
 *
 * @author SauliusALisauskas
 */
public class ClientAuthFilter extends ClientFilter {

   private Cookie mAuthCookie;
   private List<Object> mCookies = new ArrayList<Object>(1);

   @Override
   public ClientResponse handle(ClientRequest request) {
      if (null == mAuthCookie) {
         // This user must be present on testing system
         mAuthCookie = createAuthCookie("testuser1", Utils.md5("testuser1"));
         mCookies.add(mAuthCookie);
      }
      request.getHeaders().put("Cookie", mCookies);
      return getNext().handle(request);
   }

   private Cookie createAuthCookie(String username, String passoword) {
      return new Cookie("auth", username + ":" + passoword);
   }
}
