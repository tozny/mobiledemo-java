/*
 * UserDAO.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.mobiledemo;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Interface to simple in-memory database.
 */
public class UserDAO {

    private final Map<String,User> userMap;

    public UserDAO(Map<String,User> userMap) {
        this.userMap = userMap;
    }
    public UserDAO() {
        this(new HashMap());
    }

    public void insertUser(User user) {
        userMap.put(user.getEmail(), user);
    }

    @Nullable
    public User findByEmail(String email) {
        return userMap.get(email);
    }

    @Nullable
    public User findByToznyId(String toznyId) {
        if (toznyId == null) {
            return null;
        }
        for (User user : userMap.values()) {
            String userId = user.getToznyId();
            if (userId != null && userId.equals(toznyId)) {
                return user;
            }
        }
        return null;
    }

}
