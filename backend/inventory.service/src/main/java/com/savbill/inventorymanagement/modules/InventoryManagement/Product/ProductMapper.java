package com.savbill.inventorymanagement.modules.InventoryManagement.Product;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.ChargeManagement.Charge;
import com.savbill.inventorymanagement.modules.ChargeManagement.ChargeRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement.Vendor;
import com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement.VendorRepo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract  class ProductMapper implements IBaseMapper<ProductDto, Product> {
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    VendorRepo vendorRepo;
    @Autowired
    ProductCategoryRepository productCategoryRepository;
    @Mappings({
            @Mapping(target = "vendor", source = "vendorId"),
//            @Mapping(target = "productCategory", source = "productCategory"),
            @Mapping(target = "newProductCharge", source = "newProductCharge"),
            @Mapping(target = "refurburshiedProductCharge", source = "refurburshiedProductCharge")
    })
    @Override
    public abstract Product dtoToDomain(ProductDto dto, @Context CycleAvoidingMappingContext context);
    @Mappings({
           @Mapping(target = "vendorId", source = "vendor"),
//            @Mapping(target = "productCategory", source = "productCategory"),
            @Mapping(target = "newProductCharge", source = "newProductCharge"),
            @Mapping(target = "refurburshiedProductCharge", source = "refurburshiedProductCharge")
    })
    @Override
    public abstract ProductDto domainToDTO(Product domain, @Context CycleAvoidingMappingContext context);
    public Integer chargeToInteger(Charge charge) {
        return charge != null ? charge.getId() : null;
    }

    public Charge integerToCharge(Integer integer) {
        if (integer == null) {
            return null;
        }

        Charge charge = new Charge();
        charge.setId(integer);
        return charge;
    }

    Long fromVendorToVendorId(Vendor vendor) {
        return null != vendor ? vendor.getId() : null;
    }
    Vendor fromVendorIdToVendor(Long id) {
        if (null == id) return null;
        Vendor vendor = null;
        try {
            vendor = vendorRepo.findById(id).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vendor;
    }

//    Long fromProductCategoryToProductCategory(ProductCategory productCategory) {
//        return null != productCategory ? productCategory.getId() : null;
//    }
//
//    ProductCategory fromProductCategoryToProductCategory(Long id) {
//        if (null == id) return null;
//        ProductCategory productCategory = null;
//        try {
//            productCategory = productCategoryRepository.findById(id).orElse(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return productCategory;
//    }
}
