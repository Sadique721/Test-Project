package com.savbill.cpm.modules.ServiceArea.mapper;

import java.util.List;

import com.savbill.cpm.modules.Pincode.domain.Pincode;
import com.savbill.cpm.modules.Pincode.mapper.PincodeMapper;
import com.savbill.cpm.modules.Pincode.model.PincodeDTO;
import com.savbill.cpm.modules.Pincode.service.PincodeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceArea;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaDTO;

@Mapper
public abstract class ServiceAreaMapper implements IBaseMapper<ServiceAreaDTO, ServiceArea>{

    @Autowired
    private PincodeService pincodeService;

    @Autowired
    private PincodeMapper pincodeMapper;

    @Override
    @Mapping(source = "dtoData.pincodes", target = "pincodeList")
    public abstract ServiceArea dtoToDomain(ServiceAreaDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "data.pincodeList", target = "pincodes")
    @Mapping(target = "displayId", source = "data.id")
    @Mapping(target = "displayName", source = "data.name")
    public abstract ServiceAreaDTO domainToDTO(ServiceArea data, CycleAvoidingMappingContext context);

    public abstract java.util.List<Pincode> mapPincodesToPincodeList(List<Integer> value);

    public abstract java.util.List<Integer> mapPincodeListToPincodes(List<Pincode> value);

    Integer fromPincodeToId(Pincode entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Pincode fromIdToPincode(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Pincode entity;
        try {
            PincodeDTO entityDTO = pincodeService.getEntityById(entityId.longValue());
            entity = pincodeMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }


}
