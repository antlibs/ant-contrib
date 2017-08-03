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
package net.sf.antcontrib.antclipse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Path.PathElement;
import org.apache.tools.ant.util.RegexpPatternMapper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Support class for the Antclipse task. Basically, it takes the .classpath Eclipse file
 * and feeds a SAX parser. The handler is slightly different according to what we want to
 * obtain (a classpath or a fileset).
 *
 * @author <a href="mailto:aspinei@myrealbox.com">Adrian Spinei</a>
 * @version $Revision: 1.2 $
 * @since Ant 1.5
 */
public class ClassPathTask extends Task {
    /**
     * Field idContainer.
     */
    private String idContainer = "antclipse";

    /**
     * Field includeSource.
     * Default, do not include source
     */
    private boolean includeSource = false;

    /**
     * Field includeOutput.
     * Default, do not include output directory
     */
    private boolean includeOutput = false;

    /**
     * Field includeLibs.
     * Default, include all libraries
     */
    private boolean includeLibs = true;

    /**
     * Field verbosity.
     * Default quiet
     */
    private int verbosity = Project.MSG_VERBOSE;

    /**
     * Field irpm.
     */
    private RegexpPatternMapper irpm = null;

    /**
     * Field erpm.
     */
    private RegexpPatternMapper erpm = null;

    /**
     * Field TARGET_CLASSPATH.
     * (value is ""classpath"")
     */
    private static final String TARGET_CLASSPATH = "classpath";

    /**
     * Field TARGET_FILESET.
     * (value is ""fileset"")
     */
    private static final String TARGET_FILESET = "fileset";

    /**
     * Field produce.
     * classpath by default
     */
    private String produce = null;

    /**
     * Field recursivePaths.
     * avoid double processing
     */
    protected List<String> recursivePaths = new ArrayList<String>();

    /**
     * Setter for task parameter.
     *
     * @param includeLibs Boolean, whether the project libraries
     *                    must be included or not. Default is true.
     */
    public void setIncludeLibs(boolean includeLibs) {
        this.includeLibs = includeLibs;
    }

    /**
     * Setter for task parameter.
     *
     * @param produce This parameter tells the task whether to produce
     *                a "classpath" or a "fileset" (multiple filesets, as a matter of fact).
     */
    public void setproduce(String produce) {
        this.produce = produce;
    }

    /**
     * Setter for task parameter.
     *
     * @param verbose Boolean, telling the app to throw some info during
     *                each step. Default is false.
     */
    public void setVerbose(boolean verbose) {
        if (verbose) {
            this.verbosity = Project.MSG_WARN;
        }
    }

    /**
     * Setter for task parameter.
     *
     * @param excludes A regexp for files to exclude.
     *                 It is taken into account only when producing a classpath,
     *                 doesn't work on source or output files. It is a real regexp,
     *                 not a glob/wildcard expression.
     */
    public void setExcludes(String excludes) {
        if (excludes != null) {
            erpm = new RegexpPatternMapper();
            erpm.setFrom(excludes);
            erpm.setTo("."); // mandatory
        } else {
            erpm = null;
        }
    }

    /**
     * Setter for task parameter.
     *
     * @param includes A regexp for files to include.
     *                 It is taken into account only when producing a classpath,
     *                 doesn't work on source or output files. It is a real
     *                 regexp, not a glob/wildcard expression.
     */
    public void setIncludes(String includes) {
        if (includes != null) {
            irpm = new RegexpPatternMapper();
            irpm.setFrom(includes);
            irpm.setTo("."); // mandatory
        } else {
            irpm = null;
        }
    }

    /**
     * Setter for task parameter.
     *
     * @param idContainer The refid which will serve to identify the
     *                    deliverables. When multiple filesets are produced,
     *                    their refid is a concatenation between this value
     *                    and something else (usually obtained from a path).
     *                    Default value is "antclipse".
     */
    public void setIdContainer(String idContainer) {
        this.idContainer = idContainer;
    }

