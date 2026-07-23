package com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tblmvendor")
@SQLDelete(sql = "UPDATE tblmvendor SET is_deleted = true WHERE id=?")
@EntityListeners(AuditableListener.class)
public class Vendor extends Auditable implements IBaseData<Long> {
    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Long id;
    @Column(name="name")
    private String name;

    @Column(name="status")
    private String status;
    @Column(name="is_deleted")
    private boolean isDeleted;

    @DiffIgnore
    @Column(name="mvno_id")
    private Integer mvnoId;

    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", isDeleted=" + isDeleted +
                ", mvnoId=" + mvnoId +
                '}';
    }


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


    public Vendor(Vendor vendor){
        this.id = vendor.getId();
        this.name = vendor.getName();
        this.isDeleted = vendor.getDeleteFlag();
        this.mvnoId = vendor.getMvnoId();
        this.status = vendor.getStatus();
    }
}
