package com.savbill.taskmanagement.core.modules.Area.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Area.domain.Area;
import com.savbill.taskmanagement.core.modules.Area.model.AreaDTO;
import com.savbill.taskmanagement.core.modules.Pincode.mapper.PincodeMapper;
import org.mapstruct.*;

@Mapper(uses = PincodeMapper.class)
public abstract class AreaMapper implements IBaseMapper<AreaDTO, Area> {
//    String MODULE = " [AreaMapper] ";
//    @Autowired
//    StateService stateService;
//    @Autowired
//    CountryService countryService;
//    @Autowired
//    CityService cityService;
//
//    @AfterMapping
//    void afterMapping(@MappingTarget AreaDTO areaDTO, Area area) {
//        try {
//            if (area != null) {
//                if (area.getCityId() != null) {
//                    City city = cityService.get(area.getCityId());
//                    areaDTO.setCityName(city.getName());
//                }
//                if (area.getStateId() != null) {
//                    State state = stateService.get(area.getStateId());
//                    areaDTO.setStateName(state.getName());
//                }
//                if (area.getCountryId() != null) {
//                    Country country = countryService.get(area.getCountryId());
//                    areaDTO.setCountryName(country.getName());
//                }
//                if (area.getPincode() != null) {
//                    areaDTO.setPincodeId(area.getPincode().getId().intValue());
//                    areaDTO.setCode(area.getPincode().getPincode());
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    @Mapping(target = "displayId", source = "id")
//    @Mapping(target = "displayName", source = "name")
//    public abstract AreaDTO domainToDTO(Area domain, @Context CycleAvoidingMappingContext context);

}
