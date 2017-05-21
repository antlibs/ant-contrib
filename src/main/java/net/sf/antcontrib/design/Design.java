/*
 * Copyright (c) 2001-2005 Ant-Contrib project.  All rights reserved.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;

/**
 * Created on Aug 24, 2003.
 *
 * @author <a href="mailto:dean@xsoftware.biz">Dean Hiller</a>
 */
public class Design {
    /**
     * Field nameToPackage.
     */
    private final Map<String, Package> nameToPackage = new HashMap<String, Package>();

    /**
     * Field packageNameToPackage.
     */
    private final Map<String, Package> packageNameToPackage = new HashMap<String, Package>();

    /**
     * Field isCircularDesign.
     */
    private final boolean isCircularDesign;

    /**
     * Field log.
     */
    private final Log log;

    /**
     * Field location.
     */
    private final Location location;

    /**
     * Field currentClass.
     */
    private String currentClass = null;

    /**
     * Field currentPackageName.
     */
    private String currentPackageName = null;

    /**
     * Field currentAliasPackage.
     */
    private Package currentAliasPackage = null;

    /**
     * Field primitives.
     */
    private final HashSet<String> primitives = new HashSet<String>();

    /**
     * Constructor for Design.
     *
     * @param isCircularDesign boolean
     * @param log              Log
     * @param loc              Location
     */
    public Design(boolean isCircularDesign, Log log, Location loc) {
        //by default, add java as a configured package with the name java
        Package p = new Package();
        p.setIncludeSubpackages(true);
        p.setName("java");
        p.setUsed(true);
        p.setNeedDeclarations(false);
        p.setPackage("java");
        addConfiguredPackage(p);

        this.isCircularDesign = isCircularDesign;
        this.log = log;
        this.location = loc;

        primitives.add("boolean");

        //integral types
        primitives.add("byte");
        primitives.add("short");
        primitives.add("int");
        primitives.add("long");
        primitives.add("char");

        //floating point types
        primitives.add("double");
        primitives.add("float");
    }

    /**
     * Method getPackage.
     *
     * @param nameAttribute String
     * @return Package
     */
    public Package getPackage(String nameAttribute) {
        return nameToPackage.get(nameAttribute);
    }

    /**
     * Method retrievePack.
     *
     * @param thePackage String
     * @return Package
     */
    private Package retrievePack(String thePackage) {
        if (thePackage == null)
            throw new IllegalArgumentException("Cannot retrieve null packages");

        String currentPackage = thePackage;
        Package result = packageNameToPackage.get(currentPackage);
        while (!Package.DEFAULT.equals(currentPackage)) {
            log.log("p=" + currentPackage + "result=" + result, Project.MSG_DEBUG);
            if (result != null) {
                if (currentPackage.equals(thePackage))
                    return result;
                else if (result.isIncludeSubpackages())
                    return result;
                return null;
            }
            currentPackage = VerifyDesignDelegate.getPackageName(currentPackage);
            result = packageNameToPackage.get(currentPackage);
        }

        //result must now be default package
        if (result != null && result.isIncludeSubpackages())
            return result;

        return null;
    }

    /**
     * Method addConfiguredPackage.
     *
     * @param p Package
     */
    public void addConfiguredPackage(Package p) {
        Depends[] depends = p.getDepends();

        if (depends != null && !isCircularDesign) {
            //make sure all depends are in Map first
            //circular references then are not a problem because they must
            //put the stuff in order
            for (Depends depend : depends) {
                Package dependsPackage = nameToPackage.get(depend.getName());

                if (dependsPackage == null) {
                    throw new RuntimeException("package name=" + p.getName() + " did not\n"
                            + "have " + depend + " listed before it.  circularDesign is off\n"
                            + "so package=" + p.getName() + " must be moved up in the xml file");
                }
            }
        }

        nameToPackage.put(p.getName(), p);
        packageNameToPackage.put(p.getPackage(), p);
    }

    /**
     * @param className Class name of a class our currentAliasPackage depends on.
     */
    public void verifyDependencyOk(String className) {
        log.log("         className=" + className, Project.MSG_DEBUG);
        if (className.startsWith("L"))
            className = className.substring(1, className.length());

        //get the classPackage our currentAliasPackage depends on....
        String classPackage = VerifyDesignDelegate.getPackageName(className);

        //check if this is an needdeclarations="false" package,
        //if so, the dependency is ok if it is not declared
        log.log("         classPackage=" + classPackage, Project.MSG_DEBUG);
        Package p = retrievePack(classPackage);
        if (p == null) {
            throw new BuildException(getErrorMessage(currentClass, className), location);
        }
        p.setUsed(true); //set package to used since we have classes in it
        if (p != null && !p.isNeedDeclarations())
            return;

        String pack = currentAliasPackage.getPackage();

        log.log("         AllowedDepends=" + pack, Project.MSG_DEBUG);
        log.log("         CurrentDepends=" + className, Project.MSG_DEBUG);
        if (isClassInPackage(className, currentAliasPackage))
            return;

        Depends[] depends = currentAliasPackage.getDepends();

        //probably want to create a regular expression out of all the depends
        //and just match on that each time.  for now though, just get it
        //working and do the basic (optimize later if needed)
        for (Depends d : depends) {
            String name = d.getName();

            Package temp = getPackage(name);
            log.log("         AllowedDepends=" + temp.getPackage(), Project.MSG_DEBUG);
            log.log("         CurrentDepends=" + className, Project.MSG_DEBUG);
            if (isClassInPackage(className, temp)) {
                //set package to used since we are depending on it
                // (could be external package like junit)
                temp.setUsed(true);
                currentAliasPackage.addUsedDependency(d);
                return;
            }
        }

        log.log("***************************************", Project.MSG_DEBUG);
        log.log("***************************************", Project.MSG_DEBUG);

        throw new BuildException(Design.getErrorMessage(currentClass, className), location);
    }

