package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.mapper;

import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.PolyGone;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.PolyGoneDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public class PolyGoneMapper implements IBaseMapper<PolyGoneDTO, PolyGone> {
    @Override
    public PolyGoneDTO domainToDTO(PolyGone polyGone, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public PolyGone dtoToDomain(PolyGoneDTO dtoData, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public List<PolyGoneDTO> domainToDTO(List<PolyGone> polyGones, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public List<PolyGone> dtoToDomain(List<PolyGoneDTO> dtoData, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public PolyGone updateDTOToDomain(PolyGoneDTO polyGoneDTO, PolyGone polyGone, CycleAvoidingMappingContext context) {
        return null;
    }
}
