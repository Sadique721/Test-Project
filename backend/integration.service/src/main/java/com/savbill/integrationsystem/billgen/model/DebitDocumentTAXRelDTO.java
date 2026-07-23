package com.savbill.integrationsystem.billgen.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DebitDocumentTAXRelDTO {
    private Integer debitdoctaxid;
    private Integer debitdocumentid;
    private Integer taxid;
    private String taxname;
    private String description;
    private Double percentage;
    private Double taxlevel;
    private LocalDateTime startdate;
    private LocalDateTime enddate;
    private Double amount;
    private String taxLedgerId;
}
