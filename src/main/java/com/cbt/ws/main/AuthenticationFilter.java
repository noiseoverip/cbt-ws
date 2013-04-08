package com.cbt.ws.main;

import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.cbt.ws.dao.UserDao;
import com.google.inject.Inject;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

class AuthenticationFilter implements ContainerRequestFilter {

	private final Logger mLogger = Logger.getLogger(AuthenticationFilter.class);
	private UserDao mUserDao;

	@Inject
	public AuthenticationFilter(UserDao userDao) {
		mUserDao = userDao;
	}

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		if (authenticateByCookie(request) || authenticateWithHeaders(request)) {
			return request;
		}
		throw new WebApplicationException(Status.UNAUTHORIZED);
	}

	private boolean authenticateByCookie(ContainerRequest request) {
		Map<String, Cookie> cookies = request.getCookies();
		if (cookies.containsKey("auth")) {
			Cookie authCookie = cookies.get("auth");
			String[] up = authCookie.getValue().split(":");
			if (mUserDao.authenticate(up[0], up[1])) {
				return true;
			}
		}
		return false;
	}

	private boolean authenticateWithHeaders(ContainerRequest request) {
		List<String> username = request.getRequestHeader("username");
		List<String> password = request.getRequestHeader("password");
		mLogger.info("Req u:" + username + " p:" + password);
		if (null != username && null != password && mUserDao.authenticate(username.get(0), password.get(0))) {
			return true;
		}
		return false;
	}
}