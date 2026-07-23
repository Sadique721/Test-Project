package com.savbill.revenuemanagement.core.dto.customer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.stereotype.Component;

/**
 * The persistent class for the tblmsmpspr database table.
 * 
 */
@Component
public class Subscriber implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accountnumber;

	private String accounttype;

	private String authorizationpolicyname;

	private String balance;

	private Date birthdate;

	private String brand;

	private String country;

	private Date createdate;

	private String cui;

	private String customertype;

	private String email;

	private BigDecimal encryptiontype;

	private Date expirydate;

	private BigDecimal failureattempt;

	private Date firstlogintime;

	private String firstname;

	private String gatewayaddress;

	private String gender;

	private String hotspotname;

	private String imei;

	private String imsi;

	private Date lastlogintime;

	private Date lastlogouttime;

	private Date lastmodifieddate;

	private String lastname;

	private Date lastbilldate;

	private Timestamp laststatuschangedate;

	private String location;

	private String loginip;

	private String loginservicepolicy;

	private String mac;

	private String msisdn;

	private Date nextbilldate;

	private String param1;

	private String param10;

	private String param2;

	private String param3;

	private String param4;

	private String param5;

	private String param6;

	private String param7;

	private String param8;

	private String param9;

	private Date passcreatetime;

	private Date passupdatetime;

	private BigDecimal passvalidity;

	private String password;

	private String phone;

	private String qos;

	private String servicetype;

	private BigDecimal sessioncount;

	private String sessionid;

	private BigDecimal sourceport;

	private String ssid;

	private String status;

	private BigDecimal subscriberid;

	private String subscriberidentity;

	private String subscriberpackage;

	//private String subscriberpackageid;

	ArrayList<String> subscriberpackageid = new ArrayList<String>();				

	private BigDecimal timebasedtotalquota;

	private BigDecimal timebasedunusedquota;

	private BigDecimal timebasedusedquota;

	private String userdevice;

	private BigDecimal volumebasedtotalquota;

	private BigDecimal volumebasedunusedquota;

	private BigDecimal volumebasedusedquota;

	private String vouchercode;

	private String wipcontext;

	private String wipip;

	private String xforwardedfor;

	private String name;
	
	private String billrunid;

	double outstandingbalance;

	String firstusage;
	
	String pan;
	
	String gst;

	Long  parentcustid;

	String invoice_type;

	private Long buId;

	private String username;

	private Integer lcoId;

	private String calendartype;

	private String updatebyname;

	private String mobile;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	private String userName;

	public String getUpdateByName() {
		return updatebyname;
	}

	public void setUpdateByName(String updatebyname) {
		this.updatebyname = updatebyname;
	}

	public String getCreatedByName() {
		return createbyname;
	}

	public void setCreatedByName(String createbyname) {
		this.createbyname = createbyname;
	}

	private String createbyname;

	public String getCalendartype() {
		return calendartype;
	}

	public void setCalendartype(String calendartype) {
		this.calendartype = calendartype;
	}

	public Integer getLcoId() {
		return lcoId;
	}

	public void setLcoId(Integer lcoId) {
		this.lcoId = lcoId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getParentcustid() {
		return parentcustid;
	}

	public void setParentcustid(Long parentcustid) {
		this.parentcustid = parentcustid;
	}

	public String getInvoice_type() {
		return invoice_type;
	}

	public void setInvoice_type(String invoice_type) {
		this.invoice_type = invoice_type;
	}

	public String getBillrunid() {
		return billrunid;
	}

	public void setBillrunid(String billrunid) {
		this.billrunid = billrunid;
	}

	public Subscriber() {
	}

	public String getAccountnumber() {

		return this.accountnumber;
	}

	@XmlElement(nillable = true)
	public void setAccountnumber(String accountnumber) {

		this.accountnumber = accountnumber;
	}

	public String getAccounttype() {

		return this.accounttype;
	}

	@XmlElement(nillable = true)
	public void setAccounttype(String accounttype) {

		this.accounttype = accounttype;
	}

	public String getAuthorizationpolicyname() {

		return this.authorizationpolicyname;
	}

	@XmlElement(nillable = true)
	public void setAuthorizationpolicyname(String authorizationpolicyname) {

		this.authorizationpolicyname = authorizationpolicyname;
	}

	public String getBalance() {

		return this.balance;
	}

	@XmlElement(nillable = true)
	public void setBalance(String balance) {

		this.balance = balance;
	}

	public Date getBirthdate() {

		return this.birthdate;
	}

	@XmlElement(nillable = true)
	public void setBirthdate(Date birthdate) {

		this.birthdate = birthdate;
	}

	public String getBrand() {

		return this.brand;
	}

	@XmlElement(nillable = true)
	public void setBrand(String brand) {

		this.brand = brand;
	}

	public String getCountry() {

		return this.country;
	}

	@XmlElement(nillable = true)
	public void setCountry(String country) {

		this.country = country;
	}

	public Date getCreatedate() {

		return this.createdate;
	}

	@XmlElement(nillable = true)
	public void setCreatedate(Date createdate) {

		this.createdate = createdate;
	}

	public String getCui() {

		return this.cui;
	}

	@XmlElement(nillable = true)
	public void setCui(String cui) {

		this.cui = cui;
	}

	public String getCustomertype() {

		return this.customertype;
	}

	@XmlElement(nillable = true)
	public void setCustomertype(String customertype) {

		this.customertype = customertype;
	}

	public String getEmail() {

		return this.email;
	}

	@XmlElement(nillable = true)
	public void setEmail(String email) {

		this.email = email;
	}

	public BigDecimal getEncryptiontype() {

		return this.encryptiontype;
	}

	@XmlElement(nillable = true)
	public void setEncryptiontype(BigDecimal encryptiontype) {

		this.encryptiontype = encryptiontype;
	}

	public Date getExpirydate() {

		return this.expirydate;
	}

	@XmlElement(nillable = true)
	public void setExpirydate(Date expirydate) {

		this.expirydate = expirydate;
	}

	public BigDecimal getFailureattempt() {

		return this.failureattempt;
	}

	@XmlElement(nillable = true)
	public void setFailureattempt(BigDecimal failureattempt) {

		this.failureattempt = failureattempt;
	}

	public Date getFirstlogintime() {

		return this.firstlogintime;
	}

	@XmlElement(nillable = true)
	public void setFirstlogintime(Date firstlogintime) {

		this.firstlogintime = firstlogintime;
	}

	public String getFirstname() {

		return this.firstname;
	}

	@XmlElement(nillable = true)
	public void setFirstname(String firstname) {

		this.firstname = firstname;
	}

	public String getGatewayaddress() {

		return this.gatewayaddress;
	}

	@XmlElement(nillable = true)
	public void setGatewayaddress(String gatewayaddress) {

		this.gatewayaddress = gatewayaddress;
	}

	public String getGender() {

		return this.gender;
	}

	@XmlElement(nillable = true)
	public void setGender(String gender) {

		this.gender = gender;
	}

	public String getHotspotname() {

		return this.hotspotname;
	}

	@XmlElement(nillable = true)
	public void setHotspotname(String hotspotname) {

		this.hotspotname = hotspotname;
	}

	public String getImei() {

		return this.imei;
	}

	@XmlElement(nillable = true)
	public void setImei(String imei) {

		this.imei = imei;
	}

	public String getImsi() {

		return this.imsi;
	}

	@XmlElement(nillable = true)
	public void setImsi(String imsi) {

		this.imsi = imsi;
	}

	public Date getLastlogintime() {

		return this.lastlogintime;
	}

	@XmlElement(nillable = true)
	public void setLastlogintime(Date lastlogintime) {

		this.lastlogintime = lastlogintime;
	}

	public Date getLastlogouttime() {

		return this.lastlogouttime;
	}

	@XmlElement(nillable = true)
	public void setLastlogouttime(Date lastlogouttime) {

		this.lastlogouttime = lastlogouttime;
	}

	public Date getLastmodifieddate() {

		return this.lastmodifieddate;
	}

	@XmlElement(nillable = true)
	public void setLastmodifieddate(Date lastmodifieddate) {

		this.lastmodifieddate = lastmodifieddate;
	}

	public String getLastname() {

		return this.lastname;
	}

	@XmlElement(nillable = true)
	public void setLastname(String lastname) {

		this.lastname = lastname;
	}

	public Timestamp getLaststatuschangedate() {

		return this.laststatuschangedate;
	}

	@XmlElement(nillable = true)
	public void setLaststatuschangedate(Timestamp laststatuschangedate) {

		this.laststatuschangedate = laststatuschangedate;
	}

	public String getLocation() {

		return this.location;
	}

	@XmlElement(nillable = true)
	public void setLocation(String location) {

		this.location = location;
	}

	public String getLoginip() {

		return this.loginip;
	}

	public void setLoginip(String loginip) {

		this.loginip = loginip;
	}

	public String getLoginservicepolicy() {

		return this.loginservicepolicy;
	}

	public void setLoginservicepolicy(String loginservicepolicy) {

		this.loginservicepolicy = loginservicepolicy;
	}

	public String getMac() {

		return this.mac;
	}

	public void setMac(String mac) {

		this.mac = mac;
	}

	public String getMsisdn() {

		return this.msisdn;
	}

	@XmlElement(nillable = true)
	public void setMsisdn(String msisdn) {

		this.msisdn = msisdn;
	}

	public String getParam1() {

		return this.param1;
	}

	public void setParam1(String param1) {

		this.param1 = param1;
	}

	public String getParam10() {

		return this.param10;
	}

	public void setParam10(String param10) {

		this.param10 = param10;
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

	public String getParam4() {

		return this.param4;
	}

	public void setParam4(String param4) {

		this.param4 = param4;
	}

	public String getParam5() {

		return this.param5;
	}

	public void setParam5(String param5) {

		this.param5 = param5;
	}

	public String getParam6() {

		return this.param6;
	}

	public void setParam6(String param6) {

		this.param6 = param6;
	}

	public String getParam7() {

		return this.param7;
	}

	public void setParam7(String param7) {

		this.param7 = param7;
	}

	public String getParam8() {

		return this.param8;
	}

	public void setParam8(String param8) {

		this.param8 = param8;
	}

	public String getParam9() {

		return this.param9;
	}

	public void setParam9(String param9) {

		this.param9 = param9;
	}

	public Date getPasscreatetime() {

		return this.passcreatetime;
	}

	public void setPasscreatetime(Date passcreatetime) {

		this.passcreatetime = passcreatetime;
	}

	public Date getPassupdatetime() {

		return this.passupdatetime;
	}

	public void setPassupdatetime(Date passupdatetime) {

		this.passupdatetime = passupdatetime;
	}

	public BigDecimal getPassvalidity() {

		return this.passvalidity;
	}

	public void setPassvalidity(BigDecimal passvalidity) {

		this.passvalidity = passvalidity;
	}

	public String getPassword() {

		return this.password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public String getPhone() {

		return this.phone;
	}

	@XmlElement(nillable = true)
	public void setPhone(String phone) {

		this.phone = phone;
	}

	public String getQos() {

		return this.qos;
	}

	@XmlElement(nillable = true)
	public void setQos(String qos) {

		this.qos = qos;
	}

	public String getServicetype() {

		return this.servicetype;
	}

	public void setServicetype(String servicetype) {

		this.servicetype = servicetype;
	}

	public BigDecimal getSessioncount() {

		return this.sessioncount;
	}

	public void setSessioncount(BigDecimal sessioncount) {

		this.sessioncount = sessioncount;
	}

	public String getSessionid() {

		return this.sessionid;
	}

	public void setSessionid(String sessionid) {

		this.sessionid = sessionid;
	}

	public BigDecimal getSourceport() {

		return this.sourceport;
	}

	public void setSourceport(BigDecimal sourceport) {

		this.sourceport = sourceport;
	}

	public String getSsid() {

		return this.ssid;
	}

	public void setSsid(String ssid) {

		this.ssid = ssid;
	}

	public String getStatus() {

		return this.status;
	}

	@XmlElement(nillable = true)
	public void setStatus(String status) {

		this.status = status;
	}

	public BigDecimal getSubscriberid() {

		return this.subscriberid;
	}

	public void setSubscriberid(BigDecimal subscriberid) {

		this.subscriberid = subscriberid;
	}

	public String getSubscriberidentity() {

		return this.subscriberidentity;
	}

	public void setSubscriberidentity(String subscriberidentity) {

		this.subscriberidentity = subscriberidentity;
	}

	public String getSubscriberpackage() {

		return this.subscriberpackage;
	}

	@XmlElement(nillable = true)
	public void setSubscriberpackage(String subscriberpackage) {

		this.subscriberpackage = subscriberpackage;
	}

	

	public ArrayList<String> getSubscriberpackageid() {
		return subscriberpackageid;
	}

	public void setSubscriberpackageid(ArrayList<String> subscriberpackageid) {
		this.subscriberpackageid = subscriberpackageid;
	}

	public BigDecimal getTimebasedtotalquota() {

		return this.timebasedtotalquota;
	}

	@XmlElement(nillable = true)
	public void setTimebasedtotalquota(BigDecimal timebasedtotalquota) {

		this.timebasedtotalquota = timebasedtotalquota;
	}

	public BigDecimal getTimebasedunusedquota() {

		return this.timebasedunusedquota;
	}

	@XmlElement(nillable = true)
	public void setTimebasedunusedquota(BigDecimal timebasedunusedquota) {

		this.timebasedunusedquota = timebasedunusedquota;
	}

	public BigDecimal getTimebasedusedquota() {

		return this.timebasedusedquota;
	}

	@XmlElement(nillable = true)
	public void setTimebasedusedquota(BigDecimal timebasedusedquota) {

		this.timebasedusedquota = timebasedusedquota;
	}

	public String getUserdevice() {

		return this.userdevice;
	}

	public void setUserdevice(String userdevice) {

		this.userdevice = userdevice;
	}

	public BigDecimal getVolumebasedtotalquota() {

		return this.volumebasedtotalquota;
	}

	@XmlElement(nillable = true)
	public void setVolumebasedtotalquota(BigDecimal volumebasedtotalquota) {

		this.volumebasedtotalquota = volumebasedtotalquota;
	}

	public BigDecimal getVolumebasedunusedquota() {

		return this.volumebasedunusedquota;
	}

	@XmlElement(nillable = true)
	public void setVolumebasedunusedquota(BigDecimal volumebasedunusedquota) {

		this.volumebasedunusedquota = volumebasedunusedquota;
	}

	public BigDecimal getVolumebasedusedquota() {

		return this.volumebasedusedquota;
	}

	@XmlElement(nillable = true)
	public void setVolumebasedusedquota(BigDecimal volumebasedusedquota) {

		this.volumebasedusedquota = volumebasedusedquota;
	}

	public String getVouchercode() {

		return this.vouchercode;
	}

	public void setVouchercode(String vouchercode) {

		this.vouchercode = vouchercode;
	}

	public String getWipcontext() {

		return this.wipcontext;
	}

	public void setWipcontext(String wipcontext) {

		this.wipcontext = wipcontext;
	}

	public String getWipip() {

		return this.wipip;
	}

	public void setWipip(String wipip) {

		this.wipip = wipip;
	}

	public String getXforwardedfor() {

		return this.xforwardedfor;
	}

	public void setXforwardedfor(String xforwardedfor) {

		this.xforwardedfor = xforwardedfor;
	}

	/**
	 * @return the nextbilldate
	 */
	public Date getNextbilldate() {

		return nextbilldate;
	}

	/**
	 * @param nextbilldate
	 *            the nextbilldate to set
	 */
	public void setNextbilldate(Date nextbilldate) {

		this.nextbilldate = nextbilldate;
	}

	/**
	 * @return the lastbilldate
	 */
	public Date getLastbilldate() {
		return lastbilldate;
	}

	/**
	 * @param lastbilldate
	 *            the lastbilldate to set
	 */
	public void setLastbilldate(Date lastbilldate) {
		this.lastbilldate = lastbilldate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	

	public double getOutstandingbalance() {
		return outstandingbalance;
	}

	public void setOutstandingbalance(double outstandingbalance) {
		this.outstandingbalance = outstandingbalance;
	}
	
	

	public String getFirstusage() {
		return firstusage;
	}

	public void setFirstusage(String firstusage) {
		this.firstusage = firstusage;
	}

	
	
	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getGst() {
		return gst;
	}

	public void setGst(String gst) {
		this.gst = gst;
	}

	public Long getBuId() {
		return buId;
	}

	public void setBuId(Long buId) {
		this.buId = buId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "Subscriber [accountnumber=" + accountnumber + ", accounttype=" + accounttype
				+ ", authorizationpolicyname=" + authorizationpolicyname + ", balance=" + balance + ", birthdate="
				+ birthdate + ", brand=" + brand + ", country=" + country + ", createdate=" + createdate + ", cui="
				+ cui + ", customertype=" + customertype + ", email=" + email + ", encryptiontype=" + encryptiontype
				+ ", expirydate=" + expirydate + ", failureattempt=" + failureattempt + ", firstlogintime="
				+ firstlogintime + ", firstname=" + firstname + ", gatewayaddress=" + gatewayaddress + ", gender="
				+ gender + ", hotspotname=" + hotspotname + ", imei=" + imei + ", imsi=" + imsi + ", lastlogintime="
				+ lastlogintime + ", lastlogouttime=" + lastlogouttime + ", lastmodifieddate=" + lastmodifieddate
				+ ", lastname=" + lastname + ", lastbilldate=" + lastbilldate + ", laststatuschangedate="
				+ laststatuschangedate + ", location=" + location + ", loginip=" + loginip + ", loginservicepolicy="
				+ loginservicepolicy + ", mac=" + mac + ", msisdn=" + msisdn + ", nextbilldate=" + nextbilldate
				+ ", param1=" + param1 + ", param10=" + param10 + ", param2=" + param2 + ", param3=" + param3
				+ ", param4=" + param4 + ", param5=" + param5 + ", param6=" + param6 + ", param7=" + param7
				+ ", param8=" + param8 + ", param9=" + param9 + ", passcreatetime=" + passcreatetime
				+ ", passupdatetime=" + passupdatetime + ", passvalidity=" + passvalidity + ", password=" + password
				+ ", phone=" + phone + ", qos=" + qos + ", servicetype=" + servicetype + ", sessioncount="
				+ sessioncount + ", sessionid=" + sessionid + ", sourceport=" + sourceport + ", ssid=" + ssid
				+ ", status=" + status + ", subscriberid=" + subscriberid + ", subscriberidentity=" + subscriberidentity
				+ ", subscriberpackage=" + subscriberpackage + ", subscriberpackageid=" + subscriberpackageid
				+ ", timebasedtotalquota=" + timebasedtotalquota + ", timebasedunusedquota=" + timebasedunusedquota
				+ ", timebasedusedquota=" + timebasedusedquota + ", userdevice=" + userdevice
				+ ", volumebasedtotalquota=" + volumebasedtotalquota + ", volumebasedunusedquota="
				+ volumebasedunusedquota + ", volumebasedusedquota=" + volumebasedusedquota + ", vouchercode="
				+ vouchercode + ", wipcontext=" + wipcontext + ", wipip=" + wipip + ", xforwardedfor=" + xforwardedfor
				+ ", name=" + name + ", billrunid=" + billrunid + ", outstandingbalance=" + outstandingbalance
				+ ", firstusage=" + firstusage + ", pan=" + pan + ", gst=" + gst + ", createbyname=" + createbyname +" , updatebyname=" + updatebyname +" , username="+username+"]";
	}
}
