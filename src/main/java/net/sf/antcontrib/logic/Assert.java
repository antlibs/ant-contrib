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

import net.sf.antcontrib.logic.condition.BooleanConditionBase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Exit;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.Equals;

/**
 * Assert class.
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class Assert extends BooleanConditionBase {
    /**
     * Field message.
     */
    private String message;

    /**
     * Field failOnError.
     */
    private boolean failOnError = true;

    /**
     * Field execute.
     */
    private boolean execute = true;

    /**
     * Field sequential.
     */
    private Sequential sequential;

    /**
     * Field name.
     */
    private String name;

    /**
     * Field value.
     */
    private String value;

    /**
     * Method createSequential.
     *
     * @return Sequential
     */
    public Sequential createSequential() {
        this.sequential = (Sequential) getProject().createTask("sequential");
        return this.sequential;
    }

    /**
     * Method setName.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method setValue.
     *
     * @param value String
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Method setMessage.
     *
     * @param message String
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Method createBool.
     *
     * @return BooleanConditionBase
     */
    public BooleanConditionBase createBool() {
        return this;
    }

    /**
     * Method setExecute.
     *
     * @param execute boolean
     */
    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    /**
     * Method setFailOnError.
     *
     * @param failOnError boolean
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * Method execute.
     */
    public void execute() {
        String enable = getProject().getProperty("ant.enable.asserts");
        boolean assertsEnabled = Project.toBoolean(enable);

        if (assertsEnabled) {
            if (name != null) {
                if (value == null) {
                    throw new BuildException("The 'value' attribute must accompany the 'name' attribute.");
                }
                String propVal = getProject().replaceProperties("${" + name + "}");
                Equals e = new Equals();
                e.setArg1(propVal);
                e.setArg2(value);
                addEquals(e);
            }

            if (countConditions() == 0) {
                throw new BuildException("There is no condition specified.");
            } else if (countConditions() > 1) {
                throw new BuildException("There must be exactly one condition specified.");
            }

            Condition c = (Condition) getConditions().nextElement();
            if (!c.eval()) {
                if (failOnError) {
                    Exit fail = (Exit) getProject().createTask("fail");
                    fail.setMessage(message);
                    fail.execute();
                }
            } else if (execute && sequential != null) {
                this.sequential.execute();
            }
        } else if (execute && sequential != null) {
            this.sequential.execute();
        }
    }
}
