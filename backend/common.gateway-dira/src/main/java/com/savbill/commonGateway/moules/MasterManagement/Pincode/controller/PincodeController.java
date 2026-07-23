package com.savbill.commonGateway.moules.MasterManagement.Pincode.controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.mapper.PincodeMapper;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeDTO;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.service.PincodeService;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PINCODE)
public class PincodeController extends ExBaseAbstractController<PincodeDTO> {
    private static String MODULE = " [PincodeController] ";
//    @Autowired
//    private AuditLogService auditLogService;

    @Autowired
    private PincodeService pincodeService;

    @Autowired
    private MessageSender messageSender;


    @Autowired
    private CreateDataSharedService createDataSharedService;

    @Autowired
    private PincodeMapper pincodeMapper;

    @Autowired
    private  Tracer tracer;

    private static  final Logger LOGGER = LoggerFactory.getLogger(PincodeController.class);

    public PincodeController(PincodeService service) {
        super(service);
    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.PINCODE + "\")")
    @GetMapping(value = "/getDetailsByPin/{pincode}")
    public GenericDataDTO getDetailsByPin(@PathVariable String pincode, HttpServletRequest req) {
        String SUBMODULE = " [getDetailsByPin()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",pincodeService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        PincodeDTO pincodeDTO = new PincodeDTO();
        try {
            if (null == pincode) {
                genericDataDTO.setResponseMessage("Please provide pincode!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE=APIConstants.FAIL;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all pincode list"+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(pincodeService.getDetailsByPin(pincode));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            RESP_CODE=APIConstants.SUCCESS;

            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all pincode list"+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                LOGGER.error(LogConstants.REQUEST_FROM+req.getHeader("request form")+LogConstants.REQUEST_FOR+"fetch All pincode list"+LogConstants.REQUEST_BY+ pincodeService.getLoggedInUser().getUsername()+LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE +RESP_CODE);
                return genericDataDTO;
            }
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM+req.getHeader("request form")+LogConstants.REQUEST_FOR+"fetch All pincode list"+LogConstants.REQUEST_BY+ pincodeService.getLoggedInUser().getUsername()+LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() +LogConstants.LOG_STATUS_CODE+ RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.PINCODE + "\")")
    @GetMapping("/search")
    public GenericDataDTO getPincodeBySearch(@RequestParam(name = "s", defaultValue = "") String s1,HttpServletRequest req,PaginationRequestDTO requestDTO ,GenericSearchDTO genericSearchDTO,HttpServletResponse res) {
        String SUBMODULE = getModuleNameForLog() + " [getPincodeBySearch()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        int RESP_CODE = APIConstants.FAIL;
            TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "search");
        MDC.put("userName", pincodeService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.spanIdString());
        MDC.put("spanId", traceContext.traceIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
//            if ("".equals(s1)) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Please provide search criteria!");
//                return genericDataDTO;
//            }
            genericDataDTO = GenericDataDTO.getGenericDataDTO(pincodeService.getAllPincodeBySearch(s1));
            if (null != genericDataDTO) {

                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search pincode By Keyword : " + s1+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
                }
                RESP_CODE = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "serach pincode By Keyword : " + s1 + LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
        } catch (RuntimeException re) {
            LOGGER.error(re.getMessage(), re);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search pincode using keyword : " + s1+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            throw re;
        } catch (Exception ex) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search pincode using keyword : " + s1+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_VIEW + "\")")

    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req, HttpServletResponse res) {
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


//    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.PINCODE + "\")")
    @GetMapping("/getAll")
    public GenericDataDTO findAll(HttpServletRequest req, HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        int RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "search");
        MDC.put("userName", pincodeService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.spanIdString());
        MDC.put("spanId", traceContext.traceIdString());
        long startTime = System.nanoTime();  // Start measuring
        try{
            genericDataDTO = GenericDataDTO.getGenericDataDTO(pincodeService.findAllPincode());
            if (null != genericDataDTO) {

                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request By: " + LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
                }
                RESP_CODE = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request By: "+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
        } catch(Exception ex) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +" Request by: " + LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            return genericDataDTO;
        } finally{
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }
    //@PreAuthorize("validatePermission(\"" + MenuConstants.Masters.PINCODE + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
//            GenericDataDTO dataDTO = super.getEntityById(id, req,res);
//            PincodeDTO picodeDTO = (PincodeDTO) dataDTO.getData();
////        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PINCODE,
////                AclConstants.OPERATION_PINCODE_VIEW, req.getRemoteAddr(), null, picodeDTO.getPincodeid(), picodeDTO.getPincode());
//            return dataDTO;
            GenericDataDTO dataDTO = new GenericDataDTO();
            PincodeDTO pincodeRepository = pincodeService.getPincodeDTO(Long.valueOf(id));
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage("Success");
            dataDTO.setData(pincodeRepository);
            dataDTO.setTotalRecords(1);
            return dataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }
    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            return super.getAllWithoutPagination(req,res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    @Deprecated
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.PINCODE + "\")")
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
       TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Search");
        MDC.put("userName",pincodeService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO =super.search(page, pageSize, sortOrder, sortBy, filter , req,res);
            if (genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Pincode using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_FAILED+LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Pincode using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Pincode using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + APIConstants.EXPECTATION_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_ADD + "\")")

    /**
     * Create Pincode API
     * @Author Darshan
     * @param entityDTO
     * @param result
     * @param authentication
     * @param req
     * @return
     * @throws Exception
     */
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.PINCODE_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody PincodeDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        Map<String,Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "create");

        MDC.put("userName", pincodeService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try{
               if (getMvnoIdFromCurrentStaff() != null) {
                   entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
               }
//        boolean flag = pincodeService.duplicateVerifyAtSaveWithPincodeAndCityID(entityDTO.getPincode(),entityDTO.getCityId());
               boolean flag = pincodeService.duplicateVerification(entityDTO.getPincode(),entityDTO.getCityId(), null, CommonConstants.OPERATION_ADD);
               if (flag) {

                   dataDTO = super.save(entityDTO, result, authentication, req,res);
                   PincodeDTO pincodeDTO = (PincodeDTO) dataDTO.getData();
                   Pincode pincode = pincodeMapper.dtoToDomain(pincodeDTO,new CycleAvoidingMappingContext());
                   createDataSharedService.sendEntitySaveDataForAllMicroService(pincode);
                   //RabbitMq
                   //PincodeMessage pincodeMessage = new PincodeMessage(pincodeDTO);
                   //this.messageSender.send(pincodeMessage, RabbitMqConstants.QUEUE_PINCODE);

                   respCode=APIConstants.SUCCESS;
                   LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"create pincode" +LogConstants.LOG_BY_NAME+entityDTO.getPincode()+ LogConstants.REQUEST_BY +pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +respCode);

                   //       auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PINCODE,
//                    AclConstants.OPERATION_PINCODE_ADD, req.getRemoteAddr(), null, pincodeDTO.getPincodeid(), pincodeDTO.getPincode());
               } else {
                    respCode=APIConstants.FAIL;
                   dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                   dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                   LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create pincode" +LogConstants.LOG_BY_NAME+entityDTO.getPincode()+  LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Pincode with same name already exist"+LogConstants.LOG_STATUS_CODE+respCode);
               }
               return dataDTO;
           } catch (Exception ex) {
                   respCode = HttpStatus.EXPECTATION_FAILED.value();
                   response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                   LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create pincode"+LogConstants.LOG_BY_NAME+entityDTO.getPincode() +  LogConstants.REQUEST_BY +pincodeService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);

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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_EDIT + "\")")

    /**
     * Update Pincode API
     * @Author Darshan
     * @param entityDTO
     * @param result
     * @param authentication
     * @param req
     * @return
     * @throws Exception
     */
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.PINCODE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody PincodeDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
       TraceContext traceContext = tracer.currentSpan().context();
       int respCode = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName",pincodeService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        if(getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        Pincode old = pincodeService.getPinCodeById(entityDTO.getPincodeid());
        Pincode oldClone = new Pincode(old);

        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            pincodeService.getEntityForUpdateAndDelete(entityDTO.getPincodeid());
            //boolean flag = pincodeService.duplicateVerifyAtEdit(entityDTO.getPincode(), entityDTO.getPincodeid());
