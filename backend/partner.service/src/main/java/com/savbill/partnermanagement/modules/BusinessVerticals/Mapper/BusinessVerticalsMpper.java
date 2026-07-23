package com.savbill.partnermanagement.modules.BusinessVerticals.Mapper;


import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.BusinessVerticals.DTO.BusinessVerticalsDTO;
import com.savbill.partnermanagement.modules.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.partnermanagement.modules.Region.Mapper.RegionMapper;
import com.savbill.partnermanagement.modules.Region.domain.Region;
import com.savbill.partnermanagement.modules.Region.model.RegionDTO;
import com.savbill.partnermanagement.modules.Region.service.RegionService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class BusinessVerticalsMpper implements IBaseMapper<BusinessVerticalsDTO, BusinessVerticals>
{
    @Autowired
    private RegionService regionService;

    @Autowired
    private RegionMapper regionMapper;

    @Override
    @Mapping(source = "dtoData.region_id", target = "buregionidList")
    public abstract BusinessVerticals dtoToDomain(BusinessVerticalsDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "data.buregionidList", target = "region_id")
    public abstract BusinessVerticalsDTO domainToDTO(BusinessVerticals data, CycleAvoidingMappingContext context);

    Integer fromRegionToId(Region entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Region fromIdToRegion(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Region entity;
        try {
            RegionDTO entityDTO = regionService.getEntityById(entityId.longValue());
            entity = regionMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
