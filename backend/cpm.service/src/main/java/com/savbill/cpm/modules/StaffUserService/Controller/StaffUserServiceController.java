package com.savbill.cpm.modules.StaffUserService.Controller;

import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.common.StaffUser;
import com.savbill.cpm.modules.StaffUserService.Service.StaffUserServiceService;
import com.savbill.cpm.modules.StaffUserService.model.StaffUserServiceDTO;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.pojo.api.StaffUserAllPojo;
import com.savbill.cpm.service.common.ClientServiceSrv;
import com.savbill.cpm.service.common.StaffUserService;
import com.savbill.cpm.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/cpm")
public class StaffUserServiceController extends ExBaseAbstractController<StaffUserServiceDTO> {

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    StaffUserServiceService staffUserServiceService;

    @Autowired
    AuditLogService auditLogService;
    private static final Logger logger = LoggerFactory.getLogger(StaffUserServiceService.class);


    public StaffUserServiceController(StaffUserServiceService service) {
        super(service);
    }

    @PostMapping("/staff/searchbyReciept")
    public GenericDataDTO getStaffListByPagination(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name="prefix",defaultValue = "") String prefix, @RequestParam(name = "recieptNo") Integer recieptNo) {
        String SUBMODULE = getModuleNameForLog() + " [StaffUserServiceController] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try{
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (recieptNo != null) {
                genericDataDTO = staffUserServiceService.getStaffbyRecieptNumber(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(),prefix,recieptNo);
            }else{
                genericDataDTO.setResponseMessage("Reciept number cannot be Null");
                genericDataDTO.setResponseCode(APIConstants.FAIL);
            }
            if(genericDataDTO != null){
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(0);
                return genericDataDTO;
            }else{
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        MDC.remove("type");
        return genericDataDTO;
    }


    @PostMapping("/staff/Reciept")
    public GenericDataDTO createStaffReciptmapping(@Valid @RequestBody StaffUserServiceDTO staffUserServiceDTO) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        try {

                genericDataDTO = staffUserServiceService.save(staffUserServiceDTO);
               if(genericDataDTO != null){
                   genericDataDTO.setResponseCode(HttpStatus.OK.value());
                   genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                   logger.info("Recipt details Created with name "+staffUserServiceDTO.getPrefix(),genericDataDTO.getResponseCode());
               }else{
                   genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                   genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                   logger.error("Error While Recipt  with name "+staffUserServiceDTO.getPrefix(),genericDataDTO.getResponseCode());

               }
        }
        catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());

            logger.error("Error While Recipt  with name "+staffUserServiceDTO.getPrefix(),genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        return genericDataDTO;
    }


    @PostMapping(value = "/staff/uploadProfileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO uploadProfileImage(@RequestParam(value = "file") MultipartFile file,@RequestParam(name = "staffId") Integer staffId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        try {
            genericDataDTO = staffUserServiceService.uploadProfileImage(file,staffId);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }
    @PostMapping(value = "/staff/getProfileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO getProfileImage(@RequestParam(name = "staffId") Integer staffId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        try {
            genericDataDTO = staffUserServiceService.getProfileImage(staffId);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getstaffuserbyserviceareaid/{id}")
    public GenericDataDTO getStaffUserByServiceAreaId(@PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        try {
            genericDataDTO.setDataList(staffUserService.getAllStaffByServiceAreaId(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserService.getAllStaffByServiceAreaId(id).size());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/staffsByServiceAreaId/{id}")
    public GenericDataDTO staffsByServiceAreaId(@PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        try {
            genericDataDTO.setDataList(staffUserService.getStaffUserByServiceAreaId(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserService.getStaffUserByServiceAreaId(id).size());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }
    @GetMapping("/staffList/all")
    public GenericDataDTO getAllActiveStaffList() throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        try {
            List<StaffUser> staffUserList = staffUserService.getAllActiveEntitiesStaff().stream().filter(x->x.getStatus().equalsIgnoreCase("active")).collect(Collectors.toList());
            genericDataDTO.setDataList(staffUserService.convertResponseModelIntoStaffUserAllPojo(staffUserList).stream()
                    .sorted(Comparator.comparing(StaffUserAllPojo::getId).reversed()).collect(Collectors.toList()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(staffUserList.size());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }
@GetMapping("/staffReceipt/{id}")
public GenericDataDTO getStaffReceiptDataByStaffId(@PathVariable Integer id){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO.setDataList(staffUserServiceService.getStaffRecieptDataByStaffId(id));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());

        }
        catch (Exception exception){
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        return genericDataDTO;
}



    @Override
    public String getModuleNameForLog() {
        return "[StaffUserServiceController]";
    }
}
