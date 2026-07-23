package com.savbill.revenuemanagement.mastermanagement.Department.domain;

import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
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
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PostpaidPlan planId;
}
