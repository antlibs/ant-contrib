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
package net.sf.antcontrib.net.ant16;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.ImportTask;

import fr.jayasoft.ivy.Artifact;
import fr.jayasoft.ivy.DependencyResolver;
import fr.jayasoft.ivy.Ivy;
import fr.jayasoft.ivy.IvyContext;
import fr.jayasoft.ivy.ModuleDescriptor;
import fr.jayasoft.ivy.ModuleId;
import fr.jayasoft.ivy.ModuleRevisionId;
import fr.jayasoft.ivy.filter.FilterHelper;
import fr.jayasoft.ivy.report.ResolveReport;
import fr.jayasoft.ivy.repository.Repository;
import fr.jayasoft.ivy.resolver.FileSystemResolver;
import fr.jayasoft.ivy.resolver.IvyRepResolver;
import fr.jayasoft.ivy.resolver.URLResolver;
import fr.jayasoft.ivy.util.MessageImpl;

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
		
		MessageImpl oldMsgImpl = IvyContext.getContext().getMessageImpl();
		
		if (! verbose) {
			IvyContext.getContext().setMessageImpl(
					new MessageImpl() {

						public void endProgress(String arg0) {
							// TODO Auto-generated method stub

						}

						public void log(String arg0, int arg1) {
							// TODO Auto-generated method stub

						}

						public void progress() {
							// TODO Auto-generated method stub

						}

						public void rawlog(String arg0, int arg1) {
						}
					}
			);
		}
		Ivy ivy = new Ivy();
		DependencyResolver resolver = null;
		Repository rep = null;
		
		
		if (repositoryUrl != null) {
			resolver = new URLResolver();
			((URLResolver)resolver).addArtifactPattern(
					repositoryUrl + "/" + artifactPattern
					);
			((URLResolver)resolver).addIvyPattern(
					repositoryUrl + "/" + ivyPattern
					);
			resolver.setName("default");
		}
		else if (repositoryDir != null) {
			resolver = new FileSystemResolver();
			((FileSystemResolver)resolver).addArtifactPattern(
					repositoryDir + "/" + artifactPattern
					);
			((FileSystemResolver)resolver).addIvyPattern(
					repositoryDir + "/" + ivyPattern
					);
		}
		else if (ivyConfUrl != null) {
			try {
				ivy.configure(ivyConfUrl);
                                resolver = ivy.getDefaultResolver();
			}
			catch (IOException e) {
				throw new BuildException(e);
			}
			catch (ParseException e) {
				throw new BuildException(e);
			}
		}
		else if (ivyConfFile != null) {
			try {
				ivy.configure(ivyConfFile);
			}
			catch (IOException e) {
				throw new BuildException(e);
			}
			catch (ParseException e) {
				throw new BuildException(e);
			}
		}
		else {
			resolver = new IvyRepResolver();
		}
		resolver.setName("default");
		ivy.addResolver(resolver);
		ivy.setDefaultResolver(resolver.getName());
		
		
		try {
		ModuleId moduleId =
			new ModuleId(org, module);		
		ModuleRevisionId revId =
			new ModuleRevisionId(moduleId, rev);
		
		ResolveReport resolveReport = ivy.resolve(
                ModuleRevisionId.newInstance(org, module, rev),
            new String[] { "*" },
            false,
            true,
            ivy.getDefaultCache(),
            new Date(),
            ivy.doValidate(),
            false,
            false,
            FilterHelper.getArtifactTypeFilter(type));
		
		if (resolveReport.hasError()) {
			throw new BuildException("Could not resolve resource for: " +
					"org=" + org +
					";module=" + module +
					";rev=" + rev);
		}

		ModuleDescriptor desc = resolveReport.getModuleDescriptor();
		List artifacts = resolveReport.getArtifacts();
		Artifact artifact = (Artifact) artifacts.get(0);
		log("Fetched " +
				artifact.getModuleRevisionId().getOrganisation() + " | " +
				artifact.getModuleRevisionId().getName() + " | " +
				artifact.getModuleRevisionId().getRevision());
		File file = ivy.getArchiveFileInCache(ivy.getDefaultCache(), artifact);
				
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
		
	    super.setFile(importFile.getAbsolutePath());
	    super.execute();
	    log("Import complete.", Project.MSG_INFO);
		}
		catch (ParseException e) {
			throw new BuildException(e);
		}
		catch (IOException e) {
			throw new BuildException(e);
		}
		finally {
			IvyContext.getContext().setMessageImpl(oldMsgImpl);
		}
	}
}
