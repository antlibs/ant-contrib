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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.util.FileUtils;

/**
 * Identical (copy and paste, even) to the 'Ant' task, with the exception that
 * properties from the new project can be copied back into the original
 * project. Further modified to emulate "antcall". Build a sub-project. <pre>
 *  &lt;target name=&quot;foo&quot; depends=&quot;init&quot;&gt;
 *    &lt;ant antfile=&quot;build.xml&quot; target=&quot;bar&quot; &gt;
 *      &lt;property name=&quot;property1&quot; value=&quot;aaaaa&quot; /&gt;
 *      &lt;property name=&quot;foo&quot; value=&quot;baz&quot; /&gt;
 *    &lt;/ant&gt;
 *  &lt;/target&gt;
 *  &lt;target name=&quot;bar&quot; depends=&quot;init&quot;&gt;
 *    &lt;echo message=&quot;prop is ${property1} ${foo}&quot; /&gt;
 *  &lt;/target&gt;</pre>
 * <p>Developed for use with Antelope, migrated to ant-contrib Oct 2003.</p>
 * <p>Credit to Costin for the original &lt;ant&gt; task, on which this is
 * based.</p>
 *
 * @author <a href="mailto:costin@dnt.ro">Costin Manolache</a>
 * @author <a href="mailto:danson@germane-software.com">Dale Anson</a>
 * @since Ant 1.1
 */
public class AntCallBack extends Task {
    /**
     * the basedir where the build file is executed.
     */
    private File dir = null;

    /**
     * the build.xml file (can be absolute) in this case dir will be ignored.
     */
    private String antFile = null;

    /**
     * the target to call if any.
     */
    private String target = null;

    /**
     * the output.
     */
    private String output = null;

    /**
     * should we inherit properties from the parent.
     */
    private boolean inheritAll = true;

    /**
     * should we inherit references from the parent.
     */
    private boolean inheritRefs = false;

    /**
     * the properties to pass to the new project.
     */
    private final List<Property> properties = new ArrayList<Property>();

    /**
     * the references to pass to the new project.
     */
    private final List<Reference> references = new ArrayList<Reference>();

    /**
     * the temporary project created to run the build file.
     */
    private Project newProject;

    /**
     * The stream to which output is to be written.
     */
    private PrintStream out = null;

    /**
     * the name of the property to fetch from the new project.
     */
    private String returnName = null;

    /**
     * If true, pass all properties to the new Ant project. Defaults to true.
     *
     * @param value The new inheritAll value
     */
    public void setInheritAll(boolean value) {
        inheritAll = value;
    }

    /**
     * If true, pass all references to the new Ant project. Defaults to false.
     *
     * @param value The new inheritRefs value
     */
    public void setInheritRefs(boolean value) {
        inheritRefs = value;
    }

    /**
     * Creates a Project instance for the project to call.
     */
    public void init() {
        newProject = new Project();
        newProject.setJavaVersionProperty();
        newProject.addTaskDefinition("property",
                getProject().getTaskDefinitions()
                        .get("property"));
    }

    /**
     * Called in execute or createProperty if newProject is null.
     * <p>This can happen if the same instance of this task is run twice as
     * newProject is set to null at the end of execute (to save memory and help
     * the GC).</p>
     * <p>Sets all properties that have been defined as nested property
     * elements.</p>
     */
    private void reinit() {
        init();
        for (Property p : properties) {
            Property newP = (Property) newProject.createTask("property");
            newP.setName(p.getName());
            if (p.getValue() != null) {
                newP.setValue(p.getValue());
            }
            if (p.getFile() != null) {
                newP.setFile(p.getFile());
            }
            if (p.getResource() != null) {
                newP.setResource(p.getResource());
            }
            if (p.getPrefix() != null) {
                newP.setPrefix(p.getPrefix());
            }
            if (p.getRefid() != null) {
                newP.setRefid(p.getRefid());
            }
            if (p.getEnvironment() != null) {
                newP.setEnvironment(p.getEnvironment());
            }
            if (p.getClasspath() != null) {
                newP.setClasspath(p.getClasspath());
            }
            properties.set(properties.indexOf(p), newP);
        }
    }

