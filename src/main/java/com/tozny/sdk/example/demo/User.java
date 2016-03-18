/*
 * User.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.sdk.example.demo;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nullable;

public class User {

    private final String email;
    private final String toznyId;
    private final List<Device> devices;

    public User(String email, @Nullable String toznyId, @Nullable List<Device> devices) {
        this.email = email;
        this.toznyId = toznyId;
        this.devices = devices != null ? ImmutableList.copyOf(devices) : ImmutableList.of();
    }

    public String getEmail() { return email; }
    @Nullable public String getToznyId() { return toznyId; }
    public List<Device> getDevices() { return devices; }

    public User setDevices(final List<Device> devices) {
        return new User(email, toznyId, devices);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email, devices);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof User) {
            final User other = (User) obj;
            return Objects.equal(email, other.email)
                && Objects.equal(devices, other.devices);
        }
        else {
            return false;
        }
    }

}
