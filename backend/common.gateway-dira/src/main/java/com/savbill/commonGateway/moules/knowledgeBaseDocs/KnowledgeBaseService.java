package com.savbill.commonGateway.moules.knowledgeBaseDocs;

import com.savbill.commonGateway.common.service.AbstractService;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.ClientServiceConstant;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.utillity.fileUtillity.FileUtility;
import com.savbill.commonGateway.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.spring.LoggedInUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class KnowledgeBaseService extends AbstractService<KnowledgeBaseDocDTO, KnowledgeBaseDocuments, Long> {

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private KnowledgeBaseDocRepo knowledgeBaseDocRepo;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private LoggedInUserService loggedInUserService;

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseService.class);

    @Override
    protected JpaRepository<KnowledgeBaseDocDTO, Long> getRepository() {
        return null;
    }

    public void uploadDocument(String knowledgeBaseDTO, MultipartFile[] files) throws IOException {
        String path = clientServiceSrv
                .getByNameAndMvnoId(ClientServiceConstant.KNOWLEDGEBASE_DOC_PATH, getMvnoIdFromCurrentStaff()).getValue();
        KnowledgeBaseDocDTO dtoObj = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(knowledgeBaseDTO, new TypeReference<KnowledgeBaseDocDTO>() {});
        boolean exists = knowledgeBaseDocRepo.existsByEventNameAndDocumentForAndDocTypeAndMvnoId(
                dtoObj.getEventName().trim(),
                dtoObj.getDocumentFor().trim(),
                dtoObj.getDocType().trim(),
                getMvnoIdFromCurrentStaff().longValue());

        if (exists) {
            throw new CustomValidationException(417,"A Knowledge Base document with the same Event Name, Category, and Document Type already exists.",null);
        }
        try {
            String subFolderName = File.separator + dtoObj.getEventName().trim() + File.separator + dtoObj.getDocType().trim() + File.separator;
            String fullPath = path + subFolderName;
            logger.info(":::::File Path Generated for Upload Document in KnowledgeBase::::: " + fullPath);
            HashMap<String, StringBuilder> map = fileUtility.saveMultipleFiles(files, fullPath);
            StringBuilder uniqueNames = map.get("uniqueNames");
            StringBuilder fileNames = map.get("fileNames");
            dtoObj.setFilename(fileNames.toString());
            dtoObj.setUniqueName(uniqueNames.toString());
            KnowledgeBaseDocuments knowledgeBaseDocuments = dtoToDomain(dtoObj, new KnowledgeBaseDocuments());
            knowledgeBaseDocRepo.save(knowledgeBaseDocuments);
        } catch (Exception e) {
            logger.error(":::::::::Exception while Save KnowledgeBase Documents:::::::::::" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public KnowledgeBaseDocuments dtoToDomain(KnowledgeBaseDocDTO dtoObj, KnowledgeBaseDocuments knowledgeBaseDocuments) {
        knowledgeBaseDocuments.setEventName(dtoObj.getEventName());
        knowledgeBaseDocuments.setDocumentFor(dtoObj.getDocumentFor());
        knowledgeBaseDocuments.setDocType(dtoObj.getDocType());
        knowledgeBaseDocuments.setFilename(dtoObj.getFilename());
        knowledgeBaseDocuments.setUniqueName(dtoObj.getUniqueName());
        knowledgeBaseDocuments.setMvnoId(getMvnoIdFromCurrentStaff().longValue());
        knowledgeBaseDocuments.setRemarks(dtoObj.getRemarks());
        knowledgeBaseDocuments.setCreatedById(getLoggedInUserId());
        knowledgeBaseDocuments.setCreatedByName(getLoggedInUser().getFullName());
        knowledgeBaseDocuments.setCreatedate(LocalDateTime.now());
        knowledgeBaseDocuments.setLastModifiedById(getLoggedInUserId());
        knowledgeBaseDocuments.setLastModifiedByName(getLoggedInUser().getFullName());
        knowledgeBaseDocuments.setUpdatedate(LocalDateTime.now());
        return knowledgeBaseDocuments;
    }

    public void updateDocument(Long id, String knowledgeBaseDTO, MultipartFile[] files) throws IOException {
        KnowledgeBaseDocuments knowledgeBaseDocuments = new KnowledgeBaseDocuments();
        try {
            KnowledgeBaseDocuments existingObj = knowledgeBaseDocRepo.findById(id).orElseThrow(() -> new RuntimeException("Document Not Found for Id: " + id));
            String path = clientServiceSrv
                    .getByNameAndMvnoId(ClientServiceConstant.KNOWLEDGEBASE_DOC_PATH, getMvnoIdFromCurrentStaff()).getValue();
            // :::::::::::::::::::conversion of JsonString into DTO object::::::::::::::::::::::::::
            KnowledgeBaseDocDTO dtoObj = new ObjectMapper().registerModule(new JavaTimeModule())
                    .readValue(knowledgeBaseDTO, new TypeReference<KnowledgeBaseDocDTO>() {
                    });
            String existingFilenames = dtoObj.getFilename() != null ? dtoObj.getFilename() : "";
            String existingUniqueNames = existingObj.getUniqueName() != null ? existingObj.getUniqueName() : "";
            String subFolderName = File.separator + dtoObj.getEventName().trim() + File.separator + dtoObj.getDocType().trim() + File.separator;
            String fullPath = path + subFolderName;
            logger.info(":::::File Path Generated for Upload Document in KnowledgeBase::::: " + fullPath);
            HashMap<String,StringBuilder> map  = fileUtility.saveMultipleFiles(files, fullPath);
            StringBuilder uniqueNames = map.get("uniqueNames");
            StringBuilder fileNames = map.get("fileNames");
            if (!existingFilenames.isEmpty() && fileNames.length() > 0) {
                fileNames.insert(0, existingFilenames + ",");
            } else if (fileNames.length() == 0 && !existingFilenames.isEmpty()) {
                fileNames.append(existingFilenames);
            }
            if(!existingUniqueNames.isEmpty() && fileNames.length() > 0){
                uniqueNames.insert(0,existingUniqueNames+",");
            }

//            if (!existingUniqueNames.isEmpty() && uniqueNames.length() > 0) {
//                uniqueNames.insert(0, existingUniqueNames + ",");
//            } else if (uniqueNames.length() == 0 && !existingUniqueNames.isEmpty()) {
//                uniqueNames.append(existingUniqueNames);
//            }
            dtoObj.setFilename(fileNames.toString());
            dtoObj.setUniqueName(uniqueNames.toString());
            knowledgeBaseDocuments = dtoToDomain(dtoObj, existingObj);
            knowledgeBaseDocuments.setCreatedById(existingObj.getCreatedById());
            knowledgeBaseDocuments.setCreatedByName(existingObj.getCreatedByName());
            knowledgeBaseDocuments.setCreatedate(existingObj.getCreatedate());
            knowledgeBaseDocRepo.save(knowledgeBaseDocuments);
        } catch (Exception e) {
            logger.error(":::::::::Exception while Update KnowledgeBase Documents:::::::::::" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public KnowledgeBaseDocuments getAllById(Long id) {
        KnowledgeBaseDocuments knowledgeBaseDocuments = knowledgeBaseDocRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Document not found for id :" + id));
        // Split, reverse, and join back
        knowledgeBaseDocuments.setFilename(reverseAndJoin(knowledgeBaseDocuments.getFilename()));
        knowledgeBaseDocuments.setUniqueName(reverseAndJoin(knowledgeBaseDocuments.getUniqueName()));
        return knowledgeBaseDocuments;
    }

    public Resource getDocumentForUniqueName(Long id, String uniqueName) {
        Resource resource = null;
        String PATH = clientServiceSrv
                .getByNameAndMvnoId(ClientServiceConstant.KNOWLEDGEBASE_DOC_PATH, getMvnoIdFromCurrentStaff()).getValue();

        try {
            KnowledgeBaseDocuments knowledgeBaseDocuments = knowledgeBaseDocRepo.findDocumentTypeById(id);
            String subFolderName = File.separator + knowledgeBaseDocuments.getEventName() + File.separator + knowledgeBaseDocuments.getDocType() + File.separator;
            Path basePath = Paths.get(PATH + subFolderName);
            Path filePath = basePath.resolve(uniqueName).normalize();
            resource = new UrlResource(filePath.toUri());
            return resource;

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
            logger.error("Error while get KnowledgeBase Doc : " + ex.getMessage() + " for File : " + uniqueName);
        }
        return resource;
    }


    public void deleteKnowledgeBaseDocument(Long id) throws Exception {
        try {
            String PATH = clientServiceSrv
                    .getByNameAndMvnoId(ClientServiceConstant.KNOWLEDGEBASE_DOC_PATH, getMvnoIdFromCurrentStaff()).getValue();
            KnowledgeBaseDocuments knowledgeBaseDocuments = knowledgeBaseDocRepo.findById(id).orElse(null);
            String subFolderName = File.separator + knowledgeBaseDocuments.getEventName() + File.separator + knowledgeBaseDocuments.getDocType() + File.separator;
            Path basePath = Paths.get(PATH + subFolderName);
            File file = basePath.toFile();
            if (knowledgeBaseDocuments != null) {
                knowledgeBaseDocRepo.delete(knowledgeBaseDocuments);
                fileUtility.deleteDirectory(file);
            }
        } catch (Exception ex) {
            logger.error("::::::::::Error while deleteKnowledgeBaseDocument:::::::::::::::" + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Page<KnowledgeBaseDocuments> getAllDocsWithPagination(PaginationRequestDTO paginationRequestDTO) {
        PageRequest pageRequest = PageRequest.of(paginationRequestDTO.getPage() - 1, paginationRequestDTO.getPageSize(), Sort.by("id").descending());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<KnowledgeBaseDocuments> knowledgeBaseDocuments = search(paginationRequestDTO.getFilters(), pageRequest);
            if (Objects.isNull(knowledgeBaseDocuments) || knowledgeBaseDocuments == null) {
                logger.error("KnowledgeBaseDocs Details fetch failed — reason: No record found ");
            }
            return knowledgeBaseDocuments;
        } catch (Exception e) {
            logger.error("KnowledgeBaseDocs fetch failed: {}", e.getMessage(), e);
            throw new RuntimeException("Document fetch failed", e);
        }
    }

    public GenericDataDTO getKnowledgeBaseDocFordelete(Long id, String uniqueName, String fileName) {
        String PATH = clientServiceSrv
                .getByNameAndMvnoId(ClientServiceConstant.KNOWLEDGEBASE_DOC_PATH, getMvnoIdFromCurrentStaff()).getValue();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        KnowledgeBaseDocuments knowledgeBaseDocuments = getAllById(id);
        if (Objects.isNull(knowledgeBaseDocuments)) {
            throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "KnowledgeBaseDoc Not found for given id", null);
        }
        try {
            String subFolderName = File.separator + knowledgeBaseDocuments.getEventName() + File.separator + knowledgeBaseDocuments.getDocType() + File.separator;
            Path basePath = Paths.get(PATH + subFolderName);
            Path filePath = basePath.resolve(uniqueName).normalize();
            File file = filePath.toFile();
            if (!file.exists()) {
                logger.error("File not found: {} for KnowledgeBaseDocId: {}" + uniqueName + id);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("File not found.");
                return genericDataDTO;
            } else {
                String existingFileNameList = knowledgeBaseDocuments.getFilename();
                String existingUniqueNameList = knowledgeBaseDocuments.getUniqueName();
                List<String> existingFilenames = new ArrayList<>(Arrays.asList(existingFileNameList.split("\\s*,\\s*")));
                List<String> existingUniqueNames = new ArrayList<>(Arrays.asList(existingUniqueNameList.split("\\s*,\\s*")));
                existingFilenames.removeIf(oldfileName -> oldfileName.equals(fileName));
                existingUniqueNames.removeIf(olduniqueName -> olduniqueName.equals(uniqueName));
                String filenamesString = String.join(",", existingFilenames);
                String uniquenameString = String.join(",", existingUniqueNames);
                knowledgeBaseDocuments.setFilename(filenamesString);
                knowledgeBaseDocuments.setUniqueName(uniquenameString);
                knowledgeBaseDocRepo.save(knowledgeBaseDocuments);
                file.delete();
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("File deleted successfully.");
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to delete file.");
            String errorMessage = "Error while delete knowledgeBase doc : " + ex.getMessage();
            logger.error(errorMessage, ex);
            throw new RuntimeException();
        }
        return genericDataDTO;
    }


    public Page<KnowledgeBaseDocuments> search(List<GenericSearchModel> filterList, PageRequest pageRequest) {
        try {
            if (filterList != null && !filterList.isEmpty()) {
                for (GenericSearchModel searchModel : filterList) {
                    // Assuming 'ANY' is a special condition
                    if(SearchConstants.DOC_FOR.equalsIgnoreCase(searchModel.getFilterColumn().trim())) {
                        return getKnowledgeBaseByType(searchModel.getFilterValue(),pageRequest);
                    }
                    else if (SearchConstants.ANY.equalsIgnoreCase(searchModel.getFilterColumn().trim())) {
                        return getKnowledgeBase(searchModel.getFilterValue(), pageRequest);
                    }else {
                        return getFetchOnlyCustomers(searchModel.getFilterValue(),pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Search operation failed: {}", ex.getMessage(), ex);
        }
//        return knowledgeBaseDocRepo.findAll(pageRequest);
        return null;
    }
    public Page<KnowledgeBaseDocuments> getKnowledgeBase(String filterValue, PageRequest pageRequest) {
        QKnowledgeBaseDocuments qKnowledgeBaseDocuments = QKnowledgeBaseDocuments.knowledgeBaseDocuments;
        BooleanExpression booleanExpression = qKnowledgeBaseDocuments.isNotNull();
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        // Check if the filter value is not empty
        if (filterValue != null && !filterValue.trim().isEmpty()) {
            // Assuming filterValue is used to match the title or description of the documents
            booleanExpression = booleanExpression.and(qKnowledgeBaseDocuments.eventName.containsIgnoreCase(filterValue)
                    .or(qKnowledgeBaseDocuments.documentFor.containsIgnoreCase(filterValue)
                    .or(qKnowledgeBaseDocuments.docType.containsIgnoreCase(filterValue))));
        }

        if (mvnoId == 1) {
            return knowledgeBaseDocRepo.findAll(booleanExpression,pageRequest);
        } else {
            booleanExpression = booleanExpression.and(qKnowledgeBaseDocuments.mvnoId.in(mvnoId, 1));
            return knowledgeBaseDocRepo.findAll(booleanExpression, pageRequest);
        }
    }


    public Page<KnowledgeBaseDocuments> getFetchOnlyCustomers(String filterValue, PageRequest pageRequest) {
        QKnowledgeBaseDocuments qKnowledgeBaseDocuments = QKnowledgeBaseDocuments.knowledgeBaseDocuments;
        BooleanExpression booleanExpression = qKnowledgeBaseDocuments.isNotNull();
        BooleanExpression expression = qKnowledgeBaseDocuments.isNotNull();
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        // Check if the filter value is not empty
        if (filterValue != null && !filterValue.trim().isEmpty()) {
            // Assuming filterValue is used to match the title or description of the documents
            booleanExpression = booleanExpression.and(qKnowledgeBaseDocuments.documentFor.equalsIgnoreCase(SearchConstants.CUSTOMER)
                    .and(qKnowledgeBaseDocuments.eventName.containsIgnoreCase(filterValue)
                            .or(qKnowledgeBaseDocuments.docType.containsIgnoreCase(filterValue))));
        }
        if(filterValue.equalsIgnoreCase(SearchConstants.STAFF)){
            return null;
        }

        if (mvnoId == 1) {
            return knowledgeBaseDocRepo.findAll(booleanExpression,pageRequest);
        } else {
            expression = expression
                    .and(qKnowledgeBaseDocuments.mvnoId.in(mvnoId, 1)).and(qKnowledgeBaseDocuments.documentFor.equalsIgnoreCase(SearchConstants.CUSTOMER)
                    .and(qKnowledgeBaseDocuments.eventName.containsIgnoreCase(filterValue)
                            .or(qKnowledgeBaseDocuments.docType.containsIgnoreCase(filterValue))));
            return knowledgeBaseDocRepo.findAll(expression, pageRequest);
        }
    }
    private String reverseAndJoin(String input) {
        List<String> list = Arrays.asList(input.split(","));
        Collections.reverse(list);
        return String.join(",", list);
    }

    public Page<KnowledgeBaseDocuments> getKnowledgeBaseByType(String filterValue, PageRequest pageRequest) {
        QKnowledgeBaseDocuments qKnowledgeBaseDocuments = QKnowledgeBaseDocuments.knowledgeBaseDocuments;
        BooleanExpression booleanExpression = qKnowledgeBaseDocuments.isNotNull();
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        // Check if the filter value is not empty
        if (filterValue != null && !filterValue.trim().isEmpty()) {
            // Assuming filterValue is used to match the title or description of the documents
            booleanExpression =
                    booleanExpression.and(qKnowledgeBaseDocuments.documentFor.containsIgnoreCase(filterValue));
        }

        if (mvnoId == 1) {
            return knowledgeBaseDocRepo.findAll(booleanExpression,pageRequest);
        } else {
            booleanExpression = booleanExpression.and(qKnowledgeBaseDocuments.mvnoId.in(mvnoId, 1));
            return knowledgeBaseDocRepo.findAll(booleanExpression, pageRequest);
        }
    }
}