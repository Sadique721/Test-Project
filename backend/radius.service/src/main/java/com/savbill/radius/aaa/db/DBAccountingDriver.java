package com.savbill.radius.aaa.db;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.config.DbConfig;
import com.savbill.radius.dto.CustomerQuotaDTO;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.kafka.message.CustomerQuotaInfo;
import com.savbill.radius.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


public class DBAccountingDriver {
    public static String dbUrl;
    public static String dbUserName;
    public static String dbPassword;

    private static final String SQL_EXCEPTION = "SQLException";

    private static final String QUERY = "QUERY";
    private Timestamp currentDate = (new Timestamp(new Date().getTime()));


    private static String strRadiusAccountInsert = "insert into tbltacctcdr(UserName,NASIPAddress,NASPort,ServiceType,FramedProtocol,FramedIPAddress,FramedIPNetmask,FramedRouting,FilterId,FramedMTU,FramedCompression,LoginIPHost,"
            + "LoginService,LoginTCPPort,ReplyMessage,CallbackNumber,CallbackId,FramedRoute,FramedIPXNetwork,State,Class,VendorSpecific,SessionTimeout,IdleTimeout,TerminationAction,CalledStationId,CallingStationId,"
            + "NASIdentifier,ProxyState,LoginLATService,LoginLATNode,LoginLATGroup,FramedAppleTalkLink,FramedAppleTalkNetwork,FramedAppleTalkZone,AcctStatusType,AcctDelayTime,AcctInputOctets,AcctOutputOctets,AcctSessionId,"
            + "AcctAuthentic,AcctSessionTime,AcctInputPackets,AcctOutputPackets,AcctTerminateCause,AcctMultiSessionId,AcctLinkCount,AcctInputGigawords,AcctOutputGigawords,EventTimestamp,CHAPChallenge,NASPortType,PortLimit,"
            + "LoginLATPort,AcctTunnelConnection,ARAPFeatures,ARAPZoneAccess,ARAPSecurity,ARAPSecurityData,PasswordRetry,Prompt,ConnectInfo,ConfigurationToken,EAPMessage,MessageAuthenticator,ARAPChallengeResponse,"
            + "AcctInterimInterval,NASPortId,FramedPool,NASIPv6Address,FramedInterfaceId,FramedIPv6Prefix,LoginIPv6Host,FramedIPv6Route,FramedIPv6Pool,DigestResponse,DigestAttributes,framedipv6address,DelegatedIPv6Prefix,addl1,addl2,custid,createdate,lastmodificationdate,mvnoid,cprid) values("
            + ":UserName,:NASIPAddress,:NASPort,:ServiceType,:FramedProtocol,:FramedIPAddress,:FramedIPNetmask,:FramedRouting,:FilterId,:FramedMTU,:FramedCompression,:LoginIPHost,:LoginService,"
            + ":LoginTCPPort,:ReplyMessage,:CallbackNumber,:CallbackId,:FramedRoute,:FramedIPXNetwork,:State,:Class,:VendorSpecific,:SessionTimeout,:IdleTimeout,:TerminationAction,:CalledStationId,:CallingStationId,"
            + ":NASIdentifier,:ProxyState,:LoginLATService,:LoginLATNode,:LoginLATGroup,:FramedAppleTalkLink,:FramedAppleTalkNetwork,:FramedAppleTalkZone,:AcctStatusType,:AcctDelayTime,:AcctInputOctets,:AcctOutputOctets,"
            + ":AcctSessionId,:AcctAuthentic,:AcctSessionTime,:AcctInputPackets,:AcctOutputPackets,:AcctTerminateCause,:AcctMultiSessionId,:AcctLinkCount,:AcctInputGigawords,:AcctOutputGigawords,:EventTimestamp,:CHAPChallenge,"
            + ":NASPortType,:PortLimit,:LoginLATPort,:AcctTunnelConnection,:ARAPFeatures,:ARAPZoneAccess,:ARAPSecurity,:ARAPSecurityData,:PasswordRetry,:Prompt,:ConnectInfo,:ConfigurationToken,:EAPMessage,"
            + ":MessageAuthenticator,:ARAPChallengeResponse,:AcctInterimInterval,:NASPortId,:FramedPool,:NASIPv6Address,:FramedInterfaceId,:FramedIPv6Prefix,:LoginIPv6Host,:FramedIPv6Route,:FramedIPv6Pool,:DigestResponse,:DigestAttributes,:framedipv6address,:DelegatedIPv6Prefix,:addl1,:addl2,:custid,:createdate,:lastmodificationdate,:mvnoid,:cprid)";


    private static String strLiveSessionInsert = "insert into tbltliveuser(UserName,NASIPAddress,NASPort,ServiceType,FramedProtocol,FramedIPAddress,FramedIPNetmask,FramedRouting,FilterId,FramedMTU,FramedCompression,LoginIPHost,"
            + "LoginService,LoginTCPPort,ReplyMessage,CallbackNumber,CallbackId,FramedRoute,FramedIPXNetwork,State,Class,VendorSpecific,SessionTimeout,IdleTimeout,TerminationAction,CalledStationId,CallingStationId,"
            + "NASIdentifier,ProxyState,LoginLATService,LoginLATNode,LoginLATGroup,FramedAppleTalkLink,FramedAppleTalkNetwork,FramedAppleTalkZone,AcctStatusType,AcctDelayTime,AcctInputOctets,AcctOutputOctets,AcctSessionId,"
            + "AcctAuthentic,AcctSessionTime,AcctInputPackets,AcctOutputPackets,AcctTerminateCause,AcctMultiSessionId,AcctLinkCount,AcctInputGigawords,AcctOutputGigawords,EventTimestamp,CHAPChallenge,NASPortType,PortLimit,"
            + "LoginLATPort,AcctTunnelConnection,ARAPFeatures,ARAPZoneAccess,ARAPSecurity,ARAPSecurityData,PasswordRetry,Prompt,ConnectInfo,ConfigurationToken,EAPMessage,MessageAuthenticator,ARAPChallengeResponse,"
            + "AcctInterimInterval,NASPortId,FramedPool,NASIPv6Address,FramedInterfaceId,FramedIPv6Prefix,LoginIPv6Host,FramedIPv6Route,FramedIPv6Pool,DigestResponse,DigestAttributes,framedipv6address,DelegatedIPv6Prefix,addl1,addl2,custid,createdate,lastmodificationdate,sourceipaddress,mvnoid,clientgroupid,isthrottlespeed,cprid) values("
            + ":UserName,:NASIPAddress,:NASPort,:ServiceType,:FramedProtocol,:FramedIPAddress,:FramedIPNetmask,:FramedRouting,:FilterId,:FramedMTU,:FramedCompression,:LoginIPHost,:LoginService,"
            + ":LoginTCPPort,:ReplyMessage,:CallbackNumber,:CallbackId,:FramedRoute,:FramedIPXNetwork,:State,:Class,:VendorSpecific,:SessionTimeout,:IdleTimeout,:TerminationAction,:CalledStationId,:CallingStationId,"
            + ":NASIdentifier,:ProxyState,:LoginLATService,:LoginLATNode,:LoginLATGroup,:FramedAppleTalkLink,:FramedAppleTalkNetwork,:FramedAppleTalkZone,:AcctStatusType,:AcctDelayTime,:AcctInputOctets,:AcctOutputOctets,"
            + ":AcctSessionId,:AcctAuthentic,:AcctSessionTime,:AcctInputPackets,:AcctOutputPackets,:AcctTerminateCause,:AcctMultiSessionId,:AcctLinkCount,:AcctInputGigawords,:AcctOutputGigawords,:EventTimestamp,:CHAPChallenge,"
            + ":NASPortType,:PortLimit,:LoginLATPort,:AcctTunnelConnection,:ARAPFeatures,:ARAPZoneAccess,:ARAPSecurity,:ARAPSecurityData,:PasswordRetry,:Prompt,:ConnectInfo,:ConfigurationToken,:EAPMessage,"
            + ":MessageAuthenticator,:ARAPChallengeResponse,:AcctInterimInterval,:NASPortId,:FramedPool,:NASIPv6Address,:FramedInterfaceId,:FramedIPv6Prefix,:LoginIPv6Host,:FramedIPv6Route,:FramedIPv6Pool,:DigestResponse,:DigestAttributes,:framedipv6address,:DelegatedIPv6Prefix,:addl1,:addl2,:custid,:createdate,:lastmodificationdate,:sourceipaddress,:mvnoid,:clientgroupid,:isthrottlespeed,:cprid)";

    private static String strLiveSessionUpdate = "update tbltliveuser set UserName=:UserName,NASIPAddress=:NASIPAddress,NASPort=:NASPort,ServiceType=:ServiceType,FramedProtocol=:FramedProtocol,FramedIPAddress=:FramedIPAddress,"
            + "lastsessionoutputquota=IFNULL(AcctOutputOctets, 0),lastsessioninputquota=IFNULL(AcctInputOctets, 0),lastsessionquotatime=IFNULL(AcctSessionTime, 0),FramedIPNetmask=:FramedIPNetmask,FramedRouting=:FramedRouting,FilterId=:FilterId,FramedMTU=:FramedMTU,FramedCompression=:FramedCompression,LoginIPHost=:LoginIPHost,LoginService=:LoginService,LoginTCPPort=:LoginTCPPort,"
            + "ReplyMessage=:ReplyMessage,CallbackNumber=:CallbackNumber,CallbackId=:CallbackId,FramedRoute=:FramedRoute,FramedIPXNetwork=:FramedIPXNetwork,State=:State,Class=:Class,VendorSpecific=:VendorSpecific,"
            + "SessionTimeout=:SessionTimeout,IdleTimeout=:IdleTimeout,TerminationAction=:TerminationAction,CalledStationId=:CalledStationId,CallingStationId=:CallingStationId,NASIdentifier=:NASIdentifier,ProxyState=:ProxyState,"
            + "LoginLATService=:LoginLATService,LoginLATNode=:LoginLATNode,LoginLATGroup=:LoginLATGroup,FramedAppleTalkLink=:FramedAppleTalkLink,FramedAppleTalkNetwork=:FramedAppleTalkNetwork,FramedAppleTalkZone=:FramedAppleTalkZone,"
            + "AcctStatusType=:AcctStatusType,AcctDelayTime=:AcctDelayTime,AcctInputOctets=:AcctInputOctets,AcctOutputOctets=:AcctOutputOctets,AcctSessionId=:AcctSessionId,AcctAuthentic=:AcctAuthentic,AcctSessionTime=:AcctSessionTime,"
            + "AcctInputPackets=:AcctInputPackets,AcctOutputPackets=:AcctOutputPackets,AcctTerminateCause=:AcctTerminateCause,AcctMultiSessionId=:AcctMultiSessionId,AcctLinkCount=:AcctLinkCount,AcctInputGigawords=:AcctInputGigawords,"
            + "AcctOutputGigawords=:AcctOutputGigawords,EventTimestamp=:EventTimestamp,CHAPChallenge=:CHAPChallenge,NASPortType=:NASPortType,PortLimit=:PortLimit,LoginLATPort=:LoginLATPort,AcctTunnelConnection=:AcctTunnelConnection,"
            + "ARAPFeatures=:ARAPFeatures,ARAPZoneAccess=:ARAPZoneAccess,ARAPSecurity=:ARAPSecurity,ARAPSecurityData=:ARAPSecurityData,PasswordRetry=:PasswordRetry,Prompt=:Prompt,ConnectInfo=:ConnectInfo,ConfigurationToken=:ConfigurationToken,"
            + "EAPMessage=:EAPMessage,MessageAuthenticator=:MessageAuthenticator,ARAPChallengeResponse=:ARAPChallengeResponse,AcctInterimInterval=:AcctInterimInterval,NASPortId=:NASPortId,FramedPool=:FramedPool,"
            + "NASIPv6Address=:NASIPv6Address,FramedInterfaceId=:FramedInterfaceId,FramedIPv6Prefix=:FramedIPv6Prefix,LoginIPv6Host=:LoginIPv6Host,FramedIPv6Route=:FramedIPv6Route,FramedIPv6Pool=:FramedIPv6Pool,DigestResponse=:DigestResponse,"
            + "DigestAttributes=:DigestAttributes , framedipv6address=:framedipv6address,DelegatedIPv6Prefix=:DelegatedIPv6Prefix , lastmodificationdate = now(), lastsessionquotavolume=:lastsessionquotavolume, isthrottlespeed=:isthrottlespeed where AcctSessionId=:AcctSessionId and AcctMultiSessionId=:AcctMultiSessionId";


