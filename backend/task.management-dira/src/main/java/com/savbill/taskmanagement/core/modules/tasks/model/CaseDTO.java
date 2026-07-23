package com.savbill.taskmanagement.core.modules.tasks.model;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseDocDetails;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseFeedbackRel;
import com.savbill.taskmanagement.core.modules.tasks.domain.TicketAssignStaffMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseDTO extends Auditable implements IBaseDto {

    private Long caseId;
    //    private Long caseReasonId;
//    private String caseReasonName;
    private String caseTitle;
    private String caseType;
    private String caseNumber;
    private String caseFor;
    private String caseOrigin;
    private String caseStatus;
    private String priority;
    private Integer customersId;
    //private Integer staffId;
    private String caseForPartner;
    private String caseForZone;
    private LocalDate nextFollowupDate;
    private LocalTime nextFollowupTime;
    private LocalDateTime caseStartedOn;
    private String caseStartedOnString;
    private LocalDateTime firstAssignedOn;
    private String firstAssignedOnString;
    private Boolean isDelete = false;
    //Staff
    private Integer currentAssigneeId;
    //Resolution
    private Integer finalResolutionId;
    //Staff
    private Integer finalResolvedById;
    //Staff
    private Integer finalClosedById;
    private LocalDateTime finalResolutionDate;
    private LocalDateTime finalClosedDate;
    @JsonManagedReference
    private List<CaseUpdateDTO> caseUpdateList = new ArrayList<>();
    private String firstRemark;
    private LiveUserServiceAreaWiseDetailsModel liveUserServiceAreaDetails;
    private String oltName;
    private String slotName;
    private String portName;
    private String serviceAreaName;
    private Long serviceAreaId;
    private String mobile;
    private String userName;
    private String currentAssigneeName;
    private String finalResolvedByName;
    private String finalClosedByName;
    private String finalResolutionName;
    //private String customerName;
    //private String staffName;
    private String finalClosedByDateString;
    private String finalResolutionDateString;
    private String createDateString;
    private String updateDateString;
    private Integer partnerid;
    private String partnerName;
    private Integer mvnoId;
    private Integer rating;
   // private String customerFeedback;
    //private Long ticketReasonCategoryId;
    private Long caseCategoryId;
    private Long caseSubCategoryId;

    //private Long reasonSubCategoryId;

    private Long groupReasonId;
    private Long tatMappingId;
    private Long buId;
    private String caseCategoryName;
    private String caseSubCategoryName;
    private String caseReason;
    private Long rootCauseReasonId;
    private String subSource;
    private String source;

    private Integer teamHierarchyMappingId;

    private List<TicketAssignStaffMapping> ticketAssignStaffMappings;

    private String department;
    //private String customerAdditionalMobileNumber;
    //private String staffAdditionalMobileNumber;
    private String email;
    //private String staffAdditionalEmail;
    //private String customerAdditionalEmail;
    private Long parentTicketId;
    private String helperName;
    private Integer lcoId;
    private String messageId;
    private String remark;


    Object file;
    private Long case_order;

    private List<CaseDocDetails> caseDocDetails;

//    private Integer lcoId;

//    List<TicketServicemapping> ticketServicemappingList;

    private String createdFrom;

    private Integer caseSlaTime;
    private String caseSlaUnit;
    private Long parentId;
    List<CaseFeedbackRel> caseFeedbackRel;
    private Boolean call_status;
    private Boolean is_closed;
    private String deacivate_reason;

    private String serialNumber;

    private String mvnoName;

    private Integer teamId;

    private String finalTaskCompletionRemark;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean isFromCalender;
    private String teamName;
    private String assigneeName;
    private String customerName;
    private Boolean is_processed= false;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return caseId;
    }

    public Integer getCurrentAssigneeId() {
        return currentAssigneeId;
    }

    public void setCurrentAssigneeId(Integer currentAssigneeId) {
        this.currentAssigneeId = currentAssigneeId;
    }

    @Override
    public Integer getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }

    public CaseDTO(Long caseId, String caseTitle, String caseType, String caseNumber, String caseFor,
                   String caseOrigin, String caseStatus, String priority, LocalDateTime startDate,
                   LocalDateTime endDate,Integer customersId,
                   Integer teamId, String teamName, String assigneeName, String customerName,Integer currentAssigneeId,Boolean isFromCalender,String firstRemark) {
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.caseType = caseType;
        this.caseNumber = caseNumber;
        this.caseFor = caseFor;
        this.caseOrigin = caseOrigin;
        this.caseStatus = caseStatus;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customersId=customersId;
        this.teamId=teamId;
        this.teamName = teamName;
        this.currentAssigneeId=currentAssigneeId;
        this.assigneeName = assigneeName;
        this.customerName = customerName;
        this.isFromCalender=isFromCalender;
        this.firstRemark=firstRemark;
    }

    public CaseDTO(Long caseId, String caseTitle, String currentAssigneeName, String caseSubCategoryName, Integer teamId, String teamName, Long caseSubCategoryId,Integer currentAssigneeId) {
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.currentAssigneeName = currentAssigneeName;
        this.caseSubCategoryName = caseSubCategoryName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.caseSubCategoryId = caseSubCategoryId;
        this.currentAssigneeId = currentAssigneeId;
    }
}
