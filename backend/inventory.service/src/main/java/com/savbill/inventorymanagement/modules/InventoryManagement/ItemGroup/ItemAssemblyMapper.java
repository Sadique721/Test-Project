package com.savbill.inventorymanagement.modules.InventoryManagement.ItemGroup;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class ItemAssemblyMapper implements IBaseMapper<ItemAssemblyDto, ItemAssembly> {

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Override
    public abstract ItemAssemblyDto domainToDTO(ItemAssembly itemAssembly, CycleAvoidingMappingContext context);

    @Override
    public abstract ItemAssembly dtoToDomain(ItemAssemblyDto dtoData, CycleAvoidingMappingContext context);


    Integer fromEntityToId(InOutWardMACMapping entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    InOutWardMACMapping fromIdToEntity(Integer id) {
        if (id == null) {
            return null;
        }
        InOutWardMACMapping entity;
        try {
            entity = inOutWardMacRepo.findById(id.longValue()).get();
            entity.setId(id.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
