package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import com.savbill.cpm.model.postpaid.TaxTypeSlab;
import com.savbill.cpm.model.postpaid.TaxTypeTier;
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
