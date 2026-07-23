package com.diameter.controller;

import com.diameter.model.DiameterSessionCdr;
import com.diameter.service.DiameterSessionCdrService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping("/cdr")
public class DiameterSessionCdrController {

    private final
    DiameterSessionCdrService service;

    public DiameterSessionCdrController(DiameterSessionCdrService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DiameterSessionCdr> createCdr(@RequestBody DiameterSessionCdr cdr) throws ValidationException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCdr(cdr));
    }

    @GetMapping
    public ResponseEntity<?> getCdr(

            @RequestParam(required = false)
            Long id,

            @RequestParam(required = false)
            String sessionId,

            @RequestParam(required = false)
            String transactionId,

            @RequestParam(required = false)
            String msisdn,

            @RequestParam(required = false)
            String imsi,

            @RequestParam(required = false)
            String status,

            @RequestParam(required = false)
            String serviceType,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate createdDate,

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
                service.getCdr(
                        id,
                        sessionId,
                        transactionId,
                        msisdn,
                        imsi,
                        status,
                        serviceType,
                        createdDate,
                        fromDate,
                        toDate,
                        page,
                        size
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiameterSessionCdr> updateCdr(@PathVariable Long id, @RequestBody DiameterSessionCdr cdr) throws ValidationException {
        return ResponseEntity.ok(service.updateCdr(id, cdr));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCdr(@PathVariable Long id) throws ValidationException {
        service.deleteCdr(id);
        return ResponseEntity.noContent().build();
    }

}