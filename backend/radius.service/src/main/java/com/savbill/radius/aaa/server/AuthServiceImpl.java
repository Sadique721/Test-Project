package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.constant.RadiusAttributes;
import com.savbill.radius.aaa.constant.RequestType;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.redis.CacheServiceWithRedis;
import com.savbill.radius.aaa.db.DBAccountingDriver;
import com.savbill.radius.aaa.db.IPPoolManagementService;
import com.savbill.radius.aaa.eap.EAPAttribute;
import com.savbill.radius.aaa.eap.EAPMessageHandler;
import com.savbill.radius.aaa.eap.EAPPacket;
import com.savbill.radius.aaa.eap.util.EAPUtility;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.snmp.SNMPCounters;
import com.savbill.radius.aaa.util.ValidateExpression;
import com.savbill.radius.config.CacheRetrival;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.ippool.domain.IPPoolMapping;
import com.savbill.radius.utils.CommonConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class AuthServiceImpl {


    private static final String REPLY_MESSAGE = "Reply-Message";
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private String authenticationMode;
    //	AuthAcctUtilityImpl authAcctUtilityImpl=new AuthAcctUtilityImpl();
    AuthAcctUtilityServiceImpl authAcctUtilityService = new AuthAcctUtilityServiceImpl();

    private EAPMessageHandler eapMessageHandler;


    /**
     * Method for Auth Response
     *
     * @param request Radius request packet
     * @param client  address of Radius client
     * @return
     */
    public RadiusPacket accessRequestReceived(AccessRequest request, InetSocketAddress client) {
        long startTime = System.currentTimeMillis();
        log.debug("Authenticate Request Received: " + request.toString());
        RadiusPacket accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, request.getPacketIdentifier());
        String strSNMPType = accessResponse.getPacketTypeName();
        try {
            RadiusUtility radUtil = new RadiusUtility();
            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();

            CustomerData custRetrunData = null, custData = null;
            String reason = "";
            String strPassword = request.getUserPassword();

            //Identify Client
            Client cltData = radUtil.identifyClient(client.getAddress().toString().substring(1), request);
            ClientGroup cltGroupData = null;
            // This is to get Client Group Detail
            if (cltData == null || cltData.getClientGroupData() == null) {
                return accessResponse;
            } else {
                cltGroupData = cltData.getClientGroupData();
                if (log.isDebugEnabled()) {
                    log.debug("For Client Group:" + cltData.getClientGroupData().getName());
                }
                log.debug("For Client Group:" + cltData.getClientGroupData().getName());
            }


            //Process Radius Policy
            CacheRetrival cacheRetrival = new CacheRetrival();
            List<RadiusProfile> profileList = cacheRetrival.getAuthProfileData();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Radius Profiles size getAuthProfileData: %d", profileList.size()));
            }
            if (cltData.getMvnoId() != null && cltData.getMvnoId() != 1) //Check for client data in super admin Jira: ANG-10754
                profileList = profileList.stream().filter(radiusProfile -> radiusProfile.getMvnoId() == cltData.getMvnoId()).collect(Collectors.toList());

            String strUsername;
            VLANManagement vlanManagement = null;
            for (RadiusProfile radiusProfile : profileList) {
                //Trigger COA for Nokia for attributeType value 96
                // check using vendorId
                ValidateExpression validate = new ValidateExpression();
                boolean blnResponse = validate.checkExpression(radiusProfile.getCheckItem(), request, null);
                log.debug(String.format("Expression Check For %s : %s", radiusProfile.getName(), blnResponse));
                if (blnResponse) {

                    strUsername = radUtil.getUserNameFromRequest(request, radiusProfile);
                    String mac_attr = cltData.getClientGroupData().getCustomerMacAttribute();
                    if (mac_attr == null) {
                        mac_attr = "Calling-Station-Id";
                    }
                    log.debug(String.format("Processing Data with Profile %s %s", radiusProfile.getName(), radiusProfile.getRadiusProfileId()));
                    String strCalling = authAcctUtilityService.getRequestAttribute(request, mac_attr);
                    log.warn("Username:" + request.getUserName()+":ClientGroup:"+cltData.getClientGroupData().getName()+":MAC:"+strCalling+":RadiusProfile:"+radiusProfile.getRadiusProfileId()+":");
                    boolean faultyMacFound = false;
                    try {

                        //verify vlan with attribute configuration in radius profile
                        try {
//                            String vlanAttribute = cltGroupData.getVlanAttribute();//radiusProfile.getVlanAttribute();
                            if (!CollectionUtils.isEmpty(cltGroupData.getVlanProfileMapping())) {
                                long startTime1 = System.currentTimeMillis();
                                vlanManagement = radUtil.verifyVlan1(request, custRetrunData, cltGroupData, cltData.getMvnoId());
                                if (vlanManagement != null) {
                                    log.warn(String.format("Vlan Matched for username: %s, vlan name: %s", request.getAttribute("User-Name").getAttributeValue(), vlanManagement.getVlanName()));
                                } else if (cltGroupData.isVlanCheckRequired()) {
                                    log.warn(String.format("Vlan Not Matched for username: %s", request.getAttribute("User-Name").getAttributeValue()));
                                    accessResponse.addAttribute(REPLY_MESSAGE, "Vlan Not Matched");
                                    try {
                                        if (radiusProfile.getAuthAudit().equalsIgnoreCase("Enable")) {
                                            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reason) && custData != null) {
                                                reason = "Vlan Not Matched";
                                            }
                                            radaysn.AuthenticateAudit(request.getUserName(), Integer.toString(accessResponse.getPacketType()), "Vlan Not Matched", client.getAddress().toString().substring(1), cltGroupData.getClientGroupId().toString(), cltData.getMvnoId());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    long endTime = System.currentTimeMillis();
                                    log.warn("Time Taken: for accessRequestReceived: " + (endTime - startTime) + "And Authentication Response: "+ accessResponse.toString());
                                    return accessResponse;
                                }
                                long endTime1 = System.currentTimeMillis();
                                log.error("Time Taking in Vlan Match: " + (endTime1 - startTime1));
                            } else {
                                log.info("Vlan attribute not configured so skip vlan validation");
                            }
                        } catch (Exception ex) {
                            log.error("Error while vlan validation: " + ex.getMessage());
                        }

                        //TODO This is hardcode need provide solution in future. For ACT this has been done.
                        if (request.getAttribute(6527, 96) != null) {
                            String authenticationMode = radiusProfile.getAuthenticationMode();
                            String strIPAddress = authAcctUtilityService.getRequestAttribute(request, "Framed-IP-Address");
                            CustomerData data = null;
                            data = radUtil.getCustomerDetailsForAccessRequest(data, authenticationMode, strIPAddress, request, cltData, strUsername, strPassword, strCalling, false, "Auth");
                            log.debug("COA Request Raised For Nokia:: [ with request: [" + request.getAttributes().toString() + "]");
                            triggerCOADMAfterAccessRequest(data, cltData, client, request, radiusProfile.isTerminateSessionOnDuplicateMac());
                            Thread.sleep(20);
                            log.debug("COA Response Receive For NoKia Moving Ahead");
                        }
                        //check faulty mac
                        RadiusUtility radiusUtility = new RadiusUtility();
                        String finalMac = radiusUtility.normalizeMacAddress(strCalling);
                        String formattedUsername = radiusUtility.normalizeMacAddress(strUsername);
                        CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                        if (finalMac != null && cacheService.get(finalMac) == null && formattedUsername.equalsIgnoreCase(finalMac)) {
                            log.info("Username and Mac Same so checking Faulty MAC: " + finalMac + " strUsername: " + formattedUsername);
                            Map<String, FaultyMAC> faultyMacList = cacheRetrival.getFaultyMacList();
                            if (faultyMacList != null && !CollectionUtils.isEmpty(faultyMacList)) {
                                FaultyMAC faultyMAC = faultyMacList.get(finalMac);
                                if (faultyMAC != null) {
//                                if (faultyMACS.contains(strCalling)) {
                                    log.warn("Fault MAC found rejecting request with mac: " + strCalling);
                                    custRetrunData = new CustomerData();
                                    if (vlanManagement != null) {
                                        custRetrunData.setVlanManagement(vlanManagement);
                                    }
                                    RadiusAsyncUtility radiusAsyncUtility = new RadiusAsyncUtility();
                                    radiusAsyncUtility.UpdateFaultyMacData(faultyMAC, LocalDateTime.now());
                                    authAcctUtilityService.handleUnknownUser(custRetrunData, accessResponse, cltData, reason, strUsername, request);
                                    custData = custRetrunData;
                                    if (accessResponse.getPacketType() == AAAConstant.ACCESS_REJECT) {
                                        strSNMPType = "Access-Reject";
                                    } else if (accessResponse.getPacketType() == AAAConstant.ACCESS_ACCEPT) {
                                        strSNMPType = "Access-Accept";
                                    }
                                    faultyMacFound = true;
                                } else {
                                    log.info("Fault MAC Not found continue request with mac: " + strCalling);
                                }
                            }
                        } else {
                            log.info("Username and Mac are Not Same or Faulty mac not found or mac available in cache so Skipp checking Faulty MAC: " + strCalling + " strUsername: " + strUsername);
                        }

                        if (!faultyMacFound && radiusProfile.getProxyServer() != null) {
                            log.debug(String.format("Proxy Configured : %s", radiusProfile.getProxyServer().getId()));
                            try {
                                accessResponse = radUtil.proxyPacket(request, radiusProfile.getProxyServer(), client);
                                if (accessResponse.getPacketType() == AAAConstant.ACCESS_REJECT) {
                                    strSNMPType = "Proxy-Access-Reject";
                                } else if (accessResponse.getPacketType() == AAAConstant.ACCESS_ACCEPT) {
                                    strSNMPType = "Proxy-Access-Accept";
                                }
                                log.debug("Proxy Response : " + accessResponse.getAttributes());
                                if (accessResponse.getAttribute("Reply-Message") != null) {
                                    reason = accessResponse.getAttribute("Reply-Message").getAttributeValue();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                accessResponse.addAttribute(REPLY_MESSAGE, "Proxy Server Not Responding");
                                strSNMPType = "Proxy-Access-Reject";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        accessResponse.addAttribute(REPLY_MESSAGE, "Proxy Server Not Responding");
                        strSNMPType = "Proxy-Access-Reject";
                    }

                    try {
                        if (!faultyMacFound && radiusProfile.getProxyServer() == null) {
                            log.debug("Client address: " + cltData.getClientId());
                            log.debug("Authentication Type of selected radius profile is: " + radiusProfile.getAuthenticationType());
                            List<RadiusAttribute> eapAttributes = request.getAttributes(AccessRequest.EAP);

                            String authenticationType = radiusProfile.getAuthenticationType();
                            if (CommonConstants.AUTH_TYPE_EAP_TLS.equalsIgnoreCase(authenticationType) || CommonConstants.AUTH_TYPE_EAP_TTLS.equalsIgnoreCase(authenticationType)) {
                                log.debug("Checking for EAP Packet in access request");

                                if (!eapAttributes.isEmpty()) {
                                    boolean isValid = EAPUtility.validateEAPRequest(request, cltData.getSharedKey());

                                    if (isValid) {
                                        log.debug("Valid EAP Packet");
                                        accessResponse = eapMessageHandler.handleEapMessage(request, cltData, radiusProfile.getKeyStore(), authenticationType);
                                    } else {
                                        log.debug("Invalid EAP Packet");
                                        accessResponse.addAttribute(REPLY_MESSAGE, "Invalid EAP packet");
                                        accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, request.getPacketIdentifier());
                                    }

                                } else {
                                    log.debug("EAP packet not found");
                                    accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, request.getPacketIdentifier());
                                    accessResponse.addAttribute(REPLY_MESSAGE, "Missing EAP attribute");
                                }
                            } else if (CommonConstants.AUTH_TYPE_PAP.equalsIgnoreCase(radiusProfile.getAuthenticationType()) || CommonConstants.AUTH_TYPE_CHAP.equalsIgnoreCase(radiusProfile.getAuthenticationType())) {
                                if (!eapAttributes.isEmpty()) {
                                    accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, request.getPacketIdentifier());
                                    accessResponse.addAttribute(REPLY_MESSAGE, "Unsupported Authentication Type");
                                }
                            }

                            if (CommonConstants.AUTH_TYPE_PAP.equalsIgnoreCase(radiusProfile.getAuthenticationType()) || (CommonConstants.AUTH_TYPE_EAP_TTLS.equalsIgnoreCase(radiusProfile.getAuthenticationType()) && request.getUserPassword() != null && !request.getUserPassword().isEmpty())) {
                                custData = authAcctUtilityService.localDBValidation(custRetrunData, accessResponse, request, cltData, request.getAttributeValue(RadiusAttributes.USER_NAME.getName()), request.getUserPassword(), reason, radiusProfile, vlanManagement);
                                if (accessResponse.getPacketType() == AAAConstant.ACCESS_REJECT) {
                                    strSNMPType = "Access-Reject";
                                } else if (accessResponse.getPacketType() == AAAConstant.ACCESS_ACCEPT) {
                                    strSNMPType = "Access-Accept";
                                }
                            }

                        }
                    } catch (SQLException e) {
                        log.error("Error while perfoming operation timeout ", e);
                        accessResponse.addAttribute(REPLY_MESSAGE, "SQL Error Authentication Fail");
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        strSNMPType = "Access-Reject";

                    } catch (Exception ep) {
                        accessResponse.addAttribute(REPLY_MESSAGE, "Internal Error Authentication Fail");
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        log.error("Error while performing operation", ep);
                        strSNMPType = "Access-Reject";
                    }

                    try {
                        if (radiusProfile.getAuthAudit().equalsIgnoreCase("Enable")) {
                            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(reason) && custData != null) {
                                reason = custData.getStrReplyMessage();
                            }
                            radaysn.AuthenticateAudit(request.getUserName(), Integer.toString(accessResponse.getPacketType()), reason, client.getAddress().toString().substring(1), cltGroupData.getClientGroupId().toString(), cltData.getMvnoId());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if ((CommonConstants.AUTH_TYPE_EAP_TLS.equalsIgnoreCase(radiusProfile.getAuthenticationType()) || CommonConstants.AUTH_TYPE_EAP_TTLS.equalsIgnoreCase(radiusProfile.getAuthenticationType())) &&
                            (RequestType.AccessAccept.getValue() == accessResponse.getPacketType() || RequestType.AccessReject.getValue() == accessResponse.getPacketType())) {

                        if (CommonConstants.AUTH_TYPE_EAP_TLS.equalsIgnoreCase(radiusProfile.getAuthenticationType()) && RequestType.AccessAccept.getValue() == accessResponse.getPacketType()) {
                            RadiusUtility radiusUtility = new RadiusUtility();
                            radiusUtility.processReplyItem(accessResponse, null, request, cltGroupData.getClientGroupId(), cltData, false);
                        } else if (CommonConstants.AUTH_TYPE_EAP_TTLS.equalsIgnoreCase(radiusProfile.getAuthenticationType()) && RequestType.AccessReject.getValue() == accessResponse.getPacketType()) {
                            EAPPacket eapPacket = new EAPPacket(4, ((EAPAttribute) accessResponse.getAttribute(79)).getResponsePacket().getEapIdentifier(), 4);

                            EAPAttribute eapPacket1 = new EAPAttribute(AccessRequest.EAP);
                            eapPacket1.setAttributeValue(eapPacket);
                            accessResponse.addAttribute(eapPacket1);

                            accessResponse.removeAttribute(accessResponse.getAttribute(79));
                            //accessResponse.removeAttribute(accessResponse.getAttribute(311, 16));
                            //accessResponse.removeAttribute(accessResponse.getAttribute(311, 17));
                            accessResponse.removeAttribute(accessResponse.getAttribute(26));
                            accessResponse.removeAttribute(accessResponse.getAttribute(26));

                        }
                        EAPMessageHandler.formEapPacket(accessResponse, request, cltData);
                    }
                    //Remove Mac if Mac Auth disable

                    // Dynamic IP Allocation
                    dynamicIpAllocationToCustomer(request, accessResponse, cltData, custData);

                    break;
                }
            }

            if (custRetrunData != null) {
                log.info(String.format("Final Data Dumping Username : %s :Message: %s :Type: %s :Connect-Info: %s :IP: %s", request.getUserName(), custRetrunData.getStrReplyMessage(), accessResponse.getPacketType(), cltData.getClientGroupData().getClientGroupId(), client.getAddress().toString().substring(1)));
            } else {
                log.info(String.format("Final Data Dumping Username : %s :Message:  :Type: %s :Connect-Info %s :IP: %s", request.getUserName(), accessResponse.getPacketType(), cltData.getClientGroupData().getClientGroupId(), client.getAddress().toString().substring(1)));
            }
            long lendTime = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
                log.info("Total Time To Process Request in Seconds:" + (lendTime - startTime) + ":ID:" + accessResponse.getPacketIdentifier());
            }
        } catch (Exception e) {
            accessResponse.addAttribute(REPLY_MESSAGE, "Internal Error Authentication Fail");
            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
            log.error("Error while performing operation", e);
            strSNMPType = "Access-Reject";
        } finally {
            snmpCounterForAuthRequest("Access-Request");
            snmpCounterForAuthRequest(strSNMPType);
        }
        long endTime = System.currentTimeMillis();
        log.info("Time Taken: for accessRequestReceived: " + (endTime - startTime) + "And Authentication Response: "+ accessResponse.toString());
        return accessResponse;
    }

    private void snmpCounterForAuthRequest(String packetTypeName) {
        SNMPCounters snmpCounters = new SNMPCounters();
        if (packetTypeName != null) {
            if (packetTypeName.equalsIgnoreCase("Access-Request")) {
                snmpCounters.incrementAuthRequest();
            }
            if (packetTypeName.equalsIgnoreCase("Access-Accept")) {
                snmpCounters.incrementAuthSuccess();
            } else if (packetTypeName.equalsIgnoreCase("Access-Reject")) {
                snmpCounters.incrementAuthFail();
            } else if (packetTypeName.equalsIgnoreCase("Proxy-Access-Accept")) {
                snmpCounters.incrementProxyAuthSuccess();
            } else if (packetTypeName.equalsIgnoreCase("Proxy-Access-Reject")) {
                snmpCounters.incrementProxyAuthFail();
            }
        }
    }

    public void setEapMessageHandler(EAPMessageHandler eapMessageHandler) {
        this.eapMessageHandler = eapMessageHandler;
    }

    private void dynamicIpAllocationToCustomer(RadiusPacket accessRequest, RadiusPacket accessResponse, Client client, CustomerData customerData) throws SQLException {
        IPPoolManagementService ipPoolManagementService = new IPPoolManagementService();
        List<IPPoolMapping> ipPoolMappingList = client.getIpPoolMappingList();

        if (!ipPoolMappingList.isEmpty() && AAAConstant.ACCESS_ACCEPT == accessResponse.getPacketType()) {

            List<Long> poolIdList = ipPoolMappingList.stream().map(IPPoolMapping::getIpPoolId).collect(Collectors.toList());

            String acct_session_attr = client.getClientGroupData().getDynamicAcctSessionAttribute();
            if (acct_session_attr == null) {
                acct_session_attr = "Acct-Session-Id";
            }
            String selectedIp = ipPoolManagementService.allocateFreeIpFromPool(poolIdList, customerData.getCustid(), accessRequest.getAttributeValue(acct_session_attr), accessRequest.getAttributeValue(RadiusAttributes.NAS_IP_ADDRESS.getName()));

            if (selectedIp != null && !selectedIp.trim().isEmpty()) {
                accessResponse.addAttribute(client.getRadiusAttribute(), selectedIp);
                log.debug(String.format("Ip allocated successfully for customer: %s Allocated  IP is: %s ", customerData.getUsername(), selectedIp));
            } else {
                log.debug("IP-Pool Exhausted");
                if (!client.getAcceptOnIpNotFound()) {
                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                    accessResponse.addAttribute(REPLY_MESSAGE, "IP not available");
                }
            }
        }
    }

    private void triggerCOADMAfterAccessRequest(CustomerData customerData, Client cltData, InetSocketAddress client, AccessRequest request, boolean terminateSessionOnDuplicateMac) {
        log.warn("In Trigger COA for Nokia, User: " + customerData.getUsername() + " request: [" + request.getAttributes().toString() + "]");
        try {

            if (customerData.getCustomerBasePlan() != null && customerData.getCustomerBasePlan().get(0).isUpdateVolumeQuota()) {
                log.warn("Quota exhaust so set total unused volume quota for COA original quota: " + customerData.getCustomerBasePlan().get(0).getVolumequota() + " updated quota: " + customerData.getCustomerBasePlan().get(0).getTotalvolumebasedunusedquota());
                customerData.getCustomerBasePlan().get(0).setVolumequota(customerData.getCustomerBasePlan().get(0).getTotalvolumebasedunusedquota());
            }
            ClientGroup cltGroupData = cltData.getClientGroupData();
            //COA Profile
            ValidateExpression validate = new ValidateExpression();
            CacheRetrival cacheRetrival = new CacheRetrival();
            Long CoAProfileId = 0L, DMProfileId = 0L;
            RadiusUtility radiusUtility = new RadiusUtility();
            String strDMCoAIP = client.getAddress().toString().substring(1);
            if(request.getAttribute("NAS-IP-Address") != null)
                strDMCoAIP = request.getAttributeValue("NAS-IP-Address");
            List<CoaDmProfileMapping> coaProfileMappings = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getCoaProfileId() != null).sorted(Comparator.comparing(CoaDmProfileMapping::getPriority).reversed()).collect(Collectors.toList());
            for (CoaDmProfileMapping profileMapping : coaProfileMappings) {
                log.info(String.format("Expression Check For %s:", profileMapping.getCheckItem()));
                boolean response = validate.checkExpression(profileMapping.getCheckItem(), request, customerData, "AUTH_COA");
                if (response && profileMapping.getCheckItem() != null && profileMapping.getCheckItem().length() > 0) {
                    CoAProfileId = profileMapping.getCoaProfileId();
                    break;
                }
            }
            if (CoAProfileId == null || CoAProfileId == 0L) {
                Optional<CoaDmProfileMapping> coaProfileMapping = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getCoaProfileId() != null && coaProfileList.getCheckItem().equals(null)).findFirst();
                if (coaProfileMapping.isPresent()) {
                    CoAProfileId = coaProfileMapping.get().getCoaProfileId();
                    log.info("COA Profile ID:", CoAProfileId);
                } else {
                    CoAProfileId = 0L;
                    log.error("COA Profile not found..!");
                }
            }
            CoaDMProfile coaProfileData = null;
            List<CoaDMProfile> coaDMProfileDataList = cacheRetrival.getCoADMProfileData();
            RadiusPacket coaDMResponse = null;
            try {
                if (coaProfileData == null && CoAProfileId != 0L) {
                    if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                        for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                            if (CoAProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                coaProfileData = coaDMProfileDataList.get(i);
                                coaProfileData.setGateway(strDMCoAIP);
                            }
                        }
                    }
                }

                if (coaProfileData != null) {
                    log.debug("coaProfileData Profile Found : " + coaProfileData.getName() + ":Type:" + coaProfileData.getType());
                    AccountingRequest acctRequest = new AccountingRequest();
                    String nasIpAddress = null;
                    if (customerData.getNasIPAddress() != null)
                        acctRequest.addAttribute("NAS-IP-Address", customerData.getNasIPAddress());
                    else if (request.getAttribute("NAS-IP-Address") != null) {
                        acctRequest.addAttribute("NAS-IP-Address", request.getAttribute("NAS-IP-Address").getAttributeValue());
                    }
                    if (request.getAttributeValue("Acct-Session-Id") != null)
                        acctRequest.addAttribute("Acct-Session-Id", request.getAttributeValue("Acct-Session-Id"));
                    else if (request.getAttribute("Acct-Session-Id") != null) {
                        acctRequest.addAttribute("Acct-Session-Id", request.getAttribute("Acct-Session-Id").getAttributeValue());
                    }
                    if (customerData.getFramedIPAddress() != null)
                        acctRequest.addAttribute("Framed-IP-Address", customerData.getFramedIPAddress());
                    else if (request.getAttribute("Framed-IP-Address") != null) {
                        acctRequest.addAttribute("Framed-IP-Address", request.getAttribute("Framed-IP-Address").getAttributeValue());
                    }
                    if (request.getAttribute(RadiusAttributes.CLASS.getName()) != null) {
                        acctRequest.addAttribute(RadiusAttributes.CLASS.getName(), request.getAttributeValue(RadiusAttributes.CLASS.getName()));
                    }
                    String mac_attr = cltGroupData.getCustomerMacAttribute();
                    if (mac_attr == null) {
                        mac_attr = "Calling-Station-Id";
                    }
                    String strCalling = null;
                    if (request.getAttribute(mac_attr) != null) {
                        strCalling = request.getAttribute(mac_attr).getAttributeValue();
                    }//NAS-IP-ADDRESS
                    if (strCalling != null)
                        acctRequest.addAttribute(RadiusAttributes.CALLING_STATION_ID.getName(), strCalling);
                    DBAccountingDriver dbAccountingDrive = new DBAccountingDriver();
                    LiveUser liveUser = null;
                    if (acctRequest.getAttribute("NAS-IP-Address") != null) {
                        liveUser = dbAccountingDrive.validateExistingCustomerSessionUsingNasAndCallingstation(strCalling, acctRequest.getAttribute("NAS-IP-Address").getAttributeValue(), terminateSessionOnDuplicateMac);
                    } else {
                        liveUser = dbAccountingDrive.validateExistingCustomerSession(strCalling, terminateSessionOnDuplicateMac);
                    }
                    if (liveUser != null) {
                        log.info("Live user Found: " + liveUser.getUserName() + " class: " + liveUser.getlClass() + " for COA/DM: " + coaProfileData.getName());
                        if (liveUser.getlClass() != null)
                            acctRequest.addAttribute("User-Name", liveUser.getlClass());
                        else
                            acctRequest.addAttribute("User-Name", liveUser.getUserName());
                        RadiusUtility radUtil = new RadiusUtility();
                        customerData = radUtil.getCustomerDetailsForAccessRequest(customerData, "username_or_mac", null, request, cltData, liveUser.getUserName(), request.getUserPassword(), strCalling, false, "Auth");
                        log.info("Live user updated customer data: " + customerData.getUsername());
                    } else {
                        log.error("Live user not found for COADM: " + coaProfileData.getName());
                    }
                    String username = null;
                    if (acctRequest.getAttribute("User-Name") != null)
                        username = acctRequest.getAttribute("User-Name").getAttributeValue();
                    if (acctRequest.getAttribute("User-Name") == null && customerData.getUsername() != null) {
                        acctRequest.addAttribute("User-Name", customerData.getUsername());
                        username = customerData.getUsername();
                    }
                    coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, acctRequest, username, customerData, strDMCoAIP);
                    log.warn("COA/DM response:"+coaDMResponse+":For Event:AUTH_COA"+":");
                    if(coaDMResponse!=null){
                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),"AUTH_COA", cltData.getMvnoId(),coaDMResponse);
                    }
                    else{
                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs","AUTH_COA", cltData.getMvnoId(),coaDMResponse);
                    }
                } else {
                    log.debug("CoA/DM Profile Not Found Skipping CoA/DM");
                }
            } catch (Exception e) {
                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout","AUTH_COA", cltData.getMvnoId(),coaDMResponse);
                log.error("CoA/DM Failed:" + e.getMessage());
            }

        } catch (Exception ex) {
            log.error("Error to trigger COA/DM: " + ex.getMessage());
        }
    }

}
