package com.cbt.ws.tools;

import com.cbt.ws.security.CbtPrinciple;

import javax.ws.rs.core.SecurityContext;

public final class Utils {
   public static Long getUserId(SecurityContext context) {
      return ((CbtPrinciple) context.getUserPrincipal()).getId();
   }
}
