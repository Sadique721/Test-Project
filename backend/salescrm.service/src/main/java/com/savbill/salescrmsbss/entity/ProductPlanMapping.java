package com.savbill.salescrmsbss.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "tbl_product_plan_mapping")
public class ProductPlanMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, length = 40)
	private Long id;

	@JoinColumn(name = "plan_id")
	@ManyToOne
	private PostpaidPlan postPaidPlan;

	@Column(name = "product_category_id", length = 40)
	private Long productCategoryId;

	@Column(name = "product_type", length = 40)
	private String product_type;

	@JoinColumn(name = "product_id")
	@ManyToOne
	private Product product;

	@Column(name = "revised_charge")
	private Double revisedCharge;

	@Column(name = "ownership_type", length = 40)
	private String ownershipType;

	@Column(name = "name", length = 50)
	private String name;

	@Column(name = "product_quantity")
	private Long quantity;

	@Column(name = "apig_product_plan_mapping_id")
	private Long apigwProductPlanMappingId;

	@CreationTimestamp
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
	@Column(name = "CREATEDATE", nullable = false, updatable = false)
	private LocalDateTime createdate;

	@UpdateTimestamp
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
	@Column(name = "LASTMODIFIEDDATE")
	private LocalDateTime updatedate;

	@Column(name = "createbyname", nullable = false, length = 40, updatable = false)
	private String createdByName;

	@Column(name = "updatebyname", nullable = false, length = 40)
	private String lastModifiedByName;

	@Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
	private Integer createdById;

	@Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
	private Integer lastModifiedById;

	@Transient
	private String productCategoryName;
	@Transient
	private String productName;
	@Transient
	private String planName;
}
