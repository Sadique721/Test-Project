package com.savbill.radius.aaa.data;

import java.io.Serializable;

public class ProxyServerData implements Serializable{
	private int proxyserverid;
	private String name;
	private String ip;
	private String authport;
	private String acctport;
	private String secretkey;
	public int getProxyserverid() {
		return proxyserverid;
	}
	public void setProxyserverid(int proxyserverid) {
		this.proxyserverid = proxyserverid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getAuthport() {
		return authport;
	}
	public void setAuthport(String authport) {
		this.authport = authport;
	}
	public String getAcctport() {
		return acctport;
	}
	public void setAcctport(String acctport) {
		this.acctport = acctport;
	}
	public String getSecretkey() {
		return secretkey;
	}
	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}
	
	

}
