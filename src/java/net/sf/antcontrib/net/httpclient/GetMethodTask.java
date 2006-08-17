package net.sf.antcontrib.net.httpclient;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;

public class GetMethodTask
	extends AbstractMethodTask {

	protected HttpMethodBase createNewMethod() {
		return new PostMethod();
	}

	
}
