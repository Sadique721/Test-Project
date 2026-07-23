package com.savbill.revenuemanagement.mastermanagement.Department.dto;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DepartmentPojo extends Auditable {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    private Boolean isDelete = false;
    
//    private Integer mvnoId;

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
