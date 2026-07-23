package com.diameter.handler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * RO Server Handler - Handles CCR from PCEF ↔ OCS
 * and responds with online charging rules (CCA).
 */
public class ServerRoCCRHandler extends ApplicationListener{
	
	private static final String CLASS_NAME = "ServerRoCCRHandler";
	private static final org.slf4j.Logger METHOD_LOG = org.slf4j.LoggerFactory.getLogger(ServerRoCCRHandler.class);

	private IStackContext stackContext;
    private MappingHeaderServiceImpl mappingHeaderServiceImpl;
    private CustomerServiceImpl customerServiceImpl;
    private PlanServiceImpl planServiceImpl;
    private QOSPolicyServiceImpl qosPolicyServiceImpl;
    private LocalCacheManagerServiceImpl cacheManagerServiceImpl; 
    private GenericDiameterProcessor genericDiameterProcessor;
    private Integer maxChunkSizeMb = 100;
	
	public ServerRoCCRHandler(IStackContext stackContext, ApplicationEnum[] applicationEnums) {
        super(stackContext, applicationEnums);
        this.stackContext = stackContext;
        
    }

	@Override
	public String getApplicationIdentifier() {
		return Application.CC.name();
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
	
	public GenericDiameterProcessor getGenericDiameterProcessor() {
		return genericDiameterProcessor;
	}

	public void setGenericDiameterProcessor(GenericDiameterProcessor genericDiameterProcessor) {
		this.genericDiameterProcessor = genericDiameterProcessor;
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
	protected void processApplicationRequest(Session paramSession, DiameterRequest diameterRequest) {
		boolean bIsGyRequest= false;
		// Get Service-Information AVP (873)
	    IDiameterAVP serviceInfo = diameterRequest.getAVP("10415:873");
	    if(serviceInfo != null) {
	    	
	    	for (IDiameterAVP avp : serviceInfo.getGroupedAvp()) {
				if (avp.getAVPCode() == 874) {  // Check PS-Information (874)
					bIsGyRequest = true;
				}
			}
	    }
	    
	    if(bIsGyRequest && DiameterUtils.isDataRequest(diameterRequest) ) {
			ServerGyCCRHandler serverCCRHandler = new ServerGyCCRHandler(stackContext, getApplicationEnum());
			serverCCRHandler.setCustomerServiceImpl(customerServiceImpl);
			serverCCRHandler.setMappingHeaderServiceImpl(mappingHeaderServiceImpl);
			serverCCRHandler.setPlanServiceImpl(planServiceImpl);
			serverCCRHandler.setQOSPolicyServiceImpl(qosPolicyServiceImpl);
			serverCCRHandler.setCacheManagerServiceImpl(cacheManagerServiceImpl);
			serverCCRHandler.setGenericDiameterProcessor(genericDiameterProcessor);
			serverCCRHandler.setMaxChunkSizeMb(maxChunkSizeMb);
	    	serverCCRHandler.processRequest(paramSession, diameterRequest);
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
		
	    
	    LogManager.getLogger().info(CLASS_NAME, "Received CCR (RO) from: " + diameterRequest.getRequestingHost());
		
		// Build CCA (Credit-Control-Answer)
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);

		// Get Request Type
		String requestType = DiameterUtils.getRequestType(diameterRequest);
		boolean bTerminateRequest = DiameterUtils.isTerminationRequest(requestType);
		Map<String,String> valueMap = new HashMap<>();
		String strCustomerUsername = null;
		
		// Get Packet Mapping
		List<MappingHeader> mappingHeaders = mappingHeaderServiceImpl.getMappingsByRequestAndResponseType(
				"Credit-Control-Request", "Credit-Control-Answer", "RO", 0, requestType);

		Map<String, MappingDetail> requestAvp = new HashMap<>();
		Set<MappingDetail> responseAvp = new HashSet<>();
		if (mappingHeaders != null && !mappingHeaders.isEmpty()) {
			for (MappingHeader mappingHeader : mappingHeaders) {
				if (mappingHeader.getApplication().equalsIgnoreCase("RO") && (mappingHeader.getCcRequestType() == null)
						|| (mappingHeader.getCcRequestType() != null
								&& mappingHeader.getCcRequestType().equalsIgnoreCase(requestType))&& DiameterUtils.matchAvpConditions(mappingHeader.getAvpConditions(), diameterRequest)) {
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

		if (requestAvp.isEmpty() && responseAvp.isEmpty()) {

			// Add AVPs to indicate fail
			IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
			resultCodeAvp.setInteger(5005);
			diameterAnswer.addAvp(resultCodeAvp);

		} else {
			// Extract IMSI or subscriber ID (Subscription-Id AVP: Code 443)
			strCustomerUsername=  DiameterUtils.getCustomerUsername(diameterRequest);
			String strRequestedServiceUnit = null;
			String strUsedQuotasForVoice = null;

			if(bTerminateRequest || (strCustomerUsername !=null && !strCustomerUsername.isEmpty())) {
				
				List<Customer> customers = null;
				CustomerQuota customerQuota= null;
				Customer customer = null;
				CustomerPackageRel custPkgRel = null;
				CustSmsDetails smsDetails = null;
				CustVoiceDetails voiceDetails = null;
				if(strCustomerUsername !=null && !strCustomerUsername.isEmpty()) {
					//Get Customer from Database
					customers=customerServiceImpl.getCustomers(null,null, strCustomerUsername);
					
					//requestedServiceUnit
					strRequestedServiceUnit= DiameterUtils.getSMSServiceUnit(diameterRequest);
					strUsedQuotasForVoice = DiameterUtils.getUsedQuotasForVoice(diameterRequest);

					LogManager.getLogger().info(CLASS_NAME, "strCustomerUsername : "+strCustomerUsername);
					LogManager.getLogger().debug(CLASS_NAME, "getCustomers : "+customers);
				}
				
				if(customers !=null && !customers.isEmpty() && customers.get(0).getQuotas()!=null && !customers.get(0).getQuotas().isEmpty()) {
					customer=customers.get(0);

					// Convert all customer fields into String and add to valueMap
					valueMap.putAll(DiameterUtils.convertCustomerToValueMap(customer));

					customerQuota=customer.getQuotas().get(0);

					valueMap.putAll(DiameterUtils.convertCustomerQuotaToValueMap(customerQuota,strRequestedServiceUnit));

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
						valueMap.putAll(DiameterUtils.convertCustomerPlanToValueMap(plan));
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
					
					long lUsedTime = 0;
					if(strUsedQuotasForVoice !=null) {
						lUsedTime = Long.parseLong(strUsedQuotasForVoice);
					}
					valueMap.putAll(
							DiameterUtils.convertSmsDetailsToValueMap(relCustSmsDetails)
					);
					
					if(relCustSmsDetails != null && !relCustSmsDetails.isEmpty()) {
						smsDetails = relCustSmsDetails.get(0);
					}

					valueMap.putAll(
							DiameterUtils.convertVoiceDetailsToValueMap(relVoiceDetails,lUsedTime)
					);
					
					if(relVoiceDetails != null && !relVoiceDetails.isEmpty()) {
						voiceDetails = relVoiceDetails.get(0);
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
							QOSPolicyGatewayMapping mapping = gatewayMappings.get(0);
							valueMap.putAll(DiameterUtils.convertGatewayMappingToValueMap(mapping));
						}
					}
				}
				
				if(bTerminateRequest || (customers !=null && !customers.isEmpty() && customers.get(0).getQuotas()!=null && !customers.get(0).getQuotas().isEmpty())) {
					boolean bGracePeriodExpired = false;
					if (custPkgRel != null && custPkgRel.getEndDate() !=null && custPkgRel.getEndDate().before(new Timestamp(System.currentTimeMillis()))) {
						
						LogManager.getLogger().info(CLASS_NAME, "custPkgRel is :"+custPkgRel);
						
						bGracePeriodExpired = DiameterUtils.isGracePeriodExpired(diameterRequest, custPkgRel);
						if(bGracePeriodExpired) {
							LogManager.getLogger().info(CLASS_NAME, "Plan expired for subscriber");
						    
						    IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
						    resultCodeAvp.setInteger(4010); // DIAMETER_END_USER_SERVICE_DENIED
							diameterAnswer.addAvp(resultCodeAvp);
						}else {
							LogManager.getLogger().info(CLASS_NAME, "Plan expired for subscriber but grace period applied");
						}
						
					}
					
					if(!bGracePeriodExpired) {
						boolean bError = false;
						boolean bVoiceQuotaExhausted = false;
						
						//For Voice
						if(!DiameterUtils.isSmsTraffic(diameterRequest) && strRequestedServiceUnit == null && DiameterUtils.isVoiceRequest(diameterRequest)) {
							
							if(voiceDetails != null) {
								long lUsedTime = 0;
								if(strUsedQuotasForVoice !=null) {
									lUsedTime = Long.parseLong(strUsedQuotasForVoice);
								}
								BigDecimal usedTime = BigDecimal.valueOf(lUsedTime);

								// Current consumed
								BigDecimal totalUsed = BigDecimal.valueOf(DiameterUtils.convertTimeToSecond(voiceDetails.getUsedVoice().doubleValue(),voiceDetails.getVoiceType())).add(usedTime);
								BigDecimal totalVoice = BigDecimal.valueOf(DiameterUtils.convertTimeToSecond(voiceDetails.getTotalVoice().doubleValue(),voiceDetails.getVoiceType()));
								
								LogManager.getLogger().info(CLASS_NAME, "voiceDetails is : "+voiceDetails);
								LogManager.getLogger().info(CLASS_NAME, "voiceDetails totalVoice is : "+voiceDetails.getTotalVoice());
								LogManager.getLogger().info(CLASS_NAME, "voiceDetails usedVoice is : "+voiceDetails.getUsedVoice());
								LogManager.getLogger().info(CLASS_NAME, "voiceDetails voiceType is : "+voiceDetails.getVoiceType());
								LogManager.getLogger().info(CLASS_NAME, "totalUsed voice is : "+totalUsed);
								LogManager.getLogger().info(CLASS_NAME, "totalVoice voice is : "+totalVoice);
								
								// Check quota exhaustion
								if (!DiameterUtils.isUnlimited(voiceDetails.getVoiceType()) && totalUsed.compareTo(totalVoice) >= 0) {
									bVoiceQuotaExhausted = true;
									LogManager.getLogger().info(CLASS_NAME, "VOICE quota exhausted");
								}

								customerServiceImpl.updateVoiceQuotasByCustomerId(BigDecimal.valueOf(DiameterUtils.convertSecondTimeToUnit(usedTime.doubleValue(),voiceDetails.getVoiceType())), customer.getCustId(),
										customerQuota.getPlanId(), Long.valueOf(customerQuota.getCustPackageId()));
							}else {
								bVoiceQuotaExhausted = true;
							}
						}
						
						//For SMS
						if(!bTerminateRequest && customerQuota != null && customer!=null && smsDetails !=null && strRequestedServiceUnit != null) {
							BigDecimal usedTime=BigDecimal.valueOf(Long.valueOf(strRequestedServiceUnit));
							if (!DiameterUtils.isUnlimited(smsDetails.getSmsType()) && smsDetails.getTotalSms()
									.compareTo(usedTime.add(smsDetails.getUsedSms())) < 0) {
								
								bError= true;
							}
							if(!bError) {
								customerServiceImpl.updateSmsQuotasByCustomerId(usedTime, customer.getCustId(), customerQuota.getPlanId(),Long.valueOf(customerQuota.getCustPackageId()));
							}
						}else if (!DiameterUtils.isVoiceRequest(diameterRequest) && !bTerminateRequest) {
							bError= true;
						}
						
						if(!bError && !bVoiceQuotaExhausted) {
							diameterAnswer = DiameterUtils.createAnswerFromPacketMapping(diameterRequest, valueMap, responseAvp);
						}else {
							if(bVoiceQuotaExhausted) {
								DiameterUtils.getVoiceQuotaExhaustedAnswer(diameterAnswer);
							}else {
								// DIAMETER_CREDIT_LIMIT_REACHED
								IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
								resultCodeAvp.setInteger(4012);
								diameterAnswer.addAvp(resultCodeAvp);
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
			stackContext.getPeerCommunicator(diameterRequest.getRequestingHost())
					.sendAnswer(diameterRequest, diameterAnswer);
			
			//Store in Audit and CDR
            genericDiameterProcessor.process(diameterRequest, diameterAnswer,valueMap);
			
			LogManager.getLogger().info(CLASS_NAME, "CCA sent successfully for subscriber: " + strCustomerUsername);
		} catch (CommunicationException e) {
			LogManager.getLogger().error(CLASS_NAME, "CommunicationException while sending CCA: ", e);
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
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum paramApplicationEnum) {
		// TODO Auto-generated method stub
		return null;
	}

}
