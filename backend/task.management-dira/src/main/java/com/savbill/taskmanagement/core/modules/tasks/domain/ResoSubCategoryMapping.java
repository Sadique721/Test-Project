package com.savbill.taskmanagement.core.modules.tasks.domain;

import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltresosubcategorymapping")
public class ResoSubCategoryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DiffIgnore
    private Long id;
    @Column(name="res_id")
    @DiffIgnore
    private Long resId;

    @Column(name = "case_category_id")
    private Integer caseCategoryId;


}
