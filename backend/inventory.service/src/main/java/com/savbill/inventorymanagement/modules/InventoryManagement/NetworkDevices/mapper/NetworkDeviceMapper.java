package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.NetworkDeviceDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class NetworkDeviceMapper implements IBaseMapper<NetworkDeviceDTO, NetworkDevices> {

    @Autowired
    ProductServiceImpl productService;
    @Autowired
    ProductMapper productMapper;
    @Mappings({
        @Mapping(source = "inwardId", target = "inwardId"),
        @Mapping(source = "product", target = "productId")
    })
    @Override
    public abstract NetworkDeviceDTO domainToDTO(NetworkDevices networkDevices, @Context CycleAvoidingMappingContext context);

    @Mappings({
        @Mapping(source = "inwardId", target = "inwardId"),
        @Mapping(source = "productId", target = "product")
    })
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
