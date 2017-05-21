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
package net.sf.antcontrib.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;

/**
 * Task to help in calling tasks if generated files are older
 * than source files.
 * Sets a given property or runs an internal task.
 * <p>Based on {@link
 * org.apache.tools.ant.taskdefs.UpToDate UpToDate} task</p>
 *
 * @author <a href="mailto:peterreilly@users.sf.net">Peter Reilly</a>
 */
public class OutOfDate extends Task implements Condition {
    /**
     * Enumerated type for collection attribute.
     *
     * @see EnumeratedAttribute
     */
    public static class CollectionEnum extends EnumeratedAttribute {
        /**
         * Constants for the enumerations.
         */
        public static final int SOURCES = 0;

        /**
         * Field TARGETS.
         * (value is 1)
         */
        public static final int TARGETS = 1;

        /**
         * Field ALLSOURCES.
         * (value is 2)
         */
        public static final int ALLSOURCES = 2;

        /**
         * Field ALLTARGETS.
         * (value is 3)
         */
        public static final int ALLTARGETS = 3;

        /**
         * get the values.
         *
         * @return an array of the allowed values for this attribute.
         */
        public String[] getValues() {
            return new String[]{"sources", "targets", "allsources", "alltargets"};
        }
    }

    // attributes and nested elements
    /**
     * Field doTask.
     */
    private Task doTask = null;

    /**
     * Field property.
     */
    private String property;

    /**
     * Field value.
     */
    private String value = "true";

    /**
     * Field force.
     */
    private boolean force = false;

    /**
     * Field verbosity.
     */
    private int verbosity = Project.MSG_VERBOSE;

    /**
     * Field mappers.
     */
    private final List<MyMapper> mappers = new ArrayList<MyMapper>();

    /**
     * Field targetpaths.
     */
    private Path targetpaths = null;

    /**
     * Field sourcepaths.
     */
    private Path sourcepaths = null;

    /**
     * Field outputSources.
     */
    private String outputSources = null;

    /**
     * Field outputSourcesPath.
     */
    private String outputSourcesPath = null;

    /**
     * Field outputTargets.
     */
    private String outputTargets = null;

    /**
     * Field outputTargetsPath.
     */
    private String outputTargetsPath = null;

    /**
     * Field allTargets.
     */
    private String allTargets = null;

    /**
     * Field allTargetsPath.
     */
    private String allTargetsPath = null;

    /**
     * Field separator.
     */
    private String separator = " ";

    /**
     * Field deleteTargets.
     */
    private DeleteTargets deleteTargets = null;

    /**
     * Field collection.
     */
    private int collection = CollectionEnum.SOURCES;

    // variables
    /**
     * Field targetSet.
     */
    private final Map<File, File> targetSet = new HashMap<File, File>();

    /**
     * Field sourceSet.
     */
    private final Map<File, File> sourceSet = new HashMap<File, File>();

    /**
     * Field allTargetSet.
     */
    private final Map<File, File> allTargetSet = new HashMap<File, File>();

    /**
     * Field allSourceSet.
     */
    private final Map<File, File> allSourceSet = new HashMap<File, File>();

    /**
     * Set the collection attribute, controls what is
     * returned by the iterator method.
     * <dl>
     * <dt>"sources"</dt>
     * <dd>the sources that are newer than the corresponding targets.</dd>
     * <dt>"targets"</dt>
     * <dd>the targets that are older or not present than the corresponding
     * sources.</dd>
     * <dt>"allsources"</dt><dd>all the sources</dd>
     * <dt>"alltargets"</dt><dd>all the targets</dd>
     * </dl>
     *
     * @param collection "sources" the changes
     */
    public void setCollection(CollectionEnum collection) {
        this.collection = collection.getIndex();
    }

    /**
     * Defines the FileNameMapper to use (nested mapper element).
     *
     * @return Mapper to be configured
     */
    public Mapper createMapper() {
        MyMapper mapper = new MyMapper(getProject());
        mappers.add(mapper);
        return mapper;
    }

    /**
     * The property to set if any of the target files are outofdate with
     * regard to any of the source files.
     *
     * @param property the name of the property to set if Target is outofdate.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * The separator to use to separate the files.
     *
     * @param separator separator used in output properties
     */

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * The value to set the named property to the target files
     * are outofdate.
     *
     * @param value the value to set the property
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * whether to always be outofdate.
     *
     * @param force true means that outofdate is always set, default
     *              false
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    /**
     * whether to have verbose output.
     *
     * @param verbose true means that outofdate outputs debug info
     */
    public void setVerbose(boolean verbose) {
        if (verbose) {
            this.verbosity = Project.MSG_INFO;
        } else {
            this.verbosity = Project.MSG_VERBOSE;
        }
    }

