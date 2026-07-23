package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.controller;


import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.LogConstants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocumentTAXRelRepository;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.BatchPaymentMappingPojo;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.BatchPaymentPojo;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.service.BatchPaymentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PAYMENT_GATEWAY)
public class PaymentGatewayController  {
    private static String MODULE = " [PaymentGatewayController] ";

//    @Autowired
//    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private BatchPaymentService batchPaymentService;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;


    public String getModuleNameForLog() {
        return " [PaymentGatewayController] ";
    }
    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayController.class);


    @PostMapping(value = "/record/bulkpayment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRecordPayment(@Valid @RequestParam String recordpaymentDtos, @RequestParam(value = "file", required = false)MultipartFile file , @RequestParam(value = "batchname" , required = false) String batchname
    ) throws Exception {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Gson gson = new Gson();
        List<RecordPaymentPojo> recordPaymentPojoList =  new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        recordPaymentPojoList = mapper.registerModule(new JavaTimeModule())
                .readValue(recordpaymentDtos, new TypeReference<List<RecordPaymentPojo>>(){});
         Double totalAmount =0.0;
        BatchPaymentPojo batchPaymentPojo = new BatchPaymentPojo();
        batchPaymentPojo.setBatchName(batchname);
        List<BatchPaymentMappingPojo> batchPaymentMappingPojoList = new ArrayList<>();
         try {
            for(RecordPaymentPojo pojo : recordPaymentPojoList) {
                CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
                if (file != null) {
                    creditDocService.uploadDocument(pojo, file);
                }
                if(pojo.getPaytype() == null){
                    pojo.setPaytype("invoice");
                }
                 CreditDocument document=creditDocService.save(pojo, false, false, false,null,null,null,false,null,null);
                totalAmount +=pojo.getAmount();
                BatchPaymentMappingPojo batchPaymentMappingPojo = new BatchPaymentMappingPojo();
                if (document != null) {
                    batchPaymentMappingPojo.setCredit_doc_id(document.getId().longValue());
                }
                batchPaymentMappingPojoList.add(batchPaymentMappingPojo);
            }
            batchPaymentPojo.setBatchPaymentMappingList(batchPaymentMappingPojoList);
            batchPaymentPojo.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
             boolean status = batchPaymentService.isPaymentBatchAlreadyExists(batchname);
             if(!status){
                 batchPaymentService.save(batchPaymentPojo);
             }
             else{
                 throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value() , "Batch with this name already exist" , null);
             }

            //	workflowAuditService.workFlowAuditPayment(pojo, getLoggedInUserId());
            response.put("totalamount", totalAmount);
            RESP_CODE = APIConstants.SUCCESS;
              logger.info("bulk record payment  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to createRecordPayment  for " + 23 + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to createRecordPayment  for " + 23 + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        MDC.remove("type");
        return batchPaymentService.apiResponse(RESP_CODE, response);
    }



}
