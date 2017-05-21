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
package net.sf.antcontrib.antserver.commands;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.Project;

import net.sf.antcontrib.antserver.Command;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
@SuppressWarnings("serial")
public abstract class AbstractCommand implements Command {
    /**
     * Method getContentLength.
     *
     * @return long
     * @see net.sf.antcontrib.antserver.Command#getContentLength()
     */
    public long getContentLength() {
        return 0;
    }

    /**
     * Method getContentStream.
     *
     * @return InputStream
     * @throws IOException just in case
     * @see net.sf.antcontrib.antserver.Command#getContentStream()
     */
    public InputStream getContentStream() throws IOException {
        return null;
    }

    /**
     * Method getResponseContentLength.
     *
     * @return long
     * @see net.sf.antcontrib.antserver.Command#getResponseContentLength()
     */
    public long getResponseContentLength() {
        return 0;
    }

    /**
     * Method getResponseContentStream.
     *
     * @return InputStream
     * @see net.sf.antcontrib.antserver.Command#getResponseContentStream()
     */
    public InputStream getResponseContentStream() {
        return null;
    }

    /**
     * Method respond.
     *
     * @param project       Project
     * @param contentLength long
     * @param contentStream InputStream
     * @return boolean
     * @see net.sf.antcontrib.antserver.Command#respond(Project, long, InputStream)
     */
    public boolean respond(Project project,
                           long contentLength,
                           InputStream contentStream) {
        return false;
    }
}
