package com.savbill.radius.aaa.db;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerPlanData;
import com.savbill.radius.aaa.data.PlanQosPolicyMapping;
import com.savbill.radius.aaa.data.TimepolicyData;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.server.RadiusUtility;
import com.savbill.radius.config.DbConfig;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.entity.QOSPolicyGatewayMapping;
import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.entity.VLANValidationMapping;
import com.savbill.radius.utils.CommonConstants;
import com.savbill.radius.utils.RadiusUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class DBAuthenticationDriver {

    public static String dbUrl;
    public static String dbUserName;
    public static String dbPassword;

    private static final String SQL_EXCEPTION = "SQLException";

    private static final String RESULT_SET_IS = "Result Set is ";

    private static String strRadiusCustomerQuery = "select username,mvnoid,password,custid,cstatus,nas_port_id,vlanid,framed_ipv6_address,framed_ip,nas_ip_address,ip_pool_name_bind,framed_ip_bind,parentcustid,maxconcurrentsession,framedroute,delegatedprefix,mac_auth_enable,mac_provision,bngrouterinterface,vrfname,WANIP,WANIPV6,LLConnectionType,LLExpiryDate,BNGRouterName,gatewayip,ip_pool_name_bind,framed_ip_bind,framed_ip,lanip,framed_ip_netmask, framed_ipv6_prefix, primary_dns, primary_ipv6_dns, secondary_ipv6_dns, secondary_dns,macretentionperiod,macretentionunit " +
            "from tblcustomers cp where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and username=? and mvnoid=?";

    private static String strRadiusCustomerWithTerminateStatusQuery = "select username,mvnoid,password,custid,cstatus,nas_port_id,vlanid,framed_ipv6_address,framed_ip,nas_ip_address,ip_pool_name_bind,framed_ip_bind,parentcustid,maxconcurrentsession,framedroute,delegatedprefix,mac_auth_enable,mac_provision,bngrouterinterface,vrfname,WANIP,WANIPV6,LLConnectionType,LLExpiryDate,BNGRouterName,gatewayip,ip_pool_name_bind,framed_ip_bind,framed_ip,lanip,framed_ip_netmask, framed_ipv6_prefix, primary_dns, primary_ipv6_dns, secondary_ipv6_dns, secondary_dns,macretentionperiod,macretentionunit " +
            "from tblcustomers cp where cp.is_deleted = 0 and username=? and mvnoid=?";
    private static String strRadiusCustomerWithoutMvnoQuery = "select username,mvnoid,password,custid,cstatus,nas_port_id,vlanid,framed_ipv6_address,lanip,framed_ip,nas_ip_address,ip_pool_name_bind,framed_ip_bind,parentcustid,maxconcurrentsession,framedroute,delegatedprefix,mac_auth_enable,mac_provision,bngrouterinterface,vrfname,WANIP,WANIPV6,LLConnectionType,LLExpiryDate,BNGRouterName,gatewayip,ip_pool_name_bind,framed_ip_bind,framed_ip,framed_ip_netmask, framed_ipv6_prefix, primary_dns, primary_ipv6_dns, secondary_ipv6_dns, secondary_dns,macretentionperiod,macretentionunit " +
            "from tblcustomers cp where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and username=?";
    private static String strRadiusCustomerWithoutMvnoWithTerminateStatusQuery = "select username,mvnoid,password,custid,lanip,cstatus,nas_port_id,vlanid,framed_ipv6_address,framed_ip,nas_ip_address,ip_pool_name_bind,framed_ip_bind,parentcustid,maxconcurrentsession,framedroute,delegatedprefix,mac_auth_enable,mac_provision,bngrouterinterface,vrfname,WANIP,WANIPV6,LLConnectionType,LLExpiryDate,BNGRouterName,gatewayip,ip_pool_name_bind,framed_ip_bind,framed_ip,framed_ip_netmask, framed_ipv6_prefix, primary_dns, primary_ipv6_dns, secondary_ipv6_dns, secondary_dns,macretentionperiod,macretentionunit " +
            "from tblcustomers cp where cp.is_deleted = 0 and username=?";

    private static String strRadiusParentCustomerByLocationQuery = "select t.custid from tblcustomers t, tbltcustomerlocationmapping t2, tblcustquotadtls t3 where t.custid = t2.customerid and t3.parnet_quota_type = 'shareable' and t.is_deleted = 0 and t.MVNOID=? \n" +
            "and t2.is_parent_location=true and t2.mac=? limit 1;";
    private static String strRadiusChildCustomerByLocationQuery = "select t.* from tblcustomers t , tbltmacaddressmapping t2  where t.custid=t2.custid  and t2.macaddress=? and t.parentcustid=?";
    private static String strRadiusParentCustomerQuery = "select t.* from tblcustomers t ,tblcustquotadtls t2 where t.custid=? and t2.parnet_quota_type = 'shareable' and t.is_deleted = 0 and t.MVNOID=?";
    private static String strRadiusParentCustomerWithoutMvnoQuery = "select t.* from tblcustomers t ,tblcustquotadtls t2 where t.custid=? and t2.parnet_quota_type = 'shareable' and t.is_deleted = 0";

    private static String strRadiusCustomerCustidQuery = "select * from tblcustomers cp where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and custid=? and mvnoid=?";
    private static String strRadiusCustomerCustidWithTerminateQuery = "select * from tblcustomers cp where cp.is_deleted = 0 and custid=? and mvnoid=?";
    private static String strRadiusCustomerCustidWithoutMvnoQuery = "select * from tblcustomers cp where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and custid=?";
    private static String strRadiusCustomerCustidWithoutMvnoWithTerminateStatusQuery = "select * from tblcustomers cp where cp.is_deleted = 0 and custid=?";

    private static String strRadiusCustomerPlanQuery = "SELECT \n" +
            "    cpp.startdate, cpp.enddate, cpp.custpackageid,cpp.purchase_type, cpp.custid,\n" +
            "    cqu.totalquota, cqu.usedquota, cqu.currentsessionusagevolume, cqu.currentsessionusagetime, cqu.quotatype, cqu.quotaunit,\n" +
            "    pp.name as plan_name, pp.plantype, pp.plangroup, pp.postpaidplanid, pp.maxconcurrentsession, cqu.usage_quota_type,\n" +
            "    qp.baseparam1, qp.baseparam2, qp.baseparam3, qp.thparam1, qp.thparam2, qp.thparam3,\n" +
            "    qp.basepolicyname, qp.thpolicyname, pp.timebasepolicyid, pp.allowoverusage,\n" +
            "    qp.id AS qos_policy_id, qp.qosspeed,\n" +
            "    cqu.quotadtlsid, cqu.timequotaunit, cqu.timequotaused, cqu.timetotalquota,\n" +
            "    cqu.is_chunk_available, cqu.reserved_quota_in_per, cqu.total_reserved_quota,\n" +
            "    cpp.cust_plan_status, qp.type, cqu.isquotaupdateskipped, cpp.istriggercoadm, cpp.onquotaexhausteventname, pp.addon_to_base \n" +
            "FROM \n" +
            "    tblcustpackagerel cpp\n" +
            "INNER JOIN \n" +
            "    tblcustquotadtls cqu ON cpp.custid = cqu.custid AND cpp.planid = cqu.planid AND cpp.custpackageid = cqu.custpackageid\n" +
            "INNER JOIN \n" +
            "    tblmpostpaidplan pp ON cpp.planid = pp.postpaidplanid\n" +
            "LEFT JOIN \n" +
            "    tbl_qos_policy qp ON pp.qospolicy_id = qp.id\n" +
            "WHERE cpp.custid = ? AND NOW() BETWEEN cpp.startdate AND cpp.enddate";


    private static String strRadiusCustomerDetailsQueryByCprId = "SELECT t.username,t.mvnoid,t.password,t.custid,t.cstatus,t.nas_port_id,t.vlanid,t.framed_ipv6_address,t.framed_ip,t.nas_ip_address,t.ip_pool_name_bind,t.framed_ip_bind,\n" +
            "t.parentcustid,t.maxconcurrentsession,t.framedroute,t.delegatedprefix,t.mac_auth_enable,t.mac_provision,t.bngrouterinterface,t.vrfname,t.WANIP,t.WANIPV6,t.LLConnectionType,\n" +
            "t.LLExpiryDate,t.BNGRouterName,t.gatewayip,t.ip_pool_name_bind,t.framed_ip_bind,t.framed_ip,t.lanip,t.framed_ip_netmask, t.framed_ipv6_prefix, t.primary_dns, t.secondary_dns,t.secondary_ipv6_dns,t.primary_ipv6_dns,t.macretentionunit,t.macretentionperiod,\n" +
            "cpp.startdate, cpp.enddate, cpp.custpackageid, cpp.custid,\n" +
            "            cqu.totalquota, cqu.usedquota, cqu.currentsessionusagevolume, cqu.currentsessionusagetime, cqu.quotatype, cqu.quotaunit,\n" +
            "            pp.name as plan_name, pp.plantype, pp.plangroup, pp.postpaidplanid, pp.maxconcurrentsession, cqu.usage_quota_type,cpp.purchase_type,\n" +
            "            qp.baseparam1, qp.baseparam2, qp.baseparam3, qp.thparam1, qp.thparam2, qp.thparam3,\n" +
            "            qp.basepolicyname, qp.thpolicyname, pp.timebasepolicyid, pp.allowoverusage,\n" +
            "            qp.id AS qos_policy_id, qp.qosspeed,\n" +
            "            cqu.quotadtlsid, cqu.timequotaunit, cqu.timequotaused, cqu.timetotalquota,\n" +
            "            cqu.is_chunk_available, cqu.reserved_quota_in_per, cqu.total_reserved_quota,\n" +
            "            cpp.cust_plan_status, qp.type, cqu.isquotaupdateskipped, cpp.istriggercoadm, cpp.onquotaexhausteventname \n" +
            "            FROM \n" +
            "            tblcustpackagerel cpp\n" +
            "            inner join\n" +
            "            tblcustomers t on cpp.custid = t.custid\n" +
            "            INNER JOIN \n" +
            "            tblcustquotadtls cqu ON cpp.custid = cqu.custid AND cpp.planid = cqu.planid AND cpp.custpackageid = cqu.custpackageid\n" +
            "            INNER JOIN \n" +
            "            tblmpostpaidplan pp ON cpp.planid = pp.postpaidplanid\n" +
            "            LEFT JOIN \n" +
            "            tbl_qos_policy qp ON pp.qospolicy_id = qp.id\n" +
            "            WHERE cpp.custpackageid = ?";

    private static String strRadiusCustomerQoS = "select name,download_speed,upload_speed,base_download_speed,base_upload_speed,throttle_download_speed,throttle_upload_speed from tbltqospolicy_gateway_mapping where qos_policy_id=?";
    private static String strRadiusPlanQos = "select planid,from_percentage,to_percentage,qosid from tbltplanqosmapping where planid=? and isdelete=false";  //and isdelete=false or isdelete is null

    private static String strRadiusCustomerMACProvsion = "insert into tbltmacaddressmapping(custid,macaddress,createdate,lastmodificationdate,macretentiondate,normalizemac) values(?,?,?,?,?,?)";

    private static String strRadiusCustomer_MACUpdate = "update tbltmacaddressmapping set custid=?, macretentiondate=? where macaddress=?";
    private static String strRadiusCustomerMac_MACUpdate = "update tbltmacaddressmapping set custid=?, macretentiondate=?, macaddress=?,normalizemac=? where macaddress=?";

    private static String strRadiusCustomerMac_MACCount = "select count(*) from tbltmacaddressmapping where custid=? and normalizemac=?";
    private static String strRadiusCustPlanStatus = "update tblcustpackagerel set enddate=?, expirydate=?,onquotaexhausteventname=null WHERE custpackageid=?";

    private static String strUpdateFaultyMacLastConnected = "update tbltfaultymac set lastconnected=? WHERE id=?";

    private static String strRadiusCustPlanCOADMFlag = "update tblcustpackagerel set istriggercoadm=?,onquotaexhausteventname='' WHERE custpackageid=?";

    private static String strRadiusCustomerMacQuery =
            "SELECT * FROM tblcustomers cp, tbltmacaddressmapping mac " +
                    "WHERE cp.cstatus != 'Terminate' AND cp.is_deleted = 0 " +
                    "AND mac.custid = cp.custid " +
                    "AND mac.normalizemac = ? " +
                    "AND mvnoid = ? " +
                    "ORDER BY cstatus";


    private static String strRadiusCustomerIPQuery = "select * from tblcustomers cp,tblcustipmapping ip where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and "
            + "ip.custid=cp.custid and ip.ip_address=? and mvnoid=? order by cstatus";

    private static String strRadiusCustomerUserMacQuery = "select * from tblcustomers cp,tbltmacaddressmapping mac where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and "
            + "mac.custid=cp.custid and username=? and mac.normalizemac=? and mvnoid=? order by cstatus";

    private static String strRadiusCustomerUserIPQuery = "select * from tblcustomers cp,tblcustipmapping ip where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and "
            + "ip.custid=cp.custid and username=? and ip.ip_address=? and mvnoid=? order by cstatus";

    private static String strRadiusCustomerMacIPQuery = "select * from tblcustomers cp,tbltmacaddressmapping mac,tblcustipmapping ip where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and "
            + "mac.custid=cp.custid and ip.custid=cp.custid and mac.normalizemac=? and ip.ip_address=? and mvnoid=? order by cstatus";

    private static String strRadiusCustomerUserMacIPQuery = "select * from tblcustomers cp,tbltmacaddressmapping mac,tblcustipmapping ip where cp.cstatus != 'Terminate' and cp.is_deleted = 0 and "
            + "mac.custid=cp.custid and ip.custid=cp.custid and username=? and mac.normalizemac=? and ip.ip_address=? and mvnoid=? order by cstatus";

    private static String strRadiusCustomerInsertQuery = "INSERT INTO tblcustomers (custid, title, username, password, custname, contactperson, cafno, is_deleted, partnerid,firstname,cstatus,failcount,customertype,MVNOID) VALUES (?,?,?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static String strRadiusCustomerCountQuery = "SELECT MAX(custid) + 1 FROM tblcustomers";

    private static String strRadiusCustomerMACCountQuery = "select mac.macaddress,mac.createdate  from tbltmacaddressmapping mac,tblcustomers cust where cust.custid=mac.custid and cust.custid=?";

    private static String strRadiusMACCountQuery = "select count(mac.macaddress) macaddress from tbltmacaddressmapping mac where mac.normalizemac=?";

    private static String strRadiusCustomerMACDelete = "delete from tbltmacaddressmapping WHERE custid= ? and macaddress = ?";

    private static String strRadiusReservedQuota = "select * from tblreservedquotadtls t where cust_id=?";

    private static String strGetLiveUsersession = "select * from tbltliveuser where AcctSessionId=? and AcctMultiSessionId=? and NASIPAddress=?";

    private static String strFetchTotalQuotaFromLiveUSerByCustId = "select SUM(AcctInputOctets) as totalInput, sum(AcctOutputOctets) as totalOutPut, sum(AcctSessionTime) as totalTime  from tbltliveuser t where custid = ?";


    private static String strCustomerExistsByUserNameAndMvno = "select count(username) username from tblcustomers t where username=? and MVNOID=?";

    private static String strRadiusCustomerMacCountQuery = "select count(custid) custid from tbltmacaddressmapping t where t.custid=? AND macaddress=?";

    private static String strRadiusVlanManagementQuery = "select * from tblmvlanmanagement v where v.mvnoid=? order by PRIORITY desc ";
    private static String strRadiusVlanValidationMappingQuery = "select RADIUS_ATTRIBUTE, REGEX  from tblmvlanvalidationmapping t where t.VLANID=?";

    private static String strRadiusUpdateMacMappingLastUsageDate = "update TBLTMACADDRESSMAPPING set macretentiondate=? WHERE custid=? and macAddress=?";

    private static String strRadiusCOAResponseAudit = "INSERT INTO tblmcoaresponse (nasipaddress, coapacket, coaresponse, reason, createdate, mvnoid , coaresponsemessage) VALUES (?, ?, ?, ?, ?, ? , ?)";

    private static String strQoSGatewayPattern = "select name,download_speed,upload_speed,base_download_speed,base_upload_speed,throttle_download_speed,throttle_upload_speed from tbltqospolicy_gateway_pattern";

    private static Map<String, QOSPolicyGatewayMapping> gatewayMap_pattern = null;

    private static final Logger log = LoggerFactory.getLogger(DBAuthenticationDriver.class);

    private Timestamp currentDate = (new Timestamp(new Date().getTime()));

    public CustomerData authenticateCustomer(CustomerData custPassedData, int mvnoid, AccessRequest request, boolean isPasswordCheck, String acctStatusValue) throws SQLException {
        CustomerData custDBData = new CustomerData();
        custDBData.setAuthStatus(false);
//		custDBData=getDBCustomer(custPassedData.getUsername(),mvnoid,null,null, false);
        custDBData = getDBCustomer(custPassedData.getUsername(), mvnoid, null, null, false, acctStatusValue, false);
        if (custDBData == null) {
            custDBData = new CustomerData();
            custDBData.setAuthStatus(false);
            custDBData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
            return custDBData;
        }
        if (isPasswordCheck) {
            custDBData.setPasswordcheck("Y");
        } else {
            custDBData.setPasswordcheck("N");
        }
        return validateReturnCustomer(custDBData, custPassedData);
    }

    public CustomerData authenticateCustomerAll(CustomerData custPassedData, String strIdentity, int mvnoid, String strType, String mac, String ip, boolean isPasswordCheck, String acctStatusValue) throws SQLException {
        CustomerData custDBData = new CustomerData();
        custDBData.setAuthStatus(false);
        custDBData = getDBAllCustomerAuthenticate(strIdentity, mvnoid, strType, mac, ip, acctStatusValue);
        if (custDBData == null) {
            custDBData = new CustomerData();
            custDBData.setAuthStatus(false);
            custDBData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
            return custDBData;
        }
        if (isPasswordCheck) {
            custDBData.setPasswordcheck("Y");
        } else {
            custDBData.setPasswordcheck("N");
        }
        //ANG-11452: In Mac based authentication we are not checking password
        if ("mac".equalsIgnoreCase(strType))
            custDBData.setPasswordcheck("N");
        return validateReturnCustomer(custDBData, custPassedData);
    }


    public CustomerData validateReturnCustomer(CustomerData custDBData, CustomerData custPassedData) {
        try {
            String authenticationType = RadiusUtils.readValueFromProperties("radius.authentication.type");
            if (authenticationType == null) {
                authenticationType = CommonConstants.AUTHENTICATION_TYPE_DEPENDENT;
            }
            if (custDBData == null) {
                custDBData = new CustomerData();
                custDBData.setAuthStatus(false);
                custDBData.setStrReplyMessage(AAAConstant.REPLYMSG_USERNOTFOUND);
                return custDBData;
            } else {
                boolean validation = true;
                log.debug("Customer Password Check:" + custDBData.getPasswordcheck());

                if (custDBData.getPasswordcheck() != null && "Y".equalsIgnoreCase(custDBData.getPasswordcheck())) {
                    if (custPassedData.getPassword().equalsIgnoreCase(custDBData.getPassword())) {
                        validation = true;
                    } else {
                        log.error("Customer Password Check Fail :" + custDBData.getPassword());
                        custDBData.setStrReplyMessage(AAAConstant.REPLYMSG_PASSWORDFAIL);
                        custDBData.setAuthStatus(false);
                        validation = false;
                        return custDBData;
                    }
                }

                if (CommonConstants.CUST_ACTIVE.equalsIgnoreCase(custDBData.getStatus())) {
                    boolean activeplan = false;
                    List<CustomerPlanData> activePlans = new ArrayList<CustomerPlanData>();

                    if (custDBData.getCustomerAllPlan() != null && custDBData.getCustomerAllPlan().size() > 0) {
                        for (int i = 0; i < custDBData.getCustomerAllPlan().size(); i++) {
                            CustomerPlanData custPlan = custDBData.getCustomerAllPlan().get(i);
                            if (custPlan.getCustPlanStatus().equalsIgnoreCase("Active")) {
                                log.info("Plan Selected CPR : " + custPlan.getCustpackageid() + ":Custid:" + custPlan.getCustid());
                                log.info("Customer Plan Data : " + custPlan.getCustpackageid() + ":VolumeCurrent:" + custPlan.getCurrentsessionusagevolume() + ":VolumeUnused:" + custPlan.getVolumebasedunusedquota());
                                activeplan = true;
                                activePlans.add(custPlan);
                            }
                        }
                        custDBData.setCustomerAllPlan(activePlans);
                    } else if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                        log.warn("No Plan In DB");
                        custDBData.setStrReplyMessage(AAAConstant.REPLYMSG_PLANEXPIRED);
                        custDBData.setAuthStatus(false);
                        custDBData.setStatus(CommonConstants.PLAN_INACTIVE);
                        validation = false;
                        return custDBData;
                    }

                    if (!activeplan && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                        log.debug("No Active Plan");
                        custDBData.setStrReplyMessage(AAAConstant.REPLYMSG_PLANEXPIRED);
                        custDBData.setAuthStatus(false);
                        custDBData.setStatus(CommonConstants.PLAN_INACTIVE);
                        validation = false;
                        return custDBData;
                    }
                    custDBData.setAuthStatus(true);
                    custDBData.setStrReplyMessage(AAAConstant.REPLYMSG);
                } else {
                    log.warn("User status is not active. So, skipping further plan validations for user: " + custDBData.getUsername());
                }
                return custDBData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            custDBData.setAuthStatus(false);
            custDBData.setStrReplyMessage(AAAConstant.REPLYMSG_UNKNOWN);
            return custDBData;
        }
    }


    public CustomerData getDBCustomer(String strUsername, int mvnoid, String custid, String strPacket, boolean isParent) throws SQLException {
        return getDBCustomer(strUsername, mvnoid, custid, strPacket, isParent, "Auth", false);
    }

    public CustomerData getDBCustomer(String strUsername, int mvnoid, String custid, String strPacket, boolean isParent, boolean isTerminateCustomer) throws SQLException {
        return getDBCustomer(strUsername, mvnoid, custid, strPacket, isParent, "Auth", isTerminateCustomer);
    }

    /**
     * Get customer data, available plans, available quota, quo speed
     *
     * @param strUsername
     * @param mvnoid
     * @param custid
     * @param strPacket
     * @return
     * @throws RuntimeException
     * @throws SQLException
     */
    public CustomerData getDBCustomer(String strUsername, int mvnoid, String custid, String strPacket, boolean isParent, String AcctStatusValue, boolean isTerminatedCustomer) throws RuntimeException, SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CUSTOMER FROM DB : %s :QUERY: %s", strUsername, strRadiusCustomerQuery));
        }

        CustomerData custDBData = null;
        RadiusUtility radUtil = new RadiusUtility();

        String query;
        if (isTerminatedCustomer) {
            if (mvnoid != 1) {
                if (custid != null) {
                    query = isParent ? strRadiusParentCustomerQuery : strRadiusCustomerCustidWithTerminateQuery;
                } else {
                    query = strRadiusCustomerWithTerminateStatusQuery;
                }
            } else {//WithoutMvno
                if (custid != null) {
                    query = isParent ? strRadiusParentCustomerWithoutMvnoQuery : strRadiusCustomerCustidWithoutMvnoWithTerminateStatusQuery;
                } else {
                    query = strRadiusCustomerWithoutMvnoWithTerminateStatusQuery;
                }
            }
        } else {
            if (mvnoid != 1) {
                if (custid != null) {
                    query = isParent ? strRadiusParentCustomerQuery : strRadiusCustomerCustidQuery;
                } else {
                    query = strRadiusCustomerQuery;
                }
            } else {//WithoutMvno
                if (custid != null) {
                    query = isParent ? strRadiusParentCustomerWithoutMvnoQuery : strRadiusCustomerCustidWithoutMvnoQuery;
                } else {
                    query = strRadiusCustomerWithoutMvnoQuery;
                }
            }
        }

        log.info("In Get customer data for AcctStatusValue: " + AcctStatusValue);
        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (custid != null) {
                stmt.setString(1, custid);
            } else {
                stmt.setString(1, strUsername);
            }
            if (mvnoid != 1)
                stmt.setLong(2, mvnoid);

            try (ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    custDBData = new CustomerData();
                    custDBData.setAuthStatus(true);
                    custDBData.setUsername(rset.getString("username"));
                    custDBData.setMvnoId(rset.getInt("mvnoid"));
                    custDBData.setPassword(rset.getString("password"));
                    custDBData.setCustid(rset.getInt("custid"));
                    custDBData.setStatus(rset.getString("cstatus"));
                    custDBData.setNasPortidValidate(rset.getString("nas_port_id"));
                    custDBData.setVlanidValidate(rset.getString("vlanid"));
                    custDBData.setFramedIp6Validate(rset.getString("framed_ipv6_address"));
                    custDBData.setFramedIpValidate(rset.getString("framed_ip"));
                    custDBData.setNasIpValidate(rset.getString("nas_ip_address"));
                    custDBData.setPasswordcheck("Y");
                    custDBData.setIppoolbind(rset.getString("ip_pool_name_bind"));
                    custDBData.setFrameipbind(rset.getString("framed_ip_bind"));
                    custDBData.setParentCustId(rset.getObject("parentcustid") != null ? rset.getInt("parentcustid") : 0);
                    custDBData.setMaxconcurrentsession(rset.getObject("maxconcurrentsession") != null ? rset.getInt("maxconcurrentsession") : null);
                    custDBData.setMacProvision(rset.getBoolean("mac_provision"));
                    custDBData.setMacAuthEnable(rset.getBoolean("mac_auth_enable"));
                    custDBData.setFramedroute(rset.getString("framedroute"));
                    custDBData.setDelegatedprefix(rset.getString("delegatedprefix"));
                    custDBData.setBngRouterInterface(rset.getString("bngrouterinterface"));
                    custDBData.setVrfName(rset.getString("vrfname"));
                    custDBData.setWanIP(rset.getString("WANIP"));
                    custDBData.setWanIPV6(rset.getString("WANIPV6"));
                    custDBData.setLlConnectionType(rset.getString("LLConnectionType"));
                    custDBData.setLlExpiryDate(rset.getString("LLExpiryDate"));
                    custDBData.setBngRouterName(rset.getString("BNGRouterName"));
                    custDBData.setGatewayip(rset.getString("gatewayip"));
                    custDBData.setIpPoolNameBind(rset.getString("ip_pool_name_bind"));
                    custDBData.setFramedIpBind(rset.getString("framed_ip_bind"));
                    custDBData.setFramedIp(rset.getString("framed_ip"));
                    custDBData.setLanIP(rset.getString("lanip"));
                    custDBData.setFramedIPNetmask(rset.getString("framed_ip_netmask"));
                    custDBData.setFramedIPv6Prefix(rset.getString("framed_ipv6_prefix"));
                    custDBData.setPrimaryDNS(rset.getString("primary_dns"));
                    custDBData.setPrimaryIPv6DNS(rset.getString("primary_ipv6_dns"));
                    custDBData.setSecondaryIPv6DNS(rset.getString("secondary_ipv6_dns"));
                    custDBData.setSecondaryDNS(rset.getString("secondary_dns"));

                    if (rset.getString("macretentionunit") != null) {
                        custDBData.setMacRetentionUnit(rset.getString("macretentionunit"));
                    }
                    if (rset.getObject("macretentionperiod") != null) {
                        custDBData.setMacRetentionPeriod(Integer.valueOf(rset.getString("macretentionperiod")));
                    }
                    if (CommonConstants.CUST_ACTIVE.equalsIgnoreCase(custDBData.getStatus())
                            || CommonConstants.CUST_SUSPEND.equalsIgnoreCase(custDBData.getStatus())
                            || CommonConstants.PLAN_INACTIVE.equalsIgnoreCase(custDBData.getStatus())) {
                        List<CustomerPlanData> listCustomerPlanData = getCustomerPlans(conn, custDBData, radUtil);
                        if (!listCustomerPlanData.isEmpty()) {
                            custDBData.setCustomerAllPlan(listCustomerPlanData);
                        }
                    } else {
                        log.debug("User status is not active. So, skipping retrieval of further details of user.");
                    }
                }
            }
            log.debug(":getDBCustomer:" + query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (custDBData != null &&
                (CommonConstants.CUST_ACTIVE.equalsIgnoreCase(custDBData.getStatus())
                        || CommonConstants.CUST_SUSPEND.equalsIgnoreCase(custDBData.getStatus())
                        || CommonConstants.PLAN_INACTIVE.equalsIgnoreCase(custDBData.getStatus()))) {
            try {
                getPlanDetailCustomer(custDBData, AcctStatusValue);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CUSTOMER FROM DB : %s :DATAIS: %s", strUsername, custDBData));
        }
        return custDBData;
    }

    private List<CustomerPlanData> getCustomerPlans(Connection conn, CustomerData custDBData, RadiusUtility radUtil) throws SQLException {
        List<CustomerPlanData> listCustomerPlanData = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(strRadiusCustomerPlanQuery)) {
            stmt.setInt(1, custDBData.getCustid());
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    CustomerPlanData cusPlantDBData = new CustomerPlanData();
                    cusPlantDBData.setStartdate(rset.getTimestamp("startdate"));
                    cusPlantDBData.setEnddate(rset.getTimestamp("enddate"));
                    cusPlantDBData.setVolumebasedtotalquota(rset.getDouble("totalquota"));
                    cusPlantDBData.setCurrentsessionusagevolume(rset.getDouble("currentsessionusagevolume"));
                    cusPlantDBData.setCurrentsessionusagetime(rset.getDouble("currentsessionusagetime"));
                    cusPlantDBData.setVolumebasedusedquota(rset.getDouble("usedquota"));
                    cusPlantDBData.setCustpackageid(rset.getInt("custpackageid"));
                    cusPlantDBData.setTimebasedtotalquota(rset.getDouble("timetotalquota"));
                    cusPlantDBData.setTimebasedusedquota(rset.getDouble("timequotaused"));
                    cusPlantDBData.setBaseparam1(rset.getString("baseparam1"));
                    cusPlantDBData.setBaseparam2(rset.getString("baseparam2"));
                    cusPlantDBData.setBaseparam3(rset.getString("baseparam3"));
                    cusPlantDBData.setThparam1(rset.getString("thparam1"));
                    cusPlantDBData.setThparam2(rset.getString("thparam2"));
                    cusPlantDBData.setThparam3(rset.getString("thparam3"));
                    cusPlantDBData.setBasepolicyname(rset.getString("basepolicyname"));
                    cusPlantDBData.setThpolicyname(rset.getString("thpolicyname"));
                    cusPlantDBData.setPlanName(rset.getString("plan_name"));
                    cusPlantDBData.setPlanType(rset.getString("plantype"));
                    cusPlantDBData.setPlanGroup(rset.getString("plangroup"));
                    cusPlantDBData.setPlanid(rset.getInt("postpaidplanid"));
                    cusPlantDBData.setConcurrency(rset.getInt("maxconcurrentsession"));
                    cusPlantDBData.setQuotatype(rset.getString("quotatype"));
                    cusPlantDBData.setQuotaunit(rset.getString("quotaunit"));
                    cusPlantDBData.setTimequotaunit(rset.getString("timequotaunit"));
                    cusPlantDBData.setAllowoverusage(rset.getBoolean("allowoverusage"));
                    cusPlantDBData.setSkipQuotaUpdate(rset.getBoolean("isquotaupdateskipped"));
                    cusPlantDBData.setAddonToBase(rset.getBoolean("addon_to_base"));
                    if (rset.getString("purchase_type") != null) {
                        cusPlantDBData.setPurchaseType(rset.getString("purchase_type"));
                        if (!cusPlantDBData.getPurchaseType().equalsIgnoreCase("New")) {
                            cusPlantDBData.setNotBasePlan(true);
                        }
                    }
                    if (rset.getObject("quotadtlsid") != null) {
                        cusPlantDBData.setCustquotaid(rset.getInt("quotadtlsid"));
                    }
                    if (rset.getString("qosspeed") != null) {
                        cusPlantDBData.setQosspeed(Double.parseDouble(rset.getString("qosspeed")));
                    } else {
                        cusPlantDBData.setQosspeed(0d);
                    }
                    cusPlantDBData.setCustPlanStatus(rset.getString("cust_plan_status"));
                    cusPlantDBData.setTimepolicyid(rset.getInt("timebasepolicyid"));
                    cusPlantDBData.setRadServiceType(rset.getString("type"));
                    cusPlantDBData.setChunkAvailable(rset.getBoolean("is_chunk_available"));
                    cusPlantDBData.setReservedQuotaInPer(rset.getDouble("reserved_quota_in_per"));
                    cusPlantDBData.setTotalReservedQuota(rset.getDouble("total_reserved_quota"));

                    double volumebasedunusedquota = rset.getDouble("totalquota") - rset.getDouble("usedquota");
                    double timebasedunusedquota = rset.getDouble("timetotalquota") - rset.getDouble("timequotaused");
                    cusPlantDBData.setTimebasedunusedquota(timebasedunusedquota);
                    cusPlantDBData.setVolumebasedunusedquota(volumebasedunusedquota);
                    cusPlantDBData.setTotalvolumebasedunusedquota(volumebasedunusedquota);
                    cusPlantDBData.setUpdateVolumeQuota(false);

                    if (cusPlantDBData.getPlanid() != 0) {
                        cusPlantDBData.setPlanQosPolicyMapping(getPlanQosPolicyMappings(conn, cusPlantDBData.getPlanid()));
                    }
                    if (rset.getLong("qos_policy_id") != 0) {
                        List<QOSPolicyGatewayMapping> qosPolicyGatewayMappings = getQosPolicyGatewayMappings(conn, rset.getLong("qos_policy_id"));
                        cusPlantDBData.setQosPolicyGatewayMapping(qosPolicyGatewayMappings);
                        cusPlantDBData.setBasPlanQosPolicyGatewayMapping(qosPolicyGatewayMappings);
                    }
                    if (cusPlantDBData.getTimepolicyid() != 0) {
                        cusPlantDBData.setTimepolicyData(getTimePolicyData(conn, cusPlantDBData.getTimepolicyid(), radUtil));
                    }
                    if (rset.getString("usage_quota_type") != null) {
                        cusPlantDBData.setUsageQuotaType(rset.getString("usage_quota_type"));
                    } else {
                        cusPlantDBData.setUsageQuotaType("TOTAL");
                    }
                    cusPlantDBData.setTriggerCoaDm(rset.getBoolean("istriggercoadm"));
                    cusPlantDBData.setOnQuotaExhaustEventName(rset.getString("onquotaexhausteventname"));
                    getReservedQuota(conn, custDBData, cusPlantDBData);
                    log.info("cusPlantDBData: " + cusPlantDBData.getPlanName() + " Remaining Quota: " + cusPlantDBData.getVolumequota() +
                            " totalQuota: " + cusPlantDBData.getVolumebasedtotalquota() + " useQuota: " + cusPlantDBData.getVolumebasedunusedquota());
                    listCustomerPlanData.add(cusPlantDBData);
                    log.debug(":getCustomerPlans:" + strRadiusCustomerPlanQuery + ":" + custDBData.getCustid());
                }
            }
        }
        return listCustomerPlanData;
    }


    public CustomerData getCustomerDetailsByCprId(Long custpackageid, RadiusUtility radiusUtility) throws SQLException {
        CustomerData custDBData = null;
        CustomerPlanData cusPlantDBData = new CustomerPlanData();
        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(strRadiusCustomerDetailsQueryByCprId)) {
            log.debug(":GetCustomerFromCPR:" + strRadiusCustomerDetailsQueryByCprId + ":" + custpackageid);
            stmt.setLong(1, custpackageid);
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    custDBData = new CustomerData();
                    custDBData.setAuthStatus(true);
                    custDBData.setCustid(rset.getInt("custid"));
                    custDBData.setUsername(rset.getString("username"));
                    custDBData.setMvnoId(rset.getInt("mvnoid"));
                    custDBData.setPassword(rset.getString("password"));
                    custDBData.setCustid(rset.getInt("custid"));
                    custDBData.setStatus(rset.getString("cstatus"));
                    custDBData.setNasPortidValidate(rset.getString("nas_port_id"));
                    custDBData.setVlanidValidate(rset.getString("vlanid"));
                    custDBData.setFramedIp6Validate(rset.getString("framed_ipv6_address"));
                    custDBData.setFramedIpValidate(rset.getString("framed_ip"));
                    custDBData.setNasIpValidate(rset.getString("nas_ip_address"));
                    custDBData.setPasswordcheck("Y");
                    custDBData.setIppoolbind(rset.getString("ip_pool_name_bind"));
                    custDBData.setFrameipbind(rset.getString("framed_ip_bind"));
                    custDBData.setParentCustId(rset.getObject("parentcustid") != null ? rset.getInt("parentcustid") : 0);
                    custDBData.setMaxconcurrentsession(rset.getObject("maxconcurrentsession") != null ? rset.getInt("maxconcurrentsession") : null);
                    custDBData.setMacProvision(rset.getBoolean("mac_provision"));
                    custDBData.setMacAuthEnable(rset.getBoolean("mac_auth_enable"));
                    custDBData.setFramedroute(rset.getString("framedroute"));
                    custDBData.setDelegatedprefix(rset.getString("delegatedprefix"));
                    custDBData.setBngRouterInterface(rset.getString("bngrouterinterface"));
                    custDBData.setVrfName(rset.getString("vrfname"));
                    custDBData.setWanIP(rset.getString("WANIP"));
                    custDBData.setWanIPV6(rset.getString("WANIPV6"));
                    custDBData.setLlConnectionType(rset.getString("LLConnectionType"));
                    custDBData.setLlExpiryDate(rset.getString("LLExpiryDate"));
                    custDBData.setBngRouterName(rset.getString("BNGRouterName"));
                    custDBData.setGatewayip(rset.getString("gatewayip"));
                    custDBData.setIpPoolNameBind(rset.getString("ip_pool_name_bind"));
                    custDBData.setFramedIpBind(rset.getString("framed_ip_bind"));
                    custDBData.setFramedIp(rset.getString("framed_ip"));
                    custDBData.setLanIP(rset.getString("lanip"));
                    custDBData.setFramedIPNetmask(rset.getString("framed_ip_netmask"));
                    custDBData.setFramedIPv6Prefix(rset.getString("framed_ipv6_prefix"));
                    custDBData.setPrimaryDNS(rset.getString("primary_dns"));
                    custDBData.setPrimaryIPv6DNS(rset.getString("primary_ipv6_dns"));
                    custDBData.setSecondaryIPv6DNS(rset.getString("secondary_ipv6_dns"));
                    custDBData.setSecondaryDNS(rset.getString("secondary_dns"));

                    if (rset.getString("macretentionunit") != null) {
                        custDBData.setMacRetentionUnit(rset.getString("macretentionunit"));
                    }
                    if (rset.getObject("macretentionperiod") != null) {
                        custDBData.setMacRetentionPeriod(Integer.valueOf(rset.getString("macretentionperiod")));
                    }
                    cusPlantDBData = updateCustomerPlanDataFromDb(rset, cusPlantDBData, custDBData, radiusUtility, conn);
                    if(cusPlantDBData != null) {
                        custDBData.setUsageQuotaType(cusPlantDBData.getUsageQuotaType());
                        if (cusPlantDBData != null && cusPlantDBData.getCustpackageid() != null) {
                            custDBData.setCustomerBasePlan(new ArrayList<>(Collections.singleton(cusPlantDBData)));
                        }
                    }
                }
            } catch (Exception e) {
                log.error("SQL Exception:" + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        log.debug(":custDBData:" + custDBData);
        return custDBData;
    }

    public CustomerPlanData updateCustomerPlanDataFromDb(ResultSet rset, CustomerPlanData cusPlantDBData, CustomerData custDBData, RadiusUtility radiusUtility, Connection conn) throws SQLException {
//        while (rset.next()) {
        Timestamp endTimestamp = rset.getTimestamp("enddate");
        LocalDateTime endDateTime = endTimestamp.toLocalDateTime();
        Integer cprId = rset.getInt("custpackageid");
        if(endDateTime.isAfter(LocalDateTime.now())) {
            cusPlantDBData.setEnddate(endTimestamp);
            cusPlantDBData.setStartdate(rset.getTimestamp("startdate"));
            cusPlantDBData.setEnddate(rset.getTimestamp("enddate"));
            cusPlantDBData.setVolumebasedtotalquota(rset.getDouble("totalquota"));
            cusPlantDBData.setCurrentsessionusagevolume(rset.getDouble("currentsessionusagevolume"));
            cusPlantDBData.setCurrentsessionusagetime(rset.getDouble("currentsessionusagetime"));
            cusPlantDBData.setVolumebasedusedquota(rset.getDouble("usedquota"));
            cusPlantDBData.setCustpackageid(cprId);
            cusPlantDBData.setTimebasedtotalquota(rset.getDouble("timetotalquota"));
            cusPlantDBData.setTimebasedusedquota(rset.getDouble("timequotaused"));
            cusPlantDBData.setBaseparam1(rset.getString("baseparam1"));
            cusPlantDBData.setBaseparam2(rset.getString("baseparam2"));
            cusPlantDBData.setBaseparam3(rset.getString("baseparam3"));
            cusPlantDBData.setThparam1(rset.getString("thparam1"));
            cusPlantDBData.setThparam2(rset.getString("thparam2"));
            cusPlantDBData.setThparam3(rset.getString("thparam3"));
            cusPlantDBData.setBasepolicyname(rset.getString("basepolicyname"));
            cusPlantDBData.setThpolicyname(rset.getString("thpolicyname"));
            cusPlantDBData.setPlanName(rset.getString("plan_name"));
            cusPlantDBData.setPlanType(rset.getString("plantype"));
            cusPlantDBData.setPlanGroup(rset.getString("plangroup"));
            cusPlantDBData.setPlanid(rset.getInt("postpaidplanid"));
            cusPlantDBData.setConcurrency(rset.getInt("maxconcurrentsession"));
            cusPlantDBData.setQuotatype(rset.getString("quotatype"));
            cusPlantDBData.setQuotaunit(rset.getString("quotaunit"));
            cusPlantDBData.setTimequotaunit(rset.getString("timequotaunit"));
            cusPlantDBData.setAllowoverusage(rset.getBoolean("allowoverusage"));
            cusPlantDBData.setSkipQuotaUpdate(rset.getBoolean("isquotaupdateskipped"));
            if (rset.getString("purchase_type") != null) {
                cusPlantDBData.setPurchaseType(rset.getString("purchase_type"));
                if (!cusPlantDBData.getPurchaseType().equalsIgnoreCase("New")) {
                    cusPlantDBData.setNotBasePlan(true);
                }
            }
            if (rset.getObject("quotadtlsid") != null) {
                cusPlantDBData.setCustquotaid(rset.getInt("quotadtlsid"));
            }
            if (rset.getString("qosspeed") != null) {
                cusPlantDBData.setQosspeed(Double.parseDouble(rset.getString("qosspeed")));
            } else {
                cusPlantDBData.setQosspeed(0d);
            }
            cusPlantDBData.setCustPlanStatus(rset.getString("cust_plan_status"));
            cusPlantDBData.setTimepolicyid(rset.getInt("timebasepolicyid"));
            cusPlantDBData.setRadServiceType(rset.getString("type"));
            cusPlantDBData.setChunkAvailable(rset.getBoolean("is_chunk_available"));
            cusPlantDBData.setReservedQuotaInPer(rset.getDouble("reserved_quota_in_per"));
            cusPlantDBData.setTotalReservedQuota(rset.getDouble("total_reserved_quota"));

            double volumebasedunusedquota = rset.getDouble("totalquota") - rset.getDouble("usedquota");
            double timebasedunusedquota = rset.getDouble("timetotalquota") - rset.getDouble("timequotaused");
            cusPlantDBData.setTimebasedunusedquota(timebasedunusedquota);
            cusPlantDBData.setVolumebasedunusedquota(volumebasedunusedquota);
            cusPlantDBData.setTotalvolumebasedunusedquota(volumebasedunusedquota);
            cusPlantDBData.setUpdateVolumeQuota(false);
            cusPlantDBData.setVolumequota(volumebasedunusedquota);
            if (cusPlantDBData.getPlanid() != 0) {
                cusPlantDBData.setPlanQosPolicyMapping(getPlanQosPolicyMappings(conn, cusPlantDBData.getPlanid()));
            }
            if (rset.getLong("qos_policy_id") != 0) {
                List<QOSPolicyGatewayMapping> qosPolicyGatewayMappings = getQosPolicyGatewayMappings(conn, rset.getLong("qos_policy_id"));
                cusPlantDBData.setQosPolicyGatewayMapping(qosPolicyGatewayMappings);
                cusPlantDBData.setBasPlanQosPolicyGatewayMapping(qosPolicyGatewayMappings);
            }
            if (cusPlantDBData.getTimepolicyid() != 0) {
                cusPlantDBData.setTimepolicyData(getTimePolicyData(conn, cusPlantDBData.getTimepolicyid(), radiusUtility));
            }
            if (rset.getString("usage_quota_type") != null) {
                cusPlantDBData.setUsageQuotaType(rset.getString("usage_quota_type"));
            } else {
                cusPlantDBData.setUsageQuotaType("TOTAL");
            }
            cusPlantDBData.setTriggerCoaDm(rset.getBoolean("istriggercoadm"));
            cusPlantDBData.setOnQuotaExhaustEventName(rset.getString("onquotaexhausteventname"));
            getReservedQuota(conn, custDBData, cusPlantDBData);

            log.debug(":getCustomerPlans:" + strRadiusCustomerPlanQuery + ":" + custDBData.getCustid());
            return cusPlantDBData;
        } else {
            log.debug("Plan Not Active for custId: "+custDBData.getCustid()+" for cprId: "+cprId);
        }
//        }
        return null;
    }

    private List<PlanQosPolicyMapping> getPlanQosPolicyMappings(Connection conn, int planid) throws SQLException {
        List<PlanQosPolicyMapping> planQosPolicyMappings = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(strRadiusPlanQos)) {
            stmt.setInt(1, planid);
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    PlanQosPolicyMapping planQosPolicyMapping = new PlanQosPolicyMapping();
                    planQosPolicyMapping.setPlanId(rset.getLong("planid"));
                    planQosPolicyMapping.setFrompercentage(rset.getDouble("from_percentage"));
                    planQosPolicyMapping.setTopercentage(rset.getDouble("to_percentage"));
                    planQosPolicyMapping.setQosPolicy(rset.getInt("qosid"));
                    planQosPolicyMappings.add(planQosPolicyMapping);
                }
            }
        } catch (Exception ex) {
            log.error("Exception to get Qos Plicy Mapping: " + ex.getMessage());
        }
        return planQosPolicyMappings;
    }

    private List<QOSPolicyGatewayMapping> getQosPolicyGatewayMappings(Connection conn, long policyId) throws SQLException {
        List<QOSPolicyGatewayMapping> qosPolicyGatewayMappings = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(strRadiusCustomerQoS)) {
            stmt.setLong(1, policyId);
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    QOSPolicyGatewayMapping qosPolicyGateway = new QOSPolicyGatewayMapping();
                    qosPolicyGateway.setGatewayName(rset.getString("name"));
                    qosPolicyGateway.setDownloadSpeed(rset.getString("download_speed"));
                    qosPolicyGateway.setUploadSpeed(rset.getString("upload_speed"));
                    qosPolicyGateway.setBaseDownloadSpeed(rset.getString("base_download_speed"));
                    qosPolicyGateway.setBaseUploadSpeed(rset.getString("base_upload_speed"));
                    qosPolicyGateway.setThrottleDownloadSpeed(rset.getString("throttle_download_speed"));
                    qosPolicyGateway.setThrottleUploadSpeed(rset.getString("throttle_upload_speed"));
                    qosPolicyGatewayMappings.add(qosPolicyGateway);
                }
            } catch (Exception ex) {
                log.error("Exception to get Qos Plicy Mapping: " + ex.getMessage());
            }
        }
        return qosPolicyGatewayMappings;
    }

    private List<TimepolicyData> getTimePolicyData(Connection conn, int policyId, RadiusUtility radUtil) throws SQLException {
        List<TimepolicyData> timepolicyDataList = new ArrayList<>();
        Map<Integer, TimepolicyData> timepolicyDataMap = new HashMap<>();

        String combinedQuery =
                "SELECT t.*, q.download_speed, q.upload_speed, q.base_download_speed, q.base_upload_speed, " +
                        "q.throttle_download_speed, q.throttle_upload_speed " +
                        "FROM tbltimebasepolicydetails t " +
                        "LEFT JOIN tbltqospolicy_gateway_mapping q ON t.qqsid = q.qos_policy_id " +
                        "WHERE t.policy_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(combinedQuery)) {
            stmt.setInt(1, policyId);
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    int qqsid = rset.getInt("qqsid");

                    TimepolicyData tpData = timepolicyDataMap.get(qqsid);
                    if (tpData == null) {
                        tpData = new TimepolicyData();
                        tpData.setAccess(rset.getString("access"));
                        tpData.setFromDay(rset.getString("from_day"));
                        tpData.setFromTime(rset.getString("from_time"));
                        tpData.setQuotadtlid(qqsid);
                        tpData.setToDay(rset.getString("to_day"));
                        tpData.setToTime(rset.getString("to_time"));
                        tpData.setDetails_id(rset.getInt("details_id"));
                        tpData.setFromNumber(radUtil.timepolicytoformula(tpData.getFromDay(), tpData.getFromTime()));
                        tpData.setToNumber(radUtil.timepolicytoformula(tpData.getToDay(), tpData.getToTime()));
                        tpData.setFreeQuota(rset.getObject("is_free_quota") != null && rset.getBoolean("is_free_quota"));
                        tpData.setQosPolicyGatewayMapping(new ArrayList<>());

                        timepolicyDataList.add(tpData);
                        timepolicyDataMap.put(qqsid, tpData);
                    }

                    if (rset.getString("download_speed") != null) {
                        QOSPolicyGatewayMapping qosPolicyGateway = new QOSPolicyGatewayMapping();
                        qosPolicyGateway.setDownloadSpeed(rset.getString("download_speed"));
                        qosPolicyGateway.setUploadSpeed(rset.getString("upload_speed"));
                        qosPolicyGateway.setBaseDownloadSpeed(rset.getString("base_download_speed"));
                        qosPolicyGateway.setBaseUploadSpeed(rset.getString("base_upload_speed"));
                        qosPolicyGateway.setThrottleDownloadSpeed(rset.getString("throttle_download_speed"));
                        qosPolicyGateway.setThrottleUploadSpeed(rset.getString("throttle_upload_speed"));

                        tpData.getQosPolicyGatewayMapping().add(qosPolicyGateway);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return timepolicyDataList;
    }

    private void getReservedQuota(Connection conn, CustomerData custDBData, CustomerPlanData cusPlantDBData) {
        if (cusPlantDBData.getReservedQuotaInPer() != null && cusPlantDBData.getReservedQuotaInPer() > 0) {

            try (PreparedStatement stmt = conn.prepareStatement(strRadiusReservedQuota)) {
                if (log.isDebugEnabled()) {
                    log.debug("IN GET Reserved quota data:" + strRadiusReservedQuota + ": custId:" + custDBData.getCustid());
                }
                stmt.setLong(1, custDBData.getCustid());
                try (ResultSet rset = stmt.executeQuery()) {

                    while (rset.next()) {
                        cusPlantDBData.setReservedtotalquota(rset.getDouble("reserved_quota"));
                        cusPlantDBData.setReservedusedquota(rset.getDouble("used_quota"));
                        cusPlantDBData.setReservedunusedquota(rset.getDouble("unused_quota"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public CustomerData getParentCustByMac(String mac, String custmac, int mvnoid) {
        Long childId = 0L;
        Long parentId = 0L;
        CustomerData customerData = null;
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET PARENT CUSTOMER FROM MAC FROM DB : %s :QUERY: %s", mac, strRadiusChildCustomerByLocationQuery));
        }
        Connection conn = null;
        PreparedStatement stmt1 = null;
        ResultSet rset1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rset2 = null;

        try {
            conn = DataSource.getConnection();

            // First query to get parentId
            stmt1 = conn.prepareStatement(strRadiusParentCustomerByLocationQuery);
            stmt1.setInt(1, mvnoid);
            stmt1.setString(2, mac);
            rset1 = stmt1.executeQuery();
            if (rset1.next()) {
                parentId = rset1.getLong("custid");
            }

            // Close the first statement and result set
            rset1.close();
            stmt1.close();

            // Second query to get childId
            stmt2 = conn.prepareStatement(strRadiusChildCustomerByLocationQuery);
            stmt2.setString(1, custmac);
            stmt2.setInt(2, parentId.intValue());
            rset2 = stmt2.executeQuery();
            if (rset2.next()) {
                childId = rset2.getLong("custid");
            }

            // If childId is found, get the customer data
            if (childId != null) {
                customerData = getDBCustomer(null, mvnoid, childId.toString(), null, false);
            }
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e.fillInStackTrace());
        } finally {
            // Close resources
            try {
                if (rset2 != null) rset2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt2 != null) stmt2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return customerData;
    }


    public CustomerData getDBAllCustomerAuthenticate(String strIdentity, int mvnoid, String type, String mac, String ip, String AcctStatusValue) throws RuntimeException, SQLException {
        if (log.isDebugEnabled()) {
            if (type == null) {
                strIdentity = normalizeMacAddress(strIdentity);
                log.debug(String.format("IN GET CUSTOMER MAC FROM DB : %s :QUERY: %s", strIdentity, strRadiusCustomerMacQuery));
            } else if (type.equalsIgnoreCase("mac")) {
                strIdentity = normalizeMacAddress(strIdentity);
                log.debug(String.format("IN GET CUSTOMER MAC FROM DB : %s :QUERY: %s", strIdentity, strRadiusCustomerMacQuery));
            } else if (type.equalsIgnoreCase("ip")) {
                log.debug(String.format("IN GET CUSTOMER IP FROM DB : %s :QUERY: %s", strIdentity, strRadiusCustomerIPQuery));
            } else if (type.equalsIgnoreCase("username_mac")) {
                mac = normalizeMacAddress(mac);
                log.debug(String.format("IN GET CUSTOMER USER_MAC FROM DB : %s %s:QUERY: %s", strIdentity, mac, strRadiusCustomerUserMacQuery));
            } else if (type.equalsIgnoreCase("username_ip")) {
                log.debug(String.format("IN GET CUSTOMER USER_IP FROM DB : %s %s :QUERY: %s", strIdentity, ip, strRadiusCustomerUserIPQuery));
            } else if (type.equalsIgnoreCase("mac_ip")) {
                mac = normalizeMacAddress(mac);
                log.debug(String.format("IN GET CUSTOMER MAC_IP FROM DB : %s %s:QUERY: %s", mac, ip, strRadiusCustomerMacIPQuery));
            } else if (type.equalsIgnoreCase("username_mac_ip")) {
                mac = normalizeMacAddress(mac);
                log.debug(String.format("IN GET CUSTOMER USER_MAC_IP FROM DB : %s %s %s:QUERY: %s", strIdentity, mac, ip, strRadiusCustomerUserMacIPQuery));
            }
        }

        RadiusUtility radUtil = new RadiusUtility();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        CustomerData custDBData = null;

        try {
            conn = DataSource.getConnection();

            if (type == null) {
                stmt = conn.prepareStatement(strRadiusCustomerMacQuery);
                stmt.setString(1, normalizeMacAddress(strIdentity));
                stmt.setLong(2, mvnoid);
            } else if (type.equalsIgnoreCase("mac")) {
                stmt = conn.prepareStatement(strRadiusCustomerMacQuery);
                stmt.setString(1, normalizeMacAddress(strIdentity));
                stmt.setLong(2, mvnoid);
            } else if (type.equalsIgnoreCase("ip")) {
                stmt = conn.prepareStatement(strRadiusCustomerIPQuery);
                stmt.setString(1, strIdentity);
                stmt.setLong(2, mvnoid);
            } else if (type.equalsIgnoreCase("username_mac")) {
                stmt = conn.prepareStatement(strRadiusCustomerUserMacQuery);
                stmt.setString(1, strIdentity);
                stmt.setString(2, normalizeMacAddress(mac));
                stmt.setLong(3, mvnoid);
            } else if (type.equalsIgnoreCase("username_ip")) {
                stmt = conn.prepareStatement(strRadiusCustomerUserIPQuery);
                stmt.setString(1, strIdentity);
                stmt.setString(2, ip);
                stmt.setLong(3, mvnoid);
            } else if (type.equalsIgnoreCase("mac_ip")) {
                stmt = conn.prepareStatement(strRadiusCustomerMacIPQuery);
                stmt.setString(1, normalizeMacAddress(mac));
                stmt.setString(2, ip);
                stmt.setLong(3, mvnoid);
            } else if (type.equalsIgnoreCase("username_mac_ip")) {
                stmt = conn.prepareStatement(strRadiusCustomerUserMacIPQuery);
                stmt.setString(1, strIdentity);
                stmt.setString(2, normalizeMacAddress(mac));
                stmt.setString(3, ip);
                stmt.setLong(4, mvnoid);
            }
            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s %s", RESULT_SET_IS, rset));
            }

            while (rset.next()) {
                custDBData = new CustomerData();
                custDBData.setAuthStatus(true);
                custDBData.setUsername(rset.getString("username"));
                custDBData.setMvnoId(rset.getInt("mvnoid"));
                custDBData.setPassword(rset.getString("password"));
                custDBData.setCustid(rset.getInt("custid"));
                custDBData.setStatus(rset.getString("cstatus"));

                custDBData.setNasPortidValidate(rset.getString("nas_port_id"));
                custDBData.setVlanidValidate(rset.getString("vlanid"));
                custDBData.setFramedIp6Validate(rset.getString("framed_ipv6_address"));
                custDBData.setFramedIpValidate(rset.getString("framed_ip"));
                custDBData.setNasIpValidate(rset.getString("nas_ip_address"));

                custDBData.setPasswordcheck("Y");
                custDBData.setIppoolbind(rset.getString("ip_pool_name_bind"));
                custDBData.setFrameipbind(rset.getString("framed_ip_bind"));
                custDBData.setParentCustId(rset.getInt("parentcustid"));
                custDBData.setMacProvision(rset.getBoolean("mac_provision"));
                custDBData.setMacAuthEnable(rset.getBoolean("mac_auth_enable"));
                if (rset.getObject("maxconcurrentsession") != null) {
                    custDBData.setMaxconcurrentsession(rset.getInt("maxconcurrentsession"));
                } else {
                    custDBData.setMaxconcurrentsession(null);
                }
                custDBData.setMacProvision(rset.getBoolean("mac_provision"));
                custDBData.setMacAuthEnable(rset.getBoolean("mac_auth_enable"));
                custDBData.setFramedroute(rset.getString("framedroute"));
                custDBData.setDelegatedprefix(rset.getString("delegatedprefix"));
                custDBData.setBngRouterInterface(rset.getString("bngrouterinterface"));
                custDBData.setVrfName(rset.getString("vrfname"));
                custDBData.setWanIP(rset.getString("WANIP"));
                custDBData.setWanIPV6(rset.getString("WANIPV6"));
                custDBData.setLlConnectionType(rset.getString("LLConnectionType"));
                custDBData.setLlExpiryDate(rset.getString("LLExpiryDate"));
                custDBData.setBngRouterName(rset.getString("BNGRouterName"));
                custDBData.setGatewayip(rset.getString("gatewayip"));
                custDBData.setIpPoolNameBind(rset.getString("ip_pool_name_bind"));
                custDBData.setFramedIpBind(rset.getString("framed_ip_bind"));
                custDBData.setFramedIp(rset.getString("framed_ip"));
                custDBData.setLanIP(rset.getString("lanip"));
                custDBData.setFramedIPNetmask(rset.getString("framed_ip_netmask"));
                custDBData.setFramedIPv6Prefix(rset.getString("framed_ipv6_prefix"));
                custDBData.setPrimaryDNS(rset.getString("primary_dns"));
                custDBData.setPrimaryIPv6DNS(rset.getString("primary_ipv6_dns"));
                custDBData.setSecondaryIPv6DNS(rset.getString("secondary_ipv6_dns"));
                custDBData.setSecondaryDNS(rset.getString("secondary_dns"));
                if (CommonConstants.CUST_ACTIVE.equalsIgnoreCase(custDBData.getStatus())
                        || CommonConstants.CUST_SUSPEND.equalsIgnoreCase(custDBData.getStatus())
                        || CommonConstants.PLAN_INACTIVE.equalsIgnoreCase(custDBData.getStatus())) {
                    List<CustomerPlanData> listCustomerPlanData = getCustomerPlans(conn, custDBData, radUtil);
                    if (!listCustomerPlanData.isEmpty()) {
                        custDBData.setCustomerAllPlan(listCustomerPlanData);
                    }
                } else {
                    log.debug("User status is not active. So, skipping retrieval of further details of user.");
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        if (custDBData != null &&
                (CommonConstants.CUST_ACTIVE.equalsIgnoreCase(custDBData.getStatus())
                        || CommonConstants.CUST_SUSPEND.equalsIgnoreCase(custDBData.getStatus())
                        || CommonConstants.PLAN_INACTIVE.equalsIgnoreCase(custDBData.getStatus()))) {
            try {
                getPlanDetailCustomer(custDBData, AcctStatusValue);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CUSTOMER FROM DB : %s :DATAIS: %s", strIdentity, custDBData));
        }
        return custDBData;
    }


    public String normalizeMacAddress(String macAddress) {
        String newMac = macAddress;
        if (newMac != null)
            return newMac.replace(":", "").replace("-", "").replace(".", "");
        return newMac;
    }

    /**
     * Update Qos policy
     *
     * @param customerPlanData
     * @param qosId
     * @throws SQLException
     */
    public void updateCustomerPlanQos(CustomerPlanData customerPlanData, int qosId) throws SQLException {
        Connection conn2 = null;
        PreparedStatement stmt2 = null;
        ResultSet rset2 = null;
        try {
            conn2 = DataSource.getConnection();
            //Getting QoS Detail
            stmt2 = conn2.prepareStatement(strRadiusCustomerQoS);
            stmt2.setLong(1, qosId);
            rset2 = stmt2.executeQuery();

            List<QOSPolicyGatewayMapping> qosPolicyGatewayMapping = new ArrayList<QOSPolicyGatewayMapping>();
            while (rset2.next()) {
                log.debug("Updating Qos Policy: " + rset2.getString("name"));
                if (customerPlanData.getQosPolicyGatewayMapping() != null) {
                    customerPlanData.getQosPolicyGatewayMapping().get(0).setDownloadSpeed(rset2.getString("download_speed"));
                    customerPlanData.getQosPolicyGatewayMapping().get(0).setUploadSpeed(rset2.getString("upload_speed"));
                    customerPlanData.getQosPolicyGatewayMapping().get(0).setBaseDownloadSpeed(rset2.getString("base_download_speed"));
                    customerPlanData.getQosPolicyGatewayMapping().get(0).setBaseUploadSpeed(rset2.getString("base_upload_speed"));
                    customerPlanData.getQosPolicyGatewayMapping().get(0).setThrottleDownloadSpeed(rset2.getString("throttle_download_speed"));
                    customerPlanData.getQosPolicyGatewayMapping().get(0).setThrottleUploadSpeed(rset2.getString("throttle_upload_speed"));
                } else {
                    QOSPolicyGatewayMapping qosPolicyGateway = new QOSPolicyGatewayMapping();
                    qosPolicyGateway.setDownloadSpeed(rset2.getString("download_speed"));
                    qosPolicyGateway.setUploadSpeed(rset2.getString("upload_speed"));
                    qosPolicyGateway.setBaseDownloadSpeed(rset2.getString("base_download_speed"));
                    qosPolicyGateway.setBaseUploadSpeed(rset2.getString("base_upload_speed"));
                    qosPolicyGateway.setThrottleDownloadSpeed(rset2.getString("throttle_download_speed"));
                    qosPolicyGateway.setThrottleUploadSpeed(rset2.getString("throttle_upload_speed"));
                    qosPolicyGatewayMapping.add(qosPolicyGateway);
                }

            }

        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rset2 != null) rset2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt2 != null) stmt2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn2 != null) conn2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private CustomerData evaluateAndAssignCustomerPlans(CustomerData custRetrunData, String acctStatusValue) {
        try {
            log.debug("Get Customer plans: " + custRetrunData.getUsername() + " AcctStatusValue: " + acctStatusValue);
            log.debug("In getPlanDetailCustomer:" + custRetrunData.getCustomerAllPlan());
            if (custRetrunData.getCustomerAllPlan() != null && custRetrunData.getCustomerAllPlan().size() > 0) {
                int size = custRetrunData.getCustomerAllPlan().size();
                log.debug("Plan Size is:" + size);
                boolean isBasePlanQuotaNotExhaust = true;
                List<CustomerPlanData> customerQuotaBooster = new ArrayList<CustomerPlanData>();
                List<CustomerPlanData> customerVolumeBooster = new ArrayList<CustomerPlanData>();
                List<CustomerPlanData> customerBasePlan = new ArrayList<CustomerPlanData>();

                boolean isBandWidthBoosterWithQuota = false;
                boolean isBandWidthBoosterWithOutQuota = false;
                boolean isVolumeBooster = false;
                boolean isBasePlanHasVolumeQuota = false;
                boolean noBooster = true;
                boolean isBasePlanActivatedForStop = false;
                List<QOSPolicyGatewayMapping> basePlanQos = null;
                for (CustomerPlanData customerPlanData : custRetrunData.getCustomerAllPlan()) {
                    if (customerPlanData.getCustPlanStatus().equalsIgnoreCase("Active")) {
                        // differentiate base, volume and quota plans
                        double remainingVolumeQuota = customerPlanData.getVolumebasedunusedquota();
                        double sessionUsedQuota = customerPlanData.getCurrentsessionusagevolume();
                        log.info("Customer customerPlanData: " + customerPlanData.getPlanName() + " ,RemainingVolumeQuota: " + remainingVolumeQuota + " ,sessionUsedQuota: " + sessionUsedQuota);
                        remainingVolumeQuota = remainingVolumeQuota - sessionUsedQuota;
                        if (customerPlanData.isSkipQuotaUpdate() && acctStatusValue.equalsIgnoreCase("Auth")) {
                            log.debug("Customer plan Skip Quota flag: true Actual Remaining Quota: " + remainingVolumeQuota + " updating to " + customerPlanData.getVolumebasedtotalquota());
                            remainingVolumeQuota = customerPlanData.getVolumebasedtotalquota();
                            customerPlanData.setVolumebasedunusedquota(customerPlanData.getVolumebasedtotalquota());
                        }
                        String planGroup = customerPlanData.getPlanGroup();
                        switch (planGroup) {
                            case "Bandwidthbooster":
                                if (customerPlanData.getVolumebasedtotalquota() > 0 && remainingVolumeQuota > 0) {
                                    isBandWidthBoosterWithQuota = true;
                                    log.debug("Found Bandwidthbooster:" + customerPlanData.getPlanName());
                                    customerQuotaBooster.add(customerPlanData);
                                } else if (customerPlanData.getVolumebasedtotalquota() == 0) {
                                    isBandWidthBoosterWithOutQuota = true;
                                    log.debug("Found Bandwidthbooster:" + customerPlanData.getPlanName());
                                    customerQuotaBooster.add(customerPlanData);
                                    noBooster = false;
                                } else if (remainingVolumeQuota > 0) {
                                    isBandWidthBoosterWithQuota = true;
                                    log.debug("Found Bandwidthbooster:" + customerPlanData.getPlanName());
                                    customerQuotaBooster.add(customerPlanData);
                                    noBooster = false;
                                } else {
                                    log.info("Skip Bandwidthbooster customerPlanData: " + customerPlanData.getPlanName() + " total quota: " + customerPlanData.getVolumebasedtotalquota() + " remainingVolumeQuota: " + remainingVolumeQuota);
                                }
                                break;
                            case "Volume Booster":
                                if (customerPlanData.getQuotatype().equalsIgnoreCase("Data") && customerPlanData.getVolumebasedunusedquota() > 0 && remainingVolumeQuota > 0) {
                                    log.debug("Found Volume Booster:" + customerPlanData.getPlanName());
                                    customerVolumeBooster.add(customerPlanData);
                                    isVolumeBooster = true;
                                    noBooster = false;
                                }
                                if (customerPlanData.getQuotatype().equalsIgnoreCase("Time") && customerPlanData.getTimebasedunusedquota() > 0) {
                                    log.debug("Found Volume Booster:" + customerPlanData.getPlanName());
                                    customerVolumeBooster.add(customerPlanData);
                                    isVolumeBooster = true;
                                    noBooster = false;
                                }
                                if (customerPlanData.getQuotatype().equalsIgnoreCase("Both") && customerPlanData.getTimebasedunusedquota() > 0 && customerPlanData.getVolumebasedunusedquota() > 0 && remainingVolumeQuota > 0) {
                                    log.debug("Found Volume Booster:" + customerPlanData.getPlanName());
                                    customerVolumeBooster.add(customerPlanData);
                                    isVolumeBooster = true;
                                    noBooster = false;
                                }
                                break;
                            default:
                                if (customerPlanData.getEnddate() != null) {
                                    Timestamp timestampTomorrow = new Timestamp(customerPlanData.getEnddate().getTime());
                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                    long diffInMS = customerPlanData.getEnddate().getTime() - timestamp.getTime();
                                    long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMS);
                                    log.debug("EndDate:" + customerPlanData.getEnddate() + ":currentDate:" + timestamp + ":User:" + custRetrunData.getUsername() + ":Expiry Differece in milliSecond is:" + diffInMS + ":and second is:" + seconds);
                                    if (seconds <= 0) {
                                        log.error("For Customer:" + custRetrunData.getUsername() + ":Plan:" + customerPlanData.getPlanid() + ":Time Expired");
                                    } else {
                                        String username = custRetrunData.getUsername();
                                        int planId = customerPlanData.getPlanid();
                                        String quotaType = customerPlanData.getQuotatype();
                                        boolean allowOverUsage = customerPlanData.isAllowoverusage();
                                        double timeQuota = customerPlanData.getTimebasedunusedquota();

                                        switch (quotaType.toLowerCase()) {
                                            case "time":
                                                if (timeQuota > 0 || allowOverUsage) {
                                                    customerBasePlan.add(customerPlanData);
                                                    log.debug(String.format("For Customer:%s :Plan:%s :Selected", username, planId));
                                                } else {
                                                    log.error(String.format("For Customer:%s :Plan:%s :Skipped allowOverUsage: %s timeQuota: %.2f",
                                                            username, planId, allowOverUsage, timeQuota));
                                                }
                                                break;

                                            case "data":
                                                if (remainingVolumeQuota > 0 || allowOverUsage) {
                                                    customerBasePlan.add(customerPlanData);
                                                    log.debug(String.format("For Customer:%s :Plan:%s :Selected", username, planId));
                                                    if (remainingVolumeQuota > 0) {
                                                        isBasePlanHasVolumeQuota = true;
                                                    }
                                                } else {
                                                    log.error(String.format("For Customer:%s :Plan:%s :Skipped", username, planId));
                                                }
                                                break;

                                            case "both":
                                                if (allowOverUsage) {
                                                    customerBasePlan.add(customerPlanData);
                                                    log.debug(String.format("For Customer:%s :Plan:%s :Selected", username, planId));
                                                } else {
                                                    boolean selected = false;

                                                    if (remainingVolumeQuota > 0) {
                                                        customerBasePlan.add(customerPlanData);
                                                        isBasePlanHasVolumeQuota = true;
                                                        log.debug(String.format("For Customer:%s :Plan:%s :Selected (volume quota)", username, planId));
                                                        selected = true;
                                                    }

                                                    if (timeQuota > 0) {
                                                        customerBasePlan.add(customerPlanData);
                                                        log.debug(String.format("For Customer:%s :Plan:%s :Selected (time quota)", username, planId));
                                                        selected = true;
                                                    }

                                                    if (!selected) {
                                                        log.error(String.format("For Customer:%s :Plan:%s :Skipped allowOverUsage: %s timeQuota: %.2f",
                                                                username, planId, allowOverUsage, timeQuota));
                                                    }
                                                }
                                                break;

                                            default:
                                                log.error(String.format("For Customer:%s :Plan:%s :Unknown quota type: %s", username, planId, quotaType));
                                        }

                                        basePlanQos = customerPlanData.getBasPlanQosPolicyGatewayMapping();

                                        if (!CollectionUtils.isEmpty(customerBasePlan)) {
                                            custRetrunData.setUsageQuotaType(customerPlanData.getUsageQuotaType());
                                        }
                                    }
                                } else {
                                    customerBasePlan.add(customerPlanData);
                                    log.debug("For Customer:" + custRetrunData.getUsername() + ":Plan:" + customerPlanData.getPlanid() + ":Selected");
                                }
                                break;
                        }

                    }

                }
                // All the plans are differentiate as per quota
                //priority for plans are like : volume Booster, Base-plan, Quota booster
                //1.
                        /*
                            priority for plans are like : volume Booster, Base-plan, Quota booster
                            1. Volume Booster:
                                1. If base plan has quota then skip volume booster plan
                                2. If base plan totalQuota = 0 and volume booster plan is there then add volume booster plan
                         */
                //TODO: Check base-plan with 0 quota and there is VB plan then VB should be apply
                if (!CollectionUtils.isEmpty(customerVolumeBooster)) {
                    if (isBasePlanHasVolumeQuota) {
                        log.debug("Skipp volume booster plan as base plan has quota");
                        log.debug("volume booster has total quota");
                        customerBasePlan.get(0).setUpdateVolumeQuota(true);
                        customerBasePlan.get(0).setTotalvolumebasedunusedquota(customerVolumeBooster.get(0).getVolumebasedunusedquota());
                        customerVolumeBooster = new ArrayList<>();
                    } else {
                        customerVolumeBooster = customerVolumeBooster.stream().sorted(Comparator.comparing(CustomerPlanData::getEnddate)).collect(Collectors.toList());
                        customerBasePlan = new ArrayList<>();
                        customerBasePlan = customerVolumeBooster;
                        if (basePlanQos != null)
                            customerBasePlan.get(0).setQosPolicyGatewayMapping(basePlanQos);
                        customerVolumeBooster = new ArrayList<>();
                        customerBasePlan.get(0).setNotBasePlan(true);
                    }
                } else {
                    log.info("customer don't have volume booster plan");
                }
                if (!CollectionUtils.isEmpty(customerQuotaBooster)) {
                    customerQuotaBooster = customerQuotaBooster.stream().sorted(Comparator.comparing(CustomerPlanData::getQosspeed).reversed()).collect(Collectors.toList());
                    if (customerBasePlan.get(0).getCurrentsessionusagevolume() > 0 && acctStatusValue.equalsIgnoreCase("STOP")) {
                        //skip to add bandwidth booster as base plan session not end
                        isBasePlanActivatedForStop = true;
                        log.debug("Base Plan has current session and plan request for STOP, so skipp bandwidth booster");
                    } else if (isBandWidthBoosterWithQuota) {
                        customerBasePlan = new ArrayList<>();
                        customerBasePlan = customerQuotaBooster.stream().filter(customerPlanData -> customerPlanData.getVolumebasedtotalquota() > 0).collect(Collectors.toList());
                        customerBasePlan.get(0).setNotBasePlan(true);
                        customerBasePlan.get(0).setBasPlanQosPolicyGatewayMapping(basePlanQos);
                    } else {
                        log.debug("Skipp quota booster plan as quota plan don't have quota, but set qos speed");

                    }
                    if (isBandWidthBoosterWithOutQuota && isBasePlanHasVolumeQuota && customerQuotaBooster.get(0).getVolumebasedtotalquota() <= 0) {
                        log.info("Base Plan has Quota, applying Bandwidth Booster Quota Without Data plan speed");
                        customerBasePlan.get(0).setQosPolicyGatewayMapping(customerQuotaBooster.get(0).getQosPolicyGatewayMapping());
                    } else if (customerQuotaBooster.get(0).getVolumebasedtotalquota() <= 0 && !isBasePlanHasVolumeQuota) {
                        log.info("Base Plan Don't have Quota, skipp Bandwidth Booster Quota Without Data plan speed");
                    }

                    if (basePlanQos != null) {
                        List<QOSPolicyGatewayMapping> qosPolicyGatewayMapping = customerBasePlan.get(0).getQosPolicyGatewayMapping();
                        List<QOSPolicyGatewayMapping> qoutaQosPolicyGatewayMapping = customerQuotaBooster.get(0).getQosPolicyGatewayMapping();
                        if (qosPolicyGatewayMapping.size() == qoutaQosPolicyGatewayMapping.size()) {
                            log.info("Update Qos from plan: ");
                            for (int i = 0; i < qosPolicyGatewayMapping.size(); i++) {
                                // Set throttle speeds
                                qoutaQosPolicyGatewayMapping.get(i).setThrottleDownloadSpeed(qosPolicyGatewayMapping.get(i).getThrottleDownloadSpeed());
                                qoutaQosPolicyGatewayMapping.get(i).setThrottleUploadSpeed(qosPolicyGatewayMapping.get(i).getThrottleUploadSpeed());
                            }
                            customerQuotaBooster.get(0).setQosPolicyGatewayMapping(qoutaQosPolicyGatewayMapping);
                        }

                    }

                } else {
                    log.info("customer don't have Quoat booster plan");
                }


                if (!CollectionUtils.isEmpty(customerBasePlan))
                    custRetrunData.setCustomerBasePlan(customerBasePlan);
                if (!CollectionUtils.isEmpty(customerVolumeBooster))
                    custRetrunData.setCustomerVolueBooster(customerVolumeBooster);
                if (!CollectionUtils.isEmpty(customerQuotaBooster) && !isBasePlanActivatedForStop)
                    custRetrunData.setCustomerQuotaBooster(customerQuotaBooster);

            } else {
                log.error("Customer don't have plans: " + custRetrunData.getUsername());
            }

            if (custRetrunData != null && !CollectionUtils.isEmpty(custRetrunData.getCustomerQuotaBooster())) {
                if (custRetrunData.getCustomerQuotaBooster().get(0).isAddonToBase()) {
                    additionOfBaseQosInBandwithBosster(custRetrunData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Customer Plan Selection has a problem:" + e.getMessage());
        }
        return custRetrunData;
    }


    private static void additionOfBaseQosInBandwithBosster(CustomerData custRetrunData) {

        if (gatewayMap_pattern == null || gatewayMap_pattern.isEmpty()) {
            gatewayMap_pattern = initializeGatewayPattern();
        }

        if ("Bandwidthbooster".equals(custRetrunData.getCustomerQuotaBooster().get(0).getPlanGroup())) {
            List<QOSPolicyGatewayMapping> basePlanQos = custRetrunData.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping();
            List<QOSPolicyGatewayMapping> quotaBoosterQos = custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping();

            if (basePlanQos != null && !quotaBoosterQos.isEmpty() && basePlanQos.size() == quotaBoosterQos.size()) {
                log.info("Updating QoS speeds by combining base plan and bandwidth booster speeds");

                for (int i = 0; i < quotaBoosterQos.size(); i++) {
                    QOSPolicyGatewayMapping baseQos = basePlanQos.get(i);
                    QOSPolicyGatewayMapping boosterQos = quotaBoosterQos.get(i);
                    QOSPolicyGatewayMapping gatewayPattern = gatewayMap_pattern.get(baseQos.getGatewayName());

                    if (gatewayPattern != null) {
                        boosterQos.setDownloadSpeed(
                                mergeSpeed(baseQos.getDownloadSpeed(), boosterQos.getDownloadSpeed(),gatewayPattern.getDownloadSpeed()));
                        boosterQos.setUploadSpeed(
                                mergeSpeed(baseQos.getUploadSpeed(), boosterQos.getUploadSpeed(),gatewayPattern.getUploadSpeed()));

                        boosterQos.setBaseDownloadSpeed(
                                mergeSpeed(baseQos.getBaseDownloadSpeed(), boosterQos.getBaseDownloadSpeed(),gatewayPattern.getBaseDownloadSpeed()));
                        boosterQos.setBaseUploadSpeed(
                                mergeSpeed(baseQos.getBaseDownloadSpeed(), boosterQos.getBaseUploadSpeed(),gatewayPattern.getBaseUploadSpeed()));
                        boosterQos.setThrottleDownloadSpeed(
                                mergeSpeed(baseQos.getThrottleDownloadSpeed(), boosterQos.getThrottleDownloadSpeed(),gatewayPattern.getThrottleDownloadSpeed()));
                        boosterQos.setThrottleUploadSpeed(
                                mergeSpeed(baseQos.getThrottleUploadSpeed(), boosterQos.getThrottleUploadSpeed(),gatewayPattern.getThrottleUploadSpeed()));
                    }
                }

                custRetrunData.getCustomerQuotaBooster().get(0).setQosPolicyGatewayMapping(quotaBoosterQos);
                log.debug("Updated bandwidth booster QoS with combined speeds from base plan");
            }
        }
    }

    static String mergeSpeed(String base, String booster, String pattern) {
        if (pattern == null || pattern.isEmpty()) return booster;

        Pattern varPattern = Pattern.compile("var_(download|upload)");
        Matcher matcher = varPattern.matcher(pattern);
        List<String> tokens = new LinkedList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        double baseValue = extractFirstNumber(base);
        double boosterValue = extractFirstNumber(booster);
        double mergedValue = baseValue + boosterValue;
        String mergedString = (mergedValue % 1 == 0) ? String.valueOf((int) mergedValue) : String.valueOf(mergedValue);

        String merged = pattern;
        for (String token : tokens) {
            merged = merged.replace(token, mergedString);
        }

        return merged;
    }

    static double extractFirstNumber(String input) {
       // Matcher m = Pattern.compile("(\\d+)").matcher(input);
       // return m.find() ? Double.parseDouble(m.group(1)) : 0;

        Matcher m = Pattern.compile("\\d+(\\.\\d+)?").matcher(input);
        return m.find() ? Double.parseDouble(m.group()) : 0;
    }

        private static Map<String, QOSPolicyGatewayMapping> initializeGatewayPattern() {
            Map<String, QOSPolicyGatewayMapping> gatewayMapPattern = new HashMap<>();
            List<QOSPolicyGatewayMapping> qosPolicyGatewayMappings = fetchGatewayMappingsFromDB();

            for (QOSPolicyGatewayMapping mapping : qosPolicyGatewayMappings) {
                String gatewayName = mapping.getGatewayName();
                if (gatewayName != null && !gatewayName.isEmpty()) {
                    gatewayMapPattern.put(gatewayName, mapping);
                }
            }

            return gatewayMapPattern;
        }

        private static List<QOSPolicyGatewayMapping> fetchGatewayMappingsFromDB() {
            List<QOSPolicyGatewayMapping> mappings = new ArrayList<>();

            try (Connection conn = DataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(strQoSGatewayPattern);
                 ResultSet rset = stmt.executeQuery()) {

                while (rset.next()) {
                    QOSPolicyGatewayMapping mapping = new QOSPolicyGatewayMapping();
                    mapping.setGatewayName(rset.getString("name"));
                    mapping.setDownloadSpeed(rset.getString("download_speed"));
                    mapping.setUploadSpeed(rset.getString("upload_speed"));
                    mapping.setBaseDownloadSpeed(rset.getString("base_download_speed"));
                    mapping.setBaseUploadSpeed(rset.getString("base_upload_speed"));
                    mapping.setThrottleDownloadSpeed(rset.getString("throttle_download_speed"));
                    mapping.setThrottleUploadSpeed(rset.getString("throttle_upload_speed"));
                    mappings.add(mapping);
                }

            } catch (SQLException ex) {
                log.error("Error fetching QoS policy mappings from database", ex);
            }

            return mappings;
        }

    private void getPlanDetailCustomer(CustomerData custRetrunData, String acctStatusValue) throws JsonProcessingException {
        try {
            if (custRetrunData.getCustomerAllPlan() != null && custRetrunData.getCustomerAllPlan().size() > 0) {
                custRetrunData = evaluateAndAssignCustomerPlans(custRetrunData, acctStatusValue);

                double boosterVolumeQuota = 0d;
                int boosterTimeQuota = 0;
                boolean noBooster = true;
                boolean noTimeBase = true;

                if (!CollectionUtils.isEmpty(custRetrunData.getCustomerVolueBooster())) {
                    if (custRetrunData.getCustomerVolueBooster().get(0) != null) {
                        noBooster = false;
                    }
                }

                if (!CollectionUtils.isEmpty(custRetrunData.getCustomerQuotaBooster())) {
                    if (custRetrunData.getCustomerQuotaBooster().get(0) != null) {
                        noBooster = false;
                    }
                }

                if (custRetrunData.getCustomerBasePlan() == null) {
                    if (!CollectionUtils.isEmpty(custRetrunData.getCustomerAllPlan())) {
                        custRetrunData.setUsageQuotaType(custRetrunData.getCustomerAllPlan().get(0).getUsageQuotaType());
                    }
                    custRetrunData.setAuthStatus(false);
                    custRetrunData.setStrReplyMessage("Quota Consume/No Plan");
                    return;
                }

                log.debug("Time Policy Detail Booser Flag:" + noBooster + ":custRetrunData.getCustomerBasePlan().get(0):" + custRetrunData.getCustomerBasePlan().get(0) + ":custRetrunData.getCustomerBasePlan().get(0).getTimepolicyMap():" + custRetrunData.getCustomerBasePlan().get(0).getTimepolicyData());
                String basePolicyData = "";
                ObjectMapper objectMapper = new ObjectMapper();
                if (custRetrunData.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping() != null) {
                    basePolicyData = objectMapper.writeValueAsString(custRetrunData.getCustomerBasePlan().get(0).getBasPlanQosPolicyGatewayMapping());
                }
                if (noBooster && custRetrunData.getCustomerBasePlan().get(0) != null && custRetrunData.getCustomerBasePlan().get(0).getTimepolicyData() != null) {
                    ArrayList<TimepolicyData> timePolicy = (ArrayList<TimepolicyData>) custRetrunData.getCustomerBasePlan().get(0).getTimepolicyData();
                    log.debug("Time Policy Found:" + timePolicy);
                    RadiusUtility radUtil = new RadiusUtility();
                    Date date = new Date();
                    TimepolicyData matchePolicyTime = null;
                    DateFormat format = new SimpleDateFormat("HH:mm");
                    int currentone = radUtil.timepolicytoformula(LocalDate.now().getDayOfWeek().name(), format.format(date).toString());
                    for (int i = 0; i < timePolicy.size(); i++) {
                        TimepolicyData tbp = timePolicy.get(i);
                        log.debug("Time Policy Check From:" + tbp.getFromNumber() + ":To:" + tbp.getToNumber() + "Now:" + currentone);
                        if (tbp.getFromNumber() < tbp.getToNumber()) {
                            if (tbp.getFromNumber() <= currentone && tbp.getToNumber() >= currentone) {
                                matchePolicyTime = tbp;
                            }
                        } else {
                            if (tbp.getFromNumber() <= currentone && !(tbp.getToNumber() >= currentone)) {
                                matchePolicyTime = tbp;
                            }
                        }
                    }
                    if (matchePolicyTime != null) {
                        log.debug("Return is " + matchePolicyTime.getPolicyid());
                        if (matchePolicyTime.getAccess() == null) {
                            custRetrunData.setAuthStatus(false);
                            custRetrunData.setStrReplyMessage("Time Policy Not Matched");
                            custRetrunData.setFreeQuota(false);
                        } else {
                            custRetrunData.setFreeQuota(matchePolicyTime.isFreeQuota());
                            custRetrunData.setAuthStatus(true);
                            String strClass = "tpid=" + matchePolicyTime.getDetails_id();
                            custRetrunData.setStrClass(strClass);
//								if(matchePolicyTime.isFreeQuota())
                            if (matchePolicyTime.getQosPolicyGatewayMapping() != null && matchePolicyTime.getQosPolicyGatewayMapping().size() > 0) {
                                int timepolicysize = matchePolicyTime.getQosPolicyGatewayMapping().size();
                                int baseSize = custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().size();
                                int looping = 0;
                                if (timepolicysize < baseSize) {
                                    looping = timepolicysize;
                                } else {
                                    //code
                                    looping = baseSize;
                                }
                                log.debug("================Return is " + matchePolicyTime + ":looping:" + looping);

                                for (int i = 0; i < looping; i++) {
                                    if (custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i) != null && matchePolicyTime.getQosPolicyGatewayMapping().get(i) != null) {
                                        custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setDownloadSpeed(matchePolicyTime.getQosPolicyGatewayMapping().get(i).getDownloadSpeed());
                                        custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setUploadSpeed(matchePolicyTime.getQosPolicyGatewayMapping().get(i).getUploadSpeed());
                                        custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setBaseDownloadSpeed(matchePolicyTime.getQosPolicyGatewayMapping().get(i).getBaseDownloadSpeed());
                                        custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setBaseUploadSpeed(matchePolicyTime.getQosPolicyGatewayMapping().get(i).getBaseUploadSpeed());
                                        custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setThrottleDownloadSpeed(matchePolicyTime.getQosPolicyGatewayMapping().get(i).getThrottleDownloadSpeed());
                                        custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setThrottleUploadSpeed(matchePolicyTime.getQosPolicyGatewayMapping().get(i).getThrottleUploadSpeed());
                                        noBooster = false;
                                        noTimeBase = false;
                                    }

                                }
                            }
                        }
                    } else {
                        custRetrunData.setAuthStatus(true);
                        custRetrunData.setFreeQuota(false);
                    }
                }

                if (noBooster && custRetrunData.getCustomerBasePlan().get(0) != null) {
                    if (!CollectionUtils.isEmpty(custRetrunData.getCustomerBasePlan().get(0).getPlanQosPolicyMapping())) {
                        //Add dumy qsid so when quota used and plan qos set it will fire COA
                        String strClass = "qsid=" + custRetrunData.getCustomerBasePlan().get(0).getPlanQosPolicyMapping().get(0).getQosPolicy();
                        custRetrunData.setStrClass(strClass);
                        log.debug("Plan Qos Found for class: " + strClass);
                    }
                }
                if (noTimeBase && (!custRetrunData.getCustomerBasePlan().get(0).isUsagereached() || custRetrunData.getCustomerBasePlan().get(0).getVolumebasedunusedquota() == 0) && (custRetrunData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Data") || custRetrunData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {
                    double volumeQuota = 0d;
                    volumeQuota = custRetrunData.getCustomerBasePlan().get(0).getVolumebasedunusedquota();
                    double sessionUsedQuota = custRetrunData.getCustomerBasePlan().get(0).getCurrentsessionusagevolume();
                    RadiusUtility radiusUtility = new RadiusUtility();
//					sessionUsedQuota = radiusUtility.convertUsageToGivenUnit(sessionUsedQuota, custRetrunData.getCustomerBasePlan().get(0).getQuotaunit());
                    volumeQuota = volumeQuota - sessionUsedQuota;
                    log.debug("Volume Quota ==" + volumeQuota);
                    if (volumeQuota > 0d || !noBooster) {
                        if (custRetrunData.getCustomerBasePlan().get(0) != null) {
                            if (custRetrunData.getCustomerQuotaBooster() != null && custRetrunData.getCustomerQuotaBooster().get(0) != null) {
                                int boosterSize = custRetrunData.getCustomerQuotaBooster().size(), baseSize = custRetrunData.getCustomerBasePlan().size();
                                int looping = 0;

                                if (boosterSize < baseSize) {
                                    looping = boosterSize;
                                } else {
                                    looping = baseSize;
                                }

                                if (custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping() != null && custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().size() > 0) {
                                    for (int i = 0; i < looping; i++) {
                                        custRetrunData.getCustomerBasePlan().get(i).setQosPolicyGatewayMapping(custRetrunData.getCustomerQuotaBooster().get(i).getQosPolicyGatewayMapping());
                                    }
                                }

                                try {
                                    if (basePolicyData != null && !basePolicyData.isEmpty()) {
                                        List<QOSPolicyGatewayMapping> basPlanQosMapping = objectMapper.readValue(basePolicyData, new TypeReference<List<QOSPolicyGatewayMapping>>() {
                                        });
                                        if (!CollectionUtils.isEmpty(basPlanQosMapping)) {
                                            custRetrunData.getCustomerBasePlan().get(0).setBasPlanQosPolicyGatewayMapping(basPlanQosMapping);
                                        }
                                    }
                                } catch (Exception ex) {
                                    log.error("Exception while change string value to QOSPolicyGatewayMapping value for base plan speed");
                                }
                            }
                            log.info("Remaining volume quota : " + volumeQuota + " for customer plan: " + custRetrunData.getCustomerBasePlan().get(0).getPlanName());
                            custRetrunData.getCustomerBasePlan().get(0).setVolumequota(volumeQuota);
                        }
                    } else if (volumeQuota <= 0d && !custRetrunData.getCustomerBasePlan().get(0).isAllowoverusage()) {
                        //Nothing to do
                    } else if (noTimeBase) {
                        custRetrunData.getCustomerBasePlan().get(0).setVolumequota(volumeQuota);
                        if (noBooster) {
                            int looping = custRetrunData.getCustomerBasePlan().size();
                            for (int i = 0; i < looping; i++) {
                                if (custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i) != null && custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i) != null) {
                                    custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setDownloadSpeed(custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).getThrottleDownloadSpeed());
                                    custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setUploadSpeed(custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).getThrottleUploadSpeed());
                                    custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setBaseDownloadSpeed(custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).getBaseDownloadSpeed());
                                    custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setBaseUploadSpeed(custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).getBaseUploadSpeed());
                                    custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setThrottleDownloadSpeed(custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).getThrottleDownloadSpeed());
                                    custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setThrottleUploadSpeed(custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).getThrottleUploadSpeed());
                                }
                            }
                        }
                    }
                }

                if (noTimeBase && (!custRetrunData.getCustomerBasePlan().get(0).isUsagereached() || custRetrunData.getCustomerBasePlan().get(0).getTimebasedtotalquota() == 0) && (custRetrunData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Time") || custRetrunData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {
                    double timeQuota = 0d;
                    timeQuota = (int) (custRetrunData.getCustomerBasePlan().get(0).getTimebasedunusedquota());
                    log.debug("Time Quota==" + timeQuota);
                    if ((timeQuota <= 0) && !custRetrunData.getCustomerBasePlan().get(0).isAllowoverusage() && custRetrunData.isAuthStatus()) {
                        //Nothing to do
                    } else {
                        if (timeQuota > 0 || !noBooster) {
                            if (custRetrunData.getCustomerBasePlan().get(0) != null) {
                                if (custRetrunData.getCustomerQuotaBooster() != null && custRetrunData.getCustomerQuotaBooster().get(0) != null) {
                                    int boosterSize = custRetrunData.getCustomerQuotaBooster().size(), baseSize = custRetrunData.getCustomerBasePlan().size();
                                    int looping = 0;

                                    if (boosterSize < baseSize) {
                                        looping = boosterSize;
                                    } else {
                                        looping = baseSize;
                                    }

                                    for (int i = 0; i < looping; i++) {
                                        if (custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().size() > 0 && custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i) != null && custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().size() > 0 && custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().get(i) != null) {
                                            custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setDownloadSpeed(custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().get(i).getDownloadSpeed());
                                            custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setUploadSpeed(custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().get(i).getUploadSpeed());
                                            custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setBaseDownloadSpeed(custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().get(i).getBaseDownloadSpeed());
                                            custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setBaseUploadSpeed(custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().get(i).getBaseUploadSpeed());
                                            custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setThrottleDownloadSpeed(custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().get(i).getThrottleDownloadSpeed());
                                            custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setThrottleUploadSpeed(custRetrunData.getCustomerQuotaBooster().get(0).getQosPolicyGatewayMapping().get(i).getThrottleUploadSpeed());
                                        }
                                    }
                                }
                            }
                        } else {
                            if (noBooster) {
                                int looping = custRetrunData.getCustomerBasePlan().size();
                                for (int i = 0; i < looping; i++) {
                                    if (custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i) != null && custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i) != null) {
                                        custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setDownloadSpeed(custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).getThrottleDownloadSpeed());
                                        custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).setUploadSpeed(custRetrunData.getCustomerBasePlan().get(0).getQosPolicyGatewayMapping().get(i).getThrottleUploadSpeed());
                                    }
                                }
                            }
                        }
                    }
                }
                if (custRetrunData.getUsageQuotaType() != null) {
                    //skip
                } else {
                    custRetrunData.setUsageQuotaType("TOTAL");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Customer Plan Selection has a problem:" + e.getMessage());
        }
    }


    public CustomerData insertCustomer(CustomerData customerData) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET Insert Customer : %s :QUERY: %s", customerData.getUsername(), strRadiusCustomerInsertQuery));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        int nextCustId = 1;

        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusCustomerCountQuery);
            rset = stmt.executeQuery();
            if (rset.next()) {
                nextCustId = rset.getInt(1);
            }
            stmt = null;
            stmt = conn.prepareStatement(strRadiusCustomerInsertQuery);
            stmt.setLong(1, nextCustId);  // Set the next available custid dynamically
            stmt.setString(2, "Mr");
            stmt.setString(3, customerData.getUsername());
            stmt.setString(4, customerData.getPassword());
            stmt.setString(5, customerData.getUsername());
            stmt.setString(6, customerData.getUsername());
            stmt.setString(7, customerData.getUsername());
            stmt.setInt(8, 0);
            stmt.setInt(9, 1);
            stmt.setString(10, customerData.getUsername());
            stmt.setString(11, "Active");
            stmt.setInt(12, 0);
            stmt.setString(13, "Prepaid");
            stmt.setInt(14, customerData.getMvnoId());

            // Execute the INSERT query
            int rowsAffected = stmt.executeUpdate();
            customerData.setCustid(nextCustId);
            return customerData;
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e.fillInStackTrace());
            return null;
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Remove Mac from Customer Mac Mapping
     *
     * @param custid
     * @param MACID
     * @return
     * @throws RuntimeException
     */

    public boolean RemoveMAC(int custid, String MACID) throws RuntimeException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Delete MACProvision : %s mac: %s :QUERY: %s", custid, MACID, strRadiusCustomerMACDelete));
        }
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusCustomerMACDelete);
            stmt.setInt(1, custid);
            stmt.setString(2, MACID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e.fillInStackTrace());
            return false;
        } finally {

            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public boolean saveOrUpdateMac(int custid, String newMac, String oldMac, boolean isUpdate, Timestamp macRetentionDate) {
        Connection conn2 = null;
        PreparedStatement stmt2 = null;
        ResultSet resultSet = null;
        log.info(String.format("come to save or update mac Mac: %s, OldMac: %s, custId: %s, isUpdate: %s", newMac, oldMac, custid, isUpdate));
        try {
            if (isUpdate) {
                if (oldMac != null) {
                    log.info(String.format("come to get count for custome: %s and Mac: %s, query: %s", custid, newMac, strRadiusCustomerMac_MACCount));
                    conn2 = DataSource.getConnection();
                    stmt2 = conn2.prepareStatement(strRadiusCustomerMac_MACCount);
                    stmt2.setInt(1, custid);
                    stmt2.setString(2, normalizeMacAddress(newMac));
                    resultSet = stmt2.executeQuery();
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    if (count > 0) {
                        log.info(String.format("Update aborted: Duplicate MAC address: %s for the same customer: %s already exists", newMac, custid));
                        return false;
                    } else {
                        log.info(String.format("Updating Mac: %s, OldMac, custId: %s, query: %s", newMac, oldMac, custid, strRadiusCustomerMac_MACUpdate));
                        conn2 = DataSource.getConnection();
                        stmt2 = conn2.prepareStatement(strRadiusCustomerMac_MACUpdate);
                        stmt2.setInt(1, custid);
                        stmt2.setTimestamp(2, macRetentionDate);
                        stmt2.setString(3, newMac);
                        stmt2.setString(4, normalizeMacAddress(newMac));
                        stmt2.setString(5, oldMac);
                        stmt2.executeUpdate();
                        return true;
                    }
                } else {
                    log.info(String.format("Updating Mac: %s, custId: %s, query: %s", newMac, custid, strRadiusCustomer_MACUpdate));
                    conn2 = DataSource.getConnection();
                    stmt2 = conn2.prepareStatement(strRadiusCustomer_MACUpdate);
                    stmt2.setInt(1, custid);
                    stmt2.setTimestamp(2, macRetentionDate);
                    stmt2.setString(3, newMac);
                    stmt2.executeUpdate();
                    return true;
                }
            } else {
                log.info(String.format("Inserting Mac: %s, custId: %s, query: %s", newMac, custid, strRadiusCustomerMACProvsion));
                conn2 = DataSource.getConnection();
                stmt2 = conn2.prepareStatement(strRadiusCustomerMACProvsion);
                stmt2.setInt(1, custid);
                stmt2.setString(2, newMac);
                stmt2.setTimestamp(3, currentDate);
                stmt2.setTimestamp(4, currentDate);
                stmt2.setTimestamp(5, macRetentionDate);
                stmt2.setString(6, normalizeMacAddress(newMac));
                stmt2.executeUpdate();
                return true;
            }
        } catch (Exception ex) {
            log.info("Exception to save or update mac: " + newMac + ", user: " + custid + " isUpdate:" + isUpdate);
        } finally {
            log.info(String.format("Finally block update for save or update mac to close connection and statement."));
            try {
                if (stmt2 != null) stmt2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn2 != null) conn2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    public Integer noOfMacWithCustomer(int custid, String mac) {
        Integer count = 0;
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET mac count by customer : %s : mac: %s :QUERY: %s", custid, mac, strRadiusMACCountQuery));
        }

        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(strRadiusMACCountQuery)) {

            String macFormatted = normalizeMacAddress(mac);
            stmt.setString(1, macFormatted);

            try (ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    count = rset.getInt("macaddress");
                }
            }

        } catch (SQLException ex) {
            log.error("Error finding count of mac exists: " + mac, ex);
        }

        return count;
    }


    public boolean MACProvisioning(int custid, int mvnoid, String MACID, int concurrent, Timestamp macRetentionDate) throws RuntimeException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET MACProvision : %s :QUERY: %s", custid, strRadiusCustomerMACCountQuery));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        Connection conn2 = null;
        PreparedStatement stmt2 = null;

        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusMACCountQuery);
            String mac = normalizeMacAddress(MACID);
            stmt.setString(1, mac);
            rset = stmt.executeQuery();
            Integer intCount = 0;
            while (rset.next()) {
                intCount = rset.getInt("macaddress");
            }
            if (intCount <= 0 && concurrent > 0) {
                log.info(String.format("Inserting Mac: %s, custId: %s, query: %s", MACID, custid, strRadiusCustomerMACProvsion));
                conn2 = DataSource.getConnection();
                stmt2 = conn2.prepareStatement(strRadiusCustomerMACProvsion);
                stmt2.setInt(1, custid);
                stmt2.setString(2, MACID);
                stmt2.setTimestamp(3, currentDate);
                stmt2.setTimestamp(4, currentDate);
                stmt2.setTimestamp(5, macRetentionDate);
                stmt2.executeUpdate();
                return true;
            } else if (macRetentionDate != null) {
                log.info(String.format("Updating Mac: %s, custId: %s, query: %s", MACID, custid, strRadiusCustomer_MACUpdate));
                conn2 = DataSource.getConnection();
                stmt2 = conn2.prepareStatement(strRadiusCustomer_MACUpdate);
                stmt2.setInt(1, custid);
                stmt2.setTimestamp(2, macRetentionDate);
                stmt2.setString(3, MACID);
                stmt2.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e.fillInStackTrace());
            return false;
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt2 != null) stmt2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn2 != null) conn2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt2 != null) stmt2.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public HashMap getMacFromCustomerId(int custid) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET MACProvision : %s :QUERY: %s", custid, strRadiusCustomerMACCountQuery));
        }
        HashMap macMap = new HashMap();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusCustomerMACCountQuery);
            stmt.setInt(1, custid);
            // stmt.setInt(2,mvnoid);
            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s %s", RESULT_SET_IS, rset));
            }
            while (rset.next()) {
                macMap.put(rset.getString("macaddress"), rset.getTimestamp("createdate"));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return macMap;

    }

    public LiveUser getLiveUserFromSessionId(String acctSessionId, String acctMultSessionId, String nasIpAddress) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET Live session using acctSessionId: %s, acctMultSessionId: %s, nasIpAddress: %s, query: %s",
                    acctSessionId, acctMultSessionId, nasIpAddress, strGetLiveUsersession));
        }

        LiveUser liveUser = new LiveUser();
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(strGetLiveUsersession)) {

            stmt.setString(1, acctSessionId);
            stmt.setString(2, acctMultSessionId);
            stmt.setString(3, nasIpAddress);

            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    liveUser.setCdrID(rset.getLong("CDRID"));
                    liveUser.setUserName(rset.getString("UserName"));
                    liveUser.setNasIpAddress(rset.getString("NASIPAddress"));
                    liveUser.setFramedIpAddress(rset.getString("FramedIPAddress"));
                    liveUser.setlClass(rset.getString("Class"));
                    liveUser.setMvnoId(rset.getInt("mvnoid"));
                    liveUser.setCustid(rset.getString("custid"));
                    liveUser.setSourceipaddress(rset.getString("sourceipaddress"));
                    liveUser.setAcctInputOctets(rset.getString("AcctInputOctets"));
                    liveUser.setAcctOutputOctets(rset.getString("AcctOutputOctets"));
                    liveUser.setAcctSessionTime(rset.getString("AcctSessionTime"));
                    liveUser.setCprId(rset.getLong("cprid"));
                    liveUser.setAcctSessionId(acctSessionId);
                }
            }

            if (liveUser.getCustid() != null && !liveUser.getCustid().equalsIgnoreCase("0")) {
                try {
                    getTotalSessionQuota(liveUser.getCustid(), liveUser);
                } catch (Exception ex) {
                    log.error("Exception to fetch total quota by custId: " + liveUser.getCustid() + ", sessionId: " + liveUser.getAcctSessionId(), ex);
                }
            } else {
                liveUser.setTotalTime(0L);
                liveUser.setTotalQuota(0);
            }
        } catch (SQLException e) {
            log.error("SQL Exception occurred", e);
        }
        return liveUser;
    }

    public void getTotalSessionQuota(String custid, LiveUser liveUser) {
        if (log.isDebugEnabled()) {
            log.debug("getTotalSessionQuota:" + custid);
        }
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(strFetchTotalQuotaFromLiveUSerByCustId)) {
            if (liveUser != null && custid != null) {
                double totalInput = 0;
                double totalOutPut = 0;
                double totalTime = 0;
                stmt.setString(1, custid);
                try (ResultSet rset = stmt.executeQuery()) {
                    while (rset.next()) {
                        totalOutPut = totalOutPut + rset.getDouble("totalInput");
                        totalInput = totalInput + rset.getDouble("totalOutPut");
                        totalTime = totalTime + rset.getDouble("totalTime");
                    }
                    liveUser.setTotalTime(totalTime);
                    liveUser.setTotalQuota(totalInput + totalOutPut);
                }
            } else {
                liveUser.setTotalTime(0l);
                liveUser.setTotalQuota(0);
            }
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e);
        }
    }

    public boolean isCustomerExists(String username, Integer mvnoId) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN check Customer exists FROM Query : %s", strCustomerExistsByUserNameAndMvno));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        Integer intCount = 0;
        try {

            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));

            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strCustomerExistsByUserNameAndMvno);
            stmt.setString(1, username);
            stmt.setInt(2, mvnoId);
            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                intCount = rset.getInt("username");
            }
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return intCount > 0;
    }


    public void setValueFromEnvironments(String databaseUrl, String databaseUserName, String databasePassword) {
        log.info("********* DB Authentication Driver Class ***********");
        DBAuthenticationDriver.dbUrl = databaseUrl;
        log.info("Dynamic db url == " + DBAuthenticationDriver.dbUrl);
        DBAuthenticationDriver.dbUserName = databaseUserName;
        log.info("Dynamic db user == " + DBAuthenticationDriver.dbUserName);
        DBAuthenticationDriver.dbPassword = databasePassword;
        log.info("Dynamic db password == " + DBAuthenticationDriver.dbPassword);
    }

    /**
     * @param id
     * @param status
     */
    public boolean updateCustPlanStatus(Long id, String status, String endDate) {
        log.info("Perform Update of Cust Plan Status CPR ID :" + id + ": status:" + status + ": endDate: " + endDate + " query: " + strRadiusCustPlanStatus);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusCustPlanStatus);
            stmt.setString(1, endDate);
            stmt.setString(2, endDate);
            stmt.setLong(3, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e.fillInStackTrace());
            return false;
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public boolean updateCustPlanCOADMFLag(Integer id, boolean isCOADMTrigger) {
        //		System.out.println("Perform Update of Cust Plan Status CPR ID :" + id + ": status:" + status);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusCustPlanCOADMFlag);
            stmt.setBoolean(1, isCOADMTrigger);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e.fillInStackTrace());
            return false;
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public boolean isCustomerMapWithGivenMac(int custId, int mvnoid, String mac) {

        if (log.isDebugEnabled()) {
            log.debug(String.format("IN check Customer exists FROM Query : %s " + " for mac : %s", strRadiusCustomerMacCountQuery, mac));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        Integer intCount = 0;
        try {

            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusCustomerMacCountQuery);
            stmt.setInt(1, custId);
            stmt.setString(2, normalizeMacAddress(mac));
            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                intCount = rset.getInt("custid");
            }
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return intCount > 0;
    }

    public List<VLANManagement> getVlanManagementUsingMvno(Integer mvnoId) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CDR FROM DB : %s %s", mvnoId, strRadiusVlanManagementQuery));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        int intCount = 0;
        List<VLANManagement> vlanManagements = new ArrayList<>();
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusVlanManagementQuery);
            stmt.setInt(1, mvnoId);

            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                VLANManagement vlanManagement = getVlanDetailsFrom(rset);
                vlanManagements.add(vlanManagement);
            }
        } catch (SQLException | ClassNotFoundException e) {
            log.error(SQL_EXCEPTION, e);
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return vlanManagements;
    }

    public VLANManagement getVlanDetailsFrom(ResultSet rset) throws SQLException {
        VLANManagement vlanManagement = new VLANManagement();
        // Map all the fields
        vlanManagement.setVlanId(rset.getLong("VLANID"));
        vlanManagement.setVlanName(rset.getString("VLAN_NAME"));
        vlanManagement.setNasType(rset.getString("NAS_TYPE"));
        vlanManagement.setCircuitType(rset.getString("CIRCUIT_TYPE"));
        vlanManagement.setNasIdentifier(rset.getString("NAS_IDENTIFIER"));
        vlanManagement.setNasPortId1(rset.getString("NAS_PORT_ID_1"));
        vlanManagement.setNasPortId2(rset.getString("NAS_PORT_ID_2"));
        vlanManagement.setNasPortId3(rset.getString("NAS_PORT_ID_3"));
        vlanManagement.setNasPortId4(rset.getString("NAS_PORT_ID_4"));
        vlanManagement.setNasPortId5(rset.getString("NAS_PORT_ID_5"));
        vlanManagement.setCallingStationId(rset.getString("CALLING_STATION_ID"));
        vlanManagement.setContextName(rset.getString("CONTEXT_NAME"));
        vlanManagement.setFilterId(rset.getString("FILTER_ID"));
        vlanManagement.setForwardPolicy(rset.getString("FORWARD_POLICY"));
        vlanManagement.setHttpRedirectProfileName(rset.getString("HTTP_REDIRECT_PROFILE_NAME"));
        vlanManagement.setRateLimitRate(rset.getString("RATE_LIMIT_RATE"));
        vlanManagement.setRateLimitBurst(rset.getString("RATE_LIMIT_BURST"));
        vlanManagement.setQosPolicingPolicyName(rset.getString("QOS_POLICING_POLICY_NAME"));
        vlanManagement.setQosMeteringPolicyName(rset.getString("QOS_METERING_POLICY_NAME"));
        vlanManagement.setPppoeUrl(rset.getString("PPPOE_URL"));
        vlanManagement.setPppDnsPrimary(rset.getString("PPP_DNS_PRIMARY"));
        vlanManagement.setPppDnsSecondary(rset.getString("PPP_DNS_SECONDARY"));
        vlanManagement.setPppNbnsPrimary(rset.getString("PPP_NBNS_PRIMARY"));
        vlanManagement.setSessionTimeOut(rset.getString("SESSION_TIMEOUT"));
        vlanManagement.setIdleTimeOut(rset.getString("IDLE_TIMEOUT"));
        vlanManagement.setFramedIpAddress(rset.getString("FRAMED_IP_ADDRESS"));
        vlanManagement.setRbDhcpMaxLeases(rset.getString("RB_DHCP_MAX_LEASES"));
        vlanManagement.setIpAddressPoolName(rset.getString("IP_ADDRESS_POOL_NAME"));
        vlanManagement.setNatProfileName(rset.getString("NAT_PROFILE_NAME"));
        vlanManagement.setRbInterfaceName(rset.getString("RB_INTERFACE_NAME"));
        vlanManagement.setHttpRedirectUrl(rset.getString("HTTP_REDIRECT_URL"));
        vlanManagement.setFramedIpv6Prefix(rset.getString("FRAMED_IPV6_PREFIX"));
        vlanManagement.setDelegatedIpv6Prefix(rset.getString("DELEGATED_IPV6_PREFIX"));
        vlanManagement.setFramedInterfaceId(rset.getString("FRAMED_INTERFACE_ID"));
        vlanManagement.setFramedIpv6Pool(rset.getString("FRAMED_IPV6_POOL"));
        vlanManagement.setIpv6Option(rset.getString("IPV6_OPTION"));
        vlanManagement.setIpv6Dns(rset.getString("IPV6_DNS"));
        vlanManagement.setDelegatedMaxPrefix(rset.getString("DELEGATED_MAX_PREFIX"));
        vlanManagement.setDelegatedIpv6Pool(rset.getString("DELEGATED_IPV6_POOL"));
        vlanManagement.setSubProfile(rset.getString("SUB_PROFILE"));
        vlanManagement.setPriority(rset.getLong("PRIORITY"));
        vlanManagement.setMvnoId(rset.getInt("mvnoid"));
        vlanManagement.setCreatedOn(rset.getTimestamp("createdate"));
        vlanManagement.setLastModifiedOn(rset.getTimestamp("lastmodificationdate"));
        return vlanManagement;
    }

    public List<VLANValidationMapping> getVlanValidationMapping(String vlanId) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CDR FROM DB : %s %s", vlanId, strRadiusVlanValidationMappingQuery));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        int intCount = 0;
        List<VLANValidationMapping> vlanValidationMappings = new ArrayList<>();
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusVlanValidationMappingQuery);
            stmt.setString(1, vlanId);

            rset = stmt.executeQuery();
