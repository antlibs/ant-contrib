/*
 * Copyright (c) 2001-2004, 2007 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.logic.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;

import net.sf.antcontrib.util.StringTools;

/**
 * Condition to test if the first argument is less than the
 * second argument. Will deal with base 10 integer and decimal numbers, otherwise,
 * treats arguments as Strings.
 * <p>Developed for use with Antelope, migrated to ant-contrib Oct 2003.</p>
 *
 * @author <a href="mailto:danson@germane-software.com">Dale Anson</a>
 * @version $Revision: 1.4 $
 */
public final class IsLessThan implements Condition {

    /**
     * Field arg1.
     */
    private String arg1;

    /**
     * Field arg2.
     */
    private String arg2;

    /**
     * Field trim.
     */
    private boolean trim = false;

    /**
     * Field caseSensitive.
     */
    private boolean caseSensitive = true;

    /**
     * Method setArg1.
     *
     * @param a1 String
     */
    public void setArg1(String a1) {
        arg1 = a1;
    }

    /**
     * Method setArg2.
     *
     * @param a2 String
     */
    public void setArg2(String a2) {
        arg2 = a2;
    }

    /**
     * Should we want to trim the arguments before comparing them?
     *
     * @param b boolean
     * @since Revision: 1.3, Ant 1.5
     */
    public void setTrim(boolean b) {
        trim = b;
    }

    /**
     * Should the comparison be case sensitive?
     *
     * @param b boolean
     * @since Revision: 1.3, Ant 1.5
     */
    public void setCasesensitive(boolean b) {
        caseSensitive = b;
    }

    /**
     * Method eval.
     *
     * @return boolean
     * @throws BuildException if arguments are missing
     * @see org.apache.tools.ant.taskdefs.condition.Condition#eval()
     */
    public boolean eval() throws BuildException {
        if (arg1 == null || arg2 == null) {
            throw new BuildException("both arg1 and arg2 are required in "
                    + "less than");
        }

        if (trim) {
            arg1 = StringTools.trim(arg1);
            arg2 = StringTools.trim(arg2);
        }

        // check if args are numbers
        try {
            double num1 = Double.parseDouble(arg1);
            double num2 = Double.parseDouble(arg2);
            return num1 < num2;
        } catch (NumberFormatException nfe) {
            // ignored, fall thru to string comparision
        }

        return caseSensitive ? arg1.compareTo(arg2) < 0 : arg1.compareToIgnoreCase(arg2) < 0;
    }

}
