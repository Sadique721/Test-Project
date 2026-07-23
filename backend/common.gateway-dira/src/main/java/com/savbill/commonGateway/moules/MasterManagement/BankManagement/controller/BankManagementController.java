 package com.savbill.commonGateway.moules.MasterManagement.BankManagement.controller;


 import brave.Tracer;
 import brave.propagation.TraceContext;
 import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
 import com.savbill.commonGateway.constants.*;
 import com.savbill.commonGateway.constants.*;
 import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
 import com.savbill.commonGateway.core.dto.*;
 import com.savbill.commonGateway.core.dto.*;
 import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
 import com.savbill.commonGateway.moules.MasterManagement.BankManagement.domain.BankManagement;
 import com.savbill.commonGateway.moules.MasterManagement.BankManagement.mapper.BankManagementMapper;
 import com.savbill.commonGateway.moules.MasterManagement.BankManagement.model.BankManagementDTO;
 import com.savbill.commonGateway.moules.MasterManagement.BankManagement.repository.BankManagementRepository;
 import com.savbill.commonGateway.moules.MasterManagement.BankManagement.service.BankManagementService;
 import com.savbill.commonGateway.utils.UpdateDiffFinder;
 import org.slf4j.LoggerFactory;
 import org.slf4j.MDC;
 import org.slf4j.Logger;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.HttpStatus;
 import org.springframework.security.access.prepost.PreAuthorize;
 import org.springframework.security.core.Authentication;
 import org.springframework.validation.BindingResult;
 import org.springframework.web.bind.annotation.*;

 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.validation.Valid;
 import java.util.List;
 import java.util.Optional;

 @RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BANK_MANAGEMENT)
public class BankManagementController  extends ExBaseAbstractController<BankManagementDTO> {

     private static final Logger LOGGER = LoggerFactory.getLogger(BankManagementController.class);

     private static String MODULE = " [BankManagementController] ";
     @Autowired
     BankManagementService bankManagementService;

     @Autowired
     private BankManagementRepository bankManagementRepository;

     @Autowired
     CreateDataSharedService createDataSharedService;

     public BankManagementController(BankManagementService service) {
         super(service);
     }

