package com.savbill.cpm.modules;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.CreditDocument;
import com.savbill.cpm.repository.postpaid.CreditDocRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.service.common.FileSystemService;
import com.savbill.cpm.service.postpaid.CreditDocService;
import com.savbill.cpm.spring.SpringContext;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.NumberSequenceUtil;
import com.savbill.cpm.rabbitMq.message.CreditDocMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/cpm")
public class CreditDocController {

    private static String MODULE = " [CreditDocController] ";

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    private static final Logger logger = LoggerFactory.getLogger(CreditDocController.class);

    @RequestMapping(value = "/documentForInvoice/download/{docId}/{custId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Optional<Customers> customers = customersRepository.findById(custId);
            if (null == customers.get()) {
                return ResponseEntity.notFound().build();
            }
            Optional<CreditDocument> creditDocument = creditDocRepository.findById(docId.intValue());
            if (null == creditDocument) {
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = SpringContext.getBean(FileSystemService.class);
            resource = service.getBarterDoc(customers.get().getUsername().trim(), creditDocument.get().getUniquename());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Downloading document with  "+docId+" downloaded Successfully  :  request: { From : {} }; Response : {{}}",SUBMODULE, APIConstants.SUCCESS);
                System.out.println("dowload document");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                  logger.error("Unable to downloadDocument "+docId+" :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND,ResponseEntity.notFound());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to downloadDocument "+docId+"   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}", SUBMODULE,HttpStatus.NOT_FOUND,ResponseEntity.notFound(),ex.getStackTrace());
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }

//    @PostMapping(value = "/getWithdrawPayments/{customerId}")
//    public GenericDataDTO voidInvoice(@PathVariable(name = "customerId") Integer customerId, @RequestBody PaginationRequestDTO paginationRequestDTO) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            logger.info("Fetching All payments for customer id " + customerId + "  : Response : {{}}", genericDataDTO.getResponseCode());
//            genericDataDTO.setDataList(creditDocService.getWithdrawPayments(customerId,paginationRequestDTO));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successfully");
//        } catch (CustomValidationException ce) {
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable fetching all payments for customer id " + customerId +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getMessage());
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable fetching all payments for customer id " + customerId +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    @PostMapping(value = "/getChequeDetail/{id}")
    public GenericDataDTO getChequePaymode(@PathVariable(name = "id") Integer id, @RequestBody PaginationRequestDTO paginationRequestDTO) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info("Fetching All payments for customer id " + id + "  : Response : {{}}", genericDataDTO.getResponseCode());
            genericDataDTO.setDataList(Collections.singletonList(creditDocService.getChequeDetails(id)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successfully");
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable fetching all payments for customer id " + id +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getMessage());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable fetching all payments for customer id " + id +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }


//    @GetMapping("/getChequeDetail/{id}")
//    public ResponseEntity<?> getChequePaymode(@PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        //String SUBMODULE = MODULE + " [getChequekPaymode()] ";
//        MDC.put("type", "fetch");
//        //Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//
//            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
//            ChequeDetailsPojo chequeDetailsSavePojoList = creditDocService.getChequeDetails(id);
//            response.put("invoiceList", chequeDetailsSavePojoList);
//            RESP_CODE = APIConstants.SUCCESS;
//            //logger.info("Fetching invoice by customer " + customersService.get(customerid).getUsername() + "   :  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Unable to Fetch Invoice ByCustomer  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
//            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Fetch Invoice ByCustomer :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, e.getStackTrace());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response);
//    }

    @RequestMapping(value = "/documentForPayment/download/{docId}/{custId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadPaymentDocument(@PathVariable Long docId, @PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Optional<Customers> customers = customersRepository.findById(custId);
            if (null == customers.get()) {
                return ResponseEntity.notFound().build();
            }
            Optional<CreditDocument> creditDocument = creditDocRepository.findById(docId.intValue());
            if (null == creditDocument) {
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = SpringContext.getBean(FileSystemService.class);
            resource = service.getBarterDoc(customers.get().getUsername().trim(), creditDocument.get().getFilename());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Downloading document with  "+docId+" downloaded Successfully  :  request: { From : {} }; Response : {{}}",SUBMODULE, APIConstants.SUCCESS);
                System.out.println("dowload document");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                logger.error("Unable to downloadDocument "+docId+" :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND,ResponseEntity.notFound());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to downloadDocument "+docId+"   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}", SUBMODULE,HttpStatus.NOT_FOUND,ResponseEntity.notFound(),ex.getStackTrace());
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }

//    @PostMapping("/addCreditDoc")
//    public Integer addCreditDoc(@RequestBody CreditDocMessage creditDocMessage) {
//        logger.info("Inside addCreditDoc for customer: {}", creditDocMessage.getCustomer());
//        try {
//            Customers customers = customersRepository.findById(creditDocMessage.getCustomer()).orElse(null);
//            if (customers == null) {
//                logger.warn("Customer not found with id: {} in CPM, continuing with dummy/null reference", creditDocMessage.getCustomer());
//            }
//
//            if (creditDocMessage.getBuID() != null && creditDocMessage.getBuID() == 0L) {
//                creditDocMessage.setBuID(null);
//            }
//            if (creditDocMessage.getDestinationBank() != null && creditDocMessage.getDestinationBank() == 0L) {
//                creditDocMessage.setDestinationBank(null);
//            }
//            if (creditDocMessage.getXmldocument() == null || creditDocMessage.getXmldocument().isEmpty()) {
//                creditDocMessage.setXmldocument("");
//            }
//            if (creditDocMessage.getApproverid() == null) {
//                creditDocMessage.setApproverid(0);
//            }
//
//            CreditDocument creditDocument = new CreditDocument(creditDocMessage, customers);
//            // if (customers == null) {
//            //     creditDocument.setCustId(creditDocMessage.getCustomer());
//            // }
//
//            if (creditDocument.getId() == null) {
//                Integer lastId = creditDocRepository.findlast();
//                Integer nextId = (lastId != null ? lastId : 0) + 1;
//                creditDocument.setId(nextId);
//            }
//
//            if (creditDocument.getCreditdocumentno() == null || creditDocument.getCreditdocumentno().trim().isEmpty()) {
//                String creditDocNo = null;
//                try {
//                    boolean isLCO = customers != null && customers.getLcoId() != null && customers.getLcoId() > 0;
//                    Integer lcoId = customers != null ? customers.getLcoId() : null;
//                    Integer mvnoId = customers != null ? customers.getMvnoId() : null;
//                    creditDocNo = numberSequenceUtil.getPaymentNumber(isLCO, lcoId, mvnoId);
//                } catch (Exception e) {
//                    logger.error("Error generating database payment sequence: {}", e.getMessage());
//                }
//
//                if (creditDocNo == null) {
//                    java.time.LocalDate current_date = java.time.LocalDate.now();
//                    int current_Year = current_date.getYear();
//                    String randVal = String.valueOf(System.currentTimeMillis() % 100000000L);
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("PY");
//                    sb.append(current_Year);
//                    sb.append("-");
//                    while (sb.length() < 15 - randVal.length()) {
//                        sb.append('0');
//                    }
//                    sb.append(randVal);
//                    creditDocNo = sb.toString();
//                }
//                creditDocument.setCreditdocumentno(creditDocNo);
//            }
//
//            CreditDocument savedDoc = creditDocRepository.save(creditDocument);
//
//            try {
//                creditDocService.addLedgeAfterApproval(savedDoc);
//            } catch (Exception ex) {
//                logger.error("Failed to add ledger after approval for CreditDoc: {}", savedDoc.getId(), ex);
//            }
//
//            logger.info("CreditDoc saved successfully with id: {}, creditdocumentno: {}", savedDoc.getId(), savedDoc.getCreditdocumentno());
//            return savedDoc.getId();
//        } catch (Exception e) {
//            logger.error("Error in addCreditDoc: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to save CreditDoc in CPM", e);
//        }
//    }

}
