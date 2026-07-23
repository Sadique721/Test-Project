package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name="tblmcustinventoryparams")
@EntityListeners(AuditableListener.class)
public class CustInvParams extends Auditable implements IBaseData {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Long id;

     @Column(name = "param_name")
     private String paramName;

     @Column(name = "param_value")
     private String paramValue;

     @DiffIgnore
	 @Column(name = "cust_id")
     private Long custId;

     @DiffIgnore
     @Column(name="cust_serv_id")
     private Long custSerMapId;

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @DiffIgnore
    @Column(name="cust_inv_id")
    private Long custInvId;

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