    /**
     * Add to the target files.
     *
     * @return a path to be configured
     */
    public Path createTargetfiles() {
        if (targetpaths == null) {
            targetpaths = new Path(getProject());
        }
        return targetpaths;
    }

    /**
     * Add to the source files.
     *
     * @return a path to be configured
     */
    public Path createSourcefiles() {
        if (sourcepaths == null) {
            sourcepaths = new Path(getProject());
        }
        return sourcepaths;
    }

    /**
     * A property to contain the output source files.
     *
     * @param outputSources the name of the property
     */
    public void setOutputSources(String outputSources) {
        this.outputSources = outputSources;
    }

    /**
     * A property to contain the output target files.
     *
     * @param outputTargets the name of the property
     */
    public void setOutputTargets(String outputTargets) {
        this.outputTargets = outputTargets;
    }

    /**
     * A reference to contain the path of target files that
     * are outofdate.
     *
     * @param outputTargetsPath the name of the reference
     */
    public void setOutputTargetsPath(String outputTargetsPath) {
        this.outputTargetsPath = outputTargetsPath;
    }

    /**
     * A reference to contain the path of all the targets.
     *
     * @param allTargetsPath the name of the reference
     */
    public void setAllTargetsPath(String allTargetsPath) {
        this.allTargetsPath = allTargetsPath;
    }

    /**
     * A property to contain all the target filenames.
     *
     * @param allTargets the name of the property
     */
    public void setAllTargets(String allTargets) {
        this.allTargets = allTargets;
    }

    /**
     * A reference to the path containing all the sources files.
     *
     * @param outputSourcesPath the name of the reference
     */
    public void setOutputSourcesPath(String outputSourcesPath) {
        this.outputSourcesPath = outputSourcesPath;
    }

    /**
     * optional nested delete element.
     *
     * @return an element to be configured
     */
    public DeleteTargets createDeleteTargets() {
        deleteTargets = new DeleteTargets();
        return deleteTargets;
    }

    /**
     * Embedded do parallel.
     *
     * @param doTask the parallel to embed
     */
    public void addParallel(Parallel doTask) {
        if (this.doTask != null) {
            throw new BuildException("You must not nest more that one <parallel> or <sequential>"
                    + " into <outofdate>");
        }
        this.doTask = doTask;
    }

    /**
     * Embedded do sequential.
     *
     * @param doTask the sequential to embed
     */
    public void addSequential(Sequential doTask) {
        if (this.doTask != null) {
            throw new BuildException("You must not nest more that one <parallel> or <sequential>"
                    + " into <outofdate>");
        }
        this.doTask = doTask;
    }

    /**
     * Evaluate (all) target and source file(s) to
     * see if the target(s) is/are outoutdate.
     *
     * @return true if any of the targets are outofdate
     * @see org.apache.tools.ant.taskdefs.condition.Condition#eval()
     */
    public boolean eval() {
        boolean ret = false;
        FileUtils fileUtils = FileUtils.getFileUtils();
        if (sourcepaths == null) {
            throw new BuildException("You must specify a <sourcefiles> element.");
        }

        if (targetpaths == null && mappers.size() == 0) {
            throw new BuildException("You must specify a <targetfiles> or <mapper> element.");
        }

        // Source Paths
        String[] spaths = sourcepaths.list();

        for (String spath : spaths) {
            File sourceFile = new File(spath);
            if (!sourceFile.exists()) {
                throw new BuildException(sourceFile.getAbsolutePath()
                        + " not found.");
            }
        }

        // Target Paths

        if (targetpaths != null) {
            String[] paths = targetpaths.list();
            if (paths.length == 0) {
                ret = true;
            } else {
                for (String path : paths) {
                    if (targetNeedsGen(path, spaths)) {
                        ret = true;
                    }
                }
            }
        }

        // Mapper Paths
        for (MyMapper mapper : mappers) {

            File relativeDir = mapper.getDir();
            File baseDir = new File(getProject().getProperty("basedir"));
            if (relativeDir == null) {
                relativeDir = baseDir;
            }
            String[] rpaths = new String[spaths.length];
            for (int i = 0; i < spaths.length; ++i) {
                rpaths[i] = fileUtils.removeLeadingPath(relativeDir, new File(spaths[i]));
            }

            FileNameMapper fileNameMapper = mapper.getImplementation();
            for (int i = 0; i < spaths.length; ++i) {
                String[] mapped = fileNameMapper.mapFileName(rpaths[i]);
                if (mapped != null) {
                    for (String mappedName : mapped) {
                        if (outOfDate(new File(spaths[i]),
                                fileUtils.resolveFile(
                                        baseDir, mappedName))) {
                            ret = true;
                        }
                    }
                }
            }
        }

        if (allTargets != null) {
            this.getProject().setNewProperty(
                    allTargets, setToString(allTargetSet));
        }

        if (allTargetsPath != null) {
            this.getProject().addReference(
                    allTargetsPath, setToPath(allTargetSet));
        }

        if (outputSources != null) {
            this.getProject().setNewProperty(
                    outputSources, setToString(sourceSet));
        }

        if (outputTargets != null) {
            this.getProject().setNewProperty(
                    outputTargets, setToString(targetSet));
        }

        if (outputSourcesPath != null) {
            this.getProject().addReference(
                    outputSourcesPath, setToPath(sourceSet));
        }

        if (outputTargetsPath != null) {
            this.getProject().addReference(
                    outputTargetsPath, setToPath(targetSet));
        }

        if (force) {
            ret = true;
        }

        if (ret && deleteTargets != null) {
            deleteTargets.execute();
        }

        if (ret) {
            if (property != null) {
                this.getProject().setNewProperty(property, value);
            }
        }

        return ret;
    }

