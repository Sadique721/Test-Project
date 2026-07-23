//package com.savbill.ticketmanagement.core.modules.tickets.model;
//
//
//import com.savbill.ticketmanagement.core.data.Auditable;
//import com.savbill.ticketmanagement.core.dto.IBaseDto;
//import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketSubCategoryGroupReasonMapping;
//import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseSubCategoryCategoryMapping;
//import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseCategoryTatMapping;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.List;
//
//@Getter
//@Setter
//public class TicketReasonSubCategoryDTO extends Auditable implements IBaseDto {
//
//
//    Long id;
//
//    String subCategoryName;
//
//    //TicketReasonCategory parentCategory;
//
//    List<TicketSubCategoryGroupReasonMapping> ticketSubCategoryGroupReasonMappingList;
//
//    List<CaseCategoryTatMapping>caseCategoryTatMappingList;
//
//    Integer mvnoId;
//
//    Boolean isDeleted = false;
//    String status;
//
//    List<CaseSubCategoryCategoryMapping> ticketSubCategoryReasonCategoryMappingList;
//
//    private Long buId;
//
//    private Integer lcoId;
//
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
//
//    @Override
//    public Long getIdentityKey() {
//        return id;
//    }
//
//    @Override
//    public Integer getMvnoId() {
//        return mvnoId;
//    }
//
//    @Override
//    public void setMvnoId(Integer mvnoId) {
//        this.mvnoId = mvnoId;
//    }
//}
