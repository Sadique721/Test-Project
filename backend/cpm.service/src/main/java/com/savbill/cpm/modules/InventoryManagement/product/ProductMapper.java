package com.savbill.cpm.modules.InventoryManagement.product;

import com.savbill.cpm.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public abstract  class ProductMapper implements IBaseMapper<ProductDto, Product> {


//    @Autowired
//    ChargeRepository chargeRepository;
//
//    @Override
////    @Mapping(target = "charge", source = "oldProductChargeId")
////    @Mapping(target= "charge", source = "newProductChargeId")
//    @Mapping( target = "charge", expression = "java(dto.oldProductChargeId + \" \" + product.newProductChargeId")
//    public abstract Product dtoToDomain(ProductDto dto, @Context CycleAvoidingMappingContext context);
//
//    @Override
////    @Mapping(target = "oldProductChargeId", source = "charge")
////    @Mapping(target= "newProductChargeId", source = "charge")
//    @Mapping( source = "charge", expression = "java(domain.oldProductChargeId + \" \" + domain.newProductChargeId")
//    public abstract ProductDto domainToDTO(Product domain, @Context CycleAvoidingMappingContext context);
//
//    Integer fromChargeToChargeId(Charge charge) {
//        return null != charge ? charge.getId() : null;
//    }
//
//    Charge fromChargeIdToCharge(Integer id) {
//        if (null == id) return null;
//        Charge entity = null;
//        try {
//            entity = chargeRepository.findById(id).orElse(null);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return entity;
//    }
}
