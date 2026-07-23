package com.diameter.controller;

import com.diameter.model.DiameterAudit;
import com.diameter.service.DiameterAuditService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;

@RestController
@RequestMapping("/audit")
public class DiameterAuditController {

    private final
    DiameterAuditService service;

    public DiameterAuditController(DiameterAuditService service){
        this.service=service;
    }


    @PostMapping
    public ResponseEntity<DiameterAudit> createAudit(@RequestBody DiameterAudit audit) throws ValidationException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAudit(audit));
    }


    @GetMapping
    public ResponseEntity<?> getAudit(

            @RequestParam(required = false) Long id,

            @RequestParam(required = false)
            String transactionId,

            @RequestParam(required = false)
            String sessionId,

            @RequestParam(required = false)
            String msisdn,

            @RequestParam(required = false)
            String imsi,

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
            int size
    ) {

        return ResponseEntity.ok(
                service.getAudits(
                        id,
                        transactionId,
                        sessionId,
                        msisdn,
                        imsi,
                        createdAt,
                        fromDate,
                        toDate,
                        page,
                        size
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<DiameterAudit> updateAudit(@PathVariable Long id, @RequestBody DiameterAudit audit) throws ValidationException {
        return ResponseEntity.ok(service.updateAudit(id, audit));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAudit(@PathVariable Long id) throws ValidationException {
        service.deleteAudit(id);
        return ResponseEntity.noContent().build();
    }
}