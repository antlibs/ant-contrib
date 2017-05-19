/*
 * Copyright (c) 2001-2006 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.net.httpclient;

/**
 */
public class Params {
    /**
     */
    public static class Param {
        /**
         * Field name.
         */
        private String name;

        /**
         * Method getName.
         *
         * @return String
         */
        public String getName() {
            return name;
        }

        /**
         * Method setName.
         *
         * @param name String
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     */
    public static class DoubleParam extends Param {
        /**
         * Field value.
         */
        private double value;

        /**
         * Method getValue.
         *
         * @return double
         */
        public double getValue() {
            return value;
        }

        /**
         * Method setValue.
         *
         * @param value double
         */
        public void setValue(double value) {
            this.value = value;
        }
    }

    /**
     */
    public static class BooleanParam extends Param {
        /**
         * Field value.
         */
        private boolean value;

        /**
         * Method getValue.
         *
         * @return boolean
         */
        public boolean getValue() {
            return value;
        }

        /**
         * Method setValue.
         *
         * @param value boolean
         */
        public void setValue(boolean value) {
            this.value = value;
        }
    }

    /**
     */
    public static class IntParam extends Param {
        /**
         * Field value.
         */
        private int value;

        /**
         * Method getValue.
         *
         * @return int
         */
        public int getValue() {
            return value;
        }

        /**
         * Method setValue.
         *
         * @param value int
         */
        public void setValue(int value) {
            this.value = value;
        }
    }

    /**
     */
    public static class LongParam extends Param {
        /**
         * Field value.
         */
        private long value;

        /**
         * Method getValue.
         *
         * @return long
         */
        public long getValue() {
            return value;
        }

        /**
         * Method setValue.
         *
         * @param value long
         */
        public void setValue(long value) {
            this.value = value;
        }
    }

    /**
     */
    public static class StringParam extends Param {
        /**
         * Field value.
         */
        private String value;

        /**
         * Method getValue.
         *
         * @return String
         */
        public String getValue() {
            return value;
        }

        /**
         * Method setValue.
         *
         * @param value String
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
}
