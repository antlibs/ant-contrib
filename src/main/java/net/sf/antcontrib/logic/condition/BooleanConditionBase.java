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
package net.sf.antcontrib.logic.condition;

import java.lang.reflect.Field;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

/**
 * Extends ConditionBase so I can get access to the condition count and the
 * first condition. This is the class that the BooleanConditionTask is proxy
 * for.
 * <p>Developed for use with Antelope, migrated to ant-contrib Oct 2003.</p>
 *
 * @author <a href="mailto:danson@germane-software.com">Dale Anson</a>
 */
public class BooleanConditionBase extends ConditionBase {
    /**
     * Field conditions.
     */
    @SuppressWarnings("unused")
    private Vector<Condition> conditions = new Vector<Condition>();

    /**
     * Constructor.
     *
     * Copies parent conditions.
     */
    public BooleanConditionBase() {
        conditions = getParentConditions();
    }

    @SuppressWarnings("unchecked")
    private Vector<Condition> getParentConditions() {
        try {
            Field f = ConditionBase.class.getDeclaredField("conditions");
            f.setAccessible(true);
            return (Vector<Condition>) f.get(this);
        } catch (NoSuchFieldException e) {
            throw new BuildException(e);
        } catch (IllegalAccessException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Adds a feature to the IsPropertyTrue attribute of the
     * BooleanConditionBase object.
     *
     * @param i The feature to be added to the IsPropertyTrue attribute
     */
    public void addIsPropertyTrue(IsPropertyTrue i) {
        super.add(i);
    }

    /**
     * Adds a feature to the IsPropertyFalse attribute of the
     * BooleanConditionBase object.
     *
     * @param i The feature to be added to the IsPropertyFalse attribute
     */
    public void addIsPropertyFalse(IsPropertyFalse i) {
        super.add(i);
    }

    /**
     * Method addIsGreaterThan.
     *
     * @param i IsGreaterThan
     */
    public void addIsGreaterThan(IsGreaterThan i) {
        super.add(i);
    }

    /**
     * Method addIsLessThan.
     *
     * @param i IsLessThan
     */
    public void addIsLessThan(IsLessThan i) {
        super.add(i);
    }
}
