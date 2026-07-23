package com.diameter.handler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
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
import com.diameter.commons.DiameterSession;
import com.diameter.commons.DiameterUtils;
import com.diameter.commons.IDiameterAVP;
import com.diameter.commons.IStackContext;
import com.diameter.commons.LogManager;
import com.diameter.commons.ResponseListener;
import com.diameter.commons.Session;
import com.diameter.commons.SessionReleaseIndiactor;
import com.diameter.enums.QuotaType;
import com.diameter.enums.ServiceType;
import com.diameter.enums.ReAuthReason;
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

public class ServerGyCCRHandler extends ApplicationListener{

	private IStackContext stackContext;
	
	private CustomerServiceImpl customerServiceImpl;

	private PlanServiceImpl planServiceImpl;

	private QOSPolicyServiceImpl qosPolicyServiceImpl;


	private MappingHeaderServiceImpl mappingHeaderServiceImpl;
	
	private static final org.slf4j.Logger METHOD_LOG = org.slf4j.LoggerFactory.getLogger(ServerGyCCRHandler.class);

	private LocalCacheManagerServiceImpl cacheManagerServiceImpl;
	
	private GenericDiameterProcessor genericDiameterProcessor;
	
	private Integer maxChunkSizeMb = 100;
	
	private static final BigDecimal B_IN_KB = BigDecimal.valueOf(1024);
	private static final BigDecimal B_IN_MB = B_IN_KB.multiply(B_IN_KB);
	private static final BigDecimal B_IN_GB = B_IN_MB.multiply(B_IN_KB);
	
	public ServerGyCCRHandler(IStackContext stackContext, ApplicationEnum[] applicationEnums) {
		super(stackContext, applicationEnums);
		this.stackContext=stackContext;
	}
	
	public Integer getMaxChunkSizeMb() {
		return maxChunkSizeMb;
	}

	public void setMaxChunkSizeMb(Integer maxChunkSizeMb) {
		this.maxChunkSizeMb = maxChunkSizeMb;
	}

	public void setMappingHeaderServiceImpl(MappingHeaderServiceImpl mappingHeaderServiceImpl) {
		this.mappingHeaderServiceImpl=mappingHeaderServiceImpl;
	}
	
	public void setCustomerServiceImpl(CustomerServiceImpl customerServiceImpl) {
		this.customerServiceImpl=customerServiceImpl;
	}
	
	public GenericDiameterProcessor getGenericDiameterProcessor() {
		return genericDiameterProcessor;
	}

	public void setGenericDiameterProcessor(GenericDiameterProcessor genericDiameterProcessor) {
		this.genericDiameterProcessor = genericDiameterProcessor;
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
		return Application.CC.name();
	}
	
	@Override
	protected void processApplicationRequest(Session paramSession, DiameterRequest diameterRequest) {
		boolean bIsRoRequest= false;
		// Get Service-Information AVP (873)
	    IDiameterAVP serviceInfo = diameterRequest.getAVP("10415:873");
	    if(serviceInfo != null) {
	    	
	    	for (IDiameterAVP avp : serviceInfo.getGroupedAvp()) {
				if (avp.getAVPCode() == 876) { // Check IMS-Information (876)
					bIsRoRequest = true;
					break;
				}
			}
	    }
	    
	    if(bIsRoRequest && !DiameterUtils.isDataRequest(diameterRequest)) {
	    	ServerRoCCRHandler serverRoCCRHandler = new ServerRoCCRHandler(stackContext, getApplicationEnum());
	    	serverRoCCRHandler.setCustomerServiceImpl(customerServiceImpl);
	    	serverRoCCRHandler.setMappingHeaderServiceImpl(mappingHeaderServiceImpl);
	    	serverRoCCRHandler.setPlanServiceImpl(planServiceImpl);
	    	serverRoCCRHandler.setQOSPolicyServiceImpl(qosPolicyServiceImpl);
	    	serverRoCCRHandler.setCacheManagerServiceImpl(cacheManagerServiceImpl);
	    	serverRoCCRHandler.setGenericDiameterProcessor(genericDiameterProcessor);
	    	serverRoCCRHandler.setMaxChunkSizeMb(maxChunkSizeMb);
	    	serverRoCCRHandler.processRequest(paramSession, diameterRequest);
	    	return;
	    }
	    processRequest(paramSession, diameterRequest);
	}

