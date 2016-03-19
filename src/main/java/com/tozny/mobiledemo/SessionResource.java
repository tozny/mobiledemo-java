/*
 * SessionResource.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.mobiledemo;

import com.google.api.client.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tozny.sdk.RealmApi;
import com.tozny.sdk.realm.RealmConfig;
import com.tozny.sdk.realm.config.ToznyRealmKeyId;
import com.tozny.sdk.realm.config.ToznyRealmSecret;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Receive signed data from the Tozny Javascript and log the user in if
 * the authentication is successful.
 *
 * The Javascript frontend is configured to POST signed data to this URL
 * when the user authenticates via their mobile device. If the signature
 * is valid for our realm, we save the user's information to the session
 * and allow them to access protected resources.
 */
@Path("/session")
public class SessionResource {

    private final RealmApi realmApi;
    private final String contextPath;
    private final SessionDAO sessionDAO;
    private final UserDAO userDAO;

    public SessionResource(
            String contextPath,
            RealmApi realmApi,
            SessionDAO sessionDAO,
            UserDAO userDAO) {
        this.realmApi = realmApi;
        this.contextPath = contextPath;
        this.sessionDAO = sessionDAO;
        this.userDAO = userDAO;
    }

    @Nullable
    public static String getSessionToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null) {
            String[] authParts = authHeader.split(" ");
            return authParts[authParts.length - 1];
        }
        else {
            return null;
        }
    }

    @Nullable
    public static String validToznyId(HttpServletRequest req, SessionDAO sessionDAO) {
        String sessionToken = getSessionToken(req);
        if (sessionToken == null) {
            return null;
        }
        else {
            return sessionDAO.findUserIdBySessionToken(sessionToken);
        }
    }

    /*
     * POSTing a valid Tozny assertion creates an authenticated session.
     * This is how a user logs in.
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSession(
            @FormParam("signed_data") String signedData,
            @FormParam("signature")   String signature
            ) {
        if (signature == null || signedData == null) {
            return badRequest();
        }

        if (realmApi.verifyLogin(signedData, signature)) {
            UserInfo userInfo;
            try {
                userInfo = parseSignedData(signedData, UserInfo.class);
            }
            catch (IOException e) {
                return Response.serverError().entity(e).build();
            }

            User user = userDAO.findByToznyId(userInfo.user_id);
            if (user == null) {
                return notAuthorized();
            }

            Session appSession = new Session();
            sessionDAO.insertSession(userInfo.user_id, appSession);

            return Response
                .created(
                        UriBuilder
                        .fromPath(contextPath)
                        .path(getClass())
                        .build())
                .entity(appSession)
                .build();
        } else {
            return notAuthorized();
        }
    }

    /*
     * DELETEing destroys the user's authenticated session - if one is active.
     * This is how the user logs out.
     */
    @DELETE
    public Response destroySession(@Context HttpServletRequest req) {
        String sessionToken = getSessionToken(req);
        if (sessionToken == null) {
            return notAuthorized();
        }
        else {
            sessionDAO.deleteSession(sessionToken);
            return Response.noContent().build();
        }
    }

    private <T> T parseSignedData(String signedData, Class<T> klass) throws IOException {
        String json = new String(Base64.decodeBase64(signedData));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, klass);
    }

    private Response notAuthorized() {
        return Response
            .status(Response.Status.UNAUTHORIZED)
            .build();
    }

    private Response badRequest() {
        return Response
            .status(Response.Status.BAD_REQUEST)
            .build();
    }

}
