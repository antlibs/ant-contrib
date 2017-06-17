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
public class Platform {
    /**
     * Field FAMILY_NONE.
     * (value is 0)
     */
    private static final int FAMILY_NONE = 0;

    /**
     * Field FAMILY_UNIX.
     * (value is 1)
     */
    private static final int FAMILY_UNIX = 1;

    /**
     * Field FAMILY_WINDOWS.
     * (value is 2)
     */
    private static final int FAMILY_WINDOWS = 2;

    /**
     * Field FAMILY_OS2.
     * (value is 3)
     */
    private static final int FAMILY_OS2 = 3;

    /**
     * Field FAMILY_ZOS.
     * (value is 4)
     */
    private static final int FAMILY_ZOS = 4;

    /**
     * Field FAMILY_OS400.
     * (value is 5)
     */
    private static final int FAMILY_OS400 = 5;

    /**
     * Field FAMILY_DOS.
     * (value is 6)
     */
    private static final int FAMILY_DOS = 6;

    /**
     * Field FAMILY_MAC.
     * (value is 7)
     */
    private static final int FAMILY_MAC = 7;

    /**
     * Field FAMILY_MACOSX.
     * (value is 8)
     */
    private static final int FAMILY_MACOSX = 8;

    /**
     * Field FAMILY_TANDEM.
     * (value is 9)
     */
    private static final int FAMILY_TANDEM = 9;

    /**
     * Field FAMILY_OPENVMS.
     * (value is 10)
     */
    private static final int FAMILY_OPENVMS = 10;

    /**
     * Field FAMILY_NAME_UNIX.
     * (value is ""unix"")
     */
    private static final String FAMILY_NAME_UNIX = "unix";

    /**
     * Field FAMILY_NAME_WINDOWS.
     * (value is ""windows"")
     */
    private static final String FAMILY_NAME_WINDOWS = "windows";

    /**
     * Field FAMILY_NAME_OS2.
     * (value is ""os/2"")
     */
    private static final String FAMILY_NAME_OS2 = "os/2";

    /**
     * Field FAMILY_NAME_ZOS.
     * (value is ""z/os"")
     */
    private static final String FAMILY_NAME_ZOS = "z/os";

    /**
     * Field FAMILY_NAME_OS400.
     * (value is ""os/400"")
     */
    private static final String FAMILY_NAME_OS400 = "os/400";

    /**
     * Field FAMILY_NAME_DOS.
     * (value is ""dos"")
     */
    private static final String FAMILY_NAME_DOS = "dos";

    /**
     * Field FAMILY_NAME_MAC.
     * (value is ""mac"")
     */
    private static final String FAMILY_NAME_MAC = "mac";

    /**
     * Field FAMILY_NAME_TANDEM.
     * (value is ""tandem"")
     */
    private static final String FAMILY_NAME_TANDEM = "tandem";

    /**
     * Field FAMILY_NAME_OPENVMS.
     * (value is ""openvms"")
     */
    private static final String FAMILY_NAME_OPENVMS = "openvms";

    /**
     * Field familyNames.
     */
    private static final Map<Integer, String> FAMILY_NAMES;

    static {
        FAMILY_NAMES = new HashMap<Integer, String>();
        FAMILY_NAMES.put(FAMILY_WINDOWS, FAMILY_NAME_WINDOWS);
        FAMILY_NAMES.put(FAMILY_OS2, FAMILY_NAME_OS2);
        FAMILY_NAMES.put(FAMILY_ZOS, FAMILY_NAME_ZOS);
        FAMILY_NAMES.put(FAMILY_OS400, FAMILY_NAME_OS400);
        FAMILY_NAMES.put(FAMILY_DOS, FAMILY_NAME_DOS);
        FAMILY_NAMES.put(FAMILY_MAC, FAMILY_NAME_MAC);
        FAMILY_NAMES.put(FAMILY_MACOSX, FAMILY_NAME_UNIX);
        FAMILY_NAMES.put(FAMILY_TANDEM, FAMILY_NAME_TANDEM);
        FAMILY_NAMES.put(FAMILY_UNIX, FAMILY_NAME_UNIX);
        FAMILY_NAMES.put(FAMILY_OPENVMS, FAMILY_NAME_OPENVMS);
    }

    /**
     * Method getOsFamily.
     *
     * @return int
     */
    public static final int getOsFamily() {
        String osName = System.getProperty("os.name").toLowerCase();
        String pathSep = System.getProperty("path.separator");
        int family = FAMILY_NONE;

        if (osName.contains("windows")) {
            family = FAMILY_WINDOWS;
        } else if (osName.contains("os/2")) {
            family = FAMILY_OS2;
        } else if (osName.contains("z/os") || osName.contains("os/390")) {
            family = FAMILY_ZOS;
        } else if (osName.contains("os/400")) {
            family = FAMILY_OS400;
        } else if (pathSep.equals(";")) {
            family = FAMILY_DOS;
        } else if (osName.contains("mac")) {
            // MACOSX
            family = osName.endsWith("x") ? FAMILY_UNIX : FAMILY_MAC;
        } else if (osName.contains("nonstop_kernel")) {
            family = FAMILY_TANDEM;
        } else if (osName.contains("openvms")) {
            family = FAMILY_OPENVMS;
        } else if (pathSep.equals(":")) {
            family = FAMILY_UNIX;
        }

        return family;
    }

    /**
     * Method getOsFamilyName.
     *
     * @return String
     */
    public static final String getOsFamilyName() {
        return FAMILY_NAMES.get(getOsFamily());
    }

    /**
     * Method getDefaultShell.
     *
     * @return String
     */
    public static final String getDefaultShell() {
        String shell = Execute.getEnvironmentVariables().get("SHELL");

        if (shell == null) {
            int family = getOsFamily();
            switch (family) {
                case FAMILY_DOS:
                case FAMILY_WINDOWS: {
                    shell = "CMD.EXE";
                    break;
                }

                default: {
                    shell = "bash";
                    break;
                }
            }
        }
        return shell;
    }

    /**
     * Method getDefaultScriptSuffix.
     *
     * @return String
     */
    public static final String getDefaultScriptSuffix() {
        int family = getOsFamily();
        String suffix = null;

        switch (family) {
            case FAMILY_DOS:
            case FAMILY_WINDOWS: {
                suffix = ".bat";
                break;
            }

            default: {
                suffix = null;
                break;
            }
        }

        return suffix;
    }

    /**
     * Method getDefaultShellArguments.
     *
     * @return String[]
     */
    public static final String[] getDefaultShellArguments() {
        int family = getOsFamily();
        String[] args = null;

        switch (family) {
            case FAMILY_DOS:
            case FAMILY_WINDOWS: {
                args = new String[]{"/c", "call"};
                break;
            }

            default: {
                args = new String[0];
                break;
            }
        }

        return args;
    }
}
