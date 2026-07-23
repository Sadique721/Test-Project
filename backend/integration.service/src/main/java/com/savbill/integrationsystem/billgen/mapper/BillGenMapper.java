package com.savbill.integrationsystem.billgen.mapper;

import com.savbill.integrationsystem.billgen.entity.BillGenRawData;
import com.savbill.integrationsystem.billgen.entity.ServiceArea;
import com.savbill.integrationsystem.billgen.model.BillGenDTO;
import com.savbill.integrationsystem.billgen.repository.ServiceAreaInRepo;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper
@Component
public abstract class BillGenMapper implements IBaseMapper<BillGenDTO, BillGenRawData> {

    @Autowired
    ServiceAreaInRepo serviceAreaInRepo;

    @Override
    public abstract BillGenDTO domainToDTO(BillGenRawData billGenRawData, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract BillGenRawData dtoToDomain(BillGenDTO dtoData, @Context CycleAvoidingMappingContext context);

    @AfterMapping
    void afterMapping(@MappingTarget BillGenDTO billGenDTO, BillGenRawData billGenRawData) {
        if (billGenRawData.getServiceAreaId() != null) {
            ServiceArea serviceArea = serviceAreaInRepo.findById(Long.valueOf(billGenDTO.getServiceAreaId())).orElse(null);
            if (serviceArea != null) {
                billGenDTO.setServiceAreaName(serviceArea.getName());
            }
        }

    }

}
