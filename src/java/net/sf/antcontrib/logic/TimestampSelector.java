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

import static java.lang.Math.min;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * Task definition for the foreach task.  The foreach task iterates
 * over a list, a list of filesets, or both.
 * <pre>
 *
 * Usage:
 *
 *   Task declaration in the project:
 *   <code>
 *     &lt;taskdef name="latesttimestamp" classname="net.sf.antcontrib.logic.TimestampSelector" /&gt;
 *   </code>
 *
 *   Call Syntax:
 *   <code>
 *     &lt;timestampselector
 *                 [property="prop" | outputsetref="id"]
 *                 [count="num"]
 *                 [age="eldest|youngest"]
 *                 [pathSep=","]
 *                 [pathref="ref"] &gt;
 *       &lt;path&gt;
 *          ...
 *       &lt;/path&gt;
 *     &lt;/latesttimestamp&gt;
 *   </code>
 *
 *   Attributes:
 *         outputsetref --&gt; The reference of the output Path set which will contain the
 *                          files with the latest timestamps.
 *         property     --&gt; The name of the property to set with file having the latest
 *                          timestamp.  If you specify the "count" attribute, you will get
 *                          the lastest N files.  These will be the absolute pathnames
 *         count        --&gt; How many of the latest files do you wish to find
 *         pathSep      --&gt; What to use as the path separator when using the "property"
 *                          attribute, in conjunction with the "count" attribute
 *         pathref      --&gt; The reference of the path which is the input set of files.
 *
 * </pre>
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class TimestampSelector extends Task {
    /**
     * Field AGE_ELDEST.
     * (value is ""eldest"")
     */
    private static final String AGE_ELDEST = "eldest";

    /**
     * Field AGE_YOUNGEST.
     * (value is ""youngest"")
     */
    private static final String AGE_YOUNGEST = "youngest";

    /**
     * Field property.
     */
    private String property;

    /**
     * Field path.
     */
    private Path path;

    /**
     * Field outputSetId.
     */
    private String outputSetId;

    /**
     * Field count.
     */
    private int count = 1;

    /**
     * Field pathSep.
     */
    private char pathSep = ',';

    /**
     * Field age.
     */
    private String age = AGE_YOUNGEST;

    /**
     * Constructor.
     */
    public TimestampSelector() {
        super();
    }

    /**
     * Method doFileSetExecute.
     *
     * @param paths String[]
     * @throws BuildException if something goes wrong
     */
    public void doFileSetExecute(String[] paths) throws BuildException {
    }

    /**
     * Method sort.
     * Sorts entire array
     *
     * @param array List&lt;File&gt;
     */
    public void sort(List<File> array) {
        sort(array, 0, array.size() - 1);
    }

    /**
     * Method sort.
     * Sorts partial array
     *
     * @param array List&lt;File&gt;
     * @param start int
     * @param end   int
     */
    protected void sort(List<File> array, int start, int end) {
        int p;
        if (end > start) {
            p = partition(array, start, end);
            sort(array, start, p - 1);
            sort(array, p + 1, end);
        }
    }

    /**
     * Method compare.
     *
     * @param a File
     * @param b File
     * @return int
     */
    protected int compare(File a, File b) {
        if (age.equalsIgnoreCase(AGE_ELDEST))
            return new Long(a.lastModified()).compareTo(new Long(b.lastModified()));
        else
            return new Long(b.lastModified()).compareTo(new Long(a.lastModified()));
    }

    /**
     * Method partition.
     *
     * @param array List&lt;File&gt;
     * @param start int
     * @param end   int
     * @return int
     */
    protected int partition(List<File> array, int start, int end) {
        int left;
        int right;
        File partitionElement;

        partitionElement = array.get(end);

        left = start - 1;
        right = end;
        for (;;) {
            while (compare(partitionElement, array.get(++left)) == 1) {
                if (left == end) break;
            }
            while (compare(partitionElement, array.get(--right)) == -1) {
                if (right == start) break;
            }
            if (left >= right) break;
            swap(array, left, right);
        }
        swap(array, left, end);

        return left;
    }

    /**
     * Method swap.
     *
     * @param array List&lt;File&gt;
     * @param i     int
     * @param j     int
     */
    protected void swap(List<File> array, int i, int j) {
        File temp;

        temp = array.get(i);
        array.set(i, array.get(j));
        array.set(j, temp);
    }

    /**
     * Method execute.
     *
     * @throws BuildException if attributes or elements are missing
     */
    public void execute() throws BuildException {
        if (property == null && outputSetId == null)
            throw new BuildException("Property or OutputSetId must be specified.");
        if (path == null)
            throw new BuildException("A path element or pathref attribute must be specified.");

        // Figure out the list of existing file elements
        // from the designated path
        List<File> v = new ArrayList<File>();
        for (String value : path.list()) {
            File f = new File(value);
            if (f.exists())
                v.add(f);
        }

        // Sort the vector, need to make java 1.1 compliant
        sort(v);

        // Pull off the first N items
        List<File> v2 = v.subList(0, min(count, v.size()));

        // Build the resulting Path object
        Path path = new Path(getProject());
        for (File f : v2) {
            Path p = new Path(getProject(), f.getAbsolutePath());
            path.addExisting(p);
        }

        if (outputSetId != null) {
            // Add the reference to the project
            getProject().addReference(outputSetId, path);
        } else {
            // Concat the paths, and put them in a property
            // which is separated list of the files, using the
            // "pathSep" attribute as the separator
            StringBuilder sb = new StringBuilder();
            for (String paths : path.list()) {
                if (sb.length() != 0) sb.append(pathSep);
                sb.append(paths);
            }

            if (sb.length() != 0)
                getProject().setProperty(property, sb.toString());
        }
    }

    /**
     * Method setProperty.
     *
     * @param property String
     */
    public void setProperty(String property) {
        if (outputSetId != null)
            throw new BuildException("Cannot set both Property and OutputSetId.");

        this.property = property;
    }

    /**
     * Method setCount.
     *
     * @param count int
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Method setAge.
     *
     * @param age String
     */
    public void setAge(String age) {
        if (age.equalsIgnoreCase(AGE_ELDEST)
                || age.equalsIgnoreCase(AGE_YOUNGEST))
            this.age = age;
        else
            throw new BuildException("Invalid age: " + age);
    }

    /**
     * Method setPathSep.
     *
     * @param pathSep char
     */
    public void setPathSep(char pathSep) {
        this.pathSep = pathSep;
    }

    /**
     * Method setOutputSetId.
     *
     * @param outputSetId String
     */
    public void setOutputSetId(String outputSetId) {
        if (property != null)
            throw new BuildException("Cannot set both Property and OutputSetId.");
        this.outputSetId = outputSetId;
    }

    /**
     * Method setPathRef.
     *
     * @param ref Reference
     * @throws BuildException if path element is specified
     */
    public void setPathRef(Reference ref) throws BuildException {
        if (path == null) {
            path = new Path(getProject());
            path.setRefid(ref);
        } else {
            throw new BuildException("Path element already specified.");
        }
    }

    /**
     * Method createPath.
     *
     * @return Path
     * @throws BuildException if path element is specified
     */
    public Path createPath() throws BuildException {
        if (path == null)
            path = new Path(getProject());
        else
            throw new BuildException("Path element already specified.");
        return path;
    }
}
