package net.sf.antcontrib.net.httpclient;

import java.util.Date;

import org.apache.tools.ant.BuildException;

public class PurgeExpiredCookiesTask
	extends AbstractHttpStateTypeTask {

	private Date expiredDate;
	
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	protected void execute(HttpStateType stateType) throws BuildException {
		if (expiredDate != null) {
			stateType.getState().purgeExpiredCookies(expiredDate);
		}
		else {
			stateType.getState().purgeExpiredCookies();
		}
	}

	
}
