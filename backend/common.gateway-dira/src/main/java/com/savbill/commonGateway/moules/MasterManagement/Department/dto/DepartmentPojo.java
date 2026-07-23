package com.savbill.commonGateway.moules.MasterManagement.Department.dto;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.EntityListeners;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EntityListeners(AuditableListener.class)
public class DepartmentPojo extends Auditable {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    private Boolean isDelete = false;
    
    private Integer mvnoId;

    private Integer displayId;
    private String displayName;
    private List<Integer> planIds;

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

}
