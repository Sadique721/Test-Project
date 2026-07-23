package com.savbill.ticketmanagement.core.modules.CustomerAddress.mapper;


import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.CustomerAddress.domain.CustomerAddress;
import com.savbill.ticketmanagement.core.modules.CustomerAddress.dto.CustomerAddressPojo;
import com.savbill.ticketmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.ticketmanagement.core.modules.Customers.domain.Customers;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class CustomerAddressMapper implements IBaseMapper<CustomerAddressPojo, CustomerAddress> {

    @Autowired
    private CustomersService customersService;

    @Override
    @Mapping(target = "customer", source = "customerId")
    public abstract CustomerAddress dtoToDomain(CustomerAddressPojo dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "customer", target = "customerId")
    public abstract CustomerAddressPojo domainToDTO(CustomerAddress domain, @Context CycleAvoidingMappingContext context);


    Integer fromCustomerTocustomerId(Customers entity) {
        return entity == null ? null : entity.getId();
    }

    Customers fromcustomerIdToCustomer(Integer entityId) {
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
