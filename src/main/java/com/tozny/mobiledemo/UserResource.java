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
import com.tozny.sdk.realm.methods.user_add.UserAddResponse;
import com.tozny.sdk.realm.methods.user_device_add.UserDeviceAddResponse;

import de.scravy.pair.Pair;
import de.scravy.pair.Pairs;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    public Response addDevice(@PathParam("email") String email) {
        Pair<String,String> p;
        try {
            p = getEnrollmentUrlandQrUrl(email);
        }
        catch (ToznyApiException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

        String secretEnrollmentUrl = p.getFirst();
        String secretEnrollmentQrUrl = p.getSecond();

        System.out.println("###################################################################################");
        System.out.println("The next step is to send an email to "+ email +" with this secret enrollment URL:");
        System.out.println("");
        System.out.println("    "+ secretEnrollmentUrl);
        System.out.println("");
        System.out.println("The user can activate that URL on a mobile device to finalize registration of that device.");
        System.out.println("###################################################################################");

        return Response.ok().entity("See server console output for next step.").build();
    }

    private Pair<String,String> getEnrollmentUrlandQrUrl(String email) throws ToznyApiException {
        final com.tozny.sdk.realm.User toznyUser = getToznyUser(email);
        if (toznyUser != null) {
            return addDevice(toznyUser);
        }
        else {
            return enrollUser(email);
        }
    }

    private Pair<String,String> enrollUser(String email) throws ToznyApiException {
        UserAddResponse resp = realmApi.userAddWithEmail(false, email);
        List<Device> devices = ImmutableList.of();

        User localUser = new User(email, resp.getUserId(), devices);
        userDAO.insertUser(localUser);

        return Pairs.from(resp.getSecretEnrollmentUrl(), resp.getSecretEnrollmentQrUrl());
    }

    private Pair<String,String> addDevice(com.tozny.sdk.realm.User toznyUser) throws ToznyApiException {
        UserDeviceAddResponse resp = realmApi.userDeviceAdd(toznyUser.getUserId());
        return Pairs.from(resp.getSecretEnrollmentUrl(), resp.getSecretEnrollmentQrUrl());
    }

    @Nullable
    private com.tozny.sdk.realm.User getToznyUser(final String email) throws ToznyApiException {
        if (realmApi.userExistsByEmail(email)) {
            return realmApi.userGetByEmail(email);
        }
        else {
            return null;
        }
    }

}