    /**
     * Method targetNeedsGen.
     *
     * @param target String
     * @param spaths String[]
     * @return boolean
     */
    private boolean targetNeedsGen(String target, String[] spaths) {
        boolean ret = false;
        File targetFile = new File(target);
        for (String spath : spaths) {
            if (outOfDate(new File(spath), targetFile)) {
                ret = true;
            }
        }
        // Special case : there are no source files, make sure the
        //                targets exist
        if (spaths.length == 0) {
            if (outOfDate(null, targetFile)) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Call evaluate and return an iterator over the result.
     *
     * @return an iterator over the result
     */
    public Iterator<File> iterator() {
        // Perhaps should check the result and return
        // an empty set if it returns false
        eval();

        switch (collection) {
            case CollectionEnum.SOURCES:
                return sourceSet.values().iterator();
            case CollectionEnum.TARGETS:
                return targetSet.values().iterator();
            case CollectionEnum.ALLSOURCES:
                return allSourceSet.values().iterator();
            case CollectionEnum.ALLTARGETS:
                return allTargetSet.values().iterator();
            default:
                return sourceSet.values().iterator();
        }
    }

    /**
     * Sets property to true and/or executes embedded do
     * if any of the target file(s) do not have a more recent timestamp
     * than (each of) the source file(s).
     */
    public void execute() {
        if (!eval()) {
            return;
        }

        if (doTask != null) {
            doTask.perform();
        }

    }

    /**
     * Method outOfDate.
     *
     * @param sourceFile File
     * @param targetFile File
     * @return boolean
     */
    private boolean outOfDate(File sourceFile, File targetFile) {
        boolean ret = false;
        if (sourceFile != null) {
            allSourceSet.put(sourceFile, sourceFile);
        }
        allTargetSet.put(targetFile, targetFile);
        if (!targetFile.exists()) {
            ret = true;
        }
        if ((!ret) && (sourceFile != null)) {
            ret = sourceFile.lastModified() > targetFile.lastModified();
        }
        if (ret) {
            if ((sourceFile != null && sourceSet.get(sourceFile) == null)
                    || targetSet.get(targetFile) == null) {
                log("SourceFile " + sourceFile + " outofdate "
                        + "with regard to " + targetFile, verbosity);
            }
            if (sourceFile != null) {
                sourceSet.put(sourceFile, sourceFile);
            }
            targetSet.put(targetFile, targetFile);
        }
        return ret;
    }

    /**
     * Method setToString.
     *
     * @param set Map&lt;File,File&gt;
     * @return String
     */
    private String setToString(Map<File, File> set) {
        StringBuilder b = new StringBuilder();
        for (File v : set.keySet()) {
            if (b.length() != 0) {
                b.append(separator);
            }
            String s = v.getAbsolutePath();
            // TODO The following needs more work!
            // Handle paths containing separators
            if (s.contains(separator)) {
                if (s.contains("\"")) {
                    s = "'" + s + "'";
                } else {
                    s = "\"" + s + "\"";
                }
            }
            b.append(s);
        }
        return b.toString();
    }

    /**
     * Method setToPath.
     *
     * @param set Map&lt;File,File&gt;
     * @return Path
     */
    private Path setToPath(Map<File, File> set) {
        Path ret = new Path(getProject());
        for (File v : set.keySet()) {
            Path.PathElement el = ret.createPathElement();
            el.setLocation(v);
        }
        return ret;
    }

    /**
     * nested delete targets.
     */
    public class DeleteTargets {
        /**
         * Field all.
         */
        private boolean all = false;

        /**
         * Field quiet.
         */
        private boolean quiet = false;

        /**
         * Field failOnError.
         */
        private boolean failOnError = false;

        /**
         * Field myLogging.
         */
        private int myLogging = Project.MSG_INFO;

        /**
         * whether to delete all the targets
         * or just those that are newer than the
         * corresponding sources.
         *
         * @param all true to delete all, default false
         */
        public void setAll(boolean all) {
            this.all = all;
        }

        /**
         * setQuiet() method.
         *
         * @param quiet if true suppress messages on deleting files
         */
        public void setQuiet(boolean quiet) {
            this.quiet = quiet;
            myLogging = quiet ? Project.MSG_VERBOSE : Project.MSG_INFO;
        }

        /**
         * setFailOnError() method.
         *
         * @param failOnError if true halt if there is a failure to delete
         */
        public void setFailOnError(boolean failOnError) {
            this.failOnError = failOnError;
        }

        /**
         * Method execute.
         */
        private void execute() {
            if (myLogging != Project.MSG_INFO) {
                myLogging = verbosity;
            }

            // Quiet overrides failOnError
            if (quiet) {
                failOnError = false;
            }

            Path toBeDeleted = null;
            if (all) {
                toBeDeleted = setToPath(allTargetSet);
            } else {
                toBeDeleted = setToPath(targetSet);
            }

            String[] names = toBeDeleted.list();
            for (String name : names) {
                File file = new File(name);
                if (!file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    removeDir(file);
                    continue;
                }
                log("Deleting " + file.getAbsolutePath(), myLogging);
                if (!file.delete()) {
                    String message =
                            "Unable to delete file " + file.getAbsolutePath();
                    if (failOnError) {
                        throw new BuildException(message);
                    } else {
                        log(message, myLogging);
                    }
                }
            }
        }

        /**
         * Field DELETE_RETRY_SLEEP_MILLIS.
         * (value is 10)
         */
        private static final int DELETE_RETRY_SLEEP_MILLIS = 10;

        /**
         * Attempt to fix possible race condition when deleting
         * files on WinXP. If the delete does not work,
         * wait a little and try again.
         *
         * @param f File
         * @return boolean
         */
        private boolean delete(File f) {
            if (!f.delete()) {
                try {
                    Thread.sleep(DELETE_RETRY_SLEEP_MILLIS);
                    return f.delete();
                } catch (InterruptedException ex) {
                    return f.delete();
                }
            }
            return true;
        }

        /**
         * Method removeDir.
         *
         * @param d File
         */
        private void removeDir(File d) {
            String[] list = d.list();
            if (list == null) {
                list = new String[0];
            }
            for (String s : list) {
                File f = new File(d, s);
                if (f.isDirectory()) {
                    removeDir(f);
                } else {
                    log("Deleting " + f.getAbsolutePath(), myLogging);
                    if (!f.delete()) {
                        String message = "Unable to delete file "
                                + f.getAbsolutePath();
                        if (failOnError) {
                            throw new BuildException(message);
                        } else {
                            log(message, myLogging);
                        }
                    }
                }
            }
            log("Deleting directory " + d.getAbsolutePath(), myLogging);
            if (!delete(d)) {
                String message = "Unable to delete directory "
                        + d.getAbsolutePath();
                if (failOnError) {
                    throw new BuildException(message);
                } else {
                    log(message, myLogging);
                }
            }
        }
    }

    /**
     * Wrapper for mapper - includes dir.
     */
    public static class MyMapper extends Mapper {
        /**
         * Field dir.
         */
        private File dir = null;

        /**
         * Creates a new <code>MyMapper</code> instance.
         *
         * @param project the current project
         */
        public MyMapper(Project project) {
            super(project);
        }

        /**
         * setDir() method.
         *
         * @param dir the directory that the from files are relative to
         */
        public void setDir(File dir) {
            this.dir = dir;
        }

        /**
         * getDir() method.
         *
         * @return the directory that the from files are relative to
         */
        public File getDir() {
            return dir;
        }
    }
}
