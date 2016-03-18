/*
 * UserDAO.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.sdk.example.demo;

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

}
