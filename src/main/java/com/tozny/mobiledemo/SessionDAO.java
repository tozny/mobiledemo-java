/*
 * SessionDAO.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.mobiledemo;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Interface to simple in-memory database.
 */
public class SessionDAO {

    private ImmutableMap<String,Session> sessionMap;

    public SessionDAO() {
        sessionMap = ImmutableMap.of();
    }

    public void insertSession(String userId, Session session) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.putAll(sessionMap);
        builder.put(userId, session);
        sessionMap = builder.build();
    }

    @Nullable
    public Session findByUserId(String userId) {
        return sessionMap.get(userId);
    }

    @Nullable
    public String findUserIdBySessionToken(String sessionToken) {
        for (Map.Entry<String,Session> entry : sessionMap.entrySet()) {
            if (entry.getValue().getSessionToken().equals(sessionToken)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void deleteSession(String sessionToken) {
        Collection<Map.Entry<String,Session>> sessions =
            Collections2.filter(sessionMap.entrySet(), new Predicate<Map.Entry<String,Session>>() {
                public boolean apply(Map.Entry<String,Session> entry) {
                    return !entry.getValue().getSessionToken().equals(sessionToken);
                }
            });
        sessionMap = ImmutableMap.copyOf(sessions);
    }

}
