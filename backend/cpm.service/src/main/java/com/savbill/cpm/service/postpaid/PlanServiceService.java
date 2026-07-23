package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.constants.cacheKeys;
import com.savbill.cpm.controller.api.APIController;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.mapper.postpaid.PlanServiceInventoryMapper;
import com.savbill.cpm.model.postpaid.*;
import com.savbill.cpm.model.postpaid.*;
import com.savbill.cpm.modules.BusinessUnit.service.BusinessUnitService;
import com.savbill.cpm.modules.InventoryManagement.productCategory.ProductCategory;
import com.savbill.cpm.modules.InventoryManagement.productCategory.ProductCategoryRepository;
import com.savbill.cpm.modules.InventoryManagement.productCategory.ProductCategoryService;
import com.savbill.cpm.modules.Mvno.repository.MvnoRepository;
import com.savbill.cpm.modules.ServiceParameterMapping.domain.QServiceParamMapping;
import com.savbill.cpm.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import com.savbill.cpm.modules.ServiceParameterMapping.mapper.ServiceParamMappingMapper;
import com.savbill.cpm.modules.ServiceParameterMapping.repository.ServiceParamMappingRepository;
import com.savbill.cpm.modules.ServiceParameters.repository.ServcieParametersRepository;
import com.savbill.cpm.modules.servicePlan.domain.Services;
import com.savbill.cpm.modules.servicePlan.repository.ServiceRepository;
import com.savbill.cpm.pojo.api.PlanPojo;
import com.savbill.cpm.pojo.api.PlanServiceCustomDto;
import com.savbill.cpm.pojo.api.ServiceParamMappingDto;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.PlanServiceForIntegrationMessage;
import com.savbill.cpm.rabbitMq.message.PlanServiceMessage;
import com.savbill.cpm.repository.postpaid.PlanServiceInventoryRepository;
import com.savbill.cpm.repository.postpaid.PlanServiceRepository;
import com.savbill.cpm.repository.postpaid.PostPaidPlanServiceAreaMappingRepo;
import com.savbill.cpm.repository.postpaid.ServiceChargeMappingRepository;
import com.savbill.cpm.service.CacheService;
import com.savbill.cpm.service.common.StaffUserService;
import com.savbill.cpm.service.radius.AbstractService;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.spring.MessagesPropertyConfig;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanServiceService extends AbstractService<PlanService, PlanPojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CommitPropertiesProvider commitPropertiesProvider;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private BusinessUnitService businessUnitService;

    @Autowired
    private PlanServiceRepository entityRepository;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private PlanServiceInventoryRepository planServiceInventoryRepository;

    @Autowired
    private PlanServiceInventoryMapper planServiceInventoryMapper;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

     @Autowired
     private ServiceChargeMappingRepository serviceChargeMappingRepository;
    @Autowired
    MessageSender messageSender;

     @Autowired
     private ServcieParametersRepository servcieParametersRepository;

     @Autowired
     private ServiceParamMappingRepository serviceParamMappings;

     @Autowired
     private ServiceParamMappingMapper serviceParamMappingMapper;

    @Autowired
    private ServiceParamMappingRepository serviceParamMappingRepository;

    @Autowired
    private PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CacheService cacheService;
    @Autowired
    private ServiceRepository serviceRepository;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<PlanService, Integer> getRepository() {
        return entityRepository;
    }


    public Page<PlanService> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest, getMvnoIdFromCurrentStaff());
    }

    @Override
    public Page<PlanService> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if (filterList == null || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff().size() == 0)
                return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            else
                return entityRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
    }

    public List<PlanService> getAllServices() {
        List<PlanService> planServiceList = new ArrayList<>();
        QPlanService service = QPlanService.planService;
        BooleanExpression expression = service.isNotNull();
        expression = expression.and(service.isDeleted.eq(false));
        if(getLoggedInMvnoId() != 1) {
            expression = expression.and(service.mvnoId.in(getLoggedInMvnoId(), 1));
        }
        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
            expression = expression.and(service.buId.in(getBUIdsFromCurrentStaff()));
        }
        planServiceList = (List<PlanService>) entityRepository.findAll(expression);
        return planServiceList;
    }


    public List<PlanServiceCustomDto> getCustomPlanServiceList() {
        Integer mvnoId   = getLoggedInMvnoId();
        List<Integer> mvnoIds = (mvnoId != 1)
                ? Arrays.asList(mvnoId, 1)
                : Collections.singletonList(1);
        List<Long> buIds = getBUIdsFromCurrentStaff();

        List<Object[]> rows;
        if(buIds!= null && buIds.size()!=0){
            rows = entityRepository.fetchFlatPlanServiceData(
                    mvnoId, mvnoIds, buIds);
        }else {
            rows = entityRepository.fetchFlatPlanServiceData(
                    mvnoId, mvnoIds, null);
        }

        Map<Integer, PlanServiceCustomDto> dtoMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Integer    pId         = (Integer) row[0];
            String     name        = (String)  row[1];
            String     displayName = (String)  row[2];
            Integer    pMvnoId     = (Integer) row[3];

            Long       spmId       = (Long)    row[4];
            Long       serviceId   = (Long)    row[5];
            String     paramName   = (String)  row[6];
            Long       paramId     = (Long)    row[7];
            String     mvnoName    = (String)  row[8];

            // get or create the parent DTO
            PlanServiceCustomDto parentDto = dtoMap.get(pId);
            if (parentDto == null) {
                parentDto = new PlanServiceCustomDto();
                parentDto.setId(pId);
                parentDto.setName(name);
                parentDto.setDisplayName(displayName);
                parentDto.setDisplayId(pId);
                parentDto.setMvnoId(pMvnoId);
                parentDto.setMvnoName(mvnoName);
                parentDto.setServiceParamMappingList(new ArrayList<>());
                dtoMap.put(pId, parentDto);
            }

            if (spmId != null) {
                ServiceParamMappingDto child = new ServiceParamMappingDto();
                child.setId(spmId);
                child.setServiceid(serviceId);
                child.setServiceParamName(paramName);
                child.setServiceParamId(paramId);

                parentDto.getServiceParamMappingList().add(child);
            }
        }

        return new ArrayList<>(dtoMap.values());
    }


    @Transactional
    public void deletePlan(Integer id) throws Exception {
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.serviceId.eq(id));
        if (IterableUtils.toList(postpaidPlanService.getRepository().findAll(booleanExpression)).size() > 0) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, messagesProperty.get("api.plan.service.deleted.not.allowed"), null);
        } else {
               QServiceChargeMapping qServiceChargeMapping= QServiceChargeMapping.serviceChargeMapping;
               BooleanExpression booleanExpression1 = qServiceChargeMapping.isNotNull().and(qServiceChargeMapping.services.id.eq(id.longValue()));
               if (IterableUtils.toList(serviceChargeMappingRepository.findAll(booleanExpression1)).size() > 0)
               {
                   throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, messagesProperty.get("api.plan.service.deleted.not.allowed"), null);
               }
               else{
                   entityRepository.deleteById(id);
               }

        }
    }
    @Transactional
    public PlanService savePlanService(PlanService planService) throws Exception {
        if (getMvnoIdFromCurrentStaff() != null) {
            planService.setMvnoId(getMvnoIdFromCurrentStaff());
            planService.setMvnoName(mvnoRepository.findMvnoNameById(getMvnoIdFromCurrentStaff().longValue()));
        }
        PlanService save = entityRepository.save(planService);
        String cacheKey = cacheKeys.PLAN_SERVICES + save.getId();
        cacheService.saveOrUpdateInCacheAsync(save,cacheKey);
        PlanServiceMessage planServiceMessage = new PlanServiceMessage(save);
        //messageSender.send(planServiceMessage, RabbitMqConstants.QUEUE_PLAN_SERVICE_SUCCESS,RabbitMqConstants.QUEUE_PLAN_SERVICE_KPI);
        PlanServiceForIntegrationMessage message = new PlanServiceForIntegrationMessage(save);
        //messageSender.send(message ,RabbitMqConstants.QUEUE_SERVICE_FOR_INTEGRATION);
        return save;
    }

    public PlanService getPlanServiceForAdd() {
        return new PlanService();
    }

    public PlanService getPlanServiceForEdit(Integer id) {
        return entityRepository.getOne(id);
    }

    @Transactional
    public PlanPojo save(PlanPojo pojo) throws Exception {
        PlanService oldObj = null;
        if (pojo.getId() != null) {
            oldObj = get(pojo.getId());
        }
        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
        PlanService obj = convertPlanServicePojoToPlanServiceModel(pojo);
        if (getBUIdsFromCurrentStaff().size() == 1)
            obj.setBuId(getBUIdsFromCurrentStaff().get(0));
        if(pojo.getPcategoryId() != null){
            obj = getProductInventoryMappingId(pojo, obj);
        }
        if(oldObj!=null) {
            log.info("Planservice update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
        }
        obj = savePlanService(obj);
        commitPropertiesProvider.provideForCommittedObject(staffUserService.get(getLoggedInUserId()));
        pojo = convertPlanServiceModelToPlanServicePojo(obj);
        String cacheKey = cacheKeys.PLANSERVICE + obj.getId();
        cacheService.putInCache(cacheKey, obj);
        return pojo;
    }

    private PlanService getProductInventoryMappingId(PlanPojo pojo, PlanService planService) {
           PlanServiceInventoryMapping planServiceInventoryMapping =new PlanServiceInventoryMapping();
           List<Long> productCategoryList = pojo.getPcategoryId();
           List<ProductCategory> productCategories = productCategoryRepository.findAllById(productCategoryList);
           if(productCategories != null){
               planService.setProductCategories(productCategories);
           }
           return planService;
    }

    public PlanService convertPlanServicePojoToPlanServiceModel(PlanPojo pojo) throws Exception {
        PlanService planService = null;
        if (pojo != null) {
            planService = new PlanService();
            if (pojo.getId() != null) {
                planService.setId(pojo.getId());
            }
            if(pojo.getDisplayName() != null) {
                planService.setDisplayName(pojo.getDisplayName());
            } else {
                planService.setDisplayName(pojo.getName());
            }
            planService.setName(pojo.getName());
            planService.setIcname(pojo.getIcname());
            planService.setIccode(pojo.getIccode());
            planService.setExpiry(pojo.getExpiry());
            planService.setLedgerId(pojo.getLedgerId());
            planService.setIs_dtv(pojo.getis_dtv());
            planService.setInvestmentid(pojo.getInvestmentid());
            planService.setServiceParamMappingList(serviceParamMappingMapper.dtoToDomain(pojo.getServiceParamMappingList(), new CycleAvoidingMappingContext()));
            List<Long> serviceParmIds = planService.getServiceParamMappingList().stream().map(ServiceParamMapping::getServiceParamId).collect(Collectors.toList());
            if (serviceParmIds.contains(1L)) {
                planService.setIsQoSV(true);
            } else if (!serviceParmIds.contains(1L)) {
                planService.setIsQoSV(false);
            }
            planService.setFeasibility(pojo.getFeasibility());
            planService.setInstallation(pojo.getInstallation());
            planService.setIsPriceEditable(pojo.getIsPriceEditable());
            planService.setPoc(pojo.getPoc());
            planService.setProvisioning(pojo.getProvisioning());
            planService.setCreatedById(pojo.getCreatedById());
            planService.setLastModifiedById(pojo.getLastModifiedById());
            planService.setIsServiceThroughLead(pojo.getIsServiceThroughLead());

            return planService;
        }
        return planService;
    }



    public PlanPojo convertPlanServiceModelToPlanServicePojo(PlanService planService) throws Exception {
        PlanPojo planPojo = null;
        List<Long> longs = new ArrayList<>();
        if (planService != null) {
            planPojo = new PlanPojo();
            planPojo.setId(planService.getId());
            planPojo.setName(planService.getName());
            planPojo.setIcname(planService.getIcname());
            planPojo.setIccode(planService.getIccode());
            planPojo.setIsQoSV(planService.getIsQoSV());
            planPojo.setUpdatedate(planService.getUpdatedate());
            planPojo.setCreatedate(planService.getCreatedate());
            planPojo.setCreatedById(planService.getCreatedById());
            planPojo.setCreatedByName(planService.getCreatedByName());
            planPojo.setLastModifiedById(planService.getLastModifiedById());
            planPojo.setLastModifiedByName(planService.getLastModifiedByName());
            planPojo.setExpiry(planService.getExpiry());
            planPojo.setLedgerId(planService.getLedgerId());
            planPojo.setis_dtv(planService.getIs_dtv());
            planPojo.setInvestmentid(planService.getInvestmentid());
            planPojo.setProductCategory(planService.getProductCategories());
            planPojo.setPcategoryId(planPojo.getProductCategory().stream().map(x->x.getId()).collect(Collectors.toList()));
            planPojo.setDisplayId(planService.getId());
            if(planService.getDisplayName() != null)
                planPojo.setDisplayName(planService.getDisplayName());
            else
                planPojo.setDisplayName(planService.getName());
            planPojo.setFeasibility(planService.getFeasibility());
            planPojo.setInstallation(planService.getInstallation());
            planPojo.setPoc(planService.getPoc());
            planPojo.setProvisioning(planService.getProvisioning());
            planPojo.setIsPriceEditable(planService.getIsPriceEditable());
            planPojo.setIsServiceThroughLead(planService.getIsServiceThroughLead());
            planPojo.setMvnoName(planService.getMvnoName());

//            for (int i=0;i<planService.getServiceParamMappings().size();i++) {
//                longs.add(planService.getServiceParamMappings().get(i).getId());
//            }
            planPojo.setServiceParamMappingList(serviceParamMappingMapper.domainToDTO(planService.getServiceParamMappingList(), new CycleAvoidingMappingContext()));
        }
        return planPojo;
    }

    public List<PlanPojo> convertResponseModelIntoPojo(List<PlanService> planServiceList) throws Exception {
        List<PlanPojo> pojoListRes = new ArrayList<PlanPojo>();
        if (planServiceList != null && planServiceList.size() > 0) {
            pojoListRes.addAll(planServiceList.stream().map(planService -> {
                try {
                    return convertPlanServiceModelToPlanServicePojo(planService);
                }catch (Exception e){
                    ApplicationLogger.logger.error("convertResponseModelIntoPojo" + e.getMessage(), e);
                    throw new RuntimeException();
                }
            }).collect(Collectors.toList()));
//            for (PlanService planService : planServiceList) {
//                pojoListRes.add(convertPlanServiceModelToPlanServicePojo(planService));
//            }
        }
        return pojoListRes;
    }

    public void validateRequest(PlanPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Plan Service");
        List<PlanPojo> planPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, PlanPojo.class, planPojoList, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                PlanPojo.class.getDeclaredField("id"),
                PlanPojo.class.getDeclaredField("name"),
        };
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<PlanPojo> planPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, PlanPojo.class, planPojoList, getFields());
    }

     @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }


     @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    count = entityRepository.duplicateVerifyAtSave(name,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = entityRepository.duplicateVerifyAtSave(name,  getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff().isEmpty())
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = entityRepository.duplicateVerifyAtEdit(name,  id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

  /*  @Override
    public PlanService get(Integer id) {
        PlanService planService = super.get(id);
        if (getMvnoIdFromCurrentStaff() == 1 || ((planService.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || planService.getMvnoId() == 1) && (planService.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(planService.getBuId()))))
            return planService;
        return null;
    }*/
  @Override
  public PlanService get(Integer id) {
      String cacheKey = cacheKeys.PLANSERVICE + id;

      try {
          PlanService planService = (PlanService) cacheService.getFromCache(cacheKey, PlanService.class);

          if (planService == null) {
              planService = super.get(id);

              if (planService == null) {
                  return null;
              }

              // Cache the retrieved PlanService
              cacheService.putInCache(cacheKey, planService);
          }

          // Perform the condition checks before returning
          if (getMvnoIdFromCurrentStaff() == 1 ||
                  ((planService.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || planService.getMvnoId() == 1) &&
                          (planService.getMvnoId() == 1 || getBUIdsFromCurrentStaff().isEmpty() || getBUIdsFromCurrentStaff().contains(planService.getBuId())))) {
              return planService;
          }
      } catch (Exception e) {
          e.printStackTrace();
      }

      return null;
  }

    public PlanService getEntityForUpdateAndDelete(Integer id) {
        PlanService planService = get(id);
        if (planService == null || (!(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == planService.getMvnoId().intValue()) && (planService.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(planService.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return planService;
    }

    public boolean duplicateVerifyAtSaveICCode(String iccode) {
        boolean flag = false;
        Integer mvnoId = getMvnoIdFromCurrentStaff();
       if(iccode!=null && !iccode.trim().isEmpty()){
            iccode = iccode.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSaveICCode(iccode,Arrays.asList(1));
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    count = entityRepository.duplicateVerifyAtSaveICCode(iccode, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = entityRepository.duplicateVerifyAtSaveICCode(iccode, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
       else  {
           flag=true;
        }
        return flag;
    }

    public boolean duplicateVerifyAtSaveICName(String icname) {
        boolean flag = false;

        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if (icname!=null && !icname.trim().isEmpty()) {
            icname = icname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSaveICName(icname,Arrays.asList(1));
            else {
                if (getBUIdsFromCurrentStaff().isEmpty())
                    count = entityRepository.duplicateVerifyAtSaveICName(icname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = entityRepository.duplicateVerifyAtSaveICName(icname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        else  {
            flag=true;
        }
        return flag;

    }

    //duplicate verification at edit at ic code
    public boolean duplicateVerifyAtEditICName(String icname, Integer id) {
        boolean flag = false;
        Integer mvnoId = getMvnoIdFromCurrentStaff();
       if (icname != null && !icname.trim().isEmpty() ) {
            icname = icname.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSaveICName(icname);
            else {
                if(getBUIdsFromCurrentStaff().isEmpty())
                    count = entityRepository.duplicateVerifyAtSaveICName(icname, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = entityRepository.duplicateVerifyAtSaveICName(icname, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEditICName(icname, id);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0)
                        countEdit = entityRepository.duplicateVerifyAtEditICName(icname, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = entityRepository.duplicateVerifyAtEditICName(icname, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }else{
           flag=true;
       }
        return flag;


    }



    public boolean duplicateVerifyAtEditICCode(String iccode, Integer id) {
        boolean flag = false;
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        if (iccode != null && !iccode.trim().isEmpty() ) {
            iccode = iccode.trim();


            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSaveICCode(iccode);
            else {
                if(getBUIdsFromCurrentStaff().size() == 0)
                    count = entityRepository.duplicateVerifyAtSaveICCode(iccode, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = entityRepository.duplicateVerifyAtSaveICCode(iccode, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEditICCode(iccode, id);
                else {
                    if(getBUIdsFromCurrentStaff().size() == 0)
                        countEdit = entityRepository.duplicateVerifyAtEditICCode(iccode, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = entityRepository.duplicateVerifyAtEditICCode(iccode, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        else {
            flag=true;
        }
        return flag;
    }

    public List<PlanService> getAllServicesforIPCharge(){
        QPlanService qPlanService = QPlanService.planService;
        QServiceParamMapping qServiceParamMapping = QServiceParamMapping.serviceParamMapping;
        BooleanExpression booleanExpression = qServiceParamMapping.isNotNull().and(qServiceParamMapping.serviceParamId.eq(1L).and(qServiceParamMapping.serviceid.isNotNull()));
        List<ServiceParamMapping> serviceParamMappingList = IterableUtils.toList(serviceParamMappingRepository.findAll(booleanExpression));
        List<Long> serviceIdList = serviceParamMappingList.stream().map(ServiceParamMapping::getServiceid).collect(Collectors.toList());
        List<Integer> intIds =  serviceIdList.stream().map(Long::intValue)
                .collect(Collectors.toList());
        BooleanExpression expression = qPlanService.isNotNull().and(qPlanService.id.in(intIds));
        List<PlanService> planServices = IterableUtils.toList(entityRepository.findAll(expression)).stream().filter(service -> (service.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || service.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) && (service.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(service.getBuId()))).collect(Collectors.toList());
        return planServices;
    }

    public void sendCreatedDataShared(PlanPojo pojo, Integer operation) throws Exception {
        try {
            PlanService planServiceEntity = convertPlanServicePojoToPlanServiceModel(pojo);
            if(pojo.getPcategoryId() != null) {
                List<ProductCategory> productCategories = new ArrayList<>();
                for (Long pcId : pojo.getPcategoryId()) {
                    ProductCategory productCategory = new ProductCategory();
                    productCategory.setId(pcId);
                    productCategories.add(productCategory);
                }
                planServiceEntity.setProductCategories(productCategories);
            }
            planServiceEntity.setMvnoId(getMvnoIdFromCurrentStaff());
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                planServiceEntity.setIsDeleted(false);
                createDataSharedService.sendEntitySaveDataForAllMicroService(planServiceEntity);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                planServiceEntity.setIsDeleted(false);
                createDataSharedService.updateEntityDataForAllMicroService(planServiceEntity);
            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                planServiceEntity.setIsDeleted(true);
                createDataSharedService.deleteEntityDataForAllMicroService(planServiceEntity);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public Services getServices(Integer id) {
        String cacheKey = cacheKeys.SERVICES + id;

        try {
            // Check if Service exists in cache
            Services service = (Services) cacheService.getFromCache(cacheKey, Services.class);

            if (service == null) {
                Optional<Services> optionalService =  serviceRepository.findById(id.longValue());

                if (!optionalService.isPresent()) {
                    return null;
                }

                service = optionalService.get();

                // Cache the retrieved Service
                cacheService.putInCache(cacheKey, service);
            }

            // Perform the condition checks before returning
            if (getMvnoIdFromCurrentStaff() == 1 ||
                    ((service.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || service.getMvnoId() == 1) &&
                            (service.getMvnoId() == 1 || getBUIdsFromCurrentStaff().isEmpty()))) {
                return service;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
