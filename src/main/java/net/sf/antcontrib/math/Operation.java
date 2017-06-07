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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;

/**
 * Class to represent a mathematical operation.
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class Operation implements Evaluateable, DynamicConfigurator {
    /**
     * Field operation.
     */
    private String operation = "add";

    /**
     * Field operands.
     */
    private final List<Evaluateable> operands = new ArrayList<Evaluateable>();

    /**
     * Field datatype.
     */
    private String datatype = "double";

    /**
     * Field strict.
     */
    private boolean strict = false;

    /**
     * Field hasLocalOperands.
     */
    private boolean hasLocalOperands = false;

    /**
     * Field localOperands.
     */
    private final Numeric[] localOperands = new Numeric[5];

    /**
     * Method setDynamicAttribute.
     *
     * @param s  String
     * @param s1 String
     * @throws BuildException no dynamic attributes are allowed
     * @see org.apache.tools.ant.DynamicAttribute#setDynamicAttribute(String, String)
     */
    public void setDynamicAttribute(String s, String s1) throws BuildException {
        throw new BuildException("no dynamic attributes for this element");
    }

    /**
     * Method createDynamicElement.
     *
     * @param name String
     * @return Object
     * @throws BuildException if something goes wrong
     * @see org.apache.tools.ant.DynamicElement#createDynamicElement(String)
     */
    public Object createDynamicElement(String name) throws BuildException {
        Operation op = new Operation();
        op.setOperation(name);
        operands.add(op);
        return op;
    }

    /**
     * Method setLocalOperand.
     *
     * @param value String
     * @param index int
     */
    private void setLocalOperand(String value, int index) {
        hasLocalOperands = true;
        localOperands[index - 1] = new Numeric();
        localOperands[index - 1].setValue(value);
    }

    /**
     * Method setArg1.
     *
     * @param value String
     */
    public void setArg1(String value) {
        setLocalOperand(value, 1);
    }

    /**
     * Method setArg2.
     *
     * @param value String
     */
    public void setArg2(String value) {
        setLocalOperand(value, 2);
    }

    /**
     * Method setArg3.
     *
     * @param value String
     */
    public void setArg3(String value) {
        setLocalOperand(value, 3);
    }

    /**
     * Method setArg4.
     *
     * @param value String
     */
    public void setArg4(String value) {
        setLocalOperand(value, 4);
    }

    /**
     * Method setArg5.
     *
     * @param value String
     */
    public void setArg5(String value) {
        setLocalOperand(value, 5);
    }

    /**
     * Method addConfiguredNumeric.
     *
     * @param numeric Numeric
     */
    public void addConfiguredNumeric(Numeric numeric) {
        if (hasLocalOperands)
            throw new BuildException("Cannot combine operand attributes with subelements");

        operands.add(numeric);
    }

    /**
     * Method addConfiguredOperation.
     *
     * @param operation Operation
     */
    public void addConfiguredOperation(Operation operation) {
        if (hasLocalOperands)
            throw new BuildException("Cannot combine operand attributes with subelements");

        operands.add(operation);
    }

    /**
     * Method addConfiguredNum.
     *
     * @param numeric Numeric
     */
    public void addConfiguredNum(Numeric numeric) {
        if (hasLocalOperands)
            throw new BuildException("Cannot combine operand attributes with subelements");

        operands.add(numeric);
    }

    /**
     * Method addConfiguredOp.
     *
     * @param operation Operation
     */
    public void addConfiguredOp(Operation operation) {
        if (hasLocalOperands)
            throw new BuildException("Cannot combine operand attributes with subelements");

        operands.add(operation);
    }

    /**
     * Method setOp.
     *
     * @param operation String
     */
    public void setOp(String operation) {
        setOperation(operation);
    }

    /**
     * Method setOperation.
     *
     * @param operation String
     */
    public void setOperation(String operation) {
        if (operation.equals("+"))
            this.operation = "add";
        else if (operation.equals("-"))
            this.operation = "subtract";
        else if (operation.equals("*"))
            this.operation = "multiply";
        else if (operation.equals("/"))
            this.operation = "divide";
        else if (operation.equals("%"))
            this.operation = "mod";
        else
            this.operation = operation;
    }

    /**
     * Method setDatatype.
     *
     * @param datatype String
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    /**
     * Method setStrict.
     *
     * @param strict boolean
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * Method evaluate.
     *
     * @return Number
     * @see net.sf.antcontrib.math.Evaluateable#evaluate()
     */
    public Number evaluate() {
        Evaluateable[] ops = null;

        if (hasLocalOperands) {
            List<Numeric> localOps = new ArrayList<Numeric>();
            for (Numeric localOperand : localOperands) {
                if (localOperand != null)
                    localOps.add(localOperand);
            }

            ops = localOps.toArray(new Evaluateable[localOps.size()]);
        } else {
            ops = operands.toArray(new Evaluateable[operands.size()]);
        }

        return Math.evaluate(operation,
                datatype,
                strict,
                ops);
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return "Operation[operation=" + operation
                + ";datatype=" + datatype
                + ";strict=" + strict
                + ";localoperands=" + Arrays.asList(localOperands)
                + ";operands=" + operands
                + "]";
    }
}
