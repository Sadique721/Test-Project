package com.diameter.util;

import com.diameter.model.DiameterAudit;

public class CsvFormatter {
	
	public static final String HEADER = "TRANSACTION_ID," + "SESSION_ID," + "PROTOCOL," + "COMMAND_CODE,"
			+ "APPLICATION_ID," + "REQUEST_TYPE," + "SERVICE_TYPE," + "MSISDN," + "IMSI," + "IMEI," + "APN,"
			+ "ORIGIN_HOST," + "ORIGIN_REALM," + "DESTINATION_HOST," + "DESTINATION_REALM," + "RESULT_CODE,"
			+ "RESULT_DESCRIPTION," + "STATUS," + "ERROR_MESSAGE," + "PROCESSING_TIME_MS," + "REQUEST_PAYLOAD,"
			+ "RESPONSE_PAYLOAD," + "PEER_NAME," + "POD_NAME," + "CC_REQUEST_NUMBER," + "SUBSCRIPTION_ID,"
			+ "FRAMED_IP_ADDRESS," + "FRAMED_IPV6_PREFIX," + "CALLED_STATION_ID," + "THREE_GPP_RAT_TYPE,"
			+ "QOS_INFORMATION," + "BEARER_IDENTIFIER," + "IP_CAN_TYPE," + "AN_GW_ADDRESS," + "THREE_GPP_SGSN_ADDRESS,"
			+ "USER_NAME," + "ORIGIN_STATE_ID," + "USER_EQUIPMENT_INFO," + "CC_SUB_SESSION_ID,"
			+ "TFT_PACKET_FILTER_INFORMATION," + "CHARGING_RULE_INSTALL," + "CHARGING_RULE_REMOVE,"
			+ "DEFAULT_EPS_BEARER_QOS," + "SUPPORTED_FEATURES," + "EVENT_TRIGGER," + "USAGE_MONITORING_INFORMATION,"
			+ "CHARGING_RULE_REPORT," + "THREE_GPP_USER_LOCATION_INFO," + "TERMINATION_CAUSE," + "CREATED_AT,"
			+ "PDP_TYPE," + "IMSI_UNAUTHENTICATED_FLAG," + "PDP_CONTEXT_TYPE," + "SERVING_NODE_TYPE," + "CHARGING_ID,"
			+ "PDP_ADDRESS," + "GGSN_ADDRESS," + "DYNAMIC_ADDRESS_FLAG," + "IMSI_MCC_MNC," + "NSAPI,"
			+ "CHARGING_CHARACTERISTICS," + "SGSN_MCC_MNC," + "MS_TIME_ZONE," + "USER_LOCATION_INFO_TIME"
			+ "GEO_TYPE,"+ "MCC,"+ "MNC,"+ "TAC,"
			+ "ECI,"+ "ENODEB_ID,"+ "CELL_ID,"
			;

