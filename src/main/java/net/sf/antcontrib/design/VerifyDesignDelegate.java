/*
 * Copyright (c) 2004-2005 Ant-Contrib project.  All rights reserved.
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.util.JAXPUtils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:dean@xsoftware.biz">Dean Hiller</a>
 */
public class VerifyDesignDelegate implements Log {
    /**
     * Field designFile.
     */
    private File designFile;

    /**
     * Field paths.
     */
    private final List<Path> paths = new ArrayList<Path>();

    /**
     * Field isCircularDesign.
     */
    private boolean isCircularDesign = false;

    /**
     * Field deleteFiles.
     */
    private boolean deleteFiles = false;

    /**
     * Field fillInBuildException.
     */
    private boolean fillInBuildException = false;

    /**
     * Field needDeclarationsDefault.
     */
    private boolean needDeclarationsDefault = true;

    /**
     * Field needDependsDefault.
     */
    private boolean needDependsDefault = true;

    /**
     * Field task.
     */
    private final Task task;

    /**
     * Field design.
     */
    private Design design;

    /**
     * Field primitives.
     */
    private final Set<String> primitives = new HashSet<String>();

    /**
     * Field designErrors.
     */
    private final List<BuildException> designErrors = new ArrayList<BuildException>();

    /**
     * Field verifiedAtLeastOne.
     */
    private boolean verifiedAtLeastOne = false;

    /**
     * Constructor for VerifyDesignDelegate.
     *
     * @param task Task
     */
    public VerifyDesignDelegate(Task task) {
        this.task = task;
        primitives.add("B");
        primitives.add("C");
        primitives.add("D");
        primitives.add("F");
        primitives.add("I");
        primitives.add("J");
        primitives.add("S");
        primitives.add("Z");
    }

    /**
     * Method addConfiguredPath.
     *
     * @param path Path
     */
    public void addConfiguredPath(Path path) {
        paths.add(path);
    }

    /**
     * Method setJar.
     *
     * @param f File
     */
    public void setJar(File f) {
        Path p = (Path) task.getProject().createDataType("path");
        p.createPathElement().setLocation(f.getAbsoluteFile());
        addConfiguredPath(p);
    }

    /**
     * Method setDesign.
     *
     * @param f File
     */
    public void setDesign(File f) {
        this.designFile = f;
    }

    /**
     * Method setCircularDesign.
     *
     * @param isCircularDesign boolean
     */
    public void setCircularDesign(boolean isCircularDesign) {
        this.isCircularDesign = isCircularDesign;
    }

    /**
     * Method setDeleteFiles.
     *
     * @param deleteFiles boolean
     */
    public void setDeleteFiles(boolean deleteFiles) {
        this.deleteFiles = deleteFiles;
    }

    /**
     * Method setFillInBuildException.
     *
     * @param b boolean
     */
    public void setFillInBuildException(boolean b) {
        fillInBuildException = b;
    }

    /**
     * Method setNeedDeclarationsDefault.
     *
     * @param b boolean
     */
    public void setNeedDeclarationsDefault(boolean b) {
        needDeclarationsDefault = b;
    }

    /**
     * Method setNeedDependsDefault.
     *
     * @param b boolean
     */
    public void setNeedDependsDefault(boolean b) {
        needDependsDefault = b;
    }

    /**
     * Method execute.
     *
     * @throws BuildException if parsing of design file fails
     */
    public void execute() throws BuildException {
        if (!designFile.exists() || designFile.isDirectory()) {
            throw new BuildException("design attribute in verifydesign element specified an invalid file="
                    + designFile);
        }

        verifyJarFilesExist();

        try {
            XMLReader reader = JAXPUtils.getXMLReader();
            DesignFileHandler ch = new DesignFileHandler(this, designFile,
                    isCircularDesign, task.getLocation());
            ch.setNeedDeclarationsDefault(needDeclarationsDefault);
            ch.setNeedDependsDefault(needDependsDefault);
            reader.setContentHandler(ch);
            //reader.setEntityResolver(ch);
            //reader.setErrorHandler(ch);
            //reader.setDTDHandler(ch);

            log("about to start parsing file='" + designFile + "'", Project.MSG_INFO);
            FileInputStream fileInput = new FileInputStream(designFile);
            InputSource src = new InputSource(fileInput);
            reader.parse(src);
            design = ch.getDesign();

            for (Path p : paths) {
                verifyPathAdheresToDesign(design, p);
            }

            //only put unused errors if there are no other errors
            //this is because you end up with false unused errors if you don't do this.
            if (designErrors.isEmpty()) {
                design.fillInUnusedPackages(designErrors);
            }

            if (!designErrors.isEmpty()) {
                log(designErrors.size() + " errors.", Project.MSG_WARN);
                if (!fillInBuildException) {
                    throw new BuildException("Design check failed due to previous errors");
                }
                throwAllErrors();
            }

        } catch (SAXException e) {
            maybeDeleteFiles();
            if (e.getException() != null
                    && e.getException() instanceof RuntimeException) {
                throw (RuntimeException) e.getException();
            } else if (e instanceof SAXParseException) {
                SAXParseException pe = (SAXParseException) e;
                throw new BuildException("\nProblem parsing design file='"
                        + designFile + "'.  \nline=" + pe.getLineNumber()
                        + " column=" + pe.getColumnNumber() + " Reason:\n"
                        + e.getMessage() + "\n", e);
            }
            throw new BuildException("\nProblem parsing design file='"
                    + designFile + "'. Reason:\n" + e, e);
        } catch (IOException e) {
            maybeDeleteFiles();
            throw new RuntimeException("See attached exception", e);
            // throw new BuildException("IOException on design file='"
            // + designFile + "'. attached:", e);
        } catch (RuntimeException e) {
            maybeDeleteFiles();
            throw e;
        } finally {
        }

        if (!verifiedAtLeastOne) {
            throw new BuildException("Did not find any class or jar files to verify");
        }
    }

