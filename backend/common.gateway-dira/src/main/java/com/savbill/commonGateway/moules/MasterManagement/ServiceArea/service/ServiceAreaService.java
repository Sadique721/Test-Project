package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service;

import com.querydsl.jpa.JPAExpressions;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.ServiceareaLocationMappingMessage;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.CommonUtils;
import com.savbill.commonGateway.common.domain.ClientService;
import com.savbill.commonGateway.common.repository.ClientServiceRepository;
import com.savbill.commonGateway.constants.AclConstants;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.fileUtillity.FileUtility;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.kafka.service.GsonConfig;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.Branch;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.BranchServiceAreaMapping;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.QBranch;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.QBranchServiceAreaMapping;
import com.savbill.commonGateway.moules.MasterManagement.Branch.repository.BranchRepository;
import com.savbill.commonGateway.moules.MasterManagement.Branch.repository.BranchServiceAreaMappingRepository;
import com.savbill.commonGateway.moules.MasterManagement.LocationMaster.ServiceAreaLocationMappingRepository;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.repository.PincodeRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.*;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.*;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.mapper.ServiceAreaMapper;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaCommonDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTOProjection;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.PolyGoneRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.PlanServiceAreaBindingCheckMessage;
import com.savbill.commonGateway.rabbitmq.messages.ServiceAreaMesseage;
import com.savbill.commonGateway.rabbitmq.messages.ServiceareaMessage;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceAreaService extends ExBaseAbstractService<ServiceAreaDTO, ServiceArea, Long> {


//    @Autowired
//    PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;
//    @Autowired
//    PostpaidPlanRepo postpaidPlanRepo;


    @PersistenceContext
    EntityManager entityManager;

//    @Autowired
//    PlanServiceRepository repository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private PincodeRepository pincodeRepository;
    //
    @Autowired
    MessageSender messageSender;
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
    //
    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    PolyGoneRepository polyGoneRepository;

    @Autowired
    private FileUtility fileUtility;


    @Autowired
    private CreateDataSharedService createDataSharedService;


    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private ClientServiceRepository entityRepository;

    @Autowired
    private ServiceAreaLocationMappingRepository serviceAreaLocationMappingRepository;

    @Autowired
    private ServiceAreaPincodeRelRepository serviceAreaPincodeRelRepository;

    @Autowired
    KafkaProducer<String, Object> kafkaProducer;

    public ServiceAreaService(ServiceAreaRepository repository, ServiceAreaMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "service_area_id");
    }

    @Override
    public String getModuleNameForLog() {
        return "[ServiceAreaServices]";
    }

    public ServiceArea getByID(Long id) {
        Optional<ServiceArea> serviceAreaOptional = serviceAreaRepository.findById(id);
        if (serviceAreaOptional.isPresent())
            return serviceAreaRepository.findById(id).get();
        return null;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
    public ServiceArea findByName(String serviceName) {
        return serviceAreaRepository.findByName(serviceName);
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("ServiceArea");
        createExcel(workbook, sheet, ServiceAreaDTO.class, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{ServiceAreaDTO.class.getDeclaredField("id"), ServiceAreaDTO.class.getDeclaredField("name"), ServiceAreaDTO.class.getDeclaredField("status"),};
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = serviceAreaRepository.duplicateVerifyAtSave(name);
            else
                count = serviceAreaRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = serviceAreaRepository.duplicateVerifyAtEdit(name, id);
            else
                count = serviceAreaRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, ServiceAreaDTO.class, getFields());
    }

    public GenericDataDTO getAreaByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getAreaByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.name.containsIgnoreCase(name));
            booleanExpression = booleanExpression.or(qServiceArea.latitude.containsIgnoreCase(name))
                    .or(qServiceArea.longitude.containsIgnoreCase(name))
                    .or(qServiceArea.status.equalsIgnoreCase(name));
