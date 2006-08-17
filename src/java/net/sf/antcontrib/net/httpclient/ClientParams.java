package net.sf.antcontrib.net.httpclient;

import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.tools.ant.BuildException;

public class ClientParams
	extends HttpClientParams {
	private static final long serialVersionUID = -1;
	
	public void setVersion(String version) {
		try {
			setVersion(HttpVersion.parse(version));
		}
		catch (ProtocolException e) {
			throw new BuildException(e);
		}
	}
	
	public void addConfiguredDouble(Params.DoubleParam param) {
		setDoubleParameter(param.getName(), param.getValue());
	}

	public void addConfiguredInt(Params.IntParam param) {
		setIntParameter(param.getName(), param.getValue());
	}

	public void addConfiguredLong(Params.LongParam param) {
		setLongParameter(param.getName(), param.getValue());
	}
	
	public void addConfiguredString(Params.StringParam param) {
		setParameter(param.getName(), param.getValue());
	}
	
	public void setStrict(boolean strict) {
		if (strict) {
			makeStrict();
		}
		else {
			makeLenient();
		}
	}
	
}
