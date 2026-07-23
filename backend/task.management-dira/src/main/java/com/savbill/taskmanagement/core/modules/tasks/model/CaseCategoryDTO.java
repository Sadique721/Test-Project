package com.savbill.taskmanagement.core.modules.tasks.model;

import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategoryTatMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCategoryDTO extends Auditable implements IBaseDto {

    private Long categoryId;


    private String categoryName;


    private Integer mvnoId;


    private Long buId;

    private String status;


    List<CaseCategoryTatMapping> caseCategoryTatMappingList;


    private Boolean isDeleted;

    private Boolean isDefaultCaseCategory;

    private Long lcoId;

    @Override
    public Long getIdentityKey() {
        return categoryId.longValue();
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    @Override
    public Long getBuId() {
        return buId;
    }
    public void setBuId(Long buId) {
        if(buId!=null)
            this.buId= buId;
    }
}
