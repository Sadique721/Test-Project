package com.diameter.repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.DiameterLiveSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DiameterLiveSessionRepository {

    private final JdbcTemplate jdbcTemplate;

    public DiameterLiveSessionRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    // ============================
    // CREATE
    // ============================
    public void saveSession(DiameterLiveSession s){

        jdbcTemplate.update(

                DiameterSQLConstants
                        .CREATE_LIVE_SESSION,

                s.getSessionId(),
                s.getTransactionId(),

                s.getCcRequestType(),
                s.getCcRequestNumber(),

                s.getDiameterInterface(),

                s.getServiceType(),
                s.getServiceContextId(),

                s.getRatingGroup(),
                s.getServiceIdentifier(),

                s.getMsisdn(),
                s.getImsi(),
                s.getImei(),

                s.getCallingParty(),
                s.getCalledParty(),

                s.getIpAddress(),
                s.getApn(),

                s.getOriginHost(),
                s.getOriginRealm(),

                s.getDestinationHost(),
                s.getDestinationRealm(),

                s.getMediaType(),
                s.getSipMethod(),

                s.getAfChargingIdentifier(),
                s.getFlowStatus(),

                s.getCodec(),

                s.getPolicyName(),
                s.getChargingRuleBaseName(),

                s.getQosProfile(),
                s.getQci(),

                s.getUplinkBytes(),
                s.getDownlinkBytes(),
                s.getTotalBytes(),

                s.getVoiceSeconds(),
                s.getSmsCount(),

                s.getUsedUnits(),
                s.getGrantedUnits(),

                s.getSessionStatus(),
                s.getActiveFlag(),

                s.getResultCode(),
                s.getStatus(),

                s.getNodeName(),
                s.getPodName(),

                s.getNasIpAddress(),
                s.getIpv4Address(),

                s.getIpv6Address(),
                s.getCurrentPlan(),

                s.getPlanStartDate(),
                s.getPlanExpiryDate(),
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getGeoType() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getMcc() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getMnc() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getTac() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getEci() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getEnodebId() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getCellId() : null

        );

    }


    // ============================
    // UPDATE
    // ============================
    public void updateSession(String sessionId, DiameterLiveSession s){
        jdbcTemplate.update(
                DiameterSQLConstants
                        .UPDATE_LIVE_SESSION,
                s.getTransactionId(),
                s.getCcRequestType(),
                s.getCcRequestNumber(),
                s.getDiameterInterface(),
                s.getServiceType(),
                s.getServiceContextId(),
                s.getRatingGroup(),
                s.getServiceIdentifier(),
                s.getMsisdn(),
                s.getImsi(),
                s.getImei(),
                s.getCallingParty(),
                s.getCalledParty(),
                s.getIpAddress(),
                s.getApn(),
                s.getOriginHost(),
                s.getOriginRealm(),
                s.getDestinationHost(),
                s.getDestinationRealm(),
                s.getMediaType(),
                s.getSipMethod(),
                s.getAfChargingIdentifier(),
                s.getFlowStatus(),
                s.getCodec(),
                s.getPolicyName(),
                s.getChargingRuleBaseName(),
                s.getQosProfile(),
                s.getQci(),
                s.getUplinkBytes(),
                s.getDownlinkBytes(),
                s.getTotalBytes(),
                s.getVoiceSeconds(),
                s.getSmsCount(),
                s.getUsedUnits(),
                s.getGrantedUnits(),
                s.getSessionStatus(),
                s.getActiveFlag(),
                s.getResultCode(),
                s.getStatus(),
                s.getNodeName(),
                s.getPodName(),
                s.getNasIpAddress(),
                s.getIpv4Address(),
                s.getIpv6Address(),
                s.getCurrentPlan(),
                s.getPlanStartDate(),
                s.getPlanExpiryDate(),
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getGeoType() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getMcc() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getMnc() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getTac() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getEci() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getEnodebId() : null,
				s.getUserLocationInfo() != null ? s.getUserLocationInfo().getCellId() : null, sessionId
        );
    }


    // ============================
    // DELETE
    // ============================
    public void deleteSession(String sessionId){
        jdbcTemplate.update(DiameterSQLConstants.DELETE_LIVE_SESSION, sessionId);
    }


    // ============================
    // GET
    // ============================
    public DiameterLiveSession getSessionById(String sessionId){
    	return jdbcTemplate.query(
                DiameterSQLConstants.GET_BY_SESSION_ID,
                sessionRowMapper(),
                sessionId)
                .stream()
                .findFirst()
                .orElse(null);
    }


    public Map<String, Object> getByTransactionIdPaginated(String transactionId, int page, int size) {

        int offset = page * size;

        List<DiameterLiveSession> sessions = jdbcTemplate.query(DiameterSQLConstants.GET_BY_TRANSACTION_ID + " ORDER BY START_TIME DESC LIMIT ? OFFSET ?", sessionRowMapper(), transactionId, size, offset);

        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) " + "FROM tbl_diameter_live_session " + "WHERE TRANSACTION_ID = ?", Integer.class, transactionId);

        return buildPaginatedResponse(sessions, page, size, total);
    }


    public Map<String, Object> getByMsisdnPaginated(String msisdn, int page, int size) {

        int offset = page * size;

        List<DiameterLiveSession> sessions = jdbcTemplate.query(

                        DiameterSQLConstants.GET_BY_MSISDN + " ORDER BY START_TIME DESC LIMIT ? OFFSET ?",
                        sessionRowMapper(),
                        msisdn,
                        size,
                        offset);

        Integer total = jdbcTemplate.queryForObject(

                "SELECT COUNT(*) " +
                        "FROM tbl_diameter_live_session " +
                        "WHERE MSISDN = ?",

                Integer.class,
                msisdn);

        return buildPaginatedResponse(
                sessions,
                page,
                size,
                total);
    }


    public Map<String, Object> getByImsiPaginated(
            String imsi,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterLiveSession> sessions =
                jdbcTemplate.query(

                        DiameterSQLConstants.GET_BY_IMSI
                                + " ORDER BY START_TIME DESC LIMIT ? OFFSET ?",

                        sessionRowMapper(),
                        imsi,
                        size,
                        offset);

        Integer total = jdbcTemplate.queryForObject(

                "SELECT COUNT(*) " +
                        "FROM tbl_diameter_live_session " +
                        "WHERE IMSI = ?",

                Integer.class,
                imsi);

        return buildPaginatedResponse(
                sessions,
                page,
                size,
                total);
    }

    public Map<String, Object> getByStatusPaginated(
            String status,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterLiveSession> sessions =
                jdbcTemplate.query(

                        "SELECT * " +
                                "FROM tbl_diameter_live_session " +
                                "WHERE STATUS = ? " +
                                "ORDER BY START_TIME DESC " +
                                "LIMIT ? OFFSET ?",

                        sessionRowMapper(),
                        status,
                        size,
                        offset);

        Integer total = jdbcTemplate.queryForObject(

                "SELECT COUNT(*) " +
                        "FROM tbl_diameter_live_session " +
                        "WHERE STATUS = ?",

                Integer.class,
                status);

        return buildPaginatedResponse(
                sessions,
                page,
                size,
                total);
    }


    public Map<String, Object> getByDatePaginated(
            LocalDate date,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterLiveSession> sessions =
                jdbcTemplate.query(

                        "SELECT * " +
                                "FROM tbl_diameter_live_session " +
                                "WHERE DATE(START_TIME) = ? " +
                                "ORDER BY START_TIME DESC " +
                                "LIMIT ? OFFSET ?",

                        sessionRowMapper(),
                        date,
                        size,
                        offset);

        Integer total = jdbcTemplate.queryForObject(

                "SELECT COUNT(*) " +
                        "FROM tbl_diameter_live_session " +
                        "WHERE DATE(START_TIME) = ?",

                Integer.class,
                date);

        return buildPaginatedResponse(
                sessions,
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

        List<DiameterLiveSession> sessions =
                jdbcTemplate.query(

                        "SELECT * " +
                                "FROM tbl_diameter_live_session " +
                                "WHERE DATE(START_TIME) BETWEEN ? AND ? " +
                                "ORDER BY START_TIME DESC " +
                                "LIMIT ? OFFSET ?",

                        sessionRowMapper(),
                        fromDate,
                        toDate,
                        size,
                        offset);

        Integer total = jdbcTemplate.queryForObject(

                "SELECT COUNT(*) " +
                        "FROM tbl_diameter_live_session " +
                        "WHERE DATE(START_TIME) BETWEEN ? AND ?",

                Integer.class,
                fromDate,
                toDate);

        return buildPaginatedResponse(
                sessions,
                page,
                size,
                total);
    }

    private Map<String, Object> buildPaginatedResponse(
            List<DiameterLiveSession> sessions,
            int page,
            int size,
            Integer total) {

        Map<String, Object> response = new HashMap<>();

        response.put("content", sessions);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", total);
        response.put("totalPages",
                (int) Math.ceil((double) total / size));

        return response;
    }

    public Map<String, Object> getAllSessionsPaginated(
            int page,
            int size) {

        int offset = page * size;

        String sql = DiameterSQLConstants.GET_ALL_LIVE_SESSION
                + " ORDER BY START_TIME DESC LIMIT ? OFFSET ?";

        List<DiameterLiveSession> sessions =
                jdbcTemplate.query(
                        sql,
                        sessionRowMapper(),
                        size,
                        offset);

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tbl_diameter_live_session",
                Integer.class);

        return buildPaginatedResponse(
                sessions,
                page,
                size,
                total);
    }

    // ============================
    // ROW MAPPER
    // ============================
    private RowMapper<DiameterLiveSession> sessionRowMapper(){
        return (rs,rowNum)->{
            DiameterLiveSession s = new DiameterLiveSession();
            s.setSessionId(rs.getString("SESSION_ID"));
            s.setTransactionId(rs.getString("TRANSACTION_ID"));
            s.setCcRequestType(rs.getString("CC_REQUEST_TYPE"));
            s.setCcRequestNumber(rs.getLong("CC_REQUEST_NUMBER"));
            s.setDiameterInterface(rs.getString("DIAMETER_INTERFACE"));
            s.setServiceType(rs.getString("SERVICE_TYPE"));
            s.setServiceContextId(rs.getString("SERVICE_CONTEXT_ID"));
            s.setRatingGroup(rs.getLong("RATING_GROUP"));
            s.setServiceIdentifier(rs.getLong("SERVICE_IDENTIFIER"));
            s.setMsisdn(rs.getString("MSISDN"));
            s.setImsi(rs.getString("IMSI"));
            s.setImei(rs.getString("IMEI"));
            s.setCallingParty(rs.getString("CALLING_PARTY"));
            s.setCalledParty(rs.getString("CALLED_PARTY"));
            s.setIpAddress(rs.getString("IP_ADDRESS"));
            s.setApn(rs.getString("APN"));
            s.setOriginHost(rs.getString("ORIGIN_HOST"));
            s.setOriginRealm(rs.getString("ORIGIN_REALM"));
            s.setDestinationHost(rs.getString("DESTINATION_HOST"));
            s.setDestinationRealm(rs.getString("DESTINATION_REALM"));
            s.setMediaType(rs.getString("MEDIA_TYPE"));
            s.setSipMethod(rs.getString("SIP_METHOD"));
            s.setAfChargingIdentifier(rs.getString("AF_CHARGING_IDENTIFIER"));
            s.setFlowStatus(rs.getString("FLOW_STATUS"));
            s.setCodec(rs.getString("CODEC"));
            s.setPolicyName(rs.getString("POLICY_NAME"));
            s.setChargingRuleBaseName(rs.getString("CHARGING_RULE_BASE_NAME"));
            s.setQosProfile(rs.getString("QOS_PROFILE"));
            s.setQci(rs.getInt("QCI"));
            s.setUplinkBytes(rs.getLong("UPLINK_BYTES"));
            s.setDownlinkBytes(rs.getLong("DOWNLINK_BYTES"));
            s.setTotalBytes(rs.getLong("TOTAL_BYTES"));
            s.setVoiceSeconds(rs.getLong("VOICE_SECONDS"));
            s.setSmsCount(rs.getLong("SMS_COUNT"));
            s.setUsedUnits(rs.getLong("USED_UNITS"));
            s.setGrantedUnits(rs.getLong("GRANTED_UNITS"));
            s.setSessionStatus(rs.getString("SESSION_STATUS"));
            s.setActiveFlag(rs.getBoolean("ACTIVE_FLAG"));
            s.setResultCode(rs.getInt("RESULT_CODE"));
            s.setStatus(rs.getString("STATUS"));
            s.setNodeName(rs.getString("NODE_NAME"));
            s.setPodName(rs.getString("POD_NAME"));
            s.setStartTime(rs.getTimestamp("START_TIME").toLocalDateTime());
            s.setLastUpdateTime(rs.getTimestamp("LAST_UPDATE_TIME").toLocalDateTime());

            s.setNasIpAddress(rs.getString("NAS_IP_ADDRESS"));
            s.setIpv4Address(rs.getString("IPV4_ADDRESS"));
            s.setIpv6Address(rs.getString("IPV6_ADDRESS"));
            s.setCurrentPlan(rs.getString("CURRENT_PLAN"));
            if(rs.getTimestamp("PLAN_START_DATE") != null) {
                s.setPlanStartDate(rs.getTimestamp("PLAN_START_DATE").toLocalDateTime());
            }

            if(rs.getTimestamp("PLAN_EXPIRY_DATE") != null) {
                s.setPlanExpiryDate(rs.getTimestamp("PLAN_EXPIRY_DATE").toLocalDateTime());
            }
            return s;
        };
    }
}