package com.diameter.serviceImpl;

import com.diameter.model.PostpaidPlan;
import com.diameter.repository.PlanRepository;
import com.diameter.service.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

    private static final Logger logger = LoggerFactory.getLogger(PlanServiceImpl.class);
    private final PlanRepository repository;

    public PlanServiceImpl(PlanRepository repository) {
        this.repository = repository;
    }

    // CREATE
    @Override
    public PostpaidPlan createPlan(PostpaidPlan plan) throws ValidationException {

        try {

            // If ID not provided → generate
            if (plan.getId() == null) {
                BigInteger nextId = repository.getNextPlanId();
                plan.setId(nextId.intValueExact());
            }

//            plan.setCreateDate(new Timestamp(System.currentTimeMillis()));
//            plan.setIsDeleted(false);

            repository.savePlan(plan);

            return repository.getPlanById(BigInteger.valueOf(plan.getId()));

        } catch (DataIntegrityViolationException ex) {

            throw new ValidationException(
                    "Plan already exists with ID : " + plan.getId()
            );
        }
    }

    @Override
    public List<PostpaidPlan> searchPlans(BigInteger planId, String name, String planType, BigDecimal price, String status, String planStatus, String quotaUnit, String downloadSpeed, String uploadSpeed, LocalDateTime startDate, LocalDateTime endDate, BigDecimal quota, Integer validity, Double chunk) {
        List<PostpaidPlan> plans = repository.searchPlans(planId, name, planType, price, status, planStatus, quotaUnit, downloadSpeed, uploadSpeed, startDate, endDate, quota, validity, chunk);
        if (plans.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No plans found for given criteria ");
        }
        return plans;
    }

    // UPDATE (Service validates first)
    @Override
    public PostpaidPlan updatePlan(BigInteger planId, PostpaidPlan plan) throws ValidationException {

        PostpaidPlan existing = repository.getPlanById(planId);

        if (existing == null) {
            throw new ValidationException("Plan not found with id " + planId);
        }

//        plan.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));

        repository.updatePlan(planId, plan);

        return repository.getPlanById(planId);
    }


    // DELETE (Service validates first)
    @Override
    public void deletePlan(BigInteger planId) throws ValidationException {

        // 1. Validate plan exists
        PostpaidPlan existing = repository.getPlanById(planId);

        if (existing == null) {
            throw new ValidationException("Plan not found with id " + planId);
        }

        // 2. Soft delete
        repository.softDeletePlan(planId);
    }

    @Override
    public List<PostpaidPlan> getPlans(BigInteger planId, String name) {
        try {
            if (planId != null) {
                PostpaidPlan plan = repository.getPlanById(planId);
                return plan != null ? List.of(plan) : Collections.emptyList();
            } else if (name != null) {
                PostpaidPlan plan = repository.getPlanByName(name);
                return plan != null ? List.of(plan) : Collections.emptyList();
            } else {
                return repository.getAllPlans();
            }
        } catch (Exception e) {
            logger.error("Failed to fetch plans", e);
            throw new RuntimeException("Unable to fetch plans", e);
        }
    }

}