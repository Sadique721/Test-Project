package com.savbill.integrationsystem.Case;

import com.savbill.integrationsystem.rabbitmq.TicketMessageIntegration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblcases")
@AllArgsConstructor
@NoArgsConstructor

public class Case {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Long caseId;
    @Column(name = "case_title")
    private String caseTitle;
    @Column(name = "case_type")
    private String caseType;
    @Column(name = "case_number")
    private String caseNumber;
    @Column(name = "case_for")
    private String caseFor;
    @Column(name = "case_origin")
    private String caseOrigin;
    @Column(name = "case_status")
    private String caseStatus;
    @Column(name = "priority")
    private String priority;
    @Column(name = "cust_id")
    private Integer custId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "case_for_partner")
    private String caseForPartner;
    @Column(name = "case_for_zone")
    private String caseForZone;
    @Column(name = "next_followup_date")
    private String nextFollowupDate;
    @Column(name = "next_followup_time")
    private String nextFollowupTime;
    @Column(name = "case_started_on")
    private String caseStartedOn;
    @Column(name = "first_assigned_on")
    private String firstAssignedOn;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(name = "current_assignee_id")
    private Integer currentAssignee;
    @Column(name = "final_resolution_id")
    private Integer finalResolution;
    @Column(name = "final_resolved_by_id")
    private Integer finalResolvedBy;
    @Column(name = "final_closed_by_id")
    private Integer finalClosedBy;
    @Column(name = "final_resolution_date")
    private String finalResolutionDate;
    @Column(name = "final_closed_date")
    private String finalClosedDate;
    @Column(name = "partner_name")
    private String partner;
    @Column(name = "first_remark")
    private String firstRemark;
    @Column(name = "mobile")
    private Long mobile;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    @Column(name = "assignee_name")
    private String assigneeName;
    @Column(name = "rating")
    private Integer rating;
    @Column(name = "customer_feedback")
    private String customerFeedback;
    @Column(name = "ticket_reason_category_id")
    private Long ticketReasonCategoryId;

    @Column(name = "ticket_reason_category_name")
    private String ticketReasonCategoryName;

    @Column(name = "reason_subcategory_id")
    private Long reasonSubCategoryId;

    @Column(name = "reason_subcategory_name")
    private String reasonSubCategoryName;

    @Column(name = "group_reason_id")
    private Long groupReasonId;

    @Column(name = "tatmapping_id")
    private Long tatMappingId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name="rootcause_reason_id")
    private Long rootCauseReasonId;

    @Column(name = "source")
    private String source;

    @Column(name = "sub_source")
    private String subSource;

    @Column(name = "team_hir_mapping_id")
    private Long teamHierarchyMappingId;

/*    @OneToMany(targetEntity = TicketAssignStaffMapping.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private List<TicketAssignStaffMapping> ticketAssignStaffMappings;*/
    @Column(name="department")
    private String department;

    @Column(name = "cust_additional_mobile_number")
    private String customerAdditionalMobileNumber;

    @Column(name = "cust_additional_email")
    private String customerAdditionalEmail;

    @Column(name = "parent_ticket_id")
    private Integer parentTicketId;

    @Column(name="helper_name")
    private String helperName;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;

/*    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = TicketServicemapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id")
    private List<TicketServicemapping> ticketServicemappingList;*/

    @Column(name="case_order", length=40,nullable = true)
    private Long case_order;


    @Column(name= "ticket_sla_time")
    private Integer caseSlaTime;

    @Column(name= "ticket_sla_unit")
    private String caseSlaUnit;

    @Column(name="CREATEDATE")
    private String createDate;
    @Column(name="LASTMODIFIEDDATE")
    private String modifyDate;

    public Case(TicketMessageIntegration message) {

        if (message != null) {

            this.caseId = message.getCaseId();
            this.caseTitle = message.getCaseTitle();
            this.caseType = message.getCaseType();
            this.caseNumber = message.getCaseNumber();
            this.caseFor = message.getCaseFor();
            this.caseOrigin = message.getCaseOrigin();
            this.caseStatus = message.getCaseStatus();
            this.priority = message.getPriority();
            this.custId = message.getCustomers();
//        this.custName = message.getCustomers().getUsername();
            this.caseForPartner = message.getCaseForPartner();
            this.caseForZone = message.getCaseForZone();
            this.nextFollowupDate = message.getNextFollowupDate();
            this.nextFollowupTime = message.getNextFollowupTime();
            this.caseStartedOn = message.getCaseStartedOn();
            this.firstAssignedOn = message.getFirstAssignedOn();
            this.isDelete = message.getIsDelete();
            this.currentAssignee = message.getCurrentAssignee();
            this.finalResolution = message.getFinalResolution();
            this.finalResolvedBy = message.getFinalResolvedBy();
            this.finalClosedBy = message.getFinalClosedBy();
            this.finalResolutionDate = message.getFinalResolutionDate();
            this.finalClosedDate = message.getFinalClosedDate();
//        this.caseUpdateList   =  message.getCaseUpdateList();
            this.partner = message.getPartner();
            this.firstRemark = message.getFirstRemark();
            this.mobile = message.getMobile();
            this.userName = message.getUserName();
            this.mvnoId = message.getMvnoId();
            this.customerName = message.getCustomerName();
            this.assigneeName = message.getAssigneeName();
            this.rating = message.getRating();
            this.customerFeedback = message.getCustomerFeedback();
            this.ticketReasonCategoryId = message.getTicketReasonCategoryId();
            this.ticketReasonCategoryName = message.getTicketReasonCategoryName();
            this.reasonSubCategoryId = message.getReasonSubCategoryId();
            this.reasonSubCategoryName = message.getReasonSubCategoryName();
            this.groupReasonId = message.getGroupReasonId();
            this.tatMappingId = message.getTatMappingId();
            this.buId = message.getBuId();
            this.rootCauseReasonId = message.getRootCauseReasonId();
            this.source = message.getSource();
            this.subSource = message.getSubSource();
            this.teamHierarchyMappingId = message.getTeamHierarchyMappingId();
            //  this.ticketAssignStaffMappings   =  message.getTicketAssignStaffMappings();
            this.department = message.getDepartment();
            this.customerAdditionalMobileNumber = message.getCustomerAdditionalMobileNumber();
            this.customerAdditionalEmail = message.getCustomerAdditionalEmail();
            this.parentTicketId = message.getParentTicketId();
            this.helperName = message.getHelperName();
            this.lcoId = message.getLcoId();
            //this.ticketServicemappingList   =  message.getTicketServicemappingList();
            this.case_order = message.getCase_order();
            this.caseSlaTime = message.getCaseSlaTime();
            this.caseSlaUnit = message.getCaseSlaUnit();
            this.createDate = message.getCreateDate();
            this.modifyDate = message.getModifyDate();
        }
    }
}
