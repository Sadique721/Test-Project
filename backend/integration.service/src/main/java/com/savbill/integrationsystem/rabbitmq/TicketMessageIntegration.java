package com.savbill.integrationsystem.rabbitmq;

import com.savbill.integrationsystem.Case.CaseUpdateDTO;
import com.savbill.integrationsystem.Case.TicketServicemapping;
import lombok.Data;

import java.util.*;

@Data
public class TicketMessageIntegration {


    private Long caseId;
    private String caseTitle;
    private String caseType;
    private String caseNumber;
    private String caseFor;
    private String caseOrigin;
    private String caseStatus;
    private String priority;
    private Integer customers;

    private String caseForPartner;
    private String caseForZone;
    private String nextFollowupDate;
    private String nextFollowupTime;
    private String caseStartedOn;
    private String firstAssignedOn;
    private Boolean isDelete = false;
    private Integer currentAssignee;
    private Integer finalResolution;
    private Integer finalResolvedBy;
    private Integer finalClosedBy;
    private String finalResolutionDate;
    private String finalClosedDate;
    private List<CaseUpdateDTO> caseUpdateList = new ArrayList<>();
    private String partner;
    private String firstRemark;
    private Long mobile;
    private String userName;
    private Integer mvnoId;
    private String customerName;
    private String assigneeName;
    private Integer rating;
    private String customerFeedback;
    private Long ticketReasonCategoryId;
    private String ticketReasonCategoryName;
    private Long reasonSubCategoryId;
    private String reasonSubCategoryName;
    private Long groupReasonId;
    private Long tatMappingId;
    private Long buId;
    private Long rootCauseReasonId;
    private String source;
    private String subSource;
    private Long teamHierarchyMappingId;
//    private List<TicketAssignStaffMapping> ticketAssignStaffMappings;
    private String department;
    private String customerAdditionalMobileNumber;
    private String customerAdditionalEmail;
    private Integer parentTicketId;
    private String helperName;
    private Integer lcoId;
    private List<TicketServicemapping> ticketServicemappingList;
    private Long case_order;
    private Integer caseSlaTime;
    private String caseSlaUnit;
    private String createDate;
    private String modifyDate;
    public TicketMessageIntegration() {
    }
}
