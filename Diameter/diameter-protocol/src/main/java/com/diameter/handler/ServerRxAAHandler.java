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
import com.diameter.commons.DiameterSession;
import com.diameter.commons.DiameterUtils;
import com.diameter.commons.IDiameterAVP;
import com.diameter.commons.IStackContext;
import com.diameter.commons.LogManager;
import com.diameter.commons.ResponseListener;
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
 * RX Server Handler - P-CSCF → PCRF/PCF used for IMS service authorization and
 * responds with AAA.
 */
public class ServerRxAAHandler extends ApplicationListener {

	private static final String CLASS_NAME = "ServerRxAAHandler";

	private static final org.slf4j.Logger METHOD_LOG = org.slf4j.LoggerFactory.getLogger(ServerRxAAHandler.class);

	private IStackContext stackContext;
	private MappingHeaderServiceImpl mappingHeaderServiceImpl;
	private CustomerServiceImpl customerServiceImpl;
	private PlanServiceImpl planServiceImpl;
	private QOSPolicyServiceImpl qosPolicyServiceImpl;
	private LocalCacheManagerServiceImpl cacheManagerServiceImpl; 
	private GenericDiameterProcessor genericDiameterProcessor;

	public ServerRxAAHandler(IStackContext stackContext, ApplicationEnum[] applicationEnums) {
		super(stackContext, applicationEnums);
		this.stackContext = stackContext;

	}

	@Override
	public String getApplicationIdentifier() {
		return Application.TGPP_RX_29_214_18.name();
	}

	public void setMappingHeaderServiceImpl(MappingHeaderServiceImpl mappingHeaderServiceImpl) {
		this.mappingHeaderServiceImpl = mappingHeaderServiceImpl;
	}

	public void setCustomerServiceImpl(CustomerServiceImpl customerServiceImpl) {
		this.customerServiceImpl = customerServiceImpl;
	}

	public void setPlanServiceImpl(PlanServiceImpl planServiceImpl) {
		this.planServiceImpl = planServiceImpl;
	}

	public void setQOSPolicyServiceImpl(QOSPolicyServiceImpl qosPolicyServiceImpl) {
		this.qosPolicyServiceImpl = qosPolicyServiceImpl;
	}
	
	public LocalCacheManagerServiceImpl getCacheManagerServiceImpl() {
		return cacheManagerServiceImpl;
	}

	public void setCacheManagerServiceImpl(LocalCacheManagerServiceImpl cacheManagerServiceImpl) {
		this.cacheManagerServiceImpl = cacheManagerServiceImpl;
	}
	
	public GenericDiameterProcessor getGenericDiameterProcessor() {
		return genericDiameterProcessor;
	}

	public void setGenericDiameterProcessor(GenericDiameterProcessor genericDiameterProcessor) {
		this.genericDiameterProcessor = genericDiameterProcessor;
	}

