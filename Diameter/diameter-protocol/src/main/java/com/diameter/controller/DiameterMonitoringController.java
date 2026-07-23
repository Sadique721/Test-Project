package com.diameter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diameter.dto.DiameterPeerResponse;
import com.diameter.dto.DiameterSessionResponse;
import com.diameter.service.DiameterMonitoringService;
import org.springframework.web.bind.annotation.PathVariable;

import com.diameter.model.DiameterLiveSession;

@RestController
@RequestMapping("/monitor")
public class DiameterMonitoringController {

	@Autowired
    private DiameterMonitoringService monitoringService;

    /**
     * Get all live sessions
     */
    @GetMapping("/sessions/live")
    public List<DiameterSessionResponse> getLiveSessions() {
        return monitoringService.getLiveSessions();
    }

    /**
     * Get session by session id
     */
    @GetMapping("/sessions/live/{sessionId}")
    public DiameterLiveSession getSessionById(
            @PathVariable String sessionId) {

        return monitoringService
                .getSessionById(sessionId);
    }

    /**
     * Get peer status
     */
    @GetMapping("/peers/status")
    public List<DiameterPeerResponse> getPeerStatus() {
        return monitoringService.getPeerStatus();
    }
}
