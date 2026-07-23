package com.savbill.inventorymanagement.modules.CasMaster;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tblmcasmaster")
@EntityListeners(AuditableListener.class)
public class CasMaster extends Auditable implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    Long id;
    @Column(name = "casname")
    String casname;
    @Column(name = "status")
    String status;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    @OneToMany(targetEntity = CasParameterMapping.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "cas_param_mapping_id")
    List<CasParameterMapping> casParameterMappings;

    @Column(name="endpointurl")
    private String endpoint;

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
        return isDeleted;
    }


}