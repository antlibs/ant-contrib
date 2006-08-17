package net.sf.antcontrib.net.httpclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;

public class HttpClientType
	extends DataType {

	private HttpClient client;
	
	public HttpClientType(Project p) {
		super();
		setProject(p);
		
		client = new HttpClient();
	}
	
	public HttpClient getClient() {
		if (isReference()) {
			return getRef().getClient();
		}
		else {
			return client;
		}
	}
	
	public void setStateRefId(String stateRefId) {
		if (isReference()) {
			tooManyAttributes();
		}
		HttpStateType stateType = AbstractHttpStateTypeTask.getStateType(
				getProject(),
				stateRefId);
		getClient().setState(stateType.getState());
	}
	
	protected HttpClientType getRef() {
		return (HttpClientType) super.getCheckedRef(HttpClientType.class,
				"http-client");
	}
	
	public ClientParams createClientParams() {
		if (isReference()) {
			tooManyAttributes();
		}
		ClientParams clientParams = new ClientParams();
		client.setParams(clientParams);
		return clientParams;
	}
}
