package com.savbill.inventorymanagement.modules.MasterManagement.Branch;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmbranch")
@EntityListeners(AuditableListener.class)
public class Branch extends Auditable implements IBaseData<Long> {

 	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branchid")
    private Long id;

    private String name;

    private String status;

    @Column(name = "branch_code",length = 40)
    private String branch_code;
    
//    @ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "tbltbranchservicearearel", joinColumns = @JoinColumn(name = "branchid"), inverseJoinColumns = @JoinColumn(name = "servicearea_id"))
//	@ToString.Exclude
//	@LazyCollection(LazyCollectionOption.FALSE)
//    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "revenue_sharing", length = 40)
    private Boolean revenue_sharing;

    @Column(name = "sharing_percentage", length = 40)
    private Double sharing_percentage;

    @Column(name = "dunning_days")
    private String dunningDays;

//    @OneToMany(targetEntity = BranchServiceMappingEntity.class, cascade = CascadeType.ALL , orphanRemoval = true)
//    @JoinColumn(name = "branch_mapping_id")
//    List<BranchServiceMappingEntity> branchServiceMappingEntityList;


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
}
