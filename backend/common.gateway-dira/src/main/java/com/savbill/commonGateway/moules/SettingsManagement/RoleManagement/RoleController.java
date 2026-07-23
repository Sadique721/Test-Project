package com.savbill.commonGateway.moules.SettingsManagement.RoleManagement;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaConstant;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.SettingsManagement.StaffRoleMapping.StaffRoleRelRepo;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.CommonRoleMessage;
import com.savbill.commonGateway.rabbitmq.messages.RoleMessage;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ROLE)
public class RoleController extends ExBaseAbstractController<RoleDTO> {

//    @Autowired
//    AuditLogService auditLogService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private StaffRoleRelRepo staffRoleRelRepo;
    @Autowired
    CreateDataSharedService createDataSharedService;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private Tracer tracer;

    public RoleController(RoleService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RoleController]";
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_MANAGEMENT + "\")")
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

    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_MANAGEMENT + "\", \"" + MenuConstants.IwfSettings.ROLE_MANAGEMENT + "\")")
    @PostMapping(value = "permissions")
    public GenericDataDTO getAllRoleByProduct( HttpServletRequest req,
            @RequestBody PaginationRequestDTO requestDTO,
            @RequestParam(name = "productType") String productType,@RequestParam(name = "isloggedInUser", required = false, defaultValue = "false") boolean isloggedInUser) {
        String SUBMODULE = getModuleNameForLog() + " [getAllRoleByProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "fetch ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());

        try {
            RoleService roleService = SpringContext.getBean(RoleService.class);
            if(requestDTO.getSortBy()==null){
                requestDTO.setSortBy("id");
            }
            requestDTO = setDefaultPaginationValues(requestDTO);
                genericDataDTO = roleService.getpagination(
                        requestDTO.getPage(),
                        requestDTO.getPageSize(),
                        requestDTO.getSortBy(),
                        requestDTO.getSortOrder(),
                        requestDTO.getFilters(),
                        productType,isloggedInUser);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ce) {
            ce.printStackTrace();
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE +ce.getMessage()+LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);

        }
        return genericDataDTO;
    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_MANAGEMENT + "\", \"" + MenuConstants.IwfSettings.ROLE_MANAGEMENT + "\")")
    @PostMapping(value = "/searchRoleByProduct")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req , @RequestParam(name = "productType") String productType) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "search ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());
        try {
            GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
            this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
            genericSearchDTO.setFilter(requestDTO.getFilters());

            if (null == genericSearchDTO || null == genericSearchDTO.getFilter() || 0 == genericSearchDTO.getFilter().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
              LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
                return genericDataDTO;
            }

            if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue()))
                requestDTO.setPageSize(MAX_PAGE_SIZE);
                genericDataDTO = roleService.searchByProduct(genericSearchDTO.getFilter(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), productType);

                if(null != genericDataDTO && !genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO.setResponseMessage("Successfully Fetched");
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_MANAGEMENT + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
            GenericDataDTO genericDataDTO = super.getEntityById(id, req,res);
            RoleDTO role = (RoleDTO) genericDataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
//                AclConstants.OPERATION_ROLE_VIEW, req.getRemoteAddr(), null, role.getId().longValue(), role.getRolename());
            return genericDataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_MANAGEMENT + "\", \"" + MenuConstants.IwfSettings.ROLE_MANAGEMENT + "\")")
    @GetMapping(value = "/product/{id}")
    public GenericDataDTO getProductRoleById(@PathVariable String id,HttpServletRequest req ,  @RequestParam String productName) throws Exception {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "fetch ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        try {
            genericDataDTO.setData(roleService.getProductEntityById(new Long(id), new String(productName)));
            genericDataDTO.setTotalRecords(1);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR+e.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping()
    public GenericDataDTO getAllRolesWithoutPagination(@RequestParam String productType) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "fetch ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString()); GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<RoleDTO> list = roleService.getEntities(productType).stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || d.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 ).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            LOGGER.info(LogConstants.REQUEST_FROM  +LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            LOGGER.error(LogConstants.REQUEST_FROM + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_ERROR+ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_MANAGEMENT + "\")")
    @GetMapping("/byLoggedInUser")
    public GenericDataDTO getAllRolesByLoggedInUserWithoutPagination(@RequestParam String productType) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "fetch ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString()); GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<RoleDTO> list = roleService.getRolesByLoggedInId(productType).stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || d.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 ).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            LOGGER.info(LogConstants.REQUEST_FROM  +LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            LOGGER.error(LogConstants.REQUEST_FROM + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_ERROR+ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
   @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_MANAGEMENT + "\")")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable String id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
       TraceContext traceContext = tracer.currentSpan().context();
       MDC.put("type" , "fetch ");
       MDC.put("userName" , getLoggedInUser().getUsername());
       MDC.put("traceId" , traceContext.traceIdString());
       MDC.put("spanId" , traceContext.spanIdString());

       try {
            response.put("data", roleService.getEntityById(Long.valueOf(id)));
            RESP_CODE = APIConstants.SUCCESS;
            //logger.info(" Fetching Country List", APIConstants.SUCCESS);
            //ApplicationLogger.logger.error(MODULE);
           LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            //logger.error(MODULE + ex.getStackTrace(), ex);
           LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_FETCH + "Role " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_ERROR+ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           MDC.remove("spanId");
       }
        return apiResponse(RESP_CODE, response, null);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_CREATE +  MenuConstants.IwfSettings.ROLE_CREATE+"\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody RoleDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create ");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }

        if (getLoggedInUser().getLco())
            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
        else
            entityDTO.setLcoId(null);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try{
        boolean flag = roleService.duplicateVerifyAtSave(entityDTO.getRolename());
        if (flag) {
            genericDataDTO = super.save(entityDTO, result, authentication, req,res);
            RoleDTO role = (RoleDTO) genericDataDTO.getData();
            //send message
            RoleMessage roleMessage = new RoleMessage();
            if (role != null) {
                roleMessage.setId(role.getId());
                roleMessage.setRolename(role.getRolename());
                roleMessage.setStatus(role.getStatus());
                roleMessage.setProduct(role.getProduct());
                roleMessage.setIsDelete(role.getIsDelete());
                roleMessage.setSysRole(role.getSysRole());
                roleMessage.setMvnoId(role.getMvnoId());
//                roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
                roleMessage.setAclEntryDTOList(role.getAclMenu());
                //messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
                kafkaMessageSender.send(new KafkaMessageData(roleMessage,roleMessage.getClass().getSimpleName()));
                Role roleEntity = roleService.sharedData(role);
                createDataSharedService.sendEntitySaveDataForAllMicroService(roleEntity);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
//                        AclConstants.OPERATION_ROLE_VIEW, req.getRemoteAddr(), null, role.getId(), role.getRolename());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Role"+LogConstants.LOG_BY_NAME+entityDTO.getRolename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }

        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFro") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Role"+LogConstants.LOG_BY_NAME+entityDTO.getRolename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        }
    }catch (Exception ex) {
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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
@PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_CREATE + "\", \"" + MenuConstants.IwfSettings.ROLE_CREATE + "\")")
    @PostMapping("/saveRole")
    public GenericDataDTO saveRole(@Valid @RequestBody RoleDTO entityDTO ,HttpServletRequest req) throws Exception {
    TraceContext traceContext = tracer.currentSpan().context();
    MDC.put("type" , "Create ");
    MDC.put("userName" , getLoggedInUser().getUsername());
    MDC.put("traceId" , traceContext.traceIdString());
    MDC.put("spanId" , traceContext.spanIdString());
    System.out.println("save Role started: "+ new Date().getTime());

        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        if (getLoggedInUser().getLco())
            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
        else
            entityDTO.setLcoId(null);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
    try {
        boolean flag = roleService.duplicateVerifyAtSave(entityDTO.getRolename());
        if (flag) {
            RoleDTO roleDTO = roleService.saveRoleWithAcl(entityDTO);
            if (roleDTO != null) {
                new Thread(() -> {
                    //run background code here
                    roleService.sendToAllMicroservices(roleDTO);
                }).start();
            }
            genericDataDTO.setData(roleDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successfully Created");
            System.out.println("save Role started: " + new Date().getTime());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Role"+LogConstants.LOG_BY_NAME+entityDTO.getRolename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Role"+LogConstants.LOG_BY_NAME+entityDTO.getRolename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+ LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        }
    }catch (Exception ex){
        LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_CREATE + "Role"+LogConstants.LOG_BY_NAME+entityDTO.getRolename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

    }finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
    }

        return genericDataDTO;
    }


    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_EDIT + "\", \"" + MenuConstants.IwfSettings.ROLE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody RoleDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "Update ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            if(getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            Role oldRole = roleService.getRolebyId(entityDTO.getId());
            Role oldClone = new Role(oldRole);

            if(getLoggedInUser().getLco())
                entityDTO.setLcoId(getLoggedInUser().getPartnerId());
            else
                entityDTO.setLcoId(null);

            boolean flag = roleService.duplicateVerifyAtEdit(entityDTO.getRolename(), entityDTO.getId());
            if (flag) {
                Integer staffRoleCount = staffRoleRelRepo.findByRoleId(entityDTO.getId()).size();
                if(staffRoleCount == 0 || (staffRoleCount > 0 && entityDTO.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))) {
                    genericDataDTO = super.update(entityDTO, result, authentication, req,res);
                    if (genericDataDTO.getResponseCode() != 200) {
                        return genericDataDTO;
                    }
                    RoleDTO role = (RoleDTO) genericDataDTO.getData();
                    //send message
                    RoleMessage roleMessage = new RoleMessage();
                    roleMessage.setId(role.getId());
                    roleMessage.setRolename(role.getRolename());
                    roleMessage.setStatus(role.getStatus());
                    roleMessage.setIsDelete(role.getIsDelete());
                    roleMessage.setSysRole(role.getSysRole());
                    roleMessage.setMvnoId(role.getMvnoId());
//                    roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
                    roleMessage.setAclEntryDTOList(role.getAclMenu());
                    //messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
                    Role roleEntity = roleService.sharedData(role);
                    createDataSharedService.updateEntityDataForAllMicroService(roleEntity);
                    Role updatedRole = roleMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
//                            AclConstants.OPERATION_ROLE_EDIT, req.getRemoteAddr(), null, entityDTO.getId().longValue(), entityDTO.getRolename());
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE+"Role" +LogConstants.LOG_BY_NAME+entityDTO.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + " , Updated Rolr Details" + UpdateDiffFinder.getUpdatedDiff(oldClone,updatedRole)+ LogConstants.LOG_STATUS+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS  );

                } else {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(MessageConstants.ROLE_IN_USE);
                }
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE+ "Role" +LogConstants.LOG_BY_NAME+entityDTO.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+ LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE + "Role" +LogConstants.LOG_BY_NAME+entityDTO.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
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

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_EDIT + "\", \"" + MenuConstants.IwfSettings.ROLE_EDIT + "\")")
    @PutMapping("/updateRole")
    public GenericDataDTO updateRole(@Valid @RequestBody RoleDTO entityDTO , HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "Update ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Role oldRole = roleService.getRolebyId(entityDTO.getId());
        Role oldClone = new Role(oldRole);
        try {
            roleService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = roleService.duplicateVerifyAtEdit(entityDTO.getRolename(), entityDTO.getId());
            if (flag) {
                RoleDTO roleDTO = roleService.updateRole(entityDTO);
                if(roleDTO != null) {
                    new Thread(() -> {
                        //run background code here
                        roleService.sendToAllMicroservices(roleDTO);
                    }).start();
                }
                genericDataDTO.setData(roleDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Successfully Updated");
                Role updatedRole = roleMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());

                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE+"Role" +LogConstants.LOG_BY_NAME+entityDTO.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + "Updated Rolr Details" + UpdateDiffFinder.getUpdatedDiff(oldClone,updatedRole)+ LogConstants.LOG_STATUS+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS  );

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE+ "Role"+LogConstants.LOG_BY_NAME+entityDTO.getRolename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_UPDATE+ "Role" +LogConstants.LOG_BY_NAME+entityDTO.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_DELETE + "\", \"" + MenuConstants.IwfSettings.ROLE_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody RoleDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "Delete ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());

            GenericDataDTO genericDataDTO = super.delete(entityDTO, authentication, req,res);
            RoleDTO role = (RoleDTO) genericDataDTO.getData();
            //send message
        long startTime = System.nanoTime();  // Start measuring
        try {
            RoleMessage roleMessage = new RoleMessage();
            roleMessage.setId(role.getId());
            roleMessage.setRolename(role.getRolename());
            roleMessage.setStatus(role.getStatus());
            roleMessage.setIsDelete(true);
            roleMessage.setSysRole(role.getSysRole());
            roleMessage.setMvnoId(role.getMvnoId());
            roleMessage.setProduct(role.getProduct());
//        roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
            roleMessage.setAclEntryDTOList(role.getAclMenu());
            //messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
            Role roleEntity = roleService.sharedData(role);
            createDataSharedService.deleteEntityDataForAllMicroService(roleEntity);
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
//                AclConstants.OPERATION_ROLE_DELETE, req.getRemoteAddr(), null, entityDTO.getId().longValue(), entityDTO.getRolename());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE+ "Role" +LogConstants.LOG_BY_NAME+entityDTO.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE+ "Role" +LogConstants.LOG_BY_NAME+entityDTO.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+ ex.getMessage()+LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

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

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_DELETE + "\", \"" + MenuConstants.IwfSettings.ROLE_DELETE + "\")")
    @DeleteMapping("/delete/{id}")
    public GenericDataDTO deleteRole(@PathVariable Long id , HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type" , "Delete ");
        MDC.put("userName" , getLoggedInUser().getUsername());
        MDC.put("traceId" , traceContext.traceIdString());
        MDC.put("spanId" , traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        roleService.getEntityForUpdateAndDelete(id);
        RoleDTO role = new RoleDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            boolean flag = roleService.deleteVerification(Math.toIntExact(id));
            roleService.getEntityForUpdateAndDelete(id);
            flag = roleService.deleteVerification(Math.toIntExact(id));

            if (flag) {
                role = roleService.deleteRole(id);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Successfully Deleted");
                //send message
                CommonRoleMessage message = new CommonRoleMessage();
                message.setId(id);
                //messageSender.send(message, SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_REVENUE);
                //messageSender.send(message, SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_CMS);
                //messageSender.send(message, SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_INVENTORY);
                //messageSender.send(message, SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_PARTNER);
                //messageSender.send(message, SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_CRM);
                //messageSender.send(message, SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_TICKET);
                //messageSender.send(message, SharedDataConstants.QUEUE_SEND_DELETE_DATA_ROLE_RADIUS);
                kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName(), KafkaConstant.DELETE_DATA_ROLE));

                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE+ "Role "+LogConstants.LOG_BY_NAME+role.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.ROLE_DELETE_EXIST);
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE+ "Role"+LogConstants.LOG_BY_NAME+role.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE +HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + LogConstants.REQUEST_TO_DELETE+ "Role"+LogConstants.LOG_BY_NAME+role.getRolename()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +ex.getMessage()+LogConstants.LOG_STATUS_CODE +HttpStatus.EXPECTATION_FAILED);
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

    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<RoleDTO> list = roleService.getAllEntities();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");

        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

//   // @Deprecated
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(Integer page, Integer pageSize, Integer sortOrder, String sortBy, GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Settings.ROLE_MANAGEMENT + "\", \"" + MenuConstants.IwfSettings.ROLE_MANAGEMENT + "\")")
    @PostMapping("/searchrole")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO requestDTO , HttpServletRequest req,HttpServletResponse res) {
        GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
        genericSearchDTO.setFilter(requestDTO.getFilters());
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Search");
        MDC.put("userName",roleService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO =super.search(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortOrder(), requestDTO.getSortBy(), genericSearchDTO , req,res);
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Role using keyword : " +genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + roleService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Role using keyword : " +genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + roleService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Role using keyword : " +genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + roleService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);

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





//    @GetMapping("/role")
//    public GenericDataDTO getAllActiveRole() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            List<RoleDTO> roleList = roleService.convertResponseModelIntoPojo(roleService.getAllActiveEntities());
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setDataList(roleList);
//            genericDataDTO.setTotalRecords(roleList.size());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }

//    @PostMapping("/save")
//    public GenericDataDTO createRole(@Valid @RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            pojo = roleService.saveEntity(pojo);
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }

    //    @GetMapping("/role/{id}")
//    public GenericDataDTO getRole(@PathVariable Integer id) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            RoleDTO pojo = roleService.convertRoleModelToRolePojo(roleService.get(id));
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//            ;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
////
//    @PostMapping("/update")
//    public GenericDataDTO updateRole(@Valid @RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//            pojo = roleService.updateEntity(pojo);
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//
//    @PostMapping("/delete")
//    public GenericDataDTO deleteRole(@RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
//            roleService.deleteEntity(pojo);
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//
//
//    @GetMapping(value = "/role/excel")
//    public void roleExcel(HttpServletResponse response) throws Exception {
//        RoleService service = SpringContext.getBean(RoleService.class);
//        exportToExcel(service, response);
//    }
//
//    @GetMapping(value = "/role/pdf")
//    public void rolePDF(HttpServletResponse response) throws Exception {
//        RoleService service = SpringContext.getBean(RoleService.class);
//        exportToPDF(service, response);
//    }
//
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }
}
