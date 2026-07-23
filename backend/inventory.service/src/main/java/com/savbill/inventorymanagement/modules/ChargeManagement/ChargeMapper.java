package com.savbill.inventorymanagement.modules.ChargeManagement;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.Tax;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.TaxMapper;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.TaxRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {TaxMapper.class})
public abstract class ChargeMapper implements IBaseMapper<ChargePojo, Charge> {
    @Mappings({
            @Mapping(target = "taxId",source = "data.taxId"),
            @Mapping(target = "displayId", source = "id"),
            @Mapping(target = "displayName", source = "name")
    })
    @Override
    public abstract ChargePojo domainToDTO(Charge data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "dtoData.taxId", target = "taxId")
    @Override
    public abstract Charge dtoToDomain(ChargePojo dtoData, @Context CycleAvoidingMappingContext context);

    @Autowired
    TaxRepository taxRepository;

    Integer fromTax(Tax entity) {
        return entity == null ? null : entity.getId();
    }

    Tax fromTaxid(Integer taxId) {
        if (taxId == null) {
            return null;
        }
        Tax entity = null;
        try {
//            entity = taxService.get(taxId);
            entity = taxRepository.findById(taxId).get();
            entity.setId(taxId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    public void commonAfterPostmapping(@MappingTarget ChargePojo dto, Charge domain) {
        if(domain.getTaxId()!=null){
            Tax entity = taxRepository.findById(dto.getTaxId()).get();
            dto.setTaxName(entity.getName());
        }
    }
}
