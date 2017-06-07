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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;

import net.sf.antcontrib.antserver.Command;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
@SuppressWarnings("serial")
public class RunAntCommand extends AbstractCommand implements Command {
    /**
     * Field antFile.
     */
    private String antFile;

    /**
     * Field dir.
     */
    private String dir;

    /**
     * Field target.
     */
    private String target;

    /**
     * Field properties.
     */
    private List<PropertyContainer> properties;

    /**
     * Field references.
     */
    private List<ReferenceContainer> references;

    /**
     * Field inheritall.
     */
    private boolean inheritall = false;

    /**
     * Field interitrefs.
     */
    private boolean interitrefs = false;

    /**
     * Constructor for RunAntCommand.
     */
    public RunAntCommand() {
        super();
        this.properties = new ArrayList<PropertyContainer>();
        this.references = new ArrayList<ReferenceContainer>();
    }

    /**
     * Method getTarget.
     *
     * @return String
     */
    public String getTarget() {
        return target;
    }

    /**
     * Method setTarget.
     *
     * @param target String
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Method getProperties.
     *
     * @return List&lt;PropertyContainer&gt;
     */
    public List<PropertyContainer> getProperties() {
        return properties;
    }

    /**
     * Method setProperties.
     *
     * @param properties List&lt;PropertyContainer&gt;
     */
    public void setProperties(List<PropertyContainer> properties) {
        this.properties = properties;
    }

    /**
     * Method getReferences.
     *
     * @return List&lt;ReferenceContainer&gt;
     */
    public List<ReferenceContainer> getReferences() {
        return references;
    }

    /**
     * Method setReference.
     *
     * @param references List&lt;ReferenceContainer&gt;
     */
    public void setReference(List<ReferenceContainer> references) {
        this.references = references;
    }

    /**
     * Method isInheritall.
     *
     * @return boolean
     */
    public boolean isInheritall() {
        return inheritall;
    }

    /**
     * Method setInheritall.
     *
     * @param inheritall boolean
     */
    public void setInheritall(boolean inheritall) {
        this.inheritall = inheritall;
    }

    /**
     * Method isInteritrefs.
     *
     * @return boolean
     */
    public boolean isInteritrefs() {
        return interitrefs;
    }

    /**
     * Method setInteritrefs.
     *
     * @param interitrefs boolean
     */
    public void setInteritrefs(boolean interitrefs) {
        this.interitrefs = interitrefs;
    }

    /**
     * Method getAntFile.
     *
     * @return String
     */
    public String getAntFile() {
        return antFile;
    }

    /**
     * Method setAntFile.
     *
     * @param antFile String
     */
    public void setAntFile(String antFile) {
        this.antFile = antFile;
    }

    /**
     * Method getDir.
     *
     * @return String
     */
    public String getDir() {
        return dir;
    }

    /**
     * Method setDir.
     *
     * @param dir String
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * Method addConfiguredProperty.
     *
     * @param property PropertyContainer
     */
    public void addConfiguredProperty(PropertyContainer property) {
        properties.add(property);
    }

    /**
     * Method addConfiguredReference.
     *
     * @param reference ReferenceContainer
     */
    public void addConfiguredReference(ReferenceContainer reference) {
        references.add(reference);
    }

    /**
     * Method validate.
     *
     * @param project Project
     * @see net.sf.antcontrib.antserver.Command#validate(Project)
     */
    public void validate(Project project) {
    }

    /**
     * Method execute.
     *
     * @param project       Project
     * @param contentLength long
     * @param content       InputStream
     * @return boolean
     * @throws BuildException if something goes wrong
     * @see net.sf.antcontrib.antserver.Command#execute(Project, long, InputStream)
     */
    public boolean execute(Project project,
                           long contentLength,
                           InputStream content)
            throws BuildException {
        Ant ant = (Ant) project.createTask("ant");
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

        for (PropertyContainer pc : properties) {
            Property p = ant.createProperty();
            p.setName(pc.getName());
            p.setValue(pc.getValue());
        }

        for (ReferenceContainer rc : references) {
            Ant.Reference ref = new Ant.Reference();
            ref.setRefId(rc.getRefId());
            ref.setToRefid(rc.getToRefId());
            ant.addReference(ref);
        }

        ant.execute();

        return false;
    }
}
