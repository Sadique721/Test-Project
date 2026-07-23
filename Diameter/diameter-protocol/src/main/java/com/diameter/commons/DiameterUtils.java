package com.diameter.commons;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diameter.enums.QuotaType;
import com.diameter.enums.ServiceType;
import com.diameter.model.AvpCondition;
import com.diameter.model.CustSmsDetails;
import com.diameter.model.CustVoiceDetails;
import com.diameter.model.Customer;
import com.diameter.model.CustomerPackageRel;
import com.diameter.model.CustomerQuota;
import com.diameter.model.DiameterCacheRequestModel;
import com.diameter.model.MappingDetail;
import com.diameter.model.MappingHeader;
import com.diameter.model.PostpaidPlan;
import com.diameter.model.QOSPolicyGatewayMapping;
import com.diameter.serviceImpl.CustomerServiceImpl;
import com.diameter.serviceImpl.LocalCacheManagerServiceImpl;
import com.diameter.serviceImpl.MappingHeaderServiceImpl;
import com.diameter.serviceImpl.PlanServiceImpl;
import com.diameter.serviceImpl.QOSPolicyServiceImpl;
import com.diameter.stack.DiameterStack;
import com.diameter.util.GenericDiameterProcessor;

public class DiameterUtils {
	
    private static final Pattern SIP_PATTERN = Pattern.compile("sip:([^@]+)@");

	private DiameterUtils() {
		//
	}

	public static String getRequestType(DiameterRequest diameterRequest) {
		IDiameterAVP requestTypeAvp = diameterRequest.getAVP("0:416");
		if (requestTypeAvp != null) {
			String strRequestType = requestTypeAvp.getStringValue();
			if (strRequestType != null) {
				switch (strRequestType) {
				case "1":
					return "INITIAL_REQUEST";
				case "2":
					return "UPDATE_REQUEST";
				case "3":
					return "TERMINATION_REQUEST";
				case "4":
					return "EVENT_REQUEST";
				default:
					return "UNKNOWN_REQUEST";
				}
			}
		}
		return "UNKNOWN_REQUEST";
	}
	
	public static boolean isUserLocationChange(DiameterRequest diameterRequest) {
		IDiameterAVP iDiameterAVP = diameterRequest.getAVP("10415:1006");
		if (iDiameterAVP != null) {
			String strUserLocationChange = iDiameterAVP.getStringValue();
			if (strUserLocationChange != null && "13".equalsIgnoreCase(strUserLocationChange)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getRxRequestType(DiameterRequest diameterRequest) {
		IDiameterAVP requestTypeAvp = diameterRequest.getAVP("10415:533");
		if (requestTypeAvp != null) {
			String strRequestType = requestTypeAvp.getStringValue();
			if (strRequestType != null) {
				switch (strRequestType) {
				case "0":
					return "INITIAL_REQUEST";
				case "1":
					return "UPDATE_REQUEST";
				case "2":
					return "TERMINATION_REQUEST";
				default:
					return "UNKNOWN_REQUEST";
				}
			}
		}
		return "UNKNOWN_REQUEST";
	}

	public static boolean isTerminationRequest(String requestType) {
		return "TERMINATION_REQUEST".equalsIgnoreCase(requestType);
	}
	
	public static boolean isUpdateRequest(String requestType) {
		return "UPDATE_REQUEST".equalsIgnoreCase(requestType);
	}

	public static String getCustomerUsername(DiameterRequest diameterRequest) {
		List<IDiameterAVP> subs = diameterRequest.getAVPList("0:443");

		String imsi = null;
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
					if (type.equalsIgnoreCase("1")) { // END_USER_IMSI
						imsi = value;
					} else if (type.equalsIgnoreCase("0")) { // END_USER_E164
						msisdn = value;
					} else if (type.equalsIgnoreCase("2")) { // END_USER_SIP_URI
						msisdn = extractNumberFromSipUri(value);
					}
				}
			}
		}
		// priority rule
		return imsi != null ? imsi : msisdn;
	}

	public static DiameterAnswer createAnswerFromPacketMapping(DiameterRequest diameterRequest, Map<String, String> valueMap,
			Set<MappingDetail> responseAvp) {
		DiameterAnswer diameterAnswer;
		ArrayList<IDiameterAVP> requestAvps = diameterRequest.getAVPList();
		if (requestAvps != null) {
			extractAvps(requestAvps, "request", valueMap);
		}

		Map<String, IDiameterAVP> avpCache = new HashMap<>();
		diameterAnswer = new DiameterAnswer(diameterRequest);

		// Add AVPs to indicate success
		IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
		resultCodeAvp.setInteger(2001); // DIAMETER_SUCCESS
		diameterAnswer.addAvp(resultCodeAvp);

		Set<String> groupAvpSet = new HashSet<>();
		for (MappingDetail mappingDetail : responseAvp) {
			String key = mappingDetail.getVendorId() + ":" + mappingDetail.getResponseAvp();

			// Value of AVP
			String strValue = null;
			String valueExpression = mappingDetail.getValueExpression();
			if (valueExpression != null) {
				if (valueExpression.startsWith("${")) {
					String cleaned = valueExpression.replaceAll("^\\$\\{", "").replaceAll("\\}$", "");
					strValue = valueMap.get(cleaned);
				} else {
					strValue = valueExpression;
				}
			}

			if (key.contains(".")) {
				String strGroupAvp = key.split("\\.")[0];
				groupAvpSet.add(strGroupAvp.replaceAll("10415:", "0:"));
				addNestedAvp(key.replaceAll("0:", "").replaceAll("10415:", ""), strValue,
						mappingDetail.getValueType().getDbValue(), String.valueOf(mappingDetail.getVendorId()),
						avpCache);
			} else {
				IDiameterAVP iDiameterAVP = DiameterDictionary.getInstance().getAttribute(key);
				if (iDiameterAVP != null) {
					if (strValue != null) {
						iDiameterAVP.setStringValue(strValue);
					}
					diameterAnswer.addAvp(iDiameterAVP);
				}
			}
		}
		for (String strGroupAvp : groupAvpSet) {
			IDiameterAVP iDiameterAVP = avpCache.get(strGroupAvp);
			if (iDiameterAVP != null) {
				iDiameterAVP.refreshAVPHeader();
				diameterAnswer.addAvp(iDiameterAVP);
			}
		}
		return diameterAnswer;
	}

