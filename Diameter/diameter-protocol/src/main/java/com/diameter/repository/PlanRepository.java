package com.diameter.repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.PostpaidPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PlanRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PlanRepository.class);

    public PlanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================
    // CREATE
    // =========================
    public PostpaidPlan savePlan(PostpaidPlan plan) {

        logger.info("Creating plan with ID: {}", plan.getId());

        jdbcTemplate.update(
                DiameterSQLConstants.CREATE_PLAN,
                plan.getId(),
                plan.getName(),
                plan.getDisplayName(),
                plan.getCode(),
                plan.getDesc(),
                plan.getCategory(),
                plan.getQuota(),
                plan.getQuotaUnit(),
                plan.getChunk(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getStatus(),
                plan.getPlanStatus(),
                plan.getMvnoId(),
                plan.getOfferprice(),
                plan.getIsDelete()
//                plan.getCreateDate(),
//                plan.getCreatedByStaffId()
        );

        logger.info("Plan created successfully.");
        return plan;
    }

    public PostpaidPlan getPlanById(BigInteger planId) {
        try {
            return jdbcTemplate.queryForObject(
                    DiameterSQLConstants.GET_PLAN_BY_ID,
                    planRowMapper(),
                    planId
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public PostpaidPlan getPlanByName(String name) {
        try {
            return jdbcTemplate.queryForObject(
                    DiameterSQLConstants.GET_PLAN_BY_NAME,
                    planRowMapper(),
                    name
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public List<PostpaidPlan> getAllPlans() {
        try {
            return jdbcTemplate.query(
                    DiameterSQLConstants.GET_ALL_PLANS,
                    planRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public List<PostpaidPlan> searchPlans(BigInteger planId, String name, String planType, BigDecimal price, String status, String planStatus, String quotaUnit, String downloadSpeed, String uploadSpeed, LocalDateTime startDate, LocalDateTime endDate, BigDecimal quota, Integer validity, Double chunk) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tblmpostpaidplan WHERE is_delete = 0");
        List<Object> params = new ArrayList<>();

        // ---------------------------
        // ID
        // ---------------------------
        if (planId != null) {
            sql.append(" AND POSTPAIDPLANID = ?");
            params.add(planId);
        }

        // ---------------------------
        // NAME (LIKE)
        // ---------------------------
        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND NAME LIKE ?");
            params.add("%" + name.trim() + "%");
        }

        // ---------------------------
        // PLAN TYPE
        // ---------------------------
        if (planType != null && !planType.trim().isEmpty()) {
            sql.append(" AND plantype = ?");
            params.add(planType.trim());
        }

        // ---------------------------
        // PRICE
        // ---------------------------
        if (price != null) {
            sql.append(" AND offerprice = ?");
            params.add(price);
        }

        // ---------------------------
        // STATUS
        // ---------------------------
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND STATUS = ?");
            params.add(status.trim());
        }

        // ---------------------------
        // PLAN STATUS
        // ---------------------------
        if (planStatus != null && !planStatus.trim().isEmpty()) {
            sql.append(" AND PLANSTATUS = ?");
            params.add(planStatus.trim());
        }

        // ---------------------------
        // QUOTA UNIT
        // ---------------------------
        if (quotaUnit != null && !quotaUnit.trim().isEmpty()) {
            sql.append(" AND QUOTAUNIT = ?");
            params.add(quotaUnit.trim());
        }

        // ---------------------------
        // SPEEDS
        // ---------------------------
        if (downloadSpeed != null && !downloadSpeed.trim().isEmpty()) {
            sql.append(" AND DOWNLOADTS = ?");
            params.add(downloadSpeed.trim());
        }

        if (uploadSpeed != null && !uploadSpeed.trim().isEmpty()) {
            sql.append(" AND UPLOADTS = ?");
            params.add(uploadSpeed.trim());
        }

        // ---------------------------
        // DATE FILTER (IGNORE TIME)
        // ---------------------------
        // ---------------------------
        if (startDate != null) {
            if (startDate.toLocalTime().equals(LocalTime.MIDNIGHT)) {
                sql.append(" AND STARTDATE = ?");
                params.add(Timestamp.valueOf(startDate));
            } else {
                // Whole day match
                LocalDateTime dayStart = startDate.toLocalDate().atStartOfDay();
                LocalDateTime dayEnd = startDate.toLocalDate().atTime(23, 59, 59);
                sql.append(" AND STARTDATE BETWEEN ? AND ?");
                params.add(Timestamp.valueOf(dayStart));
                params.add(Timestamp.valueOf(dayEnd));
            }
        }
        if (endDate != null) {
            if (endDate.toLocalTime().equals(LocalTime.MIDNIGHT)) {
                sql.append(" AND ENDDATE = ?");
                params.add(Timestamp.valueOf(endDate));
            } else {
                LocalDateTime dayStart = endDate.toLocalDate().atStartOfDay();
                LocalDateTime dayEnd = endDate.toLocalDate().atTime(23, 59, 59);
                sql.append(" AND ENDDATE BETWEEN ? AND ?");
                params.add(Timestamp.valueOf(dayStart));
                params.add(Timestamp.valueOf(dayEnd));
            }
        }

        // ---------------------------
        // QUOTA
        // ---------------------------
        if (quota != null) {
            sql.append(" AND QUOTA = ?");
            params.add(quota);
        }

        // ---------------------------
        // VALIDITY
        // ---------------------------
        if (validity != null) {
            sql.append(" AND validity = ?");
            params.add(validity);
        }

        // ---------------------------
        // CHUNK
        // ---------------------------
        if (chunk != null) {
            sql.append(" AND chunk = ?");
            params.add(chunk);
        }

        return jdbcTemplate.query(sql.toString(), planRowMapper(), params.toArray());
    }

    // =========================
    // UPDATE
    // =========================
    public void updatePlan(BigInteger planId, PostpaidPlan plan) {

        jdbcTemplate.update(
                DiameterSQLConstants.UPDATE_PLAN,
                plan.getName(),
                plan.getDisplayName(),
                plan.getCode(),
                plan.getDesc(),
                plan.getCategory(),
                plan.getQuota(),
                plan.getQuotaUnit(),
                plan.getChunk(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getStatus(),
                plan.getPlanStatus(),
                plan.getMvnoId(),
                plan.getOfferprice(),
//                plan.getLastModifiedByStaffId(),
//                plan.getLastModifiedDate(),
                planId
        );
    }

    // =========================
    // SOFT DELETE
    // =========================
    public void softDeletePlan(BigInteger planId) {

        jdbcTemplate.update(
                DiameterSQLConstants.SOFT_DELETE_PLAN,
                true,
                planId
        );
    }


    public BigInteger getNextPlanId() {
        return jdbcTemplate.queryForObject(
                DiameterSQLConstants.GET_NEXT_PLAN_ID,
                BigInteger.class
        );
    }

    private RowMapper<PostpaidPlan> planRowMapper() {
        return (rs, rowNum) -> {
            PostpaidPlan p = new PostpaidPlan();

            p.setId(rs.getObject("POSTPAIDPLANID", BigInteger.class).intValueExact());
            p.setName(rs.getString("NAME"));
            p.setDisplayName(rs.getString("DISPLAYNAME"));
            p.setCode(rs.getString("PLANCODE"));
            p.setDesc(rs.getString("DESCRIPTION"));
            p.setCategory(rs.getString("PLANCATEGORY"));

            BigDecimal quota = rs.getBigDecimal("QUOTA");
            p.setQuota(quota != null ? quota.longValueExact() : 0);
            p.setQuotaUnit(rs.getString("QUOTAUNIT"));
            p.setChunk(getDouble(rs, "CHUNK"));

            p.setStartDate(
                    rs.getTimestamp("STARTDATE")
                            .toLocalDateTime()
                            .toLocalDate()
            );
            p.setEndDate(
                    rs.getTimestamp("ENDDATE")
                            .toLocalDateTime()
                            .toLocalDate()
            );

            p.setStatus(rs.getString("STATUS"));
            p.setPlanStatus(rs.getString("PLANSTATUS"));

            p.setMvnoId(rs.getObject("MVNOID", BigInteger.class).intValueExact());
            p.setOfferprice(getDouble(rs, "offerprice"));
            p.setValidity(getDouble(rs, "validity"));
            p.setIsDelete(rs.getBoolean("is_delete"));
            p.setAllowOverUsage(rs.getBoolean("allowoverusage"));

//            p.setCreateDate(rs.getTimestamp("CREATEDATE"));
//            p.setCreatedByStaffId(rs.getBigDecimal("CREATEDBYSTAFFID"));
//            p.setLastModifiedByStaffId(rs.getBigDecimal("LASTMODIFIEDBYSTAFFID"));
//            p.setLastModifiedDate(rs.getTimestamp("LASTMODIFIEDDATE"));

            return p;
        };
    }

    private double getDouble(ResultSet rs, String column) throws SQLException {
        BigDecimal val = rs.getBigDecimal(column);
        return val == null ? 0 : val.doubleValue();
    }

}