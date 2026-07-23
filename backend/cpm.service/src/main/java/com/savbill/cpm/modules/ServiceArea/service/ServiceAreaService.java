package com.savbill.cpm.modules.ServiceArea.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.SaveServiceAreaSharedDataMessge;
import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.UpdateServiceAreaSharedDataMessage;
import com.savbill.cpm.constants.cacheKeys;
import com.savbill.cpm.core.dto.LightweightServiceAreaDTO;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.common.StaffUserServiceAreaMapping;
import com.savbill.cpm.model.postpaid.*;
import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.model.postpaid.PlanService;
import com.savbill.cpm.model.postpaid.QPartner;
import com.savbill.cpm.modules.LocationMaster.domain.ServiceAreaLocationMapping;
import com.savbill.cpm.modules.ServiceArea.domain.QServiceArea;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceArea;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaDTO;
import com.savbill.cpm.modules.ServiceArea.repository.ServiceAreaLocationMappingRepository;
import com.savbill.cpm.modules.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import com.savbill.cpm.modules.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.cpm.rabbitMq.message.LocationServiceareaMappingMessage;
import com.savbill.cpm.repository.common.BranchServiceAreaMappingRepository;
import com.savbill.cpm.repository.common.StaffUserServiceAreaMappingRepository;
import com.savbill.cpm.repository.postpaid.*;
import com.savbill.cpm.repository.postpaid.*;
import com.savbill.cpm.service.CacheService;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServiceAreaService extends ExBaseAbstractService<ServiceAreaDTO, ServiceArea, Long> {


    //
    @Autowired
    PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    //
//
//    @PersistenceContext
//    EntityManager entityManager;
    @Autowired
    PlanServiceRepository repository;
    //    @Autowired
//    private ServiceAreaRepository serviceAreaRepository;
//
//
    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    private ServiceAreaLocationMappingRepository serviceAreaLocationMappingRepository;
    //
//    @Autowired
//    private PincodeRepository pincodeRepository;
//
//    @Autowired
//    MessageSender messageSender;
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
//    @Autowired
//    ServiceAreaMapper serviceAreaMapper;
//
//    @Autowired
//    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
//
//    @Autowired
//    private BranchRepository branchRepository;
//
//    @Autowired
//    private InventoryMappingRepo inventoryMappingRepo;
//
//    @Autowired
//    private PlanRepository planRepository;
//
//    public ServiceAreaService(ServiceAreaRepository repository, ServiceAreaMapper mapper) {
//        super(repository, mapper);
//        sortColMap.put("id", "service_area_id");
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[ServiceAreaServices]";
//    }
//


    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public ServiceArea getByID(Long id) {
        Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findById(id);
        if (serviceAreaOptional.isPresent())
            return  serviceAreaOptional.get();
        return null;
    }

    public ServiceArea get(Long id) {
        String cacheKey = cacheKeys.SERVICEAREA + id;
        ServiceArea serviceArea = null;

        try {
            // Try to get from cache
            serviceArea = (ServiceArea) cacheService.getFromCache(cacheKey, ServiceArea.class);

            if (serviceArea != null) {
                log.info("ServiceArea from cache ::::::::::::::: " + serviceArea.getId() + " ::::: Name :::::: " + serviceArea.getName());
                return serviceArea;
            }

            // If not in cache, fetch from DB
            Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findById(id);
            if (serviceAreaOptional.isPresent()) {
                serviceArea = serviceAreaOptional.get();
                log.info("ServiceArea from DB ::::::::::::::: " + serviceArea.getId() + " ::::: Name :::::: " + serviceArea.getName());

                // Put in cache
                cacheService.putInCacheWithExpire(cacheKey, serviceArea);
                return serviceArea;
            }
        } catch (Exception e) {
            log.error("Error while fetching ServiceArea: ", e);
        }

        return null;
    }

//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    public ServiceArea findByName(String serviceName) {
//        return serviceAreaRepository.findByName(serviceName);
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("ServiceArea");
//        createExcel(workbook, sheet, ServiceAreaDTO.class, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{ServiceAreaDTO.class.getDeclaredField("id"), ServiceAreaDTO.class.getDeclaredField("name"), ServiceAreaDTO.class.getDeclaredField("status"),};
//    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = serviceAreaRepository.duplicateVerifyAtSave(name);
//            else
//                count = serviceAreaRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    @Override
//    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = serviceAreaRepository.duplicateVerifyAtEdit(name, id);
//            else
//                count = serviceAreaRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        createPDF(doc, ServiceAreaDTO.class, getFields());
//    }
//
//    public GenericDataDTO getAreaByName(String name, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getAreaByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            QServiceArea qServiceArea = QServiceArea.serviceArea;
//            BooleanExpression booleanExpression = qServiceArea.isNotNull()
//                    .and(qServiceArea.isDeleted.eq(false))
//                    .and(qServiceArea.name.containsIgnoreCase(name))
//                    .or(qServiceArea.latitude.containsIgnoreCase(name))
//                    .or(qServiceArea.longitude.containsIgnoreCase(name))
//                    .or(qServiceArea.status.equalsIgnoreCase(name));
////            if (getLoggedInUserId() != 1) {
////                booleanExpression = booleanExpression.and(qServiceArea.id.in(super.getServiceAreaIdList()));
////            }
//            if (getMvnoIdFromCurrentStaff() != 1)
//                booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            Page<ServiceArea> serviceAreaList = serviceAreaRepository.findAll(booleanExpression, pageRequest);
//            if (0 < serviceAreaList.getSize()) {
//                makeGenericResponse(genericDataDTO, serviceAreaList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getAreaByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return null;
//    }
//
//    public List<ServiceAreaDTO> getAllServiceAreaForCaseReasonConfig(Long caseReasonId) {
//        String SUBMODULE = getModuleNameForLog() + " [getAllServiceAreaForCaseReasonConfig()] ";
//        try {
//            List<ServiceArea> serviceAreaList = new ArrayList<>();
//            serviceAreaList = serviceAreaRepository.findAllServiceArea(caseReasonId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (null != serviceAreaList && 0 < serviceAreaList.size()) {
//                return serviceAreaList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//            }
//            return new ArrayList<>();
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        QBranch qBranch = QBranch.branch;
//        QPlan qPlan = QPlan.plan;
//        QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
//        QPostPaidPlanServiceAreaMapping qPostPaidPlanServiceAreaMapping = QPostPaidPlanServiceAreaMapping.postPaidPlanServiceAreaMapping;
//        BooleanExpression booleanExpression = qBranchServiceAreaMapping.isNotNull().and(qBranchServiceAreaMapping.serviceareaId.eq(id));
//        BooleanExpression booleanExpression2 = qPostPaidPlanServiceAreaMapping.isNotNull().and(qPostPaidPlanServiceAreaMapping.serviceId.eq(id));
//        List<BranchServiceAreaMapping> branchServiceAreaMapping = IterableUtils.toList(branchServiceAreaMappingRepository.findAll(booleanExpression));
//        List<PostPaidPlanServiceAreaMapping> mapping = IterableUtils.toList(postPaidPlanServiceAreaMappingRepo.findAll(booleanExpression2));
//        BooleanExpression booleanExpression1 = qBranch.isNotNull();
//        List<Branch> branches = new ArrayList<>();
//        if (branchServiceAreaMapping.size() > 0) {
//            booleanExpression1 = booleanExpression1.and(qBranch.isDeleted.isFalse()).and(qBranch.id.eq(branchServiceAreaMapping.get(0).getBranchId().longValue()));
//            branches.addAll(IterableUtils.toList(branchRepository.findAll(booleanExpression1)));
//        }
//        List<Plan> plans = new ArrayList<>();
//        if (mapping.size() > 0) {
//            BooleanExpression booleanExpression3 = qPlan.isNotNull().and(qPlan.id.eq(mapping.get(0).getPlanId()));
//            plans.addAll(IterableUtils.toList(planRepository.findAll(booleanExpression3)));
//        }
//        boolean flag = false;
//        if (branches.size() == 0 && plans.size() == 0 && mapping.size() == 0) {
//            flag = true;
//        }
//        return flag;
//    }
//
//    @Override
//    public List<ServiceAreaDTO> getAllEntities() {
//        try {
//            QServiceArea qServiceArea = QServiceArea.serviceArea;
//            BooleanExpression aBoolean = qServiceArea.isNotNull();
//            aBoolean = aBoolean.and(qServiceArea.isDeleted.eq(false).and(qServiceArea.status.equalsIgnoreCase("Active")));

    /// /            if (getLoggedInUserId() != 1) {
    /// /                aBoolean = aBoolean.and(qServiceArea.id.in(super.getServiceAreaIdList()));
    /// /            }
//            if (getMvnoIdFromCurrentStaff() != 1)
//                aBoolean = aBoolean.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            List<ServiceArea> serviceAreas = IterableUtils.toList(serviceAreaRepository.findAll(aBoolean));
//            return serviceAreas.stream().map(data -> super.getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    @Override
//    public ServiceAreaDTO saveEntity(ServiceAreaDTO entity) throws Exception {
//        entity.setMvnoId(getMvnoIdFromCurrentStaff());
//        ServiceAreaDTO serviceAreaDTO = super.saveEntity(entity);
//        ServiceAreaMesseage serviceAreaMesseage = new ServiceAreaMesseage(serviceAreaDTO);
//        messageSender.send(serviceAreaMesseage, RabbitMqConstants.QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS, RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS_KPI);
//        ServiceAreaIn serviceAreaIn = new ServiceAreaIn(serviceAreaDTO);
//        messageSender.send(serviceAreaIn, RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS);
//
//        return serviceAreaDTO;
//    }
//
//    @Override
//    public ServiceAreaDTO updateEntity(ServiceAreaDTO entity) throws Exception {
//        entity.setMvnoId(getMvnoIdFromCurrentStaff());
//        ServiceAreaDTO serviceAreaDTO = super.updateEntity(entity);
//        ServiceAreaMesseage serviceAreaMesseage = new ServiceAreaMesseage(serviceAreaDTO);
//        messageSender.send(serviceAreaMesseage, RabbitMqConstants.QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS, RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS_KPI);
//        return serviceAreaDTO;
//    }
//
//    @Override
//    public void deleteEntity(ServiceAreaDTO entity) throws Exception {
//        super.deleteEntity(entity);
//        entity.setIsDeleted(true);
//        ServiceAreaDTO serviceAreaDTO = super.updateEntity(entity);
//        ServiceAreaMesseage serviceAreaMesseage = new ServiceAreaMesseage(entity);
//        messageSender.send(serviceAreaMesseage, RabbitMqConstants.QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS, RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS_KPI);
//    }
//
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<ServiceArea> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
//        if (getMvnoIdFromCurrentStaff() == 1)
//            paginationList = serviceAreaRepository.findAll(pageRequest);
//        else
//            paginationList = serviceAreaRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//
    public List<LightweightServiceAreaDTO> serviceAreaIdListWherePartnerIsNotBind(Integer partnerId, String partner_type) {
        QServiceArea qServiceArea = QServiceArea.serviceArea;
        BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false)).and(qServiceArea.status.equalsIgnoreCase("active"));
        try {
            QPartner qPartner = QPartner.partner;
            BooleanExpression exp = qPartner.isNotNull();
            if (partner_type != null)
                exp = exp.and(qPartner.partnerType.equalsIgnoreCase(partner_type));
            List<Partner> partners = (List<Partner>) partnerRepository.findAll(exp);
            List<Integer> pids1 = partners.stream().map(id -> id.getId()).collect(Collectors.toList());
            BooleanExpression exp1 = qPartner.isNotNull().and(qPartner.id.in(pids1));

            if (getMvnoIdFromCurrentStaff() == 1)
                exp1 = exp1.and(qPartner.mvnoId.in(1, getMvnoIdFromCurrentStaff()));

            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
                exp1 = exp1.and(qPartner.mvnoId.eq(1).or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));

            List<Partner> pids = (List<Partner>) partnerRepository.findAll(exp1);
            List<Integer> ids = pids.stream().map(id -> id.getId()).collect(Collectors.toList());

            if (partnerId != null && ids != null && ids.size() > 0)
                ids = ids.stream().filter(id -> !id.equals(partnerId)).collect(Collectors.toList());

            if (ids != null && ids.size() > 0) {
                List<Long> partnerServiceAreaIds = partnerServiceAreaMappingRepo.serviceAreaIdListWherePartnerIsNotBind(ids);
                booleanExpression = booleanExpression.and(qServiceArea.id.notIn(partnerServiceAreaIds));
            }

            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));

            if (getBUIdsFromCurrentStaff().size() != 0)
                booleanExpression = booleanExpression.and(qServiceArea.mvnoId.eq(1).or(qServiceArea.mvnoId.eq(getMvnoIdFromCurrentStaff())));

            JPAQuery<LightweightServiceAreaDTO> query = new JPAQuery<>(entityManager);
            List<LightweightServiceAreaDTO> serviceAreaList = query
                    .select(Projections.constructor(LightweightServiceAreaDTO.class,
                            qServiceArea.id,
                            qServiceArea.name,
                            qServiceArea.mvnoId,
                            qServiceArea.status))
                    .from(qServiceArea)
                    .where(booleanExpression)
                    .fetch();
            List<Integer> branchServiceAreaIds = branchServiceAreaMappingRepository.serviceAreaIdListWhereBranchIsBind();

            if (branchServiceAreaIds != null && !branchServiceAreaIds.isEmpty())
                serviceAreaList = serviceAreaList.stream().filter(x -> !branchServiceAreaIds.contains(x.getId().intValue())).collect(Collectors.toList());

            List<Integer> currentLoggedInUserServiceAreaIdList = getLoggedInUser().getServiceAreaIdList();
            if (currentLoggedInUserServiceAreaIdList != null && !currentLoggedInUserServiceAreaIdList.isEmpty())
                serviceAreaList = serviceAreaList.stream().filter(x -> currentLoggedInUserServiceAreaIdList.contains(x.getId().intValue())).collect(Collectors.toList());

            //return serviceAreaList.stream().filter(x -> x.getStatus().equalsIgnoreCase("active")).collect(Collectors.toList());
            return serviceAreaList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<LightweightServiceAreaDTO> serviceAreaIdListWherePartnerIsNotBindOptimized(Integer partnerId, String partnerType) {
        QServiceArea qServiceArea = QServiceArea.serviceArea;
        QPartner qPartner = QPartner.partner;
        try {
            BooleanExpression serviceAreaExpr = qServiceArea.isDeleted.eq(false)
                    .and(qServiceArea.status.equalsIgnoreCase("active"));

            BooleanExpression partnerExpr = qPartner.isDelete.isFalse();
            if (partnerType != null && !partnerType.isEmpty()) {
                partnerExpr = partnerExpr.and(qPartner.partnerType.equalsIgnoreCase(partnerType));
            }

            if (getMvnoIdFromCurrentStaff() == 1) {
                partnerExpr = partnerExpr.and(qPartner.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
            }

            List<Long> buIds = getBUIdsFromCurrentStaff();
            if (buIds != null && !buIds.isEmpty()) {
                partnerExpr = partnerExpr.and(
                        qPartner.mvnoId.eq(1)
                                .or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff())
                                        .and(qPartner.buId.in(buIds)))
                );
            }

            List<Integer> partnerIds = new JPAQuery<>(entityManager)
                    .select(qPartner.id)
                    .from(qPartner)
                    .where(partnerExpr)
                    .fetch();

            if (partnerId != null) {
                partnerIds.remove(partnerId);
            }

            if (partnerIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<Long> partnerServiceAreaIds = partnerServiceAreaMappingRepo
                    .serviceAreaIdListWherePartnerIsNotBind(partnerIds);

            if (partnerServiceAreaIds != null && !partnerServiceAreaIds.isEmpty()) {
                serviceAreaExpr = serviceAreaExpr.and(qServiceArea.id.notIn(partnerServiceAreaIds));
            }

            Integer mvnoId = getMvnoIdFromCurrentStaff();
            if (mvnoId != 1) {
                serviceAreaExpr = serviceAreaExpr.and(qServiceArea.mvnoId.in(mvnoId, 1));
            }

            if (buIds != null && !buIds.isEmpty()) {
                serviceAreaExpr = serviceAreaExpr.and(
                        qServiceArea.mvnoId.eq(1)
                                .or(qServiceArea.mvnoId.eq(mvnoId))
                );
            }

            JPAQuery<LightweightServiceAreaDTO> query = new JPAQuery<>(entityManager);
            List<LightweightServiceAreaDTO> serviceAreaList = query
                    .select(Projections.constructor(LightweightServiceAreaDTO.class,
                            qServiceArea.id,
                            qServiceArea.name,
                            qServiceArea.mvnoId,
                            qServiceArea.status))
                    .from(qServiceArea)
                    .where(serviceAreaExpr)
                    .fetch();
            List<Integer> branchServiceAreaIds = branchServiceAreaMappingRepository.serviceAreaIdListWhereBranchIsBind();

            if (branchServiceAreaIds != null && !branchServiceAreaIds.isEmpty()) {
                Set<Integer> branchServiceAreaIdSet = new HashSet<>(branchServiceAreaIds);
                List<LightweightServiceAreaDTO> filteredList = new ArrayList<>();
                for (LightweightServiceAreaDTO dto : serviceAreaList) {
                    if (!branchServiceAreaIdSet.contains(dto.getId().intValue())) {
                        filteredList.add(dto);
                    }
                }
                serviceAreaList = filteredList;
            }

            List<Integer> currentLoggedInUserServiceAreaIdList = getLoggedInUser().getServiceAreaIdList();
            if (currentLoggedInUserServiceAreaIdList != null && !currentLoggedInUserServiceAreaIdList.isEmpty())
                serviceAreaList = serviceAreaList.stream().filter(x -> currentLoggedInUserServiceAreaIdList.contains(x.getId().intValue())).collect(Collectors.toList());

            return serviceAreaList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public List<ServiceArea> serviceAreaIdListByPartnerTypeIsBind(String partnerType) {
        QServiceArea qServiceArea = QServiceArea.serviceArea;
        BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false));
        try {
            if (getLoggedInUserId() != 1) {
                QPartner qPartner = QPartner.partner;
                BooleanExpression exp = qPartner.isNotNull();
                if (partnerType != null)
                    exp = exp.and(qPartner.partnerType.equalsIgnoreCase(partnerType));

                if (getMvnoIdFromCurrentStaff() == 1)
                    exp = exp.and(qPartner.mvnoId.in(1, getMvnoIdFromCurrentStaff()));

                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
                    exp = exp.and(qPartner.mvnoId.eq(1).or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));

                List<Partner> pids = (List<Partner>) partnerRepository.findAll(exp);
                List<Integer> ids = pids.stream().map(id -> id.getId()).collect(Collectors.toList());


                if (ids != null && ids.size() > 0) {
                    List<Long> serviceAreaIds = partnerServiceAreaMappingRepo.serviceAreaIdListWherePartnerIsNotBind(ids);
                    booleanExpression = booleanExpression.and(qServiceArea.id.in(serviceAreaIds));
                }
            }

            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            if (getBUIdsFromCurrentStaff().size() != 0)
                booleanExpression = booleanExpression.and(qServiceArea.mvnoId.eq(1).or(qServiceArea.mvnoId.eq(getMvnoIdFromCurrentStaff())));

            List<ServiceArea> serviceAreaList = (List<ServiceArea>) serviceAreaRepository.findAll(booleanExpression);
            return serviceAreaList.stream().filter(x -> x.getStatus().equalsIgnoreCase("active")).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getServiceAreaByStaffId() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
            result.add(staffUserServiceAreaMappingList.get(i).getServiceId());
        }
        return result;
    }

    //
