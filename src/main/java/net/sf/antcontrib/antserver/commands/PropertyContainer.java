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

import java.io.Serializable;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
@SuppressWarnings("serial")
public class PropertyContainer implements Serializable {
    /**
     * Field name.
     */
    private String name;

    /**
     * Field value.
     */
    private String value;

    /**
     * Constructor for PropertyContainer.
     */
    public PropertyContainer() {
        super();
    }

    /**
     * Method getName.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Method setName.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method getValue.
     *
     * @return String
     */
    public String getValue() {
        return value;
    }

    /**
     * Method setValue.
     *
     * @param value String
     */
    public void setValue(String value) {
        this.value = value;
    }
}
