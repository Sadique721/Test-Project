package com.savbill.ticketmanagement.core.modules.ServiceParameters.domain;

import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.data.IBaseData2;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmserviceparams")
@EntityListeners(AuditableListener.class)
public class ServiceParameter extends Auditable implements IBaseData2 {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name",length = 40)
    private String name;

    @Column(name = "isdelete")
    private Boolean isdelete;

    @Column(name = "field_name")
    private String fieldName;
    @Column(name = "data_type")
    private String dataType;
    @Override
    @JsonIgnore
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    @JsonIgnore
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    @JsonIgnore
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    @JsonIgnore
    public void setBuId(Long buId) {

    }
}
