package com.savbill.salescrmsbss.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.savbill.salescrmsbss.helper.RejectReasonDto;
import com.savbill.salescrmsbss.helper.RejectSubReasonDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMREJECTREASON")
public class RejectReason {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reject_reason_id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "status")
	private String status;

	@JsonManagedReference
	@DiffIgnore
	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "rejectReason")
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "rejectReason")
	private List<RejectSubReason> rejectSubReasonList = new ArrayList<>();

	@Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
	
	@Column(name = "mvno_id")
	private Long mvnoId;

	@Column(name = "bu_id")
	private Long buId;
	
	public RejectReason(RejectReasonDto rejectReasonDto,Long mvnoId,Long buId) {
		this.id = rejectReasonDto.getId();
		this.name = rejectReasonDto.getName();
		this.status = rejectReasonDto.getStatus();
		this.mvnoId = mvnoId;
		this.buId = buId;
		this.isDelete = rejectReasonDto.getIsDelete();
		if(rejectReasonDto.getRejectSubReasonDtoList() != null && rejectReasonDto.getRejectSubReasonDtoList().size() > 0) {
			List<RejectSubReason> rejectSubReasonList = new ArrayList<RejectSubReason>();
			for (RejectSubReasonDto rejectSubReasonDto : rejectReasonDto.getRejectSubReasonDtoList()) {
				rejectSubReasonList.add(new RejectSubReason(rejectSubReasonDto));
			}
			this.rejectSubReasonList = rejectSubReasonList;
		}
	}

	public RejectReason(Long id) {
		this.id = id;
	}

	public RejectReason(RejectReason rejectReason) {
		this.id = rejectReason.id;
		this.name = rejectReason.name;
		this.status = rejectReason.status;
		this.rejectSubReasonList = new ArrayList<>(rejectReason.rejectSubReasonList);
		this.isDelete = rejectReason.isDelete;
		this.mvnoId = rejectReason.mvnoId;
		this.buId = rejectReason.buId;
	}

}
