package com.savbill.commonGateway.moules.MasterManagement.Branch.domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.BranchService.model.BranchServiceMappingEntity;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmbranch")
@EntityListeners(AuditableListener.class)
public class Branch extends Auditable implements IBaseData<Long> {

 	@Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branchid")
    private Long id;

    private String name;

    private String status;

    @Column(name = "branch_code",length = 40)
    private String branch_code;
//    @DiffIgnore
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tbltbranchservicearearel", joinColumns = @JoinColumn(name = "branchid"), inverseJoinColumns = @JoinColumn(name = "servicearea_id"))
	@ToString.Exclude
	@LazyCollection(LazyCollectionOption.FALSE)
    private Set<ServiceArea> serviceAreaNameList = new HashSet<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "revenue_sharing", length = 40)
    private Boolean revenue_sharing;

    @Column(name = "sharing_percentage", length = 40)
    private Double sharing_percentage;

    @Column(name = "dunning_days")
    private String dunningDays;

//    @DiffIgnore
    @OneToMany(targetEntity = BranchServiceMappingEntity.class, cascade = CascadeType.ALL , orphanRemoval = true)
    @JoinColumn(name = "branch_mapping_id")
    List<BranchServiceMappingEntity> branchServiceMappingEntityList;

    public Branch(Long id) {
        this.id = id;
    }


    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    @Override
    public void setBuId(Long buId) {

    }

    public Branch(Long id , String name , String status){
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Branch(Branch branch) {
        this.id = branch.getId();
        this.name = branch.getName();
        this.status = branch.getStatus();
        this.branch_code = branch.getBranch_code();
        Set<ServiceArea>serviceAreaList=new HashSet<>();
        for(ServiceArea area : branch.getServiceAreaNameList()){
            ServiceArea serviceArea=new ServiceArea(area);
            serviceAreaList.add(serviceArea);
        }
        this.serviceAreaNameList = serviceAreaList;
        this.isDeleted = branch.getIsDeleted();
        this.mvnoId = branch.getMvnoId();
        this.revenue_sharing = branch.revenue_sharing;
        this.sharing_percentage = branch.sharing_percentage;
        this.dunningDays = branch.dunningDays;
        this.branchServiceMappingEntityList = branch.getBranchServiceMappingEntityList();
    }

    public Branch(Long id, String name, String status, Integer mvnoId, Boolean isDeleted, String dunningDays) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.mvnoId = mvnoId;
        this.isDeleted = isDeleted;
        this.dunningDays = dunningDays;
    }
}
