package com.savbill.integrationsystem.Case;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseUpdateDetailsDTO {

    private Long id;
    private String operation;
    private String entitytype;
    private String oldvalue;
    private String newvalue;
    private String attachment;
    private String filename;
    private Long resolutionId;
    private String remarktype;
    @JsonBackReference
    @ToString.Exclude
    private CaseUpdateDTO caseUpdate;
    private Boolean isDeleted = false;
    private Integer mvnoId;
}
