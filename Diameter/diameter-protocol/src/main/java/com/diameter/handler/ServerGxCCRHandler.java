package com.diameter.handler;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.diameter.commons.Application;
import com.diameter.commons.ApplicationEnum;
import com.diameter.commons.ApplicationListener;
import com.diameter.commons.CommunicationException;
import com.diameter.commons.DiameterAnswer;
import com.diameter.commons.DiameterDictionary;
import com.diameter.commons.DiameterRequest;
import com.diameter.commons.DiameterUtils;
import com.diameter.commons.IDiameterAVP;
import com.diameter.commons.IStackContext;
import com.diameter.commons.LogManager;
import com.diameter.commons.Session;
import com.diameter.commons.SessionReleaseIndiactor;
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
import com.diameter.util.GenericDiameterProcessor;

/**
 * Gx Server Handler - Handles CCR from PCEF (PGW/UPF)
 * and responds with policy rules (CCA).
 */
public class ServerGxCCRHandler extends ApplicationListener {

    private static final org.slf4j.Logger METHOD_LOG = org.slf4j.LoggerFactory.getLogger(ServerGxCCRHandler.class);

    private IStackContext stackContext;
    private MappingHeaderServiceImpl mappingHeaderServiceImpl;
    private CustomerServiceImpl customerServiceImpl;
    private PlanServiceImpl planServiceImpl;
    private QOSPolicyServiceImpl qosPolicyServiceImpl;
    private LocalCacheManagerServiceImpl cacheManagerServiceImpl;
    private GenericDiameterProcessor genericDiameterProcessor;

    public ServerGxCCRHandler(IStackContext stackContext, ApplicationEnum[] applicationEnums) {
        super(stackContext, applicationEnums);
        this.stackContext = stackContext;
        
    }
    
    public GenericDiameterProcessor getGenericDiameterProcessor() {
		return genericDiameterProcessor;
	}

	public void setGenericDiameterProcessor(GenericDiameterProcessor genericDiameterProcessor) {
		this.genericDiameterProcessor = genericDiameterProcessor;
	}
    
    public void setMappingHeaderServiceImpl(MappingHeaderServiceImpl mappingHeaderServiceImpl) {
		this.mappingHeaderServiceImpl=mappingHeaderServiceImpl;
	}
    
    public void setCustomerServiceImpl(CustomerServiceImpl customerServiceImpl) {
		this.customerServiceImpl=customerServiceImpl;
	}
    
    public void setPlanServiceImpl(PlanServiceImpl planServiceImpl) {
		this.planServiceImpl = planServiceImpl;
	}
    
    public void setQOSPolicyServiceImpl(QOSPolicyServiceImpl qosPolicyServiceImpl) {
		this.qosPolicyServiceImpl=qosPolicyServiceImpl;
	}
    
    public LocalCacheManagerServiceImpl getCacheManagerServiceImpl() {
		return cacheManagerServiceImpl;
	}

	public void setCacheManagerServiceImpl(LocalCacheManagerServiceImpl cacheManagerServiceImpl) {
		this.cacheManagerServiceImpl = cacheManagerServiceImpl;
	}

    @Override
    public String getApplicationIdentifier() {
        return Application.TGPP_GX_29_212_18.name();
    }

