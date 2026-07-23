package com.savbill.integrationsystem.Case;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseUpdateDTO{

    private Long id;
    private Long ticketId;
    private String status;
    private String caseType;
    private Integer assignee;
    private String priority;
    private String attachment;
    private String filename;
    private Integer finalResolutionId;
    private String remarkType;
    private String remark;
    //    private Long reasonId;
    private String commentBy;

    private Boolean isDeleted = false;
    private LocalDate nextFollowupDate;
    private LocalTime nextFollowupTime;
    private String createby;
    private String updateby;
    private LocalDateTime createDateString;
    private String updateDateString;
/*    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude*/
    private Integer ticket;
    @JsonManagedReference
    private List<CaseUpdateDetailsDTO> updateDetails = new ArrayList<>();
    private Integer mvnoId;

    private Long ticketReasonCategoryId;
    private Long groupReasonId;
    private Long reasonSubCategoryId;
    private Long tatMappingId;

    private String caseTitle;

    private Long rootCauseReasonId;

    private String subSource;
    private String source;

    private Integer teamHierarchyMappingId;
    private String customerAdditionalMobileNumber;
    private String customerAdditionalEmail;

    private String helperName;
    private Long case_order;

    private Integer caseSlaTime;

    private String caseSlaUnit;

    public CaseUpdateDTO(List<CaseUpdateDTO> caseUpdateDetailsList) {

        if (caseUpdateDetailsList != null && !caseUpdateDetailsList.isEmpty()) {
            for (CaseUpdateDTO caseUpdate : caseUpdateDetailsList) {
                this.id = caseUpdate.getId();
                this.ticketId = caseUpdate.getTicketId();
                this.status = caseUpdate.getStatus();
                this.caseType = caseUpdate.getCaseType();
                this.assignee = caseUpdate.getAssignee();
                this.priority = caseUpdate.getPriority();
                this.attachment = caseUpdate.getAttachment();
                this.filename = caseUpdate.getFilename();
                this.finalResolutionId = caseUpdate.getFinalResolutionId();
                this.remarkType = caseUpdate.getRemarkType();
                this.remark = caseUpdate.getRemark();
                this.commentBy = caseUpdate.getCommentBy();
                this.isDeleted = caseUpdate.getIsDeleted();
                this.nextFollowupDate = caseUpdate.getNextFollowupDate();
                this.nextFollowupTime = caseUpdate.getNextFollowupTime();
                this.createby = caseUpdate.getCreateby();
                this.updateby = caseUpdate.getUpdateby();
                this.createDateString = caseUpdate.getCreateDateString();
                this.updateDateString = caseUpdate.getUpdateDateString();
                this.ticket = caseUpdate.getTicket();
                this.updateDetails = caseUpdate.getUpdateDetails();
                this.mvnoId = caseUpdate.getMvnoId();
                this.ticketReasonCategoryId = caseUpdate.getTicketReasonCategoryId();
                this.groupReasonId = caseUpdate.getGroupReasonId();
                this.reasonSubCategoryId = caseUpdate.getReasonSubCategoryId();
                this.tatMappingId = caseUpdate.getTatMappingId();
                this.caseTitle = caseUpdate.getCaseTitle();
                this.rootCauseReasonId = caseUpdate.getRootCauseReasonId();
                this.subSource = caseUpdate.getSubSource();
                this.source = caseUpdate.getSource();
                this.teamHierarchyMappingId = caseUpdate.getTeamHierarchyMappingId();
                this.customerAdditionalMobileNumber = caseUpdate.getCustomerAdditionalMobileNumber();
                this.customerAdditionalEmail = caseUpdate.getCustomerAdditionalEmail();
                this.helperName = caseUpdate.getHelperName();
                this.case_order = caseUpdate.getCase_order();
                this.caseSlaTime = caseUpdate.getCaseSlaTime();
                this.caseSlaUnit = caseUpdate.getCaseSlaUnit();
            }
        }
    }
}
