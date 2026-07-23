package com.savbill.cpm.modules.Matrix.controller;

import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.constants.DeleteContant;
import com.savbill.cpm.constants.MessageConstants;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController2;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.modules.Matrix.domain.Matrix;
import com.savbill.cpm.modules.Matrix.model.MatrixDTO;
import com.savbill.cpm.modules.Matrix.service.MatrixService;
import com.savbill.cpm.modules.Pincode.controller.PincodeController;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.Matrix)
public class MatrixController extends ExBaseAbstractController2<MatrixDTO> {

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    MatrixService matrixService;


    public MatrixController(MatrixService service) {
        super(service);
    }

    private static  final Logger logger= LoggerFactory.getLogger(PincodeController.class);

    @Override
    public String getModuleNameForLog() {
        return "[MatrixController]";
    }

    @Override
//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_MATRIX_ADD + "\")")
    public GenericDataDTO save(@Valid @RequestBody MatrixDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        boolean flag = matrixService.duplicateVerifyAtSave(entityDTO.getName());
        MatrixDTO matrixDTO = null;
        if (flag) {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());

                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
                }
                if (getBUIdsFromCurrentStaff().size() == 1) {
                    entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
                }
            }

            if(getLoggedInUser().getLco())
                entityDTO.setLcoId(getLoggedInUser().getPartnerId());
            else
                entityDTO.setLcoId(null);

            dataDTO = super.save(entityDTO, result, authentication, req);
            matrixDTO = (MatrixDTO) dataDTO.getData();
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MATRIX,
                    AclConstants.OPERATION_MATRIX_ADD, req.getRemoteAddr(), null, entityDTO.getId(), entityDTO.getName());
        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.MATRIX_NAME);


        }
        MDC.remove("type");
        return dataDTO;
    }

    //Get All MATRIX With Pagination

//    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_MATRIX_VIEW + "\")")
    @PostMapping(value = "/searchAll")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO) {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO=matrixService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            logger.info("Fetching all TatMatrix"+paginationRequestDTO.getFilterBy()+":  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable to fetch TatMatrix "+paginationRequestDTO.getFilterBy()+"  :  request: { From : {},}; Response : {{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(),ce.getStackTrace());
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable to fetch TatMatrix "+paginationRequestDTO.getFilterBy()+"  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //Update MATRIX Policy
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_MATRIX_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody MatrixDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        MDC.put("type", "Update");
        if(getMvnoIdFromCurrentStaff() != null) {
           entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
       }
        MatrixDTO td=matrixService.getEntityById(entityDTO.getId());
        GenericDataDTO dataDTO = new GenericDataDTO();
        String updatedValues = CommonUtils.getUpdatedDiff(entityDTO,td);
        boolean flag = matrixService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
        if (flag) {
            dataDTO = super.update(entityDTO, result, authentication, req);
            MatrixDTO matrixDTO = (MatrixDTO) dataDTO.getData();
            if(matrixDTO != null) {
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MATRIX,
                        AclConstants.OPERATION_MATRIX_EDIT, req.getRemoteAddr(), null, matrixDTO.getId(), matrixDTO.getName());
            }
        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.MATRIX_NAME);
        }
        MDC.remove("type");
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_MATRIX_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO dataDTO = super.getEntityById(id, req);
        MatrixDTO matrixDTO = (MatrixDTO) dataDTO.getData();
//        String matrixname = matrixService.getid(matrixDTO.getMatrixDetailsList().get(0).getId());
//        matrixDTO.setName(matrixname);

        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MATRIX,
                AclConstants.OPERATION_MATRIX_VIEW, req.getRemoteAddr(), null, matrixDTO.getId(), matrixDTO.getName());
        MDC.remove("type");
        return dataDTO;
    }
    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_MATRIX_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody MatrixDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
          MDC.put("type", "Delete");
          boolean flag = matrixService.deleteVerification(entityDTO.getId().intValue());
          if (flag) {
              dataDTO = super.delete(entityDTO, authentication, req);
              MatrixDTO matrixDTO = (MatrixDTO) dataDTO.getData();
              if (matrixDTO != null) {
                  auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MATRIX,
                          AclConstants.OPERATION_MATRIX_DELETE, req.getRemoteAddr(), null, matrixDTO.getId(), matrixDTO.getName());
              }
          } else {
              dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
              dataDTO.setResponseMessage(DeleteContant.MATRIX_EXIST);
          }
      }catch (Exception ex){
            if (ex instanceof RuntimeException) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(DeleteContant.MATRIX_EXIST);

            } else {
                ex.printStackTrace();

            }
      }
        MDC.remove("type");
        return dataDTO;
    }

    //Get all with Pagination
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_MATRIX_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        requestDTO=setDefaultPaginationValues(requestDTO);
        GenericDataDTO dataDTO= matrixService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage(),requestDTO.getPageSize(),requestDTO.getSortBy(),requestDTO.getSortOrder(),requestDTO.getFilters());
        return dataDTO;
    }

    ////@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_MATRIX_VIEW + "\")")
    @GetMapping("/status")
    public List<Matrix> getbyStatus ()
    {
        List<Matrix> list = matrixService.matrixList();
        return list;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

}
