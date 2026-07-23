package com.diameter.controller;

import com.diameter.model.PostpaidPlan;
import com.diameter.service.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/plans")
public class PlanController {

    private static final Logger logger = LoggerFactory.getLogger(PlanController.class);
    private final PlanService service;

    public PlanController(PlanService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<PostpaidPlan> createPlan(@RequestBody @Valid PostpaidPlan plan) throws ValidationException {
        PostpaidPlan created = service.createPlan(plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping
    public ResponseEntity<List<PostpaidPlan>> getPlans(

            // ✅ Existing params
            @RequestParam(name = "id", required = false) BigInteger planId,
            @RequestParam(name = "name", required = false) String name,
            // ✅ New params
            @RequestParam(required = false) String planType,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String planStatus,
            @RequestParam(required = false) String quotaUnit,
            @RequestParam(required = false) String downloadSpeed,
            @RequestParam(required = false) String uploadSpeed,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endDate,

            @RequestParam(required = false) BigDecimal quota,
            @RequestParam(required = false) Integer validity,
            @RequestParam(required = false) Double chunk
    ) {
        return ResponseEntity.ok(service.searchPlans(planId, name, planType, price, status, planStatus, quotaUnit, downloadSpeed, uploadSpeed, startDate, endDate, quota, validity, chunk));
    }


    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<PostpaidPlan> updatePlan(
            @PathVariable BigInteger id,
            @RequestBody PostpaidPlan plan) throws ValidationException {

        PostpaidPlan updated = service.updatePlan(id, plan);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable BigInteger id) throws ValidationException {

        service.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}