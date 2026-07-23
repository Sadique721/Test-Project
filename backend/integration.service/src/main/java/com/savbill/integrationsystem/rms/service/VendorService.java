package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.rms.entity.Vendor;
import com.savbill.integrationsystem.rms.model.VendorDto;
import org.springframework.stereotype.Service;

@Service
public interface VendorService {
    Vendor saveVendorFromInventory(VendorDto vendorDto);
}
