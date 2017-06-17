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
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.CallTarget;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;

import net.sf.antcontrib.util.ThreadPool;
import net.sf.antcontrib.util.ThreadPoolThread;

/**
 * Task definition for the foreach task.  The foreach task iterates
 * over a list, a list of filesets, or both.
 * <pre>
 *
 * Usage:
 *
 *   Task declaration in the project:
 *   <code>
 *     &lt;taskdef name="foreach" classname="net.sf.antcontrib.logic.ForEach" /&gt;
 *   </code>
 *
 *   Call Syntax:
 *   <code>
 *     &lt;foreach list="values" target="target" param="name"
 *                 [parallel="true|false"]
 *                 [delimiter="delim"] /&gt;
 *   </code>
 *
 *   Attributes:
 *         list      --&gt; The list of values to process, with the delimiter character,
 *                       indicated by the "delim" attribute, separating each value
 *         target    --&gt; The target to call for each token, passing the token as the
 *                       parameter with the name indicated by the "param" attribute
 *         param     --&gt; The name of the parameter to pass the tokens in as to the
 *                       target
 *         delimiter --&gt; The delimiter string that separates the values in the "list"
 *                       parameter.  The default is ","
 *         parallel  --&gt; Should all targets execute in parallel.  The default is false.
 *         trim      --&gt; Should we trim the list item before calling the target?
 *
 * </pre>
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class ForEach extends Task {
    /**
     * Field list.
     */
    private String list;

    /**
     * Field param.
     */
    private String param;

    /**
     * Field delimiter.
     */
    private String delimiter;

    /**
     * Field target.
     */
    private String target;

    /**
     * Field inheritAll.
     */
    private boolean inheritAll;
    /**
     * Field inheritRefs.
     */
    private boolean inheritRefs;

    /**
     * Field params.
     */
    private final List<Property> params;

    /**
     * Field references.
     */
    private final List<Ant.Reference> references;

    /**
     * Field currPath.
     */
    private Path currPath;

    /**
     * Field parallel.
     */
    private boolean parallel;

    /**
     * Field trim.
     */
    private boolean trim;

    /**
     * Field maxThreads.
     */
    private int maxThreads;

    /**
     * Field mapper.
     */
    private Mapper mapper;

    /**
     * Constructor.
     */
    public ForEach() {
        super();
        this.list = null;
        this.param = null;
        this.delimiter = ",";
        this.target = null;
        this.inheritAll = false;
        this.inheritRefs = false;
        this.params = new ArrayList<Property>();
        this.references = new ArrayList<Ant.Reference>();
        this.parallel = false;
        this.maxThreads = 5;
    }

    /**
     * Method executeParallel.
     *
     * @param tasks List&lt;CallTarget&gt;
     */
    private void executeParallel(List<CallTarget> tasks) {
        ThreadPool pool = new ThreadPool(maxThreads);
        Runnable r = null;
        List<ThreadPoolThread> threads = new ArrayList<ThreadPoolThread>();

        // start each task in it's own thread, using the
        // pool to ensure that we don't exceed the maximum
        // amount of threads
        for (final Task task : tasks) {
            // Create the Runnable object
            r = new Runnable() {
                public void run() {
                    task.execute();
                }
            };

            // Get a thread, and start the task.
            // If there is no thread available, this will
            // block until one becomes available
            try {
                ThreadPoolThread tpt = pool.borrowThread();
                tpt.setRunnable(r);
                tpt.start();
                threads.add(tpt);
            } catch (Exception ex) {
                throw new BuildException(ex);
            }

        }

        // Wait for all threads to finish before we
        // are allowed to return.
        for (Thread t : threads) {
            if (t.isAlive()) {
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    throw new BuildException(ex);
                }
            }
        }
    }

    /**
     * Method executeSequential.
     *
     * @param tasks List&lt;CallTarget&gt;
     */
    private void executeSequential(List<CallTarget> tasks) {
        TaskContainer tc = (TaskContainer) getProject().createTask("sequential");
        for (Task t : tasks) {
            tc.addTask(t);
        }

        ((Task) tc).execute();
    }

    /**
     * Method execute.
     *
     * @throws BuildException if something goes wrong
     */
    public void execute() throws BuildException {
        if (list == null && currPath == null) {
            throw new BuildException("You must have a list or path to iterate through");
        }
        if (param == null) {
            throw new BuildException("You must supply a property name to set on each iteration in param");
        }
        if (target == null) {
            throw new BuildException("You must supply a target to perform");
        }

        List<Object> values = new ArrayList<Object>();

        // Take Care of the list attribute
        if (list != null) {
            StringTokenizer st = new StringTokenizer(list, delimiter);

            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                if (trim) {
                    tok = tok.trim();
                }
                values.add(tok);
            }
        }

        if (currPath != null) {
            for (String pathElement : currPath.list()) {
                if (mapper != null) {
                    FileNameMapper m = mapper.getImplementation();
                    String[] mapped = m.mapFileName(pathElement);
                    Collections.addAll(values, mapped);
                } else {
                    values.add(new File(pathElement));
                }
            }
        }

        List<CallTarget> tasks = new ArrayList<CallTarget>();

        for (Object val : values) {
            CallTarget ct = createCallTarget();
            Property p = ct.createParam();
            p.setName(param);

            if (val instanceof File) {
                p.setLocation((File) val);
            } else {
                p.setValue((String) val);
            }

            tasks.add(ct);
        }

        if (parallel && maxThreads > 1) {
            executeParallel(tasks);
        } else {
            executeSequential(tasks);
        }
    }

    /**
     * Method setTrim.
     *
     * @param trim boolean
     */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    /**
     * Method setList.
     *
     * @param list String
     */
    public void setList(String list) {
        this.list = list;
    }

    /**
     * Method setDelimiter.
     *
     * @param delimiter String
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Method setParam.
     *
     * @param param String
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * Method setTarget.
     *
     * @param target String
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Method setParallel.
     *
     * @param parallel boolean
     */
    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    /**
     * Corresponds to <code>&lt;antcall&gt;</code>'s <code>inheritall</code>
     * attribute.
     *
     * @param b boolean
     */
    public void setInheritall(boolean b) {
        this.inheritAll = b;
    }

    /**
     * Corresponds to <code>&lt;antcall&gt;</code>'s <code>inheritrefs</code>
     * attribute.
     *
     * @param b boolean
     */
    public void setInheritrefs(boolean b) {
        this.inheritRefs = b;
    }

    /**
     * Set the maximum amount of threads we're going to allow
     * at once to execute.
     *
     * @param maxThreads int
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * Corresponds to <code>&lt;antcall&gt;</code>'s nested
     * <code>&lt;param&gt;</code> element.
     *
     * @param p Property
     */
    public void addParam(Property p) {
        params.add(p);
    }

    /**
     * Corresponds to <code>&lt;antcall&gt;</code>'s nested
     * <code>&lt;reference&gt;</code> element.
     *
     * @param r Ant.Reference
     */
    public void addReference(Ant.Reference r) {
        references.add(r);
    }

    /**
     * @param set FileSet
     * @deprecated Use createPath instead.
     */
    public void addFileset(FileSet set) {
        log("The nested fileset element is deprecated, use a nested path "
                        + "instead",
                Project.MSG_WARN);
        createPath().addFileset(set);
    }

    /**
     * Method createPath.
     *
     * @return Path
     */
    public Path createPath() {
        if (currPath == null) {
            currPath = new Path(getProject());
        }
        return currPath;
    }

    /**
     * Method createMapper.
     *
     * @return Mapper
     */
    public Mapper createMapper() {
        mapper = new Mapper(getProject());
        return mapper;
    }

    /**
     * Method createCallTarget.
     *
     * @return CallTarget
     */
    private CallTarget createCallTarget() {
        CallTarget ct = (CallTarget) getProject().createTask("antcall");
        ct.setOwningTarget(getOwningTarget());
        ct.init();
        ct.setTarget(target);
        ct.setInheritAll(inheritAll);
        ct.setInheritRefs(inheritRefs);
        for (Property param : params) {
            Property toSet = ct.createParam();
            toSet.setName(param.getName());
            if (param.getValue() != null) {
                toSet.setValue(param.getValue());
            }
            if (param.getFile() != null) {
                toSet.setFile(param.getFile());
            }
            if (param.getResource() != null) {
                toSet.setResource(param.getResource());
            }
            if (param.getPrefix() != null) {
                toSet.setPrefix(param.getPrefix());
            }
            if (param.getRefid() != null) {
                toSet.setRefid(param.getRefid());
            }
            if (param.getEnvironment() != null) {
                toSet.setEnvironment(param.getEnvironment());
            }
            if (param.getClasspath() != null) {
                toSet.setClasspath(param.getClasspath());
            }
        }

        for (Ant.Reference r : references) {
            ct.addReference(r);
        }

        return ct;
    }

    /**
     * Method handleOutput.
     *
     * @param line String
     */
    protected void handleOutput(String line) {
        try {
            super.handleOutput(line);
        } catch (IllegalAccessError e) {
            // This is needed so we can run with 1.5 and 1.5.1
            super.handleOutput(line);
        }
    }

    /**
     * Method handleErrorOutput.
     *
     * @param line String
     */
    protected void handleErrorOutput(String line) {
        try {
            super.handleErrorOutput(line);
        } catch (IllegalAccessError e) {
            // This is needed so we can run with 1.5 and 1.5.1
            super.handleErrorOutput(line);
        }
    }

}
