package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.rms.entity.InOutWardMACMapping;
import com.savbill.integrationsystem.rms.model.InOutWardMACMapingDTO;
import org.springframework.stereotype.Service;

@Service
public interface InOutWardMACMappingService {
    InOutWardMACMapping saveInOutWardMACMapping(InOutWardMACMapingDTO inOutWardMACMappingDTO);
}
