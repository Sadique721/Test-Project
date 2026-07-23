package com.diameter.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;

import com.diameter.dto.DiameterPeerResponse;
import com.diameter.model.ApiResponse;
import com.diameter.service.DiameterMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diameter.model.PeerConfiguration;
import com.diameter.service.PeerConfigurationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/peer-configurations")
public class PeerConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(PeerConfigurationController.class);
    private final PeerConfigurationService service;
    private final DiameterMonitoringService monitoringService;

    public PeerConfigurationController(PeerConfigurationService service, DiameterMonitoringService monitoringService) {
        this.service = service;
        this.monitoringService = monitoringService;
    }

    @PostMapping
    public ResponseEntity<PeerConfiguration> create(@Valid @RequestBody PeerConfiguration config) throws ValidationException {
        logger.debug("POST /api/peer-configurations - Create");
        PeerConfiguration saved = service.create(config);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<PeerConfiguration>> getAll(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "remoteIpAddress", required = false) String remoteIpAddress,
            @RequestParam(name = "realm", required = false) String realm
    ) {

        logger.debug("GET /api/peer-configurations - Get All");
        List<PeerConfiguration> peers = service.getAll(id, name, remoteIpAddress, realm);

        try {
            Map<String, String> statusMap = monitoringService.getPeerStatus().stream()
                            .collect(Collectors.toMap(
                                    DiameterPeerResponse::getPeerName,
                                    DiameterPeerResponse::getState,
                                    (existing, duplicate) -> existing));
            for (PeerConfiguration peer : peers) {
                peer.setPeerStatus(statusMap.getOrDefault(peer.getNodeName(), "Closed"));
            }
        } catch (Exception ex) {
            logger.error("Failed to fetch peer status", ex);
            peers.forEach(peer -> peer.setPeerStatus("Closed"));
        }
        return ResponseEntity.ok(peers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeerConfiguration> getById(@PathVariable long id) {
        PeerConfiguration peer = service.getById(id).orElseThrow(() -> new IllegalArgumentException("Peer configuration not found with ID " + id));
        try {
            String status = monitoringService.getPeerStatus()
                            .stream()
                            .filter(p -> p.getPeerName().equals(peer.getNodeName()))
                            .map(DiameterPeerResponse::getState)
                            .findFirst()
                            .orElse("Closed");
            peer.setPeerStatus(status);
        } catch (Exception ex) {
            logger.error("Failed to fetch peer status", ex);
            peer.setPeerStatus("Closed");
        }
        return ResponseEntity.ok(peer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeerConfiguration> update(@PathVariable long id,
                                                    @Valid @RequestBody PeerConfiguration config) throws ValidationException {
        logger.debug("PUT /api/peer-configurations/{} - Update", id);
        PeerConfiguration updated = service.update(id, config);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable long id, HttpServletRequest request) {
        logger.debug("DELETE /api/peer-configurations/{} - Deleting peer configuration", id);
        service.delete(id); // throws 404 if not found
        logger.debug("Peer configuration deleted successfully: {}", id);
        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Peer Configuration Deleted Successfully", request.getRequestURI());
        return ResponseEntity.ok(response);
    }
}
