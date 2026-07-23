//package com.savbill.revenuemanagement.productmanagement.qosPolicy.mapper;
//
//import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
//import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
//import com.savbill.revenuemanagement.productmanagement.qosPolicy.domain.QOSPolicy;
//import com.savbill.revenuemanagement.productmanagement.qosPolicy.model.QOSPolicyDTO;
//import org.mapstruct.Context;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper
//public abstract class QOSPolicyMapper implements IBaseMapper<QOSPolicyDTO, QOSPolicy> {
//
//    @Override
//    @Mapping(target = "displayId", source = "id")
//    @Mapping(target = "displayName", source = "name")
//    public abstract QOSPolicyDTO domainToDTO(QOSPolicy data, @Context CycleAvoidingMappingContext context);
//
//}
