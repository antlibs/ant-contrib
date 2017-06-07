/*
 * Copyright (c) 2003-2007 Ant-Contrib project.  All rights reserved.
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroInstance;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/**
 * Task definition for the for task.  This is based on
 * the foreach task but takes a sequential element
 * instead of a target and only works for Ant 1.7+
 * since it adds support for ResourceCollections.
 *
 * @author <a href="mailto:peterreilly@users.sf.net">Peter Reilly</a>
 * @author <a href="mailto:mattinger@yahoo.com">Matt Inger</a>
 */
public class ForTask extends Task {
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
    private String delimiter = ",";

    /**
     * Field trim.
     */
    private boolean trim;

    /**
     * Field keepgoing.
     */
    private boolean keepgoing = false;

    /**
     * Field macroDef.
     */
    private MacroDef macroDef;

    /**
     * Field hasIterators.
     */
    private final List<HasIterator> hasIterators = new ArrayList<HasIterator>();

    /**
     * Field parallel.
     */
    private boolean parallel = false;

    /**
     * Field threadCount.
     */
    private Integer threadCount;

    /**
     * Field parallelTasks.
     */
    private Parallel parallelTasks;

    /**
     * Field begin.
     */
    private int begin = 0;

    /**
     * Field end.
     */
    private Integer end = null;

    /**
     * Field step.
     */
    private int step = 1;

    /**
     * Field resourceCollections.
     */
    private List<ResourceCollection> resourceCollections = new ArrayList<ResourceCollection>();

    /**
     * Field taskCount.
     */
    private int taskCount = 0;

    /**
     * Field errorCount.
     */
    private int errorCount = 0;

    /**
     * Creates a new <code>For</code> instance.
     */
    public ForTask() {
    }

    /***
     * Add a resource collection
     * @param resourceCollection The resource collection to add
     * @since Ant 1.7
     */
    public void add(ResourceCollection resourceCollection) {
        this.resourceCollections.add(resourceCollection);
    }

    /**
     * Attribute whether to execute the loop in parallel or in sequence.
     *
     * @param parallel if true execute the tasks in parallel. Default is false.
     */
    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    /**
     * Set the maximum amount of threads we're going to allow
     * to execute in parallel.
     *
     * @param threadCount the number of threads to use
     */
    public void setThreadCount(int threadCount) {
        if (threadCount < 1) {
            throw new BuildException("Illegal value for threadCount " + threadCount
                    + " it should be > 0");
        }
        this.threadCount = threadCount;
    }

    /**
     * Set the trim attribute.
     *
     * @param trim if true, trim the value for each iterator.
     */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    /**
     * Set the keepgoing attribute, indicating whether we
     * should stop on errors or continue heedlessly onward.
     *
     * @param keepgoing a boolean, if <code>true</code> then we act in
     *                  the keepgoing manner described.
     */
    public void setKeepgoing(boolean keepgoing) {
        this.keepgoing = keepgoing;
    }

    /**
     * Set the list attribute.
     *
     * @param list a list of delimiter separated tokens.
     */
    public void setList(String list) {
        this.list = list;
    }

    /**
     * Set the delimiter attribute.
     *
     * @param delimiter the delimiter used to separate the tokens in
     *                  the list attribute. The default is ",".
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Set the param attribute.
     * This is the name of the macrodef attribute that
     * gets set for each iterator of the sequential element.
     *
     * @param param the name of the macrodef attribute.
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * createSequential() method.
     *
     * @return a MacroDef#NestedSequential object to be configured
     */
    public MacroDef.NestedSequential createSequential() {
        macroDef = new MacroDef();
        macroDef.setProject(getProject());
        return macroDef.createSequential();
    }

    /**
     * Set begin attribute.
     *
     * @param begin the value to use.
     */
    public void setBegin(int begin) {
        this.begin = begin;
    }

    /**
     * Set end attribute.
     *
     * @param end the value to use.
     */
    public void setEnd(Integer end) {
        this.end = end;
    }

    /**
     * Set step attribute.
     *
     * @param step The increment step when using begin and end attributes.
     */
    public void setStep(int step) {
        this.step = step;
    }

    /**
     * Run the for task.
     * This checks the attributes and nested elements, and
     * if there are ok, it calls doTheTasks()
     * which constructs a macrodef task and a
     * for each iteration a macrodef instance.
     */
    public void execute() {
        if (parallel) {
            parallelTasks = (Parallel) getProject().createTask("parallel");
            if (threadCount != null) {
                parallelTasks.setThreadCount(threadCount);
            }
        }
        if (list == null && resourceCollections.isEmpty() && hasIterators.size() == 0
                && end == null) {
            throw new BuildException(
                    "You must have a list or resources or sequence to iterate through");
        }
        if (param == null) {
            throw new BuildException(
                    "You must supply a property name to set on"
                            + " each iteration in param");
        }
        if (macroDef == null) {
            throw new BuildException(
                    "You must supply an embedded sequential "
                            + "to perform");
        }
        if (end != null) {
            int iEnd = end;
            if (step == 0) {
                throw new BuildException("step cannot be 0");
            } else if (iEnd > begin && step < 0) {
                throw new BuildException("end > begin, step needs to be > 0");
            } else if (iEnd <= begin && step > 0) {
                throw new BuildException("end <= begin, step needs to be < 0");
            }
        }
        doTheTasks();
        if (parallel) {
            parallelTasks.perform();
        }
    }

