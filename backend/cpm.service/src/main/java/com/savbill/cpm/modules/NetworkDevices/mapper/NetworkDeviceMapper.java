package com.savbill.cpm.modules.NetworkDevices.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.modules.InventoryManagement.product.Product;
import com.savbill.cpm.modules.InventoryManagement.product.ProductDto;
import com.savbill.cpm.modules.InventoryManagement.product.ProductMapper;
import com.savbill.cpm.modules.InventoryManagement.product.ProductServiceImpl;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.NetworkDevices.domain.NetworkDevices;
import com.savbill.cpm.modules.NetworkDevices.model.NetworkDeviceDTO;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class NetworkDeviceMapper implements IBaseMapper<NetworkDeviceDTO, NetworkDevices> {

    @Autowired
    ProductServiceImpl productService;
    @Autowired
    ProductMapper productMapper;

    @Mapping(source = "inwardId", target = "inwardId")
    @Mapping(source = "product", target = "productId")
    @Override
    public abstract NetworkDeviceDTO domainToDTO(NetworkDevices networkDevices, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "inwardId", target = "inwardId")
    @Mapping(source = "productId", target = "product")
    @Override
    public abstract NetworkDevices dtoToDomain(NetworkDeviceDTO dtoData, @Context CycleAvoidingMappingContext context);
    Integer fromProductToProductId(Product entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Product fromProductIdToProduct(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Product entity;
        try {
            ProductDto entityDTO = productService.getEntityById(entityId.longValue());
            entity = productMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

//    Integer fromProductToProductId(Product entity) {
//        return entity == null ? null : entity.getId().intValue();
//    }
//
//    Product fromProductIdToProduct(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        Product entity;
//        try {
//            ProductDto entityDTO = productService.getEntityById(entityId.longValue());
//            entity = productMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//            entity.setId(entityId.longValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }

}
