package com.savbill.ticketmanagement.core.modules.tickets.model;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.dto.IBaseDto;
import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseDocDetails;
import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseFeedbackRel;
import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketAssignStaffMapping;
import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketServicemapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String caseForPartner;
    private String caseForZone;
    private LocalDate nextFollowupDate;
    private LocalTime nextFollowupTime;
    private LocalDateTime caseStartedOn;
    private String caseStartedOnString;
    private LocalDateTime firstAssignedOn;
    private String firstAssignedOnString;
    private Boolean isDelete = false;
    private String ticketClassification;
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
    private String customerName;
    private String finalClosedByDateString;
    private String finalResolutionDateString;
    private String createDateString;
    private String updateDateString;
    private Integer partnerid;
    private String partnerName;
    private Integer mvnoId;
    private Integer rating;
    private String customerFeedback;

    private Long ticketReasonCategoryId;

    private Long reasonSubCategoryId;

    private Long groupReasonId;
    private Long tatMappingId;
    private Long buId;
    private String caseReasonCategory;
    private String caseReasonSubCategory;
    private String caseReason;
    private Long rootCauseReasonId;
    private String subSource;
    private String source;

    private Integer teamHierarchyMappingId;

    private List<TicketAssignStaffMapping> ticketAssignStaffMappings;

    private String department;
    private String customerAdditionalMobileNumber;
    private String email;
    private String customerAdditionalEmail;
    private Long parentTicketId;
    private String helperName;
    private Integer lcoId;
    private String messageId;
    private String remark;


    Object file;
    private Long case_order;

    private List<CaseDocDetails> caseDocDetails;

//    private Integer lcoId;

    List<TicketServicemapping> ticketServicemappingList;

    String createdFrom;

    private Integer caseSlaTime;
    private String caseSlaUnit;
    private Long parentId;
    List<CaseFeedbackRel> caseFeedbackRel;
    private Boolean call_status;
    private Boolean is_closed;
    private String deacivate_reason;

    private String serialNumber;

    private String mvnoName;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return caseId;
    }

    @Override
    public Integer getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }
    public CaseDTO(Long caseId, String caseTitle, String caseType, String caseNumber, String currentAssigneeName, Integer customersId, LocalTime nextFollowupTime, LocalDate nextFollowupDate, Integer currentAssigneeId, String caseStatus,LocalDateTime createdate, LocalDateTime updatedate,Integer mvnoId) {
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.caseType = caseType;
        this.caseNumber = caseNumber;
        this.currentAssigneeName = currentAssigneeName;
        this.customersId = customersId;
        this.nextFollowupTime = nextFollowupTime;
        this.nextFollowupDate = nextFollowupDate;
        this.currentAssigneeId = currentAssigneeId;
        this.caseStatus = caseStatus;
        this.setCreatedate(createdate);
        this.setUpdatedate(updatedate);
        this.createDateString = createdate != null ? createdate.toString() : null;
        this.updateDateString = updatedate != null ? updatedate.toString() : null;
        this.mvnoId = mvnoId;
    }
    public CaseDTO(Long caseId, String caseTitle, String caseType, String caseNumber, String currentAssigneeName, Integer customersId, LocalTime nextFollowupTime, LocalDate nextFollowupDate, Integer currentAssigneeId, String caseStatus, LocalDateTime createdate, LocalDateTime updatedate, Integer mvnoId, String caseReasonCategory, String caseReasonSubCategory) {
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.caseType = caseType;
        this.caseNumber = caseNumber;
        this.currentAssigneeName = currentAssigneeName;
        this.customersId = customersId;
        this.nextFollowupTime = nextFollowupTime;
        this.nextFollowupDate = nextFollowupDate;
        this.currentAssigneeId = currentAssigneeId;
        this.caseStatus = caseStatus;
        this.setCreatedate(createdate);
        this.setUpdatedate(updatedate);
        this.createDateString = createdate != null ? createdate.toString() : null;
        this.updateDateString = updatedate != null ? updatedate.toString() : null;
        this.mvnoId = mvnoId;
        this.caseReasonCategory = caseReasonCategory;
        this.caseReasonSubCategory = caseReasonSubCategory;
    }
}
