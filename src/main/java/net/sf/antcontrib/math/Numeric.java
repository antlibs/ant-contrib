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
package net.sf.antcontrib.math;

import static java.lang.Math.E;
import static java.lang.Math.PI;

import org.apache.tools.ant.BuildException;

/**
 * A numeric value that implements Evaluateable.
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class Numeric implements Evaluateable {
    /**
     * Field datatype.
     */
    private String datatype;

    /**
     * Field value.
     */
    private String value;

    /**
     * Set the value for this number. This string must parse to the set
     * datatype, for example, setting value to "7.992" and datatype to INT
     * will cause a number format exception to be thrown. Supports two special
     * numbers, "E" and "PI".
     *
     * @param value the value for this number
     */
    public void setValue(String value) {
        if (value.equals("E"))
            value = String.valueOf(E);
        else if (value.equals("PI"))
            value = String.valueOf(PI);
        this.value = value;
    }

    /**
     * evaluate() method.
     *
     * @return the value for this number as a Number. Cast as appropriate to
     * Integer, Long, Float, or Double.
     * @see net.sf.antcontrib.math.Evaluateable#evaluate()
     */
    public Number evaluate() {
        if (datatype == null)
            datatype = "double";
        if (datatype.equals("int"))
            return new Integer(value);
        if (datatype.equals("long"))
            return new Long(value);
        if (datatype.equals("float"))
            return new Float(value);
        if (datatype.equals("double"))
            return new Double(value);
        throw new BuildException("Invalid datatype.");
    }

    /**
     * Sets the datatype of this number. Allowed values are
     * "int", "long", "float", or "double".
     *
     * @param p String
     */
    public void setDatatype(String p) {
        datatype = p;
    }

    /**
     * getDatatype() method.
     *
     * @return the datatype as one of the defined types.
     */
    public String getDatatype() {
        if (datatype == null)
            datatype = "double";
        return datatype;
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return "Numeric[value=" + value
                + ";datatype=" + datatype
                + "]";
    }
}
