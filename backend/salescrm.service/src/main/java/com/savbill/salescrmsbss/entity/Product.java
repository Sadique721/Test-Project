package com.savbill.salescrmsbss.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltproduct")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "status")
	private String status;

	@Column(name = "mvno_id", updatable = false)
	private Integer mvnoId;

	@Column(name = "apig_product_id")
	private Long apigwProductId;

	@Column(name = "CREATEDATE")
	private LocalDateTime createDate;

	@Column(name = "LASTMODIFIEDDATE")
	private LocalDateTime lastModifiedDate;

	@Column(name = "createbyname")
	private String createByName;

	@Column(name = "updatebyname")
	private String upateByName;

	@Column(name = "CREATEDBYSTAFFID")
	private Long createByStaffId;

	@Column(name = "LASTMODIFIEDBYSTAFFID")
	private Long lastMoifieByStaffId;

	@Column(name = "is_deleted")
	private Integer isDeleted;

}
