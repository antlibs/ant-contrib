/*
 * Copyright (c) 2020 Business Operation Systems GmbH. All Rights Reserved
 */
package net.sf.antcontrib.util;

/**
 * 
 * 
 * @author     <a href="mailto:msi@top-logic.com">msi</a>
 */
public class Tools {

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
