package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.constant.RadiusAttributes;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerPlanData;
import com.savbill.radius.aaa.data.PlanQosPolicyMapping;
import com.savbill.radius.aaa.data.redis.CacheServiceWithRedis;
import com.savbill.radius.aaa.db.DBAccountingDriver;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.aaa.db.IPPoolManagementService;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.snmp.SNMPCounters;
import com.savbill.radius.aaa.util.RadiusException;
import com.savbill.radius.aaa.util.ValidateExpression;
import com.savbill.radius.config.CacheRetrival;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.ippool.domain.IPPoolMapping;
import com.savbill.radius.kafka.message.CustomerPackageRelMessage;
import com.savbill.radius.kafka.message.CustomerQuotaInfo;
import com.savbill.radius.services.impl.CustomerServiceImpl;
import com.savbill.radius.utils.CommonConstants;
import com.savbill.radius.utils.RadiusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.savbill.radius.utils.CommonConstants.*;
import static com.savbill.radius.utils.RadiusConstants.*;


public class AcctingServiceImpl {


    private static final String ACCT_STATUS_TYPE = "Acct-Status-Type";
    private static final Logger log = LoggerFactory.getLogger(AcctingServiceImpl.class);

    AuthAcctUtilityServiceImpl authAcctUtilityImpl = new AuthAcctUtilityServiceImpl();
    CoADMImpl coADMImpl = new CoADMImpl();

