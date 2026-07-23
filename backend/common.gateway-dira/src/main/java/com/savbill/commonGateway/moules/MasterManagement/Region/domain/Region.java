package com.savbill.commonGateway.moules.MasterManagement.Region.domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.Branch;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmregion")
@EntityListeners(AuditableListener.class)

public class Region extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "region_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String rname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbltregionbranchmapping", joinColumns = {@JoinColumn(name = "region_id")}, inverseJoinColumns = {@JoinColumn(name = "branchid")})
    private List<Branch> branchidList = new ArrayList<>();

    private String status;

    @Column(columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "MVNOID")
    private Integer mvnoId;

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


    public Region (Region region){
        this.id = region.getId();
        this.rname = region.getRname();
        List<Branch> branches = new ArrayList<>();
        for(Branch branch:region.getBranchidList()){
            Branch branch1=new Branch(branch);
            branches.add(branch1);
        }
        this.branchidList =branches;
        this.status = region.getStatus();
        this.isDeleted = region.getIsDeleted();
        this.mvnoId = region.getMvnoId();
    }

}