     @Autowired
     private Tracer tracer;
     @Autowired
     BankManagementMapper bankManagementMapper;
     @Override
     public String getModuleNameForLog() {
         return null;
     }

//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BANK + "\")")
     @Override
     public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req , HttpServletResponse res) {
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

//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_VIEW + "\")")
@PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BANK + "\")")
     @Override
     public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
    TraceContext traceContext = tracer.currentSpan().context();
    Integer respCode = APIConstants.FAIL;
    MDC.put("type", "Create");
    MDC.put("userName", bankManagementService.getLoggedInUser().getUsername());
    MDC.put("traceId", traceContext.traceIdString());
    MDC.put("spanId", traceContext.spanIdString());
    long startTime = System.nanoTime();  // Start measuring
    GenericDataDTO genericDataDTO = new GenericDataDTO();
    BankManagementDTO bankManagementDTO = new BankManagementDTO();

    try {
        genericDataDTO = super.getEntityById(id, req,res);

         bankManagementDTO = (BankManagementDTO) genericDataDTO.getData();
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Entity"+LogConstants.LOG_BY_NAME+bankManagementDTO.getBankname() + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);

//         auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BANK_UNIT,
//                 AclConstants.OPERATION_BANK_UNIT_VIEW, req.getRemoteAddr(), null, bankManagementDTO.getId(), bankManagementDTO.getBankname());
    } catch (Exception ex) {
        LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Entity"+LogConstants.LOG_BY_NAME+ bankManagementDTO.getBankname()+ LogConstants.REQUEST_BY + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_SUCCESS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);

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


//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BANK_CREATE + "\")")
     @Override
     public GenericDataDTO save(@Valid @RequestBody BankManagementDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Create");
        MDC.put("userName",bankManagementService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
         if (getMvnoIdFromCurrentStaff() != null) {
             entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
         }
         boolean flagBankName = bankManagementService.duplicateBankNameVerifyAtSave(entityDTO.getBankname(),entityDTO.getAccountnum());
         boolean flag = bankManagementService.duplicateVerifyAtSave(entityDTO.getAccountnum());
         if(entityDTO.getBanktype().equals("other")){
             flag = true;
         }
         if (flag && flagBankName) {
             dataDTO = super.save(entityDTO, result, authentication, req,res);
             BankManagementDTO bankManagementDTO = (BankManagementDTO) dataDTO.getData();
             createDataSharedService.sendEntitySaveDataForAllMicroService(bankManagementDTO);
//             auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BANK_UNIT,
//             AclConstants.OPERATION_BANK_UNIT_ADD, req.getRemoteAddr(), null, bankManagementDTO.getId(), bankManagementDTO.getAccountnum());
             respCode= APIConstants.SUCCESS;
             LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Create Bank"+LogConstants.LOG_BY_NAME+entityDTO.getBankname()+ LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode) ;
         } else {
             dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
             dataDTO.setResponseMessage(MessageConstants.BANK_NAME_EXITS);
             respCode = HttpStatus.NOT_ACCEPTABLE.value();
             LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Create Bank"+LogConstants.LOG_BY_NAME+entityDTO.getBankname()+LogConstants.REQUEST_BY + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername()+LogConstants.LOG_STATUS+ LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE+ respCode);
         }
            return dataDTO;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Create Bank"+LogConstants.LOG_BY_NAME+entityDTO+LogConstants.REQUEST_BY + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername()+LogConstants.LOG_SUCCESS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR+e.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);

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


//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BANK_EDIT + "\")")
     @Override
     public GenericDataDTO update(@Valid @RequestBody BankManagementDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {

        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName",bankManagementService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();

        BankManagement old = bankManagementService.getId(entityDTO.getId());
        BankManagement cloneold = new BankManagement(old);
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            Optional<BankManagement> bankManagement = bankManagementRepository.findById(entityDTO.getId());
            boolean flagBankName = bankManagementService.duplicateBankNameVerifyAtEdit(entityDTO.getBankname(), entityDTO.getId());
            boolean flag = true;
            if (entityDTO.getAccountnum() != null && !entityDTO.getAccountnum().equals(""))
                flag = bankManagementService.duplicateVerifyAtEdit(entityDTO.getAccountnum(), entityDTO.getId(), entityDTO.getBanktype());
            boolean flag2 = bankManagementService.deleteVerify(entityDTO.getId());
            if (entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getAccountnum() == null) {
                entityDTO.setAccountnum(bankManagement.get().getAccountnum());
            }
            if (entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getBankname() == null) {
                entityDTO.setBankname(bankManagement.get().getBankname());
            }
            if (entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getBankholdername() == null) {
                entityDTO.setBankholdername(bankManagement.get().getBankholdername());
            }
            if (entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getIfsccode() == null) {
                entityDTO.setIfsccode(bankManagement.get().getIfsccode());
            }
            if (entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getBanktype() == null) {
                entityDTO.setBanktype(bankManagement.get().getBanktype());
            }

            if (flag && flag2 && flagBankName) {

//                BankManagement oldClone = new BankManagement(old);

                BankManagementDTO bankManagementDTO = (BankManagementDTO) dataDTO.getData();
                BankManagement bankManagement1=bankManagementMapper.dtoToDomain(entityDTO,new CycleAvoidingMappingContext());
                dataDTO = super.update(entityDTO, result, authentication, req,res);
                createDataSharedService.updateEntityDataForAllMicroService(bankManagement1);

                if (bankManagementDTO != null) {
//                 auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BANK_UNIT,
//                         AclConstants.OPERATION_BANK_UNIT_EDIT, req.getRemoteAddr(), null, bankManagementDTO.getId(), bankManagementDTO.getAccountnum());
                 respCode = APIConstants.FAIL;
                }
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update Bank management : " + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + " , Updated BankManagement Details " +UpdateDiffFinder.getUpdatedDiff(cloneold,bankManagement1) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else if (!flag2) {
                Optional<BankManagement> bankManagement1 = bankManagementRepository.findById(entityDTO.getId());
                entityDTO.setAccountnum(bankManagement1.get().getAccountnum());
                entityDTO.setBankname(entityDTO.getBankname());
                respCode = HttpStatus.NOT_MODIFIED.value();
                dataDTO = super.update(entityDTO, result, authentication, req,res);
//                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"upadte banks  200" + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS + respCode);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update Bank management"+ LogConstants.LOG_BY_NAME +entityDTO.getBankname()+ LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + " Updated BankManagement Details " +UpdateDiffFinder.getUpdatedDiff(cloneold,bankManagement1) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.BANK_NAME_EXITS);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"upadte banks"+ LogConstants.LOG_BY_NAME + entityDTO.getBankname()+LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS + respCode);
            }

        }catch (Exception ex){
            respCode= HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"upadte banks"+ LogConstants.LOG_BY_NAME +entityDTO.getBankname()+ LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_FAILED +LogConstants.LOG_ERROR +ex.getMessage()+LogConstants.LOG_STATUS_CODE+ respCode);

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

//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BANK_DELETE + "\")")
     @Override
     public GenericDataDTO delete(@RequestBody BankManagementDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Delete");
        MDC.put("userName",bankManagementService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
         boolean flag = bankManagementService.deleteVerify(entityDTO.getId());
        long startTime = System.nanoTime();  // Start measuring
        try {
         if (flag) {
             dataDTO = super.delete(entityDTO, authentication, req,res);
             BankManagementDTO bankManagementDTO = (BankManagementDTO) dataDTO.getData();
             createDataSharedService.updateEntityDataForAllMicroService(bankManagementDTO);
             if (bankManagementDTO != null) {
//                 auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BANK_UNIT,
//                         AclConstants.OPERATION_BANK_UNIT_DELETE, req.getRemoteAddr(), null, bankManagementDTO.getId(), bankManagementDTO.getBankname());
                 respCode = APIConstants.SUCCESS;
                 LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"delete banks"+LogConstants.LOG_BY_NAME +entityDTO.getBankname()+ LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
             }
         } else {
             dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
             dataDTO.setResponseMessage(DeleteContant.BANK_DELETE_EXIST);
             respCode = HttpStatus.NOT_ACCEPTABLE.value();
             LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"delete banks"+LogConstants.LOG_BY_NAME + entityDTO.getBankname()+ LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_FAILED+LogConstants.LOG_STATUS_CODE+ respCode);
         }
         }catch (Exception ex){
             LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"delete banks"+LogConstants.LOG_BY_NAME + entityDTO.getBankname()+ LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_FAILED +APIConstants.ERROR_MESSAGE+ ex.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);

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
     @GetMapping("/searchByStatus")
     public GenericDataDTO getAllByStatus(HttpServletRequest req, @RequestParam(name="banktype",required=false) String banktype) {
         String SUBMODULE = getModuleNameForLog() + " [getALlByStatus] ";
         TraceContext traceContext = tracer.currentSpan().context();
         int respCode = APIConstants.FAIL;
         MDC.put("type", "Fetch");
         MDC.put("userName",bankManagementService.getLoggedInUser().getUsername());
         MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

         MDC.put("spanId",traceContext.spanIdString());
         GenericDataDTO genericDataDTO = new GenericDataDTO();
         GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
         try {
             if (banktype == null) {
                 genericDataDTO.setDataList(bankManagementService.findAllBankByStatus());
                 respCode=APIConstants.SUCCESS;
                 LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"Search Bank Status"+ LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + respCode);
             } else {
                 genericDataDTO.setDataList(bankManagementService.findAllBankByType(banktype));
                 respCode=HttpStatus.NOT_ACCEPTABLE.value();
                 LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"Search Bank Status"+ LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
             }
         } catch (Exception ex) {
             genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
             genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
             respCode=HttpStatus.EXPECTATION_FAILED.value();
             LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFroms")+LogConstants.REQUEST_FOR +"Search Bank Status"+LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +ex.getMessage()+LogConstants.LOG_STATUS_CODE + respCode);
             return genericDataDTO;
         }finally {
             MDC.remove("type");
             MDC.remove("userName");
             MDC.remove("traceId");
             MDC.remove("spanId");
         }
         return genericDataDTO;
     }

//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_VIEW + "\")")
        @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BANK + "\")")
     @Override
     public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
             , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
             , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
             , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
            TraceContext traceContext = tracer.currentSpan().context();
            GenericDataDTO dataDTO = new GenericDataDTO();
            int respCode = APIConstants.FAIL;
            MDC.put("type", "Search");
            MDC.put("userName",bankManagementService.getLoggedInUser().getUsername());
            MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

            MDC.put("spanId",traceContext.spanIdString());
            long startTime = System.nanoTime();  // Start measuring
            try {
                dataDTO=super.search(page, pageSize, sortOrder, sortBy, filter , req,res);
                if(dataDTO.getDataList().isEmpty()){
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search BankManagement using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+ LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);

                }
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search BankManagement using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
            }catch (Exception ex){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search BankManagement using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + bankManagementService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + APIConstants.EXPECTATION_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

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
     public ValidationData validateSearchCriteria(List<GenericSearchModel> filterList) {
         ValidationData validationData = new ValidationData();
         if (null == filterList || 0 < filterList.size()) {
             validationData.setValid(false);
             validationData.setMessage("Please Provide Search Criteria");
             return validationData;
         }
         validationData.setValid(true);
         return validationData;
     }
     int i =0;

     


//     public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
//         return apiResponse(responseCode, response, null);
//     }
//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_VIEW + "\")")
//     @PostMapping("/bankByName")
//     public ResponseEntity<?> searchParentCustomer(@RequestBody PaginationRequestDTO requestDTO) {
//         Integer RESP_CODE = APIConstants.FAIL;
//         HashMap<String, Object> response = new HashMap<>();
//         Page<BankManagement> bankList = null;
//         try {
//             requestDTO = setDefaultPaginationValues(requestDTO);
//             ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
//             if (validationData.isValid()) {
//                 RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                 response.put(APIConstants.ERROR_TAG, validationData.getStackTrace());
//                 return apiResponse(RESP_CODE, response);
//             }
//             CustomersService subscriberService = SpringContext.getBean(CustomersService.class);
//             parentCustomersList = subscriberService.searchParentCustomersByCustomerType(requestDTO.getFilters(),
//                     requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(),
//                     type, requestDTO.getStatus());
//             Integer Response = 0;
//             if (parentCustomersList.isEmpty()) {
//                 Response = APIConstants.NULL_VALUE;
//                 response.put(APIConstants.MESSAGE, "No Records Found!");
//                 return apiResponse(Response, response);
//
//             }
//             if (null != parentCustomersList && 0 < parentCustomersList.getSize()) {
//                 response.put("parentCustomerList", parentCustomersList.getContent().stream().map(data -> {
//                     try {
//                         return subscriberMapper.domainToDTO(data, new CycleAvoidingMappingContext());
//                     } catch (NoSuchFieldException e) {
//                         e.printStackTrace();
//                     }
//                     return null;
//                 }).collect(Collectors.toList()));
//             } else {
//                 response.put("parentCustomerList", new ArrayList<>());
//             }
//             RESP_CODE = APIConstants.SUCCESS;
//         } catch (CustomValidationException ce) {
//             ce.printStackTrace();
//             RESP_CODE = ce.getErrCode();
//             response.put(APIConstants.ERROR_TAG, ce.getStackTrace());
//         } catch (RuntimeException re) {
//             re.printStackTrace();
//             RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//             response.put(APIConstants.ERROR_TAG, re.getStackTrace());
//         } catch (Exception e) {
//             e.printStackTrace();
//             RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//             response.put(APIConstants.ERROR_TAG, e.getStackTrace());
//         }
//         return apiResponse(RESP_CODE, response, parentCustomersList);
//     }

 }







