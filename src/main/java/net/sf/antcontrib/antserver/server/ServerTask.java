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
package net.sf.antcontrib.antserver.server;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class ServerTask extends Task {
    /**
     * Field server.
     */
    private Server server;

    /**
     * Field port.
     */
    private int port = 17000;

    /**
     * Constructor for ServerTask.
     */
    public ServerTask() {
        super();
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
     * Method shutdown.
     */
    public void shutdown() {
        server.stop();
    }

    /**
     * Method execute.
     */
    public void execute() {
        try {
            server = new Server(this, port);
            server.start();
        } catch (InterruptedException e) {
            throw new BuildException(e);
        }
    }
}
