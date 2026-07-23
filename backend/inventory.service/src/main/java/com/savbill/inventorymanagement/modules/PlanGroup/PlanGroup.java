package com.savbill.inventorymanagement.modules.PlanGroup;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.ProductPlanGroupMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.PlanGroupMapping.PlanGroupMapping;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "tblmplangroup")
public class PlanGroup extends Auditable implements IBaseData {
	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plangroupid")
    private Integer planGroupId;
	
	@Column(name = "plangroupname", nullable = false, length = 40)
	private String planGroupName;
	
	@Column(name = "status", nullable = false, length = 40)
	private String status;
	
	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;

	@Column(name = "plantype", nullable = false, length = 40)
    private String plantype;

	@Column(name = "planmode", nullable = false, length = 50)
	private String planMode;
	
	@Column(name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;;
	

	@OneToMany(targetEntity = PlanGroupMapping.class, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid")
	private List<PlanGroupMapping> planMappingList = new ArrayList<>();

	@Column(name = "BUID", nullable = false, length = 40, updatable = false)
	private Long buId;
	
	@Column(name = "plangrouptype", nullable = false, length = 100)
    private String planGroupType;

	@Column(name = "PLANCATEGORY", nullable = false, length = 40)
	private String category;


	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "tbltplangroupserviceareamapping", joinColumns = {@JoinColumn(name = "plangroupid")}, inverseJoinColumns = {@JoinColumn(name = "service_area_id")})
	private List<ServiceArea> servicearea = new ArrayList<>();


	@OneToMany(cascade = CascadeType.ALL,targetEntity = ProductPlanGroupMapping.class)
	@JoinColumn(name = "plan_group_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	List<ProductPlanGroupMapping> productPlanGroupMappingList;

	@Override
	public String toString() {
		return "PlanGroup [planGroupId=" + planGroupId + ", planGroupName=" + planGroupName + ", status=" + status
				+ ", mvnoId=" + mvnoId + ", servicearea=" + servicearea + ", isDelete="
				+ isDelete + ", planMappingList=" + planMappingList + ", productPlanGroupMappingList=" + productPlanGroupMappingList + "]";
	}

	@Override
	public Serializable getPrimaryKey() {
		return planGroupId;
	}

	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		this.isDelete = deleteFlag;
	}

	@Override
	public boolean getDeleteFlag() {
		return this.isDelete;
	}
}