//            boolean flag = pincodeService.duplicateVerifyAtEdit(entityDTO.getPincode(), entityDTO.getPincodeid(), entityDTO.getCityId());
            boolean flag = pincodeService.duplicateVerification(entityDTO.getPincode(),entityDTO.getCityId(), entityDTO.getPincodeid(), CommonConstants.OPERATION_UPDATE);
            if (flag) {

                dataDTO = super.update(entityDTO, result, authentication, req,res);
                PincodeDTO pincodeDTO = (PincodeDTO) dataDTO.getData();
                //RabbitMq
                //PincodeMessage pincodeMessage = new PincodeMessage(pincodeDTO);
                //this.messageSender.send(pincodeMessage, RabbitMqConstants.QUEUE_PINCODE);
                Pincode pincode = pincodeMapper.dtoToDomain(pincodeDTO, new CycleAvoidingMappingContext());
                createDataSharedService.updateEntityDataForAllMicroService(pincode);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update pincode"+LogConstants.LOG_BY_NAME+entityDTO.getPincode() + LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + " , Updated pincode Details : " + UpdateDiffFinder.getUpdatedDiff(oldClone,pincode) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                if (pincodeDTO != null) {
//               auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PINCODE,
//                        AclConstants.OPERATION_PINCODE_EDIT, req.getRemoteAddr(), null, pincodeDTO.getPincodeid(), pincodeDTO.getPincode());
                }
            } else {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update pincode"+LogConstants.LOG_BY_NAME+entityDTO.getPincode()+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR + "Access denined for update operation "+LogConstants.LOG_STATUS_CODE+respCode);
            }
        } catch (CustomValidationException e) {
            respCode = e.getErrCode();
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update pincode"+LogConstants.LOG_BY_NAME+entityDTO.getPincode()+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update pincode"+LogConstants.LOG_BY_NAME+entityDTO.getPincode()+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.PINCODE_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody PincodeDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Delete");
        MDC.put("userName", pincodeService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            pincodeService.getEntityForUpdateAndDelete(entityDTO.getPincodeid());
            boolean flag = pincodeService.deleteVerification(entityDTO.getPincodeid().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req,res);
                PincodeDTO pincodeDTO = (PincodeDTO) dataDTO.getData();
                //RabbitMq
//            PincodeMessage pincodeMessage = new PincodeMessage(pincodeDTO);
//            this.messageSender.send(pincodeMessage, RabbitMqConstants.QUEUE_PINCODE);
                Pincode pincode = pincodeMapper.dtoToDomain(pincodeDTO, new CycleAvoidingMappingContext());
                pincode.setIsDeleted(true);
                createDataSharedService.updateEntityDataForAllMicroService(pincode);
                if (pincodeDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PINCODE,
//                        AclConstants.OPERATION_PINCODE_DELETE, req.getRemoteAddr(), null, pincodeDTO.getPincodeid(), pincodeDTO.getPincode());
                    respCode = APIConstants.SUCCESS;
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+" delete pincode By Id : " +entityDTO.getPincodeid()+LogConstants.LOG_BY_NAME+entityDTO.getPincode() + LogConstants.REQUEST_BY +pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
                }

                //PincodeMessage pincodeMessage = new PincodeMessage(pincodeDTO);
                //pincodeMessage.setIsDeleted(true);
                //this.messageSender.send(pincodeMessage, RabbitMqConstants.QUEUE_PINCODE);
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.PIN_CODE_DELETE_EXIST);
                respCode = HttpStatus.EXPECTATION_FAILED.value();
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete pincode"+LogConstants.LOG_BY_NAME+entityDTO.getPincode()+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+respCode);
            }
        }catch (CustomValidationException ce){
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"delete pincode"+LogConstants.LOG_BY_NAME+entityDTO.getPincode()+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        }catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"delete pincode"+LogConstants.LOG_BY_NAME+entityDTO.getPincode()+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[PincodeController]";
    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_VIEW + "\")")
    @PostMapping("/getPincodeListByServiceId")
    public GenericDataDTO getPincodeListByServiceId(@Valid @RequestBody List<Long> serviceAreaIds, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer respCode = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", pincodeService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [getPincodeListByServiceId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PincodeDTO pincodeDTO = new PincodeDTO();
        try{
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(pincodeService.getPincodeListByServiceId(serviceAreaIds));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all pincode list"+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+  LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all pincode list"+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);

        }
        return genericDataDTO;
    }



    @GetMapping("/getServicAreaIdByPincode")
    public GenericDataDTO getServicAreaIdByPincode(@RequestParam(name = "pincodeid", defaultValue = "") Long pincodeid, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer respCode = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", pincodeService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [getPincodeListByServiceId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PincodeDTO pincodeDTO = new PincodeDTO();
        try{
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setData(pincodeService.getServiceAreaIdForPincodeId(pincodeid));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch servicearea id "+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+  LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch service area id"+ LogConstants.REQUEST_BY + pincodeService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);

        }
        return genericDataDTO;
    }

    @GetMapping(path = "/findAllPincode")
    public GenericDataDTO getAllPincodes(HttpServletRequest req,HttpServletResponse res) {
        String SUBMODULE = " [getAllPincodes()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",pincodeService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        long startTime = System.nanoTime();  // Start measuring
        try {
            List<PincodeDTO> list = pincodeService.getAllPincodes().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || d.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 ).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            ApplicationLogger.logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            ApplicationLogger.logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

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