	/**
	 * Converts all Customer fields into String values with keys like
	 * "customer.userName".
	 */
	public static Map<String, String> convertCustomerToValueMap(Customer customer) {
		Map<String, String> valueMap = new HashMap<>();

		if (customer == null)
			return valueMap;

		valueMap.put("customer.userName", String.valueOf(customer.getUserName()));
		valueMap.put("customer.password", String.valueOf(customer.getPassword()));
		valueMap.put("customer.firstName", String.valueOf(customer.getFirstName()));
		valueMap.put("customer.lastName", String.valueOf(customer.getLastName()));
		valueMap.put("customer.email", String.valueOf(customer.getEmail()));
		valueMap.put("customer.cStatus", String.valueOf(customer.getCStatus()));
		valueMap.put("customer.lastLoginTime", String.valueOf(customer.getLastLoginTime()));
		valueMap.put("customer.failCount", String.valueOf(customer.getFailCount()));
		valueMap.put("customer.lastPasswordChange", String.valueOf(customer.getLastPasswordChange()));
		valueMap.put("customer.accountNumber", String.valueOf(customer.getAccountNumber()));
		valueMap.put("customer.accountType", String.valueOf(customer.getAccountType()));
		valueMap.put("customer.birthDate", String.valueOf(customer.getBirthDate()));
		valueMap.put("customer.country", String.valueOf(customer.getCountry()));
		valueMap.put("customer.cui", String.valueOf(customer.getCui()));
		valueMap.put("customer.customerType", String.valueOf(customer.getCustomerType()));
		valueMap.put("customer.gender", String.valueOf(customer.getGender()));
		valueMap.put("customer.imsi", String.valueOf(customer.getImsi()));
		valueMap.put("customer.phone", String.valueOf(customer.getPhone()));
		valueMap.put("customer.subscriberPackage", String.valueOf(customer.getSubscriberPackage()));
		valueMap.put("customer.subscriberPackageId", String.valueOf(customer.getSubscriberPackageId()));
		valueMap.put("customer.createDate", String.valueOf(customer.getCreateDate()));
		valueMap.put("customer.expiryDate", String.valueOf(customer.getExpiryDate()));
		valueMap.put("customer.lastStatusChangeDate", String.valueOf(customer.getLastStatusChangeDate()));
		valueMap.put("customer.nextBillDate", String.valueOf(customer.getNextBillDate()));
		valueMap.put("customer.lastBillDate", String.valueOf(customer.getLastBillDate()));
		valueMap.put("customer.billDay", String.valueOf(customer.getBillDay()));
		valueMap.put("customer.outstandingBalance", String.valueOf(customer.getOutstandingBalance()));

		return valueMap;
	}

	/**
	 * Converts all CustomerQuota fields into String values with keys like
	 * "customerQuota.planId".
	 */
	public static Map<String, String> convertCustomerQuotaToValueMap(CustomerQuota customerQuota,String strUsedTime) {
		Map<String, String> valueMap = new HashMap<>();
		if (customerQuota == null)
			return valueMap;
		
		BigDecimal usedTime = new BigDecimal(0);
		if(strUsedTime != null) {
			usedTime=BigDecimal.valueOf(Long.valueOf(strUsedTime));
		}

		valueMap.put("customerQuota.quotaDtlsId", String.valueOf(customerQuota.getQuotaDtlsId()));
		valueMap.put("customerQuota.custId", String.valueOf(customerQuota.getCustId()));
		valueMap.put("customerQuota.planId", String.valueOf(customerQuota.getPlanId()));
		valueMap.put("customerQuota.quotaType", String.valueOf(customerQuota.getQuotaType()));
		valueMap.put("customerQuota.totalQuota",customerQuota.getTotalQuota() !=null ? String.valueOf(convertQuota(customerQuota.getTotalQuota().doubleValue(), customerQuota.getQuotaUnit())):null);
		valueMap.put("customerQuota.usedQuota",customerQuota.getUsedQuota() !=null ? String.valueOf(convertQuota(customerQuota.getUsedQuota().doubleValue(), customerQuota.getQuotaUnit())):null);
		valueMap.put("customerQuota.quotaUnit", String.valueOf(customerQuota.getQuotaUnit()));
		valueMap.put("customerQuota.timeTotalQuota",customerQuota.getTimeTotalQuota() !=null ? String.valueOf(DiameterUtils.convertTime(customerQuota.getTimeTotalQuota().doubleValue(),customerQuota.getTimeQuotaUnit()))+usedTime:null);
		valueMap.put("customerQuota.timeQuotaUsed", customerQuota.getTimeQuotaUsed() !=null ?String.valueOf(DiameterUtils.convertTime(customerQuota.getTimeQuotaUsed().doubleValue(),customerQuota.getTimeQuotaUnit())):null);
		valueMap.put("customerQuota.timeQuotaUnit", String.valueOf(customerQuota.getTimeQuotaUnit()));
		valueMap.put("customerQuota.createdByStaffId", String.valueOf(customerQuota.getCreatedByStaffId()));
		valueMap.put("customerQuota.createDate", String.valueOf(customerQuota.getCreateDate()));
		valueMap.put("customerQuota.lastModifiedByStaffId", String.valueOf(customerQuota.getLastModifiedByStaffId()));
		valueMap.put("customerQuota.lastModifiedDate", String.valueOf(customerQuota.getLastModifiedDate()));
		valueMap.put("customerQuota.isDeleted", String.valueOf(customerQuota.getIsDeleted()));
		valueMap.put("customerQuota.totalQuotaKb", String.valueOf(customerQuota.getTotalQuotaKb()));
		valueMap.put("customerQuota.usedQuotaKb", String.valueOf(customerQuota.getUsedQuotaKb()));
		valueMap.put("customerQuota.timeUsedQuotaSec", String.valueOf(customerQuota.getTimeUsedQuotaSec()));
		valueMap.put("customerQuota.timeTotalQuotaSec", String.valueOf(customerQuota.getTimeTotalQuotaSec()));
		valueMap.put("customerQuota.custPackageId", String.valueOf(customerQuota.getCustPackageId()));
		valueMap.put("customerQuota.didTotalQuota", String.valueOf(customerQuota.getDidTotalQuota()));
		valueMap.put("customerQuota.didUsedQuota", String.valueOf(customerQuota.getDidUsedQuota()));
		valueMap.put("customerQuota.intercomTotalQuota", String.valueOf(customerQuota.getIntercomTotalQuota()));
		valueMap.put("customerQuota.intercomUsedQuota", String.valueOf(customerQuota.getIntercomUsedQuota()));
		valueMap.put("customerQuota.didQuotaUnit", String.valueOf(customerQuota.getDidQuotaUnit()));
		valueMap.put("customerQuota.intercomQuotaUnit", String.valueOf(customerQuota.getIntercomQuotaUnit()));
		valueMap.put("customerQuota.createByName", String.valueOf(customerQuota.getCreateByName()));
		valueMap.put("customerQuota.updateByName", String.valueOf(customerQuota.getUpdateByName()));
		valueMap.put("customerQuota.speedDowngradeFlag", String.valueOf(customerQuota.getSpeedDowngradeFlag()));
		valueMap.put("customerQuota.isFupApplied", String.valueOf(customerQuota.getIsFupApplied()));
		valueMap.put("customerQuota.fupAppliedDate", String.valueOf(customerQuota.getFupAppliedDate()));
		valueMap.put("customerQuota.currentSessionUsageTime",
				String.valueOf(customerQuota.getCurrentSessionUsageTime()));
		valueMap.put("customerQuota.currentSessionUsageVolume",
				String.valueOf(customerQuota.getCurrentSessionUsageVolume()));
		valueMap.put("customerQuota.parentQuotaType", String.valueOf(customerQuota.getParentQuotaType()));
		valueMap.put("customerQuota.isChunkAvailable", String.valueOf(customerQuota.getIsChunkAvailable()));
		valueMap.put("customerQuota.reservedQuotaInPer", String.valueOf(customerQuota.getReservedQuotaInPer()));
		valueMap.put("customerQuota.totalReservedQuota", String.valueOf(customerQuota.getTotalReservedQuota()));
		valueMap.put("customerQuota.usageQuotaType", String.valueOf(customerQuota.getUsageQuotaType()));
		valueMap.put("customerQuota.skipQuotaUpdate", String.valueOf(customerQuota.getSkipQuotaUpdate()));
		valueMap.put("customerQuota.lastQuotaReset", String.valueOf(customerQuota.getLastQuotaReset()));
		valueMap.put("customerQuota.isQuotaUpdateSkipped", String.valueOf(customerQuota.getIsQuotaUpdateSkipped()));

		return valueMap;
	}

