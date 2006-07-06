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
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;


/****************************************************************************
 * Place class description here.
 *
 * @author <a href='mailto:mattinger@yahoo.com'>Matthew Inger</a>
 * @author		<additional author>
 *
 * @since
 *
 ****************************************************************************/


public class IniFileTask
        extends Task
{
    public static abstract class IniOperation
    {
        private String section;
        private String property;
        private String ifCond;
        private String unlessCond;

        public IniOperation()
        {
            super();
        }

        public String getSection()
        {
            return section;
        }


        public void setSection(String section)
        {
            this.section = section;
        }


        public String getProperty()
        {
            return property;
        }


        public void setProperty(String property)
        {
            this.property = property;
        }


        public void setIf(String ifCond)
        {
            this.ifCond = ifCond;
        }

        public void setUnless(String unlessCond)
        {
            this.unlessCond = unlessCond;
        }

        /**
         * Returns true if the define's if and unless conditions
         * (if any) are satisfied.
         */
        public boolean isActive(org.apache.tools.ant.Project p)
        {
            if (ifCond != null && p.getProperty(ifCond) == null)
            {
                return false;
            }
            else if (unlessCond != null && p.getProperty(unlessCond) != null)
            {
                return false;
            }

            return true;
        }

        public void execute(Project project, IniFile iniFile)
        {
            if (isActive(project))
                operate(iniFile);
        }

        protected abstract void operate(IniFile file);
    }

    public static final class Remove
            extends IniOperation
    {
        public Remove()
        {
            super();
        }

        protected void operate(IniFile file)
        {
            String secName = getSection();
            String propName = getProperty();

            if (propName == null)
            {
                file.removeSection(secName);
            }
            else
            {
                IniSection section = file.getSection(secName);
                if (section != null)
                    section.removeProperty(propName);
            }
        }
    }


    public final class Set
            extends IniOperation
    {
        private String value;
        private String operation;

        public Set()
        {
            super();
        }


        public void setValue(String value)
        {
            this.value = value;
        }


        public void setOperation(String operation)
        {
            this.operation = operation;
        }


        protected void operate(IniFile file)
        {
            String secName = getSection();
            String propName = getProperty();

            IniSection section = file.getSection(secName);
            if (section == null)
            {
                section = new IniSection(secName);
                file.setSection(section);
            }

            if (propName != null)
            {
                if (operation != null)
                {
                    if ("+".equals(operation))
                    {
                        IniProperty prop = section.getProperty(propName);
                        value = prop.getValue();
                        int intVal = Integer.parseInt(value) + 1;
                        value = String.valueOf(intVal);
                    }
                    else if ("-".equals(operation))
                    {
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

    private File source;
    private File dest;
    private Vector operations;

    public IniFileTask()
    {
        super();
        this.operations = new Vector();
    }

    public Set createSet()
    {
        Set set = new Set();
        operations.add(set);
        return set;
    }

    public Remove createRemove()
    {
        Remove remove = new Remove();
        operations.add(remove);
        return remove;
    }


    public void setSource(File source)
    {
        this.source = source;
    }


    public void setDest(File dest)
    {
        this.dest = dest;
    }


    public void execute()
        throws BuildException
    {
        if (dest == null)
            throw new BuildException("You must supply a dest file to write to.");

        IniFile iniFile = null;

        try
        {
            iniFile = readIniFile(source);
        }
        catch (IOException e)
        {
            throw new BuildException(e);
        }

        Iterator it = operations.iterator();
        IniOperation operation = null;
        while (it.hasNext())
        {
            operation = (IniOperation)it.next();
            operation.execute(getProject(), iniFile);
        }

        FileWriter writer = null;

        try
        {
            try
            {
                writer = new FileWriter(dest);
                iniFile.write(writer);
            }
            finally
            {
                try
                {
                    if (writer != null)
                        writer.close();
                }
                catch (IOException e)
                {
                    ; // gulp
                }
            }
        }
        catch (IOException e)
        {
            throw new BuildException(e);
        }

    }


    private IniFile readIniFile(File source)
        throws IOException
    {
        FileReader reader = null;
        IniFile iniFile = new IniFile();

        if (source == null)
            return iniFile;

        try
        {
            reader = new FileReader(source);
            iniFile.read(reader);
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close();
            }
            catch (IOException e)
            {
                ; // gulp
            }
        }

        return iniFile;
    }
}
