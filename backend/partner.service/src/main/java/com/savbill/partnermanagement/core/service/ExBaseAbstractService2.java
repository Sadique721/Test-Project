package com.savbill.partnermanagement.core.service;

import com.savbill.partnermanagement.constants.APIConstants;
import com.savbill.partnermanagement.constants.ClientServiceConstant;
import com.savbill.partnermanagement.core.constants.CommonConstants;
import com.savbill.partnermanagement.core.constants.Constants;
import com.savbill.partnermanagement.core.data.IBaseData2;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.GenericSearchModel;
import com.savbill.partnermanagement.core.dto.IBaseDto2;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.exceptions.DataNotFoundException;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import com.savbill.partnermanagement.utils.CommonUtils;
import com.itextpdf.text.Document;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.savbill.partnermanagement.core.utillity.log.ApplicationLogger.logger;

@Slf4j
public abstract class ExBaseAbstractService2<DTO extends IBaseDto2, DATA extends IBaseData2, ID> implements ExBaseService<DTO, ID> {

    @Autowired
    ClientServiceSrv clientServiceSrv;

    private final JpaRepository<DATA, ID> repository;
    private final IBaseMapper<DTO, DATA> mapper;

    public Integer MAX_PAGE_SIZE;

    public Map<String, String> sortColMap = new HashMap<>();

    public PageRequest pageRequest = null;

