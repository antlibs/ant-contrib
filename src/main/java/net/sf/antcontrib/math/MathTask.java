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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Task;

/**
 * Task for mathematical operations.
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class MathTask extends Task implements DynamicConfigurator {
    /**
     * Field result.
     * Storage for result
     */
    private String result = null;

    /**
     * Field operation.
     */
    private Operation operation = null;

    /**
     * Field locOperation.
     */
    private Operation locOperation = null;

    /**
     * Field datatype.
     */
    private String datatype = null;

    /**
     * Field strict.
     */
    private boolean strict = false;

    /**
     * Constructor for MathTask.
     */
    public MathTask() {
        super();
    }

    /**
     * Method execute.
     *
     * @throws BuildException if something goes wrong
     */
    public void execute() throws BuildException {
        Operation op = locOperation;
        if (op == null) {
            op = operation;
        }

        Number res = op.evaluate();

        if (datatype != null) {
            res = Math.convert(res, datatype);
        }
        getProject().setUserProperty(result, res.toString());
    }

    /**
     * Method setDynamicAttribute.
     *
     * @param s  String
     * @param s1 String
     * @throws BuildException no dynamic attributes allowed
     * @see org.apache.tools.ant.DynamicAttribute#setDynamicAttribute(String, String)
     */
    public void setDynamicAttribute(String s, String s1) throws BuildException {
        throw new BuildException("No dynamic attributes for this task");
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
        operation = op;
        return op;
    }

    /**
     * Method setResult.
     *
     * @param result String
     */
    public void setResult(String result) {
        this.result = result;
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
     * Method getLocalOperation.
     *
     * @return Operation
     */
    private Operation getLocalOperation() {
        if (locOperation == null) {
            locOperation = new Operation();
            locOperation.setDatatype(datatype);
            locOperation.setStrict(strict);
        }
        return locOperation;
    }

    /**
     * Method setOperation.
     *
     * @param operation String
     */
    public void setOperation(String operation) {
        getLocalOperation().setOperation(operation);
    }

    /**
     * Method setDataType.
     *
     * @param dataType String
     */
    public void setDataType(String dataType) {
        getLocalOperation().setDatatype(dataType);
    }

    /**
     * Method setOperand1.
     *
     * @param operand1 String
     */
    public void setOperand1(String operand1) {
        getLocalOperation().setArg1(operand1);
    }

    /**
     * Method setOperand2.
     *
     * @param operand2 String
     */
    public void setOperand2(String operand2) {
        getLocalOperation().setArg2(operand2);
    }

    /**
     * Method createOperation.
     *
     * @return Operation
     */
    public Operation createOperation() {
        if (locOperation != null || operation != null) {
            throw new BuildException("Only 1 operation can be specified");
        }
        this.operation = new Operation();
        this.operation.setStrict(strict);
        this.operation.setDatatype(datatype);
        return this.operation;
    }

    // conform to old task

    /**
     * Method createOp.
     *
     * @return Operation
     */
    public Operation createOp() {
        return createOperation();
    }
}