//            if (getLoggedInUserId() != 1) {
//                booleanExpression = booleanExpression.and(qServiceArea.id.in(super.getServiceAreaIdList()));
//            }
            booleanExpression = booleanExpression.and(qServiceArea.isDeleted.eq(false));
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));

            Page<ServiceArea> serviceAreaList = serviceAreaRepository.findAll(booleanExpression, pageRequest);
            if (0 < serviceAreaList.getSize()) {
                serviceAreaList.stream().forEach(x -> {
                    DecimalFormat df = new DecimalFormat("0.0000000");

                    if (x.getLatitude() != null && !x.getLatitude().isEmpty() && isNumeric(x.getLatitude()))
                        x.setLatitude(df.format(Double.parseDouble(x.getLatitude())));

                    if (x.getLongitude() != null && !x.getLongitude().isEmpty() && isNumeric(x.getLongitude()))
                        x.setLongitude(df.format(Double.parseDouble(x.getLongitude())));
                });
                makeGenericResponse(genericDataDTO, serviceAreaList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getAreaByName(searchModel.getFilterValue(), pageRequest);
                    }
                }

            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public List<ServiceAreaDTO> getAllServiceAreaForCaseReasonConfig(Long caseReasonId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllServiceAreaForCaseReasonConfig()] ";
        try {
            List<ServiceArea> serviceAreaList = new ArrayList<>();
            serviceAreaList = serviceAreaRepository.findAllServiceArea(caseReasonId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != serviceAreaList && 0 < serviceAreaList.size()) {
                return serviceAreaList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        QBranch qBranch = QBranch.branch;
        QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
        BooleanExpression booleanExpression = qBranchServiceAreaMapping.isNotNull().and(qBranchServiceAreaMapping.serviceareaId.eq(id));
        List<BranchServiceAreaMapping> branchServiceAreaMapping = IterableUtils.toList(branchServiceAreaMappingRepository.findAll(booleanExpression));

        BooleanExpression booleanExpression1 = qBranch.isNotNull();
        List<Branch> branches = new ArrayList<>();
        if (branchServiceAreaMapping.size() > 0) {
            booleanExpression1 = booleanExpression1.and(qBranch.isDeleted.isFalse()).and(qBranch.id.eq(branchServiceAreaMapping.get(0).getBranchId().longValue()));
            branches.addAll(IterableUtils.toList(branchRepository.findAll(booleanExpression1)));
        }
        ServiceArea serviceArea = serviceAreaRepository.findById(id.longValue()).orElse(null);
//
        boolean flag = false;
        if (branches.size() == 0) {
            flag = true;
        }
        if (serviceArea.getIsBindWithPlan() != null) {
            if (serviceArea.getIsBindWithPlan().equals(true)) {
                flag = false;
            }
        }

        return flag;
    }

    @Override
    public List<ServiceAreaDTO> getAllEntities() {
        try {
            List<Integer> serviceareaIds = getServiceAreaByStaffId();
            List<ServiceArea> serviceAreaList = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
            } else {
                if (serviceareaIds.isEmpty()) {
                    serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                    serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), longServiceAreas);
                }
            }
            return serviceAreaList.stream().map(data -> super.getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<ServiceAreaDTO> getAllEntitiesForCafCustomer() {
        try {
            ClientService clientService = getByName(CommonConstants.IS_USED_UNDERDEVELOPMENT_SERVICEAREA);
            List<ServiceArea> serviceAreaList = new ArrayList<>();
            List<Integer> serviceareaIds = getServiceAreaByStaffId();
            if ((null != clientService) && (null != clientService.getName()) && (clientService.getValue().equals("1"))) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusIn(Arrays.asList(CommonConstants.UNDERDEVELOPMENT_STATUS, CommonConstants.ACTIVE_STATUS));
                } else {
                    if (serviceareaIds.isEmpty()) {
                        serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusInAndMvnoIdIn(Arrays.asList(CommonConstants.UNDERDEVELOPMENT_STATUS, CommonConstants.ACTIVE_STATUS), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                        serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusInAndMvnoIdInAndIdIn(Arrays.asList(CommonConstants.UNDERDEVELOPMENT_STATUS, CommonConstants.ACTIVE_STATUS), Arrays.asList(getMvnoIdFromCurrentStaff(), 1), longServiceAreas);
                    }
                }
            } else {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
                } else {
                    if (serviceareaIds.isEmpty()) {
                        serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                        serviceAreaList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), longServiceAreas);
                    }
                }
            }
            return serviceAreaList.stream().map(data -> super.getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<LightServiceAreaDTO> getAllEntitiesForCafCustomerDropdown() {
        try {
            long startTime = System.currentTimeMillis();
            ClientService clientService = getByName(CommonConstants.IS_USED_UNDERDEVELOPMENT_SERVICEAREA);
            List<Integer> serviceAreaIds = getServiceAreaByStaffId();
            List<Long> longServiceAreas = serviceAreaIds.stream().map(Integer::longValue).collect(Collectors.toList());
            boolean isMvnoAdmin = getMvnoIdFromCurrentStaff() == 1;
            boolean isUnderDevelopment = clientService != null && "1".equals(clientService.getValue());

            List<String> statuses = isUnderDevelopment
                    ? Arrays.asList(CommonConstants.UNDERDEVELOPMENT_STATUS, CommonConstants.ACTIVE_STATUS)
                    : Collections.singletonList(CommonConstants.ACTIVE_STATUS);

            List<LightServiceAreaDTO> serviceAreaList;
            List<Integer> mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);

            if (isMvnoAdmin) {
                serviceAreaList = isUnderDevelopment
                        ? serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusIn(statuses)
                        : serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
            } else {
                serviceAreaList = serviceAreaIds.isEmpty()
                        ? (isUnderDevelopment
                        ? serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusInAndMvnoIdIn(statuses, mvnoIds)
                        : serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, mvnoIds))
                        : (isUnderDevelopment
                        ? serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusInAndMvnoIdInAndIdIn(statuses, mvnoIds, longServiceAreas)
                        : serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(CommonConstants.ACTIVE_STATUS, mvnoIds, longServiceAreas));
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Execution time: " + (endTime - startTime) + " ms");
            return serviceAreaList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public ClientService getByName(String name) {
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        return entityRepository.findByNameContainingIgnoreCaseAndMvnoIdEquals(name, mvnoId);
    }

    @Override
    public ServiceAreaDTO saveEntity(ServiceAreaDTO entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        ServiceAreaDTO serviceAreaDTO = super.saveEntity(entity);
        ServiceAreaMesseage serviceAreaMesseage = new ServiceAreaMesseage(serviceAreaDTO);
        //messageSender.send(serviceAreaMesseage, RabbitMqConstants.QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS);
        kafkaMessageSender.send(new KafkaMessageData(serviceAreaMesseage, serviceAreaMesseage.getClass().getSimpleName()));
        //ServiceAreaIn serviceAreaIn = new ServiceAreaIn(serviceAreaDTO);
        //messageSender.send(serviceAreaIn, RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS);

        return serviceAreaDTO;
    }

    @Override
    public ServiceAreaDTO updateEntity(ServiceAreaDTO entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        ServiceAreaDTO serviceAreaDTO = super.updateEntity(entity);
        ServiceAreaMesseage serviceAreaMesseage = new ServiceAreaMesseage(serviceAreaDTO);
        //messageSender.send(serviceAreaMesseage, RabbitMqConstants.QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS);
        kafkaMessageSender.send(new KafkaMessageData(serviceAreaMesseage, serviceAreaMesseage.getClass().getSimpleName()));
        return serviceAreaDTO;
    }


    @Override
    public void deleteEntity(ServiceAreaDTO entity) throws Exception {
        List<PolyGone> polyGoneList = polyGoneRepository.findAllByServiceAreaIdAndMvnoid(entity.getId().intValue(), entity.getMvnoId());
        entity.setPolyGoneList(polyGoneList);
        super.deleteEntity(entity);
        entity.setIsDeleted(true);
        serviceAreaRepository.save(serviceAreaMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
        ServiceAreaDTO serviceAreaDTO = super.updateEntity(entity);
        ServiceAreaMesseage serviceAreaMesseage = new ServiceAreaMesseage(entity);
        //messageSender.send(serviceAreaMesseage, RabbitMqConstants.QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS);
        kafkaMessageSender.send(new KafkaMessageData(serviceAreaMesseage, serviceAreaMesseage.getClass().getSimpleName()));
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<ServiceArea> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = serviceAreaRepository.findAll(pageRequest);
        else
            paginationList = serviceAreaRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            paginationList.stream().forEach(x -> {
                DecimalFormat df = new DecimalFormat("0.0000000");

                if (x.getLatitude() != null && !x.getLatitude().isEmpty() && isNumeric(x.getLatitude()))
                    x.setLatitude(df.format(Double.parseDouble(x.getLatitude())));

                if (x.getLongitude() != null && !x.getLongitude().isEmpty() && isNumeric(x.getLongitude()))
                    x.setLongitude(df.format(Double.parseDouble(x.getLongitude())));
            });
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

//    public List<ServiceArea> serviceAreaIdListWherePartnerIsNotBind(Integer partnerId,String partner_type) {
//
//
//            QServiceArea qServiceArea = QServiceArea.serviceArea;
//            BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false));
//            try {
//
//                if (getLoggedInUserId() != 1) {
//

    /// /        	List<Integer> pids = new ArrayList<Integer>();
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


    // Common method for find Service Area Id List Based on StaffId with Iteger
    public List<Integer> getServiceAreaByStaffId() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffId(getLoggedInUserId());
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
            result.add(staffUserServiceAreaMappingList.get(i).getServiceId());
        }
        return result;
    }

    // Common method for find Service Area Id List Based on StaffId with Long
//    public List<Long> getServiceAreaByStaffIdLong() {
//        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffId(Collections.singletonList(getLoggedInUserId()));
//        List<Long> result = new ArrayList<>();
//        for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
//            result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
//        }
//        return result;
//    }

    // Get All Service Area List By UserStaff
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
    public List<Pincode> getPincodefromcity(Integer id) {
        List<Pincode> list = new ArrayList<>();
        list = pincodeRepository.findallcitybyid(id);
        return list;
    }

    public List<Map<String, Object>> getpincodefromcityWithSpecificParameter(Integer id) {
        List<Object[]> results = pincodeRepository.findPincodeByCityId(id);

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("pincode", row[1]);
            responseList.add(map);
        }
        return responseList;
    }

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

    public List<ServiceAreaDTOProjection> serviceAreaIdListWhereBranchIsNotBind() {
        List<Integer> serviceareaIds = getServiceAreaByStaffId();
        QServiceArea qServiceArea = QServiceArea.serviceArea;
        ClientService clientService = getByName(CommonConstants.IS_USED_UNDERDEVELOPMENT_SERVICEAREA);
        BooleanExpression booleanExpression;
        if ((null != clientService) && (null != clientService.getName()) && (clientService.getValue().equals("1"))) {
            BooleanExpression statusCondition = qServiceArea.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)
                    .or(qServiceArea.status.equalsIgnoreCase(CommonConstants.UNDERDEVELOPMENT_STATUS));
            booleanExpression = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false).and(statusCondition));
        } else {
            booleanExpression = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false).and(qServiceArea.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)));
        }
        if (getMvnoIdFromCurrentStaff() == 1)
            booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        else {
            if (!serviceareaIds.isEmpty()) {
                List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                booleanExpression = booleanExpression.and(qServiceArea.id.in(longServiceAreas));
            }else{
                booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(),1));
                booleanExpression = booleanExpression.and(qServiceArea.locations.isEmpty().or(qServiceArea.locations.isNotEmpty()));
            }
        }
