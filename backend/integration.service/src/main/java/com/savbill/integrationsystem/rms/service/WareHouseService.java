package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.rms.entity.WareHouse;
import com.savbill.integrationsystem.rms.model.WareHouseDto;
import org.springframework.stereotype.Service;

@Service
public interface WareHouseService {
    WareHouse saveWareHouseFromIntegration(WareHouseDto wareHouseDto);
}
