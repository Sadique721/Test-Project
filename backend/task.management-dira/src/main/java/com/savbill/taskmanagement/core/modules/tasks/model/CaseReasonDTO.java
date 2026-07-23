package com.savbill.taskmanagement.core.modules.tasks.model;

import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
public class CaseReasonDTO extends Auditable<CaseReasonDTO> implements IBaseDto {

    private Long reasonId;
    private String name;
    private String status;
    private String tatConsideration;
    private Boolean isDelete = false;
    private Integer mvnoId;
    private String timeUnit;
    private Double time;
    private Long buId;

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }

    // @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CaseReasonConfigPojo> caseReasonConfigList = new ArrayList<>();

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return reasonId;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
