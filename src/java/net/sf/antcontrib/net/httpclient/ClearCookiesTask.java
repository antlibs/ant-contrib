package net.sf.antcontrib.net.httpclient;

import org.apache.tools.ant.BuildException;

public class ClearCookiesTask
	extends AbstractHttpStateTypeTask {

	protected void execute(HttpStateType stateType) throws BuildException {
		stateType.getState().clearCookies();
	}

	
}
