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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created on Aug 24, 2003.
 *
 * @author <a href="mailto:dean@xsoftware.biz">Dean Hiller</a>
 */
public class Package {
    /**
     * Field DEFAULT.
     * (value is ""default package"")
     */
    public static final String DEFAULT = "default package";

    /**
     * Field name.
     */
    private String name;

    /**
     * Field pack.
     */
    private String pack;

    /**
     * Field depends.
     * Holds the name attribute of the package element of each
     * package this package depends on.
     */
    private List<Depends> depends;

    /**
     * Field unusedDepends.
     */
    private final Set<Depends> unusedDepends = new HashSet<Depends>();

    /**
     * Field isIncludeSubpackages.
     */
    private boolean isIncludeSubpackages;

    /**
     * Field needDeclarations.
     */
    private boolean needDeclarations;

    /**
     * Field needDepends.
     */
    private boolean needDepends;

    /**
     * Field isUsed.
     */
    private boolean isUsed = false;

    /**
     * Method setName.
     *
     * @param name String
     */
    public void setName(String name) {
        if ("".equals(name)) {
            name = DEFAULT;
        }
        this.name = name;
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
     * Method setPackage.
     *
     * @param pack String
     */
    public void setPackage(String pack) {
        this.pack = pack;
    }

    /**
     * Method getPackage.
     *
     * @return String
     */
    public String getPackage() {
        return pack;
    }

    /**
     * Method addDepends.
     *
     * @param d Depends
     */
    public void addDepends(Depends d) {
        if (depends == null) {
            depends = new ArrayList<Depends>();
        }
        depends.add(d);
        unusedDepends.add(d);
    }

    /**
     * Method getDepends.
     *
     * @return Depends[]
     */
    public Depends[] getDepends() {
        Depends[] d = new Depends[0];
        return depends == null ? d : depends.toArray(d);
    }

    /**
     * setIncludeSubpackages() method.
     *
     * @param b boolean
     */
    public void setIncludeSubpackages(boolean b) {
        isIncludeSubpackages = b;
    }

    /**
     * isIncludeSubpackages() method.
     *
     * @return boolean
     */
    public boolean isIncludeSubpackages() {
        return isIncludeSubpackages;
    }

    /**
     * setNeedDeclarations() method.
     *
     * @param b boolean
     */
    public void setNeedDeclarations(boolean b) {
        needDeclarations = b;
    }

    /**
     * isNeedDeclarations() method.
     *
     * @return boolean
     */
    public boolean isNeedDeclarations() {
        return needDeclarations;
    }

    /**
     * setNeedDepends() method.
     *
     * @param b boolean
     */
    public void setNeedDepends(boolean b) {
        needDepends = b;
    }

    /**
     * Method getNeedDepends.
     *
     * @return boolean
     */
    public boolean getNeedDepends() {
        return needDepends;
    }

    /**
     * setUsed() method.
     *
     * @param b boolean
     */
    public void setUsed(boolean b) {
        isUsed = b;
    }

    /**
     * Method isUsed.
     *
     * @return boolean
     */
    public boolean isUsed() {
        return isUsed;
    }

    /**
     * addUsedDependency() method.
     *
     * @param d Depends
     */
    public void addUsedDependency(Depends d) {
        unusedDepends.remove(d);
    }

    /**
     * Method getUnusedDepends.
     *
     * @return Set&lt;Depends&gt;
     */
    public Set<Depends> getUnusedDepends() {
        return unusedDepends;
    }
}
