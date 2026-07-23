package com.savbill.salescrmsbss.helper;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LeadMgmtWfDTO {

    private Long id;

    private String username;

    private String firstname;

    private String status;

    private Long mvnoId;

    private Long buId;

    private Integer nextApproveStaffId;

    private Integer nextTeamMappingId;

    private Long serviceareaid;
    private String leadStatus;

    private String flag;

    private String remark;

    private Integer currentLoggedInStaffId;

    private String teamName;

    private boolean finalApproved;
    private boolean isApproveRequest;

    private Long rejectedReasonMasterId;


    private String remarkType;

    private String oldValue;

    private String newValue;


    private String EntityType;


    private String operation;


    private String createDateString;


    private String updateDateString;


    private String createdBy;


    private String lastUpdatedBy;

    private Boolean isForLeadAssign;
    private String nextfollowupdate;
    private String nextfollowuptime;

    private Boolean isLeadFromCWSC;


}
