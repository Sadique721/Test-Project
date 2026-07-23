package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "tblmnetworkdevices")
@DynamicInsert
@EntityListeners(AuditableListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
@AllArgsConstructor
@NoArgsConstructor
public class NetworkDevices extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deviceid")
    private Long id;

    private String name;
    private String displayname;
    private String devicetype;
    private String status;
    private String latitude;
    private String longitude;

    @ToString.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "networkDevices")
    @DiffIgnore
    private List<Oltslots> oltslotsList = new ArrayList<>();

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private Integer mvnoId;

    @Column(name = "total_in_ports")
    private Integer totalInPorts;
    @Column(name = "available_in_ports")
    private Integer availableInPorts;
    @Column(name = "total_out_ports")
    private Integer totalOutPorts;
    @Column(name = "available_out_ports")
    private Integer availableOutPorts;
    @Column(name = "total_ports")
    private Integer totalPorts;

    @Column(name = "available_ports")
    private Integer availablePorts;
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
        return isDeleted;
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(targetEntity = ServiceArea.class,orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltnetworkdevicesservicearearel", joinColumns = {@JoinColumn(name = "deviceid")}
            ,inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    @DiffIgnore
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();


    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "product_id")
    @DiffIgnore
    private Product product;

    @Column(name = "inward_id")
    private Long inwardId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "customer_inventory_id")
    private Long custInventoryId;


    @Column(name = "inventory_mapping_id")
    private Long inventorymappingId;

    @Transient
    private String  productName;

    public NetworkDevices(Long id, Long custInventoryId, Long inventorymappingId, Long itemId) {
        this.id = id;
        this.custInventoryId = custInventoryId;
        this.inventorymappingId = inventorymappingId;
        this.itemId = itemId;
    }

    public NetworkDevices(Long id, String name, String displayname, String devicetype, String status, Boolean isDeleted, Integer mvnoId, Long itemId) {
        this.id = id;
        this.name = name;
        this.displayname = displayname;
        this.devicetype = devicetype;
        this.status = status;
        this.isDeleted = isDeleted;
        this.mvnoId = mvnoId;
        this.itemId = itemId;
    }
}
