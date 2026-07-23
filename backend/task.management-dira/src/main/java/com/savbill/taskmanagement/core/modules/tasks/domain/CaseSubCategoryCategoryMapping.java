package com.savbill.taskmanagement.core.modules.tasks.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltcasesubcategorycategorymapping")
public class CaseSubCategoryCategoryMapping {

    @DiffIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @DiffIgnore
    @Column(name = "case_category_id", nullable = false)
    private Long caseCategoryId;

    @DiffIgnore
    @Column(name = "case_sub_category_id", nullable = false)
    private Long caseSubCategoryId;


    public CaseSubCategoryCategoryMapping(Long caseCategoryId, Long caseSubCategoryId) {
        this.caseCategoryId = caseCategoryId;
        this.caseSubCategoryId = caseSubCategoryId;
    }
}
