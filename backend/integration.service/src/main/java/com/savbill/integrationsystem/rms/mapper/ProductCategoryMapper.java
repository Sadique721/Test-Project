package com.savbill.integrationsystem.rms.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.rms.entity.ProductCategory;
import com.savbill.integrationsystem.rms.model.ProductCategoryDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class ProductCategoryMapper implements IBaseMapper<ProductCategoryDto, ProductCategory> {
    @Override
    public abstract ProductCategoryDto domainToDTO(ProductCategory productCategory, CycleAvoidingMappingContext context);

    @Override
    public abstract ProductCategory dtoToDomain(ProductCategoryDto dtoData, CycleAvoidingMappingContext context);
    @Override
    public List<ProductCategoryDto> domainToDTO(List<ProductCategory> productCategories, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public ProductCategory updateDTOToDomain(ProductCategoryDto productCategoryDto, ProductCategory productCategory, CycleAvoidingMappingContext context) {
        return null;
    }
}
