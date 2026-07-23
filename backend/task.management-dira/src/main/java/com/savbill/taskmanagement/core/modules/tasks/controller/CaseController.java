package com.savbill.taskmanagement.core.modules.tasks.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.taskmanagement.core.constants.CaseConstants;
import com.savbill.taskmanagement.core.constants.LogConstants;
import com.savbill.taskmanagement.core.constants.MenuConstants;
import com.savbill.taskmanagement.core.controller.APIResponseController;
import com.savbill.taskmanagement.core.controller.ExBaseAbstractController;
import com.savbill.taskmanagement.core.dto.*;
import com.savbill.taskmanagement.core.dto.*;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.exceptions.DataNotFoundException;
import com.savbill.taskmanagement.core.modules.ClientServ.domain.ClientService;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Customers.dto.CustomerServicePlanDTO;
import com.savbill.taskmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamsRepository;
import com.savbill.taskmanagement.core.modules.TicketRemark.service.TicketRemarkService;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseDocDetails;
import com.savbill.taskmanagement.core.modules.tasks.domain.CustomerTaskFileMapping;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.*;
import com.savbill.taskmanagement.core.modules.tasks.model.*;
import com.savbill.taskmanagement.core.modules.tasks.repository.CustomerTaskFileMappingRepo;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseDocDetailsService;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseUpdateService;
import com.savbill.taskmanagement.core.modules.tasks.service.LiveCustomerNetworkDetailsService;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.modules.utils.Constants;
import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.core.security.spring.SpringContext;
import com.savbill.taskmanagement.core.service.FileSystemService;
import com.savbill.taskmanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static com.savbill.taskmanagement.core.modules.common.AuditableListener.MODULE;

@Slf4j
@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.CASE)
public class CaseController extends ExBaseAbstractController<CaseDTO> {

    @Autowired
    CaseMapper caseMapper;
    @Autowired
    private CaseService caseService;

    @Autowired
    private CaseUpdateService caseUpdateService;
    @Autowired
    private LiveCustomerNetworkDetailsService liveCustomerNetworkDetailsService;
    @Autowired
    private StaffUserService staffUserService;


    @Autowired
    private ClientServiceSrv clientService;
    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private CustomerRepository customerRepository;


//    @Autowired
//    private SubscriberMapper subscriberMapper;
//    @Autowired
//    private AuditLogService auditLogService;

    @Autowired
    CaseDocDetailsService caseDocDetailsService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    private APIResponseController responseController;

    @Autowired
    private TicketRemarkService ticketRemarkService;

    @Autowired
    private Tracer tracer;


    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private CustomerTaskFileMappingRepo customerTaskFileMappingRepo;


    //private final Logger log = Logger.getLogger(CaseController.class);

//    public CaseController(ExBaseService service) {
//        super(service);
//    }

    //
    public CaseController(CaseService service) {
        super(service);
    }

