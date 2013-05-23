package com.cbt.ws.security;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

/**
 * Subclass of {@link SecurityContext} for holding security related information
 * 
 * @author SauliusAlisauskas
 * 
 */
public class CbtSecurityContext implements javax.ws.rs.core.SecurityContext {
	private CbtPrinciple mUser;
	private Logger mLogger = Logger.getLogger(CbtSecurityContext.class);

	public CbtSecurityContext(CbtPrinciple user) {
		mUser = user;
		mLogger.trace("Created security context for user:" + user);
	}

	@Override
	public Principal getUserPrincipal() {
		return mUser;
	}

	@Override
	public boolean isUserInRole(String role) {
		return false;
	}

	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public String getAuthenticationScheme() {
		return null;
	}
}
