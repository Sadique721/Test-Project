package com.savbill.ticketmanagement.core.modules.tickets.domain;


import com.savbill.ticketmanagement.core.data.IBaseData;
import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.modules.Customers.domain.Customers;
import com.savbill.ticketmanagement.core.modules.Partner.domain.Partner;
import com.savbill.ticketmanagement.core.modules.ResolutionReasons.domain.ResolutionReasons;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblcases")
@EntityListeners(AuditableListener.class)
public class Case extends Auditable implements IBaseData<Long> {

    @DiffIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseId;

    private String caseTitle;
    private String caseType;
    private String caseNumber;
    private String caseFor;
    private String caseOrigin;
    private String caseStatus;
    private String priority;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "case_for_id")
    private Customers customers;

    private String caseForPartner;
    private String caseForZone;
    private LocalDate nextFollowupDate;
    private LocalTime nextFollowupTime;
    @CreationTimestamp
    private LocalDateTime caseStartedOn;
    @CreationTimestamp
    private LocalDateTime firstAssignedOn;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "current_assignee_id")
    private StaffUser currentAssignee;


    @ManyToOne
    @JoinColumn(name = "final_resolution_id")
    private ResolutionReasons finalResolution;

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "final_resolved_by_id")
    private StaffUser finalResolvedBy;

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "final_closed_by_id")
    private StaffUser finalClosedBy;

    @Column(name = "final_resolution_date")
    private LocalDateTime finalResolutionDate;

    @Column(name = "final_closed_date")
    private LocalDateTime finalClosedDate;

    @DiffIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ticket")
    @OrderBy("id desc")
    private List<CaseUpdate> caseUpdateList = new ArrayList<>();

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "partnerid")
    private Partner partner;

    @Column(name = "first_remark")
    private String firstRemark;

    @Transient
    private Long mobile;

    @Transient
    private String userName;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Transient
    private String customerName;
    @Transient
    private String assigneeName;

    private Integer rating;

    @Column(name = "customer_feedback")
    private String customerFeedback;

    private Long ticketReasonCategoryId;

    private Long reasonSubCategoryId;

    @Column(name = "group_reason_id")
    private Long groupReasonId;

    private Long tatMappingId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @DiffIgnore
    private Long rootCauseReasonId;

    @Column(name = "source")
    private String source;

    @Column(name = "sub_source")
    private String subSource;

    @Column(name = "team_hir_mapping_id")
    private Long teamHierarchyMappingId;

    @OneToMany(targetEntity = TicketAssignStaffMapping.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private List<TicketAssignStaffMapping> ticketAssignStaffMappings;

    private String department;

    @Column(name = "cust_additional_mobile_number")
    private String customerAdditionalMobileNumber;

    @Column(name = "cust_additional_email")
    private String customerAdditionalEmail;

    @DiffIgnore
    @Column(name = "parent_ticket_id")
    private Integer parentTicketId;

    @DiffIgnore
    @Column(name="helper_name")
    private String helperName;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = TicketServicemapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id")
    List<TicketServicemapping> ticketServicemappingList;


    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return caseId;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
    @Column(name="case_order", length=40,nullable = true)
    private Long case_order;


    @Column(name= "ticket_sla_time")
    private Integer caseSlaTime;

    @Column(name= "ticket_sla_unit")
    private String caseSlaUnit;

    @Transient
    private Long parentId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ticketid")
    @OrderBy("id desc")
//    @OneToMany(targetEntity = CaseFeedbackRel.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "ticketid")
    List<CaseFeedbackRel> caseFeedbackRel;

    @Column(name= "call_status", columnDefinition = "Boolean default false", nullable = false)
    private Boolean call_status= false;

    @Column(name= "is_closed", columnDefinition = "Boolean default false", nullable = false)
    private Boolean is_closed= false;

    @Column(name = "deacivate_reason", length = 200)
    private String deacivate_reason;

    @Transient
    private String reasonSubCategoryName;
    @Transient
    private String ticketReasonCategoryName;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "ticket_classification", nullable = false)
    private String ticketClassification;

    @Transient
    private String mvnoName;

    public Case() {
    }

    public Case(String caseNumber, String priority, String caseStatus, String customerName, String caseType, LocalDate nextFollowupDate, String assigneeName) {
        this.caseNumber = caseNumber;
        this.priority = priority;
        this.customerName = customerName;
        this.caseType = caseType;
        this.caseStatus = caseStatus;
        this.nextFollowupDate = nextFollowupDate;
        this.assigneeName = assigneeName;
    }

    public Case(Long caseId,String caseNumber, String caseType, String customerName, Long reasonSubCategoryId) {
        this.caseId = caseId;
        this.caseNumber = caseNumber;
        this.customerName = customerName;
        this.caseType = caseType;
        this.reasonSubCategoryId = reasonSubCategoryId;

    }
}
