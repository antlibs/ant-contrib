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
package net.sf.antcontrib.property;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class URLEncodeTask extends AbstractPropertySetterTask {
    /**
     * Field value.
     */
    private String value;

    /**
     * Field ref.
     */
    private Reference ref;

    /**
     * Method setName.
     *
     * @param name String
     */
    public void setName(String name) {
        setProperty(name);
    }

    /**
     * Method setValue.
     *
     * @param value String
     */
    public void setValue(String value) {
        try {
            this.value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method getValue.
     *
     * @param p Project
     * @return String
     */
    public String getValue(Project p) {
        String val = value;

        if (ref != null)
            val = ref.getReferencedObject(p).toString();

        return val;
    }

    /**
     * Method setLocation.
     *
     * @param location File
     */
    public void setLocation(File location) {
        setValue(location.getAbsolutePath());
    }

    /**
     * Method setRefid.
     *
     * @param ref Reference
     */
    public void setRefid(Reference ref) {
        this.ref = ref;
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return value == null ? "" : value;
    }

    /**
     * Method validate.
     */
    protected void validate() {
        super.validate();
        if (value == null && ref == null) {
            throw new BuildException("You must specify value, location or "
                    + "refid with the name attribute",
                    getLocation());
        }
    }

    /**
     * Method execute.
     */
    public void execute() {
        validate();
        String val = getValue(getProject());
        setPropertyValue(val);
    }
}
