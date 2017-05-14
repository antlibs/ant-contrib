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
package net.sf.antcontrib.platform;

import static org.junit.Assert.assertTrue;

import net.sf.antcontrib.BuildFileTestBase;

import org.junit.Before;
import org.junit.Test;

/**
 * Testcase for &lt;shellscript&gt;.
 *
 * @author Peter Reilly
 */
public class ShellScriptTest extends BuildFileTestBase {

    @Before
    public void setUp() {
        configureProject("platform/shellscript.xml");
        staticInitialize();
    }

    @Test
    public void testShHello() {
        if (! hasSh)
            return;
        executeTarget("sh.hello");
        assertTrue(getLog().contains("hello world"));
     }

    @Test
    public void testBashHello() {
        if (! hasBash)
            return;
        executeTarget("bash.hello");
        assertTrue(getLog().contains("hello world"));
     }

    @Test
    public void testShInputString() {
        if (! hasSh)
            return;
        executeTarget("sh.inputstring");
        assertTrue(getLog().contains("hello world"));
     }

    @Test
    public void testShProperty() {
        if (! hasSh)
            return;
        executeTarget("sh.property");
        assertTrue(getLog().contains("this is a property"));
     }

    @Test
    public void testPythonHello() {
        if (! hasPython)
            return;
        executeTarget("python.hello");
        assertTrue(getLog().contains("hello world"));
    }

    @Test
    public void testPerlHello() {
        if (! hasPerl)
            return;
        executeTarget("perl.hello");
        assertTrue(getLog().contains("hello world"));
    }

    @Test
    public void testNoShell() {
        expectBuildExceptionContaining(
            "noshell", "Execute failed", "a shell that should not exist");
    }

    @Test
    public void testSed() {
        if (! hasSed)
            return;
        executeTarget("sed.test");
        assertTrue(getLog().contains("BAR bar bar bar BAR bar"));
    }

    @Test
    public void testSetProperty() {
        if (! hasSh)
            return;
        executeTarget("sh.set.property");
        assertPropertyEquals("sh.set.property", "hello world");
    }

    @Test
    public void testTmpSuffix() {
        if (! hasSh)
            return;
        executeTarget("sh.tmp.suffix");
        assertTrue(getLog().contains(".bat"));
    }

    @Test
    public void testCmd() {
        if (! hasCmd)
            return;
        executeTarget("cmd.test");
        assertTrue(getLog().contains("hello world"));
    }

    @Test
    public void testDir() {
        if (! hasBash)
            return;
        executeTarget("dir.test");
        assertTrue(
                getProject().getProperty("dir.test.property").contains("subdir"));
    }

    @Test
    public void testCommand() {
        expectBuildExceptionContaining(
            "command.test", "Attribute failed",
            "Attribute command is not supported");
    }

    private static boolean initialized = false;
    private static boolean hasSh       = false;
    private static boolean hasBash     = false;
    private static boolean hasPython   = false;
    private static boolean hasPerl     = false;
    private static boolean hasSed      = false;
    private static boolean hasCmd      = false;
    private static final Object staticMonitor = new Object();

    /**
     * check if the env contains the shells
     *    sh, bash, python and perl
     *    assume cmd.exe exists for windows
     */
    private void staticInitialize() {
        synchronized (staticMonitor) {
            if (initialized)
                return;
            initialized = true;
            hasSh = hasShell("hassh");
            hasBash = hasShell("hasbash");
            hasPerl = hasShell("hasperl");
            hasPython = hasShell("haspython");
            hasSed = hasShell("hassed");
            hasCmd = hasShell("hascmd");

        }
    }

    private boolean hasShell(String target) {
        try {
            executeTarget(target);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

}
