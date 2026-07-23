package com.savbill.radius.helper;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(value = "Proxy Server",description = "This is data transfer object for ProxyServer which is used to create/update proxy server")
public class ProxyServerDto {
	
	@NotNull
	@NotBlank
	@ApiModelProperty(notes = "Name of the proxy server",required=true)
	private String name;

	@NotNull
	@NotBlank
	@ApiModelProperty(notes = "Ip address of the proxy server",required=true)
	private String ip;

	@NotNull
	@NotBlank
	@ApiModelProperty(notes = "Auth port of the proxy server",required=true)
	private String authport;

	@NotNull
	@NotBlank
	@ApiModelProperty(notes = "Acct port of the proxy server",required=true)
	private String acctport;

	@NotNull
	@NotBlank
	@ApiModelProperty(notes = "Secret key of the proxy server",required=true)
	private String secretkey;
	
	@NotNull
	@ApiModelProperty(notes = "Status of the proxy server",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",required = true)
	private String status;

	@ApiModelProperty(notes = "Status of the proxy server",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive")
	private String dynaAuthPort;
	
	@ApiModelProperty(notes = "Status of the proxy server",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive")
	private Boolean overrideNAS;

	@ApiModelProperty(notes = "NAS Ip address for replacement",required=true)
	private String nasip;
	
	
	@ApiModelProperty(notes = "timeout for proxy",required=true)
	private String timeout;

	
	public String getDynaAuthPort() {
		return dynaAuthPort;
	}

	public void setDynaAuthPort(String dynaAuthPort) {
		this.dynaAuthPort = dynaAuthPort;
	}

	public Boolean getOverrideNAS() {
		return overrideNAS;
	}

	public void setOverrideNAS(Boolean overrideNAS) {
		this.overrideNAS = overrideNAS;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNasip() {
		return nasip;
	}

	public void setNasip(String nasip) {
		this.nasip = nasip;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	
	
}
