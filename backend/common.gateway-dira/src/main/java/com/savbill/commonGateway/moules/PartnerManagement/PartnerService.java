package com.savbill.commonGateway.moules.PartnerManagement;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.SavePartnerSharedDataMessage;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.UpdatePartnerSharedDataMessage;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.repository.CityRepository;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.repository.CountryRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.ServiceAreaService;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.State.repository.StateRepository;
import com.savbill.commonGateway.moules.PartnerServiceAreaMapping.PartnerServiceAreaMapping;
import com.savbill.commonGateway.moules.PartnerServiceAreaMapping.PartnerServiceAreaMappingRepo;
import com.savbill.commonGateway.moules.PartnerServiceAreaMapping.QPartnerServiceAreaMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerService extends ExBaseAbstractService<PartnerPojo, Partner, Integer> {
    public PartnerService(PartnerRepository repository, PartnerMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PartnerService]";
    }

    private static String MODULE = " [PartnerService] ";
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    StaffUserService staffUserService;
    @Autowired
    ServiceAreaService serviceAreaService;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    StateRepository stateRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;
    private static final Logger logger = LoggerFactory.getLogger(PartnerService.class);

    public void savePartnerEntiry(SavePartnerSharedDataMessage message) throws Exception {
        try {
            Partner partner = new Partner();
            partner.setId(message.getId());
            partner.setName(message.getName());
            partner.setStatus(message.getStatus());
            partner.setCity(message.getCity());
            partner.setCountry(message.getCountry());
            partner.setState(message.getState());
            partner.setPincode(message.getPincode());
            partner.setEmail(message.getEmail());
            partner.setPartnerType(message.getPartnerType());
            if (message.getParentPartnerId() != null) {
                Partner parentPartner = partnerRepository.findById(message.getParentPartnerId()).orElse(null);
                partner.setParentPartner(parentPartner);
            }
            List<ServiceArea> serviceAreaList=new ArrayList<>();
            for (ServiceArea area:message.getServiceAreaList())
            {
                serviceAreaList.add(new ServiceArea(area));
            }
            partner.setServiceAreaList(serviceAreaList);
            partner.setIsDelete(message.getIsDelete());
            partner.setCreatedById(message.getCreatedById());
            partner.setLastModifiedById(message.getLastModifiedById());
            partner.setBuId(message.getBuId());
            partner.setMvnoId(message.getMvnoId());
            partner.setBranch(message.getBranch());
            partner.setMobile(message.getMobile());
            Partner savedPartner = partnerRepository.save(partner);
            staffUserService.savePartnerStaff(savedPartner, "add", message.getCreatedById());
            logger.info("Partner created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create partner with name " + message.getName(), e.getMessage());
        }
    }

    public void updatePartnerEntiry(UpdatePartnerSharedDataMessage message) throws Exception {
        try {
            Partner partner = partnerRepository.findById(message.getId()).orElse(null);
            if(partner!=null && message.getIsDelete().equals(true))
            {
                try {
                    deletePartner(partner.getId());
                    Partner partner1 = partnerRepository.findById(message.getId()).orElse(null);
                    staffUserService.savePartnerStaff(partner1, "edit", message.getLastModifiedById());
                }catch (Exception e){throw  new RuntimeException(e);}
            }
            else if (partner != null) {
                partner.setId(message.getId());
                partner.setName(message.getName());
                partner.setStatus(message.getStatus());
                partner.setCity(message.getCity());
                partner.setCountry(message.getCountry());
                partner.setState(message.getState());
                partner.setCreatedById(message.getCreatedById());
                partner.setLastModifiedById(message.getLastModifiedById());
                partner.setPincode(message.getPincode());
                partner.setEmail(message.getEmail());
                partner.setPartnerType(message.getPartnerType());
                if (message.getParentPartnerId() != null) {
                    Partner parentPartner = partnerRepository.findById(message.getParentPartnerId()).orElse(null);
                    partner.setParentPartner(parentPartner);
                }
                partner.setServiceAreaList(message.getServiceAreaList());
                partner.setIsDelete(message.getIsDelete());
                partner.setBuId(message.getBuId());
                partner.setMvnoId(message.getMvnoId());
                partner.setBranch(message.getBranch());
                partner.setMobile(message.getMobile());
                Partner updatedPartner = partnerRepository.save(partner);
                staffUserService.savePartnerStaff(updatedPartner, "edit", message.getLastModifiedById());
            } else {
                Partner partner1 = new Partner();
                partner1.setId(message.getId());
                partner1.setName(message.getName());
                partner1.setStatus(message.getStatus());
                partner1.setCity(message.getCity());
                partner1.setCountry(message.getCountry());
                partner1.setState(message.getState());
                partner1.setPincode(message.getPincode());
                partner1.setEmail(message.getEmail());
                partner1.setPartnerType(message.getPartnerType());
                if (message.getParentPartnerId() != null) {
                    Partner parentPartner = partnerRepository.findById(message.getParentPartnerId()).orElse(null);
                    partner1.setParentPartner(parentPartner);
                }
                partner1.setServiceAreaList(message.getServiceAreaList());
                partner1.setIsDelete(message.getIsDelete());
                partner1.setCreatedById(message.getCreatedById());
                partner1.setLastModifiedById(message.getLastModifiedById());
                partner1.setBuId(message.getBuId());
                partner1.setMvnoId(message.getMvnoId());
                partner1.setBranch(message.getBranch());
                partner1.setMobile(message.getMobile());
                Partner savedPartner = partnerRepository.save(partner1);
                staffUserService.savePartnerStaff(savedPartner, "add", message.getLastModifiedById());
            }
            logger.info("Partner updated successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to update partner with name " + message.getName(), e.getMessage());
        }
    }

    public void deletePartner(Integer id) throws Exception {
        String SUBMODULE = MODULE + " [deletePartner()] ";
        try {

            QPartnerServiceAreaMapping qPartnerServiceAreaMapping=QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            BooleanExpression exp = qPartnerServiceAreaMapping.isNotNull();
            exp=exp.and(qPartnerServiceAreaMapping.partnerId.eq(id));
            List<PartnerServiceAreaMapping> partnerServiceAreaMapping= (List<PartnerServiceAreaMapping>) partnerServiceAreaMappingRepo.findAll(exp);
            partnerServiceAreaMappingRepo.deleteAll(partnerServiceAreaMapping);

            Partner partner = partnerRepository.findById(id).orElse(null);
            partner.setIsDelete(true);
            partner=partnerRepository.save(partner);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<PartnerPojo> getAllActiveEntities() {
        try {
            long start = System.currentTimeMillis();
            Integer mvnoId = getMvnoIdFromCurrentStaff();
            List<Long> buIds = getBUIdsFromCurrentStaff();
            int buListSize = buIds.size();

            List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
            boolean hasServiceAreaIds;
            if(getLoggedInUserPartnerId() == 1){
                hasServiceAreaIds =  false;
            }else {
                hasServiceAreaIds = !serviceAreaIds.isEmpty();
            }

            boolean isLco = getLoggedInUser().getLco() != null && getLoggedInUser().getLco(); // assuming Boolean wrapper

            List<PartnerPojo> partnerList = partnerRepository.findFilteredPartners(
                    mvnoId,
                    buIds,
                    buListSize,
                    serviceAreaIds,
                    hasServiceAreaIds,
                    isLco
            );
            long end = System.currentTimeMillis();
            System.out.println("**** total time for getallEntity ***"+(end-start));
            return partnerList;
//            return convertResponseModelIntoPojo(partnerList);
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }


    public List<PartnerPojo> getAllActiveEntitiesWithoutPartnerType() {
        try {

            Integer mvnoId = getMvnoIdFromCurrentStaff();

            List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
            boolean hasServiceAreaIds = !serviceAreaIds.isEmpty();

            List<PartnerPojo> partnerList = partnerRepository.getAllPartner(
                    mvnoId,
                    serviceAreaIds,
                    hasServiceAreaIds
            );

            return partnerList;
//            return convertResponseModelIntoPojo(partnerList);
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public List<PartnerPojo> convertResponseModelIntoPojo(List<Partner> partnerList) {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<PartnerPojo> pojoListRes = new ArrayList<PartnerPojo>();
        try {
            if (partnerList != null && partnerList.size() > 0) {
                for (Partner partner : partnerList) {
                    pojoListRes.add(convertPartnerModelToPartnerPojo(partner));
                }
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
        return pojoListRes;
    }

    public PartnerPojo convertPartnerModelToPartnerPojo(Partner partner) {
        String SUBMODULE = MODULE + " [convertPartnerModelToPartnerPojo()] ";
        PartnerPojo pojo = null;
        long start = System.currentTimeMillis();
        try {
            if (partner != null) {
                pojo = new PartnerPojo();
                pojo.setId(partner.getId());
                pojo.setName(partner.getName());
                pojo.setStatus(partner.getStatus());
                pojo.setCity(partner.getCity());
                pojo.setState(partner.getState());
                pojo.setCountry(partner.getCountry());
                pojo.setPincode(partner.getPincode());
                pojo.setEmail(partner.getEmail());
                pojo.setIsDelete(partner.getIsDelete());
                pojo.setCreatedById(partner.getCreatedById());
                pojo.setCreatedate(partner.getCreatedate());
                pojo.setCreatedByName(partner.getCreatedByName());
                pojo.setLastModifiedById(partner.getLastModifiedById());
                pojo.setLastModifiedByName(partner.getLastModifiedByName());
                pojo.setUpdatedate(partner.getUpdatedate());
                pojo.setPartnerType(partner.getPartnerType());
                pojo.setDisplayId(partner.getId());
                pojo.setDisplayName(partner.getName());
                pojo.setBranch(partner.getBranch());
                if (partner.getMvnoId() != null) {
                    pojo.setMvnoId(partner.getMvnoId());
                }
                if (null != partner.getServiceAreaList() && 0 < partner.getServiceAreaList().size()) {
                    pojo.setServiceAreaIds(partner.getServiceAreaList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
                    pojo.setServiceAreaNameList(partner.getServiceAreaList());
                }
                if (partner.getParentPartner() != null) {
                    pojo.setParentpartnerid(partner.getParentPartner().getId().longValue());
                } else pojo.setParentPartnerName("-");

                if (null != partner.getCity()) {
                    City city = cityRepository.findById(partner.getCity()).orElse(null);
                    pojo.setCityName(null != city ? city.getName() : "-");
                } else pojo.setCityName("-");
                if (null != partner.getCountry()) {
                    Country country = countryRepository.findById(partner.getCountry()).orElse(null);
                    pojo.setCountryName(null != country ? country.getName() : "-");
                } else pojo.setCountryName("-");
                if (null != partner.getState()) {
                    State state = stateRepository.findById(partner.getState()).orElse(null);
                    pojo.setStateName(null != state ? state.getName() : "-");
                } else pojo.setStateName("-");
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
        long end = System.currentTimeMillis();
        System.out.println("**** total time for COVERPOJO ***"+(end-start));
        return pojo;
    }
}