//        long startTime = System.currentTimeMillis();
//        List<Integer> ids = branchServiceAreaMappingRepository.serviceAreaIdListWhereBranchIsNotBind();
//        long endTime = System.currentTimeMillis();
//        long totalTime = endTime - startTime;
//        System.out.println("======================================Total Time : "+totalTime+"================================================");
//        List<Long> serviceareaids = ids.stream().map(integer -> integer.longValue()).collect(Collectors.toList());
//        booleanExpression = booleanExpression.and(qServiceArea.id.notIn(serviceareaids));
//        List<ServiceArea> serviceAreasList= (List<ServiceArea>) serviceAreaRepository.findAll(booleanExpression);

        QBranchServiceAreaMapping qRel = QBranchServiceAreaMapping.branchServiceAreaMapping;
        BooleanExpression notBoundExpression = qServiceArea.id.notIn(
                JPAExpressions.select(qRel.serviceareaId.castToNum(Long.class))
                        .from(qRel)
        );


        booleanExpression = booleanExpression.and(notBoundExpression);

//        List<ServiceArea> serviceAreasList = query.select(
//                        Projections.constructor(ServiceArea.class, qServiceArea.id, qServiceArea.name, qServiceArea.status, qServiceArea.mvnoId, qServiceArea.latitude, qServiceArea.longitude, qServiceArea.serviceAreaType)
//                )
//                .from(qServiceArea)
//                .where(booleanExpression)
//                .fetch();

    //   List<ServiceAreaDTO> serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceAreasList, new CycleAvoidingMappingContext());

        JPAQuery<ServiceArea> query = new JPAQuery<>(entityManager);
        List<ServiceAreaDTOProjection> serviceAreaDTOs = query.select(
                        Projections.fields(ServiceAreaDTOProjection.class,
                                qServiceArea.id,
                                qServiceArea.name,
                                qServiceArea.status,
                                qServiceArea.mvnoId,
                                qServiceArea.latitude,
                                qServiceArea.longitude,
                                qServiceArea.serviceAreaType)
                )
                .from(qServiceArea)
                .where(booleanExpression)
                .fetch();
        return serviceAreaDTOs;
    }

    //Validate Inventory Assign to Service Area at Delete
