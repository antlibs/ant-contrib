/*
 * Copyright (c) 2001-2006 Ant-Contrib project.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.antcontrib.net.httpclient;

/**
 */
public class Credentials {
    /**
     * Field host.
     */
    private String host;

    /**
     * Field port.
     */
    private int port = -1;

    /**
     * Field realm.
     */
    private String realm;

    /**
     * Field scheme.
     */
    private String scheme;

    /**
     * Field username.
     */
    private String username;

    /**
     * Field password.
     */
    private String password;

    /**
     * Method getPassword.
     *
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method setPassword.
     *
     * @param password String
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Method getUsername.
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Method setUsername.
     *
     * @param username String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Method getHost.
     *
     * @return String
     */
    public String getHost() {
        return host;
    }

    /**
     * Method setHost.
     *
     * @param host String
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Method getPort.
     *
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * Method setPort.
     *
     * @param port int
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Method getRealm.
     *
     * @return String
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Method setRealm.
     *
     * @param realm String
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * Method getScheme.
     *
     * @return String
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Method setScheme.
     *
     * @param scheme String
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
}