	public void processRequest(Session paramSession, DiameterRequest diameterRequest) {
		long __mStart = System.currentTimeMillis();
		if (METHOD_LOG.isDebugEnabled()) {
			METHOD_LOG.debug(">> ENTRY processApplicationRequest sessionId={}", (paramSession != null ? paramSession.getSessionId() : "null"));
		}
		try {
		CustomerPackageRel custPkgRel = null;
		CustomerQuota customerQuota = null;
		QOSPolicyGatewayMapping mapping = null;
		
	    
	    LogManager.getLogger().info("ServerCCRHandler", "MSISDN recieved at handler : "+diameterRequest.getAVP("0:443"));
		
		LogManager.getLogger().info("ServerCCRHandler", "Multiple-Services-Credit-Control recieved at handler : "+diameterRequest.getAVP("0:456"));


		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
		boolean bRAREnableForQuotaExhausted = false;
		boolean allowOverUsage= false;
		boolean bPlanExpire= false;
		boolean bVoiceQuotaExhausted = false;
		String strCustomerUsername = null;
		Map<String,String> valueMap = new HashMap<>();

		// Get Request Type
		boolean bTerminateRequest = false;
		IDiameterAVP requestTypeAvp = diameterRequest.getAVP("0:416");
		String requestType = null;
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

		//Get Packet Mapping
		List<MappingHeader> mappingHeaders=mappingHeaderServiceImpl.getMappingsByRequestAndResponseType("Credit-Control-Request", "Credit-Control-Answer","GY",0,requestType);

		Map<String, MappingDetail> requestAvp = new HashMap<>();
		Set<MappingDetail> responseAvp = new HashSet<>();
		if(mappingHeaders !=null && !mappingHeaders.isEmpty()) {
			for(MappingHeader mappingHeader:mappingHeaders) {
				if(mappingHeader.getApplication().equalsIgnoreCase("GY") && (mappingHeader.getCcRequestType() ==null) || (mappingHeader.getCcRequestType() !=null && mappingHeader.getCcRequestType().equalsIgnoreCase(requestType))&& DiameterUtils.matchAvpConditions(mappingHeader.getAvpConditions(), diameterRequest)) {
					List<MappingDetail> details =mappingHeader.getDetails();
					if(details !=null) {
						for(MappingDetail mappingDetail:details) {
							if(mappingDetail.getRequestAvp() !=null) {
								requestAvp.put(mappingDetail.getVendorId()+":"+mappingDetail.getRequestAvp(), mappingDetail);
							}
							if(mappingDetail.getResponseAvp() !=null) {
								responseAvp.add(mappingDetail);
							}
						}
					}
				}
			}
		}
		
		ServiceType serviceType=DiameterUtils.getServiceType(diameterRequest);
		QuotaType quotaType=DiameterUtils.getQuotaType(diameterRequest);
		String usedTimeQuota=DiameterUtils.getUsedTimeQuota(diameterRequest);
		
		LogManager.getLogger().info("ServerCCRHandler", "serviceType is : : "+serviceType);
		LogManager.getLogger().info("ServerCCRHandler", "quotaType is : : "+quotaType);
		LogManager.getLogger().info("ServerCCRHandler", "usedTimeQuota is : : "+usedTimeQuota);

		if(requestAvp.isEmpty() && responseAvp.isEmpty()){

			// Add AVPs to indicate fail
			IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
			resultCodeAvp.setInteger(5005);
			diameterAnswer.addAvp(resultCodeAvp);

		}else {
			strCustomerUsername = getCustomerUsername(diameterRequest);
			
			if(bTerminateRequest || (strCustomerUsername !=null && !strCustomerUsername.isEmpty())) {

				List<Customer> customers = null;
				String strUsedQuotas = null;
				CustVoiceDetails voiceDetails = null;

				if(strCustomerUsername !=null && !strCustomerUsername.isEmpty()) {
					//UsedQuotas
					strUsedQuotas= getUsedQuotas(diameterRequest);

					//Get Customer from Database
					customers=customerServiceImpl.getCustomers(null,null, strCustomerUsername);

					LogManager.getLogger().info("ServerCCRHandler", "strCustomerUsername : "+strCustomerUsername);
					LogManager.getLogger().info("ServerCCRHandler", "strUsedQuotas : "+strUsedQuotas);
					LogManager.getLogger().info("ServerCCRHandler", "getCustomers : "+customers);
				}

				if(customers !=null && !customers.isEmpty() && customers.get(0).getQuotas()!=null && !customers.get(0).getQuotas().isEmpty()) {
					Customer customer=customers.get(0);
					
					//Store in cache
					String strSessionId =diameterRequest.getAVPValue("0:263");
					
					DiameterCacheRequestModel diameterCacheRequestModel = new DiameterCacheRequestModel();
					diameterCacheRequestModel.setSessionId(strSessionId);
					diameterCacheRequestModel.setRequestingHost(diameterRequest.getRequestingHost());
					diameterCacheRequestModel.setRequestingRealm(diameterRequest.getAVPValue("0:296"));
					
					//Last Session Access
					cacheManagerServiceImpl.setValueWithExpiry("LAST-GY-SESSION_ID", diameterCacheRequestModel,15,TimeUnit.DAYS);
					
					if(bTerminateRequest) {
						cacheManagerServiceImpl.deleteKey("GY-SESSION_ID"+customer.getCustId());
					}else {
						cacheManagerServiceImpl.setValueWithExpiry("GY-SESSION_ID"+customer.getCustId(), diameterCacheRequestModel,15,TimeUnit.DAYS);
					}

					// Convert all customer fields into String and add to valueMap
					valueMap.putAll(convertCustomerToValueMap(customer));

					customerQuota=customer.getQuotas().get(0);
					
					LogManager.getLogger().info("ServerCCRHandler", "customerQuota : "+customerQuota);
					if(("TIME".equalsIgnoreCase(customerQuota.getQuotaType()) || "BOTH".equalsIgnoreCase(customerQuota.getQuotaType()) ) && serviceType == ServiceType.DATA) {
						if(customerQuota.getTimeTotalQuota()!=null && customerQuota.getTimeQuotaUsed() !=null && customerQuota.getTimeQuotaUsed().compareTo(customerQuota.getTimeTotalQuota()) >= 0) {
					    	bRAREnableForQuotaExhausted = true;
					    }
					}

					valueMap.putAll(convertCustomerQuotaToValueMap(customerQuota,strUsedQuotas,usedTimeQuota,bRAREnableForQuotaExhausted));

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

						/*
						 * Calculate Granted-Service-Unit octets
						 * based on remaining quota
						 */

                        // Remaining quota in customer unit
						double totalQuota = customerQuota.getTotalQuota().doubleValue();
						double usedQuota = customerQuota.getUsedQuota().doubleValue();

                        // Add current request usage if available
                        if (strUsedQuotas != null) {
                            BigDecimal currentUsage = convert(Long.valueOf(strUsedQuotas),customerQuota.getQuotaUnit());
                            usedQuota = usedQuota + currentUsage.doubleValue();
                        }

                        // Remaining quota
                        double remainingQuota = totalQuota - usedQuota;

                        if (remainingQuota < 0) {
                            remainingQuota = 0;
                        }

                        // Final granted quota
                        long grantedUnits;
                        long fixedChunkBytes =maxChunkSizeMb * 1024L * 1024L;

					    long remainingBytes =convertQuota(remainingQuota,customerQuota.getQuotaUnit());

					    grantedUnits =Math.min(remainingBytes,fixedChunkBytes);

					    valueMap.put("customerPlan.chunk",String.valueOf(grantedUnits));
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
					
					if(relVoiceDetails != null && !relVoiceDetails.isEmpty()) {
						voiceDetails = relVoiceDetails.get(0);
					}
					
					if(usedTimeQuota != null) {

					    BigDecimal usedSeconds =BigDecimal.valueOf(Long.parseLong(usedTimeQuota));

					    // Voice time charging
					    if(serviceType == ServiceType.VOICE) {
					    	// Current consumed
							BigDecimal totalUsed = BigDecimal.valueOf(DiameterUtils.convertTimeToSecond(voiceDetails.getUsedVoice().doubleValue(),voiceDetails.getVoiceType())).add(usedSeconds);
							BigDecimal totalVoice = BigDecimal.valueOf(DiameterUtils.convertTimeToSecond(voiceDetails.getTotalVoice().doubleValue(),voiceDetails.getVoiceType()));
							
							LogManager.getLogger().info("ServerCCRHandler", "totalUsed is : "+totalUsed);
							LogManager.getLogger().info("ServerCCRHandler", "totalVoice is :"+totalVoice);

					        customerServiceImpl.updateVoiceQuotasByCustomerId(
					        		BigDecimal.valueOf(DiameterUtils.convertSecondTimeToUnit(usedSeconds.doubleValue(),voiceDetails.getVoiceType())),
					                customer.getCustId(),
					                customerQuota.getPlanId(),
					                Long.valueOf(customerQuota.getCustPackageId())
					        );
					        
					        // Check quota exhaustion
							if (!DiameterUtils.isUnlimited(voiceDetails.getVoiceType()) && totalUsed.compareTo(totalVoice) >= 0) {
								bVoiceQuotaExhausted = true;
								LogManager.getLogger().info("ServerCCRHandler", "Voice quota exhausted");
							}
					    }

					    // Data time charging
					    if(("TIME".equalsIgnoreCase(customerQuota.getQuotaType()) || "BOTH".equalsIgnoreCase(customerQuota.getQuotaType()) ) && serviceType == ServiceType.DATA) {

					        customerServiceImpl.updateTimeQuotaByCustomerId(
					        		BigDecimal.valueOf(DiameterUtils.convertSecondTimeToUnit(usedSeconds.doubleValue(),customerQuota.getTimeQuotaUnit())),
					                customer.getCustId(),
					                customerQuota.getPlanId()
					        );
					        
					        BigDecimal totalTime=BigDecimal.valueOf(DiameterUtils.convertTime(customerQuota.getTimeTotalQuota().doubleValue(),customerQuota.getTimeQuotaUnit()));
						    BigDecimal usedTime=BigDecimal.valueOf(DiameterUtils.convertTime(customerQuota.getTimeQuotaUsed().doubleValue(),customerQuota.getTimeQuotaUnit()));

						    usedTime =usedTime.add(BigDecimal.valueOf(Long.parseLong(usedTimeQuota)));
						    if(usedTime.compareTo(totalTime) >= 0) {
						    	bRAREnableForQuotaExhausted = true;
						    }
						    LogManager.getLogger().info("ServerCCRHandler", "totalTime : "+totalTime);
						    LogManager.getLogger().info("ServerCCRHandler", "usedTime : "+usedTime);
					    }
					}
					
					if(("DATA".equalsIgnoreCase(customerQuota.getQuotaType()) || "BOTH".equalsIgnoreCase(customerQuota.getQuotaType())) && serviceType == ServiceType.DATA) {
						BigDecimal usedQuota = new BigDecimal(0);
						if(strUsedQuotas !=null) {
							usedQuota=convert(Long.valueOf(strUsedQuotas),customerQuota.getQuotaUnit());
						}
						customerServiceImpl.updateQuotasByCustomerId(usedQuota, customer.getCustId(), customerQuota.getPlanId(),Long.valueOf(customerQuota.getCustPackageId()));
						if (customerQuota.getTotalQuota()
								.compareTo(usedQuota.add(customerQuota.getUsedQuota())) <= 0) {
							bRAREnableForQuotaExhausted = true;
						}

					}
					
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
							valueMap.putAll(convertGatewayMappingToValueMap(mapping,bRAREnableForQuotaExhausted,allowOverUsage));
						}
					}
				}

				if(bTerminateRequest || (customers !=null && !customers.isEmpty() && customers.get(0).getQuotas()!=null && !customers.get(0).getQuotas().isEmpty())) {
					
					if (custPkgRel != null && custPkgRel.getEndDate() !=null && custPkgRel.getEndDate().before(new Timestamp(System.currentTimeMillis()))) {
					    bPlanExpire = DiameterUtils.isGracePeriodExpired(diameterRequest, custPkgRel);
						
						if(bPlanExpire) {
							LogManager.getLogger().info("ServerCCRHandler", "Plan expired for subscriber");
						}else {
							LogManager.getLogger().info("ServerCCRHandler", "Plan expired for subscriber but grace period applied");
						}
					}else {
						ArrayList<IDiameterAVP> requestAvps=diameterRequest.getAVPList();
						if(requestAvps !=null) {
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
								if(iDiameterAVP !=null) {
									if(strValue !=null) {
										iDiameterAVP.setStringValue(strValue);
									}
									diameterAnswer.addAvp(iDiameterAVP);
								}
							}
						}
						for(String strGroupAvp:groupAvpSet) {
							IDiameterAVP iDiameterAVP = avpCache.get(strGroupAvp);
							if(iDiameterAVP != null) {
								iDiameterAVP.refreshAVPHeader();
								diameterAnswer.addAvp(iDiameterAVP);
							}
						}
					}

				}else {
					// Subscriber not found
					IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
					resultCodeAvp.setInteger(5032);
					diameterAnswer.addAvp(resultCodeAvp);
				}

			}else {
				// Subscriber Id is missing in request
				IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
				resultCodeAvp.setInteger(5005);
				diameterAnswer.addAvp(resultCodeAvp);
			}
		}

		try {
			LogManager.getLogger().info("ServerCCRHandler", "bTerminateRequest is : "+bTerminateRequest);
			LogManager.getLogger().info("ServerCCRHandler", "bVoiceQuotaExhausted is : "+bVoiceQuotaExhausted);
			LogManager.getLogger().info("ServerCCRHandler", "bRAREnableForQuotaExhausted is : "+bRAREnableForQuotaExhausted);
			LogManager.getLogger().info("ServerCCRHandler", "allowOverUsage is : "+allowOverUsage);
			LogManager.getLogger().info("ServerCCRHandler", "bPlanExpire is : "+bPlanExpire);
			
			if(!bVoiceQuotaExhausted && ((!bTerminateRequest && bRAREnableForQuotaExhausted && !allowOverUsage) || bPlanExpire)) {

				IDiameterAVP gsu = DiameterDictionary.getInstance().getAttribute("0:431"); // GSU
				
				IDiameterAVP quotaAvp;
				if(quotaType == QuotaType.TIME) {
				    quotaAvp=DiameterDictionary.getInstance().getAttribute("0:420");
				    quotaAvp.setInteger(0);
				} else {
				    quotaAvp=DiameterDictionary.getInstance().getAttribute("0:421");
				    quotaAvp.setInteger(0);
				}

				ArrayList<IDiameterAVP> totalOctetsList = new ArrayList<>();
				totalOctetsList.add(quotaAvp);
				gsu.setGroupedAvp(totalOctetsList);

				IDiameterAVP fui = DiameterDictionary.getInstance().getAttribute("0:430");
				IDiameterAVP action = DiameterDictionary.getInstance().getAttribute("0:449");

				action.setInteger(0); // TERMINATE

				ArrayList<IDiameterAVP> paramArrayList = new ArrayList<>();
				paramArrayList.add(action);
				fui.setGroupedAvp(paramArrayList);
				
				// Add AVPs to DIAMETER_CREDIT_LIMIT_REACHED
				IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
				resultCodeAvp.setInteger(4012); // DIAMETER_CREDIT_LIMIT_REACHED

				IDiameterAVP mscc = DiameterDictionary.getInstance().getAttribute("0:456");

				ArrayList<IDiameterAVP> msccList = new ArrayList<>();
				msccList.add(gsu);
				msccList.add(fui);
				msccList.add(resultCodeAvp);

				mscc.setGroupedAvp(msccList);

				upsertAvp(diameterAnswer, mscc);
				
				// Add AVPs to indicate success
				resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
				resultCodeAvp.setInteger(2001); // DIAMETER_SUCCESS
				diameterAnswer.addAvp(resultCodeAvp);
				
			}else if(!bVoiceQuotaExhausted &&!bTerminateRequest && bRAREnableForQuotaExhausted && allowOverUsage){
				// Charging-Rule-Install (1001)
				IDiameterAVP chargingRuleInstall = DiameterDictionary.getInstance().getAttribute("10415:1001");

				// Charging-Rule-Definition (1003)
				IDiameterAVP chargingRuleDefinition = DiameterDictionary.getInstance().getAttribute("10415:1003");


				// -----------------------------
				// QoS-Information
				// -----------------------------
				IDiameterAVP qos = DiameterDictionary.getInstance().getAttribute("10415:1016");

				// Max-Requested-Bandwidth-UL
				IDiameterAVP ul = DiameterDictionary.getInstance().getAttribute("10415:516");
				if(mapping!=null) {
					ul.setInteger(Integer.valueOf(mapping.getThrottleUploadSpeed()));
				}else {
					ul.setInteger(0);
				}

				// Max-Requested-Bandwidth-DL
				IDiameterAVP dl = DiameterDictionary.getInstance().getAttribute("10415:515");
				if(mapping!=null) {
					dl.setInteger(Integer.valueOf(mapping.getThrottleDownloadSpeed()));
				}else {
					dl.setInteger(0);
				}

				// Group Flow AVPs
				ArrayList<IDiameterAVP> flowInfoList = new ArrayList<>();
				flowInfoList.add(ul);
				flowInfoList.add(dl);

				qos.setGroupedAvp(flowInfoList);

				// -----------------------------
				// Build Charging-Rule-Definition
				// -----------------------------
				ArrayList<IDiameterAVP> ruleDefList = new ArrayList<>();
				ruleDefList.add(qos);

				chargingRuleDefinition.setGroupedAvp(ruleDefList);

				// -----------------------------
				// Build Charging-Rule-Install
				// -----------------------------
				ArrayList<IDiameterAVP> installList = new ArrayList<>();
				installList.add(chargingRuleDefinition);

				chargingRuleInstall.setGroupedAvp(installList);

				// Add to Diameter Answer (CCA)
				upsertAvp(diameterAnswer, chargingRuleInstall);

				// Result-Code (success for Gy)
				IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268");
				resultCodeAvp.setInteger(2001); // DIAMETER_SUCCESS

				upsertAvp(diameterAnswer, resultCodeAvp);
			}else if(bVoiceQuotaExhausted) {
				DiameterUtils.getVoiceQuotaExhaustedAnswer(diameterAnswer);
			}
			stackContext.getPeerCommunicator(diameterRequest.getRequestingHost()).sendAnswer(diameterRequest, diameterAnswer);
			
			//Store in Audit and CDR
            genericDiameterProcessor.process(diameterRequest, diameterAnswer,valueMap);
			
			if (!bTerminateRequest && bPlanExpire && "UPDATE_REQUEST".equals(requestType)) {
				// Plan expired and grace period over: on the next CCR-Update push a Gx
				// RAR carrying the expiry policy (configured separately from FUP).
				sendRAR(diameterRequest,valueMap,strCustomerUsername,customerQuota,ReAuthReason.PLAN_EXPIRE);
			} else if(!bTerminateRequest && bRAREnableForQuotaExhausted && allowOverUsage) {
				sendRAR(diameterRequest,valueMap,strCustomerUsername,customerQuota,ReAuthReason.FUP);
			}

		} catch (CommunicationException e) {
			LogManager.getLogger().error("ServerCCRHandler", "CommunicationException : ", e);
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



	private String getCustomerUsername(DiameterRequest diameterRequest) {
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


	private String getUsedQuotas(DiameterRequest diameterRequest) {
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
								if("421".equalsIgnoreCase(String.valueOf(iDiameterAVP.getAVPCode()))) {
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

	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum paramApplicationEnum) {
		return null;
	}
	
	public static BigDecimal convert(long value, String toUnit) {
		
		if(toUnit == null || toUnit.isEmpty()) {
			toUnit = "BYTE";
		}

		String fromUnit = System.getenv("ENV_QUOTA_UNIT");
		if (fromUnit == null) {
			fromUnit = "BYTES";
		}
		
	    BigDecimal bytes;

	    // Step 1: Convert input to BYTES
	    switch (fromUnit.toUpperCase()) {
	        case "GB":
	            bytes = BigDecimal.valueOf(value).multiply(B_IN_GB);
	            break;

	        case "MB":
	            bytes = BigDecimal.valueOf(value).multiply(B_IN_MB);
	            break;

	        case "KB":
	            bytes = BigDecimal.valueOf(value).multiply(B_IN_KB);
	            break;

	        case "B":
	        case "BYTES":
	            bytes = BigDecimal.valueOf(value);
	            break;

	        default:
	            throw new IllegalArgumentException("Unsupported fromUnit: " + fromUnit);
	    }

	    // Step 2: Convert BYTES to target unit
	    switch (toUnit.toUpperCase()) {
	        case "GB":
	            return bytes.divide(B_IN_GB, 4, RoundingMode.HALF_UP);

	        case "MB":
	            return bytes.divide(B_IN_MB, 4, RoundingMode.HALF_UP);

	        case "KB":
	            return bytes.divide(B_IN_KB, 4, RoundingMode.HALF_UP);

	        case "B":
	        case "BYTES":
	            return bytes;

	        default:
	            throw new IllegalArgumentException("Unsupported toUnit: " + toUnit);
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
	private Map<String, String> convertCustomerQuotaToValueMap(CustomerQuota customerQuota,String strUsedQuotas,String strUsedTimeQuota,boolean bRAREnableForQuotaExhausted) {
		Map<String, String> valueMap = new HashMap<>();
		if (customerQuota == null) return valueMap;
		
		BigDecimal usedQuota = new BigDecimal(0);
		if(strUsedQuotas != null) {
			usedQuota=BigDecimal.valueOf(Long.valueOf(strUsedQuotas));
		}
		
		BigDecimal usedTimeQuota = new BigDecimal(0);
		if(strUsedTimeQuota != null) {
			usedTimeQuota=BigDecimal.valueOf(Long.valueOf(strUsedTimeQuota));
		}

		valueMap.put("customerQuota.quotaDtlsId", String.valueOf(customerQuota.getQuotaDtlsId()));
		valueMap.put("customerQuota.custId", String.valueOf(customerQuota.getCustId()));
		valueMap.put("customerQuota.planId", String.valueOf(customerQuota.getPlanId()));
		valueMap.put("customerQuota.quotaType", String.valueOf(customerQuota.getQuotaType()));
		if(bRAREnableForQuotaExhausted) {
			valueMap.put("customerQuota.totalQuota","0");
			valueMap.put("customerQuota.usedQuota","0");
			valueMap.put("customerQuota.timeTotalQuota","0");
			valueMap.put("customerQuota.timeQuotaUsed","0");
		}else {
			valueMap.put("customerQuota.totalQuota",customerQuota.getTotalQuota() !=null ? String.valueOf(convertQuota(customerQuota.getTotalQuota().doubleValue(), customerQuota.getQuotaUnit())):null);
			valueMap.put("customerQuota.usedQuota",customerQuota.getUsedQuota() !=null ? String.valueOf(convertQuota(customerQuota.getUsedQuota().doubleValue(), customerQuota.getQuotaUnit()))+usedQuota:null);
			valueMap.put("customerQuota.timeTotalQuota",customerQuota.getTimeTotalQuota() !=null ? String.valueOf(DiameterUtils.convertTime(customerQuota.getTimeTotalQuota().doubleValue(),customerQuota.getTimeQuotaUnit())):null);
			valueMap.put("customerQuota.timeQuotaUsed", customerQuota.getTimeQuotaUsed() !=null ?String.valueOf(DiameterUtils.convertTime(customerQuota.getTimeQuotaUsed().doubleValue(),customerQuota.getTimeQuotaUnit()))+usedTimeQuota:null);
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

		Map<String,String> valueMap =
				new HashMap<>();

		if(smsList == null || smsList.isEmpty()) {
			return valueMap;
		}

		CustSmsDetails sms =
				smsList.get(0);


		valueMap.put(
				"sms.smsType",
				String.valueOf(sms.getSmsType()));

		valueMap.put("sms.totalSms", String.valueOf(sms.getTotalSms()));

		valueMap.put("sms.usedSms", String.valueOf(sms.getUsedSms()));

		return valueMap;
	}

	private Map<String, String> convertVoiceDetailsToValueMap(
			List<CustVoiceDetails> voiceList) {

		Map<String,String> valueMap =
				new HashMap<>();

		if(voiceList == null || voiceList.isEmpty()) {
			return valueMap;
		}

		CustVoiceDetails voice =
				voiceList.get(0);


		valueMap.put(
				"voice.voiceType",
				String.valueOf(voice.getVoiceType()));

		if(voice.getTotalVoice() !=null) {
			valueMap.put("voice.totalVoice", String.valueOf(DiameterUtils.convertTimeToSecond(voice.getTotalVoice().doubleValue(),voice.getVoiceType())));
		}

		if(voice.getUsedVoice() !=null) {
			valueMap.put("voice.usedVoice", String.valueOf(DiameterUtils.convertTimeToSecond(voice.getUsedVoice().doubleValue(),voice.getVoiceType())));
		}

		return valueMap;
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
					LogManager.getLogger().error("ServerGyCCRHandler", "Failed to clone AVP template", e);
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
	
	private void sendRAR(DiameterRequest diameterRequest,Map<String,String> valueMap,String strCustomerUsername,CustomerQuota customerQuota,ReAuthReason reason) {

	    LogManager.getLogger().info("ServerGyCCRHandler", "Sending RAR for session: " + diameterRequest.getSessionID());

	    try {
	    	
	    	//Get Packet Mapping
			// Select the RAR packet mapping by reason so the expiry RAR can carry
			// values configured independently of the FUP RAR.
			String rarReqType = (reason == ReAuthReason.PLAN_EXPIRE) ? "Re-Auth-Request-Expiry" : "Re-Auth-Request";
			List<MappingHeader> mappingHeaders=mappingHeaderServiceImpl.getMappingsByRequestAndResponseType(rarReqType, "Re-Auth-Answer","GY",0,null);
			
			Map<String, MappingDetail> requestAvp = new HashMap<>();
			Set<MappingDetail> responseAvp = new HashSet<>();
			if (mappingHeaders != null && !mappingHeaders.isEmpty()) {
				for (MappingHeader mappingHeader : mappingHeaders) {
					if (mappingHeader.getApplication().equalsIgnoreCase("GY")) {
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
			
			

	        // Create RAR request (Command Code 258)
	    	DiameterRequest rar = new DiameterRequest(true);
	    	rar.setCommandCode(258);
	    	rar.setApplicationID(16777238);
	    	
	    	String strSessionId =diameterRequest.getAVPValue("0:263");
	    	String requestingHost = diameterRequest.getRequestingHost();
	    	String requestingRealm = diameterRequest.getAVPValue("0:296");
	    	
	    	if(customerQuota !=null) {
	    		DiameterCacheRequestModel  diameterCacheRequestModel= cacheManagerServiceImpl.getValue("GX-SESSION_ID"+customerQuota.getCustId(), DiameterCacheRequestModel.class);
	    		if(diameterCacheRequestModel !=null) {
	    			strSessionId = diameterCacheRequestModel.getSessionId();
	    			requestingHost = diameterCacheRequestModel.getRequestingHost();
	    			requestingRealm = diameterCacheRequestModel.getRequestingRealm();
	    		}
	    	}
	    	
	    	//Not send RAR if already sent (separate de-dup key per reason so expiry and FUP fire independently)
	    	String rarDedupKey = ((reason == ReAuthReason.PLAN_EXPIRE) ? "RAR-EXPIRE" : "RAR") + strSessionId;
	    	String strCacheSessionId=cacheManagerServiceImpl.getValue(rarDedupKey, String.class);
	    	if(strCacheSessionId !=null && !strCacheSessionId.isEmpty()) {
	    		LogManager.getLogger().info("ServerGyCCRHandler", "Not send RAR if already sent");
	    		return;
	    	}else {
	    		cacheManagerServiceImpl.setValueWithExpiry(rarDedupKey, strSessionId,1,TimeUnit.DAYS);
	    	}
	    	
	    	//Update Table (FUP status is FUP-specific; skip for plan-expiry RAR)
	    	if(reason == ReAuthReason.FUP && customerQuota !=null) {
	    		customerServiceImpl.updateFupStatusByQuotaId(customerQuota.getQuotaDtlsId());
	    	}

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

	        Set<String> groupAvpSet = new HashSet<>();
	        Map<String, IDiameterAVP> avpCache = new HashMap<>();
	        
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
	            		rar.addAvp(iDiameterAVP);
	            	}
	            }
	        }
	        for(String strGroupAvp:groupAvpSet) {
	        	IDiameterAVP iDiameterAVP = avpCache.get(strGroupAvp);
	        	rar.addAvp(iDiameterAVP);
	        }
	        
	        
	        DiameterSession diameterSession = (DiameterSession)stackContext.getOrCreateSession(strSessionId,16777238);

	        // Send RAR request
	        stackContext.getPeerCommunicator(requestingHost).sendServerInitiatedRequest(
	        		diameterSession,
	                rar,
	                new ResponseListener() {

						@Override
						public void requestTimedout(String paramString, DiameterSession paramDiameterSession) {
							LogManager.getLogger().error("RAR", "RAA timeout");
						}

						@Override
						public void responseReceived(DiameterAnswer paramDiameterAnswer, String paramString,
								DiameterSession paramDiameterSession) {
							LogManager.getLogger().info("RAR", "Received RAA: " + paramDiameterAnswer);
						}
	                }
	        );

	        //Store in Audit and CDR
            genericDiameterProcessor.process(rar, null,valueMap);
            
	        LogManager.getLogger().info("ServerGyCCRHandler", "RAR sent successfully");

	    } catch (Exception e) {
	        LogManager.getLogger().error("ServerGyCCRHandler", "Error sending RAR", e);
	    }
	}
	
	private void upsertAvp(DiameterAnswer answer, IDiameterAVP avp) {
	    IDiameterAVP existing = answer.getAVP(avp.getAVPId());
	    if (existing != null) {
	        answer.removeAVP(existing);
	    }
	    answer.addAvp(avp);
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



}
