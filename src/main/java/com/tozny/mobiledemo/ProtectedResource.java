/*
 * ProtectedResource.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.mobiledemo;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/protected/secretmessage")
public class ProtectedResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessage(@Context SecurityContext securityContext) {
        Principal user = securityContext != null ? securityContext.getUserPrincipal() : null;

        if (user == null) {
            return Response
                .status(Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", "Bearer")
                .build();
        }

        return Response.ok(
                Collections.<String,String>singletonMap("message",
                    "You are authenticated as " + user.getName()
                    )
                )
            .build();
    }

}
