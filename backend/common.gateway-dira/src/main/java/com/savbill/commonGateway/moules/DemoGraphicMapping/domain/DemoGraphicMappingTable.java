package com.savbill.commonGateway.moules.DemoGraphicMapping.domain;

import com.savbill.commonGateway.core.data.IBaseData;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name= "tblmdemographicmapping")
public class DemoGraphicMappingTable implements IBaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 40)
    private Long id;
    @Column(name="current_name", nullable = false, length = 200)
    private String currentName;
    @Column(name="new_name", nullable = false, length = 200)
    private String newName;
    @Column(name="validation_regex", nullable = true, length = 200)
    private String validationRegex;

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    public void setBuId(Long buId) {

    }
}
