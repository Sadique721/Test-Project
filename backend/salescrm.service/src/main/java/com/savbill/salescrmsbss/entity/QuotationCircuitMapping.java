package com.savbill.salescrmsbss.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

@Entity
@Data
@Table(name = "tbltquotationcircuitmapping")
public class QuotationCircuitMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "quotation_circuit_mapping_id")
	private Long id;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quotation_details_id")
	private QuotationDetails quotationDetails;

	@Column(name = "lead_service_mapping_id")
	private Long leadServiceMappingId;
	
	@Column(name="offer_price")
	private Double offerPrice;
	
	@Column(name = "tax_amount")
	private Double taxAmount;

	@CreationTimestamp
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@UpdateTimestamp
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
	@Column(name = "modified_at")
	private LocalDateTime modifiedAt = LocalDateTime.now();

	@Column(name = "is_deleted", columnDefinition = "Boolean default false")
	private Boolean isDeleted = false;

	public QuotationCircuitMapping() {}
}
