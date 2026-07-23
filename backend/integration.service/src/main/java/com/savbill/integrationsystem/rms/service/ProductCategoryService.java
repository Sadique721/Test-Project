package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.rms.entity.ProductCategory;
import com.savbill.integrationsystem.rms.model.ProductCategoryDto;
import org.springframework.stereotype.Service;

@Service
public interface ProductCategoryService {
    ProductCategory saveProductCategoryFromInventory(ProductCategoryDto productCategoryDto);
}
