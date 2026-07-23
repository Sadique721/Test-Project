package com.savbill.cpm.modules.Area.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.postpaid.City;
import com.savbill.cpm.model.postpaid.Country;
import com.savbill.cpm.model.postpaid.State;
import com.savbill.cpm.modules.Area.domain.Area;
import com.savbill.cpm.modules.Area.model.AreaDTO;
import com.savbill.cpm.modules.Pincode.mapper.PincodeMapper;
import com.savbill.cpm.service.postpaid.CityService;
import com.savbill.cpm.service.postpaid.CountryService;
import com.savbill.cpm.service.postpaid.StateService;

@Mapper(uses = PincodeMapper.class)
public abstract class AreaMapper implements IBaseMapper<AreaDTO, Area> {
    String MODULE = " [AreaMapper] ";
    @Autowired
    StateService stateService;
    @Autowired
    CountryService countryService;
    @Autowired
    CityService cityService;

    @AfterMapping
    void afterMapping(@MappingTarget AreaDTO areaDTO, Area area) {
        try {
            if (area != null) {
                if (area.getCityId() != null) {
                    City city = cityService.get(area.getCityId());
                    areaDTO.setCityName(city.getName());
                }
                if (area.getStateId() != null) {
                    State state = stateService.get(area.getStateId());
                    areaDTO.setStateName(state.getName());
                }
                if (area.getCountryId() != null) {
                    Country country = countryService.get(area.getCountryId());
                    areaDTO.setCountryName(country.getName());
                }
                if (area.getPincode() != null) {
                    areaDTO.setPincodeId(area.getPincode().getId().intValue());
                    areaDTO.setCode(area.getPincode().getPincode());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    @Override
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract AreaDTO domainToDTO(Area domain, @Context CycleAvoidingMappingContext context);

}