//    // Common method for find Service Area Id List Based on StaffId with Long
    public List<Long> getServiceAreaByStaffIdLong() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
            result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
        }
        return result;
    }

//    // Get All Service Area List By UserStaff
//    public List<ServiceAreaDTO> getAllServiceAreaByStaffId() {
//
//        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffId(Collections.singletonList(getLoggedInUserId()));
//        List<Long> result = new ArrayList<>();
//        if(staffUserServiceAreaMappingList.size() != 0) {
//            for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
//                result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
//            }
//        } else {
//            List <ServiceArea> serviceArea = serviceAreaRepository.findAll();
//            if(serviceArea.size() != 0) {
//                for (int i = 0; i < serviceArea.size(); i++) {
//                    result.add(serviceArea.get(i).getId());
//                }
//            }
//        }
//        List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByIdInAndStatusAndIsDeletedIsFalse(result, CommonConstants.ACTIVE_STATUS);
//        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
//        for(ServiceArea serviceArea : serviceAreaList){
//            ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext());
//            serviceAreaDTOS.add(serviceAreaDTO);
//        }

    /// /        List<ServiceAreaDTO> serviceAreaList = serviceAreaMapper.domainToDTO(, new CycleAvoidingMappingContext());
//        return serviceAreaDTOS;
//    }
//
//    public List<Pincode> getPincodefromcity(Integer id) {
//        List<Pincode> list = new ArrayList<>();
//        list = pincodeRepository.findallcitybyid(id);
//        return list;
//    }
//
    public List<PlanService> getAllServicebyServiceAreaId(List<Integer> serviceAreaId, boolean isCwsc) {
        if (isCwsc) {
            List<String> siteName = serviceAreaRepository.findSiteNameByServiceAreaId(serviceAreaId.stream().map(Integer::longValue).collect(Collectors.toList()));
            for (String siteNames : siteName) {
                if (siteNames != null) {
                    serviceAreaId = serviceAreaRepository.findServiceAreaIdsFromSiteName(siteName).stream().map(Long::intValue).collect(Collectors.toList());
                } else {
                    serviceAreaId = new ArrayList<>(serviceAreaId);
                }
            }
        }



        List<PlanService> list = repository.findAllByServiceAreaId(serviceAreaId);
        if (!isCwsc) {
            List<PlanService> finalList = list.stream().filter(service -> (service.getMvnoId() != null && (service.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || service.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) &&
                    (service.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(service.getBuId())))).collect(Collectors.toList());
            return finalList;
        } else {
            List<PlanService> finalList = list.stream().collect(Collectors.toList());
            return finalList;
        }


    }
