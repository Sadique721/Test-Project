package com.diameter.repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.DiameterSessionCdr;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DiameterSessionCdrRepository {

    private final JdbcTemplate jdbcTemplate;

    public DiameterSessionCdrRepository(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    public Long getLastInsertedId(){

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
        );
    }

    public void saveCdr(DiameterSessionCdr cdr){
        jdbcTemplate.update(
                DiameterSQLConstants.CREATE_CDR,
                cdr.getSessionId(),
                cdr.getTransactionId(),
                cdr.getDiameterInterface(),
                cdr.getCcRequestNumber(),
                cdr.getServiceType(),
                cdr.getServiceContextId(),
                cdr.getRatingGroup(),
                cdr.getServiceIdentifier(),
                cdr.getMsisdn(),
                cdr.getImsi(),
                cdr.getImei(),
                cdr.getCallingParty(),
                cdr.getCalledParty(),
                cdr.getIpAddress(),
                cdr.getApn(),
                cdr.getOriginHost(),
                cdr.getOriginRealm(),
                cdr.getDestinationHost(),
                cdr.getDestinationRealm(),
                cdr.getMediaType(),
                cdr.getSipMethod(),
                cdr.getAfChargingIdentifier(),
                cdr.getFlowStatus(),
                cdr.getCodec(),
                cdr.getPolicyName(),
                cdr.getChargingRuleBaseName(),
                cdr.getQosProfile(),
                cdr.getQci(),
                cdr.getUplinkBytes(),
                cdr.getDownlinkBytes(),
                cdr.getTotalBytes(),
                cdr.getVoiceSeconds(),
                cdr.getSmsCount(),
                cdr.getUsedUnits(),
                cdr.getGrantedUnits(),
                cdr.getStartTime(),
                cdr.getLastUpdateTime(),
                cdr.getEndTime(),
                cdr.getSessionDuration(),
                cdr.getTerminationCause(),
                cdr.getTerminationReason(),
                cdr.getDisconnectSource(),
                cdr.getResultCode(),
                cdr.getResultDescription(),
                cdr.getStatus(),
                cdr.getErrorMessage(),
                cdr.getRequestPayload(),
                cdr.getResponsePayload(),
                cdr.getNodeName(),
                cdr.getPodName(),
				cdr.getUserLocationInfo() != null ? cdr.getUserLocationInfo().getGeoType() : null,
				cdr.getUserLocationInfo() != null ? cdr.getUserLocationInfo().getMcc() : null,
				cdr.getUserLocationInfo() != null ? cdr.getUserLocationInfo().getMnc() : null,
				cdr.getUserLocationInfo() != null ? cdr.getUserLocationInfo().getTac() : null,
				cdr.getUserLocationInfo() != null ? cdr.getUserLocationInfo().getEci() : null,
				cdr.getUserLocationInfo() != null ? cdr.getUserLocationInfo().getEnodebId() : null,
				cdr.getUserLocationInfo() != null ? cdr.getUserLocationInfo().getCellId() : null
        );
    }

    public DiameterSessionCdr getCdrById(Long id){
        return jdbcTemplate.queryForObject(DiameterSQLConstants.GET_CDR_BY_ID, cdrRowMapper(), id);
    }

    public Map<String,Object> getByIdPaginated(Long id,int page,int size){

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_CDR_BY_ID +
                                " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",
                        cdrRowMapper(),
                        id,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_session_cdr WHERE ID=?",
                        Integer.class,
                        id);

        return buildPaginatedResponse(cdrs,page,size,total);
    }


    public Map<String,Object> getBySessionIdPaginated(String sessionId,int page,int size){

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_CDR_BY_SESSION_ID +
                                " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",
                        cdrRowMapper(),
                        sessionId,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_session_cdr WHERE SESSION_ID=?",
                        Integer.class,
                        sessionId);

        return buildPaginatedResponse(cdrs,page,size,total);
    }


    public Map<String,Object> getByTransactionIdPaginated(String transactionId,int page,int size){

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_CDR_BY_TRANSACTION_ID +
                                " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",
                        cdrRowMapper(),
                        transactionId,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_session_cdr WHERE TRANSACTION_ID=?",
                        Integer.class,
                        transactionId);

        return buildPaginatedResponse(cdrs,page,size,total);
    }

    public Map<String, Object> getByMsisdnPaginated(
            String msisdn,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(

                        DiameterSQLConstants.GET_CDR_BY_MSISDN
                                + " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",

                        cdrRowMapper(),
                        msisdn,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(

                        "SELECT COUNT(*) " +
                                "FROM tbl_diameter_session_cdr " +
                                "WHERE MSISDN = ?",

                        Integer.class,
                        msisdn);

        return buildPaginatedResponse(
                cdrs,
                page,
                size,
                total);
    }


    public Map<String, Object> getByImsiPaginated(
            String imsi,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(

                        DiameterSQLConstants.GET_CDR_BY_IMSI
                                + " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",

                        cdrRowMapper(),
                        imsi,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(

                        "SELECT COUNT(*) " +
                                "FROM tbl_diameter_session_cdr " +
                                "WHERE IMSI = ?",

                        Integer.class,
                        imsi);

        return buildPaginatedResponse(
                cdrs,
                page,
                size,
                total);
    }


    public Map<String, Object> getByStatusPaginated(
            String status,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(

                        DiameterSQLConstants.GET_CDR_BY_STATUS
                                + " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",

                        cdrRowMapper(),
                        status,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(

                        "SELECT COUNT(*) " +
                                "FROM tbl_diameter_session_cdr " +
                                "WHERE STATUS = ?",

                        Integer.class,
                        status);

        return buildPaginatedResponse(
                cdrs,
                page,
                size,
                total);
    }


    public Map<String, Object> getByServiceTypePaginated(
            String serviceType,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(

                        DiameterSQLConstants.GET_CDR_BY_SERVICE_TYPE
                                + " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",

                        cdrRowMapper(),
                        serviceType,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(

                        "SELECT COUNT(*) " +
                                "FROM tbl_diameter_session_cdr " +
                                "WHERE SERVICE_TYPE = ?",

                        Integer.class,
                        serviceType);

        return buildPaginatedResponse(
                cdrs,
                page,
                size,
                total);
    }


    public Map<String,Object> getByDatePaginated(
            LocalDate date,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_CDR_BY_DATE +
                                " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",
                        cdrRowMapper(),
                        date,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_session_cdr WHERE DATE(CREATED_DATE)=?",
                        Integer.class,
                        date);

        return buildPaginatedResponse(cdrs,page,size,total);
    }

    public Map<String,Object> getByDateRangePaginated(
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_CDR_BETWEEN_DATES +
                                " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",
                        cdrRowMapper(),
                        fromDate,
                        toDate,
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_session_cdr " +
                                "WHERE DATE(CREATED_DATE) BETWEEN ? AND ?",
                        Integer.class,
                        fromDate,
                        toDate);

        return buildPaginatedResponse(cdrs,page,size,total);
    }

    public Map<String,Object> getAllCdrPaginated(int page,int size){

        int offset = page * size;

        List<DiameterSessionCdr> cdrs =
                jdbcTemplate.query(
                        DiameterSQLConstants.GET_ALL_CDR +
                                " ORDER BY CREATED_DATE DESC LIMIT ? OFFSET ?",
                        cdrRowMapper(),
                        size,
                        offset);

        Integer total =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_diameter_session_cdr",
                        Integer.class);

        return buildPaginatedResponse(cdrs,page,size,total);
    }




    public void updateCdr(Long id, DiameterSessionCdr cdr){
        jdbcTemplate.update(
                DiameterSQLConstants.UPDATE_CDR,

                cdr.getSessionId(),
                cdr.getTransactionId(),
                cdr.getDiameterInterface(),
                cdr.getCcRequestNumber(),
                cdr.getServiceType(),
                cdr.getServiceContextId(),
                cdr.getRatingGroup(),
                cdr.getServiceIdentifier(),
                cdr.getMsisdn(),
                cdr.getImsi(),
                cdr.getImei(),
                cdr.getCallingParty(),
                cdr.getCalledParty(),
                cdr.getIpAddress(),
                cdr.getApn(),
                cdr.getOriginHost(),
                cdr.getOriginRealm(),
                cdr.getDestinationHost(),
                cdr.getDestinationRealm(),
                cdr.getMediaType(),
                cdr.getSipMethod(),
                cdr.getAfChargingIdentifier(),
                cdr.getFlowStatus(),
                cdr.getCodec(),
                cdr.getPolicyName(),
                cdr.getChargingRuleBaseName(),
                cdr.getQosProfile(),
                cdr.getQci(),
                cdr.getUplinkBytes(),
                cdr.getDownlinkBytes(),
                cdr.getTotalBytes(),
                cdr.getVoiceSeconds(),
                cdr.getSmsCount(),
                cdr.getUsedUnits(),
                cdr.getGrantedUnits(),
                cdr.getStartTime(),
                cdr.getLastUpdateTime(),
                cdr.getEndTime(),
                cdr.getSessionDuration(),
                cdr.getTerminationCause(),
                cdr.getTerminationReason(),
                cdr.getDisconnectSource(),
                cdr.getResultCode(),
                cdr.getResultDescription(),
                cdr.getStatus(),
                cdr.getErrorMessage(),
                cdr.getRequestPayload(),
                cdr.getResponsePayload(),
                cdr.getNodeName(),
                cdr.getPodName(),
                id
        );
    }

    public void deleteCdr(Long id){jdbcTemplate.update(DiameterSQLConstants.DELETE_CDR, id);
    }

    private RowMapper<DiameterSessionCdr> cdrRowMapper(){

        return (rs,rowNum)->{DiameterSessionCdr cdr = new DiameterSessionCdr();

            cdr.setId(rs.getLong("ID"));

            cdr.setSessionId(
                    rs.getString("SESSION_ID"));

            cdr.setTransactionId(
                    rs.getString("TRANSACTION_ID"));

            cdr.setDiameterInterface(
                    rs.getString("DIAMETER_INTERFACE"));

            cdr.setCcRequestNumber(
                    rs.getLong("CC_REQUEST_NUMBER"));

            cdr.setServiceType(
                    rs.getString("SERVICE_TYPE"));

            cdr.setServiceContextId(
                    rs.getString("SERVICE_CONTEXT_ID"));

            cdr.setRatingGroup(
                    rs.getLong("RATING_GROUP"));

            cdr.setServiceIdentifier(
                    rs.getLong("SERVICE_IDENTIFIER"));

            cdr.setMsisdn(
                    rs.getString("MSISDN"));

            cdr.setImsi(
                    rs.getString("IMSI"));

            cdr.setImei(
                    rs.getString("IMEI"));

            cdr.setCallingParty(
                    rs.getString("CALLING_PARTY"));

            cdr.setCalledParty(
                    rs.getString("CALLED_PARTY"));

            cdr.setIpAddress(
                    rs.getString("IP_ADDRESS"));

            cdr.setApn(
                    rs.getString("APN"));

            cdr.setOriginHost(
                    rs.getString("ORIGIN_HOST"));

            cdr.setOriginRealm(
                    rs.getString("ORIGIN_REALM"));

            cdr.setDestinationHost(
                    rs.getString("DESTINATION_HOST"));

            cdr.setDestinationRealm(
                    rs.getString("DESTINATION_REALM"));

            cdr.setMediaType(
                    rs.getString("MEDIA_TYPE"));

            cdr.setSipMethod(
                    rs.getString("SIP_METHOD"));

            cdr.setAfChargingIdentifier(
                    rs.getString("AF_CHARGING_IDENTIFIER"));

            cdr.setFlowStatus(
                    rs.getString("FLOW_STATUS"));

            cdr.setCodec(
                    rs.getString("CODEC"));

            cdr.setPolicyName(
                    rs.getString("POLICY_NAME"));

            cdr.setChargingRuleBaseName(
                    rs.getString("CHARGING_RULE_BASE_NAME"));

            cdr.setQosProfile(
                    rs.getString("QOS_PROFILE"));

            cdr.setQci(
                    rs.getInt("QCI"));

            cdr.setUplinkBytes(
                    rs.getLong("UPLINK_BYTES"));

            cdr.setDownlinkBytes(
                    rs.getLong("DOWNLINK_BYTES"));

            cdr.setTotalBytes(
                    rs.getLong("TOTAL_BYTES"));

            cdr.setVoiceSeconds(
                    rs.getLong("VOICE_SECONDS"));

            cdr.setSmsCount(
                    rs.getLong("SMS_COUNT"));

            cdr.setUsedUnits(
                    rs.getLong("USED_UNITS"));

            cdr.setGrantedUnits(
                    rs.getLong("GRANTED_UNITS"));

            cdr.setSessionDuration(
                    rs.getLong("SESSION_DURATION"));

            cdr.setTerminationCause(
                    rs.getString("TERMINATION_CAUSE"));

            cdr.setTerminationReason(
                    rs.getString("TERMINATION_REASON"));

            cdr.setDisconnectSource(
                    rs.getString("DISCONNECT_SOURCE"));

            cdr.setResultCode(
                    rs.getInt("RESULT_CODE"));

            cdr.setResultDescription(
                    rs.getString("RESULT_DESCRIPTION"));

            cdr.setStatus(
                    rs.getString("STATUS"));

            cdr.setErrorMessage(
                    rs.getString("ERROR_MESSAGE"));

            cdr.setRequestPayload(
                    rs.getString("REQUEST_PAYLOAD"));

            cdr.setResponsePayload(
                    rs.getString("RESPONSE_PAYLOAD"));

            cdr.setNodeName(
                    rs.getString("NODE_NAME"));

            cdr.setPodName(
                    rs.getString("POD_NAME"));
            cdr.setStartTime(
                    rs.getTimestamp("START_TIME") != null ?
                            rs.getTimestamp("START_TIME")
                                    .toLocalDateTime()
                            : null);

            cdr.setLastUpdateTime(
                    rs.getTimestamp("LAST_UPDATE_TIME") != null ?
                            rs.getTimestamp("LAST_UPDATE_TIME")
                                    .toLocalDateTime()
                            : null);

            cdr.setEndTime(
                    rs.getTimestamp("END_TIME") != null ?
                            rs.getTimestamp("END_TIME")
                                    .toLocalDateTime()
                            : null);

            cdr.setCreatedDate(
                    rs.getTimestamp("CREATED_DATE") != null ?
                            rs.getTimestamp("CREATED_DATE")
                                    .toLocalDateTime()
                            : null);
            return cdr;
        };
    }

    // Helper method for pagination
    private Map<String, Object> buildPaginatedResponse(
            List<DiameterSessionCdr> cdrs,
            int page,
            int size,
            Integer total) {

        Map<String, Object> response = new HashMap<>();

        response.put("content", cdrs);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", total);
        response.put("totalPages", (int) Math.ceil((double) total / size));

        return response;
    }
}