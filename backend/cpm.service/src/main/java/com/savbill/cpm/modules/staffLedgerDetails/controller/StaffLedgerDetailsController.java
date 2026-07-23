package com.savbill.cpm.modules.staffLedgerDetails.controller;

import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController2;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchDTO;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.common.StaffUser;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.modules.staffLedgerDetails.dto.StaffLedgerDetailsDto;
import com.savbill.cpm.modules.staffLedgerDetails.Service.StaffLedgerDetailsService;
import com.savbill.cpm.service.common.StaffUserService;
import com.savbill.cpm.utils.APIConstants;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.STAFF_LEDGER_DETAILS)
public class StaffLedgerDetailsController extends ExBaseAbstractController2<StaffLedgerDetailsDto> {

    @Autowired
    StaffLedgerDetailsService staffLedgerDetailsService;

    @Autowired
    StaffUserService staffUserService;

    public StaffLedgerDetailsController(StaffLedgerDetailsService staffLedgerDetailsService) {
        super(staffLedgerDetailsService);
    }

    @Override
    public String getModuleNameForLog() {
        return "{StaffLedgerDetailsController}";
    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\"" + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req) {
        return super.search(page, pageSize, sortOrder, sortBy, filter, req);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\"" + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
//    @PostMapping("/transferredToBank")
//    public StaffLedgerDetailsDto TransferredToBank(@Valid @RequestBody StaffLedgerDetailsDto entityDTO,BindingResult result) throws Exception {
//        StaffLedgerDetailsDto dataDTO = new StaffLedgerDetailsDto();
//        MDC.put("type", "Create");
//        String SUBMODULE = getModuleNameForLog() + " [TransferredToBank()] ";
//        try {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                dataDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//
//                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
//                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
//                }
//                if (getBUIdsFromCurrentStaff().size() == 1) {
//                    dataDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
//                }
//            }
//
//            dataDTO = staffLedgerDetailsService.save(entityDTO);
//
//            MDC.remove("type");
//            return dataDTO;
//        } catch (Exception e){
//            ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
//            if (e instanceof DataNotFoundException) {
//
//                return dataDTO;
//            }
//            if (e instanceof RuntimeException) {
////                genericDataDTO.setResponseMessage(e.getMessage());
////                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
////                logger.error("Unable to Upload DocFor Customer "+docDetailsList+"   Response : {module: {}}; Response : {{},message:{}; Exception: {}",getModuleNameForLog(), APIConstants.FAIL,genericDataDTO.getResponseMessage(),e.getStackTrace());
//                return dataDTO;
//            }
////            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
////            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
////            logger.error("Unable to Upload DocFor Customer "+docDetailsList+"   Response : {module: {}}; Response : {{},message:{};",getModuleNameForLog(), APIConstants.FAIL,genericDataDTO.getResponseMessage());
////            MDC.remove("type");
//            return dataDTO;
//        }
//        }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\"" + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
    @PostMapping("/transferredToBank")
    public GenericDataDTO TransferredToBank(@Valid @RequestBody List<StaffLedgerDetailsDto> entityDTO, BindingResult result) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        MDC.put("type", "Create");
        String SUBMODULE = getModuleNameForLog() + " [TransferredToBank()] ";
        List<StaffLedgerDetailsDto> staffLedgerDetailsDto = new ArrayList<>();
        for (StaffLedgerDetailsDto detailsDto : entityDTO) {
            if (getMvnoIdFromCurrentStaff() != null) {
                detailsDto.setMvnoId(getMvnoIdFromCurrentStaff());

                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
                }
                if (getBUIdsFromCurrentStaff().size() == 1) {
                    detailsDto.setBuId(getBUIdsFromCurrentStaff().get(0));
                }
            }
            try {
                staffLedgerDetailsDto.add(staffLedgerDetailsService.save(detailsDto));
                staffLedgerDetailsService.setStatusForStaffWalletAdjustedAmount(detailsDto);
            } catch (Exception ex) {
                genericDataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
            }

        }

        try {

//            if(!entityDTO.getAmountList().isEmpty()  && !entityDTO.getLedgerIds().isEmpty()){
//                staffLedgerDetailsService.insertTransfferedAmountinLedger(entityDTO.getLedgerIds() , entityDTO.getAmountList());

//            }
            genericDataDTO.setData(staffLedgerDetailsDto);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\"" + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
    @GetMapping("/getStaffLedgerDetailsbyStaffId/{id}")
    public GenericDataDTO getStaffLedgerDetailsbyStaffId(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(staffLedgerDetailsService.getStaffLedgerDetailsbyStaffId(id));
            genericDataDTO = genericDataDTO.getGenericDataDTO(staffLedgerDetailsService.getStaffLedgerDetailsbyStaffId(id));
            if (genericDataDTO != null) {
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
            }

        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Not Found");
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        MDC.remove("type");
        return genericDataDTO;
    }


  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\"" + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
    @GetMapping("/walletAmount/{id}")

    public Map<String, Double> getEntityById(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");

        GenericDataDTO dataDTO = new GenericDataDTO();
        StaffUser user = staffUserService.get(id);
        Map<String, Double> map = new HashMap<>();
        map.put("totalCollected", user.getTotalCollected());
        map.put("totalTransferred", user.getTotalTransferred());
        map.put("availableAmount", user.getAvailableAmount());


        MDC.remove("type");
        return map;
    }

    @GetMapping("/getAllTransferById/{id}")
    public GenericDataDTO getAllTransfferedId(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = genericDataDTO.getGenericDataDTO(staffLedgerDetailsService.getStaffLedgerDetailsbyTransfered(id));
            genericDataDTO.setData(staffLedgerDetailsService.getTotalAmountFromID(id).toString());
            if (genericDataDTO != null) {
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
            }

        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Not Found");
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping("/getStaffLedgerDetailsbyStaffIdAndPaymentType/{id}/{type}")
    public GenericDataDTO getStaffLedgerDetailsbyStaffIdAndType(@PathVariable Integer id, @PathVariable String type, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(staffLedgerDetailsService.getStaffLedgerDetailsbyStaffId(id));
            genericDataDTO = genericDataDTO.getGenericDataDTO(staffLedgerDetailsService.getStaffLedgerDetailsbyStaffIdandPaymentMode(id, type));
            if (genericDataDTO != null) {
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
            }

        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Not Found");
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        MDC.remove("type");
        return genericDataDTO;
    }


}

