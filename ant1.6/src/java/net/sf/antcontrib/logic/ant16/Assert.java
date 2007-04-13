/*
 * Copyright (c) 2001-2007 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.logic.ant16;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Exit;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.taskdefs.condition.Equals;


/**
 *
 * @ant.task   name="assert" category="logic"
 */
public class Assert
	extends ConditionBase {

	private String message;
	private boolean failOnError = true;
	private boolean execute = true;
    private Sequential sequential;
    private String name;
    private String value;
	
    public Sequential createSequential() {
    	this.sequential = (Sequential) getProject().createTask("sequential");
    	return this.sequential;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }
    
	public void setMessage(String message) {
		this.message = message;
	}

	public ConditionBase createBool() {
		return this;
	}
	
	public void setExecute(boolean execute) {
		this.execute = execute;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public void execute() {
		String enable = getProject().getProperty("ant.enable.asserts");
		boolean assertsEnabled = Project.toBoolean(enable);
		
		if (assertsEnabled) {
			if (name != null) {
				if (value == null) {
					throw new BuildException("The 'value' attribute must accompany the 'name' attribute.");
				}
				String propVal = getProject().replaceProperties("${" + name + "}");
				Equals e = new Equals();
				e.setArg1(propVal);
				e.setArg2(value);
				addEquals(e);
			}

			if (countConditions() == 0) {
				throw new BuildException("There is no condition specified.");
			}
			else if (countConditions() > 1) {
				throw new BuildException("There must be exactly one condition specified.");
			}

			Condition c = (Condition) getConditions().nextElement();
			if (! c.eval()) {
				if (failOnError) {
					Exit fail = (Exit) getProject().createTask("fail");
					fail.setMessage(message);
					fail.execute();
				}
			}
			else {
				if (execute) {
					this.sequential.execute();
				}
			}
		}
		else {
			if (execute) {
				this.sequential.execute();
			}			
		}
	}
	

}
