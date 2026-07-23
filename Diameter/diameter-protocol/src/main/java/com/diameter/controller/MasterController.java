package com.diameter.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diameter.service.MasterService;

@RestController
@RequestMapping("/api/masters")
public class MasterController {

    private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getMasterData(
            @RequestParam String type) {
        logger.debug("GET /api/masters - Fetching master data for type: {}", type);
        Map<String, String> response = masterService.getMasterData(type);
        logger.debug("Master data fetched successfully for type: {}, count: {}",
                type, response.size());
        return ResponseEntity.ok(response);
    }
}