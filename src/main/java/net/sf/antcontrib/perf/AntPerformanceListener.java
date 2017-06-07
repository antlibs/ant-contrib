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
package net.sf.antcontrib.perf;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * This BuildListener keeps track of the total time it takes for each target
 * and task to execute, then prints out the totals when the build is finished.
 * This can help pinpoint the areas where a build is taking a lot of time so
 * optimization efforts can focus where they'll do the most good. Execution
 * times are grouped by targets and tasks, and are sorted from fastest running
 * to slowest running.
 * <p>Output can be saved to a file by setting a property in Ant. Set
 * "<code>performance.log</code>" to the name of a file. This can be set either
 * on the command line with the <code>-D</code> option
 * (<code>-Dperformance.log=/tmp/performance.log</code>)
 * or in the build file itself (<code>&lt;property name="performance.log"
 * location="/tmp/performance.log"/&gt;</code>).</p>
 * <p>Developed for use with Antelope, migrated to ant-contrib Oct 2003.</p>
 *
 * @author <a href="mailto:danson@germane-software.com">Dale Anson</a>
 * @version $Revision: 1.5 $
 */
public class AntPerformanceListener implements BuildListener {
    /**
     * Field targetStats.
     */
    private HashMap<Target, StopWatch> targetStats = new HashMap<Target, StopWatch>();

    /**
     * Field taskStats.
     */
    private HashMap<Task, StopWatch> taskStats = new HashMap<Task, StopWatch>();

    /**
     * Field master.
     */
    private StopWatch master = null;

    /**
     * Field start_time.
     */
    private long start_time = 0;

    /**
     * Starts a 'running total' stopwatch.
     *
     * @param be BuildEvent
     * @see org.apache.tools.ant.BuildListener#buildStarted(BuildEvent)
     */
    public void buildStarted(BuildEvent be) {
        master = new StopWatch();
        start_time = master.start();
    }

    /**
     * Sorts and prints the results.
     *
     * @param be BuildEvent
     * @see org.apache.tools.ant.BuildListener#buildFinished(BuildEvent)
     */
    public void buildFinished(BuildEvent be) {
        long stop_time = master.stop();

        // sort targets, key is StopWatch, value is Target
        TreeMap<StopWatch, Target> sortedTargets = new TreeMap<StopWatch, Target>(new StopWatchComparator());
        for (Target key : targetStats.keySet()) {
            StopWatch value = targetStats.get(key);
            sortedTargets.put(value, key);
        }

        // sort tasks, key is StopWatch, value is Task
        TreeMap<StopWatch, Task> sortedTasks = new TreeMap<StopWatch, Task>(new StopWatchComparator());
        for (Task key : taskStats.keySet()) {
            StopWatch value = taskStats.get(key);
            sortedTasks.put(value, key);
        }

        // print the sorted results
        StringBuilder msg = new StringBuilder();
        String lSep = System.getProperty("line.separator");
        msg.append(lSep).append("Statistics:").append(lSep);
        msg.append("-------------- Target Results ---------------------").append(lSep);
        for (StopWatch key : sortedTargets.keySet()) {
            StringBuilder sb = new StringBuilder();
            Target target = sortedTargets.get(key);
            if (target != null) {
                Project p = target.getProject();
                if (p != null && p.getName() != null) {
                    sb.append(p.getName()).append(".");
                }
                String total = format(key.total());
                String target_name = target.getName();
                if (target_name == null || target_name.length() == 0) {
                    target_name = "<implicit>";
                }
                sb.append(target_name).append(": ").append(total);
            }
            msg.append(sb.toString()).append(lSep);
        }
        msg.append(lSep);
        msg.append("-------------- Task Results -----------------------").append(lSep);
        for (StopWatch key : sortedTasks.keySet()) {
            Task task = sortedTasks.get(key);
            StringBuilder sb = new StringBuilder();
            Target target = task.getOwningTarget();
            if (target != null) {
                Project p = target.getProject();
                if (p != null && p.getName() != null) {
                    sb.append(p.getName()).append(".");
                }
                String target_name = target.getName();
                if (target_name == null || target_name.length() == 0) {
                    target_name = "<implicit>";
                }
                sb.append(target_name).append(".");
            }
            sb.append(task.getTaskName()).append(": ").append(format(key.total()));
            msg.append(sb.toString()).append(lSep);
        }

        msg.append(lSep);
        msg.append("-------------- Totals -----------------------------").append(lSep);
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss.SSS");
        msg.append("Start time: ").append(format.format(new Date(start_time))).append(lSep);
        msg.append("Stop time: ").append(format.format(new Date(stop_time))).append(lSep);
        msg.append("Total time: ").append(format(master.total())).append(lSep);
        System.out.println(msg.toString());

        // write stats to file?
        Project p = be.getProject();
        File outfile = null;
        if (p != null) {
            String f = p.getProperty("performance.log");
            if (f != null) {
                outfile = new File(f);
            }
        }
        if (outfile != null) {
            try {
                FileWriter fw = new FileWriter(outfile);
                fw.write(msg.toString());
                fw.flush();
                fw.close();
                System.out.println("Wrote stats to: " + outfile.getAbsolutePath() + lSep);
            } catch (Exception e) {
                // ignored
            }
        }

        // reset the stats registers

        targetStats = new HashMap<Target, StopWatch>();
        taskStats = new HashMap<Task, StopWatch>();
    }

