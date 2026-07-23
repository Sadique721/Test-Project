package com.diameter.util;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.diameter.commons.DiameterAnswer;
import com.diameter.commons.DiameterRequest;
import com.diameter.commons.DiameterUtils;
import com.diameter.model.DiameterAudit;
import com.diameter.model.DiameterLiveSession;
import com.diameter.service.AsyncDiameterAuditService;
import com.diameter.service.DiameterLiveSessionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenericDiameterProcessor {

    @Autowired
    private DiameterLiveSessionService diameterLiveSessionService;
    
    @Autowired
    private AsyncDiameterAuditService asyncDiameterAuditService;
    
    @Autowired
    private GenericDiameterParser genericDiameterParser;
    
    @Value("${info.app.auditEnable}")
    private String auditEnable;

    public void process(DiameterRequest diameterRequest,DiameterAnswer diameterAnswer,Map<String,String> valueMap) {
        try {
        	DiameterLiveSession dto = genericDiameterParser.parse(diameterRequest, diameterAnswer,valueMap);

			if (auditEnable.equalsIgnoreCase("true")) {
				try {
					asyncDiameterAuditService.saveAudit(convertToAudit(dto, diameterRequest));
				} catch (Exception e) {
					log.error("Diameter audit insert failed for session: {}", dto.getSessionId(), e);
				}
			}

			try {
				diameterLiveSessionService.process(dto);
			} catch (Exception e) {
				log.error("Diameter live/CDR processing failed for session: {}", dto.getSessionId(), e);
			}

        } catch (Exception e) {
            log.error("Generic processing failed",e);
        }
    }
    
    private DiameterAudit convertToAudit(DiameterLiveSession session,DiameterRequest diameterRequest) {

        DiameterAudit audit = new DiameterAudit();

        audit.setTransactionId(
                session.getTransactionId());

        audit.setSessionId(
                session.getSessionId());

        audit.setProtocol(
                session.getDiameterInterface());

        audit.setServiceType(
                session.getServiceType());

        audit.setRequestType(
                session.getCcRequestType());

        audit.setMsisdn(
                session.getMsisdn());

        audit.setImsi(
                session.getImsi());

        audit.setImei(
                session.getImei());

        audit.setApn(
                session.getApn());

        audit.setOriginHost(
                session.getOriginHost());

        audit.setOriginRealm(
                session.getOriginRealm());

        audit.setDestinationHost(
                session.getDestinationHost());

        audit.setDestinationRealm(
                session.getDestinationRealm());

        audit.setResultCode(
                session.getResultCode());

        audit.setStatus(
                session.getStatus());

        audit.setRequestPayload(
                session.getRequestPayload());

        audit.setResponsePayload(
                session.getResponsePayload());

        audit.setPeerName(
                session.getNodeName());

        audit.setPodName(
                session.getPodName());
        
        audit.setCommandCode(
        		diameterRequest.getCommandCode());

        audit.setApplicationId(
        		diameterRequest.getApplicationID());

        audit.setCreatedAt(
                LocalDateTime.now());
        
        if(audit.getResultCode() !=null) {
	        audit.setResultDescription(DiameterUtils.getFailureReason(audit.getResultCode()));
	        
	        audit.setErrorMessage(DiameterUtils.getFailureReason(audit.getResultCode()));
        }

        if(session.getStartTime() != null
                && session.getLastUpdateTime() != null) {

            long processingTime =
                    java.time.Duration.between(
                            session.getStartTime(),
                            session.getLastUpdateTime())
                            .toMillis();

            audit.setProcessingTimeMs(
                    processingTime);
        }
        
        audit.setCcRequestNumber(session.getCcRequestNumber() !=null ? String.valueOf(session.getCcRequestNumber()):null);
        audit.setSubscriptionId(session.getSubscriptionId());
        audit.setFramedIpAddress(session.getFramedIpAddress());
        audit.setFramedIpv6Prefix(session.getFramedIpv6Prefix());
        audit.setCalledStationId(session.getCalledStationId());
        audit.setThreeGppRatType(session.getThreeGppRatType());
        audit.setQosInformation(session.getQosInformation());
        audit.setBearerIdentifier(session.getBearerIdentifier());
        audit.setIpCanType(session.getIpCanType());
        audit.setAnGwAddress(session.getAnGwAddress());
        audit.setThreeGppSgsnAddress(session.getThreeGppSgsnAddress());
        audit.setUserName(session.getUserName());
        audit.setOriginStateId(session.getOriginStateId());
        audit.setUserEquipmentInfo(session.getUserEquipmentInfo());
        audit.setCcSubSessionId(session.getCcSubSessionId());
        audit.setTftPacketFilterInformation(session.getTftPacketFilterInformation());
        audit.setChargingRuleInstall(session.getChargingRuleInstall());
        audit.setChargingRuleRemove(session.getChargingRuleRemove());
        audit.setSupportedFeatures(session.getSupportedFeatures());
        audit.setEventTrigger(session.getEventTrigger());
        audit.setUsageMonitoringInformation(session.getUsageMonitoringInformation());
        audit.setChargingRuleReport(session.getChargingRuleReport());
        audit.setThreeGppUserLocationInfo(session.getThreeGppUserLocationInfo());
        audit.setTerminationCause(session.getTerminationCause());
        

        // PS-Information Fields
        audit.setPdpType(session.getPdpType());
        audit.setImsiUnauthenticatedFlag(session.getImsiUnauthenticatedFlag());
        audit.setPdpContextType(session.getPdpContextType());
        audit.setServingNodeType(session.getServingNodeType());

       // Existing Live Session field
        audit.setChargingId(session.getAfChargingIdentifier());

        audit.setPdpAddress(session.getPdpAddress());
        audit.setGgsnAddress(session.getGgsnAddress());
        audit.setDynamicAddressFlag(session.getDynamicAddressFlag());
        audit.setImsiMccMnc(session.getImsiMccMnc());
        audit.setNsapi(session.getNsapi());
        audit.setChargingCharacteristics(session.getChargingCharacteristics());
        audit.setSgsnMccMnc(session.getSgsnMccMnc());
        audit.setMsTimeZone(session.getMsTimeZone());
        audit.setUserLocationInfoTime(session.getUserLocationInfoTime());
        audit.setUserLocationInfo(session.getUserLocationInfo());
        
        return audit;
    }
}