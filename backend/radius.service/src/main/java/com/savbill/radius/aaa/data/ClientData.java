package com.savbill.radius.aaa.data;

import java.io.Serializable;

public class ClientData implements Serializable{
   private String groupname;
   private String clintid;
   private String sharedkey;
   private String timeout;
   private String clientip;
   private int groupid;
   private String iptype;
   private int mvnoid;
   
   
   
   public String getIptype() {
		return iptype;
	}
	
	public void setIptype(String iptype) {
		this.iptype = iptype;
	}
	
	public int getGroupid() {
		return groupid;
	}
	
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	
	public String getClientip() {
	   return clientip;
	}

   public void setClientip(String clientip) {
		this.clientip = clientip;
	}
   
   public String getGroupname() {
	   return groupname;
   }

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getClintid() {
		return clintid;
	}
	public void setClintid(String clintid) {
		this.clintid = clintid;
	}
	public String getSharedkey() {
		return sharedkey;
	}
	public void setSharedkey(String sharedkey) {
		this.sharedkey = sharedkey;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public int getMvnoid() {
		return mvnoid;
	}

	public void setMvnoid(int mvnoid) {
		this.mvnoid = mvnoid;
	}
	
}