    /**
     * Attaches the build listeners of the current project to the new project,
     * configures a possible logfile, transfers task and data-type definitions,
     * transfers properties (either all or just the ones specified as user
     * properties to the current project, depending on inheritall), transfers
     * the input handler.
     */
    private void initializeProject() {
        newProject.setInputHandler(getProject().getInputHandler());

        List<BuildListener> listeners = getProject().getBuildListeners();
        for (BuildListener listener : listeners) {
            newProject.addBuildListener(listener);
        }

        if (output != null) {
            File outfile = null;
            if (dir != null) {
                outfile = FileUtils.getFileUtils().resolveFile(dir, output);
            } else {
                outfile = getProject().resolveFile(output);
            }
            try {
                out = new PrintStream(new FileOutputStream(outfile));
                DefaultLogger logger = new DefaultLogger();
                logger.setMessageOutputLevel(Project.MSG_INFO);
                logger.setOutputPrintStream(out);
                logger.setErrorPrintStream(out);
                newProject.addBuildListener(logger);
            } catch (IOException ex) {
                log("Ant: Can't set output to " + output);
            }
        }

        Hashtable<String, Class<?>> taskdefs = getProject().getTaskDefinitions();
        Enumeration<String> et = taskdefs.keys();
        while (et.hasMoreElements()) {
            String taskName = et.nextElement();
            if (taskName.equals("property")) {
                // we have already added this taskdef in #init
                continue;
            }
            Class<?> taskClass = taskdefs.get(taskName);
            newProject.addTaskDefinition(taskName, taskClass);
        }

        Hashtable<String, Class<?>> typedefs = getProject().getDataTypeDefinitions();
        Enumeration<String> e = typedefs.keys();
        while (e.hasMoreElements()) {
            String typeName = e.nextElement();
            Class<?> typeClass = typedefs.get(typeName);
            newProject.addDataTypeDefinition(typeName, typeClass);
        }

        // set user-defined properties
        getProject().copyUserProperties(newProject);

        if (!inheritAll) {
            // set Java built-in properties separately,
            // b/c we won't inherit them.
            newProject.setSystemProperties();
        } else {
            // set all properties from calling project
            Hashtable<String, Object> props = getProject().getProperties();
            e = props.keys();
            while (e.hasMoreElements()) {
                String arg = e.nextElement();
                if ("basedir".equals(arg) || "ant.file".equals(arg)) {
                    // basedir and ant.file get special treatment in execute()
                    continue;
                }

                String value = props.get(arg).toString();
                // don't re-set user properties, avoid the warning message
                if (newProject.getProperty(arg) == null) {
                    // no user property
                    newProject.setNewProperty(arg, value);
                }
            }
        }
    }

    /**
     * Pass output sent to System.out to the new project.
     *
     * @param line Description of the Parameter
     * @since Ant 1.5
     */
    protected void handleOutput(String line) {
        if (newProject != null) {
            newProject.demuxOutput(line, false);
        } else {
            super.handleOutput(line);
        }
    }

    /**
     * Pass output sent to System.err to the new project.
     *
     * @param line Description of the Parameter
     * @since Ant 1.5
     */
    protected void handleErrorOutput(String line) {
        if (newProject != null) {
            newProject.demuxOutput(line, true);
        } else {
            super.handleErrorOutput(line);
        }
    }

