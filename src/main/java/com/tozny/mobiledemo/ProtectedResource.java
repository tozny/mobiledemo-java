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

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/protected/secretmessage")
public class ProtectedResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String,String> getMessage() {
        return Collections.<String,String>singletonMap("message", "You are authenticated.");
    }

}
