package net.sf.antcontrib.net.httpclient;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public abstract class AbstractHttpStateTypeTask
	extends Task {
	
	private String stateRefId;

	public void setStateRefId(String stateRefId) {
		this.stateRefId = stateRefId;
	}
	
	public Credentials createCredentials() {
		return new Credentials();
	}
	
	static HttpStateType getStateType(Project project, String stateRefId) {
		if (stateRefId == null) {
			throw new BuildException("Missing 'stateRefId'.");
		}
		
		Object stateRef = project.getReference(stateRefId);
		if (stateRef == null) {
			throw new BuildException("Reference '" + stateRefId +
					"' is not defined.");
		}
		if (! (stateRef instanceof HttpStateType)) {
			throw new BuildException("Reference '" + stateRefId +
					"' is not of the correct type.");
		}
		
		return (HttpStateType) stateRef;
	}
	
	public void execute()
		throws BuildException {		
		execute(getStateType(getProject(), stateRefId));
	}

	protected abstract void execute(HttpStateType stateType)
		throws BuildException;
}
