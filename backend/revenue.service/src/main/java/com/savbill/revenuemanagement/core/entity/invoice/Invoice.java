package com.savbill.revenuemanagement.core.entity.invoice;

import com.savbill.revenuemanagement.core.controller.invoice.postpaid.ServiceQosPojo;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.ServiceTotalAmount;
import com.savbill.revenuemanagement.core.dto.customer.Subscriber;
import com.savbill.revenuemanagement.core.dto.invoice.xml.PlanInformation;
import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxDto;
import com.savbill.revenuemanagement.core.utillity.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "invoice")
@XmlType(propOrder = {"id", "number", "customerId", "billDate", "createDate", "startDate", "endDate", "dueDate",
        "latePaymentDate", "charge", "tax", "discount", "usage", "total", "previousBalance", "latePaymentFee",
        "currentPayment", "currentDebit", "currentCredit", "totalDue", "totalAmountInWords", "totalDueInWords",
        "billrunid", "billrunStatus", "customerInformation", "addressDetail", "planInformation", "invoiceList",
        "taxList", "laststatuschange", "email", "phone", "firstusage", "planid", "cstchargeid", "custpackrelid", "totalCustomerDiscount",
        "childCustomerIdentity", "BUID", "qty", "totalCustDirectChargeAmount", "custRefName", "printCounter",
        "localbilldate", "localstartdate", "localenddate","createbyname","updatebyname","paymentOwner",
        "inventoryMappingId" ,"custStatus","paymentStatus","paymentOwnerId", "mobile","reprintBy","reprintById","lastReprintDate",
        "promiseStartDate","promiseEndDate","promiseToPayHoldDays","isContainsPromiseToPay","remark","serviceQosPojos","serviceTotalAmounts","partnerTax","tds","operatorName","fullName","chargeDetails","invoiceNumber","partnerPlanCommissionDetail","operatorAddress","childPartnerAddress","pan","verifyQr","kraInvoiceId","curRecptNo","totRecptNo","scuInternalData","scuReceiptSignature","sdcid","sdcmrcNo","sdcDateTime","isStockIO","gracePeriodEndDate","previousWalletBalance","customerActivationDate","planValidityInDays","billingCycleInMonths","subscriptionPlanType","dueAmountFromLastInvoice","customerCurrency"})
public class Invoice implements Serializable {

    private static final double serialVersionUID = 1L;

    long id;

    String number;

    String customerId;

    Date billDate;

    Date createDate;

    Date startDate;

    Date endDate;

    Date dueDate;

    Date latePaymentDate;

    double charge;

    double tax;

    double discount;

    double usage;

    double total;

    double previousBalance;

    double latePaymentFee;

    double currentPayment;

    double currentDebit;

    double currentCredit;

    double totalDue;

    String totalAmountInWords;

    String totalDueInWords;

    Subscriber customerInformation;

    double partnerTax;

    double tds;

    String invoiceNumber;

    String operatorName;

    ArrayList<PlanInformation> planInformation;

    ArrayList<TaxDto> taxList;

    ArrayList<InvoiceDetail> invoiceList;

    ArrayList<SubscriberAddress> addressDetail;

    int billrunid;

    String billrunStatus;

    String laststatuschange;

    String email;

    String phone;

    String mobile;

    String firstusage;

    long planid;

    List<Long> cstchargeid;

    List<Long> custpackrelid;

    Double totalCustomerDiscount;

    String childCustomerIdentity;

    Long BUID;

    Long qty;

    Double totalCustDirectChargeAmount;

    String custRefName;

    Integer printCounter = 0;

    String localbilldate;

    String localstartdate;

    String localenddate;

    private String createbyname;

    private String updatebyname;

    private String paymentOwner;

    private String paymentStatus;

    String reprintBy;

    Integer reprintById;

    Date lastReprintDate;

    private PartnerPlanCommissionDetail partnerPlanCommissionDetail;

    private OperatorAddress operatorAddress;

    private ChildPartnerAddress childPartnerAddress;

    @XmlTransient
    Integer inventoryMappingId;

    @XmlTransient
    private String custStatus;

    @XmlTransient
    public Integer paymentOwnerId;

    @XmlTransient
    LocalDate promiseStartDate;

    @XmlTransient
    LocalDate promiseEndDate;

    @XmlTransient
    Integer promiseToPayHoldDays;

    @XmlTransient
    Boolean isContainsPromiseToPay;

