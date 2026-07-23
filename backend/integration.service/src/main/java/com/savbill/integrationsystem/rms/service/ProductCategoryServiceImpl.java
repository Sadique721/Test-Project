package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.rms.entity.ProductCategory;
import com.savbill.integrationsystem.rms.mapper.ProductCategoryMapper;
import com.savbill.integrationsystem.rms.model.ProductCategoryDto;
import com.savbill.integrationsystem.rms.repository.ProductCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService{

    @Autowired
    ProductCategoryRepo productCategoryRepo;

    @Autowired
    ProductCategoryMapper productCategoryMapper;

    @Override
    public ProductCategory saveProductCategoryFromInventory(ProductCategoryDto productCategoryDto) {
        ProductCategory productCategory = productCategoryMapper.dtoToDomain(productCategoryDto,new CycleAvoidingMappingContext());
        if(productCategoryDto.getId() != null){
            productCategory.setId(productCategoryDto.getId());
        }
        try{
            productCategoryRepo.save(productCategory);
            return productCategory;
        }catch (PersistenceException e) {
            throw new PersistenceException("Not able to save Product Category from integration : " + e);
        }
    }
}
