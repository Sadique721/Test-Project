package com.diameter.model;

import javax.validation.constraints.*;
import java.util.List;

public class PeerConfiguration {

	private Long id;

	@NotBlank(message = "Node name is required")
	@Size(max = 64, message = "Node name must be at most 64 characters")
	//@Pattern(regexp = "^(?=[A-Za-z_])(?=.*[A-Za-z])[A-Za-z0-9_]{1,64}$", message = "Node name must be alphanumeric with underscores, must not start with a digit, and must contain at least one alphabet")
	private String nodeName;

	@NotBlank(message = "Realm is required")
	@Size(max = 255, message = "Realm must be at most 255 characters")
	@Pattern(regexp = "^(?=.{1,255}$)(?!.*\\.\\.)(?:_?[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(?:\\.(?:_?[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?))*$", message = "Realm labels must be dot-separated. Each label may start with an underscore followed by alphanumeric and dashes only. Labels must end with a letter or digit, and be at most 63 characters.")
	private String realm;

	@NotBlank(message = "FQDN is required")
	@Size(max = 255, message = "FQDN must be at most 255 characters")
	@Pattern(regexp = "^(?=.{1,255}$)(?!.*\\.\\.)(?:_?[A-Za-z](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(?:\\.(?:_?[A-Za-z](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?))*$", message = "FQDN must be dot-separated labels with valid characters. Labels may contain letters, digits, dash (-), and underscore (_) only at the start. Each label must start with a letter or underscore, and end with a letter or digit.")
	private String fqdn;

	@Min(value = 1024, message = "SCTP Listen Port must be at least 1024")
	@Max(value = 49151, message = "SCTP Listen Port must not exceed 49151")
	private Integer sctpListenPort = 3868;

	@NotNull(message = "TCP port is required")
	@Min(value = 1024, message = "TCP Listen Port must be at least 1024")
	@Max(value = 49151, message = "TCP Listen Port must not exceed 49151")
	private Integer tcpListenPort = 3868;

	@Min(value = 1024, message = "DTLS/SCTP Listen Port must be at least 1024")
	@Max(value = 49151, message = "DTLS/SCTP Listen Port must not exceed 49151")
	private Integer dtlsSctpListenPort = 5658;

	@Min(value = 1024, message = "TLS/TCP Listen Port must be at least 1024")
	@Max(value = 49151, message = "TLS/TCP Listen Port must not exceed 49151")
	private Integer tlsTcpListenPort = 5658;

	@Min(value = 1024, message = "RADIUS UDP Server Port must be at least 1024")
	@Max(value = 49151, message = "RADIUS UDP Server Port must not exceed 49151")
	private Integer radiusUdpServerPort;

	private boolean enableRadiusUdpClientPorts = false;

	@Min(value = 1024, message = "RADIUS Client UDP Port Range Start must be at least 1024")
	@Max(value = 49151, message = "RADIUS Client UDP Port Range Start must not exceed 49151")
	private Integer radiusClientUdpPortRangeStart = 2000;

	@Min(value = 1024, message = "RADIUS Client UDP Port Range End must be at least 1024")
	@Max(value = 49151, message = "RADIUS Client UDP Port Range End must not exceed 49151")
	private Integer radiusClientUdpPortRangeEnd = 2499;

	//@NotNull(message = "Verification mode is required")
	private VerificationMode verificationMode = VerificationMode.VERIFY_NONE;

	private String certificateType;

	@Size(max = 128, message = "Certificate name must be at most 128 characters")
	private String certificateName;

	@NotNull(message = "IP address list is required")
	@Size(min = 1, max = 128, message = "You must assign between 1 and 128 IP addresses")
	private List<String> ipAddresses;

	@NotNull(message = "Status is required")
	private Status status = Status.ACTIVE;

	@NotNull(message = "Remote port is required")
	@Min(value = 1024, message = "TCP Listen Port must be at least 1024")
	@Max(value = 49151, message = "TCP Listen Port must not exceed 49151")
	private Integer remotePort = 3868;

	@NotBlank(message = "Remote IP address list is required")
	@Size(min = 1, max = 128, message = "You must assign between 1 and 128 IP addresses")
	private String remoteIpAddress;
	
	@NotNull(message = "WatchDogInterval is required")
	@Min(value = 0, message = "Watchdog interval must be non-negative")
	private Integer watchdogInterval = 1000;

	private String peerStatus;