    /**
     * Method for Accounting start and stop Response
     *
     * @param request Radius request packet
     * @param client  address of Radius client
     * @return
     */
    public RadiusPacket accountingRequestReceived(AccountingRequest request, InetSocketAddress client) {
        long startTime = System.currentTimeMillis();
        log.info("AcctStatusValue at start : " + request.getAttributeValue(ACCT_STATUS_TYPE) + " time: " + (LocalDateTime.now()));
        log.warn("Accounting Request Received: " + request.toString());
        RadiusPacket accoutningResponse = new RadiusPacket(AAAConstant.ACCOUNTING_RESPONSE, request.getPacketIdentifier());
        String authenticationType = RadiusUtils.readValueFromProperties("radius.authentication.type");
        if (authenticationType == null) {
            authenticationType = CommonConstants.AUTHENTICATION_TYPE_DEPENDENT;
        }

        SNMPCounters snmpCounters = new SNMPCounters();
        RadiusUtility radiusUtility = new RadiusUtility();
        CacheRetrival cacheRetrival = new CacheRetrival();
        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
        DBAccountingDriver dbAcct = new DBAccountingDriver();
        CustomerQuotaInfo custQuotaInfo = new CustomerQuotaInfo();

        String attribClass = null, AcctStatusValue = "", strSNMP = request.getAttributeValue(ACCT_STATUS_TYPE).toString();
        ClientGroup cltGroupData = null;
        Double percentageUsage = 0d;
        boolean isBandwidthQuotaupdate = false;

        try {
            // Identification of Trusted Client
            String strDMCoAIP = client.getAddress().toString().substring(1);
            if(request.getAttribute("NAS-IP-Address") != null)
                strDMCoAIP = request.getAttributeValue("NAS-IP-Address");
            Client cltData = radiusUtility.identifyClient(strDMCoAIP, request);
            Long CoAProfileId = 0L, DMProfileId = 0L;
            if (cltData.getClientGroupData() != null) {
                 cltGroupData = (ClientGroup) cltData.getClientGroupData();
                if (log.isDebugEnabled()) {
                    log.info("For Client Group:" + cltData.getClientGroupData().getClientGroupId() + ":DATA:" + cltGroupData.getName() + ":mvnoid:" + cltData.getMvnoId());
                }
            }
            if (cltData == null || cltGroupData == null) {
                log.warn("Request From Unknown Client Rejected");
                return null;  //Unknown Client Return Null
            }
            // add Accounting-On and Accounting-Off development for : Jira=>SUP-1422
            if (request.getAttributeValue(ACCT_STATUS_TYPE).equalsIgnoreCase("Accounting-On") || request.getAttributeValue(ACCT_STATUS_TYPE).equalsIgnoreCase("Accounting-Off") ||
                    request.getAttributeValue(ACCT_STATUS_TYPE).equalsIgnoreCase("7") || request.getAttributeValue(ACCT_STATUS_TYPE).equalsIgnoreCase("8")) {
                String acctRadiusAtt = cltData.getAcctOnAttribute();
                log.debug(String.format("Accoutning On/Off  %s", request.getAttributeValue(ACCT_STATUS_TYPE)));
                if (acctRadiusAtt != null && acctRadiusAtt.length() > 0 && request.getAttribute(acctRadiusAtt) != null) {
                    log.info(request.getAttributeValue(ACCT_STATUS_TYPE) + " captured");
                    boolean isNasIdentifier = acctRadiusAtt.equalsIgnoreCase("NAS-Identifier");
                    List<Long> liveUserCdrIds = dbAcct.getLiveUserIdsByRadiusAttr(request.getAttributeValue(acctRadiusAtt), isNasIdentifier);
                    RadiusAsyncUtility radiusAsyncUtility = new RadiusAsyncUtility();
                    radiusAsyncUtility.RemoveLiveSessionOnAccountingOn(liveUserCdrIds, cltData.getMvnoId());
                } else {
                    log.error("Accounting On/off Attribute not bind with Client, skip live session purge");
                }
                accoutningResponse.setPacketType(AAAConstant.ACCOUNTING_RESPONSE);
                return accoutningResponse;
            }

            //Processing Accounting Profiles
            List<RadiusProfile> profileList = cacheRetrival.getAcctProfileData();
            log.debug(String.format("Radius Profiles size getAcctProfileData:", profileList.size()));
            for (RadiusProfile radiusProfile : profileList) {
                ValidateExpression validate = new ValidateExpression();
                log.debug(String.format("Radius profile data Expression Check For %s:", radiusProfile.getCheckItem()));
                boolean blnResponse = validate.checkExpression(radiusProfile.getCheckItem(), request, null);
                log.debug(String.format("Expression Check For %s : %s", radiusProfile.getName(), blnResponse));
                if (blnResponse) {
                    log.info(String.format("Radius Profile found: %s", radiusProfile.getName()));
                    // Packet Type Identification and Processing
                    log.info("Status Attribute:" + cltGroupData.getStartStopAttributeValue());
                    String AcctStatusName = "Acct-Status-Type";
                    if (cltGroupData.getStartStopAttributeValue() != null) {
                        AcctStatusName = cltGroupData.getStartStopAttributeValue();
                    }
                    if (request.getAttributeValue(AcctStatusName) != null) {
                        AcctStatusValue = (request.getAttributeValue(AcctStatusName));
                    }
                    if (AcctStatusValue == null || AcctStatusValue == "") {
                        log.info("AcctStatusValue found null so check value from : " + ACCT_STATUS_TYPE);
                        AcctStatusValue = request.getAttributeValue(ACCT_STATUS_TYPE);
                    }
                    log.warn("AcctStatusValue: " + AcctStatusValue);
                    log.warn("AcctStatusValue: " + AcctStatusValue + " time: " + (LocalDateTime.now()));
                    String acct_session_attr = cltData.getClientGroupData().getDynamicAcctSessionAttribute();
                    if (acct_session_attr == null) {
                        acct_session_attr = "Acct-Session-Id";
                    }
                    String strAcctSessionId = null;
                    if (request.getAttribute(acct_session_attr) != null) {
                        strAcctSessionId = request.getAttribute(acct_session_attr).getAttributeValue();
                        log.info("Acct-Session-Id: " + strAcctSessionId);
                    }
                    String strAcctMultiSessionId = null;
                    if (request.getAttribute(RadiusAttributes.ACCT_MULTI_SESSION_ID.getName()) != null) {
                        strAcctMultiSessionId = request.getAttribute(RadiusAttributes.ACCT_MULTI_SESSION_ID.getName()).getAttributeValue();
                        log.info("Acct-Multi-Session-Id: " + strAcctMultiSessionId);
                    } else {
                        strAcctMultiSessionId = "0";
                    }

                    //trigger COA/DM on itrim update if add live session false and disconnect on session true
                    if (!AcctStatusValue.equalsIgnoreCase("Start") && !AcctStatusValue.equalsIgnoreCase("Stop") &&
                            !radiusProfile.isAddLiveSessionOnInterim() && radiusProfile.isDisconnectSessionOnInterim()) {
                        try {
                            int currentSession = dbAcct.isLiveSessionExists("", "", strAcctSessionId, strAcctMultiSessionId);
                            if (currentSession <= 0) {
                                //trigger COA
                                if (request.getAttribute(6527, 13) != null && request.getAttribute(6527, 13).getAttributeValue().equalsIgnoreCase("REAUTHSLA")) {
                                    log.warn("Skip RE_AUTH Event as found 6527:13 start CoA value:" + request.getAttribute(6527, 13).getAttributeValue());
                                } else {
                                    log.warn("Non 6527:13 skipping start CoA and trigger RE Auth COA");
                                    log.warn("Session not available trigger COA/DM Acct-Session-Id: " + strAcctSessionId + " Acct-Multi-Session-Id: " + strAcctMultiSessionId + " Event Name: " + CommonConstants.EVENTCONSTANTS.RE_AUTH);
                                    triggerCOADMAfterAcct(null, cltGroupData, client, request, CommonConstants.EVENTCONSTANTS.RE_AUTH, false, cltData);
                                    return accoutningResponse;
                                }
                            } else {
                                log.info("Session available skip session disconnect Acct-Session-Id: " + strAcctSessionId + " Acct-Multi-Session-Id: " + strAcctMultiSessionId);
                            }
                        } catch (Exception ex) {
                            log.error("CoA on Update Failed: " + ex.getMessage());
                            return accoutningResponse;
                        }
                    }

                    //Fetch Username , Coa/DM and Class Attribute
                    String strUsername = null;
                    String strUsernameAttr = null;
                    boolean isClassAsUserName = false;
                    String user_name_attr = radiusProfile.getCustomerUserNameAttribute();//cltData.getClientGroupData().getCustomerUserNameAttribute();
                    log.debug("User-Name attribute: " + user_name_attr);
                    if (user_name_attr == null || user_name_attr.trim().isEmpty() || request.getAttribute(user_name_attr.trim()) == null) {
                        user_name_attr = "User-Name";
                        log.debug("User-Name attribute: " + user_name_attr);
                    }
                    strUsername = request.getAttribute(user_name_attr).getAttributeValue();

                    if (request.getAttribute(user_name_attr) != null) {
                        strUsername = request.getAttribute(user_name_attr).getAttributeValue();
                        log.warn("Username for Accounting: " + strUsername);
                        String userNameRegex = radiusProfile.getUsernameIdentityRegex();//cltData.getClientGroupData().getUsernameIdentityRegex();
                        if (userNameRegex != null && !userNameRegex.isEmpty()) {
                            String regExUsername = null;
                            regExUsername = radiusUtility.extractValueFromRegex(strUsername, userNameRegex);
                            if (regExUsername != null) {
                                strUsername = regExUsername;
                            }
                            log.info("After apply regex: " + userNameRegex + " strUsername: " + strUsername);
                        }
                    }
                    strUsernameAttr = strUsername;
                    if (request.getAttribute(25) != null) {
                        attribClass = request.getAttribute(25).getAttributeValue();
                        log.info("Class: " + attribClass);

                    }
                    String strCalled = null;
                    if (request.getAttribute("Called-Station-Id") != null) {
                        strCalled = request.getAttribute("Called-Station-Id").getAttributeValue();
                        log.info("Called-Station-Id: " + strCalled);
                    }

                    String mac_attr = cltData.getClientGroupData().getCustomerMacAttribute();
                    if (mac_attr == null) {
                        mac_attr = "Calling-Station-Id";
                    }
                    String strCalling = null;
                    if (request.getAttribute(mac_attr) != null) {
                        strCalling = request.getAttribute(mac_attr).getAttributeValue();
                        log.debug("Calling-Station-Id: " + strCalling);
                    }//NAS-IP-ADDRESS


                    if (cltGroupData != null) {
                        if (cltGroupData.getCoaDMProfile() != null)
                            CoAProfileId = cltGroupData.getCoaDMProfile();
                        if (cltGroupData.getDMProfile() != null)
                            DMProfileId = cltGroupData.getDMProfile();
                        log.debug("For Client Group COA:" + CoAProfileId + ":CoA/DM:" + DMProfileId);
                    }

                    String strIPAddress = null;
                    if (request.getAttribute("Framed-IP-Address") != null) {
                        strIPAddress = request.getAttribute("Framed-IP-Address").getAttributeValue();
                        log.info("Framed-IP-Address:" + strIPAddress);
                    }


                    try {
                        log.debug("TypeAttribute:" + AcctStatusName + ":TypeofPacket:" + request.getAttributeValue(ACCT_STATUS_TYPE) + ":strAcctSessionId:" + strAcctSessionId + ":strUsername:" + strUsername + ":macAttribute:" + mac_attr + "MacValue" + strCalling + ":cltGroup:" + cltData.getClientGroupData().getName());
                    } catch (Exception e) {
                        log.error("Error to to get data from request: " + e.getMessage());
                    }


                    // Upload , Download Attribute Idenftification
                    log.info("Input Attribute:" + cltGroupData.getInputPacketAttributeValue() + ":Output Attribute:" + cltGroupData.getOutputPacketAttributeValue() + ":Unit:" + cltGroupData.getPacketType());
                    String AcctInputName = "Acct-Input-Octets";
                    String AcctOutputName = "Acct-Output-Octets";
                    String AcctUsageType = "Byte";
                    if (cltGroupData.getInputPacketAttributeValue() != null) {
                        AcctInputName = cltGroupData.getInputPacketAttributeValue();
                    }
                    if (cltGroupData.getOutputPacketAttributeValue() != null) {
                        AcctOutputName = cltGroupData.getOutputPacketAttributeValue();
                    }
                    if (cltGroupData.getPacketType() != null) {
                        AcctUsageType = cltGroupData.getPacketType();
                    }

                    boolean isStandardCheck = cltGroupData.isStandardAttributeChecked();
                    //Download and Upload Value Derivation
                    //upload = input_octet
                    //download = output_octet
                    double upload = radiusUtility.getUploadDataFromAccountingRequest(request, cltGroupData); // Acct-Input-Octets
                    double download = radiusUtility.getDownLoadDataFromAccountingRequest(request, cltGroupData); // Acct-Output-Octets


                    log.debug("Total Input Value:" + download + ":Total Output Value:" + upload + ":Unit:" + AcctUsageType + ":Status:" + AcctStatusValue + ":SessionId:" + strAcctSessionId + ":Client:" + cltData.getClientIpAddress());
                    log.debug("Total Input Value:" + download + ":Total Output Value:" + upload + ":Unit:" + AcctUsageType + ":Status:" + AcctStatusValue + ":SessionId:" + strAcctSessionId);

                    double totalUsage = download + upload;
                    DecimalFormat df = new DecimalFormat("#");
                    df.setMaximumFractionDigits(10);
                    log.debug("UPLOAD:" + df.format(upload) + ":DOWNLOAD:" + df.format(download) + ":TOTAL:" + df.format(totalUsage) + ":");
                    double usedQuota = 0d;


                    //Time Quota
                    long totaltime = 0L;
                    double totalTimeMin = 0d;
                    if (request.getAttribute(46) != null) {
                        totaltime = Long.parseLong(request.getAttribute(46).getAttributeValue());
                        totalTimeMin = (double) totaltime / 60;
                    }


                    log.debug(":TOTALTIME:" + totaltime);
                    Double usedTime = (double) 0;
                    LiveUser currentliveUser = null;
                    if (request.getAttribute(RadiusAttributes.ACCT_MULTI_SESSION_ID.getName()) != null) {
                        currentliveUser = dbAuth.getLiveUserFromSessionId(request.getAttribute(RadiusAttributes.ACCT_SESSION_ID.getName()).getAttributeValue(), request.getAttribute(RadiusAttributes.ACCT_MULTI_SESSION_ID.getName()).getAttributeValue(), request.getAttribute(RadiusAttributes.NAS_IP_ADDRESS.getName()).getAttributeValue());
                    } else {
                        currentliveUser = dbAuth.getLiveUserFromSessionId(request.getAttribute(RadiusAttributes.ACCT_SESSION_ID.getName()).getAttributeValue(),
                                "0", request.getAttribute(RadiusAttributes.NAS_IP_ADDRESS.getName()).getAttributeValue());
                    }

                    //Get Customer
                    String authenticationMode = radiusProfile.getAuthenticationMode();
                    CustomerData custRetrunData = null;
                    boolean isFaultyMac = false;
                    boolean isFirstSession = false;
                    if (currentliveUser != null && currentliveUser.getCprId() != null && currentliveUser.getCprId() != 0 && currentliveUser.getUserName() != null) {
                        log.warn("Live User Found: " + currentliveUser.getAcctSessionId() + ", username: " + currentliveUser.getUserName() + ", cprId: " + currentliveUser.getCprId());
                        custRetrunData = dbAuth.getCustomerDetailsByCprId(currentliveUser.getCprId(), radiusUtility);
                    } else {
                        isFirstSession = true;
                        log.warn("Live User Not Found, looking for customer from authenticationMode: " + authenticationMode);
                        custRetrunData = radiusUtility.getCustomerDetailsForAcctRequest(dbAuth, custRetrunData, authenticationMode, strIPAddress, cltData, strUsername, strCalling, AcctStatusValue);
                    }
                    //Get Customer Completed

                    Double lastSessionTime = 0d;//liveUser.getAcctInputOctets()+liveUser.getAcctOutputOctets();//existingDetailsPerSession.getCurrentSessionUsageTime();
                    Double lastSessionVolume = 0d;//existingDetailsPerSession.getCurrentSessionUsageVolume();
                    double totalQuotaByUser = 0d;
                    double totalTimeByUser = 0d;

                    if (custRetrunData != null && custRetrunData.getCustid() != 0 && currentliveUser.getUserName() == null) {
                        log.info("CurrentLive User Not Found Going for all session for customer:" + custRetrunData.getCustid());
                        //Get Live User here.
                        LiveUser InSessionUsage = new LiveUser();
                        dbAuth.getTotalSessionQuota(Integer.toString(custRetrunData.getCustid()), InSessionUsage);
                        totalQuotaByUser = InSessionUsage.getTotalQuota();
                        totalTimeByUser = InSessionUsage.getTotalTime();
                    } else if (currentliveUser != null && currentliveUser.getCdrID() != null) {
                        log.info("CurrentLive User Found Going for all session for customer:" + currentliveUser.getUserName());
                        if (currentliveUser != null && currentliveUser.getAcctInputOctets() != null && currentliveUser.getAcctOutputOctets() != null) {
                            lastSessionVolume = Double.valueOf(currentliveUser.getAcctInputOctets()) + Double.valueOf(currentliveUser.getAcctOutputOctets());
                        }
                        if (currentliveUser != null && currentliveUser.getAcctSessionTime() != null) {
                            lastSessionTime = Double.valueOf(currentliveUser.getAcctSessionTime());
                        }
                        totalQuotaByUser = currentliveUser.getTotalQuota();
                        totalTimeByUser = currentliveUser.getTotalTime();
                    }
                    //Processing as per Status (Intrim/Stop etc)
                    //Check for usage quota type
                    String usageQuotaType = CommonConstants.TOTAL;
                    if (custRetrunData != null && custRetrunData.getUsageQuotaType() != null) {
                        usageQuotaType = custRetrunData.getUsageQuotaType();
                    }
                    //upload = input_octet->
                    //download = output_octet->
                    log.info("usageQuotaType: " + usageQuotaType);
                    if (usageQuotaType.equalsIgnoreCase(CommonConstants.DOWNLOAD)) {
                        totalUsage = download;
                        upload = 0;
                    }
                    if (usageQuotaType.equalsIgnoreCase(CommonConstants.UPLOAD)) {
                        totalUsage = upload;
                        download = 0;
                    }
                    log.warn("Last Session data volume: " + lastSessionVolume + " time: " + lastSessionTime, " totalQuotaByUser: " + totalQuotaByUser + ",totalTimeByUser: " + totalTimeByUser);
                    double currentUsage = totalUsage - lastSessionVolume;
                    long currentTimeUsage = totaltime - lastSessionTime.longValue();
                    if (!AcctStatusValue.equalsIgnoreCase("Stop"))
                        totalUsage = totalQuotaByUser + currentUsage;
                    double remainingquoata = totalQuotaByUser - lastSessionVolume;
                    double remainingtime = totalTimeByUser - lastSessionTime;
                    log.warn("CurrentUsage: " + currentUsage + " currentTimeUsage: " + currentTimeUsage + ", Customer totalUsage: " + totalUsage);
                    //Processing Quota As per Unit
                    if (AcctUsageType.equalsIgnoreCase("Byte")) {
                        //Skip as default is Byte
                    } else if (AcctUsageType.equalsIgnoreCase("KB")) {
                        totalUsage = totalUsage * 1024;
                        download = download * 1024;
                        upload = upload * 1024;
                    } else if (AcctUsageType.equalsIgnoreCase("MB")) {
                        totalUsage = totalUsage * 1024 * 1024;
                        download = download * 1024 * 1024;
                        upload = upload * 1024 * 1024;

                    } else if (AcctUsageType.equalsIgnoreCase("GB")) {
                        totalUsage = totalUsage * 1024 * 1024 * 1024;
                        download = download * 1024 * 1024 * 1024;
                        upload = upload * 1024 * 1024 * 1024;
                    }
                    df.setMaximumFractionDigits(10);
                    String COATriggerReson = "";
                    Integer custPackgeRelId = 0;
                    String timeBasePolicyId = "";

                    log.debug("Final UPLOAD:" + df.format(upload) + ":Final DOWNLOAD:" + df.format(download) + ":TOTAL:" + df.format(totalUsage) + ":Unit:" + AcctUsageType);

                    log.info(String.format("Processing Data with Profile: %s", radiusProfile.getName()));
                    log.info(String.format("Processing Data with Profile: %s", radiusProfile.getName()));


                    //If there is proxy server
                    if (radiusProfile.getProxyServer() != null) {
                        log.debug(String.format("Proxy Configured Local New: %s", radiusProfile.getProxyServer().getId()));
                        try {
                            accoutningResponse = radiusUtility.proxyPacket(request, radiusProfile.getProxyServer(), client);
                            custRetrunData.setSavbillBSSDb(true);
                            log.debug("Customer Data in IMPL:" + custRetrunData);

                            authAcctUtilityImpl.insertOrUpdateAuthAndCDRdataAsync(radiusProfile.getSessionStatus().equalsIgnoreCase("Enable"),
                                    radiusUtility, request, accoutningResponse, radiusProfile.getDbFieldMapping()
                                    , cltData.getMvnoId(), strDMCoAIP, totalTimeMin, custRetrunData, AcctStatusValue, cltData, totalUsage, upload, download);

                            authAcctUtilityImpl.insertOrUpdateAcctSessionAsync(radiusProfile.getAccountCdrStatus().equalsIgnoreCase("Enable"),
                                    radiusUtility, request, accoutningResponse, radiusProfile.getDbFieldMapping()
                                    , cltData.getMvnoId(), strDMCoAIP, custRetrunData, AcctStatusValue, cltData, radiusProfile.isAddLiveSessionOnInterim(), totalUsage, 0, upload, download, isFaultyMac,isFirstSession, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.info(String.format("Proxy Server Not Responding"));
                        } finally {
                            strSNMP = AcctStatusValue.toString();
                            if (strSNMP.equalsIgnoreCase("Start")) {
                                strSNMP = "Proxy-Start";
                            } else if (strSNMP.equalsIgnoreCase("Interim-Update")) {
                                strSNMP = "Proxy-Interim-Update";
                            } else if (strSNMP.equalsIgnoreCase("Stop")) {
                                strSNMP = "Proxy-Stop";
                            }
                        }
                    } else if (radiusProfile.getProxyServer() == null) {
                        if ((custRetrunData == null || custRetrunData.getUsername() == null) && isClassAsUserName) {
                            log.info("Customer data Not Found checking using Class Value: " + strUsernameAttr);
                            custRetrunData = radiusUtility.getCustomerDetailsForAcctRequest(dbAuth, custRetrunData, authenticationMode, strIPAddress, cltData, strUsernameAttr, strCalling, AcctStatusValue);
                        }
                        custRetrunData.setSavbillBSSDb(true);
                        log.debug("Customer Data in IMPL:" + custRetrunData);
                        log.debug("Customer Data in Fetched:" + custRetrunData);
                        CustomerData parentCust = null;
                        List<CoaDMProfile> coaDMProfileDataList = cacheRetrival.getCoADMProfileData();
                        if (custRetrunData != null) {
                            //check faulty mac
                            String finalMac = radiusUtility.normalizeMacAddress(strCalling);
                            String formattedUsername = radiusUtility.normalizeMacAddress(strUsername);
                            if (formattedUsername.equalsIgnoreCase(finalMac)) {
                                log.info("Username and Mac Same so checking Faulty MAC:" + strCalling + ":strUsername:" + strUsername);
                                log.info("Request Username:" + strUsername + ":customer username:" + custRetrunData.getUsername());
//                                List<String> faultyMACS = cacheRetrival.getFaultyMacData();
                                Map<String, FaultyMAC> faultyMacList = cacheRetrival.getFaultyMacList();
                                if (faultyMacList != null && !CollectionUtils.isEmpty(faultyMacList)) {
                                    FaultyMAC faultyMAC = faultyMacList.get(finalMac);
                                    if (faultyMAC != null) {
                                        log.error("Fault MAC found So updating Username in customer strCalling:" + strCalling + ":username:" + strUsername + " ");
                                        custRetrunData.setUsername(strUsername);
                                        isFaultyMac = true;
                                    } else {
                                        isFaultyMac = false;
                                        log.info("Fault MAC Not found continue request with mac: " + strCalling);
                                    }
                                }
                            } else {
                                log.info("Request Username: " + strUsername + " customer username: " + custRetrunData.getUsername() + " MAC: " + strCalling);
                            }

                            //If independent customer access using mac then check for parent customer available on same location with shareble quota
                            if (custRetrunData.isMacflow() && custRetrunData.isAuthStatus() && strCalled != null && custRetrunData.getParentCustId() == 0) {
                                CustomerData childdata = dbAuth.getParentCustByMac(strCalled, strCalling, cltData.getMvnoId());
                                if (childdata != null) {
                                    custRetrunData = childdata;
                                    log.debug(String.format("Mac accounting success for independent customer and Free quota available on location: " + strCalled));
                                    log.debug(String.format("child customer Found for Location " + strCalled));
                                } else {
                                    log.debug(String.format("Parent quota available for location " + strCalled + " but child not available for independent customer: " + custRetrunData.getUsername()));
                                }
                            }
                            if (custRetrunData.getParentCustId() != 0) {
                                parentCust = dbAuth.getDBCustomer(null, cltData.getMvnoId(), String.valueOf(custRetrunData.getParentCustId()), "ACCT", true);
                            }
                        }


                        //Update customer mac Retention date and Time
                        if (strCalling != null && custRetrunData.getMacRetentionPeriod() != null) {
                            updateCustomerMacRetentionDate(strCalling, custRetrunData, dbAuth);
                        }

                        double reservedQuotaOnStop = 0.0;
                        String strUsedQuotaOnStop = "0";
                        CustomerPlanData customerPlanOnStop = null;
                        boolean interimSkipOnCoA = false;

                        //start actual functionality
                        if (AcctStatusValue != null) {
                            log.warn(String.format("Accounting Type :" + AcctStatusValue));
                            //TODO: In case on nokia SLA Stop, AcctStatusValue comes as sla-stop, so in radius is ignore and consider as update.
                            // add check for AcctStatusValue if contains stop then set value as stop
                            if(AcctStatusValue.toLowerCase().contains("stop"))
                                AcctStatusValue = "Stop"; // from ntraping and bng stop value is like camel case Stop

                            try {
                                snmpCounterForAcctRequest(AcctStatusValue.toString());
                            } catch (Exception e) {
                                log.info("SNMP Error: " + e.getMessage());
                            }

                            String acctStatusKey = AcctStatusValue.toLowerCase();
                            switch (acctStatusKey) {
                                case "stop":
                                    long stopStartTime = System.currentTimeMillis();
                                    //SUP-1845 and SUP-1780: If there is no live user based on access request then don't calculate quota
                                    if (currentliveUser != null && currentliveUser.getCdrID() != null) {
                                        if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                                            double unusedQuota = 0.0;
                                            double unusedTime = 0.0;
                                            double totalTimeQuota = 0.0;
                                            double reservedQuota = 0.0;

                                            boolean noBooster = true;
                                            String strUsedQuota = "0";
                                            if (parentCust != null) {
                                                custQuotaInfo.setMvnoId((long) cltData.getMvnoId());
                                                custQuotaInfo.setUserName(strUsername);

                                                if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && parentCust != null && parentCust.getUsername() != null) {
                                                    if (parentCust.getCustomerVolueBooster() != null && !parentCust.getCustomerVolueBooster().isEmpty()) {
                                                        CustomerPlanData parentBooster = parentCust.getCustomerVolueBooster().get(0);
                                                        custQuotaInfo.setTimeBasedTotalQuota(parentBooster.getTimebasedtotalquota());
                                                        custQuotaInfo.setVolumeBasedTotalQuota(parentBooster.getVolumebasedtotalquota());
                                                        log.info("Customer Type:" + parentBooster.getQuotatype() + ":Quota:" + parentBooster.getQuotaunit());
                                                        remainingquoata = radiusUtility.convertUsageToGivenUnit(remainingquoata, parentBooster.getQuotaunit());
                                                        custQuotaInfo.setVolumeBasedSessionUsedQuota(remainingquoata);
                                                        custQuotaInfo.setTimeBasedSessionUsedQuota(remainingtime);

                                                        if (parentBooster.getQuotatype().equalsIgnoreCase("Data") || parentBooster.getQuotatype().equalsIgnoreCase("Both")) {
//													handleDataQuota(parentBooster, custQuotaInfo, noBooster, usedQuota, totalUsage,unusedQuota, strUsedQuota);
                                                            noBooster = false;
                                                            double dbltTotalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, parentBooster.getQuotaunit());
                                                            usedQuota = calculateUsedQuota(parentBooster.getVolumebasedusedquota(), dbltTotalUsage);
                                                            df.setMaximumFractionDigits(10);
                                                            strUsedQuota = df.format(usedQuota);
                                                            strUsedQuota = new String(df.format(usedQuota));
                                                            custQuotaInfo.setVolumeBasedUnusedQuota(unusedQuota);
                                                            custQuotaInfo.setVolumeBasedUsedQuota(usedQuota);
                                                            percentageUsage = ((dbltTotalUsage + parentBooster.getVolumebasedusedquota()) * 100) / parentBooster.getVolumebasedtotalquota();
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        } else if (parentBooster.getQuotatype().equalsIgnoreCase("Time") || parentBooster.getQuotatype().equalsIgnoreCase("Both")) {
//													handleTimeQuota(parentBooster, custQuotaInfo, noBooster, totalTimeMin,totalTimeQuota,usedTime,unusedTime, strUsedQuota);
                                                            noBooster = false;
                                                            totalTimeMin = getTotalTimeMin(parentBooster.getTimequotaunit(), totalTimeMin);
                                                            totalTimeQuota = getTotalTimeQuota(parentBooster.getTimebasedtotalquota(), parentBooster.getTimequotaunit());
                                                            usedTime = calculateUsedTime(parentBooster.getTimebasedusedquota(), totalTimeMin);
                                                            custQuotaInfo.setTimeBasedUsedQuota(usedTime);
                                                            custQuotaInfo.setTimeBasedUnusedQuota(unusedTime);
                                                            percentageUsage = (usedTime * 100) / totalTimeQuota;
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        }

                                                        if (!noBooster) {
//                                                            updateQuotaInfoAndSync(custQuotaInfo, parentCust, reservedQuota, strUsedQuota, usedTime, parentBooster, dbAcct, radiusUtility);
                                                            reservedQuotaOnStop = reservedQuota;
                                                            strUsedQuotaOnStop = strUsedQuota;
                                                            customerPlanOnStop = parentBooster;
                                                        }
                                                    }

                                                    if (noBooster && parentCust != null && parentCust.getUsername() != null && parentCust.getCustomerBasePlan() != null && !parentCust.getCustomerBasePlan().isEmpty()) {
                                                        CustomerPlanData parentBasePlan = parentCust.getCustomerBasePlan().get(0);
                                                        custQuotaInfo.setTimeBasedTotalQuota(parentBasePlan.getTimebasedtotalquota());
                                                        custQuotaInfo.setVolumeBasedTotalQuota(parentBasePlan.getVolumebasedtotalquota());
                                                        remainingquoata = radiusUtility.convertUsageToGivenUnit(remainingquoata, parentBasePlan.getQuotaunit());
                                                        custQuotaInfo.setVolumeBasedSessionUsedQuota(remainingquoata);
                                                        custQuotaInfo.setTimeBasedSessionUsedQuota(remainingtime);

                                                        if (parentBasePlan.getQuotatype().equalsIgnoreCase("Data") || parentBasePlan.getQuotatype().equalsIgnoreCase("Both")) {
//													handleDataQuota(parentBasePlan, custQuotaInfo, noBooster, usedQuota, totalUsage,unusedQuota, strUsedQuota);
                                                            noBooster = true;
                                                            double dbltTotalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, parentBasePlan.getQuotaunit());
                                                            usedQuota = calculateUsedQuota(parentBasePlan.getVolumebasedusedquota(), dbltTotalUsage);
                                                            df.setMaximumFractionDigits(10);
                                                            strUsedQuota = df.format(usedQuota);
                                                            strUsedQuota = new String(df.format(usedQuota));
                                                            if (parentBasePlan.isSkipQuotaUpdate()) {
                                                                custQuotaInfo.setSkipQuotaReset(true);
                                                            } else {
                                                                custQuotaInfo.setSkipQuotaReset(false);
                                                            }
                                                            custQuotaInfo.setVolumeBasedUnusedQuota(unusedQuota);
                                                            custQuotaInfo.setVolumeBasedUsedQuota(usedQuota);
                                                            percentageUsage = ((dbltTotalUsage + parentBasePlan.getVolumebasedusedquota()) * 100) / parentBasePlan.getVolumebasedtotalquota();
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        } else if (parentBasePlan.getQuotatype().equalsIgnoreCase("Time") || parentBasePlan.getQuotatype().equalsIgnoreCase("Both")) {
//													handleTimeQuota(parentBasePlan, custQuotaInfo, noBooster, totalTimeMin,totalTimeQuota,usedTime,unusedTime, strUsedQuota);
                                                            noBooster = true;
                                                            totalTimeMin = getTotalTimeMin(parentBasePlan.getTimequotaunit(), totalTimeMin);
                                                            totalTimeQuota = getTotalTimeQuota(parentBasePlan.getTimebasedtotalquota(), parentBasePlan.getTimequotaunit());
                                                            usedTime = calculateUsedTime(parentBasePlan.getTimebasedusedquota(), totalTimeMin);
                                                            custQuotaInfo.setTimeBasedUsedQuota(usedTime);
                                                            custQuotaInfo.setTimeBasedUnusedQuota(unusedTime);
                                                            percentageUsage = (usedTime * 100) / totalTimeQuota;
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        }
                                                        if(custQuotaInfo.getSkipQuotaReset() != null && custQuotaInfo.getSkipQuotaReset()) {
                                                            log.debug("Skip quota flag in plan is true so skip quota update");
                                                        } else {
//                                                            updateQuotaInfoAndSync(custQuotaInfo, parentCust, reservedQuota, strUsedQuota, usedTime, parentBasePlan, dbAcct, radiusUtility);
                                                            reservedQuotaOnStop = reservedQuota;
                                                            strUsedQuotaOnStop = strUsedQuota;
                                                            customerPlanOnStop = parentBasePlan;
                                                        }
                                                    }
                                                }
                                            }
                                            //original customer
                                            else {
                                                custQuotaInfo.setMvnoId((long) cltData.getMvnoId());
                                                custQuotaInfo.setUserName(strUsername);
                                                boolean isVolumePlan = false;
                                                boolean isQuotaPlan = false;
                                                if (custRetrunData != null && custRetrunData.getUsername() != null && custRetrunData.getCustomerBasePlan() != null) {
                                                    //customer volume booster plan
                                                    if (custRetrunData.getCustomerQuotaBooster() != null && !custRetrunData.getCustomerQuotaBooster().isEmpty() && custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota() > 0) {
                                                        isQuotaPlan = true;
                                                        CustomerPlanData customerQuotaBooster = custRetrunData.getCustomerQuotaBooster().get(0);
                                                        custQuotaInfo.setTimeBasedTotalQuota(customerQuotaBooster.getTimebasedtotalquota());
                                                        custQuotaInfo.setVolumeBasedTotalQuota(customerQuotaBooster.getVolumebasedtotalquota());
                                                        remainingquoata = radiusUtility.convertUsageToGivenUnit(remainingquoata, customerQuotaBooster.getQuotaunit());
                                                        custQuotaInfo.setVolumeBasedSessionUsedQuota(remainingquoata);
                                                        custQuotaInfo.setTimeBasedSessionUsedQuota(remainingtime);

                                                        log.info("Customer Type:" + customerQuotaBooster.getQuotatype() + ":Quota:" + customerQuotaBooster.getQuotaunit());
                                                        if (customerQuotaBooster.getQuotatype().equalsIgnoreCase("Data") || customerQuotaBooster.getQuotatype().equalsIgnoreCase("Both")) {
//													handleDataQuota(customerQuotaBooster, custQuotaInfo, noBooster, usedQuota, totalUsage,unusedQuota, strUsedQuota);
                                                            noBooster = false;
                                                            double dbltTotalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, customerQuotaBooster.getQuotaunit());
                                                            usedQuota = calculateUsedQuota(customerQuotaBooster.getVolumebasedusedquota(), dbltTotalUsage);
                                                            df.setMaximumFractionDigits(10);
                                                            strUsedQuota = df.format(usedQuota);
                                                            strUsedQuota = new String(df.format(usedQuota));
                                                            custQuotaInfo.setVolumeBasedUnusedQuota(unusedQuota);
                                                            custQuotaInfo.setVolumeBasedUsedQuota(usedQuota);
                                                            percentageUsage = ((dbltTotalUsage + customerQuotaBooster.getVolumebasedusedquota()) * 100) / customerQuotaBooster.getVolumebasedtotalquota();
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        }
                                                        if (customerQuotaBooster.getQuotatype().equalsIgnoreCase("Time") || customerQuotaBooster.getQuotatype().equalsIgnoreCase("Both")) {
//													handleTimeQuota(customerQuotaBooster, custQuotaInfo, noBooster, totalTimeMin,totalTimeQuota,usedTime,unusedTime, strUsedQuota);
                                                            noBooster = false;
                                                            totalTimeMin = getTotalTimeMin(customerQuotaBooster.getTimequotaunit(), totalTimeMin);
                                                            totalTimeQuota = getTotalTimeQuota(customerQuotaBooster.getTimebasedtotalquota(), customerQuotaBooster.getTimequotaunit());
                                                            usedTime = calculateUsedTime(customerQuotaBooster.getTimebasedusedquota(), totalTimeMin);
                                                            custQuotaInfo.setTimeBasedUsedQuota(usedTime);
                                                            custQuotaInfo.setTimeBasedUnusedQuota(unusedTime);
                                                            percentageUsage = (usedTime * 100) / totalTimeQuota;
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        }
                                                        if (!noBooster) {
//                                                            updateQuotaInfoAndSync(custQuotaInfo, custRetrunData, reservedQuota, strUsedQuota, usedTime, customerQuotaBooster, dbAcct, radiusUtility);
                                                            reservedQuotaOnStop = reservedQuota;
                                                            strUsedQuotaOnStop = strUsedQuota;
                                                            customerPlanOnStop = customerQuotaBooster;
                                                        }
                                                    }
                                                    //customer volume booster plan
                                                    else if (custRetrunData.getCustomerVolueBooster() != null && !custRetrunData.getCustomerVolueBooster().isEmpty()) {
                                                        isVolumePlan = true;
                                                        CustomerPlanData customerVolumeBooster = custRetrunData.getCustomerVolueBooster().get(0);
                                                        custQuotaInfo.setTimeBasedTotalQuota(customerVolumeBooster.getTimebasedtotalquota());
                                                        custQuotaInfo.setVolumeBasedTotalQuota(customerVolumeBooster.getVolumebasedtotalquota());

                                                        if(customerVolumeBooster.getQuotaunit() != null)
                                                            remainingquoata = radiusUtility.convertUsageToGivenUnit(remainingquoata, customerVolumeBooster.getQuotaunit());

                                                        custQuotaInfo.setVolumeBasedSessionUsedQuota(remainingquoata);
                                                        custQuotaInfo.setTimeBasedSessionUsedQuota(remainingtime);

                                                        log.info("Customer Type:" + customerVolumeBooster.getQuotatype() + ":Quota:" + customerVolumeBooster.getQuotaunit());
                                                        if (customerVolumeBooster.getQuotatype().equalsIgnoreCase("Data") || customerVolumeBooster.getQuotatype().equalsIgnoreCase("Both")) {
//													handleDataQuota(customerVolumeBooster, custQuotaInfo, noBooster, usedQuota, totalUsage,unusedQuota, strUsedQuota);
                                                            noBooster = false;
                                                            double dbltTotalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, customerVolumeBooster.getQuotaunit());
                                                            usedQuota = calculateUsedQuota(customerVolumeBooster.getVolumebasedusedquota(), dbltTotalUsage);
                                                            df.setMaximumFractionDigits(10);
                                                            strUsedQuota = df.format(usedQuota);
                                                            strUsedQuota = new String(df.format(usedQuota));
                                                            custQuotaInfo.setVolumeBasedUnusedQuota(unusedQuota);
                                                            custQuotaInfo.setVolumeBasedUsedQuota(usedQuota);
                                                            percentageUsage = ((dbltTotalUsage + customerVolumeBooster.getVolumebasedusedquota()) * 100) / customerVolumeBooster.getVolumebasedtotalquota();
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        }
                                                        if (customerVolumeBooster.getQuotatype().equalsIgnoreCase("Time") || customerVolumeBooster.getQuotatype().equalsIgnoreCase("Both")) {
//													handleTimeQuota(customerVolumeBooster, custQuotaInfo, noBooster, totalTimeMin,totalTimeQuota,usedTime,unusedTime, strUsedQuota);
                                                            noBooster = false;
                                                            totalTimeMin = getTotalTimeMin(customerVolumeBooster.getTimequotaunit(), totalTimeMin);
                                                            totalTimeQuota = getTotalTimeQuota(customerVolumeBooster.getTimebasedtotalquota(), customerVolumeBooster.getTimequotaunit());
                                                            usedTime = calculateUsedTime(customerVolumeBooster.getTimebasedusedquota(), totalTimeMin);
                                                            custQuotaInfo.setTimeBasedUsedQuota(usedTime);
                                                            custQuotaInfo.setTimeBasedUnusedQuota(unusedTime);
                                                            percentageUsage = (usedTime * 100) / totalTimeQuota;
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        }
                                                        if (!noBooster) {
//                                                            updateQuotaInfoAndSync(custQuotaInfo, custRetrunData, reservedQuota, strUsedQuota, usedTime, customerVolumeBooster, dbAcct, radiusUtility);
                                                            reservedQuotaOnStop = reservedQuota;
                                                            strUsedQuotaOnStop = strUsedQuota;
                                                            customerPlanOnStop = customerVolumeBooster;
                                                        }
                                                    }
                                                    //customer volume base plan
                                                    else if (noBooster && custRetrunData.getCustomerBasePlan() != null && !custRetrunData.getCustomerBasePlan().isEmpty()) {
                                                        CustomerPlanData customerBasePlan = custRetrunData.getCustomerBasePlan().get(0);
                                                        log.info("Customer Plan Type: " + customerBasePlan.getPurchaseType());
                                                        custQuotaInfo.setTimeBasedTotalQuota(customerBasePlan.getTimebasedtotalquota());
                                                        custQuotaInfo.setVolumeBasedTotalQuota(customerBasePlan.getVolumebasedtotalquota());
                                                        if(customerBasePlan.getQuotaunit() != null)
                                                            remainingquoata = radiusUtility.convertUsageToGivenUnit(remainingquoata, customerBasePlan.getQuotaunit());
                                                        custQuotaInfo.setVolumeBasedSessionUsedQuota(remainingquoata);
                                                        custQuotaInfo.setTimeBasedSessionUsedQuota(remainingtime);
                                                        log.debug("Customer quota Skip Quota Update Plan: " + customerBasePlan.getPlanName() + " Flag:" + customerBasePlan.isSkipQuotaUpdate());
                                                        if (customerBasePlan.isSkipQuotaUpdate()) {
                                                            custQuotaInfo.setSkipQuotaReset(true);
                                                            customerPlanOnStop = customerBasePlan;
                                                        } else {
                                                            custQuotaInfo.setSkipQuotaReset(false);
                                                        }

                                                        if (customerBasePlan.getQuotatype().equalsIgnoreCase("Data") || customerBasePlan.getQuotatype().equalsIgnoreCase("Both")) {
//													handleDataQuota(customerBasePlan, custQuotaInfo, noBooster, usedQuota, totalUsage,unusedQuota, strUsedQuota);
                                                            noBooster = true;
                                                            double dbltTotalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, customerBasePlan.getQuotaunit());
                                                            usedQuota = calculateUsedQuota(customerBasePlan.getVolumebasedusedquota(), dbltTotalUsage);
                                                            df.setMaximumFractionDigits(10);
                                                            log.info("Customer Quota Skip, Quota Update Plan: " + customerBasePlan.getPlanName() + " Flag:" + custQuotaInfo.getSkipQuotaReset());
                                                            if (custQuotaInfo.getSkipQuotaReset() != null && custQuotaInfo.getSkipQuotaReset()) {
                                                                strUsedQuota = "0";
                                                                custQuotaInfo.setVolumeBasedUnusedQuota(0d);
                                                                custQuotaInfo.setVolumeBasedUsedQuota(0d);
                                                            } else {
                                                                strUsedQuota = df.format(usedQuota);
                                                                strUsedQuota = new String(df.format(usedQuota));
                                                                custQuotaInfo.setVolumeBasedUnusedQuota(unusedQuota);
                                                                custQuotaInfo.setVolumeBasedUsedQuota(usedQuota);
                                                            }
                                                            percentageUsage = ((dbltTotalUsage + customerBasePlan.getVolumebasedusedquota()) * 100) / customerBasePlan.getVolumebasedtotalquota();
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        }
                                                        if (customerBasePlan.getQuotatype().equalsIgnoreCase("Time") || customerBasePlan.getQuotatype().equalsIgnoreCase("Both")) {
//													handleTimeQuota(customerBasePlan, custQuotaInfo, noBooster, totalTimeMin,totalTimeQuota,usedTime,unusedTime, strUsedQuota);
                                                            noBooster = true;
                                                            totalTimeMin = getTotalTimeMin(customerBasePlan.getTimequotaunit(), totalTimeMin);
                                                            totalTimeQuota = getTotalTimeQuota(customerBasePlan.getTimebasedtotalquota(), customerBasePlan.getTimequotaunit());
                                                            log.info("Customer Quota Skip, Quota Update Plan: " + customerBasePlan.getPlanName() + " Flag:" + custQuotaInfo.getSkipQuotaReset());
                                                            if (custQuotaInfo.getSkipQuotaReset() != null && custQuotaInfo.getSkipQuotaReset()) {
                                                                usedTime = 0d;
                                                                custQuotaInfo.setTimeBasedUsedQuota(0d);
                                                                custQuotaInfo.setTimeBasedUnusedQuota(0d);
                                                            } else {
                                                                usedTime = calculateUsedTime(customerBasePlan.getTimebasedusedquota(), totalTimeMin);
                                                                custQuotaInfo.setTimeBasedUsedQuota(usedTime);
                                                                custQuotaInfo.setTimeBasedUnusedQuota(unusedTime);
                                                            }
                                                            percentageUsage = (usedTime * 100) / totalTimeQuota;
                                                            log.info("Quota Used in Percentage:" + percentageUsage);
                                                        }
                                                        if(custQuotaInfo.getSkipQuotaReset() != null && custQuotaInfo.getSkipQuotaReset()) {
                                                            log.debug("Skip quota flag in plan is true so skip quota update");
                                                        } else {
//                                                            updateQuotaInfoAndSync(custQuotaInfo, custRetrunData, reservedQuota, strUsedQuota, usedTime, customerBasePlan, dbAcct, radiusUtility);
                                                            reservedQuotaOnStop = reservedQuota;
                                                            strUsedQuotaOnStop = strUsedQuota;
                                                            customerPlanOnStop = customerBasePlan;
                                                        }
                                                    }
                                                }

                                                //CoA DM Processing END
                                                // If Add On Plans quota is used or over then update status of Cust Plan status
                                                if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().get(0).isNotBasePlan() && percentageUsage >= 100) {
                                                    updateCustPlanStatus((long) custRetrunData.getCustomerBasePlan().get(0).getCustpackageid(), CommonConstants.PLAN_STAGE_EXPIRED);
                                                }
                                            }
                                            // Remove Mac From Cache if available
                                            CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                            if (strCalling != null && cacheService.get(radiusUtility.normalizeMacAddress(strCalling)) != null) {
                                                log.debug("Cache Available on STOP for mac: " + strCalling);
                                                // Add check of Clear Cache Mapping
                                                List<ClearCacheMapping> cacheMappings = cltGroupData.getClearCacheMappings();
                                                if (!CollectionUtils.isEmpty(cacheMappings)) {
                                                    boolean allValid = true;
                                                    for (ClearCacheMapping clearCacheMapping : cacheMappings) {
                                                        String checkItem = clearCacheMapping.getCheckitem();
                                                        if (checkItem != null) {
                                                            if (!validate.checkExpression(checkItem, request, null)) {
                                                                allValid = false; // Stop processing if any item is invalid
                                                                break;
                                                            }
                                                        }
                                                    }

                                                    if (allValid) {
                                                        cacheService.remove(radiusUtility.normalizeMacAddress(strCalling));
                                                        log.debug("Cache removed on STOP for mac: " + strCalling);
                                                    } else {
                                                        log.info("Cache removal skipped as not all checkItems are valid for Client Group: " + cltGroupData.getName());
                                                    }
                                                } else {
                                                    log.info("Mac Cache remove skipped as ClearCacheMapping is empty for Client Group: " + cltGroupData.getName());
                                                }
                                            }

                                        }
                                    } else {
                                        log.warn("Live user not found so skipp quota calcultion on stop strAcctMultiSessionId: " + strAcctMultiSessionId + ", strAcctSessionId: " + strAcctSessionId);
                                        upload = 0;
                                        download = 0;
                                    }
                                    break;
                                case "start":
                                    try {
                                        boolean authProvisionMac = false;
                                        if (radiusProfile.getAutoProvisionMac() != null && radiusProfile.getAutoProvisionMac().equalsIgnoreCase("Enable")) {
                                            authProvisionMac = true;
                                        }
                                        if (custRetrunData.getMacProvision() != null && custRetrunData.getMacProvision()) {
                                            authProvisionMac = true;
                                        }
                                        RadiusUtility utility = new RadiusUtility();
                                        if (authProvisionMac) {
                                            log.info("Save mac in customer: " + strCalling);
                                            RadiusAsyncUtility radiusAsyncUtility = new RadiusAsyncUtility();
                                            radiusAsyncUtility.UpdateCustomerMac(strCalling, null, custRetrunData, cltData.getMvnoId(), true);
//                                            utility.saveOrUpdateCustomerMac(strCalling, null, custRetrunData, cltData.getMvnoId(), dbAuth, true);
                                        }
                                    } catch (Exception ex) {
                                        log.error("Error for Mac Provision at Accounting start: " + ex.getMessage());
                                    }
                                    break;

