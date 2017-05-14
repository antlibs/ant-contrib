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
package net.sf.antcontrib.antserver;

import static org.junit.Assert.assertTrue;

import net.sf.antcontrib.BuildFileTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Place class description here.
 *
 * @author		inger
 *
 */
public class AntServerTest extends BuildFileTestBase
{

    @Before
    public void setUp()
    {
        configureProject("antserver/antservertest.xml");
    }

    @After
    public void tearDown()
    {
        executeTarget("cleanup");
    }

    @Test
    public void test1()
    {
        String[] expected = new String[]
        {
            "Test1 Successfully Called",
            "[test1_remote]"
        };

        expectLogContaining("test1", expected);
    }

    @Test
    public void test2()
    {
        String[] expected = new String[]
        {
            "Test2 Successfully Called",
            "[test2_remote]"
        };

        expectLogContaining("test2", expected);
    }

    @Test
    public void test3()
    {
        String[] expected = new String[]
        {
            "Test3 Successfully Called",
            "[test3_remote]"
        };

        expectLogContaining("test3", expected);
    }

    @Test
    public void test4()
    {
        String[] expected = new String[]
        {
            "Test4 Successfully Called",
            "[test4_remote]"
        };

        expectLogContaining("test4", expected);
    }

    @Test
    public void test5()
    {
        this.executeTarget("test5");
    }

    /**
     * Assert that the given message has been logged with a priority
     * &gt;= INFO when running the given target.
     */
    protected void expectLogContaining(String target,
                                       String[] logs)
    {
        executeTarget(target);
        String realLog = getLog();

        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < logs.length; i++)
        {
            if (realLog.contains(logs[i]))
                cnt++;
            if (i != 0)
                sb.append(" and ");
            sb.append("\"").append(logs[i]).append("\"");
        }

        assertTrue("expecting log to contain " + sb.toString()
                + " log was \"" + realLog + "\"",
                cnt == logs.length);
    }

}
