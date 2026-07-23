package com.savbill.revenuemanagement.core.entity.invoice;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

public class InvoiceDetail {

	String id;

	String invoiceId;

	String itemChargeId;

	String name;

	String description;

	String type;

	String cycle;

	double price;

	double tax;

	double discount;

	double total;

	Date startDate;

	Date endDate;

	String prorationType;

	int noOfCycle;

	String customerid;

	String partenrid;

	String comm_type;

	String comm_rel_value;

	double comm_value;

	String saccode;

	double customerDiscount;

	double custDirectChargeAmount;

	long customerPackageId;


	long customerInventoryMappingId;

	String planId;

	public String getCustRefName() {
		return custRefName;
	}
	public void setCustRefName(String custRefName) {
		this.custRefName = custRefName;
	}
	String custRefName;


	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	private String createbyname;

	private String updatebyname;


	private String ledgerId;

	private String iccode;

	private Long custServiceId;

	private Long serviceId;

	public String getLedgerId() {
		return ledgerId;
	}

	public void setLedgerId(String ledgerId) {
		this.ledgerId = ledgerId;
	}

	public String getIccode() {
		return iccode;
	}

	public void setIccode(String iccode) {
		this.iccode = iccode;
	}

	public String getUpdateByName() {
		return updatebyname;
	}

	public void setUpdateByName(String updatebyname) {
		this.updatebyname = updatebyname;
	}

	public String getCreatedByName() {
		return createbyname;
	}

	public void setCreatedByname(String createbyname) {
		this.createbyname = createbyname;
	}

	public long getCustomerInventoryMappingId() {
		return customerInventoryMappingId;
	}

	public void setCustomerInventoryMappingId(long customerInventoryMappingId) {
		this.customerInventoryMappingId = customerInventoryMappingId;
	}


	public long getCustomerPackageId() {
		return customerPackageId;
	}

	public void setCustomerPackageId(long customerPackageId) {
		this.customerPackageId = customerPackageId;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getPartenrid() {
		return partenrid;
	}

	public void setPartenrid(String partenrid) {
		this.partenrid = partenrid;
	}

	public String getComm_type() {
		return comm_type;
	}

	public void setComm_type(String comm_type) {
		this.comm_type = comm_type;
	}

	public String getComm_rel_value() {
		return comm_rel_value;
	}

	public void setComm_rel_value(String comm_rel_value) {
		this.comm_rel_value = comm_rel_value;
	}

	public double getComm_value() {
		return comm_value;
	}

	public void setComm_value(double comm_value) {
		this.comm_value = comm_value;
	}

	/**
	 * @return the id
	 */
	public String getId() {

		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	@XmlElement(nillable = true)
	public void setId(String id) {

		this.id = id;
	}

	/**
	 * @return the invoiceId
	 */
	public String getInvoiceId() {

		return invoiceId;
	}

	/**
	 * @param invoiceId
	 *            the invoiceId to set
	 */
	@XmlElement(nillable = true)
	public void setInvoiceId(String invoiceId) {

		this.invoiceId = invoiceId;
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
	@XmlElement(nillable = true)
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {

		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	@XmlElement(nillable = true)
	public void setDescription(String description) {

		this.description = description;
	}

	/**
	 * @return the type
	 */
	public String getType() {

		return type;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	@XmlElement(nillable = true)
	public void setType(String type) {

		this.type = type;
	}

	/**
	 * @return the cycle
	 */
	public String getCycle() {

		return cycle;
	}

	/**
	 * @param cycle
	 *            the cycle to set
	 */
	@XmlElement(nillable = true)
	public void setCycle(String cycle) {

		this.cycle = cycle;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {

		return price;
	}

	/**
	 * @param double1
	 *            the price to set
	 */
	@XmlElement(nillable = true)
	public void setPrice(Double double1) {

		this.price = double1;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {

		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	@XmlElement(nillable = true)
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
	 * @param endDate
	 *            the endDate to set
	 */
	@XmlElement(nillable = true)
	public void setEndDate(Date endDate) {

		this.endDate = endDate;
	}

	/**
	 * @return the prorationType
	 */
	public String getProrationType() {

		return prorationType;
	}

	/**
	 * @param prorationType
	 *            the prorationType to set
	 */
	@XmlElement(nillable = true)
	public void setProrationType(String prorationType) {

		this.prorationType = prorationType;
	}

	/**
	 * @return the noOfCycle
	 */
	public int getNoOfCycle() {

		return noOfCycle;
	}

	/**
	 * @param noOfCycle
	 *            the noOfCycle to set
	 */
	@XmlElement(nillable = true)
	public void setNoOfCycle(int noOfCycle) {

		this.noOfCycle = noOfCycle;
	}

	/**
	 * @return the itemChargeId
	 */
	public String getItemChargeId() {

		return itemChargeId;
	}

	/**
	 * @param itemChargeId
	 *            the itemChargeId to set
	 */
	@XmlElement(nillable = true)
	public void setItemChargeId(String itemChargeId) {

		this.itemChargeId = itemChargeId;
	}

	/**
	 * @return the tax
	 */
	public double getTax() {

		return tax;
	}

	/**
	 * @param tax
	 *            the tax to set
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
	 * @param discount
	 *            the discount to set
	 */
	@XmlElement(nillable = true)
	public void setDiscount(double discount) {

		this.discount = discount;
	}

	/**
	 * @return the total
	 */
	public double getTotal() {

		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	@XmlElement(nillable = true)
	public void setTotal(double total) {

		this.total = total;
	}

	public double getCustomerDiscount() {
		return customerDiscount;
	}

	public void setCustomerDiscount(double customerDiscount) {
		this.customerDiscount = customerDiscount;
	}

	public String getSaccode() {
		return saccode;
	}

	public void setSaccode(String saccode) {
		this.saccode = saccode;
	}

	public double getCustDirectChargeAmount() {
		return custDirectChargeAmount;
	}

	public void setCustDirectChargeAmount(double custDirectChargeAmount) {
		this.custDirectChargeAmount = custDirectChargeAmount;
	}

	public Long getCustServiceId() {
		return custServiceId;
	}

	public void setCustServiceId(Long custServiceId) {
		this.custServiceId = custServiceId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String toString() {
		return "InvoiceDetail [id=" + id + ", invoiceId=" + invoiceId + ", itemChargeId=" + itemChargeId + ", name="
				+ name + ", description=" + description + ", type=" + type + ", cycle=" + cycle + ", price=" + price
				+ ", tax=" + tax + ", discount=" + discount + ", total=" + total + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", prorationType=" + prorationType + ", noOfCycle=" + noOfCycle
				+ ", customerid=" + customerid + ", partenrid=" + partenrid + ", comm_type=" + comm_type
				+ ", comm_rel_value=" + comm_rel_value + ", comm_value=" + comm_value + ", saccode=" + saccode + ", createbyname=" + createbyname +" , updatebyname=" + updatebyname + "]";
	}


}