    //some auto builds like cruisecontrol can only report all the
    //standard ant task errors and the build exceptions so here
    //we need to fill in the buildexception so the errors are reported
    //correctly through those tools....though, you think ant has a hook
    //in that cruisecontrol is not using like LogListeners or something

    /**
     * Method throwAllErrors.
     */
    private void throwAllErrors() {
        StringBuilder result = new StringBuilder("Design check failed due to following errors");
        for (BuildException be : designErrors) {
            result.append("\n").append(be.getMessage());
        }
        throw new BuildException(result.toString());
    }

    /**
     * Method verifyJarFilesExist.
     */
    private void verifyJarFilesExist() {
        for (Path p : paths) {
            for (String fileName : p.list()) {
                File file = new File(fileName);

                if (!file.exists()) {
                    throw new BuildException(VisitorImpl.getNoFileMsg(file));
                }
            }
        }
    }

    /**
     * Method maybeDeleteFiles.
     */
    private void maybeDeleteFiles() {
        if (deleteFiles) {
            log("Deleting all class and jar files so you do not get tempted to\n"
                    + "use a jar that doesn't abide by the design (This option can\n"
                    + "be turned off if you really want)", Project.MSG_INFO);

            for (Path p : paths) {
                deleteFilesInPath(p);
            }
        }
    }

    /**
     * Method deleteFilesInPath.
     *
     * @param p Path
     */
    private void deleteFilesInPath(Path p) {
        for (String fileName : p.list()) {
            File file = new File(fileName);

            boolean deleted = file.delete();
            if (!deleted) {
                file.deleteOnExit();
            }
        }
    }

    /**
     * Method verifyPathAdheresToDesign.
     *
     * @param d Design
     * @param p Path
     * @throws ClassFormatException if ClassParser fails
     * @throws IOException          if ClassParser fails
     */
    private void verifyPathAdheresToDesign(Design d, Path p)
            throws ClassFormatException, IOException {
        for (String fileName : p.list()) {
            File file = new File(fileName);
            if (file.isDirectory()) {
                FileSet set = new FileSet();
                set.setDir(file);
                set.setProject(task.getProject());
                PatternSet.NameEntry entry1 = set.createInclude();
                PatternSet.NameEntry entry2 = set.createInclude();
                PatternSet.NameEntry entry3 = set.createInclude();
                entry1.setName("**/*.class");
                entry2.setName("**/*.jar");
                entry3.setName("**/*.war");
                DirectoryScanner scanner = set.getDirectoryScanner(task.getProject());
                scanner.setBasedir(file);
                String[] scannerFiles = scanner.getIncludedFiles();
                for (String scannerFile : scannerFiles) {
                    verifyPartOfPath(scannerFile, new File(file, scannerFile), d);
                }
            } else {
                verifyPartOfPath(fileName, file, d);
            }
        }
    }

    /**
     * Method verifyPartOfPath.
     *
     * @param fileName String
     * @param file     File
     * @param d        Design
     * @throws BuildException       if a file that does not contain
     *                              Java bytecode is supposed to be verified
     * @throws ClassFormatException if ClassParser fails
     * @throws IOException          if ClassParser fails
     */
    private void verifyPartOfPath(String fileName, File file, Design d)
            throws BuildException, ClassFormatException, IOException {
        if (fileName.endsWith(".jar") || fileName.endsWith(".war")) {
            JarFile jarFile = new JarFile(file);
            verifyJarAdheresToDesign(d, jarFile, file);
        } else if (fileName.endsWith(".class")) {
            verifyClassAdheresToDesign(d, file);
        } else {
            throw new BuildException("Only directories, jars, wars, and class files can be supplied to verify design, not file="
                    + file.getAbsolutePath());
        }
    }

