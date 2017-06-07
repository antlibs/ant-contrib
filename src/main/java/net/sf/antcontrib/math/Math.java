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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tools.ant.BuildException;

/**
 * Utility class for executing calculations.
 *
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class Math {
    /**
     * Method evaluate.
     *
     * @param operation String
     * @param datatype  String
     * @param strict    boolean
     * @param operands  Evaluateable[]
     * @return Number
     */
    public static final Number evaluate(String operation,
                                        String datatype,
                                        boolean strict,
                                        Evaluateable[] operands) {
        if (datatype == null)
            datatype = "double";

        try {
            operation = operation.toLowerCase();

            Method m = Math.class.getDeclaredMethod(operation,
                    new Class[]{
                            String.class,
                            Boolean.TYPE,
                            operands.getClass()
                    });

            return (Number) m.invoke(null,
                    new Object[]{
                            datatype,
                            strict ? Boolean.TRUE : Boolean.FALSE,
                            operands
                    });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        }
        return null;
    }

    /**
     * Method add.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number add(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        Number result = null;

        Number[] numbers = new Number[operands.length];
        for (int i = 0; i < operands.length; i++)
            numbers[i] = operands[i].evaluate();

        if (datatype.equalsIgnoreCase("int")) {
            int sum = 0;
            for (Number number : numbers) sum += number.intValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("long")) {
            long sum = 0;
            for (Number number : numbers) sum += number.longValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("float")) {
            float sum = 0;
            for (Number number : numbers) sum += number.floatValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("double")) {
            double sum = 0;
            for (Number number : numbers) sum += number.doubleValue();
            result = sum;
        }
        return result;
    }

    /**
     * Method subtract.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number subtract(String datatype,
                                        boolean strict,
                                        Evaluateable[] operands) {
        Number result = null;

        Number[] numbers = new Number[operands.length];
        for (int i = 0; i < operands.length; i++)
            numbers[i] = operands[i].evaluate();

        if (datatype.equalsIgnoreCase("int")) {
            int sum = numbers[0].intValue();
            for (int i = 1; i < numbers.length; i++)
                sum -= numbers[i].intValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("long")) {
            long sum = numbers[0].longValue();
            for (int i = 1; i < numbers.length; i++)
                sum -= numbers[i].longValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("float")) {
            float sum = numbers[0].floatValue();
            for (int i = 1; i < numbers.length; i++)
                sum -= numbers[i].floatValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("double")) {
            double sum = numbers[0].doubleValue();
            for (int i = 1; i < numbers.length; i++)
                sum -= numbers[i].doubleValue();
            result = sum;
        }
        return result;
    }

    /**
     * Method multiply.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number multiply(String datatype,
                                        boolean strict,
                                        Evaluateable[] operands) {
        Number result = null;

        Number[] numbers = new Number[operands.length];
        for (int i = 0; i < operands.length; i++)
            numbers[i] = operands[i].evaluate();

        if (datatype.equalsIgnoreCase("int")) {
            int sum = 1;
            for (Number number : numbers) sum *= number.intValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("long")) {
            long sum = 1;
            for (Number number : numbers) sum *= number.longValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("float")) {
            float sum = 1;
            for (Number number : numbers) sum *= number.floatValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("double")) {
            double sum = 1;
            for (Number number : numbers) sum *= number.doubleValue();
            result = sum;
        }
        return result;
    }

    /**
     * Method divide.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number divide(String datatype,
                                      boolean strict,
                                      Evaluateable[] operands) {
        Number result = null;

        Number[] numbers = new Number[operands.length];
        for (int i = 0; i < operands.length; i++)
            numbers[i] = operands[i].evaluate();

        if (datatype.equalsIgnoreCase("int")) {
            int sum = numbers[0].intValue();
            for (int i = 1; i < numbers.length; i++)
                sum /= numbers[i].intValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("long")) {
            long sum = numbers[0].longValue();
            for (int i = 1; i < numbers.length; i++)
                sum /= numbers[i].longValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("float")) {
            float sum = numbers[0].floatValue();
            for (int i = 1; i < numbers.length; i++)
                sum /= numbers[i].floatValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("double")) {
            double sum = numbers[0].doubleValue();
            for (int i = 1; i < numbers.length; i++)
                sum /= numbers[i].doubleValue();
            result = sum;
        }
        return result;
    }

    /**
     * Method mod.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number mod(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        Number result = null;

        Number[] numbers = new Number[operands.length];
        for (int i = 0; i < operands.length; i++)
            numbers[i] = operands[i].evaluate();

        if (datatype.equalsIgnoreCase("int")) {
            int sum = numbers[0].intValue();
            for (int i = 1; i < numbers.length; i++)
                sum %= numbers[i].intValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("long")) {
            long sum = numbers[0].longValue();
            for (int i = 1; i < numbers.length; i++)
                sum %= numbers[i].longValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("float")) {
            float sum = numbers[0].floatValue();
            for (int i = 1; i < numbers.length; i++)
                sum %= numbers[i].floatValue();
            result = sum;
        } else if (datatype.equalsIgnoreCase("double")) {
            double sum = numbers[0].doubleValue();
            for (int i = 1; i < numbers.length; i++)
                sum %= numbers[i].doubleValue();
            result = sum;
        }
        return result;
    }

    /**
     * Method convert.
     *
     * @param n        Number
     * @param datatype String
     * @return Number
     */
    public static final Number convert(Number n, String datatype) {
        if (datatype == null)
            datatype = "double";
        if (datatype.equals("int"))
            return n.intValue();
        if (datatype.equals("long"))
            return n.longValue();
        if (datatype.equals("float"))
            return n.floatValue();
        if (datatype.equals("double"))
            return n.doubleValue();
        throw new BuildException("Invalid datatype.");
    }

    /**
     * Method execute.
     *
     * @param method     String
     * @param datatype   String
     * @param strict     boolean
     * @param paramTypes Class&lt;?&gt;[]
     * @param params     Object[]
     * @return Number
     */
    public static final Number execute(String method,
                                       String datatype,
                                       boolean strict,
                                       Class<?>[] paramTypes,
                                       Object[] params) {
        try {
            Class<?> c = null;
            if (strict) {
                c = Thread.currentThread().getContextClassLoader().loadClass("java.lang.StrictMath");
            } else {
                c = Thread.currentThread().getContextClassLoader().loadClass("java.lang.Math");
            }

            Method m = c.getDeclaredMethod(method, paramTypes);
            Number n = (Number) m.invoke(null, params);
            return convert(n, datatype);
        } catch (ClassNotFoundException e) {
            throw new BuildException(e);
        } catch (NoSuchMethodException e) {
            throw new BuildException(e);
        } catch (IllegalAccessException e) {
            throw new BuildException(e);
        } catch (InvocationTargetException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Method random.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number random(String datatype,
                                      boolean strict,
                                      Evaluateable[] operands) {
        return execute("random",
                datatype,
                strict,
                new Class[0],
                new Object[0]);
    }

    /**
     * Method getPrimitiveClass.
     *
     * @param datatype String
     * @return Class&lt;? extends Number&lt;
     */
    private static Class<? extends Number> getPrimitiveClass(String datatype) {
        if (datatype == null)
            return Double.TYPE;
        if (datatype.equals("int"))
            return Integer.TYPE;
        if (datatype.equals("long"))
            return Long.TYPE;
        if (datatype.equals("float"))
            return Float.TYPE;
        if (datatype.equals("double"))
            return Double.TYPE;
        throw new BuildException("Invalid datatype.");

    }

    /**
     * Method abs.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number abs(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        Object[] ops = new Object[]{convert(operands[0].evaluate(), datatype)};
        Class<?>[] params = new Class<?>[]{getPrimitiveClass(datatype)};

        return execute("abs",
                datatype,
                strict,
                params,
                ops);
    }

    /**
     * Method doOneDoubleArg.
     *
     * @param operation String
     * @param datatype  String
     * @param strict    boolean
     * @param operands  Evaluateable[]
     * @return Number
     */
    private static Number doOneDoubleArg(String operation,
                                         String datatype,
                                         boolean strict,
                                         Evaluateable[] operands) {
        Object[] ops = new Object[]{convert(operands[0].evaluate(),
                "double")};
        Class<?>[] params = new Class<?>[]{Double.TYPE};

        return execute(operation,
                datatype,
                strict,
                params,
                ops);
    }

    /**
     * Method acos.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number acos(String datatype,
                                    boolean strict,
                                    Evaluateable[] operands) {
        return doOneDoubleArg("acos", datatype, strict, operands);
    }

    /**
     * Method asin.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number asin(String datatype,
                                    boolean strict,
                                    Evaluateable[] operands) {
        return doOneDoubleArg("asin", datatype, strict, operands);
    }

    /**
     * Method atan.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number atan(String datatype,
                                    boolean strict,
                                    Evaluateable[] operands) {
        return doOneDoubleArg("atan", datatype, strict, operands);
    }

    /**
     * Method atan2.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number atan2(String datatype,
                                     boolean strict,
                                     Evaluateable[] operands) {
        Object[] ops = new Object[]{convert(operands[0].evaluate(),
                "double"),
                convert(operands[1].evaluate(),
                        "double")};
        Class<?>[] params = new Class<?>[]{Double.TYPE,
                Double.TYPE};

        return execute("atan2",
                datatype,
                strict,
                params,
                ops);
    }

    /**
     * Method sin.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number sin(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        return doOneDoubleArg("sin", datatype, strict, operands);
    }

    /**
     * Method tan.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number tan(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        return doOneDoubleArg("sin", datatype, strict, operands);
    }

    /**
     * Method cos.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number cos(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        return doOneDoubleArg("cos", datatype, strict, operands);
    }

    /**
     * Method ceil.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number ceil(String datatype,
                                    boolean strict,
                                    Evaluateable[] operands) {
        return doOneDoubleArg("ceil", datatype, strict, operands);
    }

    /**
     * Method floor.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number floor(String datatype,
                                     boolean strict,
                                     Evaluateable[] operands) {
        return doOneDoubleArg("floor", datatype, strict, operands);
    }

    /**
     * Method exp.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number exp(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        return doOneDoubleArg("exp", datatype, strict, operands);
    }

    /**
     * Method rint.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number rint(String datatype,
                                    boolean strict,
                                    Evaluateable[] operands) {
        return doOneDoubleArg("rint", datatype, strict, operands);
    }

    /**
     * Method round.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number round(String datatype,
                                     boolean strict,
                                     Evaluateable[] operands) {
        Object[] ops = new Object[]{convert(operands[0].evaluate(),
                datatype)};
        Class<?>[] params = new Class<?>[]{getPrimitiveClass(datatype)};

        return execute("round",
                datatype,
                strict,
                params,
                ops);
    }

    /**
     * Method sqrt.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number sqrt(String datatype,
                                    boolean strict,
                                    Evaluateable[] operands) {
        return doOneDoubleArg("sqrt", datatype, strict, operands);
    }

    /**
     * Method degrees.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number degrees(String datatype,
                                       boolean strict,
                                       Evaluateable[] operands) {
        return todegrees(datatype, strict, operands);
    }

    /**
     * Method todegrees.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number todegrees(String datatype,
                                         boolean strict,
                                         Evaluateable[] operands) {
        return doOneDoubleArg("toDegrees", datatype, strict, operands);
    }

    /**
     * Method radians.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number radians(String datatype,
                                       boolean strict,
                                       Evaluateable[] operands) {
        return toradians(datatype, strict, operands);
    }

    /**
     * Method toradians.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number toradians(String datatype,
                                         boolean strict,
                                         Evaluateable[] operands) {
        return doOneDoubleArg("toRadians", datatype, strict, operands);
    }

    /**
     * Method ieeeremainder.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number ieeeremainder(String datatype,
                                             boolean strict,
                                             Evaluateable[] operands) {
        Object[] ops = new Object[]{convert(operands[0].evaluate(),
                "double"),
                convert(operands[1].evaluate(),
                        "double")};
        Class<?>[] params = new Class<?>[]{Double.TYPE,
                Double.TYPE};

        return execute("IEEERemainder",
                datatype,
                strict,
                params,
                ops);
    }

    /**
     * Method min.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number min(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        Object[] ops = new Object[]{convert(operands[0].evaluate(),
                datatype),
                convert(operands[1].evaluate(),
                        datatype)};
        Class<?>[] params = new Class<?>[]{getPrimitiveClass(datatype),
                getPrimitiveClass(datatype)};

        return execute("min",
                datatype,
                strict,
                params,
                ops);
    }

    /**
     * Method max.
     *
     * @param datatype String
     * @param strict   boolean
     * @param operands Evaluateable[]
     * @return Number
     */
    public static final Number max(String datatype,
                                   boolean strict,
                                   Evaluateable[] operands) {
        Object[] ops = new Object[]{convert(operands[0].evaluate(),
                datatype),
                convert(operands[1].evaluate(),
                        datatype)};
        Class<?>[] params = new Class<?>[]{getPrimitiveClass(datatype),
                getPrimitiveClass(datatype)};

        return execute("max",
                datatype,
                strict,
                params,
                ops);
    }

}
