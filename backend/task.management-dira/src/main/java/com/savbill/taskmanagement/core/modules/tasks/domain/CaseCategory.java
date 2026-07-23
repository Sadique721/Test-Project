package com.savbill.taskmanagement.core.modules.tasks.domain;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.data.IBaseData;
import com.savbill.taskmanagement.core.modules.common.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="tblmcasecategory")
@EntityListeners(AuditableListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCategory extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name",nullable = false)
    private String categoryName;

    @Column(name = "status")
    private String status;

    @Column(name="mvnoid",nullable = false)
    private Integer mvnoId;

    @Column(name="buid")
    private Long buId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = CaseCategoryTatMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "case_category_id")
    List<CaseCategoryTatMapping> caseCategoryTatMappingList;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "caseCategory", cascade = CascadeType.ALL)
//    Set<CaseSubCategory> caseSubCategories;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name="is_default_case_category", nullable = false)
    private Boolean isDefaultCaseCategory;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;


    @Override
    public Long getPrimaryKey() {
        return categoryId;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        if(buId!=null)
            this.buId = buId;
    }





}
