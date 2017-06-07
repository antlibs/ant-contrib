/*
 * Copyright (c) 2001-2004 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.antserver;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
@SuppressWarnings("serial")
public class Response implements Serializable {
    /**
     * Field succeeded.
     */
    private boolean succeeded;

    /**
     * Field errorStackTrace.
     */
    private String errorStackTrace;

    /**
     * Field errorMessage.
     */
    private String errorMessage;

    /**
     * Field resultsXml.
     */
    private String resultsXml;

    /**
     * Field contentLength.
     */
    private long contentLength;

    /**
     * Constructor for Response.
     */
    public Response() {
        super();
        this.succeeded = true;
    }

    /**
     * Method isSucceeded.
     *
     * @return boolean
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Method setSucceeded.
     *
     * @param succeeded boolean
     */
    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    /**
     * Method setThrowable.
     *
     * @param t Throwable
     */
    public void setThrowable(Throwable t) {
        errorMessage = t.getMessage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        t.printStackTrace(ps);
        ps.flush();
        setErrorStackTrace(baos.toString());
    }

    /**
     * Method getErrorStackTrace.
     *
     * @return String
     */
    public String getErrorStackTrace() {
        return errorStackTrace;
    }

    /**
     * Method setErrorStackTrace.
     *
     * @param errorStackTrace String
     */
    public void setErrorStackTrace(String errorStackTrace) {
        this.errorStackTrace = errorStackTrace;
    }

    /**
     * Method getErrorMessage.
     *
     * @return String
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Method setErrorMessage.
     *
     * @param errorMessage String
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Method getResultsXml.
     *
     * @return String
     */
    public String getResultsXml() {
        return resultsXml;
    }

    /**
     * Method setResultsXml.
     *
     * @param resultsXml String
     */
    public void setResultsXml(String resultsXml) {
        this.resultsXml = resultsXml;
    }

    /**
     * Method getContentLength.
     *
     * @return long
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * Method setContentLength.
     *
     * @param contentLength long
     */
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }
}