    @XmlTransient
    String remark;

    List<ServiceQosPojo> serviceQosPojos;

    List<ServiceTotalAmount> serviceTotalAmounts;

    String fullName;

    List<ChargeDetails> chargeDetails;

    String pan;

    String verifyQr;

    String kraInvoiceId;

    String curRecptNo;

    String totRecptNo;

    String scuInternalData;

    String scuReceiptSignature;

    String sdcid;

    String sdcmrcNo;

    String sdcDateTime;

    String isStockIO;

    @XmlTransient
    Date gracePeriodEndDate;

    double previousWalletBalance;

    @XmlTransient
    Date customerActivationDate;

    Integer planValidityInDays;

    Integer billingCycleInMonths;

    String subscriptionPlanType;

    double dueAmountFromLastInvoice;

    String customerCurrency;

    public Boolean getIsContainsPromiseToPay() {
        return isContainsPromiseToPay;
    }

    public void setIsContainsPromiseToPay(Boolean containsPromiseToPay) {
        isContainsPromiseToPay = containsPromiseToPay;
    }

    public Integer getPromiseToPayHoldDays() {
        return promiseToPayHoldDays;
    }

    public void setPromiseToPayHoldDays(Integer promiseToPayHoldDays) {
        this.promiseToPayHoldDays = promiseToPayHoldDays;
    }

    public LocalDate getPromiseStartDate() {
        return promiseStartDate;
    }

    public void setPromiseStartDate(LocalDate promiseStartDate) {
        this.promiseStartDate = promiseStartDate;
    }

    public LocalDate getPromiseEndDate() {
        return promiseEndDate;
    }

    public void setPromiseEndDate(LocalDate promiseEndDate) {
        this.promiseEndDate = promiseEndDate;
    }

    public String getPaymentOwner() {
        return paymentOwner;
    }

    public void setPaymentOwner(String paymentOwner) {
        this.paymentOwner = paymentOwner;
    }

    public String getCreatebyname() {
        return createbyname;
    }

    public void setCreatebyname(String createbyname) {
        this.createbyname = createbyname;
    }

    public String getUpdatebyname() {
        return updatebyname;
    }

    public void setUpdatebyname(String updatebyname) {
        this.updatebyname = updatebyname;
    }

    public Integer getPrintCounter() {
        return printCounter;
    }

    public void setPrintCounter(Integer printCounter) {
        this.printCounter = printCounter;
    }

    public String getCustRefName() {
        return custRefName;
    }

    public void setCustRefName(String custRefName) {
        this.custRefName = custRefName;
    }

    public Long getQty() {
        return qty;
    }

    public List<Long> getCstchargeid() {
        return cstchargeid;
    }

