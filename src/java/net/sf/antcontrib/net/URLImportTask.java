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
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.ImportTask;
import org.apache.tools.ant.types.FileSet;

import fr.jayasoft.ivy.ant.IvyCacheFileset;
import fr.jayasoft.ivy.ant.IvyConfigure;
import fr.jayasoft.ivy.ant.IvyResolve;

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
	private String repositoryDir;
	private URL ivyConfUrl;
	private File ivyConfFile;
	private String resource = "build.xml";
	private String artifactPattern = "/[org]/[module]/[ext]s/[module]-[revision].[ext]";
	private String ivyPattern = "/[org]/[module]/ivy-[revision].xml";
	private boolean verbose = false;
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
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

	public void setRepositoryDir(String repositoryDir) {
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

	public void execute()
		throws BuildException {
		
		IvyConfigure configure = new IvyConfigure();
		configure.setProject(getProject());
		configure.setLocation(getLocation());
		configure.setOwningTarget(getOwningTarget());
		configure.setTaskName(getTaskName());
		configure.init();
		if (ivyConfUrl != null) {
			if (ivyConfUrl.getProtocol().equalsIgnoreCase("file")) {
				configure.setFile(new File(ivyConfUrl.getPath()));
			}
			else {
				try {
					configure.setUrl(ivyConfUrl.toExternalForm());
				}
				catch (MalformedURLException e) {
					throw new BuildException(e);
				}
			}
		}
		else if (ivyConfFile != null) {
			configure.setFile(ivyConfFile);
		}
		else if (repositoryDir != null ||
				 repositoryUrl != null) {
			File temp = null;
			FileWriter fw = null;
			
			try {
				temp = File.createTempFile("ivyconf", ".xml");
				temp.deleteOnExit();
				fw = new FileWriter(temp);
				fw.write("<ivyconf>");
				fw.write("<conf defaultResolver=\"default\" />");
				fw.write("<resolvers>");
				if (repositoryDir != null) {
					fw.write("<filesystem name=\"default\">");
					fw.write("<ivy pattern=\"" + repositoryDir + "/" + ivyPattern + "\"  />");
					fw.write("<artifact pattern=\"" + repositoryDir + "/" + artifactPattern + "\"  />");
					fw.write("</filesystem>");
				}
				else {
					fw.write("<url name=\"default\">");
					fw.write("<ivy pattern=\"" + repositoryUrl + "/" + ivyPattern + "\"  />");
					fw.write("<artifact pattern=\"" + repositoryUrl + "/" + artifactPattern + "\"  />");
					fw.write("</url>");
				}
				fw.write("</resolvers>");
	
				fw.write("<latest-strategies>");
				fw.write("<latest-revision name=\"latest\"/>");
				fw.write("</latest-strategies>");
				fw.write("</ivyconf>");
				fw.close();
				fw = null;
				
				configure.setFile(temp);
			}
			catch (IOException e) {
				throw new BuildException(e);
			}
			finally {
				try {
					if (fw != null) {
						fw.close();
						fw = null;
					}
				}
				catch (IOException e) {
					;
				}
			}
		}
		
		configure.execute();

		IvyCacheFileset cacheFileSet = new IvyCacheFileset();
		cacheFileSet.setProject(getProject());
		cacheFileSet.setLocation(getLocation());
		cacheFileSet.setOwningTarget(getOwningTarget());
		cacheFileSet.setTaskName(getTaskName());
		cacheFileSet.setInline(true);
		cacheFileSet.setOrganisation(org);
		cacheFileSet.setModule(module);
		cacheFileSet.setRevision(rev);
		cacheFileSet.setConf(conf);
		cacheFileSet.init();
		cacheFileSet.setSetid(org + module + rev + ".fileset");
		cacheFileSet.execute();
		
		FileSet fileset =
			(FileSet) getProject().getReference(org + module + rev + ".fileset");
		
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
