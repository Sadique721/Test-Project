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
@Table(name ="tblmcasesubcategory")
@EntityListeners(AuditableListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseSubCategory extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sub_category_id",nullable = false)
    private Long subCategoryId;

    @Column(name="sub_category_name", nullable = false)
    private String subCategoryName;

    @Column(name= "discription")
    private String discription;

    @Column(name="status", nullable = false)
    private String status;

    @Column(name="is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name="mvnoid", nullable = false)
    private Integer mvnoId;

    @Column(name= "buid")
    private Long buId;

    @Column(name= "lcoid")
    private Integer lcoId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = CaseSubCategoryCategoryMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "case_sub_category_id")
    List<CaseSubCategoryCategoryMapping> caseSubCategoryCategoryMappingList;

    @Column(name="is_default_case_sub_category", nullable = false)
    private Boolean isDefaultCaseSubCategory;

    @Override
    public Long getPrimaryKey() {
        return subCategoryId;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }


}
