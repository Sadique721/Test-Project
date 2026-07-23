package com.savbill.cpm.modules.customerDocDetails.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.customerDocDetails.domain.CustomerDocDetails;
import com.savbill.cpm.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.savbill.cpm.repository.radius.CustomersRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class CustomerDocDetailsMapper implements IBaseMapper<CustomerDocDetailsDTO, CustomerDocDetails> {

    @Autowired
    private CustomersRepository customersRepository;

    @Override
    @Mapping(target = "customer", source = "custId")
    public abstract CustomerDocDetails dtoToDomain(CustomerDocDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "customer", target = "custId")
    public abstract CustomerDocDetailsDTO domainToDTO(CustomerDocDetails domain, @Context CycleAvoidingMappingContext context);


    Integer fromCustomerToCustId(Customers entity) {
        return entity == null ? null : entity.getId();
    }

    Customers fromCustIdToCustomer(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Customers entity;
        try {
            entity = customersRepository.findById(entityId).orElse(null);
            if (entity != null) {
                entity.setId(entityId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