    /**
     * Setter for task parameter.
     *
     * @param includeOutput Boolean, whether the project output directories
     *                      must be included or not. Default is false.
     */
    public void setIncludeOutput(boolean includeOutput) {
        this.includeOutput = includeOutput;
    }

    /**
     * Setter for task parameter.
     *
     * @param includeSource Boolean, whether to include or not the project source directories.
     *                      Default is false.
     */
    public void setIncludeSource(boolean includeSource) {
        this.includeSource = includeSource;
    }

    /**
     * @throws BuildException when something goes wrong
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        if (!TARGET_CLASSPATH.equalsIgnoreCase(this.produce)
                && !TARGET_FILESET.equals(this.produce)) {
            throw new BuildException("Mandatory target must be either '"
                    + TARGET_CLASSPATH + "' or '" + TARGET_FILESET + "'");
        }
        ClassPathParser parser = new ClassPathParser();
        AbstractCustomHandler handler;
        if (TARGET_CLASSPATH.equalsIgnoreCase(this.produce)) {
            Path path = new Path(getProject());
            getProject().addReference(idContainer, path);
            handler = new PathCustomHandler(path);
        } else {
            FileSet fileSet = new FileSet();
            getProject().addReference(idContainer, fileSet);
            fileSet.setDir(new File(getProject().getBaseDir().getAbsolutePath()));
            handler = new FileSetCustomHandler(fileSet);
        }
        parser.parse(new File(getProject().getBaseDir().getAbsolutePath(), ".classpath"), handler);
    }

    /**
     */
    abstract class AbstractCustomHandler extends DefaultHandler {
        /**
         * Field projDir.
         */
        protected String projDir;

        /**
         * Field task.
         */
        protected Task task;

        /**
         * Field ATTRNAME_PATH.
         * (value is ""path"")
         */
        protected static final String ATTRNAME_PATH = "path";

        /**
         * Field ATTRNAME_KIND.
         * (value is ""kind"")
         */
        protected static final String ATTRNAME_KIND = "kind";

        /**
         * Field ATTR_LIB.
         * (value is ""lib"")
         */
        protected static final String ATTR_LIB = "lib";

        /**
         * Field ATTR_SRC.
         * (value is ""src"")
         */
        protected static final String ATTR_SRC = "src";

        /**
         * Field ATTR_OUTPUT.
         * (value is ""output"")
         */
        protected static final String ATTR_OUTPUT = "output";

        /**
         * Field ATTR_VAR (Eclipse variable).
         * (value is ""var"")
         */
         protected static final String ATTR_VAR = "var";

        /**
         * Field EMPTY.
         * (value is """")
         */
        protected static final String EMPTY = "";
    }

    /**
     */
    class FileSetCustomHandler extends AbstractCustomHandler {
        /**
         * Field fileSet.
         */
        private FileSet fileSet = null;

        /**
         * nazi style, forbid default constructor.
         */
        @SuppressWarnings("unused")
        private FileSetCustomHandler() {
        }

        /**
         * Constructor.
         *
         * @param fileSet FileSet
         */
        public FileSetCustomHandler(FileSet fileSet) {
            super();
            this.fileSet = fileSet;
            projDir = getProject().getBaseDir().getAbsolutePath();
            task = getProject().getThreadTask(Thread.currentThread());
        }

        /**
         * @throws SAXException if parsing fails
         * @see org.xml.sax.ContentHandler#endDocument()
         */
        public void endDocument() throws SAXException {
            super.endDocument();
            if (fileSet != null && !fileSet.hasPatterns()) {
                // exclude everything or we'll take all the project dirs
                fileSet.setExcludes("**/*");
            }
        }

