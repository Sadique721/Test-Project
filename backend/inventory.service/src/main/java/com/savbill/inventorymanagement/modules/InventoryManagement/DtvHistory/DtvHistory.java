package com.savbill.inventorymanagement.modules.InventoryManagement.DtvHistory;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@Table(name = "tblhdtvhistory")
@EntityListeners(AuditableListener.class)
public class DtvHistory extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customerid")
    private Long customerId;

    @Column(name="stb_serialnumber")
    private String stbSerialNumber;

    @Column(name="card_serialnumber")
    private String cardSerialNumber;

    @Column(name="event_type")
    private String evenType;

    @Transient
    private String staffName;

    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
