package com.savbill.commonGateway.moules.MasterManagement.Department.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltdepartmentplanmapping")
public class DepartmentPlanMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    @Column(name = "plan_id")
    private Integer planId;
}