        /**
         * Method startElement.
         *
         * @param uri       String
         * @param localName String
         * @param tag       String
         * @param attrs     Attributes
         * @throws SAXParseException when tags lack mandatory attributes
         */
        @Override
        public void startElement(String uri, String localName, String tag, Attributes attrs)
                throws SAXParseException {
            if (tag.equalsIgnoreCase("classpathentry")) {
                // start by checking if the classpath is coherent at all
                String kind = attrs.getValue(ATTRNAME_KIND);
                if (kind == null) {
                    throw new BuildException("classpathentry 'kind' attribute is mandatory");
                }
                String path = attrs.getValue(ATTRNAME_PATH);
                if (path == null) {
                    throw new BuildException("classpathentry 'path' attribute is mandatory");
                }

                // put the outputdirectory in a property
                if (kind.equalsIgnoreCase(ATTR_OUTPUT)) {
                    String propName = idContainer + "outpath";
                    Property property = new Property();
                    property.setName(propName);
                    property.setValue(path);
                    property.setProject(getProject());
                    property.execute();
                    getProject().log(task,"Setting property " + propName + " to value " + path, verbosity);
                }

                // put the last source directory in a property
                if (kind.equalsIgnoreCase(ATTR_SRC)) {
                    String propName = idContainer + "srcpath";
                    Property property = new Property();
                    property.setName(propName);
                    property.setValue(path);
                    property.setProject(getProject());
                    property.execute();
                    getProject().log(task,"Setting property " + propName + " to value " + path, verbosity);
                }

                if ((kind.equalsIgnoreCase(ATTR_SRC) && includeSource)
                        || (kind.equalsIgnoreCase(ATTR_OUTPUT) && includeOutput)
                        || (kind.equalsIgnoreCase(ATTR_LIB) && includeLibs)) {
                    // all seems fine
                    // check the includes
                    String[] inclResult = new String[]{"all included"};
                    if (irpm != null) {
                        inclResult = irpm.mapFileName(path);
                    }
                    String[] exclResult = null;
                    if (erpm != null) {
                        exclResult = erpm.mapFileName(path);
                    }
                    if (inclResult != null && exclResult == null) {
                        // THIS is the specific code
                        if (kind.equalsIgnoreCase(ATTR_OUTPUT)) {
                            // we have included output so let's build a new fileset
                            FileSet outFileSet = new FileSet();
                            String newReference = idContainer + "-" + path.replace('/', '-');
                            getProject().addReference(newReference, outFileSet);
                            getProject().log(task, String.format("Created new fileset %s containing all the files from the output dir %s/%s",
                                    newReference, projDir, path), verbosity);
                            outFileSet.setDefaultexcludes(false);
                            outFileSet.setDir(new File(projDir, path));
                            outFileSet.setIncludes("**/*"); // get everything
                        } else if (kind.equalsIgnoreCase(ATTR_SRC)) {
                            //we have included source so let's build a new fileset
                            FileSet srcFileSet = new FileSet();
                            String newReference = idContainer + "-" + path.replace('/', '-');
                            getProject().addReference(newReference, srcFileSet);
                            getProject().log(task, String.format("Created new fileset %s containing all the files from the source dir %s/%s",
                                    newReference, projDir, path), verbosity);
                            srcFileSet.setDefaultexcludes(false);
                            srcFileSet.setDir(new File(projDir, path));
                            srcFileSet.setIncludes("**/*"); // get everything
                        } else {
                            // not output, just add file after file to the fileset
                            File file = new File(fileSet.getDir(getProject()), path);
                            if (file.isDirectory()) {
                                path += "/**/*";
                            }
                            getProject().log(task, String.format("Adding  %s to fileset %s at %s",
                                    path, idContainer, fileSet.getDir(getProject())), verbosity);
                            fileSet.setIncludes(path);
                        }
                    }
                }
            }
        }
    }

    /**
     */
    class PathCustomHandler extends AbstractCustomHandler {
        /**
         * Field path.
         */
        private Path path = null;

        /**
         * Constructor.
         *
         * @param path the path to add files
         */
        public PathCustomHandler(Path path) {
            super();
            this.path = path;
            this.task = getProject().getThreadTask(Thread.currentThread());
        }

