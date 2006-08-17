package net.sf.antcontrib.net.httpclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.tools.ant.BuildException;

public class AddCookieTask
	extends AbstractHttpStateTypeTask {

	private List cookies = new ArrayList();
	
	public void addConfiguredCookie(Cookie cookie) {
		this.cookies.add(cookie);
	}

	protected void execute(HttpStateType stateType) throws BuildException {
		if (this.cookies.isEmpty()) {
			throw new BuildException("At least one cookie must be specified.");
		}
		
		Iterator it = cookies.iterator();
		while (it.hasNext()) {
			Cookie c = (Cookie)it.next();
			stateType.addConfiguredCookie(c);
		}
	}
	
	
}
