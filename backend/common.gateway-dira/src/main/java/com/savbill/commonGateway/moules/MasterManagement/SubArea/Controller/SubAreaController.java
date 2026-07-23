package com.savbill.commonGateway.moules.MasterManagement.SubArea.Controller;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.ClientServiceConstant;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.dto.ValidationData;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Repository.BuildingMgmtRepository;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Service.BuildingMgmtService;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaAll;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Mapper.SubAreaMapper;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Repository.SubAreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Service.SubAreaService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SUB_AREA)
public class SubAreaController extends ExBaseAbstractController<SubAreaDTO> {

    @Autowired
    SubAreaService subAreaService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    BuildingMgmtService buildingMgmtService;

    @Autowired
    SubAreaRepository subAreaRepository;

    @Autowired
    private SubAreaMapper subAreaMapper;

    private static final Logger logger = LoggerFactory.getLogger(ExBaseAbstractController.class);
//    private static final Logger logger = LoggerFactory.getLogger(SubAreaController.class);

    public SubAreaController(SubAreaService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[SubAreaController]";
    }



    @Override
    @PostMapping
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, HttpServletResponse res){
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
//
//                genericDataDTO = subAreaService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder()
//                        , requestDTO.getFilters());
//
//            else
//                genericDataDTO = subAreaService.search(requestDTO.getFilters()
//                        , requestDTO.getPage(), requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder());
//
//
//            if (null != genericDataDTO&& genericDataDTO.getDataList() != null && !genericDataDTO.getDataList().isEmpty()) {
//                //                  logger.info("Fetching data :  request: { From : {}}; Response : {Code{},Message:{};}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
//                genericDataDTO.setResponseMessage("No records found.");
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                //                   logger.info("Unable to fetch all Entities   :  request: { module : {}}; Response : {Code{},Message:{};}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            }
            genericDataDTO = super.getAll(requestDTO, req, res);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to fetch all Entities   :  request: { Module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    @Override
    @GetMapping("{id}")
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        long startTime = System.nanoTime();  // Start measuring
        genericDataDTO.setResponseMessage("Success");
        logger.info("Fetching All Entities by id "+id+" :  request: {Module:{} }; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        try {
            genericDataDTO.setData(subAreaService.getEntityById(new Long(id)));
            genericDataDTO.setTotalRecords(1);
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            logger.error("Unable to fetch Entity by id "+id+"  :  request: { Module : {}};  Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    //@Override
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO save(@RequestParam String entityDTO,@RequestParam(value = "file", required = false) MultipartFile[] documents,HttpServletRequest req,HttpServletResponse res) throws Exception {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Successfully Created");
        SubAreaDTO subAreaDTO = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(entityDTO, new TypeReference<SubAreaDTO>() {
                });

         logger.info("Creating New Entity with MVNO Id "+subAreaDTO.getId()+" :  request: { Module : {}, }; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        long startTime = System.nanoTime();  // Start measuring
        try {
            ValidationData validation = validateSave(subAreaDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("Unable Create New Records With MVNO Id "+subAreaDTO.getMvnoId()+":  request: { Module : {}}}; Response : {Code{},Message:{};};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
//            #==================  Code for Duplicate Varification  ===================#
            Long pincodeId=null;
            if (subAreaDTO.getAreaId()!=null){
                           pincodeId = subAreaService.findPincodeIdByAreaId(subAreaDTO.getAreaId());
            }

            boolean flag = subAreaService.duplicateVerification1(subAreaDTO.getName(), subAreaDTO.getCityId(), subAreaDTO.getStateId(), subAreaDTO.getAreaId(),pincodeId, null, CommonConstants.OPERATION_ADD);
            if (flag) {
                ApplicationLogger.logger.info(getModuleNameForLog() + " entityDto :: " + subAreaDTO);
                SubAreaDTO dtoData = subAreaService.saveEntity(subAreaDTO);
                subAreaDTO.setId(dtoData.getId());
                if (documents != null && documents.length>0) {
                    subAreaDTO = subAreaService.uploadDocumentsForSubArea(subAreaDTO,documents);
                    subAreaDTO = subAreaService.saveEntity(subAreaDTO);
                }
                createDataSharedService.sendEntitySaveDataForAllMicroService(subAreaDTO);
                genericDataDTO.setData(dtoData);
                genericDataDTO.setTotalRecords(1);
                logger.info("Creating New Entity with MVNO Id "+subAreaDTO.getMvnoId()+" :  request: { module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Same Name Already Exist");
                logger.info("Unable Create New Records With MVNO Id "+subAreaDTO.getMvnoId()+":  request: { Module : {}}}; Response : {Code{},Message:{};};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            logger.error("Unable to Create Entity With MVNO id "+subAreaDTO.getMvnoId()+" :  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;
    }


    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO update(@RequestParam String entityDTO,@RequestParam(value = "file", required = false) MultipartFile[] documents,HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Successfully Created");
        SubAreaDTO subAreaDTO = new ObjectMapper().registerModule(new JavaTimeModule())
                .readValue(entityDTO, new TypeReference<SubAreaDTO>() {
                });
        long startTime = System.nanoTime();  // Start measuring
        try {
//            if (result.hasErrors()) {
//               ApplicationLogger.logger.debug("Base Controller Error"+result.getFieldErrors());
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
//                logger.error("Unable to fetch Update Entity "+entityDTO.getIdentityKey()+"  :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
            ValidationData validation = validateUpdate(subAreaDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("Unable to update   "+getModuleNameForLog()+" With  id "+subAreaDTO.getIdentityKey()+"  :  request: { Module : {}}; Response : {Code{};}", getModuleNameForLog(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }

            SubAreaDTO dtoData = subAreaService.getEntityForUpdateAndDelete(subAreaDTO.getIdentityKey());
//            #==================  Code for Duplicate Varification  ===================#
            Long pincodeId=null;
            if (subAreaDTO.getAreaId()!=null){
                pincodeId = subAreaService.findPincodeIdByAreaId(subAreaDTO.getAreaId());
            }
            boolean flag = subAreaService.duplicateVerification1(subAreaDTO.getName(), subAreaDTO.getCityId(), subAreaDTO.getStateId(),subAreaDTO.getAreaId(), pincodeId, subAreaDTO.getId(), CommonConstants.OPERATION_UPDATE);
            if (flag) {
//            entityDTO.setMvnoId(dtoData.getMvnoId());
                // String updatedValues = CommonUtils.getUpdatedDiff(dtoData,entityDTO);
                SubAreaDTO dto = subAreaService.updateEntity(subAreaDTO);
                genericDataDTO.setData(dto);
                buildingMgmtService.updateBuildingNamesBySubAreaId(subAreaDTO.getId().intValue(), subAreaDTO.getName());
                createDataSharedService.updateEntityDataForAllMicroService(subAreaDTO);

                if (documents != null) {
                    subAreaDTO = subAreaService.uploadDocumentsForSubArea(subAreaDTO, documents);
                    subAreaDTO = subAreaService.saveEntity(subAreaDTO);
                }

                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
                genericDataDTO.setTotalRecords(1);
                //logger.info("Updating All  "+updatedValues+" :  request: { module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            } else{
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Same Profile Name Already Exist");
                logger.info("Unable to update   "+getModuleNameForLog()+" With  id "+subAreaDTO.getIdentityKey()+"  :  request: { Module : {}}; Response : {Code{};}", getModuleNameForLog(),genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                //  ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to Update "+getModuleNameForLog() +" by id  :  request: { module : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
            } else if (ex instanceof CustomValidationException){
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                logger.error("Unable to update "+getModuleNameForLog() +" by id  :  request: { From : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                logger.error("Unable to Update "+getModuleNameForLog() +" by id  :  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
            }
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    @DeleteMapping(value = "/delete/{subAreaId}")
    public GenericDataDTO delete(@PathVariable Integer subAreaId,HttpServletResponse res,HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            SubAreaDTO dtoData = subAreaService.getEntityForUpdateAndDelete(subAreaId.longValue());
            ApplicationLogger.logger.info(getModuleNameForLog() + " [DELETE] " + dtoData);
            dtoData.setIsDeleted(true);
            subAreaRepository.deleteById(subAreaId.longValue());
            createDataSharedService.updateEntityDataForAllMicroService(dtoData);
            genericDataDTO.setData(dtoData);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successfully Deleted");

            logger.info("Deleting  Entity by id "+subAreaId+" :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            //   logger.info(getModuleNameForLog()+"is  deleted with "+entityDTO+"   :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);

        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to delete Entity by id "+subAreaId+" :  request: { module : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
            }  else if (ex instanceof CustomValidationException){
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                logger.error("Unable to Delete Entity by id "+subAreaId+" :  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
                logger.error("Unable to Delete Entity by id  "+subAreaId+":  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
            }
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    @GetMapping(path = "/all")
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        long startTime = System.nanoTime();  // Start measuring
        try {
            List<SubAreaAll> list = subAreaService.getAllSubareas();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;
    }

    @GetMapping(path = "/allWithPagination")
    public GenericDataDTO getAllWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long area,
            HttpServletRequest req,
            HttpServletResponse res) {

        long startTime = System.nanoTime();
        GenericDataDTO genericDataDTO;

        try {
            genericDataDTO = subAreaService.getAllSubAreasWithPagination(page, pageSize, area);

            logger.info("Fetching ALL DATA with pagination : request: { Module : {} }; Response : { Code:{}, Message:{} }",
                    getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);

            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;
    }



    @PostMapping(value = "/search")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
//            if (genericDataDTO.getResponseCode() == 406)
//            {
//                List<DTO> list = service.getAllEntities().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff() || d.getMvnoId() == null ).collect(Collectors.toList());
//                genericDataDTO.setDataList(list);
//                genericDataDTO.setTotalRecords(list.size());
//                return genericDataDTO;
//            }
            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
                logger.error("Unable to Search data by  "+filter.getFilter()+":  request: { module : {}}; Response : {Code{},Message:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            if (null != pageSize && pageSize > MAX_PAGE_SIZE)
                pageSize = MAX_PAGE_SIZE;
            genericDataDTO = subAreaService.search(filter.getFilter(), page, pageSize, sortBy, sortOrder);

            if (null != genericDataDTO) {

                if(genericDataDTO.getDataList().isEmpty())
                {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);
                    logger.info("Fetching data with  filter "+filter.getFilter()+":  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());

                }
                return genericDataDTO;

            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                logger.error("Unable to Search data by  "+filter.getFilter()+":  request: { module : {}}; Response : {Code{},Message:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to Search data by  "+filter.getFilter()+":  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    @GetMapping("/getAreaIdFromSubAreaId")
    public GenericDataDTO getAreaIdFromSubAreaId(@RequestParam(required = true,value = "subAreaId") Long subAreaId){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            ApplicationLogger.logger.info(getModuleNameForLog() + " [getAreaIdFromSubAreaId] " );
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setData(subAreaRepository.findAreaIdBySubAreaId(subAreaId));
            return genericDataDTO;
        }catch (Exception e){
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            return genericDataDTO;
        }
    }

    @GetMapping("/getSubAreaFromArea")
    public GenericDataDTO getSubAreaFromArea(@RequestParam(required = true,value = "areaId") Long areaId){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            ApplicationLogger.logger.info(getModuleNameForLog() + " [getAreaIdFromSubAreaId] " );
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(subAreaRepository.findSubAreaFromAreaID(areaId));
            return genericDataDTO;
        }catch (Exception e){
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            return genericDataDTO;
        }
    }
    @RequestMapping(value = "/document/download/{subareaId}/{uniqueName}/", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Integer subareaId, @PathVariable String uniqueName) {
        org.slf4j.MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [downloadDocument()] ";
        Resource resource = null;
        try {
            resource =  subAreaService.getsubareadoc(subareaId,uniqueName);
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Downloading document with  " + subareaId + " downloaded Successfully  :  request: { From : {} }; Response : {{}}");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                String errorMessage = "File not found: " + uniqueName + " for subareaId: " + subareaId;
                logger.error(errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Error-Message", errorMessage).build();
            }
        } catch (Exception ex) {
            logger.error("Unable to downloadDocument " + subareaId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}");
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        org.slf4j.MDC.remove("type");
        return null;
    }

    @RequestMapping(value = "/document/delete/{subareaId}/{uniqueName}/{fileName}/", method = RequestMethod.DELETE)
    public GenericDataDTO deleteDocument(@PathVariable Integer subareaId, @PathVariable String uniqueName,@PathVariable String fileName) {
        org.slf4j.MDC.put("type", "Delete");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {

            File file = subAreaService.getsubareadocdelete(subareaId, uniqueName);
            if (!file.exists()) {
                logger.error("File not found: {} for subareaId: {}" + uniqueName + subareaId);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("File not found.");
                return genericDataDTO;
            } else if (file.exists()) {
                SubArea subArea = subAreaRepository.findById(subareaId.longValue()).orElse(null);
                if(subArea == null){
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Sub area not found for given id",null);
                }
                String existingFileNameList = subArea.getFilename();
                String existingUniqueNameList = subArea.getUniquename();

                List<String> existingFilenames = new ArrayList<>(Arrays.asList(existingFileNameList.split("\\s*,\\s*")));
                List<String> existingUniqueNames = new ArrayList<>(Arrays.asList(existingUniqueNameList.split("\\s*,\\s*")));

                existingFilenames.removeIf(oldfileName -> oldfileName.equals(fileName));
                existingUniqueNames.removeIf(olduniqueName -> olduniqueName.equals(uniqueName));
                String filenamesString = String.join(",", existingFilenames);
                String uniquenameString = String.join(",", existingUniqueNames);
                subArea.setFilename(filenamesString);
                subArea.setUniquename(uniquenameString);
                subAreaRepository.save(subArea);


                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("File deleted successfully.");

            } else {
                logger.error("Failed to delete file: {} for subareaId: {}" + uniqueName + subareaId);
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                genericDataDTO.setResponseMessage("Failed to delete file.");
            }
            if (file.delete()) {
                logger.info("File deleted successfully: {} for subareaId: {}" + uniqueName + subareaId);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("File deleted successfully.");
            } else {
                logger.error("Failed to delete file: {} for subareaId: {}" + uniqueName + subareaId);
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                genericDataDTO.setResponseMessage("Failed to delete file.");
            }
        } catch (Exception ex) {
            logger.error("Error occurred while deleting file for subareaId: {}" + subareaId, ex);
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage("An error occurred while deleting the file.");
        } finally {
            org.slf4j.MDC.remove("type");
        }
        return genericDataDTO;
    }
}