    public ExBaseAbstractService2(JpaRepository<DATA, ID> repository, IBaseMapper<DTO, DATA> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<DTO> getAllEntities() throws Exception {
        try {
            return repository.findAll().stream().filter(data -> !data.getDeleteFlag()).map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(dto -> (dto.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || dto.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue()) && (dto.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(dto.getBuId()))).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public DTO getEntityById(ID id) throws Exception {
        try {
            DATA domain = (null == repository.findById(id)) ? null : repository.findById(id).get();
            if (null == domain || domain.getDeleteFlag()) {
                logger.error(getModuleNameForLog() + "--" + "Data not found for id " + id);
                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
            }
            DTO dto = mapper.domainToDTO(repository.findById(id).get(), new CycleAvoidingMappingContext());
            if (dto != null && ((getMvnoIdFromCurrentStaff() == 1 || (dto.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || dto.getMvnoId() == 1)) && (dto.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(dto.getBuId()))))
                return dto;
            return null;
            /*if(null == dto){

            }*/
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                logger.error("{} -- Error while retrieving entity by id [{}]: {}", getModuleNameForLog(), id, ex.getMessage(), ex);
                throw new DataNotFoundException();
            }
            logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public DTO getEntityById(ID id, boolean considerDeleteFlag) throws Exception {
        try {
            DATA domain = (null == repository.findById(id)) ? null : repository.findById(id).get();
            if (null == domain || (domain.getDeleteFlag() && considerDeleteFlag)) {
                logger.error(getModuleNameForLog() + "--" + "Data not found for id " + id);
                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
            }
            DTO dto = mapper.domainToDTO(repository.findById(id).get(), new CycleAvoidingMappingContext());
            if (dto == null || (!(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == dto.getMvnoId().intValue()) && (dto.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(dto.getBuId()))))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            return dto;
            /*if(null == dto){

            }*/
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                logger.error("{} -- Error while retrieving entity by id [{}]: {}", getModuleNameForLog(), id, ex.getMessage(), ex);
                throw new DataNotFoundException();
            }
            logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public DTO getEntityForUpdateAndDelete(ID id) throws Exception {
        DTO dto = getEntityById(id);
        logger.info(getModuleNameForLog() + "--" + "Retrieved Entity. Data[" + dto.toString() + "]");
        if (dto == null || (!(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == dto.getMvnoId().intValue()) && (dto.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(dto.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return dto;
    }

    @Override
    public DTO saveEntity(DTO entity) throws Exception {
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
            throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        logger.info(getModuleNameForLog() + "--" + "saving Entity. Data[" + entity.toString() + "]");
        DATA entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1)
            entityDomain.setBuId(getBUIdsFromCurrentStaff().get(0));
        logger.info(getModuleNameForLog() + "--" + "saving Entity. Data[" + entityDomain.toString() + "]");
        try {
            logger.info(getModuleNameForLog() + "--" + "saving Entity. Data[" + entityDomain.toString() + "]");
            return mapper.domainToDTO(repository.save(entityDomain), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while saving Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public DTO updateEntity(DTO entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        DATA entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        logger.info(getModuleNameForLog() + "--" + "updating Entity. Data[" + entityDomain.toString() + "]");
        DTO oldObj = getEntityById((ID) entity.getIdentityKey());
        logger.info(getModuleNameForLog() + "--" + "updating Entity. Data[" + entityDomain.toString() + "]");
        String updatedValues = CommonUtils.getUpdatedDiff(oldObj, entity);
        logger.info("Updating data " + updatedValues + " is successfull :  request: { From : {}}; Response : {{}}", getModuleNameForLog());
        //     ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "updating Entity. Data[" + entityDomain.toString() + "]");
        try {
            if (entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            logger.info(getModuleNameForLog() + "--" + "updating Entity. Data[" + entityDomain.toString() + "]");
            return mapper.domainToDTO(repository.save(entityDomain), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while saving Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteEntity(DTO entity) throws Exception {
        DATA entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
        try {
            if (entityDomain.getDeleteFlag()) {
                logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
                throw new DataNotFoundException();
            }
            if (entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            entityDomain.setDeleteFlag(true);
            repository.save(entityDomain);
            logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
        } catch (Exception ex) {
            logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        return false;
    }

    public JpaRepository<DATA, ID> getRepository() {
        return repository;
    }

    public IBaseMapper<DTO, DATA> getMapper() {
        return mapper;
    }

    public abstract String getModuleNameForLog();

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        return null;
    }

    //Pagination
    public GenericDataDTO getListByPagination(PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        logger.info(getModuleNameForLog() + "--" + "getListByPagination()");
        Page<DATA> paginationList = getRepository().findAll(pageRequest);
        logger.info(getModuleNameForLog() + "--" + "getListByPagination()");
        if (null != paginationList && 0 < paginationList.getSize()) {
            makeGenericResponse(genericDataDTO, paginationList);
            logger.info(getModuleNameForLog() + "--" + "getListByPagination()");
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        try {
            logger.info(SUBMODULE + "--" + "getListByPageAndSizeAndSortByAndOrderBy()");
            return getListByPagination(generatePageRequest(page, size, sortBy, sortOrder));
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<DATA> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public void createExcel(Workbook workbook, Sheet sheet, Class clazz, Field[] fields) throws Exception {
        //new ExcelUtil<DTO>().generateExcel(workbook, sheet, clazz, getAllEntities(), fields);
    }

    public void createCell(Row row, int columnCount, Object value, CellStyle style, Sheet sheet) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else {
            cell.setCellValue(value + "");
        }
        cell.setCellStyle(style);
    }

    public void excelGenerate(Workbook workbook) throws Exception {
    }

    public void pdfGenerate(Document doc) throws Exception {
    }

    public void createPDF(Document doc, Class clazz, Field[] fields) throws Exception {
        //new PdfUtil<DTO>().generatePdf(doc, clazz, getAllEntities(), fields);
    }

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;
        logger.info(getModuleNameForLog() + "--" + "generatePageRequest()");
        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        logger.info(getModuleNameForLog() + "--" + "generatePageRequest()");
        return pageRequest;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
                logger.info(getModuleNameForLog() + "--" + "getLoggedInUserId()");
            }
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + "--" + e.getMessage(), e);
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
                logger.info(getModuleNameForLog() + "--" + "getLoggedInUserPartnerId()");
            }
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + "--" + e.getMessage(), e);
            partnerId = -1;
        }
        return partnerId;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            logger.info(getModuleNameForLog() + "--" + "getLoggedInUser()");
            }
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + "--" + e.getMessage(), e);
            user = null;
        }
        return user;
    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            logger.info(getModuleNameForLog() + "--" + "getMvnoIdFromCurrentStaff()");
            }
        } catch (Exception e) {
            logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    //Verify Duplicate

    public boolean duplicateVerifyAtSave(String name) throws Exception {
        return false;
    }

    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        return false;
    }

    public List<Long> getBUIdsFromCurrentStaff() {
        List<Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            logger.info(getModuleNameForLog() + "--" + "getBUIdsFromCurrentStaff()");
            }
        } catch (Exception e) {
            logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }
}
