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
package net.sf.antcontrib.inifile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class IniFileTask extends Task {
    /**
     */
    public abstract static class IniOperation {
        /**
         * Field section.
         */
        private String section;

        /**
         * Field property.
         */
        private String property;

        /**
         * Constructor for IniOperation.
         */
        public IniOperation() {
            super();
        }

        /**
         * Method getSection.
         *
         * @return String
         */
        public String getSection() {
            return section;
        }

        /**
         * Method setSection.
         *
         * @param section String
         */
        public void setSection(String section) {
            this.section = section;
        }

        /**
         * Method getProperty.
         *
         * @return String
         */
        public String getProperty() {
            return property;
        }

        /**
         * Method setProperty.
         *
         * @param property String
         */
        public void setProperty(String property) {
            this.property = property;
        }

        /**
         * Method execute.
         *
         * @param project Project
         * @param iniFile IniFile
         */
        public void execute(Project project, IniFile iniFile) {
            operate(iniFile);
        }

        /**
         * Method operate.
         *
         * @param file IniFile
         */
        protected abstract void operate(IniFile file);
    }

    /**
     */
    public abstract static class IniOperationConditional extends IniOperation {
        /**
         * Field ifCond.
         */
        private String ifCond;
        /**
         * Field unlessCond.
         */
        private String unlessCond;

        /**
         * Constructor for IniOperationConditional.
         */
        public IniOperationConditional() {
            super();
        }

        /**
         * Method setIf.
         *
         * @param ifCond String
         */
        public void setIf(String ifCond) {
            this.ifCond = ifCond;
        }

        /**
         * Method setUnless.
         *
         * @param unlessCond String
         */
        public void setUnless(String unlessCond) {
            this.unlessCond = unlessCond;
        }

        /**
         * Returns true if the define's if and unless conditions
         * (if any) are satisfied.
         *
         * @param p Project
         * @return boolean
         */
        public boolean isActive(Project p) {
            if (ifCond != null && p.getProperty(ifCond) == null) {
                return false;
            } else if (unlessCond != null && p.getProperty(unlessCond) != null) {
                return false;
            }

            return true;
        }

        /**
         * Method execute.
         *
         * @param project Project
         * @param iniFile IniFile
         */
        public void execute(Project project, IniFile iniFile) {
            if (isActive(project))
                operate(iniFile);
        }
    }

    /**
     */
    public abstract static class IniOperationPropertySetter extends IniOperation {
        /**
         * Field override.
         */
        private boolean override;

        /**
         * Field resultproperty.
         */
        private String resultproperty;

        /**
         * Constructor for IniOperationPropertySetter.
         */
        public IniOperationPropertySetter() {
            super();
        }

        /**
         * Method setOverride.
         *
         * @param override boolean
         */
        public void setOverride(boolean override) {
            this.override = override;
        }

        /**
         * Method setResultProperty.
         *
         * @param resultproperty String
         */
        public void setResultProperty(String resultproperty) {
            this.resultproperty = resultproperty;
        }

        /**
         * Method setResultPropertyValue.
         *
         * @param project Project
         * @param value   String
         */
        protected final void setResultPropertyValue(Project project, String value) {
            if (value != null) {
                if (override) {
                    if (project.getUserProperty(resultproperty) == null)
                        project.setProperty(resultproperty, value);
                    else
                        project.setUserProperty(resultproperty, value);
                } else {
                    Property p = (Property) project.createTask("property");
                    p.setName(resultproperty);
                    p.setValue(value);
                    p.execute();
                }
            }
        }
    }

    /**
     */
    public static final class Remove extends IniOperationConditional {
        /**
         * Constructor for Remove.
         */
        public Remove() {
            super();
        }

        /**
         * Method operate.
         *
         * @param file IniFile
         */
        protected void operate(IniFile file) {
            String secName = getSection();
            String propName = getProperty();

            if (propName == null) {
                file.removeSection(secName);
            } else {
                IniSection section = file.getSection(secName);
                if (section != null)
                    section.removeProperty(propName);
            }
        }
    }

    /**
     */
    public final class Set extends IniOperationConditional {
        /**
         * Field value.
         */
        private String value;

        /**
         * Field operation.
         */
        private String operation;

        /**
         * Constructor for Set.
         */
        public Set() {
            super();
        }

        /**
         * Method setValue.
         *
         * @param value String
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Method setOperation.
         *
         * @param operation String
         */
        public void setOperation(String operation) {
            this.operation = operation;
        }

        /**
         * Method operate.
         *
         * @param file IniFile
         */
        protected void operate(IniFile file) {
            String secName = getSection();
            String propName = getProperty();

            IniSection section = file.getSection(secName);
            if (section == null) {
                section = new IniSection(secName);
                file.setSection(section);
            }

            if (propName != null) {
                if (operation != null) {
                    if ("+".equals(operation)) {
                        IniProperty prop = section.getProperty(propName);
                        value = prop.getValue();
                        int intVal = Integer.parseInt(value) + 1;
                        value = String.valueOf(intVal);
                    } else if ("-".equals(operation)) {
                        IniProperty prop = section.getProperty(propName);
                        value = prop.getValue();
                        int intVal = Integer.parseInt(value) - 1;
                        value = String.valueOf(intVal);
                    }
                }
                section.setProperty(new IniProperty(propName, value));
            }
        }
    }

    /**
     */
    public final class Exists extends IniOperationPropertySetter {
        /**
         * Constructor for Exists.
         */
        public Exists() {
            super();
        }

        /**
         * Method operate.
         *
         * @param file IniFile
         */
        protected void operate(IniFile file) {
            boolean exists = false;
            String secName = getSection();
            String propName = getProperty();

            if (secName == null)
                throw new BuildException("You must supply a section to search for.");

            if (propName == null)
                exists = (file.getSection(secName) != null);
            else
                exists = (file.getProperty(secName, propName) != null);

            setResultPropertyValue(getProject(), Boolean.valueOf(exists).toString());
        }
    }

    /**
     */
    public final class Get extends IniOperationPropertySetter {
        /**
         * Constructor for Get.
         */
        public Get() {
            super();
        }

        /**
         * Method operate.
         *
         * @param file IniFile
         */
        protected void operate(IniFile file) {
            String secName = getSection();
            String propName = getProperty();

            if (secName == null)
                throw new BuildException("You must supply a section to search for.");

            if (propName == null)
                throw new BuildException("You must supply a property name to search for.");

            setResultPropertyValue(getProject(), file.getProperty(secName, propName));
        }
    }

    /**
     * Field source.
     */
    private File source;

    /**
     * Field dest.
     */
    private File dest;

    /**
     * Field operations.
     */
    private final List<IniOperation> operations;

    /**
     * Constructor for IniFileTask.
     */
    public IniFileTask() {
        super();
        this.operations = new ArrayList<IniOperation>();
    }

    /**
     * Method createSet.
     *
     * @return Set
     */
    public Set createSet() {
        Set set = new Set();
        operations.add(set);
        return set;
    }

    /**
     * Method createRemove.
     *
     * @return Remove
     */
    public Remove createRemove() {
        Remove remove = new Remove();
        operations.add(remove);
        return remove;
    }

    /**
     * Method createExists.
     *
     * @return Exists
     */
    public Exists createExists() {
        Exists exists = new Exists();
        operations.add(exists);
        return exists;
    }

    /**
     * Method createGet.
     *
     * @return Get
     */
    public Get createGet() {
        Get get = new Get();
        operations.add(get);
        return get;
    }

    /**
     * Method setSource.
     *
     * @param source File
     */
    public void setSource(File source) {
        this.source = source;
    }

    /**
     * Method setDest.
     *
     * @param dest File
     */
    public void setDest(File dest) {
        this.dest = dest;
    }

    /**
     * Method execute.
     *
     * @throws BuildException if something goes wrong
     */
    public void execute() throws BuildException {
        if (dest == null)
            throw new BuildException("You must supply a dest file to write to.");

        IniFile iniFile = null;

        try {
            iniFile = readIniFile(source);
        } catch (IOException e) {
            throw new BuildException(e);
        }

        Iterator<IniOperation> it = operations.iterator();
        IniOperation operation = null;
        while (it.hasNext()) {
            operation = it.next();
            operation.execute(getProject(), iniFile);
        }

        FileWriter writer = null;

        try {
            try {
                writer = new FileWriter(dest);
                iniFile.write(writer);
            } finally {
                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    // gulp
                }
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }

    }

    /**
     * Method readIniFile.
     *
     * @param source File
     * @return IniFile
     * @throws IOException if read fails
     */
    private IniFile readIniFile(File source) throws IOException {
        FileReader reader = null;
        IniFile iniFile = new IniFile();

        if (source == null)
            return iniFile;

        try {
            reader = new FileReader(source);
            iniFile.read(reader);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                // gulp
            }
        }

        return iniFile;
    }
}