    /**
     * Method isClassInPackage.
     *
     * @param className String
     * @param p         Package
     * @return boolean
     */
    public boolean isClassInPackage(String className, Package p) {
        String classPackage = VerifyDesignDelegate.getPackageName(className);
        if (p.isIncludeSubpackages()) {
            if (className.startsWith(p.getPackage()))
                return true;
        } else { //if not including subpackages, the it must be the exact package.
            if (classPackage.equals(p.getPackage()))
                return true;
        }
        return false;
    }

    /**
     * @param className String
     * @return whether or not this class needs to be checked. (ie. if the
     * attribute needdepends=false, we don't care about this package.
     */
    public boolean needEvalCurrentClass(String className) {
        currentClass = className;
        String packageName = VerifyDesignDelegate.getPackageName(className);
//      log("class="+className, Project.MSG_DEBUG);
        if (!packageName.equals(currentPackageName) || currentAliasPackage == null) {
            currentPackageName = packageName;
            log.log("\nEvaluating package=" + currentPackageName, Project.MSG_INFO);
            currentAliasPackage = retrievePack(packageName);
            // TODO test this scenario
            if (currentAliasPackage == null) {
                log.log("   class=" + className, Project.MSG_VERBOSE);
                throw new BuildException(getNoDefinitionError(className), location);
            }

            currentAliasPackage.setUsed(true);
        }
        log.log("   class=" + className, Project.MSG_VERBOSE);

        if (packageName.equals(Package.DEFAULT)) {
            if (className.indexOf('.') != -1) {
                throw new RuntimeException("Internal error");
            }
        } else if (!className.startsWith(currentPackageName))
            throw new RuntimeException("Internal error");

        return currentAliasPackage.getNeedDepends();
    }

    /**
     * Method getCurrentClass.
     *
     * @return String
     */
    public String getCurrentClass() {
        return currentClass;
    }

    /**
     * Method checkClass.
     *
     * @param dependsOn String
     */
    @SuppressWarnings("unused")
    void checkClass(String dependsOn) {
        log.log("         dependsOn1=" + dependsOn, Project.MSG_DEBUG);
        if (dependsOn.endsWith("[]")) {
            int index = dependsOn.indexOf("[");
            dependsOn = dependsOn.substring(0, index);
            log.log("         dependsOn2=" + dependsOn, Project.MSG_DEBUG);
        }

        if (primitives.contains(dependsOn))
            return;

        //Anything in java.lang package seems to be passed in as just the
        //className with no package like Object, String or Class, so here we try to
        //see if the name is a java.lang class....
        String tempTry = "java.lang." + dependsOn;
        try {
            Class<?> c = VerifyDesign.class.getClassLoader().loadClass(tempTry);
            return;
        } catch (ClassNotFoundException e) {
            //not found, continue on...
        }
        //sometimes instead of passing java.lang.String or java.lang.Object, the bcel
        //passes just String or Object
//      if("String".equals(dependsOn) || "Object".equals(dependsOn))
//          return;

        verifyDependencyOk(dependsOn);

    }

    /**
     * Method getErrorMessage.
     *
     * @param className      String
     * @param dependsOnClass String
     * @return String
     */
    public static String getErrorMessage(String className, String dependsOnClass) {
        return "\nYou are violating your own design...."
                + "\nClass = " + className + " depends on\nClass = " + dependsOnClass
                + "\nThe dependency to allow this is not defined in your design"
                + "\nPackage=" + VerifyDesignDelegate.getPackageName(className) + " is not defined to depend on"
                + "\nPackage=" + VerifyDesignDelegate.getPackageName(dependsOnClass)
                + "\nChange the code or the design";
    }

    /**
     * Method getNoDefinitionError.
     *
     * @param className String
     * @return String
     */
    public static String getNoDefinitionError(String className) {
        return "\nPackage=" + VerifyDesignDelegate.getPackageName(className) + " is not defined in the design.\n"
                + "All packages with classes must be declared in the design file\n"
                + "Class found in the offending package=" + className;
    }

    /**
     * Method getWrapperMsg.
     *
     * @param originalFile File
     * @param message      String
     * @return String
     */
    public static String getWrapperMsg(File originalFile, String message) {
        return "\nThe file '" + originalFile.getAbsolutePath() + "' failed due to: " + message;
    }

    /**
     * fillInUnusedPackages() method.
     *
     * @param designErrors List&lt;BuildException&gt;
     */
    public void fillInUnusedPackages(List<BuildException> designErrors) {
        for (Package pack : nameToPackage.values()) {
            if (!pack.isUsed()) {
                String msg = "Package name=" + pack.getName() + " is unused.  Full package=" + pack.getPackage();
                log.log(msg, Project.MSG_ERR);
                designErrors.add(new BuildException(msg));
            } else {
                fillInUnusedDepends(designErrors, pack);
            }
        }
    }

    /**
     * fillInUnusedDepends() method.
     *
     * @param designErrors List&lt;BuildException&gt;
     * @param pack         Package
     */
    private void fillInUnusedDepends(List<BuildException> designErrors, Package pack) {
        for (Depends depends : pack.getUnusedDepends()) {
            String msg = "Package name=" + pack.getName()
                    + " has a dependency declared that is not true anymore.  Please erase the dependency <depends>"
                    + depends.getName() + "</depends> from package=" + pack.getName();
            log.log(msg, Project.MSG_ERR);
            designErrors.add(new BuildException(msg));
        }
    }
}
