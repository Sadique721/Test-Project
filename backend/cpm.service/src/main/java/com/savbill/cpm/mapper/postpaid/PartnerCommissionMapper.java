package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.PartnerCommission;
import com.savbill.cpm.pojo.api.PartnerCommissionPojo;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.spring.SpringContext;

@Mapper
public abstract class PartnerCommissionMapper implements IBaseMapper<PartnerCommissionPojo, PartnerCommission> {

    @Override
    public abstract PartnerCommissionPojo domainToDTO(PartnerCommission partnerCommission, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract PartnerCommission dtoToDomain(PartnerCommissionPojo dtoData, @Context CycleAvoidingMappingContext context);

    @AfterMapping
    void afterMap(@MappingTarget PartnerCommissionPojo partnerCommissionPojo, PartnerCommission data) {
        CustomersService customersService = SpringContext.getBean(CustomersService.class);
        Customers customers = customersService.get(data.getCustomerid());
        partnerCommissionPojo.setCustomerName(customers.getFullName());
    }
}