//            if (log.isDebugEnabled()) {
//                log.debug(String.format("Result Set is %s", rset));
//            }
            while (rset.next()) {
                VLANValidationMapping vlanManagement = new VLANValidationMapping();
                vlanManagement.setRegex(rset.getString("REGEX"));
                vlanManagement.setRadiusAttribute(rset.getString("RADIUS_ATTRIBUTE"));
                vlanValidationMappings.add(vlanManagement);
            }
        } catch (SQLException | ClassNotFoundException e) {
            log.error(SQL_EXCEPTION, e);
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return vlanValidationMappings;
    }

    public boolean updateLastUsageDateInCustomerMac(CustomerData customerData, String mac, Timestamp macRetentionDate) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN UPDATE LAST USAGE DATE FOR MAC : %s AND CUSTOMER : %s, SQL: %s", mac, customerData.getUsername(), strRadiusUpdateMacMappingLastUsageDate));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusUpdateMacMappingLastUsageDate);
            stmt.setTimestamp(1, macRetentionDate);
            stmt.setString(2, String.valueOf(customerData.getCustid()));
            stmt.setString(3, mac);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while update LastUsageDate for Customer MAC: " + mac + " exception: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public boolean updateLastConnectedInFaultyMac(Long id, String mac, String lastConnected) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("UPDATE LAST CONNECTED FOR FAULTY MAC : %s, SQL: %s", mac, strUpdateFaultyMacLastConnected));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strUpdateFaultyMacLastConnected);
            stmt.setString(1, lastConnected);
            stmt.setLong(2, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e.fillInStackTrace());
            return false;
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public VLANManagement getVlanBasedOnRegex(String value) throws SQLException {
        String sql = "SELECT vm.* FROM savbillradius.TBLMVLANMANAGEMENT vm " +
                "JOIN savbillradius.tblmvlanvalidationmapping vvm ON vm.VLANID = vvm.VLANID " +
                "WHERE vvm.regex like CONCAT('%', ?, '%') AND ? REGEXP vvm.REGEX ORDER BY PRIORITY LIMIT 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            String partialValue="";
            try {
                String[] strings = value.split(" ");
                partialValue=strings[2];
            }
            catch(Exception e)
            {
            e.printStackTrace();
            }

            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, partialValue);
            stmt.setString(2, value);
            ResultSet rs = stmt.executeQuery();
            VLANManagement vlan = new VLANManagement();
            while (rs.next()){
                vlan.setVlanId(rs.getLong("VLANID"));
                vlan.setVlanName(rs.getString("VLAN_NAME"));
                vlan.setNasType(rs.getString("NAS_TYPE"));
            }
            return vlan;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            stmt.close();
            conn.close();
        }
        return null;
    }



    public boolean storeCOAResponseAudit(String nasIpAddress, String coaPacket, String coaResponse, String reason, Integer mvnoId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        try {
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strRadiusCOAResponseAudit);
            stmt.setString(1, nasIpAddress);
            stmt.setString(2, coaPacket);
            stmt.setString(3, coaResponse);
            stmt.setString(4, reason);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, mvnoId);
            stmt.setString(7, coaResponse.toString());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error(SQL_EXCEPTION, e.fillInStackTrace());
            return false;
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

}
