package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.ServiceareaLocationMappingMessage;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.MasterManagement.LocationMaster.ServiceAreaLocationMappingRepository;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeMvnoDto;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.repository.PincodeRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.AssignServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.PolyGone;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceAreaLocationMapping;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.mapper.ServiceAreaMapper;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTOProjection;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.PolyGoneRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.ServiceAreaService;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.GoogleMaps;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SERVICE_AREA)
public class ServiceAreaController extends ExBaseAbstractController<ServiceAreaDTO> {
    @Autowired
    private PincodeRepository pincodeRepository;

    public ServiceAreaController(ServiceAreaService service) {
        super(service);
    }

    private static String MODULE = " [ServiceAreaController] ";

    @Autowired
    private Tracer tracer;

    private final Logger LOGGER = LoggerFactory.getLogger(ServiceAreaController.class);
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

//    @Autowired
//    private AuditLogService auditLogService;
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private CreateDataSharedService createDataSharedService;

    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private GoogleMaps googleMaps;

    @Autowired
    PolyGoneRepository  polyGoneRepository;

    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    MvnoRepository  mvnoRepository;

    @Autowired
    ServiceAreaLocationMappingRepository serviceAreaLocationMappingRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Override
    public String getModuleNameForLog() {
        return "[ServiceAreaController]";
    }

    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(serviceAreaService.getAllEntities());
            genericDataDTO.setTotalRecords(serviceAreaService.getAllEntities().size());
        //    logger.info("Fetching Sevice area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
         //   logger.error("Unable to fetch data without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;
    }

    @GetMapping("/all/caf/customer")
    public GenericDataDTO getAllWithoutPaginationCafCustomer() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(serviceAreaService.getAllEntitiesForCafCustomer());
            genericDataDTO.setTotalRecords(serviceAreaService.getAllEntitiesForCafCustomer().size());
            //    logger.info("Fetching Sevice area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            //   logger.error("Unable to fetch data without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
        }

        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
@PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SERVICE_AREA + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
    TraceContext traceContext = tracer.currentSpan().context();
    MDC.put("type","Search");
    MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
    MDC.put("traceId",traceContext.traceIdString());
    MDC.put("spanId",traceContext.spanIdString());
    GenericDataDTO genericDataDTO = new GenericDataDTO();
    long startTime = System.nanoTime();  // Start measuring
    try{
        genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter ,req,res );
        List<ServiceAreaDTO> serviceAreaDTOList = genericDataDTO.getDataList();
        for(ServiceAreaDTO serviceAreaDTO :serviceAreaDTOList){
            for(PolyGone polyGone : serviceAreaDTO.getPolyGoneList()){
                polyGone.setServiceAreaType(serviceAreaDTO.getServiceAreaType());
            }
        }
        genericDataDTO.setDataList(serviceAreaDTOList);
        if(genericDataDTO.getDataList().isEmpty()){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Search Service area By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_FAILED+ LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
        }else
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Search Service area By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

    }catch (Exception ex){
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Search Service area By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +APIConstants.EXPECTATION_FAILED +APIConstants.ERROR_MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
    }finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
        long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
        res.addHeader("Server-Timing", "app;dur=" + durationInMs);
    }
    return genericDataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
    @GetMapping("/all/byreasonconfig/{caseReasonId}")
    public GenericDataDTO getAllServiceAreaForCaseReasonConfig(@PathVariable Long caseReasonId,HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getAllServiceAreaForCaseReasonConfig()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        try {
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Fetch service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);

            return GenericDataDTO.getGenericDataDTO(serviceAreaService.getAllServiceAreaForCaseReasonConfig(caseReasonId));
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Fetch service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.FAIL);


        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_DELETE + "\")")
  @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SERVICE_AREA_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody ServiceAreaDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
      Integer respCode = APIConstants.FAIL;

      TraceContext traceContext = tracer.currentSpan().context();
      MDC.put("type","Delete");
      MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
      MDC.put("traceId",traceContext.traceIdString());
      MDC.put("spanId",traceContext.spanIdString());
      long startTime = System.nanoTime();  // Start measuring
      try {
            serviceAreaService.getEntityForUpdateAndDelete(entityDTO.getId());
            //serviceAreaService.validateServiceAreaInventory(entityDTO);
            boolean flag = serviceAreaService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req,res);
                ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
                serviceAreaService.sendServiceAreaToAllMicroServiceWhenDelted(serviceArea);
                dataDTO.setResponseMessage("Successfully Deleted");
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete service area By Id :"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.SERVICE_AREA_DELETE_EXIST);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete service area By Id :" +LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+respCode);
            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode=e.getErrCode();
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete service area By Id :"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete service area By Id :"+LogConstants.LOG_BY_NAME+entityDTO.getName() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_EDIT + "\")")
@PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SERVICE_AREA_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody ServiceAreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
    Integer respCode = APIConstants.FAIL;
    TraceContext traceContext = tracer.currentSpan().context();
    MDC.put("type","Update");
    MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
    MDC.put("traceId",traceContext.traceIdString());
    MDC.put("spanId",traceContext.spanIdString());
    String mvnosList="";
    long startTime = System.nanoTime();  // Start measuring
    try {

//            serviceAreaService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = serviceAreaService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
            if (flag) {
                ServiceArea oldname = serviceAreaService.getByID(entityDTO.getId());
                ServiceArea oldClone = new ServiceArea(oldname);
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }

                boolean duplicateNameCheck  = entityDTO.getPolyGoneList()
                                                   .stream()
                                                   .collect(Collectors.groupingBy(o -> o.getPolyOrder()+"-"+ o.getPolygoneName())).values().stream().anyMatch(polyGoneList -> polyGoneList.size()>1);
                if(!duplicateNameCheck){
                    entityDTO = serviceAreaService.updatePolygone(entityDTO,oldClone);
                }else{
                    throw  new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"Same name for diffrent polygone is not allowd!",null);
                }

                if(entityDTO.getMvnoIds() != null && !entityDTO.getMvnoIds().isEmpty()){
                    String mvnoLists = entityDTO.getMvnoIds().stream().map(String::valueOf).collect(Collectors.joining(","));
                    entityDTO.setMvnoLists(mvnoLists);
                }
                if(oldname.getMvnoLists()!=null){
                    mvnosList = oldname.getMvnoLists();
                }
                dataDTO = super.update(entityDTO, result, authentication, req,res);
                if(dataDTO.getResponseCode() == HttpStatus.NOT_ACCEPTABLE.value()){
                    throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
                }
                ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();

                if(mvnosList !=null){
                    //update mvnoList
                    List<Integer> mvnoOldList;
                    if(!mvnosList.isEmpty()){
                        mvnoOldList = Arrays.stream(mvnosList.split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                        serviceAreaService.updateServiceArea(mvnoOldList,serviceArea.getSiteName(),serviceArea);
                    } else {
                        mvnoOldList = new ArrayList<Integer>();
                    }
                    List<Integer> mvnoNewList = entityDTO.getMvnoIds();

                    if (mvnoNewList != mvnoOldList) {
                        if (!mvnoOldList.isEmpty() || !mvnoNewList.isEmpty()) {
                            // Find elements that are in oldList but not in newList (deletedList)
                            List<Integer> deletedList = mvnoOldList.stream()
                                    .filter(element -> !mvnoNewList.contains(element))
                                    .collect(Collectors.toList());

                            // Find elements that are in newList but not in oldList (createdList)
                            List<Integer> createdList = mvnoNewList.stream()
                                    .filter(element -> !mvnoOldList.contains(element))
                                    .collect(Collectors.toList());

                            serviceAreaService.updateServiceAreaBasedOnMvnoListUpdate(deletedList,createdList,serviceArea.getSiteName(),serviceArea);

                        }
                    }
                }else{
                    List<Integer> mvnoList = entityDTO.getMvnoIds();
                    serviceAreaService.updateServiceAreaBasedOnMvnoListUpdate(null,mvnoList,serviceArea.getSiteName(),serviceArea);

                }

                serviceAreaService.sendServiceAreaToAllMicroServiceWhenUpdate(serviceArea);
                serviceArea.setLocationIds(entityDTO.getLocationIds());
                List<ServiceareaLocationMappingMessage> locationMappingMessages = new ArrayList<>();
                if (!serviceArea.getLocationIds().isEmpty()) {

                    for (Long locationId : serviceArea.getLocationIds()) {
                        if (!serviceAreaLocationMappingRepository.existsByServiceAreaIdAndLocationId(serviceArea.getId(), locationId)) {
                            // Only create a new instance if it doesn't exist
                            ServiceAreaLocationMapping serviceAreaLocationMapping = new ServiceAreaLocationMapping();
                            serviceAreaLocationMapping.setServiceAreaId(serviceArea.getId());
                            serviceAreaLocationMapping.setLocationId(locationId);
                            serviceAreaLocationMappingRepository.save(serviceAreaLocationMapping);

                            // Create and send the message after successful save
                            ServiceareaLocationMappingMessage locationServiceareaMappingMessage = new ServiceareaLocationMappingMessage();
                            locationServiceareaMappingMessage.setLocationId(locationId);
                            locationServiceareaMappingMessage.setServiceAreaId(serviceArea.getId());
                            locationMappingMessages.add(locationServiceareaMappingMessage);
//                            messageSender.send(locationServiceareaMappingMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA_LOCATION_MAPPING);
                            kafkaMessageSender.send(new KafkaMessageData(locationServiceareaMappingMessage,ServiceareaLocationMappingMessage.class.getSimpleName()));
                        }
                    }
                }
                if (!locationMappingMessages.isEmpty()) {
//                    messageSender.sendMapping(locationMappingMessages, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA_LOCATION_MAPPING);
                } else {
                    ServiceareaLocationMappingMessage locationServiceareaMappingMessage = new ServiceareaLocationMappingMessage();
                    locationServiceareaMappingMessage.setServiceAreaId(serviceArea.getId());
                    locationMappingMessages.add(locationServiceareaMappingMessage);
//                        messageSender.sendMapping(locationMappingMessages, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA_LOCATION_MAPPING);
                        kafkaMessageSender.send(new KafkaMessageData(locationMappingMessages,locationMappingMessages.getClass().getSimpleName()));
                    }
                ServiceArea updatedServiceArea = serviceAreaMapper.dtoToDomain(serviceArea, new CycleAvoidingMappingContext());

                dataDTO.setResponseMessage("Successfully Updated");
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update Area" +LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + " , Updated Area Service Details " + UpdateDiffFinder.getUpdatedDiff(oldClone,updatedServiceArea)+ LogConstants.LOG_STATUS +" "+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(MessageConstants.SERVICE_AREA_NAME_EXITS);
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_FAILED+ LogConstants.LOG_STATUS_CODE+respCode);
            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
        res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_ADD + "\")")
@PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SERVICE_AREA_CREATE + "\")")
@Override
public GenericDataDTO save(@Valid @RequestBody ServiceAreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
    GenericDataDTO dataDTO = new GenericDataDTO();
    Integer respCode = APIConstants.FAIL;
    HashMap<String, Object> response = new HashMap<>();
    TraceContext traceContext = tracer.currentSpan().context();
    MDC.put("type", "Create");
    MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
    MDC.put("traceId", traceContext.traceIdString());
    MDC.put("spanId", traceContext.spanIdString());
    long startTime = System.nanoTime();  // Start measuring
    try {
        boolean flag = serviceAreaService.duplicateVerifyAtSave(entityDTO.getName());
        boolean isAddPolygon = serviceAreaRepository.existsBySiteNameAndMvnoId(entityDTO.getSiteName(), 1);
        Integer currentMvnoId = getMvnoIdFromCurrentStaff();

        boolean staffServiceMap = false;
        if (flag) {
            if (currentMvnoId != null) {
                entityDTO.setMvnoId(currentMvnoId);
            }
            ServiceAreaDTO serviceAreaDTO = serviceAreaService.setDataforServicAreaExcludingPolygone(entityDTO);

            if (entityDTO.getPincodes() == null || entityDTO.getPincodes().isEmpty() || entityDTO.getPincodes().stream().allMatch(Objects::isNull)) {
                dataDTO.setResponseCode(HttpStatus.BAD_REQUEST.value());
                dataDTO.setResponseMessage("Pincodes cannot be null or empty.");
                return dataDTO;
            }
            List<Long> longPincodes = entityDTO.getPincodes().stream().filter(Objects::nonNull).map(Integer::longValue).collect(Collectors.toList());
            List<Long> pincode = pincodeRepository.findExistingIdsByIds(longPincodes);
            if (pincode.size() != longPincodes.size()) {
                dataDTO.setResponseCode(HttpStatus.BAD_REQUEST.value());
                dataDTO.setResponseMessage("pin-code Is not found In System.");
                log.warn("pin-code:{} Is not Found In System.", longPincodes);
                return dataDTO;
            }
            List<PincodeMvnoDto> matchedPincodes = pincodeRepository.findAllById(pincode, currentMvnoId);
            if (matchedPincodes.size() != longPincodes.size()) {
                dataDTO.setResponseCode(HttpStatus.BAD_REQUEST.value());
                dataDTO.setResponseMessage("pin-code Is not valid for this current LoggedInUser.");
                log.warn("pin-code:{} Is not valid for this current LoggedInUser.{}",pincode,currentMvnoId);
                return dataDTO;
            }
            String mvnoLists = "";
            if (serviceAreaDTO.getMvnoIds() != null) {
                mvnoLists = serviceAreaDTO.getMvnoIds().stream().map(String::valueOf).collect(Collectors.joining(","));
            } else {
                mvnoLists = null;
            }
            serviceAreaDTO.setMvnoLists(mvnoLists);
            dataDTO = super.save(serviceAreaDTO, result, authentication, req,res);
            ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
            serviceArea.setLocationIds(entityDTO.getLocationIds());
            if (!entityDTO.getPolyGoneList().isEmpty() && entityDTO.getPolyGoneList().size() > 0) {
                boolean isSameNamePolyGoneExist = serviceAreaService.validatePolygoneSave(entityDTO.getPolyGoneList().get(0).getPolygoneName(), serviceArea.getMvnoId(), serviceArea.getId().intValue());
                boolean duplicateNameCheck = entityDTO.getPolyGoneList()
                        .stream()
                        .collect(Collectors.groupingBy(o -> o.getPolyOrder() + "-" + o.getPolygoneName())).values().stream().anyMatch(polyGoneList -> polyGoneList.size() > 1);
                //saving polygone details
                if (!isAddPolygon && !isSameNamePolyGoneExist && !duplicateNameCheck) {
                    serviceAreaService.savePoliGonList(entityDTO, serviceArea);
                } else {
                    dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    dataDTO.setResponseMessage("You can't save polygone with same name, Which already exist in the system");
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "You can't save polygone with same name, Which already exist in the system", null);
                }
            }
            staffServiceMap = serviceAreaService.saveStaffUserServiceAreaMapping(serviceArea, staffServiceMap);
            serviceAreaService.sendServiceAreaToOtherMicroserviceWhenSave(serviceArea, staffServiceMap);
            serviceAreaService.saveMVNOIdsList(serviceArea, staffServiceMap);
            dataDTO.setResponseMessage("Successfully Created");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create service area" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } else {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(MessageConstants.SERVICE_AREA_NAME_EXITS);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create service area" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + " Service area with same name already exist " + LogConstants.LOG_STATUS_CODE + respCode);
        }
    } catch (DataIntegrityViolationException ex) {
        respCode = HttpStatus.NOT_ACCEPTABLE.value();
        response.put(APIConstants.ERROR_TAG, "Input Size Exceeded");
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create service area" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
    } catch (CustomValidationException ce) {
        respCode = ce.getErrCode();
        response.put(APIConstants.ERROR_TAG, ce.getMessage());
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create service area" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
    } catch (Exception ex) {
        respCode = HttpStatus.EXPECTATION_FAILED.value();
        response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create service area" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);

    } finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
        long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
        res.addHeader("Server-Timing", "app;dur=" + durationInMs);
    }
    return dataDTO;
}


    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req,res);
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String,Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        dataDTO.setData(serviceAreaService.getEntityByServiceAreaId(Long.valueOf(id)));
        dataDTO.setTotalRecords(1);
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring

       try {
           ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
               ServiceAreaDTO updatedServiceAreaWithBranchId = serviceAreaService.setBranchIdInServiceAreaDTO(serviceArea);
           if(serviceArea.getMvnoLists() != null && !serviceArea.getMvnoLists().isEmpty()){
               updatedServiceAreaWithBranchId.setMvnoIds(Arrays.stream(serviceArea.getMvnoLists().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
           }
        //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_VIEW, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
        if(updatedServiceAreaWithBranchId!=null){
            dataDTO.setData(updatedServiceAreaWithBranchId);
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch service area"+ LogConstants.REQUEST_BY +serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_INFO + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
       }catch (CustomValidationException ce) {
           LOGGER.error(ce.getMessage(),ce);
           RESP_CODE = ce.getErrCode();
           response.put(APIConstants.ERROR_TAG, ce.getMessage());
           LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetch service area" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
       } catch (Exception ex) {
           LOGGER.error(ex.getMessage(),ex);
           RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
           response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
           LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch service area"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser() .getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
       }finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           MDC.remove("spanId");
           long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
           res.addHeader("Server-Timing", "app;dur=" + durationInMs);
       }
        return dataDTO;

    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            return super.getAll(requestDTO,req,res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    // Get All Service Area List By UserStaff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
    @GetMapping("/getAllServiceAreaByStaff")
    public GenericDataDTO getAllServiceAreaByStaff(HttpServletRequest req){
        HashMap<String,Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            RESP_CODE = APIConstants.SUCCESS;
            genericDataDTO.setDataList(serviceAreaService.getAllServiceAreaByStaffId());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetch All service area list"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            LOGGER.error(ce.getMessage(),ce);
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All service area list" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser() .getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }
        catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch All service area list"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Get StaffIds by ServiceAreas
    @GetMapping("/getStaffUserByServiceArea")
    public GenericDataDTO getStaffUserByServiceArea(HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//            genericDataDTO.setDataList(staffUserService
//                    .getStaffUserByServiceArea());
//            genericDataDTO.setTotalRecords(staffUserService
//                    .getStaffUserByServiceArea().size());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All service area list"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }
        catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All service area list " + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/viewStaffUserByServiceArea")
    public GenericDataDTO viewStaffUserByServiceArea(HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//            genericDataDTO.setDataList(staffUserService
//                    .viewStaffUserByServiceArea());
//            genericDataDTO.setTotalRecords(staffUserService
//                    .viewStaffUserByServiceArea().size());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All service area list" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }
        catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All service area list" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @GetMapping("/getPincodefromCity")
    public GenericDataDTO getpincodefromcity(@RequestParam("id") Integer id , HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            RESP_CODE = APIConstants.SUCCESS;
            genericDataDTO.setDataList(serviceAreaService.getPincodefromcity(id));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch picode from city"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);

        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch picode from city"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @ApiOperation(value = "This API will fetch pincode from specific parameters like id, pincode")
    @GetMapping("/getPincodefromCity/withSpecificParameter")
    public GenericDataDTO getpincodefromcityWithSpecificParameter(@RequestParam("id") Integer id , HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            RESP_CODE = APIConstants.SUCCESS;
            genericDataDTO.setDataList(serviceAreaService.getpincodefromcityWithSpecificParameter(id));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch picode from city"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);

        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch picode from city"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

    @PostMapping("/getAllServicesByServiceAreaId")
    public GenericDataDTO getAllServicesByServiceAreaId(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            //genericDataDTO.setDataList(serviceAreaService.getAllServicebyServiceAreaId(serviceAreaId));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch service area"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch all service area" +LogConstants.LOG_BY_NAME+ entityDTO.getName() + LogConstants.REQUEST_BY +serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
    @GetMapping("/serviceAreaListWhereBranchIsNotBind")
    public GenericDataDTO getAllserviceAreaListWhereBranchIsNotBind(HttpServletRequest req,HttpServletResponse res){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        long startTime = System.nanoTime();  // Start measuring

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            List<ServiceAreaDTOProjection> serviceAreaDTOList = serviceAreaService.serviceAreaIdListWhereBranchIsNotBind();
            genericDataDTO.setDataList(serviceAreaDTOList);
            genericDataDTO.setTotalRecords(serviceAreaDTOList.size());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch Service Area List Where Branch Is Not Bind" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Fetch Service Area List Where Branch Is Not Bind" +  LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getPlaceId")
    public ResponseEntity<?> getLocation(@Valid @RequestParam String query, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> resp = new HashMap<>();
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        try {
            HashMap<String, Object> response = new HashMap<>();
            response = googleMaps.getPlaces(query);
            RESP_CODE = Integer.parseInt(response.get("code").toString());
            MDC.remove("type");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch place" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return apiResponse(RESP_CODE, response);
        } catch (CustomValidationException ex) {
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"fetch place" +  LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
            ///e.printStackTrace();
            throw new CustomValidationException(ex.getErrCode(),ex.getMessage(),null);
        } catch (Exception e) {
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"fetch place" +  LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
            ///e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
    @GetMapping(value = "/getLatitudeAndLongitude")
    public ResponseEntity<?> getLatitudeAndLongitude(@Valid @RequestParam String placeId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> resp = new HashMap<>();
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        try {
            HashMap<String, Object> response = new HashMap<>();
            response = googleMaps.getLatitudeAndLongitude(placeId);
            RESP_CODE = Integer.parseInt(response.get("code").toString());
            MDC.remove("type");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch lattitude and longitude of location" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return apiResponse(RESP_CODE, response);
        } catch (Exception e) {
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch lattitude and longitude of location" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            //e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @GetMapping(value = "/getServiceAreasBylatlog")
    public ResponseEntity<?> getServiceAreaByLatitudeAndLongitude(@RequestParam Double lat, @RequestParam Double longt, @RequestParam(required = false) Integer mvnoId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            HashMap<String, Object> response = new HashMap<>();
            List<ServiceAreaDTO> list = serviceAreaService.getServiceAreaIdByLatAndLong(lat, longt,mvnoId);
            RESP_CODE = 200;
            if(!CollectionUtils.isEmpty(list)) {
                list.sort(Comparator.comparing(ServiceAreaDTO::getRadiusDis));
                response.put("list",list.stream().map(ServiceAreaDTO::getId).collect(Collectors.toList()));
                response.put("dataList",list);
                response.put("code",200);
            } else {
                response.put("dataList",new ArrayList<>());
                response.put("code",404);
            }
            MDC.remove("type");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch Service Area by lattitude and longitude of location" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return apiResponse(RESP_CODE, response);
        } catch (Exception e) {
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch Service Area by lattitude and longitude of location" + LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            //e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SERVICE_AREA_EDIT + "\")")
    @PostMapping("/getListServiceAreaData")
    public GenericDataDTO seviceArealistData(@RequestBody List<Long> serviceAreaIds, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<ServiceArea> serviceAreaList = new ArrayList<>();
            List<ServiceArea> serviceAreas = serviceAreaService.getRepository().findAllById(serviceAreaIds);
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage("Success");
            dataDTO.setDataList(serviceAreas);
            dataDTO.setTotalRecords(serviceAreas.size());
        return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
           // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
           // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return dataDTO;
    }

    @GetMapping("/isSiteNameExists/{siteName}")
    public GenericDataDTO checkSiteNameExistsByMvno(@PathVariable(name = "siteName") String siteName, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            boolean siteNameExists = serviceAreaService.isSiteNameExists(siteName, getMvnoIdFromCurrentStaff());
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage("Success");
            dataDTO.setData(siteNameExists);
            return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SERVICE_AREA + "\")")
    @GetMapping("/getPolygonFromServiceArea/{siteName}")
    public GenericDataDTO getPolygoneBySiteName(@PathVariable(name = "siteName") String siteName, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            List<ServiceArea> serviceAreaList = new ArrayList<>();
            List<PolyGone> polyGoneList = serviceAreaService.getPolygoneFromSitename(siteName);
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage("Success");
            dataDTO.setDataList(polyGoneList);
            return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return dataDTO;
    }

    @PostMapping(value = "/uploadcordinates/{serviceAreaId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO processUploadFile(@RequestParam MultipartFile file, @PathVariable(name = "serviceAreaId") Long serviceAreaId, HttpServletRequest req) throws IOException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        LoggedInUser loggedInUser = serviceAreaService.getLoggedInUser();
        MDC.put("type","Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        try {
            serviceAreaService.uploadPolygonCordinate(file, serviceAreaId);
            genericDataDTO.setData("success..!");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getCsvFromPolygon/{siteName}")
    public GenericDataDTO getCsvFromPolygoneBySiteName(@PathVariable(name = "siteName") String siteName, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        try {
            String data = serviceAreaService.downloadCsvFile(siteName);
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage("Success");
            dataDTO.setData(data);
            return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return dataDTO;
    }
    @GetMapping("/site/all")
    public ResponseEntity<?> getAllSiteName(HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
         MDC.put("type", "Fetch");
        MDC.put("userName",serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();

        try {
            List<String> siteName=serviceAreaRepository.findsiteNameBymvnoId(getMvnoIdFromCurrentStaff());
            response.put("SiteName",siteName);
            RESP_CODE = APIConstants.SUCCESS;
        }
        catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }
    @GetMapping("/getLocationFromServiceArea")
    public GenericDataDTO getLocationFromServiceArea(@RequestParam("id") Long id , HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        ServiceAreaDTO entityDTO = new ServiceAreaDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            RESP_CODE = APIConstants.SUCCESS;
            genericDataDTO.setDataList(serviceAreaService.getLocationByServiceArea(id));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch picode from city"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);

        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch picode from city"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    @PostMapping("/all")
    public GenericDataDTO seviceArealistData(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            dataDTO = serviceAreaService.getAllEntitiesWithPagination(requestDTO);
            return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return dataDTO;
    }

    @PostMapping("/all/activeAndUnderDevelopment")
    public GenericDataDTO seviceArealistDataActiveAndUnderDevelopment(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            dataDTO = serviceAreaService.getAllEntitiesWithPaginationActiveAndUd(requestDTO);
            return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);

        }

        return dataDTO;
    }

    @PostMapping("/all/byStatus")
    public GenericDataDTO seviceArealistDataActive(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            dataDTO = serviceAreaService.getAllEntitiesWithPaginationDynamicStatus(requestDTO);
            return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);

        }

        return dataDTO;
    }


    @GetMapping("/dropdown/all")
    public GenericDataDTO getServiceAreafordropdown(HttpServletRequest req , HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage("Success");
            dataDTO.setDataList(serviceAreaService.getAllEntitiesForDropdown());
            dataDTO.setTotalRecords(serviceAreaService.getAllEntitiesForDropdown().size());
            return dataDTO;
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = e.getErrCode();
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            // LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update service area"+LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return dataDTO;
    }

    @GetMapping("/dropdown/all/caf/customer")
    public GenericDataDTO getAllWithoutPaginationCafCustomerForDropdown() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(serviceAreaService.getAllEntitiesForCafCustomerDropdown());
            genericDataDTO.setTotalRecords(serviceAreaService.getAllEntitiesForCafCustomerDropdown().size());
            //    logger.info("Fetching Sevice area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            //   logger.error("Unable to fetch data without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
        }

        return genericDataDTO;
    }

    @PostMapping("/assignToStaff")
    public GenericDataDTO assignToStaff(@RequestBody AssignServiceArea assignServiceArea, HttpServletRequest req,HttpServletResponse res){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Fetch");
        MDC.put("userName", serviceAreaService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        serviceAreaService.assignStaffToServiceArea(assignServiceArea.getServiceAreaId(), assignServiceArea.getStaffIds());
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            //genericDataDTO.setDataList(serviceAreaService.getAllServicebyServiceAreaId(serviceAreaId));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Assign Service Area to Staff"+ LogConstants.REQUEST_BY + serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign Service Area to Staff" +LogConstants.LOG_BY_NAME+ assignServiceArea.getStaffIds() + LogConstants.REQUEST_BY +serviceAreaService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }
}
