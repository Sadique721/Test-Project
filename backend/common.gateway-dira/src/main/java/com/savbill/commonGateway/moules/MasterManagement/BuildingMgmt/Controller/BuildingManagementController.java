package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Controller;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.ClientServiceConstant;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.dto.ValidationData;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingManagementDTO;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMappingDTO;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingManagement;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Repository.BuildingMgmtRepository;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Service.BuildingMgmtService;
import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Entity.BuildingRefrence;
import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Repocitory.BuildingReferenceRepocitory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BUILDING_MGMT)
public class BuildingManagementController extends ExBaseAbstractController<BuildingManagementDTO> {


    @Autowired
    BuildingMgmtService buildingMgmtService;


    @Autowired
    BuildingMgmtRepository buildingMgmtRepository;


    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    BuildingReferenceRepocitory buildingReferenceRepocitory;


    public BuildingManagementController(BuildingMgmtService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[BuildingMgmtController]";
    }


    private static final Logger logger = LoggerFactory.getLogger(BuildingManagementController.class);




    // getAll API
    @Override
    @PostMapping
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req ,HttpServletResponse res) {
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
//
//                genericDataDTO = buildingMgmtService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder()
//                        , requestDTO.getFilters());
//
//            else
//                genericDataDTO = buildingMgmtService.search(requestDTO.getFilters()
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


    // get EntityById API
    @Override
    @GetMapping("{id}")
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        long startTime = System.nanoTime();  // Start measuring
        genericDataDTO.setResponseMessage("Success");
        logger.info("Fetching All Entities by id " + id + " :  request: {Module:{} }; Response : {Code{},Message:{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        try {
            genericDataDTO.setData(buildingMgmtService.getEntityById(new Long(id)));
            genericDataDTO.setTotalRecords(1);
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            logger.error("Unable to fetch Entity by id " + id + "  :  request: { Module : {}};  Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    // save API
    //@Override
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO save(@Valid @RequestParam String entityDTO, @RequestParam(required = false, value = "file") MultipartFile file, HttpServletRequest req,HttpServletResponse res) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Successfully Created");
        long startTime = System.nanoTime();  // Start measuring
        try {
            BuildingRefrence buildingRefrence = buildingReferenceRepocitory.findByMvnoId(getMvnoIdFromCurrentStaff());
            if (buildingRefrence != null) {
                BuildingManagementDTO buildingManagementDTO = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(entityDTO, new TypeReference<BuildingManagementDTO>() {
                        });

                ValidationData validation = validateSave(buildingManagementDTO);
                Boolean validateBuilding=buildingMgmtService.validateBuilding(buildingManagementDTO);
                if (!validation.isValid() && validateBuilding) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(validation.getMessage());
                    return genericDataDTO;
                }

                // Handle CSV File
                if (file != null && !file.isEmpty()) {
                    List<BuildingMappingDTO> mappings = buildingMgmtService.processCsvFile(file, false, null);
                    buildingManagementDTO.setBuildingMappings(mappings);
                }

                BuildingManagementDTO dtoData = buildingMgmtService.saveEntity(buildingManagementDTO);

                createDataSharedService.sendEntitySaveDataForAllMicroService(dtoData);
                genericDataDTO.setData(dtoData);
                genericDataDTO.setTotalRecords(1);
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please Configure Building Reference Before Using Building Management in The System", null);
            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;
    }


    // update API
    @PostMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public GenericDataDTO update(@Valid @RequestParam String entityDTO, @RequestParam(required = false, value = "file") MultipartFile file, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {

            BuildingManagementDTO buildingManagementDTO = new ObjectMapper().registerModule(new JavaTimeModule())
                    .readValue(entityDTO, new TypeReference<BuildingManagementDTO>() {
                    });

            ValidationData validation = validateUpdate(buildingManagementDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("Unable to update   " + getModuleNameForLog() + " With  id " + buildingManagementDTO.getBuildingMgmtId() + "  :  request: { Module : {}}; Response : {Code{};}", getModuleNameForLog(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }

            BuildingManagementDTO dtoData = buildingMgmtService.getEntityForUpdateAndDelete(buildingManagementDTO.getBuildingMgmtId());

            // Handle CSV File
            if (file != null && !file.isEmpty()) {
                List<BuildingMappingDTO> mappings = buildingMgmtService.processCsvFile(file, true, dtoData.getBuildingMappings());
                buildingManagementDTO.setBuildingMappings(mappings);
            }
            BuildingManagementDTO managementDTO = buildingMgmtService.updateEntity(buildingManagementDTO);
            genericDataDTO.setData(managementDTO);
            createDataSharedService.updateEntityDataForAllMicroService(managementDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(1);
            //logger.info("Updating All  "+updatedValues+" :  request: { module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                //  ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to Update " + getModuleNameForLog() + " by id  :  request: { module : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
            } else if (ex instanceof CustomValidationException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                logger.error("Unable to update " + getModuleNameForLog() + " by id  :  request: { From : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());

            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                logger.error("Unable to Update " + getModuleNameForLog() + " by id  :  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
            }
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    // delete API
    @Override
    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO delete(@RequestBody BuildingManagementDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            BuildingManagementDTO buildingManagementDTO = buildingMgmtService.getEntityForUpdateAndDelete(entityDTO.getBuildingMgmtId());
            ApplicationLogger.logger.info(getModuleNameForLog() + " [DELETE] " + buildingManagementDTO);
            buildingMgmtService.deleteEntity(buildingManagementDTO);
            genericDataDTO.setData(buildingManagementDTO);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successfully Deleted");
            logger.info("Deleting  Entity by id " + entityDTO.getIdentityKey() + " :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            //   logger.info(getModuleNameForLog()+"is  deleted with "+entityDTO+"   :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);

        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to delete Entity by id " + entityDTO.getIdentityKey() + " :  request: { module : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
            } else if (ex instanceof CustomValidationException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                logger.error("Unable to Delete Entity by id " + entityDTO.getIdentityKey() + " :  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
                logger.error("Unable to Delete Entity by id  " + entityDTO.getIdentityKey() + ":  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
            }
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    //getall withoutpgination API

    @Override
    @GetMapping(path = "/all")
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        long startTime = System.nanoTime();  // Start measuring
        try {
            List<BuildingManagementDTO> list = buildingMgmtService.getAllEntities().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || d.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
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


    @PostMapping(value = "/search")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO genericDataDTO = new GenericDataDTO();
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
                logger.error("Unable to Search data by  " + filter.getFilter() + ":  request: { module : {}}; Response : {Code{},Message:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            if (null != pageSize && pageSize > MAX_PAGE_SIZE)
                pageSize = MAX_PAGE_SIZE;
            genericDataDTO = buildingMgmtService.search(filter.getFilter(), page, pageSize, sortBy, sortOrder);

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
                    logger.info("Fetching data with  filter " + filter.getFilter() + ":  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());

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
                logger.error("Unable to Search data by  " + filter.getFilter() + ":  request: { module : {}}; Response : {Code{},Message:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());

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


    @GetMapping("/getBuildingMgmt")
    public GenericDataDTO getBuildingMGMT(@RequestParam("entityname") String entityname, @RequestParam("entityid") Long entityid) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            genericDataDTO.setDataList(buildingMgmtService.getBuildingByEntity(entityname, entityid));
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to Fetch data by  " + entityname + ":  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getMessage());
        }
        return genericDataDTO;
    }


    @PostMapping(path = "/allWithPage")
    public GenericDataDTO getAllWithPagination(@RequestBody PaginationRequestDTO paginationRequestDTO) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");

        try {
            Page<BuildingManagement> entityPage = buildingMgmtService.getAllEntitiesWithPage(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize());
            genericDataDTO.setDataList(entityPage.getContent());
            int totalRecords = genericDataDTO.getDataList().size();
            int totalpages = (int) Math.ceil((double) totalRecords / paginationRequestDTO.getPageSize());
            int fromIndex = (paginationRequestDTO.getPage() - 1) * paginationRequestDTO.getPageSize();
            int toIndex = Math.min(fromIndex + paginationRequestDTO.getPageSize(), totalRecords);

            if (totalRecords > 0) {
                List paginateList = genericDataDTO.getDataList().subList(fromIndex, toIndex);
                genericDataDTO.setDataList(paginateList);
                genericDataDTO.setTotalRecords(totalRecords);
                genericDataDTO.setPageRecords(paginateList.size());
                genericDataDTO.setCurrentPageNumber(paginationRequestDTO.getPage());
                genericDataDTO.setTotalPages(totalpages);
            }
            logger.info("Fetching paginated data - Page: {}, Size: {}, Total Records: {}",
                    paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), totalRecords);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data - Exception: {}", ex.getMessage());
        }

        return genericDataDTO;
    }
    @GetMapping("/getBuildingMgmtNumbers")
    public GenericDataDTO getBuildingMGMTNumbers(@RequestParam("buildingMgmtId") Integer buildingMgmtId,HttpServletRequest req){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            genericDataDTO.setDataList(buildingMgmtService.getAvailableBuildingMgmtNumbers(buildingMgmtId,req.getHeader("Authorization")));
        }catch (Exception e){
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to Fetch data by  "+buildingMgmtId+":  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getMessage());
        }
        return genericDataDTO;
    }




}
