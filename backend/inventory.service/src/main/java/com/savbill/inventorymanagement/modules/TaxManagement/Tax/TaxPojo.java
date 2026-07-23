package com.savbill.inventorymanagement.modules.TaxManagement.Tax;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.TaxManagement.TaxSlab.TaxTypeSlabPojo;
import com.savbill.inventorymanagement.modules.TaxManagement.TaxTier.TaxTypeTierPojo;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaxPojo extends Auditable implements IBaseDto {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    @Length(max = 150, message = "The field must be less than 150 characters")
    private String desc;

    @NotNull
    private String taxtype;
    @NotNull
    private String status;
    private Boolean isDelete = false;

    private List<TaxTypeTierPojo> tieredList = new ArrayList<>();

    private List<TaxTypeSlabPojo> slabList = new ArrayList<>();
    
    private Integer mvnoId;

    private Long buId;


    @Override
    public String toString() {
        return "TaxPojo [id=" + id + ", name=" + name + ", desc=" + desc + ", taxType=" + taxtype + ", status=" + status
                + ", tieredList=" + tieredList + ", slabList=" + slabList + "]";
    }

    @Override
    public Long getIdentityKey() {
        return Long.valueOf(id);
    }
}
