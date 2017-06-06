package net.sf.antcontrib.net;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tools.ant.BuildException;

import fr.jayasoft.ivy.ant.IvyCacheFileset;
import fr.jayasoft.ivy.ant.IvyConfigure;

public class Ivy14Adapter implements IvyAdapter {

    public void configure(URLImportTask task) {
        IvyConfigure configure = new IvyConfigure();
        configure.setProject(task.getProject());
        configure.setLocation(task.getLocation());
        configure.setOwningTarget(task.getOwningTarget());
        configure.setTaskName(task.getTaskName());
        configure.init();

        URL ivyConfUrl = task.getIvyConfUrl();
        File ivyConfFile = task.getIvyConfFile();
        String repositoryUrl = task.getRepositoryUrl();
        File repositoryDir = task.getRepositoryDir();
        String ivyPattern = task.getIvyPattern();
        String artifactPattern = task.getArtifactPattern();

        if (ivyConfUrl != null) {
            if (ivyConfUrl.getProtocol().equalsIgnoreCase("file")) {
                String path = ivyConfUrl.getPath();
                File f = new File(path);
                if (! f.isAbsolute()) {
                    f = new File(task.getProject().getBaseDir(), path);
                }
                configure.setFile(f);
            } else {
                try {
                    configure.setUrl(ivyConfUrl.toExternalForm());
                } catch (MalformedURLException e) {
                    throw new BuildException(e);
                }
            }
        } else if (ivyConfFile != null) {
            configure.setFile(ivyConfFile);
        } else if (repositoryDir != null
                || repositoryUrl != null) {
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
                } else {
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
            } catch (IOException e) {
                throw new BuildException(e);
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                        fw = null;
                    }
                } catch (IOException e) {
                    ;
                }
            }
        }

        configure.execute();
    }

    public void fileset(URLImportTask task, String setId) {
        String org = task.getOrg();
        String module = task.getModule();
        String rev = task.getRev();
        String conf = task.getConf();

        IvyCacheFileset cacheFileSet = new IvyCacheFileset();
        cacheFileSet.setProject(task.getProject());
        cacheFileSet.setLocation(task.getLocation());
        cacheFileSet.setOwningTarget(task.getOwningTarget());
        cacheFileSet.setTaskName(task.getTaskName());
        cacheFileSet.setInline(true);
        cacheFileSet.setOrganisation(org);
        cacheFileSet.setModule(module);
        cacheFileSet.setRevision(rev);
        cacheFileSet.setConf(conf);
        cacheFileSet.init();
        cacheFileSet.setSetid(setId);
        cacheFileSet.execute();
    }

}
