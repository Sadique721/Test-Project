package com.savbill.radius.aaa.data;

import com.savbill.radius.entity.PlanUsagePercentageMapping;
import com.savbill.radius.entity.QOSPolicyGatewayMapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CustomerPlanData {

    private double timebasedtotalquota;
    private double timebasedusedquota;
    private double timebasedunusedquota;

    private double volumebasedtotalquota;
    private double currentsessionusagevolume;
    private double currentsessionusagetime;
    private double volumebasedusedquota;
    private double totalvolumebasedunusedquota;
    private double volumebasedunusedquota;

    private double reservedtotalquota;
    private double reservedusedquota;
    private double reservedunusedquota;

    private String planType;
    private String planGroup;
    private String planName;
    private int planid;
    private int custid;

    private Timestamp startdate;
    private Timestamp enddate;

    private String baseparam1;
    private String baseparam2;
    private String baseparam3;
    private String thparam1;
    private String thparam2;
    private String thparam3;
    private String basepolicyname;
    private String thpolicyname;

    private String quotatype;
    private String quotaunit;
    private String timequotaunit;
    private boolean allowoverusage;
    private boolean usagereached;

    private long timequota;
    private double volumequota;

    private Integer custpackageid;
    private Integer mvnoId;

    private int concurrency;
    private String custPlanStatus;
    private int timepolicyid;

    private String radServiceType;

    private double qosspeed;

    List<QOSPolicyGatewayMapping> qosPolicyGatewayMapping;

    List<QOSPolicyGatewayMapping> basPlanQosPolicyGatewayMapping;

    List timepolicyData = new ArrayList<TimepolicyData>();

    List<PlanQosPolicyMapping> planQosPolicyMapping;

    List<PlanUsagePercentageMapping> planUsagePercentageMappingList;

    private boolean isChunkAvailable;

    private boolean isPlanQosFire;

    private Double reservedQuotaInPer;

    private Double totalReservedQuota;

    private Integer custquotaid;

    private String usageQuotaType;

    private boolean skipQuotaUpdate;

    private boolean isTriggerCoaDm;

    private String onQuotaExhaustEventName;

    private boolean isNotBasePlan;
    private boolean updateVolumeQuota;

    private String purchaseType;

    public boolean isAddonToBase() {
        return addonToBase;
    }

    public void setAddonToBase(boolean addonToBase) {
        this.addonToBase = addonToBase;
    }

    private boolean addonToBase;

    public List getTimepolicyData() {
        return timepolicyData;
    }

    public void setTimepolicyData(List timepolicyData) {
        this.timepolicyData = timepolicyData;
    }

    public double getTimebasedtotalquota() {
        return timebasedtotalquota;
    }

    public void setTimebasedtotalquota(double timebasedtotalquota) {
        this.timebasedtotalquota = timebasedtotalquota;
    }

    public double getTimebasedusedquota() {
        return timebasedusedquota;
    }

    public void setTimebasedusedquota(double timebasedusedquota) {
        this.timebasedusedquota = timebasedusedquota;
    }

    public double getTimebasedunusedquota() {
        return timebasedunusedquota;
    }

    public void setTimebasedunusedquota(double timebasedunusedquota) {
        this.timebasedunusedquota = timebasedunusedquota;
    }

    public double getVolumebasedtotalquota() {
        return volumebasedtotalquota;
    }

    public void setVolumebasedtotalquota(double volumebasedtotalquota) {
        this.volumebasedtotalquota = volumebasedtotalquota;
    }

    public double getVolumebasedusedquota() {
        return volumebasedusedquota;
    }

    public void setVolumebasedusedquota(double volumebasedusedquota) {
        this.volumebasedusedquota = volumebasedusedquota;
    }

    public double getVolumebasedunusedquota() {
        return volumebasedunusedquota;
    }

    public void setVolumebasedunusedquota(double volumebasedunusedquota) {
        this.volumebasedunusedquota = volumebasedunusedquota;
    }

    public double getTotalvolumebasedunusedquota() {
        return totalvolumebasedunusedquota;
    }

    public void setTotalvolumebasedunusedquota(double totalvolumebasedunusedquota) {
        this.totalvolumebasedunusedquota = totalvolumebasedunusedquota;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getPlanid() {
        return planid;
    }

    public void setPlanid(int planid) {
        this.planid = planid;
    }

    public Timestamp getStartdate() {
        return startdate;
    }

    public void setStartdate(Timestamp startdate) {
        this.startdate = startdate;
    }

    public Timestamp getEnddate() {
        return enddate;
    }

    public void setEnddate(Timestamp enddate) {
        this.enddate = enddate;
    }

    public String getThpolicyname() {
        return thpolicyname;
    }

    public void setThpolicyname(String thpolicyname) {
        this.thpolicyname = thpolicyname;
    }

    public String getBaseparam1() {
        return baseparam1;
    }

    public void setBaseparam1(String baseparam1) {
        this.baseparam1 = baseparam1;
    }

    public String getBaseparam2() {
        return baseparam2;
    }

    public void setBaseparam2(String baseparam2) {
        this.baseparam2 = baseparam2;
    }

    public String getBaseparam3() {
        return baseparam3;
    }

    public void setBaseparam3(String baseparam3) {
        this.baseparam3 = baseparam3;
    }

    public String getThparam1() {
        return thparam1;
    }

    public void setThparam1(String thparam1) {
        this.thparam1 = thparam1;
    }

    public String getThparam2() {
        return thparam2;
    }

    public void setThparam2(String thparam2) {
        this.thparam2 = thparam2;
    }

    public String getThparam3() {
        return thparam3;
    }

    public void setThparam3(String thparam3) {
        this.thparam3 = thparam3;
    }

    public String getBasepolicyname() {
        return basepolicyname;
    }

    public void setBasepolicyname(String basepolicyname) {
        this.basepolicyname = basepolicyname;
    }

    public String getQuotatype() {
        return quotatype;
    }

    public void setQuotatype(String quotatype) {
        this.quotatype = quotatype;
    }

    public String getQuotaunit() {
        return quotaunit;
    }

    public void setQuotaunit(String quotaunit) {
        this.quotaunit = quotaunit;
    }

    public String getTimequotaunit() {
        return timequotaunit;
    }

    public void setTimequotaunit(String timequotaunit) {
        this.timequotaunit = timequotaunit;
    }

    public boolean isAllowoverusage() {
        return allowoverusage;
    }

    public void setAllowoverusage(boolean allowoverusage) {
        this.allowoverusage = allowoverusage;
    }

    public boolean isUsagereached() {
        return usagereached;
    }

    public void setUsagereached(boolean usagereached) {
        this.usagereached = usagereached;
    }

    public long getTimequota() {
        return timequota;
    }

    public void setTimequota(long timequota) {
        this.timequota = timequota;
    }

    public double getVolumequota() {
        return volumequota;
    }

    public void setVolumequota(double volumequota) {
        this.volumequota = volumequota;
    }

    public Integer getCustpackageid() {
        return custpackageid;
    }

    public void setCustpackageid(Integer custpackageid) {
        this.custpackageid = custpackageid;
    }

    public Integer getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }


    public int getCustid() {
        return custid;
    }

    public void setCustid(int custid) {
        this.custid = custid;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }


    public String getCustPlanStatus() {
        return custPlanStatus;
    }

    public void setCustPlanStatus(String custPlanStatus) {
        this.custPlanStatus = custPlanStatus;
    }

    public List<QOSPolicyGatewayMapping> getQosPolicyGatewayMapping() {
        return qosPolicyGatewayMapping;
    }

    public void setQosPolicyGatewayMapping(List<QOSPolicyGatewayMapping> qosPolicyGatewayMapping) {
        this.qosPolicyGatewayMapping = qosPolicyGatewayMapping;
    }

    public List<QOSPolicyGatewayMapping> getBasPlanQosPolicyGatewayMapping() {
        return basPlanQosPolicyGatewayMapping;
    }

    public void setBasPlanQosPolicyGatewayMapping(List<QOSPolicyGatewayMapping> basPlanQosPolicyGatewayMapping) {
        this.basPlanQosPolicyGatewayMapping = basPlanQosPolicyGatewayMapping;
    }

    public String getPlanGroup() {
        return planGroup;
    }

    public void setPlanGroup(String planGroup) {
        this.planGroup = planGroup;
    }

    public int getTimepolicyid() {
        return timepolicyid;
    }

    public void setTimepolicyid(int timepolicyid) {
        this.timepolicyid = timepolicyid;
    }


    public String getRadServiceType() {
        return radServiceType;
    }

    public void setRadServiceType(String radServiceType) {
        this.radServiceType = radServiceType;
    }

    public boolean isChunkAvailable() {
        return isChunkAvailable;
    }

    public void setChunkAvailable(boolean chunkAvailable) {
        isChunkAvailable = chunkAvailable;
    }

    public Double getReservedQuotaInPer() {
        return reservedQuotaInPer;
    }

    public void setReservedQuotaInPer(Double reservedQuotaInPer) {
        this.reservedQuotaInPer = reservedQuotaInPer;
    }

    public Double getTotalReservedQuota() {
        return totalReservedQuota;
    }

    public void setTotalReservedQuota(Double totalReservedQuota) {
        this.totalReservedQuota = totalReservedQuota;
    }

    public double getReservedtotalquota() {
        return reservedtotalquota;
    }

    public void setReservedtotalquota(double reservedtotalquota) {
        this.reservedtotalquota = reservedtotalquota;
    }

    public double getReservedusedquota() {
        return reservedusedquota;
    }

    public void setReservedusedquota(double reservedusedquota) {
        this.reservedusedquota = reservedusedquota;
    }

    public double getReservedunusedquota() {
        return reservedunusedquota;
    }

    public void setReservedunusedquota(double reservedunusedquota) {
        this.reservedunusedquota = reservedunusedquota;
    }

    public Integer getCustquotaid() {
        return custquotaid;
    }

    public void setCustquotaid(Integer custquotaid) {
        this.custquotaid = custquotaid;
    }

    public List<PlanQosPolicyMapping> getPlanQosPolicyMapping() {
        return planQosPolicyMapping;
    }

    public void setPlanQosPolicyMapping(List<PlanQosPolicyMapping> planQosPolicyMapping) {
        this.planQosPolicyMapping = planQosPolicyMapping;
    }

    public boolean isPlanQosFire() {
        return isPlanQosFire;
    }

    public void setPlanQosFire(boolean planQosFire) {
        isPlanQosFire = planQosFire;
    }

    public double getQosspeed() {
        return qosspeed;
    }

    public void setQosspeed(double qosspeed) {
        this.qosspeed = qosspeed;
    }

    public List<PlanUsagePercentageMapping> getPlanUsagePercentageMappingList() {
        return planUsagePercentageMappingList;
    }

    public void setPlanUsagePercentageMappingList(List<PlanUsagePercentageMapping> planUsagePercentageMappingList) {
        this.planUsagePercentageMappingList = planUsagePercentageMappingList;
    }

    public double getCurrentsessionusagevolume() {
        return this.currentsessionusagevolume;
    }

    public void setCurrentsessionusagevolume(double currentsessionusagevolume) {
        this.currentsessionusagevolume = currentsessionusagevolume;
    }

    public double getCurrentsessionusagetime() {
        return currentsessionusagetime;
    }

    public void setCurrentsessionusagetime(double currentsessionusagetime) {
        this.currentsessionusagetime = currentsessionusagetime;
    }

    public String getUsageQuotaType() {
        return usageQuotaType;
    }

    public void setUsageQuotaType(String usageQuotaType) {
        this.usageQuotaType = usageQuotaType;
    }

    public boolean isSkipQuotaUpdate() {
        return skipQuotaUpdate;
    }

    public void setSkipQuotaUpdate(boolean skipQuotaUpdate) {
        this.skipQuotaUpdate = skipQuotaUpdate;
    }

    public String getOnQuotaExhaustEventName() {
        return onQuotaExhaustEventName;
    }

    public void setOnQuotaExhaustEventName(String onQuotaExhaustEventName) {
        this.onQuotaExhaustEventName = onQuotaExhaustEventName;
    }

    public boolean isTriggerCoaDm() {
        return isTriggerCoaDm;
    }

    public void setTriggerCoaDm(boolean triggerCoaDm) {
        isTriggerCoaDm = triggerCoaDm;
    }

    public boolean isNotBasePlan() {
        return isNotBasePlan;
    }

    public void setNotBasePlan(boolean notBasePlan) {
        isNotBasePlan = notBasePlan;
    }

    public boolean isUpdateVolumeQuota() {
        return updateVolumeQuota;
    }

    public void setUpdateVolumeQuota(boolean updateVolumeQuota) {
        this.updateVolumeQuota = updateVolumeQuota;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String custid() {
        return "DATA : custid:" + custid + ":planid:" + planid + ":custpackageid:" + custpackageid + ":mvnoId:"
                + mvnoId + ":allowoverusage:" + allowoverusage;
    }

}
