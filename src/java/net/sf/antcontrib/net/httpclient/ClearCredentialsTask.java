package net.sf.antcontrib.net.httpclient;

import org.apache.tools.ant.BuildException;

public class ClearCredentialsTask
	extends AbstractHttpStateTypeTask {
	
	private boolean proxy = false;

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}
	
	protected void execute(HttpStateType stateType) throws BuildException {
		if (proxy) {
			stateType.getState().clearProxyCredentials();
		}
		else {
			stateType.getState().clearCredentials();
		}		
	}
}
