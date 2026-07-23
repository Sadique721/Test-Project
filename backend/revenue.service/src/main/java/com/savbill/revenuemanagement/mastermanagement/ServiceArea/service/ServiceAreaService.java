package com.savbill.revenuemanagement.mastermanagement.ServiceArea.service;


import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.core.service.ExBaseAbstractService;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.model.ServiceAreaDTO;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages.SaveServiceAreaSharedDataMessge;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages.UpdateServiceAreaSharedDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class ServiceAreaService extends ExBaseAbstractService<ServiceAreaDTO, ServiceArea, Long> {


    public ServiceAreaService(JpaRepository<ServiceArea, Long> repository, IBaseMapper<ServiceAreaDTO, ServiceArea> mapper) {
        super(repository, mapper);
    }
    private static Log log = LogFactory.getLog(ServiceAreaService.class);
    @Override
    public String getModuleNameForLog() {
        return "[ServiceAreaServices]";
    }

    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    ServiceAreaPincodeRelRepository serviceAreaPincodeRelRepository;

@Transactional
    public void saveServiceArea(SaveServiceAreaSharedDataMessge messge){
        try {
            ServiceArea serviceArea = new ServiceArea();
            serviceArea.setId(messge.getId());
            serviceArea.setName(messge.getName());
            serviceArea.setStatus(messge.getStatus());
            serviceArea.setLatitude(messge.getLatitude());
            serviceArea.setLongitude(messge.getLongitude());
            serviceArea.setCityid(messge.getCityid());
            serviceArea.setMvnoId(messge.getMvnoId());
            serviceArea.setIsDeleted(messge.getIsDeleted());
            serviceArea.setAreaId(messge.getAreaId());
            serviceArea.setPincodeList(messge.getPincodeList());
            serviceAreaRepository.save(serviceArea);
        }catch (Exception e){
            log.error("Unable to create Sevice With name"+messge.getName()+""+e.getMessage());
        }
//        List<ServiceAreaPincodeRel> serviceAreaPincodeRelList = new ArrayList<>();
//        if(messge.getPincodeList()!=null){
//            for(Pincode message : messge.getPincodeList()){
//                ServiceAreaPincodeRel serviceAreaPincodeRel = new ServiceAreaPincodeRel();
//                serviceAreaPincodeRel.setServiceArea(savedServiceArea);
//                serviceAreaPincodeRel.setPincodeData(message);
//                serviceAreaPincodeRelList.add(serviceAreaPincodeRel);
//            }
//            serviceAreaPincodeRelRepository.saveAll(serviceAreaPincodeRelList);
//        }


    }


@Transactional
    public void updateServiceArea(UpdateServiceAreaSharedDataMessage messge){
    try {
        if(messge.getName()!=null) {
            ServiceArea serviceArea = serviceAreaRepository.findById(messge.getId()).orElse(null);
            if(serviceArea!=null) {
                serviceArea.setName(messge.getName());
                serviceArea.setStatus(messge.getStatus());
                serviceArea.setLatitude(messge.getLatitude());
                serviceArea.setLongitude(messge.getLongitude());
                serviceArea.setCityid(messge.getCityid());
                serviceArea.setMvnoId(messge.getMvnoId());
                serviceArea.setIsDeleted(messge.getIsDeleted());
                serviceArea.setAreaId(messge.getAreaId());
                serviceArea.setPincodeList(messge.getPincodeList());
                serviceAreaRepository.save(serviceArea);
            }else{
                ServiceArea serviceArea1 = new ServiceArea();
                serviceArea1.setId(messge.getId());
                serviceArea1.setName(messge.getName());
                serviceArea1.setStatus(messge.getStatus());
                serviceArea1.setLatitude(messge.getLatitude());
                serviceArea1.setLongitude(messge.getLongitude());
                serviceArea1.setCityid(messge.getCityid());
                serviceArea1.setMvnoId(messge.getMvnoId());
                serviceArea1.setIsDeleted(messge.getIsDeleted());
                serviceArea1.setAreaId(messge.getAreaId());
                serviceArea1.setPincodeList(messge.getPincodeList());
                serviceAreaRepository.save(serviceArea1);
            }
        }
    }catch (Exception e){
        log.error("Unable to Update Service"+e.getMessage());
    }




//        List<ServiceAreaPincodeRel> serviceAreaPincodeRelList = new ArrayList<>();
//        if(messge.getPincodeList()!=null){
//            for(Pincode message : messge.getPincodeList()){
//                ServiceAreaPincodeRel serviceAreaPincodeRel = new ServiceAreaPincodeRel();
//                serviceAreaPincodeRel.setServiceArea(savedServiceArea);
//                serviceAreaPincodeRel.setPincodeData(message);
//                serviceAreaPincodeRelList.add(serviceAreaPincodeRel);
//            }
//            serviceAreaPincodeRelRepository.saveAll(serviceAreaPincodeRelList);
//        }


    }
//
//
//    @Autowired
//    PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;
//    @Autowired
//    PostpaidPlanRepo postpaidPlanRepo;
//
//
//    @PersistenceContext
//    EntityManager entityManager;
//    @Autowired
//    PlanServiceRepository repository;
//    @Autowired
//    private ServiceAreaRepository serviceAreaRepository;
//
//
//    @Autowired
//    private PartnerRepository partnerRepository;
//
//    @Autowired
//    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;
//
//    @Autowired
//    private PincodeRepository pincodeRepository;
//
//    @Autowired
//    MessageSender messageSender;
//    @Autowired
//    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
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
    @Transactional
    public ServiceArea getByID(Long id) {
        Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findById(id);
        if (serviceAreaOptional.isPresent())
            return serviceAreaRepository.findById(id).get();
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
////            if (getLoggedInUserId() != 1) {
////                aBoolean = aBoolean.and(qServiceArea.id.in(super.getServiceAreaIdList()));
////            }
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
//    public List<ServiceArea> serviceAreaIdListWherePartnerIsNotBind(Integer partnerId,String partner_type) {
//
//
//            QServiceArea qServiceArea = QServiceArea.serviceArea;
//            BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false));
//            try {
//
//                if (getLoggedInUserId() != 1) {
//
////        	List<Integer> pids = new ArrayList<Integer>();
//                    QPartner qPartner = QPartner.partner;
//                    BooleanExpression exp = qPartner.isNotNull();
//                    if (partner_type != null)
//                        exp = exp.and(qPartner.partnerType.equalsIgnoreCase(partner_type));
//                    List<Partner> partners = (List<Partner>) partnerRepository.findAll(exp);
//                    List<Integer> pids1 = partners.stream().map(id -> id.getId()).collect(Collectors.toList());
//                    BooleanExpression exp1 = qPartner.isNotNull().and(qPartner.id.in(pids1));
//
//                    if (getMvnoIdFromCurrentStaff() == 1) {
//                        exp1 = exp1.and(qPartner.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//                    }
//                    if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//                        exp1 = exp1.and(qPartner.mvnoId.eq(1).or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));
//                    }
//
//                    List<Partner> pids = (List<Partner>) partnerRepository.findAll(exp1);
//                    List<Integer> ids = pids.stream().map(id -> id.getId()).collect(Collectors.toList());
//
//                    if (partnerId != null && ids != null && ids.size() > 0)
//                        ids = ids.stream().filter(id -> !id.equals(partnerId)).collect(Collectors.toList());
//
//                    if (ids != null && ids.size() > 0) {
//                        List<Long> serviceAreaIds = partnerServiceAreaMappingRepo.serviceAreaIdListWherePartnerIsNotBind(ids);
//                        booleanExpression = booleanExpression.and(qServiceArea.id.notIn(serviceAreaIds));
//                    }
//                }
//                if (getMvnoIdFromCurrentStaff() != 1)
//                    booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                if (getBUIdsFromCurrentStaff().size() != 0)
//                    booleanExpression = booleanExpression
//                            .and(qServiceArea.mvnoId.eq(1)
//                                    .or(qServiceArea.mvnoId.eq(getMvnoIdFromCurrentStaff())));
//                List<ServiceArea> serviceAreaList = (List<ServiceArea>) serviceAreaRepository.findAll(booleanExpression);
//
//                return serviceAreaList.stream().filter(x -> x.getStatus().equalsIgnoreCase("active")).collect(Collectors.toList());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//    }
//
//
//    // Common method for find Service Area Id List Based on StaffId with Iteger
//    public List<Integer> getServiceAreaByStaffId() {
//        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffId(Collections.singletonList(getLoggedInUserId()));
//        List<Integer> result = new ArrayList<>();
//        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
//            result.add(staffUserServiceAreaMappingList.get(i).getServiceId());
//        }
//        return result;
//    }
//
//    // Common method for find Service Area Id List Based on StaffId with Long
//    public List<Long> getServiceAreaByStaffIdLong() {
//        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffId(Collections.singletonList(getLoggedInUserId()));
//        List<Long> result = new ArrayList<>();
//        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
//            result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
//        }
//        return result;
//    }
//
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
////        List<ServiceAreaDTO> serviceAreaList = serviceAreaMapper.domainToDTO(, new CycleAvoidingMappingContext());
//        return serviceAreaDTOS;
//    }
//
//    public List<Pincode> getPincodefromcity(Integer id) {
//        List<Pincode> list = new ArrayList<>();
//        list = pincodeRepository.findallcitybyid(id);
//        return list;
//    }
//
//    public List<PlanService> getAllServicebyServiceAreaId(List<Integer> serviceAreaId) {
//
//        List<PostPaidPlanServiceAreaMapping> postPaidPlanServiceAreaMappings = postPaidPlanServiceAreaMappingRepo.findAllByServiceIdIn(serviceAreaId);
//        List<PlanService> list = new ArrayList<>();
//
//        postPaidPlanServiceAreaMappings.forEach(x -> {
//            Integer planId = x.getPlanId();
//            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(planId);
//            Optional<PlanService> planService = Optional.of(new PlanService());
//            if (postpaidPlan.isPresent()) {
//                planService = repository.findById(postpaidPlan.get().getServiceId());
//            }
//            if (planService.isPresent()) {
//                if (!list.contains(planService.get())) {
//                    list.add(planService.get());
//                }
//            }
//        });
//        List<PlanService> finalList = list.stream().filter(service -> (service.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || service.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) &&
//                (service.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(service.getBuId()))).collect(Collectors.toList());
//        return finalList;
//
//    }
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
}
