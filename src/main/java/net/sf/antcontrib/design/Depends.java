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
package net.sf.antcontrib.design;

/**
 * Created on Dec 24, 2004.
 *
 * @author <a href="mailto:dean@xsoftware.biz">Dean Hiller</a>
 */
public class Depends {
    /**
     * Field name.
     */
    private String name;

    /**
     * Constructor for Depends.
     */
    public Depends() {
    }

    /**
     * Constructor.
     *
     * @param name String
     */
    public Depends(String name) {
        super();
        this.name = name;
    }

    /**
     * setName() method.
     *
     * @param s String
     */
    public void setName(String s) {
        this.name = s;
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
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return name;
    }
}
