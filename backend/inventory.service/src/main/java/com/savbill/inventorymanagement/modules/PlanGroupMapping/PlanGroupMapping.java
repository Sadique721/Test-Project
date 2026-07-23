package com.savbill.inventorymanagement.modules.PlanGroupMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroup;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltplangroupmapping")
public class PlanGroupMapping extends Auditable {
	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plangroupmappingid")
    private Integer planGroupMappingId;
	
	@ManyToOne
    @JoinColumn(name = "postpaidplanid", referencedColumnName = "postpaidplanid")
    private PostpaidPlan plan;
	
	@Column(nullable = false, length = 40)
    private String service;

	@JsonBackReference
	@ManyToOne
    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid")
    private PlanGroup planGroup;
	
	@Column(name = "is_deleted")
    private Boolean isDelete = false;;
	
	@Column(name= "MVNOID")
	private Integer mvnoId;


}
