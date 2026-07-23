package com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EntityListeners(AuditableListener.class)
@Table(name = "tblmrequestinventory")
@AllArgsConstructor
@NoArgsConstructor
public class RequestInventory extends Auditable implements IBaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_inventory_name")
    private String requestInventoryName;

    @Column(name="onbehalfof")
    private String onBehalfOf;

    @Column(name="request_name_id")
    @DiffIgnore
    private Long requestNameId;

    @Column(name="request_to_warehouse_id")
    @DiffIgnore
    private Long requestToWarehouseId;

    @Column(name="status")
    private String status;

    @Column(name="reason")
    private String reason;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @Column(name="inventoryrequeststatus")
    private String inventoryRequestStatus;

     @Transient
    List<RequestInvenotryProductMapping> requestInvenotryProductMappings=new ArrayList<>();

     @Transient
     private String  requesterName;

     @Transient
     private String requestToName;


    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted=deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
}
