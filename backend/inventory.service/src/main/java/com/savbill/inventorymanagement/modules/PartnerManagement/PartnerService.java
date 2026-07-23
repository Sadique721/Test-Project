package com.savbill.inventorymanagement.modules.PartnerManagement;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.MasterManagement.City.City;
import com.savbill.inventorymanagement.modules.MasterManagement.City.CityRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.Country;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.CountryRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.MasterManagement.State.State;
import com.savbill.inventorymanagement.modules.MasterManagement.State.StateRepository;
import com.savbill.inventorymanagement.modules.PartnerServiceAreaMapping.PartnerServiceAreaMapping;
import com.savbill.inventorymanagement.modules.PartnerServiceAreaMapping.PartnerServiceAreaMappingRepo;
import com.savbill.inventorymanagement.modules.PartnerServiceAreaMapping.QPartnerServiceAreaMapping;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserService;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SavePartnerSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdatePartnerSharedDataMessage;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
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
    CityRepository cityRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    ServiceAreaService serviceAreaService;
    @Autowired
    PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;
    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger logger = Logger.getLogger(PartnerService.class);

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
            partner.setParentPartner(message.getParentPartnerId());
            partner.setServiceAreaList(message.getServiceAreaList());
            partner.setIsDelete(message.getIsDelete());
            partner.setCreatedById(message.getCreatedById());
            partner.setLastModifiedById(message.getLastModifiedById());
            partner.setBuId(message.getBuId());
            partner.setMvnoId(message.getMvnoId());
            partner.setBranch(message.getBranch());
            partnerRepository.save(partner);
            logger.info("Partner created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create partner with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public void updatePartnerEntiry(UpdatePartnerSharedDataMessage message) throws Exception {
        try {
            Partner partner = partnerRepository.findById(message.getId()).orElse(null);
            if(partner!=null && message.getIsDelete().equals(true))
            {
                try {
                    deletePartner(partner.getId());
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
                partner.setParentPartner(message.getParentPartnerId());
                partner.setServiceAreaList(message.getServiceAreaList());
                partner.setIsDelete(message.getIsDelete());
                partner.setBuId(message.getBuId());
                partner.setMvnoId(message.getMvnoId());
                partner.setBranch(message.getBranch());
                partnerRepository.save(partner);
                logger.info("Partner updated successfully with name " + message.getName());
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
                partner1.setParentPartner(message.getParentPartnerId());
                partner1.setServiceAreaList(message.getServiceAreaList());
                partner1.setIsDelete(message.getIsDelete());
                partner1.setCreatedById(message.getCreatedById());
                partner1.setLastModifiedById(message.getLastModifiedById());
                partner1.setBuId(message.getBuId());
                partner1.setMvnoId(message.getMvnoId());
                partner1.setBranch(message.getBranch());
                partnerRepository.save(partner1);
                logger.info("Partner updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update partner with name " + message.getName() + " , Error: " + e.getMessage());
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
            partnerRepository.save(partner);
            if(partner.getIsDelete().equals(true)){
                List<StaffUser> staffUser = staffUserService.getActiveStaffUserFromUsername(partner.getEmail());
                if(staffUser != null && !staffUser.isEmpty())
                {
                    staffUser.get(0).setIsDelete(true);
                    staffUserRepository.save(staffUser.get(0));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<Partner> getAllTypePartner() {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS));
            if(getMvnoIdFromCurrentStaff() != 1) {
                aBoolean = aBoolean.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            }
            if (!getBUIdsFromCurrentStaff().isEmpty()) {
                aBoolean = aBoolean.and(qPartner.buId.in(getBUIdsFromCurrentStaff()));
            }
//                List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
            List<Integer> serviceIDs = serviceAreaService.getServiceAreaByStaffId();
            if (!serviceIDs.isEmpty()) {
                aBoolean = aBoolean.and(qPartner.id.in(query.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))));
            }

            if(!getLoggedInUser().getLco())
                aBoolean = aBoolean.and(qPartner.partnerType.ne(CommonConstants.PARTNER_TYPE_LCO));

            aBoolean = aBoolean.or(qPartner.id.in(1));
//            List<Partner> partnerList = IterableUtils.toList(partnerRepository.findAll(aBoolean)).stream().filter(partner -> partner.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() &&  (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)).collect(Collectors.toList());
            List<Partner> partnerList = IterableUtils.toList(partnerRepository.findAll(aBoolean));
            return partnerList;
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
    }

    public List<PartnerPojo> getAllActiveEntities() {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS));
            if(getMvnoIdFromCurrentStaff() != 1) {
                aBoolean = aBoolean.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            }
            if (!getBUIdsFromCurrentStaff().isEmpty()) {
                aBoolean = aBoolean.and(qPartner.buId.in(getBUIdsFromCurrentStaff()));
            }
//                List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
            List<Integer> serviceIDs = serviceAreaService.getServiceAreaByStaffId();
            if (!serviceIDs.isEmpty()) {
                aBoolean = aBoolean.and(qPartner.id.in(query.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))));
            }

            if(!getLoggedInUser().getLco())
                aBoolean = aBoolean.and(qPartner.partnerType.ne(CommonConstants.PARTNER_TYPE_LCO));

            aBoolean = aBoolean.or(qPartner.id.in(1));
