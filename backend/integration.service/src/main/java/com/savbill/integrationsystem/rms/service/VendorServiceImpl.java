package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.rms.entity.Vendor;
import com.savbill.integrationsystem.rms.mapper.VendorMapper;
import com.savbill.integrationsystem.rms.model.VendorDto;
import com.savbill.integrationsystem.rms.repository.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;

public class VendorServiceImpl implements VendorService{

    @Autowired
    VendorMapper vendorMapper;

    @Autowired
    VendorRepo vendorRepo;

    @Override
    public Vendor saveVendorFromInventory(VendorDto vendorDto) {
        Vendor vendor = vendorMapper.dtoToDomain(vendorDto,new CycleAvoidingMappingContext());
        try{
            vendorRepo.save(vendor);
            return vendor;
        }catch (PersistenceException e){
            throw new PersistenceException();
        }
    }
}
