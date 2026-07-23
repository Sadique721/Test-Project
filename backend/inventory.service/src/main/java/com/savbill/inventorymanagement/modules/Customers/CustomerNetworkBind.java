package com.savbill.inventorymanagement.modules.Customers;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltcustomernetworkbind")
@EntityListeners(AuditableListener.class)
public class CustomerNetworkBind extends Auditable implements IBaseData<Long> {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customerid")
    private Long customerId;

    @Column(name = "popid")
    private Long popId;

    @Column(name = "oltid")
    private Long oltId;

    @Column(name = "dnsplitterid")
    private Long dnSplitterId;

    @Column(name = "snsplitterid")
    private Long snSplitterId;

    @Column(name = "masterdbid")
    private Long masterDBId;

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
