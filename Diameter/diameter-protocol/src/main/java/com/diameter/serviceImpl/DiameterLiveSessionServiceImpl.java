package com.diameter.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.diameter.commons.DiameterUtils;
import com.diameter.model.DiameterLiveSession;
import com.diameter.model.DiameterSessionCdr;
import com.diameter.repository.DiameterLiveSessionRepository;
import com.diameter.service.DiameterLiveSessionService;
import com.diameter.service.DiameterSessionCdrService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DiameterLiveSessionServiceImpl implements DiameterLiveSessionService {
	
	 @Value("${info.app.liveSessionExpiry:7200}")
	 private long liveSessionExpiry;

	@Autowired
    private DiameterLiveSessionRepository repository;
    
    @Autowired
    private DiameterSessionCdrService diameterSessionCdrService;
    
    @Autowired
    private LocalCacheManagerServiceImpl cacheManagerServiceImpl;
    
    private static final Logger logger = LoggerFactory.getLogger(DiameterLiveSessionServiceImpl.class);

    @Override
    public DiameterLiveSession createSession(DiameterLiveSession session) {
        DiameterLiveSession existingSession = cacheManagerServiceImpl.getValue("LIVE_SESSION_"+session.getSessionId(), DiameterLiveSession.class);
        if (existingSession != null) {
            log.warn("Session already exists : {}", session.getSessionId());
            return existingSession;
        }
        cacheManagerServiceImpl.setValueWithExpiry("LIVE_SESSION_"+session.getSessionId(), session,liveSessionExpiry,TimeUnit.SECONDS);
        repository.saveSession(session);
        return session;
    }


    @Override
    public Map<String, Object> getSessions(
            String sessionId,
            String transactionId,
            String msisdn,
            String imsi,
            String status,
            LocalDate createdAt,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {

        try {

            /*
             * Single Record
             */
            if (sessionId != null) {

                DiameterLiveSession session =
                        repository.getSessionById(sessionId);

                List<DiameterLiveSession> sessions =
                        new ArrayList<>();

                if (session != null) {
                    sessions.add(session);
                }

                return buildResponse(
                        sessions,
                        0,
                        sessions.size(),
                        sessions.size());
            }

            /*
             * Multiple Records (Paginated)
             */
            if (transactionId != null) {
                return repository.getByTransactionIdPaginated(
                        transactionId,
                        page,
                        size);
            }

            if (msisdn != null) {
                return repository.getByMsisdnPaginated(
                        msisdn,
                        page,
                        size);
            }

            if (imsi != null) {
                return repository.getByImsiPaginated(
                        imsi,
                        page,
                        size);
            }

            if (status != null) {
                return repository.getByStatusPaginated(
                        status,
                        page,
                        size);
            }

            if (createdAt != null) {
                return repository.getByDatePaginated(
                        createdAt,
                        page,
                        size);
            }

            if (fromDate != null && toDate != null) {
                return repository.getByDateRangePaginated(
                        fromDate,
                        toDate,
                        page,
                        size);
            }

            return repository.getAllSessionsPaginated(
                    page,
                    size);

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
        }

        return Collections.emptyMap();
    }

    @Override
    public DiameterLiveSession updateSession(
            String sessionId,
            DiameterLiveSession session) {

        DiameterLiveSession liveSession = cacheManagerServiceImpl.getValue("LIVE_SESSION_"+session.getSessionId(), DiameterLiveSession.class);

        /*
         * Existing behavior:
         * If session does not exist, create it first.
         */
        if (liveSession == null) {

            log.info("Session not found : {}", sessionId);

            createSession(session);

            return session;
        }

        liveSession.setCcRequestType(
                session.getCcRequestType());

        liveSession.setCcRequestNumber(
                session.getCcRequestNumber());

        liveSession.setUplinkBytes(
                liveSession.getUplinkBytes()
                        + session.getUplinkBytes());

        liveSession.setDownlinkBytes(
                liveSession.getDownlinkBytes()
                        + session.getDownlinkBytes());

        liveSession.setTotalBytes(
                liveSession.getTotalBytes()
                        + session.getTotalBytes());

        liveSession.setVoiceSeconds(
                liveSession.getVoiceSeconds()
                        + session.getVoiceSeconds());

        liveSession.setUsedUnits(
                liveSession.getUsedUnits()
                        + session.getUsedUnits());

        liveSession.setGrantedUnits(
                liveSession.getGrantedUnits()
                        + session.getGrantedUnits());

        liveSession.setLastUpdateTime(
                session.getLastUpdateTime());

        liveSession.setActiveFlag(
                session.getActiveFlag());

        liveSession.setSessionStatus(
                session.getSessionStatus());

        liveSession.setResultCode(
                session.getResultCode());

        if (session.getRatingGroup() != null) {
            liveSession.setRatingGroup(
                    session.getRatingGroup());
        }

        if (session.getQci() != null) {
            liveSession.setQci(
                    session.getQci());
        }

        if (session.getChargingRuleBaseName() != null) {
            liveSession.setChargingRuleBaseName(
                    session.getChargingRuleBaseName());
        }

        if (session.getQosProfile() != null) {
            liveSession.setQosProfile(
                    session.getQosProfile());
        }

        if (session.getMsisdn() != null) {
            liveSession.setMsisdn(
                    session.getMsisdn());
        }

        if (session.getImsi() != null) {
            liveSession.setImsi(
                    session.getImsi());
        }

        if (session.getImei() != null) {
            liveSession.setImei(
                    session.getImei());
        }

        if (session.getNasIpAddress() != null) {
            liveSession.setNasIpAddress(
                    session.getNasIpAddress());
        }

        if (session.getIpv4Address() != null) {
            liveSession.setIpv4Address(
                    session.getIpv4Address());
        }

        if (session.getIpv6Address() != null) {
            liveSession.setIpv6Address(
                    session.getIpv6Address());
        }

        if (session.getCurrentPlan() != null) {
            liveSession.setCurrentPlan(
                    session.getCurrentPlan());
        }

        if (session.getPlanStartDate() != null) {
            liveSession.setPlanStartDate(
                    session.getPlanStartDate());
        }

        if (session.getPlanExpiryDate() != null) {
            liveSession.setPlanExpiryDate(
                    session.getPlanExpiryDate());
        }
        
        if (session.getUserLocationInfo() != null) {
        	liveSession.setUserLocationInfo(session.getUserLocationInfo());
        }

        repository.updateSession(sessionId,liveSession);

        return liveSession;
    }

    @Override
    public void deleteSession(String sessionId){
        repository.deleteSession(sessionId);
    }

    private void terminateSession(DiameterLiveSession session) {
        try {
            DiameterLiveSession liveSession = cacheManagerServiceImpl.getValue("LIVE_SESSION_"+session.getSessionId(), DiameterLiveSession.class);

            /*
             * Existing behavior:
             * If session is not found, use the incoming
             * session object itself.
             */
            if (liveSession == null) {

                log.info("Session not found : {}",
                        session.getSessionId());

                liveSession = session;

            } else {

                liveSession.setRequestPayload(
                        session.getRequestPayload());

                liveSession.setResponsePayload(
                        session.getResponsePayload());

                liveSession.setCcRequestType(
                        session.getCcRequestType());

                liveSession.setCcRequestNumber(
                        session.getCcRequestNumber());

                liveSession.setUplinkBytes(
                        liveSession.getUplinkBytes()
                                + session.getUplinkBytes());

                liveSession.setDownlinkBytes(
                        liveSession.getDownlinkBytes()
                                + session.getDownlinkBytes());

                liveSession.setTotalBytes(
                        liveSession.getTotalBytes()
                                + session.getTotalBytes());

                liveSession.setVoiceSeconds(
                        liveSession.getVoiceSeconds()
                                + session.getVoiceSeconds());

                liveSession.setUsedUnits(
                        liveSession.getUsedUnits()
                                + session.getUsedUnits());

                liveSession.setGrantedUnits(
                        liveSession.getGrantedUnits()
                                + session.getGrantedUnits());

                liveSession.setLastUpdateTime(
                        LocalDateTime.now());

                liveSession.setActiveFlag(
                        session.getActiveFlag());

                liveSession.setSessionStatus(
                        session.getSessionStatus());

                liveSession.setResultCode(
                        session.getResultCode());

                if (session.getMsisdn() != null) {

                    liveSession.setMsisdn(
                            session.getMsisdn());
                }

                if (session.getImsi() != null) {

                    liveSession.setImsi(
                            session.getImsi());
                }

                if (session.getImei() != null) {

                    liveSession.setImei(
                            session.getImei());
                }

                if (session.getRatingGroup() != null) {

                    liveSession.setRatingGroup(
                            session.getRatingGroup());
                }

                if (session.getQci() != null) {

                    liveSession.setQci(
                            session.getQci());
                }

                if (session.getChargingRuleBaseName() != null) {

                    liveSession.setChargingRuleBaseName(
                            session.getChargingRuleBaseName());
                }

                if (session.getQosProfile() != null) {

                    liveSession.setQosProfile(
                            session.getQosProfile());
                }

                if (session.getNasIpAddress() != null) {

                    liveSession.setNasIpAddress(
                            session.getNasIpAddress());
                }

                if (session.getIpv4Address() != null) {

                    liveSession.setIpv4Address(
                            session.getIpv4Address());
                }

                if (session.getIpv6Address() != null) {

                    liveSession.setIpv6Address(
                            session.getIpv6Address());
                }

                if (session.getCurrentPlan() != null) {

                    liveSession.setCurrentPlan(
                            session.getCurrentPlan());
                }

                if (session.getPlanStartDate() != null) {

                    liveSession.setPlanStartDate(
                            session.getPlanStartDate());
                }

                if (session.getPlanExpiryDate() != null) {

                    liveSession.setPlanExpiryDate(
                            session.getPlanExpiryDate());
                }
                
                if(session.getUserLocationInfo() !=null) {
                	liveSession.setUserLocationInfo(session.getUserLocationInfo());
                }
            }

            diameterSessionCdrService.createCdr(
                    convertToCdr(liveSession));
            
            cacheManagerServiceImpl.deleteKey("LIVE_SESSION_"+session.getSessionId());

            deleteSession(session.getSessionId());

            log.debug("Session terminated : {}",
                    session.getSessionId());

        } catch (Exception e) {

            log.error("Unable to terminate session", e);
        }
    }


	private void createEventCdr(DiameterLiveSession session) {
        try {
        	diameterSessionCdrService.createCdr(convertToCdr(session));
        	log.debug("Event CDR created : {}",session.getSessionId());

        } catch (Exception e) {
            log.error("Unable to create event CDR",e);
        }
	}
    
    @Override
    public void process(DiameterLiveSession dto) {
        try {

            if(dto == null) {
                return;
            }

            String requestType =dto.getCcRequestType();

            if(requestType == null) {
                return;
            }

            switch(requestType) {

                case "INITIAL_REQUEST":

                	createSession(dto);
                    break;

                case "UPDATE_REQUEST":

                	updateSession(dto.getSessionId(),dto);
                    break;
                    
                case "AUTHORIZE_AUTHENTICATE":

                	updateSession(dto.getSessionId(),dto);
                    break;
                
                case "AUTHORIZE_ONLY":

                	updateSession(dto.getSessionId(),dto);
                    break;

                case "TERMINATION_REQUEST":

                    terminateSession(dto);
                    break;

                case "EVENT_REQUEST":

                    createEventCdr(dto);
                    break;

                default:
                    log.warn("Unknown CC Request Type : {}",requestType);
                    updateSession(dto.getSessionId(),dto);
                    break;
            }

        } catch (Exception e) {
            log.error("Error while processing session",e);
        }
    }
    
    private DiameterSessionCdr convertToCdr(
            DiameterLiveSession session) {

        DiameterSessionCdr cdr =new DiameterSessionCdr();

        cdr.setSessionId(
                session.getSessionId());

        cdr.setTransactionId(
                session.getTransactionId());

        cdr.setDiameterInterface(
                session.getDiameterInterface());

        cdr.setCcRequestNumber(
                session.getCcRequestNumber());

        cdr.setServiceType(
                session.getServiceType());

        cdr.setServiceContextId(
                session.getServiceContextId());

        cdr.setRatingGroup(
                session.getRatingGroup());

        cdr.setServiceIdentifier(
                session.getServiceIdentifier());

        cdr.setMsisdn(
                session.getMsisdn());

        cdr.setImsi(
                session.getImsi());

        cdr.setImei(
                session.getImei());

        cdr.setCallingParty(
                session.getCallingParty());

        cdr.setCalledParty(
                session.getCalledParty());

        cdr.setIpAddress(
                session.getIpAddress());

        cdr.setApn(
                session.getApn());

        cdr.setOriginHost(
                session.getOriginHost());

        cdr.setOriginRealm(
                session.getOriginRealm());

        cdr.setDestinationHost(
                session.getDestinationHost());

        cdr.setDestinationRealm(
                session.getDestinationRealm());

        cdr.setMediaType(
                session.getMediaType());

        cdr.setSipMethod(
                session.getSipMethod());

        cdr.setAfChargingIdentifier(
                session.getAfChargingIdentifier());

        cdr.setFlowStatus(
                session.getFlowStatus());

        cdr.setCodec(
                session.getCodec());

        cdr.setPolicyName(
                session.getPolicyName());

        cdr.setChargingRuleBaseName(
                session.getChargingRuleBaseName());

        cdr.setQosProfile(
                session.getQosProfile());

        cdr.setQci(
                session.getQci());

        cdr.setUplinkBytes(
                session.getUplinkBytes());

        cdr.setDownlinkBytes(
                session.getDownlinkBytes());

        cdr.setTotalBytes(
                session.getTotalBytes());

        cdr.setVoiceSeconds(
                session.getVoiceSeconds());

        cdr.setSmsCount(
                session.getSmsCount());

        cdr.setUsedUnits(
                session.getUsedUnits());

        cdr.setGrantedUnits(
                session.getGrantedUnits());

        cdr.setStartTime(
                session.getStartTime());

        cdr.setLastUpdateTime(
                session.getLastUpdateTime());

        cdr.setEndTime(
                LocalDateTime.now());
        
        cdr.setIpAddress(session.getIpAddress());

        if(session.getStartTime() != null) {

            long duration =
                    java.time.Duration.between(
                            session.getStartTime(),
                            LocalDateTime.now())
                            .getSeconds();

            cdr.setSessionDuration(duration);
        }

        cdr.setResultCode(
                session.getResultCode());

        cdr.setStatus(
                session.getStatus());

        cdr.setRequestPayload(
                session.getRequestPayload());

        cdr.setResponsePayload(
                session.getResponsePayload());

       cdr.setNodeName(
                session.getNodeName());

        cdr.setPodName(
                session.getPodName());

        cdr.setCreatedDate(
                LocalDateTime.now());
        
        cdr.setUserLocationInfo(session.getUserLocationInfo());
        
        if(cdr.getResultCode() !=null) {
	        cdr.setResultDescription(DiameterUtils.getFailureReason(cdr.getResultCode()));
	        
	        cdr.setErrorMessage(DiameterUtils.getFailureReason(cdr.getResultCode()));
        }

        return cdr;
    }

    private Map<String, Object> buildResponse(
            List<DiameterLiveSession> sessions,
            int page,
            int size,
            long total) {

        Map<String, Object> response = new HashMap<>();

        response.put("content", sessions);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", total);
        response.put("totalPages",
                (int) Math.ceil((double) total / Math.max(size, 1)));

        return response;
    }
}