	public String format(DiameterAudit audit) {
		
		StringBuilder sb = new StringBuilder(8192);
		
		sb.append(CsvUtils.escape(audit.getTransactionId())).append(',');
		sb.append(CsvUtils.escape(audit.getSessionId())).append(',');
		sb.append(CsvUtils.escape(audit.getProtocol())).append(',');
		sb.append(CsvUtils.escape(audit.getCommandCode())).append(',');
		sb.append(CsvUtils.escape(audit.getApplicationId())).append(',');
		sb.append(CsvUtils.escape(audit.getRequestType())).append(',');
		sb.append(CsvUtils.escape(audit.getServiceType())).append(',');
		sb.append(CsvUtils.escape(audit.getMsisdn())).append(',');
		sb.append(CsvUtils.escape(audit.getImsi())).append(',');
		sb.append(CsvUtils.escape(audit.getImei())).append(',');
		sb.append(CsvUtils.escape(audit.getApn())).append(',');
		sb.append(CsvUtils.escape(audit.getOriginHost())).append(',');
		sb.append(CsvUtils.escape(audit.getOriginRealm())).append(',');
		sb.append(CsvUtils.escape(audit.getDestinationHost())).append(',');
		sb.append(CsvUtils.escape(audit.getDestinationRealm())).append(',');
		sb.append(CsvUtils.escape(audit.getResultCode())).append(',');
		sb.append(CsvUtils.escape(audit.getResultDescription())).append(',');
		sb.append(CsvUtils.escape(audit.getStatus())).append(',');
		sb.append(CsvUtils.escape(audit.getErrorMessage())).append(',');
		sb.append(CsvUtils.escape(audit.getProcessingTimeMs())).append(',');
		sb.append(CsvUtils.escape(audit.getRequestPayload())).append(',');
		sb.append(CsvUtils.escape(audit.getResponsePayload())).append(',');
		sb.append(CsvUtils.escape(audit.getPeerName())).append(',');
		sb.append(CsvUtils.escape(audit.getPodName())).append(',');
		sb.append(CsvUtils.escape(audit.getCcRequestNumber())).append(',');
		sb.append(CsvUtils.escape(audit.getSubscriptionId())).append(',');
		sb.append(CsvUtils.escape(audit.getFramedIpAddress())).append(',');
		sb.append(CsvUtils.escape(audit.getFramedIpv6Prefix())).append(',');
		sb.append(CsvUtils.escape(audit.getCalledStationId())).append(',');
		sb.append(CsvUtils.escape(audit.getThreeGppRatType())).append(',');
		sb.append(CsvUtils.escape(audit.getQosInformation())).append(',');
		sb.append(CsvUtils.escape(audit.getBearerIdentifier())).append(',');
		sb.append(CsvUtils.escape(audit.getIpCanType())).append(',');
		sb.append(CsvUtils.escape(audit.getAnGwAddress())).append(',');
		sb.append(CsvUtils.escape(audit.getThreeGppSgsnAddress())).append(',');
		sb.append(CsvUtils.escape(audit.getUserName())).append(',');
		sb.append(CsvUtils.escape(audit.getOriginStateId())).append(',');
		sb.append(CsvUtils.escape(audit.getUserEquipmentInfo())).append(',');
		sb.append(CsvUtils.escape(audit.getCcSubSessionId())).append(',');
		sb.append(CsvUtils.escape(audit.getTftPacketFilterInformation())).append(',');
		sb.append(CsvUtils.escape(audit.getChargingRuleInstall())).append(',');
		sb.append(CsvUtils.escape(audit.getChargingRuleRemove())).append(',');
		sb.append(CsvUtils.escape(audit.getDefaultEpsBearerQos())).append(',');
		sb.append(CsvUtils.escape(audit.getSupportedFeatures())).append(',');
		sb.append(CsvUtils.escape(audit.getEventTrigger())).append(',');
		sb.append(CsvUtils.escape(audit.getUsageMonitoringInformation())).append(',');
		sb.append(CsvUtils.escape(audit.getChargingRuleReport())).append(',');
		sb.append(CsvUtils.escape(audit.getThreeGppUserLocationInfo())).append(',');
		sb.append(CsvUtils.escape(audit.getTerminationCause())).append(',');
		sb.append(CsvUtils.escape(audit.getCreatedAt())).append(',');
		sb.append(CsvUtils.escape(audit.getPdpType())).append(',');
		sb.append(CsvUtils.escape(audit.getImsiUnauthenticatedFlag())).append(',');
		sb.append(CsvUtils.escape(audit.getPdpContextType())).append(',');
		sb.append(CsvUtils.escape(audit.getServingNodeType())).append(',');
		sb.append(CsvUtils.escape(audit.getChargingId())).append(',');
		sb.append(CsvUtils.escape(audit.getPdpAddress())).append(',');
		sb.append(CsvUtils.escape(audit.getGgsnAddress())).append(',');
		sb.append(CsvUtils.escape(audit.getDynamicAddressFlag())).append(',');
		sb.append(CsvUtils.escape(audit.getImsiMccMnc())).append(',');
		sb.append(CsvUtils.escape(audit.getNsapi())).append(',');
		sb.append(CsvUtils.escape(audit.getChargingCharacteristics())).append(',');
		sb.append(CsvUtils.escape(audit.getSgsnMccMnc())).append(',');
		sb.append(CsvUtils.escape(audit.getMsTimeZone())).append(',');
		sb.append(CsvUtils.escape(audit.getUserLocationInfoTime()));
		sb.append(CsvUtils.escape(audit.getUserLocationInfo() !=null ? audit.getUserLocationInfo().getGeoType() : null)).append(',');
		sb.append(CsvUtils.escape(audit.getUserLocationInfo() !=null ? audit.getUserLocationInfo().getMcc() : null)).append(',');
		sb.append(CsvUtils.escape(audit.getUserLocationInfo() !=null ? audit.getUserLocationInfo().getMnc() : null)).append(',');
		sb.append(CsvUtils.escape(audit.getUserLocationInfo() !=null ? audit.getUserLocationInfo().getTac() : null)).append(',');
		sb.append(CsvUtils.escape(audit.getUserLocationInfo() !=null ? audit.getUserLocationInfo().getEci() : null)).append(',');
		sb.append(CsvUtils.escape(audit.getUserLocationInfo() !=null ? audit.getUserLocationInfo().getEnodebId() : null)).append(',');
		sb.append(CsvUtils.escape(audit.getUserLocationInfo() !=null ? audit.getUserLocationInfo().getCellId() : null)).append(',');
		sb.append('\n');
		return sb.toString();
	}
}