	public enum VerificationMode {
		VERIFY_NONE("Verify None"), VERIFY_PEER("Verify Peer"), FAIL_IF_NO_PEER_CERT("Fail if No Peer Certificate"),
		VERIFY_CLIENT_ONCE("Verify Client Once");

		private final String dbValue;

		VerificationMode(String dbValue) {
			this.dbValue = dbValue;
		}

		public String getDbValue() {
			return dbValue;
		}

		public static VerificationMode fromDbValue(String value) {
			for (VerificationMode mode : values()) {
				if (mode.dbValue.equalsIgnoreCase(value)) {
					return mode;
				}
			}
			throw new IllegalArgumentException("Invalid verification mode: " + value);
		}
	}

	public enum Status {
		ACTIVE("Active"), INACTIVE("Inactive"), DELETED("Deleted");

		private final String dbValue;

		Status(String dbValue) {
			this.dbValue = dbValue;
		}

		public String getDbValue() {
			return dbValue;
		}

		public static Status fromDbValue(String value) {
			for (Status status : values()) {
				if (status.dbValue.equalsIgnoreCase(value)) {
					return status;
				}
			}
			throw new IllegalArgumentException("Invalid status: " + value);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

	public Integer getSctpListenPort() {
		return sctpListenPort;
	}

	public void setSctpListenPort(Integer sctpListenPort) {
		this.sctpListenPort = sctpListenPort;
	}

	public Integer getTcpListenPort() {
		return tcpListenPort;
	}

	public void setTcpListenPort(Integer tcpListenPort) {
		this.tcpListenPort = tcpListenPort;
	}

	public Integer getDtlsSctpListenPort() {
		return dtlsSctpListenPort;
	}

	public void setDtlsSctpListenPort(Integer dtlsSctpListenPort) {
		this.dtlsSctpListenPort = dtlsSctpListenPort;
	}

	public Integer getTlsTcpListenPort() {
		return tlsTcpListenPort;
	}

	public void setTlsTcpListenPort(Integer tlsTcpListenPort) {
		this.tlsTcpListenPort = tlsTcpListenPort;
	}

	public Integer getRadiusUdpServerPorts() {
		return radiusUdpServerPort;
	}

	public void setRadiusUdpServerPorts(Integer radiusUdpServerPort) {
		this.radiusUdpServerPort = radiusUdpServerPort;
	}

	public boolean isEnableRadiusUdpClientPorts() {
		return enableRadiusUdpClientPorts;
	}

	public void setEnableRadiusUdpClientPorts(boolean enableRadiusUdpClientPorts) {
		this.enableRadiusUdpClientPorts = enableRadiusUdpClientPorts;
	}

	public Integer getRadiusClientUdpPortRangeStart() {
		return radiusClientUdpPortRangeStart;
	}

	public void setRadiusClientUdpPortRangeStart(Integer radiusClientUdpPortRangeStart) {
		this.radiusClientUdpPortRangeStart = radiusClientUdpPortRangeStart;
	}

	public Integer getRadiusClientUdpPortRangeEnd() {
		return radiusClientUdpPortRangeEnd;
	}

	public void setRadiusClientUdpPortRangeEnd(Integer radiusClientUdpPortRangeEnd) {
		this.radiusClientUdpPortRangeEnd = radiusClientUdpPortRangeEnd;
	}

	public VerificationMode getVerificationMode() {
		return verificationMode;
	}

	public void setVerificationMode(VerificationMode verificationMode) {
		this.verificationMode = verificationMode;
	}

	public String getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	public List<String> getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(List<String> ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getRadiusUdpServerPort() {
		return radiusUdpServerPort;
	}

	public void setRadiusUdpServerPort(Integer radiusUdpServerPort) {
		this.radiusUdpServerPort = radiusUdpServerPort;
	}

	public Integer getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(Integer remotePort) {
		this.remotePort = remotePort;
	}

	public String getRemoteIpAddress() {
		return remoteIpAddress;
	}

	public void setRemoteIpAddress(String remoteIpAddress) {
		this.remoteIpAddress = remoteIpAddress;
	}

	public Integer getWatchdogInterval() {
		return watchdogInterval;
	}

	public void setWatchdogInterval(Integer watchdogInterval) {
		this.watchdogInterval = watchdogInterval;
	}

	public String getPeerStatus() {
		return peerStatus;
	}

	public void setPeerStatus(String peerStatus) {
		this.peerStatus = peerStatus;
	}
}
