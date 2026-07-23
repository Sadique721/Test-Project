package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.rms.entity.Product;
import com.savbill.integrationsystem.rms.model.ProductRmsDto;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    Product saveProductFromRms(ProductRmsDto productRmsDto);
}
