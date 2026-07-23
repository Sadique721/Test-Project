//package com.savbill.ticketmanagement.core.modules.tickets.domain;
//
//
//
//import com.savbill.ticketmanagement.core.data.Auditable;
//import com.savbill.ticketmanagement.core.data.IBaseData;
//import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.annotations.LazyCollection;
//import org.hibernate.annotations.LazyCollectionOption;
//import org.javers.core.metamodel.annotation.DiffIgnore;
//
//import javax.persistence.*;
//import java.util.List;
//
//@Getter
//@Setter
//@EntityListeners(AuditableListener.class)
//@Entity
//@Table(name = "tblmticketreasonsubcategory")
//public class TicketReasonSubCategory extends Auditable implements IBaseData<Long> {
//  @DiffIgnore
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    String subCategoryName;
//
//    /*@ManyToOne(targetEntity = TicketReasonCategory.class)
//    @JoinColumn(name = "parent_category_id", nullable = false, referencedColumnName = "id")
//    private TicketReasonCategory parentCategory;*/
//
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(targetEntity = TicketSubCategoryGroupReasonMapping.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "ticket_reason_sub_category_id")
//    List<TicketSubCategoryGroupReasonMapping> ticketSubCategoryGroupReasonMappingList;
//
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(targetEntity = CaseSubCategoryCategoryMapping.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "ticket_reason_sub_category_id")
//    List<CaseSubCategoryCategoryMapping> caseSubCategoryCategoryMappingList;
//
//    @DiffIgnore
//    @Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
//    private Integer mvnoId;
//
//    @Column(columnDefinition = "Boolean default false", nullable = false)
//    private Boolean isDeleted = false;
//
//    String status;
//
//    @DiffIgnore
//    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
//    private Long buId;
//
//    @DiffIgnore
//    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
//    private Integer lcoId;
//
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(targetEntity = CaseCategoryTatMapping.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "sub_category_mapping_id")
//    List<CaseCategoryTatMapping> caseCategoryTatMappingList;
//
////    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
////    private Integer lcoId;
//
//    @Column(name = "is_default_sub_problem_domain", nullable = false)
//    Boolean isDefaultSubProblemDomain;
//
//    public Long getBuId() {
//        return buId;
//    }
//
//    public void setBuId(Long buId) {
//        this.buId = buId;
//    }
//
//    @Override
//    public Long getPrimaryKey() {
//        return this.id;
//    }
//
//    @Override
//    public void setDeleteFlag(boolean deleteFlag) {
//        this.isDeleted = deleteFlag;
//    }
//
//    @Override
//    public boolean getDeleteFlag() {
//        return this.isDeleted;
//    }
//}
