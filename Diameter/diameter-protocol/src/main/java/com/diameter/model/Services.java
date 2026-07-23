package com.diameter.model;

import com.diameter.service.IBaseData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tblmservices")
public class Services
//        extends Auditable
        implements IBaseData {

    @Id
    @Column(name = "serviceid")
    private Long id;

    @Column(name = "servicename")
    private String serviceName;

    @DiffIgnore
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
