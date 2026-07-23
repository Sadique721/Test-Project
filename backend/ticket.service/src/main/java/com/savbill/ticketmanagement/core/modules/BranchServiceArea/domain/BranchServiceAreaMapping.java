package com.savbill.ticketmanagement.core.modules.BranchServiceArea.domain;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.data.IBaseData;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tblmbranchservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class BranchServiceAreaMapping extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branchid", nullable = false, length = 40)
    private Integer branchId;

    @Column(name = "servicearea_id", nullable = false, length = 40)
    private Integer serviceareaId;
    
    @Override
    public Long getPrimaryKey() {
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

