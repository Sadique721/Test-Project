package com.savbill.salescrmsbss.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "tbltqotationdetails")
public class QuotationDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "quotation_detail_id")
	private Long id;

	@Column(name = "lead_id")
	private Long leadId;

	@Column(name = "version_id")
	private Long versionId;

	@Column(name = "template_id")
	private Long template_id;

	@Column(name = "status")
	private String status;
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "quotationDetails")
	private List<QuotationCircuitMapping> quotationCircuitMappingList = new ArrayList<>();

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
	
	@Column(name = "validity_unit")
	private String validityUnit;
	
	@Column(name = "validity")
	private Integer validity;
	
	@Column(name = "installation_unit")
	private String installationUnit;
	
	@Column(name = "installation_validity")
	private Integer installationValidity;

	@Column(name= "BUID")
	private Long buId;

	@Column(name= "MVNOID")
	private Long mvnoId;

	@Column(name ="next_approve_staff_id")
	private Integer nextApproveStaffId;

	@Column(name = "next_team_mapping_id")
	private Integer nextTeamMappingId;

	@Column(name = "final_approved")
	private Boolean finalApproved;

	public QuotationDetails(Long id) {
		this.id = id;
	}
}