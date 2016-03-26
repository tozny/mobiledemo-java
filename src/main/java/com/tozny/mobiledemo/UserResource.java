/*
 * UserResource.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.mobiledemo;

import com.google.common.collect.ImmutableList;

import com.tozny.sdk.RealmApi;
import com.tozny.sdk.ToznyApiException;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/users")
public final class UserResource {

    private final String contextPath;
    private final RealmApi realmApi;
    private final UserDAO userDAO;

    public UserResource(String contextPath, RealmApi realmApi, UserDAO userDAO) {
        this.contextPath = contextPath;
        this.realmApi = realmApi;
        this.userDAO = userDAO;
    }

    @Path("{email}/devices")
    @POST
    public Response addDevice(@PathParam("email") String email) {
        User user = getUser(email);

        // Device device = realmApi.userAddDevice(toznyUser);
        if (user == null) {
            return Response
                .status(Response.Status.NOT_IMPLEMENTED)
                .entity("Adding a user to the Tozny realm is not implemented yet.")
                .build();
        }

        return Response
            .status(Response.Status.NOT_IMPLEMENTED)
            .entity("Adding a device via the Java SDK is not implemented yet.")
            .build();
    }

    private User getUser(String email) throws ToznyApiException {
        final User user = userDAO.findByEmail(email);
        if (user == null) {
            return addUser(email);
        }
        else {
            return user;
        }
    }

    private User addUser(final String email) throws ToznyApiException {
        final com.tozny.sdk.realm.User toznyUser = getToznyUser(email);

        // TODO
        if (toznyUser == null) {
            return null;
        }

        List<Device> devices = ImmutableList.of();
        return new User(email, toznyUser.getUserId(), devices);
    }

    private com.tozny.sdk.realm.User getToznyUser(final String email) throws ToznyApiException {
        if (realmApi.userExistsByEmail(email)) {
            return realmApi.userGetByEmail(email);
        }
        else {
            // TODO
            return null;
        }
    }

    private UriBuilder base() {
        return UriBuilder.fromPath(contextPath);
    }

}