//    public void validateServiceAreaInventory(ServiceAreaDTO entityDto) {
//        QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
//        BooleanExpression booleanExpression = qInventoryMapping.isDeleted.eq(false).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Pending").or(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve"))).and(qInventoryMapping.ownerId.eq(entityDto.getId())).and(qInventoryMapping.ownerType.equalsIgnoreCase(CommonConstants.SERVICE_AREA));
//        List<InventoryMapping> inventoryMappings = IterableUtils.toList(inventoryMappingRepo.findAll(booleanExpression));
//        if (inventoryMappings.size() != 0) {
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Do not delete service area due to inventory assigned to service area", null);
//        }
//    }

    public void updateServiceAreaFlafForPlanBinding(PlanServiceAreaBindingCheckMessage message) {
        List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByIdIn(message.getServicAreaIds());
        if (serviceAreaList.size() > 0) {
            for (ServiceArea serviceArea : serviceAreaList) {
                serviceArea.setIsBindWithPlan(true);
                serviceAreaRepository.save(serviceArea);
            }
        }
    }

    public void updateServiceAreaFlafForPlanBindingAtDelte(PlanServiceAreaBindingCheckMessage message) {
        List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByIdIn(message.getServicAreaIds());
        if (serviceAreaList.size() > 0) {
            for (ServiceArea serviceArea : serviceAreaList) {
                serviceArea.setIsBindWithPlan(false);
                serviceAreaRepository.save(serviceArea);
            }
        }
    }

    public List<ServiceAreaDTO> getAllServiceAreaByStaffId() {

        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
        List<Long> result = new ArrayList<>();
        if (!staffUserServiceAreaMappingList.isEmpty()) {
            for (int i = 0; i < staffUserServiceAreaMappingList.size(); i++) {
                result.add(Long.valueOf(staffUserServiceAreaMappingList.get(i).getServiceId()));
            }
        } else {
            List<ServiceArea> serviceArea = serviceAreaRepository.getAllLightServiceAreaFromIds();
            if (!serviceArea.isEmpty()) {
                for (int i = 0; i < serviceArea.size(); i++) {
                    result.add(serviceArea.get(i).getId());
                }
            }
        }
        List<ServiceArea> serviceAreaList = new ArrayList<>();
        if (getMvnoIdFromCurrentStaff() == 1) {
            serviceAreaList = serviceAreaRepository.findLightServiceAreas(result, CommonConstants.ACTIVE_STATUS);
        } else {
            serviceAreaList = serviceAreaRepository.findLightServiceAreasWithMvnoIds(result, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        }
        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
        for (ServiceArea serviceArea : serviceAreaList) {
            ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext());
            serviceAreaDTOS.add(serviceAreaDTO);
        }

        serviceAreaDTOS.stream().forEach(x -> {
            DecimalFormat df = new DecimalFormat("0.0000000");

            if (x.getLatitude() != null && !x.getLatitude().isEmpty() && isNumeric(x.getLatitude()))
                x.setLatitude(df.format(Double.parseDouble(x.getLatitude())));

            if (x.getLongitude() != null && !x.getLongitude().isEmpty() && isNumeric(x.getLongitude()))
                x.setLongitude(df.format(Double.parseDouble(x.getLongitude())));
        });
        serviceAreaDTOS.sort(Comparator.comparing(ServiceAreaDTO::getId).reversed());
        return serviceAreaDTOS;
    }


    public List<ServiceAreaDTO> getServiceAreaIdByLatAndLong(Double lat, Double longt, Integer mvnoId) {
        List<ServiceAreaDTO> serviceAreaList = new ArrayList<>();
        if (mvnoId == null || mvnoId == 1) {
            serviceAreaList = serviceAreaRepository.findAllByLatitudeAndLongitude();//(List<ServiceArea>) serviceAreaRepository.findAll(expression);
        } else {
            serviceAreaList = serviceAreaRepository.findAllByLatitudeAndLongitudeAndMvno(mvnoId);
        }
        if (!CollectionUtils.isEmpty(serviceAreaList)) {
            List<ServiceAreaDTO> list = serviceAreaList.stream().filter(serviceArea -> isCustomerWithinRadius(lat, longt, serviceArea)).collect(Collectors.toList());
            return list;
        }
        return new ArrayList<>();
    }

    public boolean isCustomerWithinRadius(double customerLatitude, double customerLongitude, ServiceAreaDTO serviceArea) {
        try {
            double distance = CommonUtils.calculateDistance(Double.valueOf(serviceArea.getLatitude()), Double.valueOf(serviceArea.getLongitude()), customerLatitude, customerLongitude);
            serviceArea.setRadiusDis(distance);
            return distance <= Double.valueOf(serviceArea.getRadius());
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isSiteNameExists(String siteName, Integer mvnoId) {
        return serviceAreaRepository.existsBySiteNameAndMvnoId(siteName, mvnoId);
    }

    public List<PolyGone> getPolygoneFromSitename(String siteName) {
        return polyGoneRepository.findAllByServiceAreaIdSiteName(siteName);
    }

    public List<PolyGone> uploadPolygonCordinate(MultipartFile file, Long serviceAreaId) throws IOException {
        Optional<ServiceArea> serviceArea = serviceAreaRepository.findById(serviceAreaId);
        if (!serviceArea.isPresent()) {
            throw new RuntimeException("Given Service Area Not Available..!");
        }
        String fileType = file.getContentType();
        List<PolyGone> polyGoneList = new ArrayList<>();
        switch (fileType) {
            case "text/csv":
                System.out.println("Csv type file");
                polyGoneList = fileUtility.readCsv(PolyGone.class, file.getInputStream());
                break;

            case "text/kml":
                System.out.println("Kml type file");
                polyGoneList = fileUtility.readKml(fileUtility.convertMultipartFileToFile(file));
                break;

            case "text/json":
                System.out.println("json type file");
                polyGoneList = fileUtility.readJeoJson(fileUtility.convertMultipartFileToFile(file));
                break;
            default:
                throw new RuntimeException("Invalid file format");
        }
        if (CollectionUtils.isEmpty(polyGoneList)) {
            throw new RuntimeException("Unable to read Polygon Coordinates..!");
        }
        List<PolyGone> oldPolygone = polyGoneRepository.findAllByServiceAreaIdSiteName(serviceArea.get().getSiteName());
        polyGoneRepository.deleteInBatch(oldPolygone);
        final Integer[] order = {1};
        polyGoneList.forEach(polyGone -> {
                    polyGone.setServiceAreaId(serviceArea.get().getId().intValue());
                    polyGone.setMvnoid(serviceArea.get().getMvnoId());
                    polyGone.setServiceAreaType(serviceArea.get().getServiceAreaType());
                    polyGone.setPolyOrder(order[0]);
                    order[0]++;
                }
        );
        return polyGoneRepository.saveAll(polyGoneList);
    }

    public String downloadCsvFile(String siteName) {
        List<PolyGone> oldPolygone = polyGoneRepository.findAllByServiceAreaIdSiteName(siteName);
        if (CollectionUtils.isEmpty(oldPolygone)) {
            throw new RuntimeException("No Polygon found for SiteName: " + siteName);
        }

//        FileUtility.export(oldPolygone, siteName+".csv");
        return fileUtility.generateCsv(oldPolygone);
    }


    public void sendServiceAreaToOtherMicroserviceWhenSave(ServiceAreaDTO serviceArea, boolean staffServiceMap) {
        //send message
        ServiceareaMessage serviceareaMessage = new ServiceareaMessage();
        serviceareaMessage.setId(serviceArea.getId());
        serviceareaMessage.setName(serviceArea.getName());
        serviceareaMessage.setStatus(serviceArea.getStatus());
        serviceareaMessage.setIsDeleted(serviceArea.getIsDeleted());
        serviceareaMessage.setMvnoId(serviceArea.getMvnoId());
        serviceareaMessage.setLatitude(serviceArea.getLatitude());
        serviceareaMessage.setLongitude(serviceArea.getLongitude());
        serviceareaMessage.setAreaId(serviceArea.getAreaid());
        serviceareaMessage.setSiteName(serviceArea.getSiteName());
        kafkaMessageSender.send(new KafkaMessageData(serviceareaMessage, serviceareaMessage.getClass().getSimpleName()));
        saveServiceAreaLocationMapping(serviceArea);
        ServiceArea serviceAreaEntity = new ServiceArea();
        serviceAreaEntity = serviceAreaMapper.dtoToDomain(serviceArea, new CycleAvoidingMappingContext());
        serviceAreaEntity.setLocationIdList(serviceArea.getLocationIds());
        createDataSharedService.sendCreatedServiceAreaData(serviceAreaEntity, staffServiceMap);
    }

    public void saveServiceAreaLocationMapping(ServiceAreaDTO serviceArea) {
        List<ServiceareaLocationMappingMessage> locationMappingMessages = new ArrayList<>();
        if (serviceArea.getLocationIds() != null && !serviceArea.getLocationIds().isEmpty()) {  // Ensure there are locationIds to process
            for (Long locationId : serviceArea.getLocationIds()) {  // Loop over each locationId
                ServiceAreaLocationMapping serviceAreaLocationMapping = new ServiceAreaLocationMapping();  // Create a new instance for each location
                serviceAreaLocationMapping.setServiceAreaId(serviceArea.getId());  // Set the service area ID
                serviceAreaLocationMapping.setLocationId(locationId);  // Set the single location ID as a list
                serviceAreaLocationMappingRepository.save(serviceAreaLocationMapping);
            }
        }
    }


    public void sendServiceAreaToAllMicroServiceWhenUpdate(ServiceAreaDTO serviceArea) {
        if (serviceArea != null) {
            //send message
            ServiceareaMessage serviceAreaMessage = new ServiceareaMessage();
            serviceAreaMessage.setId(serviceArea.getId());
            serviceAreaMessage.setName(serviceArea.getName());
            serviceAreaMessage.setStatus(serviceArea.getStatus());
            serviceAreaMessage.setIsDeleted(serviceArea.getIsDeleted());
            serviceAreaMessage.setMvnoId(serviceArea.getMvnoId());
            serviceAreaMessage.setLatitude(serviceArea.getLatitude());
            serviceAreaMessage.setLongitude(serviceArea.getLongitude());
            serviceAreaMessage.setAreaId(serviceArea.getAreaid());
            serviceAreaMessage.setSiteName(serviceArea.getSiteName());
            //this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA); pending via kafka
            kafkaMessageSender.send(new KafkaMessageData(serviceAreaMessage, serviceAreaMessage.getClass().getSimpleName()));
            //this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA); pending via kafka
//            kafkaMessageSender.send(new KafkaMessageData(serviceAreaMessage,serviceAreaMessage.getClass().getSimpleName()));
            ServiceArea updatedServiceArea = serviceAreaMapper.dtoToDomain(serviceArea, new CycleAvoidingMappingContext());
            createDataSharedService.updateEntityDataForAllMicroService(updatedServiceArea);

//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_EDIT, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
        }
    }

    public void sendServiceAreaToAllMicroServiceWhenDelted(ServiceAreaDTO serviceArea) {
        if (serviceArea != null) {
            //send message
            ServiceareaMessage serviceAreaMessage = new ServiceareaMessage();
            serviceAreaMessage.setId(serviceArea.getId());
            serviceAreaMessage.setName(serviceArea.getName());
            serviceAreaMessage.setStatus(serviceArea.getStatus());
            serviceAreaMessage.setIsDeleted(true);
            serviceAreaMessage.setMvnoId(serviceArea.getMvnoId());
            serviceAreaMessage.setLatitude(serviceArea.getLatitude());
            serviceAreaMessage.setLongitude(serviceArea.getLongitude());
            serviceAreaMessage.setAreaId(serviceArea.getAreaid());
            //this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA); pending via kafka
            kafkaMessageSender.send(new KafkaMessageData(serviceAreaMessage, serviceAreaMessage.getClass().getSimpleName()));
            //this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA); pending via kafka
//            kafkaMessageSender.send(new KafkaMessageData(serviceAreaMessage,serviceAreaMessage.getClass().getSimpleName()));
            ServiceArea deletedServiceArea = serviceAreaMapper.dtoToDomain(serviceArea, new CycleAvoidingMappingContext());
            createDataSharedService.deleteEntityDataForAllMicroService(deletedServiceArea);
            List<PolyGone> existingPolygonList = polyGoneRepository.findAllByServiceAreaIdAndMvnoid(serviceArea.getId().intValue(), serviceArea.getMvnoId());
            if (!existingPolygonList.isEmpty()) {
                polyGoneRepository.deleteInBatch(existingPolygonList);
            }
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_DELETE, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
        }
    }

    public void updateServiceAreaBasedOnMvnoListUpdate(List<Integer> deletedList, List<Integer> createdList, String siteName, ServiceAreaDTO serviceAreaDTOCreateRef) {
        if (deletedList != null && !deletedList.isEmpty()) {
            List<ServiceArea> deletedServiceAreaList = serviceAreaRepository.findAllBySiteNameAndMvnoIdInAndIsDeletedFalse(siteName, deletedList);
            for (ServiceArea serviceArea : deletedServiceAreaList) {
                serviceArea.setIsDeleted(true);
                serviceAreaRepository.save(serviceArea);
                sendServiceAreaToAllMicroServiceWhenDelted(serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext()));
            }
        }
        if (!createdList.isEmpty()) {
            for (Integer id : createdList) {
                ServiceArea updatedServiceArea = new ServiceArea();
                String mvnoName = mvnoRepository.findMvnoNameById(id.longValue());
                String serviceAreaName = serviceAreaDTOCreateRef.getName() + "_" + mvnoName;
                ServiceAreaDTO newMvnoServiceArea = new ServiceAreaDTO(serviceAreaDTOCreateRef);
                newMvnoServiceArea.setId(null);
                newMvnoServiceArea.setPolyGoneList(null);
                newMvnoServiceArea.setName(serviceAreaName);
                newMvnoServiceArea.setMvnoId(id.intValue());
                updatedServiceArea = serviceAreaRepository.save(serviceAreaMapper.dtoToDomain(newMvnoServiceArea, new CycleAvoidingMappingContext()));
                sendServiceAreaToOtherMicroserviceWhenSave(serviceAreaMapper.domainToDTO(updatedServiceArea, new CycleAvoidingMappingContext()), false);
            }
        }
    }

    public void updateServiceArea(List<Integer> mvnoList, String siteName, ServiceAreaDTO serviceAreaDTOCreateRef) {
        if (!mvnoList.isEmpty()) {
            List<ServiceArea> existingServiceAreas = serviceAreaRepository.findAllBySiteNameAndMvnoIdInAndIsDeletedFalse(siteName, mvnoList);

            existingServiceAreas.forEach(serviceArea -> {
                String mvnoName = mvnoRepository.findMvnoNameById(serviceArea.getMvnoId().longValue());
                String updatedServiceAreaName = serviceAreaDTOCreateRef.getName() + "_" + mvnoName;

                serviceArea.setName(updatedServiceAreaName);
                serviceArea.setStatus(serviceAreaDTOCreateRef.getStatus());
                //serviceArea.setPolyGoneList(serviceAreaDTOCreateRef.getPolyGoneList());
            });

            serviceAreaRepository.saveAll(existingServiceAreas);

            existingServiceAreas.forEach(serviceArea ->
                    sendServiceAreaToOtherMicroserviceWhenSave(
                            serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext()),
                            false
                    )
            );
        }
    }


    public ServiceAreaDTO setBranchIdInServiceAreaDTO(ServiceAreaDTO serviceAreaDTO) {
        BranchServiceAreaMapping branchServiceAreaMapping = branchServiceAreaMappingRepository.findBranchServiceAreaMappingByServiceareaId(serviceAreaDTO.getId().intValue());
        if (branchServiceAreaMapping != null) {
            serviceAreaDTO.setBranchId(branchServiceAreaMapping.getBranchId());
        }
        return serviceAreaDTO;


    }

    public List<ServiceAreaLocationMapping> getLocationByServiceArea(Long id) {
        List<ServiceAreaLocationMapping> list = new ArrayList<>();
        list = serviceAreaLocationMappingRepository.findLocationIdsByServiceAreaId(id);
        return list;
    }

    public GenericDataDTO getAllEntitiesWithPagination(PaginationRequestDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());

            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
                genericDataDTO = getListByPageAndSizeAndSortByAndOrderByWithAssignServiceAreaCheck(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters());

            else
                genericDataDTO = searchWithAssignServiceAreaCheck(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder());


            if (null != genericDataDTO && genericDataDTO.getDataList() != null && !genericDataDTO.getDataList().isEmpty()) {
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No records found.");
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
            }

            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public GenericDataDTO getAllEntitiesWithPaginationActiveAndUd(PaginationRequestDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());

            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
                genericDataDTO = getListByPageAndSizeAndSortByAndOrderByWithAssignServiceAreaCheckActiveAndUd(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters());

            else
                genericDataDTO = searchWithAssignServiceAreaCheck(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder());


            if (null != genericDataDTO && genericDataDTO.getDataList() != null && !genericDataDTO.getDataList().isEmpty()) {
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No records found.");
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
            }

            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }
    public GenericDataDTO getAllEntitiesWithPaginationDynamicStatus(PaginationRequestDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());

            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
                genericDataDTO = getListByPageAndSizeAndSortByAndOrderByWithAssignServiceAreaCheckActiveAndUd(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters());

            else
                genericDataDTO = searchWithAssignServiceAreaStatusCheck(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder());


            if (null != genericDataDTO && genericDataDTO.getDataList() != null && !genericDataDTO.getDataList().isEmpty()) {
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No records found.");
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
            }

            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderByWithAssignServiceAreaCheck(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<ServiceArea> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        List<Integer> serviceareaIds = getServiceAreaByStaffId();
        if (getMvnoIdFromCurrentStaff() == 1) {
            paginationList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatus(pageRequest, CommonConstants.ACTIVE_STATUS);
        }
        else {
            if (serviceareaIds.isEmpty()) {
                paginationList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(pageRequest, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            } else {
                List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                paginationList = serviceAreaRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(pageRequest, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), longServiceAreas);
            }
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            paginationList.stream().forEach(x -> {
                DecimalFormat df = new DecimalFormat("0.0000000");

                if (x.getLatitude() != null && !x.getLatitude().isEmpty() && isNumeric(x.getLatitude()))
                    x.setLatitude(df.format(Double.parseDouble(x.getLatitude())));

                if (x.getLongitude() != null && !x.getLongitude().isEmpty() && isNumeric(x.getLongitude()))
                    x.setLongitude(df.format(Double.parseDouble(x.getLongitude())));
            });
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }


    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderByWithAssignServiceAreaCheckActiveAndUd(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = super.generatePageRequest(page,size, "createdate", sortOrder);
        QServiceArea qServiceArea = QServiceArea.serviceArea;
        BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false));
        try {
            List<Integer> serviceareaIds = getServiceAreaByStaffId();
            Integer currentMvnoId = getMvnoIdFromCurrentStaff();
            List<Integer> serviceAreaIdsInt = getServiceAreaByStaffId();
            List<Long> serviceAreaIds = serviceAreaIdsInt.stream().map(Integer::longValue).collect(Collectors.toList());
            Page<ServiceAreaCommonDTO> pageResult;
            List<String> activeStatuses = Arrays.asList(CommonConstants.ACTIVE_STATUS, CommonConstants.UNDERDEVELOPMENT_STATUS);
            if (currentMvnoId == 1) {
                pageResult = serviceAreaRepository.findAllDTOByStatuses(activeStatuses, pageRequest);
            } else {
                List<Integer> mvnoIds = Arrays.asList(currentMvnoId, 1);
                if (serviceAreaIds.isEmpty()) {
                    pageResult = serviceAreaRepository.findAllDTOByStatusesAndMvnoIds(activeStatuses, mvnoIds, pageRequest);
                } else {
                    List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                    pageResult = serviceAreaRepository.findAllDTOByStatusesMvnoIdsAndIds(activeStatuses, mvnoIds, serviceAreaIds, pageRequest);
                }
            }
            makeGenericResponseFromDTO(genericDataDTO, pageResult);
        } catch (Exception e) {
            ApplicationLogger.logger.error("error while fetching service areas; error-Message: {};", e.getMessage());
            throw e;
        }
        return genericDataDTO;
    }



    public GenericDataDTO searchWithAssignServiceAreaCheck(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getAreaByNameWithAssignServiceAreaCheck(searchModel.getFilterValue(), pageRequest);
                    }
                }

            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public GenericDataDTO searchWithAssignServiceAreaStatusCheck(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            String searchValue = null;
            String statusValue = "Active";
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn() != null) {
                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                            searchValue = searchModel.getFilterValue();
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.STATUS)) {
                            statusValue = searchModel.getFilterValue();  // usually "Active"
                        }
                    }
                }

            }
            return getActiveServiceAreaWithStatusSearch(searchValue, statusValue, pageRequest);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }
    public GenericDataDTO getActiveServiceAreaWithStatusSearch(String searchValue, String statusValue, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getAreaByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            List<Integer> serviceareaIds = getServiceAreaByStaffId();
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            // Start with base predicate: not deleted and status match
            BooleanExpression booleanExpression = qServiceArea.isDeleted.eq(false)
                    .and(qServiceArea.status.equalsIgnoreCase(statusValue));

            // If searchValue is not empty, add OR conditions on multiple columns
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                BooleanExpression searchExpression = qServiceArea.name.containsIgnoreCase(searchValue)
                        .or(qServiceArea.siteName.containsIgnoreCase(searchValue))
                        .or(qServiceArea.latitude.containsIgnoreCase(searchValue))
                        .or(qServiceArea.longitude.containsIgnoreCase(searchValue));
                booleanExpression = booleanExpression.and(searchExpression);
            }
            if (getMvnoIdFromCurrentStaff() != 1) {
                if (serviceareaIds.isEmpty()) {
                    booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                    booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1))
                            .and(qServiceArea.id.in(longServiceAreas));
                }

            }
            Page<ServiceArea> serviceAreaList = serviceAreaRepository.findAll(booleanExpression, pageRequest);

            if (0 < serviceAreaList.getSize()) {
                serviceAreaList.stream().forEach(x -> {
                    DecimalFormat df = new DecimalFormat("0.0000000");

                    if (x.getLatitude() != null && !x.getLatitude().isEmpty() && isNumeric(x.getLatitude()))
                        x.setLatitude(df.format(Double.parseDouble(x.getLatitude())));

                    if (x.getLongitude() != null && !x.getLongitude().isEmpty() && isNumeric(x.getLongitude()))
                        x.setLongitude(df.format(Double.parseDouble(x.getLongitude())));
                });
                makeGenericResponse(genericDataDTO, serviceAreaList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getAreaByNameWithAssignServiceAreaCheck(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getAreaByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            List<Integer> serviceareaIds = getServiceAreaByStaffId();
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            BooleanExpression booleanExpression = qServiceArea.isNotNull().and(qServiceArea.name.containsIgnoreCase(name));
            booleanExpression = booleanExpression.or(qServiceArea.latitude.containsIgnoreCase(name))
                    .or(qServiceArea.longitude.containsIgnoreCase(name))
                    .or(qServiceArea.status.equalsIgnoreCase(name));
            booleanExpression = booleanExpression.and(qServiceArea.isDeleted.eq(false));
            if (getMvnoIdFromCurrentStaff() != 1) {
                if (serviceareaIds.isEmpty()) {
                    booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                    booleanExpression = booleanExpression.and(qServiceArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1))
                            .and(qServiceArea.id.in(longServiceAreas));
                }

            }
            Page<ServiceArea> serviceAreaList = serviceAreaRepository.findAll(booleanExpression, pageRequest);

            if (0 < serviceAreaList.getSize()) {
                serviceAreaList.stream().forEach(x -> {
                    DecimalFormat df = new DecimalFormat("0.0000000");

                    if (x.getLatitude() != null && !x.getLatitude().isEmpty() && isNumeric(x.getLatitude()))
                        x.setLatitude(df.format(Double.parseDouble(x.getLatitude())));

                    if (x.getLongitude() != null && !x.getLongitude().isEmpty() && isNumeric(x.getLongitude()))
                        x.setLongitude(df.format(Double.parseDouble(x.getLongitude())));
                });
                makeGenericResponse(genericDataDTO, serviceAreaList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    public ServiceAreaDTO setDataforServicAreaExcludingPolygone(ServiceAreaDTO serviceAreaDTO){
        String SUBMODULE = getModuleNameForLog() + " [setDataforServicAreaExcludingPolygone()] ";
        try {
            ServiceAreaDTO servicAreaExcludingpolygoneDTO = new ServiceAreaDTO();
            servicAreaExcludingpolygoneDTO.setAreaid(serviceAreaDTO.getAreaid());
            servicAreaExcludingpolygoneDTO.setSiteName(serviceAreaDTO.getSiteName());
            servicAreaExcludingpolygoneDTO.setCityid(serviceAreaDTO.getCityid());
            servicAreaExcludingpolygoneDTO.setName(serviceAreaDTO.getName());
            servicAreaExcludingpolygoneDTO.setLatitude(serviceAreaDTO.getLatitude());
            servicAreaExcludingpolygoneDTO.setLongitude(serviceAreaDTO.getLongitude());
            servicAreaExcludingpolygoneDTO.setDisplayId(serviceAreaDTO.getDisplayId());
            servicAreaExcludingpolygoneDTO.setPincodes(serviceAreaDTO.getPincodes());
            servicAreaExcludingpolygoneDTO.setRadius(serviceAreaDTO.getRadius());
            servicAreaExcludingpolygoneDTO.setIsDeleted(serviceAreaDTO.getIsDeleted());
            servicAreaExcludingpolygoneDTO.setMvnoId(serviceAreaDTO.getMvnoId());
            servicAreaExcludingpolygoneDTO.setStatus(serviceAreaDTO.getStatus());
            servicAreaExcludingpolygoneDTO.setServiceAreaType(serviceAreaDTO.getServiceAreaType());
            servicAreaExcludingpolygoneDTO.setBlockNo(serviceAreaDTO.getBlockNo());
            servicAreaExcludingpolygoneDTO.setMvnoIds(serviceAreaDTO.getMvnoIds());
            return servicAreaExcludingpolygoneDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }
    public void savePoliGonList(ServiceAreaDTO entityDTO, ServiceAreaDTO serviceArea) {
        String SUBMODULE = getModuleNameForLog() + " [savePoliGonList()] ";
        try {
            List<PolyGone> polyGoneList = entityDTO.getPolyGoneList();
            polyGoneList.forEach(polyGone -> {
                        polyGone.setServiceAreaId(serviceArea.getId().intValue());
                        polyGone.setMvnoid(serviceArea.getMvnoId());
                        polyGone.setServiceAreaType(serviceArea.getServiceAreaType());
                    }
            );
            polyGoneRepository.saveAll(polyGoneList);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    public Boolean validatePolygoneSave(String name, Integer mvnoId, Integer serviceAreaId){
        return polyGoneRepository.existsByPolygoneNameAndMvnoidAndServiceAreaId(name,mvnoId,serviceAreaId);
    }

    public boolean saveStaffUserServiceAreaMapping(ServiceAreaDTO serviceArea, boolean staffServiceMap) {
        String SUBMODULE = getModuleNameForLog() + " [saveStaffUserServiceAreaMapping()] ";
        try {
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = staffUserServiceAreaMappingRepository.findByStaffId(getLoggedInUserId());
            if (!CollectionUtils.isEmpty(staffUserServiceAreaMappings)) {
                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = new ArrayList<>();
                StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                staffUserServiceAreaMapping.setServiceId(serviceArea.getId().intValue());
                staffUserServiceAreaMapping.setStaffId(getLoggedInUserId());
                staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping);
//                if (serviceAreaService.getLoggedInUserId() != 1) {
//                    StaffUserServiceAreaMapping staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
//                    staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
//                    staffUserServiceAreaMapping1.setServiceId(serviceArea.getId().intValue());
//                    staffUserServiceAreaMapping1.setStaffId(1);
//                    staffUserServiceAreaMapping1.setCreatedOn(LocalDateTime.now());
//                    staffUserServiceAreaMapping1.setLastmodifiedOn(LocalDateTime.now());
//                    staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping1);
//                }
                staffUserServiceAreaMappingRepository.saveAll(staffUserServiceAreaMappingList);
                staffServiceMap = true;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return staffServiceMap;
    }

    public void saveMVNOIdsList(ServiceAreaDTO serviceArea, boolean staffServiceMap) {
        String SUBMODULE = getModuleNameForLog() + " [sendServiceAreaWhenSave()] ";
        try {
            List<Integer> mvnoList = new ArrayList<>();
            if (serviceArea.getMvnoLists() != null && !serviceArea.getMvnoLists().isEmpty()) {
                mvnoList = Arrays.stream(serviceArea.getMvnoLists().split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }
            if (!mvnoList.isEmpty()) {
                for (Integer id : mvnoList) {
                    ServiceArea savedServiceArea = new ServiceArea();
                    String mvnoName = mvnoRepository.findMvnoNameById(id.longValue());
                    String serviceAreaName = serviceArea.getName() + "_" + mvnoName;
                    ServiceAreaDTO newMvnoServiceArea = new ServiceAreaDTO(serviceArea);
                    newMvnoServiceArea.setId(null);
                    newMvnoServiceArea.setPolyGoneList(null);
                    newMvnoServiceArea.setName(serviceAreaName);
                    newMvnoServiceArea.setMvnoId(id.intValue());
                    savedServiceArea = serviceAreaRepository.save(serviceAreaMapper.dtoToDomain(newMvnoServiceArea, new CycleAvoidingMappingContext()));
                    sendServiceAreaToOtherMicroserviceWhenSave(serviceAreaMapper.domainToDTO(savedServiceArea, new CycleAvoidingMappingContext()), staffServiceMap);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public boolean saveStaffUserServiceAreaMappingForBulk(ServiceAreaDTO serviceArea, boolean staffServiceMap) {
        String SUBMODULE = getModuleNameForLog() + " [saveStaffUserServiceAreaMapping()] ";
        try {
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = staffUserServiceAreaMappingRepository.findByStaffId(serviceArea.getCreatedById());
            if (!CollectionUtils.isEmpty(staffUserServiceAreaMappings)) {
                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = new ArrayList<>();
                StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                staffUserServiceAreaMapping.setServiceId(serviceArea.getId().intValue());
                staffUserServiceAreaMapping.setStaffId(serviceArea.getCreatedById());
                staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping);
//                if (serviceAreaService.getLoggedInUserId() != 1) {
//                    StaffUserServiceAreaMapping staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
//                    staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
//                    staffUserServiceAreaMapping1.setServiceId(serviceArea.getId().intValue());
//                    staffUserServiceAreaMapping1.setStaffId(1);
//                    staffUserServiceAreaMapping1.setCreatedOn(LocalDateTime.now());
//                    staffUserServiceAreaMapping1.setLastmodifiedOn(LocalDateTime.now());
//                    staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping1);
//                }
                staffUserServiceAreaMappingRepository.saveAll(staffUserServiceAreaMappingList);
                staffServiceMap = true;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return staffServiceMap;
    }

/*
    public List<LightServiceAreaDTO> getAllEntitiesForDropdown() {
        try {
            List<Integer> serviceareaIds = getServiceAreaByStaffId();
            List<LightServiceAreaDTO> serviceAreaList = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                serviceAreaList = serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
                serviceAreaList.forEach(lightServiceAreaDTO ->  {
                    lightServiceAreaDTO.setPincodes(serviceAreaPincodeRelRepository.getPincodeIdsFromServiceAreaId(lightServiceAreaDTO.getId()));
                });
            } else {
                if (serviceareaIds.isEmpty()) {
                    serviceAreaList = serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    serviceAreaList.forEach(lightServiceAreaDTO ->  {
                        lightServiceAreaDTO.setPincodes(serviceAreaPincodeRelRepository.getPincodeIdsFromServiceAreaId(lightServiceAreaDTO.getId()));
                    });
                } else {
                    List<Long> longServiceAreas = serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                    serviceAreaList = serviceAreaRepository.findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), longServiceAreas);
                    serviceAreaList.forEach(lightServiceAreaDTO ->  {
                        lightServiceAreaDTO.setPincodes(serviceAreaPincodeRelRepository.getPincodeIdsFromServiceAreaId(lightServiceAreaDTO.getId()));
                    });
                }
            }
            return serviceAreaList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }
*/



    public List<LightServiceAreaDTO> getAllEntitiesForDropdown() {
        try {
            List<Integer> serviceareaIds = getServiceAreaByStaffId();
            List<LightServiceAreaDTO> serviceAreaList;
            List<Integer> mvnoIds = (getMvnoIdFromCurrentStaff() == 1)
                    ? null
                    : Arrays.asList(getMvnoIdFromCurrentStaff(), 1);

            List<Long> longServiceAreas = (serviceareaIds != null && !serviceareaIds.isEmpty())
                    ? serviceareaIds.stream().map(Integer::longValue).collect(Collectors.toList())
                    : null;

            // Fetch service area list
            if (getMvnoIdFromCurrentStaff() == 1) {
                serviceAreaList = serviceAreaRepository
                        .findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
            } else if (longServiceAreas == null) {
                serviceAreaList = serviceAreaRepository
                        .findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, mvnoIds);
            } else {
                serviceAreaList = serviceAreaRepository
                        .findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(CommonConstants.ACTIVE_STATUS, mvnoIds, longServiceAreas);
            }

            // Fetch pincodes in batch
            List<Long> serviceAreaIds = serviceAreaList.stream().map(LightServiceAreaDTO::getId).collect(Collectors.toList());
            Map<Long, List<Integer>> pincodeMap = getPincodeMapForServiceAreas(serviceAreaIds);

            // Assign pincodes to each DTO
            for (LightServiceAreaDTO dto : serviceAreaList) {
                List<Integer> pincodes = pincodeMap.getOrDefault(dto.getId(), new ArrayList<>());
                List<Long> longPincodes = pincodes.stream()
                        .map(Integer::longValue)
                        .collect(Collectors.toList());
                dto.setPincodes(longPincodes);
            }


            return serviceAreaList;

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + " Error while getting list", ex);
            throw ex;
        }
    }
    private Map<Long, List<Integer>> getPincodeMapForServiceAreas(List<Long> serviceAreaIds) {
        List<Object[]> rows = serviceAreaPincodeRelRepository.findPincodeIdsByServiceAreaIds(serviceAreaIds);
        Map<Long, List<Integer>> map = new HashMap<>();

        for (Object[] row : rows) {
            Long serviceAreaId = (Long) row[0];
            Integer pincodeId = ((Long) row[1]).intValue();
            map.computeIfAbsent(serviceAreaId, k -> new ArrayList<>()).add(pincodeId);
        }

        return map;
    }

    public ServiceAreaDTO getEntityById(Long id) throws Exception {
        try {
            ServiceArea area = serviceAreaRepository.findByServiceAreaId(id);
            ServiceAreaDTO dto = serviceAreaMapper.domainToDTO(area, new CycleAvoidingMappingContext());
            dto.setPincodes(serviceAreaPincodeRelRepository.getPincodeIdsFromServiceAreaId(id).stream().map(Long::intValue)
                    .collect(Collectors.toList()));
            if (dto != null && (getMvnoIdFromCurrentStaff() == 1 || (dto.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || dto.getMvnoId() == 1)))
                return dto;
            return null;
            /*if(null == dto){

            }*/
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            //   ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public ServiceAreaDTO getEntityByServiceAreaId(Long id) throws Exception {
        try {
            ServiceAreaDTO dto = serviceAreaRepository.findDTOById(id);
            dto.setPincodes(serviceAreaPincodeRelRepository.getPincodeIdsFromServiceAreaId(id).stream().map(Long::intValue)
                    .collect(Collectors.toList()));
            if (dto != null && (getMvnoIdFromCurrentStaff() == 1 || (dto.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || dto.getMvnoId() == 1)))
                return dto;
            return null;
            /*if(null == dto){

            }*/
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            //   ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }
    public boolean assignStaffToServiceArea(Long serviceAreaId, List<Integer> staffIds) {
        String SUBMODULE = getModuleNameForLog() + " [assignStaffToServiceArea()] ";
        try {
//            if (!CollectionUtils.isEmpty(staffIds)) {
//
//                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = new ArrayList<>();
//                for (Integer staffId:staffIds){
//                    StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
//                    staffUserServiceAreaMapping.setServiceId(serviceAreaId.intValue());
//                    staffUserServiceAreaMapping.setStaffId(staffId);
//                    staffUserServiceAreaMapping.setCreatedById(getLoggedInUserId());
//                    staffUserServiceAreaMapping.setLastModifiedById(getLoggedInUserId());
//                    staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
//                    staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
//                    staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping);
//                }
//                kafkaMessageSender.send(new KafkaMessageData(staffUserServiceAreaMappingList,staffUserServiceAreaMappingList.getClass().getSimpleName()));
//                staffUserServiceAreaMappingRepository.saveAll(staffUserServiceAreaMappingList);
//            }
            if (!CollectionUtils.isEmpty(staffIds)) {

                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = new ArrayList<>();

                for (Integer staffId : staffIds) {
                    StaffUserServiceAreaMapping mapping = new StaffUserServiceAreaMapping();
                    mapping.setServiceId(serviceAreaId.intValue());
                    mapping.setStaffId(staffId);
                    mapping.setCreatedById(getLoggedInUserId());
                    mapping.setLastModifiedById(getLoggedInUserId());
                    mapping.setCreatedOn(LocalDateTime.now());
                    mapping.setLastmodifiedOn(LocalDateTime.now());
                    staffUserServiceAreaMappingList.add(mapping);
                }
                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = staffUserServiceAreaMappingRepository.saveAll(staffUserServiceAreaMappingList);
                SaveStaffAssignmentMessage message = new SaveStaffAssignmentMessage();
                message.setMappingList(staffUserServiceAreaMappings);
                message.setCreatedById(getLoggedInUserId());
                message.setUpdatedById(getLoggedInUserId());
                message.setAreaId(serviceAreaId);
                message.setStaffSAMap(true);

                Gson gson = GsonConfig.buildGson();
                String json = gson.toJson(message);
                KafkaMessageData kafkaMsg = new KafkaMessageData(gson.fromJson(json, Object.class), SaveStaffAssignmentMessage.class.getSimpleName());
                kafkaMessageSender.send(kafkaMsg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return true;
    }


    //@Transactional
    public ServiceAreaDTO updatePolygone(ServiceAreaDTO entityDTO, ServiceArea oldname) {

        List<PolyGone> existingPolygonList = polyGoneRepository.findAllByServiceAreaIdAndMvnoid(oldname.getId().intValue(), oldname.getMvnoId());
        polyGoneRepository.deleteInBatch(existingPolygonList);

        for (PolyGone polyGones : entityDTO.getPolyGoneList()) {
            //updating polygone details
            if (entityDTO.getPolyGoneList() != null) {
                List<PolyGone> polyGoneList = entityDTO.getPolyGoneList();

                polyGoneList.forEach(polyGone -> {
                            polyGone.setServiceAreaId(oldname.getId().intValue());
                            polyGone.setMvnoid(oldname.getMvnoId());
                            polyGone.setServiceAreaType(oldname.getServiceAreaType());
                        }
                );
                List<PolyGone> newPolygonList = polyGoneRepository.saveAll(polyGoneList);
                entityDTO.setPolyGoneList(newPolygonList);
            }
        }
        return entityDTO;
    }
}
