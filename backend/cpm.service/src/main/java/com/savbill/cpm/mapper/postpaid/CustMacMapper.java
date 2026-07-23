package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.CustMacMappping;
import com.savbill.cpm.model.postpaid.CustMacMapppingPojo;
import com.savbill.cpm.service.common.CustomersService;

@Mapper
public abstract class CustMacMapper implements IBaseMapper<CustMacMapppingPojo, CustMacMappping> {

    @Autowired
    private CustomersService customersService;

    @Override
    @Mapping(target = "customer", source = "custid")
    public abstract CustMacMappping dtoToDomain(CustMacMapppingPojo pojo, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "custid", source = "customer")
    public abstract CustMacMapppingPojo domainToDTO(CustMacMappping domain, @Context CycleAvoidingMappingContext context);

    Integer fromCustomerToId(Customers entity) {
        return entity == null ? null : entity.getId();
    }

    Customers fromIdToCustomer(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Customers entity;
        try {
            entity = customersService.get(entityId);
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

}
