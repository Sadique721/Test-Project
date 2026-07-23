package com.diameter.repository;

import java.sql.ResultSet;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.Vendor;

@Repository
public class VendorRepository {

    private static final Logger logger = LoggerFactory.getLogger(VendorRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public VendorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ================= CREATE ================= */

    public Vendor saveVendor(Vendor vendor) throws ValidationException {

        Integer count = jdbcTemplate.queryForObject(
                DiameterSQLConstants.CHECK_VENDOR_ID_EXISTS,
                Integer.class,
                vendor.getVendor_id()
        );

        // 🔹 Name mandatory check (extra safety)
        if (vendor.getName() == null || vendor.getName().trim().isEmpty()) {
            throw new ValidationException("Vendor name is mandatory");
        }

        if (count != null && count > 0) {
            throw new ValidationException(
                    "Vendor already exists with vendor_id: " + vendor.getVendor_id()
            );
        }

        // 🔹 Check duplicate name
        Integer nameCount = jdbcTemplate.queryForObject(
                DiameterSQLConstants.CHECK_VENDOR_NAME_EXISTS,
                Integer.class,
                vendor.getName()
        );

        if (nameCount != null && nameCount > 0) {
            throw new ValidationException(
                    "Vendor already exists with name: " + vendor.getName()
            );
        }

        jdbcTemplate.update(
                DiameterSQLConstants.INSERT_VENDOR,
                vendor.getId(),
                vendor.getVendor_id(),
                vendor.getName(),
                vendor.getDescription(),
                vendor.getStatus()
        );

        logger.info("Vendor created successfully: {}", vendor.getName());
        return vendor;
    }

    /* ================= UPDATE ================= */

    public Vendor updateVendor(Vendor vendor) throws ValidationException {

        // 🔹 Check duplicate vendor_id (excluding self)
        Integer count = jdbcTemplate.queryForObject(
                DiameterSQLConstants.CHECK_VENDOR_ID_EXISTS_EXCEPT_SELF,
                Integer.class,
                vendor.getVendor_id(),
                vendor.getId()
        );

        if (count != null && count > 0) {
            throw new ValidationException(
                    "Vendor already exists with vendor_id: " + vendor.getVendor_id()
            );
        }

        int updated = jdbcTemplate.update(
                DiameterSQLConstants.UPDATE_VENDOR,
                vendor.getVendor_id(),
                vendor.getName(),
                vendor.getDescription(),
                vendor.getStatus(),
                vendor.getId()
        );

        if (updated == 0) {
            throw new ValidationException(
                    "Vendor not found with id: " + vendor.getId()
            );
        }

        logger.info("Vendor updated successfully: {}", vendor.getId());
        return vendor;
    }

    /* ================= DELETE ================= */

    public void deleteVendor(String id) {
        jdbcTemplate.update(DiameterSQLConstants.DELETE_VENDOR, id);
        logger.info("Vendor deleted successfully: {}", id);
    }

    /* ================= READ ================= */

    public Vendor getVendorById(String id) {
        try {
            return jdbcTemplate.queryForObject(
                    DiameterSQLConstants.GET_VENDOR_BY_ID,
                    vendorRowMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Vendor not found with id: {}", id);
            return null;
        }
    }

    public Vendor getVendorByVendorId(Integer vendorId) {
        try {
            return jdbcTemplate.queryForObject(
                    DiameterSQLConstants.GET_VENDOR_BY_VENDOR_ID,
                    vendorRowMapper(),
                    vendorId
            );
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Vendor not found with vendor_id: {}", vendorId);
            return null;
        }
    }

    public Vendor getVendorByName(String name) {
        try {
            return jdbcTemplate.queryForObject(
                    DiameterSQLConstants.GET_VENDOR_BY_NAME,
                    vendorRowMapper(),
                    name
            );
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Vendor not found with name: {}", name);
            return null;
        }
    }

    public List<Vendor> getAllVendors(String status) {

        if (status != null) {
            return jdbcTemplate.query(
                    DiameterSQLConstants.GET_ALL_VENDORS_BY_STATUS,
                    vendorRowMapper(),
                    status
            );
        }

        return jdbcTemplate.query(
                DiameterSQLConstants.GET_ALL_VENDORS,
                vendorRowMapper()
        );
    }

    /* ================= ROW MAPPER ================= */

    private RowMapper<Vendor> vendorRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            Vendor v = new Vendor();
            v.setId(rs.getString("id"));
            v.setVendor_id(rs.getInt("vendor_id"));
            v.setName(rs.getString("name"));
            v.setDescription(rs.getString("description"));
            v.setStatus(rs.getString("status"));
            return v;
        };
    }
}