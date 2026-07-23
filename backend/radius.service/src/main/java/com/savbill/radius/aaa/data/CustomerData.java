package com.savbill.radius.aaa.data;

import com.savbill.radius.entity.VLANManagement;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerData {

    private String title;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String status;
    private int custid;
    private int balance;
    private Timestamp expirydate;
    private String checkitem;
    private Map<String, String> replyitem;
    private boolean authStatus;
    private int failcount;
    private Timestamp lastlogintime;
    private String strReplyMessage;
    private List<Integer> radiusprofile;
    private Timestamp lastpasswordupdate;

    private String passwordcheck;
    private String asnNumber;
    private String bngRouterInterface;
    private String bngRouterName;
    private String ipPrefixes;
    private String ipv6Prefixes;
    private String lanIP;
    private String lanIPV6;
    private String llAccountID;
    private String llConnectionType;
    private String llExpiryDate;
    private String llMedium;
    private String llServiceID;
    private String macAddress;
    private String peerIP;
    private String poolIP;
    private String qosPolicyName;
    private String rdExport;
    private String rdValue;
    private String vLanId;
    private String vrfName;
    private String vsiId;
    private String vsiName;
    private String wanIP;
    private String wanIPV6;
    private boolean macflow;
    private Integer mvnoId;
    private String framedIPAddress;
    private String nasIPAddress;

    private String vlanidValidate;
    private String nasPortidValidate;
    private String framedIpValidate;
    private String framedIp6Validate;
    private String macAddressValidate;
    private String nasIpValidate;

    private String sourceip;
    private String strClass;
    private String ippoolbind;
    private String frameipbind;

    private int parentCustId;

    List<CustomerPlanData> customerAllPlan;

    List<CustomerPlanData> customerVolueBooster;

    List<CustomerPlanData> customerQuotaBooster;

    List<CustomerPlanData> customerBasePlan;

    boolean isFreeQuota;

    private Integer maxconcurrentsession;

    private Double usedQuota;

    private boolean isSavbillBSSDb;

    private String eventName;

    private Boolean macProvision;

    private Boolean macAuthEnable;

    private String framedroute;

    private String delegatedprefix;

    private String gatewayip;

    private String ipPoolNameBind;

    private String framedIpBind;

    private String framedIp;

    private VLANManagement vlanManagement;

    private String usageQuotaType;

    private String framedIPNetmask;
    private String framedIPv6Prefix;
    private String primaryDNS;
    private String primaryIPv6DNS;
    private String secondaryIPv6DNS;
    private String secondaryDNS;

    private Integer macRetentionPeriod;
    private String macRetentionUnit;

    private boolean isThrottleSpeed;

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public int getCustid() {
        return custid;
    }


    public void setCustid(int custid) {
        this.custid = custid;
    }


    public int getBalance() {
        return balance;
    }


    public void setBalance(int balance) {
        this.balance = balance;
    }


    public Timestamp getExpirydate() {
        return expirydate;
    }


    public void setExpirydate(Timestamp expirydate) {
        this.expirydate = expirydate;
    }


    public String getCheckitem() {
        return checkitem;
    }


    public void setCheckitem(String checkitem) {
        this.checkitem = checkitem;
    }


    public void setReplyitem(HashMap<String, String> replyitem) {
        this.replyitem = replyitem;
    }

    public Map getReplyitem() {
        return replyitem;
    }

    public boolean isAuthStatus() {
        return authStatus;
    }


    public void setAuthStatus(boolean authStatus) {
        this.authStatus = authStatus;
    }


    public int getFailcount() {
        return failcount;
    }


    public void setFailcount(int failcount) {
        this.failcount = failcount;
    }


    public Timestamp getLastlogintime() {
        return lastlogintime;
    }


    public void setLastlogintime(Timestamp lastlogintime) {
        this.lastlogintime = lastlogintime;
    }


    public String getStrReplyMessage() {
        return strReplyMessage;
    }


    public void setStrReplyMessage(String strReplyMessage) {
        this.strReplyMessage = strReplyMessage;
    }


    public List<Integer> getRadiusprofile() {
        return radiusprofile;
    }


    public void setRadiusprofile(List<Integer> radiusprofile) {
        this.radiusprofile = radiusprofile;
    }


    public Timestamp getLastpasswordupdate() {
        return lastpasswordupdate;
    }


    public void setLastpasswordupdate(Timestamp lastpasswordupdate) {
        this.lastpasswordupdate = lastpasswordupdate;
    }


    public String getPasswordcheck() {
        return passwordcheck;
    }


    public void setPasswordcheck(String passwordcheck) {
        this.passwordcheck = passwordcheck;
    }


    public String getAsnNumber() {
        return asnNumber;
    }


    public void setAsnNumber(String asnNumber) {
        this.asnNumber = asnNumber;
    }


    public String getBngRouterInterface() {
        return bngRouterInterface;
    }


    public void setBngRouterInterface(String bngRouterInterface) {
        this.bngRouterInterface = bngRouterInterface;
    }


    public String getBngRouterName() {
        return bngRouterName;
    }


    public void setBngRouterName(String bngRouterName) {
        this.bngRouterName = bngRouterName;
    }


    public String getIpPrefixes() {
        return ipPrefixes;
    }


    public void setIpPrefixes(String ipPrefixes) {
        this.ipPrefixes = ipPrefixes;
    }


    public String getIpv6Prefixes() {
        return ipv6Prefixes;
    }


    public void setIpv6Prefixes(String ipv6Prefixes) {
        this.ipv6Prefixes = ipv6Prefixes;
    }


    public String getLanIP() {
        return lanIP;
    }


    public void setLanIP(String lanIP) {
        this.lanIP = lanIP;
    }


    public String getLanIPV6() {
        return lanIPV6;
    }


    public void setLanIPV6(String lanIPV6) {
        this.lanIPV6 = lanIPV6;
    }


    public String getLlAccountID() {
        return llAccountID;
    }


    public void setLlAccountID(String llAccountID) {
        this.llAccountID = llAccountID;
    }


    public String getLlConnectionType() {
        return llConnectionType;
    }


    public void setLlConnectionType(String llConnectionType) {
        this.llConnectionType = llConnectionType;
    }


    public String getLlExpiryDate() {
        return llExpiryDate;
    }


    public void setLlExpiryDate(String llExpiryDate) {
        this.llExpiryDate = llExpiryDate;
    }


    public String getLlMedium() {
        return llMedium;
    }


    public void setLlMedium(String llMedium) {
        this.llMedium = llMedium;
    }


    public String getLlServiceID() {
        return llServiceID;
    }


    public void setLlServiceID(String llServiceID) {
        this.llServiceID = llServiceID;
    }


    public String getMacAddress() {
        return macAddress;
    }


    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }


    public String getPeerIP() {
        return peerIP;
    }


    public void setPeerIP(String peerIP) {
        this.peerIP = peerIP;
    }


    public String getPoolIP() {
        return poolIP;
    }


    public void setPoolIP(String poolIP) {
        this.poolIP = poolIP;
    }


    public String getQosPolicyName() {
        return qosPolicyName;
    }


    public void setQosPolicyName(String qosPolicyName) {
        this.qosPolicyName = qosPolicyName;
    }


    public String getRdExport() {
        return rdExport;
    }


    public void setRdExport(String rdExport) {
        this.rdExport = rdExport;
    }


    public String getRdValue() {
        return rdValue;
    }


    public void setRdValue(String rdValue) {
        this.rdValue = rdValue;
    }


    public String getvLanId() {
        return vLanId;
    }


    public void setvLanId(String vLanId) {
        this.vLanId = vLanId;
    }


    public String getVrfName() {
        return vrfName;
    }


    public void setVrfName(String vrfName) {
        this.vrfName = vrfName;
    }


    public String getVsiId() {
        return vsiId;
    }


    public void setVsiId(String vsiId) {
        this.vsiId = vsiId;
    }


    public String getVsiName() {
        return vsiName;
    }


    public void setVsiName(String vsiName) {
        this.vsiName = vsiName;
    }


    public String getWanIP() {
        return wanIP;
    }


    public void setWanIP(String wanIP) {
        this.wanIP = wanIP;
    }


    public String getWanIPV6() {
        return wanIPV6;
    }


    public void setWanIPV6(String wanIPV6) {
        this.wanIPV6 = wanIPV6;
    }


    public boolean isMacflow() {
        return macflow;
    }


    public void setMacflow(boolean macflow) {
        this.macflow = macflow;
    }


    public Integer getMvnoId() {
        return mvnoId;
    }


    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }


    public List<CustomerPlanData> getCustomerAllPlan() {
        return customerAllPlan;
    }


    public void setCustomerAllPlan(List<CustomerPlanData> customerAllPlan) {
        this.customerAllPlan = customerAllPlan;
    }


    public List<CustomerPlanData> getCustomerVolueBooster() {
        return customerVolueBooster;
    }


    public void setCustomerVolueBooster(List<CustomerPlanData> customerVolueBooster) {
        this.customerVolueBooster = customerVolueBooster;
    }


    public List<CustomerPlanData> getCustomerQuotaBooster() {
        return customerQuotaBooster;
    }


    public void setCustomerQuotaBooster(List<CustomerPlanData> customerQuotaBooster) {
        this.customerQuotaBooster = customerQuotaBooster;
    }


    public List<CustomerPlanData> getCustomerBasePlan() {
        return customerBasePlan;
    }


    public void setCustomerBasePlan(List<CustomerPlanData> customerBasePlan) {
        this.customerBasePlan = customerBasePlan;
    }


    public String getStrClass() {
        return strClass;
    }


    public void setStrClass(String strClass) {
        this.strClass = strClass;
    }

    public String getIppoolbind() {
        return ippoolbind;
    }


    public void setIppoolbind(String ippoolbind) {
        this.ippoolbind = ippoolbind;
    }


    public String getFrameipbind() {
        return frameipbind;
    }


    public void setFrameipbind(String frameipbind) {
        this.frameipbind = frameipbind;
    }

    public int getParentCustId() {
        return parentCustId;
    }

    public void setParentCustId(int parentCustId) {
        this.parentCustId = parentCustId;
    }

    public boolean isFreeQuota() {
        return isFreeQuota;
    }

    public void setFreeQuota(boolean freeQuota) {
        isFreeQuota = freeQuota;
    }

    public Integer getMaxconcurrentsession() {
        return maxconcurrentsession;
    }

    public void setMaxconcurrentsession(Integer maxconcurrentsession) {
        this.maxconcurrentsession = maxconcurrentsession;
    }

    public boolean isSavbillBSSDb() {
        return isSavbillBSSDb;
    }

    public void setSavbillBSSDb(boolean savbillBSSDb) {
        isSavbillBSSDb = savbillBSSDb;
    }

    public String getSourceip() {
        return sourceip;
    }

    public void setSourceip(String sourceip) {
        this.sourceip = sourceip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getFramedIPAddress() {
        return framedIPAddress;
    }


    public void setFramedIPAddress(String framedIPAddress) {
        this.framedIPAddress = framedIPAddress;
    }

    public String getNasIPAddress() {
        return nasIPAddress;
    }


    public void setNasIPAddress(String nasIPAddress) {
        this.nasIPAddress = nasIPAddress;
    }


    public String getVlanidValidate() {
        return vlanidValidate;
    }


    public void setVlanidValidate(String vlanidValidate) {
        this.vlanidValidate = vlanidValidate;
    }


    public String getNasPortidValidate() {
        return nasPortidValidate;
    }


    public void setNasPortidValidate(String nasPortidValidate) {
        this.nasPortidValidate = nasPortidValidate;
    }


    public String getFramedIpValidate() {
        return framedIpValidate;
    }


    public void setFramedIpValidate(String framedIpValidate) {
        this.framedIpValidate = framedIpValidate;
    }


    public String getFramedIp6Validate() {
        return framedIp6Validate;
    }


    public void setFramedIp6Validate(String framedIp6Validate) {
        this.framedIp6Validate = framedIp6Validate;
    }


    public String getMacAddressValidate() {
        return macAddressValidate;
    }


    public String getNasIpValidate() {
        return nasIpValidate;
    }


    public void setNasIpValidate(String nasIpValidate) {
        this.nasIpValidate = nasIpValidate;
    }


    public void setMacAddressValidate(String macAddressValidate) {
        this.macAddressValidate = macAddressValidate;
    }

    public Double getUsedQuota() {
        return usedQuota;
    }

    public void setUsedQuota(Double usedQuota) {
        this.usedQuota = usedQuota;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Boolean getMacProvision() {
        return macProvision;
    }

    public void setMacProvision(Boolean macProvision) {
        this.macProvision = macProvision;
    }

    public String getFramedroute() {
        return framedroute;
    }

    public void setFramedroute(String framedroute) {
        this.framedroute = framedroute;
    }

    public String getDelegatedprefix() {
        return delegatedprefix;
    }

    public void setDelegatedprefix(String delegatedprefix) {
        this.delegatedprefix = delegatedprefix;
    }

    public Boolean getMacAuthEnable() {
        return macAuthEnable;
    }

    public void setMacAuthEnable(Boolean macAuthEnable) {
        this.macAuthEnable = macAuthEnable;
    }

    public VLANManagement getVlanManagement() {
        return vlanManagement;
    }

    public void setVlanManagement(VLANManagement vlanManagement) {
        this.vlanManagement = vlanManagement;
    }

    public String getGatewayip() {
        return gatewayip;
    }

    public void setGatewayip(String gatewayip) {
        this.gatewayip = gatewayip;
    }

    public String getIpPoolNameBind() {
        return ipPoolNameBind;
    }

    public void setIpPoolNameBind(String ipPoolNameBind) {
        this.ipPoolNameBind = ipPoolNameBind;
    }

    public String getFramedIpBind() {
        return framedIpBind;
    }

    public void setFramedIpBind(String framedIpBind) {
        this.framedIpBind = framedIpBind;
    }

    public String getFramedIp() {
        return framedIp;
    }

    public void setFramedIp(String framedIp) {
        this.framedIp = framedIp;
    }


    public String getUsageQuotaType() {
        return usageQuotaType;
    }

    public void setUsageQuotaType(String usageQuotaType) {
        this.usageQuotaType = usageQuotaType;
    }

    public String getFramedIPNetmask() {
        return framedIPNetmask;
    }

    public void setFramedIPNetmask(String framedIPNetmask) {
        this.framedIPNetmask = framedIPNetmask;
    }

    public String getFramedIPv6Prefix() {
        return framedIPv6Prefix;
    }

    public void setFramedIPv6Prefix(String framedIPv6Prefix) {
        this.framedIPv6Prefix = framedIPv6Prefix;
    }

    public String getPrimaryDNS() {
        return primaryDNS;
    }

    public void setPrimaryDNS(String primaryDNS) {
        this.primaryDNS = primaryDNS;
    }

    public String getPrimaryIPv6DNS() {
        return primaryIPv6DNS;
    }

    public void setPrimaryIPv6DNS(String primaryIPv6DNS) {
        this.primaryIPv6DNS = primaryIPv6DNS;
    }

    public String getSecondaryIPv6DNS() {
        return secondaryIPv6DNS;
    }

    public void setSecondaryIPv6DNS(String secondaryIPv6DNS) {
        this.secondaryIPv6DNS = secondaryIPv6DNS;
    }

    public String getSecondaryDNS() {
        return secondaryDNS;
    }

    public void setSecondaryDNS(String secondaryDNS) {
        this.secondaryDNS = secondaryDNS;
    }

    public Integer getMacRetentionPeriod() {
        return macRetentionPeriod;
    }

    public void setMacRetentionPeriod(Integer macRetentionPeriod) {
        this.macRetentionPeriod = macRetentionPeriod;
    }

    public String getMacRetentionUnit() {
        return macRetentionUnit;
    }

    public void setMacRetentionUnit(String macRetentionUnit) {
        this.macRetentionUnit = macRetentionUnit;
    }

    public boolean isThrottleSpeed() {
        return isThrottleSpeed;
    }

    public void setThrottleSpeed(boolean throttleSpeed) {
        isThrottleSpeed = throttleSpeed;
    }

    @Override
    public String toString() {
        return "CustomerData{" +
                "title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", custid=" + custid +
                ", balance=" + balance +
                ", expirydate=" + expirydate +
                ", checkitem='" + checkitem + '\'' +
                ", replyitem=" + replyitem +
                ", authStatus=" + authStatus +
                ", failcount=" + failcount +
                ", lastlogintime=" + lastlogintime +
                ", strReplyMessage='" + strReplyMessage + '\'' +
                ", radiusprofile=" + radiusprofile +
                ", lastpasswordupdate=" + lastpasswordupdate +
                ", passwordcheck='" + passwordcheck + '\'' +
                ", asnNumber='" + asnNumber + '\'' +
                ", bngRouterInterface='" + bngRouterInterface + '\'' +
                ", bngRouterName='" + bngRouterName + '\'' +
                ", ipPrefixes='" + ipPrefixes + '\'' +
                ", ipv6Prefixes='" + ipv6Prefixes + '\'' +
                ", lanIP='" + lanIP + '\'' +
                ", lanIPV6='" + lanIPV6 + '\'' +
                ", llAccountID='" + llAccountID + '\'' +
                ", llConnectionType='" + llConnectionType + '\'' +
                ", llExpiryDate='" + llExpiryDate + '\'' +
                ", llMedium='" + llMedium + '\'' +
                ", llServiceID='" + llServiceID + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", peerIP='" + peerIP + '\'' +
                ", poolIP='" + poolIP + '\'' +
                ", qosPolicyName='" + qosPolicyName + '\'' +
                ", rdExport='" + rdExport + '\'' +
                ", rdValue='" + rdValue + '\'' +
                ", vLanId='" + vLanId + '\'' +
                ", vrfName='" + vrfName + '\'' +
                ", vsiId='" + vsiId + '\'' +
                ", vsiName='" + vsiName + '\'' +
                ", wanIP='" + wanIP + '\'' +
                ", wanIPV6='" + wanIPV6 + '\'' +
                ", macflow=" + macflow +
                ", mvnoId=" + mvnoId +
                ", framedIPAddress='" + framedIPAddress + '\'' +
                ", nasIPAddress='" + nasIPAddress + '\'' +
                ", vlanidValidate='" + vlanidValidate + '\'' +
                ", nasPortidValidate='" + nasPortidValidate + '\'' +
                ", framedIpValidate='" + framedIpValidate + '\'' +
                ", framedIp6Validate='" + framedIp6Validate + '\'' +
                ", macAddressValidate='" + macAddressValidate + '\'' +
                ", nasIpValidate='" + nasIpValidate + '\'' +
                ", sourceip='" + sourceip + '\'' +
                ", strClass='" + strClass + '\'' +
                ", ippoolbind='" + ippoolbind + '\'' +
                ", frameipbind='" + frameipbind + '\'' +
                ", parentCustId=" + parentCustId +
                ", customerBasePlan=" + customerBasePlan +
                ", isFreeQuota=" + isFreeQuota +
                ", maxconcurrentsession=" + maxconcurrentsession +
                ", usedQuota=" + usedQuota +
                ", isSavbillBSSDb=" + isSavbillBSSDb +
                ", eventName='" + eventName + '\'' +
                ", macProvision=" + macProvision +
                ", macAuthEnable=" + macAuthEnable +
                ", framedroute='" + framedroute + '\'' +
                ", delegatedprefix='" + delegatedprefix + '\'' +
                '}';
    }
}
