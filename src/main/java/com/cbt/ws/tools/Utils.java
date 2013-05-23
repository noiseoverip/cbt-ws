package com.cbt.ws.tools;

import javax.ws.rs.core.SecurityContext;

import com.cbt.ws.security.CbtPrinciple;

public final class Utils {
	public static Long getUserId(SecurityContext context) {
		return ((CbtPrinciple)context.getUserPrincipal()).getId();
	}
}