                                case FRAMED_IP_ADDRESS_DOWN:
                                    log.warn("Nokia stop recieved : " + FRAMED_IP_ADDRESS_DOWN);
                                    break;
                                case DELEGATED_IPV6_PREFIX_DOWN:
                                    log.warn("Nokia stop recieved : " + DELEGATED_IPV6_PREFIX_DOWN);
                                    break;
                                case ALC_IPV6_ADDRESS_DOWN:
                                    log.warn("Nokia stop recieved : " + ALC_IPV6_ADDRESS_DOWN);
                                    break;

                                default: {
                                    custQuotaInfo.setMvnoId((long) cltData.getMvnoId());
                                    custQuotaInfo.setUserName(strUsername);
                                    String strUsedQuota = "0";
                                    boolean expiry = false, firecoa = false, noBooster = true, skipQuotaReset = false;

                                    if (custRetrunData == null || custRetrunData.getUsername() == null || !custRetrunData.isAuthStatus()) {
                                        log.debug(String.format("Customer Not Found or Auth Status False. Perfomring DM"));
                                        log.debug(String.format("Customer Not Found, updating data in session: " + strAcctSessionId));
                                        authAcctUtilityImpl.insertOrUpdateAcctSessionAsync(radiusProfile.getAccountCdrStatus().equalsIgnoreCase("Enable"),
                                                radiusUtility, request, accoutningResponse, radiusProfile.getDbFieldMapping()
                                                , cltData.getMvnoId(), strDMCoAIP, custRetrunData, AcctStatusValue, cltData, radiusProfile.isAddLiveSessionOnInterim(), upload + download, totaltime, upload, download, isFaultyMac,isFirstSession, false);
                                        CoaDMProfile coaProfileData = null;
                                        RadiusPacket coaDMResponse = null;
                                        try {

                                            // Trigger COA if there is start and STOP for REAUTH Issue
                                            if (!AcctStatusValue.equalsIgnoreCase("STOP")) {
                                                try {
                                                    if (request.getAttribute(6527, 13) != null && request.getAttribute(6527, 13).getAttributeValue().equalsIgnoreCase("REAUTHSLA")) {
                                                        log.warn("Yes 6527:13 start CoA value:" + request.getAttribute(6527, 13).getAttributeValue());
                                                        triggerCOADMAfterAcct(custRetrunData, cltGroupData, client, request, CommonConstants.EVENTCONSTANTS.START_COA, true, cltData);
                                                        log.warn("COA Response Receive For NoKia Moving Ahead for event: "+CommonConstants.EVENTCONSTANTS.START_COA);
                                                    } else {
                                                        log.debug("Non 6527:13 skipping start CoA");
                                                    }
                                                } catch (Exception ex) {
                                                    log.error("CoA in Start Failed");
                                                }
                                            }

                                            if (coaProfileData == null && DMProfileId != 0L) {
                                                if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                                    for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                                        if (DMProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                                            coaProfileData = coaDMProfileDataList.get(i);
                                                            coaProfileData.setGateway(strDMCoAIP);
                                                        }
                                                    }
                                                }
                                            }
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

                                            log.debug("coaProfileData:" + coaProfileData);
                                            if (coaProfileData != null) {
                                                log.debug("DM Profile Found : " + coaProfileData.getName() + ":Type:" + coaProfileData.getType() + ":CoA/DM Firing on:" + coaProfileData.getGateway() + ":Key:" + cltData.getSharedKey() + ":Port:" + coaProfileData.getPort());
                                                log.debug("Customer FOUND Hence CoA/DM Firing on:" + coaProfileData.getGateway() + ":Key:" + cltData.getSharedKey() + ":Port:" + coaProfileData.getPort());
                                                coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, request, strUsername, custRetrunData, strDMCoAIP);
                                                log.warn("COA/DM response:"+coaDMResponse+":For Event: "+":");
                                                if(coaDMResponse!=null){
                                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                    radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),"Customer not found", cltData.getMvnoId() , coaDMResponse);
                                                }
                                                else{
                                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                                    radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs","Customer not found", cltData.getMvnoId(), coaDMResponse);
                                                }
                                            } else {
                                                log.debug("CoA/DM Profile Not Found Skipping CoA/DM");
                                                log.debug("Customer Not FOUND & CoA DM Prifile not found skipping CoA/DM");
                                            }
                                        } catch (Exception e) {
                                            log.error("CoA/DM Failed:" + e.getMessage());
                                            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                            radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout","Customer not found", cltData.getMvnoId(), coaDMResponse);
                                            log.error("CoA/DM Failed:" + e.getMessage());
                                        }

                                        return accoutningResponse;
                                    }
                                    // Add check for customer inactive
                                    //Time Base Policy Change check for CoA
                                    if (CollectionUtils.isEmpty(custRetrunData.getCustomerBasePlan()) && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                                        log.info("No Plan In DB Fire CoA");
                                        log.info("No Plan In DB for Customer hence Firing CoA");
                                        custRetrunData.setStrReplyMessage(AAAConstant.REPLYMSG_PLANEXPIRED);
                                        custRetrunData.setAuthStatus(false);
                                        custRetrunData.setStatus(CommonConstants.PLAN_INACTIVE);
                                        firecoa = true;
                                        custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.PLAN_EXPIRE);
                                    }
                                    log.info("Class From DB:" + custRetrunData.getStrClass() + ":Packet Class:" + attribClass);
                                    if (custRetrunData.isAuthStatus() && custRetrunData.getStrClass() != null && attribClass != null) {
                                        if (!attribClass.contains(custRetrunData.getStrClass())) {
                                            log.info("Time Policy Change Fire CoA:" + custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(0).getThrottleDownloadSpeed());
                                            log.info("Time Policy Change Fire CoA:" + custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(0).getThrottleDownloadSpeed());
                                            firecoa = true;
                                            COATriggerReson = CommonConstants.CoaDmResonContant.TIME_BASE_POLICY_CHANGE;
                                            timeBasePolicyId = custRetrunData.getStrClass().substring(custRetrunData.getStrClass().indexOf("="), custRetrunData.getStrClass().length() - 1);
                                            if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                custPackgeRelId = custRetrunData.getCustomerBasePlan().get(0).getCustpackageid();
                                            }
                                        }
                                    }
                                    boolean updateCPRCOADMFlag = false;
                                    boolean isTriggerCOADM = true;
                                    //Check first parent available
                                    if (parentCust != null) {
                                        log.debug("Parent User available: " + parentCust.getUsername());
                                        if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && parentCust.isAuthStatus() && parentCust.getCustomerQuotaBooster() != null && parentCust.getCustomerQuotaBooster().get(0) != null) {
                                            log.info("Bandwidth Customer Type:" + parentCust.getCustomerQuotaBooster().get(0).getQuotatype() + ":Quota:" + parentCust.getCustomerQuotaBooster().get(0).getQuotaunit());
                                            if (parentCust.getCustomerQuotaBooster().get(0).getVolumebasedunusedquota() > 0 && parentCust.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota() > 0 && parentCust.getCustomerQuotaBooster().get(0).getQuotatype().equalsIgnoreCase("Data") || parentCust.getCustomerQuotaBooster().get(0).getQuotatype().equalsIgnoreCase("Both")) {
                                                totalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, parentCust.getCustomerQuotaBooster().get(0).getQuotaunit());
                                                percentageUsage = ((totalUsage + parentCust.getCustomerQuotaBooster().get(0).getVolumebasedusedquota()) * 100) / parentCust.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota();
                                                log.info("Total Used in Session:" + totalUsage + ":Current Unused:" + parentCust.getCustomerQuotaBooster().get(0).getVolumebasedunusedquota() + ":Total Allowed:" + parentCust.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                                if (parentCust.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota() != 0 || parentCust.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota() != -1) {
                                                    if (parentCust.getCustomerQuotaBooster().get(0).getVolumebasedunusedquota() < 0) {
                                                        log.info("Already Consumed and Overuage allowance is " + parentCust.getCustomerQuotaBooster().get(0).isAllowoverusage());
                                                    } else {
                                                        usedQuota = parentCust.getCustomerQuotaBooster().get(0).getVolumebasedunusedquota() - totalUsage;
                                                        if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable() && parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null && parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                                            double reservedQuota = (Double.valueOf(parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota()) / 100;
                                                            usedQuota = reservedQuota - totalUsage;
                                                            log.info("userQuota: " + usedQuota + " parent reserved Quota: " + parentCust.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                        }
                                                        if (usedQuota <= 0) {
                                                            firecoa = true;
                                                            log.info("UsedQuota<0 FireCoA");
                                                            custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE);
                                                            COATriggerReson = CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE;
                                                            if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                        }
                                                        noBooster = false;
                                                        isBandwidthQuotaupdate = true;
                                                    }
                                                }
                                            }

                                            if (parentCust.getCustomerQuotaBooster().get(0).getTimebasedunusedquota() > 0 && parentCust.getCustomerQuotaBooster().get(0).getQuotatype().equalsIgnoreCase("Time") || parentCust.getCustomerQuotaBooster().get(0).getQuotatype().equalsIgnoreCase("Both")) {
                                                Double unusedTimeQuota = (double) -1;
                                                Double totalTimeQuota = (double) 0;
                                                Double usedTimeQuota = (double) -1;

                                                if (parentCust.getCustomerQuotaBooster().get(0).getTimebasedtotalquota() != 0 || parentCust.getCustomerQuotaBooster().get(0).getTimebasedtotalquota() != -1) {
                                                    unusedTimeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerQuotaBooster().get(0).getTimebasedunusedquota(), parentCust.getCustomerQuotaBooster().get(0).getTimequotaunit());
                                                    totalTimeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerQuotaBooster().get(0).getTimebasedtotalquota(), parentCust.getCustomerQuotaBooster().get(0).getTimequotaunit());
                                                    usedTimeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerQuotaBooster().get(0).getTimebasedusedquota(), parentCust.getCustomerQuotaBooster().get(0).getTimequotaunit());
                                                    percentageUsage = ((totaltime + usedTimeQuota) * 100) / totalTimeQuota;
                                                    log.info("Total Time in Session:" + totaltime + ":Current Unused:" + unusedTimeQuota + ":Total Allowed:" + parentCust.getCustomerQuotaBooster().get(0).getTimebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                                    if (unusedTimeQuota < 0) {
                                                        log.info("Already Consumed and Overuage allowance is " + unusedTimeQuota);
                                                    } else {
                                                        if (parentCust.getCustomerQuotaBooster().get(0).isChunkAvailable() && parentCust.getCustomerQuotaBooster().get(0).getReservedQuotaInPer() != null && parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                                            log.info("Parent Reserved Quota in per: " + parentCust.getCustomerQuotaBooster().get(0).getReservedQuotaInPer());
                                                            log.info("Parent Reserved unused Quota: " + parentCust.getCustomerQuotaBooster().get(0).getReservedunusedquota());
                                                            double reservedQuota = (Double.valueOf(parentCust.getCustomerQuotaBooster().get(0).getReservedQuotaInPer()) * parentCust.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota()) / 100;
                                                            usedQuota = parentCust.getCustomerQuotaBooster().get(0).getTotalReservedQuota() - reservedQuota - usedQuota;
                                                            if (usedQuota <= 0) {
                                                                log.info("UsedQuota<0 FireCoA");
                                                                log.info("userQuota: " + usedQuota + " parent reserved Quota: " + parentCust.getCustomerQuotaBooster().get(0).getReservedunusedquota());
                                                                firecoa = true;
                                                                noBooster = true;
                                                                COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                                if (parentCust.getCustomerQuotaBooster() != null && parentCust.getCustomerQuotaBooster().size() > 0) {
                                                                    custPackgeRelId = parentCust.getCustomerQuotaBooster().get(0).getCustpackageid();
                                                                }
                                                                parentCust.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                            } else {
                                                                noBooster = false;
                                                                isBandwidthQuotaupdate = true;
                                                            }
                                                        } else {
                                                            usedQuota = unusedTimeQuota - totaltime;
                                                            if (usedQuota <= 0) {
                                                                log.info("UsedQuota<0 FireCoA");
                                                                firecoa = true;
                                                                noBooster = true;
                                                                COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                                if (parentCust.getCustomerQuotaBooster() != null && parentCust.getCustomerQuotaBooster().size() > 0) {
                                                                    custPackgeRelId = parentCust.getCustomerQuotaBooster().get(0).getCustpackageid();
                                                                }
                                                                parentCust.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                            } else {
                                                                noBooster = false;
                                                                isBandwidthQuotaupdate = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (!isBandwidthQuotaupdate && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && parentCust.isAuthStatus() && parentCust.getCustomerVolueBooster() != null && parentCust.getCustomerVolueBooster().get(0) != null) {
                                            log.info("Volume Customer Type:" + parentCust.getCustomerVolueBooster().get(0).getQuotatype() + ":Quota:" + parentCust.getCustomerVolueBooster().get(0).getQuotaunit());
                                            if (parentCust.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Data") || parentCust.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Both")) {
                                                totalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, parentCust.getCustomerVolueBooster().get(0).getQuotaunit());
                                                percentageUsage = ((totalUsage + parentCust.getCustomerVolueBooster().get(0).getVolumebasedusedquota()) * 100) / parentCust.getCustomerVolueBooster().get(0).getVolumebasedtotalquota();
                                                log.info("Total Used in Session:" + totalUsage + ":Current Unused:" + parentCust.getCustomerVolueBooster().get(0).getVolumebasedunusedquota() + ":Total Allowed:" + parentCust.getCustomerVolueBooster().get(0).getVolumebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                                if (parentCust.getCustomerVolueBooster().get(0).getVolumebasedtotalquota() != 0 || parentCust.getCustomerVolueBooster().get(0).getVolumebasedtotalquota() != -1) {
                                                    if (parentCust.getCustomerVolueBooster().get(0).getVolumebasedunusedquota() < 0) {
                                                        log.info("Already Consumed and Overuage allowance is " + parentCust.getCustomerVolueBooster().get(0).isAllowoverusage());
                                                    } else {
                                                        usedQuota = parentCust.getCustomerVolueBooster().get(0).getVolumebasedunusedquota() - totalUsage;
                                                        if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable() && parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null && parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                                            double reservedQuota = (Double.valueOf(parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota()) / 100;
                                                            usedQuota = reservedQuota - totalUsage;
                                                            log.info("userQuota: " + usedQuota + " parent reserved Quota: " + parentCust.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                        }
                                                        if (usedQuota <= 0) {
                                                            firecoa = true;
                                                            log.info("UsedQuota<0 FireCoA");
                                                            custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE);
                                                            COATriggerReson = CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE;
                                                            if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                        }
                                                        noBooster = false;
                                                    }
                                                }
                                            }

                                            if (parentCust.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Time") || parentCust.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Both")) {
                                                Double unusedTimeQuota = (double) -1;
                                                Double totalTimeQuota = (double) 0;
                                                Double usedTimeQuota = (double) -1;

                                                if (parentCust.getCustomerVolueBooster().get(0).getTimebasedtotalquota() != 0 || parentCust.getCustomerVolueBooster().get(0).getTimebasedtotalquota() != -1) {
                                                    unusedTimeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerVolueBooster().get(0).getTimebasedunusedquota(), parentCust.getCustomerVolueBooster().get(0).getTimequotaunit());
                                                    totalTimeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerVolueBooster().get(0).getTimebasedtotalquota(), parentCust.getCustomerVolueBooster().get(0).getTimequotaunit());
                                                    usedTimeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerVolueBooster().get(0).getTimebasedusedquota(), parentCust.getCustomerVolueBooster().get(0).getTimequotaunit());
                                                    percentageUsage = ((totaltime + usedTimeQuota) * 100) / totalTimeQuota;
                                                    log.info("Total Time in Session:" + totaltime + ":Current Unused:" + unusedTimeQuota + ":Total Allowed:" + parentCust.getCustomerVolueBooster().get(0).getTimebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                                    if (unusedTimeQuota < 0) {
                                                        log.info("Already Consumed and Overuage allowance is " + unusedTimeQuota);
                                                    } else {
                                                        if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable() && parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null && parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                                            log.info("Parent Reserved Quota in per: " + parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer());
                                                            log.info("Parent Reserved unused Quota: " + parentCust.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                            double reservedQuota = (Double.valueOf(parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota()) / 100;
                                                            usedQuota = parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() - reservedQuota - usedQuota;
                                                            if (usedQuota <= 0) {
                                                                log.info("UsedQuota<0 FireCoA");
                                                                log.info("userQuota: " + usedQuota + " parent reserved Quota: " + parentCust.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                                firecoa = true;
                                                                noBooster = true;
                                                                COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                                if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                                    custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                                }
                                                                parentCust.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                            } else {
                                                                noBooster = false;
                                                            }
                                                        } else {
                                                            usedQuota = unusedTimeQuota - totaltime;
                                                            if (usedQuota <= 0) {
                                                                log.info("UsedQuota<0 FireCoA");
                                                                firecoa = true;
                                                                noBooster = true;
                                                                COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                                if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                                    custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                                }
                                                                parentCust.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                            } else {
                                                                noBooster = false;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    //Plan Identification QuotaBooster plan
                                    else if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && custRetrunData.isAuthStatus() && custRetrunData.getCustomerQuotaBooster() != null && custRetrunData.getCustomerQuotaBooster().get(0) != null) {
                                        log.warn("Bandwidth Customer Type:" + custRetrunData.getCustomerQuotaBooster().get(0).getQuotatype() + ":Quota:" + custRetrunData.getCustomerQuotaBooster().get(0).getQuotaunit());
                                        if (custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedunusedquota() > 0 && custRetrunData.getCustomerQuotaBooster().get(0).getQuotatype().equalsIgnoreCase("Data") || custRetrunData.getCustomerQuotaBooster().get(0).getQuotatype().equalsIgnoreCase("Both")) {
                                            totalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, custRetrunData.getCustomerQuotaBooster().get(0).getQuotaunit());
                                            percentageUsage = ((totalUsage + custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedusedquota()) * 100) / custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota();
                                            log.info("Total Used in Session:" + totalUsage + ":Current Unused:" + custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedunusedquota() + ":Total Allowed:" + custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                            if (custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota() != 0 || custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota() != -1) {
                                                if (custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedunusedquota() < 0) {
                                                    log.info("Already Consumed and Overuage allowance is " + custRetrunData.getCustomerQuotaBooster().get(0).isAllowoverusage());
                                                } else {
                                                    usedQuota = custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedunusedquota() - totalUsage;
                                                    if (custRetrunData.getCustomerQuotaBooster().get(0).isChunkAvailable() && custRetrunData.getCustomerQuotaBooster().get(0).getReservedQuotaInPer() != null && custRetrunData.getCustomerQuotaBooster().get(0).getTotalReservedQuota() != null) {
                                                        double reservedQuota = (Double.valueOf(custRetrunData.getCustomerQuotaBooster().get(0).getReservedQuotaInPer()) * custRetrunData.getCustomerQuotaBooster().get(0).getTimebasedtotalquota()) / 100;
                                                        usedQuota = reservedQuota - totalUsage;
                                                        log.info("userQuota: " + usedQuota + " parent reserved Quota: " + custRetrunData.getCustomerQuotaBooster().get(0).getReservedunusedquota());
                                                    }
                                                    if (usedQuota <= 0) {
                                                        firecoa = true;
                                                        log.info("UsedQuota<0 FireCoA");
                                                        custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.QUOTA_BOOSTER_EXPIRE);
                                                        COATriggerReson = CommonConstants.EVENTCONSTANTS.QUOTA_BOOSTER_EXPIRE;
                                                        if (custRetrunData.getCustomerQuotaBooster() != null && custRetrunData.getCustomerQuotaBooster().size() > 0) {
                                                            custPackgeRelId = custRetrunData.getCustomerQuotaBooster().get(0).getCustpackageid();
                                                        }
                                                    }
                                                    noBooster = false;
                                                    isBandwidthQuotaupdate = true;
                                                }
                                            }
                                        }
                                        // check customer quota booster
                                        if (custRetrunData.getCustomerQuotaBooster().get(0).getTimebasedunusedquota() > 0 && custRetrunData.getCustomerQuotaBooster().get(0).getQuotatype().equalsIgnoreCase("Time") || custRetrunData.getCustomerQuotaBooster().get(0).getQuotatype().equalsIgnoreCase("Both")) {
                                            Double unusedTimeQuota = (double) -1;
                                            Double totalTimeQuota = (double) 0;
                                            Double usedTimeQuota = (double) -1;

                                            if (custRetrunData.getCustomerQuotaBooster().get(0).getTimebasedtotalquota() != 0 || custRetrunData.getCustomerQuotaBooster().get(0).getTimebasedtotalquota() != -1) {
                                                unusedTimeQuota = (double) radiusUtility.convertUsageToSec((long) custRetrunData.getCustomerQuotaBooster().get(0).getTimebasedunusedquota(), custRetrunData.getCustomerQuotaBooster().get(0).getTimequotaunit());
                                                totalTimeQuota = (double) radiusUtility.convertUsageToSec((long) custRetrunData.getCustomerQuotaBooster().get(0).getTimebasedtotalquota(), custRetrunData.getCustomerQuotaBooster().get(0).getTimequotaunit());
                                                usedTimeQuota = (double) radiusUtility.convertUsageToSec((long) custRetrunData.getCustomerQuotaBooster().get(0).getTimebasedusedquota(), custRetrunData.getCustomerQuotaBooster().get(0).getTimequotaunit());
                                                percentageUsage = ((totaltime + usedTimeQuota) * 100) / totalTimeQuota;
                                                log.warn("Total Time in Session:" + totaltime + ":Current Unused:" + unusedTimeQuota + ":Total Allowed:" + custRetrunData.getCustomerQuotaBooster().get(0).getTimebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                                if (unusedTimeQuota < 0) {
                                                    log.info("Already Consumed and Overuage allowance is " + unusedTimeQuota);
                                                } else {
                                                    if (custRetrunData.getCustomerQuotaBooster().get(0).isChunkAvailable() && custRetrunData.getCustomerQuotaBooster().get(0).getReservedQuotaInPer() != null && custRetrunData.getCustomerQuotaBooster().get(0).getTotalReservedQuota() != null) {
                                                        log.info("Parent Reserved Quota in per: " + custRetrunData.getCustomerQuotaBooster().get(0).getReservedQuotaInPer());
                                                        log.info("Parent Reserved unused Quota: " + custRetrunData.getCustomerQuotaBooster().get(0).getReservedunusedquota());
                                                        double reservedQuota = (Double.valueOf(custRetrunData.getCustomerQuotaBooster().get(0).getReservedQuotaInPer()) * custRetrunData.getCustomerQuotaBooster().get(0).getVolumebasedtotalquota()) / 100;
                                                        usedQuota = custRetrunData.getCustomerQuotaBooster().get(0).getTotalReservedQuota() - reservedQuota - usedQuota;
                                                        if (usedQuota <= 0) {
                                                            log.info("UsedQuota<0 FireCoA");
                                                            log.info("userQuota: " + usedQuota + " parent reserved Quota: " + custRetrunData.getCustomerQuotaBooster().get(0).getReservedunusedquota());
                                                            firecoa = true;
                                                            noBooster = true;
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (custRetrunData.getCustomerQuotaBooster() != null && custRetrunData.getCustomerQuotaBooster().size() > 0) {
                                                                custPackgeRelId = custRetrunData.getCustomerQuotaBooster().get(0).getCustpackageid();
                                                            }
                                                            custRetrunData.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                        } else {
                                                            noBooster = false;
                                                            isBandwidthQuotaupdate = true;
                                                        }
                                                    } else {
                                                        usedQuota = unusedTimeQuota - totaltime;
                                                        if (usedQuota <= 0) {
                                                            log.info("UsedQuota<0 FireCoA");
                                                            firecoa = true;
                                                            noBooster = true;
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (custRetrunData.getCustomerQuotaBooster() != null && custRetrunData.getCustomerQuotaBooster().size() > 0) {
                                                                custPackgeRelId = custRetrunData.getCustomerQuotaBooster().get(0).getCustpackageid();
                                                            }
                                                            custRetrunData.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                        } else {
                                                            noBooster = false;
                                                            isBandwidthQuotaupdate = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //Plan Identification for volume booster base plan
                                    else if (!isBandwidthQuotaupdate && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && custRetrunData.isAuthStatus() && custRetrunData.getCustomerVolueBooster() != null && custRetrunData.getCustomerVolueBooster().get(0) != null) {
                                        log.warn("Parent shareable quota not available Looking for independent customer: " + custRetrunData.getUsername());
                                        log.info("Customer Type:" + custRetrunData.getCustomerVolueBooster().get(0).getQuotatype() + ":Quota:" + custRetrunData.getCustomerVolueBooster().get(0).getQuotaunit());
                                        if (custRetrunData.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Data") || custRetrunData.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Both")) {
                                            totalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, custRetrunData.getCustomerVolueBooster().get(0).getQuotaunit());
                                            percentageUsage = ((totalUsage + custRetrunData.getCustomerVolueBooster().get(0).getVolumebasedusedquota()) * 100) / custRetrunData.getCustomerVolueBooster().get(0).getVolumebasedtotalquota();
                                            log.warn("Total Used in Session:" + totalUsage + ":Current Unused:" + custRetrunData.getCustomerVolueBooster().get(0).getVolumebasedunusedquota() + ":Total Allowed:" + custRetrunData.getCustomerVolueBooster().get(0).getVolumebasedtotalquota() + ":percentageUsage:" + percentageUsage);
                                            if (custRetrunData.getCustomerVolueBooster().get(0).getVolumebasedtotalquota() != 0 || custRetrunData.getCustomerVolueBooster().get(0).getVolumebasedtotalquota() != -1) {
                                                if (custRetrunData.getCustomerVolueBooster().get(0).getVolumebasedunusedquota() < 0) {
                                                    log.info("Already Consumed and Overuage allowance is " + custRetrunData.getCustomerVolueBooster().get(0).isAllowoverusage());
                                                } else {
                                                    if (custRetrunData.getCustomerBasePlan().get(0).isChunkAvailable() && custRetrunData.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null && custRetrunData.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                                        usedQuota = custRetrunData.getCustomerBasePlan().get(0).getReservedunusedquota() - totalUsage;
                                                        if (usedQuota <= 0) {
                                                            log.info("UsedQuota<0 FireCoA");
                                                            log.info("Used quota: " + usedQuota + " customer reserved quota: " + custRetrunData.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                            firecoa = true;
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = custRetrunData.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                            custRetrunData.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                        }
                                                        noBooster = false;
                                                    } else {
                                                        usedQuota = custRetrunData.getCustomerVolueBooster().get(0).getVolumebasedunusedquota() - totalUsage;
                                                        if (usedQuota <= 0) {
                                                            log.info("UsedQuota<0 FireCoA");
                                                            log.info("Used quota: " + usedQuota);
                                                            firecoa = true;
                                                            COATriggerReson = CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE;
                                                            if (custRetrunData.getCustomerVolueBooster() != null && custRetrunData.getCustomerVolueBooster().size() > 0) {
                                                                custPackgeRelId = custRetrunData.getCustomerVolueBooster().get(0).getCustpackageid();
                                                            }
                                                            custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE);
                                                        }
                                                        noBooster = false;
                                                    }

                                                }
                                            }
                                        }

                                        if (custRetrunData.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Time") || custRetrunData.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Both")) {
                                            Double unusedTimeQuota = (double) -1;
                                            Double totalTimeQuota = (double) 0;
                                            Double usedTimeQuota = (double) -1;

                                            if (custRetrunData.getCustomerVolueBooster().get(0).getTimebasedtotalquota() != 0 || custRetrunData.getCustomerVolueBooster().get(0).getTimebasedtotalquota() != -1) {
                                                unusedTimeQuota = (double) radiusUtility.convertUsageToSec((long) custRetrunData.getCustomerVolueBooster().get(0).getTimebasedunusedquota(), custRetrunData.getCustomerVolueBooster().get(0).getTimequotaunit());
                                                totalTimeQuota = (double) radiusUtility.convertUsageToSec((long) custRetrunData.getCustomerVolueBooster().get(0).getTimebasedtotalquota(), custRetrunData.getCustomerVolueBooster().get(0).getTimequotaunit());
                                                usedTimeQuota = (double) radiusUtility.convertUsageToSec((long) custRetrunData.getCustomerVolueBooster().get(0).getTimebasedusedquota(), custRetrunData.getCustomerVolueBooster().get(0).getTimequotaunit());

                                                percentageUsage = ((totaltime + usedTimeQuota) * 100) / totalTimeQuota;
                                                log.info("Total Time in Session:" + totaltime + ":Current Unused:" + unusedTimeQuota + ":Total Allowed:" + custRetrunData.getCustomerVolueBooster().get(0).getTimebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                                if (unusedTimeQuota < 0) {
                                                    log.info("Already Consumed and Overuage allowance is " + unusedTimeQuota);
                                                } else {
                                                    if (custRetrunData.getCustomerBasePlan().get(0).isChunkAvailable() && custRetrunData.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null && custRetrunData.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                                        usedQuota = custRetrunData.getCustomerBasePlan().get(0).getReservedunusedquota() - totaltime;
                                                        if (usedQuota <= 0) {
                                                            log.info("UsedQuota<0 FireCoA");
                                                            log.info("Used quota: " + usedQuota + " Customer reserved quota; " + custRetrunData.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                            firecoa = true;
                                                            noBooster = true;
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = custRetrunData.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                            custRetrunData.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                        } else {
                                                            noBooster = false;
                                                        }
                                                    } else {

                                                        usedQuota = unusedTimeQuota - totaltime;
                                                        if (usedQuota <= 0) {
                                                            log.info("UsedQuota<0 FireCoA");
                                                            log.info("Used quota: " + usedQuota);
                                                            firecoa = true;
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = custRetrunData.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                            custRetrunData.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                            noBooster = true;
                                                        } else {
                                                            noBooster = false;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //parent customer with no booster plan
                                    if (noBooster && parentCust != null) {
                                        if (!isBandwidthQuotaupdate && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && parentCust.isAuthStatus() && noBooster && parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota() != 0 && (parentCust.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Data") || parentCust.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {
                                            totalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, parentCust.getCustomerBasePlan().get(0).getQuotaunit());
                                            percentageUsage = ((totalUsage + parentCust.getCustomerBasePlan().get(0).getVolumebasedusedquota()) * 100) / parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota();

                                            log.info("Total Used in Session:" + totalUsage + ":Current Unused:" + parentCust.getCustomerBasePlan().get(0).getVolumebasedunusedquota() + ":Total Allowed:" + parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota() + ":percentageUsage:" + percentageUsage);
                                            if (parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota() != 0 || parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota() != -1) {
                                                if (parentCust.getCustomerBasePlan().get(0).getVolumebasedunusedquota() < 0) {
                                                    log.info("Already Consumed and Overuage allowance is " + parentCust.getCustomerBasePlan().get(0).isAllowoverusage());
                                                } else {
                                                    if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable() && parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null && parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                                        double reservedQuota = (Double.valueOf(parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota()) / 100;
                                                        usedQuota = reservedQuota - totalUsage;
                                                        log.info("userQuota: " + usedQuota + " parent reserved Quota: " + parentCust.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                        if (usedQuota <= 0) {
                                                            log.info("Used quota: " + usedQuota + " parent reserved Quota: " + parentCust.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                            firecoa = true;
                                                            log.info("UsedQuota<0 FireCoA");
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                            parentCust.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                        }
                                                    } else {
                                                        usedQuota = parentCust.getCustomerBasePlan().get(0).getVolumebasedunusedquota() - totalUsage;
                                                        if (usedQuota <= 0) {
                                                            log.info("Used quota: " + usedQuota);
                                                            firecoa = true;
                                                            log.info("UsedQuota<0 FireCoA");
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                            parentCust.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //Customer base plan with volume quota
                                    else if (!isBandwidthQuotaupdate && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && custRetrunData.isAuthStatus() && noBooster && custRetrunData.getCustomerBasePlan().get(0).getVolumebasedtotalquota() != 0 && (custRetrunData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Data") || custRetrunData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {

                                        CustomerPlanData customerPlanData = custRetrunData.getCustomerBasePlan().get(0);
                                        log.warn("Customer Plan Type: " + customerPlanData.getPurchaseType());
                                        if (customerPlanData.isSkipQuotaUpdate()) {
                                            log.info("Skip quota flag enabled, Customer Plan: " + customerPlanData.getPlanName());
                                            // trigger COA DM
                                            skipQuotaReset = true;
                                            COATriggerReson = CommonConstants.CoaDmResonContant.QUOTA_RESET;
                                            firecoa = true;
                                            custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.QUOTA_RESET);
                                        } else if (customerPlanData.isTriggerCoaDm()) {
                                            firecoa = true;
                                            if (customerPlanData.getOnQuotaExhaustEventName() != null && !customerPlanData.getOnQuotaExhaustEventName().isEmpty()) {
                                                COATriggerReson = customerPlanData.getOnQuotaExhaustEventName();
                                                custRetrunData.setEventName(customerPlanData.getOnQuotaExhaustEventName());
                                            }
                                            customerPlanData.setTriggerCoaDm(false);
                                            updateCPRCOADMFlag = true;
                                            isTriggerCOADM = false;
                                            log.warn("Trigegr COA/DM flag enable event: " + customerPlanData.getOnQuotaExhaustEventName() + " ,Customer Plan: " + customerPlanData.getPlanName());
                                        }

                                        totalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, customerPlanData.getQuotaunit());
                                        percentageUsage = ((totalUsage + customerPlanData.getVolumebasedusedquota()) * 100) / customerPlanData.getVolumebasedtotalquota();

                                        log.warn("Total Used in Session:" + totalUsage + ":Current Unused:" + customerPlanData.getVolumebasedunusedquota() + ":Total Allowed:" + customerPlanData.getVolumebasedtotalquota() + ":percentageUsage:" + percentageUsage);
                                        if (customerPlanData.getVolumebasedtotalquota() != 0 || customerPlanData.getVolumebasedtotalquota() != -1) {
                                            if (customerPlanData.getVolumebasedunusedquota() < 0) {
                                                log.info("Already Consumed and Overuage allowance is " + customerPlanData.isAllowoverusage());
                                            } else {
                                                if (customerPlanData.isChunkAvailable() && customerPlanData.getReservedQuotaInPer() != null && customerPlanData.getTotalReservedQuota() != null) {
                                                    usedQuota = customerPlanData.getReservedunusedquota() - totalUsage;
                                                    if (usedQuota <= 0) {
                                                        customerPlanData.setVolumebasedunusedquota(usedQuota);
                                                        customerPlanData.setVolumequota(usedQuota);
                                                        log.warn("Used quota: " + usedQuota + " Customer Reserved Quota: " + customerPlanData.getReservedunusedquota());
                                                        firecoa = true;
                                                        log.warn("UsedQuota<0 FireCoA");
                                                        COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                        if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                            custPackgeRelId = customerPlanData.getCustpackageid();
                                                        }
                                                        custRetrunData.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                    }
                                                } else {
                                                    usedQuota = customerPlanData.getVolumebasedunusedquota() - totalUsage;
                                                    if (usedQuota <= 0) {
                                                        customerPlanData.setVolumebasedunusedquota(usedQuota);
                                                        customerPlanData.setVolumequota(usedQuota);
                                                        log.info("UsedQuota<0 FireCoA");
                                                        log.info("Used quota: " + usedQuota);
                                                        firecoa = true;
                                                        COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                        if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                            custPackgeRelId = customerPlanData.getCustpackageid();
                                                        }
                                                        custRetrunData.setEventName(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST);
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    if (noBooster && parentCust != null) {
                                        if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && parentCust.isAuthStatus() && noBooster && parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota() != 0 && (parentCust.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Time") || parentCust.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {
                                            Double timeQuota = (double) -1;
                                            Double totalTimeQuota = (double) 0;
                                            Double usedTimeQuota = (double) -1;

                                            if (parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota() != 0 || parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota() != -1) {
                                                timeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerBasePlan().get(0).getTimebasedunusedquota(), parentCust.getCustomerVolueBooster().get(0).getTimequotaunit());
                                                totalTimeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota(), parentCust.getCustomerVolueBooster().get(0).getTimequotaunit());
                                                usedTimeQuota = (double) radiusUtility.convertUsageToSec((long) parentCust.getCustomerBasePlan().get(0).getTimebasedusedquota(), parentCust.getCustomerVolueBooster().get(0).getTimequotaunit());

                                                percentageUsage = ((totaltime + usedTimeQuota) * 100) / totalTimeQuota;
                                                log.info("Total Time in Session:" + totaltime + ":Current Unused:" + timeQuota + ":Total Allowed:" + parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                                if (timeQuota < 0) {
                                                    log.info("Already Consumed and Overuage allowance is " + timeQuota);
                                                } else {
                                                    if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable() && parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null && parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                                        double reservedQuota = (Double.valueOf(parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota()) / 100;
                                                        usedQuota = reservedQuota - totalTimeQuota;
                                                        if (usedQuota <= 0) {
                                                            log.info("UsedQuota<0 FireCoA");
                                                            log.info("Used quota: " + usedQuota + " Parent reserved quota: " + parentCust.getCustomerBasePlan().get(0).getReservedunusedquota());
                                                            firecoa = true;
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                        }
                                                    } else {
                                                        usedQuota = timeQuota - totaltime;
                                                        if (usedQuota <= 0) {
                                                            log.info("UsedQuota<0 FireCoA");
                                                            log.info("Used quota: " + usedQuota);
                                                            firecoa = true;
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                            if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //Customer base plan with time quota
                                    else if (!isBandwidthQuotaupdate && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && custRetrunData.isAuthStatus() && noBooster && custRetrunData.getCustomerBasePlan().get(0).getTimebasedtotalquota() != 0 && (custRetrunData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Time") || custRetrunData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {
                                        Double timeQuota = (double) -1;
                                        Double totalTimeQuota = (double) 0;
                                        Double usedTimeQuota = (double) -1;

                                        CustomerPlanData customerPlanData = custRetrunData.getCustomerBasePlan().get(0);
                                        log.info("Customer Plan Type: " + customerPlanData.getPurchaseType());
                                        if (customerPlanData.getTimebasedtotalquota() != 0 || customerPlanData.getTimebasedtotalquota() != -1) {

                                            if (customerPlanData.isSkipQuotaUpdate()) {
                                                // trigger COA DM
                                                skipQuotaReset = true;
                                                COATriggerReson = CommonConstants.CoaDmResonContant.QUOTA_RESET;
                                                firecoa = true;
                                                custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.QUOTA_RESET);
                                            }

                                            timeQuota = (double) radiusUtility.convertUsageToSec((long) customerPlanData.getTimebasedunusedquota(), customerPlanData.getTimequotaunit());
                                            totalTimeQuota = (double) radiusUtility.convertUsageToSec((long) customerPlanData.getTimebasedtotalquota(), customerPlanData.getTimequotaunit());
                                            usedTimeQuota = (double) radiusUtility.convertUsageToSec((long) customerPlanData.getTimebasedusedquota(), customerPlanData.getTimequotaunit());
                                            if (usedTimeQuota > 1)
                                                usedTimeQuota = 1d;
                                            percentageUsage = ((totaltime + (usedTimeQuota / 60)) * 100) / totalTimeQuota;
                                            log.info("Total Time in Session:" + totaltime + ":Current Unused:" + timeQuota + ":Total Allowed:" + customerPlanData.getTimebasedtotalquota() + ":percentageUsage:" + percentageUsage);

                                            if (timeQuota < 0) {
                                                log.info("Already Consumed and Overuage allowance is " + timeQuota);
                                            } else {
                                                if (customerPlanData.isChunkAvailable() && customerPlanData.getReservedQuotaInPer() != null && customerPlanData.getTotalReservedQuota() != null) {
                                                    usedQuota = customerPlanData.getReservedunusedquota() - timeQuota - totaltime;
                                                    if (usedQuota <= 0) {
                                                        log.info("Used quota: " + usedQuota + " Customer reserved Quota: " + customerPlanData.getReservedunusedquota());
                                                        firecoa = true;
                                                        COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                        if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                            custPackgeRelId = customerPlanData.getCustpackageid();
                                                        }
                                                    }
                                                } else {
                                                    usedQuota = timeQuota - totaltime;
                                                    if (usedQuota <= 0) {
                                                        log.info("Used quota: " + usedQuota);
                                                        firecoa = true;
                                                        COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                        if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                            custPackgeRelId = customerPlanData.getCustpackageid();
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }

                                    //Base Plan Validity Varification for parent customer
                                    if (parentCust != null) {
                                        if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && parentCust.isAuthStatus() && parentCust.getCustomerBasePlan().get(0).getEnddate() != null) {
                                            Timestamp timestampTomorrow = new Timestamp(parentCust.getCustomerBasePlan().get(0).getEnddate().getTime());
                                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                            long diffInMS = parentCust.getCustomerBasePlan().get(0).getEnddate().getTime() - timestamp.getTime();
                                            long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMS);
                                            log.debug("EndDate:" + parentCust.getCustomerBasePlan().get(0).getEnddate() + ":currentDate:" + timestamp + ":User:" + parentCust.getUsername() + ":Expiry Differece in milliSecond is:" + diffInMS + ":and second is:" + seconds);
                                            log.debug("Total Volume Quota:" + parentCust.getCustomerBasePlan().get(0).getVolumequota() + ":Total Time Quota:" + parentCust.getCustomerBasePlan().get(0).getTimequota() + ":Quota Type:" + parentCust.getCustomerBasePlan().get(0).getQuotatype());
                                            if (seconds <= 0) {
                                                log.info("Validity Over FireCoA");
                                                log.info("Used seconds: " + seconds);
                                                firecoa = true;
                                                expiry = true;
                                                COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                                if (parentCust.getCustomerBasePlan() != null && parentCust.getCustomerBasePlan().size() > 0) {
                                                    custPackgeRelId = parentCust.getCustomerBasePlan().get(0).getCustpackageid();
                                                }
                                            }
                                        }
                                    }
                                    //Base Plan Validity Varification for customer
                                    else if (!isBandwidthQuotaupdate && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT) && custRetrunData.isAuthStatus() && custRetrunData.getCustomerBasePlan().get(0).getEnddate() != null) {
                                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                        long diffInMS = custRetrunData.getCustomerBasePlan().get(0).getEnddate().getTime() - timestamp.getTime();
                                        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMS);
                                        log.debug("EndDate:" + custRetrunData.getCustomerBasePlan().get(0).getEnddate() + ":currentDate:" + timestamp + ":User:" + custRetrunData.getUsername() + ":Expiry Differece in milliSecond is:" + diffInMS + ":and second is:" + seconds);
                                        log.warn("Total Volume Quota:" + custRetrunData.getCustomerBasePlan().get(0).getVolumequota() + ":Total Time Quota:" + custRetrunData.getCustomerBasePlan().get(0).getTimequota() + ":Quota Type:" + custRetrunData.getCustomerBasePlan().get(0).getQuotatype());
                                        if (seconds <= 0) {
                                            log.info("Validity Over FireCoA");
                                            log.info("Used seconds: " + seconds);
                                            firecoa = true;
                                            expiry = true;
                                            COATriggerReson = CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST;
                                            if (custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                custPackgeRelId = custRetrunData.getCustomerBasePlan().get(0).getCustpackageid();
                                            }
                                        }
                                        if (custRetrunData != null && custRetrunData.getCustomerBasePlan() != null
                                                && custRetrunData.getCustomerBasePlan().get(0).isSkipQuotaUpdate()) {
                                            // trigger COA DM
                                            skipQuotaReset = true;
                                            COATriggerReson = CommonConstants.CoaDmResonContant.QUOTA_RESET;
                                            firecoa = true;
                                            custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.QUOTA_RESET);
                                        }
                                    }

                                    //% Usage and Notification
                                    log.warn("Final Percentage Use" + percentageUsage);
                                    if (percentageUsage > 0) {
                                        //check parent and child
                                        CustomerData notificaitonCustData = null;
                                        if (parentCust != null) {
                                            notificaitonCustData = parentCust;
                                        } else {
                                            notificaitonCustData = custRetrunData;
                                        }
                                        //check baseplan or volume booster
                                        if (notificaitonCustData.getCustomerVolueBooster() != null && notificaitonCustData.getCustomerVolueBooster().size() > 0) {
                                            //Send notification for add on plan
                                            radiusUtility.sendNotificationOnQuotaUsage(notificaitonCustData.getCustomerVolueBooster().get(0).getCustpackageid(), notificaitonCustData.getCustid(), percentageUsage);
                                        } else {
                                            //Send notification for Base Plan
                                            radiusUtility.sendNotificationOnQuotaUsage(notificaitonCustData.getCustomerBasePlan().get(0).getCustpackageid(), notificaitonCustData.getCustid(), percentageUsage);
                                        }
                                    }
                                    //check QOS
                                    if (custRetrunData.isAuthStatus() && custRetrunData.getCustomerBasePlan() != null) {
                                        if (custRetrunData.getCustomerBasePlan().get(0).getPlanQosPolicyMapping() != null && percentageUsage != null && percentageUsage > 0) {
                                            List<PlanQosPolicyMapping> planQosPolicyMappings = custRetrunData.getCustomerBasePlan().get(0).getPlanQosPolicyMapping();
                                            if (!CollectionUtils.isEmpty(planQosPolicyMappings)) {
                                                int planQosId = 0;
                                                for (int i = 0; i < planQosPolicyMappings.size(); i++) {
                                                    if (percentageUsage >= planQosPolicyMappings.get(i).getFrompercentage() && percentageUsage <= planQosPolicyMappings.get(i).getTopercentage())
                                                        planQosId = planQosPolicyMappings.get(i).getQosPolicy();
                                                    if (planQosId != 0)
                                                        break;
                                                }
                                                if (planQosId != 0) {
                                                    log.debug("Plan Qos Policy found with Id: " + planQosId + " for percentage: " + percentageUsage);
                                                    dbAuth.updateCustomerPlanQos(custRetrunData.getCustomerBasePlan().get(0), planQosId);
                                                    if (percentageUsage < 100 && !custRetrunData.getCustomerBasePlan().get(0).isAllowoverusage()) {
                                                        String strClass = "tpid=" + planQosId;
                                                        custRetrunData.setStrClass(strClass);
                                                        custRetrunData.getCustomerBasePlan().get(0).setPlanQosFire(true);
                                                    }
                                                    if (custRetrunData.getStrClass() != null && attribClass != null) {
                                                        if (!attribClass.contains(custRetrunData.getStrClass())) {
                                                            log.info("Plan Qos Change Fire CoA:" + planQosId);
                                                            log.info(String.format("Plan Qos Change First Time Fire CoA %s", planQosId));
                                                            firecoa = true;
                                                            COATriggerReson = CommonConstants.CoaDmResonContant.TIME_BASE_POLICY_CHANGE;
                                                            if (!isBandwidthQuotaupdate && custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                                custPackgeRelId = custRetrunData.getCustomerBasePlan().get(0).getCustpackageid();
                                                            }
                                                            timeBasePolicyId = custRetrunData.getStrClass().substring(custRetrunData.getStrClass().indexOf("="), custRetrunData.getStrClass().length() - 1);
                                                        }
                                                    } else if (custRetrunData.getStrClass() != null && attribClass == null) {
                                                        log.info(String.format("Plan Qos Change First Time Fire CoA %s with class %s", planQosId, custRetrunData.getStrClass()));
                                                        firecoa = true;
                                                        COATriggerReson = CommonConstants.CoaDmResonContant.TIME_BASE_POLICY_CHANGE;
                                                        if (!isBandwidthQuotaupdate && custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0) {
                                                            custPackgeRelId = custRetrunData.getCustomerBasePlan().get(0).getCustpackageid();
                                                        }
                                                        timeBasePolicyId = custRetrunData.getStrClass().substring(custRetrunData.getStrClass().indexOf("="), custRetrunData.getStrClass().length() - 1);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    //Intrim Quota Reporting for In Session Usage
                                    if (!skipQuotaReset &&!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                                        if (!noBooster) {
                                            DecimalFormat userQuotaFormat = new DecimalFormat("#");
                                            userQuotaFormat.setMaximumFractionDigits(10);
                                            strUsedQuota = (df.format(usedQuota));
                                            //Add parent update quota
                                            if (isBandwidthQuotaupdate) {
                                                custQuotaInfo.setPlanType(custRetrunData.getCustomerQuotaBooster().get(0).getPlanType());
                                                custQuotaInfo.setPlanName(custRetrunData.getCustomerQuotaBooster().get(0).getPlanName());
                                                custQuotaInfo.setCustpackageid(custRetrunData.getCustomerQuotaBooster().get(0).getCustpackageid());
                                            } else {
                                                custQuotaInfo.setPlanType(custRetrunData.getCustomerVolueBooster().get(0).getPlanType());
                                                custQuotaInfo.setPlanName(custRetrunData.getCustomerVolueBooster().get(0).getPlanName());
                                                custQuotaInfo.setCustpackageid(custRetrunData.getCustomerVolueBooster().get(0).getCustpackageid());
                                            }
                                            try {
                                                boolean isFreeQuota = true;
                                                if (parentCust != null && !parentCust.isFreeQuota()) {
                                                    isFreeQuota = parentCust.isFreeQuota();
                                                    if (isBandwidthQuotaupdate) {
                                                        radiusUtility.sendCustQuotaIntrimDetailToApigw(parentCust.getCustomerQuotaBooster().get(0).getCustpackageid(), totaltime, totalUsage);
                                                        dbAcct.updateCustomerQuota(parentCust.getUsername(), String.valueOf(totalUsage), String.valueOf(totaltime), parentCust.getCustomerQuotaBooster().get(0).getCustpackageid());
                                                    } else {
                                                        radiusUtility.sendCustQuotaIntrimDetailToApigw(parentCust.getCustomerVolueBooster().get(0).getCustpackageid(), totaltime, totalUsage);
                                                        dbAcct.updateCustomerQuota(parentCust.getUsername(), String.valueOf(totalUsage), String.valueOf(totaltime), parentCust.getCustomerVolueBooster().get(0).getCustpackageid());
                                                    }
                                                } else if (!custRetrunData.isFreeQuota()) {
                                                    isFreeQuota = custRetrunData.isFreeQuota();
                                                    if (isBandwidthQuotaupdate) {
                                                        radiusUtility.sendCustQuotaIntrimDetailToApigw(custRetrunData.getCustomerQuotaBooster().get(0).getCustpackageid(), totaltime, totalUsage);
                                                        dbAcct.updateCustomerQuota(custRetrunData.getUsername(), String.valueOf(totalUsage), String.valueOf(totaltime), custRetrunData.getCustomerQuotaBooster().get(0).getCustpackageid());
                                                    } else {
                                                        radiusUtility.sendCustQuotaIntrimDetailToApigw(custRetrunData.getCustomerVolueBooster().get(0).getCustpackageid(), totaltime, totalUsage);
                                                        dbAcct.updateCustomerQuota(custRetrunData.getUsername(), String.valueOf(totalUsage), String.valueOf(totaltime), custRetrunData.getCustomerVolueBooster().get(0).getCustpackageid());
                                                    }
                                                }
                                                //update reserved quota
                                                if (!isFreeQuota) {
                                                    if (totalUsage > 0) {
                                                        dbAcct.updateReservedQuotaDtls(custRetrunData.getUsername(), custRetrunData.getCustid(), totalUsage, custRetrunData.getCustomerBasePlan().get(0).getTotalReservedQuota() - totalUsage);
                                                    } else {
                                                        dbAcct.updateReservedQuotaDtls(custRetrunData.getUsername(), custRetrunData.getCustid(), Double.valueOf(totaltime), custRetrunData.getCustomerBasePlan().get(0).getTotalReservedQuota() - totaltime);
                                                    }
                                                }

                                                log.debug("Quota Reported");
                                            } catch (Exception e) {
                                                log.debug("Sync Quota with BSS Failed:" + e.getMessage());
                                                e.printStackTrace();
                                            }
                                        } else if (parentCust != null || !custRetrunData.getStatus().equals(CommonConstants.PLAN_INACTIVE)) {
                                            DecimalFormat userQuotaFormat = new DecimalFormat("#");
                                            userQuotaFormat.setMaximumFractionDigits(10);
                                            strUsedQuota = (df.format(usedQuota));
                                            //Add parent update quota
                                            if (parentCust != null) {
                                                custQuotaInfo.setPlanType(parentCust.getCustomerBasePlan().get(0).getPlanType());
                                                custQuotaInfo.setPlanName(parentCust.getCustomerBasePlan().get(0).getPlanName());
                                                custQuotaInfo.setCustpackageid(parentCust.getCustomerBasePlan().get(0).getCustpackageid());
                                            } else {
                                                custQuotaInfo.setPlanType(custRetrunData.getCustomerBasePlan().get(0).getPlanType());
                                                custQuotaInfo.setPlanName(custRetrunData.getCustomerBasePlan().get(0).getPlanName());
                                                custQuotaInfo.setCustpackageid(custRetrunData.getCustomerBasePlan().get(0).getCustpackageid());
                                            }
                                            try {
                                                boolean isFreeQuota = false;
                                                if (parentCust != null && !parentCust.isFreeQuota()) {
                                                    isFreeQuota = parentCust.isFreeQuota();
                                                    radiusUtility.sendCustQuotaIntrimDetailToApigw(parentCust.getCustomerBasePlan().get(0).getCustpackageid(), totaltime, totalUsage);
                                                    dbAcct.updateCustomerQuota(parentCust.getUsername(), String.valueOf(totalUsage), String.valueOf(totaltime), parentCust.getCustomerBasePlan().get(0).getCustpackageid());
                                                } else if (!custRetrunData.isFreeQuota()) {
                                                    isFreeQuota = custRetrunData.isFreeQuota();
                                                    radiusUtility.sendCustQuotaIntrimDetailToApigw(custRetrunData.getCustomerBasePlan().get(0).getCustpackageid(), totaltime, totalUsage);
                                                    dbAcct.updateCustomerQuota(custRetrunData.getUsername(), String.valueOf(totalUsage), String.valueOf(totaltime), custRetrunData.getCustomerBasePlan().get(0).getCustpackageid());
                                                }
                                                //update reserved quota
                                                if (!isFreeQuota) {
                                                    double unusedQuota = 0;
                                                    if (totalUsage > 0) {
                                                        if (parentCust != null && parentCust.getCustomerBasePlan() != null)
                                                            unusedQuota = parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() - totalUsage;
                                                        else
                                                            unusedQuota = custRetrunData.getCustomerBasePlan().get(0).getTotalReservedQuota() - totalUsage;
                                                        dbAcct.updateReservedQuotaDtls(custRetrunData.getUsername(), custRetrunData.getCustid(), totalUsage, unusedQuota);
                                                    } else {
                                                        if (parentCust != null && parentCust.getCustomerBasePlan() != null)
                                                            unusedQuota = parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() - totaltime;
                                                        else
                                                            unusedQuota = custRetrunData.getCustomerBasePlan().get(0).getTotalReservedQuota() - totaltime;
                                                        dbAcct.updateReservedQuotaDtls(custRetrunData.getUsername(), custRetrunData.getCustid(), Double.valueOf(totaltime), unusedQuota);
                                                    }
                                                }
                                                log.debug("Quota Reported");
                                            } catch (Exception e) {
                                                log.debug("Sync Quota with BSS Failed:" + e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        log.warn("skipQuotaReset: "+skipQuotaReset+" skip quota calculations");
                                    }
                                    //CoA DM Processing
                                    if (skipQuotaReset) {
                                        COATriggerReson = CommonConstants.CoaDmResonContant.QUOTA_RESET;
                                        firecoa = true;
                                        custRetrunData.setEventName(CommonConstants.EVENTCONSTANTS.QUOTA_RESET);
                                        log.warn("Quota reset flag was true, will initiate COA");
                                    }
                                    if (firecoa && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                                        if (!isBandwidthQuotaupdate &&
                                                (COATriggerReson.equalsIgnoreCase(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST) || COATriggerReson.equalsIgnoreCase(CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE))
                                                && custRetrunData.getCustomerBasePlan() != null
                                                && custRetrunData.getCustomerBasePlan().get(0).getOnQuotaExhaustEventName() != null) {
                                            log.debug("PLAN_QUOTA_EXHAUST Event and Another Event also found for quota Exhaust: " + custRetrunData.getCustomerBasePlan().get(0).getOnQuotaExhaustEventName() + " for cprId: " + custPackgeRelId);
                                            if (custRetrunData.getCustomerBasePlan().get(0).getOnQuotaExhaustEventName() != null)
                                                custRetrunData.setEventName(custRetrunData.getCustomerBasePlan().get(0).getOnQuotaExhaustEventName());
                                            updateCPRCOADMFlag = true;
                                            isTriggerCOADM = false;
                                        }

                                        log.debug("CoA Profile Cache size:" + coaDMProfileDataList.size());
                                        boolean triggerSNMP = false;
                                        if (firecoa && cltGroupData != null && !CollectionUtils.isEmpty(cltGroupData.getCoaDmProfileMappings())) {
                                            //COA Profile
                                            List<CoaDmProfileMapping> coaProfileMappings = cltGroupData.getCoaDmProfileMappings().stream().sorted(Comparator.comparing(CoaDmProfileMapping::getPriority).reversed()).collect(Collectors.toList());
                                            for (CoaDmProfileMapping profileMapping : coaProfileMappings) {
                                                log.info(String.format("COA Expression Check For %s: Event: %s ", profileMapping.getCheckItem(), custRetrunData.getEventName()));
                                                boolean response = validate.checkExpression(profileMapping.getCheckItem(), request, custRetrunData, custRetrunData.getEventName());
                                                if (response) {
                                                    log.info("Expression matched: " + profileMapping.getCheckItem() + " Event: " + custRetrunData.getEventName());
                                                    if (profileMapping.getCoaProfileId() != null) {
                                                        CoAProfileId = profileMapping.getCoaProfileId();
                                                        break;
                                                    } else if (profileMapping.getDmProfileId() != null) {
                                                        CoAProfileId = profileMapping.getDmProfileId();
                                                        break;
                                                    } else if (profileMapping.getCoaDmSelection() != "SNMP") {
                                                        triggerSNMP = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (!triggerSNMP) {
                                                if (CoAProfileId == null || CoAProfileId == 0L) {
                                                    Optional<CoaDmProfileMapping> coaProfileMapping = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getCoaProfileId() != null && coaProfileList.getCheckItem().equals(null)).findFirst();
                                                    if (coaProfileMapping.isPresent()) {
                                                        CoAProfileId = coaProfileMapping.get().getCoaProfileId();
                                                        log.warn("CoAProfileId: ",CoAProfileId);
                                                    } else {
                                                        CoAProfileId = 0L;
                                                        log.error("COA Profile not found..!");
                                                    }
                                                }
                                                if (DMProfileId == null || DMProfileId == 0L) {
                                                    Optional<CoaDmProfileMapping> dmProfileMapping = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getDmProfileId() != null && coaProfileList.getCheckItem().equals(null)).findFirst();
                                                    if (dmProfileMapping.isPresent()) {
                                                        DMProfileId = dmProfileMapping.get().getDmProfileId();
                                                        log.warn("CoAProfileId: ",CoAProfileId);
                                                    } else {
                                                        DMProfileId = 0L;
                                                        log.error("DM Profile not found..!");
                                                    }
                                                }
                                            } else {
                                                log.debug("SNMP Profile captured");
                                            }
                                        }
                                        if (!triggerSNMP) {
                                            // public CoaDmTracker(Integer custpackageid, String timeBasePolicyId, String classStr, String cause, Integer custId)
                                            if (custRetrunData.getEventName() != null)
                                                COATriggerReson = custRetrunData.getEventName();
                                            //End CoA/DM for No Profile
                                            if (parentCust != null) {
                                                log.warn("CoA/DM Status:" + firecoa + ":DMProfile:" + DMProfileId + ":COA Profile:" + CoAProfileId);
                                                if (firecoa &&
                                                        (DMProfileId != 0L || CoAProfileId != 0L) &&
                                                        (CommonConstants.EVENTCONSTANTS.PLAN_EXPIRE.equalsIgnoreCase(custRetrunData.getEventName()) || !CollectionUtils.isEmpty(parentCust.getCustomerBasePlan()))) {
                                                    if (usedQuota <= 0 && parentCust.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping() != null && parentCust.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping().size() > 0) {
                                                        log.warn("Quota exhaust so set baseplan qos quota: " + usedQuota);
                                                        parentCust.getCustomerBasePlan().get(0).setQosPolicyGatewayMapping(parentCust.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping());
                                                    }
                                                    RadiusPacket coaDMResponse = coADMImpl.intiateCoAAcct(firecoa, DMProfileId, CoAProfileId, expiry, parentCust, coaDMProfileDataList, strDMCoAIP, strUsername, request, dbAcct);
                                                    if(coaDMResponse!=null && coaDMResponse.getPacketType() == 44) {
                                                        log.debug("coa/DM Response : " + coaDMResponse.toString());
                                                        interimSkipOnCoA = true;
                                                    }
                                                } else {
                                                    log.debug("CoA/DM NOT Required Username:" + strUsername + "Overusage Flag:" + parentCust.getCustomerBasePlan().get(0).isUsagereached() + ":Booster:" + noBooster);
                                                }
                                            } else {
                                                CustomerData custData = null;
                                                log.info("Fetch Customer details As Quota Exhaust");
                                                custData = radiusUtility.getCustomerDetailsForAcctRequest(dbAuth, custData, authenticationMode, strIPAddress, cltData, strUsername, strCalling, AcctStatusValue);
                                                log.warn("CoA/DM Status:" + firecoa + ":DMProfile:" + DMProfileId + ":COA Profile:" + CoAProfileId);
                                                if (firecoa && (DMProfileId != 0L || CoAProfileId != 0L) &&
                                                        (custData.getStatus().equals(CommonConstants.PLAN_INACTIVE) || !CollectionUtils.isEmpty(custData.getCustomerBasePlan()) || CommonConstants.EVENTCONSTANTS.PLAN_EXPIRE.equalsIgnoreCase(custRetrunData.getEventName()))) {
                                                    if (usedQuota <= 0 && custData.getCustomerBasePlan() != null && custData.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping() != null && custData.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping().size() > 0) {
                                                        log.info("Quota exhaust so set baseplan qos quota: " + usedQuota);
                                                        if(custRetrunData.getCustomerBasePlan() == null || custRetrunData.getCustomerBasePlan().isEmpty()){  // adding because of Bandwidth quota is expired
                                                            custRetrunData.setCustomerBasePlan(custData.getCustomerBasePlan());
                                                        } else {
                                                            custRetrunData.getCustomerBasePlan().get(0).setQosPolicyGatewayMapping(custData.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping());

                                                        }


                                                    }
                                                    if (custRetrunData.getEventName() != null && custRetrunData.getEventName().equalsIgnoreCase(CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST)
                                                            && custData.getCustomerBasePlan() != null && custData.getCustomerBasePlan().get(0).isUpdateVolumeQuota()) {
                                                        log.info("Quota exhaust so set total unused volume quota for COA original quota: " + custRetrunData.getCustomerBasePlan().get(0).getVolumequota() + " updated quota: " + custData.getCustomerBasePlan().get(0).getTotalvolumebasedunusedquota());
                                                        custRetrunData.getCustomerBasePlan().get(0).setVolumequota(custData.getCustomerBasePlan().get(0).getTotalvolumebasedunusedquota());
                                                    }
                                                    RadiusPacket coaDMResponse = coADMImpl.intiateCoAAcct(firecoa, DMProfileId, CoAProfileId, expiry, custRetrunData, coaDMProfileDataList, strDMCoAIP, strUsername, request, dbAcct);
                                                    if(coaDMResponse!=null && coaDMResponse.getPacketType() == 44) {
                                                        log.debug("coa/DM Response : " + coaDMResponse.toString());
                                                        interimSkipOnCoA = true;
                                                    }
                                                } else {
                                                    if (custRetrunData.getCustomerBasePlan() != null)
                                                        log.warn("CoA/DM NOT Required Username:" + strUsername + "Overusage Flag:" + custRetrunData.getCustomerBasePlan().get(0).isUsagereached() + ":Booster:" + noBooster);
                                                    else
                                                        log.warn("CoA/DM NOT Required Username:" + strUsername + "Overusage Flag:" + false + ":Booster:" + noBooster);
                                                }
                                            }
                                        } else {
                                            // trigger SNMP
                                            SNMPClientProfile snmpClientProfile = cltData.getSnmpClientProfile();
                                            if (snmpClientProfile != null) {
                                                log.info("SNMP Firing on:" + snmpClientProfile.getDestinationIp() + ":Key:" + cltData.getSharedKey() + ":Port:" + snmpClientProfile.getDestinationPort());
                                                radiusUtility.sendSNMP(strAcctSessionId, snmpClientProfile);
                                            } else {
                                                log.error("SNMP Profile Not found for client: " + cltData.getClientIpAddress());
                                            }
                                        }
                                    }

                                    if (updateCPRCOADMFlag) {
                                        //updateCustPlanCOADMFLag
                                        dbAuth.updateCustPlanCOADMFLag(custRetrunData.getCustomerBasePlan().get(0).getCustpackageid(), isTriggerCOADM);
                                    }
                                }

                                break;
                            }


                        }
                        strSNMP = AcctStatusValue.toString();
                        log.info("Insert or update or delet session Id: " + strAcctSessionId + " : AcctStatusValue: " + AcctStatusValue + " time: " + (new Date()));
                        //Processing in Local CDR and Live User Table
                        authAcctUtilityImpl.insertOrUpdateAcctSessionAsync(radiusProfile.getAccountCdrStatus().equalsIgnoreCase("Enable"),
                                radiusUtility, request, accoutningResponse, radiusProfile.getDbFieldMapping()
                                , cltData.getMvnoId(), strDMCoAIP, custRetrunData, AcctStatusValue, cltData, radiusProfile.isAddLiveSessionOnInterim(), upload + download, totaltime, upload, download, isFaultyMac,isFirstSession, interimSkipOnCoA);
                        boolean issuccess = authAcctUtilityImpl.insertOrUpdateAuthAndCDRdataAsync(radiusProfile.getSessionStatus().equalsIgnoreCase("Enable"),
                                radiusUtility, request, accoutningResponse, radiusProfile.getDbFieldMapping()
                                , cltData.getMvnoId(), strDMCoAIP, totalTimeMin, custRetrunData, AcctStatusValue, cltData, currentUsage, upload, download);
                        // Trigger COA if there is start and STOP for REAUTH Issue
                        if (!AcctStatusValue.equalsIgnoreCase("STOP")) {
                            try {
                                if (request.getAttribute(6527, 13) != null && request.getAttribute(6527, 13).getAttributeValue().equalsIgnoreCase("REAUTHSLA")) {
                                    log.warn("Yes 6527:13 start CoA value:" + request.getAttribute(6527, 13).getAttributeValue());
                                    if (custRetrunData != null && custRetrunData.getCustomerBasePlan() != null) {
                                        double volumeQuota = custRetrunData.getCustomerBasePlan().get(0).getVolumequota() - custRetrunData.getCustomerBasePlan().get(0).getCurrentsessionusagevolume();
                                        volumeQuota = radiusUtility.convertUsageToBytes(volumeQuota, custRetrunData.getCustomerBasePlan().get(0).getQuotaunit());
                                        custRetrunData.getCustomerBasePlan().get(0).setVolumequota(volumeQuota);
                                    }
                                    triggerCOADMAfterAcct(custRetrunData, cltGroupData, client, request, CommonConstants.EVENTCONSTANTS.START_COA, true, cltData);
                                    log.warn("COA Response Receive For NoKia Moving Ahead for event: "+CommonConstants.EVENTCONSTANTS.START_COA);
                                } else {
                                    log.debug("Non 6527:13 skipping start CoA");
                                }
                            } catch (Exception ex) {
                                log.error("CoA in Start Failed");
                            }
                        }

                        if(issuccess && customerPlanOnStop != null &&
                                !(FRAMED_IP_ADDRESS_DOWN.equalsIgnoreCase(AcctStatusValue) ||
                                        DELEGATED_IPV6_PREFIX_DOWN.equalsIgnoreCase(AcctStatusValue) ||
                                        ALC_IPV6_ADDRESS_DOWN.equalsIgnoreCase(AcctStatusValue))
                        && !custQuotaInfo.getSkipQuotaReset()) {
                            updateQuotaInfoAndSync(custQuotaInfo, custRetrunData, reservedQuotaOnStop, strUsedQuotaOnStop, usedTime, customerPlanOnStop, dbAcct, radiusUtility);
                        } else {
                            log.debug("Customer plan not found or Live user entry is missing, acctcdr status issuccess: "+issuccess);
                        }
                        if(AcctStatusValue.toLowerCase().contains("stop") && custQuotaInfo != null && custQuotaInfo.getSkipQuotaReset() != null && custQuotaInfo.getSkipQuotaReset()) {
                            //update skipquota flag
                            log.info("Update customer skip quota flag: "+custQuotaInfo.getCustId());
                            if(customerPlanOnStop != null && customerPlanOnStop.getCustpackageid() != null) {
                                dbAcct.updateCustomerQuotaSkipFlag(customerPlanOnStop.getCustpackageid());
                            }
                        }
                    }
                    // Dynamic IP Allocation status change
                    updateIpAllocationStatusForCustomer(request, cltData, custRetrunData);
                    break;
                }
            }
            if (AcctStatusValue != null) {
                snmpCounterForAcctRequest(strSNMP);
            }
        } catch (Exception e) {
            log.error("Error while processing accounting request", e);
            e.printStackTrace();
            snmpCounters.incrementAcctFail();
        } finally {
            snmpCounters.incrementAcctRequest();
        }
        long endTime = System.currentTimeMillis();
        log.warn("Time Taken: for accountingRequestReceived: " + (endTime - startTime) + "And Accounting Response: "+accoutningResponse.toString());
        return accoutningResponse;
    }

    private void updateIpAllocationStatusForCustomer(AccountingRequest accountingRequest, Client client, CustomerData custRetrunData) {
        List<IPPoolMapping> ipPoolMappingList = client.getIpPoolMappingList();

        if (!ipPoolMappingList.isEmpty()) {
            IPPoolManagementService ipPoolManagementService = new IPPoolManagementService();
            String acct_session_attr = client.getClientGroupData().getDynamicAcctSessionAttribute();
            if (acct_session_attr == null) {
                acct_session_attr = "Acct-Session-Id";
            }
            String acctSessionId = accountingRequest.getAttributeValue(acct_session_attr);
            String nasIpAddress = accountingRequest.getAttributeValue(RadiusAttributes.NAS_IP_ADDRESS.getName());
            String attributeValue = accountingRequest.getAttributeValue(ACCT_STATUS_TYPE);
            String value = null;
            int result = 0;
            if (START.equalsIgnoreCase(attributeValue)) {
                value = ALLOCATED;
                result = ipPoolManagementService.setIpAllocatedStatusInIpPool(value, custRetrunData.getCustid(), acctSessionId, nasIpAddress);
            } else if (STOP.equalsIgnoreCase(attributeValue)) {
                value = FREE;
                result = ipPoolManagementService.setIpStatusToFreeInIpPool(value, custRetrunData.getCustid(), acctSessionId, nasIpAddress);
            } else if (INTERIM_UPDATE.equalsIgnoreCase(attributeValue)) {
                result = ipPoolManagementService.setLastModifiedTImeIpPool(custRetrunData.getCustid(), acctSessionId, nasIpAddress);
            }

            if (result == 1) {
                log.debug(String.format("IP allocation status has been successfully changed for customer : %s and Acct-session-id: %s", custRetrunData.getUsername(), acctSessionId));
            } else if (result == 0) {
                log.debug(String.format("IP not allocated for customer: %s Acct-session-id: %s", custRetrunData.getUsername(), acctSessionId));
            }

        }
    }

    private void handleDataQuota(CustomerPlanData customerPlanData, CustomerQuotaInfo custQuotaInfo, boolean noBooster, double usedQuota, double totalUsage, double unusedQuota, String strUsedQuota) {
        if (customerPlanData.getVolumebasedunusedquota() >= 0) {
            RadiusUtility radiusUtility = new RadiusUtility();
            noBooster = false;
            double dbltTotalUsage = radiusUtility.convertUsageToGivenUnit(totalUsage, customerPlanData.getQuotaunit());
            usedQuota = calculateUsedQuota(customerPlanData.getVolumebasedusedquota(), dbltTotalUsage);
            DecimalFormat df = new DecimalFormat("########.########");
            df.setMaximumFractionDigits(10);
            strUsedQuota = df.format(usedQuota);
            strUsedQuota = new String(df.format(usedQuota));
            custQuotaInfo.setVolumeBasedUnusedQuota(unusedQuota);
            custQuotaInfo.setVolumeBasedUsedQuota(usedQuota);
            double percentageUsage = ((dbltTotalUsage + customerPlanData.getVolumebasedusedquota()) * 100) / customerPlanData.getVolumebasedtotalquota();
            log.info("Quota Used in Percentage:" + percentageUsage);
        }
    }

    private void handleTimeQuota(CustomerPlanData customerPlanData, CustomerQuotaInfo custQuotaInfo, boolean noBooster, double totalTimeMin, double totalTimeQuota, double usedTime, double unusedTime, String strUsedQuota) {
        if (customerPlanData.getTimebasedunusedquota() >= 0) {
            noBooster = false;
            totalTimeMin = getTotalTimeMin(customerPlanData.getTimequotaunit(), totalTimeMin);
            totalTimeQuota = getTotalTimeQuota(customerPlanData.getTimebasedtotalquota(), customerPlanData.getTimequotaunit());
            usedTime = calculateUsedTime(customerPlanData.getTimebasedusedquota(), totalTimeMin);
            custQuotaInfo.setTimeBasedUsedQuota(usedTime);
            custQuotaInfo.setTimeBasedUnusedQuota(unusedTime);
            double percentageUsage = (usedTime * 100) / totalTimeQuota;
            log.info("Quota Used in Percentage:" + percentageUsage);
        }
    }

    private void updateQuotaInfoAndSync(CustomerQuotaInfo custQuotaInfo, CustomerData custRetrunData, double reservedQuota, String strUsedQuota, double usedTime, CustomerPlanData customerPlanData, DBAccountingDriver dbAcct, RadiusUtility radiusUtility) {
        RadiusAsyncUtility radiusAsyncUtility = new RadiusAsyncUtility();
        custQuotaInfo.setCustId(custRetrunData.getCustid());
        if (custQuotaInfo != null && custQuotaInfo.getCustpackageid() != null && customerPlanData != null) {
            custQuotaInfo.setCustpackageid(customerPlanData.getCustpackageid());
        }
        radiusAsyncUtility.updateQuotaInfoProcess(custQuotaInfo, custRetrunData, reservedQuota, strUsedQuota, usedTime, customerPlanData, dbAcct, radiusUtility);

        authAcctUtilityImpl.updateAcountingQuotaUse(custQuotaInfo, custRetrunData.getUsername(), strUsedQuota, String.valueOf(usedTime), customerPlanData.getCustpackageid(), dbAcct, custRetrunData.isFreeQuota(), radiusUtility);
        log.debug("Quota Updated");
        if (customerPlanData.getReservedQuotaInPer() != null && customerPlanData.isChunkAvailable() && customerPlanData.getTotalReservedQuota() > 0) {
            double totalReservedQuota = customerPlanData.getTotalReservedQuota() - reservedQuota;
            if (totalReservedQuota >= 0) {
                // Delete quota from tblreservedquotadtls
                dbAcct.updateReservedQuotaForChild(custRetrunData.getUsername(), totalReservedQuota);
//					log.debug("Reserved quota updated for customer: " + username + " available reserved quota: " + totalReservedQuota);
                CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
                customerServiceImpl.sendReservedQuotaUpdateToAPIGateway(customerPlanData.getCustpackageid(), true, totalReservedQuota);
                dbAcct.deleteReservedQuotaDtls(custRetrunData.getCustid());
            }
        }
    }


    // Extracted method to calculate used quota
    private double calculateUsedQuota(double volumeBasedUsedQuota, double dbltTotalUsage) {
        if (volumeBasedUsedQuota != 0) {
            return volumeBasedUsedQuota + dbltTotalUsage;
        } else {
            return dbltTotalUsage;
        }
    }

    // Extracted method to calculate total time in minutes
    private double getTotalTimeMin(String timeQuotaUnit, double totalTimeMin) {
        switch (timeQuotaUnit.toUpperCase()) {
            case "MIN":
                return totalTimeMin;
            case "HOUR":
                return totalTimeMin / 60;
            case "DAY":
                return totalTimeMin / 60 / 24;
            default:
                return totalTimeMin;
        }
    }

    // Extracted method to calculate total time quota based on quota unit
    private double getTotalTimeQuota(double timeBasedTotalQuota, String timeQuotaUnit) {
        switch (timeQuotaUnit.toUpperCase()) {
            case "MIN":
                return timeBasedTotalQuota;
            case "HOUR":
                return timeBasedTotalQuota / 60;
            case "DAY":
                return timeBasedTotalQuota / 60 / 24;
            default:
                return timeBasedTotalQuota;
        }
    }

    // Extracted method to calculate used time
    private double calculateUsedTime(double timeBasedUsedQuota, double totalTimeMin) {
        if (timeBasedUsedQuota != 0) {
            return timeBasedUsedQuota + totalTimeMin;
        } else {
            return totalTimeMin;
        }
    }

    private void snmpCounterForAcctRequest(String acctStatusType) {
        SNMPCounters snmpCounters = new SNMPCounters();
        if (acctStatusType != null) {
            switch (acctStatusType.toLowerCase()) {
                case "start":
                    snmpCounters.incrementAcctStart();
                    break;
                case "stop":
                    snmpCounters.incrementAcctStop();
                    break;
                case "interim-update":
                    snmpCounters.incrementAcctUpdate();
                    break;
                case "proxy-start":
                    snmpCounters.incrementProxyAcctStart();
                    break;
                case "proxy-stop":
                    snmpCounters.incrementProxyAcctStop();
                    break;
                case "proxy-interim-update":
                    snmpCounters.incrementProxyAcctUpdate();
                    break;
                default:
                    // Handle unexpected acctStatusType values if necessary
                    break;
            }

        }
    }

    public void updateCustPlanStatus(Long custPackId, String status) {
        log.debug("Updating endDate for custPackId: " + custPackId + " with status: " + status);
        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
        dbAuth.updateCustPlanStatus(custPackId, status, LocalDateTime.now().toString());
        RadiusUtility radiusUtility = new RadiusUtility();
        CustomerPackageRelMessage message = new CustomerPackageRelMessage();
        Map<String, Object> data = new HashMap<>();
        data.put("custPlanId", custPackId);
        data.put("status", status);
        data.put("endDate", LocalDateTime.now().toString());
        message.setData(data);
        radiusUtility.SendCustPlanInfo(message);
    }


    private void triggerCOADMAfterAcct(CustomerData customerData, ClientGroup cltGroupData, InetSocketAddress client, AccountingRequest request, String eventName, boolean addSlip, Client cltData) {
        log.debug("In Trigger Start COA for Acct, request: [" + request.getAttributes().toString() + "]");
        try {
            if (addSlip) {
                //Add Thread Sleep for Nokia COA
                try {
                    Thread.sleep(500);
                    try {
                        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                        if (customerData != null && customerData.getUsername() != null && customerData.getCustid() != 0) {
                            CustomerData customerDataNew = dbAuth.getDBCustomer(customerData.getUsername(), customerData.getMvnoId(), String.valueOf(customerData.getCustid()), "ACCT", false);//radiusUtility.getCustomerDetailsForAcctRequest(dbAuth, customerData, authenticationMode, strIPAddress, cltData, strUsername, strCalling, AcctStatusValue);
                            if (customerDataNew != null && customerDataNew.getUsername() != null && customerDataNew.getCustid() != 0) {
                                customerData = customerDataNew;
                                if (customerData.getCustomerBasePlan() != null && customerData.getCustomerBasePlan().size() > 0) {
                                    RadiusUtility radiusUtility = new RadiusUtility();
                                    CustomerPlanData planData = customerData.getCustomerBasePlan().get(0);
                                    double volumeQuota = planData.getVolumequota() - planData.getCurrentsessionusagevolume();
                                    volumeQuota = radiusUtility.convertUsageToBytes(volumeQuota, planData.getQuotaunit());
                                    customerData.getCustomerBasePlan().get(0).setVolumequota(volumeQuota);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.error("Exception to get Customer details on COA eventName:" + eventName + ", Error: " + ex.getMessage());
                    }

                } catch (Exception ex) {
                    log.error("Exception to add error: " + ex.getMessage());
                }
            }
            if (customerData != null && customerData.getCustomerBasePlan() != null && customerData.getCustomerBasePlan().size() > 0) {
                log.warn("COA Trigger for Customer plan details: " + customerData.getCustomerBasePlan().get(0).getPlanName()
                        + ", unused quota: " + customerData.getCustomerBasePlan().get(0).getVolumequota() + " , Event: " + eventName);
            } else {
                log.warn("COA Trigger for Event: " + eventName);
            }
            //COA Profile
            ValidateExpression validate = new ValidateExpression();
            CacheRetrival cacheRetrival = new CacheRetrival();
            Long CoAProfileId = 0L, DMProfileId = 0L;
            RadiusUtility radiusUtility = new RadiusUtility();
            String strDMCoAIP = client.getAddress().toString().substring(1);
            RadiusPacket coaDMResponse = null ;
            if(request.getAttribute("NAS-IP-Address") != null)
                strDMCoAIP = request.getAttributeValue("NAS-IP-Address");

            List<CoaDmProfileMapping> coaProfileMappings = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> !coaProfileList.getCheckItem().equals(null) && !coaProfileList.getCheckItem().isEmpty()).sorted(Comparator.comparing(CoaDmProfileMapping::getPriority).reversed()).collect(Collectors.toList());
            boolean triggerSNMP = false;
            for (CoaDmProfileMapping profileMapping : coaProfileMappings) {
                log.info(String.format("Expression Check For %s:", profileMapping.getCheckItem()));
                boolean response = validate.checkExpression(profileMapping.getCheckItem(), request, customerData, eventName);
                if (response) {
                    log.info("Expression matched: " + profileMapping.getCheckItem() + " Event: " + eventName);
                    if (profileMapping.getCoaProfileId() != null) {
                        CoAProfileId = profileMapping.getCoaProfileId();
                        break;
                    } else if (profileMapping.getDmProfileId() != null) {
                        CoAProfileId = profileMapping.getDmProfileId();
                        break;
                    } else if (profileMapping.getCoaDmSelection() != "SNMP") {
                        triggerSNMP = true;
                        break;
                    }
                }
            }
            CoaDMProfile coaProfileData = null;
            List<CoaDMProfile> coaDMProfileDataList = cacheRetrival.getCoADMProfileData();
            try {
                if (!triggerSNMP) {
                    if (CoAProfileId == null || CoAProfileId == 0L) {
                        Optional<CoaDmProfileMapping> coaProfileMapping = cltGroupData.getCoaDmProfileMappings().stream().filter(coaProfileList -> coaProfileList.getCoaProfileId() != null && coaProfileList.getCheckItem().equals(null)).findFirst();
                        if (coaProfileMapping.isPresent()) {
                            CoAProfileId = coaProfileMapping.get().getCoaProfileId();
                        } else {
                            CoAProfileId = 0L;
                            log.error("COA Profile not found..!");
                        }
                    }

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
                    log.debug("coaProfileData Profile Found : " + coaProfileData.getName() + ":Type:" + coaProfileData.getType());
                    AccountingRequest acctRequest = new AccountingRequest();
                    if (customerData != null && customerData.getNasIPAddress() != null)
                        acctRequest.addAttribute("NAS-IP-Address", customerData.getNasIPAddress());
                    else if (request.getAttribute("NAS-IP-Address") != null) {
                        acctRequest.addAttribute("NAS-IP-Address", request.getAttribute("NAS-IP-Address").getAttributeValue());
                    }
                    if (request.getAttributeValue("Acct-Session-Id") != null)
                        acctRequest.addAttribute("Acct-Session-Id", request.getAttributeValue("Acct-Session-Id"));
                    else if (request.getAttribute("Acct-Session-Id") != null) {
                        acctRequest.addAttribute("Acct-Session-Id", request.getAttribute("Acct-Session-Id").getAttributeValue());
                    }
                    if (customerData != null && customerData.getUsername() != null)
                        acctRequest.addAttribute("User-Name", customerData.getUsername());
                    else if (request.getAttribute("User-Name") != null)
                        acctRequest.addAttribute("User-Name", request.getAttribute("User-Name").getAttributeValue());

                    if (customerData != null && customerData.getFramedIPAddress() != null)
                        acctRequest.addAttribute("Framed-IP-Address", customerData.getFramedIPAddress());
                    else if (request.getAttribute("Framed-IP-Address") != null) {
                        acctRequest.addAttribute("Framed-IP-Address", request.getAttribute("Framed-IP-Address").getAttributeValue());
                    }
                    String mac_attr = cltGroupData.getCustomerMacAttribute();
                    if (mac_attr == null) {
                        mac_attr = "Calling-Station-Id";
                    }
                    String strCalling = null;
                    if (request.getAttribute(mac_attr) != null) {
                        strCalling = request.getAttribute(mac_attr).getAttributeValue();
                    }
                    if (request.getAttribute(RadiusAttributes.CLASS.getName()) != null) {
                        acctRequest.addAttribute(RadiusAttributes.CLASS.getName(), request.getAttributeValue(RadiusAttributes.CLASS.getName()));
                    }
                    if (strCalling != null)
                        acctRequest.addAttribute(RadiusAttributes.CALLING_STATION_ID.getName(), strCalling);

                    coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, acctRequest, acctRequest.getUserName(), customerData, strDMCoAIP);
                    log.warn("COA/DM response:"+coaDMResponse+":For Event:"+eventName+":");
                    if(coaDMResponse!=null){
                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),eventName, cltData.getMvnoId() , coaDMResponse);
                    }
                    else{
                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",eventName, cltData.getMvnoId() , coaDMResponse);
                    }
                 } else if (triggerSNMP) {
                    // trigger SNMP
                    SNMPClientProfile snmpClientProfile = cltData.getSnmpClientProfile();
                    if (snmpClientProfile != null) {
                        log.info("SNMP Firing on:" + snmpClientProfile.getDestinationIp() + ":Key:" + cltData.getSharedKey() + ":Port:" + snmpClientProfile.getDestinationPort());
                        radiusUtility.sendSNMP(request.getAttributeValue("Acct-Session-Id"), snmpClientProfile);
                    } else {
                        log.error("SNMP Profile Not found for client: " + cltData.getClientIpAddress());
                    }
                } else {
                    log.warn("CoA/DM Profile Not Found Skipping CoA/DM");
                }
            } catch (Exception e) {
                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout",eventName, cltData.getMvnoId() , coaDMResponse);
                log.error("CoA/DM Failed:" + e.getMessage());
            }

        } catch (Exception ex) {
            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
            try {
                radaysn.coaRespnseProcess(cltData.getClientIpAddress(),request.getUserName(), "Error or Timeout",eventName, cltData.getMvnoId() , null);
            } catch (RadiusException e) {
                throw new RuntimeException(e);
            }
            log.error("Error to trigger COA/DM: " + ex.getMessage());
        }
    }

    public static Map<String, String> convertStringToMap(String data) {
        Map<String, String> map = new HashMap<>();
        try {
            StringTokenizer tokenizer = new StringTokenizer(data, ",");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                String[] keyValue = token.split("=");
                log.debug("Adding Map From Class:" + keyValue[0].trim() + ":" + keyValue[1].trim());
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        } catch (Exception e) {
            log.error("Cannot Generate Map");
        }
        return map;
    }

    private void updateCustomerMacRetentionDate(String strCalling, CustomerData custRetrunData, DBAuthenticationDriver dbAuth) {
        RadiusUtility radiusUtility = new RadiusUtility();
        Timestamp macRetentionTime = radiusUtility.getMacRetentionDate(custRetrunData);
        try {
            log.info("In Update Mac Retention for Customer: " + custRetrunData.getUsername() + " Mac: " + strCalling + " macRetentionTime: " + macRetentionTime);
            dbAuth.updateLastUsageDateInCustomerMac(custRetrunData, strCalling, macRetentionTime);
        } catch (Exception ex) {
            log.error("Error In Update Mac Retention for Customer: " + custRetrunData.getUsername() + " Mac: " + strCalling + " macRetentionTime: " + macRetentionTime);
        }
    }

}
