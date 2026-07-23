package com.savbill.integrationsystem.Case;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseDocDetailsDTO {
    private Long docId;
    private Integer ticketId;

    private String remark;
    private String docStatus;
    private String filename;
    private String uniquename;
    private Boolean isDelete = false;
    private Integer mvnoId;

}