	@Override
	protected void processApplicationRequest(Session session, DiameterRequest diameterRequest) {
		long __mStart = System.currentTimeMillis();
		if (METHOD_LOG.isDebugEnabled()) {
			METHOD_LOG.debug(">> ENTRY processApplicationRequest sessionId={}", (session != null ? session.getSessionId() : "null"));
		}
		try {
		LogManager.getLogger().info("ServerGxCCRHandler", "Received CCR (Gx) from: " + diameterRequest.getRequestingHost());

		String subscriberId = null;
		// Build CCA (Credit-Control-Answer)
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);

		// Get Request Type
		boolean bTerminateRequest = false;
		CustomerPackageRel custPkgRel = null;
		boolean bQuotaExhausted = false;
		boolean allowOverUsage= false;
		IDiameterAVP requestTypeAvp = diameterRequest.getAVP("0:416");
		String requestType = null;
		QOSPolicyGatewayMapping mapping = null;
		Map<String,String> valueMap = new HashMap<>();
		
		if(requestTypeAvp !=null) {
			String strRequestType = requestTypeAvp.getStringValue();
			if(strRequestType != null) {
				if(strRequestType.equalsIgnoreCase("1")) {
					requestType= "INITIAL_REQUEST";
				}else if(strRequestType.equalsIgnoreCase("2")) {
					requestType= "UPDATE_REQUEST";
				}else if(strRequestType.equalsIgnoreCase("3")) {
					requestType= "TERMINATION_REQUEST";
					bTerminateRequest = true;
				}
			}
		}

		// Get Packet Mapping
		List<MappingHeader> mappingHeaders = mappingHeaderServiceImpl
				.getMappingsByRequestAndResponseType("Credit-Control-Request", "Credit-Control-Answer", "GX", 0, requestType);

		Map<String, MappingDetail> requestAvp = new HashMap<>();
		Set<MappingDetail> responseAvp = new HashSet<>();
		if (mappingHeaders != null && !mappingHeaders.isEmpty()) {
			for (MappingHeader mappingHeader : mappingHeaders) {
				if (mappingHeader.getApplication().equalsIgnoreCase("GX") && (mappingHeader.getCcRequestType() ==null) || (mappingHeader.getCcRequestType() !=null && mappingHeader.getCcRequestType().equalsIgnoreCase(requestType)) && DiameterUtils.matchAvpConditions(mappingHeader.getAvpConditions(), diameterRequest)) {
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

		if(requestAvp.isEmpty() && responseAvp.isEmpty()){

			// Add AVPs to indicate fail
			IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
			resultCodeAvp.setInteger(5005);
			diameterAnswer.addAvp(resultCodeAvp);

		}else {
			
			// Extract IMSI or subscriber ID (Subscription-Id AVP: Code 443)
			IDiameterAVP subscriptionIdAVP = diameterRequest.getAVP("0:443");
			if(bTerminateRequest || subscriptionIdAVP !=null) {
				subscriberId = extractSubscriptionId(diameterRequest);
				if(bTerminateRequest || subscriberId !=null) {
					LogManager.getLogger().info("ServerGxCCRHandler", "SubscriberId: " + subscriberId);

					Map<String, IDiameterAVP> avpCache = new HashMap<>();

					// Retrieve policy rules for subscriber from DB or logic
					if(bTerminateRequest || subscriberId !=null) {
						List<Customer> customers = null;
						
						if(subscriberId !=null) {
							customers=customerServiceImpl.getCustomers(null,null, subscriberId);
						}

						if(customers !=null && !customers.isEmpty() && customers.get(0).getQuotas()!=null && !customers.get(0).getQuotas().isEmpty()) {
							Customer customer=customers.get(0);
							
							//Store in cache
							String strSessionId =diameterRequest.getAVPValue("0:263");
							
							if(bTerminateRequest) {
								cacheManagerServiceImpl.deleteKey("GX-SESSION_ID"+customer.getCustId());
							}else {
								DiameterCacheRequestModel diameterCacheRequestModel = new DiameterCacheRequestModel();
								diameterCacheRequestModel.setSessionId(strSessionId);
								diameterCacheRequestModel.setRequestingHost(diameterRequest.getRequestingHost());
								diameterCacheRequestModel.setRequestingRealm(diameterRequest.getAVPValue("0:296"));
								
								cacheManagerServiceImpl.setValueWithExpiry("GX-SESSION_ID"+customer.getCustId(), diameterCacheRequestModel,15,TimeUnit.DAYS);
							}

							// Convert all customer fields into String and add to valueMap
							valueMap.putAll(convertCustomerToValueMap(customer));

							CustomerQuota customerQuota=customer.getQuotas().get(0);
							
							LogManager.getLogger().info("ServerGxCCRHandler", "customerQuota : "+customerQuota);
							if(("TIME".equalsIgnoreCase(customerQuota.getQuotaType()) || "BOTH".equalsIgnoreCase(customerQuota.getQuotaType()) )) {
								if(customerQuota.getTimeTotalQuota()!=null && customerQuota.getTimeQuotaUsed() !=null && customerQuota.getTimeQuotaUsed().compareTo(customerQuota.getTimeTotalQuota()) >= 0) {
									bQuotaExhausted = true;
							    }
							}

							valueMap.putAll(convertCustomerQuotaToValueMap(customerQuota,bQuotaExhausted));
							
							if (customerQuota.getTotalQuota()
									.compareTo(customerQuota.getUsedQuota()) <= 0) {
								bQuotaExhausted = true;
							}

							// ============================
							// 👉 FETCH PLAN USING SERVICE
							// ============================
							List<PostpaidPlan> plans = planServiceImpl.searchPlans(
									customerQuota.getPlanId(),   // planId
									null,                        // name
									null,                        // planType
									null,                        // price
									null,                        // status
									null,                        // planStatus
									null,                        // quotaUnit
									null,                        // downloadSpeed
									null,                        // uploadSpeed
									null,                        // startDate
									null,                        // endDate
									null,                        // quota
									null,                        // validity
									null                         // chunk
							);

							if (plans != null && !plans.isEmpty()) {
								PostpaidPlan plan = plans.get(0);
								valueMap.putAll(convertCustomerPlanToValueMap(plan));
								allowOverUsage=plan.getAllowOverUsage();
							}

							List<CustomerPackageRel> relList =
									customerServiceImpl.getCustomerPackageRel(
											customer.getCustId(),
											customerQuota.getPlanId(),
											BigInteger.valueOf(customerQuota.getCustPackageId())
									);

							custPkgRel =
									(relList != null && !relList.isEmpty()) ? relList.get(0) : null;
							
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
									convertSmsDetailsToValueMap(relCustSmsDetails)
							);

							valueMap.putAll(
									convertVoiceDetailsToValueMap(relVoiceDetails)
							);


							// ============================
							// FETCH QOS GATEWAY MAPPING
							// ============================
							if (custPkgRel != null && custPkgRel.getQosPolicyId() != null) {
								
								valueMap.put("customerPackage.startDate", custPkgRel.getStartDate()!=null ? String.valueOf(custPkgRel.getStartDate().toLocalDateTime()
								        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))):null);
								valueMap.put("customerPackage.expiryDate", custPkgRel.getExpiryDate() !=null ? String.valueOf(custPkgRel.getExpiryDate().toLocalDateTime()
								        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))):null);

								List<QOSPolicyGatewayMapping> gatewayMappings =
										qosPolicyServiceImpl.getGatewayMappingByQosPolicyId(
												String.valueOf(custPkgRel.getQosPolicyId())
										);

								if (gatewayMappings != null && !gatewayMappings.isEmpty()) {
									mapping = gatewayMappings.get(0);
									valueMap.putAll(convertGatewayMappingToValueMap(mapping,bQuotaExhausted,allowOverUsage));
									
								}
							}
						}
					}
					
					ArrayList<IDiameterAVP> requestAvps=diameterRequest.getAVPList();
					if(requestAvps !=null) {
						extractAvps(requestAvps, "request", valueMap);
					}

					Set<String> groupAvpSet = new HashSet<>();
					for (MappingDetail mappingDetail : responseAvp) {
						String key = mappingDetail.getVendorId()+":"+mappingDetail.getResponseAvp();

						//Value of AVP
						String strValue = null;
						String valueExpression = mappingDetail.getValueExpression();
						if(valueExpression !=null) {
							if(valueExpression.startsWith("${")) {
								String cleaned = valueExpression.replaceAll("^\\$\\{", "").replaceAll("\\}$", "");
								strValue =  valueMap.get(cleaned);
							}else {
								strValue = valueExpression;
							}
						}

						if (key.contains(".")) {
							String strGroupAvp = key.split("\\.")[0];
							groupAvpSet.add(strGroupAvp.replaceAll("10415:", "0:"));
							addNestedAvp(key.replaceAll("0:", "").replaceAll("10415:", ""), strValue, mappingDetail.getValueType().getDbValue(), String.valueOf(mappingDetail.getVendorId()),avpCache);
						}else {
							IDiameterAVP iDiameterAVP = DiameterDictionary.getInstance().getAttribute(key);
							if(iDiameterAVP != null) {
								if(strValue !=null) {
									iDiameterAVP.setStringValue(strValue);
								}
								diameterAnswer.addAvp(iDiameterAVP);
							}
						}
					}
					for(String strGroupAvp:groupAvpSet) {
						IDiameterAVP iDiameterAVP = avpCache.get(strGroupAvp);
						if (iDiameterAVP != null) {
							// Recompute length after (possibly multiple) child AVPs were
							// attached, so repeated grouped instances encode correctly.
							iDiameterAVP.refreshAVPHeader();
							diameterAnswer.addAvp(iDiameterAVP);
						}
					}

					// Add AVPs to indicate success
					IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
					resultCodeAvp.setInteger(2001); // DIAMETER_SUCCESS
					diameterAnswer.addAvp(resultCodeAvp);

				}else {
					// Subscriber not found
					IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
					resultCodeAvp.setInteger(5032);
					diameterAnswer.addAvp(resultCodeAvp);
				}

			}else {
				
				if(DiameterUtils.isUserLocationChange(diameterRequest)) {
					// Add AVPs to indicate success
					IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
					resultCodeAvp.setInteger(2001); // DIAMETER_SUCCESS
					diameterAnswer.addAvp(resultCodeAvp);
				}else {
					// Subscriber Id is missing in request
					IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
					resultCodeAvp.setInteger(5005);
					diameterAnswer.addAvp(resultCodeAvp);
				}
			}
		}



		try {
			stackContext.getPeerCommunicator(diameterRequest.getRequestingHost())
					.sendAnswer(diameterRequest, diameterAnswer);
			
			//Store in Audit and CDR
            genericDiameterProcessor.process(diameterRequest, diameterAnswer,valueMap);
			
			LogManager.getLogger().info("ServerGxCCRHandler", "CCA sent successfully for subscriber: " + subscriberId);
		} catch (CommunicationException e) {
			LogManager.getLogger().error("ServerGxCCRHandler", "CommunicationException while sending CCA: ", e);
		}
		} catch (RuntimeException __ex) {
			METHOD_LOG.error("!! EXCEPTION in processApplicationRequest after {}ms", System.currentTimeMillis() - __mStart, __ex);
			throw __ex;
		} finally {
			if (METHOD_LOG.isDebugEnabled()) {
				METHOD_LOG.debug("<< EXIT processApplicationRequest tookMs={}", System.currentTimeMillis() - __mStart);
			}
		}
	}

    @Override
    protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
        return null;
    }

    
    private String extractSubscriptionId(DiameterRequest diameterRequest) {
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
	                if (type.equalsIgnoreCase("1")) {       // END_USER_IMSI
	                    imsi = value;
	                } else if (type.equalsIgnoreCase("0")) { // END_USER_E164
	                    msisdn = value;
	                }
	            }
	        }
	    }
	    // priority rule
	    return imsi != null ? imsi : msisdn;
	}
    
    public static void addNestedAvp(
	        String responseAvp,
	        String value,
	        String valueType,
	        String vendorId,
	        Map<String, IDiameterAVP> avpCache) {

	    String[] parts = responseAvp.split("\\.");
	    IDiameterAVP parent = null;
	    StringBuilder pathKey = new StringBuilder();

	    for (int i = 0; i < parts.length; i++) {

	        String avpCode = parts[i];
	        pathKey.append("0:").append(avpCode);

	        String cacheKey = pathKey.toString();
	        boolean isLeaf = (i == parts.length - 1);

	        // Multi-instance support: a path segment may carry a leading "<n>_"
	        // prefix (e.g. "1_1003", "2_1003") so the same grouped AVP can be
	        // emitted repeatedly as siblings under one parent. The prefix is part
	        // of the cache key above (which keeps the instances distinct) but is
	        // stripped here so the dictionary lookup and wire output use the real
	        // AVP code. Segments without the prefix are unchanged (no-op),
	        // preserving existing single-instance behaviour.
	        avpCode = avpCode.replaceFirst("^\\d+_", "");

	        IDiameterAVP avp = null;

	        if (!isLeaf && avpCache.containsKey(cacheKey)) {

	            // reuse grouped parent
	            avp = avpCache.get(cacheKey);

	        } else {
	        	String tempVendorId= vendorId;
	        	if(avpCode.equalsIgnoreCase("456")) {
	        		tempVendorId = "0";
	        	}
	        	if(avpCode.equalsIgnoreCase("1001")) {
	        		tempVendorId = "10415";
	        	}
	        	if(avpCode.equalsIgnoreCase("1003")) {
	        		tempVendorId = "10415";
	        	}

	            // get template and clone
	            IDiameterAVP template =
	                DiameterDictionary.getInstance()
	                    .getAttribute(tempVendorId + ":" + avpCode);

	            try {
					avp = (IDiameterAVP) template.clone();
				} catch (CloneNotSupportedException e) {
					LogManager.getLogger().error("ServerGxCCRHandler", "Failed to clone AVP template", e);
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
        }catch (Exception e) {
        	LogManager.getLogger().error("ServerGxCCRHandler", "setAvpValue", e);
		}
    }
	
	
	/**
	 * Converts all Customer fields into String values with keys like "customer.userName".
	 */
	private Map<String, String> convertCustomerToValueMap(Customer customer) {
		Map<String, String> valueMap = new HashMap<>();

		if (customer == null) return valueMap;

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
	 * Converts all CustomerQuota fields into String values with keys like "customerQuota.planId".
	 */
	private Map<String, String> convertCustomerQuotaToValueMap(CustomerQuota customerQuota,boolean bQuotaExhausted) {
		Map<String, String> valueMap = new HashMap<>();
		if (customerQuota == null) return valueMap;

		valueMap.put("customerQuota.quotaDtlsId", String.valueOf(customerQuota.getQuotaDtlsId()));
		valueMap.put("customerQuota.custId", String.valueOf(customerQuota.getCustId()));
		valueMap.put("customerQuota.planId", String.valueOf(customerQuota.getPlanId()));
		valueMap.put("customerQuota.quotaType", String.valueOf(customerQuota.getQuotaType()));
		if(bQuotaExhausted) {
			valueMap.put("customerQuota.totalQuota","0");
			valueMap.put("customerQuota.usedQuota","0");
			valueMap.put("customerQuota.timeTotalQuota","0");
			valueMap.put("customerQuota.timeQuotaUsed","0");
		}else {
			valueMap.put("customerQuota.totalQuota",customerQuota.getTotalQuota() !=null ? String.valueOf(convertQuota(customerQuota.getTotalQuota().doubleValue(), customerQuota.getQuotaUnit())):null);
			valueMap.put("customerQuota.usedQuota",customerQuota.getUsedQuota() !=null ? String.valueOf(convertQuota(customerQuota.getUsedQuota().doubleValue(), customerQuota.getQuotaUnit())):null);
			valueMap.put("customerQuota.timeTotalQuota",customerQuota.getTimeTotalQuota() !=null ? String.valueOf(DiameterUtils.convertTime(customerQuota.getTimeTotalQuota().doubleValue(),customerQuota.getTimeQuotaUnit())):null);
			valueMap.put("customerQuota.timeQuotaUsed", customerQuota.getTimeQuotaUsed() !=null ?String.valueOf(DiameterUtils.convertTime(customerQuota.getTimeQuotaUsed().doubleValue(),customerQuota.getTimeQuotaUnit())):null);
		}
		
		valueMap.put("customerQuota.quotaUnit", String.valueOf(customerQuota.getQuotaUnit()));
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
		valueMap.put("customerQuota.currentSessionUsageTime", String.valueOf(customerQuota.getCurrentSessionUsageTime()));
		valueMap.put("customerQuota.currentSessionUsageVolume", String.valueOf(customerQuota.getCurrentSessionUsageVolume()));
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
	 * Converts all PostpaidPlan fields into String values
	 * with keys like "customerPlan.planCode".
	 */

	private Map<String, String> convertCustomerPlanToValueMap(PostpaidPlan plan) {

		Map<String, String> valueMap = new HashMap<>();

		if (plan == null) return valueMap;

		valueMap.put("customerPlan.postPaidPlanId", String.valueOf(plan.getId()));
		valueMap.put("customerPlan.name", String.valueOf(plan.getName()));
		valueMap.put("customerPlan.displayName", String.valueOf(plan.getDisplayName()));
		valueMap.put("customerPlan.planCode", String.valueOf(plan.getCode()));
		valueMap.put("customerPlan.description", String.valueOf(plan.getDesc()));
		valueMap.put("customerPlan.planCategory", String.valueOf(plan.getCategory()));

		valueMap.put("customerPlan.quota", String.valueOf(convertQuota(plan.getQuota().doubleValue(), plan.getQuotaUnit())));
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

//		valueMap.put("customerPlan.createDate", String.valueOf(plan.getCreateDate()));
//		valueMap.put("customerPlan.createdByStaffId", String.valueOf(plan.getCreatedByStaffId()));
//		valueMap.put("customerPlan.lastModifiedByStaffId", String.valueOf(plan.getLastModifiedByStaffId()));
//		valueMap.put("customerPlan.lastModifiedDate", String.valueOf(plan.getLastModifiedDate()));

		return valueMap;
	}

	
	/**
	 * Converts all QOSPolicyGatewayMapping fields into String values
	 * with keys like "gatewayMapping.downloadSpeed".
	 */
	private Map<String, String> convertGatewayMappingToValueMap(QOSPolicyGatewayMapping mapping,boolean bQuotaExhausted,boolean allowOverUsage) {

		Map<String, String> valueMap = new HashMap<>();

		if (mapping == null) return valueMap;

		valueMap.put("gatewayMapping.id", String.valueOf(mapping.getId()));
		valueMap.put("gatewayMapping.name", String.valueOf(mapping.getName()));
		
		if(bQuotaExhausted && allowOverUsage) {
			valueMap.put("gatewayMapping.downloadSpeed",
					String.valueOf(mapping.getThrottleDownloadSpeed()));

			valueMap.put("gatewayMapping.uploadSpeed",
					String.valueOf(mapping.getThrottleUploadSpeed()));
		}else {
			valueMap.put("gatewayMapping.downloadSpeed",
					String.valueOf(mapping.getDownloadSpeed()));

			valueMap.put("gatewayMapping.uploadSpeed",
					String.valueOf(mapping.getUploadSpeed()));
		}


		valueMap.put("gatewayMapping.baseDownloadSpeed",
				String.valueOf(mapping.getBaseDownloadSpeed()));

		valueMap.put("gatewayMapping.baseUploadSpeed",
				String.valueOf(mapping.getBaseUploadSpeed()));

		valueMap.put("gatewayMapping.throttleDownloadSpeed",
				String.valueOf(mapping.getThrottleDownloadSpeed()));

		valueMap.put("gatewayMapping.throttleUploadSpeed",
				String.valueOf(mapping.getThrottleUploadSpeed()));

		valueMap.put("gatewayMapping.qosPolicyId",
				String.valueOf(mapping.getQosPolicyId()));

		return valueMap;
	}

	private Map<String, String> convertSmsDetailsToValueMap(
			List<CustSmsDetails> smsList) {

		Map<String, String> valueMap = new HashMap<>();

		if (smsList == null || smsList.isEmpty()) {
			return valueMap;
		}

		CustSmsDetails sms = smsList.get(0);


		valueMap.put("sms.smsType",
				String.valueOf(sms.getSmsType()));

		valueMap.put("sms.totalSms", String.valueOf(sms.getTotalSms()));

		valueMap.put("sms.usedSms", String.valueOf(sms.getUsedSms()));

		return valueMap;
	}

	private Map<String, String> convertVoiceDetailsToValueMap(
			List<CustVoiceDetails> voiceList) {

		Map<String, String> valueMap = new HashMap<>();

		if (voiceList == null || voiceList.isEmpty()) {
			return valueMap;
		}

		CustVoiceDetails voice = voiceList.get(0);


		valueMap.put("voice.voiceType",
				String.valueOf(voice.getVoiceType()));

		if(voice.getTotalVoice() !=null) {
			valueMap.put("voice.totalVoice", String.valueOf(DiameterUtils.convertTimeToSecond(voice.getTotalVoice().doubleValue(),voice.getVoiceType())));
		}

		if(voice.getUsedVoice() !=null) {
			valueMap.put("voice.usedVoice", String.valueOf(DiameterUtils.convertTimeToSecond(voice.getUsedVoice().doubleValue(),voice.getVoiceType())));
		}

		return valueMap;
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
	
	private void extractAvps(List<IDiameterAVP> avps, String prefix, Map<String, String> valueMap) {

	    for (IDiameterAVP avp : avps) {

	        String name = DiameterDictionary.getInstance()
	                .getAttributeName(avp.getAVPId());

	        String key = prefix + "." + name;

	        // If grouped → recurse
	        if (avp.getGroupedAvp() != null && !avp.getGroupedAvp().isEmpty()) {
	            extractAvps(avp.getGroupedAvp(), key, valueMap);
	        } 
	        else {
	            valueMap.put(key, avp.getStringValue());
	        }
	    }
	}
	
	private void upsertAvp(DiameterAnswer answer, IDiameterAVP avp) {
	    IDiameterAVP existing = answer.getAVP(avp.getAVPId());
	    if (existing != null) {
	        answer.removeAVP(existing);
	    }
	    answer.addAvp(avp);
	}

}