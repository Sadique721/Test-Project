package com.savbill.salescrmsbss.helper;

import com.savbill.salescrmsbss.entity.RejectSubReason;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectSubReasonDto {

	private Long id;

	private String name;
	
	private Long rejectReasonId;
	
	private Boolean isDelete = false;
	
	public RejectSubReasonDto(RejectSubReason rejectSubReason) {
		this.id = rejectSubReason.getId();
		this.name = rejectSubReason.getName();
		this.isDelete = rejectSubReason.getIsDelete();
		if(rejectSubReason.getRejectReason() != null)
			this.rejectReasonId = rejectSubReason.getRejectReason().getId();
	}
}
