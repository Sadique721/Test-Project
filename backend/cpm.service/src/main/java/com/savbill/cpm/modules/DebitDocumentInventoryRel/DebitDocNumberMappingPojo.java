package com.savbill.cpm.modules.DebitDocumentInventoryRel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitDocNumberMappingPojo {
    private  Integer debitdocId;
    private String docnumber;
    private String billRunStatus;
}
