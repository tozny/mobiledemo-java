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

import java.io.UnsupportedEncodingException;
import java.lang.RuntimeException;
import java.net.URLEncoder;
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
    private final String realmKeyId;
    private final UserDAO userDAO;

    public UserResource(String contextPath, RealmApi realmApi, String realmKeyId, UserDAO userDAO) {
        this.contextPath = contextPath;
        this.realmApi = realmApi;
        this.realmKeyId = realmKeyId;
        this.userDAO = userDAO;
    }

    @Path("{email}/devices")
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    public Response addDevice(@PathParam("email") String email) {
        String tempKey;
        try {
            tempKey = getUserTempKey(email);
        }
        catch (ToznyApiException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

        String secretEnrollmentUrl = buildEnrollmentUrl(tempKey);

        System.out.println("###################################################################################");
        System.out.println("");
        System.out.println("The next step is to send an email to "+ email +" with this secret enrollment URL:");
        System.out.println("");
        System.out.println("    "+ secretEnrollmentUrl);
        System.out.println("");
        System.out.println("The user can activate that URL on a mobile device to finalize registration of that device.");
        System.out.println("");
        System.out.println("###################################################################################");

        return Response.ok().entity("See server console output for next step.").build();
    }

    private String getUserTempKey(String email) throws ToznyApiException {
        final com.tozny.sdk.realm.User toznyUser = getToznyUser(email);
        if (toznyUser != null) {
            return addDevice(toznyUser);
        }
        else {
            return enrollUser(email);
        }
    }

    /**
     * Register a new user record with Tozny. Returns a temporary key that the
     * user can use in a mobile app to complete registration.
     */
    private String enrollUser(String email) throws ToznyApiException {
        UserAddResponse resp = realmApi.userAddWithEmail(true, email);
        List<Device> devices = ImmutableList.of();

        User localUser = new User(email, resp.getUserId(), devices);
        userDAO.insertUser(localUser);

        return resp.getUserTempKey();
    }

    /**
     * Add a new authentication device to an existing Tozny user record. Returns
     * a temporary key that the user can use in a mobile app to complete
     * device registration.
     */
    private String addDevice(com.tozny.sdk.realm.User toznyUser) throws ToznyApiException {
        UserDeviceAddResponse resp = realmApi.userDeviceAdd(toznyUser.getUserId());
        return resp.getTempKey();
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

    private String buildEnrollmentUrl(String tempKey) {
        try {
            String encodedTempKey = URLEncoder.encode(tempKey, "UTF-8");
            String encodedRealmKeyId = URLEncoder.encode(realmKeyId);
            return "tozdemo://api.tozny.com/tozadd/?k=" + encodedTempKey + "&r=" + encodedRealmKeyId;
        }
        catch (UnsupportedEncodingException e) {
            // We should never get here - assume that the runtime environment
            // can encode UTF-8.
            throw new RuntimeException(e);
        }
    }

}
