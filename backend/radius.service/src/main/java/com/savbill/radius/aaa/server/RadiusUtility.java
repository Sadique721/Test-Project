package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.attribute.StringAttribute;
import com.savbill.radius.aaa.attribute.VendorSpecificAttribute;
import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.constant.RadiusAttributes;
import com.savbill.radius.aaa.data.CoaDmTracker;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerPlanData;
import com.savbill.radius.aaa.data.TimepolicyData;
import com.savbill.radius.aaa.data.redis.CacheServiceWithRedis;
import com.savbill.radius.aaa.db.DBAccountingDriver;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.aaa.expressions.ExpressionEvaluator;
import com.savbill.radius.aaa.packet.*;
import com.savbill.radius.aaa.packet.*;
import com.savbill.radius.aaa.util.RadiusClient;
import com.savbill.radius.aaa.util.RadiusException;
import com.savbill.radius.aaa.util.ValidateExpression;
import com.savbill.radius.config.CacheRetrival;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.kafka.KafkaMessageData;
import com.savbill.radius.kafka.KafkaMessageSender;
import com.savbill.radius.kafka.MessageConstants;
import com.savbill.radius.kafka.message.*;
import com.savbill.radius.kafka.message.*;
import com.savbill.radius.repository.VlanValidationMappingRepository;
import com.savbill.radius.services.impl.CustomerServiceImpl;
import com.savbill.radius.spring.SpringContext;
import com.savbill.radius.utils.CommonConstants;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RadiusUtility {
//	@Autowired
//	private MessageSender messageSender;

//	@Autowired
//	private KafkaMessageSender kafkaMessageSender;

    private static final String ACCT_STATUS_TYPE = "Acct-Status-Type";
    public static final String REPLY_MESSAGE = "Reply-Message";
    private DatagramSocket proxySocket = null;
    private static final Logger log = LoggerFactory.getLogger(RadiusUtility.class);
    private static final Logger logImp = LoggerFactory.getLogger("savbillradiuscriticle");
    private static int proxyIndex = 1;
    private static int coaIndex = 1;

    @Autowired
    VlanValidationMappingRepository validationMappingRepository;


    public CustomerData authenticateCustomer(String strUsername, String strPassword, int mvnoid, AccessRequest request, boolean isPasswordCheck, String acctStatusValue) throws SQLException {
        DBAuthenticationDriver dbAuthDrive = new DBAuthenticationDriver();
        CustomerData custData = new CustomerData();
        custData.setUsername(strUsername);
        custData.setPassword(strPassword);
        return dbAuthDrive.authenticateCustomer(custData, mvnoid, request, isPasswordCheck, acctStatusValue);
    }

    public CustomerData authenticateCustomerAll(String strIdentity, int mvnoid, String strType, String mac, String ip, String strPassword, boolean isPasswordCheck, String acctStatusValue) throws SQLException {
        DBAuthenticationDriver dbAuthDrive = new DBAuthenticationDriver();
        CustomerData custData = new CustomerData();
        custData.setUsername(strIdentity);
        custData.setPassword(strPassword);
        return dbAuthDrive.authenticateCustomerAll(custData, strIdentity, mvnoid, strType, mac, ip, isPasswordCheck, acctStatusValue);
    }


    public String removeFirstandLast(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.deleteCharAt(str.length() - 1);
        sb.deleteCharAt(0);
        return sb.toString();
    }

    public String getDynamicReplyValue(String attributeStr, CustomerData custReturnData, RadiusPacket accessRequest) {
        List<String> strList = Arrays.asList(attributeStr.split("\""));
        // StringBuilder resultStr = new StringBuilder();
        String resultStr = null;
        boolean bAttrbValFound = true;
        try {
            log.debug("getDynamicReplyValue attributeStr:" + attributeStr);
            if (custReturnData != null)
                resultStr = getDynamicCustomerValue(custReturnData, attributeStr);
            log.debug("resultStr:" + resultStr);
        } catch (Exception e) {
            e.printStackTrace();
            resultStr = null;
        }

        if (resultStr == null)
            return null;
        else
            return resultStr;
    }

    public String getDynamicAttributeName(String str) {
        int openIdx = str.indexOf("{");
        int closeIdx = str.indexOf("}");
        String attrStr = (String) str.subSequence(openIdx + 1, closeIdx);
        return attrStr;
    }


    public String getDynamicCustomerValue(CustomerData custRetrunData, String attriValue) {
        log.info("getDynamicValue looking for: " + attriValue);

        if (custRetrunData == null || attriValue == null) {
            return null;
        }

        switch (attriValue.toLowerCase()) {
            case "user-password":
                return custRetrunData.getPassword();
            case "nas-ip-address":
                return custRetrunData.getNasIpValidate();
            case "asnnumber":
                return custRetrunData.getAsnNumber();
            case "framed-ip-address":
                return custRetrunData.getFramedIpValidate();
            case "framed-ip6-address":
                return custRetrunData.getFramedIp6Validate();
            case "delegatedprefix":
                return custRetrunData.getDelegatedprefix();
            case "framedroute":
                return custRetrunData.getFramedroute();
            case "status":
                try {
                    if (custRetrunData == null) {
                        return "unknown";
                    } else if (custRetrunData.getStatus() == null) {
                        return "unknown";
                    } else if (custRetrunData.getStatus().equalsIgnoreCase("inactive")) {
                        return "expire";
                    }
                    return custRetrunData.getStatus();
                } catch (Exception e) {
                    return "expire";
                }
            case "ipbind":
                return custRetrunData.getFrameipbind();
            case "ippoolbind":
                return custRetrunData.getIppoolbind();
            case "bngrouterinterface":
                return custRetrunData.getBngRouterInterface();
            case "bngroutername":
                return custRetrunData.getBngRouterName();
            case "ipprefixes":
                return custRetrunData.getIpPrefixes();
            case "ipv6prefixes":
                return custRetrunData.getIpv6Prefixes();
            case "lanip":
                return custRetrunData.getLanIP();
            case "lanipv6":
                return custRetrunData.getLanIPV6();
            case "llaccountid":
                return custRetrunData.getLlAccountID();
            case "llconnectiontype":
                return custRetrunData.getLlConnectionType();
            case "llexpirydate":
                return custRetrunData.getLlExpiryDate();
            case "llmedium":
                return custRetrunData.getLlMedium();
            case "llserviceid":
                return custRetrunData.getLlServiceID();
            case "macaddress":
                return custRetrunData.getMacAddress();
            case "peerip":
                return custRetrunData.getPeerIP();
            case "poolip":
                return custRetrunData.getPoolIP();
            case "qospolicyname":
                return custRetrunData.getQosPolicyName();
            case "rdexport":
                return custRetrunData.getRdExport();
            case "rdvalue":
                return custRetrunData.getRdValue();
            case "vlanid":
                return custRetrunData.getvLanId();
            case "vrfname":
                return custRetrunData.getVrfName();
            case "vsiid":
                return custRetrunData.getVsiId();
            case "vsiname":
                return custRetrunData.getVsiName();
            case "wanip":
                return custRetrunData.getWanIP();
            case "wanipv6":
                return custRetrunData.getWanIPV6();
            case "username":
                return custRetrunData.getUsername();
            case "usage_quota_type":
                return custRetrunData.getUsageQuotaType();
            case "framed_ip_netmask":
                return custRetrunData.getFramedIPNetmask();
            case "framed_ipv6_prefix":
                return custRetrunData.getFramedIPv6Prefix();
            case "primary_dns":
                return custRetrunData.getPrimaryDNS();
            case "primary_ipv6_dns":
                return custRetrunData.getPrimaryIPv6DNS();
            case "secondary_ipv6_dns":
                return custRetrunData.getSecondaryIPv6DNS();
            case "secondary_dns":
                return custRetrunData.getSecondaryDNS();
            case "gatewayip":
                return custRetrunData.getGatewayip();
            case "mac_provision":
                return String.valueOf(custRetrunData.getMacAuthEnable());
            case "mac_auth_enable":
                return String.valueOf(custRetrunData.getMacAuthEnable());
            case "planname":
                if (custRetrunData.getCustomerBasePlan() != null && !custRetrunData.getCustomerBasePlan().isEmpty()) {
                    return custRetrunData.getCustomerBasePlan().get(0).getPlanName();
                } else {
                    return null;
                }
            case "timebasedunusedquota":
                if (custRetrunData.getCustomerBasePlan() != null && !custRetrunData.getCustomerBasePlan().isEmpty()) {
                    long val = custRetrunData.getCustomerBasePlan().get(0).getTimequota();
                    return val != 0 ? String.valueOf(val) : "0";
                } else {
                    return "0";
                }
            case "volumebasedunusedquota":
                if (custRetrunData.getCustomerBasePlan() != null && !custRetrunData.getCustomerBasePlan().isEmpty()) {
                    double val = custRetrunData.getCustomerBasePlan().get(0).getVolumequota();
                    log.info("volumebasedunusedquota: " + val + " plan: " + custRetrunData.getCustomerBasePlan().get(0).getPlanName());
                    return val != 0 ? String.valueOf(val) : "0";
                } else {
                    return "0";
                }
            case "baseparam1":
                return getCustomerBasePlanValue(custRetrunData, CustomerPlanData::getBaseparam1);
            case "baseparam2":
                return getCustomerBasePlanValue(custRetrunData, CustomerPlanData::getBaseparam2);
            case "baseparam3":
                return getCustomerBasePlanValue(custRetrunData, CustomerPlanData::getBaseparam3);
            case "thparam1":
                return getCustomerBasePlanValue(custRetrunData, CustomerPlanData::getThparam1);
            case "thparam2":
                return getCustomerBasePlanValue(custRetrunData, CustomerPlanData::getThparam2);
            case "thparam3":
                return getCustomerBasePlanValue(custRetrunData, CustomerPlanData::getThparam3);
            case "servicetype":
                return getCustomerBasePlanValue(custRetrunData, CustomerPlanData::getRadServiceType);
            default:
                return getDefaultValues(custRetrunData, attriValue);
        }
    }

    public String getDefaultValues(CustomerData custRetrunData, String attriValue) {
        if (attriValue.contains("vlan") && custRetrunData.getVlanManagement() != null) {
            return getDynamicVlanValue(custRetrunData.getVlanManagement(), attriValue);
        } else {
            return getQosPolicyValue(custRetrunData, attriValue);
        }
    }

    public String getDynamicVlanValue(VLANManagement vlanManagement, String attriValue) {
        log.info("Get Dynamic Vlan Value looking for: " + attriValue);
        if (attriValue.contains(".")) {
            String[] expPair = attriValue.split("\\.");
            String expKey = expPair[0];
            attriValue = expPair[1];
        }
        switch (attriValue) {
            case "VLAN_NAME":
                return vlanManagement.getVlanName();
            case "NAS_TYPE":
                return vlanManagement.getNasType();
            case "CIRCUIT_TYPE":
                return vlanManagement.getCircuitType();
            case "NAS_IDENTIFIER":
                return vlanManagement.getNasIdentifier();
            case "NAS_PORT_ID_1":
                return vlanManagement.getNasPortId1();
            case "NAS_PORT_ID_2":
                return vlanManagement.getNasPortId2();
            case "NAS_PORT_ID_3":
                return vlanManagement.getNasPortId3();
            case "NAS_PORT_ID_4":
                return vlanManagement.getNasPortId4();
            case "NAS_PORT_ID_5":
                return vlanManagement.getNasPortId5();
            case "CALLING_STATION_ID":
                return vlanManagement.getCallingStationId();
            case "CONTEXT_NAME":
                return vlanManagement.getContextName();
            case "FILTER_ID":
                return vlanManagement.getFilterId();
            case "FORWARD_POLICY":
                return vlanManagement.getForwardPolicy();
            case "HTTP_REDIRECT_PROFILE_NAME":
                return vlanManagement.getHttpRedirectProfileName();
            case "RATE_LIMIT_RATE":
                return vlanManagement.getRateLimitRate();
            case "RATE_LIMIT_BURST":
                return vlanManagement.getRateLimitBurst();
            case "QOS_POLICING_POLICY_NAME":
                return vlanManagement.getQosPolicingPolicyName();
            case "QOS_METERING_POLICY_NAME":
                return vlanManagement.getQosMeteringPolicyName();
            case "PPPOE_URL":
                return vlanManagement.getPppoeUrl();
            case "PPP_DNS_PRIMARY":
                return vlanManagement.getPppDnsPrimary();
            case "PPP_DNS_SECONDARY":
                return vlanManagement.getPppDnsSecondary();
            case "PPP_NBNS_PRIMARY":
                return vlanManagement.getPppNbnsPrimary();
            case "SESSION_TIMEOUT":
                return vlanManagement.getSessionTimeOut();
            case "IDLE_TIMEOUT":
                return vlanManagement.getIdleTimeOut();
            case "FRAMED_IP_ADDRESS":
                return vlanManagement.getFramedIpAddress();
            case "RB_DHCP_MAX_LEASES":
                return vlanManagement.getRbDhcpMaxLeases();
            case "IP_ADDRESS_POOL_NAME":
                return vlanManagement.getIpAddressPoolName();
            case "NAT_PROFILE_NAME":
                return vlanManagement.getNatProfileName();
            case "RB_INTERFACE_NAME":
                return vlanManagement.getRbInterfaceName();
            case "HTTP_REDIRECT_URL":
                return vlanManagement.getHttpRedirectUrl();
            case "FRAMED_IPV6_PREFIX":
                return vlanManagement.getFramedIpv6Prefix();
            case "DELEGATED_IPV6_PREFIX":
                return vlanManagement.getDelegatedIpv6Prefix();
            case "FRAMED_INTERFACE_ID":
                return vlanManagement.getFramedInterfaceId();
            case "FRAMED_IPV6_POOL":
                return vlanManagement.getFramedIpv6Pool();
            case "IPV6_OPTION":
                return vlanManagement.getIpv6Option();
            case "IPV6_DNS":
                return vlanManagement.getIpv6Dns();
            case "DELEGATED_MAX_PREFIX":
                return vlanManagement.getDelegatedMaxPrefix();
            case "DELEGATED_IPV6_POOL":
                return vlanManagement.getDelegatedIpv6Pool();
            case "SUB_PROFILE":
                return vlanManagement.getSubProfile();
            default:
                return "";
        }
    }

    private String getCustomerBasePlanValue(CustomerData custRetrunData, Function<CustomerPlanData, String> getter) {
        if (custRetrunData.getCustomerBasePlan() != null && !custRetrunData.getCustomerBasePlan().isEmpty()) {
            return getter.apply(custRetrunData.getCustomerBasePlan().get(0));
        } else {
            return null;
        }
    }

    private String getQosPolicyValue(CustomerData custRetrunData, String attriValue) {
        if (custRetrunData.getCustomerBasePlan() == null || custRetrunData.getCustomerBasePlan().isEmpty()) {
            log.error("Base Plan is Empty for attriValue: " + attriValue);
            return null;
        }
        CustomerPlanData basePlan = custRetrunData.getCustomerBasePlan().get(0);
        if (basePlan.getQosPolicyGatewayMapping() == null || basePlan.getQosPolicyGatewayMapping().isEmpty()) {
            log.error("QosPolicy not Available for : " + attriValue);
            return null;
        }

        switch (attriValue.toLowerCase()) {
            case "downloadspeed1":
                return getQosValue(basePlan, 0, "downloadspeed");
            case "uploadspeed1":
                return getQosValue(basePlan, 0, "uploadspeed");
            case "basedownloadspeed1":
                return getQosValue(basePlan, 0, "basedownloadspeed");
            case "baseuploadspeed1":
                return getQosValue(basePlan, 0, "baseuploadspeed");
            case "thdownloadspeed1":
                return getQosValue(basePlan, 0, "thdownloadspeed");
            case "thuploadspeed1":
                return getQosValue(basePlan, 0, "thuploadspeed");

            case "downloadspeed2":
                return getQosValue(basePlan, 1, "downloadspeed");
            case "uploadspeed2":
                return getQosValue(basePlan, 1, "uploadspeed");
            case "basedownloadspeed2":
                return getQosValue(basePlan, 1, "basedownloadspeed");
            case "baseuploadspeed2":
                return getQosValue(basePlan, 1, "baseuploadspeed");
            case "thdownloadspeed2":
                return getQosValue(basePlan, 1, "thdownloadspeed");
            case "thuploadspeed2":
                return getQosValue(basePlan, 1, "thuploadspeed");

            case "downloadspeed3":
                return getQosValue(basePlan, 2, "downloadspeed");
            case "uploadspeed3":
                return getQosValue(basePlan, 2, "uploadspeed");
            case "basedownloadspeed3":
                return getQosValue(basePlan, 2, "basedownloadspeed");
            case "baseuploadspeed3":
                return getQosValue(basePlan, 2, "baseuploadspeed");
            case "thdownloadspeed3":
                return getQosValue(basePlan, 2, "thdownloadspeed");
            case "thuploadspeed3":
                return getQosValue(basePlan, 2, "thuploadspeed");

            case "downloadspeed4":
                return getQosValue(basePlan, 3, "downloadspeed");
            case "uploadspeed4":
                return getQosValue(basePlan, 3, "uploadspeed");
            case "basedownloadspeed4":
                return getQosValue(basePlan, 3, "basedownloadspeed");
            case "baseuploadspeed4":
                return getQosValue(basePlan, 3, "baseuploadspeed");
            case "thdownloadspeed4":
                return getQosValue(basePlan, 3, "thdownloadspeed");
            case "thuploadspeed4":
                return getQosValue(basePlan, 3, "thuploadspeed");

            default:
                return null;
        }
    }

    private String getQosValue(CustomerPlanData basePlan, int index, String type) {
        if (index >= basePlan.getQosPolicyGatewayMapping().size()) {
            log.error("QosPolicy Gateway Mapping Not Found: " + type);
            return null;
        }

        QOSPolicyGatewayMapping qosPolicy = basePlan.getQosPolicyGatewayMapping().get(index);

        switch (type) {
            case "downloadspeed":
                return qosPolicy.getDownloadSpeed();
            case "uploadspeed":
                return qosPolicy.getUploadSpeed();
            case "basedownloadspeed":
                return qosPolicy.getBaseDownloadSpeed();
            case "baseuploadspeed":
                return qosPolicy.getBaseUploadSpeed();
            case "thdownloadspeed":
                return qosPolicy.getThrottleDownloadSpeed();
            case "thuploadspeed":
                return qosPolicy.getThrottleUploadSpeed();
            default:
                return null;
        }
    }

    public String getDynamicAuthRequestValue(RadiusPacket accessRequest, String attriValue) {
        log.info("getDynamicValue looking for:" + attriValue);
        return null;
    }

    public Client identifyClient(String strClient, RadiusPacket request) throws CloneNotSupportedException {
        log.debug("In identifyClient strClient: " + strClient + " request: " + request.toString());
        String strNASIP = null;
        if (request.getAttribute(RadiusAttributes.NAS_IP_ADDRESS.getName()) != null) {
            strNASIP = request.getAttributeValue(RadiusAttributes.NAS_IP_ADDRESS.getName());
            log.info("NAS IP Address: " + strNASIP);
        }
        Client client = identifyClientOnly(strClient, strNASIP);
        if (client != null) {
            if (request != null && (client.getClientGroupId() == null || client.getClientGroupId() == 0L)) {
                // for multiple client group scenario we need to clone client group
                client = (Client) client.clone();
                ValidateExpression validate = new ValidateExpression();
                List<ClientGroupMapping> clientGroupMappings = client.getClientGroupMappings();
                for (ClientGroupMapping clientGroupMapping : clientGroupMappings) {
                    String checkItem = clientGroupMapping.getCheckItem();
                    boolean evaluate = validate.checkExpression(checkItem, request, null);
                    if (evaluate) {
                        client.setClientGroupId(clientGroupMapping.getClientGroupData().getClientGroupId());
                        client.setClientGroupData(clientGroupMapping.getClientGroupData());
                        log.debug("Selected client group for user: " + request.getAttribute("User-Name") != null ? request.getAttributeValue("User-Name") : " " + "is " + clientGroupMapping.getClientGroupData().getName());
                        break;
                    }
                }

            }
        }
        return client;
    }

    public Client identifyClientOnly(String strClient, String strNASIP) {
        CacheRetrival cacheRetrival = new CacheRetrival();
        Client cltData = new Client();
        Map<String, Object> clientData = cacheRetrival.cacheClientConfig();
        // This is for IP Address
        if (strNASIP != null && strNASIP.equals("")) {
            log.debug("NAS IP Found:" + strNASIP);
            strClient = strNASIP;
        }

        if (clientData.get(strClient) != null) {
            cltData = (Client) clientData.get(strClient);
            if (log.isDebugEnabled()) {
                log.debug("For Client:" + strClient + ":Shared Key is:" + cltData.getSharedKey() + ":" + ":MVNO:" + cltData.getMvnoId());
            }
            return cltData;
        }// This is for Subnet
        else {
            //for (Object key: cache.getKeys()) {
            for (Map.Entry<String, Object> entry : clientData.entrySet()) {
                Client cltDataDat = (Client) clientData.get(entry.getKey());
                if (cltDataDat.getIpType().equalsIgnoreCase("subnet")) {
                    cltDataDat.getClientIpAddress();
                    if (log.isDebugEnabled()) {
                        log.debug("Address is :" + strClient + ":Client Ip:" + cltDataDat.getClientIpAddress() + ":" + ":MVNO:" + cltData.getMvnoId());
                    }
                    IPAddress subnetAddress = new IPAddressString(cltDataDat.getClientIpAddress()).getAddress();
                    IPAddress subnet = subnetAddress.toIPv4();
                    IPAddress testAddress = new IPAddressString(strClient).getAddress();
                    boolean result = subnet.contains(testAddress);
                    if (result) {
                        log.debug("MATCHED Address is :" + strClient + ":Client Ip:" + cltDataDat.getClientIpAddress() + ":" + ":MVNO:" + cltData.getMvnoId());
                        return cltDataDat;
                    }
                }
            }
        }
        // This is for Allow All.If 0.0.0.0 added
        if (clientData.get(AAAConstant.ALLOWALL) != null) {
            cltData = (Client) clientData.get(AAAConstant.ALLOWALL);
            if (log.isDebugEnabled()) {
                log.debug("ALLOW ALL For Client:" + AAAConstant.ALLOWALL + ":Shared Key is " + cltData.getSharedKey());
            }
            return cltData;
        }
        log.error("For Client " + strClient + " Returning Null");
        return null;
    }


    public void processAcctPacketSession(AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap acctFieldMapping, int mvnoid, String sourceAddress, CustomerData custRetrunData, String AcctStatusValue, Client cltData, Boolean addLiveSessionOnInterim, double currentUsage, long currentTimeUsage, double upload, double download, boolean isFaultyMac) {
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("processAcctPacketSession"));
            }
            // to update value in process CDR
            if (custRetrunData != null && custRetrunData.getUsername() != null && custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().get(0).getVolumebasedunusedquota() <= 0) {
                custRetrunData.setThrottleSpeed(true);
            } else if (custRetrunData.getCustomerBasePlan() == null)
                custRetrunData.setThrottleSpeed(true);
            else {
                custRetrunData.setThrottleSpeed(false);
            }
            String strAcctMultiSessionId = null;
            if (request.getAttribute("Acct-Multi-Session-Id") != null) {
                strAcctMultiSessionId = request.getAttribute("Acct-Multi-Session-Id").getAttributeValue();
            } else {
                strAcctMultiSessionId = "0";
            }
            //check Live session is there or not
            if (addLiveSessionOnInterim && !AcctStatusValue.equalsIgnoreCase("Start") && !AcctStatusValue.equalsIgnoreCase("Stop")) {
                DBAccountingDriver dbAccountingDrive = new DBAccountingDriver();
                int currentSession = dbAccountingDrive.isLiveSessionExists("", "", request.getAttribute("Acct-Session-Id").getAttributeValue(), strAcctMultiSessionId);
                if (currentSession <= 0) { // if there is already Live session then ignore
                    addLiveSessionOnInterim = true;
                } else {
                    addLiveSessionOnInterim = false;
                }
            }
            if (addLiveSessionOnInterim && !AcctStatusValue.equalsIgnoreCase("Stop") && !AcctStatusValue.equalsIgnoreCase("Start"))
                AcctStatusValue = "Start";
            DBAccountingDriver dbAccountingDrive = new DBAccountingDriver();
            if (AcctStatusValue.equalsIgnoreCase("Start")) {
                dbAccountingDrive.insertDBSesion(request, acctFieldMapping, mvnoid, sourceAddress, custRetrunData, cltData.getClientGroupData().getClientGroupId(), upload, download, isFaultyMac);
//                dbAccountingDrive.upsertDBSesion(request, acctFieldMapping, mvnoid, sourceAddress, custRetrunData, cltData.getClientGroupId(), 0, 0, AcctStatusValue);
            } else if (AcctStatusValue.equalsIgnoreCase("Stop")) {

                dbAccountingDrive.deleteDBSession(request.getAttributeValue("Acct-Session-Id"), request.getAttributeValue("NAS-IP-Address"), strAcctMultiSessionId);
            } else {
                if (addLiveSessionOnInterim)
                    dbAccountingDrive.upsertDBSesion(request, acctFieldMapping, mvnoid, sourceAddress, custRetrunData, cltData.getClientGroupId(), currentUsage, currentTimeUsage, AcctStatusValue, upload, download, isFaultyMac);
                else
                    dbAccountingDrive.updateDBSesion(request, acctFieldMapping, mvnoid, custRetrunData, currentUsage, currentTimeUsage, AcctStatusValue, upload, download);
            }
            accoutningResponse.setPacketType(AAAConstant.ACCOUNTING_RESPONSE);
        } catch (Exception e) {
            log.error("Error while processing accouting request packet", e);
        }
    }

    public boolean processAcctPacketCDR(AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap acctFieldMapping, int mvnoid, String sourceAddress, Double totalTimeMin, CustomerData custRetrunData, String AcctStatusValue, double totalUsage, double upload, double download) {
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("processAcctPacketCDR"));
            }
            if (AcctStatusValue.equalsIgnoreCase("Stop")) {
                boolean isSuccess = true;
                DBAccountingDriver dbAccountingDrive = new DBAccountingDriver();
                isSuccess = dbAccountingDrive.insertCDR(request, acctFieldMapping, mvnoid, totalTimeMin, sourceAddress, custRetrunData, upload, download);
                accoutningResponse.setPacketType(AAAConstant.ACCOUNTING_RESPONSE);
                return isSuccess;
            } else {
                log.debug(String.format("Skipping CDR Dump For Accounting Packet: %s", AcctStatusValue));
                return false;
            }
        } catch (Exception e) {
            log.error("Error while processing accouting request packet" + e.getMessage());
            return false;
        }
    }

    public void processRadiusPacketCDR(AccountingRequest request, RadiusPacket accoutningResponse, ConcurrentMap acctFieldMapping, int mvnoid, String sourceAddress, Double totalTimeMin, CustomerData custRetrunData, String AcctStatusValue, double totalUsage, double upload, double download, Timestamp curentDate) {
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("processRadiusPacketCDR"));
            }

            DBAccountingDriver dbAccountingDrive = new DBAccountingDriver();
            dbAccountingDrive.insertRADIUSPACKET(request, acctFieldMapping, mvnoid, totalTimeMin, sourceAddress, custRetrunData, upload, download, curentDate);
            accoutningResponse.setPacketType(AAAConstant.ACCOUNTING_RESPONSE);

        } catch (Exception e) {
            log.error("Error while processing accouting request packet" + e.getMessage());
        }
    }



    public void processReplyItem(RadiusPacket accessResponse, CustomerData custRetrunData, RadiusPacket accessRequest, Long cltGroupid, Client clientData, boolean accessReject) {
        log.debug("*********** processReplyItem   *******************");
        RadiusUtility radUtil = new RadiusUtility();

        // Reply Item Client Group
        List<ClientReply> clientReply = null;
        if (clientData != null & clientData.getClientGroupData() != null) {
            if (clientData.getClientGroupData().getClientReplyList() != null) {
                if (accessReject && !CollectionUtils.isEmpty(clientData.getClientGroupData().getClientReplyListForRejectAuthResponse()))
                    clientReply = clientData.getClientGroupData().getClientReplyListForRejectAuthResponse();
                else
                    clientReply = clientData.getClientGroupData().getClientReplyList();
            }
        }
        if (clientReply != null) {
            log.debug("*********** Client Group Reply  *******************" + clientReply.size());
            String dynaReplyValue = null;
            for (int i = 0; i < clientReply.size(); i++) {
                ClientReply clientReplyData = clientReply.get(i);
                if (clientReplyData.getType().equalsIgnoreCase("AUTH")) {
                    String strCheckItem = clientReplyData.getCheckitem();
                    log.debug("Check Item is:" + strCheckItem + ": for: " + clientReplyData.getAttribute());
                    ValidateExpression validate = new ValidateExpression();
                    boolean isCheckedTrue = validate.checkExpression(strCheckItem, accessRequest, custRetrunData);
                    log.debug("Check Item Resposne" + isCheckedTrue + ": for: " + clientReplyData.getAttribute());
                    if (strCheckItem == null || strCheckItem == "" || isCheckedTrue) {
                        if (clientReplyData.getClientGroupId().equals(cltGroupid)) {
                            if (clientReplyData.getAttributeValue().startsWith("REQ{") && clientReplyData.getAttributeValue().endsWith("}")) {
                                String dynaAttribute = clientReplyData.getAttributeValue().substring(4);
                                StringBuilder sb = new StringBuilder(dynaAttribute);
                                sb.deleteCharAt(dynaAttribute.length() - 1);
                                dynaAttribute = sb.toString();
                                log.debug("Dynaimc Value Searaching Attribute :" + dynaAttribute);
                                dynaAttribute = accessRequest.getAttribute(dynaAttribute).getAttributeValue();
                                log.debug("Final Dynaimc Value Searaching Value:" + dynaAttribute);
                                accessResponse.addAttribute(clientReplyData.getAttribute(), dynaAttribute);
                            } else if (clientReplyData.getAttributeValue().startsWith("{") && clientReplyData.getAttributeValue().endsWith("}")) {
                                try {
                                    dynaReplyValue = ExpressionEvaluator.getValueFromGivenExpression(clientReplyData.getAttributeValue(), custRetrunData, accessRequest);//getExpressionValue(radUtil, accessRequest,custRetrunData, clientReplyData);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                if (dynaReplyValue != null && !dynaReplyValue.trim().isEmpty()) {
                                    if (!dynaReplyValue.equalsIgnoreCase("NA")) {
                                        //Hardcoded for Subisu so disabled it
                                        /*
                                        if(dynaReplyValue.contains("INTERNET") || dynaReplyValue.contains("GGC") || dynaReplyValue.contains("AKAMAI") || dynaReplyValue.contains("NPIX") || dynaReplyValue.contains("NTA")) {
                                            if(dynaReplyValue.contains("INTERNET")) {
                                                accessResponse.setTag((byte) 01);
                                            }
                                            if(dynaReplyValue.contains("GGC")) {
                                                accessResponse.setTag((byte) 02);
                                            }
                                            if(dynaReplyValue.contains("AKAMAI")) {
                                                accessResponse.setTag((byte) 03);
                                            }
                                            if(dynaReplyValue.contains("NPIX")) {
                                                accessResponse.setTag((byte) 04);
                                            }
                                            if(dynaReplyValue.contains("NTA")) {
                                                accessResponse.setTag((byte) 02);
                                            }
                                        }
                                        */

                                        if (dynaReplyValue.contains(".") && isDouble(dynaReplyValue))
                                            dynaReplyValue = dynaReplyValue.split("\\.")[0];
                                        accessResponse.addAttribute(clientReplyData.getAttribute(), dynaReplyValue);
                                    }
                                } else {
                                    log.debug("dynaReplyValue is null");
                                }
                            } else {
                                accessResponse.addAttribute(clientReplyData.getAttribute(), clientReplyData.getAttributeValue());
                            }

                            log.debug("Adding Data for :" + clientReplyData.getAttribute() + ":Data is " + clientReplyData.getAttributeValue() + ":Value:" + dynaReplyValue);
                        }
                    }
                }
            }
        }

    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public RadiusPacket initiateCoADM(CoaDMProfile coaProfileData, AccountingRequest request, String username, CustomerData custRetrunData, String gatewayIp) {
        return initiateCoADM(coaProfileData, request, username, custRetrunData, null, gatewayIp);
    }

    public RadiusPacket initiateCoADM(CoaDMProfile coaProfileData, AccountingRequest request, String username, CustomerData custRetrunData, CoaDmTracker coaDmTracker, String gatewayIp) {

        log.warn("In initiateCoADM username: " + username + " coaProfileData: " + coaProfileData.getName());
        try {

            RadiusPacket response = null;

            if (coaProfileData != null && coaProfileData.getType().equalsIgnoreCase("coa")) {

                CoaRequest coaRequest = new CoaRequest();
                List<CoaDMProfileAttribute> coaDMProfileAttributeList = coaProfileData.getCoaDMProfileAttributeList();
                log.debug("In CoA with Vaues:" + coaDMProfileAttributeList);

                if (coaDMProfileAttributeList != null && coaDMProfileAttributeList.size() > 0) {
                    for (int i = 0; i < coaDMProfileAttributeList.size(); i++) {
                        CoaDMProfileAttribute coaDMProfileAttribute = coaDMProfileAttributeList.get(i);
                        ValidateExpression validate = new ValidateExpression();
                        boolean isCheckedTrue = true;
                        if (coaDMProfileAttribute.getCheckitem() != null && coaDMProfileAttribute.getCheckitem().trim().length() > 0) {
                            isCheckedTrue = validate.checkExpression(coaDMProfileAttribute.getCheckitem(), request, custRetrunData);
                        } else {
                            log.info("Check Item is empty for coaDMProfileAttribute radius attribute: " + coaDMProfileAttribute.getRadiusAtt() + " profile attribute: " + coaDMProfileAttribute.getProfileAtt());
                        }
                        if (isCheckedTrue) {
                            String replyItem = coaDMProfileAttribute.getProfileAtt();
                            replyItem = ExpressionEvaluator.getValueFromGivenExpression(replyItem, custRetrunData, request);
                            coaRequest.addAttribute(coaDMProfileAttributeList.get(i).getRadiusAtt(), replyItem);
                        }
                    }
                }
                if (custRetrunData != null && custRetrunData.getStrClass() != null) {
                    coaRequest.addAttribute("Class", custRetrunData.getStrClass());
                }
                log.warn("CoA Packet: " + coaRequest + ":ON IP:" + gatewayIp + ":Key:" + coaProfileData.getSharedkey() + ":ON PORT:" + coaProfileData.getPort());
                RadiusClient rc = new RadiusClient(gatewayIp, coaProfileData.getSharedkey());
                rc.setSocketTimeout(coaProfileData.getTimevar().intValue());
                rc.setRetryCount(1);

                response = rc.communicate(coaRequest, coaProfileData.getPort());
                if (custRetrunData != null && custRetrunData.getStrClass() != null) {
                    response.addAttribute("Class", custRetrunData.getStrClass());
                }
                log.warn("COA Response is " + response.getPacketType());

            } else {
                DmRequest dmRequest = new DmRequest();
                List<CoaDMProfileAttribute> coaDMProfileAttributeList = coaProfileData.getCoaDMProfileAttributeList();
                log.debug("In DM with Vaues:" + coaDMProfileAttributeList);

                if (coaDMProfileAttributeList != null && coaDMProfileAttributeList.size() > 0) {
                    for (int i = 0; i < coaDMProfileAttributeList.size(); i++) {
                        String replyItem = coaDMProfileAttributeList.get(i).getProfileAtt();
                        replyItem = ExpressionEvaluator.getValueFromGivenExpression(replyItem, custRetrunData, request);
                        dmRequest.addAttribute(coaDMProfileAttributeList.get(i).getRadiusAtt(), replyItem);
                    }
                }
                log.warn("DM Packet " + dmRequest + ":ON IP:" + coaProfileData.getGateway() + ":Key:" + coaProfileData.getSharedkey() + ":ON PORT:" + coaProfileData.getPort());
                //Remove Mac From Cache if available
                RadiusAttribute radiusAttribute = request.getAttribute(RadiusAttributes.CALLING_STATION_ID.getName());
                //Remove Cache
                if (radiusAttribute != null) {
                    CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                    String strCalling = radiusAttribute.getAttributeValue();
                    RadiusUtility radiusUtility = new RadiusUtility();
                    if (strCalling != null && cacheService.get(radiusUtility.normalizeMacAddress(strCalling)) != null) {
                        log.warn("Cache remove on DM for mac: " + strCalling);
                        cacheService.remove(radiusUtility.normalizeMacAddress(strCalling));
                    }
                }
                RadiusClient rc = new RadiusClient(coaProfileData.getGateway(), coaProfileData.getSharedkey());
                rc.setSocketTimeout(coaProfileData.getTimevar().intValue());
                response = rc.communicate(dmRequest, coaProfileData.getPort());
                log.warn("DM Response is " + response.getPacketType());
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while processing CoA request", e);
        }
        return null;
    }


    public static String encryptPassword(String password, String key) {
        try {
            // Huawei devices typically use AES encryption with a 16-byte key

            int blockSize = 16; // AES block size is 16 bytes
            int paddingLength = blockSize - (key.getBytes().length % blockSize);

            // Create a new byte array for padded data
            byte[] padded = new byte[key.getBytes().length + paddingLength];
            // If no padding is needed, return the original array
            if (paddingLength == blockSize) {

            } else {

                // Copy the original data into the padded array
                System.arraycopy(key.getBytes(), 0, padded, 0, key.getBytes().length);
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(padded, "AES");

            // Create AES cipher instance
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            // Encrypt the password
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());

            // Encode the encrypted bytes to Base64
            String encryptedPassword = Base64.getEncoder().encodeToString(encryptedBytes);

            return encryptedPassword;
        } catch (Exception e) {
            System.out.println("Encryption error: " + e.getMessage());
            return null;
        }
    }

    protected RadiusPacket proxyPacket(RadiusPacket packet, ProxyServer proxyServerData, InetSocketAddress client) throws Exception, RadiusException {
        /*synchronized (RadiusUtility.class) {
            proxyIndex++;
            String proxyIndexStr = Integer.toString(proxyIndex);
            packet.addAttribute(new RadiusAttribute(33, proxyIndexStr.getBytes()));
        }*/
        RadiusPacket proxyReplyRad = null;
        try {
            CacheRetrival cacheRetrival = new CacheRetrival();
            Map<String, Object> proxyMap = cacheRetrival.getProxyServerData();
            log.debug("proxyPacket IP:" + proxyServerData.getIp() + ":");
            if (proxyServerData != null) {

                //Change Packet
                if (proxyServerData.getOverrideNAS() && proxyServerData.getNasip() != null) {
                    if (packet.getAttribute("NAS-IP-Address") != null) {
                        packet.removeAttributes(4);
                        packet.addAttribute("NAS-IP-Address", proxyServerData.getNasip());
                    }
                }

                // Prepare Data
                InetAddress serverAddress = InetAddress.getByName(proxyServerData.getIp());
                int serverPort = 0;
                String serverSecret = proxyServerData.getSecretkey().trim();

                if (packet.getPacketType() == 1 || packet.getPacketType() == 2 || packet.getPacketType() == 3) {
                    serverPort = Integer.parseInt(proxyServerData.getAuthport());
                } else if (packet.getPacketType() == 40 || packet.getPacketType() == 43) {
                    serverPort = Integer.parseInt(proxyServerData.getDynaAuthPort());
                } else {
                    serverPort = Integer.parseInt(proxyServerData.getAcctport());
                }

                // save request authenticator (will be calculated new)
                byte[] auth = packet.getAuthenticator();

                //Send Packet
                // encode new packet (with new authenticator)
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                packet.encodeRequestPacket(bos, serverSecret);
                byte[] data = bos.toByteArray();
                DatagramPacket datagram = new DatagramPacket(data, data.length, serverAddress, serverPort);
                //		System.out.println("Proxy Server:"+serverAddress+":Port:"+serverPort+":Secret:"+serverSecret);

                // restore original authenticator
                packet.setAuthenticator(auth);


                // send packet
                DatagramSocket proxySocket = new DatagramSocket();
                proxySocket.setSoTimeout(Integer.parseInt(proxyServerData.getTimeout()));
                proxySocket.send(datagram);

                // Receive Packet
                byte[] receivesize = new byte[proxySocket.getReceiveBufferSize()];
                DatagramPacket reply = new DatagramPacket(receivesize, receivesize.length);
                proxySocket.receive(reply);

                ByteArrayInputStream in = new ByteArrayInputStream(reply.getData());
                //		System.out.println("Proxy Response Recevied :"+reply.getData());
                proxyReplyRad = new RadiusPacket(packet.getPacketType(), packet.getPacketIdentifier(), packet.getAttributes());
                proxyReplyRad = RadiusPacket.decodeResponsePacket(in, serverSecret, packet);
                proxySocket.close();
                VendorSpecificAttribute vsa = null;
                int size = 0;
                try {
                    //HARDCODE and Very bed code. Need to work once UAT over
                    if (proxyReplyRad != null) {
                        LinkedList result = (LinkedList) proxyReplyRad.getVendorAttributes(4874);
                        //		System.out.println("VSA LIST :"+result+":Size is:"+result.size());
                        for (Iterator i = result.iterator(); i.hasNext(); ) {
                            vsa = (VendorSpecificAttribute) i.next();
                            List subAttributes = vsa.getSubAttributes();
                            size = size + subAttributes.size();
                            //		System.out.println("Child List:"+subAttributes+":Size:"+size);
                            for (Iterator j = subAttributes.iterator(); j.hasNext(); ) {
                                StringAttribute childVsa = (StringAttribute) j.next();
                                //		System.out.println("VSA Recevied is :"+childVsa+":Value:"+childVsa.getAttributeValue());
                                if (childVsa.getAttributeValue().contains("INTERNET") || childVsa.getAttributeValue().contains("GGC") || childVsa.getAttributeValue().contains("AKAMAI") || childVsa.getAttributeValue().contains("NPIX") || childVsa.getAttributeValue().contains("NTA")) {
                                    String strValue = childVsa.getAttributeValue();
                                    strValue = strValue.substring(1);
                                    //		System.out.println("Changed:"+strValue);
                                    if (strValue.contains("INTERNET")) {
                                        proxyReplyRad.setTag((byte) 01);
                                    }
                                    if (strValue.contains("GGC")) {
                                        proxyReplyRad.setTag((byte) 02);
                                    }
                                    if (strValue.contains("AKAMAI")) {
                                        proxyReplyRad.setTag((byte) 03);
                                    }
                                    if (strValue.contains("NPIX")) {
                                        proxyReplyRad.setTag((byte) 04);
                                    }
                                    if (strValue.contains("NTA")) {
                                        proxyReplyRad.setTag((byte) 02);
                                    }
                                    proxyReplyRad.addAttribute("Unisphere-Service-Activate", strValue);
                                }
                            }
                        }

                        for (int i = 0; i < size; i++) {
                            //		System.out.println("Removing Unisphere-Service-Activate");
                            proxyReplyRad.removeAttribute(proxyReplyRad.getAttribute("Unisphere-Service-Activate"));
                        }

                    }
                } catch (Exception e) {
                    log.error("Uniphere Process Problem" + e.getMessage());
                }
                    /*
                    if(proxyReplyRad.getAttribute("Unisphere-Service-Activate")!=null) {
                        String strValue=proxyReplyRad.getAttribute(4874, 65).getAttributeValue();
                        proxyReplyRad.removeAttribute(proxyReplyRad.getAttribute("Unisphere-Service-Activate"));
                        strValue=strValue.substring(1);
                        //		System.out.println("Changed "+strValue);
                        proxyReplyRad.addAttribute("Unisphere-Service-Activate",strValue);
                        if(strValue.contains("INTERNET") || strValue.contains("GGC") || strValue.contains("AKAMAI") || strValue.contains("NPIX")) {
                            if(strValue.contains("INTERNET")) {
                                proxyReplyRad.setTag((byte) 01);
                            }
                            if(strValue.contains("GGC")) {
                                proxyReplyRad.setTag((byte) 02);
                            }
                            if(strValue.contains("AKAMAI")) {
                                proxyReplyRad.setTag((byte) 03);
                            }
                            if(strValue.contains("NPIX")) {
                                proxyReplyRad.setTag((byte) 04);
                            }
                        }
                    }
                    */
                return proxyReplyRad;
            } else {
                log.debug("Proxy Server Not Found in Cache:" + proxyServerData);
            }
        } catch (Exception e) {
            log.error("Error while Proxy radius request:" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return proxyReplyRad;
    }

    protected RadiusPacket proxyPacketCoADM(RadiusPacket packet, ProxyServer proxyServerData, InetSocketAddress client) throws Exception, RadiusException {
        RadiusPacket proxyReplyRad = null;
        try {
            CacheRetrival cacheRetrival = new CacheRetrival();
            Map<String, Object> proxyMap = cacheRetrival.getProxyServerData();
            if (proxyServerData != null) {
                log.debug("proxyPacketCoADM IP:" + proxyServerData.getIp() + ":");
                // Prepare Data
                int serverPort = Integer.parseInt(proxyServerData.getDynaAuthPort());
                String serverSecret = proxyServerData.getSecretkey().trim();
                VendorSpecificAttribute vsa = null;
                int size = 0;

                if (packet.getPacketType() == 40) {
                    DmRequest dmRequest = new DmRequest();

                    try {
                        //HARDCODE and Very bed code. Need to work once UAT over
                        if (packet != null) {
                            LinkedList result = (LinkedList) packet.getVendorAttributes(4874);
                            //		System.out.println("VSA LIST :"+result+":Size is:"+result.size());
                            for (Iterator i = result.iterator(); i.hasNext(); ) {
                                vsa = (VendorSpecificAttribute) i.next();
                                List subAttributes = vsa.getSubAttributes();
                                size = size + subAttributes.size();
                                //		System.out.println("Child List:"+subAttributes+":Size:"+size);
                                for (Iterator j = subAttributes.iterator(); j.hasNext(); ) {
                                    StringAttribute childVsa = (StringAttribute) j.next();
                                    //		System.out.println("VSA Recevied is :"+childVsa+":Value:"+childVsa.getAttributeValue());
                                    if (childVsa.getAttributeValue().contains("INTERNET") || childVsa.getAttributeValue().contains("GGC") || childVsa.getAttributeValue().contains("AKAMAI") || childVsa.getAttributeValue().contains("NPIX") || childVsa.getAttributeValue().contains("NTA")) {
                                        String strValue = childVsa.getAttributeValue();
                                        strValue = strValue.substring(1);
                                        //		System.out.println("Changed:"+strValue);
                                        if (strValue.contains("INTERNET")) {
                                            packet.setTag((byte) 01);
                                        }
                                        if (strValue.contains("GGC")) {
                                            packet.setTag((byte) 02);
                                        }
                                        if (strValue.contains("AKAMAI")) {
                                            packet.setTag((byte) 03);
                                        }
                                        if (strValue.contains("NPIX")) {
                                            packet.setTag((byte) 04);
                                        }
                                        if (strValue.contains("NTA")) {
                                            packet.setTag((byte) 02);
                                        }
                                        packet.addAttribute("Unisphere-Service-Activate", strValue);
                                    }
                                }
                            }

                            for (int i = 0; i < size; i++) {
                                //		System.out.println("Removing Unisphere-Service-Activate");
                                packet.removeAttribute(packet.getAttribute("Unisphere-Service-Activate"));
                            }

                        }
                    } catch (Exception e) {
                        log.error("Uniphere Process Problem" + e.getMessage());
                    }

                    for (Iterator i = packet.getAttributes().iterator(); i.hasNext(); ) {
                        RadiusAttribute radAttrib = (RadiusAttribute) i.next();
                        dmRequest.addAttribute(radAttrib);
                    }
                    log.debug("DM Packet" + dmRequest + ":ON IP:" + proxyServerData.getIp() + ":Key:" + serverSecret);
                    RadiusClient rc = new RadiusClient(proxyServerData.getIp(), serverSecret);
                    rc.setSocketTimeout(Integer.parseInt(proxyServerData.getTimeout()));
                    rc.setRetryCount(1);
                    proxyReplyRad = rc.communicate(dmRequest, serverPort);
                } else {
                    CoaRequest coaRequest = new CoaRequest();


                    try {
                        //HARDCODE and Very bed code. Need to work once UAT over
                        if (packet != null) {
                            LinkedList result = (LinkedList) packet.getVendorAttributes(4874);
                            //		System.out.println("VSA LIST :"+result+":Size is:"+result.size());
                            for (Iterator i = result.iterator(); i.hasNext(); ) {
                                vsa = (VendorSpecificAttribute) i.next();
                                List subAttributes = vsa.getSubAttributes();
                                size = size + subAttributes.size();
                                //		System.out.println("Child List:"+subAttributes+":Size:"+size);
                                for (Iterator j = subAttributes.iterator(); j.hasNext(); ) {
                                    StringAttribute childVsa = (StringAttribute) j.next();
                                    //		System.out.println("VSA Recevied is :"+childVsa+":Value:"+childVsa.getAttributeValue());
                                    if (childVsa.getAttributeValue().contains("INTERNET") || childVsa.getAttributeValue().contains("GGC") || childVsa.getAttributeValue().contains("AKAMAI") || childVsa.getAttributeValue().contains("NPIX") || childVsa.getAttributeValue().contains("NTA")) {
                                        String strValue = childVsa.getAttributeValue();
                                        strValue = strValue.substring(1);
                                        //		System.out.println("Changed:"+strValue);
                                        if (strValue.contains("INTERNET")) {
                                            packet.setTag((byte) 01);
                                        }
                                        if (strValue.contains("GGC")) {
                                            packet.setTag((byte) 02);
                                        }
                                        if (strValue.contains("AKAMAI")) {
                                            packet.setTag((byte) 03);
                                        }
                                        if (strValue.contains("NPIX")) {
                                            packet.setTag((byte) 04);
                                        }
                                        if (strValue.contains("NTA")) {
                                            packet.setTag((byte) 02);
                                        }
                                        packet.addAttribute("Unisphere-Service-Activate", strValue);
                                    }
                                }
                            }

                            for (int i = 0; i < size; i++) {
                                //		System.out.println("Removing Unisphere-Service-Activate");
                                packet.removeAttribute(packet.getAttribute("Unisphere-Service-Activate"));
                            }

                        }
                    } catch (Exception e) {
                        log.error("Uniphere Process Problem" + e.getMessage());
                    }


                    for (Iterator i = packet.getAttributes().iterator(); i.hasNext(); ) {
                        RadiusAttribute radAttrib = (RadiusAttribute) i.next();
                        coaRequest.addAttribute(radAttrib);
                    }
                    log.debug("CoA Packet" + coaRequest + ":ON IP:" + proxyServerData.getIp() + ":Key:" + serverSecret);
                    RadiusClient rc = new RadiusClient(proxyServerData.getIp(), serverSecret);
                    rc.setSocketTimeout(Integer.parseInt(proxyServerData.getTimeout()));
                    rc.setRetryCount(1);
                    proxyReplyRad = rc.communicate(coaRequest, serverPort);
                }
                //		System.out.println(":COA/DM:Identifier Receved:"+packet.getPacketIdentifier()+":Generated:"+proxyReplyRad.getPacketIdentifier());
                proxyReplyRad.setPacketIdentifier(packet.getPacketIdentifier());
                log.debug("Response is " + proxyReplyRad);
                return proxyReplyRad;
            } else {
                log.debug("Proxy Server Not Found in Cache:" + proxyServerData);
            }
        } catch (Exception e) {
            log.error("Error while Proxy radius request:" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return proxyReplyRad;
    }


    public void SendUsedQotaInfo(CustomerQuotaInfo info) {
        // Set message in queue to send info related to used data.
        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
        radaysn.custQuotaInfoUpdateProcess(info, MessageConstants.QUEUE_UPDATE_QUOTA);
    }


    public void sendCustQuotaIntrimDetailToApigw(Integer cprid, double currentSessionUsageTime, double currentSessionUsageVolume) {
        log.debug("enter in custquotadetailtoapigw");
        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
        radaysn.custQuotaIntrimUpdateProcess(cprid, currentSessionUsageTime, currentSessionUsageVolume, MessageConstants.QUEUE_SEND_QUOTA_INTRIM_FROM_RADIUS);
    }


    public void SendUpdateNASIPInfo(NasUpdateMessage info) {
        // Set message in queue to send info related to used data.
        KafkaMessageSender kafkaMessageSender1 = SpringContext.getBean(KafkaMessageSender.class);
        if (kafkaMessageSender1 != null) {
            //messageSender.send(info,RabbitMqConstants.QUEUE_SEND_NASUPDATE);
            kafkaMessageSender1.send(new KafkaMessageData(info, info.getClass().getSimpleName()));
        } else {
            log.error("SendUpdateNASIPInfo Fail");
        }
    }


    public int timepolicytoformula(String fromDay, String fromTime) {
        int i = 0;
        String basic;
        switch (fromDay.toLowerCase()) {
            case "sunday":
                basic = "1";
                fromTime = fromTime.replace(":", "");
                basic = basic + fromTime;
                i = Integer.parseInt(basic);
                return i;
            case "monday":
                basic = "2";
                fromTime = fromTime.replace(":", "");
                basic = basic + fromTime;
                i = Integer.parseInt(basic);
                return i;
            case "tuesday":
                basic = "3";
                fromTime = fromTime.replace(":", "");
                basic = basic + fromTime;
                i = Integer.parseInt(basic);
                return i;
            case "wednesday":
                basic = "4";
                fromTime = fromTime.replace(":", "");
                basic = basic + fromTime;
                i = Integer.parseInt(basic);
                return i;
            case "thursday":
                basic = "5";
                fromTime = fromTime.replace(":", "");
                basic = basic + fromTime;
                i = Integer.parseInt(basic);
                return i;
            case "friday":
                basic = "6";
                fromTime = fromTime.replace(":", "");
                basic = basic + fromTime;
                i = Integer.parseInt(basic);
                return i;
            case "saturday":
                basic = "7";
                fromTime = fromTime.replace(":", "");
                basic = basic + fromTime;
                i = Integer.parseInt(basic);
                return i;
        }
        return i;
    }


    private TimepolicyData verifyslab(HashMap hmPolicy, int currentone) {
        Iterator it = hmPolicy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            TimepolicyData tbp = (TimepolicyData) pair.getValue();
            log.debug("Check From:" + tbp.getFromNumber() + ":To:" + tbp.getToNumber() + ":with:" + currentone);
            if (tbp.getFromNumber() <= currentone && tbp.getToNumber() >= currentone) {
                return tbp;
            }
        }
        return null;
    }


    public void SendCustMacInfo(CustMacMessage info) {
        // Set message in queue to send info related to used data.
        KafkaMessageSender kafkaMessageSender1 = SpringContext.getBean(KafkaMessageSender.class);
        if (kafkaMessageSender1 != null) {
            log.info(String.format("Send mac save or update message : %s", info));
            kafkaMessageSender1.send(new KafkaMessageData(info, info.getClass().getSimpleName()));
        } else {
            log.error("Add Mac message not sent in APIGTW");
        }
    }

    public void SendCustNextBillDateMessageInfo(CustNextBilldateMessage info) {
        KafkaMessageSender kafkaMessageSender1 = SpringContext.getBean(KafkaMessageSender.class);
        if (kafkaMessageSender1 != null) {
            kafkaMessageSender1.send(new KafkaMessageData(info, info.getClass().getSimpleName()));
        } else {
            log.error("Customer nextBilldate message not sent in APIGTW");
        }
    }

    public void SendCustPlanInfo(CustomerPackageRelMessage info) {
        // Set message in queue to send info related to used data.
        KafkaMessageSender kafkaMessageSender1 = SpringContext.getBean(KafkaMessageSender.class);
        if (kafkaMessageSender1 != null) {
            //messageSender.send(info,RabbitMqConstants.QUEUE_SEND_CUST_PLAN_DETAIL_FROM_RADIUS);
            kafkaMessageSender1.send(new KafkaMessageData(info, info.getClass().getSimpleName()));
        }
    }

    public void sendNotificationOnQuotaUsage(Integer cprId, Integer custId, double percentageUsage) {
        CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
        Boolean flag = customerServiceImpl.isNotificationAppilicableToSend(cprId, percentageUsage);
        if (flag) {
            //add parent cust
            customerServiceImpl.sendCustQuotaDetailToApigw(cprId, percentageUsage, 0d, 0d);
            log.debug("Send Notification for:" + custId);
        } else {
            log.debug("flag" + flag + "thats why no notification");
        }
    }

    public boolean checkMacMappingExistWithcustomer(int custId, int mvnoid, String mac) throws SQLException {
        DBAuthenticationDriver dbAuthDrive = new DBAuthenticationDriver();
        return dbAuthDrive.isCustomerMapWithGivenMac(custId, mvnoid, mac);
    }

    public double convertUsageToBytes(double usage, String unit) {
        switch (unit) {
            case "KB":
                return usage * 1024;
            case "MB":
                return usage * 1048576;
            case "GB":
                return usage * 1073741824;
            default:
                return usage;
        }
    }

    public double convertUsageToGivenUnit(double usage, String unit) {
        switch (unit) {
            case "KB":
                return usage / 1024;
            case "MB":
                return usage / 1048576;
            case "GB":
                return usage / 1073741824;
            default:
                return usage;
        }
    }

    public long convertUsageToSec(long timeQuota, String unit) {
        switch (unit) {
            case "MIN":
                return timeQuota *= 60;
            case "HOUR":
                return timeQuota *= 60 * 60;
            case "DAY":
                return timeQuota *= 60 * 60 * 60;
            default:
                return timeQuota;
        }
    }

    public CustomerData getCustomerDetailsForAccessRequest(CustomerData custReturnData, String authenticationMode, String strIPAddress, AccessRequest request, Client cltData, String strUsername, String strPassword, String strCalling, boolean isPasswordCheck, String acctStatusValue) throws SQLException {
        long startTime = System.currentTimeMillis();
        switch (authenticationMode != null ? authenticationMode.toLowerCase() : "") {
            case "":
                log.debug(String.format("Authentication mode is not selected. So, Customer fetching using User-Name"));
                custReturnData = getCustomerDataUsingUserNameOrMac(request, cltData, strUsername, strPassword, strCalling, isPasswordCheck, acctStatusValue);
                break;
            case "username_or_mac":
                log.debug(String.format("Customer Fetching using username_or_mac"));
                custReturnData = getCustomerDataUsingUserNameOrMac(request, cltData, strUsername, strPassword, strCalling, isPasswordCheck, acctStatusValue);
                break;
            case "username":
                log.debug(String.format("Customer Fetching using username"));
                custReturnData = authenticateCustomer(strUsername, strPassword, cltData.getMvnoId(), request, isPasswordCheck, acctStatusValue);
                break;
            case "mac":
                log.debug(String.format("Customer Fetching using mac"));
                custReturnData = authenticateCustomerAll(strCalling, cltData.getMvnoId(), authenticationMode, null, null, strPassword, isPasswordCheck, acctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;
            case "ip":
                strUsername = extractIP(strUsername);
                log.debug(String.format("Customer Fetching using ip: %s", strUsername));
                custReturnData = authenticateCustomerAll(strUsername, cltData.getMvnoId(), authenticationMode, null, null, strPassword, isPasswordCheck, acctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                }
                break;
            case "username_mac":
                log.debug(String.format("Customer Fetching using username_mac"));
                custReturnData = authenticateCustomerAll(strUsername, cltData.getMvnoId(), authenticationMode, strCalling, null, strPassword, isPasswordCheck, acctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;
            case "username_ip":
                log.debug(String.format("Customer Fetching using username_ip"));
                custReturnData = authenticateCustomerAll(strUsername, cltData.getMvnoId(), authenticationMode, null, strIPAddress, strPassword, isPasswordCheck, acctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                }
                break;
            case "mac_ip":
                log.debug(String.format("Customer Fetching using mac_ip"));
                custReturnData = authenticateCustomerAll(null, cltData.getMvnoId(), authenticationMode, strCalling, strIPAddress, strPassword, isPasswordCheck, acctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;
            case "username_mac_ip":
                log.debug(String.format("Customer Fetching using username_mac_ip"));
                custReturnData = authenticateCustomerAll(strUsername, cltData.getMvnoId(), authenticationMode, strCalling, strIPAddress, strPassword, isPasswordCheck, acctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;
            default:
                // Handle unexpected authentication modes if needed
                break;
        }
        long endTime = System.currentTimeMillis();
        log.info("Time Taken: for getCustomerDetails:   " + (endTime - startTime));
        return custReturnData;
    }

    private CustomerData getCustomerDataUsingUserNameOrMac(AccessRequest request, Client cltData, String strUsername, String strPassword, String strCalling, boolean isPasswordCheck, String acctStatusValue) throws SQLException {
        CustomerData custReturnData;
        custReturnData = authenticateCustomer(strUsername, strPassword, cltData.getMvnoId(), request, isPasswordCheck, acctStatusValue);
        if (custReturnData == null || custReturnData.getUsername() == null) {

            log.debug(String.format("Customer Data not found using User-Name, trying to fetch using MAC"));

            custReturnData = authenticateCustomerAll(strCalling, cltData.getMvnoId(), "mac", null, null, strPassword, isPasswordCheck, acctStatusValue);
            log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
            if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                custReturnData = new CustomerData();
                custReturnData.setAuthStatus(false);
                custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
            }
        }
        return custReturnData;
    }

    public CustomerData getCustomerDetailsForAcctRequest(DBAuthenticationDriver dbAuth, CustomerData custReturnData, String authenticationMode, String strIPAddress, Client cltData, String strUsername, String strCalling, String AcctStatusValue) throws SQLException {
        log.debug(String.format("In getCustomerDetailsForAcctRequest authenticationMode: %s: username: %s: ipAddress: %s: strCalling: %s", authenticationMode, strUsername, strIPAddress, strCalling));
        switch (authenticationMode != null ? authenticationMode.toLowerCase() : "") {
            case "username_or_mac":
                log.debug(String.format("Customer Fetching using username: %s", strUsername));
                custReturnData = dbAuth.getDBCustomer(strUsername, cltData.getMvnoId(), null, "ACCT", false, AcctStatusValue, false);
                if (custReturnData == null || custReturnData.getUsername() == null) {
                    log.debug(String.format("Customer Data not found using User-Name, trying to fetch using MAC"));
                    log.debug(String.format("Customer Fetching using mac: %s", strCalling));
                    custReturnData = dbAuth.getDBAllCustomerAuthenticate(strCalling, cltData.getMvnoId(), "mac", null, null, AcctStatusValue);
                    if (custReturnData == null) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage("User Not Found");
                        custReturnData.setUsername(null);
                    } else {
                        custReturnData.setMacflow(true);
                        log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                        if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                            custReturnData = new CustomerData();
                            custReturnData.setAuthStatus(false);
                            custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                        }
                    }
                    break;
                }
                break;
            case "username":
                log.debug(String.format("Customer Fetching using username: %s", strUsername));
                custReturnData = dbAuth.getDBCustomer(strUsername, cltData.getMvnoId(), null, "ACCT", false);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                }
                break;

            case "mac":
                log.debug(String.format("Customer Fetching using mac: %s", strUsername));
                custReturnData = dbAuth.getDBAllCustomerAuthenticate(strUsername, cltData.getMvnoId(), authenticationMode, null, null, AcctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;

            case "ip":
                strUsername = extractIP(strUsername);
                log.debug(String.format("Customer Fetching using ip: %s", strUsername));
                custReturnData = dbAuth.getDBAllCustomerAuthenticate(strUsername, cltData.getMvnoId(), authenticationMode, null, null, AcctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                }
                break;

            case "username_mac":
                log.debug(String.format("Customer Fetching using username_mac: %s and %s", strUsername, strCalling));
                custReturnData = dbAuth.getDBAllCustomerAuthenticate(strUsername, cltData.getMvnoId(), authenticationMode, strCalling, null, AcctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;

            case "username_ip":
                log.debug(String.format("Customer Fetching using username_ip: %s and %s", strUsername, strIPAddress));
                custReturnData = dbAuth.getDBAllCustomerAuthenticate(strUsername, cltData.getMvnoId(), authenticationMode, null, strIPAddress, AcctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                }
                break;

            case "mac_ip":
                log.debug(String.format("Customer Fetching using mac_ip: %s and %s", strCalling, strIPAddress));
                custReturnData = dbAuth.getDBAllCustomerAuthenticate(null, cltData.getMvnoId(), authenticationMode, strCalling, strIPAddress, AcctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;

            case "username_mac_ip":
                log.debug(String.format("Customer Fetching using username_mac_ip: %s and %s and %s", strCalling, strIPAddress));
                custReturnData = dbAuth.getDBAllCustomerAuthenticate(strUsername, cltData.getMvnoId(), authenticationMode, strCalling, strIPAddress, AcctStatusValue);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;

            default:
                // Default case when authenticationMode is null or any other value
                log.debug("Customer Fetching using Default");
                custReturnData = dbAuth.getDBCustomer(strUsername, cltData.getMvnoId(), null, "ACCT", false);
                if (custReturnData == null) {
                    custReturnData = new CustomerData();
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage("User Not Found");
                    custReturnData.setUsername(null);
                } else {
                    custReturnData.setMacflow(true);
                    log.debug("Customer Mac Auth Enable Flag: " + custReturnData.getMacAuthEnable());
                    if (custReturnData.getMacAuthEnable() != null && !custReturnData.getMacAuthEnable()) {
                        custReturnData = new CustomerData();
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                    }
                }
                break;
        }
        return custReturnData;
    }

    public static String extractIP(String text) {
        String ipRegex = "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b";
        Pattern pattern = Pattern.compile(ipRegex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    public String extractValueFromRegex(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    public double getDownLoadDataFromAccountingRequest(AccountingRequest request, ClientGroup clientGroup) {
        double upload = 0d;
        boolean isStandardAttrChecked = clientGroup.isStandardAttributeChecked();
        try {
            if (request.getAttribute("Alc-Acct-O-Inprof-Octets-64") != null) {
                // need to verify
                return getQuotaFromHexaDecimal(request, "Alc-Acct-O-Inprof-Octets-64", isStandardAttrChecked);
            }
        } catch (IllegalArgumentException ex) {
            log.error("Exception to get value for 'Alc-Acct-O-Outprof-Octets-64' " + ex.getMessage());
        }
        if (clientGroup.getOutPutPacketAttributeVendorId() != null && clientGroup.getOutPutPacketAttributeType() != null)
            upload = Long.parseLong(getRequestAttribute(request, Integer.parseInt(clientGroup.getOutPutPacketAttributeVendorId()), Integer.parseInt(clientGroup.getOutPutPacketAttributeType()), clientGroup.getOutputPacketAttributeValue()));

        /*
        Add check for standard_attribute_checked from cltGroupData
        if standard_attribute_checked = true
        then add AcctInputName and AcctOutputName in download and upload
         */
        if (isStandardAttrChecked && !clientGroup.getOutputPacketAttributeValue().equalsIgnoreCase("Acct-Output-Octets")) {
            if (request.getAttribute("Acct-Output-Octets") != null) {
                upload = upload + Double.parseDouble(request.getAttributeValue("Acct-Output-Octets"));
            }
        }
        //Gigawords
        upload = addGigaWordsinUpload(upload, request, isStandardAttrChecked);
        return upload;
    }

    /**
     * Get Download speed for
     *
     * @param request
     * @param AcctInputName
     * @param isStandardAttrChecked\
     * @return
     */
    public double getUploadDataFromAccountingRequest(AccountingRequest request, ClientGroup clientGroup) {
        double download = 0d;
        boolean isStandardAttrChecked = clientGroup.isStandardAttributeChecked();
        try {
            if (request.getAttribute("Alc-Acct-I-All-Octets_64") != null) {
                // need to verify
                return getQuotaFromHexaDecimal(request, "Alc-Acct-I-All-Octets_64", isStandardAttrChecked);
            }
        } catch (IllegalArgumentException ex) {
            log.error("Exception to get value for 'Alc-Acct-O-Inprof-Octets-64' " + ex.getMessage());
        }
        if (clientGroup.getInPutPacketAttributeVendorId() != null && clientGroup.getInPutPacketAttributeType() != null) {
            int vendorId = Integer.valueOf(clientGroup.getInPutPacketAttributeVendorId());
            int attrtype = Integer.valueOf(clientGroup.getInPutPacketAttributeType());
            download = Long.parseLong(getRequestAttribute(request, Integer.parseInt(clientGroup.getInPutPacketAttributeVendorId()), Integer.parseInt(clientGroup.getInPutPacketAttributeType()), clientGroup.getInputPacketAttributeValue()));
        }
        /*
        Add check for standard_attribute_checked from cltGroupData
        if standard_attribute_checked = true
        then add AcctInputName and AcctOutputName in download and upload
         */
        if (isStandardAttrChecked && !clientGroup.getInputPacketAttributeValue().equalsIgnoreCase("Acct-Input-Octets")) {
            if (request.getAttribute("Acct-Input-Octets") != null) {
                download = download + Double.parseDouble(request.getAttributeValue("Acct-Input-Octets"));
            }
        }
        //Gigawords
        download = addGigaWordsinDownload(download, request, isStandardAttrChecked);
        return download;
    }


    public double addGigaWordsinUpload(double upload, AccountingRequest request, boolean isStandardAttrChecked) {

        try {
            //Gigawords HW-Acct-ipv6-Output-Octets
            if (request.getAttribute("HW-Acct-ipv6-Output-Gigawords") != null) {
                Double gigaWords = Double.parseDouble(request.getAttribute("HW-Acct-ipv6-Output-Gigawords").getAttributeValue());
                gigaWords = (1073741824d * 4d) * gigaWords;
                upload = upload + gigaWords;
            }
        } catch (IllegalArgumentException ex) {
            log.error("Error while get gigawords calculation for attribute HW-Acct-ipv6-Output-Gigawords : " + ex.getMessage());
        }
        try {
            if (isStandardAttrChecked && request.getAttribute(53) != null) {
                Double gigaWords = Double.parseDouble(request.getAttribute(53).getAttributeValue());
                gigaWords = (1073741824d * 4d) * gigaWords;
                upload = upload + gigaWords;
            }
        } catch (IllegalArgumentException ex) {
            log.error("Error while get gigawords calculation for standard attribute: " + ex.getMessage());
        }
        return upload;
    }

    public double addGigaWordsinDownload(double download, AccountingRequest request, boolean isStandardAttrChecked) {

        try {
            //Gigawords HW-Acct-ipv6-Output-Octets
            if (request.getAttribute("HW-Acct-ipv6-Input-Gigawords") != null) {
                Double gigaWords = Double.parseDouble(request.getAttribute("HW-Acct-ipv6-Input-Gigawords").getAttributeValue());
                gigaWords = (1073741824d * 4d) * gigaWords;
                download = download + gigaWords;
            }
        } catch (IllegalArgumentException ex) {
            log.error("Error while get gigawords calculation for attribute HW-Acct-ipv6-Input-Gigawords : " + ex.getMessage());
        }
        try {
            if (isStandardAttrChecked && request.getAttribute(52) != null) {
                Double gigaWords = Double.parseDouble(request.getAttribute(52).getAttributeValue());
                gigaWords = (1073741824d * 4d) * gigaWords;
                download = download + gigaWords;
            }
        } catch (IllegalArgumentException ex) {
            log.error("Error while get gigawords calculation for standard attribute: " + ex.getMessage());
        }
        return download;
    }

    public double getQuotaFromHexaDecimal(AccountingRequest request, String AcctName, boolean isStandardAttrChecked) {
        try {
            String hex = request.getAttribute(AcctName).getAttributeValue();
            log.debug(String.format("Get Attribute value for name %s: %s", AcctName, hex));
            if (hex != null) {
                if (hex.contains("0x8003") || hex.contains("0x0003")) {
                    //skip for LAN3
                } else {
                    hex = hex.substring(6);
                }
                return convertHexaDecimalToDecimal(hex);
            }
        } catch (Exception ex) {
            log.debug(String.format("Error to get Attribute value for name %s: %s : %s", AcctName, "Hexadecimal", ex.getMessage()));
        }
        return 0;
    }

    public double convertHexaDecimalToDecimal(String hex) {
        return Long.parseLong(hex, 16);
    }

    private String getRequestAttribute(AccountingRequest request, int vendorId, int attributeType, String attributeName) {
        log.debug(String.format("Get Attribute value for name %s: %s", attributeName, attributeType));
        String value = "0";
        if (vendorId != 0) {
            if (request.getAttribute(vendorId, attributeType) != null)
                value = request.getAttribute(vendorId, attributeType).getAttributeValue();
            log.debug(String.format("%s: %s", attributeName, value));
            return value;
        } else {
            if (request.getAttribute(attributeType) != null)
                value = request.getAttribute(attributeType).getAttributeValue();
            log.debug(String.format("%s: %s", attributeName, value));
            return value;
        }
    }

    public RadiusPacket getRequestFromLiveUser(LiveUser liveUser) {
        AccountingRequest acctReq = new AccountingRequest();
        if (liveUser.getUserName() != null)
            acctReq.addAttribute("User-Name", liveUser.getUserName());

        if (liveUser.getFramedIpAddress() != null && liveUser.getFramedIpAddress() != "") {
            acctReq.addAttribute("Framed-IP-Address", liveUser.getFramedIpAddress());
        }

        if (liveUser.getCallingStationId() != null && liveUser.getCallingStationId() != "") {
            acctReq.addAttribute(RadiusAttributes.CALLING_STATION_ID.getName(), liveUser.getCallingStationId());
        }
        if (liveUser.getAcctSessionId() != null) {
            acctReq.addAttribute("Acct-Session-Id", liveUser.getAcctSessionId());
        }
        if (liveUser.getNasIpAddress() != null) {
            acctReq.addAttribute("NAS-IP-Address", liveUser.getNasIpAddress());
        }
        if (liveUser.getNasPortId() != null) {
            acctReq.addAttribute("NAS-Port-Id", liveUser.getNasPortId());
        }
        if (liveUser.getlClass() != null) {
            acctReq.addAttribute(RadiusAttributes.CLASS.getName(), liveUser.getlClass());
        }
        if (liveUser.getLoginService() != null && liveUser.getLoginService() != "") {
            acctReq.addAttribute("Login-Service", liveUser.getLoginService());
        }
        if (liveUser.getNasIdentifier() != null) {
            acctReq.addAttribute(RadiusAttributes.NAS_IDENTIFIER.getName(), liveUser.getNasIdentifier());
        }
        return acctReq;
    }

    public void saveOrUpdateCustomerMac(String newMac, String oldMac, CustomerData custReturnData, Integer mvnoId, DBAuthenticationDriver dbAuthDrive, boolean isUpdate) {
        try {
            if (!custReturnData.isSavbillBSSDb()) {
                if (!dbAuthDrive.isCustomerExists(custReturnData.getUsername(), mvnoId))
                    custReturnData = dbAuthDrive.insertCustomer(custReturnData);
            }
            //check faulty mac
            CacheRetrival cacheRetrival = new CacheRetrival();
            Map<String, FaultyMAC> faultyMacList = cacheRetrival.getFaultyMacList();
            boolean faultyMacNotFound = true;
            String normalizedTargetMac = newMac;
            if (newMac != null) {
                normalizedTargetMac = normalizeMacAddress(newMac);
            }
            FaultyMAC faultyMAC = faultyMacList.get(normalizedTargetMac);
            if (faultyMAC != null) {
                faultyMacNotFound = false;
            }

            if (faultyMacNotFound) {
                Timestamp macRetentionTime = getMacRetentionDate(custReturnData);
                Integer macCountExists = dbAuthDrive.noOfMacWithCustomer(custReturnData.getCustid(), newMac);
                if (macCountExists > 0)
                    isUpdate = true;
                else
                    isUpdate = false;
                boolean strMacRespose = dbAuthDrive.saveOrUpdateMac(custReturnData.getCustid(), newMac, oldMac, isUpdate, macRetentionTime);
                log.info(String.format("Save or update mac response: %s", strMacRespose));
                if (newMac != null && faultyMacNotFound) {
                    log.debug("MAC Provision Response:" + newMac + ", User:" + custReturnData.getUsername());
                    HashMap<String, Timestamp> mac = new HashMap<String, Timestamp>();
                    mac.put(newMac, macRetentionTime);
                    if (strMacRespose) {
                        try {
                            CustMacMessage message = new CustMacMessage();
                            message.setMac(mac);
                            message.setMvnoId(custReturnData.getMvnoId());
                            message.setUserName(custReturnData.getUsername());
                            message.setBulkDelete(false);
                            message.setUpdate(isUpdate);
                            message.setOldMac(oldMac);
                            message.setFromRadius(true);
                            if (custReturnData.getCustomerBasePlan() != null && custReturnData.getCustomerBasePlan().size() > 0)
                                message.setCustPlanMapId(custReturnData.getCustomerBasePlan().get(0).getCustpackageid());
                            RadiusUtility radiusUtility = new RadiusUtility();
                            radiusUtility.SendCustMacInfo(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                dbAuthDrive.updateLastConnectedInFaultyMac(faultyMAC.getId(), faultyMAC.getMackId(), LocalDateTime.now().toString());
            }

        } catch (Exception ex) {
            log.error("Exception to save or update mac with customer: " + custReturnData.getUsername() + " mac: " + newMac + " error: " + ex.getMessage());
        }
    }

    public Timestamp getMacRetentionDate(CustomerData custRetrunData) {
        LocalDateTime macRetentionTime = LocalDateTime.now();
        if (custRetrunData.getMacRetentionPeriod() != null) {
            String retentionUnit = custRetrunData.getMacRetentionUnit();
            int retentionPeriod = custRetrunData.getMacRetentionPeriod();

            switch (retentionUnit != null ? retentionUnit.toUpperCase() : "DAY") {
                case "HOURS":
                    macRetentionTime = macRetentionTime.plusHours(retentionPeriod);
                    break;
                case "MINUTE":
                    macRetentionTime = macRetentionTime.plusMinutes(retentionPeriod);
                    break;
                default: // Default to "DAY" if unit is null or unrecognized
                    macRetentionTime = macRetentionTime.plusDays(retentionPeriod);
                    break;
            }
        } else {
            return null;
        }
        return Timestamp.valueOf(macRetentionTime);
    }

    public String getUserNameFromRequest(RadiusPacket request, RadiusProfile radiusProfile) {
        String user_name_attr = radiusProfile.getCustomerUserNameAttribute();//cltData.getClientGroupData().getCustomerUserNameAttribute();
        log.debug("User-Name attribute: " + user_name_attr);
        if (user_name_attr == null || user_name_attr.trim().isEmpty() || request.getAttribute(user_name_attr.trim()) == null) {
            user_name_attr = "User-Name";
        }
        String strUsername = request.getAttribute(user_name_attr).getAttributeValue();
        String userNameRegex = radiusProfile.getUsernameIdentityRegex();//cltData.getClientGroupData().getUsernameIdentityRegex();
        if (userNameRegex != null && !userNameRegex.isEmpty()) {
            strUsername = extractValueFromRegex(strUsername, userNameRegex);
            log.info("After apply regex: " + userNameRegex + " strUsername: " + strUsername);
        }
        return strUsername;
    }

    public VLANManagement verifyVlan1(RadiusPacket request, CustomerData customerData, ClientGroup clientGroup, Integer mvnoId) {
        VLANManagement vlanManagement = null;

        try {
            DBAuthenticationDriver dbAuthenticationDriver = new DBAuthenticationDriver();
            CacheRetrival cacheRetrival = new CacheRetrival();
            Map<String, List<VLANManagement>> vlanDetailsData1 = cacheRetrival.getVlanDetailsData1();//dbAuthenticationDriver.getVlanManagementUsingMvno(mvnoId);

            RadiusAttribute attribute1 = request.getAttribute("NAS-Port-Id");
            List<VLANManagement> vlanManagementList;
            if(attribute1 != null) {
                Pattern pattern1 = Pattern.compile("vlan-id\\s*(\\d+)");
                Matcher matcher1 = pattern1.matcher(attribute1.getAttributeValue());
                String splitedString = "";
                if(matcher1.find()){
                    splitedString = matcher1.group(1);
                }
                vlanManagementList = vlanDetailsData1.get(splitedString);
            }else {
                vlanManagementList = new ArrayList<>();
            }

            RadiusAttribute attribute2 = request.getAttribute("NAS-Identifier");
            List<VLANManagement> collect;
            if(attribute2!=null) {
                String attributeValue = attribute2.getAttributeValue();
                collect = vlanManagementList.stream().filter(en -> attributeValue.contains(en.getNasIdentifier())).collect(Collectors.toList());
            }else {
              collect = new ArrayList<>();
            }

            for (VLANManagement vlanManagement1 : collect) {
                if (vlanManagement1 != null) {
                    // check NasPort Id
                    if (customerData != null) {
                    } else {
                        customerData = new CustomerData();
                    }
                    customerData.setVlanManagement(vlanManagement1);
                    if (isListVLANValidationMappingMatched(vlanManagement1, request, customerData, dbAuthenticationDriver)) {
                        vlanManagement = vlanManagement1;
                        log.info("Vlan Magangement matched: " + vlanManagement.getVlanName());
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(String.format("Error to verfiy username:%s,  exception: ", request.getAttributeValue("User-Name"), ex.getMessage()));
        }
        return vlanManagement;
    }


    public VLANManagement verifyVlan(RadiusPacket request, CustomerData customerData, ClientGroup clientGroup, Integer mvnoId) {
        VLANManagement vlanManagement = null;

        try {
            // Fetch VLAN details efficiently
            DBAuthenticationDriver dbAuthenticationDriver = new DBAuthenticationDriver();
            CacheRetrival cacheRetrival = new CacheRetrival();
            List<VLANManagement> vlanManagements = cacheRetrival.getVlanDetailsData();

            log.info("VLAN Management count: {}", vlanManagements.size());

            if (vlanManagements.isEmpty()) {
                return null;
            }

            // Efficient Filtering & Sorting
            List<VLANManagement> vlanManagementList = filterAndSortVlan(vlanManagements, clientGroup.getVlanProfileMapping(), request);
            vlanManagementList.sort(Comparator.comparingLong(VLANManagement::getPriority).reversed());

            for (VLANManagement vlan : vlanManagementList) {
                if (customerData == null) {
                    customerData = new CustomerData();
                }
                customerData.setVlanManagement(vlan);

                if (isListVLANValidationMappingMatched(vlan, request, customerData, dbAuthenticationDriver)) {
                    vlanManagement = vlan;
                    log.info("VLAN Management matched: {}", vlan.getVlanName());
                    break; // Early exit
                }
            }
        } catch (Exception ex) {
            log.error("Error verifying VLAN: {}, Exception: {}", request.getAttributeValue("User-Name"), ex.getMessage(), ex);
        }

        return vlanManagement;
    }


    public List<VLANManagement> filterAndSortVlan(List<VLANManagement> vlanManagements, List<VlanProfileMapping> vlanProfileMappings, RadiusPacket request) {
        if (vlanManagements == null || vlanManagements.isEmpty()) {
            return Collections.emptyList(); // Early exit for empty input
        }

        List<VLANManagement> vlanManagementList = new ArrayList<>();

        // Convert mappings into a lookup map for O(1) access
        Map<String, String> vlanAttributeMap = vlanProfileMappings.stream()
                .collect(Collectors.toMap(
                        VlanProfileMapping::getAttribute,
                        mapping -> request.getAttributeValue(mapping.getAttribute()), // Use a lambda instead of a method reference
                        (existing, replacement) -> existing // Handle duplicate keys by keeping the first occurrence
                ));
        int i = 1;
        for (VLANManagement vlan : vlanManagements) {
            try {
                boolean allMatch = vlanProfileMappings.stream()
                        .allMatch(mapping -> {
                            String vlanValue = vlanAttributeMap.get(mapping.getAttribute());
                            String columnValue = getColumnValue(vlan, mapping.getColoumn());

                            return (vlanValue == null || vlanValue.isEmpty()) || (columnValue != null && vlanValue.contains(columnValue));
                        });
                i++;
                if (allMatch) {
                    vlanManagementList.add(vlan);
                }
            } catch (Exception e) {
                log.error("Error filtering VLAN: {}", e.getMessage(), e);
            }
        }
        log.info("Vlan no: " + i);
        return vlanManagementList;
    }

    // Optimized method to fetch column values (no reflection)
    private String getColumnValue(VLANManagement vlan, String columnName) {
        switch (columnName) {
            case "vlanId":
                return String.valueOf(vlan.getVlanId());
            case "vlanName":
                return vlan.getVlanName();
            case "nasType":
                return vlan.getNasType();
            case "circuitType":
                return vlan.getCircuitType();
            case "nasIdentifier":
                return vlan.getNasIdentifier();
            case "nasPortId1":
                return vlan.getNasPortId1();
            case "nasPortId2":
                return vlan.getNasPortId2();
            case "nasPortId3":
                return vlan.getNasPortId3();
            case "nasPortId4":
                return vlan.getNasPortId4();
            case "nasPortId5":
                return vlan.getNasPortId5();
            case "callingStationId":
                return vlan.getCallingStationId();
            case "contextName":
                return vlan.getContextName();
            case "filterId":
                return vlan.getFilterId();
            case "forwardPolicy":
                return vlan.getForwardPolicy();
            case "httpRedirectProfileName":
                return vlan.getHttpRedirectProfileName();
            case "rateLimitRate":
                return vlan.getRateLimitRate();
            case "rateLimitBurst":
                return vlan.getRateLimitBurst();
            case "qosPolicingPolicyName":
                return vlan.getQosPolicingPolicyName();
            case "qosMeteringPolicyName":
                return vlan.getQosMeteringPolicyName();
            case "pppoeUrl":
                return vlan.getPppoeUrl();
            case "pppDnsPrimary":
                return vlan.getPppDnsPrimary();
            case "pppDnsSecondary":
                return vlan.getPppDnsSecondary();
            case "pppNbnsPrimary":
                return vlan.getPppNbnsPrimary();
            case "sessionTimeOut":
                return vlan.getSessionTimeOut();
            case "idleTimeOut":
                return vlan.getIdleTimeOut();
            case "framedIpAddress":
                return vlan.getFramedIpAddress();
            case "rbDhcpMaxLeases":
                return vlan.getRbDhcpMaxLeases();
            case "ipAddressPoolName":
                return vlan.getIpAddressPoolName();
            case "natProfileName":
                return vlan.getNatProfileName();
            case "rbInterfaceName":
                return vlan.getRbInterfaceName();
            case "httpRedirectUrl":
                return vlan.getHttpRedirectUrl();
            case "framedIpv6Prefix":
                return vlan.getFramedIpv6Prefix();
            case "delegatedIpv6Prefix":
                return vlan.getDelegatedIpv6Prefix();
            case "framedInterfaceId":
                return vlan.getFramedInterfaceId();
            case "framedIpv6Pool":
                return vlan.getFramedIpv6Pool();
            case "ipv6Option":
                return vlan.getIpv6Option();
            case "ipv6Dns":
                return vlan.getIpv6Dns();
            case "delegatedMaxPrefix":
                return vlan.getDelegatedMaxPrefix();
            case "delegatedIpv6Pool":
                return vlan.getDelegatedIpv6Pool();
            case "subProfile":
                return vlan.getSubProfile();
            case "priority":
                return String.valueOf(vlan.getPriority());
            case "mvnoId":
                return String.valueOf(vlan.getMvnoId());
            case "createdOn":
                return String.valueOf(vlan.getCreatedOn());
            case "lastModifiedOn":
                return String.valueOf(vlan.getLastModifiedOn());
            case "RADIUS_ATTRIBUTE_GROUP_ID":
                return vlan.getRADIUS_ATTRIBUTE_GROUP_ID();
            case "lastAuthMatched":
                return String.valueOf(vlan.getLastAuthMatched());
            default:
                return null;
        }
    }


    public List<VLANManagement> filterAndSortVlan1(List<VLANManagement> vlanManagements, List<VlanProfileMapping> vlanProfileMappings, RadiusPacket request) {
        List<VLANManagement> vlanManagementList = new ArrayList<>();

        for (VLANManagement vlan : vlanManagements) {
            boolean allMatch = true;  // Flag to track if all mappings match

            try {
                for (VlanProfileMapping vlanProfileMapping : vlanProfileMappings) {
                    String vlanAttribute = vlanProfileMapping.getAttribute();
                    String vlanValue = request.getAttributeValue(vlanAttribute);
                    String columnName = vlanProfileMapping.getColoumn();

                    // Use reflection to get the method corresponding to the column name
                    Method method = VLANManagement.class.getMethod("get" + capitalize(columnName));
                    String columnValue = (String) method.invoke(vlan);

//                    log.info(String.format("Vlan attribute: %s, columnValue: %s, value: %s", vlanAttribute, columnValue, vlanValue));

                    // If the columnValue is null or doesn't contain the vlanValue, it's not a match
                    if ((vlanValue != null && vlanValue.length() > 0) && (columnValue == null || !vlanValue.contains(columnValue))) {
                        allMatch = false;
                        break;  // Break inner loop since it's not a match
                    }
                }

                // If all the vlanProfileMappings match, add to vlanManagementList
                if (allMatch) {
                    vlanManagementList.add(vlan);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        return vlanManagementList;
    }

    // Helper method to capitalize the first letter of the column name (to match getter naming convention)
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public boolean isListVLANValidationMappingMatchedFORimprove(VLANManagement vlanManagement, RadiusAttribute request) throws Exception {
        List<VLANValidationMapping> mappingList = vlanManagement.getMappingList();
        for (VLANValidationMapping vlanValidationMapping : mappingList) {
            String regex = vlanValidationMapping.getRegex();
            Pattern compile = Pattern.compile(regex);
            Matcher matcher = compile.matcher(request.getAttributeValue());
            if(matcher.matches()){
                return true;
            }
        }
        return false;
    }

    public boolean isListVLANValidationMappingMatched(VLANManagement vlanManagement, RadiusPacket request, CustomerData customerData, DBAuthenticationDriver dbAuthenticationDriver) throws Exception {
        List<VLANValidationMapping> mappingList = vlanManagement.getMappingList();//dbAuthenticationDriver.getVlanValidationMapping(vlanManagement.getVlanId().toString());
//        log.info("Vlan validation list count: " + mappingList.size());
        boolean response = false;
        ValidateExpression validate = new ValidateExpression();
        for (VLANValidationMapping vlanValidationMapping : mappingList) {
            log.info("Vlan Regex value: " + vlanValidationMapping.getRegexValue());
            String attribute = vlanValidationMapping.getRadiusAttribute();
            String regex = vlanValidationMapping.getRegex();    //{^(?:\S+\s+){3}key\s+\d+:\d+,{PROFILE{vlan.NAS_PORT_ID_2}}
            if (regex.startsWith("REGEX")) {
                String value = request.getAttribute(attribute).getAttributeValue();
                log.info("Checking vlan management : " + vlanManagement.getVlanName(), " for value: " + value + " and Regex: " + regex);
                regex = ExpressionEvaluator.parseExpression(regex, "REGEX\\{(.*)\\}");
                String[] expPair = regex.split(",");
                regex = expPair[0];
                String key = vlanValidationMapping.getRegexValue();//ExpressionEvaluator.getValueFromGivenExpression(key, customerData, request);
                value = ExpressionEvaluator.parseExpression(value, regex);
                if (value != null && key.equalsIgnoreCase(value)) {
                    log.info("Vlan Regex expression matched for attribute: " + attribute + " value: " + key + " key: " + value);
                    response = true;
                } else {
                    log.info("Vlan Regex expression Not matched for attribute: " + attribute + " value: " + key + " key: " + value);
                    return false;
                }
            } else {
//                regex = ExpressionEvaluator.getValueFromGivenExpression(regex, customerData, request);
                // CONTAINS{REQ{User-Name},REGEX{\b(?:\d{1,3}\.){3}\d{1,3}(?:@[a-zA-Z0-9_]+)?\b}}
                String exp = vlanValidationMapping.getRegexValue();//"CONTAINS{REQ{" + attribute + "}," + regex + "}";
                log.info("Vlan validation final expression: " + exp + " regex: " + regex + " for value: " + request.getAttribute(attribute));
                boolean isValid = validate.checkExpression(exp, request, customerData);
                // if exprission match then
                if (isValid) {
                    response = isValid;
                } else {
                    return false;
                }
            }


        }
        return response;
    }


    public CustomerData validateDynamicAttribute(Client cltData, List<DynamicAttributeMapping> dynamicAttributeMappingList, CustomerData custReturnData, RadiusPacket request, RadiusPacket accessResponse, String eventName) {
        try {
            if (dynamicAttributeMappingList != null) {
                for (int i = 0; i < dynamicAttributeMappingList.size(); i++) {
                    DynamicAttributeMapping dynamicAttributeMapping = dynamicAttributeMappingList.get(i);
                    String strCustomer = dynamicAttributeMapping.getCustomerAttribute();
                    String strRadius = dynamicAttributeMapping.getRadiusAttribute();
                    String strValueData = getAttributeValueFromRequest(strRadius, request);

                    if (strValueData != null) {
                        log.debug("Authorization Validating:" + strRadius + ":value:" + strValueData);
                        boolean isAbsenceAccepted = dynamicAttributeMapping.getIsAbsenceAccepted();

                        switch (strCustomer != null ? strCustomer.toLowerCase() : "") {
                            case "vlanid":
                                log.debug("Customer:" + custReturnData.getvLanId() + ":Radius:" + strValueData);
                                if ((custReturnData.getvLanId() == null || custReturnData.getvLanId().isEmpty()) && isAbsenceAccepted) {
                                    // null and null value accepted then skip
                                } else if ((custReturnData.getvLanId() == null || custReturnData.getvLanId().isEmpty()) || !custReturnData.getvLanId().equalsIgnoreCase(strValueData)) {
                                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                    custReturnData.setAuthStatus(false);
                                    custReturnData.setStrReplyMessage(strCustomer + " Validation Fail");
                                }
                                break;

                            case "nasportid":
                                String nasPortId = custReturnData.getNasPortidValidate();
                                log.debug("Customer:" + nasPortId + ":Radius:" + strValueData);
                                if (custReturnData.getNasPortidValidate() != null && nasPortId.contains("*")) {
                                    if (custReturnData.getNasPortidValidate() == null || nasPortId.isEmpty() && isAbsenceAccepted) {
                                        // null and null value accepted then skip
                                    } else {
                                        nasPortId = nasPortId.replaceAll("\\*", "");
                                        List<String> list = Arrays.asList(nasPortId.split(","));
                                        boolean matchFound = false;
                                        for (String str : list) {
                                            if (str != null && !str.isEmpty() && (str.contains(strValueData) || strValueData.contains(str))) {
                                                matchFound = true;
                                                break; // Exit loop as soon as a match is found
                                            }
                                        }
                                        if (!matchFound) {
                                            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                            custReturnData.setAuthStatus(false);
                                            custReturnData.setStrReplyMessage(strCustomer + " Validation Fail");
                                        }
                                    }
                                } else if ((nasPortId == null || nasPortId.isEmpty()) && isAbsenceAccepted) {
                                    // null and null value accepted then skip
                                } else if ((nasPortId == null || nasPortId.isEmpty()) || !nasPortId.equalsIgnoreCase(strValueData)) {
                                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                    custReturnData.setAuthStatus(false);
                                    custReturnData.setStrReplyMessage(strCustomer + " Validation Fail");
                                }
                                break;

                            case "nasip":
                                log.debug("Customer:" + custReturnData.getNasIpValidate() + ":Radius:" + strValueData);
                                if ((custReturnData.getNasIpValidate() == null || custReturnData.getNasIpValidate().isEmpty()) && isAbsenceAccepted) {
                                    // null and null value accepted then skip
                                } else if ((custReturnData.getNasIpValidate() == null || custReturnData.getNasIpValidate().isEmpty()) || !custReturnData.getNasIpValidate().equalsIgnoreCase(strValueData)) {
                                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                    custReturnData.setAuthStatus(false);
                                    custReturnData.setStrReplyMessage(strCustomer + " Validation Fail");
                                }
                                break;

                            case "framedip":
                                log.debug("Customer:" + custReturnData.getFramedIpValidate() + ":Radius:" + strValueData);
                                if ((custReturnData.getFramedIpValidate() == null || custReturnData.getFramedIpValidate().isEmpty()) && isAbsenceAccepted) {
                                    // null and null value accepted then skip
                                } else if ((custReturnData.getFramedIpValidate() == null || custReturnData.getFramedIpValidate().isEmpty()) || !custReturnData.getFramedIpValidate().equalsIgnoreCase(strValueData)) {
                                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                    custReturnData.setAuthStatus(false);
                                    custReturnData.setStrReplyMessage(strCustomer + " Validation Fail");
                                }
                                break;

                            case "framedip6":
                                log.debug("Customer:" + custReturnData.getFramedIp6Validate() + ":Radius:" + strValueData);
                                if ((custReturnData.getFramedIp6Validate() == null || custReturnData.getFramedIp6Validate() == null) && custReturnData.getFramedIp6Validate().isEmpty() && isAbsenceAccepted) {
                                    // null and null value accepted then skip
                                } else if (custReturnData.getFramedIp6Validate().isEmpty() || !custReturnData.getFramedIp6Validate().equalsIgnoreCase(strValueData)) {
                                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                    custReturnData.setAuthStatus(false);
                                    custReturnData.setStrReplyMessage(strCustomer + " Validation Fail");
                                }
                                break;

                            case "macaddress":
                                log.debug("Customer:" + custReturnData.getFramedIp6Validate() + ":Radius:" + strValueData);
                                boolean isExists = checkMacMappingExistWithcustomer(custReturnData.getCustid(), cltData.getMvnoId(), strValueData);
                                if (!isExists && isAbsenceAccepted) {
                                    // null and null value accepted then skip
                                } else if (!isExists) {
                                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                    custReturnData.setAuthStatus(false);
                                    custReturnData.setStrReplyMessage(strCustomer + " Validation Fail");
                                }
                                break;

                            default:
                                log.debug("No matching attribute for customer: " + strCustomer);
                                break;
                        }
                    } else {
                        log.debug("Attribute:" + strRadius + ":Not Found:");
                    }
                }
                if (custReturnData.getStrReplyMessage().contains(" Validation Fail")) {
                    eventName = CommonConstants.AuthResponseEvent.DYNAMIC_VALIDATION_FAIL;
                }
            } else {
                log.debug("No Authorization Attribute Configured");
            }
        } catch (Exception ex) {

        }
        return custReturnData;
    }

    public String getAttributeValueFromRequest(String dynaAttribute, RadiusPacket request) {
        String attributeValue = null;
        try {
            log.debug("Looking for " + dynaAttribute + " attribute from request");
            RadiusAttribute attribute = request.getAttribute(dynaAttribute);
            if (attribute != null) {
                attributeValue = attribute.getAttributeValue();
                log.debug("Attribute value is : " + attributeValue);
            } else {
                log.debug("Attribute not found in the request.");
            }

        } catch (Exception e) {
            log.error("Unable to get attribute, Reason: " + e.getMessage());
            e.printStackTrace();
        }
        return attributeValue;
    }

    public void sendSNMP(String strSessionid, SNMPClientProfile snmpClientProfile) throws IOException {
        String targetAddress = snmpClientProfile.getDestinationIp();
        String port = snmpClientProfile.getDestinationPort();
        //  String community = "nmp_community";
        String community = snmpClientProfile.getCommunityString();

        //String oidString = "1.3.6.1.4.1.2352.2.27.1.1.3.9.0"; // sysName
        String oidString = snmpClientProfile.getBaseOid();
        Variable oidValue = new Gauge32(Integer.parseInt(snmpClientProfile.getBaseValue()));

        //String oidStringNew = "1.3.6.1.4.1.2352.2.27.1.1.3.4.0";
        String oidStringNew = snmpClientProfile.getNewOid();
        String oidValueNew = strSessionid;

        // Create SNMP object
        Snmp snmp = new Snmp(new DefaultUdpTransportMapping());

        // Set community for SNMPv1/v2c
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(new UdpAddress(targetAddress + "/" + port));
        target.setVersion(1);

        // Create PDU for SET request
        PDU pdu = new PDU();
        pdu.setType(PDU.SET);

        // Add OID and new value to PDUs
        pdu.add(new VariableBinding(new OID(oidString), oidValue));
        pdu.add(new VariableBinding(new OID(oidStringNew), new OctetString(oidValueNew)));

        // Send SET request
        snmp.send(pdu, target, null, null);

        System.out.println("Sent SET request for OID: " + oidString + " with new value: " + oidStringNew);
        log.warn("Sent SET request for OID: " + oidString + " with new value: " + oidStringNew);

        // Close connection
        snmp.close();
    }

    public String normalizeMacAddress(String mac) {
        if (mac != null)
            return mac.replaceAll("[-:.]", "").toLowerCase();
        else
            return mac;
    }
}
