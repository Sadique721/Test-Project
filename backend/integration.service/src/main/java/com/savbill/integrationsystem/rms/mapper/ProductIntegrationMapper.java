package com.savbill.integrationsystem.rms.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.rms.entity.Product;
import com.savbill.integrationsystem.rms.model.ProductDto;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper
public abstract class ProductIntegrationMapper implements IBaseMapper<ProductDto, Product> {


    @Override
    public abstract ProductDto domainToDTO(Product product, CycleAvoidingMappingContext context);

    @Override
    public abstract Product dtoToDomain(ProductDto dtoData, CycleAvoidingMappingContext context);

    @Override
    public List<ProductDto> domainToDTO(List<Product> product, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public Product updateDTOToDomain(ProductDto productDto, Product product, CycleAvoidingMappingContext context) {
        return null;
    }
}
