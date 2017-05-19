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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 */
public class AddCredentialsTask extends AbstractHttpStateTypeTask {
    /**
     * Field credentials.
     */
    private final List<Credentials> credentials = new ArrayList<Credentials>();

    /**
     * Field proxyCredentials.
     */
    private final List<Credentials> proxyCredentials = new ArrayList<Credentials>();

    /**
     * Method addConfiguredCredentials.
     *
     * @param credentials Credentials
     */
    public void addConfiguredCredentials(Credentials credentials) {
        this.credentials.add(credentials);
    }

    /**
     * Method addConfiguredProxyCredentials.
     *
     * @param credentials Credentials
     */
    public void addConfiguredProxyCredentials(Credentials credentials) {
        this.proxyCredentials.add(credentials);
    }

    /**
     * Method execute.
     *
     * @param stateType HttpStateType
     * @throws BuildException when there are no credentials
     */
    protected void execute(HttpStateType stateType) throws BuildException {
        if (credentials.isEmpty() && proxyCredentials.isEmpty()) {
            throw new BuildException("Either regular or proxy credentials"
                    + " must be supplied.");
        }

        Iterator<Credentials> it = credentials.iterator();
        while (it.hasNext()) {
            Credentials c = it.next();
            stateType.addConfiguredCredentials(c);
        }

        it = proxyCredentials.iterator();
        while (it.hasNext()) {
            Credentials c = it.next();
            stateType.addConfiguredProxyCredentials(c);
        }
    }
}
