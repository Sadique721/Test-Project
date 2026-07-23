package com.diameter.service;

import com.diameter.model.PostpaidPlan;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public interface PlanService {
    PostpaidPlan createPlan(PostpaidPlan plan) throws ValidationException;

    List<PostpaidPlan> getPlans(BigInteger planId, String name);

    PostpaidPlan updatePlan(BigInteger planId, PostpaidPlan plan) throws ValidationException;

    void deletePlan(BigInteger planId) throws ValidationException;

    List<PostpaidPlan> searchPlans(
            BigInteger planId,
            String name,
            String planType,
            BigDecimal price,
            String status,
            String planStatus,
            String quotaUnit,
            String downloadSpeed,
            String uploadSpeed,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal quota,
            Integer validity,
            Double chunk
    );


}