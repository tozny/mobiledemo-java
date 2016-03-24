/*
 * AuthenticationFilter.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.mobiledemo;

import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class AuthenticationFilter implements ContainerRequestFilter {

    private final SessionDAO sessionDAO;
    private final UserDAO userDAO;

    public AuthenticationFilter(SessionDAO sessionDAO, UserDAO userDAO) {
        this.sessionDAO = sessionDAO;
        this.userDAO = userDAO;
    }

    public void filter(ContainerRequestContext requestContext) {
        String authorization = requestContext.getHeaderString("Authorization");
        String accessToken = authorization != null ?
            authorization.replaceFirst("Bearer ", "") : "";

        if (accessToken.length() == 0) {
            return;
        }

        String userId = sessionDAO.findUserIdBySessionToken(accessToken);
        User user = userId != null ? userDAO.findByToznyId(userId) : null;

        if (user == null) {
            requestContext.abortWith(
                    Response
                    .status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Bearer error=\"invalid_token\"")
                    .entity("An 'Authorization' header was given, but did not contain a valid session token.")
                    .build()
                    );
        }
        else {
            requestContext.setSecurityContext(new AuthenticatedSecurityContext(user));
        }
    }

    private class AuthenticatedSecurityContext implements SecurityContext {

        private final User user;

        public AuthenticatedSecurityContext(User user) {
            this.user = user;
        }

        public Principal getUserPrincipal() { return user; }
        public boolean isUserInRole(String role) { return false; }
        public boolean isSecure() { return false; }
        public String getAuthenticationScheme() { return "BEARER"; }

    }

}