    private static String strUpdateCustomerQuota = "update tblcustomers cus,tblcustquotadtls cquota set timequotaused=?,usedquota=?,currentsessionusagetime=?,currentsessionusagevolume=? where cus.custid=cquota.custid and username=? and cquota.custpackageid=?";

    private static String strUpdateCustomerQuotaWithSkipQuotaUpdate = "update tblcustomers cus,tblcustquotadtls cquota set currentsessionusagetime=0,currentsessionusagevolume=0,usedquota=0,timequotaused=0, isquotaupdateskipped = false where cus.custid=cquota.custid and username=? and cquota.custpackageid=?";
    private static String strTotalReservedQuota = "update tblcustomers cus,tblcustquotadtls cquota set total_reserved_quota=? where cus.custid=cquota.custid and username=?";

    private static String strUpdateCustomerInSessionUsage = "update tblcustquotadtls cquota set currentsessionusagetime=?,currentsessionusagevolume=? where cquota.custpackageid=?";

    private static String strDeleteLiveUser = "delete from  tbltliveuser where NASIPAddress=? and AcctSessionId=? and AcctMultiSessionId=?";

    private static String strFetchLiveUserByMac = "select * from  tbltliveuser where REPLACE(REPLACE(REPLACE(CallingStationId, '-', ''), ':', ''), '.', '')=?";
    //REPLACE(REPLACE(REPLACE(CallingStationId, '-', ''), ':', ''), '.', '')
    private static String strFetchLiveUserByMacAndNasIp = "select * from  tbltliveuser where CallingStationId=? and NASIPAddress=?";

    private static String strDeleteLiveUserByMac = "delete from  tbltliveuser where CallingStationId=?";

    private static String strGetNoofUsersession = "select count(1) noofsession from tbltliveuser where UserName=? and CallingStationId!=? and clientgroupid=?";

    private static String strGetNoofUsersessionByCustId = "select count(1) noofsession from tbltliveuser where clientgroupid=? and custid=?";

    private static String strExistsNoofUsersession = "select count(1) noofsession from tbltliveuser where AcctSessionId=? and AcctMultiSessionId=?";

    private static String strReservedQuotaInsert = "insert into tblreservedquotadtls(cust_id,custquotadtlsid,used_quota,unused_quota,reserved_quota,parent_cust_id) values(?,?,?,?,?,?)";

    private static String strCheckQuotaAvailable = "select count(*) totalcust from tblreservedquotadtls t where cust_id=?";

    private static String strReservedQuotaupdate = "update tblreservedquotadtls cus set used_quota=?, unused_quota=? where cus.cust_id=?";

    private static String strReservedQuotaDelete = "DELETE FROM tblreservedquotadtls WHERE cust_id=?";

    private static String strGetSessionUsage = "select timetotalquota, totalquota, currentsessionusagetime, currentsessionusagevolume, timequotaused, usedquota from tblcustquotadtls where custid=? and custpackageid = ? and (currentsessionusagetime > 0 or currentsessionusagevolume > 0)";
    private static String strCustomerPackageRelationId = "SELECT custpackageid FROM tblcustpackagerel where custid = ? and cust_plan_status = 'ACTIVE'";

    private static String strUpdateVlanLastAuthDate = "UPDATE tblmvlanmanagement SET lastauthmatched=? WHERE VLANID=?";

    private static String strUpdateSkipQuotaFlag = "update tblcustquotadtls t set t.isquotaupdateskipped = false,currentsessionusagetime=0,currentsessionusagevolume=0 where t.custpackageid=?";
    private static String strFetchMacFromCustId = "SELECT macaddress FROM tbltmacaddressmapping WHERE custid = ?";

    private static String strFetchLiveSessionByNASIdentifier = "select CDRID from tbltliveuser t where NASIdentifier = ?";
    private static String strFetchLiveSessionByNASIPAddress = "select CDRID from tbltliveuser t where NASIPAddress = ?";
    private static String strFetchLiveSessionByCustId = "select CDRID,UserName,Class,NASIPAddress,FramedIPAddress, CallingStationId,NASPort, sourceipaddress,AcctInputOctets, AcctOutputOctets,AcctSessionTime,cprid,AcctSessionId from savbillradius.tbltliveuser t where custid = ?";

    private static String strInsertCustomerQuotaReset = "INSERT INTO tblcustquotaresetdtls (quotaunit, custid, cprid, totalquotaused, totaltimequota, createdate, lastmodifieddate) " +
            "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
    //NOW() BETWEEN cpp.startdate AND cpp.enddate

    private static String strLiveSessionUpsert =
            "INSERT INTO tbltliveuser (" +
                    "UserName, NASIPAddress, NASPort, ServiceType, FramedProtocol, FramedIPAddress, FramedIPNetmask, FramedRouting, FilterId, FramedMTU, " +
                    "FramedCompression, LoginIPHost, LoginService, LoginTCPPort, ReplyMessage, CallbackNumber, CallbackId, FramedRoute, FramedIPXNetwork, State, " +
                    "Class, VendorSpecific, SessionTimeout, IdleTimeout, TerminationAction, CalledStationId, CallingStationId, NASIdentifier, ProxyState, LoginLATService, " +
                    "LoginLATNode, LoginLATGroup, FramedAppleTalkLink, FramedAppleTalkNetwork, FramedAppleTalkZone, AcctStatusType, AcctDelayTime, AcctInputOctets, AcctOutputOctets, " +
                    "AcctSessionId, AcctAuthentic, AcctSessionTime, AcctInputPackets, AcctOutputPackets, AcctTerminateCause, AcctMultiSessionId, AcctLinkCount, AcctInputGigawords, AcctOutputGigawords, " +
                    "EventTimestamp, CHAPChallenge, NASPortType, PortLimit, LoginLATPort, AcctTunnelConnection, ARAPFeatures, ARAPZoneAccess, ARAPSecurity, ARAPSecurityData, " +
                    "PasswordRetry, Prompt, ConnectInfo, ConfigurationToken, EAPMessage, MessageAuthenticator, ARAPChallengeResponse, AcctInterimInterval, NASPortId, FramedPool, " +
                    "NASIPv6Address, FramedInterfaceId, FramedIPv6Prefix, LoginIPv6Host, FramedIPv6Route, FramedIPv6Pool, DigestResponse, DigestAttributes, framedipv6address, DelegatedIPv6Prefix, " +
                    "addl1, addl2, custid, createdate, lastmodificationdate, sourceipaddress, mvnoid, clientgroupid, isthrottlespeed,cprid" +
                    ") VALUES (" +
                    ":UserName, :NASIPAddress, :NASPort, :ServiceType, :FramedProtocol, :FramedIPAddress, :FramedIPNetmask, :FramedRouting, :FilterId, :FramedMTU, " +
                    ":FramedCompression, :LoginIPHost, :LoginService, :LoginTCPPort, :ReplyMessage, :CallbackNumber, :CallbackId, :FramedRoute, :FramedIPXNetwork, :State, " +
                    ":Class, :VendorSpecific, :SessionTimeout, :IdleTimeout, :TerminationAction, :CalledStationId, :CallingStationId, :NASIdentifier, :ProxyState, :LoginLATService, " +
                    ":LoginLATNode, :LoginLATGroup, :FramedAppleTalkLink, :FramedAppleTalkNetwork, :FramedAppleTalkZone, :AcctStatusType, :AcctDelayTime, :AcctInputOctets, :AcctOutputOctets, " +
                    ":AcctSessionId, :AcctAuthentic, :AcctSessionTime, :AcctInputPackets, :AcctOutputPackets, :AcctTerminateCause, :AcctMultiSessionId, :AcctLinkCount, :AcctInputGigawords, :AcctOutputGigawords, " +
                    ":EventTimestamp, :CHAPChallenge, :NASPortType, :PortLimit, :LoginLATPort, :AcctTunnelConnection, :ARAPFeatures, :ARAPZoneAccess, :ARAPSecurity, :ARAPSecurityData, " +
                    ":PasswordRetry, :Prompt, :ConnectInfo, :ConfigurationToken, :EAPMessage, :MessageAuthenticator, :ARAPChallengeResponse, :AcctInterimInterval, :NASPortId, :FramedPool, " +
                    ":NASIPv6Address, :FramedInterfaceId, :FramedIPv6Prefix, :LoginIPv6Host, :FramedIPv6Route, :FramedIPv6Pool, :DigestResponse, :DigestAttributes, :framedipv6address, :DelegatedIPv6Prefix, " +
                    ":addl1, :addl2, :custid, :createdate, now(), :sourceipaddress, :mvnoid, :clientgroupid, :isthrottlespeed,:cprid" +
                    ") ON DUPLICATE KEY UPDATE " +
                    "lastsessionoutputquota=IFNULL(AcctOutputOctets, 0),lastsessioninputquota=IFNULL(AcctInputOctets, 0),lastsessionquotatime=IFNULL(AcctSessionTime, 0),UserName=:UserName, NASIPAddress=:NASIPAddress, NASPort=:NASPort, ServiceType=:ServiceType, FramedProtocol=:FramedProtocol, FramedIPAddress=:FramedIPAddress, " +
                    "FramedIPNetmask=:FramedIPNetmask, FramedRouting=:FramedRouting, FilterId=:FilterId, FramedMTU=:FramedMTU, FramedCompression=:FramedCompression, LoginIPHost=:LoginIPHost, " +
                    "LoginService=:LoginService, LoginTCPPort=:LoginTCPPort, ReplyMessage=:ReplyMessage, CallbackNumber=:CallbackNumber, CallbackId=:CallbackId, FramedRoute=:FramedRoute, " +
                    "FramedIPXNetwork=:FramedIPXNetwork, State=:State, Class=:Class, VendorSpecific=:VendorSpecific, SessionTimeout=:SessionTimeout, IdleTimeout=:IdleTimeout, " +
                    "TerminationAction=:TerminationAction, CalledStationId=:CalledStationId, CallingStationId=:CallingStationId, NASIdentifier=:NASIdentifier, ProxyState=:ProxyState, " +
                    "LoginLATService=:LoginLATService, LoginLATNode=:LoginLATNode, LoginLATGroup=:LoginLATGroup, FramedAppleTalkLink=:FramedAppleTalkLink, FramedAppleTalkNetwork=:FramedAppleTalkNetwork, " +
                    "FramedAppleTalkZone=:FramedAppleTalkZone, AcctStatusType=:AcctStatusType, AcctDelayTime=:AcctDelayTime, AcctInputOctets=:AcctInputOctets, AcctOutputOctets=:AcctOutputOctets, " +
                    "AcctSessionId=:AcctSessionId, AcctAuthentic=:AcctAuthentic, AcctSessionTime=:AcctSessionTime, AcctInputPackets=:AcctInputPackets, AcctOutputPackets=:AcctOutputPackets, " +
                    "AcctTerminateCause=:AcctTerminateCause, AcctMultiSessionId=:AcctMultiSessionId, AcctLinkCount=:AcctLinkCount, AcctInputGigawords=:AcctInputGigawords, AcctOutputGigawords=:AcctOutputGigawords, " +
                    "EventTimestamp=:EventTimestamp, CHAPChallenge=:CHAPChallenge, NASPortType=:NASPortType, PortLimit=:PortLimit, LoginLATPort=:LoginLATPort, AcctTunnelConnection=:AcctTunnelConnection, " +
                    "ARAPFeatures=:ARAPFeatures, ARAPZoneAccess=:ARAPZoneAccess, ARAPSecurity=:ARAPSecurity, ARAPSecurityData=:ARAPSecurityData, PasswordRetry=:PasswordRetry, Prompt=:Prompt, " +
                    "ConnectInfo=:ConnectInfo, ConfigurationToken=:ConfigurationToken, EAPMessage=:EAPMessage, MessageAuthenticator=:MessageAuthenticator, ARAPChallengeResponse=:ARAPChallengeResponse, " +
                    "AcctInterimInterval=:AcctInterimInterval, NASPortId=:NASPortId, FramedPool=:FramedPool, NASIPv6Address=:NASIPv6Address, FramedInterfaceId=:FramedInterfaceId, FramedIPv6Prefix=:FramedIPv6Prefix, " +
                    "LoginIPv6Host=:LoginIPv6Host, FramedIPv6Route=:FramedIPv6Route, FramedIPv6Pool=:FramedIPv6Pool, DigestResponse=:DigestResponse, DigestAttributes=:DigestAttributes, framedipv6address=:framedipv6address, " +
                    "DelegatedIPv6Prefix=:DelegatedIPv6Prefix, lastmodificationdate = now(), isthrottlespeed=:isthrottlespeed, cprid=:cprid";


