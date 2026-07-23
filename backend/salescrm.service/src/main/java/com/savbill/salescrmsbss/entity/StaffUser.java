package com.savbill.salescrmsbss.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblstaffuser")
public class StaffUser {

	@Id
	@Column(name = "staffid", nullable = false, length = 40)
	private Integer id;

	@Column(nullable = false, length = 40)
	private String username;

	@Column(nullable = false, length = 40)
	private String password;

	@Column(nullable = false, length = 40)
	private String firstname;

	@Column(nullable = false, length = 40)
	private String lastname;

	@Column(nullable = false, length = 40)
	private String email;

	@Column(nullable = false, length = 40)
	private String phone;

	@Column(nullable = false, length = 40)
	private Integer failcount = 0;

	@Column(name = "sstatus", nullable = false, length = 40)
	private String status;

	@Column(name = "country_code")
	private String countryCode;

	@Column(nullable = false)
	private LocalDateTime last_login_time;

	@CreationTimestamp
	@Column(name = "created_on", nullable = false, updatable = false)
	private LocalDateTime createdate;

	@UpdateTimestamp
	@Column(name = "lastmodified_on")
	private LocalDateTime updatedate;

	@Column(name = "partnerid", nullable = false, length = 40)
	private Integer partnerid;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tblstaffrolerel", joinColumns = { @JoinColumn(name = "staffid") }, inverseJoinColumns = {
			@JoinColumn(name = "roleid") })
	private Set<Role> roles = new HashSet<>();

	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "tblstaffbusinessunitrel", joinColumns = { @JoinColumn(name = "staffid") }, inverseJoinColumns = {
			@JoinColumn(name = "businessunitid") })
	private List<BusinessUnit> businessUnitNameList = new ArrayList<>();

	private String otp;

	private LocalDateTime otpvalidate;

	@Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDelete = false;

	@Column(columnDefinition = "Boolean default false", nullable = false)
	private Boolean sysstaff = false;

	@Column(name = "service_area_id")
	private Long serviceareaId;

	@Column(name = "businessunitid")
	private Long businessunitid;

	@Column(name = "parent_staff_id")
	private Integer staffUserparentId;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;


	@Column(name = "branchid", nullable = true, length = 40)
	private Integer branchId;

	@Column(name = "lcoid",  length = 40, updatable = false)
	private Integer lcoId;

	public StaffUser(UserMessage message) {
		this.id = message.getId();
		this.username = message.getUsername();
		this.password = message.getPassword();
		this.firstname = message.getFirstname();
		this.lastname = message.getLastname();
		this.email = message.getEmail();
		this.phone = message.getPhone();
		this.failcount = message.getFailcount();
		this.status = message.getStatus();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		if (message.getLast_login_time() != null)
			this.last_login_time = LocalDateTime.parse(message.getLast_login_time(), formatter);
		this.partnerid = message.getPartnerid();
		if (message.getRoles() != null && message.getRoles().size() > 0) {
			Set<Role> roles = new HashSet<Role>();
			for (RoleMessage roleMessage : message.getRoles()) {
				roles.add(new Role(roleMessage));
			}
			this.roles = roles;
		}
		if (message.getBusinessUnitMessageList() != null && message.getBusinessUnitMessageList().size() > 0) {
			List<BusinessUnit> businessUnits = new ArrayList<BusinessUnit>();
			for (BusinessUnitMessage businessUnitMessage : message.getBusinessUnitMessageList()) {
				businessUnits.add(new BusinessUnit(businessUnitMessage));
			}
			this.businessUnitNameList = businessUnits;
		}
		this.otp = message.getOtp();
		this.countryCode = message.getCountryCode();
		if (message.getOtpvalidate() != null)
			this.otpvalidate = LocalDateTime.parse(message.getOtpvalidate(), formatter);
		this.isDelete = message.getIsDelete();
		this.sysstaff = message.getSysstaff();
		this.serviceareaId = message.getServiceareaId();
		this.businessunitid = message.getBusinessunitid();
		this.staffUserparentId = message.getStaffUserparentId();
		this.mvnoId = message.getMvnoId();
		this.branchId = message.getBranchId();
	}

	public StaffUser(Integer id) {
		this.id = id;
	}

	public StaffUser(SaveStaffUserSharedDataMessage message) {
		this.id = message.getId();
		this.username = message.getUsername();
		this.password = message.getPassword();
		this.firstname = message.getFirstname();
		this.lastname = message.getLastname();
		this.email = message.getEmail();
		this.phone = message.getPhone();
		this.status = message.getStatus();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		if (message.getLast_login_time() != null && !message.getLast_login_time().equalsIgnoreCase("null") )
			this.last_login_time = LocalDateTime.parse(message.getLast_login_time(), formatter);
		this.partnerid = message.getPartnerid();
		if (message.getRoles() != null && message.getRoles().size() > 0) {
			Set<Role> roles = new HashSet<Role>();
			roles.addAll(message.getRoles());
			this.roles = roles;
		}
		if (message.getBusinessUnit() != null && Objects.nonNull(message.getBusinessUnitNameList())) {
			List<BusinessUnit> businessUnits = new ArrayList<BusinessUnit>();
			businessUnits.addAll(message.getBusinessUnitNameList());
			this.businessUnitNameList = businessUnits;
		}
//		this.otp = message.getOtp();
		this.countryCode = message.getCountryCode();
//		if (message.getOtpvalidate() != null)
//			this.otpvalidate = LocalDateTime.parse(message.getOtpvalidate(), formatter);
		this.isDelete = message.getIsDelete();
//		this.sysstaff = message.getS();
		if(message.getLcoId()!=null){
			this.lcoId= message.getLcoId();
		}
		if(message.getServicearea()!=null){
			this.serviceareaId = message.getServicearea().getId();
		}
		if(message.getBusinessUnit()!=null){
			this.businessunitid = message.getBusinessUnit().getId();
		}
		this.staffUserparentId = message.getParentStaffId();
		this.mvnoId = message.getMvnoId();
		this.branchId = message.getBranchId();

	}

	public StaffUser(UpdateStaffUserSharedDataMessage message) {
		this.id = message.getId();
		this.username = message.getUsername();
		this.password = message.getPassword();
		this.firstname = message.getFirstname();
		this.lastname = message.getLastname();
		this.email = message.getEmail();
		this.phone = message.getPhone();
		this.status = message.getStatus();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		if (message.getLast_login_time() != null && !message.getLast_login_time().equalsIgnoreCase("null") )
			this.last_login_time = LocalDateTime.parse(message.getLast_login_time(), formatter);
		this.partnerid = message.getPartnerid();
		if (message.getRoles() != null && message.getRoles().size() > 0) {
			Set<Role> roles = new HashSet<Role>();
			roles.addAll(message.getRoles());
			this.roles = roles;
		}
		if(message.getLcoId()!=null){
			this.lcoId= message.getLcoId();
		}
		this.mvnoId = message.getMvnoId();
		this.branchId = message.getBranchId();
		this.countryCode = message.getCountryCode();
		this.isDelete = message.getIsDelete();
	}
}
