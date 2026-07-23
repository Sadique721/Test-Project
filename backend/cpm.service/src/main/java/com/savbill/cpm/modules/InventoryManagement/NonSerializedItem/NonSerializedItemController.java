package com.savbill.cpm.modules.InventoryManagement.NonSerializedItem;


import com.savbill.cpm.constants.MessageConstants;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchDTO;
import com.savbill.cpm.modules.CreditDocController;
import com.savbill.cpm.modules.InventoryManagement.inward.InwardRepository;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.service.common.ClientServiceSrv;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Api(value = "NonSerializedItemController", description = "REST APIs related to non-serialized item Entity!!!!", tags = "non-serialized-item-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.NON_SERIALIZED_ITEM_MANAGEMENT)
public class NonSerializedItemController extends ExBaseAbstractController<NonSerializedItemDto> {

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    NonSerializedItemServiceImpl nonSerializedItemService;

    private static String MODULE = " [NonSerializedItemController] ";

    private static final Logger logger = LoggerFactory.getLogger(CreditDocController.class);

    @Autowired
    NonSerializedItemRepository nonSerializedItemRepository;

    @Autowired
    InwardRepository inwardRepository;


    public NonSerializedItemController(NonSerializedItemServiceImpl nonSerializedItemService) {
        super(nonSerializedItemService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[NonSerializedItemController]";
    }


    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody NonSerializedItemDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        try {
            boolean flag = nonSerializedItemService.duplicateVerifyAtSave(entityDTO.getName());
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                NonSerializedItemDto entity = nonSerializedItemService.saveEntity(entityDTO);
                genericDataDTO.setData(entity);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.NON_SERIALIZED_ITEM_NAME_EXITS);
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page, @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize, @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder, @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req) {
        return super.search(page, pageSize, sortOrder, sortBy, filter , req);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody NonSerializedItemDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        boolean flag = nonSerializedItemService.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            dataDTO = super.delete(entityDTO, authentication, req);

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.NON_SERIALIZED_ITEM_NAME_EXITS);
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody NonSerializedItemDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        boolean flag = nonSerializedItemService.duplicateVerifyAtEdit(entityDTO.getName(), (entityDTO.getId()));
//        if (flag) {
//            if(getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            dataDTO = super.update(entityDTO, result, authentication, req);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.NON_SERIALIZED_ITEM_NAME_EXITS);
//        }
//        return dataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_VIEW + "\")")
//    @PostMapping("/searchItems")
//    public GenericDataDTO searchNonSerializedItems(@RequestBody PaginationRequestDTO requestDTO, @ModelAttribute SearchItemsPojo entity) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (entity != null) {
//                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                requestDTO = setDefaultPaginationValues(requestDTO);
//                NonSerializedItemServiceImpl nonSerializedItemService1 = SpringContext.getBean(NonSerializedItemServiceImpl.class);
//                genericDataDTO = nonSerializedItemService1.searchItems(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), entity);
//            }
//            if (genericDataDTO.getDataList().isEmpty()) {
//                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No Data Found", null);
//            }
//        } catch (CustomValidationException ce) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//        } catch (Exception ex) {
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return genericDataDTO;
//    }
//
//
//    @RequestMapping(value = "/getAllSuibiuseItem/currentInwardId", method = RequestMethod.GET)
//    public List<ItemDto> getAllSuibsuOwnedItem(@RequestParam("currentInwardId") Long currentInwardId) {
//        List<ItemDto> itemList = null;
//        try {
//            itemList = itemService.findItemsSuibiseOwned(currentInwardId);
//        } catch (Exception ex) {
//            throw new RuntimeException(ex.getMessage());
//        }
//        return itemList;
//    }
}
