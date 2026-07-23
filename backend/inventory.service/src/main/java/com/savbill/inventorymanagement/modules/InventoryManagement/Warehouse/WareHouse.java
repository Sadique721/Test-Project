package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.model.ProductWarehouseMappingDTO;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmwarehousemanagement")
@SQLDelete(sql = "UPDATE tblmwarehousemanagement SET is_deleted = true WHERE warehouse_id=?")
@Where(clause = "is_deleted=false")
@EntityListeners(AuditableListener.class)
public class WareHouse extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "address1", nullable = false)
    private String address1;
    @Column(name = "address2", nullable = false)
    private String address2;
    @Column(name = "pincode", nullable = false)
    @DiffIgnore
    private String pincode;
    @Column(name = "state", nullable = false)
    @DiffIgnore
    private String state;
    @Column(name = "city", nullable = false)
    @DiffIgnore
    private String city;
    @Column(name = "country", nullable = false)
    @DiffIgnore
    private String country;
    @Column(name = "latitude", nullable = false)
    private String latitude;
    @Column(name = "longitude", nullable = false)
    private String longitude;
    @DiffIgnore
    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "rms_warehouse_id")
    private String rmsWarehouseId;

    @DiffIgnore
    @Column(name = "nav_warehouse_id")
    private String navWarehouseId;
    @Column(name = "warehouse_code")
    private String warehouseCode;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltwarehousemanagmentservicearearel", joinColumns = {@JoinColumn(name = "warehouse_id")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    @Column (name = "warehousetype")
    private String warehouseType;

    @Column(name = "branch_id")
    private Long branchId;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltwarehousemanagmentteamsmapping", joinColumns = {@JoinColumn(name = "warehouse_id")}
            , inverseJoinColumns = {@JoinColumn(name = "team_id")})
    private List<Teams> teamsIdsList = new ArrayList<>();

    @Transient
    private List<Teams> teamsList = new ArrayList<>();

    @Transient
    @DiffIgnore
    private List<ProductWarehouseMappingDTO> productWarehouseMappingDTOS;

    public WareHouse(Long id) {
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

   public WareHouse(WareHouse wareHouse){
        this.name = wareHouse.getName();
        this.status = wareHouse.getStatus();
        this.description = wareHouse.getDescription();
        this.address1 = wareHouse.getAddress1();
        this.pincode = wareHouse.getPincode();
        this.state = wareHouse.getState();
        this.city = wareHouse.getCity();
        this.country = wareHouse.getCountry();
        this.latitude = wareHouse.getLatitude();
        this.mvnoId = wareHouse.getMvnoId();
        this.isDeleted = wareHouse.getIsDeleted();
        this.rmsWarehouseId = wareHouse.getRmsWarehouseId();
        this. navWarehouseId= wareHouse.getNavWarehouseId();
        this.serviceAreaNameList = wareHouse.getServiceAreaNameList();
        this.warehouseType = wareHouse.getWarehouseType();
        this.branchId = wareHouse.getBranchId();
        this.teamsIdsList = wareHouse.getTeamsIdsList();
        this.teamsList = wareHouse.getTeamsList();


    }

    public WareHouse(Long id, String name, String warehouseType, String address1, String address2, String status) {
        this.id = id;
        this.name = name;
        this.warehouseType = warehouseType;
        this.address1 = address1;
        this.address2 = address2;
        this.status = status;
    }

    public WareHouse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
