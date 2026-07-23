package com.savbill.cpm.modules.InventoryManagement.RequestInventory;

import com.savbill.cpm.core.data.IBaseData;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EntityListeners(AuditableListener.class)
@Table(name = "tblmrequestinventory")
@AllArgsConstructor
@NoArgsConstructor
public class RequestInventory extends Auditable implements IBaseData{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_inventory_name")
    private String requestInventoryName;

    @Column(name="onbehalfof")
    private String onBehalfOf;

    @Column(name="request_name_id")
    private Long requestNameId;

    @Column(name="request_to_warehouse_id")
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
