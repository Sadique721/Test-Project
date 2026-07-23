package com.savbill.salescrmsbss.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLMQUOTATIONPODOCUMENT")
public class QuotationPODoc {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "quotation_po_doc_id")
	private Long id;
	
	@Column(name = "quotation_detail_id")
	private Long quotationDetailId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "ponumber")
	private String poNumber;
	
	@Column(name = "filename")
	private String filename;
	
	@Column(name = "uniquename")
	private String uniquename;
	
	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;
}
