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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.selectors.OrSelector;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class PathFilterTask extends Task {
    /**
     * Field select.
     */
    private OrSelector select;

    /**
     * Field path.
     */
    private Path path;

    /**
     * Field pathid.
     */
    private String pathid;

    /**
     * Method setPathId.
     *
     * @param pathid String
     */
    public void setPathId(String pathid) {
        this.pathid = pathid;
    }

    /**
     * Method createSelect.
     *
     * @return OrSelector
     */
    public OrSelector createSelect() {
        select = new OrSelector();
        return select;
    }

    /**
     * Method addConfiguredFileSet.
     *
     * @param fileset FileSet
     */
    public void addConfiguredFileSet(FileSet fileset) {
        if (this.path == null) {
            this.path = (Path) getProject().createDataType("path");
        }
        this.path.addFileset(fileset);
    }

    /**
     * Method addConfiguredDirSet.
     *
     * @param dirset DirSet
     */
    public void addConfiguredDirSet(DirSet dirset) {
        if (this.path == null) {
            this.path = (Path) getProject().createDataType("path");
        }
        this.path.addDirset(dirset);
    }

    /**
     * Method addConfiguredFileList.
     *
     * @param filelist FileList
     */
    public void addConfiguredFileList(FileList filelist) {
        if (this.path == null) {
            this.path = (Path) getProject().createDataType("path");
        }
        this.path.addFilelist(filelist);
    }

    /**
     * Method addConfiguredPath.
     *
     * @param path Path
     */
    public void addConfiguredPath(Path path) {
        if (this.path == null) {
            this.path = (Path) getProject().createDataType("path");
        }
        this.path.add(path);
    }

    /**
     * Method execute.
     *
     * @throws BuildException if something goes wrong
     */
    public void execute() throws BuildException {
        if (select == null) {
            throw new BuildException("A <select> element must be specified.");
        }

        if (pathid == null) {
            throw new BuildException("A 'pathid' attribute must be specified.");
        }

        Path selectedFiles = (Path) getProject().createDataType("path");

        if (this.path != null) {
            String[] files = this.path.list();
            for (String fileName : files) {
                File file = new File(fileName);
                if (select.isSelected(file.getParentFile(),
                        file.getName(),
                        file)) {
                    selectedFiles.createPathElement().setLocation(file);
                }
            }

            getProject().addReference(pathid, selectedFiles);
        }
    }
}