    /**
     * Do the execution.
     *
     * @throws BuildException Description of the Exception
     */
    public void execute() throws BuildException {
        setAntfile(getProject().getProperty("ant.file"));

        File savedDir = dir;
        String savedAntFile = antFile;
        String savedTarget = target;
        try {
            if (newProject == null) {
                reinit();
            }

            if ((dir == null) && (inheritAll)) {
                dir = getProject().getBaseDir();
            }

            initializeProject();

            if (dir != null) {
                newProject.setBaseDir(dir);
                if (savedDir != null) {   // has been set explicitly
                    newProject.setInheritedProperty("basedir",
                            dir.getAbsolutePath());
                }
            } else {
                dir = getProject().getBaseDir();
            }

            overrideProperties();

            if (antFile == null) {
                throw new BuildException("Attribute target is required.",
                        getLocation());
                //antFile = "build.xml";
            }

            File file = FileUtils.getFileUtils().resolveFile(dir, antFile);
            antFile = file.getAbsolutePath();

            log("calling target " + (target != null ? target : "[default]")
                            + " in build file " + antFile,
                    Project.MSG_VERBOSE);
            newProject.setUserProperty("ant.file", antFile);
            ProjectHelper.configureProject(newProject, new File(antFile));

            if (target == null) {
                target = newProject.getDefaultTarget();
            }

            addReferences();

            // Are we trying to call the target in which we are defined?
            if (newProject.getBaseDir().equals(getProject().getBaseDir())
                    && newProject.getProperty("ant.file").equals(getProject().getProperty("ant.file"))
                    && getOwningTarget() != null
                    && target.equals(this.getOwningTarget().getName())) {

                throw new BuildException("antcallback task calling its own parent target");
            }

            newProject.executeTarget(target);

            // copy back the props if possible
            if (returnName != null) {
                StringTokenizer st = new StringTokenizer(returnName, ",");
                while (st.hasMoreTokens()) {
                    String name = st.nextToken().trim();
                    String value = newProject.getUserProperty(name);
                    if (value != null) {
                        getProject().setUserProperty(name, value);
                    } else {
                        value = newProject.getProperty(name);
                        if (value != null) {
                            getProject().setProperty(name, value);
                        }
                    }
                }
            }
        } finally {
            // help the gc
            newProject = null;
            if (output != null && out != null) {
                try {
                    out.close();
                } catch (final Exception e) {
                    //ignore
                }
            }
            dir = savedDir;
            antFile = savedAntFile;
            target = savedTarget;
        }
    }

    /**
     * Override the properties in the new project with the one explicitly
     * defined as nested elements here.
     *
     * @throws BuildException Description of the Exception
     */
    private void overrideProperties() throws BuildException {
        for (Property p : properties) {
            p.setProject(newProject);
            p.execute();
        }
        getProject().copyInheritedProperties(newProject);
    }

    /**
     * Add the references explicitly defined as nested elements to the new
     * project. Also copy over all references that don't override existing
     * references in the new project if inheritrefs has been requested.
     *
     * @throws BuildException Description of the Exception
     */
    @SuppressWarnings("unchecked")
    private void addReferences() throws BuildException {
        Hashtable<String, Object> thisReferences = (Hashtable<String, Object>) getProject().getReferences().clone();
        Hashtable<String, Object> newReferences = newProject.getReferences();
        if (references.size() > 0) {
            for (Reference ref : references) {
                String refid = ref.getRefId();
                if (refid == null) {
                    throw new BuildException("the refid attribute is required"
                            + " for reference elements");
                }
                if (!thisReferences.containsKey(refid)) {
                    log("Parent project contains no reference '"
                                    + refid + "'",
                            Project.MSG_WARN);
                    continue;
                }

                thisReferences.remove(refid);
                String toRefid = ref.getToRefid();
                if (toRefid == null) {
                    toRefid = refid;
                }
                copyReference(refid, toRefid);
            }
        }

        // Now add all references that are not defined in the
        // subproject, if inheritRefs is true
        if (inheritRefs) {
            Enumeration<String> f = thisReferences.keys();
            while (f.hasMoreElements()) {
                String key = f.nextElement();
                if (newReferences.containsKey(key)) {
                    continue;
                }
                copyReference(key, key);
            }
        }
    }

