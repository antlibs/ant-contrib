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

import org.apache.tools.ant.BuildFileRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Testcase for &lt;foreach&gt;.
 */
public class TimestampSelectorTest {
    @Rule
    public BuildFileRule buildRule = new BuildFileRule();

    /**
     * Method setUp.
     */
    @Before
    public void setUp() {
        buildRule.configureProject("src/test/resources/logic/timestampselector.xml");
    }

    /**
     * Method tearDown.
     */
    @After
    public void tearDown() {
        buildRule.executeTarget("teardown");
    }

    /**
     * Method testFileStampFL.
     */
    @Test
    public void testFileStampFL() {
        buildRule.executeTarget("filestamp.fl");
        assertThat(buildRule.getLog(), containsString("file2.txt"));
    }

    /**
     * Method testFileStampPR.
     */
    @Test
    public void testFileStampPR() {
        buildRule.executeTarget("filestamp.pr");
        assertThat(buildRule.getLog(), containsString("file2.txt"));
    }

    /**
     * Method testDirStampDL.
     */
    @Test
    public void testDirStampDL() {
        buildRule.executeTarget("dirstamp.dl");
        assertThat(buildRule.getLog(), containsString("dir2"));
    }

    /**
     * Method testDirStampPR.
     */
    @Test
    public void testDirStampPR() {
        buildRule.executeTarget("dirstamp.pr");
        assertThat(buildRule.getLog(), containsString("dir2"));
    }
}
