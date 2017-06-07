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

import java.util.ArrayList;
import java.util.List;

/**
 * Regular Expression utilities.
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class RegexUtil {
    /**
     * An arbitrary node in a select expression.
     */
    public interface SelectNode {
        /**
         * Select the value based on the groups.
         *
         * @param groups The groups found in the match
         * @return String
         */
        String select(List<String> groups);
    }

    /**
     * A group node in a select expression.
     */
    private static class GroupSelectNode implements SelectNode {
        /**
         * Field groupNumber.
         */
        private final int groupNumber;

        /**
         * Constructor for GroupSelectNode.
         *
         * @param groupNumber int
         */
        public GroupSelectNode(int groupNumber) {
            this.groupNumber = groupNumber;
        }

        /**
         * Method select.
         *
         * @param groups List&lt;String&gt;
         * @return String
         */
        public String select(List<String> groups) {
            return (groupNumber < groups.size()) ? groups.get(groupNumber)
                    : "\\" + groupNumber;
        }

        /**
         * Method toString.
         *
         * @return String
         */
        public String toString() {
            return "group: " + groupNumber;
        }
    }

    /**
     * An arbitrary node in a select expression.
     */
    private static class StringSelectNode implements SelectNode {
        /**
         * Field text.
         */
        private final String text;

        /**
         * Constructor for StringSelectNode.
         *
         * @param text String
         */
        public StringSelectNode(String text) {
            this.text = text;
        }

        /**
         * Method select.
         *
         * @param groups List&lt;String&gt;
         * @return String
         */
        public String select(List<String> groups) {
            return text;
        }

        /**
         * Method toString.
         *
         * @return String
         */
        public String toString() {
            return "string: " + text;
        }
    }

    /**
     * Parses a select string into a List of SelectNode objects.
     * These objects can then be merged with a group list to produce
     * an output string (using the "select" method)
     *
     * @param input The select string
     * @return a List of SelectNode objects
     */
    private static List<SelectNode> parseSelectString(String input) {
        List<SelectNode> nodes = new ArrayList<SelectNode>();
        StringBuilder buf = new StringBuilder();
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\\') {
                if (buf.length() > 0) {
                    nodes.add(new StringSelectNode(buf.toString()));
                    buf.setLength(0);
                }

                while (i + 1 < c.length && Character.isDigit(c[i + 1])) {
                    buf.append(c[i + 1]);
                    i++;
                }

                int groupNum = Integer.parseInt(buf.toString());
                buf.setLength(0);
                nodes.add(new GroupSelectNode(groupNum));
            } else {
                buf.append(c[i]);
            }
        }

        if (buf.length() > 0) {
            nodes.add(new StringSelectNode(buf.toString()));
            buf.setLength(0);
        }

        return nodes;
    }

    /**
     * Parse a select string, and merge it with a match groups
     * vector to produce an output string.  Each group placeholder
     * in the select string is replaced with the group at the
     * corresponding index in the match groups vector
     *
     * @param select The select string
     * @param groups The match groups
     * @return The output string with the merged selection
     */
    public static String select(String select, List<String> groups) {
        List<SelectNode> nodes = parseSelectString(select);

        StringBuilder buf = new StringBuilder();
        for (SelectNode node : nodes) {
            buf.append(node.select(groups));
        }
        return buf.toString();
    }
}
