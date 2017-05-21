/*
 * Copyright (c) 2004-2005 Ant-Contrib project.  All rights reserved.
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

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * @author <a href="mailto:dean@xsoftware.biz">Dean Hiller</a>
 */
public class VerifyDesign extends Task implements Log {

    /**
     * Field delegate.
     */
    private final VerifyDesignDelegate delegate;

    /**
     * Constructor for VerifyDesign.
     */
    public VerifyDesign() {
        delegate = new VerifyDesignDelegate(this);
    }

    /**
     * Method setJar.
     *
     * @param f File
     */
    public void setJar(File f) {
        delegate.setJar(f);
    }

    /**
     * Method setDesign.
     *
     * @param f File
     */
    public void setDesign(File f) {
        delegate.setDesign(f);
    }

    /**
     * Method setCircularDesign.
     *
     * @param isCircularDesign boolean
     */
    public void setCircularDesign(boolean isCircularDesign) {
        delegate.setCircularDesign(isCircularDesign);
    }

    /**
     * Method setDeleteFiles.
     *
     * @param deleteFiles boolean
     */
    public void setDeleteFiles(boolean deleteFiles) {
        delegate.setDeleteFiles(deleteFiles);
    }

    /**
     * Method setFillInBuildException.
     *
     * @param b boolean
     */
    public void setFillInBuildException(boolean b) {
        delegate.setFillInBuildException(b);
    }

    /**
     * Method setNeedDeclarationsDefault.
     *
     * @param b boolean
     */
    public void setNeedDeclarationsDefault(boolean b) {
        delegate.setNeedDeclarationsDefault(b);
    }

    /**
     * Method setNeedDependsDefault.
     *
     * @param b boolean
     */
    public void setNeedDependsDefault(boolean b) {
        delegate.setNeedDependsDefault(b);
    }

    /**
     * Method addConfiguredPath.
     *
     * @param path Path
     */
    public void addConfiguredPath(Path path) {
        delegate.addConfiguredPath(path);
    }

    /**
     * Method execute.
     *
     * @throws BuildException if parsing of design file fails
     */
    public void execute() throws BuildException {
        delegate.execute();
    }
}
