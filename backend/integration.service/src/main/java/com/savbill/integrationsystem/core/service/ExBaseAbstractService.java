package com.savbill.integrationsystem.core.service;

import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.data.IBaseData;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.exceptions.DataNotFoundException;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ExBaseAbstractService<DTO extends IBaseDto, DATA extends IBaseData, ID> implements ExBaseService<DTO, ID> {

//    @Autowired
//    ClientServiceSrv clientServiceSrv;
//
//    @Autowired
//    StaffUserService staffUserService;

    private final JpaRepository<DATA, ID> repository;
    private final IBaseMapper<DTO, DATA> mapper;

    public Integer MAX_PAGE_SIZE = 5;

    public Map<String, String> sortColMap = new HashMap<>();

    public PageRequest pageRequest = null;



    public ExBaseAbstractService(JpaRepository<DATA, ID> repository, IBaseMapper<DTO, DATA> mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<DTO> getAllEntities() throws Exception {
        try {
           return repository.findAll().stream().filter(data -> !data.getDeleteFlag()).map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//                    .stream().filter(dto -> dto.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || dto.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public DTO getEntityById(ID id,Long mvnoId) throws Exception {
        try {
            DATA domain = (null == repository.findById(id)) ? null : repository.findById(id).get();
            if (null == domain || domain.getDeleteFlag()) {
                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
            }
            DTO dto = mapper.domainToDTO(repository.findById(id).get(), new CycleAvoidingMappingContext());
            if (dto != null && (mvnoId == 1 || (Objects.equals(dto.getMvnoId(), mvnoId) || dto.getMvnoId() == 1))) {
                return dto;
            }
            return null;
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            //   ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    /*
     * If considerDeleteFlag will be false
     * * This will give result of all records (i.e,Deleted Also)

     * */
    @Override
    public DTO getEntityById(ID id, boolean considerDeleteFlag) throws Exception {
        try {
            DATA domain = (null == repository.findById(id)) ? null : repository.findById(id).get();
//            if (null == domain || (domain.getDeleteFlag() && considerDeleteFlag)) {
//                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
//            }
            DTO dto = mapper.domainToDTO(repository.findById(id).get(), new CycleAvoidingMappingContext());
//            if(dto == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == dto.getMvnoId().intValue()))
//                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            return dto;
            /*if(null == dto){

            }*/
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            //   ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public DTO getEntityForUpdateAndDelete(ID id,Long mvnoId) throws Exception {
        DTO dto = getEntityById(id,mvnoId);
        if(dto == null || !(mvnoId == 1 || mvnoId == dto.getMvnoId().intValue()))
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), CommonConstant.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return dto;
    }

    @Override
    public DTO saveEntity(DTO entity) throws Exception {
//        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        DATA entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());

        //      ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "saving Entity. Data[" + entityDomain.toString() + "]");
        try {
            return mapper.domainToDTO(repository.save(entityDomain), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            //         ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while saving Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public DTO updateEntity(DTO entity) throws Exception {
//        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        DATA entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());

        //       ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "updating Entity. Data[" + entityDomain.toString() + "]");
        try {
//            if(entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.getMvnoId().intValue()))
//                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            return mapper.domainToDTO(repository.save(entityDomain), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while saving Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteEntity(DTO entity) throws Exception {
        DATA entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
     //   ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
        try {
            if (entityDomain.getDeleteFlag()) {
                throw new DataNotFoundException();
            }
            entityDomain.setDeleteFlag(true);
            repository.save(entityDomain);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
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
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,HttpServletRequest request) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        return null;
    }

    //Pagination
    public GenericDataDTO getListByPagination(PageRequest pageRequest, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<DATA> paginationList = getRepository().findAll(pageRequest);
        if (null != paginationList && 0 < paginationList.getSize()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,HttpServletRequest request) {
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        try {
            return getListByPagination(generatePageRequest(page, size, sortBy, sortOrder),request);
        } catch (Exception ex) {
            //        ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
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

//    public void createExcel(Workbook workbook, Sheet sheet, Class clazz, Field[] fields) throws Exception {
//        new ExcelUtil<DTO>().generateExcel(workbook, sheet, clazz, getAllEntities(), fields);
//    }

//    public void createCell(Row row, int columnCount, Object value, CellStyle style, Sheet sheet) {
//        sheet.autoSizeColumn(columnCount);
//        Cell cell = row.createCell(columnCount);
//        if (value instanceof Integer) {
//            cell.setCellValue((Integer) value);
//        } else if (value instanceof Boolean) {
//            cell.setCellValue((Boolean) value);
//        } else if (value instanceof Long) {
//            cell.setCellValue((Long) value);
//        } else {
//            cell.setCellValue(value + "");
//        }
//        cell.setCellStyle(style);
//    }
//
//    public void excelGenerate(Workbook workbook) throws Exception {
//    }
//
//    public void pdfGenerate(Document doc) throws Exception {
//    }
//
//    public void createPDF(Document doc, Class clazz, Field[] fields) throws Exception {
//        new PdfUtil<DTO>().generatePdf(doc, clazz, getAllEntities(), fields);
//    }

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        if (pageSize < MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

//        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
        pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
//        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        return pageRequest;
    }

//    public int getLoggedInUserId() {
//        int loggedInUserId = -1;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
//            }
//        } catch (Exception e) {
//            loggedInUserId = -1;
//        }
//        return loggedInUserId;
//    }
//
//    public int getLoggedInUserPartnerId() {
//        int partnerId = -1;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
//            }
//        } catch (Exception e) {
//            partnerId = -1;
//        }
//        return partnerId;
//    }

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

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
    //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
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

//    public List<Long> getServiceAreaIdList() {
//        List<Long> idList = new ArrayList<>();
//
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                idList = staffUserService.get(getLoggedInUserId()).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList());
//                if(idList==null || idList.isEmpty()){
//                    idList = staffUserService.get(1).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList());
//                }
//                //idList.addAll(staffUserService.get(1).getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
//        }
//
//        return idList;
//    }
//
//    public List<Long> getBUIdsFromCurrentStaff() {
//        List<Long> mvnoIds = null;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
//        }
//        return mvnoIds;
//    }

    public Long getMvnoId(String encodedToken) throws IOException {
        String decodedToken = getDecoded(encodedToken);
        Long mavnoId = null;
        if (decodedToken != null) {
            JSONObject primaryObject = new JSONObject(decodedToken);
            JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
            mavnoId = mainObj.getLong("mvnoId");
        }
        return mavnoId;
    }

    public String getDecoded(String encodedToken) throws UnsupportedEncodingException {
        String[] pieces = encodedToken.split("\\.");
        String b64payload = pieces[1];
        return new String(Base64.decodeBase64(b64payload), "UTF-8");
    }


}
