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

import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import net.sf.antcontrib.antserver.Command;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
@SuppressWarnings("serial")
public class HelloWorldCommand extends AbstractCommand implements Command {
    /**
     * Method validate.
     *
     * @param project Project
     * @see net.sf.antcontrib.antserver.Command#validate(Project)
     */
    public void validate(Project project) {
    }

    /**
     * Method execute.
     *
     * @param project       Project
     * @param contentLength long
     * @param content       InputStream
     * @return boolean
     * @throws BuildException if something goes wrong
     * @see net.sf.antcontrib.antserver.Command#execute(Project, long, InputStream)
     */
    public boolean execute(Project project,
                           long contentLength,
                           InputStream content)
            throws BuildException {
        project.log("Hello World", Project.MSG_ERR);
        return false;
    }
}
