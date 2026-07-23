package com.savbill.partnermanagement.modules.PriceGroup.controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.partnermanagement.auditLog.service.AuditLogService;
import com.savbill.partnermanagement.constants.*;
import com.savbill.partnermanagement.constants.*;
import com.savbill.partnermanagement.core.constants.UrlConstants;
import com.savbill.partnermanagement.core.controller.ExBaseAbstractController2;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.GenericSearchDTO;
import com.savbill.partnermanagement.core.dto.PaginationRequestDTO;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBook;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.savbill.partnermanagement.modules.PriceGroup.mapper.PriceBookMapper;
import com.savbill.partnermanagement.modules.PriceGroup.model.PriceBookDTO;
//import com.savbill.partnermanagement.modules.PriceGroup.repository.PriceBookPlanDtlRepository;
//import com.savbill.partnermanagement.modules.PriceGroup.service.PriceBookPlanDetailService;
import com.savbill.partnermanagement.modules.PriceGroup.service.PriceBookService;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import com.savbill.partnermanagement.utils.UpdateDiffFinder;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL +UrlConstants.PARTNER_CONTROLLER +UrlConstants.PRICE_BOOK)
public class PriceBookController extends ExBaseAbstractController2<PriceBookDTO> {
    public PriceBookController(PriceBookService service) {
        super(service);
    }
    private static String MODULE = " [PriceBookController] ";
    @Autowired
    private PriceBookService priceBookService;
    @Autowired
    private AuditLogService auditLogService;
//    @Autowired
//    private PriceBookPlanDetailService priceBookPlanDetailService;
//    @Autowired
//    private PriceBookPlanDtlRepository priceBookPlanDtlRepository;
    @Autowired
    private CreateDataSharedService createDataSharedService;
    @Autowired
    PriceBookMapper priceBookMapper;
    @Autowired
    private Tracer tracer;

    private final Logger log = Logger.getLogger(PriceBookController.class);