	/**
	 * Converts all PostpaidPlan fields into String values with keys like
	 * "customerPlan.planCode".
	 */
	public static Map<String, String> convertCustomerPlanToValueMap(PostpaidPlan plan) {

		Map<String, String> valueMap = new HashMap<>();

		if (plan == null)
			return valueMap;

		valueMap.put("customerPlan.postPaidPlanId", String.valueOf(plan.getId()));
		valueMap.put("customerPlan.name", String.valueOf(plan.getName()));
		valueMap.put("customerPlan.displayName", String.valueOf(plan.getDisplayName()));
		valueMap.put("customerPlan.planCode", String.valueOf(plan.getCode()));
		valueMap.put("customerPlan.description", String.valueOf(plan.getDesc()));
		valueMap.put("customerPlan.planCategory", String.valueOf(plan.getCategory()));

		valueMap.put("customerPlan.quota",
				String.valueOf(convertQuota(plan.getQuota().doubleValue(), plan.getQuotaUnit())));
		valueMap.put("customerPlan.quotaUnit", String.valueOf(plan.getQuotaUnit()));
		valueMap.put("customerPlan.chunk", String.valueOf(plan.getChunk()));
		valueMap.put("customerPlan.validity", String.valueOf(plan.getValidity()));

		valueMap.put("customerPlan.startDate", String.valueOf(plan.getStartDate()));
		valueMap.put("customerPlan.endDate", String.valueOf(plan.getEndDate()));

		valueMap.put("customerPlan.status", String.valueOf(plan.getStatus()));
		valueMap.put("customerPlan.planStatus", String.valueOf(plan.getPlanStatus()));

		valueMap.put("customerPlan.mvnoId", String.valueOf(plan.getMvnoId()));
		valueMap.put("customerPlan.offerPrice", String.valueOf(plan.getOfferprice()));
		valueMap.put("customerPlan.isDeleted", String.valueOf(plan.getIsDelete()));

		return valueMap;
	}

	/**
	 * Converts all QOSPolicyGatewayMapping fields into String values with keys like
	 * "gatewayMapping.downloadSpeed".
	 */
	public static Map<String, String> convertGatewayMappingToValueMap(QOSPolicyGatewayMapping mapping) {

		Map<String, String> valueMap = new HashMap<>();

		if (mapping == null)
			return valueMap;

		valueMap.put("gatewayMapping.id", String.valueOf(mapping.getId()));
		valueMap.put("gatewayMapping.name", String.valueOf(mapping.getName()));

		valueMap.put("gatewayMapping.downloadSpeed", String.valueOf(mapping.getDownloadSpeed()));

		valueMap.put("gatewayMapping.uploadSpeed", String.valueOf(mapping.getUploadSpeed()));

		valueMap.put("gatewayMapping.baseDownloadSpeed", String.valueOf(mapping.getBaseDownloadSpeed()));

		valueMap.put("gatewayMapping.baseUploadSpeed", String.valueOf(mapping.getBaseUploadSpeed()));

		valueMap.put("gatewayMapping.throttleDownloadSpeed", String.valueOf(mapping.getThrottleDownloadSpeed()));

		valueMap.put("gatewayMapping.throttleUploadSpeed", String.valueOf(mapping.getThrottleUploadSpeed()));

		valueMap.put("gatewayMapping.qosPolicyId", String.valueOf(mapping.getQosPolicyId()));

		return valueMap;
	}
	
	public static Map<String,String> convertSmsDetailsToValueMap(List<CustSmsDetails> smsList){

		Map<String,String> valueMap = new HashMap<>();

		if(smsList == null || smsList.isEmpty()){
			return valueMap;
		}

		CustSmsDetails sms = smsList.get(0);

		valueMap.put("sms.smsType", String.valueOf(sms.getSmsType()));

		valueMap.put("sms.totalSms", String.valueOf(sms.getTotalSms()));

		valueMap.put("sms.usedSms", String.valueOf(sms.getUsedSms()));

		return valueMap;
	}

