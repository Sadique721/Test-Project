package com.savbill.salescrmsbss.helper;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LeadQuotationWfDTO {

    private Long quotationId;
    private Long leadMasterId;
    private String status;
    private Long mvnoId;
    private Long buId;
    private Integer nextApproveStaffId;
    private Integer nextTeamMappingId;
    private String flag;
    private String remark;
    private Integer currentLoggedInStaffId;
    private String teamName;
    private Boolean finalApproved;
    private Boolean approveRequest;
    private Long rejectedReasonMasterId;
    private String remarkType;

}