        /**
         * nazi style, forbid default constructor.
         */
        @SuppressWarnings("unused")
        private PathCustomHandler() {
        }

        /**
         * Method startElement.
         *
         * @param uri       String
         * @param localName String
         * @param tag       String
         * @param attrs     Attributes
         * @throws SAXParseException when tags lack mandatory attributes
         */
        @Override
        public void startElement(String uri, String localName, String tag, Attributes attrs)
                throws SAXParseException {
            if (tag.equalsIgnoreCase("classpathentry")) {
                // start by checking if the classpath is coherent at all
                String kind = attrs.getValue(ATTRNAME_KIND);
                if (kind == null) {
                    throw new BuildException("classpathentry 'kind' attribute is mandatory");
                }
                String path = attrs.getValue(ATTRNAME_PATH);
                if (path == null) {
                    throw new BuildException("classpathentry 'path' attribute is mandatory");
                }

                // put the outputdirectory in a property
                if (kind.equalsIgnoreCase(ATTR_OUTPUT)) {
                    String propName = idContainer + "outpath";
                    Property property = new Property();
                    property.setName(propName);
                    property.setValue(path);
                    property.setProject(getProject());
                    property.execute();
                    getProject().log(task,"Setting property " + propName + " to value " + path, verbosity);
                }

                // replace the variable assuming it is a PREFIX in the file path
                if (kind.equalsIgnoreCase(ATTR_VAR)) {
                    kind = ATTR_LIB; //change type: after variable substitution this tag is like a "lib" tag...
                    StringBuilder newPath = new StringBuilder();
                    for (String aPathSplit : path.split("/")) {
                        if (newPath.length() == 0) {
                            String propertyName = aPathSplit;
                            String propertyValue = getProject().getProperty(propertyName);
                            if (propertyValue == null) {
                                throw new BuildException("property must be defined: " + propertyName);
                            }
                            newPath.append(propertyValue);
                        } else {
                            newPath.append("/").append(aPathSplit);
                        }
                    }
                    path = newPath.toString();
                }

                // put the last source directory in a property
                if (kind.equalsIgnoreCase(ATTR_SRC)) {
                    String propName = idContainer + "srcpath";
                    Property property = new Property();
                    property.setName(propName);
                    property.setValue(path);
                    property.setProject(getProject());
                    property.execute();
                    getProject().log(task,"Setting property " + propName + " to value " + path, verbosity);
                }

                if ((kind.equalsIgnoreCase(ATTR_SRC) && includeSource)
                        || (kind.equalsIgnoreCase(ATTR_OUTPUT) && includeOutput)
                        || (kind.equalsIgnoreCase(ATTR_LIB) && includeLibs)) {
                    // all seems fine
                    // check the includes
                    String[] inclResult = new String[]{"all included"};
                    if (irpm != null) {
                        inclResult = irpm.mapFileName(path);
                    }
                    String[] exclResult = null;
                    if (erpm != null) {
                        exclResult = erpm.mapFileName(path);
                    }
                    if (inclResult != null && exclResult == null) {
                         if (kind.equalsIgnoreCase(ATTR_SRC) && path.startsWith("/")) {
                             // including a project -> recursion is needed...
                             // assume that the name of the project and its path on disk are equals
                             File classpath = new File(getProject().getBaseDir().getParentFile(), path + "/.classpath");
                             if (!recursivePaths.contains(classpath.getAbsolutePath())) { //avoid double-processing
                                 recursivePaths.add(classpath.getAbsolutePath());
                                 getProject().log(task,"parsing " + classpath, verbosity);
                                 new ClassPathParser().parse(classpath, this);
                             }
                         } else {
                             // THIS is the only specific code
                             getProject().log(task,"Adding  " + path + " to classpath " + idContainer, verbosity);
                             PathElement element = this.path.createPathElement();
                             element.setLocation(new File(path));
                         }
                    }
                }
            }
        }
    }
}
