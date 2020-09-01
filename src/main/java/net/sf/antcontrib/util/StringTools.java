/*
 * Copyright (c) 2020 Business Operation Systems GmbH. All Rights Reserved
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
package net.sf.antcontrib.util;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:msi@top-logic.com">msi</a>
 */
public class StringTools {

    /**
     * Method trim.
     * Uses {@code String#strip()} if available (Java 11 or higher) or falls back on {@link String#trim()}.
     * <ol>
     * <li>Trims leading or trailing white spaces</li>
     * <li>Removes all "invisible unicode characters" except white spaces.</li>
     * </ol>
     *
     * @param string a string to trim
     * @return a trimmed string
     */
    public static String trim(final String string) {
        try {
            Method strip = String.class.getMethod("strip");
            return (String) strip.invoke(string, (Object[]) null);
        } catch (Exception e) {
            // Strip does not exist because the project uses an old Java version.
        }
        return string.trim().replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}&&[^\\s]]", "");
    }
}
