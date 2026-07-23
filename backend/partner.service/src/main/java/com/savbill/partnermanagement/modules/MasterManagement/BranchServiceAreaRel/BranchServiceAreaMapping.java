package com.savbill.partnermanagement.modules.MasterManagement.BranchServiceAreaRel;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.core.data.IBaseData;
import com.savbill.partnermanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbltbranchservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class BranchServiceAreaMapping extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branchid", length = 40)
    private Integer branchId;

    @Column(name = "servicearea_id", length = 40)
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