    /**
     * Method doSequentialIteration.
     *
     * @param val String
     */
    private void doSequentialIteration(String val) {
        MacroInstance instance = new MacroInstance();
        instance.setProject(getProject());
        instance.setOwningTarget(getOwningTarget());
        instance.setMacroDef(macroDef);
        instance.setDynamicAttribute(param.toLowerCase(),
                val);
        if (!parallel) {
            instance.execute();
        } else {
            parallelTasks.addTask(instance);
        }
    }

    /**
     * Method doToken.
     *
     * @param tok String
     */
    private void doToken(String tok) {
        try {
            taskCount++;
            doSequentialIteration(tok);
        } catch (BuildException bx) {
            if (keepgoing) {
                log(tok + ": " + bx.getMessage(), Project.MSG_ERR);
                errorCount++;
            } else {
                throw bx;
            }
        }
    }

    /**
     * Method doTheTasks.
     */
    private void doTheTasks() {
        errorCount = 0;
        taskCount = 0;

        // Create a macro attribute
        if (macroDef.getAttributes().isEmpty()) {
            MacroDef.Attribute attribute = new MacroDef.Attribute();
            attribute.setName(param);
            macroDef.addConfiguredAttribute(attribute);
        }

        // Take Care of the list attribute
        if (list != null) {
            StringTokenizer st = new StringTokenizer(list, delimiter);

            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                if (trim) {
                    tok = tok.trim();
                }
                doToken(tok);
            }
        }

        // Take care of the begin/end/step attributes
        if (end != null) {
            int iEnd = end;
            if (step > 0) {
                for (int i = begin; i < (iEnd + 1); i = i + step) {
                    doToken("" + i);
                }
            } else {
                for (int i = begin; i > (iEnd - 1); i = i + step) {
                    doToken("" + i);
                }
            }
        }

        // Take care of resources
        for (ResourceCollection collection : resourceCollections) {
            for (Resource resource : collection) {
                doToken(resource.toString());
            }
        }

        // Take care of iterators
        for (HasIterator hasIterator : hasIterators) {
            Iterator<Object> it = hasIterator.iterator();
            while (it.hasNext()) {
                doToken(it.next().toString());
            }
        }

        if (keepgoing && (errorCount != 0)) {
            throw new BuildException(
                    "Keepgoing execution: " + errorCount
                            + " of " + taskCount + " iterations failed.");
        }
    }

    /**
     * Add a Map, iterate over the values.
     *
     * @param map a Map object - iterate over the values.
     */
    public void add(Map<Object, Object> map) {
        hasIterators.add(new MapIterator(map));
    }

    /**
     * Add a collection that can be iterated over.
     *
     * @param collection a <code>Collection</code> value.
     */
    public void add(Collection<Object> collection) {
        hasIterators.add(new ReflectIterator(collection));
    }

    /**
     * Add an iterator to be iterated over.
     *
     * @param iterator an <code>Iterator</code> value
     */
    public void add(Iterator<Object> iterator) {
        hasIterators.add(new IteratorIterator(iterator));
    }

    /**
     * Add an object that has an Iterator iterator() method
     * that can be iterated over.
     *
     * @param obj An object that can be iterated over.
     */
    public void add(Object obj) {
        hasIterators.add(new ReflectIterator(obj));
    }

    /**
     * Interface for the objects in the iterator collection.
     */
    public interface HasIterator {
        /**
         * Method iterator.
         *
         * @return Iterator&lt;Object&gt;
         */
        Iterator<Object> iterator();
    }

    /**
     */
    private static class IteratorIterator implements HasIterator {
        /**
         * Field iterator.
         */
        private final Iterator<Object> iterator;

        /**
         * Constructor for IteratorIterator.
         *
         * @param iterator Iterator&lt;Object&gt;
         */
        public IteratorIterator(Iterator<Object> iterator) {
            this.iterator = iterator;
        }

        /**
         * Method iterator.
         *
         * @return Iterator&lt;Object&gt;
         */
        public Iterator<Object> iterator() {
            return this.iterator;
        }
    }

    /**
     */
    private static class MapIterator implements HasIterator {
        /**
         * Field map.
         */
        private final Map<Object, Object> map;

        /**
         * Constructor for MapIterator.
         *
         * @param map Map&lt;Object,Object&gt;
         */
        public MapIterator(Map<Object, Object> map) {
            this.map = map;
        }

        /**
         * Method iterator.
         *
         * @return Iterator&lt;Object&gt;
         */
        public Iterator<Object> iterator() {
            return map.values().iterator();
        }
    }

    /**
     */
    private static class ReflectIterator implements HasIterator {
        /**
         * Field obj.
         */
        private final Object obj;
        /**
         * Field method.
         */
        private Method method;

        /**
         * Constructor for ReflectIterator.
         *
         * @param obj Object
         */
        public ReflectIterator(Object obj) {
            this.obj = obj;
            try {
                method = obj.getClass().getMethod(
                        "iterator", new Class[]{});
            } catch (Throwable t) {
                throw new BuildException(
                        "Invalid type " + obj.getClass() + " used in For task, it does"
                                + " not have a public iterator method");
            }
        }

        /**
         * Method iterator.
         *
         * @return Iterator&lt;Object&gt;
         */
        public Iterator<Object> iterator() {
            try {
                return (Iterator<Object>) method.invoke(obj, new Object[]{});
            } catch (Throwable t) {
                throw new BuildException(t);
            }
        }
    }
}
