/*
 * Application.java
 *
 * Copyright (C) 2016, Tozny, LLC.
 * All Rights Reserved.
 *
 * Released under the Apache license. See the file "LICENSE"
 * for more information.
 */

package com.tozny.mobiledemo;

import com.tozny.sdk.RealmApi;
import com.tozny.sdk.realm.RealmConfig;
import com.tozny.sdk.realm.config.ToznyRealmKeyId;
import com.tozny.sdk.realm.config.ToznyRealmSecret;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {

    public Application(@Context ServletContext context) throws IOException {
        RealmApi realmApi = getRealmApi(context);
        String contextPath = context.getContextPath();
        UserDAO userDAO = new UserDAO();
        SessionDAO sessionDAO = new SessionDAO();

        register(new ProtectedResource());
        register(new SessionResource(contextPath, realmApi, sessionDAO, userDAO));
        register(new UserResource(contextPath, realmApi, userDAO));
    }

    private RealmApi getRealmApi(ServletContext context) throws IOException {
        // Load realm configuration from a properties file.
        Properties prop = new Properties();
        InputStream in = context.getResourceAsStream("/WEB-INF/properties/tozny.properties");
        if (in == null) {
            throw new IOException("property file not found: /WEB-INF/properties/tozny.properties");
        }
        try {
            prop.load(in);
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
        ToznyRealmKeyId realmKey = new ToznyRealmKeyId(prop.getProperty("realmKey"));
        ToznyRealmSecret realmSecret = new ToznyRealmSecret(prop.getProperty("realmSecret"));
        RealmConfig realmConfig = new RealmConfig(realmKey, realmSecret);
        return new RealmApi(realmConfig);
    }

}