    public void setCstchargeid(List<Long> cstchargeid) {
        this.cstchargeid = cstchargeid;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public String getChildCustomerIdentity() {
        return childCustomerIdentity;
    }

    public void setChildCustomerIdentity(String childCustomerIdentity) {
        this.childCustomerIdentity = childCustomerIdentity;
    }

    public Invoice() {

    }

    /**
     * @return the id
     */
    public long getId() {

        return id;
    }

    /**
     * @param id the id to set
     */
    @XmlElement(nillable = true)
    public void setId(long id) {

        this.id = id;
    }

    /**
     * @return the number
     */
    public String getNumber() {

        return number;
    }

    /**
     * @param number the number to set
     */
    @XmlElement(nillable = true)
    public void setNumber(String number) {

        this.number = number;
    }

    /**
     * @return the customerId
     */
    public String getCustomerId() {

        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    @XmlElement(nillable = true)
    public void setCustomerId(String customerId) {

        this.customerId = customerId;
    }

    /**
     * @return the billDate
     */
    public Date getBillDate() {

        return billDate;
    }

    /**
     * @param billDate the billDate to set
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setBillDate(Date billDate) {

        this.billDate = billDate;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {

        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setCreateDate(Date createDate) {

        this.createDate = createDate;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {

        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setStartDate(Date startDate) {

        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {

        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setEndDate(Date endDate) {

        this.endDate = endDate;
    }


    public Integer getInventoryMappingId() {
        return inventoryMappingId;
    }

    @XmlElement(nillable = true)
    public void setInventoryMappingId(Integer inventoryMappingId) {
        this.inventoryMappingId = inventoryMappingId;
    }


    /**
     * @return the dueDate
     */
    public Date getDueDate() {

        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDueDate(Date dueDate) {

        this.dueDate = dueDate;
    }

    /**
     * @return the latePaymentDate
     */
    public Date getLatePaymentDate() {

        return latePaymentDate;
    }

    /**
     * @param latePaymentDate the latePaymentDate to set
     */
    @XmlElement(nillable = true)
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setLatePaymentDate(Date latePaymentDate) {

        this.latePaymentDate = latePaymentDate;
    }

    /**
     * @return the charge
     */
    public double getCharge() {

        return charge;
    }

    /**
     * @param charge the charge to set
     */
    @XmlElement(nillable = true)
    public void setCharge(double charge) {

        this.charge = charge;
    }

    /**
     * @return the tax
     */
    public double getTax() {

        return tax;
    }

    /**
     * @param tax the tax to set
     */
    @XmlElement(nillable = true)
    public void setTax(double tax) {

        this.tax = tax;
    }

    /**
     * @return the discount
     */
    public double getDiscount() {

        return discount;
    }

    /**
     * @param discount the discount to set
     */
    @XmlElement(nillable = true)
    public void setDiscount(double discount) {

        this.discount = discount;
    }

    /**
     * @return the usage
     */
    public double getUsage() {

        return usage;
    }

    /**
     * @param usage the usage to set
     */
    @XmlElement(nillable = true)
    public void setUsage(double usage) {

        this.usage = usage;
    }

    /**
     * @return the total
     */
    public double getTotal() {

        return total;
    }

    /**
     * @param total the total to set
     */
    @XmlElement(nillable = true)
    public void setTotal(double total) {

        this.total = total;
    }

    /**
     * @return the previousBalance
     */
    public double getPreviousBalance() {

        return previousBalance;
    }

    /**
     * @param previousBalance the previousBalance to set
     */
    @XmlElement(nillable = true)
    public void setPreviousBalance(double previousBalance) {

        this.previousBalance = previousBalance;
    }

    /**
     * @return the latePaymentFee
     */
    public double getLatePaymentFee() {

        return latePaymentFee;
    }

    /**
     * @param latePaymentFee the latePaymentFee to set
     */
    @XmlElement(nillable = true)
    public void setLatePaymentFee(double latePaymentFee) {

        this.latePaymentFee = latePaymentFee;
    }

    /**
     * @return the currentPayment
     */
    public double getCurrentPayment() {

        return currentPayment;
    }

    /**
     * @param currentPayment the currentPayment to set
     */
    @XmlElement(nillable = true)
    public void setCurrentPayment(double currentPayment) {

        this.currentPayment = currentPayment;
    }

    /**
     * @return the currentDebit
     */
    public double getCurrentDebit() {

        return currentDebit;
    }

    /**
     * @param currentDebit the currentDebit to set
     */
    @XmlElement(nillable = true)
    public void setCurrentDebit(double currentDebit) {

        this.currentDebit = currentDebit;
    }

    /**
     * @return the currentCredit
     */
    public double getCurrentCredit() {

        return currentCredit;
    }

    /**
     * @param currentCredit the currentCredit to set
     */
    @XmlElement(nillable = true)
    public void setCurrentCredit(double currentCredit) {

        this.currentCredit = currentCredit;
    }

    /**
     * @return the totalDue
     */
    public double getTotalDue() {

        return totalDue;
    }

    /**
     * @param totalDue the totalDue to set
     */
    @XmlElement(nillable = true)
    public void setTotalDue(double totalDue) {

        this.totalDue = totalDue;
    }

    /**
     * @return the totalAmountInWords
     */
    public String getTotalAmountInWords() {

        return totalAmountInWords;
    }

    /**
     * @param totalAmountInWords the totalAmountInWords to set
     */
    @XmlElement(nillable = true)
    public void setTotalAmountInWords(String totalAmountInWords) {

        this.totalAmountInWords = totalAmountInWords;
    }

    /**
     * @return the totalDueInWords
     */
    public String getTotalDueInWords() {

        return totalDueInWords;
    }

    /**
     * @param totalDueInWords the totalDueInWords to set
     */
    @XmlElement(nillable = true)
    public void setTotalDueInWords(String totalDueInWords) {

        this.totalDueInWords = totalDueInWords;
    }

    /**
     * @return the customerInformation
     */
    public Subscriber getCustomerInformation() {

        return customerInformation;
    }

    /**
     * @param customerInformation the customerInformation to set
     */
    @XmlElement(nillable = true)
    public void setCustomerInformation(Subscriber customerInformation) {

        this.customerInformation = customerInformation;
    }

    /**
     * @return the planInformation
     */
    public ArrayList<PlanInformation> getPlanInformation() {

        return planInformation;
    }

    /**
     * @param planInformation the planInformation to set
     */
    @XmlElement(nillable = true)
    public void setPlanInformation(ArrayList<PlanInformation> planInformation) {

        this.planInformation = planInformation;
    }

    /**
     * @return the taxList
     */
    @XmlElementWrapper(name = "taxInforamtion")
    public ArrayList<TaxDto> getTaxList() {
        return taxList;
    }

    /**
     * @param taxList the taxList to set
     */
    @XmlElement(nillable = true)
    public void setTaxList(ArrayList<TaxDto> taxList) {
        this.taxList = taxList;
    }

//    /**
//     * @return the discountList
//     */
//    @XmlElementWrapper(name = "discountInformation")
//    public ArrayList<Discount> getDiscountList() {
//        return discountList;
//    }
//
//    /**
//     * @param discountList the discountList to set
//     */
//    @XmlElement(nillable = true)
//    public void setDiscountList(ArrayList<Discount> discountList) {
//        this.discountList = discountList;
//    }

    /**
     * @return the invoiceList
     */
    @XmlElementWrapper(name = "invoiceDetail")
    public ArrayList<InvoiceDetail> getInvoiceList() {
        return invoiceList;
    }

    /**
     * @param invoiceList the invoiceList to set
     */
    @XmlElement(nillable = true)
    public void setInvoiceList(ArrayList<InvoiceDetail> invoiceList) {
        this.invoiceList = invoiceList;
    }

    /**
     * @return the addressDetail
     */
    public ArrayList<SubscriberAddress> getAddressDetail() {
        return addressDetail;
    }

    /**
     * @param addressDetail the addressDetail to set
     */
    @XmlElement(nillable = true)
    public void setAddressDetail(ArrayList<SubscriberAddress> addressDetail) {
        this.addressDetail = addressDetail;
    }

    /**
     * @return the billrunid
     */
    public int getBillrunid() {

        return billrunid;
    }

    /**
     * @param billrunid the billrunid to set
     */
    @XmlElement(nillable = true)
    public void setBillrunid(int billrunid) {

        this.billrunid = billrunid;
    }

    /**
     * @return the billrunStatus
     */
    public String getBillrunStatus() {
        return billrunStatus;
    }

    /**
     * @param billrunStatus the billrunStatus to set
     */
    @XmlElement(nillable = true)
    public void setBillrunStatus(String billrunStatus) {
        this.billrunStatus = billrunStatus;
    }


    public String getLaststatuschange() {
        return laststatuschange;
    }

    /**
     * @param laststatuschange the laststatuschange to set
     */
    @XmlElement(nillable = true)
    public void setLaststatuschange(String laststatuschange) {
        this.laststatuschange = laststatuschange;
    }

    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getFirstusage() {
        return firstusage;
    }

    public void setFirstusage(String firstusage) {
        this.firstusage = firstusage;
    }


    public long getPlanid() {
        return planid;
    }

    public void setPlanid(long planid) {
        this.planid = planid;
    }


    public static double getSerialversionuid() {
        return serialVersionUID;
    }


    public List<Long> getCustpackrelid() {
        return custpackrelid;
    }

    public void setCustpackrelid(List<Long> custpackrelid) {
        this.custpackrelid = custpackrelid;
    }


    public Double getTotalCustomerDiscount() {
        return totalCustomerDiscount;
    }

    public void setTotalCustomerDiscount(Double totalCustomerDiscount) {
        this.totalCustomerDiscount = totalCustomerDiscount;
    }


    public Long getBUID() {
        return BUID;
    }

    public void setBUID(Long bUID) {
        BUID = bUID;
    }

    public Double getTotalCustDirectChargeAmount() {
        return totalCustDirectChargeAmount;
    }

    public void setTotalCustDirectChargeAmount(Double totalCustDirectChargeAmount) {
        this.totalCustDirectChargeAmount = totalCustDirectChargeAmount;
    }

    public String getLocalbilldate() {
        return localbilldate;
    }

    public void setLocalbilldate(String localbilldate) {
        this.localbilldate = localbilldate;
    }

    public String getLocalstartdate() {
        return localstartdate;
    }

    public void setLocalstartdate(String localstartdate) {
        this.localstartdate = localstartdate;
    }

    public String getLocalenddate() {
        return localenddate;
    }

    public void setLocalenddate(String localenddate) {
        this.localenddate = localenddate;
    }

//    public List<Inventory> getInventoryDetail() {
//        return inventoryDetail;
//    }
//
//    @XmlElement(nillable = true)
//    public void setInventoryDetail(List<Inventory> inventoryDetail) {
//        this.inventoryDetail = inventoryDetail;
//    }

    public String getCustStatus() {
        return custStatus;
    }

    @XmlElement(nillable = true)
    public void setCustStatus(String custStatus) {
        this.custStatus = custStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getPaymentOwnerId() {
        return paymentOwnerId;
    }

    public void setPaymentOwnerId(Integer paymentOwnerId) {
        this.paymentOwnerId = paymentOwnerId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getReprintBy() {
        return reprintBy;
    }

    public void setReprintBy(String reprintBy) {
        this.reprintBy = reprintBy;
    }

    public Integer getReprintById() {
        return reprintById;
    }

    public void setReprintById(Integer reprintById) {
        this.reprintById = reprintById;
    }


    public Date getLastReprintDate() {
        return lastReprintDate;
    }

    public void setLastReprintDate(Date lastReprintDate) {
        this.lastReprintDate = lastReprintDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public List<ServiceQosPojo> getServiceQosPojos() {
        return serviceQosPojos;
    }

    @XmlElement(nillable = true)
    public void setServiceQosPojos(List<ServiceQosPojo> serviceQosPojos) {
        this.serviceQosPojos = serviceQosPojos;
    }


    public List<ServiceTotalAmount> getServiceTotalAmounts() {
        return serviceTotalAmounts;
    }

    @XmlElement(nillable = true)
    public void setServiceTotalAmounts(List<ServiceTotalAmount> serviceTotalAmounts) {
        this.serviceTotalAmounts = serviceTotalAmounts;
    }


    public String getFullName() {
        return fullName;
    }

    @XmlElement(nillable = true)
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<ChargeDetails> getChargeDetails() {
        return chargeDetails;
    }

    @XmlElement(nillable = true)
    public void setChargeDetails(List<ChargeDetails> chargeDetails) {
        this.chargeDetails = chargeDetails;
    }

    public double getPartnerTax() {
        return partnerTax;
    }
    public void setPartnerTax(double partnerTax) {
        this.partnerTax = partnerTax;
    }
    public double getTds() {
        return tds;
    }
    public void setTds(double tds) {
        this.tds = tds;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public PartnerPlanCommissionDetail getPartnerPlanCommissionDetail() {
        return partnerPlanCommissionDetail;
    }

    public void setPartnerPlanCommissionDetail(PartnerPlanCommissionDetail partnerPlanCommissionDetail) {
        this.partnerPlanCommissionDetail = partnerPlanCommissionDetail;
    }

    public OperatorAddress getOperatorAddress() {
        return operatorAddress;
    }

    public void setOperatorAddress(OperatorAddress operatorAddress) {
        this.operatorAddress = operatorAddress;
    }

    public ChildPartnerAddress getChildPartnerAddress() {
        return childPartnerAddress;
    }

    public void setChildPartnerAddress(ChildPartnerAddress childPartnerAddress) {
        this.childPartnerAddress = childPartnerAddress;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getVerifyQr() {
        return verifyQr;
    }

    public void setVerifyQr(String verifyQr) {
        this.verifyQr = verifyQr;
    }

    public String getKraInvoiceId() {
        return kraInvoiceId;
    }

    public void setKraInvoiceId(String kraInvoiceId) {
        this.kraInvoiceId = kraInvoiceId;
    }

    public String getCurRecptNo() {
        return curRecptNo;
    }

    public void setCurRecptNo(String curRecptNo) {
        this.curRecptNo = curRecptNo;
    }

    public String getTotRecptNo() {
        return totRecptNo;
    }

    public void setTotRecptNo(String totRecptNo) {
        this.totRecptNo = totRecptNo;
    }

    public String getScuInternalData() {
        return scuInternalData;
    }

    public void setScuInternalData(String scuInternalData) {
        this.scuInternalData = scuInternalData;
    }

    public String getScuReceiptSignature() {
        return scuReceiptSignature;
    }

    public void setScuReceiptSignature(String scuReceiptSignature) {
        this.scuReceiptSignature = scuReceiptSignature;
    }

    public String getSdcid() {
        return sdcid;
    }

    public void setSdcid(String sdcid) {
        this.sdcid = sdcid;
    }

    public String getSdcmrcNo() {
        return sdcmrcNo;
    }

    public void setSdcmrcNo(String sdcmrcNo) {
        this.sdcmrcNo = sdcmrcNo;
    }

    public String getSdcDateTime() {
        return sdcDateTime;
    }

    public void setSdcDateTime(String sdcDateTime) {
        this.sdcDateTime = sdcDateTime;
    }

    public String getIsStockIO() {
        return isStockIO;
    }

    public void setIsStockIO(String stockIO) {
        isStockIO = stockIO;
    }

    public Date getGracePeriodEndDate() {
        return gracePeriodEndDate;
    }

    public void setGracePeriodEndDate(Date gracePeriodEndDate) {
        this.gracePeriodEndDate = gracePeriodEndDate;
    }

    public double getPreviousWalletBalance() {
        return previousWalletBalance;
    }

    public void setPreviousWalletBalance(double previousWalletBalance) {
        this.previousWalletBalance = previousWalletBalance;
    }

    public Date getCustomerActivationDate() {
        return customerActivationDate;
    }

    public void setCustomerActivationDate(Date customerActivationDate) {
        this.customerActivationDate = customerActivationDate;
    }

    public Integer getPlanValidityInDays() {
        return planValidityInDays;
    }

    public void setPlanValidityInDays(Integer planValidityInDays) {
        this.planValidityInDays = planValidityInDays;
    }

    public Integer getBillingCycleInMonths() {
        return billingCycleInMonths;
    }

    public void setBillingCycleInMonths(Integer billingCycleInMonths) {
        this.billingCycleInMonths = billingCycleInMonths;
    }


    public String getSubscriptionPlanType() {
        return subscriptionPlanType;
    }

    public void setSubscriptionPlanType(String subscriptionPlanType) {
        this.subscriptionPlanType = subscriptionPlanType;
    }

    public double getDueAmountFromLastInvoice() {
        return dueAmountFromLastInvoice;
    }

    public void setDueAmountFromLastInvoice(double dueAmountFromLastInvoice) {
        this.dueAmountFromLastInvoice = dueAmountFromLastInvoice;
    }

    public String getCustomerCurrency() {
        return customerCurrency;
    }

    public void setCustomerCurrency(String customerCurrency) {
        this.customerCurrency = customerCurrency;
    }

    @Override
    public String toString() {
        return "Invoice [id=" + id + ", number=" + number + ", customerId=" + customerId + ", billDate=" + billDate
                + ", createDate=" + createDate + ", startDate=" + startDate + ", endDate=" + endDate + ", dueDate="
                + dueDate + ", latePaymentDate=" + latePaymentDate + ", charge=" + charge + ", tax=" + tax
                + ", discount=" + discount + ", usage=" + usage + ", total=" + total + ", previousBalance="
                + previousBalance + ", latePaymentFee=" + latePaymentFee + ", currentPayment=" + currentPayment
                + ", currentDebit=" + currentDebit + ", currentCredit=" + currentCredit + ", totalDue=" + totalDue
                + ", totalAmountInWords=" + totalAmountInWords + ", totalDueInWords=" + totalDueInWords
                + ", customerInformation=" + customerInformation + ", planInformation=" + planInformation + ", taxList="
                + taxList + ", discountList="  + ", invoiceList=" + invoiceList + ", addressDetail="
                + addressDetail + ", billrunid=" + billrunid + ", billrunStatus=" + billrunStatus
                + ", laststatuschange=" + laststatuschange + ", email=" + email + ", phone=" + phone + ", firstusage="
                + firstusage + ", planid=" + planid + ", cstchargeid=" + cstchargeid + ", custpackrelid="
                + custpackrelid + ",childCustomerIdentity=" + childCustomerIdentity + ",printCounter=" + printCounter + ", inventoryMappingId=" +inventoryMappingId + ",reprintBy=" + reprintBy + ",reprintById=" +reprintById+ ",lastReprintDate=" +lastReprintDate+ "]";
    }

}
