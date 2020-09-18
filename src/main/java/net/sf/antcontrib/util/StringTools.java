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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * 
 * @author     <a href="mailto:msi@top-logic.com">msi</a>
 */
public class StringTools {

    /**
     * Method trim.
     * Uses {@link String#strip()} if Java 11 or higher is used or self implementation of trimming.<br>
     * 
     * 1. Trimming leading or trailing white spaces <br>
     * 2. Replaces all line endings with {@link System#lineSeparator()} <br>
     * 3. Removing all "invisible unicode characters" except white spaces.
     */
    public static String trim(String string) {
    	try {
    		Method strip = String.class.getMethod("strip");
    		return (String) strip.invoke(string, (Object[])null);
		} catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
			// Just catch if strip not exists because the project works on old java version. No need for logs.
		}
    	return string.trim().replaceAll("\r\n", System.lineSeparator()).replaceAll("\r[^\\n]", System.lineSeparator()).replaceAll("[^\\r]\n", System.lineSeparator()).replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}&&[^\\s]]", "");
    	
    }
}
