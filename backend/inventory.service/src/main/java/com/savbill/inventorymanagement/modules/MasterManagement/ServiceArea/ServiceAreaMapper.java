package com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.MasterManagement.Pincode.Pincode;
import com.savbill.inventorymanagement.modules.MasterManagement.Pincode.PincodeMapper;
import com.savbill.inventorymanagement.modules.MasterManagement.Pincode.PincodeRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class ServiceAreaMapper implements IBaseMapper<ServiceAreaDTO, ServiceArea> {

//    @Autowired
//    private PincodeService pincodeService;

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private PincodeMapper pincodeMapper;

    @Override
//    @Mapping(source = "dtoData.pincodes", target = "pincodeList")
    public abstract ServiceArea dtoToDomain(ServiceAreaDTO dtoData, CycleAvoidingMappingContext context);

    @Mappings({
//        @Mapping(source = "data.pincodeList", target = "pincodes"),
        @Mapping(target = "displayId", source = "data.id"),
        @Mapping(target = "displayName", source = "data.name")
    })
    @Override
    public abstract ServiceAreaDTO domainToDTO(ServiceArea data, CycleAvoidingMappingContext context);

//    public abstract List<Pincode> mapPincodesToPincodeList(List<Integer> value);

    public abstract List<Integer> mapPincodeListToPincodes(List<Pincode> value);

    Integer fromPincodeToId(Pincode entity) {
        return entity == null ? null : entity.getId().intValue();
    }

//    Pincode fromIdToPincode(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        Pincode entity;
//        try {
////            PincodeDTO entityDTO = pincodeService.getEntityById(entityId.longValue());
//            PincodeDTO entityDTO = pincodeRepository.findById(Long.valueOf(entityId));
//            entity = pincodeMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//            entity.setId(entityId.longValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }


}