    /**
     * Formats the milliseconds from a StopWatch into decimal seconds.
     *
     * @param ms long
     * @return String
     */
    private String format(long ms) {
        String total = String.valueOf(ms);
        String frontpad = "000";
        int pad_length = 3 - total.length();
        if (pad_length >= 0) {
            total = "0." + frontpad.substring(0, pad_length) + total;
        } else {
            total = total.substring(0, total.length() - 3) + "."
                    + total.substring(total.length() - 3);
        }
        return total + " sec";
    }

    /**
     * Start timing the given target.
     *
     * @param be BuildEvent
     * @see org.apache.tools.ant.BuildListener#targetStarted(BuildEvent)
     */
    public void targetStarted(BuildEvent be) {
        StopWatch sw = new StopWatch();
        sw.start();
        targetStats.put(be.getTarget(), sw);
    }

    /**
     * Stop timing the given target.
     *
     * @param be BuildEvent
     * @see org.apache.tools.ant.BuildListener#targetFinished(BuildEvent)
     */
    public void targetFinished(BuildEvent be) {
        StopWatch sw = targetStats.get(be.getTarget());
        sw.stop();
    }

    /**
     * Start timing the given task.
     *
     * @param be BuildEvent
     * @see org.apache.tools.ant.BuildListener#taskStarted(BuildEvent)
     */
    public void taskStarted(BuildEvent be) {
        StopWatch sw = new StopWatch();
        sw.start();
        taskStats.put(be.getTask(), sw);
    }

    /**
     * Stop timing the given task.
     *
     * @param be BuildEvent
     * @see org.apache.tools.ant.BuildListener#taskFinished(BuildEvent)
     */
    public void taskFinished(BuildEvent be) {
        StopWatch sw = taskStats.get(be.getTask());
        if (sw != null) {
            sw.stop();
        }
    }

    /**
     * no-op.
     *
     * @param be BuildEvent
     * @see org.apache.tools.ant.BuildListener#messageLogged(BuildEvent)
     */
    public void messageLogged(BuildEvent be) {
        // does nothing
    }

    /**
     * Compares the total times for two StopWatches.
     */
    public class StopWatchComparator implements Comparator<StopWatch> {
        /**
         * Compares the total times for two StopWatches.
         *
         * @param a StopWatch
         * @param b StopWatch
         * @return int
         */
        public int compare(StopWatch a, StopWatch b) {
            return a.total() < b.total() ? -1 : a.total() == b.total() ? 0 : 1;
        }
    }

    /**
     * A stopwatch, useful for 'quick and dirty' performance testing.
     *
     * <a href="mailto:danson@germane-software.com">Dale Anson</a>
     * @version $Revision: 1.5 $
     */
    public class StopWatch {

        /**
         * storage for start time.
         */
        private long _start_time = 0;

        /**
         * storage for stop time.
         */
        @SuppressWarnings("unused")
        private long _stop_time = 0;

        /**
         * cumulative elapsed time.
         */
        private long _total_time = 0;

        /**
         * Starts the stopwatch.
         */
        public StopWatch() {
            start();
        }

        /**
         * Starts/restarts the stopwatch.
         *
         * @return the start time, the long returned System.currentTimeMillis().
         */
        public long start() {
            _start_time = System.currentTimeMillis();
            return _start_time;
        }

        /**
         * Stops the stopwatch.
         *
         * @return the stop time, the long returned System.currentTimeMillis().
         */
        public long stop() {
            long stop_time = System.currentTimeMillis();
            _total_time += stop_time - _start_time;
            _start_time = 0;
            _stop_time = 0;
            return stop_time;
        }

        /**
         * Total cumulative elapsed time.
         *
         * @return the total time
         */
        public long total() {
            return _total_time;
        }

        /**
         * Elapsed time, difference between the last start time and now.
         *
         * @return the elapsed time
         */
        public long elapsed() {
            return System.currentTimeMillis() - _start_time;
        }
    }

    // quick test for the formatter

    /**
     * Method main.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        AntPerformanceListener apl = new AntPerformanceListener();

        System.out.println(apl.format(1));
        System.out.println(apl.format(10));
        System.out.println(apl.format(100));
        System.out.println(apl.format(1000));
        System.out.println(apl.format(100000));
        System.out.println(apl.format(1000000));
        System.out.println(apl.format(10000000));
        System.out.println(apl.format(100000000));
        System.out.println(apl.format(1000000000));
    }
}