	public static Map<String,String> convertVoiceDetailsToValueMap(List<CustVoiceDetails> voiceList,long usedSecond){

		Map<String,String> valueMap = new HashMap<>();

		if(voiceList == null || voiceList.isEmpty()){
			return valueMap;
		}

		CustVoiceDetails voice = voiceList.get(0);

		valueMap.put("voice.voiceType", String.valueOf(voice.getVoiceType()));
		
		String strMaxChunk = System.getenv("ENV_MAX_CHUNK_SIZE_SECOND");
	    if (strMaxChunk == null) {
	    	strMaxChunk = "60";
	    }
	    Integer iMaxChunk = Integer.valueOf(strMaxChunk);
		
		long lTotalVoice=DiameterUtils.convertTimeToSecond(voice.getTotalVoice().doubleValue(),voice.getVoiceType());
		long lusedVoice=DiameterUtils.convertTimeToSecond(voice.getUsedVoice().doubleValue(),voice.getVoiceType())+usedSecond;
		long lChunk = lTotalVoice-lusedVoice;
		if(voice.getVoiceType().equalsIgnoreCase("Unlimited")) {
			lChunk=iMaxChunk;
		}else if(lChunk<0) {
			lChunk =0;
		}else if (lChunk >iMaxChunk) {
			lChunk=iMaxChunk;
		}

		if(voice.getTotalVoice() !=null) {
			valueMap.put("voice.totalVoice", String.valueOf(lTotalVoice));
		}

		if(voice.getUsedVoice() !=null) {
			valueMap.put("voice.usedVoice", String.valueOf(lusedVoice));
		}
		valueMap.put("voice.chunk", String.valueOf(lChunk));

		return valueMap;
	}
	
	public static long convertTime(double value, String inputUnit) {
		
		if(inputUnit == null || inputUnit.isEmpty()) {
			inputUnit = "SECOND";
		}

	    String targetUnit = System.getenv("ENV_TIME_UNIT");
	    if (targetUnit == null) {
	        targetUnit = "SECOND";
	    }

	    // Step 1: Convert input to SECONDS
	    double seconds;
	    switch (inputUnit.toUpperCase()) {
	        case "DAY":
	        case "DAYS":
	            seconds = value * 24 * 60 * 60;
	            break;

	        case "HOUR":
	        case "HOURS":
	            seconds = value * 60 * 60;
	            break;

	        case "MINUTE":
	        case "MINUTES":
	            seconds = value * 60;
	            break;

	        case "SECOND":
	        case "SECONDS":
	            seconds = value;
	            break;

	        default:
	            seconds = value;
	    }

	    // Step 2: Convert SECONDS → target unit
	    switch (targetUnit.toUpperCase()) {

	        case "DAY":
	        case "DAYS":
	            return (long) (seconds / (24 * 60 * 60));

	        case "HOUR":
	        case "HOURS":
	            return (long) (seconds / (60 * 60));

	        case "MINUTE":
	        case "MINUTES":
	            return (long) (seconds / 60);

	        case "SECOND":
	        case "SECONDS":
	            return (long) seconds;

	        default:
	            return (long) seconds;
	    }
	}
	
	public static long convertTimeToSecond(double seconds, String inputUnit) {
		if(inputUnit == null || inputUnit.isEmpty()) {
			inputUnit = "SECOND";
		}
		// Step 1: Convert input to SECONDS
	    switch (inputUnit.toUpperCase()) {
	        case "DAY":
	        case "DAYS":
	        	return (long)(seconds * 24 * 60 * 60);

	        case "HOUR":
	        case "HOURS":
	        	return (long)(seconds * 60 * 60);

	        case "MINUTE":
	        case "MINUTES":
	        	return (long)(seconds * 60);

	        case "SECOND":
	        case "SECONDS":
	        	return (long)(seconds);

	        default:
	        	return (long)(seconds);
	    }
	}
	
	public static long convertSecondTimeToUnit(double seconds, String targetUnit) {
		if(targetUnit == null || targetUnit.isEmpty()) {
			targetUnit = "SECOND";
		}
		// Step 2: Convert SECONDS → target unit
	    switch (targetUnit.toUpperCase()) {

	        case "DAY":
	        case "DAYS":
	            return (long) (seconds / (24 * 60 * 60));

	        case "HOUR":
	        case "HOURS":
	            return (long) (seconds / (60 * 60));

	        case "MINUTE":
	        case "MINUTES":
	            return (long) (seconds / 60);

	        case "SECOND":
	        case "SECONDS":
	            return (long) seconds;

	        default:
	            return (long) seconds;
	    }
	}

	public static long convertQuota(double value, String inputUnit) {
		
		if(inputUnit == null || inputUnit.isEmpty()) {
			inputUnit = "BYTE";
		}

		String targetUnit = System.getenv("ENV_QUOTA_UNIT");
		if (targetUnit == null) {
			targetUnit = "BYTES";
		}

		// Step 1: Convert input to BYTES
		double bytes;
		switch (inputUnit.toUpperCase()) {
		case "GB":
			bytes = value * 1024 * 1024 * 1024;
			break;
		case "MB":
			bytes = value * 1024 * 1024;
			break;
		case "KB":
			bytes = value * 1024;
			break;
		case "BYTE":
		case "BYTES":
			bytes = value;
			break;
		default:
			bytes = value;
		}

		// Step 2: Convert BYTES → target unit
		switch (targetUnit) {
		case "GB":
			return (long) (bytes / (1024 * 1024 * 1024));
		case "MB":
			return (long) (bytes / (1024 * 1024));
		case "KB":
			return (long) (bytes / 1024);
		case "BYTE":
		case "BYTES":
			return (long) bytes;
		default:
			return (long) bytes;
		}
	}

	private static void extractAvps(List<IDiameterAVP> avps, String prefix, Map<String, String> valueMap) {
		for (IDiameterAVP avp : avps) {

			String name = DiameterDictionary.getInstance().getAttributeName(avp.getAVPId());

			String key = prefix + "." + name;

			// If grouped → recurse
			if (avp.getGroupedAvp() != null && !avp.getGroupedAvp().isEmpty()) {
				extractAvps(avp.getGroupedAvp(), key, valueMap);
			} else {
				valueMap.put(key, avp.getStringValue());
			}
		}
	}