    private static String strRadiusPacketInsert = "INSERT INTO tbltradiuspackets ("
            + "UserName, NASIPAddress, NASPort, ServiceType, FramedProtocol, FramedIPAddress, FramedIPNetmask, "
            + "FramedRouting, FilterId, FramedMTU, FramedCompression, LoginIPHost, LoginService, LoginTCPPort, "
            + "ReplyMessage, CallbackNumber, CallbackId, FramedRoute, FramedIPXNetwork, State, Class, VendorSpecific, "
            + "SessionTimeout, IdleTimeout, TerminationAction, CalledStationId, CallingStationId, NASIdentifier, "
            + "ProxyState, LoginLATService, LoginLATNode, LoginLATGroup, FramedAppleTalkLink, FramedAppleTalkNetwork, "
            + "FramedAppleTalkZone, AcctStatusType, AcctDelayTime, AcctInputOctets, AcctOutputOctets, AcctSessionId, "
            + "AcctAuthentic, AcctSessionTime, AcctInputPackets, AcctOutputPackets, AcctTerminateCause, AcctMultiSessionId, "
            + "AcctLinkCount, AcctInputGigawords, AcctOutputGigawords, EventTimestamp, CHAPChallenge, NASPortType, "
            + "PortLimit, LoginLATPort, AcctTunnelConnection, ARAPFeatures, ARAPZoneAccess, ARAPSecurity, ARAPSecurityData, "
            + "PasswordRetry, Prompt, ConnectInfo, ConfigurationToken, EAPMessage, MessageAuthenticator, "
            + "ARAPChallengeResponse, AcctInterimInterval, NASPortId, FramedPool, NASIPv6Address, FramedInterfaceId, "
            + "FramedIPv6Prefix, LoginIPv6Host, FramedIPv6Route, FramedIPv6Pool, DigestResponse, DigestAttributes, "
            + "framedipv6address, DelegatedIPv6Prefix, addl1, addl2, custid, createdate, lastmodificationdate, mvnoid, "
            + "processed,isthrottlespeed, planname) "
            + "VALUES ("
            + ":UserName, :NASIPAddress, :NASPort, :ServiceType, :FramedProtocol, :FramedIPAddress, :FramedIPNetmask, "
            + ":FramedRouting, :FilterId, :FramedMTU, :FramedCompression, :LoginIPHost, :LoginService, :LoginTCPPort, "
            + ":ReplyMessage, :CallbackNumber, :CallbackId, :FramedRoute, :FramedIPXNetwork, :State, :Class, :VendorSpecific, "
            + ":SessionTimeout, :IdleTimeout, :TerminationAction, :CalledStationId, :CallingStationId, :NASIdentifier, "
            + ":ProxyState, :LoginLATService, :LoginLATNode, :LoginLATGroup, :FramedAppleTalkLink, :FramedAppleTalkNetwork, "
            + ":FramedAppleTalkZone, :AcctStatusType, :AcctDelayTime, :AcctInputOctets, :AcctOutputOctets, :AcctSessionId, "
            + ":AcctAuthentic, :AcctSessionTime, :AcctInputPackets, :AcctOutputPackets, :AcctTerminateCause, :AcctMultiSessionId, "
            + ":AcctLinkCount, :AcctInputGigawords, :AcctOutputGigawords, :EventTimestamp, :CHAPChallenge, :NASPortType, "
            + ":PortLimit, :LoginLATPort, :AcctTunnelConnection, :ARAPFeatures, :ARAPZoneAccess, :ARAPSecurity, :ARAPSecurityData, "
            + ":PasswordRetry, :Prompt, :ConnectInfo, :ConfigurationToken, :EAPMessage, :MessageAuthenticator, "
            + ":ARAPChallengeResponse, :AcctInterimInterval, :NASPortId, :FramedPool, :NASIPv6Address, :FramedInterfaceId, "
            + ":FramedIPv6Prefix, :LoginIPv6Host, :FramedIPv6Route, :FramedIPv6Pool, :DigestResponse, :DigestAttributes, "
            + ":framedipv6address, :DelegatedIPv6Prefix, :addl1, :addl2, :custid, :createdate, :lastmodificationdate, :mvnoid, "
            + ":processed, :isthrottlespeed, :planname)";




    private static final Logger log = LoggerFactory.getLogger(DBAccountingDriver.class);


    public int getNoofCustomerSession(String strUsername, String strCalling, Long cltGrpId) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CDR FROM DB : %s %s %s", strUsername, QUERY, strGetNoofUsersession));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        int intCount = 0;

        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strGetNoofUsersession);
            stmt.setString(1, strUsername);
            stmt.setString(2, strCalling);
            stmt.setLong(3, cltGrpId);

            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                intCount = rset.getInt("noofsession");
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
        return intCount;
    }

    public int getNoofCustomerSessionByCustomerId(Long cltGrpId, String custId) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CDR FROM DB : %s %s %s", custId, QUERY, strGetNoofUsersession));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        int intCount = 0;

        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strGetNoofUsersessionByCustId);
