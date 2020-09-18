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

/**
 * 
 * 
 * @author     <a href="mailto:msi@top-logic.com">msi</a>
 */
public class StringTools {

    /**
     * Method trim.<br>
     * 
     * 1. trimming leading or trailing white spaces <br>
     * 2. dos2unix<br>
     * 3. mac2unix<br>
     * 4. removing all "invisible Unicode characters" except white spaces.
     * 
     * @param tok String
     */
    public static String trim(String string) {
    	return string.trim().replaceAll("\r\n", System.lineSeparator()).replaceAll("\r[^\\n]", System.lineSeparator()).replaceAll("[^\\r]\n", System.lineSeparator()).replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}&&[^\\s]]", "");
    	
    }
}