    //
    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
        }
        return loggedInUser;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET_CREATE + "\")")
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO save(@Valid @RequestParam String entityDTO, @RequestParam(required = false, value = "file") List<MultipartFile> file, HttpServletRequest req) {

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        CaseDTO caseDTO = new CaseDTO();
        Customers customers = new Customers();
        boolean isSuperCreateFromSuperadmin = false;
        StaffUser staffUser = new StaffUser();
        try {
            caseDTO = new ObjectMapper().registerModule(new JavaTimeModule())
                    .readValue(entityDTO, new TypeReference<CaseDTO>() {
                    });
            if (null == caseDTO.getCaseType()) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Select CaseType!");
//                ApplicationLogger.logger.error("Unable to create new Cases with name " + caseDTO.getCaseTitle() + ":  request: { From : {}}; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create ticket with Tittle" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
//            if (null != caseDTO.getStaffId()) {
//                staffUser = staffUserService.get(caseDTO.getStaffId());
//                if (null != staffUser) {
//                    //caseDTO.setPartnerid(customers.getParnterId());
//                    caseDTO.setParentId(1L);
//                }
//            }
            caseDTO.setPartnerid(1);
            log.info("request DTO "+caseDTO);
            if (getMvnoIdFromCurrentStaff() != null) {
                if(caseDTO.getTeamId()!=null &&  caseDTO.getCurrentAssigneeId()==null){
                    Teams teams = teamsRepository.findById(caseDTO.getTeamId().longValue()).orElse(null);
                    if(teams!=null)
                        caseDTO.setMvnoId(teams.getMvnoId());
                }else if(caseDTO.getTeamId()!=null &&  caseDTO.getCurrentAssigneeId()!=null){
                    StaffUser currentAssignStaff = staffUserService.get(caseDTO.getCurrentAssigneeId());
                    log.info("current assignee id"+caseDTO.getCurrentAssigneeId());
                    if(currentAssignStaff!=null){
                        caseDTO.setMvnoId(currentAssignStaff.getMvnoId());
                        caseDTO.setCurrentAssigneeName(currentAssignStaff.getFullName());
                    }
                }
//                if (getMvnoIdFromCurrentStaff() == 1) {
//                    isSuperCreateFromSuperadmin = true;
//                    throw new CustomValidationException(HttpStatus.UNAUTHORIZED.value(), Constants.SUPER_ADMIN_TICKET_CREATE_RISTRICTED, null);
//
//                }
            }
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            }
            if (customers.getBuId() != null) {
                caseDTO.setBuId(customers.getBuId());
            }

            if (getLoggedInUser().getLco())
                caseDTO.setLcoId(getLoggedInUser().getPartnerId());
            else
                caseDTO.setLcoId(null);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create ticket with Tittle" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            caseDTO = caseService.saveEntity(caseDTO, file);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                    AclConstants.OPERATION_CASE_ADD, req.getRemoteAddr(), null, caseDTO.getCaseId(), caseDTO.getCustomerName());
            genericDataDTO.setData(caseDTO);
            genericDataDTO.setTotalRecords(1);

            return genericDataDTO;
        } catch (CustomValidationException e) {
            if (isSuperCreateFromSuperadmin) {

                genericDataDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
                genericDataDTO.setResponseMessage(Constants.SUPER_ADMIN_TICKET_CREATE_RISTRICTED);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            RESP_CODE = e.getErrCode();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            return genericDataDTO;
        } catch (Exception ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(getModuleNameForLog() + " [save] " + ex.getStackTrace(), ex);
            if (ex instanceof DataNotFoundException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (ex instanceof RuntimeException) {
                ex.printStackTrace();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            }
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.TaskConversation.CHANGE_STATUS + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @PostMapping(path = UrlConstants.CASE_UPDATE_DETAILS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO updateDetails(@RequestParam String caseUpdate, @RequestParam(value = "file", required = false) List<MultipartFile> file, @ModelAttribute TaskFileUploadDTO taskFileUploadDTO, @RequestParam(value = "resoultionFileMappingDTO", required = false) String resoultionFileMappingDTO, @RequestParam(value = "resolutionFiles", required = false) List<MultipartFile> resolutionFiles, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";
        CaseUpdateDTO convDTO;


        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseMessage("Success");
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        CaseDTO caseDTO = new CaseDTO();
        try {
            convDTO = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(caseUpdate, CaseUpdateDTO.class);
//            Case oldCase = caseService.getRepository().getOne(convDTO.getTicketId());
//            if(oldCase != null) {
//                CaseDTO oldCaseDTO = caseMapper.domainToDTO(oldCase, new CycleAvoidingMappingContext());
//                if (oldCaseDTO != null) {
//                    log.info("case update details: " + UpdateDiffFinder.getUpdatedDiff(oldCaseDTO, caseDTO));
//                }
//            }
            caseDTO = caseUpdateService.updateEntity(convDTO, file, false, taskFileUploadDTO);

            if(Objects.nonNull(resoultionFileMappingDTO) ){
                ResoultionFileMappingDTO resoultionFileMappingDTO1=new ObjectMapper().readValue(resoultionFileMappingDTO,ResoultionFileMappingDTO.class);
                caseUpdateService.uploadResolutionDocuments(resoultionFileMappingDTO1,resolutionFiles);
            }
            caseService.getCaseDataFromStrig(caseDTO);
            if (getMvnoIdFromCurrentStaff() != null) {
                convDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            genericDataDTO.setData(caseDTO);
            genericDataDTO.setTotalRecords(1);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                    AclConstants.OPERATION_CASE_EDIT, req.getRemoteAddr(), null, caseDTO.getCaseId(), caseDTO.getCustomerName());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } catch (JsonProcessingException e) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseCode(HttpStatus.FAILED_DEPENDENCY.value());
            genericDataDTO.setResponseMessage(HttpStatus.FAILED_DEPENDENCY.getReasonPhrase());
            RESP_CODE = HttpStatus.FAILED_DEPENDENCY.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            return genericDataDTO;
        } catch (CustomValidationException ce) {
            //ApplicationLogger.ApplicationLogger.logger.error(ce.getMessage()), ce);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            return genericDataDTO;
        } catch (IOException ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
            genericDataDTO.setResponseMessage("File not saved");
            RESP_CODE = HttpStatus.NOT_MODIFIED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            return genericDataDTO;
        } catch (Exception e) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @RequestMapping(value = "/documentList/{ticketId}", method = RequestMethod.GET)
    public GenericDataDTO downloadDocument(@PathVariable Integer ticketId) {
        org.slf4j.MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [getAllInventoryDocument()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<FileMappingListDTO> customerInventoryFileMappingList = caseUpdateService.getFilesByTaskId(Long.valueOf(ticketId));
            if(customerInventoryFileMappingList.isEmpty()){
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setData(null);
                genericDataDTO.setDataList(new ArrayList());
                genericDataDTO.setResponseMessage("No Record found for download");
            }
            else{
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setData(null);
                genericDataDTO.setDataList(customerInventoryFileMappingList);
                genericDataDTO.setResponseMessage("Record found successfully");
            }

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setData(null);
            log.error("Unable to fetch document by  " + ticketId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}");
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        org.slf4j.MDC.remove("type");
        return genericDataDTO;
    }

    @RequestMapping(value = "/document/download/{ticketId}/{uniqueName}/{section}/", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Integer ticketId,@PathVariable String uniqueName,@PathVariable String section) {
        org.slf4j.MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";
        Resource resource = null;
        try {
            CustomerTaskFileMapping customerInventoryMapping = customerTaskFileMappingRepo.findByCustomerTaskByUniqueName(uniqueName);
            if (null == customerInventoryMapping) {
                return ResponseEntity.notFound().build();
            }
            resource =  caseUpdateService.getAssignTaskDoc(customerInventoryMapping,uniqueName,section,ticketId);
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                log.info("Downloading document with  " + customerInventoryMapping.getId() + " downloaded Successfully  :  request: { From : {} }; Response : {{}}");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                String errorMessage = "File not found: " + uniqueName + " for inventoryId: " + customerInventoryMapping.getId();
                log.error(errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Error-Message", errorMessage).build();
            }
        } catch (Exception ex) {
            log.error("Unable to downloadDocument " + ticketId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}");
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        org.slf4j.MDC.remove("type");
        return null;
    }

    @RequestMapping(value = "/document/delete/{ticketId}/{fileName}/{uniqueName}/{section}/", method = RequestMethod.DELETE)
    public GenericDataDTO deleteDocument(@PathVariable Integer ticketId, @PathVariable String fileName, @PathVariable String uniqueName, @PathVariable String section) {
        org.slf4j.MDC.put("type", "Delete");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            CustomerTaskFileMapping customerTicketMapping = customerTaskFileMappingRepo.findByCustomerTaskByUniqueName(uniqueName);
            if (customerTicketMapping == null) {
                log.error("CustomerTicketFileMapping not found for ID: {}" + ticketId);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Ticket Mapping not found.");
                return genericDataDTO;
            }
            File file = caseUpdateService.getAssignTaskFile(customerTicketMapping, uniqueName, section, ticketId);
            if (!file.exists()) {
                log.error("File not found: {} for inventoryMappingId: {}" + uniqueName + customerTicketMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("File not found.");
                return genericDataDTO;
            } else if (file.exists()) {
                caseUpdateService.deleteFileFromDatabase(uniqueName);
                log.info("File deleted successfully: {} for inventoryMappingId: {}" + uniqueName + customerTicketMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("File deleted successfully.");

            } else {
                log.error("Failed to delete file: {} for inventoryMappingId: {}" + uniqueName + customerTicketMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                genericDataDTO.setResponseMessage("Failed to delete file.");
            }
            if (file.delete()) {
                log.info("File deleted successfully: {} for ticketId: {}" + uniqueName + customerTicketMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("File deleted successfully.");
            } else {
                log.error("Failed to delete file: {} for ticketId: {}" + uniqueName + customerTicketMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                genericDataDTO.setResponseMessage("Failed to delete file.");
            }
        } catch (Exception ex) {
            log.error("Error occurred while deleting file for ticketId: {}" + ticketId, ex);
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage("An error occurred while deleting the file.");
        } finally {
            org.slf4j.MDC.remove("type");
        }
        return genericDataDTO;
    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @PostMapping(path = UrlConstants.CASE_UPDATE_DETAILS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public GenericDataDTO updateDetailsByBulkStatus
//            (@RequestParam List<Case> caseUpdate, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest req) {
//
//        MDC.put("type", "Update");
//        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";
//        CaseUpdateDTO convDTO;
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseMessage("Success");
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
//        try {
//            for(Case case1: caseUpdate ) {
//                case1.getCaseId();
//            }
//
//                convDTO = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(caseUpdate, CaseUpdateDTO.class);
//
//            CaseDTO caseDTO = caseUpdateService.updateEntity(convDTO, file);
//            if (getMvnoIdFromCurrentStaff() != null) {
//                convDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            genericDataDTO.setDataList();
//            genericDataDTO.setData(caseDTO);
//            genericDataDTO.setTotalRecords(1);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                    AclConstants.OPERATION_CASE_EDIT, req.getRemoteAddr(), null, caseDTO.getCaseId(), caseDTO.getCustomerName());
//            ApplicationLogger.logger.info("Updating the case with title "+caseDTO.getCaseTitle()+":  request: { From : {}}; Response : {{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (JsonProcessingException e) {
//            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.FAILED_DEPENDENCY.value());
//            genericDataDTO.setResponseMessage(HttpStatus.FAILED_DEPENDENCY.getReasonPhrase());
//            ApplicationLogger.logger.error("Unable to update case with title "+caseUpdate+":  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
//
//            return genericDataDTO;
//        } catch (CustomValidationException ce) {
//            //ApplicationLogger.ApplicationLogger.logger.error(ce.getMessage()), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            ApplicationLogger.logger.error("Unable to update case with title "+caseUpdate+":  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ce.getStackTrace());
//
//            return genericDataDTO;
//        } catch (IOException e) {
//            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
//            genericDataDTO.setResponseMessage("File not saved");
//            ApplicationLogger.logger.error("Unable to update case with title "+caseUpdate+":  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
//
//            return genericDataDTO;
//        } catch (Exception e) {
//            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            ApplicationLogger.logger.error("Unable to update case with title "+caseUpdate+":  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
//
//            MDC.remove("type");
//
//            return genericDataDTO;
//        }
//    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @PostMapping(path = UrlConstants.CASES_ASSIGNED_TO_ME)
    public GenericDataDTO getLoggedInUserCases(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) throws Exception {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [getLoggedInUserCases()] ";
        try {
//            ApplicationLogger.logger.info("Fetching all cases by logged in users " + requestDTO + " :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch all tickets by logged in users" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            requestDTO = setDefaultPaginationValues(requestDTO);
            return caseService.getAllCaseByStaffWithPagination
                    (requestDTO.getPage(), requestDTO.getPageSize()
                            , requestDTO.getSortBy(), requestDTO.getSortOrder());

        } catch (Exception ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch all tickets by logged in users" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            MDC.remove("type");
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @PostMapping(value = UrlConstants.CASES_BY_STATUS)
    public GenericDataDTO getCaseByStatus(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = getModuleNameForLog() + " [getCaseByStatus()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (null == requestDTO.getStatus()) {
                genericDataDTO.setResponseMessage("Please Select Status!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All ticket by status" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All ticket by status" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.getAllCaseByStatusWithPagination(requestDTO.getStatus(), requestDTO.getPage()
                    , requestDTO.getPageSize()
                    , requestDTO.getSortBy()
                    , requestDTO.getSortOrder());
        } catch (Exception ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All ticket by status" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @GetMapping(value = UrlConstants.CASES_BY_ASSIGNEE + "/{staffId}")
    public GenericDataDTO getCasesByAssignee(@PathVariable Integer staffId, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = getModuleNameForLog() + " [getCasesByAssignee()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == staffId) {
                genericDataDTO.setResponseMessage("Please Select Assignee!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All ticket by Assignee" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All ticket by Assignee" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(caseService.getAllCaseByStaff(staffId));
        } catch (Exception ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All ticket by Assignee" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = UrlConstants.GET_LIVE_NETWORK_DETAILS + "/{custId}")
    public GenericDataDTO getLiveUserDetailsByCustomer(@PathVariable Integer custId, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [getLiveUserDetailsByCustomer()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == custId) {
                genericDataDTO.setResponseMessage("Please Provide Customer!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch LiveUser Details ByCustomer" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch LiveUser Details ByCustomer" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(liveCustomerNetworkDetailsService.getCustomerWiseNetworkDetailsFromLiveUser(custId));
        } catch (Exception ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch LiveUser Details ByCustomer" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @GetMapping(value = UrlConstants.GET_CASES_BY_TEAM+ "/{teamId}")
    public GenericDataDTO getCasesByCustomer(@PathVariable Integer teamId, HttpServletRequest req) {

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [getCasesByCustomer()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == teamId) {
                genericDataDTO.setResponseMessage("Please Provide team details!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch case By Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch case By Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return GenericDataDTO.getGenericDataDTO(caseService.getAllCaseByWorkingStaff(teamId));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch case By Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @GetMapping(value = UrlConstants.ASSIGNED_TO + "/{caseId}")
    public GenericDataDTO caseAssignedTo(@PathVariable Long caseId, HttpServletRequest req) {

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [caseAssignedTo()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == caseId) {
                genericDataDTO.setResponseMessage("Please Provide Case!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all assigned Case" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(caseService.assignedTo(caseId));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all assigned Case" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } catch (Exception ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            if (ex instanceof DataNotFoundException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all assigned Case" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all assigned Case" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseController]";
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
    //@Deprecated
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
        return super.search(page, pageSize, sortOrder, sortBy, filter);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @PostMapping("/case/search")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Object Page;
        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            CaseService caseService = SpringContext.getBean(CaseService.class);
            genericDataDTO = caseService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            if (genericDataDTO.getTotalPages() > 0) {
                response.put(APIConstants.MESSAGE, "No Records Found!");
                RESP_CODE = APIConstants.NOT_FOUND;
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All  case: " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All case for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.name());
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All ase for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (RuntimeException re) {
            re.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.toString());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All case for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All case for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
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


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination() {
        log.info(LogConstants.REQUEST_FOR + " get All Without Pagination " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        return super.getAllWithoutPagination();
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TASK_DELETE + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody CaseDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        log.debug(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Delete ticket For Case : "+ entityDTO.getCaseNumber() + LogConstants.LOG_BY_NAME + entityDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() );
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            genericDataDTO = super.delete(entityDTO, authentication, req);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Delete ticket" + LogConstants.LOG_BY_NAME + entityDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete ticket" + LogConstants.LOG_BY_NAME + entityDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE, AclConstants.OPERATION_CASE_DELETE, req.getRemoteAddr(), null, entityDTO.getCaseId().longValue(), entityDTO.getCustomerName());

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TASK_EDIT + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody CaseDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            genericDataDTO = super.update(entityDTO, result, authentication, req);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update ticket" + LogConstants.LOG_BY_NAME + entityDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            CaseDTO caseEntity = (CaseDTO) genericDataDTO.getData();
            caseService.getCaseDataFromStrig(caseEntity);
            if (caseEntity != null) {
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                    AclConstants.OPERATION_CASE_EDIT, req.getRemoteAddr(), null, caseEntity.getCaseId().longValue(), caseEntity.getUserName());
                //ApplicationLogger.logger.info("Fetching All Entities by id "+id+":  request: { From : {}}; Response : {{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                MDC.remove("type");
            }

        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update ticket" + LogConstants.LOG_BY_NAME + entityDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {

        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.SUCCESS;
        GenericDataDTO genericDataDTO = caseService.getCaseEntitiyById(Long.parseLong(id));
        CaseDTO caseEntity = (CaseDTO) genericDataDTO.getData();
        log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "get TatMatrix by ID : "+id  + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CASE,
//                AclConstants.OPERATION_CASE_VIEW, req.getRemoteAddr(), null, caseEntity.getCaseId().longValue(), caseEntity.getUserName());
        MDC.remove("type");

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @Override
    @PostMapping
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
                genericDataDTO = caseService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters());
            if (null != requestDTO.getFilters() && 0 < requestDTO.getFilters().size())
                genericDataDTO = caseService.search(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder());
            if (null != requestDTO.getFilterBy() && requestDTO.getFilterBy().equalsIgnoreCase(CaseConstants.FILTER_BY_MY_CASES))
                genericDataDTO = caseService.getAllCaseByStaffWithPagination(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder());
            if (null != requestDTO.getFilterBy() && requestDTO.getFilterBy().equalsIgnoreCase(CaseConstants.FILTER_BY_STATUS))
                genericDataDTO = caseService.getAllCaseByStatusWithPagination(requestDTO.getStatus()
                        , requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder());
            if (null != requestDTO.getFilterBy() && requestDTO.getFilterBy().equalsIgnoreCase(CaseConstants.FILTER_BY_BOTH))
                genericDataDTO = caseService.getAllCaseByStatusAndMyCasesWithPagination(requestDTO.getStatus()
                        , requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder());

            if (null != genericDataDTO) {
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch all cases" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                RESP_CODE = APIConstants.NOT_FOUND;
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all cases " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + " Unable to fetch all cases " +  LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            RESP_CODE = APIConstants.EXPECTATION_FAILED;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all cases " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + " Unable to fetch all cases " + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        MDC.remove("type");

        return genericDataDTO;
    }


    @GetMapping(value = "/excel/mycases")
    public void exportToExcelForMyCases(HttpServletResponse response) throws Exception {

        MDC.put("type", "Fetch");
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Excel_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        Workbook workbook = new XSSFWorkbook();
        caseService.excelGenerateForMyCases(workbook);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        log.info(LogConstants.REQUEST_FOR + "Export To Excel For Cases" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        MDC.remove("type");

    }

    @GetMapping(value = "/pdf/mycases")
    public void generatePdfForMyCases(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");

        MDC.put("type", "Fetch");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        Document pdfDoc = new Document();
        PdfWriter.getInstance(pdfDoc, response.getOutputStream());
        log.info(LogConstants.REQUEST_FOR + "generate pdf for cases" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        MDC.remove("type");
        caseService.pdfGenerateForMyCases(pdfDoc);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @GetMapping(value = "/assignTicketFromTeam/{caseId}")
    public GenericDataDTO assignTicketFromTeam(@PathVariable Long caseId, @RequestParam(required = false) Integer teamId, @RequestParam String remark, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(caseUpdateService.assignTicketFromTeam(caseId, teamId, remark));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign All Ticket" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(getModuleNameForLog() + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign All Ticket " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @GetMapping("/approveTicket")
    public GenericDataDTO approveTicket(@RequestParam(name = "caseId") Long caseId, @RequestParam(name = "isApproveRequest") boolean isApproveRequest, @RequestParam(name = "remarks", required = false) String remarks, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        CaseDTO caseDTO = new CaseDTO();

        try {
            RESP_CODE = APIConstants.SUCCESS;
            //genericDataDTO = caseService.approveTicket(caseId, isApproveRequest, remarks);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Approved Successfully..");
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "approve Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "approve Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "approve Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @GetMapping("/assignPickedTicket")
    public GenericDataDTO assignPickedTicket(@RequestParam(name = "caseId") Long caseId, @RequestParam(name = "staffId") Integer staffId, @RequestParam(name = "remark") String remark, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        CaseDTO caseDTO = new CaseDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "assign PickedTicket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.assignPickedTicket(caseId, staffId, remark);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "assign PickedTicket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @GetMapping("/assignEveryStaffFromList")
    public GenericDataDTO assignEveryStaffFromList(@RequestParam(name = "caseId") Long caseId, @RequestParam(name = "remark") String remark, @RequestParam(name = "isApproveRequest") Boolean isApproveRequest, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        CaseDTO caseDTO = new CaseDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "assign Every Staff FromList" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.assignEveryStaffFromList(caseId, remark, isApproveRequest);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "assign Every Staff FromList" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.TaskConversation.CHANGE_STATUS + "\",\"" + MenuConstants.TaskConversation.CHANGE_PRIORITY + "\",\"" + MenuConstants.TaskConversation.BULK_REASIGN + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @PutMapping(path = UrlConstants.CASE_BULK_UPDATE_DETAILS)
    public GenericDataDTO bulkUpdateDetails(@RequestBody List<CaseUpdateDTO> caseUpdate, HttpServletRequest req) throws JsonProcessingException, IOException {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = getModuleNameForLog() + " [updateDetails()] ";

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseMessage("Success");
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        CaseDTO caseDTO = new CaseDTO();
        try {
            String caseStatus = caseUpdate.get(0).getStatus();
            Boolean isNotSameStatus = caseUpdate.stream().noneMatch(caseUpdateDTO -> caseStatus.equalsIgnoreCase(caseUpdateDTO.getStatus()));
            if( Objects.isNull(caseUpdate.get(0).getTeamId()) ){
                     if(!isNotSameStatus ) {
                         throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "To perform bulk status change operation please set all task status to : " + caseStatus + ", for different status this operation is not allowed", null);
                     }
                }else{
                for (CaseUpdateDTO case1 : caseUpdate) {
                    caseDTO = caseUpdateService.updateEntity(case1, null, false, null);
                }
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update bulk Details" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }



        } catch (CustomValidationException ce) {
            //ApplicationLogger.ApplicationLogger.logger.error(ce.getMessage()), ce);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            RESP_CODE = (HttpStatus.NOT_ACCEPTABLE.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update bulk Details" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            return genericDataDTO;
        } catch (Exception e) {
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            RESP_CODE = (HttpStatus.EXPECTATION_FAILED.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update bulk Details" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @GetMapping("/linkTicket")
    public GenericDataDTO linkTicket(@RequestParam(name = "caseId") Long caseId, @RequestParam(name = "linkTicketId") Integer linkTicketId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        CaseDTO caseDTO = new CaseDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Link Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.linkTicket(caseId, linkTicketId);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Link Ticket " + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @PostMapping(value = "/updateDocumentDetails", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO updateDocumentDetails(@RequestParam(name = "caseId") Long caseId, @RequestParam(value = "file") List<MultipartFile> file, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Document Details" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.updateDocumentDetails(caseId, file, req);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Document Details" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @PostMapping("/reassignTicket")
//    public GenericDataDTO reassignTicket(@RequestBody TasksAssignDTO tasksAssignDTO, HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Integer RESP_CODE = APIConstants.FAIL;
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Update");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//        CaseDTO caseDTO = new CaseDTO();
//        try {
//            RESP_CODE = APIConstants.SUCCESS;
//            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reassign Ticket " + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            return caseService.assignTasks(tasksAssignDTO);
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reassign Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }

    @PostMapping("/reassingTask")
    public GenericDataDTO reassignTicket(@RequestBody TasksAssignDTO tasksAssignDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        CaseDTO caseDTO = new CaseDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reassign Ticket " + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.assignTasks(tasksAssignDTO);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reassign Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }




    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
//            + AclConstants.OPERATION_CUSTOMER_GET_DOCUMENT + "\")")
    @RequestMapping(value = "/document/download/{ticketId}/{docId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Long ticketId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            CaseDTO caseDTO = caseService.getEntityById(ticketId);
            if (null == caseDTO) {
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Download recipt for ticket" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
            CaseDocDetails docDetailsDTO = caseDocDetailsService.downloadDocument(docId, ticketId);
            if (null == docDetailsDTO) {
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Download recipt for ticket" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = SpringContext.getBean(FileSystemService.class);
            resource = service.getTicketDoc(docDetailsDTO.getUniquename());
            // resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Download recipt for ticket" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable to Download recipt" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);

            } else {
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Download recipt for ticket" + ticketId + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable to Download reciptzz" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            RESP_CODE = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Download recipt for ticket" + ticketId + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            //ApplicationLogger.ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return null;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TASK_ETR + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @RequestMapping(value = "/sendETRtoCustomer", method = RequestMethod.POST)
    public GenericDataDTO sendETRTicketNotification(@Valid @RequestBody TicketETRPojo entityDTO, HttpServletRequest req) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "send ETR to Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.sendETRTicketNotification(entityDTO);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "send ETR to Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @PostMapping(value = "/getTicketETRReport/{caseId}")
    public GenericDataDTO getTicketETRReport(@PathVariable Long caseId, HttpServletRequest req) {

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All etr for case" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            return caseService.getETRDetailsForCase(caseId);
        } catch (Exception ex) {
            //ApplicationLogger.ApplicationLogger.logger.error(getModuleNameForLog() + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All etr for case" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;

    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @GetMapping("/getTatDetials")
    public GenericDataDTO getTatDetails(@RequestParam(name = "caseId") Long caseId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Ticket TAT details " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.getTatDetails(caseId);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Ticket TAT details " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getAllStaffUserByServiceArea/{serviceAreaId}")
    public GenericDataDTO getAllStaffByServiceArea(@PathVariable Integer serviceAreaId, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getAllStaffUserByServiceAreaId] ";
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = GenericDataDTO.getGenericDataDTO(caseService.findAllStaffUser(serviceAreaId));
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

                }

                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Staffuser" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Staffuser" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @PostMapping("/linkBulkTicket")
    public GenericDataDTO linkTicket(@RequestBody List<Integer> childTickets, @RequestParam(name = "taskId") Integer taskId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        CaseDTO caseDTO = new CaseDTO();

        List<Integer> casesIDs = new ArrayList<>();
        for (int i = 0; i < childTickets.size(); i++) {
            casesIDs.add(Math.toIntExact(childTickets.get(i)));
        }
        try {
            //ApplicationLogger.logger.info("Getting Ticket Approve from  with id  " + casesIDs.get(i) + "is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            genericDataDTO.setData(caseService.linkBulkTicket(casesIDs, taskId));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "link Ticket " + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            //ApplicationLogger.logger.error("Unable to Approve Ticket  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "link ticket " + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @PostMapping("/reassignTicketInBulk")
    public GenericDataDTO reassignTicketInBulk(@RequestBody List<CaseDTO> childTickets, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        CaseDTO caseDTO = new CaseDTO();
        try {
            List<Long> casesIDs = new ArrayList<>();
            for (int i = 0; i < childTickets.size(); i++) {
                casesIDs.add(childTickets.get(i).getCaseId());
            }
            //ApplicationLogger.logger.info("Getting Ticket Reassigned from  with id  " + caseId + "is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "reassign Ticket In Bulk" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.reassignTaskInBulk(casesIDs);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "reassign Ticket In Bulk" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_VIEW + "\")")
    @PostMapping("/filter")
    public GenericDataDTO filterCase(@RequestParam(name = "filter") String filter, @RequestBody PaginationRequestDTO requestDTO) {

        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            genericDataDTO = caseService.filterCase(filter, requestDTO);
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FOR + " filter Case : "+filter + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.getStackTrace();
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            RESP_CODE = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FOR + " filter Case : " + filter + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @GetMapping("/getChildTickets")
    public GenericDataDTO getChildTickets(@RequestParam(name = "caseId") Long caseId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch child Tickets  with id : " + caseId + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.getChildTickets(caseId);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch child Tickets  with id : " + caseId + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/findAll/ContactFailed")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllServicerType(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = clientServiceSrv.getByName("ContactFailed");
            //List<String>contactfieldList=new ArrayList<>();

            if (clientService == null) {
                response.put("ContactFailed", new ArrayList<>());
                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put("ContactFailed", servicerTypeList);

            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching ServicerTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching ServicerTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching ServicerTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);

    }

    @GetMapping("/findAll/ProblemType")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByProblemType(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = clientServiceSrv.getByName("ProblemType");
            //List<String>contactfieldList=new ArrayList<>();

            if (clientService == null) {
                response.put("ProblemTypeList", new ArrayList<>());
                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
                responseCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Problem Type" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put("ProblemTypeList", servicerTypeList);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Problem Type" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Problem Type" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Problem Type" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);

    }

    @GetMapping("/findAll/PaymentMode")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByPaymentMode(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = clientServiceSrv.getByName("PaymentMode");
            //List<String>contactfieldList=new ArrayList<>();

            if (clientService == null) {
                response.put("PaymentModeList", new ArrayList<>());
                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
                responseCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Payment Mode" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put("PaymentModeList", servicerTypeList);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Payment Mode" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + response);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Payment Mode" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Payment Mode" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);

    }

    @GetMapping("/findAll/TicketsRaisedoption")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByTicketsRaisedoption(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = clientServiceSrv.getByName("TicketsRaisedoption");
            //List<String>contactfieldList=new ArrayList<>();

            if (clientService == null) {
                response.put("TicketsRaisedoptionList", new ArrayList<>());
                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
                responseCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Tickets Raised option" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);

            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put("TicketsRaisedoptionList", servicerTypeList);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Tickets Raised option" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + response);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Tickets Raised option" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Tickets Raised option" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);

    }

    @GetMapping("/findAll/Satisfied")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByTicketsSatisfied(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = clientServiceSrv.getByName("Satisfied");
            //List<String>contactfieldList=new ArrayList<>();

            if (clientService == null) {
                response.put("SatisfiedList", new ArrayList<>());
                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
                responseCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Satisfied" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put("SatisfiedList", servicerTypeList);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Satisfied" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Satisfied" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Satisfied" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);

    }

    @GetMapping("/findAll/Unsatisfied")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByTicketsUnsatisfied(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = clientServiceSrv.getByName("Unsatisfied");
            //List<String>contactfieldList=new ArrayList<>();

            if (clientService == null) {
                response.put("UnsatisfiedList", new ArrayList<>());
                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
                responseCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All UnSatisfied" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put("UnsatisfiedList", servicerTypeList);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All UnSatisfied" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All UnSatisfied" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All UnSatisfied" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);

    }

    @GetMapping("/findAll/Feedback")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByFeedback(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = clientServiceSrv.getByName("FeedBack");
            //List<String>contactfieldList=new ArrayList<>();

            if (clientService == null) {
                response.put("UnsatisfiedList", new ArrayList<>());
                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
                responseCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Feedback" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);

            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put("UnsatisfiedList", servicerTypeList);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Feedback" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Feedback" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Feedback" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);

    }

    @GetMapping("/findAll/informationofpaymentmode")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByinformationofpaymentmode(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = clientServiceSrv.getByName("informationofpaymentmode");
            //List<String>contactfieldList=new ArrayList<>();

            if (clientService == null) {
                response.put("paymentinfolist", new ArrayList<>());
                response.put(APIConstants.SUCCESS.toString(), "No Records Found!");
                responseCode = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All information of paymentmode " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put("paymentinfolist", servicerTypeList);
            }
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All information of paymentmode" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All information of paymentmode" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(APIConstants.FAIL.toString(), e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All information of paymentmode" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);

    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @GetMapping("/getTatAuditDetails")
    public GenericDataDTO getTatAuditDetails(@RequestParam(name = "caseId") Long caseId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Audit Details" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.getTatAuditDetails(caseId);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Audit Details" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET + "\")")
    @ApiOperation(value = "Get list of  all customer using email")
    @GetMapping("/findCustomerByEmail/{email}")
    public ResponseEntity<?> findCustomerByEmailOrDomain(@PathVariable String email, HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            List<Customers> customersList = new ArrayList<>();
            if (!getBUIdsFromCurrentStaff().isEmpty() && getBUIdsFromCurrentStaff() != null) {
                customersList = ticketRemarkService.getCustomerListFromEmailAndBuIdAndMvnoId(email, getBUIdsFromCurrentStaff().get(0), getMvnoIdFromCurrentStaff().longValue());
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch customer by email : " + email + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                customersList = ticketRemarkService.getCustomerListFromEmail(email, getMvnoIdFromCurrentStaff());
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch customer by email : " + email + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            CustomerServicePlanDTO customerServicePlanDTOList = new CustomerServicePlanDTO();
            if (!customersList.isEmpty()) {
                customerServicePlanDTOList = ticketRemarkService.getCustomerServicePlan(customersList.get(0));
            } else {
                customerServicePlanDTOList.setIsAvaileble(false);
            }
            Integer responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch customer by email : " + email + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put("serviceplanlist", customerServicePlanDTOList);
//            ApplicationLogger.logger.debug("All customer History using email");
            return responseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch customer by email : " + email + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return responseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

//    @PreAuthorize("validatePermission(\"" + MenuConstants.Task.TICKET_CREATE + "\")")
//    /**new api because for attchment**/
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_ADD + "\")")
//    @PostMapping(value = "/savecase", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public GenericDataDTO savecase(@Valid @RequestParam String entityDTO, @RequestParam(required = false, value = "file") List<MultipartFile> file, HttpServletRequest req) {
//
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Create");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//        Integer RESP_CODE = APIConstants.FAIL;
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        CaseDTO caseDTO = new CaseDTO();
//        try {
//            caseDTO = new ObjectMapper().registerModule(new JavaTimeModule())
//                    .readValue(entityDTO, new TypeReference<CaseDTO>() {
//                    });
//            if (null == caseDTO.getCaseType()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Please Select CaseType!");
//                return genericDataDTO;
//            }
//            if (null != caseDTO.getCustomersId()) {
//                Customers customers = customersService.get(caseDTO.getCustomersId());
//                if (null != customers && null != customers.getParnterId()) {
//                    caseDTO.setPartnerid(customers.getParnterId());
//                }
//            }
//
//            if (getMvnoIdFromCurrentStaff() != null) {
//                caseDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
//                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
//            }
//            if (getMvnoIdFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1) {
//                caseDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
//            }
//
//            if (getLoggedInUser().getLco())
//                caseDTO.setLcoId(getLoggedInUser().getPartnerId());
//            else
//                caseDTO.setLcoId(null);
//
//            caseDTO = caseService.saveEntityWithAttchment(caseDTO, file);
//            genericDataDTO.setData(entityDTO);
//            genericDataDTO.setTotalRecords(1);
//            RESP_CODE = APIConstants.SUCCESS;
//            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            return genericDataDTO;
//        } catch (CustomValidationException e) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            return genericDataDTO;
//        } catch (Exception ex) {
//            //ApplicationLogger.logger.error(getModuleNameForLog() + " [save] " + ex.getStackTrace(), ex);
//            if (ex instanceof DataNotFoundException) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                RESP_CODE = HttpStatus.NOT_FOUND.value();
//                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_NO_RECORD_FOUND + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//                return genericDataDTO;
//            }
//            if (ex instanceof RuntimeException) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//                return genericDataDTO;
//            }
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//
//        return genericDataDTO;
//    }

    @PostMapping(value = "/approval/getTaskApprovals")
    public GenericDataDTO getTaskApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getCustomersApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        CaseDTO caseDTO = new CaseDTO();
        try {
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Approve Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return caseService.getTaskApprovalsList(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Approve Ticket" + LogConstants.LOG_BY_NAME + caseDTO.getCaseTitle() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
    @PostMapping("/casehistory")
    public GenericDataDTO getCaseUpdates(@RequestParam("customerId") Integer customerId, HttpServletRequest req,@RequestBody PaginationRequestDTO requestDTO) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            Customers customers = customerRepository.findById(customerId).orElse(null);
            if(customers != null){
                genericDataDTO = caseService.getCaseByCustomersId(customers.getId(),requestDTO);
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Case history by customer ID" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }else{
                genericDataDTO.setResponseMessage("customer Id is InValid!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Case history by customer ID" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_INFO + " customer Id is InValid! " + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        }catch (Exception e){
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Case history by customer ID" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
//    @GetMapping("/checkAvailablity")
//    public GenericDataDTO checkAvailablity(@RequestParam("staffId") Integer staffId, @RequestParam ("startingTime")LocalDateTime startingTime,@RequestParam ("endingTime")LocalDateTime endingTime, HttpServletRequest req) {
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "fetch");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            Optional<StaffUser>  staffUser = staffUserRepository.findById(staffId);
//            if( staffUser.isPresent()&&  Objects.nonNull(staffUser)){
//                genericDataDTO = caseService.getStaffAvailibity(staffUser.get(),startingTime,endingTime);
//            }else{
//                genericDataDTO.setResponseMessage("customer Id is InValid!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            }
//        }catch (Exception exception){
//            genericDataDTO.setResponseMessage(exception.getMessage());
//            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom_") + LogConstants.REQUEST_FOR + "Get Case history" + exception.getMessage() );
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }

    @GetMapping("/checkAvailablity")
    public ResponseEntity<GenericDataDTO> checkAvailability(
            @RequestParam("staffId") Integer staffId,
            @RequestParam("startingTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startingTime,
            @RequestParam(name = "endingTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endingTime,
            HttpServletRequest req) {

        // Set up logging context
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            Optional<StaffUser> staffUser = staffUserRepository.findById(staffId);
            if (staffUser.isPresent()) {

                genericDataDTO = caseService.getStaffAvailibity(staffUser.get(), startingTime, endingTime);
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " check Availablity " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseCode(APIConstants.NOT_FOUND);
                genericDataDTO.setResponseMessage("Staff ID is invalid!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " check Availablity " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_INFO + " Staff ID is invalid! " + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(genericDataDTO);
            }
        } catch (Exception exception) {
            // Handle exceptions
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage("Internal Server Error: " + exception.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " check Availablity " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericDataDTO);
        } finally {
            MDC.clear();
        }

        return ResponseEntity.ok(genericDataDTO);
    }

}
