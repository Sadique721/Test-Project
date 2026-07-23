package com.savbill.revenuemanagement.core.dto.invoice.xml;

import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the tblmpostpaidplan database table.
 * 
 */

@Data
public class PlanInformation
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long postpaidplanid;

    private String displayname;

    private Date createdate;

    private BigDecimal createdbystaffid;

    private String description;

    private String downloadqos;

    private Date enddate;

    private BigDecimal eventgroupid;

    private BigDecimal lastmodifiedbystaffid;

    private Date lastmodifieddate;

    private BigDecimal mvnoid;

    private String name;

    private String param1;

    private String param2;

    private String param3;

    private BigDecimal quota;

    private String quotaunit;

    private Date startdate;

    private String status;

    private String uploadqos;
    
    private String saccode;

    private Charge charges;

    private String planGroupName;

    private String service;

    private String serviceId;

    private Long customerCount;

    private Double totalCommissionAmount;

    private String sac;

    private  Double validity;

    public PlanInformation() {
    }

    public long getPostpaidplanid() {

        return this.postpaidplanid;
    }

    public void setPostpaidplanid(long postpaidplanid) {

        this.postpaidplanid = postpaidplanid;
    }

    public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public Date getCreatedate() {

        return this.createdate;
    }

    public void setCreatedate(Date createdate) {

        this.createdate = createdate;
    }

    public BigDecimal getCreatedbystaffid() {

        return this.createdbystaffid;
    }

    public void setCreatedbystaffid(BigDecimal createdbystaffid) {

        this.createdbystaffid = createdbystaffid;
    }

    public String getDescription() {

        return this.description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getDownloadqos() {

        return this.downloadqos;
    }

    public void setDownloadqos(String downloadqos) {

        this.downloadqos = downloadqos;
    }

    public Date getEnddate() {

        return this.enddate;
    }

    public void setEnddate(Date enddate) {

        this.enddate = enddate;
    }

    public BigDecimal getEventgroupid() {

        return this.eventgroupid;
    }

    public void setEventgroupid(BigDecimal eventgroupid) {

        this.eventgroupid = eventgroupid;
    }

    public BigDecimal getLastmodifiedbystaffid() {

        return this.lastmodifiedbystaffid;
    }

    public void setLastmodifiedbystaffid(BigDecimal lastmodifiedbystaffid) {

        this.lastmodifiedbystaffid = lastmodifiedbystaffid;
    }

    public Date getLastmodifieddate() {

        return this.lastmodifieddate;
    }

    public void setLastmodifieddate(Date lastmodifieddate) {

        this.lastmodifieddate = lastmodifieddate;
    }

    public BigDecimal getMvnoid() {

        return this.mvnoid;
    }

    public void setMvnoid(BigDecimal mvnoid) {

        this.mvnoid = mvnoid;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getParam1() {

        return this.param1;
    }

    public void setParam1(String param1) {

        this.param1 = param1;
    }

    public String getParam2() {

        return this.param2;
    }

    public void setParam2(String param2) {

        this.param2 = param2;
    }

    public String getParam3() {

        return this.param3;
    }

    public void setParam3(String param3) {

        this.param3 = param3;
    }

    public BigDecimal getQuota() {

        return this.quota;
    }

    public void setQuota(BigDecimal quota) {

        this.quota = quota;
    }

    public String getQuotaunit() {

        return this.quotaunit;
    }

    public void setQuotaunit(String quotaunit) {

        this.quotaunit = quotaunit;
    }

    public Date getStartdate() {

        return this.startdate;
    }

    public void setStartdate(Date startdate) {

        this.startdate = startdate;
    }

    public String getStatus() {

        return this.status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public String getUploadqos() {

        return this.uploadqos;
    }

    public void setUploadqos(String uploadqos) {

        this.uploadqos = uploadqos;
    }
        

	public String getSaccode() {
		return saccode;
	}

	public void setSaccode(String saccode) {
		this.saccode = saccode;
	}

    public Charge getCharges() {
        return charges;
    }

    public void setCharges(Charge charges) {
        this.charges = charges;
    }

    public String getPlanGroupName() {
        return planGroupName;
    }

    public void setPlanGroupName(String planGroupName) {
        this.planGroupName = planGroupName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */

    public String getSac() {
        return sac;
    }

    public void setSac(String sac) {
        this.sac = sac;
    }

    public Double getValidity() {
        return validity;
    }

    public void setValidity(Double validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {

        return "PostpaidPlan [postpaidplanid=" + postpaidplanid + ", displayname="
            + displayname + ", createdate=" + createdate + ", createdbystaffid="
            + createdbystaffid + ", description=" + description
            + ", downloadqos=" + downloadqos + ", enddate=" + enddate
            + ", eventgroupid=" + eventgroupid + ", lastmodifiedbystaffid="
            + lastmodifiedbystaffid + ", lastmodifieddate=" + lastmodifieddate
            + ", mvnoid=" + mvnoid + ", name=" + name + ", param1=" + param1
            + ", param2=" + param2 + ", param3=" + param3 + ", quota=" + quota
            + ", quotaunit=" + quotaunit + ", startdate=" + startdate
            + ", status=" + status + ", uploadqos=" + uploadqos + "]";
    }

}
