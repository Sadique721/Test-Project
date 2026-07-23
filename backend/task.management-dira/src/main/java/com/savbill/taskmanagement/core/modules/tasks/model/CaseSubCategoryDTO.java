package com.savbill.taskmanagement.core.modules.tasks.model;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseSubCategoryCategoryMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseSubCategoryDTO extends Auditable implements IBaseDto {
    

    private Long subCategoryId;
    

    private String subCategoryName;
    

    private String discription;

    private Integer mvnoId;

    private Long buId;
    private String status;

    private Boolean isDeleted;

    private List<CaseSubCategoryCategoryMapping> caseSubCategoryCategoryMappingList;
    private Boolean isDefaultCaseSubCategory;

    @Override
    public Long getIdentityKey() {
        return subCategoryId;
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
}