	@Override
	protected void processApplicationRequest(Session paramSession, DiameterRequest diameterRequest) {
		long __mStart = System.currentTimeMillis();
		if (METHOD_LOG.isDebugEnabled()) {
			METHOD_LOG.debug(">> ENTRY processApplicationRequest sessionId={}", (paramSession != null ? paramSession.getSessionId() : "null"));
		}
		try {

		LogManager.getLogger().info(CLASS_NAME, "Received AAR (RX) from: " + diameterRequest.getRequestingHost());

		// Build AAA
		DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);

		// Get Request Type
		String requestType = DiameterUtils.getRxRequestType(diameterRequest);
		boolean bTerminateRequest = DiameterUtils.isTerminationRequest(requestType);
		boolean bUpdateRequest = DiameterUtils.isUpdateRequest(requestType);
		Map<String, String> valueMap = new HashMap<>();
		String strCustomerUsername = null;
		boolean bValid = false;
		Customer customer = null;
		
		// Get Packet Mapping
		List<MappingHeader> mappingHeaders = null;
		
		int commandCode = diameterRequest.getCommandCode();
        if (commandCode == 275) {
        	bTerminateRequest = true;
        	mappingHeaders = mappingHeaderServiceImpl.getMappingsByRequestAndResponseType(
    				"Session-Termination-Request", "Session-Termination-Answer", "RX", 0, null);
        }else {
        	mappingHeaders = mappingHeaderServiceImpl.getMappingsByRequestAndResponseType(
    				"Authentication-Authorization-Request", "Authentication-Authorization-Answer", "RX", 0, requestType);
        }

		Map<String, MappingDetail> requestAvp = new HashMap<>();
		Set<MappingDetail> responseAvp = new HashSet<>();
		if (mappingHeaders != null && !mappingHeaders.isEmpty()) {
			for (MappingHeader mappingHeader : mappingHeaders) {
				if (mappingHeader.getApplication().equalsIgnoreCase("RX") && (mappingHeader.getCcRequestType() == null)
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
			strCustomerUsername = DiameterUtils.getCustomerUsername(diameterRequest);

			if (bTerminateRequest || (strCustomerUsername != null && !strCustomerUsername.isEmpty())) {

				List<Customer> customers = null;
				CustomerQuota customerQuota = null;
				CustomerPackageRel custPkgRel = null;
				
				if (strCustomerUsername != null && !strCustomerUsername.isEmpty()) {
					// Get Customer from Database
					customers = customerServiceImpl.getCustomers(null, null, strCustomerUsername);

					LogManager.getLogger().info(CLASS_NAME, "strCustomerUsername : " + strCustomerUsername);
					LogManager.getLogger().debug(CLASS_NAME, "getCustomers : " + customers);
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

				if (bTerminateRequest || (customers != null && !customers.isEmpty() && customers.get(0).getQuotas()!=null && !customers.get(0).getQuotas().isEmpty())) {
					
					boolean bGracePeriodExpired = false;
					if (custPkgRel != null && custPkgRel.getEndDate() !=null && custPkgRel.getEndDate().before(new Timestamp(System.currentTimeMillis()))) {
					   
						String planExpiryCheck="true";
						if(DiameterUtils.isVoiceRequest(diameterRequest)) {
							planExpiryCheck = System.getenv("ENV_PLAN_EXPIRY_CHECK_IN_RX");
							if (planExpiryCheck == null) {
								planExpiryCheck ="false";
							}
						}
						if(planExpiryCheck.equalsIgnoreCase("true")) {
							bGracePeriodExpired = DiameterUtils.isGracePeriodExpired(diameterRequest, custPkgRel);
							
							if(bGracePeriodExpired) {
								LogManager.getLogger().info(CLASS_NAME, "Plan expired for subscriber");
							    
							    IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
							    resultCodeAvp.setInteger(4010); // DIAMETER_END_USER_SERVICE_DENIED
								diameterAnswer.addAvp(resultCodeAvp);
							}else {
								LogManager.getLogger().info(CLASS_NAME, "Plan expired for subscriber but grace period applied");
							}
						}else {
							LogManager.getLogger().info(CLASS_NAME, "ENV_PLAN_EXPIRY_CHECK_IN_RX set as false");
						}
						
						
						
					}
					if(!bGracePeriodExpired) {
						diameterAnswer = DiameterUtils.createAnswerFromPacketMapping(diameterRequest, valueMap,
								responseAvp);
						bValid = true;
					}

				} else {
					// Subscriber not found
					IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
					resultCodeAvp.setInteger(5032);
					diameterAnswer.addAvp(resultCodeAvp);
				}

			} else {
				// Subscriber Id is missing in request
				IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
				resultCodeAvp.setInteger(5005);
				diameterAnswer.addAvp(resultCodeAvp);
			}
		}

		try {
			
			if(bValid) {
				sendRAR(diameterRequest, diameterAnswer, valueMap, strCustomerUsername,customer);
			}else {
				stackContext.getPeerCommunicator(diameterRequest.getRequestingHost()).sendAnswer(diameterRequest,
						diameterAnswer);
				
				//Store in Audit and CDR
	            genericDiameterProcessor.process(diameterRequest, diameterAnswer,valueMap);
				
				LogManager.getLogger().info(CLASS_NAME, "AAA sent successfully for subscriber: " + strCustomerUsername);
			}
			
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

	private void sendRAR(DiameterRequest diameterRequest,DiameterAnswer diameterAnswer,Map<String,String> valueMap,String strCustomerUsername,Customer customer) {

	    LogManager.getLogger().info("ServerRxAAHandler", "Sending RAR for session: " + diameterRequest.getSessionID());

	    try {
	    	
	    	//Get Packet Mapping
			List<MappingHeader> mappingHeaders=mappingHeaderServiceImpl.getMappingsByRequestAndResponseType("Re-Auth-Request", "Re-Auth-Answer","GX",0,null);
			
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
			
			

	        // Create RAR request (Command Code 258)
	    	DiameterRequest rar = new DiameterRequest(true);
	    	rar.setCommandCode(258);
	    	rar.setApplicationID(16777238);
	    	
	    	String strSessionId =diameterRequest.getAVPValue("0:263");
	    	String requestingHost = diameterRequest.getRequestingHost();
	    	String requestingRealm = diameterRequest.getAVPValue("0:296");
	    	
	    	if(customer !=null) {
	    		DiameterCacheRequestModel  diameterCacheRequestModel= cacheManagerServiceImpl.getValue("GX-SESSION_ID"+customer.getCustId(), DiameterCacheRequestModel.class);
	    		if(diameterCacheRequestModel !=null) {
	    			strSessionId = diameterCacheRequestModel.getSessionId();
	    			requestingHost = diameterCacheRequestModel.getRequestingHost();
	    			requestingRealm = diameterCacheRequestModel.getRequestingRealm();
	    		}
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
	            	DiameterUtils.addNestedAvp(key.replaceAll("0:", "").replaceAll("10415:", ""), strValue, mappingDetail.getValueType().getDbValue(), String.valueOf(mappingDetail.getVendorId()),avpCache);
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
							LogManager.getLogger().error("ServerRxAAHandler", "RAA timeout");
						}

						@Override
						public void responseReceived(DiameterAnswer paramDiameterAnswer, String paramString,
								DiameterSession paramDiameterSession) {
							LogManager.getLogger().info("ServerRxAAHandler", "Received RAA: " + paramDiameterAnswer);
							
							try {
								stackContext.getPeerCommunicator(diameterRequest.getRequestingHost()).sendAnswer(diameterRequest,
										diameterAnswer);
								
								//Store in Audit and CDR
					            genericDiameterProcessor.process(diameterRequest, diameterAnswer,valueMap);
								
								LogManager.getLogger().info(CLASS_NAME, "AAA sent successfully for subscriber: " + strCustomerUsername);
							} catch (CommunicationException e) {
								LogManager.getLogger().error(CLASS_NAME, "CommunicationException while sending CCA: ", e);
							}
						}
	                }
	        );

	        //Store in Audit and CDR
            genericDiameterProcessor.process(rar, null,valueMap);
	        LogManager.getLogger().info("ServerRxAAHandler", "RAR sent successfully");

	    } catch (Exception e) {
	        LogManager.getLogger().error("ServerRxAAHandler", "Error sending RAR", e);
	    }
	}

	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum paramApplicationEnum) {
		return null;
	}

}
