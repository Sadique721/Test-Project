package com.diameter.commons;

import java.util.List;

public interface PeerData extends PeerConnectionData {
	public static final int MAX_RETRANSMISSION_COUNT = 3;

	public static final int NO_RETRANSMISSION = 0;

	public static final int MAX_TIMEOUT_MS = 10000;

	public static final int DEFAULT_TIMEOUT_MS = 3000;

	public static final int MIN_TIMEOUT_MS = 1000;

	public static final String DEFAULT_URI_FORMAT = "${aaa}${FQDN}${port}${tansport-protocol}${aaa-protocol}";

	String getPeerName();

	String getHostIdentity();

	String getRealmName();

	boolean isInitConnection();

	int getWatchdogInterval();

	List<IDiameterAVP> getAdditionalCERAvps();

	List<IDiameterAVP> getAdditionalDWRAvps();

	List<IDiameterAVP> getAdditionalDPRAvps();

	int getInitiateConnectionDuration();

	int getRetryCount();

	long getRequestTimeout();

	void setPeerName(String paramString);

	void setHostIdentity(String paramString);

	boolean isSessionCleanUpOnCER();

	boolean isSessionCleanUpOnDPR();

	boolean isSendDPRonCloseEvent();

	boolean isMsccBasedReservationInitialRequest();

	boolean isFollowRedirection();

	RedirectHostAVPFormat getRedirectHostAVPFormat();

	String getURI();

	Object clone() throws CloneNotSupportedException;

	String getExclusiveAuthAppIDs();

	String getExclusiveAcctAppIDs();

	long getPeerIndex();

	void setPeerIndex(long paramLong);

	String getHAAddress();

	String getDHCPAddress();

	String getSecondaryPeerName();

	String getHotlinePolicy();

	void setHotlinePolicy(String paramString);

	boolean isReTransmissionCompliant();

	void setWatchdogInterval(int paramInt);

	void setAdditionalCERAvps(List<IDiameterAVP> paramList);

	void setAdditionalDWRAvps(List<IDiameterAVP> paramList);

	void setAdditionalDPRAvps(List<IDiameterAVP> paramList);

	void setRequestTimeout(long paramLong);

	void setSessionCleanUpOnCER(boolean paramBoolean);

	void setSessionCleanUpOnDPR(boolean paramBoolean);

	void setSendDPRonCloseEvent(boolean paramBoolean);

	void setFollowRedirection(boolean paramBoolean);

	void setRedirectHostAVPFormat(RedirectHostAVPFormat paramRedirectHostAVPFormat);

	void setExclusiveAuthAppIDs(String paramString);

	void setExclusiveAcctAppIDs(String paramString);

	void setRetransmissionCount(Integer paramInteger);

	void setInitiateConnectionDuration(Integer paramInteger);

	void setURI(String paramString);

	void setSecondaryPeerName(String paramString);

	void setHaIpAddress(String paramString);

	void setDhcpIpAddress(String paramString);

	void reload(PeerData paramPeerData);

	DuplicateConnectionPolicyType getDuplicateConnectionPolicyType();

	public enum DuplicateConnectionPolicyType {
		DEFAULT, DISCARD_OLD;
	}

	void setRealmName(String string);
}
