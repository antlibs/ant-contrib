package net.sf.antcontrib.net.httpclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;

public class AddCredentialsTask
	extends AbstractHttpStateTypeTask {
	
	private List credentials = new ArrayList();
	private List proxyCredentials = new ArrayList();

	public void addConfiguredCredentials(Credentials credentials) {
		this.credentials.add(credentials);
	}

	public void addConfiguredProxyCredentials(Credentials credentials) {
		this.proxyCredentials.add(credentials);
	}
	
	protected void execute(HttpStateType stateType) throws BuildException {
		if (credentials.isEmpty() && proxyCredentials.isEmpty()) {
			throw new BuildException("Either regular or proxy credentials" +
					" must be supplied.");
		}
		
		Iterator it = credentials.iterator();
		while (it.hasNext()) {
			Credentials c = (Credentials)it.next();
			stateType.addConfiguredCredentials(c);
		}

		it = proxyCredentials.iterator();
		while (it.hasNext()) {
			Credentials c = (Credentials)it.next();
			stateType.addConfiguredProxyCredentials(c);
		}
	}
}
