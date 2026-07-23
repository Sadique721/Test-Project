package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.Inward;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParameters;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name="tblminventoryspecification")
@EntityListeners(AuditableListener.class)
public class InventorySpecification extends Auditable implements IBaseData {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Long id;;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name="param_id")
    SpecificationParameters specificationParameters;

     @Column(name = "param_value")
     private String paramValue;

     @ToString.Exclude
     @ManyToOne
     @JoinColumn(name="inward_id")
     Inward inward;

     @Column(name="inven_spec_id")
     private Long invenSpecId;

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
