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
package net.sf.antcontrib.platform;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Task definition for the <code>OsFamily</code> task.
 * This task sets the property indicated in the "property"
 * attribute with the string representing the operating
 * system family.  Possible values include "unix", "dos", "mac"
 * and "windows".
 * <pre>
 *
 * Task Declaration:
 *
 * <code>
 *   &lt;taskdef name="osfamily" classname="net.sf.antcontrib.platform.OsFamily" /&gt;
 * </code>
 *
 * Usage:
 * <code>
 *   &lt;osfamily property="propname" /&gt;
 * </code>
 *
 * Attributes:
 *   property --&gt; The name of the property to set with the OS family name
 *
 * </pre>
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class OsFamily extends Task {
    /**
     * Field property.
     */
    private String property;

    /**
     * Constructor for OsFamily.
     */
    public OsFamily() {
    }

    /**
     * Method setProperty.
     *
     * @param property String
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Method execute.
     *
     * @throws BuildException if something goes wrong
     */
    public void execute()
            throws BuildException {
        if (property == null) {
            throw new BuildException("The attribute 'property' is required "
                    + "for the OsFamily task.");
        }

        String familyStr = Platform.getOsFamilyName();
        if (familyStr != null) {
            getProject().setProperty(property, familyStr);
        }
    }
}
