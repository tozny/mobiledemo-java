/*
 * Session.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.mobiledemo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Bean that represents a session token
 */
public class Session {

    @JsonProperty private String session_token;

    public Session() {
        this.session_token = UUID.randomUUID().toString();
    }

    public String getSessionToken() { return session_token; }

}
