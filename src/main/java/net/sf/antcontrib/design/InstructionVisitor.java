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

import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.Type;
import org.apache.tools.ant.Project;

/**
 * @author <a href="mailto:dean@xsoftware.biz">Dean Hiller</a>
 */
public class InstructionVisitor extends EmptyVisitor {
    /**
     * Field poolGen.
     */
    private final ConstantPoolGen poolGen;

    /**
     * Field log.
     */
    private final Log log;

    /**
     * Field design.
     */
    private final Design design;

    /**
     * Constructor.
     *
     * @param poolGen ConstantPoolGen
     * @param log     Log
     * @param d       Design
     */
    public InstructionVisitor(ConstantPoolGen poolGen, Log log, Design d) {
        this.poolGen = poolGen;
        this.log = log;
        this.design = d;
    }

    /**
     * Method visitCHECKCAST.
     *
     * @param c CHECKCAST
     * @see org.apache.bcel.generic.Visitor#visitCHECKCAST(CHECKCAST)
     */
    public void visitCHECKCAST(CHECKCAST c) {
        Type t = c.getType(poolGen);
        log.log("         instr(checkcast)=" + t, Project.MSG_DEBUG);
        String type = t.toString();

        design.checkClass(type);
    }

    /**
     * Method visitLoadInstruction.
     *
     * @param l LoadInstruction
     * @see org.apache.bcel.generic.Visitor#visitLoadInstruction(LoadInstruction)
     */
    public void visitLoadInstruction(LoadInstruction l) {
        //log.log(" visit load", Project.MSG_DEBUG);
        Type t = l.getType(poolGen);
        log.log("         instr(loadinstr)=" + t, Project.MSG_DEBUG);
        String type = t.toString();

        design.checkClass(type);
    }

    /**
     * Method visitNEW.
     *
     * @param n NEW
     * @see org.apache.bcel.generic.Visitor#visitNEW(NEW)
     */
    public void visitNEW(NEW n) {
        Type t = n.getType(poolGen);
        log.log("         instr(new)=" + t, Project.MSG_DEBUG);
        String type = t.toString();

        design.checkClass(type);
    }

    /**
     * Method visitANEWARRAY.
     *
     * @param n ANEWARRAY
     * @see org.apache.bcel.generic.Visitor#visitANEWARRAY(ANEWARRAY)
     */
    public void visitANEWARRAY(ANEWARRAY n) {
        Type t = n.getType(poolGen);
        log.log("         instr(anewarray)=" + t, Project.MSG_DEBUG);
        String type = t.toString();

        design.checkClass(type);
    }

    /**
     * Method visitINSTANCEOF.
     *
     * @param i INSTANCEOF
     * @see org.apache.bcel.generic.Visitor#visitINSTANCEOF(INSTANCEOF)
     */
    public void visitINSTANCEOF(INSTANCEOF i) {
        Type t = i.getType(poolGen);
        log.log("         instr(instanceof)=" + t, Project.MSG_DEBUG);
        String type = t.toString();

        design.checkClass(type);
    }

    /**
     * Method visitINVOKESTATIC.
     *
     * @param s INVOKESTATIC
     * @see org.apache.bcel.generic.Visitor#visitINVOKESTATIC(INVOKESTATIC)
     */
    public void visitINVOKESTATIC(INVOKESTATIC s) {
        String t = s.getReferenceType(poolGen).getClass().getName();
        log.log("         instr(invokestatic)=" + t, Project.MSG_DEBUG);

        design.checkClass(t);
    }

    /**
     * Method visitPUTSTATIC.
     *
     * @param s PUTSTATIC
     * @see org.apache.bcel.generic.Visitor#visitPUTSTATIC(PUTSTATIC)
     */
    public void visitPUTSTATIC(PUTSTATIC s) {
        String one = s.getReferenceType(poolGen).getClass().getName();
        String two = s.getFieldName(poolGen);
        String three = s.getName(poolGen);
        String four = s.getSignature(poolGen);
        String five = s.getReferenceType(poolGen).getClass().getName() + "";
        String six = s.getFieldType(poolGen) + "";
        log.log("         instr(putstatic)a=" + one, Project.MSG_DEBUG);
        log.log("         instr(putstatic)b=" + two, Project.MSG_DEBUG);
        log.log("         instr(putstatic)c=" + three, Project.MSG_DEBUG);
        log.log("         instr(putstatic)d=" + four, Project.MSG_DEBUG);
        log.log("         instr(putstatic)e=" + five, Project.MSG_DEBUG);
        log.log("         instr(putstatic)f=" + six, Project.MSG_DEBUG);

        String className = s.getFieldName(poolGen);
        if ("staticField".equals(className))
            return;

        if (className.startsWith("class$") || className.startsWith("array$"))
            ;
        else return;

        log.log("         instr(putstatic)1=" + className, Project.MSG_DEBUG);
        className = className.substring(6, className.length());
        log.log("         instr(putstatic)2=" + className, Project.MSG_DEBUG);
        className = className.replace('$', '.');
        log.log("         instr(putstatic)3=" + className, Project.MSG_DEBUG);

        design.checkClass(className);
    }
}
