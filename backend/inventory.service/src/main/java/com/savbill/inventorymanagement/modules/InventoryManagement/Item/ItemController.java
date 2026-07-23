package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.ACLMenuConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.common.FileSystemService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionsMapping;
import com.savbill.inventorymanagement.modules.acl.constants.AclConstants;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.savbill.inventorymanagement.utils.APIConstants;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Api(value = "ItemController", description = "REST APIs related to item Entity!!!!", tags = "item-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.ITEM_MANAGEMENT)
public class ItemController extends ExBaseAbstractController<ItemDto> {

    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    InOutWardMACService inOutWardMACService;
    private static String MODULE = " [CreditDocController] ";

    private static final Logger LOGGER = Logger.getLogger(ItemController.class);

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    InwardRepository repository;

    @Autowired
    FileSystemService fileSystemService;
    @Autowired
    Tracer tracer;

    public ItemController(ItemServiceImpl itemService) {
        super(itemService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ItemController]";
    }

    ItemDto itemDto = new ItemDto();
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody ItemDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        try {
            boolean flag = itemService.duplicateVerifyAtSave(entityDTO.getName());
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                ItemDto entity = itemService.saveEntity(entityDTO);
                genericDataDTO.setData(entity);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.ITEM_NAME_EXITS);
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Create item"+ LogConstant.LOG_BY_NAME+itemDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Create item"+ LogConstant.LOG_BY_NAME+entityDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page,
                                 @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize,
                                 @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder,
                                 @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy,
                                 @RequestBody GenericSearchDTO filter , HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO = super.search(page,pageSize,sortOrder,sortBy,filter,req);
            if(genericDataDTO.getDataList().isEmpty()) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Item By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED+LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search Item By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search Item By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstant.REQUEST_BY +getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+ APIConstants.EXPECTATION_FAILED + APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        } return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody ItemDto entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        boolean flag = itemService.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            dataDTO = super.delete(entityDTO, req);

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.ITEM_NAME_EXITS);
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody ItemDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        boolean flag = itemService.duplicateVerifyAtEdit(entityDTO.getName(), (entityDTO.getId()));
        try {
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                dataDTO = super.update(entityDTO, result, req);
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.ITEM_NAME_EXITS);
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item" +LogConstant.LOG_BY_NAME+ itemDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }
        catch (Exception e)
        {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item" +LogConstant.LOG_BY_NAME+ itemDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_VIEW + "\")")
    @PostMapping(value = "/getAllItemsByOwner")
    public GenericDataDTO getAllItemsByOwner(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = itemService.getAllItemsByOwner(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), ownerId,ownerType);
            if (null != genericDataDTO) {
//                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"fetch all assigned inventories"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
//                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"fetch all assigned inventories"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + genericDataDTO.getResponseMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"fetch all assigned inventories"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

// Before hitting this api, check  all ids selected has same inward, if not give error from gui
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
    @PostMapping(value = "/return")
    public GenericDataDTO returnItems(@Valid @RequestBody List<ItemReturnDTO> itemsToReturn, Authentication authentication) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        List<Long> itemIds = new ArrayList<>();
        for(ItemReturnDTO itemReturnDTO : itemsToReturn){
            itemIds.add(itemReturnDTO.getId());
        }
        boolean flag = itemService.itemReturnCheck(itemIds);
        if (flag) {
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage(itemService.returnItem(itemsToReturn));
        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.CANNOT_RETURN_ITEM);
        }
        return dataDTO;
    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
@PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_CHANGE_TYPE + "\")")
@PostMapping(value = "/updateItemTypeByList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO updateItemTypeByList(@Valid @RequestParam String entityDTOs, @RequestParam(required = false, value = "file") List<MultipartFile> file, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
    TraceContext traceContext =tracer.currentSpan().context();
    MDC.put("type", "Update");
    MDC.put("userName", getLoggedInUser().getUsername());
    MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
    MDC.put("spanId", traceContext.spanIdString());
    Integer RESP_CODE = APIConstants.FAIL;
        try {

            if (entityDTOs != null) {
                List<ItemChangeTypeDto> itemChangeTypeDtoList = new ArrayList<>(6);
                // List<StringBuffer> sb = new ArrayList<>();
                ItemChangeTypeDto itemChangeTypeDto = null;
                Gson gson = new Gson();
                ArrayList<LinkedTreeMap> list = gson.fromJson(entityDTOs, ArrayList.class);
                itemChangeTypeDtoList = list.stream().map(s -> gson.fromJson(gson.toJson(s), ItemChangeTypeDto.class)).collect(Collectors.toList());


//                    List<String> resultList = Splitter.on("//")
//                            .trimResults()
//                            .omitEmptyStrings()
//                            .splitToList(entityDTOs);
//                    //sb.get(i).append(entityDTOs.split("}"));
                //System.out.println(sb);
//                for(int i =0 ;i<file.size();i++) {
//                                     itemChangeTypeDto = new ObjectMapper().registerModule(new JavaTimeModule())
//                            .readValue(resultList.get(i), new TypeReference<ItemChangeTypeDto>() {
//                            });
//                    itemChangeTypeDtoList.add(itemChangeTypeDto);
//
//                }


                dataDTO = itemService.updateItemTypeByList(itemChangeTypeDtoList, file);
            }
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item type by list"+ LogConstant.LOG_BY_NAME+itemDto.getName() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );


        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item type by list"+ LogConstant.LOG_BY_NAME+itemDto.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return dataDTO;

    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
@PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_WARRANTY + "\")")
@PostMapping(value = "/updateItemWarrantyByList")
    public GenericDataDTO updateItemWarrantyByList(@Valid @RequestBody List<ItemWarrantyTypeDTO> itemWarrantyTypeDTOS, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
    TraceContext traceContext =tracer.currentSpan().context();
    MDC.put("type", "Update");
    MDC.put("userName", getLoggedInUser().getUsername());
    MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
    MDC.put("spanId", traceContext.spanIdString());
    Integer RESP_CODE = APIConstants.FAIL;
    ItemDto itemDto1 = new ItemDto();
        try {
            dataDTO = itemService.updateItemWarrantyByList(itemWarrantyTypeDTOS);

            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item warranty by list" +LogConstant.LOG_BY_NAME+itemDto1.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return dataDTO;

    }

    //API for Item-Type By Single Id .
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
    @GetMapping(value = "/updateItemType")
    public GenericDataDTO updateItemType(@Valid @RequestParam Long itemId, @RequestParam String itemCondition, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            if (itemId != null) {
                dataDTO = itemService.updateItemType(itemId, itemCondition);

                dataDTO.setResponseCode(HttpStatus.OK.value());
                dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update Item By type"+ itemCondition + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update Item By type"+itemCondition+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return dataDTO;

    }

    // API for Item-Warranty By Single Id .
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
    @GetMapping(value = "/updateItemWarranty")
    public GenericDataDTO updateItemWarranty(@Valid @RequestParam Long itemId, @RequestParam String itemWarranty, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            if (itemId != null) {
                Item item = itemRepository.findById(itemId).orElse(null);
                dataDTO = itemService.updateItemWarranty(item, itemWarranty);
                dataDTO.setResponseCode(HttpStatus.OK.value());
                dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item warranty"+ LogConstant.LOG_BY_NAME +itemWarranty + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item warranty"+ LogConstant.LOG_BY_NAME+itemWarranty + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return dataDTO;

    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
@PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_STATUS + "\")")
@PostMapping(value = "/updateItemStatusByList")
    public GenericDataDTO updateItemStatusByList(@Valid @RequestBody List<ItemStatusDTO> itemStatusLists, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());


    try {
            dataDTO = itemService.updateItemStatusByList(itemStatusLists);

            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item status by list: "+ itemStatusLists + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item status by list: "+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;

    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
@PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_OWNERSHIP_STATUS + "\")")
@PostMapping(value = "/updateItemOwnerShipStatusByList")
    public GenericDataDTO updateItemOwnerShipStatusByList(@Valid @RequestBody List<ItemOwnerShipDTO> itemOwnerShipDTOS, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        ItemDto itemDto1 = new ItemDto();
        try {
            dataDTO = itemService.updateItemOwnerShipStatusByList(itemOwnerShipDTOS);

            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item Owner Ship Status by List"+LogConstant.LOG_BY_NAME +itemDto1.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item Owner Ship Status by List" + LogConstant.LOG_BY_NAME +itemDto1.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return dataDTO;

    }

    @RequestMapping(value = "/documentForItemComplain/download/{conditionId}/{itemId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long conditionId, @PathVariable Integer itemId,HttpServletRequest req) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Item item = itemRepository.getOne(itemId.longValue());
            if (null == item) {
                return ResponseEntity.notFound().build();
            }
            Optional<ItemConditionsMapping> itemConditionsMapping = itemConditionMappingRepository.findById(conditionId);
            if (null == itemConditionsMapping) {
                return ResponseEntity.notFound().build();
            }
            resource = fileSystemService.getItemDoc(item.getName().trim(), itemConditionsMapping.get().getUniquename());
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Downloading document with  " + conditionId + " downloaded Successfully"+LogConstant.LOG_BY_NAME  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
//                System.out.println("dowload document");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Downloading document with  " + conditionId + " downloaded Successfully"+LogConstant.LOG_BY_NAME  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Downloading document with  " + conditionId + " downloaded Successfully" + LogConstant.LOG_BY_NAME +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }
        MDC.remove("type");
        return null;
    }


    @PostMapping("/searchItems")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS + "\")")
    public GenericDataDTO searchItems(@RequestBody PaginationRequestDTO requestDTO, @ModelAttribute SearchItemsPojo entity, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {

            if (entity != null) {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                requestDTO = setDefaultPaginationValues(requestDTO);
                ItemServiceImpl itemService1 = SpringContext.getBean(ItemServiceImpl.class);
                genericDataDTO = itemService1.serializedItems(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), entity);
            }
            if (genericDataDTO.getDataList().isEmpty()) {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No Data Found", null);
            }
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search All Inventories Items"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search All Inventories Items"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search All Inventories Items "+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PostMapping("/searchByProductAndCustomer")
     public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = itemService.searchItembasedOnProductAndCustomer(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search item using keyword " +requestDTO.getFilters().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search item using keyword " +requestDTO.getFilters().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            throw ex;
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @RequestMapping(value = "/getAllSuibiuseItem/currentInwardId", method = RequestMethod.GET)
    public List<ItemDto> getAllSuibsuOwnedItem(@RequestParam("currentInwardId") Long currentInwardId, HttpServletRequest req) {
        List<ItemDto> itemList = null;
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"fetch Inwned item"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

            itemList = itemService.findItemsSuibiseOwned(currentInwardId);
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"fetch Inwned item"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            throw new RuntimeException(ex.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return itemList;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ITEM_ALL + "\",\"" + AclConstants.OPERATION_ITEM_EDIT + "\")")
    @PostMapping("/updateItemMacAndSerial")
    public GenericDataDTO updateItemMacAndSerial(@Valid @RequestParam("itemId") Long itemId, @RequestParam("macAddress") String macAddress, @RequestParam("serialNumber") String serialNumber, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            Item item = itemRepository.findById(itemId).get();
            itemService.validateMac(macAddress, serialNumber);
            if (!Objects.equals(macAddress, "null") ) {
                boolean flag = inOutWardMACService.duplicateVerifyAtSave1(macAddress,itemId);
                if (flag) {
                    genericDataDTO.setData(itemService.updateItemMacAndSerial(itemId, macAddress, serialNumber));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item mac and serial"+ LogConstant.LOG_BY_NAME+itemDto.getName() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
                }
                else {
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item mac and serial"+ LogConstant.LOG_BY_NAME+itemDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + HttpStatus.NOT_ACCEPTABLE +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    genericDataDTO.setResponseMessage("Mac Address Already Exists, It Should Be Unique");
                }
            }
            else {
                genericDataDTO.setData(itemService.updateItemSerial(itemId, serialNumber));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item mac and serial"+ LogConstant.LOG_BY_NAME + itemDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }
        catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Update item mac and serial"+ LogConstant.LOG_BY_NAME+itemDto.getMacAddress()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_HISTORY + "\",\""+ ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_HISTORY + "\")")
    @GetMapping("/getAllCustomerInvetoryDetailshistory")
    public GenericDataDTO getAllCustomerInvetoryDetailshistory(@RequestParam("custId") Long custId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        //List<ItemStatusMapping> itemList = null;
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setDataList(itemService.getAllCustomerInvetoryHistory(custId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            //itemList = itemService.getAllCustomerInvetoryHistory(custId);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch Customer Inventory Details History "+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch Customer Inventory Details History "+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
    @GetMapping("/getItemDetails")
    public GenericDataDTO getItemDetails(@RequestParam("itemId") Long itemId, @RequestParam("custinventoryid") Long custinventoryid, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setData(itemService.getItemDetails(itemId,custinventoryid));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"fetch product Item Details"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"fetch product Item Details"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

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
