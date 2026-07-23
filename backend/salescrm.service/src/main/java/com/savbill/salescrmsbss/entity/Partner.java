package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.SavePartnerSharedDataMessage;
import com.savbill.salescrmsbss.rabbitMq.message.UpdatePartnerSharedDataMessage;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblpartners")
public class Partner {

	@Id
	@Column(name = "PARTNERID", nullable = false, length = 40)
	private Integer id;

	@Column(name = "PARTNERNAME", nullable = false, length = 40)
	private String name;

	@Column(name = "STATUS", nullable = false, length = 40)
	private String status;

	@Column(name = "COMM_TYPE", nullable = false, length = 40)
	private String commtype;

	@Column(name = "COMM_REL_VALUE", length = 40)
	private Double commrelvalue;

	@Column(name = "balance", length = 40)
	private Double balance;

	@Column(name = "COMM_DUE_DAY", length = 40)
	private Integer commdueday;

	@Column(name = "NEXTBILLDATE", nullable = false, length = 40)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate nextbilldate;

	@Column(name = "LASTBILLDATE", nullable = false, length = 40)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate lastbilldate;

	@Column(name = "taxid", nullable = false, length = 40)
	private Integer taxid;

	@Column(name = "addresstype", nullable = false, length = 40)
	private String addresstype;

	@Column(name = "address1", nullable = false, length = 40)
	private String address1;

	@Column(name = "address2", nullable = false, length = 40)
	private String address2;

	@Column(name = "city", nullable = false, length = 40)
	private Integer city;

	@Column(name = "state", nullable = false, length = 40)
	private Integer state;

	@Column(name = "country", nullable = false, length = 40)
	private Integer country;

	@Column(name = "pincode", nullable = false, length = 40)
	private String pincode;

	@Column(name = "mobile", nullable = false, length = 40)
	private String mobile;

	private String countryCode;

	@Column(name = "email", nullable = false, length = 40)
	private String email;

	@Column(name = "parentpartnerid")
	private Integer parentPartnerId;

	@Column(name = "pricebookid")
	private Long priceBookId;

	@Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;

	@Column(name = "commission_share_type", nullable = false, length = 40)
	private String commissionShareType;

	@Column(name = "BUID", nullable = false, length = 40, updatable = false)
	private Long buId;

	@Column(name = "new_customer_count")
	private Long newCustomerCount;

	@Column(name = "renew_customer_count")
	private Long renewCustomerCount;

	@Column(name = "total_customer_count")
	private Long totalCustomerCount;

	public Partner(SavePartnerSharedDataMessage message) {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.id = message.getId();
		this.name = message.getName();
		this.status = message.getStatus();
		this.commtype = message.getCommtype();
		this.commrelvalue = message.getCommrelvalue();
		this.balance = message.getBalance();
		this.commdueday = message.getCommdueday();
//		this.nextbilldate = LocalDate.parse(message.getNextbilldate(), formatter);
//		this.lastbilldate = LocalDate.parse(message.getLastbilldate(), formatter);
		this.taxid = message.getTaxid();
		this.addresstype = message.getAddresstype();
		this.address1 = message.getAddress1();
		this.address2 = message.getAddress2();
		this.city = message.getCity();
		this.state = message.getState();
		this.country = message.getCountry();
		this.pincode = message.getPincode();
		this.mobile = message.getMobile();
		this.countryCode = message.getCountryCode();
		this.email = message.getEmail();
		if (message.getParentPartnerId() != null) {
			this.parentPartnerId = message.getParentPartnerId();
		}
	//	this.priceBookId = message.get;
		this.isDelete = message.getIsDelete();
		this.mvnoId = message.getMvnoId();
		this.commissionShareType = message.getCommissionShareType();
		this.buId = message.getBuId();
		this.newCustomerCount = message.getNewCustomerCount();
		this.renewCustomerCount = message.getRenewCustomerCount();
		this.totalCustomerCount = message.getTotalCustomerCount();
	}
	public Partner(UpdatePartnerSharedDataMessage message) {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.id = message.getId();
		this.name = message.getName();
		this.status = message.getStatus();
		this.commtype = message.getCommtype();
		this.commrelvalue = message.getCommrelvalue();
		this.balance = message.getBalance();
		this.commdueday = message.getCommdueday();
//		this.nextbilldate = LocalDate.parse(message.getNextbilldate(), formatter);
//		this.lastbilldate = LocalDate.parse(message.getLastbilldate(), formatter);
		this.taxid = message.getTaxid();
		this.addresstype = message.getAddresstype();
		this.address1 = message.getAddress1();
		this.address2 = message.getAddress2();
		this.city = message.getCity();
		this.state = message.getState();
		this.country = message.getCountry();
		this.pincode = message.getPincode();
		this.mobile = message.getMobile();
		this.countryCode = message.getCountryCode();
		this.email = message.getEmail();
		if (message.getParentPartnerId() != null) {
			this.parentPartnerId = message.getParentPartnerId();
		}
		//	this.priceBookId = message.get;
		this.isDelete = message.getIsDelete();
		this.mvnoId = message.getMvnoId();
		this.commissionShareType = message.getCommissionShareType();
		this.buId = message.getBuId();
		this.newCustomerCount = message.getNewCustomerCount();
		this.renewCustomerCount = message.getRenewCustomerCount();
		this.totalCustomerCount = message.getTotalCustomerCount();
	}
	
	public Partner(Integer id) {
		this.id = id;
	}



}
