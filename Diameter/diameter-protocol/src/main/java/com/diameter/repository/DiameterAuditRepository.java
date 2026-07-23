package com.diameter.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.DiameterAudit;

@Repository
public class DiameterAuditRepository {

    private final JdbcTemplate jdbcTemplate;

    public DiameterAuditRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    public Long getLastInsertedId(){

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
        );

    }

    public Long saveAudit(DiameterAudit audit){
    	KeyHolder keyHolder = new GeneratedKeyHolder();

    	jdbcTemplate.update(connection -> {
    	    PreparedStatement ps = connection.prepareStatement(
    	            DiameterSQLConstants.CREATE_AUDIT,
    	            Statement.RETURN_GENERATED_KEYS
    	    );

    	    int i = 1;
    	    ps.setObject(i++, audit.getTransactionId());
    	    ps.setObject(i++, audit.getSessionId());
    	    ps.setObject(i++, audit.getProtocol());
    	    ps.setObject(i++, audit.getCommandCode());
    	    ps.setObject(i++, audit.getApplicationId());
    	    ps.setObject(i++, audit.getRequestType());
    	    ps.setObject(i++, audit.getServiceType());
    	    ps.setObject(i++, audit.getMsisdn());
    	    ps.setObject(i++, audit.getImsi());
    	    ps.setObject(i++, audit.getImei());
    	    ps.setObject(i++, audit.getApn());
    	    ps.setObject(i++, audit.getOriginHost());
    	    ps.setObject(i++, audit.getOriginRealm());
    	    ps.setObject(i++, audit.getDestinationHost());
    	    ps.setObject(i++, audit.getDestinationRealm());
    	    ps.setObject(i++, audit.getResultCode());
    	    ps.setObject(i++, audit.getResultDescription());
    	    ps.setObject(i++, audit.getStatus());
    	    ps.setObject(i++, audit.getErrorMessage());
    	    ps.setObject(i++, audit.getProcessingTimeMs());
    	    ps.setObject(i++, audit.getRequestPayload());
    	    ps.setObject(i++, audit.getResponsePayload());
    	    ps.setObject(i++, audit.getPeerName());
    	    ps.setObject(i++, audit.getPodName());

    	    ps.setObject(i++, audit.getCcRequestNumber());
    	    ps.setObject(i++, audit.getSubscriptionId());
    	    ps.setObject(i++, audit.getFramedIpAddress());
    	    ps.setObject(i++, audit.getFramedIpv6Prefix());
    	    ps.setObject(i++, audit.getCalledStationId());
    	    ps.setObject(i++, audit.getThreeGppRatType());
    	    ps.setObject(i++, audit.getQosInformation());
    	    ps.setObject(i++, audit.getBearerIdentifier());
    	    ps.setObject(i++, audit.getIpCanType());
    	    ps.setObject(i++, audit.getAnGwAddress());
    	    ps.setObject(i++, audit.getThreeGppSgsnAddress());
    	    ps.setObject(i++, audit.getUserName());
    	    ps.setObject(i++, audit.getOriginStateId());
    	    ps.setObject(i++, audit.getUserEquipmentInfo());
    	    ps.setObject(i++, audit.getCcSubSessionId());
    	    ps.setObject(i++, audit.getTftPacketFilterInformation());
    	    ps.setObject(i++, audit.getChargingRuleInstall());
    	    ps.setObject(i++, audit.getChargingRuleRemove());
    	    ps.setObject(i++, audit.getDefaultEpsBearerQos());
    	    ps.setObject(i++, audit.getSupportedFeatures());
    	    ps.setObject(i++, audit.getEventTrigger());
    	    ps.setObject(i++, audit.getUsageMonitoringInformation());
    	    ps.setObject(i++, audit.getChargingRuleReport());
    	    ps.setObject(i++, audit.getThreeGppUserLocationInfo());
    	    ps.setObject(i++, audit.getTerminationCause());

    	    return ps;
    	}, keyHolder);

    	return Optional.ofNullable(keyHolder.getKey())
    	        .map(Number::longValue)
    	        .orElseThrow(() -> new IllegalStateException("Failed to retrieve generated key."));
    }

    public void saveAuditDetailInformation(Long auditId,
                                           DiameterAudit audit){
        jdbcTemplate.update(
                DiameterSQLConstants.CREATE_AUDIT_DETAIL_INFORMATION,

                auditId,
                audit.getPdpType(),
                audit.getImsiUnauthenticatedFlag(),
                audit.getPdpContextType(),
                audit.getServingNodeType(),
                audit.getChargingId(),
                audit.getPdpAddress(),
                audit.getGgsnAddress(),
                audit.getDynamicAddressFlag(),
                audit.getImsiMccMnc(),
                audit.getNsapi(),
                audit.getChargingCharacteristics(),
                audit.getSgsnMccMnc(),
                audit.getMsTimeZone(),
				audit.getUserLocationInfoTime(),
				audit.getUserLocationInfo() != null ? audit.getUserLocationInfo().getGeoType() : null,
				audit.getUserLocationInfo() != null ? audit.getUserLocationInfo().getMcc() : null,
				audit.getUserLocationInfo() != null ? audit.getUserLocationInfo().getMnc() : null,
				audit.getUserLocationInfo() != null ? audit.getUserLocationInfo().getTac() : null,
				audit.getUserLocationInfo() != null ? audit.getUserLocationInfo().getEci() : null,
				audit.getUserLocationInfo() != null ? audit.getUserLocationInfo().getEnodebId() : null,
				audit.getUserLocationInfo() != null ? audit.getUserLocationInfo().getCellId() : null
        );
    }

    public DiameterAudit getAuditById(Long id){
        return jdbcTemplate.queryForObject(DiameterSQLConstants.GET_AUDIT_BY_ID, auditRowMapper(), id);
    }

    public Map<String, Object> getByIdPaginated(
            Long id,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterAudit> audits =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_AUDIT_BY_ID
                                + " ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?",
                        auditRowMapper(),
                        id,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_audit WHERE ID=?",
                        Integer.class,
                        id);

        return buildPaginatedResponse(
                audits,
                page,
                size,
                total);
    }

    public Map<String, Object> getByTxnPaginated(
            String txn,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterAudit> audits =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_AUDIT_BY_TXN
                                + " ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?",
                        auditRowMapper(),
                        txn,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_audit WHERE TRANSACTION_ID=?",
                        Integer.class,
                        txn);

        return buildPaginatedResponse(
                audits,
                page,
                size,
                total);
    }


    public Map<String, Object> getBySessionIdPaginated(
            String sessionId,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterAudit> audits =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_AUDIT_BY_SESSION_ID
                                + " ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?",
                        auditRowMapper(),
                        sessionId,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_audit WHERE SESSION_ID=?",
                        Integer.class,
                        sessionId);

        return buildPaginatedResponse(
                audits,
                page,
                size,
                total);
    }


    public Map<String, Object> getByMsisdnPaginated(
            String msisdn,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterAudit> audits =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_AUDIT_BY_MSISDN
                                + " ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?",
                        auditRowMapper(),
                        msisdn,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_audit WHERE MSISDN=?",
                        Integer.class,
                        msisdn);

        return buildPaginatedResponse(
                audits,
                page,
                size,
                total);
    }

    public Map<String, Object> getByImsiPaginated(
            String imsi,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterAudit> audits =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_AUDIT_BY_IMSI
                                + " ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?",
                        auditRowMapper(),
                        imsi,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_audit WHERE IMSI=?",
                        Integer.class,
                        imsi);

        return buildPaginatedResponse(
                audits,
                page,
                size,
                total);
    }


    public Map<String, Object> getByDatePaginated(
            LocalDate createdAt,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterAudit> audits =
                jdbcTemplate.query(
                        "SELECT * FROM tbl_diameter_audit " +
                                "WHERE DATE(CREATED_AT)=? " +
                                "ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?",
                        auditRowMapper(),
                        createdAt,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_audit " +
                                "WHERE DATE(CREATED_AT)=?",
                        Integer.class,
                        createdAt);

        return buildPaginatedResponse(
                audits,
                page,
                size,
                total);
    }


    public Map<String, Object> getByDateRangePaginated(
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterAudit> audits =
                jdbcTemplate.query(
                        "SELECT * FROM tbl_diameter_audit " +
                                "WHERE DATE(CREATED_AT) BETWEEN ? AND ? " +
                                "ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?",
                        auditRowMapper(),
                        fromDate,
                        toDate,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_audit " +
                                "WHERE DATE(CREATED_AT) BETWEEN ? AND ?",
                        Integer.class,
                        fromDate,
                        toDate);

        return buildPaginatedResponse(
                audits,
                page,
                size,
                total);
    }


    public Map<String, Object> getAllAuditPaginated(
            int page,
            int size) {

        int offset = page * size;

        List<DiameterAudit> audits =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_ALL_AUDIT
                                + " ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?",
                        auditRowMapper(),
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_audit",
                        Integer.class);

        return buildPaginatedResponse(
                audits,
                page,
                size,
                total);
    }

    public void updateAudit(Long id, DiameterAudit audit){
        jdbcTemplate.update(
                DiameterSQLConstants.UPDATE_AUDIT,
                audit.getTransactionId(),
                audit.getSessionId(),
                audit.getProtocol(),
                audit.getCommandCode(),
                audit.getApplicationId(),
                audit.getRequestType(),
                audit.getServiceType(),
                audit.getMsisdn(),
                audit.getImsi(),
                audit.getImei(),
                audit.getApn(),
                audit.getOriginHost(),
                audit.getOriginRealm(),
                audit.getDestinationHost(),
                audit.getDestinationRealm(),
                audit.getResultCode(),
                audit.getResultDescription(),
                audit.getStatus(),
                audit.getErrorMessage(),
                audit.getProcessingTimeMs(),
                audit.getRequestPayload(),
                audit.getResponsePayload(),
                audit.getPeerName(),
                audit.getPodName(),

                audit.getCcRequestNumber(),
                audit.getSubscriptionId(),
                audit.getFramedIpAddress(),
                audit.getFramedIpv6Prefix(),
                audit.getCalledStationId(),
                audit.getThreeGppRatType(),
                audit.getQosInformation(),
                audit.getBearerIdentifier(),
                audit.getIpCanType(),
                audit.getAnGwAddress(),
                audit.getThreeGppSgsnAddress(),
                audit.getUserName(),
                audit.getOriginStateId(),
                audit.getUserEquipmentInfo(),
                audit.getCcSubSessionId(),
                audit.getTftPacketFilterInformation(),
                audit.getChargingRuleInstall(),
                audit.getChargingRuleRemove(),
                audit.getDefaultEpsBearerQos(),
                audit.getSupportedFeatures(),
                audit.getEventTrigger(),
                audit.getUsageMonitoringInformation(),
                audit.getChargingRuleReport(),
                audit.getThreeGppUserLocationInfo(),
                audit.getTerminationCause(),
                id
        );
    }

    public void deleteAudit(Long id){jdbcTemplate.update(DiameterSQLConstants.DELETE_AUDIT, id);
    }

    private RowMapper<DiameterAudit> auditRowMapper() {

        return (rs, rowNum) -> {
            DiameterAudit a = new DiameterAudit();

            a.setId(rs.getLong("ID"));
            a.setTransactionId(rs.getString("TRANSACTION_ID"));
            a.setSessionId(rs.getString("SESSION_ID"));
            a.setProtocol(rs.getString("PROTOCOL"));
            a.setCommandCode(rs.getInt("COMMAND_CODE"));
            a.setApplicationId(rs.getLong("APPLICATION_ID"));
            a.setRequestType(rs.getString("REQUEST_TYPE"));
            a.setServiceType(rs.getString("SERVICE_TYPE"));
            a.setMsisdn(rs.getString("MSISDN"));
            a.setImsi(rs.getString("IMSI"));
            a.setImei(rs.getString("IMEI"));
            a.setApn(rs.getString("APN"));
            a.setOriginHost(rs.getString("ORIGIN_HOST"));
            a.setOriginRealm(rs.getString("ORIGIN_REALM"));
            a.setDestinationHost(rs.getString("DESTINATION_HOST"));
            a.setDestinationRealm(rs.getString("DESTINATION_REALM"));
            a.setResultCode(rs.getInt("RESULT_CODE"));
            a.setResultDescription(rs.getString("RESULT_DESCRIPTION"));
            a.setStatus(rs.getString("STATUS"));
            a.setErrorMessage(rs.getString("ERROR_MESSAGE"));
            a.setProcessingTimeMs(rs.getLong("PROCESSING_TIME_MS"));
            a.setRequestPayload(rs.getString("REQUEST_PAYLOAD"));
            a.setResponsePayload(rs.getString("RESPONSE_PAYLOAD"));
            a.setPeerName(rs.getString("PEER_NAME"));
            a.setPodName(rs.getString("POD_NAME"));

            // Newly Added Columns
            a.setCcRequestNumber(rs.getString("CC_REQUEST_NUMBER"));
            a.setSubscriptionId(rs.getString("SUBSCRIPTION_ID"));
            a.setFramedIpAddress(rs.getString("FRAMED_IP_ADDRESS"));
            a.setFramedIpv6Prefix(rs.getString("FRAMED_IPV6_PREFIX"));
            a.setCalledStationId(rs.getString("CALLED_STATION_ID"));
            a.setThreeGppRatType(rs.getString("THREE_GPP_RAT_TYPE"));
            a.setQosInformation(rs.getString("QOS_INFORMATION"));
            a.setBearerIdentifier(rs.getString("BEARER_IDENTIFIER"));
            a.setIpCanType(rs.getString("IP_CAN_TYPE"));
            a.setAnGwAddress(rs.getString("AN_GW_ADDRESS"));
            a.setThreeGppSgsnAddress(rs.getString("THREE_GPP_SGSN_ADDRESS"));
            a.setUserName(rs.getString("USER_NAME"));
            a.setOriginStateId(rs.getString("ORIGIN_STATE_ID"));
            a.setUserEquipmentInfo(rs.getString("USER_EQUIPMENT_INFO"));
            a.setCcSubSessionId(rs.getString("CC_SUB_SESSION_ID"));
            a.setTftPacketFilterInformation(rs.getString("TFT_PACKET_FILTER_INFORMATION"));
            a.setChargingRuleInstall(rs.getString("CHARGING_RULE_INSTALL"));
            a.setChargingRuleRemove(rs.getString("CHARGING_RULE_REMOVE"));
            a.setDefaultEpsBearerQos(rs.getString("DEFAULT_EPS_BEARER_QOS"));
            a.setSupportedFeatures(rs.getString("SUPPORTED_FEATURES"));
            a.setEventTrigger(rs.getString("EVENT_TRIGGER"));
            a.setUsageMonitoringInformation(rs.getString("USAGE_MONITORING_INFORMATION"));
            a.setChargingRuleReport(rs.getString("CHARGING_RULE_REPORT"));
            a.setThreeGppUserLocationInfo(rs.getString("THREE_GPP_USER_LOCATION_INFO"));
            a.setTerminationCause(rs.getString("TERMINATION_CAUSE"));

            a.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());

            return a;
        };
    }

    private Map<String, Object> buildPaginatedResponse(
            List<DiameterAudit> audits,
            int page,
            int size,
            Integer total) {

        Map<String, Object> response = new HashMap<>();

        response.put("content", audits);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", total);
        response.put("totalPages",
                (int) Math.ceil((double) total / size));

        return response;
    }
}