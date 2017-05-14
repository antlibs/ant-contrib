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
package net.sf.antcontrib.antserver.commands;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;

import net.sf.antcontrib.antserver.Command;

/**
 *
 * @author <a href='mailto:mattinger@yahoo.com'>Matthew Inger</a>
 *
 */
@SuppressWarnings("serial")
public class RunAntCommand
        extends AbstractCommand
        implements Command
{

    private String antFile;
    private String dir;
    private String target;
    private List<PropertyContainer> properties;
    private List<ReferenceContainer> references;
    private boolean inheritall = false;
    private boolean interitrefs = false;

    public RunAntCommand()
    {
        super();
        this.properties = new ArrayList<PropertyContainer>();
        this.references = new ArrayList<ReferenceContainer>();
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public List<PropertyContainer> getProperties()
    {
        return properties;
    }

    public void setProperties(List<PropertyContainer> properties)
    {
        this.properties = properties;
    }

    public List<ReferenceContainer> getReferences()
    {
        return references;
    }

    public void setReference(List<ReferenceContainer> references)
    {
        this.references = references;
    }

    public boolean isInheritall()
    {
        return inheritall;
    }

    public void setInheritall(boolean inheritall)
    {
        this.inheritall = inheritall;
    }

    public boolean isInteritrefs()
    {
        return interitrefs;
    }

    public void setInteritrefs(boolean interitrefs)
    {
        this.interitrefs = interitrefs;
    }

    public String getAntFile()
    {
        return antFile;
    }

    public void setAntFile(String antFile)
    {
        this.antFile = antFile;
    }

    public String getDir()
    {
        return dir;
    }

    public void setDir(String dir)
    {
        this.dir = dir;
    }

    public void addConfiguredProperty(PropertyContainer property)
    {
        properties.add(property);
    }

    public void addConfiguredReference(ReferenceContainer reference)
    {
        references.add(reference);
    }

    public void validate(Project project)
    {
    }

    public boolean execute(Project project,
                           long contentLength,
                           InputStream content)
            throws Throwable
    {
        Ant ant = (Ant)project.createTask("ant");
        File baseDir = project.getBaseDir();
        if (dir != null)
            baseDir = new File(dir);
        ant.setDir(baseDir);
        ant.setInheritAll(inheritall);
        ant.setInheritRefs(interitrefs);

        if (target != null)
            ant.setTarget(target);

        if (antFile != null)
            ant.setAntfile(antFile);


        for (PropertyContainer pc : properties)
        {
            Property p = ant.createProperty();
            p.setName(pc.getName());
            p.setValue(pc.getValue());
        }

        for (ReferenceContainer rc : references)
        {
            Ant.Reference ref = new Ant.Reference();
            ref.setRefId(rc.getRefId());
            ref.setToRefid(rc.getToRefId());
            ant.addReference(ref);
        }

        ant.execute();

        return false;
    }
}
