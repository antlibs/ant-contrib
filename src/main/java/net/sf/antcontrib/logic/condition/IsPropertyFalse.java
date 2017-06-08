/*
 * Copyright (c) 2001-2004, 2007 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.logic.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;

import static org.apache.tools.ant.Project.toBoolean;

/**
 * Checks the value of a specified property.
 * <p>Developed for use with Antelope, migrated to ant-contrib Oct 2003.</p>
 *
 * @author <a href="mailto:danson@germane-software.com">Dale Anson</a>
 * @version $Revision: 1.3 $
 */
public final class IsPropertyFalse extends ProjectComponent implements Condition {
    /**
     * Field name.
     */
    private String name = null;

    /**
     * Method setProperty.
     *
     * @param name String
     */
    public void setProperty(String name) {
        this.name = name;
    }

    /**
     * Method eval.
     *
     * @return boolean
     * @throws BuildException if property name is not specified
     * @see org.apache.tools.ant.taskdefs.condition.Condition#eval()
     */
    public boolean eval() throws BuildException {
        if (name == null) {
            throw new BuildException("Property name must be set.");
        }
        String value = getProject().getProperty(name);
        return value == null || !toBoolean(value);
    }
}
