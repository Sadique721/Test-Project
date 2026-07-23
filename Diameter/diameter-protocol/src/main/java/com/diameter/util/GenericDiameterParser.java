package com.diameter.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.diameter.commons.AvpUserLocationInfo;
import com.diameter.commons.DiameterAnswer;
import com.diameter.commons.DiameterRequest;
import com.diameter.commons.DiameterUtils;
import com.diameter.commons.IDiameterAVP;
import com.diameter.model.DiameterLiveSession;
import com.diameter.model.UserLocationInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenericDiameterParser {

    public DiameterLiveSession parse(DiameterRequest request,DiameterAnswer response,Map<String,String> valueMap) {
    	DiameterLiveSession dto = new DiameterLiveSession();
        try {
            dto.setSessionId(getStringAvp(request,"0:263"));

            dto.setTransactionId(extractTransactionId(request));

            dto.setCcRequestType(getRequestType(request));

            dto.setCcRequestNumber(getLongAvp(request,"0:415"));

            dto.setDiameterInterface(detectInterface(request));
            
            dto.setIpAddress(getFramedIpAddress(request));

            dto.setMsisdn(extractMsisdn(request));

            dto.setImsi(extractImsi(request));

            dto.setImei(getStringAvp(request,"10415:1402"));

            dto.setCallingParty(getCallingPartyAddress(request));

            dto.setCalledParty(getCalledPartyAddress(request));

            dto.setApn(getStringAvp(request,"0:30"));

            dto.setOriginHost(getStringAvp(request,"0:264"));

            dto.setOriginRealm(getStringAvp(request,"0:296"));

            dto.setDestinationHost(getStringAvp(request,"0:293"));

            dto.setDestinationRealm(getStringAvp(request,"0:283"));

            dto.setServiceContextId(getStringAvp(request,"0:461"));

            dto.setRatingGroup(extractRatingGroups(request));
            
            dto.setChargingRuleBaseName(getChargingRuleName(request));
            
            dto.setQci(getQci(request));
            
            dto.setQosProfile(getQos(request));

            dto.setServiceIdentifier(extractServiceIdentifier(request));

            dto.setMediaType(getMediaType(request));

            dto.setSipMethod(extractSipMethod(request));

            dto.setCodec(extractCodecs(request).toString());

            dto.setFlowStatus(extractFlowStatuses(request).toString());

            dto.setServiceType(extractServiceType(request));

            dto.setUplinkBytes(extractTotalInputOctets(request));

            dto.setDownlinkBytes(extractTotalOutputOctets(request));

            dto.setTotalBytes(nvl(dto.getUplinkBytes())+ nvl(dto.getDownlinkBytes()));

            dto.setVoiceSeconds(extractVoiceSeconds(request));

            dto.setSmsCount(extractSmsCount(request));

            if(response != null) {
                dto.setResultCode(extractResultCode(response));
            }

            dto.setStatus(getStatus(dto.getResultCode()));

            dto.setLastUpdateTime(LocalDateTime.now());

            dto.setStartTime(LocalDateTime.now());

            dto.setRequestPayload(request.toString());
            
            dto.setActiveFlag(!"TERMINATION_REQUEST".equals(dto.getCcRequestType()));
            
            dto.setUsedUnits(extractUsedUnits(dto));
            
            dto.setGrantedUnits(getRequestedServiceUnitTime(request,dto));

            dto.setSessionStatus(getSessionStatus(dto.getCcRequestType()));
            
            dto.setNodeName(request.getPeerData()!=null ? request.getPeerData().getPeerName(): "Unknown");
            
            String podName = System.getenv("ENV_POD_NAME");
    	    if (podName == null) {
    	    	podName = "diameter_server";
    	    }
    	    dto.setPodName(podName);
            
            if(response != null) {
                dto.setResponsePayload(response.toString());
            }
            
            dto.setNasIpAddress(null);
            dto.setIpv4Address(null);
            dto.setIpv6Address(null);
            dto.setCurrentPlan(valueMap.get("customerPlan.name"));
            
            String strStartDate = valueMap.get("customerPackage.startDate");
            if(strStartDate !=null) {
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            	LocalDateTime localDateTime = LocalDateTime.parse(strStartDate, formatter);
            	dto.setPlanStartDate(localDateTime);
            }
            
            String strExpiryDate = valueMap.get("customerPackage.expiryDate");
            if(strExpiryDate !=null) {
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            	LocalDateTime localDateTime = LocalDateTime.parse(strExpiryDate, formatter);
            	dto.setPlanExpiryDate(localDateTime);
            }

            dto.setPdpType(getPdpType(request));
            dto.setImsiUnauthenticatedFlag(getImsiUnauthenticatedFlag(request));
            dto.setPdpContextType(getPdpContextType(request));
            dto.setServingNodeType(getServingNodeType(request));
            dto.setAfChargingIdentifier(getChargingId(request));
            dto.setPdpAddress(getPdpAddress(request));
            dto.setThreeGppSgsnAddress(getSgsnAddress(request));
            dto.setGgsnAddress(getGgsnAddress(request));
            dto.setDynamicAddressFlag(getDynamicAddressFlag(request));
            dto.setImsiMccMnc(getImsiMccMnc(request));
            dto.setNsapi(getNsapi(request));
            dto.setChargingCharacteristics(getChargingCharacteristics(request));
            dto.setSgsnMccMnc(getSgsnMccMnc(request));
            dto.setMsTimeZone(getMsTimeZone(request));
            dto.setUserLocationInfoTime(getUserLocationInfoTime(request));
            dto.setThreeGppRatType(getRatType(request));
            
            UserLocationInfo userLocationInfo=extractUserLocationInfo(request);
            if(userLocationInfo !=null) {
            	dto.setUserLocationInfo(userLocationInfo);
            }
            
        } catch (Exception e) {
            log.error("Diameter parsing failed",e);
        }
        return dto;
    }
    
    public static Integer getQci(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> chargingRuleInstalls =diameterRequest.getAVPList("10415:1001");

            if (chargingRuleInstalls == null || chargingRuleInstalls.isEmpty()) {
                return null;
            }

            for (IDiameterAVP chargingRuleInstall : chargingRuleInstalls) {

                List<IDiameterAVP> installAvps =
                        chargingRuleInstall.getGroupedAvp();

                if (installAvps == null) {
                    continue;
                }

                for (IDiameterAVP installAvp : installAvps) {

                    // Charging-Rule-Definition
                    if (installAvp.getAVPCode() == 1003) {

                        List<IDiameterAVP> ruleDefAvps =
                                installAvp.getGroupedAvp();

                        if (ruleDefAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP ruleDefAvp : ruleDefAvps) {

                            // QoS-Information
                            if (ruleDefAvp.getAVPCode() == 1016) {

                                List<IDiameterAVP> qosAvps =ruleDefAvp.getGroupedAvp();

                                if (qosAvps == null) {
                                    continue;
                                }

                                for (IDiameterAVP qosAvp : qosAvps) {

                                    // QoS-Class-Identifier (QCI)
                                    if (qosAvp.getAVPCode() == 1028) {
                                        return Integer.parseInt(qosAvp.getStringValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract QCI", e);
        }
        return null;
    }
    
    public static String getQos(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> chargingRuleInstalls =diameterRequest.getAVPList("10415:1001");

            if (chargingRuleInstalls == null || chargingRuleInstalls.isEmpty()) {
                return null;
            }

            for (IDiameterAVP chargingRuleInstall : chargingRuleInstalls) {

                List<IDiameterAVP> installAvps =
                        chargingRuleInstall.getGroupedAvp();

                if (installAvps == null) {
                    continue;
                }

                for (IDiameterAVP installAvp : installAvps) {

                    // Charging-Rule-Definition
                    if (installAvp.getAVPCode() == 1003) {

                        List<IDiameterAVP> ruleDefAvps =installAvp.getGroupedAvp();

                        if (ruleDefAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP ruleDefAvp : ruleDefAvps) {

                            // QoS-Information
                            if (ruleDefAvp.getAVPCode() == 1016) {

                                List<IDiameterAVP> qosAvps =ruleDefAvp.getGroupedAvp();

                                if (qosAvps == null) {
                                    continue;
                                }

                                for (IDiameterAVP qosAvp : qosAvps) {

                                    // QoS-Class-Identifier (QCI)
                                    if (qosAvp.getAVPCode() == 1028) {
                                        qosAvp.getStringValue();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract QCI", e);
        }
        return null;
    }
    
    public static String getChargingRuleName(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> chargingRuleInstalls =diameterRequest.getAVPList("10415:1001");

            if (chargingRuleInstalls == null) {
                return null;
            }

            for (IDiameterAVP chargingRuleInstall : chargingRuleInstalls) {

                List<IDiameterAVP> installAvps =chargingRuleInstall.getGroupedAvp();

                if (installAvps == null) {
                    continue;
                }

                for (IDiameterAVP installAvp : installAvps) {

                    if (installAvp.getAVPCode() == 1003) {

                        List<IDiameterAVP> ruleDefAvps =installAvp.getGroupedAvp();

                        if (ruleDefAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP ruleDefAvp : ruleDefAvps) {

                            if (ruleDefAvp.getAVPCode() == 1005) {
                                return ruleDefAvp.getStringValue();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract Charging Rule Name", e);
        }

        return null;
    }
    
    public static String getFramedIpAddress(DiameterRequest diameterRequest) {
        try {
        	IDiameterAVP iDiameterAVP = diameterRequest.getAVP("0:8");

            if (iDiameterAVP == null) {
                return null;
            }

            byte[] ipBytes = iDiameterAVP.getBytes();

            if (ipBytes == null || ipBytes.length != 4) {
                return null;
            }

            return (ipBytes[0] & 0xFF) + "." +
                   (ipBytes[1] & 0xFF) + "." +
                   (ipBytes[2] & 0xFF) + "." +
                   (ipBytes[3] & 0xFF);

        } catch (Exception e) {
            log.error("Unable to extract Framed-IP-Address", e);
            return null;
        }
    }
    
    private String getSessionStatus(String requestType) {
        if("TERMINATION_REQUEST".equals(requestType)) {
            return "TERMINATED";
        }

        if("EVENT_REQUEST".equals(requestType)) {
            return "EVENT";
        }

        return "ACTIVE";
    }
    
    private Long extractUsedUnits(DiameterLiveSession dto) {
        if("SMS".equalsIgnoreCase(dto.getServiceType())) {
            return nvl(dto.getSmsCount());
        }

        if("VOICE".equalsIgnoreCase(dto.getServiceType())) {
            return nvl(dto.getVoiceSeconds());
        }

        if("DATA".equalsIgnoreCase(dto.getServiceType())) {
            return nvl(dto.getTotalBytes());
        }

        return 0L;
    }
    
    public static Long getRequestedServiceUnitTime(DiameterRequest diameterRequest,DiameterLiveSession dto) {
        try {
            List<IDiameterAVP> msccList = diameterRequest.getAVPList("0:456");

            if (msccList == null) {
                return 0L;
            }

            for (IDiameterAVP mscc : msccList) {

                List<IDiameterAVP> msccAvps = mscc.getGroupedAvp();

                if (msccAvps == null) {
                    continue;
                }

                for (IDiameterAVP avp : msccAvps) {

                    // Requested-Service-Unit
                    if (avp.getAVPCode() == 437) {

                        List<IDiameterAVP> rsuAvps = avp.getGroupedAvp();

                        if (rsuAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP rsuAvp : rsuAvps) {

                            // CC-Time
                        	if("VOICE".equalsIgnoreCase(dto.getServiceType())) {
	                            if (rsuAvp.getAVPCode() == 420) {
	                                return Long.valueOf(rsuAvp.getStringValue());
	                            }
                        	}else {
                        		 if (rsuAvp.getAVPCode() == 421) {
                                 	return Long.valueOf(rsuAvp.getStringValue());
                                 }
                        	}
                           
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract Requested-Service-Unit CC-Time", e);
        }
        return 0L;
    }

	private static long getTotalGrantedUnits(long totalGrantedUnits, IDiameterAVP avp) {
		String value = avp.getStringValue();
		if(value != null && !value.isEmpty()) {
		    totalGrantedUnits +=Long.parseLong(value);
		}
		return totalGrantedUnits;
	}

    private String detectInterface(DiameterRequest request) {
        long appId =
                request.getApplicationID();

        if(appId == 4) {
            return "GY";
        }

        if(appId == 16777238) {
            return "GX";
        }

        if(appId == 16777236) {
            return "RX";
        }

        if(appId == 16777216) {
            return "RO";
        }
        return "UNKNOWN";
    }


    public static String extractServiceType(DiameterRequest diameterRequest) {
        try {
        	 /*
             * SMS
             */
            if(isSmsTraffic(diameterRequest)) {
                return "SMS";
            }
            
            String strUsed = DiameterUtils.getSMSServiceUnit(diameterRequest);
        	if(strUsed !=null) {
        		return "SMS";
        	}
            
            /*
             * RX / IMS Voice
             */
            if(extractSipMethod(diameterRequest)!= null) {
                return "VOICE";
            }

            if(DiameterUtils.isVoiceRequest(diameterRequest)) {
                return "VOICE";
            }

            /*
             * Internet traffic
             */
            return "DATA";


        } catch (Exception e) {
            log.error("Service Type extraction failed",e);
        }

        return "DATA";
    }
    
    public static Long extractVoiceSeconds(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> msccList =diameterRequest.getAVPList("0:456");

            if(msccList == null || msccList.isEmpty()) {
                return 0L;
            }

            
            for(IDiameterAVP mscc : msccList) {
                for(IDiameterAVP groupedAvp: mscc.getGroupedAvp()) {

                    if(groupedAvp.getAVPCode() == 446) {
                        for(IDiameterAVP avp: groupedAvp.getGroupedAvp()) {
                            if(avp.getAVPCode() == 420) {

                                String value =avp.getStringValue();
                                if(value != null && !value.isEmpty()) {
                                    return Long.parseLong(value);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract Voice Seconds",e);
        }
        return 0L;
    }
    
    private static Long extractTotalInputOctets(DiameterRequest diameterRequest) {
        long totalInput = 0L;
        try {
            List<IDiameterAVP> msccList =diameterRequest.getAVPList("0:456");

            if(msccList == null) {
                return totalInput;
            }

            for(IDiameterAVP mscc : msccList) {

                for(IDiameterAVP groupedAvp: mscc.getGroupedAvp()) {

                    if(groupedAvp.getAVPCode() == 446) {

                        for(IDiameterAVP avp: groupedAvp.getGroupedAvp()) {

                            if(avp.getAVPCode() == 412) {

                                totalInput = getTotalGrantedUnits(totalInput, avp);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract total Input Octets",e);
        }
        return totalInput;
    }
    
    private static Long extractTotalOutputOctets(DiameterRequest diameterRequest) {
        long totalOutput = 0L;
        try {
        	IDiameterAVP diameterAVP=diameterRequest.getAVP("0:456");
    		if(diameterAVP !=null) {
    			ArrayList<IDiameterAVP> diameterAVPs=diameterAVP.getGroupedAvp();
    			if(diameterAVPs !=null) {
    				for(IDiameterAVP avp:diameterAVPs) {
    					if("446".equalsIgnoreCase(String.valueOf(avp.getAVPCode()))) {
    						ArrayList<IDiameterAVP> listIDiameterAVP = avp.getGroupedAvp();
    						if(listIDiameterAVP !=null) {
    							for(IDiameterAVP iDiameterAVP:listIDiameterAVP) {
    								if("421".equalsIgnoreCase(String.valueOf(iDiameterAVP.getAVPCode()))) {
    									return Long.valueOf(iDiameterAVP.getStringValue());
    								}
    							}
    						}
    					}
    				}
    			}
    		}
        } catch (Exception e) {
            log.error("Unable to extract total Output Octets",e);
        }
        return totalOutput;
    }
    
    public static boolean isSmsTraffic(DiameterRequest diameterRequest) {
        try {
            Long serviceIdentifier =extractServiceIdentifier( diameterRequest);

            if(serviceIdentifier != null && serviceIdentifier == 3L) {
                return true;
            }

            Long ratingGroup = extractRatingGroups(diameterRequest);

            if(ratingGroup != null && ratingGroup == 3001L) {
                return true;
            }

            String serviceContextId =getStringAvp(diameterRequest,"0:461");

            if(serviceContextId != null && serviceContextId.toLowerCase().contains("sms")) {
                return true;
            }

            String sipMethod =extractSipMethod(diameterRequest);

            if(sipMethod != null && sipMethod.equalsIgnoreCase("MESSAGE")) {
                return true;
            }

        } catch (Exception e) {
            log.error("Unable to detect SMS traffic",e);
        }
        return false;
    }
    
    public static Long extractSmsCount(DiameterRequest diameterRequest) {
        try {
        	String strUsed = DiameterUtils.getSMSServiceUnit(diameterRequest);
        	if(strUsed !=null) {
        		return Long.valueOf(strUsed);
        	}

            /*
             * Fallback Logic
             */
            if(isSmsTraffic(diameterRequest)) {
                return 1L;
            }

        } catch (Exception e) {
            log.error("Unable to extract SMS Count",e);
        }
        return 0L;
    }
    
    public static Integer extractResultCode(DiameterAnswer diameterResponse) {
        try {
            List<IDiameterAVP> resultCodes =diameterResponse.getAVPList("0:268");

            if(resultCodes == null || resultCodes.isEmpty()) {
                return null;
            }

            for(IDiameterAVP avp : resultCodes) {
                String value =avp.getStringValue();

                if(value != null && !value.isEmpty()) {
                    return Integer.parseInt(value);
                }
            }
        } catch (Exception e) {
            log.error("Unable to extract Result Code",e);
        }
        return null;
    }

    private String extractMsisdn(DiameterRequest diameterRequest) {
        try {
        	List<IDiameterAVP> subs = diameterRequest.getAVPList("0:443");

    		String msisdn = null;
    		if (subs != null) {
    			for (IDiameterAVP sub : subs) {

    				String type = null;
    				String value = null;

    				for (IDiameterAVP avp : sub.getGroupedAvp()) {
    					if (avp.getAVPCode() == 450) { // Subscription-Id-Type
    						type = avp.getStringValue();
    					} else if (avp.getAVPCode() == 444) { // Subscription-Id-Data
    						value = avp.getStringValue();
    					}
    				}

    				if (type != null && value != null) {
    					if (type.equalsIgnoreCase("0")) { // END_USER_E164
    						msisdn = value;
    					}
    				}
    			}
    		}
    		return msisdn;
        } catch (Exception e) {
            log.error("MSISDN extraction failed",e);
        }
        return null;
    }

    private String extractImsi(DiameterRequest diameterRequest) {
        try {
        	List<IDiameterAVP> subs = diameterRequest.getAVPList("0:443");

    		String imsi = null;
    		if (subs != null) {
    			for (IDiameterAVP sub : subs) {

    				String type = null;
    				String value = null;

    				for (IDiameterAVP avp : sub.getGroupedAvp()) {
    					if (avp.getAVPCode() == 450) { // Subscription-Id-Type
    						type = avp.getStringValue();
    					} else if (avp.getAVPCode() == 444) { // Subscription-Id-Data
    						value = avp.getStringValue();
    					}
    				}

    				if (type != null && value != null) {
    					if (type.equalsIgnoreCase("1")) { // END_USER_IMSI
    						imsi = value;
    					}
    				}
    			}
    		}
    		return imsi;
        } catch (Exception e) {
            log.error("IMSI extraction failed",e);
        }
        return null;
    }
    
    public static Long extractRatingGroups(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> msccList =diameterRequest.getAVPList("0:456");

            if(msccList == null) {
                return 0L;
            }

            for(IDiameterAVP mscc : msccList) {
                for(IDiameterAVP avp: mscc.getGroupedAvp()) {

                    if(avp.getAVPCode() == 432) {

                        String value = avp.getStringValue();

                        if(value != null&& !value.isEmpty()) {
                        	return Long.valueOf(value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Unable to extract Rating Groups",e);
        }
        return 0L;
    }

    public static Long extractServiceIdentifier(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> msccList =diameterRequest.getAVPList("0:456");

            if(msccList == null|| msccList.isEmpty()) {
                return null;
            }

            for(IDiameterAVP mscc : msccList) {

                for(IDiameterAVP avp: mscc.getGroupedAvp()) {

                    if(avp.getAVPCode() == 439) {
                        String value =avp.getStringValue();
                        if(value != null&& !value.isEmpty()) {
                            return Long.parseLong(value);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract Service Identifier",e);
        }
        return null;
    }

    public static String extractSipMethod(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> sipMethods = diameterRequest.getAVPList("10415:824");

            if(sipMethods == null || sipMethods.isEmpty()) {
                return null;
            }
            
            for(IDiameterAVP avp : sipMethods) {

                String value = avp.getStringValue();

                if(value != null&& !value.isEmpty()) {
                    return value;
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract SIP Method",e);
        }
        return null;
    }

    public static List<String> extractCodecs(DiameterRequest diameterRequest) {
        List<String> codecs =new ArrayList<>();
        try {
            List<IDiameterAVP> codecAvps =diameterRequest.getAVPList("10415:628");

            if(codecAvps == null) {
                return codecs;
            }

            for(IDiameterAVP avp : codecAvps) {

                String value =avp.getStringValue();

                if(value != null&& !value.isEmpty()) {
                    codecs.add(value);
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract Codecs",e);
        }
        return codecs;
    }

    public static List<String> extractFlowStatuses(DiameterRequest diameterRequest) {
        List<String> flowStatuses =new ArrayList<>();
        try {
            List<IDiameterAVP> avps =diameterRequest.getAVPList("10415:511");

            if(avps == null) {
                return flowStatuses;
            }

            for(IDiameterAVP avp : avps) {
                String value = avp.getStringValue();

                if(value != null&& !value.isEmpty()) {
                    flowStatuses.add(value);
                }
            }

        } catch (Exception e) {
            log.error( "Unable to extract Flow Statuses",e);
        }
        return flowStatuses;
    }
    
    public static String getCallingPartyAddress(DiameterRequest diameterRequest) {
        try {
        	String value = getOriginatorAddressData(diameterRequest);
        	if(value !=null) {
        		return value;
        	}
            // 3GPP-Service-Information
            List<IDiameterAVP> serviceInformations = diameterRequest.getAVPList("10415:873");

            if (serviceInformations == null || serviceInformations.isEmpty()) {
                return null;
            }

            for (IDiameterAVP serviceInfo : serviceInformations) {

                // IMS-Information
                List<IDiameterAVP> imsInformationList = serviceInfo.getGroupedAvp();

                if (imsInformationList == null) {
                    continue;
                }

                for (IDiameterAVP imsInfo : imsInformationList) {
                    if (imsInfo.getAVPCode() == 876) {
                        List<IDiameterAVP> imsAvps = imsInfo.getGroupedAvp();

                        if (imsAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP avp : imsAvps) {

                            // Calling-Party-Address
                            if (avp.getAVPCode() == 831) {
                                return avp.getStringValue();
                            }
                        }
                    }
                }
            }
            
            List<IDiameterAVP> smsInfos =diameterRequest.getAVPList("10415:2000");

            if (smsInfos == null) {
                return null;
            }

            for (IDiameterAVP smsInfo : smsInfos) {

                List<IDiameterAVP> childAvps =smsInfo.getGroupedAvp();

                if (childAvps == null) {
                    continue;
                }

                for (IDiameterAVP avp : childAvps) {

                    // Originator-Received-Address
                    if (avp.getAVPCode() == 2027) {

                        List<IDiameterAVP> addrAvps =avp.getGroupedAvp();

                        if (addrAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP addrAvp : addrAvps) {

                            // Address-Data
                            if (addrAvp.getAVPCode() == 897) {
                                return addrAvp.getStringValue();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract Calling-Party-Address", e);
        }

        return null;
    }
    
    public static String getOriginatorAddressData(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> serviceInformations =diameterRequest.getAVPList("10415:873");

            if (serviceInformations == null || serviceInformations.isEmpty()) {
                return null;
            }

            for (IDiameterAVP serviceInfo : serviceInformations) {

                List<IDiameterAVP> serviceAvps = serviceInfo.getGroupedAvp();

                if (serviceAvps == null) {
                    continue;
                }

                for (IDiameterAVP avp : serviceAvps) {

                    // SMS-Information
                    if (avp.getAVPCode() == 2000) {

                        List<IDiameterAVP> smsAvps = avp.getGroupedAvp();

                        if (smsAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP smsAvp : smsAvps) {

                            // Originator-Received-Address
                            if (smsAvp.getAVPCode() == 2027) {

                                List<IDiameterAVP> originatorAvps =smsAvp.getGroupedAvp();

                                if (originatorAvps == null) {
                                    continue;
                                }

                                for (IDiameterAVP originatorAvp : originatorAvps) {

                                    // Address-Data
                                    if (originatorAvp.getAVPCode() == 897) {
                                        return originatorAvp.getStringValue();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract Originator Address Data", e);
        }
        return null;
    }
    
    public static String getCalledPartyAddress(DiameterRequest diameterRequest) {
        try {
        	String value = getRecipientAddressData(diameterRequest);
        	if(value !=null) {
        		return value;
        	}
            // 3GPP-Service-Information
            List<IDiameterAVP> serviceInformations = diameterRequest.getAVPList("10415:873");

            if (serviceInformations == null || serviceInformations.isEmpty()) {
                return null;
            }

            for (IDiameterAVP serviceInfo : serviceInformations) {

                // IMS-Information
                List<IDiameterAVP> imsInformationList = serviceInfo.getGroupedAvp();

                if (imsInformationList == null) {
                    continue;
                }

                for (IDiameterAVP imsInfo : imsInformationList) {
                    if (imsInfo.getAVPCode() == 876) {
                        List<IDiameterAVP> imsAvps = imsInfo.getGroupedAvp();
                        if (imsAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP avp : imsAvps) {
                            // Calling-Party-Address
                            if (avp.getAVPCode() == 832) {
                                return avp.getStringValue();
                            }
                        }
                    }
                }
            }
            
            List<IDiameterAVP> smsInfos =diameterRequest.getAVPList("10415:2000");

            if (smsInfos == null) {
                return null;
            }

            for (IDiameterAVP smsInfo : smsInfos) {
                List<IDiameterAVP> childAvps =smsInfo.getGroupedAvp();

                if (childAvps == null) {
                    continue;
                }

                for (IDiameterAVP avp : childAvps) {

                    // Recipient-Info
                    if (avp.getAVPCode() == 2026) {
                        List<IDiameterAVP> recipientAvps =avp.getGroupedAvp();

                        if (recipientAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP recipientAvp : recipientAvps) {
                            // Address-Data
                            if (recipientAvp.getAVPCode() == 897) {
                                return recipientAvp.getStringValue();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Unable to extract Calling-Party-Address", e);
        }
        return null;
    }
    
    public static String getRecipientAddressData(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> serviceInformations =diameterRequest.getAVPList("10415:873");

            if (serviceInformations == null || serviceInformations.isEmpty()) {
                return null;
            }

            for (IDiameterAVP serviceInfo : serviceInformations) {

                List<IDiameterAVP> serviceAvps = serviceInfo.getGroupedAvp();

                if (serviceAvps == null) {
                    continue;
                }

                for (IDiameterAVP avp : serviceAvps) {

                    // SMS-Information
                    if (avp.getAVPCode() == 2000) {

                        List<IDiameterAVP> smsAvps = avp.getGroupedAvp();

                        if (smsAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP smsAvp : smsAvps) {

                            // Recipient-Info
                            if (smsAvp.getAVPCode() == 2026) {

                                List<IDiameterAVP> recipientAvps =smsAvp.getGroupedAvp();

                                if (recipientAvps == null) {
                                    continue;
                                }

                                for (IDiameterAVP recipientAvp : recipientAvps) {

                                    // Address-Data
                                    if (recipientAvp.getAVPCode() == 897) {
                                        return recipientAvp.getStringValue();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract Recipient Address Data", e);
        }
        return null;
    }
    
    public static String getMediaType(DiameterRequest diameterRequest) {
        try {
            List<IDiameterAVP> serviceInformations =diameterRequest.getAVPList("10415:873");

            if (serviceInformations == null || serviceInformations.isEmpty()) {
                return null;
            }

            for (IDiameterAVP serviceInfo : serviceInformations) {

                List<IDiameterAVP> serviceAvps = serviceInfo.getGroupedAvp();

                if (serviceAvps == null) {
                    continue;
                }

                for (IDiameterAVP avp : serviceAvps) {

                    if (avp.getAVPCode() == 876) {

                        List<IDiameterAVP> smsAvps = avp.getGroupedAvp();

                        if (smsAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP smsAvp : smsAvps) {

                            if (smsAvp.getAVPCode() == 843) {

                                List<IDiameterAVP> originatorAvps =smsAvp.getGroupedAvp();

                                if (originatorAvps == null) {
                                    continue;
                                }

                                for (IDiameterAVP originatorAvp : originatorAvps) {

                                    // Address-Data
                                    if (originatorAvp.getAVPCode() == 844) {
                                        return originatorAvp.getStringValue();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract media Data", e);
        }
        return null;
    }

    /**
     * =========================================================
     * STATUS
     * =========================================================
     */

    private String getStatus(
            Integer resultCode) {

        if(resultCode == null) {
            return "UNKNOWN";
        }

        if(resultCode == 2001) {
            return "SUCCESS";
        }

        return "FAILED";
    }

    private String extractTransactionId(DiameterRequest request) {

        return request.getEnd_to_endIdentifier()
                + "-"
                + request.getHop_by_hopIdentifier();
    }

    private static String getStringAvp(DiameterRequest request,String avpCode) {
        try {
        	IDiameterAVP iDiameterAVP = request.getAVP(avpCode);

            if(iDiameterAVP == null) {
                return null;
            }

            return iDiameterAVP.getStringValue();
        } catch (Exception e) {
            log.error("String AVP extraction failed : {}",avpCode,e);
            return null;
        }
    }
    
    private String getRequestType(DiameterRequest request) {
    	IDiameterAVP requestTypeAvp = request.getAVP("0:416");
		String requestType = null;
		
		if(requestTypeAvp ==null) {
			requestTypeAvp = request.getAVP("10415:533");
		}
			
		if(requestTypeAvp !=null) {
			String strRequestType = requestTypeAvp.getStringValue();
			if(strRequestType != null) {
				if(strRequestType.equalsIgnoreCase("1")) {
					requestType= "INITIAL_REQUEST";
				}else if(strRequestType.equalsIgnoreCase("2")) {
					requestType= "UPDATE_REQUEST";
				}else if(strRequestType.equalsIgnoreCase("3")) {
					requestType= "TERMINATION_REQUEST";
				}else if(strRequestType.equalsIgnoreCase("4")) {
					requestType= "EVENT_REQUEST";
				}
			}
		}
		if(requestType == null) {
			requestTypeAvp = request.getAVP("0:285");
			if(requestTypeAvp !=null) {
				String strRequestType = requestTypeAvp.getStringValue();
				if(strRequestType != null) {
					if(strRequestType.equalsIgnoreCase("1")) {
						requestType= "AUTHORIZE_AUTHENTICATE";
					}else if(strRequestType.equalsIgnoreCase("0")) {
						requestType= "AUTHORIZE_ONLY";
					}
				}
			}
		}
		return requestType;
    }

    private Long getLongAvp(DiameterRequest request,String avpCode) {
        try {
        	IDiameterAVP iDiameterAVP = request.getAVP(avpCode);

            if(iDiameterAVP == null) {
                return null;
            }
            return Long.valueOf(iDiameterAVP.getStringValue());
        } catch (Exception e) {
            log.error("Long AVP extraction failed : {}",avpCode,e);
            return null;
        }
    }

    private Long nvl(Long value) {

        return value == null
                ? 0L
                : value;
    }
    
    private static IDiameterAVP getPsInformationChildAvp(DiameterRequest diameterRequest, int childAvpCode) {
        try {
            List<IDiameterAVP> serviceInformations = diameterRequest.getAVPList("10415:873");

            if (serviceInformations == null || serviceInformations.isEmpty()) {
                return null;
            }

            for (IDiameterAVP serviceInfo : serviceInformations) {

                List<IDiameterAVP> serviceAvps = serviceInfo.getGroupedAvp();
                if (serviceAvps == null) {
                    continue;
                }

                for (IDiameterAVP avp : serviceAvps) {

                    // PS-Information
                    if (avp.getAVPCode() == 874) {

                        List<IDiameterAVP> psAvps = avp.getGroupedAvp();

                        if (psAvps == null) {
                            continue;
                        }

                        for (IDiameterAVP psAvp : psAvps) {

                            if (psAvp.getAVPCode() == childAvpCode) {
                                return psAvp;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unable to extract PS-Information AVP {}", childAvpCode, e);
        }

        return null;
    }
    
    public static String getPdpType(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 3);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract PDP-Type", e);
            return null;
        }
    }
    
    public static String getImsiUnauthenticatedFlag(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 2308);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract IMSI-Unauthenticated-Flag", e);
            return null;
        }
    }
    
    public static String getPdpContextType(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 1247);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract PDP-Context-Type", e);
            return null;
        }
    }
    
    public static String getServingNodeType(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 2047);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract Serving-Node-Type", e);
            return null;
        }
    }
    
    public static String getChargingId(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 2);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract Charging-Id", e);
            return null;
        }
    }
    
    public static String getPdpAddress(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 1227);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract PDP-Address", e);
            return null;
        }
    }
    
    public static String getSgsnAddress(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 1228);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract SGSN-Address", e);
            return null;
        }
    }
    
    public static String getGgsnAddress(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 847);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract GGSN-Address", e);
            return null;
        }
    }
    
    public static String getDynamicAddressFlag(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 2051);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract Dynamic-Address-Flag", e);
            return null;
        }
    }
    
    public static String getImsiMccMnc(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 8);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract IMSI-MCC-MNC", e);
            return null;
        }
    }
    
    public static String getNsapi(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 10);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract NSAPI", e);
            return null;
        }
    }
    
    public static String getChargingCharacteristics(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 13);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract Charging-Characteristics", e);
            return null;
        }
    }
    
    public static String getSgsnMccMnc(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 18);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract SGSN-MCC-MNC", e);
            return null;
        }
    }
    
    public static String getMsTimeZone(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 23);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract MS-TimeZone", e);
            return null;
        }
    }
    
    public static String getUserLocationInfoTime(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 2812);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract User-Location-Info-Time", e);
            return null;
        }
    }
    
    public static String getRatType(DiameterRequest request) {
        try {
            IDiameterAVP avp = getPsInformationChildAvp(request, 21);
            return avp != null ? avp.getStringValue() : null;
        } catch (Exception e) {
            log.error("Unable to extract RAT-Type", e);
            return null;
        }
    }
    
    public static UserLocationInfo extractUserLocationInfo(DiameterRequest diameterRequest) {
        try {
            IDiameterAVP avp = diameterRequest.getAVP("10415:22");
            if(avp != null) {
            	return decode(avp);
            }
            
            List<IDiameterAVP> iDiameterAVPs = diameterRequest.getAVPList("10415:874");
            if(iDiameterAVPs != null && !iDiameterAVPs.isEmpty()) {
            	for(IDiameterAVP iDiameterAVP : iDiameterAVPs) {

            		if (iDiameterAVP.getAVPCode() == 22) {
            			return decode(iDiameterAVP);
            		}
                }
            }
        } catch (Exception e) {
            log.error("Unable to extract 3GPP-User-Location-Info", e);
            return null;
        }
        return null;
    }
    
    public static UserLocationInfo decode(IDiameterAVP avp) {
    	
    	
    	if (avp instanceof AvpUserLocationInfo avpUserLocationInfo) {
    		UserLocationInfo info = new UserLocationInfo();
    		
    		info.setGeoType(avpUserLocationInfo.getKeyStringValue("Location-Type"));
    		info.setMcc(avpUserLocationInfo.getKeyStringValue("TAC-MCC"));
    		info.setMnc(avpUserLocationInfo.getKeyStringValue("TAC-MNC"));
            info.setTac(avpUserLocationInfo.getKeyStringValue("TAC-TAC"));
            info.setEci(avpUserLocationInfo.getKeyStringValue("ECGI-ECI"));
            String eci = avpUserLocationInfo.getKeyStringValue("ECGI-ECI");
            if(eci !=null) {
            	long lEci = Long.valueOf(eci);
            	info.setEnodebId(String.valueOf((int)(lEci >> 8)));
                info.setCellId(String.valueOf((int)(lEci & 0xFF)));
            }
            return info;
    	}
    	return null;
    }
    
}
