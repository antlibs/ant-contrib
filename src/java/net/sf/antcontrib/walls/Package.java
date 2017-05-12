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
package net.sf.antcontrib.walls;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

/**
 * Created on Aug 24, 2003
 *
 * @author Dean Hiller (dean@xsoftware.biz)
 */
public class Package {

    private String name;
    private String pack;

    //signifies the package did not end with .* or .**
    @SuppressWarnings("unused")
    private boolean badPackage = false;
    private String failureReason = null;

    //holds the name attribute of the package element of each
    //package this package depends on.
    private String[] depends;

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPackage(String pack) {
        this.pack = pack;
    }
    public String getPackage() {
        return pack;
    }

    public void setDepends(String d) {
        if (d == null) {
            throw new RuntimeException("depends cannot be set to null");
        }

        //parse this first.
        StringTokenizer tok = new StringTokenizer(d, ", \t");
        depends = new String[tok.countTokens()];
        int i = 0;
        while (tok.hasMoreTokens()) {
            depends[i] = tok.nextToken();
            i++;
        }
    }

    public String[] getDepends() {
        return depends;
    }

    /**
     * getJavaCopyFileSet() method.
     * @param p Project
     * @param l Location
     * @return FileSet
     * @throws BuildException if something goes wrong
     */
    public FileSet getJavaCopyFileSet(Project p, Location l) throws BuildException {

        if (failureReason != null)
            throw new BuildException(failureReason, l);
        else if (pack.contains("/") || pack.contains("\\"))
            throw new BuildException("A package name cannot contain '\\' or '/' like package="
        	    		+ pack + "\nIt must look like biz.xsoftware.* for example", l);
        FileSet set = new FileSet();

        String match = getMatch(p, pack, ".java");
        //log("match="+match+" pack="+pack);
        //first exclude the compilation module, then exclude all it's
        //dependencies too.
        set.setIncludes(match);

        return set;
    }

    /**
     * getClassCopyFileSet() method.
     * @param p Project
     * @param l Location
     * @return FileSet
     * @throws BuildException if something goes wrong
     */
    public FileSet getClassCopyFileSet(Project p, Location l) throws BuildException {
        FileSet set = new FileSet();
        set.setIncludes("**/*.class");
        return set;
    }

    public File getBuildSpace(File baseDir) {
        return new File(baseDir, name);
    }

    /**
     * getSrcPath() method.
     * @param baseDir File
     * @param p Project
     * @return the source path
     */
    public Path getSrcPath(File baseDir, Project p) {
        Path path = new Path(p);

        path.setLocation(getBuildSpace(baseDir));
        return path;
    }

    /**
     * getClasspath() method.
     * @param baseDir File
     * @param p Project
     * @return the classpath
     */
    public Path getClasspath(File baseDir, Project p) {
        Path path = new Path(p);

            for(int i = 0; i < depends.length; i++) {
                String buildSpace = depends[i];

        if (depends != null) {
                File dependsDir = new File(baseDir, buildSpace);
                path.setLocation(dependsDir);
            }
        }
        return path;
    }

    private String getMatch(Project p, String pack, String postFix) {
        pack = p.replaceProperties(pack);

        pack = pack.replace('.', File.separatorChar);

        String match;
        @SuppressWarnings("unused")
        String classMatch;
        if (pack.endsWith("**")) {
            match  = pack + File.separatorChar + "*" + postFix;
        }
        else if (pack.endsWith("*")) {
            match  = pack + postFix;
        }
        else
            throw new RuntimeException("Please report this bug");

        return match;
    }

    /**
     * setFaultReason() method.
     * @param r a fault reason string
     */
    public void setFaultReason(String r) {
        failureReason = r;
    }
}
