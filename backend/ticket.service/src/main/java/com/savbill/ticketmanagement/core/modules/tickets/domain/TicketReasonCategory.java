package com.savbill.ticketmanagement.core.modules.tickets.domain;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.data.IBaseData;
import com.savbill.ticketmanagement.core.modules.PlanService.domain.PlanService;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import lombok.Getter;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EntityListeners(AuditableListener.class)
@Entity
@Table(name = "tblmticketreasoncategory")
public class TicketReasonCategory extends Auditable implements IBaseData<Long> {

    @DiffIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String categoryName;

    @ManyToOne(targetEntity = PlanService.class)
    @JoinColumn(name = "service_id", nullable = false, referencedColumnName = "serviceid")
    private PlanService service;


    @DiffIgnore
    @Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(targetEntity = TicketReasonCategoryTATMapping.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_reason_category_id")
    @DiffIgnore
    List<TicketReasonCategoryTATMapping> ticketReasonCategoryTATMappingList;

    String status;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @DiffIgnore
    @Column(name = "sla_time_P3")
    Long slaTimeP3;

    @DiffIgnore
    @Column(name = "sunitP3")
    String slaUnitP3;

    @DiffIgnore
    @Column(name = "sla_time_P2")
    Long slaTimeP2;

    @DiffIgnore
    @Column(name = "sunitP2")
    String slaUnitP2;

    @DiffIgnore
    @Column(name = "sla_time_P1")
    Long slaTimeP1;

    @DiffIgnore
    @Column(name = "sunitP1")
    String slaUnitP1;

    @Column(name = "department")
    String department;

    @DiffIgnore
    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    Integer lcoId;

    @Column(name = "is_default_problem_domain", nullable = false)
    Boolean isDefaultProblemDomain;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
