package com.savbill.salescrmsbss.helper;

public class LeadRejectDto {

	private Long leadMasterId;
	
	private Long rejectReasonId;
	
	private Long rejectSubReasonId;
	
	private String remark;

	public Long getLeadMasterId() {
		return leadMasterId;
	}

	public void setLeadMasterId(Long leadMasterId) {
		this.leadMasterId = leadMasterId;
	}

	public Long getRejectReasonId() {
		return rejectReasonId;
	}

	public void setRejectReasonId(Long rejectReasonId) {
		this.rejectReasonId = rejectReasonId;
	}

	public Long getRejectSubReasonId() {
		return rejectSubReasonId;
	}

	public void setRejectSubReasonId(Long rejectSubReasonId) {
		this.rejectSubReasonId = rejectSubReasonId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}