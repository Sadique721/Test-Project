package com.savbill.revenuemanagement.productmanagement.PlanService.domain;



import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tblmservices")
public class Services extends Auditable implements IBaseData {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceid")
    private Long id;
    @Column(name = "servicename")
    private String serviceName;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
