package com.savbill.revenuemanagement.mastermanagement.ServiceArea.mapper;



import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.mastermanagement.Pincode.domain.Pincode;
import com.savbill.revenuemanagement.mastermanagement.Pincode.mapper.PincodeMapper;
import com.savbill.revenuemanagement.mastermanagement.Pincode.model.PincodeDTO;
import com.savbill.revenuemanagement.mastermanagement.Pincode.service.PincodeService;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.model.ServiceAreaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class ServiceAreaMapper implements IBaseMapper<ServiceAreaDTO, ServiceArea> {

    @Autowired
    private PincodeService pincodeService;

    @Autowired
    private PincodeMapper pincodeMapper;

    @Override
    @Mapping(source = "dtoData.pincodes", target = "pincodeList")
    public abstract ServiceArea dtoToDomain(ServiceAreaDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mappings({
            @Mapping(source = "data.pincodeList", target = "pincodes"),
            @Mapping(target = "displayId", source = "data.id"),
            @Mapping(target = "displayName", source = "data.name")
    })
    public abstract ServiceAreaDTO domainToDTO(ServiceArea data, CycleAvoidingMappingContext context);

    public abstract List<Pincode> mapPincodesToPincodeList(List<Integer> value);

    public abstract List<Integer> mapPincodeListToPincodes(List<Pincode> value);

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
