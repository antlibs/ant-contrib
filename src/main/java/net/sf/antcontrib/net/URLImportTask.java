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

/**
 * Task to import a build file from a url.  The build file can be a build.xml,
 * or a .zip/.jar, in which case we download and extract the entire archive, and
 * import the file "build.xml"
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class URLImportTask extends ImportTask {
    /**
     * Field org.
     */
    private String org;

    /**
     * Field module.
     */
    private String module;

    /**
     * Field rev.
     */
    private String rev = "latest.integration";

    /**
     * Field conf.
     */
    private String conf = "default";

    /**
     * Field type.
     * (value is ""jar"")
     */
    private final String type = "jar";

    /**
     * Field repositoryUrl.
     */
    private String repositoryUrl;

    /**
     * Field repositoryDir.
     */
    private File repositoryDir;

    /**
     * Field ivyConfUrl.
     */
    private URL ivyConfUrl;

    /**
     * Field ivyConfFile.
     */
    private File ivyConfFile;

    /**
     * Field resource.
     */
    private String resource = "build.xml";

    /**
     * Field artifactPattern.
     */
    private String artifactPattern = "/[org]/[module]/[ext]s/[module]-[revision].[ext]";

    /**
     * Field ivyPattern.
     */
    private String ivyPattern = "/[org]/[module]/ivy-[revision].xml";

    /**
     * Method setModule.
     *
     * @param module String
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * Method setOrg.
     *
     * @param org String
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * Method setRev.
     *
     * @param rev String
     */
    public void setRev(String rev) {
        this.rev = rev;
    }

    /**
     * Method setConf.
     *
     * @param conf String
     */
    public void setConf(String conf) {
        this.conf = conf;
    }

    /**
     * Method setIvyConfFile.
     *
     * @param ivyConfFile File
     */
    public void setIvyConfFile(File ivyConfFile) {
        this.ivyConfFile = ivyConfFile;
    }

    /**
     * Method setIvyConfUrl.
     *
     * @param ivyConfUrl URL
     */
    public void setIvyConfUrl(URL ivyConfUrl) {
        this.ivyConfUrl = ivyConfUrl;
    }

    /**
     * Method setArtifactPattern.
     *
     * @param artifactPattern String
     */
    public void setArtifactPattern(String artifactPattern) {
        this.artifactPattern = artifactPattern;
    }

    /**
     * Method setIvyPattern.
     *
     * @param ivyPattern String
     */
    public void setIvyPattern(String ivyPattern) {
        this.ivyPattern = ivyPattern;
    }

    /**
     * Method setRepositoryDir.
     *
     * @param repositoryDir File
     */
    public void setRepositoryDir(File repositoryDir) {
        this.repositoryDir = repositoryDir;
    }

    /**
     * Method setRepositoryUrl.
     *
     * @param repositoryUrl String
     */
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    /**
     * Method setResource.
     *
     * @param resource String
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Method setOptional.
     *
     * @param optional boolean
     */
    public void setOptional(boolean optional) {
        throw new BuildException("'optional' property not accessed for ImportURL.");
    }

    /**
     * Method setFile.
     *
     * @param file String
     */
    public void setFile(String file) {
        throw new BuildException("'file' property not accessed for ImportURL.");
    }

    /**
     * Method getOrg.
     *
     * @return String organisation name
     */
    public String getOrg() {
        return org;
    }

    /**
     * Method getModule.
     *
     * @return String module name
     */
    public String getModule() {
        return module;
    }

    /**
     * Method getRev.
     *
     * @return String revision
     */
    public String getRev() {
        return rev;
    }

    /**
     * Method getConf.
     *
     * @return String configuration
     */
    public String getConf() {
        return conf;
    }

    /**
     * Method getType.
     *
     * @return String artifact type
     */
    public String getType() {
        return type;
    }

    /**
     * Method getRepositoryUrl.
     *
     * @return String repository URL
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    /**
     * Method getRepositoryDir.
     *
     * @return String repository directory
     */
    public File getRepositoryDir() {
        return repositoryDir;
    }

    /**
     * Method getIvyConfUrl.
     *
     * @return String Ivy configuration URL
     */
    public URL getIvyConfUrl() {
        return ivyConfUrl;
    }

    /**
     * Method getIvyConfFile.
     *
     * @return String Ivy configuration file
     */
    public File getIvyConfFile() {
        return ivyConfFile;
    }

    /**
     * Method getResource.
     *
     * @return String resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * Method getArtifactPattern.
     *
     * @return String artifact pattern
     */
    public String getArtifactPattern() {
        return artifactPattern;
    }

    /**
     * Method getIvyPattern.
     *
     * @return String descriptor pattern
     */
    public String getIvyPattern() {
        return ivyPattern;
    }

    /**
     * Method execute.
     *
     * @throws BuildException if something goes wrong
     */
    public void execute() throws BuildException {
        IvyAdapter adapter = null;

        try {
            Class.forName("org.apache.ivy.Ivy");
            adapter = new Ivy20Adapter();
        } catch (ClassNotFoundException e) {
            adapter = new Ivy14Adapter();
        }

        String setId = org + "." + module + "." + rev + ".fileset";
        adapter.configure(this);
        adapter.fileset(this, setId);

        FileSet fileset = getProject().getReference(setId);

        DirectoryScanner scanner =
                fileset.getDirectoryScanner(getProject());

        String[] files = scanner.getIncludedFiles();

        File file = new File(scanner.getBasedir(), files[0]);

        File importFile = null;

        if ("xml".equalsIgnoreCase(type)) {
            importFile = file;
        } else if ("jar".equalsIgnoreCase(type)
                || "zip".equalsIgnoreCase(type)) {
            File dir = new File(file.getParentFile(),
                    file.getName() + ".extracted");
            if (!dir.exists()
                    || dir.lastModified() < file.lastModified()) {
                dir.mkdir();
                Expand expand = (Expand) getProject().createTask("unjar");
                expand.setSrc(file);
                expand.setDest(dir);
                expand.perform();
            }
            importFile = new File(dir, resource);
            if (!importFile.exists()) {
                throw new BuildException("Cannot find a '" + resource + "' file in "
                        + file.getName());
            }
        } else {
            throw new BuildException("Don't know what to do with type: " + type);
        }

        log("Importing " + importFile.getName(), Project.MSG_INFO);

        super.setFile(importFile.getAbsolutePath());
        super.execute();

        log("Import complete.", Project.MSG_INFO);
    }
}
