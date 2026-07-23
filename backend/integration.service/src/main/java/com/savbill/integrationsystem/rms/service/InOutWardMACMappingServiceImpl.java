package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.rms.entity.InOutWardMACMapping;
import com.savbill.integrationsystem.rms.mapper.InOutWardMACMappingMapper;
import com.savbill.integrationsystem.rms.model.InOutWardMACMapingDTO;
import com.savbill.integrationsystem.rms.repository.InOutWardMACMappingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InOutWardMACMappingServiceImpl implements InOutWardMACMappingService {

    @Autowired
    InOutWardMACMappingRepo inOutWardMACMappingRepo;

    @Autowired
    InOutWardMACMappingMapper inOutWardMACMappingMapper;

    @Override
    public InOutWardMACMapping saveInOutWardMACMapping(InOutWardMACMapingDTO inOutWardMACMappingDTO) {
        InOutWardMACMapping inOutWardMACMapping = inOutWardMACMappingMapper.dtoToDomain(inOutWardMACMappingDTO,new CycleAvoidingMappingContext());
        inOutWardMACMappingRepo.save(inOutWardMACMapping);
        return inOutWardMACMapping;
    }
}
