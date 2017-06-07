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
package net.sf.antcontrib.design;

import java.io.File;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;

/**
 * Created on Jan 9, 2005.
 *
 * @author <a href="mailto:dean@xsoftware.biz">Dean Hiller</a>
 */
class VisitorImpl extends EmptyVisitor {
    /**
     * Field pool.
     */
    private final ConstantPool pool;

    /**
     * Field log.
     */
    private final Log log;

    /**
     * Field design.
     */
    private final Design design;

    /**
     * Field poolGen.
     */
    private final ConstantPoolGen poolGen;

    /**
     * Field visitor.
     */
    private final InstructionVisitor visitor;

    /**
     * Field location.
     */
    private final Location location;

    /**
     * Constructor for VisitorImpl.
     *
     * @param pool ConstantPool
     * @param log  Log
     * @param d    Design
     * @param loc  Location
     */
    public VisitorImpl(ConstantPool pool, Log log, Design d, Location loc) {
        this.pool = pool;
        this.log = log;
        this.design = d;
        this.location = loc;
        this.poolGen = new ConstantPoolGen(pool);
        visitor = new InstructionVisitor(poolGen, log, d);
    }

    /**
     * Method log.
     *
     * @param s     String
     * @param level int
     */
    private void log(String s, int level) {
        log.log(s, level);
    }

    /**
     * Method visitJavaClass.
     *
     * @param c JavaClass
     * @see org.apache.bcel.classfile.Visitor#visitJavaClass(JavaClass)
     */
    public void visitJavaClass(JavaClass c) {
        log("      super=" + c.getSuperclassName(), Project.MSG_VERBOSE);
        String[] names = c.getInterfaceNames();

        String superClass = c.getSuperclassName();

        design.checkClass(superClass);

        for (String name : names) {
            log("      interfaces=" + name, Project.MSG_VERBOSE);
            design.checkClass(name);
        }
    }

    /**
     * @param f Field
     * @see org.apache.bcel.classfile.Visitor#visitField(org.apache.bcel.classfile.Field)
     */
    public void visitField(Field f) {
        String type = Utility.methodSignatureReturnType(f.getSignature());
        log("      field type=" + type, Project.MSG_VERBOSE);
        design.checkClass(type);

    }

    /**
     * @param v LocalVariable
     * @see org.apache.bcel.classfile.Visitor#visitLocalVariable(org.apache.bcel.classfile.LocalVariable)
     */
    public void visitLocalVariable(LocalVariable v) {
        String type = Utility.methodSignatureReturnType(v.getSignature());
        log("         localVar type=" + type, Project.MSG_VERBOSE);
        design.checkClass(type);
    }

    /**
     * @param m Method
     * @see org.apache.bcel.classfile.Visitor#visitMethod(org.apache.bcel.classfile.Method)
     */
    public void visitMethod(Method m) {
        log("      method=" + m.getName(), Project.MSG_VERBOSE);
        String retType = Utility.methodSignatureReturnType(m.getSignature());
        log("         method ret type=" + retType, Project.MSG_VERBOSE);
        if (!"void".equals(retType)) {
            design.checkClass(retType);
        }

        String[] types = Utility.methodSignatureArgumentTypes(m.getSignature());
        for (int i = 0; i < types.length; i++) {
            log("         method param[" + i + "]=" + types[i],
                    Project.MSG_VERBOSE);
            design.checkClass(types[i]);
        }

        ExceptionTable excs = m.getExceptionTable();
        if (excs != null) {
            types = excs.getExceptionNames();
            for (String type : types) {
                log("         exc=" + type, Project.MSG_VERBOSE);
                design.checkClass(type);
            }
        }

        processInstructions(m);
    }

    /**
     * Method processInstructions.
     *
     * @param m Method
     */
    private void processInstructions(Method m) {
        MethodGen mg = new MethodGen(m, design.getCurrentClass(), poolGen);

        if (!mg.isAbstract() && !mg.isNative()) {
            InstructionHandle ih = mg.getInstructionList().getStart();
            for (; ih != null; ih = ih.getNext()) {
                Instruction i = ih.getInstruction();
                log("         instr=" + i, Project.MSG_DEBUG);
                // if (i instanceof BranchInstruction) {
                // branch_map.put(i, ih); // memorize container
                // }

                // if (ih.hasTargeters()) {
                // if (i instanceof BranchInstruction) {
                // _out.println(" InstructionHandle ih_"
                // + ih.getPosition() + ";");
                // } else {
                // _out.print(" InstructionHandle ih_"
                // + ih.getPosition() + " = ");
                // }
                // } else {
                // _out.print(" ");
                // }

                // if (!visitInstruction(i))
                i.accept(visitor);
            }

            // CodeExceptionGen[] handlers = mg.getExceptionHandlers();
            //
            // log("handlers len="+handlers.length, Project.MSG_DEBUG);
            // for (int i = 0; i < handlers.length; i++) {
            // CodeExceptionGen h = handlers[i];
            // ObjectType t = h.getCatchType();
            // log("type="+t, Project.MSG_DEBUG);
            // if(t != null) {
            // log("type="+t.getClassName(), Project.MSG_DEBUG);
            // }
            // }
            // updateExceptionHandlers();
        }
    }

    /**
     * Method visitCodeException.
     *
     * @param c CodeException
     * @see org.apache.bcel.classfile.Visitor#visitCodeException(CodeException)
     */
    @SuppressWarnings("unused")
    public void visitCodeException(CodeException c) {
        String s = c.toString(pool, false);

        int catch_type = c.getCatchType();

        if (catch_type == 0) {
            return;
        }

        String temp = pool.getConstantString(catch_type,
                Constants.CONSTANT_Class);
        String str = Utility.compactClassName(temp, false);

        log("         catch=" + str, Project.MSG_DEBUG);
        design.checkClass(str);
    }

    //

    /**
     * Method visitCode.
     *
     * @param c Code
     * @see org.apache.bcel.classfile.Visitor#visitCode(Code)
     */
    public void visitCode(Code c) {
        LineNumberTable table = c.getLineNumberTable();
        // LocalVariableTable table = c.getLocalVariableTable();
        if (table == null) {
            throw new BuildException(getNoDebugMsg(design.getCurrentClass()), location);
        }
    }

    /**
     * Method getNoDebugMsg.
     *
     * @param className String
     * @return String
     */
    public static String getNoDebugMsg(String className) {
        return "Class=" + className + " was not compiled with the debug option(-g) and\n"
                + "therefore verifydesign cannot be used on this jar.  Please compile your code\n"
                + "with -g option in javac or debug=\"true\" in the ant build.xml file";
    }

    /**
     * getNoFileMsg() method.
     *
     * @param jarName File
     * @return String
     */
    public static String getNoFileMsg(File jarName) {
        return "File you specified in your path(or jar attribute)='"
                + jarName.getAbsolutePath() + "' does not exist";
    }
}
