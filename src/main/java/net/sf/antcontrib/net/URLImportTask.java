/*
 * Copyright (c) 2006 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.net;

import java.io.File;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.ImportTask;
import org.apache.tools.ant.types.FileSet;

/***
 * Task to import a build file from a url.  The build file can be a build.xml,
 * or a .zip/.jar, in which case we download and extract the entire archive, and
 * import the file "build.xml"
 * @author inger
 *
 */
public class URLImportTask
	extends ImportTask {

	private String org;
	private String module;
	private String rev = "latest.integration";
	private String conf = "default";
	private String type = "jar";
	private String repositoryUrl;
	private File repositoryDir;
	private URL ivyConfUrl;
	private File ivyConfFile;
	private String resource = "build.xml";
	private String artifactPattern = "/[org]/[module]/[ext]s/[module]-[revision].[ext]";
	private String ivyPattern = "/[org]/[module]/ivy-[revision].xml";
	
	public void setModule(String module) {
		this.module = module;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}
	
	public void setIvyConfFile(File ivyConfFile) {
		this.ivyConfFile = ivyConfFile;
	}

	public void setIvyConfUrl(URL ivyConfUrl) {
		this.ivyConfUrl = ivyConfUrl;
	}

	public void setArtifactPattern(String artifactPattern) {
		this.artifactPattern = artifactPattern;
	}

	public void setIvyPattern(String ivyPattern) {
		this.ivyPattern = ivyPattern;
	}

	public void setRepositoryDir(File repositoryDir) {
		this.repositoryDir = repositoryDir;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
	public void setOptional(boolean optional) {
		throw new BuildException("'optional' property not accessed for ImportURL.");
	}

	public void setFile(String file) {
		throw new BuildException("'file' property not accessed for ImportURL.");
	}

	public String getOrg() {
		return org;
	}

	public String getModule() {
		return module;
	}

	public String getRev() {
		return rev;
	}

	public String getConf() {
		return conf;
	}

	public String getType() {
		return type;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public File getRepositoryDir() {
		return repositoryDir;
	}

	public URL getIvyConfUrl() {
		return ivyConfUrl;
	}

	public File getIvyConfFile() {
		return ivyConfFile;
	}

	public String getResource() {
		return resource;
	}

	public String getArtifactPattern() {
		return artifactPattern;
	}

	public String getIvyPattern() {
		return ivyPattern;
	}

	public void execute()
		throws BuildException {
		
		IvyAdapter adapter = null;
		
		try {
			Class.forName("org.apache.ivy.Ivy");
			adapter = new Ivy20Adapter();
		}
		catch (ClassNotFoundException e) {
			adapter = new Ivy14Adapter();
		}
		
		String setId = org + "." + module + "." + rev + ".fileset";
		adapter.configure(this);
		adapter.fileset(this, setId);
		
		FileSet fileset =(FileSet) getProject().getReference(setId);
		
		DirectoryScanner scanner =
			fileset.getDirectoryScanner(getProject());
		
		String files[] = scanner.getIncludedFiles();
		
		File file = new File(scanner.getBasedir(), files[0]);

		File importFile = null;
		
	    if ("xml".equalsIgnoreCase(type)) {
	    	importFile = file;
	    }
	    else if ("jar".equalsIgnoreCase(type) ||
	    		"zip".equalsIgnoreCase(type)) {
	    	File dir = new File(file.getParentFile(),
	    			file.getName() + ".extracted");
	    	if (! dir.exists() ||
	    			dir.lastModified() < file.lastModified()) {
	    		dir.mkdir();
	    		Expand expand = (Expand)getProject().createTask("unjar");
	    		expand.setSrc(file);
	    		expand.setDest(dir);
	    		expand.perform();
	    	}
	    	importFile = new File(dir, resource);
	    	if (! importFile.exists()) {
	    		throw new BuildException("Cannot find a '" + resource + "' file in " +
	    				file.getName());
	    	}
	    }
	    else {
	    	throw new BuildException("Don't know what to do with type: " + type);
	    }
		
	    log("Importing " + importFile.getName(), Project.MSG_INFO);
	    
	    super.setFile(importFile.getAbsolutePath());
	    super.execute();

	    log("Import complete.", Project.MSG_INFO);
	}
}
