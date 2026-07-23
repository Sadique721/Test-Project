package com.savbill.partnermanagement.modules.MasterManagement.Pincode;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public abstract class PincodeMapper implements IBaseMapper<PincodeDTO, Pincode> {
//    String MODULE = " [PincodeMapper] ";
//
//    @Autowired
//    private CityRepository cityRepository;
//
//    @Autowired
//    private CountryRepository countryRepository;
//
//    @Autowired
//    private StateRepository stateRepository;
//
//    @Override
//    @Mapping(target = "id", source = "pincodeid")
//    public abstract Pincode dtoToDomain(PincodeDTO pojo, @Context CycleAvoidingMappingContext context);
//
//    @Mappings({
//        @Mapping(target = "pincodeid", source = "id"),
//        @Mapping(target = "displayId", source = "id"),
//        @Mapping(target = "displayName", source = "pincode")
//    })
//    @Override
//    public abstract PincodeDTO domainToDTO(Pincode domain, @Context CycleAvoidingMappingContext context);
//
//    @AfterMapping
//    void afterMapping(@MappingTarget PincodeDTO pincodeDTO, Pincode pincode) {
//        try {
//            if(pincode!=null){
//                if(pincode.getCityId()!=null){
////                    City city = cityService.get(pincode.getCityId());
//                    City city = cityRepository.findById(pincode.getCityId()).get();
//                    if(city != null) {
//                        pincodeDTO.setCityName(city.getName());
//                    }
//                }
//                if(pincode.getStateId()!=null){
////                    State state = stateService.get(pincode.getStateId());
//                    State state = stateRepository.findById(pincode.getStateId()).get();
//                    if(state != null) {
//                        pincodeDTO.setStateName(state.getName());
//                    }
//                }
//                if(pincode.getCountryId()!=null){
////                    Country country = countryService.get(pincode.getCountryId());
//                    Country country = countryRepository.findById(pincode.getCountryId()).get();
//                    if(country != null) {
//                        pincodeDTO.setCountryName(country.getName());
//                    }
//                }
//                if(pincode.getAreaList().size()>0){
//                    StringBuilder stringBuilder = new StringBuilder("");
//                    pincode.getAreaList().forEach(data->{
//                        if(pincode.getAreaList().indexOf(data)==0){
//                            stringBuilder.append(data.getName());
//                        }
//                        else{
//                            stringBuilder.append(","+data.getName());
//                        }
//                    });
//                    pincodeDTO.setAreas(stringBuilder.toString());
//                }
//            }
//        }
//        catch (Exception ex){
//            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//    }
}
