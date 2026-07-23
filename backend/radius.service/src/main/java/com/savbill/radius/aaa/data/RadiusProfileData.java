package com.savbill.radius.aaa.data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RadiusProfileData implements Serializable{
	private String name;
	private String checkitem;
	private String accountcdrstatus;
	private String sessionstatus;
	private int mappingmasterid;
	private int priority;
	private int proxyserverid;
	private String type;
	private String authaudit;
	
    private transient ConcurrentMap dbFieldMapping=new ConcurrentHashMap();
    
	
	public ConcurrentMap getDbFieldMapping() {
		return dbFieldMapping;
	}
	public void setDbFieldMapping(ConcurrentMap dbFieldMapping) {
		this.dbFieldMapping = dbFieldMapping;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCheckitem() {
		return checkitem;
	}
	public void setCheckitem(String checkitem) {
		this.checkitem = checkitem;
	}
	public String getAccountcdrstatus() {
		return accountcdrstatus;
	}
	public void setAccountcdrstatus(String accountcdrstatus) {
		this.accountcdrstatus = accountcdrstatus;
	}
	public String getSessionstatus() {
		return sessionstatus;
	}
	public void setSessionstatus(String sessionstatus) {
		this.sessionstatus = sessionstatus;
	}
	public int getMappingmasterid() {
		return mappingmasterid;
	}
	public void setMappingmasterid(int mappingmasterid) {
		this.mappingmasterid = mappingmasterid;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getProxyserverid() {
		return proxyserverid;
	}
	public void setProxyserverid(int proxyserverid) {
		this.proxyserverid = proxyserverid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAuthaudit() {
		return authaudit;
	}
	public void setAuthaudit(String authaudit) {
		this.authaudit = authaudit;
	}
	

	
	
}
