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

/***
 * 
 * @author minger
 * @ant.task name="pathfilter" category="property"
 *
 */
public class PathFilterTask
	extends Task {

	private OrSelector select;	
    private Path    path;
	private String  pathid;
	
	
	/***
	 * Set the pathid which we will set with the results
	 * @param pathid
	 */
	public void setPathId(String pathid) {
		this.pathid = pathid;
	}
	
	/***
	 * Create a selector.  The specified selectors will be ORed together.
	 * @return
	 */
	public OrSelector createSelect() {
		select = new OrSelector();
		return select;
	}
	
	/***
	 * Adds a fileset
	 * @param fileset
	 */
	public void addConfiguredFileSet(FileSet fileset) {
		if (this.path == null) {
			this.path = (Path)getProject().createDataType("path");
		}
		this.path.addFileset(fileset);
	}
	
	/***
	 * Adds a DirSet
	 * @param dirset
	 */
	public void addConfiguredDirSet(DirSet dirset) {
		if (this.path == null) {
			this.path = (Path)getProject().createDataType("path");
		}
		this.path.addDirset(dirset);
	}

	/***
	 * Adds a FileList
	 * @param filelist
	 */
	public void addConfiguredFileList(FileList filelist) {
		if (this.path == null) {
			this.path = (Path)getProject().createDataType("path");
		}
		this.path.addFilelist(filelist);
	}

	/***
	 * Adds a Path
	 * @param path
	 */
	public void addConfiguredPath(Path path) {
		if (this.path == null) {
			this.path = (Path)getProject().createDataType("path");
		}
		this.path.addExisting(path);
	}

	
	public void execute() throws BuildException {
		if (select == null) {
			throw new BuildException("A <select> element must be specified.");
		}
		
		if (pathid == null) {
			throw new BuildException("A 'pathid' attribute must be specified.");
		}
		
		Path selectedFiles = (Path)getProject().createDataType("path");
		
		if (this.path != null) {
			String files[] = this.path.list();
			for (int i=0;i<files.length;i++) {
				File file = new File(files[i]);			
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
