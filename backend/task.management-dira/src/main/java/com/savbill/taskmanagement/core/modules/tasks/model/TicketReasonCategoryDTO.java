//package com.savbill.ticketmanagement.core.modules.tickets.model;
//
//
//import com.savbill.ticketmanagement.core.dto.IBaseDto;
//import com.savbill.ticketmanagement.core.modules.PlanService.domain.PlanService;
//import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketReasonCategoryTATMapping;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.List;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class TicketReasonCategoryDTO implements IBaseDto {
//
//
//    Long id;
//
//    String categoryName;
//
//    PlanService service;
//
//    Integer mvnoId;
//
//    Boolean isDeleted = false;
//
//    List<TicketReasonCategoryTATMapping> ticketReasonCategoryTATMappingList;
//
//    String status;
//
//    private Long buId;
//
//    Long slaTimeP3;
//
//    String slaUnitP3;
//
//    Long slaTimeP2;
//
//    String slaUnitP2;
//
//    Long slaTimeP1;
//
//    String slaUnitP1;
//
//    String department;
//
//    Integer lcoId;
//
//    Boolean isDefaultProblemDomain;
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
//    public Long getIdentityKey() {
//        return this.id;
//    }
//
//    @Override
//    public Integer getMvnoId() {
//        return this.mvnoId;
//    }
//
//    @Override
//    public void setMvnoId(Integer mvnoId) {
//        this.mvnoId = mvnoId;
//    }
//}
