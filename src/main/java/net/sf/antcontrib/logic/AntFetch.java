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
package net.sf.antcontrib.logic;

import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;

/**
 * Subclass of CallTarget which allows us to fetch
 * properties which are set in the scope of the called
 * target, and set them in the scope of the calling target.
 * Normally, these properties are thrown away as soon as the
 * called target completes execution.
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 * @author <a href="mailto:danson@germane-software.com">Dale Anson</a>
 */
public class AntFetch extends Ant {
    /**
     * the name of the property to fetch from the new project.
     */
    private String returnName = null;

    /**
     * Field fakeProject.
     */
    private ProjectDelegate fakeProject = null;

    /**
     * Method setProject.
     *
     * @param realProject Project
     */
    public void setProject(Project realProject) {
        fakeProject = new ProjectDelegate(realProject);
        super.setProject(fakeProject);
    }

    /**
     * Do the execution.
     *
     * @throws BuildException Description of the Exception
     */
    public void execute() throws BuildException {
        super.execute();

        // copy back the props if possible
        if (returnName != null) {
            StringTokenizer st = new StringTokenizer(returnName, ",");
            while (st.hasMoreTokens()) {
                String name = st.nextToken().trim();
                String value = fakeProject.getSubproject().getUserProperty(name);
                if (value != null) {
                    getProject().setUserProperty(name, value);
                } else {
                    value = fakeProject.getSubproject().getProperty(name);
                    if (value != null) {
                        getProject().setProperty(name, value);
                    }
                }
            }
        }
    }

    /**
     * Set the property or properties that are set in the new project to be
     * transferred back to the original project. As with all properties, if the
     * property already exists in the original project, it will not be
     * overridden by a different value from the new project.
     *
     * @param r the name of a property in the new project to set in the original
     *          project. This may be a comma separate list of properties.
     */
    public void setReturn(String r) {
        returnName = r;
    }
}
