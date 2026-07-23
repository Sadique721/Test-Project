package com.diameter.model;

public class DiameterCacheRequestModel {
	
	private String sessionId;
	private String requestingHost;
	private String requestingRealm;
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getRequestingHost() {
		return requestingHost;
	}
	public void setRequestingHost(String requestingHost) {
		this.requestingHost = requestingHost;
	}
	public String getRequestingRealm() {
		return requestingRealm;
	}
	public void setRequestingRealm(String requestingRealm) {
		this.requestingRealm = requestingRealm;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DiameterCachRequestModel [sessionId=");
		builder.append(sessionId);
		builder.append(", requestingHost=");
		builder.append(requestingHost);
		builder.append(", requestingRealm=");
		builder.append(requestingRealm);
		builder.append("]");
		return builder.toString();
	}
}
