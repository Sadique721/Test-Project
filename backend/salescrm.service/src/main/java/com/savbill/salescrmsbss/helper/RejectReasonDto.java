package com.savbill.salescrmsbss.helper;

import java.util.ArrayList;
import java.util.List;

import com.savbill.salescrmsbss.entity.RejectReason;
import com.savbill.salescrmsbss.entity.RejectSubReason;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectReasonDto {

	private Long id;

	private String name;

	private String status;

	private List<RejectSubReasonDto> rejectSubReasonDtoList;
	
	private List<Long> rejectSubReasonDeletedIds;
	
	private Boolean isDelete = false;
	
	public RejectReasonDto(RejectReason rejectReason) {
		this.id = rejectReason.getId();
		this.name = rejectReason.getName();
		this.status = rejectReason.getStatus();
		this.isDelete = rejectReason.getIsDelete();
		if(rejectReason.getRejectSubReasonList() != null && rejectReason.getRejectSubReasonList().size() > 0) {
			List<RejectSubReasonDto> rejectSubReasonDtoList = new ArrayList<RejectSubReasonDto>();
			for (RejectSubReason rejectSubReason : rejectReason.getRejectSubReasonList()) {
				if(rejectSubReason.getIsDelete() == false)
				    rejectSubReasonDtoList.add(new RejectSubReasonDto(rejectSubReason));
			}
			this.rejectSubReasonDtoList = rejectSubReasonDtoList;
		}
	}
}
