package com.savbill.partnermanagement.modules.MasterManagement.Area;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.core.data.IBaseData;
import com.savbill.partnermanagement.modules.MasterManagement.Pincode.Pincode;
import com.savbill.partnermanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmarea")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Area extends Auditable implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "areaid")
    private Long id;

    private String name;
    private String status;
    private Boolean isDeleted = false;

    @Column(name = "countryid", nullable = false, length = 40)
    private Integer countryId;

    @Column(name = "cityid", nullable = false, length = 40)
    private Integer cityId;

    @Column(name = "stateid", nullable = false, length = 40)
    private Integer stateId;

    @JsonBackReference
    @ManyToOne()
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "pincodeid")
    private Pincode pincode;
//    @Column(name = "pincodeid", nullable = false, length = 40)
//    private Integer pincodeId;

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
    public Area(Area area) {
        this.id = area.getId();
        this.name = area.getName();
        this.status = area.getStatus();
        this.isDeleted = area.getIsDeleted();
        this.countryId = area.getCountryId();
        this.cityId = area.getCityId();
        this.stateId = area.getStateId();
        this.mvnoId = area.getMvnoId();
    }
}