    @GetMapping("/active")
    public GenericDataDTO getAllActive(HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<PriceBookDTO> priceBookDTOList = priceBookService.getAllActive();
            genericDataDTO.setDataList(priceBookDTOList);
            genericDataDTO.setTotalRecords(priceBookDTOList.size());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetching All Active price" +  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetching All Active price" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_BUNDLE_CREATE + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody PriceBookDTO entityDTO, BindingResult result, Authentication authentication,HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Create");
//        MDC.put("userName", getLoggedInUser().getUsername());
//        MDC.put("traceId",traceContext.traceIdString());
//        MDC.put("spanId",traceContext.spanIdString());
//        Integer RESP_CODE = APIConstants.FAIL;
//        PlanGroup planGroup = null;
//        try {
//            boolean flag = priceBookService.duplicateVerifyAtSave(entityDTO.getBookname());
//            PriceBookDTO priceBookDTO = null;
//            if (flag)
//            {
//            	if(getMvnoIdFromCurrentStaff() != null) {
//            		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//
//                    if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
//                    {
//                        throw new CustomValidationException(APIConstants.FAIL , Constants.AVOID_SAVE_MULTIPLE_BU , null);
//                    }
//                    if(getBUIdsFromCurrentStaff().size()  == 1)
//                    {
//                        entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
//                    }
//                }
//                entityDTO=priceBookService.checkAndUpdateAllPlanSelected(entityDTO);
//
//                entityDTO=priceBookService.checkAndUpdateAllPlangroupSelected(entityDTO);
//
//                priceBookService.validateSaveOrUpdateData(entityDTO);
//                dataDTO = super.save(entityDTO, result, authentication,req);
//                dataDTO.setResponseMessage("Partner plan bundle created successfully");
//                priceBookDTO = (PriceBookDTO) dataDTO.getData();
//                createDataSharedService.sendEntitySaveDataForAllMicroService(priceBookMapper.dtoToDomain((PriceBookDTO) dataDTO.getData(),new CycleAvoidingMappingContext()));
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRICE_BOOK,
//                        AclConstants.OPERATION_PRICE_BOOK_ADD, req.getRemoteAddr(), null, entityDTO.getId(), entityDTO.getBookname());
//                RESP_CODE = APIConstants.SUCCESS;
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "create partner plan bundle" +LogConstants.LOG_BY_NAME+ entityDTO.getBookname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            }
//            else
//            {
//                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                dataDTO.setResponseMessage(MessageConstants.PRICEBOOK_NAME_EXITS);
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR + "create partner plan bundle"+LogConstants.LOG_BY_NAME+ entityDTO.getBookname() + LogConstants.REQUEST_BY +getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Partner with same name already exist"+LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            }
//        }catch(CustomValidationException e)
//        {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(e.getMessage());
//            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//            log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR + "create partner plan bundle"+LogConstants.LOG_BY_NAME+ entityDTO.getBookname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        }
//        catch (Exception ex) {
//            ApplicationLogger.logger.error(ex.getMessage(), ex);
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(ex.getMessage());
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR + "create partner plan bundle"+LogConstants.LOG_BY_NAME+ entityDTO.getBookname() +LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        }
//        finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return dataDTO;
//    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_BUNDLE_EDIT+ "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody PriceBookDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            priceBookService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = priceBookService.duplicateVerifyAtEdit(entityDTO.getBookname(), entityDTO.getId().intValue());
            priceBookService.validateSaveOrUpdateData(entityDTO);
            if (flag) {
                PriceBook olddata = priceBookService.getRepository().getOne(entityDTO.getId());
                PriceBookDTO olddatadto = priceBookMapper.domainToDTO(olddata , new CycleAvoidingMappingContext());

                dataDTO = super.update(entityDTO, result, authentication, req);
                dataDTO.setResponseMessage("Partner plan bundle updated successfully");
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRICE_BOOK, AclConstants.OPERATION_PRICE_BOOK_EDIT, req.getRemoteAddr(), null, entityDTO.getId(), entityDTO.getBookname());
                //createDataSharedService.updateEntityDataForAllMicroService(priceBookMapper.dtoToDomain((PriceBookDTO) dataDTO.getData(),new CycleAvoidingMappingContext()));
                RESP_CODE = APIConstants.SUCCESS;
                if (olddatadto != null)
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Partner Bundle" + LogConstants.LOG_BY_NAME + entityDTO.getBookname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.PRICEBOOK_NAME_EXITS);
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Partner Bundle"+LogConstants.LOG_BY_NAME+entityDTO.getBookname()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            dataDTO.setResponseCode(ce.getErrCode());
            dataDTO.setResponseMessage(ce.getMessage());
            RESP_CODE = ce.getErrCode();
            log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Partner Bundle"+LogConstants.LOG_BY_NAME+entityDTO.getBookname()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Partner Bundle"+LogConstants.LOG_BY_NAME+entityDTO.getBookname()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_BUNDLE_DELETE +  "\")")
    @Override
    public GenericDataDTO delete(@Valid @RequestBody PriceBookDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        try{
            priceBookService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = priceBookService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req);
                dataDTO.setResponseMessage("partner plan bundle delete successfully");
                PriceBookDTO priceBookDTO = (PriceBookDTO) dataDTO.getData();
                priceBookDTO.setIsDeleted(true);
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRICE_BOOK, AclConstants.OPERATION_PRICE_BOOK_DELETE, req.getRemoteAddr(), null, priceBookDTO.getId(), priceBookDTO.getBookname());
                //createDataSharedService.updateEntityDataForAllMicroService(priceBookMapper.dtoToDomain(priceBookDTO,new CycleAvoidingMappingContext()));
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+ "delete Partner bundle for id : " +entityDTO.getId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(DeleteContant.PLAN_BUNDLE_EXIST);
            }
        } catch (CustomValidationException ex) {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = ex.getErrCode();
            log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+ "delete Partner bundle for id : " +entityDTO.getId()  + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex){
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete Partner bundle for id : " +entityDTO.getId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_PLAN_BUNDLE + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter,req);
            if(!genericDataDTO.getDataList().isEmpty())
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Search Pricebook By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            else
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Pricebook By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_INFO + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
        }catch (Exception ex){
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Search Pricebook By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ LogConstants.LOG_NO_RECORD_FOUND+ APIConstants.ERROR_MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return super.search(page, pageSize, sortOrder, sortBy, filter,req);
    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_PLAN_BUNDLE +  "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = super.getEntityById(id, req);
        PriceBookDTO priceBook = (PriceBookDTO) genericDataDTO.getData();
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRICE_BOOK, AclConstants.OPERATION_PRICE_BOOK_DELETE, req.getRemoteAddr(), null, priceBook.getId(), priceBook.getBookname());
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_PLAN_BUNDLE + "\")")
    @Override
    @PostMapping("/list")
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.getAll(requestDTO,req);
            if(!genericDataDTO.getDataList().isEmpty())
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch pricebook"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            else
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch pricebook" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
        }catch (Exception ex){
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch pricebook"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ LogConstants.LOG_NO_RECORD_FOUND+ APIConstants.ERROR_MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


//    @PostMapping(value = "/priceBookPlanDtl/{pricebookDtlId}")
//    public GenericDataDTO getPriceBookPlanDetails(@PathVariable Long pricebookDtlId,HttpServletRequest req) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Fetch");
//        MDC.put("userName", getLoggedInUser().getUsername());
//        MDC.put("traceId",traceContext.traceIdString());
//        MDC.put("spanId",traceContext.spanIdString());
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + " [getCustomerVoice()] ";
//        try {
//            if (pricebookDtlId == null) {
//                genericDataDTO.setResponseMessage("Please provide customer id!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                RESP_CODE = APIConstants.NOT_FOUND;
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch priceBook plan details for pricebookId : "+ pricebookDtlId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);
//                return genericDataDTO;
//            }
////            PriceBookPlanDetail priceBookPlanDetail = priceBookPlanDtlRepository.getOne(pricebookDtlId);
//            if (priceBookPlanDetail == null) {
//                genericDataDTO.setResponseMessage("PriceBookPLanDetail not found!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                RESP_CODE = APIConstants.NOT_FOUND;
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch priceBook plan details for pricebookId : "+ pricebookDtlId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);
//                return genericDataDTO;
//            }
//            priceBookPlanDetailService.planIsDelete(priceBookPlanDetail);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            RESP_CODE = APIConstants.SUCCESS;
//            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch priceBook plan details for pricebookId : "+ pricebookDtlId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch priceBook plan details for pricebookId : "+ pricebookDtlId+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        }
//        finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }

    @Override
    public String getModuleNameForLog() {
        return "PriceBook Controller";
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }
}
