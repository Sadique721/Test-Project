package com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmpopmanagement")
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class PopManagement extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pop_id")
    private Long id;

    @Column(name = "pop_name", nullable = false)
    private String name;

    @Column(name = "latitude", nullable = false)
    private String latitude;

    @Column(name = "longitude", nullable = false)
    private String longitude;
    @DiffIgnore
    @Column(name = "pop_code")
    private String popCode;

    @ManyToMany
    @DiffIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltpopmanagemengservicearearel", joinColumns = {@JoinColumn(name = "pop_id")}
            , inverseJoinColumns = {@JoinColumn(name = "servicearea_id")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    //private transient String serviceareaName;

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

	public PopManagement(Long id) {
		this.id = id;
	}

    public PopManagement(PopManagement popManagement){
        this.id= popManagement.getId();
        this.name= popManagement.getName();
        this.latitude= popManagement.getLatitude();
        this.longitude= popManagement.getLongitude();
        this.popCode= popManagement.getPopCode();
        this.serviceAreaNameList= popManagement.getServiceAreaNameList();
        this.status= popManagement.getStatus();
        this.isDeleted= popManagement.getIsDeleted();
        this.mvnoId= popManagement.getMvnoId();
    }

    public PopManagement(Long id,String name){
        this.id = id;
        this.name = name;
    }
}
