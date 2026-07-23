package com.diameter.controller;

import com.diameter.model.DiameterLiveSession;
import com.diameter.service.DiameterLiveSessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/live-session")
public class DiameterLiveSessionController {

    private final DiameterLiveSessionService service;

    public DiameterLiveSessionController(DiameterLiveSessionService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DiameterLiveSession>
    createSession(@RequestBody DiameterLiveSession session) throws ValidationException {
        return ResponseEntity.ok(service.createSession(session));
    }


    @GetMapping
    public ResponseEntity<?> getSession(

            @RequestParam(required = false) String sessionId,

            @RequestParam(required = false) String transactionId,

            @RequestParam(required = false) String msisdn,

            @RequestParam(required = false) String imsi,

            @RequestParam(required = false) String status,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate createdAt,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate toDate,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                service.getSessions(
                        sessionId,
                        transactionId,
                        msisdn,
                        imsi,
                        status,
                        createdAt,
                        fromDate,
                        toDate,
                        page,
                        size));
    }


    @PutMapping("/{sessionId}")
    public ResponseEntity<DiameterLiveSession> updateSession(@PathVariable String sessionId, @RequestBody DiameterLiveSession session) throws ValidationException {
        return ResponseEntity.ok(service.updateSession(sessionId, session));
    }


    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) throws ValidationException {service.deleteSession(sessionId);return ResponseEntity.noContent().build();}
}