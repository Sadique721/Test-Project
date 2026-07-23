package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.rms.entity.WareHouse;
import com.savbill.integrationsystem.rms.mapper.WareHouseMapper;
import com.savbill.integrationsystem.rms.model.WareHouseDto;
import com.savbill.integrationsystem.rms.repository.WareHouseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WareHouseServiceImpl implements WareHouseService{

    @Autowired
    WareHouseMapper wareHouseMapper;

    @Autowired
    WareHouseRepo wareHouseRepo;


    @Override
    public WareHouse saveWareHouseFromIntegration(WareHouseDto wareHouseDto) {
        WareHouse wareHouse = wareHouseMapper.dtoToDomain(wareHouseDto,new CycleAvoidingMappingContext());
        wareHouseRepo.save(wareHouse);
        return wareHouse;
    }
}
