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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.Socket;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.Project;

import net.sf.antcontrib.antserver.Command;
import net.sf.antcontrib.antserver.Response;
import net.sf.antcontrib.antserver.Util;
import net.sf.antcontrib.antserver.commands.DisconnectCommand;
import net.sf.antcontrib.antserver.commands.ShutdownCommand;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class ConnectionHandler implements Runnable {
    /**
     * Field nextGroupId.
     */
    private static long nextGroupId = 0;

    /**
     * Field task.
     */
    private final ServerTask task;

    /**
     * Field socket.
     */
    private final Socket socket;

    /**
     * Field thrown.
     */
    private Throwable thrown;

    /**
     * Constructor for ConnectionHandler.
     *
     * @param task   ServerTask
     * @param socket Socket
     */
    public ConnectionHandler(ServerTask task, Socket socket) {
        super();
        this.socket = socket;
        this.task = task;
    }

    /**
     * Method start.
     */
    public void start() {
        long gid = nextGroupId;
        if (nextGroupId == Long.MAX_VALUE) {
            nextGroupId = 0;
        } else {
            nextGroupId++;
        }

        ThreadGroup group = new ThreadGroup("server-tg-" + gid);
        Thread thread = new Thread(group, this);
        thread.start();
    }

    /**
     * Method getThrown.
     *
     * @return Throwable
     */
    public Throwable getThrown() {
        return thrown;
    }

    /**
     * Method run.
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        InputStream is = null;
        OutputStream os = null;

        try {
            ConnectionBuildListener cbl = null;

            is = socket.getInputStream();
            os = socket.getOutputStream();

            ObjectInputStream ois = new ObjectInputStream(is);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            // Write the initial response object so that the
            // object stream is initialized
            oos.writeObject(new Response());

            boolean disconnect = false;
            Command inputCommand = null;
            Response response = null;

            while (!disconnect) {
                task.getProject().log("Reading command object.",
                        Project.MSG_DEBUG);

                inputCommand = (Command) ois.readObject();

                task.getProject().log("Executing command object: " + inputCommand,
                        Project.MSG_DEBUG);

                response = new Response();

                try {
                    cbl = new ConnectionBuildListener();
                    task.getProject().addBuildListener(cbl);

                    inputCommand.execute(task.getProject(),
                            inputCommand.getContentLength(), is);

                    response.setSucceeded(true);
                } catch (Throwable t) {
                    response.setSucceeded(false);
                    response.setThrowable(t);
                } finally {
                    if (cbl != null) {
                        task.getProject().removeBuildListener(cbl);
                    }
                }

                StringWriter xmlWriter = new StringWriter();
                Transformer idTransform = TransformerFactory.newInstance().newTransformer();
                idTransform.setOutputProperty(OutputKeys.METHOD, "xml");
                idTransform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "false");
                Source input = new DOMSource(cbl.getDocument());
                Result output = new StreamResult(xmlWriter);
                idTransform.transform(input, output);
                response.setResultsXml(xmlWriter.toString());

                task.getProject().log("Executed command object: " + inputCommand,
                        Project.MSG_DEBUG);

                task.getProject().log("Sending response: " + response,
                        Project.MSG_DEBUG);

                response.setContentLength(inputCommand.getContentLength());

                oos.writeObject(response);

                if (inputCommand.getResponseContentLength() != 0) {
                    Util.transferBytes(inputCommand.getResponseContentStream(),
                            inputCommand.getResponseContentLength(),
                            os, true);
                }

                if (inputCommand instanceof DisconnectCommand) {
                    disconnect = true;
                    task.getProject().log("Got disconnect command",
                            Project.MSG_DEBUG);
                } else if (inputCommand instanceof ShutdownCommand) {
                    disconnect = true;
                    task.getProject().log("Got shutdown command",
                            Project.MSG_DEBUG);
                    task.shutdown();
                }
            }
        } catch (ClassNotFoundException e) {
            thrown = e;
        } catch (IOException e) {
            thrown = e;
        } catch (Throwable t) {
            thrown = t;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