    /**
     * Try to clone and reconfigure the object referenced by oldkey in the
     * parent project and add it to the new project with the key newkey.
     * <p>If we cannot clone it, copy the referenced object itself and keep
     * our fingers crossed.</p>
     *
     * @param oldKey Description of the Parameter
     * @param newKey Description of the Parameter
     */
    private void copyReference(String oldKey, String newKey) {
        Object orig = getProject().getReference(oldKey);
        Class<?> c = orig.getClass();
        Object copy = orig;
        try {
            Method cloneM = c.getMethod("clone");
            if (cloneM != null) {
                copy = cloneM.invoke(orig);
            }
        } catch (Exception e) {
            // not Clonable
        }

        if (copy instanceof ProjectComponent) {
            ((ProjectComponent) copy).setProject(newProject);
        } else {
            try {
                Method setProjectM =
                        c.getMethod("setProject", Project.class);
                if (setProjectM != null) {
                    setProjectM.invoke(copy, newProject);
                }
            } catch (NoSuchMethodException e) {
                // ignore this if the class being referenced does not have
                // a set project method.
            } catch (Exception e2) {
                String msg = "Error setting new project instance for "
                        + "reference with id " + oldKey;
                throw new BuildException(msg, e2, getLocation());
            }
        }
        newProject.addReference(newKey, copy);
    }

    /**
     * The directory to use as a base directory for the new Ant project.
     * Defaults to the current project's basedir, unless inheritall has been
     * set to false, in which case it doesn't have a default value. This will
     * override the basedir setting of the called project.
     *
     * @param d The new dir value
     */
    public void setDir(File d) {
        this.dir = d;
    }

    /**
     * The build file to use. Defaults to "build.xml". This file is expected to
     * be a filename relative to the dir attribute given.
     *
     * @param s The new antfile value
     */
    public void setAntfile(String s) {
        // @note: it is a string and not a file to handle relative/absolute
        // otherwise a relative file will be resolved based on the current
        // basedir.
        this.antFile = s;
    }

    /**
     * The target of the new Ant project to execute. Defaults to the new
     * project's default target.
     *
     * @param s The new target value
     */
    public void setTarget(String s) {
        this.target = s;
    }

    /**
     * Filename to write the output to. This is relative to the value of the dir
     * attribute if it has been set or to the base directory of the current
     * project otherwise.
     *
     * @param s The new output value
     */
    public void setOutput(String s) {
        this.output = s;
    }

    /**
     * Property to pass to the new project. The property is passed as a 'user
     * property'
     *
     * @return Description of the Return Value
     */
    public Property createProperty() {
        if (newProject == null) {
            reinit();
        }
        /*
         *  Property p = new Property(true, getProject());
         */
        Property p = new Property();
        p.setProject(newProject);
        p.setTaskName("property");
        properties.add(p);
        return p;
    }

    /**
     * Property to pass to the invoked target.
     *
     * @return Property
     */
    public Property createParam() {
        return createProperty();
    }

    /**
     * Set the property or properties that are set in the new project to be
     * transferred back to the original project. As with all properties, if the
     * property already exists in the original project, it will not be
     * overridden by a different value from the new project.
     *
     * @param r the name of a property in the new project to set in the original
     *          project. This may be a comma separate list of properties.
     */
    public void setReturn(String r) {
        returnName = r;
    }

    /**
     * Reference element identifying a data type to carry over to the new
     * project.
     *
     * @param r The feature to be added to the Reference attribute
     */
    public void addReference(Reference r) {
        references.add(r);
    }

    /**
     * Helper class that implements the nested &lt;reference&gt; element of
     * &lt;ant&gt; and &lt;antcall&gt;.
     *
     * @author <a href="mailto:danson@germane-software.com">Dale Anson</a>
     */
    public static class Reference
            extends org.apache.tools.ant.types.Reference {

        /**
         * Creates a reference to be configured by Ant.
         *
         * @param p  Project
         * @param id String
         */
        public Reference(Project p, String id) {
            super(p, id);
        }

        /**
         * Field targetid.
         */
        private String targetid = null;

        /**
         * Set the id that this reference to be stored under in the new project.
         *
         * @param targetid the id under which this reference will be passed to
         *                 the new project
         */
        public void setToRefid(String targetid) {
            this.targetid = targetid;
        }

        /**
         * Get the id under which this reference will be stored in the new project.
         *
         * @return the id of the reference in the new project.
         */
        public String getToRefid() {
            return targetid;
        }
    }
}
