package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.helper.RejectSubReasonDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTREJECTSUBREASON")
public class RejectSubReason {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reject_sub_reason_id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reject_reason_id")
	private RejectReason rejectReason;
	
	@Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
	
	public RejectSubReason(RejectSubReasonDto rejectSubReasonDto) {
		this.id = rejectSubReasonDto.getId();
		this.name = rejectSubReasonDto.getName();
		this.isDelete = rejectSubReasonDto.getIsDelete();
		if(rejectSubReasonDto.getRejectReasonId() != null)
			this.rejectReason = new RejectReason(rejectSubReasonDto.getRejectReasonId());
	}

	public RejectSubReason(Long id) {
		this.id = id;
	}
}