//            stmt.setString(1, strUsername);
//            stmt.setString(2, strCalling);
            stmt.setLong(1, cltGrpId);
            stmt.setString(2, custId);

            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                intCount = rset.getInt("noofsession");
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
        return intCount;
    }

    public int isLiveSessionExists(String strUsername, String strCalling, String acctSessionId, String strAcctMultiSessionId) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CDR FROM DB : %s %s %s : %s : %s ", strUsername, QUERY, strExistsNoofUsersession, acctSessionId, strAcctMultiSessionId));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        int intCount = 0;

        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strExistsNoofUsersession);
            stmt.setString(1, acctSessionId);
            stmt.setString(2, strAcctMultiSessionId);

            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                intCount = rset.getInt("noofsession");
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
        return intCount;
    }

    public String normalizeMacAddress(String macAddress) {
        if (macAddress != null)
            return macAddress.replace(":", "").replace("-", "").replace(".", "");
        return macAddress;
    }

    /**
     * If due to any reason customer try to authenticate again and there is already entry in live user for mac
     * then remove existing entry
     *
     * @param strCalling
     * @return
     * @throws SQLException
     */
    public LiveUser validateExistingCustomerSession(String strCalling, boolean terminateSessionOnDuplicateMac) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CDR FROM DB : %s %s", QUERY, strFetchLiveUserByMac));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        int intCount = 0;
        LiveUser liveUser = new LiveUser();
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strFetchLiveUserByMac);
            stmt.setString(1, normalizeMacAddress(strCalling));

            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                intCount = intCount + 1;
                liveUser.setUserName(rset.getString("UserName"));
                liveUser.setlClass(rset.getString("CLASS"));
                liveUser.setAcctAuthentic(rset.getString("ACCTAUTHENTIC"));
                liveUser.setAcctInputGigawords(rset.getString("ACCTINPUTGIGAWORDS"));
                liveUser.setNasPortType(rset.getString("NASPORTTYPE"));
                liveUser.setCallingStationId(rset.getString("CALLINGSTATIONID"));
                liveUser.setCalledStationId(rset.getString("CALLEDSTATIONID"));
                liveUser.setNasPortId(rset.getString("NASPORTID"));
                liveUser.setNasPort(rset.getString("NASPORT"));
                liveUser.setFramedIpAddress(rset.getString("FRAMEDIPADDRESS"));
                liveUser.setVendorSpecific(rset.getString("VENDORSPECIFIC"));
                liveUser.setAcctInputOctets(rset.getString("ACCTINPUTOCTETS"));
                liveUser.setAcctOutputOctets(rset.getString("ACCTOUTPUTOCTETS"));
                liveUser.setAcctInputGigawords(rset.getString("ACCTINPUTGIGAWORDS"));
                liveUser.setAcctOutputGigawords(rset.getString("ACCTOUTPUTGIGAWORDS"));
                liveUser.setAcctInputPackets(rset.getString("ACCTINPUTPACKETS"));
                liveUser.setAcctOutputPackets(rset.getString("ACCTOUTPUTPACKETS"));
                liveUser.setAcctSessionTime(rset.getString("ACCTSESSIONTIME"));
                liveUser.setAcctOutputPackets(rset.getString("ACCTOUTPUTPACKETS"));
                liveUser.setNasIdentifier(rset.getString("NASIdentifier"));
                liveUser.setAcctSessionId(rset.getString("AcctSessionId"));
                liveUser.setNasIpAddress(rset.getString("NASIPAddress"));

            }
            if (intCount > 0 && terminateSessionOnDuplicateMac) {
                //Remove existing live user entry. This was hughes
                if (log.isDebugEnabled()) {
                    log.debug(String.format("IN GET CDR FROM DB : %s %s", QUERY, strDeleteLiveUserByMac));
                }
                // Live User issue hence commenting.
                stmt = null;
                stmt = conn.prepareStatement(strDeleteLiveUserByMac);
                stmt.setString(1, strCalling);
                stmt.executeUpdate();

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
        return liveUser;
    }

    public LiveUser validateExistingCustomerSessionUsingNasAndCallingstation(String strCalling, String strNasIpAddress, boolean terminateSessionOnDuplicateMac) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN GET CDR FROM DB : %s %s", QUERY, strFetchLiveUserByMacAndNasIp));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        int intCount = 0;
        LiveUser liveUser = new LiveUser();
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strFetchLiveUserByMacAndNasIp);
            stmt.setString(1, strCalling);
            stmt.setString(2, strNasIpAddress);

            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                intCount = intCount + 1;
                liveUser.setUserName(rset.getString("UserName"));
                liveUser.setlClass(rset.getString("CLASS"));
                liveUser.setAcctAuthentic(rset.getString("ACCTAUTHENTIC"));
                liveUser.setAcctInputGigawords(rset.getString("ACCTINPUTGIGAWORDS"));
                liveUser.setNasPortType(rset.getString("NASPORTTYPE"));
                liveUser.setCallingStationId(rset.getString("CALLINGSTATIONID"));
                liveUser.setCalledStationId(rset.getString("CALLEDSTATIONID"));
                liveUser.setNasPortId(rset.getString("NASPORTID"));
                liveUser.setNasPort(rset.getString("NASPORT"));
                liveUser.setFramedIpAddress(rset.getString("FRAMEDIPADDRESS"));
                liveUser.setVendorSpecific(rset.getString("VENDORSPECIFIC"));
                liveUser.setAcctInputOctets(rset.getString("ACCTINPUTOCTETS"));
                liveUser.setAcctOutputOctets(rset.getString("ACCTOUTPUTOCTETS"));
                liveUser.setAcctInputGigawords(rset.getString("ACCTINPUTGIGAWORDS"));
                liveUser.setAcctOutputGigawords(rset.getString("ACCTOUTPUTGIGAWORDS"));
                liveUser.setAcctInputPackets(rset.getString("ACCTINPUTPACKETS"));
                liveUser.setAcctOutputPackets(rset.getString("ACCTOUTPUTPACKETS"));
                liveUser.setAcctSessionTime(rset.getString("ACCTSESSIONTIME"));
                liveUser.setAcctOutputPackets(rset.getString("ACCTOUTPUTPACKETS"));
                liveUser.setNasIdentifier(rset.getString("NASIdentifier"));
                liveUser.setAcctSessionId(rset.getString("AcctSessionId"));
                liveUser.setNasIpAddress(rset.getString("NASIPAddress"));

            }
            if (intCount > 0 && terminateSessionOnDuplicateMac) {
                //Remove existing live user entry. This was hughes
                if (log.isDebugEnabled()) {
                    log.debug(String.format("IN GET CDR FROM DB : %s %s", QUERY, strDeleteLiveUserByMac));
                }
                // Live User issue hence commenting.
                stmt = null;
                stmt = conn.prepareStatement(strDeleteLiveUserByMac);
                stmt.setString(1, strCalling);
                stmt.executeUpdate();
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
        return liveUser;
    }

    public boolean insertCDR(AccountingRequest acctRequest, ConcurrentMap acctFieldMapping, int mvnoid, Double totalTimeMin, String sourceAddress, CustomerData custRetrunData, double upload, double download) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Insert CDR FROM DB : %s %s", QUERY, strRadiusAccountInsert));
        }

        Connection conn = null;
        NamedParameterStatement stmt = null;
        try {
            Properties props = new Properties();
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = new NamedParameterStatement(conn, strRadiusAccountInsert);
            Iterator<Map.Entry<String, String>> itr = acctFieldMapping.entrySet().iterator();
            while (itr.hasNext()) {
                RadiusAttribute radAttrib = null;
                Map.Entry<String, String> entry = itr.next();
                if (acctRequest != null && !entry.getValue().equalsIgnoreCase("sourceipaddress") && !entry.getValue().equalsIgnoreCase("custid") && !entry.getValue().equalsIgnoreCase("UserName")) {
                    radAttrib = acctRequest.getAttribute(entry.getKey());
                }

                if (radAttrib != null) {
                    if (custRetrunData.getUsageQuotaType() != null && entry.getValue().equalsIgnoreCase("AcctOutputOctets") && custRetrunData.getUsageQuotaType().equalsIgnoreCase(CommonConstants.UPLOAD)) {
                        //If usage quota type is download then set upload to zero
                        stmt.setString(entry.getValue(), "0");
                    } else if (custRetrunData.getUsageQuotaType() != null && entry.getValue().equalsIgnoreCase("AcctInputOctets") && custRetrunData.getUsageQuotaType().equalsIgnoreCase(CommonConstants.DOWNLOAD)) {
                        //If usage quota type is upload then set download to zero
                        stmt.setString(entry.getValue(), "0");
                    } else
                        stmt.setString(entry.getValue(), radAttrib.getAttributeValue());
                } else {
                    if (entry.getValue().equalsIgnoreCase("sourceipaddress")) {
                        //skip
                    } else if (entry.getValue().equalsIgnoreCase("custid")) {
                        stmt.setString(entry.getValue(), Integer.toString(custRetrunData.getCustid()));
                    } else if (entry.getValue().equalsIgnoreCase("UserName")) {
                        //stmt.setString(entry.getValue(),custRetrunData.getUsername());
                        String strUsername = custRetrunData.getUsername();
                        log.debug("Username from custRetrunData to insert value in cdr: " + strUsername);
                        if (strUsername == null || strUsername.isEmpty()) {
                            strUsername = acctRequest.getAttribute("User-Name").getAttributeValue();
                            log.debug("Username from request to insert value in cdr: " + strUsername);
                        }
                        stmt.setString(entry.getValue(), strUsername);
                        log.debug(" final Username value to insert value in cdr: " + strUsername);
                    } //entry.getValue().equalsIgnoreCase("NASIdentifier") && entry.getValue().equalsIgnoreCase("AcctSessionId")
                    else if (entry.getValue().equalsIgnoreCase("NASIdentifier") && acctRequest.getAttribute("NAS-Identifier") != null) {
                        stmt.setString(entry.getValue(), acctRequest.getAttribute("NAS-Identifier").getAttributeValue());
                    } else if (entry.getValue().equalsIgnoreCase("AcctSessionId") && acctRequest.getAttribute("Acct-Session-Id") != null) {
                        stmt.setString(entry.getValue(), acctRequest.getAttribute("Acct-Session-Id").getAttributeValue());
                    } else if (entry.getValue().equalsIgnoreCase("NASIPAddress") && acctRequest.getAttribute("NAS-IP-Address") != null) {
                        stmt.setString(entry.getValue(), acctRequest.getAttribute("NAS-IP-Address").getAttributeValue());
                    } else if (entry.getValue().equalsIgnoreCase("AcctMultiSessionId")) {
                        String AcctMultiSessionId = "0";
                        if (acctRequest.getAttribute("Acct-Multi-Session-Id") != null) {
                            AcctMultiSessionId = acctRequest.getAttribute("Acct-Multi-Session-Id").getAttributeValue();
                        }
                        stmt.setString(entry.getValue(), AcctMultiSessionId);
                    } else {
                        stmt.setString(entry.getValue(), null);
                    }
                }
            }
            Date startDate = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(totalTimeMin.longValue()));
            Timestamp ts = new Timestamp(startDate.getTime());

            stmt.setTimestamp("createdate", ts);
            stmt.setTimestamp("lastmodificationdate", currentDate);
            stmt.setInt("mvnoid", mvnoid);
            stmt.setString("AcctInputOctets", String.valueOf((long) upload));
            stmt.setString("AcctOutputOctets", String.valueOf((long) download));
            String cprId = "0";
            if (custRetrunData.getCustomerBasePlan() != null && !custRetrunData.getCustomerBasePlan().isEmpty()) {
                if (custRetrunData.getCustomerBasePlan().get(0).getCustpackageid() != null) {
                    cprId = String.valueOf(custRetrunData.getCustomerBasePlan().get(0).getCustpackageid());
                }
            }
            stmt.setString("cprid", cprId);
            log.debug(stmt.getStatement().toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }


    public boolean insertDBSesion(AccountingRequest acctRequest, ConcurrentMap acctFieldMapping, int mvnoid, String sourceAddress, CustomerData custRetrunData, Long clientGroupId, double upload, double download, boolean isFaultyMac) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Insert Session FROM DB : %s %s :Mapping %s", QUERY, strLiveSessionInsert, acctFieldMapping.size()));
        }
        Connection conn = null;
        NamedParameterStatement stmt = null;
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = new NamedParameterStatement(conn, strLiveSessionInsert);
            Iterator<Map.Entry<String, String>> itr = acctFieldMapping.entrySet().iterator();
            while (itr.hasNext()) {
                RadiusAttribute radAttrib = null;
                Map.Entry<String, String> entry = itr.next();
                if (!entry.getValue().equalsIgnoreCase("sourceipaddress") && !entry.getValue().equalsIgnoreCase("custid") && !entry.getValue().equalsIgnoreCase("UserName")) {
                    radAttrib = acctRequest.getAttribute(entry.getKey());
                }
                if (radAttrib != null) {
                    stmt.setString(entry.getValue(), radAttrib.getAttributeValue());
//					stmt.setString(entry.getValue(),radAttrib.getAttributeValue());
                } else {
                    if (entry.getValue().equalsIgnoreCase("sourceipaddress")) {
                        stmt.setString(entry.getValue(), sourceAddress);
                    } else if (entry.getValue().equalsIgnoreCase("custid")) {
                        stmt.setString(entry.getValue(), isFaultyMac ? "0" : Integer.toString(custRetrunData.getCustid()));
                    } else if (entry.getValue().equalsIgnoreCase("UserName")) {
                        //Issue Resolved for ANG-10886
                        String strUsername = custRetrunData.getUsername();
                        log.debug("Username from custRetrunData to insert value in live-user: " + strUsername);
                        if (strUsername == null || strUsername.isEmpty()) {
                            strUsername = acctRequest.getAttribute("User-Name").getAttributeValue();
                            log.debug("Username from request to insert value in live-user: " + strUsername);
                        }
                        stmt.setString(entry.getValue(), strUsername);
                        log.debug(" final Username value to insert value in live-user: " + strUsername);
                    } else if (entry.getValue().equalsIgnoreCase("AcctMultiSessionId")) {
                        String AcctMultiSessionId = "0";
                        if (acctRequest.getAttribute("Acct-Multi-Session-Id") != null) {
                            AcctMultiSessionId = acctRequest.getAttribute("Acct-Multi-Session-Id").getAttributeValue();
                        }
                        stmt.setString(entry.getValue(), AcctMultiSessionId);
                    } else {
                        stmt.setString(entry.getValue(), null);
                    }
                }
            }
            stmt.setTimestamp("createdate", currentDate);
            stmt.setTimestamp("lastmodificationdate", currentDate);
            stmt.setInt("mvnoid", mvnoid);
            stmt.setLong("clientgroupid", clientGroupId);
            stmt.setString("AcctInputOctets", String.valueOf((long) upload));
            stmt.setString("AcctOutputOctets", String.valueOf((long) download));
            if (custRetrunData != null && custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0 && !isFaultyMac)
                stmt.setLong("cprid", custRetrunData.getCustomerBasePlan().get(0).getCustpackageid());
            else
                stmt.setLong("cprid", 0); // For unknown customer cprid will null so set 0

            stmt.setObject("isthrottlespeed", custRetrunData.isThrottleSpeed());
            stmt.executeUpdate();
            log.debug(stmt.getStatement().toString());
            log.debug(String.format("Insert Query for live user : %s", stmt.getStatement().toString()));
        } catch (SQLIntegrityConstraintViolationException e) {
            log.warn("Unique Key Exception and its obivious one" + SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public boolean deleteDBSession(String strSessionId, String nasIpAddress, String strMultiSessionId) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strDeleteLiveUser);
            if (log.isDebugEnabled()) {
                log.debug("IN delete FROM DB:" + strDeleteLiveUser + ":NasIPAddress:" + nasIpAddress + ":strSessionId:" + strSessionId + ":MultiSessionId:" + strMultiSessionId);
            }
            stmt.setString(1, nasIpAddress);
            stmt.setString(2, strSessionId);
            if (strMultiSessionId != null && !strMultiSessionId.equalsIgnoreCase("null") && strMultiSessionId.trim().length() > 0) {
                log.info("strMultiSessionId 1: " + (strMultiSessionId != null) + " ,2: " + !strMultiSessionId.equalsIgnoreCase("null") + " ,3: " + strMultiSessionId.trim().length());
                stmt.setString(3, strMultiSessionId);
            } else {
                log.info("Set strMultiSessionId 0");
                stmt.setString(3, "0");
            }
            int i = stmt.executeUpdate();
            log.debug("IN delete FROM DB: " + stmt.toString());
            log.debug(strDeleteLiveUser + ":NasIPAddress:" + nasIpAddress + "strSessionId:" + strSessionId + ":MultiSessionId:" + strMultiSessionId);
        } catch (Exception ex) {
            log.info("Exception to delete session: " + ex.getMessage());
            ex.printStackTrace();
            log.error(SQL_EXCEPTION, ex);
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

        return true;
    }

    public boolean updateDBSesion(AccountingRequest acctRequest, ConcurrentMap acctFieldMapping, int mvnoid, CustomerData custRetrunData, double currentUsageVolumeBySession, long currentUsageTimeBySession, String acctStatusType, double upload, double download) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update Session FROM DB :QUERY %s", strLiveSessionUpdate));
        }
        Connection conn = null;
        NamedParameterStatement stmt = null;
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = new NamedParameterStatement(conn, strLiveSessionUpdate);
            Iterator<Map.Entry<String, String>> itr = acctFieldMapping.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, String> entry = itr.next();
                RadiusAttribute radAttrib = null;
                if (entry.getValue().equalsIgnoreCase("lastmodificationdate")) {
                    stmt.setTimestamp(entry.getValue(), new Timestamp(new Date().getTime()));
                } else if (!entry.getValue().equalsIgnoreCase("sourceipaddress") && !entry.getValue().equalsIgnoreCase("custid") && !entry.getValue().equalsIgnoreCase("addl2") &&
                        !entry.getValue().equalsIgnoreCase("addl1") && !entry.getValue().equalsIgnoreCase("custid")
                        && !entry.getValue().equalsIgnoreCase("createdate") &&
                        !entry.getValue().equalsIgnoreCase("sourceipaddress") && !entry.getValue().equalsIgnoreCase("mvnoid") && !entry.getValue().equalsIgnoreCase("UserName")) {
                    radAttrib = acctRequest.getAttribute(entry.getKey());

                    if (radAttrib != null) {
                        log.info("Live user update attribute: " + radAttrib.getAttributeType() + ":" + radAttrib.getAttributeValue() + " : Column: " + entry.getValue());
                        String usageQuotaType = custRetrunData.getUsageQuotaType();
                        String entryValue = entry.getValue();
                        if (usageQuotaType != null) {
                            //upload = input_octet->
                            //download = output_octet->
                            if (entryValue.equalsIgnoreCase("AcctOutputOctets") && usageQuotaType.equalsIgnoreCase(CommonConstants.UPLOAD)) {
                                log.debug("usageQuotaType: " + usageQuotaType + " so set AcctOutputOctets: " + 0);
                                // If usage quota type is download, set upload to zero
                                stmt.setString(entryValue, "0");
                            } else if (entryValue.equalsIgnoreCase("AcctInputOctets") && usageQuotaType.equalsIgnoreCase(CommonConstants.DOWNLOAD)) {
                                log.debug("usageQuotaType: " + usageQuotaType + " so set AcctInputOctets: " + 0);
                                // If usage quota type is upload, set download to zero
                                stmt.setString(entryValue, "0");
                            } else {
                                stmt.setString(entryValue, radAttrib.getAttributeValue());
                            }
                        } else {
                            stmt.setString(entryValue, radAttrib.getAttributeValue());
                        }
                        switch (entryValue.toLowerCase()) {
                            case "framedipaddress":
                                if (acctStatusType.equalsIgnoreCase("framed-ip-address-up")) {
                                    stmt.setString("FramedIPAddress", radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("framed-ip-address-down")) {
                                    stmt.setString("FramedIPAddress", null);
                                }
                                break;

                            case "framedipv6address":
                                if (acctStatusType.equalsIgnoreCase("alc-ipv6-address-up")) {
                                    stmt.setString("framedipv6address", radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("alc-ipv6-address-down")) {
                                    stmt.setString("framedipv6address", null);
                                }
                                break;

                            case "delegatedipv6prefix":
                                if (acctStatusType.equalsIgnoreCase("delegated-ipv6-prefix-up")) {
                                    stmt.setString("DelegatedIPv6Prefix", radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("delegated-ipv6-prefix-down")) {
                                    stmt.setString("DelegatedIPv6Prefix", null);
                                }
                                break;

                            default:
                                // No action needed for other entry values
                                break;
                        }

                        switch (entryValue.toLowerCase()) {
                            case "framedipaddress":
                                if (acctStatusType.equalsIgnoreCase("framed-ip-address-up")) {
                                    stmt.setString(entryValue, radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("framed-ip-address-down")) {
                                    stmt.setString(entryValue, null);
                                }
                                break;

                            case "framedipv6address":
                                if (acctStatusType.equalsIgnoreCase("alc-ipv6-address-up")) {
                                    stmt.setString(entryValue, radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("alc-ipv6-address-down")) {
                                    stmt.setString(entryValue, null);
                                }
                                break;

                            case "delegatedipv6prefix":
                                if (acctStatusType.equalsIgnoreCase("delegated-ipv6-prefix-up")) {
                                    log.info(acctStatusType + ": delegatedipv6prefix: " + radAttrib.getAttributeValue() + " ,entryValue: " + entryValue);
                                    stmt.setString(entryValue, radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("delegated-ipv6-prefix-down")) {
                                    log.info(acctStatusType + " :delegatedipv6prefix: null");
                                    stmt.setString(entryValue, null);
                                }
                                break;

                            default:
                                // No action needed for other entry values
                                break;
                        }

                    } else {
                        stmt.setString(entry.getValue(), null);
                    }
                } else if (entry.getValue().equalsIgnoreCase("UserName")) {
                    String strUsername = custRetrunData.getUsername();
                    if (strUsername == null || strUsername.isEmpty()) {
                        strUsername = acctRequest.getAttribute("User-Name").getAttributeValue();
                        log.debug("Username from request to update value in live-user: " + strUsername);
                    }
                    log.debug(" final Username value to update value in live-user: " + strUsername);
                    stmt.setString(entry.getValue(), strUsername);
                }
            }


            stmt.setString("lastsessionquotavolume", String.valueOf(currentUsageVolumeBySession));
//            stmt.setString("lastsessionquotatime", String.valueOf(currentUsageTimeBySession));
            stmt.setString("AcctInputOctets", String.valueOf((long) upload));
            stmt.setString("AcctOutputOctets", String.valueOf((long) download));
            stmt.setObject("isthrottlespeed", custRetrunData.isThrottleSpeed());
            String AcctMultiSessionId = "0";
            if (acctRequest.getAttribute("Acct-Multi-Session-Id") != null) {
                AcctMultiSessionId = acctRequest.getAttribute("Acct-Multi-Session-Id").getAttributeValue();
            }
            stmt.setString("AcctMultiSessionId", AcctMultiSessionId);
            log.debug(stmt.getStatement().toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
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
        return true;
    }

    public boolean updateReservedQuotaForChild(String username, Double totalReservedQuota) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update updateReservedQuotaForChild FROM Query : %s : USERNAME : %s :TOTALRESERVEDQUOTA %s", strTotalReservedQuota, username, totalReservedQuota));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strTotalReservedQuota);
            if (log.isDebugEnabled()) {
                log.debug("IN Update updateQuota FROM DB" + strTotalReservedQuota);
            }
            stmt.setDouble(1, totalReservedQuota);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public boolean deleteReservedQuotaDtls(Integer custId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strReservedQuotaDelete);
            if (log.isDebugEnabled()) {
                log.debug("IN Delete updateQuota FROM DB" + strReservedQuotaDelete);
            }
            stmt.setInt(1, custId);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(SQL_EXCEPTION, ex);
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

        return true;

    }

    public boolean updateReservedQuotaDtls(String username, Integer custId, Double usedQuota, Double unUsedQuota) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Insert updateReservedQuotaForChild FROM Query : %s : USERNAME : %s :USEDQUOTA %s", strReservedQuotaupdate, username, usedQuota));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strReservedQuotaupdate);
            if (log.isDebugEnabled()) {
                log.debug("IN Update updateQuota FROM DB" + strReservedQuotaupdate);
            }
            stmt.setDouble(1, usedQuota);
            stmt.setDouble(2, unUsedQuota);
            stmt.setInt(3, custId);
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public boolean addReservedQuotaDtls(String username, Integer custId, Integer parentCustId, Double totalReservedQuota, Integer custQuotaDtlsId) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Insert addReservedQuotaForChild FROM Query : %s : USERNAME : %s :TOTALRESERVEDQUOTA %s", strReservedQuotaInsert, username, totalReservedQuota));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strReservedQuotaInsert);
            if (log.isDebugEnabled()) {
                log.debug("IN Update updateQuota FROM DB" + strTotalReservedQuota);
            }
            stmt.setInt(1, custId);
            stmt.setInt(2, custQuotaDtlsId);
            stmt.setDouble(3, 0);
            stmt.setDouble(4, totalReservedQuota);
            stmt.setDouble(5, totalReservedQuota);
            stmt.setDouble(6, parentCustId);
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public boolean checkReservedQuotavailabe(Integer custId) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN check checkReservedQuotavailabe FROM Query : %s", strCheckQuotaAvailable));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        Integer intCount = 0;
        try {

            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strCheckQuotaAvailable);
            if (log.isDebugEnabled()) {
                log.debug("IN Update updateQuota FROM DB" + strCheckQuotaAvailable);
            }
            stmt.setInt(1, custId);
            rset = stmt.executeQuery();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result Set is %s", rset));
            }
            while (rset.next()) {
                intCount = rset.getInt("totalcust");
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
        return intCount < 1;
    }


    public boolean updateQuota(String username, String volumeused, String sessionVolumeQuota, String sessionTimeQuota, String timeused, Integer custpackageid, Boolean skipQuotaReset) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update updateQuota username %s :VOLUME %s :Time %s :SESSIONVOLUMEQUOTA %s :SESSIONTIMEQUOTA %s :CUSTPACKAGEID %s :SKIPQUOTARESET %s"
                    , username, volumeused, timeused, sessionVolumeQuota, sessionTimeQuota, sessionTimeQuota, skipQuotaReset));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            if (skipQuotaReset != null && skipQuotaReset) {
                stmt = conn.prepareStatement(strUpdateCustomerQuotaWithSkipQuotaUpdate);
                log.debug("IN Update updateQuota FROM DB" + strUpdateCustomerQuotaWithSkipQuotaUpdate);
            } else {
                stmt = conn.prepareStatement(strUpdateCustomerQuota);
                log.debug("IN Update updateQuota FROM DB" + strUpdateCustomerQuota);
            }
            if (skipQuotaReset != null && skipQuotaReset) {
                stmt.setString(1, username);
                stmt.setInt(2, custpackageid);
            } else {
                stmt.setString(1, convertValueToRedableFormat(timeused, 7));
                stmt.setString(2, convertValueToRedableFormat(volumeused, 7));
                stmt.setString(3, convertValueToRedableFormat(sessionTimeQuota, 7));
                stmt.setString(4, convertValueToRedableFormat(sessionVolumeQuota, 7));
                stmt.setString(5, username);
                stmt.setInt(6, custpackageid);
            }
            stmt.executeUpdate();
            log.info("Update Quota: " + stmt.toString());
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public boolean addCustomerQuotaResetHistory(CustomerQuotaInfo custQuotaDetails) {
        String sql = strInsertCustomerQuotaReset;
        try {
            if (custQuotaDetails != null && custQuotaDetails.getCustpackageid() != null) {

                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rset = null;
                try {
                    log.info(String.format("Inserting currentsessionusagevolume: %s, custId: %s, cprId: %s, query: %s", custQuotaDetails.getVolumeBasedSessionUsedQuota()
                            , custQuotaDetails.getCustId(), custQuotaDetails.getCustpackageid(), strInsertCustomerQuotaReset));
                    conn = DataSource.getConnection();
                    stmt = conn.prepareStatement(strInsertCustomerQuotaReset);
                    stmt.setString(1, custQuotaDetails.getQuotaUnit());
                    stmt.setLong(2, custQuotaDetails.getCustId());
                    stmt.setInt(3, custQuotaDetails.getCustpackageid());
                    stmt.setDouble(4, custQuotaDetails.getVolumeBasedUsedQuota() + custQuotaDetails.getVolumeBasedSessionUsedQuota());

                    double timeSession = 0d;
                    if (custQuotaDetails.getTimeBasedUsedQuota() != null)
                        timeSession = timeSession + custQuotaDetails.getTimeBasedUsedQuota();
                    if (custQuotaDetails.getTimeBasedSessionUsedQuota() != null)
                        timeSession = timeSession + custQuotaDetails.getTimeBasedSessionUsedQuota();

                    stmt.setDouble(5, timeSession);
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
            } else {
                return false;
            }
        } catch (Exception ex) {
            log.error("Error adding customer quota reset history: " + ex.getMessage(), ex);
            return false;
        }
    }

    public boolean updatevlanManagement(VLANManagement vlanManagement) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update update Vlan FROM Query : %s :Vlan Management %s :LastAuthDate %s", strUpdateVlanLastAuthDate, vlanManagement.getVlanName(), vlanManagement.getLastAuthMatched()));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strUpdateVlanLastAuthDate);
            stmt.setTimestamp(1, Timestamp.valueOf(vlanManagement.getLastAuthMatched()));
            stmt.setLong(2, vlanManagement.getVlanId());
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public boolean updateCustomerQuota(String username, String volumeused, String timeused, Integer custpackageid) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update updateReservedQuota FROM Query : %s :VOLUME %s :Time %s", username, volumeused, timeused));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strUpdateCustomerInSessionUsage);
            if (log.isDebugEnabled()) {
                log.debug("IN Update updateReservedQuota FROM DB" + strUpdateCustomerInSessionUsage);
            }
            stmt.setString(1, convertValueToRedableFormat(timeused, 7));
            stmt.setString(2, convertValueToRedableFormat(volumeused, 7));
            stmt.setInt(3, custpackageid);
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public boolean updateCustomerQuotaSkipFlag(Integer custpackageid) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Update updateReservedQuota FROM Query : %s :CPRID %s", strUpdateSkipQuotaFlag, custpackageid));
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = conn.prepareStatement(strUpdateSkipQuotaFlag);
            if (log.isDebugEnabled()) {
                log.debug("IN Update update customer skip quota flag FROM DB" + strUpdateSkipQuotaFlag);
            }
            stmt.setInt(1, custpackageid);
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public String convertValueToRedableFormat(String value, int scale) {
        try {
            try {
                return new BigDecimal(value).toString();
            } catch (Exception ex) {
                log.error("Exception to convert Value: " + value);
            }
            if (value.length() > 7 && value.contains("E")) {
                BigDecimal d = new BigDecimal(value).multiply(BigDecimal.TEN);
                return d.toString();
            } else if (value.contains(".") && value.length() > 7) {
                value = value.substring(0, value.indexOf(".") + 5);
                return value;
            } else
                return value;
        } catch (Exception ex) {
            log.error("Error while convert Expontial value: " + value);
        }
        return value;
    }

    public DBAccountingDriver() {

    }

    public void setValueFromEnvironments(String databaseUrl, String databaseUserName, String databasePassword) {
        DBAccountingDriver.dbUrl = databaseUrl;
        DBAccountingDriver.dbUserName = databaseUserName;
        DBAccountingDriver.dbPassword = databasePassword;
    }

    public boolean insertCDR(LiveUser liveUser, CustomerData customerData) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Insert CDR FROM DB : %s %s", QUERY, strRadiusAccountInsert));
        }

        Connection conn = null;
        NamedParameterStatement stmt = null;
        try {
            Properties props = new Properties();
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = new NamedParameterStatement(conn, strRadiusAccountInsert);
            copyDataFromLiveUser(liveUser, customerData, stmt);

            Date startDate = new Date(liveUser.getCreatedDate().getTime());
            Timestamp ts = new Timestamp(startDate.getTime());

            stmt.setTimestamp("createdate", ts);
            stmt.setTimestamp("lastmodificationdate", currentDate);
            stmt.setInt("mvnoid", liveUser.getMvnoId());
            if (liveUser.getCprId() != null) {
                stmt.setString("cprid", String.valueOf(liveUser.getCprId()));
            } else {
                stmt.setString("cprid", "0");
            }
            log.debug(stmt.getStatement().toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    private static void copyDataFromLiveUser(LiveUser liveUser, CustomerData customerData, NamedParameterStatement stmt) throws SQLException {
        if (customerData != null)
            stmt.setString("custid", String.valueOf(customerData.getCustid()));
        else
            stmt.setString("custid", "0");

        stmt.setString("UserName", liveUser.getUserName());
        stmt.setString("AcctSessionId", liveUser.getAcctSessionId());
        stmt.setString("NASIPAddress", liveUser.getNasIpAddress());
        stmt.setString("NASPort", liveUser.getNasPort());
        stmt.setString("ServiceType", liveUser.getServiceType());
        stmt.setString("FramedProtocol", liveUser.getFramedProtocol());
        stmt.setString("FramedIPAddress", liveUser.getFramedIpAddress());
        stmt.setString("FramedIPNetmask", liveUser.getFramedNetwork());
        stmt.setString("FramedRouting", liveUser.getFramedRouting());
        stmt.setString("FilterId", liveUser.getFilterId());
        stmt.setString("FramedMTU", liveUser.getFrmaedMTU());
        stmt.setString("FramedCompression", liveUser.getFramedCompression());
        stmt.setString("LoginIPHost", liveUser.getLoginIPHost());
        stmt.setString("LoginService", liveUser.getLoginService());
        stmt.setString("LoginTCPPort", liveUser.getLoginTCPPort());
        stmt.setString("ReplyMessage", liveUser.getReplyMessage());
        stmt.setString("CallbackNumber", liveUser.getCallbackNumber());
        stmt.setString("CallbackId", liveUser.getCallbackId());
        stmt.setString("FramedRoute", liveUser.getFramedRoute());
        stmt.setString("FramedIPXNetwork", liveUser.getFramedIPXNetwork());
        stmt.setString("State", liveUser.getState());
        stmt.setString("Class", liveUser.getlClass());
        stmt.setString("VendorSpecific", liveUser.getVendorSpecific());
        stmt.setString("SessionTimeout", liveUser.getSessionTimeout());
        stmt.setString("IdleTimeout", liveUser.getIdleTimeout());
        stmt.setString("TerminationAction", liveUser.getTerminationAction());
        stmt.setString("CalledStationId", liveUser.getCalledStationId());
        stmt.setString("CallingStationId", liveUser.getCallingStationId());
        stmt.setString("NASIdentifier", liveUser.getNasIdentifier());
        stmt.setString("ProxyState", liveUser.getProxyState());
        stmt.setString("LoginLATService", liveUser.getLoginLATService());
        stmt.setString("LoginLATNode", liveUser.getLoginLATNode());
        stmt.setString("LoginLATGroup", liveUser.getLoginLATGroup());
        stmt.setString("FramedAppleTalkLink", liveUser.getFramedAppleTalkLink());
        stmt.setString("FramedAppleTalkNetwork", liveUser.getFramedAppleTalkNetwork());
        stmt.setString("FramedAppleTalkZone", liveUser.getFramedAppleTalkZone());
        stmt.setString("AcctStatusType", "Stop"); // is set to stop
        stmt.setString("AcctDelayTime", liveUser.getAcctDelayTime());
        stmt.setString("AcctInputOctets", liveUser.getAcctInputOctets());
        stmt.setString("AcctOutputOctets", liveUser.getAcctOutputOctets());
        stmt.setString("AcctSessionId", liveUser.getAcctSessionId());
        stmt.setString("AcctAuthentic", liveUser.getAcctAuthentic());
        stmt.setString("AcctSessionTime", liveUser.getAcctSessionTime());
        stmt.setString("AcctInputPackets", liveUser.getAcctInputPackets());
        stmt.setString("AcctOutputPackets", liveUser.getAcctOutputPackets());
        stmt.setString("AcctTerminateCause", liveUser.getAcctTerminateCause());
        stmt.setString("AcctMultiSessionId", liveUser.getAcctMultiSessionId());
        stmt.setString("AcctLinkCount", liveUser.getAcctLinkCount());
        stmt.setString("AcctInputGigawords", liveUser.getAcctInputGigawords());
        stmt.setString("AcctOutputGigawords", liveUser.getAcctOutputGigawords());
        stmt.setString("EventTimestamp", liveUser.getEventTimestamp());
        stmt.setString("CHAPChallenge", liveUser.getCallbackId());
        stmt.setString("NASPortType", liveUser.getNasPortType());
        stmt.setString("PortLimit", liveUser.getPortLimit());
        stmt.setString("LoginLATPort", liveUser.getLoginLATPort());
        stmt.setString("AcctTunnelConnection", liveUser.getAcctTunnelConnection());
        stmt.setString("ARAPFeatures", liveUser.getArapFeatures());
        stmt.setString("ARAPZoneAccess", liveUser.getArapZoneAccess());
        stmt.setString("ARAPSecurity", liveUser.getArapSecurity());
        stmt.setString("ARAPSecurityData", liveUser.getArapSecurityData());
        stmt.setString("PasswordRetry", liveUser.getPasswordRetry());
        stmt.setString("Prompt", liveUser.getPrompt());
        stmt.setString("ConnectInfo", liveUser.getConnectInfo());
        stmt.setString("ConfigurationToken", liveUser.getConfigurationToken());
        stmt.setString("EAPMessage", liveUser.getEapMessage());
        stmt.setString("MessageAuthenticator", liveUser.getMessageAuthenticator());
        stmt.setString("ARAPChallengeResponse", liveUser.getArapChallengeResponse());
        stmt.setString("AcctInterimInterval", liveUser.getAcctInterimInterval());
        stmt.setString("NASPortId", liveUser.getNasPortId());
        stmt.setString("FramedPool", liveUser.getFramedPool());
        stmt.setString("NASIPv6Address", liveUser.getNasIPv6Address());
        stmt.setString("FramedInterfaceId", liveUser.getFramedInterfaceId());
        stmt.setString("FramedIPv6Prefix", liveUser.getFramedIPv6Prefix());
        stmt.setString("LoginIPv6Host", liveUser.getLoginIPv6Host());
        stmt.setString("FramedIPv6Route", liveUser.getFramedIPv6Route());
        stmt.setString("FramedIPv6Pool", liveUser.getFramedIPv6Pool());
        stmt.setString("DigestResponse", liveUser.getDigestResponse());
        stmt.setString("DigestAttributes", liveUser.getDigestAttributes());
        stmt.setString("framedipv6address", liveUser.getFramedipv6address());
        stmt.setString("DelegatedIPv6Prefix", liveUser.getDelegatedIPv6Prefix());
        stmt.setString("addl1", liveUser.getAddl1());
        stmt.setString("addl2", liveUser.getAddl2());
        stmt.setString("custid", liveUser.getCustid());
        //stmt.setString("sourceipaddress", liveUser.getSourceipaddress());
    }

    public List<String> getCustomerPackageRelationId(String customerId) {
        List<String> customerPackageList = new ArrayList<>();
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Getting CustomerPackageRelId for customer ID: %s", customerId));
        }
        ResultSet result = null;
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(strCustomerPackageRelationId)) {
            stmt.setString(1, customerId);
            result = stmt.executeQuery();
            while (result.next()) {
                customerPackageList.add(result.getString("custpackageid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(SQL_EXCEPTION, e);
        } finally {
            try {
                if (result != null) result.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return customerPackageList;
    }

    public List<CustomerQuotaDTO> getCustomerSessionUsageData(List<String> packageIds, String customerId) {
        List<CustomerQuotaDTO> customerPackageList = new ArrayList<>();
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Getting CustomerPackageRelId for customer ID: %s", packageIds));
        }
        ResultSet result = null;
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(strGetSessionUsage)) {

            for (String id : packageIds) {
                stmt.setString(1, customerId);
                stmt.setLong(2, Long.parseLong(id));
                result = stmt.executeQuery();
                while (result.next()) {
                    CustomerQuotaDTO quotaDetails = new CustomerQuotaDTO();
                    if (result.getString("currentsessionusagevolume") != null)
                        quotaDetails.setCurrentSessionVolumeUsage(Double.parseDouble(result.getString("currentsessionusagevolume")));
                    else
                        quotaDetails.setCurrentSessionVolumeUsage(0d);

                    if (result.getString("currentsessionusagevolume") != null)
                        quotaDetails.setGetCurrentSessionTimeUsage(Double.parseDouble(result.getString("currentsessionusagevolume")));
                    else
                        quotaDetails.setGetCurrentSessionTimeUsage(0d);

                    if (result.getString("timequotaused") != null)
                        quotaDetails.setTotalUsedTime(Double.parseDouble(result.getString("timequotaused")));
                    else
                        quotaDetails.setTotalUsedTime(0d);

                    if (result.getString("usedquota") != null)
                        quotaDetails.setTotalUsedQuota(Double.parseDouble(result.getString("usedquota")));
                    else
                        quotaDetails.setTotalUsedQuota(0d);

                    if (result.getString("totalquota") != null)
                        quotaDetails.setTotalQuota(Double.parseDouble(result.getString("totalquota")));
                    else
                        quotaDetails.setTotalQuota(0d);

                    if (result.getString("timetotalquota") != null)
                        quotaDetails.setTimeTotalQuota(Double.parseDouble(result.getString("timetotalquota")));
                    else
                        quotaDetails.setTimeTotalQuota(0d);
                    quotaDetails.setCustomerPackageId(Integer.valueOf(id));
                    customerPackageList.add(quotaDetails);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(SQL_EXCEPTION, e);
        } finally {
            try {
                if (result != null) result.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return customerPackageList;
    }

    public List<String> fetchMacFromCustId(int custId) {
        List<String> macAddresses = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("IN Getting fetchMacFromCustId for customer ID: %s, query:  %s", custId, strFetchMacFromCustId));
            }
            try (Connection conn = DataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(strFetchMacFromCustId)) {
                stmt.setInt(1, custId);
                resultSet = stmt.executeQuery();

                while (resultSet.next()) {
                    macAddresses.add(resultSet.getString("macaddress"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.error(SQL_EXCEPTION, e);
            }

        } catch (Exception ex) {
            log.error("Error to Fetch MAC based on custId: " + custId);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return macAddresses;
    }

    public LiveUser getFirstSessionBasedOnCltGrpcAndCustId(int custId, Long cltGrpId) {
        try {
            List<String> macs = fetchMacFromCustId(custId);
            if (!CollectionUtils.isEmpty(macs)) {
                LiveUser liveUser = new LiveUser();
                ResultSet rset = null;
                StringBuilder sql = new StringBuilder("SELECT * FROM tbltliveuser WHERE custid = ? AND clientgroupid = ? AND CallingStationId IN (");

                for (int i = 0; i < macs.size(); i++) {
                    sql.append("'" + macs.get(i) + "'");
                    if (i < macs.size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(") order by 1 LIMIT 1");

                try (Connection conn = DataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    stmt.setInt(1, custId);
                    stmt.setLong(2, cltGrpId);
                    rset = stmt.executeQuery();
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Result Set is %s", rset));
                    }
                    while (rset.next()) {
                        liveUser.setUserName(rset.getString("UserName"));
                        liveUser.setlClass(rset.getString("CLASS"));
                        liveUser.setAcctAuthentic(rset.getString("ACCTAUTHENTIC"));
                        liveUser.setAcctInputGigawords(rset.getString("ACCTINPUTGIGAWORDS"));
                        liveUser.setNasPortType(rset.getString("NASPORTTYPE"));
                        liveUser.setCallingStationId(rset.getString("CALLINGSTATIONID"));
                        liveUser.setCalledStationId(rset.getString("CALLEDSTATIONID"));
                        liveUser.setNasPortId(rset.getString("NASPORTID"));
                        liveUser.setNasPort(rset.getString("NASPORT"));
                        liveUser.setFramedIpAddress(rset.getString("FRAMEDIPADDRESS"));
                        liveUser.setVendorSpecific(rset.getString("VENDORSPECIFIC"));
                        liveUser.setAcctInputOctets(rset.getString("ACCTINPUTOCTETS"));
                        liveUser.setAcctOutputOctets(rset.getString("ACCTOUTPUTOCTETS"));
                        liveUser.setAcctInputGigawords(rset.getString("ACCTINPUTGIGAWORDS"));
                        liveUser.setAcctOutputGigawords(rset.getString("ACCTOUTPUTGIGAWORDS"));
                        liveUser.setAcctInputPackets(rset.getString("ACCTINPUTPACKETS"));
                        liveUser.setAcctOutputPackets(rset.getString("ACCTOUTPUTPACKETS"));
                        liveUser.setAcctSessionTime(rset.getString("ACCTSESSIONTIME"));
                        liveUser.setAcctOutputPackets(rset.getString("ACCTOUTPUTPACKETS"));
                        liveUser.setNasIdentifier(rset.getString("NASIdentifier"));
                        liveUser.setAcctSessionId(rset.getString("AcctSessionId"));
                        liveUser.setNasIpAddress(rset.getString("NASIPAddress"));
                    }
                    return liveUser;
                } catch (SQLException ex) {
                    log.error(SQL_EXCEPTION, ex.getMessage());
                } finally {
                    try {
                        if (rset != null) rset.close();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
                if (liveUser.getUserName() != null)
                    return liveUser;
            }
        } catch (Exception ex) {
            log.error("Error to Fetch First Session based on Mac and custId: " + custId + " cltGrpId: " + cltGrpId);
        }
        return null;
    }

    public List<Long> getLiveUserIdsByRadiusAttr(String strAcctOn, boolean isNasIdentifier) {
        List<Long> cdrIds = new ArrayList<>();

        ResultSet result = null;
        if (isNasIdentifier) {
            try (Connection conn = DataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(strFetchLiveSessionByNASIdentifier)) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("IN Getting Live Session Using: %s, Query: %s", strAcctOn, strFetchLiveSessionByNASIdentifier));
                }
                stmt.setString(1, strAcctOn);
//            stmt.setLong(2, cprId);
                result = stmt.executeQuery();
                while (result.next()) {
                    cdrIds.add(result.getLong("CDRID"));
                }
                log.info(String.format("No Of Live Session Found: %s, attribute: %s", cdrIds.size(), strAcctOn));
            } catch (Exception ex) {
                log.error("Exception to fetch Live Sesison By: " + strAcctOn);
            } finally {
                try {
                    if (result != null) result.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        } else {
            try (Connection conn = DataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(strFetchLiveSessionByNASIPAddress)) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("IN Getting Live Session Using: %s, Query: %s", strAcctOn, strFetchLiveSessionByNASIPAddress));
                }
                stmt.setString(1, strAcctOn);
//            stmt.setLong(2, cprId);
                result = stmt.executeQuery();
                while (result.next()) {
                    cdrIds.add(result.getLong("CDRID"));
                }
                log.info(String.format("No Of Live Session Found: %s, attribute: %s", cdrIds.size(), strAcctOn));
            } catch (Exception ex) {
                log.error("Exception to fetch Live Sesison By: " + strAcctOn);
            } finally {
                try {
                    if (result != null) result.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }

        return cdrIds;
    }

    public boolean upsertDBSesion(AccountingRequest acctRequest, ConcurrentMap acctFieldMapping, int mvnoid, String sourceAddress, CustomerData custRetrunData, Long clientGroupId, double currentUsageVolumeBySession, long currentUsageTimeBySession, String acctStatusType, double upload, double download, boolean isFaultyMac) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Insert Session FROM DB : %s %s :Mapping %s", QUERY, strLiveSessionUpsert, acctFieldMapping.size()));
        }
        Connection conn = null;
        NamedParameterStatement stmt = null;
        try {
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = new NamedParameterStatement(conn, strLiveSessionUpsert);
            Iterator<Map.Entry<String, String>> itrForInsert = acctFieldMapping.entrySet().iterator();
            while (itrForInsert.hasNext()) {
                RadiusAttribute radAttrib = null;
                Map.Entry<String, String> entry = itrForInsert.next();
                if (!entry.getValue().equalsIgnoreCase("sourceipaddress") && !entry.getValue().equalsIgnoreCase("custid") && !entry.getValue().equalsIgnoreCase("UserName")) {
                    radAttrib = acctRequest.getAttribute(entry.getKey());
                }
                if (radAttrib != null) {
                    log.info("Insert value from Request, key: " + entry.getKey() + " Value: " + radAttrib.getAttributeValue());
                    stmt.setString(entry.getValue(), radAttrib.getAttributeValue());
//					stmt.setString(entry.getValue(),radAttrib.getAttributeValue());
                } else {
                    log.info("Value Not found in Request: " + entry.getKey() + " value: " + entry.getValue());
                    if (entry.getValue().equalsIgnoreCase("sourceipaddress")) {
                        stmt.setString(entry.getValue(), sourceAddress);
                    } else if (entry.getValue().equalsIgnoreCase("custid")) {
                        stmt.setString(entry.getValue(), isFaultyMac ? "0" : Integer.toString(custRetrunData.getCustid()));
                    } else if (entry.getValue().equalsIgnoreCase("UserName")) {
                        //Issue Resolved for ANG-10886
                        String strUsername = custRetrunData.getUsername();
                        log.debug("Username from custRetrunData to insert value in live-user: " + strUsername);
                        if (strUsername == null || strUsername.isEmpty()) {
                            strUsername = acctRequest.getAttribute("User-Name").getAttributeValue();
                            log.debug("Username from request to insert value in live-user: " + strUsername);
                        }
                        stmt.setString(entry.getValue(), strUsername);
                        log.debug(" final Username value to insert value in live-user: " + strUsername);
                    } else if (entry.getValue().equalsIgnoreCase("AcctMultiSessionId")) {
                        String AcctMultiSessionId = "0";
                        if (acctRequest.getAttribute("Acct-Multi-Session-Id") != null) {
                            AcctMultiSessionId = acctRequest.getAttribute("Acct-Multi-Session-Id").getAttributeValue();
                        }
                        stmt.setString(entry.getValue(), AcctMultiSessionId);
                    } else {
                        stmt.setString(entry.getValue(), null);
                    }
                }
            }
            stmt.setTimestamp("createdate", currentDate);
            stmt.setInt("mvnoid", mvnoid);
            stmt.setLong("clientgroupid", clientGroupId);
            stmt.setObject("isthrottlespeed", custRetrunData.isThrottleSpeed());
            stmt.setString("AcctInputOctets", String.valueOf((long) upload));
            stmt.setString("AcctOutputOctets", String.valueOf((long) download));
            if (custRetrunData != null && custRetrunData.getCustomerBasePlan() != null && custRetrunData.getCustomerBasePlan().size() > 0 && !isFaultyMac)
                stmt.setLong("cprid", custRetrunData.getCustomerBasePlan().get(0).getCustpackageid());
            else
                stmt.setLong("cprid", 0); // For unknown customer cprid will null so set 0
            //update values
            Iterator<Map.Entry<String, String>> itrForUpdate = acctFieldMapping.entrySet().iterator();
            while (itrForUpdate.hasNext()) {
                Map.Entry<String, String> entry = itrForUpdate.next();
                RadiusAttribute radAttrib = null;
                if (entry.getValue().equalsIgnoreCase("lastmodificationdate")) {
                    stmt.setTimestamp(entry.getValue(), new Timestamp(new Date().getTime()));
                } else if (!entry.getValue().equalsIgnoreCase("sourceipaddress") && !entry.getValue().equalsIgnoreCase("custid") && !entry.getValue().equalsIgnoreCase("addl2") &&
                        !entry.getValue().equalsIgnoreCase("addl1") && !entry.getValue().equalsIgnoreCase("custid")
                        && !entry.getValue().equalsIgnoreCase("createdate") &&
                        !entry.getValue().equalsIgnoreCase("sourceipaddress") && !entry.getValue().equalsIgnoreCase("mvnoid") && !entry.getValue().equalsIgnoreCase("UserName")) {
                    radAttrib = acctRequest.getAttribute(entry.getKey());

                    if (radAttrib != null) {
                        log.info("Live user update attribute: " + radAttrib.getAttributeType() + ":" + radAttrib.getAttributeValue() + " : Column: " + entry.getValue());
                        String usageQuotaType = custRetrunData.getUsageQuotaType();
                        String entryValue = entry.getValue();
                        if (usageQuotaType != null) {
                            //upload = input_octet->
                            //download = output_octet->
                            if (entryValue.equalsIgnoreCase("AcctOutputOctets") && usageQuotaType.equalsIgnoreCase(CommonConstants.UPLOAD)) {
                                log.debug("usageQuotaType: " + usageQuotaType + " so set AcctOutputOctets: " + 0);
                                // If usage quota type is download, set upload to zero
                                stmt.setString(entryValue, "0");
                            } else if (entryValue.equalsIgnoreCase("AcctInputOctets") && usageQuotaType.equalsIgnoreCase(CommonConstants.DOWNLOAD)) {
                                log.debug("usageQuotaType: " + usageQuotaType + " so set AcctInputOctets: " + 0);
                                // If usage quota type is upload, set download to zero
                                stmt.setString(entryValue, "0");
                            } else {
                                stmt.setString(entryValue, radAttrib.getAttributeValue());
                            }
                        } else {
                            stmt.setString(entryValue, radAttrib.getAttributeValue());
                        }
                        switch (entryValue.toLowerCase()) {
                            case "framedipaddress":
                                if (acctStatusType.equalsIgnoreCase("framed-ip-address-up")) {
                                    stmt.setString("FramedIPAddress", radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("framed-ip-address-down")) {
                                    stmt.setString("FramedIPAddress", null);
                                }
                                break;

                            case "framedipv6address":
                                if (acctStatusType.equalsIgnoreCase("alc-ipv6-address-up")) {
                                    stmt.setString("framedipv6address", radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("alc-ipv6-address-down")) {
                                    stmt.setString("framedipv6address", null);
                                }
                                break;

                            case "delegatedipv6prefix":
                                if (acctStatusType.equalsIgnoreCase("delegated-ipv6-prefix-up")) {
                                    stmt.setString("DelegatedIPv6Prefix", radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("delegated-ipv6-prefix-down")) {
                                    stmt.setString("DelegatedIPv6Prefix", null);
                                }
                                break;

                            default:
                                // No action needed for other entry values
                                break;
                        }

                        switch (entryValue.toLowerCase()) {
                            case "framedipaddress":
                                if (acctStatusType.equalsIgnoreCase("framed-ip-address-up")) {
                                    stmt.setString(entryValue, radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("framed-ip-address-down")) {
                                    stmt.setString(entryValue, null);
                                }
                                break;

                            case "framedipv6address":
                                if (acctStatusType.equalsIgnoreCase("alc-ipv6-address-up")) {
                                    stmt.setString(entryValue, radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("alc-ipv6-address-down")) {
                                    stmt.setString(entryValue, null);
                                }
                                break;

                            case "delegatedipv6prefix":
                                if (acctStatusType.equalsIgnoreCase("delegated-ipv6-prefix-up")) {
                                    log.info(acctStatusType + ": delegatedipv6prefix: " + radAttrib.getAttributeValue() + " ,entryValue: " + entryValue);
                                    stmt.setString(entryValue, radAttrib.getAttributeValue());
                                } else if (acctStatusType.equalsIgnoreCase("delegated-ipv6-prefix-down")) {
                                    log.info(acctStatusType + " :delegatedipv6prefix: null");
                                    stmt.setString(entryValue, null);
                                }
                                break;

                            default:
                                // No action needed for other entry values
                                break;
                        }

                    } else if (entry.getValue().equalsIgnoreCase("AcctMultiSessionId")) {
                        String AcctMultiSessionId = "0";
                        if (acctRequest.getAttribute("Acct-Multi-Session-Id") != null) {
                            AcctMultiSessionId = acctRequest.getAttribute("Acct-Multi-Session-Id").getAttributeValue();
                        }
                        stmt.setString(entry.getValue(), AcctMultiSessionId);
                    } else {
                        stmt.setString(entry.getValue(), null);
                    }
                } else if (entry.getValue().equalsIgnoreCase("UserName")) {
                    String strUsername = custRetrunData.getUsername();
                    if (strUsername == null || strUsername.isEmpty()) {
                        strUsername = acctRequest.getAttribute("User-Name").getAttributeValue();
                        log.debug("Username from request to update value in live-user: " + strUsername);
                    }
                    log.debug(" final Username value to update value in live-user: " + strUsername);
                    stmt.setString(entry.getValue(), strUsername);
                }
            }
            stmt.setObject("isthrottlespeed", custRetrunData.isThrottleSpeed());
            stmt.setString("AcctInputOctets", String.valueOf((long) upload));
            stmt.setString("AcctOutputOctets", String.valueOf((long) download));
            stmt.executeUpdate();
            log.debug(stmt.getStatement().toString());
            log.debug(String.format("Insert Query for live user : %s", stmt.getStatement().toString()));
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }

    public List<LiveUser> fetchLiveSessionsAsRequestBasedOnCustId(int custId) {
        List<LiveUser> liveUsersSessions = new ArrayList<>();
        ResultSet rset = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("IN Getting fetchMacFromCustId for customer ID: %s, query:  %s", custId, strFetchLiveSessionByCustId));
            }
            try (Connection conn = DataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(strFetchLiveSessionByCustId)) {
                stmt.setInt(1, custId);
                rset = stmt.executeQuery();

                while (rset.next()) {
                    LiveUser liveUser = new LiveUser();
                    liveUser.setCdrID(rset.getLong("CDRID"));
                    liveUser.setUserName(rset.getString("UserName"));
                    liveUser.setNasIpAddress(rset.getString("NASIPAddress"));
                    liveUser.setFramedIpAddress(rset.getString("FramedIPAddress"));
                    liveUser.setlClass(rset.getString("Class"));
                    liveUser.setCustid(String.valueOf(custId));
                    liveUser.setCallingStationId(rset.getString("CallingStationId"));
                    liveUser.setNasPort(rset.getString("NASPort"));
                    liveUser.setSourceipaddress(rset.getString("sourceipaddress"));
                    liveUser.setAcctInputOctets(rset.getString("AcctInputOctets"));
                    liveUser.setAcctOutputOctets(rset.getString("AcctOutputOctets"));
                    liveUser.setAcctSessionTime(rset.getString("AcctSessionTime"));
                    liveUser.setCprId(rset.getLong("cprid"));
                    liveUser.setAcctSessionId(rset.getString("AcctSessionId"));
                    liveUsersSessions.add(liveUser);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.error(SQL_EXCEPTION, e);
            }

        } catch (Exception ex) {
            log.error("Error to Fetch MAC based on custId: " + custId);
        } finally {
            try {
                if (rset != null) rset.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return liveUsersSessions;
    }



    public boolean insertRADIUSPACKET(AccountingRequest acctRequest, ConcurrentMap acctFieldMapping, int mvnoid, Double totalTimeMin, String sourceAddress, CustomerData custRetrunData, double upload, double download, Timestamp curentDate) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("IN Insert insertRADIUSPACKET FROM DB : %s %s", QUERY, strRadiusPacketInsert));
        }

        Connection conn = null;
        NamedParameterStatement stmt = null;
        try {
            Properties props = new Properties();
            Class.forName(DbConfig.properties.getProperty("spring.datasource.driver-class-name"));
            conn = DataSource.getConnection();
            stmt = new NamedParameterStatement(conn, strRadiusPacketInsert);
            Iterator<Map.Entry<String, String>> itr = acctFieldMapping.entrySet().iterator();
            while (itr.hasNext()) {
                RadiusAttribute radAttrib = null;
                Map.Entry<String, String> entry = itr.next();
                if (acctRequest != null && !entry.getValue().equalsIgnoreCase("sourceipaddress") && !entry.getValue().equalsIgnoreCase("custid") && !entry.getValue().equalsIgnoreCase("UserName")) {
                    radAttrib = acctRequest.getAttribute(entry.getKey());
                }

                if (radAttrib != null) {
                    if (custRetrunData.getUsageQuotaType() != null && entry.getValue().equalsIgnoreCase("AcctOutputOctets") && custRetrunData.getUsageQuotaType().equalsIgnoreCase(CommonConstants.UPLOAD)) {
                        //If usage quota type is download then set upload to zero
                        stmt.setString(entry.getValue(), "0");
                    } else if (custRetrunData.getUsageQuotaType() != null && entry.getValue().equalsIgnoreCase("AcctInputOctets") && custRetrunData.getUsageQuotaType().equalsIgnoreCase(CommonConstants.DOWNLOAD)) {
                        //If usage quota type is upload then set download to zero
                        stmt.setString(entry.getValue(), "0");
                    } else
                        stmt.setString(entry.getValue(), radAttrib.getAttributeValue());
                } else {
                    if (entry.getValue().equalsIgnoreCase("sourceipaddress")) {
                        //skip
                    } else if (entry.getValue().equalsIgnoreCase("custid")) {
                        stmt.setString(entry.getValue(), Integer.toString(custRetrunData.getCustid()));
                    } else if (entry.getValue().equalsIgnoreCase("UserName")) {
                        //stmt.setString(entry.getValue(),custRetrunData.getUsername());
                        String strUsername = custRetrunData.getUsername();
                        log.debug("Username from custRetrunData to insert value in cdr: " + strUsername);
                        if (strUsername == null || strUsername.isEmpty()) {
                            strUsername = acctRequest.getAttribute("User-Name").getAttributeValue();
                            log.debug("Username from request to insert value in cdr: " + strUsername);
                        }
                        stmt.setString(entry.getValue(), strUsername);
                        log.debug(" final Username value to insert value in cdr: " + strUsername);
                    } //entry.getValue().equalsIgnoreCase("NASIdentifier") && entry.getValue().equalsIgnoreCase("AcctSessionId")
                    else if (entry.getValue().equalsIgnoreCase("NASIdentifier") && acctRequest.getAttribute("NAS-Identifier") != null) {
                        stmt.setString(entry.getValue(), acctRequest.getAttribute("NAS-Identifier").getAttributeValue());
                    } else if (entry.getValue().equalsIgnoreCase("AcctSessionId") && acctRequest.getAttribute("Acct-Session-Id") != null) {
                        stmt.setString(entry.getValue(), acctRequest.getAttribute("Acct-Session-Id").getAttributeValue());
                    } else if (entry.getValue().equalsIgnoreCase("NASIPAddress") && acctRequest.getAttribute("NAS-IP-Address") != null) {
                        stmt.setString(entry.getValue(), acctRequest.getAttribute("NAS-IP-Address").getAttributeValue());
                    } else if (entry.getValue().equalsIgnoreCase("AcctMultiSessionId")) {
                        String AcctMultiSessionId = "0";
                        if (acctRequest.getAttribute("Acct-Multi-Session-Id") != null) {
                            AcctMultiSessionId = acctRequest.getAttribute("Acct-Multi-Session-Id").getAttributeValue();
                        }
                        stmt.setString(entry.getValue(), AcctMultiSessionId);
                    } else {
                        stmt.setString(entry.getValue(), null);
                    }
                }
            }
//            Date startDate = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(totalTimeMin.longValue()));
//            Timestamp ts = new Timestamp(startDate.getTime());
//            Timestamp timestamp = new Timestamp(new Date().getTime());
            stmt.setTimestamp("createdate", curentDate);
            stmt.setTimestamp("lastmodificationdate", currentDate);
            stmt.setInt("mvnoid", mvnoid);
            stmt.setString("AcctInputOctets", String.valueOf((long) upload));
            stmt.setString("AcctOutputOctets", String.valueOf((long) download));
            stmt.setObject("isthrottlespeed", custRetrunData.isThrottleSpeed());
            stmt.setObject("processed", false);
            if((custRetrunData != null) && (custRetrunData.getCustomerBasePlan()!=null)){
                stmt.setString("planname", custRetrunData.getCustomerBasePlan().get(0).getPlanName());
            }else {
                stmt.setString("planname", "");
            }

            log.debug(stmt.getStatement().toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(SQL_EXCEPTION, e);
            e.printStackTrace();
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
        return true;
    }


}
