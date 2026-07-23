package com.savbill.integrationsystem.deviceveri.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.Data;

@Data
@Entity
@Table(name = "tblcustomers")
public class CustomersData{
	@Id
	@Column(name = "custid", nullable = false, length = 40)
	private Integer id;

	@Column(nullable = false, length = 40)
	private String title;

	@Column(nullable = false, length = 40)
	private String username;

	@Column(length = 40)
	private String password;

	@Column(nullable = false, length = 40)
	private String firstname;

	@Column(nullable = false, length = 40)
	private String lastname;



	@Column(name = "email" , nullable = false, unique = true)
	private String email;


	@Column(name = "mobile" ,nullable = false, length = 10)
	private String mobile;

	private String countryCode;


	@Column(name = "servicearea_id", nullable = false, length = 40)
	private Integer serviceAreaId;


	@Column(name = "status", nullable = false, length = 100)
	private String status;


	@Column(name = "customer_type", nullable = false, length = 100)
	private String custtype; //Postpaid,Prepaid



	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;



	@Column(name = "BUID", nullable = false, length = 40, updatable = false)
	private Long buId;


	@Column(name = "partner_id", nullable = false, length = 40, updatable = false)
	private Integer parnterId;

	@Column(name="blockno")
	private String blockNo;

	@Column(name = "parentcustid")
	private String parentcustid;

}
