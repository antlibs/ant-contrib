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

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.taskdefs.Execute;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class Platform
{
    private static final int FAMILY_NONE = 0;
    private static final int FAMILY_UNIX = 1;
    private static final int FAMILY_WINDOWS = 2;
    private static final int FAMILY_OS2 = 3;
    private static final int FAMILY_ZOS = 4;
    private static final int FAMILY_OS400 = 5;
    private static final int FAMILY_DOS = 6;
    private static final int FAMILY_MAC = 7;
    private static final int FAMILY_MACOSX = 8;
    private static final int FAMILY_TANDEM = 9;
    private static final int FAMILY_OPENVMS = 10;

    private static final String FAMILY_NAME_UNIX = "unix";
    private static final String FAMILY_NAME_WINDOWS = "windows";
    private static final String FAMILY_NAME_OS2 = "os/2";
    private static final String FAMILY_NAME_ZOS = "z/os";
    private static final String FAMILY_NAME_OS400 = "os/400";
    private static final String FAMILY_NAME_DOS = "dos";
    private static final String FAMILY_NAME_MAC = "mac";
    private static final String FAMILY_NAME_TANDEM = "tandem";
    private static final String FAMILY_NAME_OPENVMS = "openvms";

    private static final Map<Integer, String> familyNames;

    static
    {
        familyNames = new HashMap<Integer, String>();
        familyNames.put(FAMILY_WINDOWS, FAMILY_NAME_WINDOWS);
        familyNames.put(FAMILY_OS2, FAMILY_NAME_OS2);
        familyNames.put(FAMILY_ZOS, FAMILY_NAME_ZOS);
        familyNames.put(FAMILY_OS400, FAMILY_NAME_OS400);
        familyNames.put(FAMILY_DOS, FAMILY_NAME_DOS);
        familyNames.put(FAMILY_MAC, FAMILY_NAME_MAC);
        familyNames.put(FAMILY_MACOSX, FAMILY_NAME_UNIX);
        familyNames.put(FAMILY_TANDEM, FAMILY_NAME_TANDEM);
        familyNames.put(FAMILY_UNIX, FAMILY_NAME_UNIX);
        familyNames.put(FAMILY_OPENVMS, FAMILY_NAME_OPENVMS);
    }

    public static final int getOsFamily()
    {
        String osName = System.getProperty("os.name").toLowerCase();
        String pathSep = System.getProperty("path.separator");
        int family = FAMILY_NONE;

        if (osName.contains("windows"))
        {
            family = FAMILY_WINDOWS;
        }
        else if (osName.contains("os/2"))
        {
            family = FAMILY_OS2;
        }
        else if (osName.contains("z/os") || osName.contains("os/390"))
        {
            family = FAMILY_ZOS;
        }
        else if (osName.contains("os/400"))
        {
            family = FAMILY_OS400;
        }
        else if (pathSep.equals(";"))
        {
            family = FAMILY_DOS;
        }
        else if (osName.contains("mac"))
        {
            if (osName.endsWith("x"))
                family = FAMILY_UNIX; // MACOSX
            else
                family = FAMILY_MAC;
        }
        else if (osName.contains("nonstop_kernel"))
        {
            family = FAMILY_TANDEM;
        }
        else if (osName.contains("openvms"))
        {
            family = FAMILY_OPENVMS;
        }
        else if (pathSep.equals(":"))
        {
            family = FAMILY_UNIX;
        }

        return family;
    }

    public static final String getOsFamilyName()
    {
        return familyNames.get(getOsFamily());
    }

    public static final String getDefaultShell()
    {
        String shell = Execute.getEnvironmentVariables().get("SHELL");

        if (shell == null)
        {
            int family = getOsFamily();
            switch (family)
            {
                case FAMILY_DOS:
                case FAMILY_WINDOWS:
                {
                    shell = "CMD.EXE";
                    break;
                }

                default:
                {
                    shell = "bash";
                    break;
                }
            }
        }
        return shell;
    }

    public static final String getDefaultScriptSuffix()
    {
        int family = getOsFamily();
        String suffix = null;

        switch (family)
        {
            case FAMILY_DOS:
            case FAMILY_WINDOWS:
            {
                suffix = ".bat";
                break;
            }

            default:
            {
                suffix = null;
                break;
            }
        }

        return suffix;
    }

    public static final String[] getDefaultShellArguments()
    {
        int family = getOsFamily();
        String[] args = null;

        switch (family)
        {
            case FAMILY_DOS:
            case FAMILY_WINDOWS:
            {
                args = new String[] { "/c" , "call" };
                break;
            }

            default:
            {
                args = new String[0];
                break;
            }
        }

        return args;
    }
}
