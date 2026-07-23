//package com.savbill.revenuemanagement.productmanagement.PlanQosMapping;
//
//
//import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
//import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
//import com.savbill.revenuemanagement.productmanagement.Plan.service.PostPaidPlanService;
////import com.savbill.revenuemanagement.productmanagement.qosPolicy.mapper.QOSPolicyMapper;
////import com.savbill.revenuemanagement.productmanagement.qosPolicy.service.QOSPolicyService;
//import org.mapstruct.Context;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Mapper
//public abstract class PlanQosMappingMapper implements IBaseMapper<PlanQosMappingPojo , PlanQosMappingEntity> {
//
//    @Autowired
//    private PostPaidPlanService postpaidPlanService;
//
//    @Autowired
//    private QOSPolicyService qosPolicyService;
//
//    @Autowired
//    private QOSPolicyMapper qosPolicyMapper;
//
////    @Mapping(source = "planid" , target = "postpaidPlan")
////    @Mapping(source = "qosid" , target = "qosPolicy")
//    public abstract PlanQosMappingEntity  dtoToDomain(PlanQosMappingPojo dto, @Context CycleAvoidingMappingContext context);
//
//    @Mapping(source = "postpaidPlan.id" , target = "planid")
//    @Mapping(source = "qosPolicy.id" , target = "qosid")
//    @Mapping(source = "qosPolicy.name" , target = "qosPolicyName")
//    public abstract PlanQosMappingPojo domainToDTO(PlanQosMappingEntity data, @Context CycleAvoidingMappingContext context);
//
//
//
//
//}