	public static void addNestedAvp(String responseAvp, String value, String valueType, String vendorId,
			Map<String, IDiameterAVP> avpCache) {

		String[] parts = responseAvp.split("\\.");
		IDiameterAVP parent = null;
		StringBuilder pathKey = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {

			String avpCode = parts[i];
			pathKey.append("0:").append(avpCode);

			String cacheKey = pathKey.toString();
			boolean isLeaf = (i == parts.length - 1);
			
			// Multi-instance support: strip a leading "<n>_" instance prefix (e.g.
			// "1_1003", "2_1003") so a grouped AVP can repeat as siblings under one
			// parent. The prefix stays in the cache key above (keeping instances
			// distinct) but is removed from the dictionary lookup and wire output.
			// Generalises the previous 1_/2_ handling to any instance count; plain
			// segments without the prefix are unchanged (no-op).
			avpCode = avpCode.replaceFirst("^\\d+_", "");

			IDiameterAVP avp = null;

			if (!isLeaf && avpCache.containsKey(cacheKey)) {

				// reuse grouped parent
				avp = avpCache.get(cacheKey);

			} else {
				String tempVendorId = vendorId;
				if (avpCode.equalsIgnoreCase("456")) {
					tempVendorId = "0";
				}
				if (avpCode.equalsIgnoreCase("1001")) {
					tempVendorId = "10415";
				}
				if (avpCode.equalsIgnoreCase("1003")) {
					tempVendorId = "10415";
				}

				// get template and clone
				IDiameterAVP template = DiameterDictionary.getInstance().getAttribute(tempVendorId + ":" + avpCode);

				try {
					avp = (IDiameterAVP) template.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}

				// cache only non-leaf nodes
				if (!isLeaf) {
					avpCache.put(cacheKey, avp);
				}

				// attach to parent
				if (parent != null) {
					ArrayList<IDiameterAVP> children = parent.getGroupedAvp();
					if (children == null) {
						children = new ArrayList<>();
						parent.setGroupedAvp(children);
					}
					children.add(avp);
				}
			}

			parent = avp;
			pathKey.append(".");

			if (isLeaf) {
				setAvpValue(avp, value, valueType);
			}
		}
	}

	private static void setAvpValue(IDiameterAVP avp, String value, String valueType) {
		try {
			if ("String".equalsIgnoreCase(valueType)) {
				avp.setStringValue(value);
			} else if ("Integer".equalsIgnoreCase(valueType)) {
				try {
					double d = Double.parseDouble(value.trim());
					avp.setInteger((int) d);
				} catch (Exception e) {
					avp.setInteger(0); // fallback
				}
			}
		} catch (Exception e) {
			LogManager.getLogger().error("DiameterUtils", "setAvpValue", e);
		}
	}
	
