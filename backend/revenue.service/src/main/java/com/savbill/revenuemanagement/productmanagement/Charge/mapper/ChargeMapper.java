package com.savbill.revenuemanagement.productmanagement.Charge.mapper;

import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.Charge.dto.ChargePojo;
import com.savbill.revenuemanagement.productmanagement.Tax.mapper.TaxMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TaxMapper.class})
public abstract class ChargeMapper implements IBaseMapper<ChargePojo, Charge> {

//    @Mapping(source = "data.tax", target = "taxid")
//    @Override
//    @Mapping(target = "displayId", source = "id")
//    @Mapping(target = "displayName", source = "name")
    public abstract ChargePojo domainToDTO(Charge data, @Context CycleAvoidingMappingContext context);

//    @Mapping(source = "dtoData.taxid", target = "tax")
//    @Override
  public abstract Charge dtoToDomain(ChargePojo dtoData, @Context CycleAvoidingMappingContext context);

//    @Autowired
//    TaxService taxService;
//
//    Integer fromTax(Tax entity) {
//        return entity == null ? null : entity.getId();
//    }
//
//    Tax fromTaxid(Integer taxId) {
//        if (taxId == null) {
//            return null;
//        }
//        Tax entity = null;
//        try {
//            entity = taxService.get(taxId);
//            entity.setId(taxId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }

    @AfterMapping
    public void commonAfterPostmapping(@MappingTarget ChargePojo dto, Charge domain) {
        if(domain.getTax()!=null){
            dto.setTaxName(domain.getTax().getName());
        }
    }
}
