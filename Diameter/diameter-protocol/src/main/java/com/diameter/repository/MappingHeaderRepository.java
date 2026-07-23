package com.diameter.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.diameter.model.AvpCondition;
import com.diameter.model.MappingDetail;
import com.diameter.model.MappingDetail.ValueType;
import com.diameter.model.MappingHeader;

@Repository
public class MappingHeaderRepository {

    private static final Logger logger = LoggerFactory.getLogger(MappingHeaderRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public MappingHeader save(MappingHeader header) {
        logger.info("Saving MappingHeader: {}", header.getRequestType());
        String sql = """
        INSERT INTO tblm_packet_mapping_header 
        (id, request_type, response_type, application, vendor_id, enabled, description,
         cc_request_type, peer,
         created_date, created_by, modified_date, modified_by, include_standard_avp)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                header.getId(),
                header.getRequestType(),
                header.getResponseType(),
                header.getApplication(),
                header.getVendorId(),
                header.isEnabled(),
                header.getDescription(),
                // 🔹 NEW OPTIONAL FIELDS
                header.getCcRequestType(),     // can be NULL
                convertListToCsv(header.getPeer()),
                header.getCreatedDate(),
                header.getCreatedBy(),
                header.getModifiedDate(),
                header.getModifiedBy(),
                header.isIncludeStandardAvp()
        );
        return header;
    }

    public MappingHeader update(MappingHeader header) {
        logger.info("Updating MappingHeader: {}", header.getId());
        String sql = """
        UPDATE tblm_packet_mapping_header 
        SET request_type = ?,
            response_type = ?,
            application = ?,
            vendor_id = ?,
            enabled = ?,
            description = ?,
            cc_request_type = ?,
            peer = ?,
            modified_date = ?,
            modified_by = ?,
            include_standard_avp = ?
        WHERE id = ?
        """;
        jdbcTemplate.update(sql,
                header.getRequestType(),
                header.getResponseType(),
                header.getApplication(),
                header.getVendorId(),
                header.isEnabled(),
                header.getDescription(),
                header.getCcRequestType(), // 🔹 NEW OPTIONAL FIELDS
                convertListToCsv(header.getPeer()),  // 🔹 NEW OPTIONAL FIELDS
                header.getModifiedDate(),
                header.getModifiedBy(),
                header.isIncludeStandardAvp(),
                header.getId()
        );
        return header;
    }

    public void deleteById(String id) {
        logger.info("Deleting MappingHeader by ID: {}", id);
        jdbcTemplate.update("DELETE FROM tblm_packet_mapping_detail WHERE mapping_header_id = ?", id);
        jdbcTemplate.update("DELETE FROM tblm_packet_mapping_header WHERE id = ?", id);
    }

    public List<MappingHeader> findAll() {
        String sql = "SELECT * FROM tblm_packet_mapping_header ORDER BY created_date DESC";
        return jdbcTemplate.query(sql, new MappingHeaderRowMapper());
    }

    public MappingHeader findById(String id) {
        String sql = "SELECT * FROM tblm_packet_mapping_header WHERE id = ?";
        List<MappingHeader> list = jdbcTemplate.query(sql, new MappingHeaderRowMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public MappingHeader findByCommandCode(String commandCode) {
        String sql = "SELECT * FROM tblm_packet_mapping_header WHERE request_type = ?";
        List<MappingHeader> list = jdbcTemplate.query(sql, new MappingHeaderRowMapper(), commandCode);
        return list.isEmpty() ? null : list.get(0);
    }

    public void saveMappingDetail(MappingDetail detail) {
        logger.info("Saving MappingDetail for Header ID: {}", detail.getMappingHeaderId());
        String sql = "INSERT INTO tblm_packet_mapping_detail "
                + "(id, mapping_header_id, request_avp, response_avp, value_expression, value_type, sequence,vendor_id, mandatory, created_date, created_by, modified_date, modified_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

        jdbcTemplate.update(sql,
                detail.getId(),
                detail.getMappingHeaderId(),
                detail.getRequestAvp(),
                detail.getResponseAvp(),
                detail.getValueExpression(),
                detail.getValueType().getDbValue(),
                detail.getSequence(),
                detail.getVendorId(),
                detail.isMandatory(),
                detail.getCreatedDate(),
                detail.getCreatedBy(),
                detail.getModifiedDate(),
                detail.getModifiedBy());
    }

    public void deleteMappingDetailsByHeaderId(String headerId) {
        logger.info("Deleting MappingDetails for Header ID: {}", headerId);
        jdbcTemplate.update("DELETE FROM tblm_packet_mapping_detail WHERE mapping_header_id = ?", headerId);
    }

    public List<MappingDetail> findDetailsByHeaderId(String headerId) {
        String sql = "SELECT * FROM tblm_packet_mapping_detail WHERE mapping_header_id = ? ORDER BY sequence ASC ";
        return jdbcTemplate.query(sql, new MappingDetailRowMapper(), headerId);
    }

    public List<MappingHeader> findByRequestAndResponseType(
            String requestType,
            String responseType,
            String application,
            Integer vendorId,
            String ccRequestType
    ) {

        StringBuilder sql =
                new StringBuilder("SELECT * FROM tblm_packet_mapping_header WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (requestType != null && !requestType.isEmpty()) {
            sql.append(" AND request_type = ?");
            params.add(requestType);
        }

        if (responseType != null && !responseType.isEmpty()) {
            sql.append(" AND response_type = ?");
            params.add(responseType);
        }

        if (application != null && !application.isEmpty()) {
            sql.append(" AND application = ?");
            params.add(application);
        }

        if (vendorId != null) {
            sql.append(" AND vendor_id = ?");
            params.add(vendorId);
        }

        // ✅ NEW CONDITION
        if (ccRequestType != null && !ccRequestType.isEmpty()) {
            sql.append(" AND cc_request_type = ?");
            params.add(ccRequestType);
        }
        return jdbcTemplate.query(sql.toString(), params.toArray(), new MappingHeaderRowMapper());
    }


    private static class MappingHeaderRowMapper implements RowMapper<MappingHeader> {
        @Override
        public MappingHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
            MappingHeader h = new MappingHeader();
            h.setId(rs.getString("id"));
            h.setRequestType(rs.getString("request_type"));
            h.setResponseType(rs.getString("response_type"));
            h.setApplication(rs.getString("application"));
            h.setVendorId(rs.getInt("vendor_id"));
            h.setEnabled(rs.getBoolean("enabled"));
            h.setDescription(rs.getString("description"));
            if (rs.getTimestamp("created_date") != null)
                h.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
            if (rs.getTimestamp("modified_date") != null)
                h.setModifiedDate(rs.getTimestamp("modified_date").toLocalDateTime());
            h.setCcRequestType(rs.getString("cc_request_type"));
            h.setPeer(convertCsvToList(rs.getString("peer")));
            h.setCreatedBy(rs.getString("created_by"));
            h.setModifiedBy(rs.getString("modified_by"));
            return h;
        }
    }

    private static class MappingDetailRowMapper implements RowMapper<MappingDetail> {
        @Override
        public MappingDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
            MappingDetail d = new MappingDetail();
            d.setId(rs.getString("id"));
            d.setMappingHeaderId(rs.getString("mapping_header_id"));
            d.setRequestAvp(rs.getString("request_avp"));
            d.setResponseAvp(rs.getString("response_avp"));
            d.setValueExpression(rs.getString("value_expression"));
            d.setValueType(ValueType.fromDbValue(rs.getString("value_type")));
            d.setSequence(rs.getInt("sequence"));
            d.setVendorId(rs.getInt("vendor_id"));
            d.setMandatory(rs.getBoolean("mandatory"));
            if (rs.getTimestamp("created_date") != null)
                d.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
            if (rs.getTimestamp("modified_date") != null)
                d.setModifiedDate(rs.getTimestamp("modified_date").toLocalDateTime());
            d.setCreatedBy(rs.getString("created_by"));
            d.setModifiedBy(rs.getString("modified_by"));
            return d;
        }
    }

    private String convertListToCsv(List<?> list) {
        return list == null ? null : list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private static List<String> convertCsvToList(String csv) {
        return csv == null || csv.isEmpty()
                ? List.of()
                : Arrays.asList(csv.split(","));
    }
    
    public void saveMappingCondition(AvpCondition condition) {
        logger.info("Saving MappingCondition for Header ID: {}", condition.getMappingHeaderId());

        String sql = "INSERT INTO tblm_packet_mapping_condition "
                + "(id, mapping_header_id, avp_code, vendor_id, match_type, expected_value, sequence, "
                + "created_date, created_by, modified_date, modified_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                condition.getId(),
                condition.getMappingHeaderId(),
                condition.getAvpCode(),
                condition.getVendorId(),
                condition.getMatchType(),
                condition.getExpectedValue(),
                condition.getSequence(),
                condition.getCreatedDate(),
                condition.getCreatedBy(),
                condition.getModifiedDate(),
                condition.getModifiedBy());
    }
    
    public void deleteMappingConditionsByHeaderId(String headerId) {
        logger.info("Deleting MappingConditions for Header ID: {}", headerId);

        jdbcTemplate.update(
                "DELETE FROM tblm_packet_mapping_condition WHERE mapping_header_id = ?",
                headerId
        );
    }
    
    public List<AvpCondition> findConditionsByHeaderId(String headerId) {
        String sql = "SELECT * FROM tblm_packet_mapping_condition "
                + "WHERE mapping_header_id = ? ORDER BY sequence ASC";

        return jdbcTemplate.query(sql, new MappingConditionRowMapper(), headerId);
    }
    
    public class MappingConditionRowMapper implements RowMapper<AvpCondition> {

        @Override
        public AvpCondition mapRow(ResultSet rs, int rowNum) throws SQLException {

        	AvpCondition condition = new AvpCondition();

            condition.setId(rs.getString("id"));
            condition.setMappingHeaderId(rs.getString("mapping_header_id"));
            condition.setAvpCode(rs.getInt("avp_code"));
            condition.setVendorId(rs.getInt("vendor_id"));
            condition.setMatchType(rs.getString("match_type"));
            condition.setExpectedValue(rs.getString("expected_value"));
            condition.setSequence(rs.getInt("sequence"));

            condition.setCreatedDate(rs.getTimestamp("created_date") != null
                    ? rs.getTimestamp("created_date").toLocalDateTime()
                    : null);

            condition.setCreatedBy(rs.getString("created_by"));

            condition.setModifiedDate(rs.getTimestamp("modified_date") != null
                    ? rs.getTimestamp("modified_date").toLocalDateTime()
                    : null);

            condition.setModifiedBy(rs.getString("modified_by"));

            return condition;
        }
    }
}