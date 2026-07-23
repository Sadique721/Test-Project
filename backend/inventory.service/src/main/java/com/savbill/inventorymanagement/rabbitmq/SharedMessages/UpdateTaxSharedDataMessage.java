package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.TaxManagement.TaxSlab.TaxTypeSlab;
import com.savbill.inventorymanagement.modules.TaxManagement.TaxTier.TaxTypeTier;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTaxSharedDataMessage {
    private Integer id;
    private String name;
    private String desc;
    private String taxtype;
    private String status;
    private Integer mvnoId;
    private Long buId;
    private List<TaxTypeTier> tieredList;
    private List<TaxTypeSlab> slabList;
    private Boolean isDelete;
    private Integer createdById;
    private Integer lastModifiedById;
}