    /**
     * Method verifyClassAdheresToDesign.
     *
     * @param d         Design
     * @param classFile File
     * @throws ClassFormatException if ClassParser fails
     * @throws IOException          if ClassParser fails
     */
    private void verifyClassAdheresToDesign(Design d, File classFile)
            throws ClassFormatException, IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(classFile);
            verifyClassAdheresToDesign(d, fis, classFile.getAbsolutePath(), classFile);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                //doh!!
            }
        }

    }

    /**
     * Method verifyJarAdheresToDesign.
     *
     * @param d        Design
     * @param jarFile  JarFile
     * @param original File
     * @throws ClassFormatException if ClassParser fails
     * @throws IOException          if ClassParser fails
     */
    private void verifyJarAdheresToDesign(Design d, JarFile jarFile, File original)
            throws ClassFormatException, IOException {

        try {
            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                ZipEntry entry = en.nextElement();
                InputStream in = null;
                if (entry.getName().endsWith(".class")) {
                    in = jarFile.getInputStream(entry);
                    try {
                        in = jarFile.getInputStream(entry);
                        verifyClassAdheresToDesign(d, in, entry.getName(), original);
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                            // doh!!!
                        }
                    }
                }
            }
        } finally {
            try {
                jarFile.close();
            } catch (IOException e) {
                //doh!!!
            }
        }
    }

    /**
     * Method verifyClassAdheresToDesign.
     *
     * @param d                      Design
     * @param in                     InputStream
     * @param name                   String
     * @param originalClassOrJarFile File
     * @throws ClassFormatException if ClassParser fails
     * @throws IOException          if ClassParser fails
     */
    private void verifyClassAdheresToDesign(Design d, InputStream in, String name, File originalClassOrJarFile)
            throws ClassFormatException, IOException {
        try {
            verifiedAtLeastOne = true;
            ClassParser parser = new ClassParser(in, name);
            JavaClass javaClass = parser.parse();
            String className = javaClass.getClassName();

            if (!d.needEvalCurrentClass(className)) {
                return;
            }

            ConstantPool pool = javaClass.getConstantPool();
            processConstantPool(pool);
            VisitorImpl visitor = new VisitorImpl(pool, this, d, task.getLocation());
            DescendingVisitor desc = new DescendingVisitor(javaClass, visitor);
            desc.visit();
        } catch (BuildException e) {
            log(Design.getWrapperMsg(originalClassOrJarFile, e.getMessage()), Project.MSG_ERR);
            designErrors.add(e);
        }
    }

    /**
     * Method processConstantPool.
     *
     * @param pool ConstantPool
     */
    private void processConstantPool(ConstantPool pool) {
        Constant[] constants = pool.getConstantPool();
        if (constants == null) {
            log("      constants=null", Project.MSG_VERBOSE);
            return;
        }

        log("      constants len=" + constants.length, Project.MSG_VERBOSE);
        for (int i = 0; i < constants.length; i++) {
            processConstant(pool, constants[i], i);
        }
    }

    /**
     * Method processConstant.
     *
     * @param pool ConstantPool
     * @param c    Constant
     * @param i    int
     */
    private void processConstant(ConstantPool pool, Constant c, int i) {
        if (c == null) {
            //don't know why, but constant[0] seems to be always null.
            return;
        }

        log("      const[" + i + "]=" + pool.constantToString(c) + " inst=" + c.getClass().getName(),
                Project.MSG_DEBUG);
        byte tag = c.getTag();
        switch (tag) {
            //reverse engineered from ConstantPool.constantToString..
            case Constants.CONSTANT_Class:
                int ind = ((ConstantClass) c).getNameIndex();
                c = pool.getConstant(ind, Constants.CONSTANT_Utf8);
                String className = Utility.compactClassName(((ConstantUtf8) c).getBytes(), false);
                log("      classNamePre=" + className, Project.MSG_DEBUG);
                className = getRidOfArray(className);
                String firstLetter = className.charAt(0) + "";
                if (primitives.contains(firstLetter)) {
                    return;
                }
                log("      className=" + className, Project.MSG_VERBOSE);
                design.checkClass(className);
                break;
            default:
        }
    }

    /**
     * Method getRidOfArray.
     *
     * @param className String
     * @return String
     */
    private static String getRidOfArray(String className) {
        while (className.startsWith("[")) {
            className = className.substring(1, className.length());
        }
        return className;
    }

    /**
     * Method getPackageName.
     *
     * @param className String
     * @return String
     */
    public static String getPackageName(String className) {
        String packageName = Package.DEFAULT;
        int index = className.lastIndexOf(".");
        if (index > 0) {
            packageName = className.substring(0, index);
        }
        // TODO test the else scenario here (it is a corner case)...

        return packageName;
    }

    /**
     * Method log.
     *
     * @param msg   String
     * @param level int
     * @see net.sf.antcontrib.design.Log#log(String, int)
     */
    public void log(String msg, int level) {
        //if(level == Project.MSG_WARN || level == Project.MSG_INFO
        //      || level == Project.MSG_ERR || level == Project.MSG_VERBOSE)
        //VerifyDesignTest.log(msg);
        task.log(msg, level);
    }
}
