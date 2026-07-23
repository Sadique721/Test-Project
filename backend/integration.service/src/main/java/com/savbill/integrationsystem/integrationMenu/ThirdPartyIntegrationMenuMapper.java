package com.savbill.integrationsystem.integrationMenu;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper
public class ThirdPartyIntegrationMenuMapper implements IBaseMapper<ThirdPartyIntegrationMenuDto,ThirdPartyIntegrationMenu> {

    @Override
    public ThirdPartyIntegrationMenuDto domainToDTO(ThirdPartyIntegrationMenu thirdPartyIntegrationMenu, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public ThirdPartyIntegrationMenu dtoToDomain(ThirdPartyIntegrationMenuDto dtoData, CycleAvoidingMappingContext context) {
        return null;
    }

    @Override
    public List<ThirdPartyIntegrationMenuDto> domainToDTO(List<ThirdPartyIntegrationMenu> thirdPartyIntegrationMenus, CycleAvoidingMappingContext context) {
        return Collections.emptyList();
    }

    @Override
    public ThirdPartyIntegrationMenu updateDTOToDomain(ThirdPartyIntegrationMenuDto thirdPartyIntegrationMenuDto, ThirdPartyIntegrationMenu thirdPartyIntegrationMenu, CycleAvoidingMappingContext context) {
        return null;
    }
}
