package com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.Region.domain.Region;
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
@Table(name = "tblmbusinessverticals")
@EntityListeners(AuditableListener.class)
public class BusinessVerticals extends Auditable implements IBaseData<Long> {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bu_verticals_id")
    private Long id;

    private String vname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbltbusinessverticalsmapping", joinColumns = {@JoinColumn(name = "buverticalsid")}, inverseJoinColumns = {@JoinColumn(name = "region_id")})
    private List<Region> buregionidList = new ArrayList<>();

    private String status;

   @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

   @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
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
    public BusinessVerticals (BusinessVerticals businessVerticals){
        this.id = businessVerticals.getId();
        this.vname = businessVerticals.getVname();
                List<Region> regions = new ArrayList<>();
        for(Region region:businessVerticals.getBuregionidList()){
            Region region1=new Region(region);
           regions.add(region1);
        }
        this.buregionidList =regions;

        this.status = businessVerticals.getStatus();
        this.isDeleted = businessVerticals.getIsDeleted();
        this.mvnoId = businessVerticals.getMvnoId();
    }
}