//
//    public List<ServiceArea> serviceAreaIdListWhereBranchIsNotBind() {
//        QServiceArea qServiceArea = QServiceArea.serviceArea;
//        BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false).and(qServiceArea.status.equalsIgnoreCase("ACTIVE")));
//        booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//        List<Integer> ids = branchServiceAreaMappingRepository.serviceAreaIdListWhereBranchIsNotBind();
//        List<Long> serviceareaids = ids.stream().map(integer -> integer.longValue()).collect(Collectors.toList());
//        booleanExpression = booleanExpression.and(qServiceArea.id.notIn(serviceareaids));
//        return (List<ServiceArea>) serviceAreaRepository.findAll(booleanExpression);
//    }
//
//    //Validate Inventory Assign to Service Area at Delete
//    public void validateServiceAreaInventory(ServiceAreaDTO entityDto) {
//        QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
//        BooleanExpression booleanExpression = qInventoryMapping.isDeleted.eq(false).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Pending").or(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve"))).and(qInventoryMapping.ownerId.eq(entityDto.getId())).and(qInventoryMapping.ownerType.equalsIgnoreCase(CommonConstants.SERVICE_AREA));
//        List<InventoryMapping> inventoryMappings = IterableUtils.toList(inventoryMappingRepo.findAll(booleanExpression));
//        if (inventoryMappings.size() != 0) {
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Do not delete service area due to inventory assigned to service area", null);
//        }
//    }


    /* Getting data from the CommonService */


    public ServiceAreaService(JpaRepository<ServiceArea, Long> repository, IBaseMapper<ServiceAreaDTO, ServiceArea> mapper) {
        super(repository, mapper);
    }

    //    private static Log log = LogFactory.getLog(ServiceAreaService.class);
    @Override
    public String getModuleNameForLog() {
        return "[ServiceAreaServices]";
    }

    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    CacheService cacheService;

    @Autowired
    ServiceAreaPincodeRelRepository serviceAreaPincodeRelRepository;

    @Transactional
    public void saveServiceArea(SaveServiceAreaSharedDataMessge message) {
        try {
            ServiceArea serviceArea = new ServiceArea();
            serviceArea.setId(message.getId());
            serviceArea.setName(message.getName());
            serviceArea.setStatus(message.getStatus());
            serviceArea.setIsDeleted(message.getIsDeleted());
            serviceArea.setMvnoId(message.getMvnoId());
            serviceArea.setLatitude(message.getLatitude());
            serviceArea.setLongitude(message.getLongitude());
            serviceArea.setPincodeList(message.getPincodeList());
            serviceArea.setCityid(message.getCityid());
            serviceArea.setAreaId(message.getAreaId());
            serviceArea.setCreatedById(message.getCreatedById());
            serviceArea.setCreatedByName(message.getCreatedByName());
            serviceArea.setLastModifiedByName(message.getLastModifiedByName());
            serviceArea.setSiteName(message.getSiteName());
            ServiceArea savedServiceArea = serviceAreaRepository.save(serviceArea);
            if (message.getLocationIdList() != null && !message.getLocationIdList().isEmpty()) {
                for (Long locationId : message.getLocationIdList()) {
                    if (locationId != null) {
                        if (!serviceAreaLocationMappingRepository.existsByServiceAreaIdAndLocationId(savedServiceArea.getId(), locationId)) {
                            ServiceAreaLocationMapping serviceAreaLocationMapping = new ServiceAreaLocationMapping();
                            serviceAreaLocationMapping.setServiceAreaId(savedServiceArea.getId());
                            serviceAreaLocationMapping.setLocationId(locationId);
                            serviceAreaLocationMappingRepository.save(serviceAreaLocationMapping);
                        }
                    }
                }
            }
            if (message.getStaffSAMap() == true) {
                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = new ArrayList<>();
                StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                staffUserServiceAreaMapping.setServiceId(serviceArea.getId().intValue());
                staffUserServiceAreaMapping.setStaffId(message.getCreatedById());
                staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping);
                if (message.getCreatedById() != 1) {
                    StaffUserServiceAreaMapping staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
                    staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
                    staffUserServiceAreaMapping1.setServiceId(message.getId().intValue());
                    staffUserServiceAreaMapping1.setStaffId(1);
                    staffUserServiceAreaMapping1.setCreatedOn(LocalDateTime.now());
                    staffUserServiceAreaMapping1.setLastmodifiedOn(LocalDateTime.now());
                    staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping1);
                }
                staffUserServiceAreaMappingRepository.saveAll(staffUserServiceAreaMappingList);
                log.info("Staff User Service Area details save successfully with  service area name " + message.getName());
            }
        } catch (Exception e) {
            log.error("Unable to create Sevice With name" + message.getName() + "" + e.getMessage());
        }
    }


    @Transactional
    public void updateServiceArea(UpdateServiceAreaSharedDataMessage messge) {
        try {
            if (messge.getName() != null) {
                ServiceArea serviceArea = serviceAreaRepository.findById(messge.getId()).orElse(null);
                if (serviceArea != null) {
                    serviceArea.setName(messge.getName());
                    serviceArea.setStatus(messge.getStatus());
                    serviceArea.setLatitude(messge.getLatitude());
                    serviceArea.setLongitude(messge.getLongitude());
                    serviceArea.setCityid(messge.getCityid());
                    serviceArea.setMvnoId(messge.getMvnoId());
                    serviceArea.setIsDeleted(messge.getIsDeleted());
                    serviceArea.setAreaId(messge.getAreaId());
                    serviceArea.setPincodeList(messge.getPincodeList());
                    serviceArea.setCreatedByName(messge.getCreatedByName());
                    serviceArea.setLastModifiedByName(messge.getLastModifiedByName());
                    serviceArea.setSiteName(messge.getSiteName());
                    serviceAreaRepository.save(serviceArea);
                } else {
                    ServiceArea serviceArea1 = new ServiceArea();
                    serviceArea1.setId(messge.getId());
                    serviceArea1.setName(messge.getName());
                    serviceArea1.setStatus(messge.getStatus());
                    serviceArea1.setIsDeleted(messge.getIsDeleted());
                    serviceArea1.setMvnoId(messge.getMvnoId());
                    serviceArea1.setLatitude(messge.getLatitude());
                    serviceArea1.setLongitude(messge.getLongitude());
                    serviceArea1.setPincodeList(messge.getPincodeList());
                    serviceArea1.setCityid(messge.getCityid());
                    serviceArea1.setAreaId(messge.getAreaId());
                    serviceArea1.setLastModifiedById(messge.getUpdatedById());
                    serviceArea1.setCreatedByName(messge.getCreatedByName());
                    serviceArea1.setLastModifiedByName(messge.getLastModifiedByName());
                    serviceArea1.setSiteName(messge.getSiteName());
                    serviceAreaRepository.save(serviceArea1);
                }
            }
        } catch (Exception e) {
            log.error("Unable to Update Service" + e.getMessage());
        }
    }

    @Transactional
    public void saveServiceAreaLocationMapping(List<LocationServiceareaMappingMessage> message) {
        try {
            if (message != null && !message.isEmpty()) {
                Set<Long> serviceAreaIds = new HashSet<>();
                for (LocationServiceareaMappingMessage messages : message) {
                    if (messages.getServiceAreaId() != null) {
                        serviceAreaIds.add(messages.getServiceAreaId());
                    }
                }
                for (Long serviceAreaId : serviceAreaIds) {
                    serviceAreaLocationMappingRepository.deleteByServiceAreaId(serviceAreaId);
                }

                for (LocationServiceareaMappingMessage messages : message) {
                    if (messages.getServiceAreaId() != null && messages.getLocationId() != null) {
                        if (!serviceAreaLocationMappingRepository.existsByServiceAreaIdAndLocationId(messages.getServiceAreaId(), messages.getLocationId())) {
                            ServiceAreaLocationMapping serviceAreaLocationMapping = new ServiceAreaLocationMapping();
                            serviceAreaLocationMapping.setServiceAreaId(messages.getServiceAreaId());
                            serviceAreaLocationMapping.setLocationId(messages.getLocationId());
                            serviceAreaLocationMappingRepository.save(serviceAreaLocationMapping);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public List<ServiceAreaDTO> getAllRemainingPlansForServiceArea(Integer serviceAreaId) {
        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();

        try {
            List<Object[]> objects = serviceAreaRepository.findRemainingPlansForServiceArea(serviceAreaId, getLoggedInUser().getMvnoId());

            for (Object[] obj : objects) {
                ServiceAreaDTO dto = new ServiceAreaDTO();

                // Set displayId and displayName
                dto.setDisplayId(obj[0] != null ? Long.valueOf(obj[0].toString()) : null);
                dto.setDisplayName(obj[1] != null ? obj[1].toString() : null);

                serviceAreaDTOS.add(dto);
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Something went wrong with fetching remaining plan details for service area id: "
                    + serviceAreaId + ", error: " + e.getMessage(), e);
        }

        return serviceAreaDTOS;
    }

    public List<StaffUserServiceAreaMapping> assignStaffToServiceArea(List<StaffUserServiceAreaMapping> mappingList) {
        if (mappingList == null || mappingList.isEmpty()) {
            log.warn("No staff-user-service area mappings received to assign.");
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        for (StaffUserServiceAreaMapping mapping : mappingList) {
            if (mapping.getCreatedOn() == null) {
                mapping.setCreatedOn(now);
            }
            if (mapping.getLastmodifiedOn() == null) {
                mapping.setLastmodifiedOn(now);
            }
        }
        List<StaffUserServiceAreaMapping> savedMappings = staffUserServiceAreaMappingRepository.saveAll(mappingList);
        log.info("Saved {} staff-service area mappings received via Kafka.", savedMappings.size());

        return savedMappings;
    }

}
