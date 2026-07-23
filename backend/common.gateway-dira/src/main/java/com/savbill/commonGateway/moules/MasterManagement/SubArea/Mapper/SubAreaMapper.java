package com.savbill.commonGateway.moules.MasterManagement.SubArea.Mapper;

import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface  SubAreaMapper extends IBaseMapper<SubAreaDTO, SubArea> {

    @Override
    @Mapping(source = "subAreaDTO.areaId", target = "area", qualifiedByName = "mapAreaById")
    SubArea dtoToDomain(SubAreaDTO subAreaDTO, @Context CycleAvoidingMappingContext context);

    @Named("mapAreaById")
    default Area mapAreaById(Long areaId) {
        return (areaId != null) ? new Area(areaId) : null;
    }

    @Override
    default List<SubAreaDTO> domainToDTO(List<SubArea> data, @Context CycleAvoidingMappingContext context) {
        return data.stream().map(this::mapSubAreaToDTO).collect(Collectors.toList());
    }


//    @Mapping(source = "area", target = "areaId", qualifiedByName = "mapArea")
    SubAreaDTO mapSubAreaToDTO(SubArea subArea);

    @Named("mapArea")
    default Long mapArea(Area area) {
        return (area != null) ? area.getId() : null;
    }
//    @Mapping(source = "area", target = "areaId", qualifiedByName = "mapArea")
    SubAreaDTO domainToDTO(SubArea data, @org.mapstruct.Context CycleAvoidingMappingContext context);
}

