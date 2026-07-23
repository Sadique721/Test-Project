package com.savbill.inventorymanagement.modules.MasterManagement.ServiceAreaPincodeMapping;

import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.MasterManagement.City.City;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltserviceareapincoderel")
public class ServiceAreaPincodeRel implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "serviceareaid")
    private Long serviceAreaId;

    @Column(name = "pincodeid")
    private Long pincodeId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @ManyToOne(targetEntity = City.class)
    @JoinColumn(name = "cityid", referencedColumnName = "CITYID", updatable = true, insertable = true)
    private City cityData;

    @Override
    public Long getPrimaryKey() { return id; }

    @Override
    public void setDeleteFlag(boolean deleteFlag) { this.isDeleted = deleteFlag; }

    @Override
    public boolean getDeleteFlag()  {
        return isDeleted;
    }
}
