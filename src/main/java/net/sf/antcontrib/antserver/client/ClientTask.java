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
package net.sf.antcontrib.antserver.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.antcontrib.antserver.Command;
import net.sf.antcontrib.antserver.Response;
import net.sf.antcontrib.antserver.commands.RunAntCommand;
import net.sf.antcontrib.antserver.commands.RunTargetCommand;
import net.sf.antcontrib.antserver.commands.SendFileCommand;
import net.sf.antcontrib.antserver.commands.ShutdownCommand;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class ClientTask extends Task {
    /**
     * Field machine.
     */
    private String machine = "localhost";

    /**
     * Field port.
     */
    private int port = 17000;

    /**
     * Field commands.
     */
    private final List<Command> commands;

    /**
     * Field persistent.
     */
    private boolean persistent = false;

    /**
     * Field failOnError.
     */
    private boolean failOnError = true;

    /**
     * Constructor for ClientTask.
     */
    public ClientTask() {
        super();
        this.commands = new ArrayList<Command>();
    }

    /**
     * Method setMachine.
     *
     * @param machine String
     */
    public void setMachine(String machine) {
        this.machine = machine;
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
     * Method setPersistent.
     *
     * @param persistent boolean
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    /**
     * Method setFailOnError.
     *
     * @param failOnError boolean
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * Method addConfiguredShutdown.
     *
     * @param cmd ShutdownCommand
     */
    public void addConfiguredShutdown(ShutdownCommand cmd) {
        commands.add(cmd);
    }

    /**
     * Method addConfiguredRunTarget.
     *
     * @param cmd RunTargetCommand
     */
    public void addConfiguredRunTarget(RunTargetCommand cmd) {
        commands.add(cmd);
    }

    /**
     * Method addConfiguredRunAnt.
     *
     * @param cmd RunAntCommand
     */
    public void addConfiguredRunAnt(RunAntCommand cmd) {
        commands.add(cmd);
    }

    /**
     * Method addConfiguredSendFile.
     *
     * @param cmd SendFileCommand
     */
    public void addConfiguredSendFile(SendFileCommand cmd) {
        commands.add(cmd);
    }

    /**
     * Method execute.
     */
    public void execute() {
        for (Command c : commands) {
            c.validate(getProject());
        }

        Client client = new Client(getProject(), machine, port);

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            try {
                int failCount = 0;

                client.connect();

                Response r = null;
                Document d = null;
                boolean keepGoing = true;
                for (Command c : commands) {
                    r = client.sendCommand(c);
                    if (!r.isSucceeded()) {
                        failCount++;
                        log("Command caused a build failure:" + c,
                                Project.MSG_ERR);
                        log(r.getErrorMessage(), Project.MSG_ERR);
                        log(r.getErrorStackTrace(), Project.MSG_DEBUG);
                        if (!persistent) {
                            keepGoing = false;
                        }
                    }

                    try {
                        ByteArrayInputStream bais =
                                new ByteArrayInputStream(r.getResultsXml().getBytes());
                        d = db.parse(bais);
                        NodeList nl = d.getElementsByTagName("target");
                        int len = nl.getLength();
                        Element element = null;
                        for (int i = 0; i < len; i++) {
                            element = (Element) nl.item(i);
                            getProject().log("[" + element.getAttribute("name") + "]",
                                    Project.MSG_INFO);
                        }
                    } catch (SAXException se) {
                    }

                    if (c instanceof ShutdownCommand) {
                        client.shutdown();
                    }

                    if (!keepGoing) {
                        break;
                    }
                }

                if (failCount > 0 && failOnError) {
                    throw new BuildException("One or more commands failed.");
                }
            } finally {
                if (client != null) {
                    client.disconnect();
                }
            }
        } catch (ParserConfigurationException ex) {
            throw new BuildException(ex);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }
}
