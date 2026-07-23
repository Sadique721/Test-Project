package com.savbill.commonGateway.moules.MasterManagement.Area.mapper;


import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area;
import com.savbill.commonGateway.moules.MasterManagement.Area.model.AreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.City.repository.CityRepository;
import com.savbill.commonGateway.moules.MasterManagement.City.service.CityService;
import com.savbill.commonGateway.moules.MasterManagement.Country.repository.CountryRepository;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.mapper.PincodeMapper;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.repository.PincodeRepository;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.service.PincodeService;
import com.savbill.commonGateway.moules.MasterManagement.State.repository.StateRepository;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = PincodeMapper.class)
public abstract class AreaMapper implements IBaseMapper<AreaDTO, Area> {
    String MODULE = " [AreaMapper] ";
    @Autowired
    StateService stateService;
    @Autowired
    CountryService countryService;
    @Autowired
    CityService cityService;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    PincodeService pincodeService;

    @Autowired
    PincodeRepository pincodeRepository;

    @Autowired
    StateRepository stateRepository;

    @Autowired
    CountryRepository countryRepository;
    @AfterMapping
    void afterMapping(@MappingTarget AreaDTO areaDTO, Area area) {
        try {
            if (area != null) {
                if (area.getCityId() != null) {
                    String cityName = cityRepository.findNameByCityId(area.getCityId());
                    if (cityName != null && !cityName.isEmpty()) {
                        areaDTO.setCityName(cityName);
                    }
                }
                if (area.getStateId() != null) {
                    String state = stateRepository.findNameById(area.getStateId());
                    if (state != null && !state.isEmpty()) {
                        areaDTO.setStateName(state);
                    }
                }
                if (area.getCountryId() != null) {
                    String country = countryRepository.findCountryNameById(area.getCountryId());
                    if (country != null && !country.isEmpty()) {
                        areaDTO.setCountryName(country);
                    }
                }
                if (area.getPincode() != null) {
                    String pincode = pincodeRepository.getPincodeByPincodeId(area.getPincode().getId());
                    if (pincode != null && !pincode.isEmpty()) {
                        areaDTO.setPincodeId(Math.toIntExact(area.getPincode().getId()));
                        areaDTO.setCode(pincode);
                    }
                }
                else if (area.getPincodeId() != null) {
                    String pincode = pincodeRepository.getPincodeByPincodeId(area.getPincodeId());
                    if (pincode != null && !pincode.isEmpty()) {
                        areaDTO.setPincodeId(Math.toIntExact(area.getPincodeId()));
                        areaDTO.setCode(pincode);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    @Override
    @Mappings({@Mapping(target = "displayId", source = "id"),
            @Mapping(target = "displayName", source = "name")})

    public abstract AreaDTO domainToDTO(Area domain, @Context CycleAvoidingMappingContext context);

}
