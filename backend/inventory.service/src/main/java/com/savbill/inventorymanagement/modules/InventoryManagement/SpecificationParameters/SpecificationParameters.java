package com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name="tblmspecificationparameter")
@EntityListeners(AuditableListener.class)
public class SpecificationParameters extends Auditable implements IBaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "product_id")
    ProductCategory productCategory;

    @Column(name="param_name")
    private String paramName;

    @Column(name="is_mandatory")
    private Boolean isMandatory;

    @Column(name="MVNOID")
    private Integer mvnoId;

    @Column(name="is_multi_value")
    private Boolean isMultiValueParam;

    @Column(name="param_values")
    private String paramValues;

    @Transient
    private String newParamDefaultValue;


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
}
