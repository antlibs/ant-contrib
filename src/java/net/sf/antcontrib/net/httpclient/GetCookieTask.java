/*
 * Copyright (c) 2001-2006 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.net.httpclient;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;

public class GetCookieTask
	extends AbstractHttpStateTypeTask {

	private String property;
    private String prefix;
    private String fieldSeparator = "/";
    
	private String cookiePolicy = CookiePolicy.DEFAULT;
	
	private String realm = null;
    private int port = 80;
    private String path = null;
    private boolean secure = false;
    
	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public void setCookiePolicy(String cookiePolicy) {
		this.cookiePolicy = cookiePolicy;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	protected void execute(HttpStateType stateType) throws BuildException {
		
		if (realm == null || path == null) {
			throw new BuildException("'realm' and 'path' attributes are required");
		}
		
		HttpState state = stateType.getState();
		CookieSpec spec = CookiePolicy.getCookieSpec(cookiePolicy);
		Cookie cookies[] = state.getCookies();
		Cookie matches[] = spec.match(realm, port, path, secure, cookies);
		
		if (property != null) {
			if (matches != null && matches.length > 0) {
				if (matches.length > 1) {
					log("Multiple cookies matched the query, returning only the first one.",
							Project.MSG_WARN);
				}
				Property p = (Property)getProject().createTask("property");
				p.setName(property);
				p.setValue(matches[0].getValue());
			}
		}
		else if (prefix != null) {
			if (matches != null && matches.length > 0) {
				for (int i=0;i<matches.length;i++) {
					String propName =
						prefix +
						fieldSeparator + matches[i].getDomain() +
						fieldSeparator + matches[i].getPath() +
						fieldSeparator + matches[i].getName();
					Property p = (Property)getProject().createTask("property");
					p.setName(propName);
					p.setValue(matches[i].getValue());
					
				}
			}
			
		}
	}
	
	
}
