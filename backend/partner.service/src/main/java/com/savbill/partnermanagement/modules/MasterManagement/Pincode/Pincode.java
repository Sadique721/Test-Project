package com.savbill.partnermanagement.modules.MasterManagement.Pincode;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.core.data.IBaseData;
import com.savbill.partnermanagement.modules.MasterManagement.Area.Area;
import com.savbill.partnermanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "tblmpincode")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
public class Pincode extends Auditable implements IBaseData<Long> {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pincodeid", nullable = false, length = 40)
    private Long id;

    private String pincode;
    private String status;
    private Boolean isDeleted = false;

    @Column(name = "countryid", nullable = false, length = 40)
    private Integer countryId;

    @Column(name = "cityid", nullable = false, length = 40)
    private Integer cityId;

    @Column(name = "stateid", nullable = false, length = 40)
    private Integer stateId;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pincode")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Area> areaList = new ArrayList<>();

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
    public Pincode(Pincode pincodeDto) {
        this.id = pincodeDto.getId();
        this.pincode = pincodeDto.getPincode();
        this.status = pincodeDto.getStatus();
        this.isDeleted = pincodeDto.getIsDeleted();
        this.countryId = pincodeDto.getCountryId();
        this.cityId = pincodeDto.getCityId();
        this.stateId = pincodeDto.getStateId();
        if(!CollectionUtils.isEmpty(pincodeDto.getAreaList()))
            this.areaList = pincodeDto.getAreaList().stream().map(Area::new).collect(Collectors.toList());
        this.mvnoId = mvnoId;
    }
}
