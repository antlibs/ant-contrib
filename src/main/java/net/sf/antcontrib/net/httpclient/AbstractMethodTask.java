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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.util.FileUtils;

/**
 */
public abstract class AbstractMethodTask extends Task {
    /**
     * Field method.
     */
    private HttpMethodBase method;

    /**
     * Field responseDataFile.
     */
    private File responseDataFile;

    /**
     * Field responseDataProperty.
     */
    private String responseDataProperty;

    /**
     * Field statusCodeProperty.
     */
    private String statusCodeProperty;

    /**
     * Field httpClient.
     */
    private HttpClient httpClient;

    /**
     * Field responseHeaders.
     */
    private final List<ResponseHeader> responseHeaders = new ArrayList<ResponseHeader>();

    /**
     */
    public static class ResponseHeader {
        /**
         * Field name.
         */
        private String name;
        /**
         * Field property.
         */
        private String property;

        /**
         * Method getName.
         *
         * @return String
         */
        public String getName() {
            return name;
        }

        /**
         * Method setName.
         *
         * @param name String
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Method getProperty.
         *
         * @return String
         */
        public String getProperty() {
            return property;
        }

        /**
         * Method setProperty.
         *
         * @param property String
         */
        public void setProperty(String property) {
            this.property = property;
        }
    }

    /**
     * Method createNewMethod.
     *
     * @return HttpMethodBase
     */
    protected abstract HttpMethodBase createNewMethod();

    /**
     * Method configureMethod.
     *
     * @param method HttpMethodBase
     */
    protected void configureMethod(HttpMethodBase method) {
    }

    /**
     * Method cleanupResources.
     *
     * @param method HttpMethodBase
     */
    protected void cleanupResources(HttpMethodBase method) {
    }

    /**
     * Method addConfiguredResponseHeader.
     *
     * @param responseHeader ResponseHeader
     */
    public void addConfiguredResponseHeader(ResponseHeader responseHeader) {
        this.responseHeaders.add(responseHeader);
    }

    /**
     * Method addConfiguredHttpClient.
     *
     * @param httpClientType HttpClientType
     */
    public void addConfiguredHttpClient(HttpClientType httpClientType) {
        this.httpClient = httpClientType.getClient();
    }

    /**
     * Method createMethodIfNecessary.
     *
     * @return HttpMethodBase
     */
    protected HttpMethodBase createMethodIfNecessary() {
        if (method == null) {
            method = createNewMethod();
        }
        return method;
    }

    /**
     * Method setResponseDataFile.
     *
     * @param responseDataFile File
     */
    public void setResponseDataFile(File responseDataFile) {
        this.responseDataFile = responseDataFile;
    }

    /**
     * Method setResponseDataProperty.
     *
     * @param responseDataProperty String
     */
    public void setResponseDataProperty(String responseDataProperty) {
        this.responseDataProperty = responseDataProperty;
    }

    /**
     * Method setStatusCodeProperty.
     *
     * @param statusCodeProperty String
     */
    public void setStatusCodeProperty(String statusCodeProperty) {
        this.statusCodeProperty = statusCodeProperty;
    }

    /**
     * Method setClientRefId.
     *
     * @param clientRefId String
     */
    public void setClientRefId(String clientRefId) {
        Object clientRef = getProject().getReference(clientRefId);
        if (clientRef == null) {
            throw new BuildException("Reference '" + clientRefId + "' does not exist.");
        }
        if (!(clientRef instanceof HttpClientType)) {
            throw new BuildException("Reference '" + clientRefId + "' is of the wrong type.");
        }
        httpClient = ((HttpClientType) clientRef).getClient();
    }

    /**
     * Method setDoAuthentication.
     *
     * @param doAuthentication boolean
     */
    public void setDoAuthentication(boolean doAuthentication) {
        createMethodIfNecessary().setDoAuthentication(doAuthentication);
    }

    /**
     * Method setFollowRedirects.
     *
     * @param doFollowRedirects boolean
     */
    public void setFollowRedirects(boolean doFollowRedirects) {
        createMethodIfNecessary().setFollowRedirects(doFollowRedirects);
    }

    /**
     * Method addConfiguredParams.
     *
     * @param params MethodParams
     */
    public void addConfiguredParams(MethodParams params) {
        createMethodIfNecessary().setParams(params);
    }

    /**
     * Method setPath.
     *
     * @param path String
     */
    public void setPath(String path) {
        createMethodIfNecessary().setPath(path);
    }

    /**
     * Method setURL.
     *
     * @param url String
     */
    public void setURL(String url) {
        try {
            createMethodIfNecessary().setURI(new URI(url, false));
        } catch (URIException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Method setQueryString.
     *
     * @param queryString String
     */
    public void setQueryString(String queryString) {
        createMethodIfNecessary().setQueryString(queryString);
    }

    /**
     * Method addConfiguredHeader.
     *
     * @param header Header
     */
    public void addConfiguredHeader(Header header) {
        createMethodIfNecessary().setRequestHeader(header);
    }

    /**
     * Method execute.
     *
     * @throws BuildException if something goes wrong
     */
    public void execute() throws BuildException {
        if (httpClient == null) {
            httpClient = new HttpClient();
        }

        HttpMethodBase method = createMethodIfNecessary();
        configureMethod(method);
        try {
            int statusCode = httpClient.executeMethod(method);
            if (statusCodeProperty != null) {
                Property p = (Property) getProject().createTask("property");
                p.setName(statusCodeProperty);
                p.setValue(String.valueOf(statusCode));
                p.perform();
            }

            for (ResponseHeader responseHeader : responseHeaders) {
                Property p = (Property) getProject().createTask("property");
                p.setName(responseHeader.getProperty());
                Header h = method.getResponseHeader(responseHeader.getName());
                if (h != null && h.getValue() != null) {
                    p.setValue(h.getValue());
                    p.perform();
                }

            }
            if (responseDataProperty != null) {
                Property p = (Property) getProject().createTask("property");
                p.setName(responseDataProperty);
                p.setValue(method.getResponseBodyAsString());
                p.perform();
            } else if (responseDataFile != null) {
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    is = method.getResponseBodyAsStream();
                    fos = new FileOutputStream(responseDataFile);
                    byte[] buf = new byte[10 * 1024];
                    int read = 0;
                    while ((read = is.read(buf, 0, 10 * 1024)) != -1) {
                        fos.write(buf, 0, read);
                    }
                } finally {
                    FileUtils.close(fos);
                    FileUtils.close(is);
                }
            }
        } catch (IOException e) {
            throw new BuildException(e);
        } finally {
            cleanupResources(method);
        }
    }
}
