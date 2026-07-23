package com.diameter.service;

import java.math.BigDecimal;
import java.util.List;

import com.diameter.dto.DiameterPeerResponse;
import com.diameter.dto.DiameterSessionResponse;
import com.diameter.model.DiameterLiveSession;

public interface DiameterMonitoringService {
	
	List<DiameterSessionResponse> getLiveSessions();
	
	List<DiameterPeerResponse> getPeerStatus();

	DiameterLiveSession getSessionById(String sessionId);
	
}