//            List<Partner> partnerList = IterableUtils.toList(partnerRepository.findAll(aBoolean)).stream().filter(partner -> partner.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() &&  (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)).collect(Collectors.toList());
            //List<Partner> partnerList = IterableUtils.toList(partnerRepository.findAll(aBoolean));
            List<Partner> partnerList = new JPAQuery<>(entityManager)
                    .select(Projections.constructor(Partner.class,
                            qPartner.id, qPartner.name))
                    .from(qPartner)
                    .where(aBoolean)
                    .orderBy(qPartner.id.desc())
                    .fetch();

            return convertResponseModelIntoPojo(partnerList);
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
                    pojo.setParentpartnerid(partner.getParentPartner());
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
        return pojo;
    }
    public GenericDataDTO getPartnerListServiceArea(Integer serviceAreaIds, PaginationRequestDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Partner> partners;
        PageRequest pageRequest = generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), "createdate", requestDTO.getSortOrder());
        List<Integer> partnerIds = partnerServiceAreaMappingRepo.findAllByServiceId(serviceAreaIds).stream().map(PartnerServiceAreaMapping::getPartnerId).collect(Collectors.toList());
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            partners = partnerRepository.findAllByIdInAndStatusAndIsDeleteIsFalse(partnerIds,CommonConstants.ACTIVE_STATUS, pageRequest);
//        } else {
//            partners = partnerRepository.findAllByIdInAndStatusAndIsDeleteIsFalseAndMvnoIdIn(partnerIds,CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
//        }
        QPartner qPartner = QPartner.partner;
        BooleanExpression booleanExpression = qPartner.isDelete.eq(false).and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS)).and(qPartner.id.in(partnerIds));
        if (getMvnoIdFromCurrentStaff() != 1) {
            booleanExpression = booleanExpression.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        }
        if (!getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qPartner.buId.in(getBUIdsFromCurrentStaff()));
        }
        partners = partnerRepository.findAll(booleanExpression, pageRequest);
        if (partners != null && partners.getSize() > 0) {
            makeGenericResponse(genericDataDTO, partners);
        }
        return genericDataDTO;
    }

    public GenericDataDTO searchPartnersByServiceArea(Integer serviceAreaIds, List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("name")) {
                        return getPartnerByName(serviceAreaIds, searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getPartnerByName(Integer serviceAreaIds, String name,PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Partner> partnerPage = null;
        List<Partner> partnerList;
        List<Integer> partnerIds = partnerServiceAreaMappingRepo.findAllByServiceId(serviceAreaIds).stream().map(PartnerServiceAreaMapping::getPartnerId).collect(Collectors.toList());
        if (getMvnoIdFromCurrentStaff() == 1) {
            partnerList = partnerRepository.findAllByIdInAndStatusAndIsDeleteIsFalse(partnerIds,CommonConstants.ACTIVE_STATUS)
                    .stream().filter(partner -> partner.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        } else {
            partnerList = partnerRepository.findAllByIdInAndStatusAndIsDeleteIsFalseAndMvnoIdIn(partnerIds,CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1))
                    .stream().filter(partner -> partner.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        }
        List<Partner> paginatedList = partnerList.stream()
                .skip(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .sorted(Comparator.comparing(Partner::getCreatedate,Comparator.reverseOrder()))
                .collect(Collectors.toList());

        partnerPage = new PageImpl<>(paginatedList, pageRequest, partnerList.size());
        if (partnerList != null && partnerPage.getSize() > 0) {
            makeGenericResponse(genericDataDTO, partnerPage);
        }
        return genericDataDTO;
    }
}
