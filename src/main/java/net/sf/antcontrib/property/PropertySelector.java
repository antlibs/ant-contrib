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
package net.sf.antcontrib.property;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class PropertySelector extends AbstractPropertySetterTask {
    /**
     * Field match.
     */
    private RegularExpression match;

    /**
     * Field select.
     */
    private String select = "\\0";

    /**
     * Field delim.
     */
    private char delim = ',';

    /**
     * Field caseSensitive.
     */
    private boolean caseSensitive = true;

    /**
     * Field distinct.
     */
    private boolean distinct = false;

    /**
     * Constructor for PropertySelector.
     */
    public PropertySelector() {
        super();
    }

    /**
     * Method setMatch.
     *
     * @param match String
     */
    public void setMatch(String match) {
        this.match = new RegularExpression();
        this.match.setPattern(match);
    }

    /**
     * Method setSelect.
     *
     * @param select String
     */
    public void setSelect(String select) {
        this.select = select;
    }

    /**
     * Method setCaseSensitive.
     *
     * @param caseSensitive boolean
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Method setDelimiter.
     *
     * @param delim char
     */
    public void setDelimiter(char delim) {
        this.delim = delim;
    }

    /**
     * Method setDistinct.
     *
     * @param distinct boolean
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * Method validate.
     */
    protected void validate() {
        super.validate();
        if (match == null)
            throw new BuildException("No match expression specified.");
    }

    /**
     * Method execute.
     *
     * @throws BuildException if no regex is specified or no match groups are found
     */
    @SuppressWarnings("unchecked")
    public void execute() throws BuildException {
        validate();

        int options = 0;
        if (!caseSensitive)
            options |= Regexp.MATCH_CASE_INSENSITIVE;

        Regexp regex = match.getRegexp(getProject());
        Hashtable<String, Object> props = getProject().getProperties();
        Enumeration<String> e = props.keys();
        StringBuilder buf = new StringBuilder();
        int cnt = 0;

        Vector<String> used = new Vector<String>();

        while (e.hasMoreElements()) {
            String key = e.nextElement();
            if (regex.matches(key, options)) {
                String output = select;
                Vector<String> groups = regex.getGroups(key, options);
                int sz = groups.size();
                for (int i = 0; i < sz; i++) {
                    String s = groups.elementAt(i);

                    RegularExpression result = null;
                    result = new RegularExpression();
                    result.setPattern("\\\\" + i);
                    Regexp sregex = result.getRegexp(getProject());
                    output = sregex.substitute(output, s, Regexp.MATCH_DEFAULT);
                }

                if (!(distinct && used.contains(output))) {
                    used.addElement(output);
                    if (cnt != 0) buf.append(delim);
                    buf.append(output);
                    cnt++;
                }
            }
        }

        if (buf.length() > 0)
            setPropertyValue(buf.toString());
    }
}
