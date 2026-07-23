package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;


import lombok.Data;

@Data
public class SaveTaxSharedDataMessage {
    private Integer id;
    private String name;
    private String desc;
    private String taxtype;
    private String status;
    private Integer mvnoId;
    private Long buId;
//    private List<TaxTypeTier> tieredList;
//    private List<TaxTypeSlab> slabList;
    private Boolean isDelete;
    private Integer createdById;
    private Integer lastModifiedById;
}
