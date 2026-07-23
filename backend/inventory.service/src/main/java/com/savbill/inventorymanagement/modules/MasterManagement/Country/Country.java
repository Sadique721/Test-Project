package com.savbill.inventorymanagement.modules.MasterManagement.Country;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.ToString;


import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblmcountry")
@EntityListeners(AuditableListener.class)
public class Country extends Auditable implements IBaseData<Long> {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "countryid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

////    @DiffIgnore
//    @JsonManagedReference
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "country")
//    private List<State> stateList = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    public Country(CountryPojo pojo, Integer id) {
        this.id=pojo.getId();
        this.name=pojo.getName();
        this.isDelete=pojo.getIsDelete();
        this.status=pojo.getStatus();

    }

    public Country() {
    }
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    public Country(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getPrimaryKey() {
        return Long.valueOf(id);
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