	public static String getSMSServiceUnit(DiameterRequest diameterRequest) {
		IDiameterAVP diameterAVP;
		diameterAVP=diameterRequest.getAVP("10415:873");
		if(diameterAVP !=null && !isMTSms(diameterRequest)) {
			ArrayList<IDiameterAVP> diameterAVPs=diameterAVP.getGroupedAvp();
			if(diameterAVPs !=null) {
				for(IDiameterAVP avp:diameterAVPs) {
					if("2000".equalsIgnoreCase(String.valueOf(avp.getAVPCode()))) {
						ArrayList<IDiameterAVP> listIDiameterAVP = avp.getGroupedAvp();
						if(listIDiameterAVP !=null) {
							for(IDiameterAVP iDiameterAVP:listIDiameterAVP) {
								if("2019".equalsIgnoreCase(String.valueOf(iDiameterAVP.getAVPCode()))) {
									return iDiameterAVP.getStringValue();
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static boolean isVoiceRequest(DiameterRequest diameterRequest) {
		IDiameterAVP diameterAVP=diameterRequest.getAVP("0:461");
		if(diameterAVP !=null) {
			String strValue=diameterAVP.getStringValue();
			if(strValue !=null) {
				if(strValue.toLowerCase().contains("voice") || strValue.toLowerCase().contains("ims")) {
					return true;
				}
			}
		}
		diameterAVP=diameterRequest.getAVP("10415:504");
		if(diameterAVP !=null) {
			String strValue=diameterAVP.getStringValue();
			if(strValue !=null) {
				if(strValue.toLowerCase().contains("voice") || strValue.toLowerCase().contains("ims")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isDataRequest(DiameterRequest diameterRequest) {
		IDiameterAVP diameterAVP=diameterRequest.getAVP("0:461");
		if(diameterAVP !=null) {
			String strValue=diameterAVP.getStringValue();
			if(strValue !=null) {
				if(strValue.toLowerCase().contains("sms") || strValue.toLowerCase().contains("mmtel") || strValue.toLowerCase().contains("voice") || strValue.toLowerCase().contains("ims") || strValue.toLowerCase().contains("sip")) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static String getUsedQuotasForVoice(DiameterRequest diameterRequest) {
		IDiameterAVP diameterAVP;
		diameterAVP=diameterRequest.getAVP("0:456");
		if(diameterAVP !=null && !isMTCall(diameterRequest)) {
			ArrayList<IDiameterAVP> diameterAVPs=diameterAVP.getGroupedAvp();
			if(diameterAVPs !=null) {
				for(IDiameterAVP avp:diameterAVPs) {
					if("446".equalsIgnoreCase(String.valueOf(avp.getAVPCode()))) {
						ArrayList<IDiameterAVP> listIDiameterAVP = avp.getGroupedAvp();
						if(listIDiameterAVP !=null) {
							for(IDiameterAVP iDiameterAVP:listIDiameterAVP) {
								if("420".equalsIgnoreCase(String.valueOf(iDiameterAVP.getAVPCode()))) {
									return iDiameterAVP.getStringValue();
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static boolean matchAvpConditions(List<AvpCondition> conditions, DiameterRequest request) {

	    if (conditions == null || conditions.isEmpty())
	        return true;

	    for (AvpCondition cond : conditions) {

	    	IDiameterAVP avp = request.getAVP(cond.getVendorId()+":"+cond.getAvpCode());

	        if ("EXISTS".equals(cond.getMatchType())) {
	            if (avp == null) return false;
	        }

	        if ("EQUALS".equals(cond.getMatchType())) {
	            if (avp == null || !avp.getStringValue().equals(cond.getExpectedValue()))
	                return false;
	        }
	    }

	    return true;
	}
	
	public static String extractNumberFromSipUri(String sipUri) {
        if (sipUri == null || sipUri.isBlank()) {
            throw new IllegalArgumentException("SIP URI must not be null or blank");
        }
        Matcher matcher = SIP_PATTERN.matcher(sipUri.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid SIP URI format: " + sipUri);
    }
	
	public static ServiceType getServiceType(DiameterRequest request) {
	    IDiameterAVP sciAvp = request.getAVP("0:461");

	    String sci = sciAvp != null
	            ? sciAvp.getStringValue().toLowerCase()
	            : "";

	    // SMS
	    if (sci.contains("sms")) {
	        return ServiceType.SMS;
	    }

	    // Voice / IMS
	    if (sci.contains("voice")
	            || sci.contains("ims")
	            || sci.contains("mmtel")) {
	        return ServiceType.VOICE;
	    }

	    // IMS Information check
	    IDiameterAVP serviceInfo = request.getAVP("10415:873");

	    if(serviceInfo != null) {
	        for (IDiameterAVP avp : serviceInfo.getGroupedAvp()) {

	            // IMS-Information
	            if (avp.getAVPCode() == 876) {
	                return ServiceType.VOICE;
	            }
	        }
	    }
	    return ServiceType.DATA;
	}
	
	public static QuotaType getQuotaType(DiameterRequest request) {
		IDiameterAVP mscc = request.getAVP("0:456");

		if (mscc != null) {
			for (IDiameterAVP avp : mscc.getGroupedAvp()) {

				// Used-Service-Unit
				if (avp.getAVPCode() == 446) {
					for (IDiameterAVP child : avp.getGroupedAvp()) {

						// CC-Time
						if (child.getAVPCode() == 420) {
							return QuotaType.TIME;
						}

						// Total/Input/Output Octets
						if (child.getAVPCode() == 421 || child.getAVPCode() == 412 || child.getAVPCode() == 414) {
							return QuotaType.OCTET;
						}

						// Service-Specific-Units
						if (child.getAVPCode() == 417) {
							return QuotaType.SSU;
						}
					}
				}
			}
		}
		return QuotaType.UNKNOWN;
	}
	
	public static String getUsedTimeQuota(DiameterRequest diameterRequest) {
		IDiameterAVP diameterAVP;
		diameterAVP=diameterRequest.getAVP("0:456");
		if(diameterAVP !=null) {
			ArrayList<IDiameterAVP> diameterAVPs=diameterAVP.getGroupedAvp();
			if(diameterAVPs !=null) {
				for(IDiameterAVP avp:diameterAVPs) {
					if("446".equalsIgnoreCase(String.valueOf(avp.getAVPCode()))) {
						ArrayList<IDiameterAVP> listIDiameterAVP = avp.getGroupedAvp();
						if(listIDiameterAVP !=null) {
							for(IDiameterAVP iDiameterAVP:listIDiameterAVP) {
								if("420".equalsIgnoreCase(String.valueOf(iDiameterAVP.getAVPCode()))) {
									return iDiameterAVP.getStringValue();
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static boolean isUnlimited(String strInput) {
		if(strInput == null || strInput.isEmpty()) {
			return false;
		}
		if(strInput.toLowerCase().equalsIgnoreCase("unlimited")) {
			return true;
		}
		return false;
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
            e.printStackTrace();
        }
        return false;
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

                        if(value != null&& !value.trim().isEmpty()) {
                        	return Long.valueOf(value.trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
        	 e.printStackTrace();
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
                        if(value != null&& !value.trim().isEmpty()) {
                            return Long.parseLong(value.trim());
                        }
                    }
                }
            }

        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
	
	private static String getStringAvp(DiameterRequest request,String avpCode) {
        try {
        	IDiameterAVP iDiameterAVP = request.getAVP(avpCode);

            if(iDiameterAVP == null) {
                return null;
            }

            return iDiameterAVP.getStringValue();
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
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
        	e.printStackTrace();
        }
        return null;
    }
	
	public static void getVoiceQuotaExhaustedAnswer(DiameterAnswer diameterAnswer) {
		IDiameterAVP resultCode =DiameterDictionary.getInstance().getAttribute("0:268");

		resultCode.setInteger(4012);

		diameterAnswer.addAvp(resultCode);


		IDiameterAVP mscc =DiameterDictionary.getInstance().getAttribute("0:456");

		ArrayList<IDiameterAVP> msccList =new ArrayList<>();

		IDiameterAVP gsu =DiameterDictionary.getInstance().getAttribute("0:431");

		ArrayList<IDiameterAVP> gsuList =new ArrayList<>();

		IDiameterAVP ccTime =DiameterDictionary.getInstance().getAttribute("0:420");

		ccTime.setInteger(0);

		gsuList.add(ccTime);

		gsu.setGroupedAvp(gsuList);

		msccList.add(gsu);

		// ==================================================
		// Final-Unit-Indication
		// ==================================================

		IDiameterAVP fui =DiameterDictionary.getInstance().getAttribute("0:430");

		ArrayList<IDiameterAVP> fuiList =
		        new ArrayList<>();

		IDiameterAVP action =DiameterDictionary.getInstance().getAttribute("0:449");

		// TERMINATE
		action.setInteger(0);

		fuiList.add(action);

		fui.setGroupedAvp(fuiList);

		msccList.add(fui);

		mscc.setGroupedAvp(msccList);

		diameterAnswer.addAvp(mscc);
	}
	
	public static boolean isMTCall(DiameterRequest diameterRequest) {

	    // Service-Information
	    IDiameterAVP serviceInfo = diameterRequest.getAVP("10415:873");

	    if (serviceInfo == null) {
	        return false;
	    }

	    for (IDiameterAVP avp : serviceInfo.getGroupedAvp()) {
	        // IMS-Information
	        if (avp.getAVPCode() == 876) {

	            for (IDiameterAVP imsAvp
	                    : avp.getGroupedAvp()) {

	                // Role-Of-Node
	                if (imsAvp.getAVPCode() == 829) {
	                    String role = imsAvp.getStringValue();
	                    return "1".equals(role);
	                }
	            }
	        }
	    }
	    return false;
	}
	
	public static boolean isMTSms(DiameterRequest diameterRequest) {
	    IDiameterAVP serviceInfo =diameterRequest.getAVP("10415:873");

	    if (serviceInfo == null) {
	        return false;
	    }

	    for (IDiameterAVP avp : serviceInfo.getGroupedAvp()) {

	        if (avp.getAVPCode() == 2000) {

	            for (IDiameterAVP smsAvp: avp.getGroupedAvp()) {

	                if (smsAvp.getAVPCode() == 2007) {

	                    return "1".equals(smsAvp.getStringValue()); // DELIVERY
	                }
	            }
	        }
	    }

	    return false;
	}
	
	public static int getGraceDays(DiameterRequest diameterRequest) {
		if(DiameterUtils.isMTCall(diameterRequest) || DiameterUtils.isMTSms(diameterRequest)) {
			String graceDays = System.getenv("ENV_GRACE_PERIOD");
			if (graceDays == null) {
				graceDays = "30";
			}
			try {
				return Integer.parseInt(graceDays.trim());
			} catch (NumberFormatException e) {
				LogManager.getLogger().warn("DiameterUtils", "Invalid ENV_GRACE_PERIOD value: " + graceDays + ", using 30");
				return 30;
			}
		}
		return 30;
	}
	
	public static boolean isGracePeriodExpired(DiameterRequest diameterRequest, CustomerPackageRel custPkgRel) {
		boolean bGracePeriodExpired = true;
		if(DiameterUtils.isMTCall(diameterRequest) || DiameterUtils.isMTSms(diameterRequest)) {
			bGracePeriodExpired = false;
			int iGraceDays=DiameterUtils.getGraceDays(diameterRequest);
			Calendar calendar =Calendar.getInstance();

		    calendar.setTime(custPkgRel.getEndDate());

		    calendar.add(Calendar.DAY_OF_MONTH,iGraceDays);

		    Timestamp graceExpiryDate = new Timestamp(  calendar.getTimeInMillis());
		    if (graceExpiryDate.before( new Timestamp(System.currentTimeMillis()))) {
		    	bGracePeriodExpired = true;
		    }
			
		}
		return bGracePeriodExpired;
	}
	
	public static void sendRAR(BigDecimal customerId,LocalCacheManagerServiceImpl cacheManagerServiceImpl,DiameterStack stackContext,GenericDiameterProcessor genericDiameterProcessor,MappingHeaderServiceImpl mappingHeaderServiceImpl,CustomerServiceImpl customerServiceImpl,PlanServiceImpl planServiceImpl,QOSPolicyServiceImpl qosPolicyServiceImpl) {
		LogManager.getLogger().info("DiameterUtils", "Sending RAR for customer: " + customerId);
		
		try {
			if(customerId !=null) {
	    		DiameterCacheRequestModel  diameterCacheRequestModel= cacheManagerServiceImpl.getValue("GY-SESSION_ID"+customerId, DiameterCacheRequestModel.class);
	    		if(diameterCacheRequestModel ==null) {
	    			diameterCacheRequestModel= cacheManagerServiceImpl.getValue("LAST-GY-SESSION_ID", DiameterCacheRequestModel.class);
	    		}
	    		if(diameterCacheRequestModel !=null) {
	    			
	    			//Get Packet Mapping
	    			List<MappingHeader> mappingHeaders=mappingHeaderServiceImpl.getMappingsByRequestAndResponseType("Re-Auth-Request", "Re-Auth-Answer","GX",10415,null);
	    			
	    			Map<String, MappingDetail> requestAvp = new HashMap<>();
	    			Set<MappingDetail> responseAvp = new HashSet<>();
	    			if (mappingHeaders != null && !mappingHeaders.isEmpty()) {
	    				for (MappingHeader mappingHeader : mappingHeaders) {
	    					if (mappingHeader.getApplication().equalsIgnoreCase("GX")) {
	    						List<MappingDetail> details = mappingHeader.getDetails();
	    						if (details != null) {
	    							for (MappingDetail mappingDetail : details) {
	    								if (mappingDetail.getRequestAvp() != null) {
	    									requestAvp.put(mappingDetail.getVendorId() + ":" + mappingDetail.getRequestAvp(),
	    											mappingDetail);
	    								}
	    								if (mappingDetail.getResponseAvp() != null) {
	    									responseAvp.add(mappingDetail);
	    								}
	    							}
	    						}
	    					}
	    				}
	    			}
	    			
	    			List<Customer> customers = null;
					CustomerPackageRel custPkgRel = null;
					Customer customer = null;
					CustomerQuota customerQuota= null;
					Map<String, String> valueMap = new HashMap<>();
					Map<String, IDiameterAVP> avpCache = new HashMap<>();
					
					if (customerId != null) {
						// Get Customer from Database
						customers = customerServiceImpl.getCustomers(String.valueOf(customerId), null, null);
					}
					
					if (customers != null && !customers.isEmpty() && customers.get(0).getQuotas()!=null && !customers.get(0).getQuotas().isEmpty()) {
						customer = customers.get(0);

						// Convert all customer fields into String and add to valueMap
						valueMap.putAll(DiameterUtils.convertCustomerToValueMap(customer));
						
						customerQuota = customer.getQuotas().get(0);

						valueMap.putAll(
								DiameterUtils.convertCustomerQuotaToValueMap(customerQuota, null));

						// ============================
						// 👉 FETCH PLAN USING SERVICE
						// ============================
						List<PostpaidPlan> plans = planServiceImpl.searchPlans(customerQuota.getPlanId(), // planId
								null, // name
								null, // planType
								null, // price
								null, // status
								null, // planStatus
								null, // quotaUnit
								null, // downloadSpeed
								null, // uploadSpeed
								null, // startDate
								null, // endDate
								null, // quota
								null, // validity
								null // chunk
						);

						if (plans != null && !plans.isEmpty()) {
							PostpaidPlan plan = plans.get(0);
							valueMap.putAll(DiameterUtils.convertCustomerPlanToValueMap(plan));
						}

						List<CustomerPackageRel> relList = customerServiceImpl.getCustomerPackageRel(customer.getCustId(),
								customerQuota.getPlanId(), BigInteger.valueOf(customerQuota.getCustPackageId()));

						custPkgRel = (relList != null && !relList.isEmpty()) ? relList.get(0) : null;
						
						//SMS
						List<CustSmsDetails> relCustSmsDetails =
								customerServiceImpl.getCustomerSmsPackageRel(
										customer.getCustId(),
										customerQuota.getPlanId(),
										Long.valueOf(customerQuota.getCustPackageId())
								);
						
						//voice
						List<CustVoiceDetails> relVoiceDetails =
								customerServiceImpl.getCustomerVoicePackageRel(
										customer.getCustId(),
										customerQuota.getPlanId(),
										Long.valueOf(customerQuota.getCustPackageId())
								);
						
						valueMap.putAll(
								DiameterUtils.convertSmsDetailsToValueMap(relCustSmsDetails)
						);
						
						valueMap.putAll(
								DiameterUtils.convertVoiceDetailsToValueMap(relVoiceDetails,0)
						);
						
						// ============================
						// FETCH QOS GATEWAY MAPPING
						// ============================
						if (custPkgRel != null && custPkgRel.getQosPolicyId() != null) {
							
							valueMap.put("customerPackage.startDate", custPkgRel.getStartDate()!=null ? String.valueOf(custPkgRel.getStartDate().toLocalDateTime()
							        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))):null);
							valueMap.put("customerPackage.expiryDate", custPkgRel.getExpiryDate() !=null ? String.valueOf(custPkgRel.getExpiryDate().toLocalDateTime()
							        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))):null);

							List<QOSPolicyGatewayMapping> gatewayMappings = qosPolicyServiceImpl
									.getGatewayMappingByQosPolicyId(String.valueOf(custPkgRel.getQosPolicyId()));

							if (gatewayMappings != null && !gatewayMappings.isEmpty()) {
								QOSPolicyGatewayMapping mapping = gatewayMappings.get(0);
								valueMap.putAll(DiameterUtils.convertGatewayMappingToValueMap(mapping));
							}
						}
					}
	    			
	    			String strSessionId = diameterCacheRequestModel.getSessionId();
	    			String requestingHost = diameterCacheRequestModel.getRequestingHost();
	    			String requestingRealm = diameterCacheRequestModel.getRequestingRealm();
	    			
	    			// Create RAR request (Command Code 258)
	    	    	DiameterRequest rar = new DiameterRequest(true);
	    	    	
	    	    	Set<String> groupAvpSet = new HashSet<>();
	    			for (MappingDetail mappingDetail : responseAvp) {
	    				String key = mappingDetail.getVendorId() + ":" + mappingDetail.getResponseAvp();

	    				// Value of AVP
	    				String strValue = null;
	    				String valueExpression = mappingDetail.getValueExpression();
	    				if (valueExpression != null) {
	    					if (valueExpression.startsWith("${")) {
	    						String cleaned = valueExpression.replaceAll("^\\$\\{", "").replaceAll("\\}$", "");
	    						strValue = valueMap.get(cleaned);
	    					} else {
	    						strValue = valueExpression;
	    					}
	    				}

	    				if (key.contains(".")) {
	    					String strGroupAvp = key.split("\\.")[0];
	    					groupAvpSet.add(strGroupAvp.replaceAll("10415:", "0:"));
	    					addNestedAvp(key.replaceAll("0:", "").replaceAll("10415:", ""), strValue,
	    							mappingDetail.getValueType().getDbValue(), String.valueOf(mappingDetail.getVendorId()),
	    							avpCache);
	    				} else {
	    					IDiameterAVP iDiameterAVP = DiameterDictionary.getInstance().getAttribute(key);
	    					if (iDiameterAVP != null) {
	    						if (strValue != null) {
	    							iDiameterAVP.setStringValue(strValue);
	    						}
	    						rar.addAvp(iDiameterAVP);
	    					}
	    				}
	    			}
	    			for (String strGroupAvp : groupAvpSet) {
	    				IDiameterAVP iDiameterAVP = avpCache.get(strGroupAvp);
	    				if (iDiameterAVP != null) {
	    					iDiameterAVP.refreshAVPHeader();
	    					rar.addAvp(iDiameterAVP);
	    				}
	    			}
	    	    	
	    	    	rar.setCommandCode(258);
	    	    	rar.setApplicationID(4);
	    	    	
	    	    	// Session-Id
	    	        IDiameterAVP sessionIdAvp = DiameterDictionary.getInstance().getAttribute("0:263");
	    	        sessionIdAvp.setStringValue(strSessionId);
	    	        rar.addAvp(sessionIdAvp);

	    	        // Destination-Host
	    	        IDiameterAVP destHost = DiameterDictionary.getInstance().getAttribute("0:293");
	    	        destHost.setStringValue(requestingHost);
	    	        rar.addAvp(destHost);

	    	        // Destination-Realm
	    	        IDiameterAVP destRealm = DiameterDictionary.getInstance().getAttribute("0:283");
	    	        destRealm.setStringValue(requestingRealm);
	    	        rar.addAvp(destRealm);
	    	        
	    	        DiameterSession diameterSession = (DiameterSession)stackContext.getStackContext().getOrCreateSession(strSessionId,4);
	    	        
	    	        // Send RAR request
	    	        stackContext.getStackContext().getPeerCommunicator(requestingHost).sendServerInitiatedRequest(
	    	        		diameterSession,
	    	                rar,
	    	                new ResponseListener() {

	    						@Override
	    						public void requestTimedout(String paramString, DiameterSession paramDiameterSession) {
	    							LogManager.getLogger().error("DiameterUtils", "RAA timeout");
	    						}

	    						@Override
	    						public void responseReceived(DiameterAnswer paramDiameterAnswer, String paramString,
	    								DiameterSession paramDiameterSession) {
	    							LogManager.getLogger().info("DiameterUtils", "Received RAA: " + paramDiameterAnswer);
	    						}
	    	                }
	    	        );

	    	        //Store in Audit and CDR
	                genericDiameterProcessor.process(rar, null,valueMap);
	                
	    	        LogManager.getLogger().info("DiameterUtils", "RAR sent successfully");
	    			
	    		}else {
	    			LogManager.getLogger().error("DiameterUtils", "Session is not available for customer : "+customerId);
	    		}
	    	}
			
		}catch (CommunicationException e) {
			LogManager.getLogger().error("DiameterUtils", "CommunicationException : ", e);
		}
	}
	
	public static String getFailureReason(long resultCode) {

        switch ((int) resultCode) {
            case 2001:
                return "DIAMETER_SUCCESS";

            case 3002:
                return "DIAMETER_UNABLE_TO_DELIVER";

            case 4001:
                return "DIAMETER_AUTHENTICATION_REJECTED";

            case 4010:
                return "DIAMETER_END_USER_SERVICE_DENIED";

            case 4012:
                return "DIAMETER_CREDIT_LIMIT_REACHED";

            case 5001:
                return "DIAMETER_AVP_UNSUPPORTED";

            case 5004:
                return "DIAMETER_INVALID_AVP_VALUE";

            case 5005:
                return "DIAMETER_ERROR_USER_UNKNOWN";
                
            case 5032:
                return "DIAMETER_ERROR_USER_UNKNOWN";

            default:
                return "Unknown Result Code: " + resultCode;
        }
    }
}
