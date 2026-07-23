package com.savbill.commonGateway.moules.knowledgeBaseDocs;

import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.controller.APIResponseController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.exceptions.CustomValidationException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@Api(value = "KnowledgeBaseController")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.KNOWLEDGE_BASE_DOC)
public class KnowledgeBaseDocController extends APIResponseController {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseDocController.class);

    @Autowired
    KnowledgeBaseService knowledgeBaseService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(@RequestParam(value = "knowledgeBaseDTO") String knowledgeBaseDTO, @RequestParam(value = "file", required = false) MultipartFile[] file) {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        try {
            if ((knowledgeBaseDTO.isEmpty() && Objects.isNull(knowledgeBaseDTO))) {
                RESP_CODE = APIConstants.NO_CONTENT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Request Dto or File for Upload Document is Empty.");
                logger.error(":::::::::::::::Request Dto for Upload Document is Empty::::::::::::::");
                return apiResponse(RESP_CODE, response);
            }
            knowledgeBaseService.uploadDocument(knowledgeBaseDTO, file);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("message", "Document Uploaded For KnowledgeBase Submitted Successfully.");
            logger.info(":::::::::::::::::::::Document Uploaded For KnowledgeBase Submitted Successfully:::::::::::::::::::::");
        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Something went Wromg while Upload document : {}", ce.getMessage());
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Exception While Upload Document For KnowledgeBase:{} ", e.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestParam(value = "knowledgeBaseDTO") String knowledgeBaseDTO, @RequestParam(value = "file", required = false) MultipartFile[] file) {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        try {
            if ((knowledgeBaseDTO.isEmpty() && Objects.isNull(knowledgeBaseDTO))) {
                RESP_CODE = APIConstants.NO_CONTENT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Request Dto or File for Updated the Document is Empty.");
                logger.error(":::::::::::::::Request Dto for Updated Document is Empty::::::::::::::");
                return apiResponse(RESP_CODE, response);
            }
            knowledgeBaseService.updateDocument(id, knowledgeBaseDTO, file);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("message", "Updated Document For KnowledgeBase Submitted Successfully.");
            logger.info(":::::::::::::::::::::Updated Document For KnowledgeBase Submitted Successfully:::::::::::::::::::::");
        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Something went Wrong while Update the Document : {}", ce.getMessage());
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Exception While Update the Document For KnowledgeBase:{} ", e.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            knowledgeBaseService.deleteKnowledgeBaseDocument(id);
            response.put("message", "KnowledgeBase Document Successfully deleted.");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(":::::::::::KnowledgeBaseDocument Deleted Successfully:::::::::::::::");
        } catch (com.savbill.commonGateway.core.exceptions.CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put("message", Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(":::::::::::::::::::Something went wrong while deleting::::::::::::::::::::::::");
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, ex.getMessage());
                logger.error(":::::::::::::::Exception While Perform Deletion During KnowledgeBase Documents::::::::::" + ex.getMessage(), ex);
            } else {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                logger.error("::::::::::::::::::Error While Deleting KnowledgeBase Documents::::::::::::::" + ex.getMessage(), ex);
            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/getDocumentById/{id}")
    public GenericDataDTO getAreaIdFromSubAreaId(@PathVariable(required = true, value = "id") Long id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info(":::::::::::::Call Get getDocumentById For KnowledgeBase:::::::::::::::::::::");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setData(knowledgeBaseService.getAllById(id));
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            return genericDataDTO;
        }
    }


    @GetMapping(value = "/download/{id}/{uniqueName}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id, @PathVariable String uniqueName) {
        Resource resource = null;
        try {
            resource = knowledgeBaseService.getDocumentForUniqueName(id, uniqueName);
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Document Download Successfully for file: " + uniqueName);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                String errorMessage = "File not found for Id: " + id;
                logger.error(errorMessage);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).header("Error-Message", errorMessage).build();
            }
        } catch (Exception ex) {
            logger.error("Unable to downloadDocument For Id " + id);
            logger.error(ex.getMessage(), ex.getStackTrace(), ex);
        }
        return null;
    }

    @PostMapping("/search")
    public GenericDataDTO getAllDocsWithPagination(@RequestBody PaginationRequestDTO paginationRequestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (Objects.isNull(paginationRequestDTO)) {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("Fail To Fetch Details.");
                genericDataDTO.setTotalRecords(0);
                logger.error(":::::::::::::::KnowledgeBaseDocs Details fetch failed — reason: missing Payload for Pagination in the request.::::::::::::::");
                return genericDataDTO;
            }
            Page<KnowledgeBaseDocuments> knowledgeBaseDocuments = knowledgeBaseService.getAllDocsWithPagination(paginationRequestDTO);
            if(knowledgeBaseDocuments!=null) {
                List<KnowledgeBaseDocuments> knowledgeBaseDocumentsList = knowledgeBaseDocuments.getContent().stream().collect(Collectors.toList());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("KnowledgeBaseDoc Details fetch Successfully");
                genericDataDTO.setDataList(knowledgeBaseDocumentsList);
                genericDataDTO.setTotalRecords(knowledgeBaseDocuments.getTotalElements());
                logger.info(":::::::::::::::::::::KnowledgeBaseDoc Details fetch Successfully:::::::::::::::::::::");
            }else {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No records Found.");
                genericDataDTO.setDataList(null);
                genericDataDTO.setTotalRecords(0);
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Exception While Fetch Details For KnowledgeBaseDocs.");
            genericDataDTO.setTotalRecords(0);
            logger.error(e.getMessage());
        }
        return genericDataDTO;
    }

    @DeleteMapping(value = "/deleteSingleDoc/{id}")
    public GenericDataDTO deleteDocument(@PathVariable Long id, @RequestParam String uniqueName, @RequestParam String fileName) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = knowledgeBaseService.getKnowledgeBaseDocFordelete(id, uniqueName, fileName);
        } catch (Exception ex) {
            logger.error("Error occurred while deleting file for knowledgeBaseDocId: {}" + id);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("An error occurred while deleting the file.");
        }
        return genericDataDTO;
    }

}
