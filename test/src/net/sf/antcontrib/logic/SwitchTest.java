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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.sf.antcontrib.BuildFileTestBase;

import org.junit.Before;
import org.junit.Test;

/**
 * Testcase for &lt;switch&gt;.
 */
public class SwitchTest extends BuildFileTestBase {

    @Before
    public void setUp() {
        configureProject("logic/switch.xml");
    }

    @Test
    public void testNoValue() {
        expectSpecificBuildException("noValue", "no value",
                                     "Value is missing");
    }

    @Test
    public void testNoChildren() {
        expectSpecificBuildException("noChildren", "no children",
                                     "No cases supplied");
    }

    @Test
    public void testTwoDefaults() {
        expectSpecificBuildException("twoDefaults", "two defaults",
                                     "Cannot specify multiple default cases");
    }

    @Test
    public void testNoMatch() {
        expectSpecificBuildException("noMatch", "no match",
                                     "No case matched the value foo"
                                     + " and no default has been specified.");
    }

    @Test
    public void testCaseNoValue() {
        expectSpecificBuildException("caseNoValue", "<case> no value",
                                     "Value is required for case.");
    }

    @Test
    public void testDefault() {
        executeTarget("testDefault");
        assertTrue(getLog().contains("In default"));
        assertTrue(getLog().contains("baz"));
        assertEquals(-1, getLog().indexOf("${inner}"));
        assertEquals(-1, getLog().indexOf("In case"));
    }

    @Test
    public void testCase() {
        executeTarget("testCase");
        assertTrue(getLog().contains("In case"));
        assertTrue(getLog().contains("baz"));
        assertEquals(-1, getLog().indexOf("${inner}"));
        assertEquals(-1, getLog().indexOf("In default"));
    }

    @Test
    public void testCaseSensitive() {
        executeTarget("testCaseSensitive");
        assertTrue(getLog().contains("In default"));
        assertEquals(-1, getLog().indexOf("In case"));
    }

    @Test
    public void testCaseInSensitive() {
        executeTarget("testCaseInSensitive");
        assertTrue(getLog().contains("In case"));
        assertEquals(-1, getLog().indexOf("In default"));
    }

}
