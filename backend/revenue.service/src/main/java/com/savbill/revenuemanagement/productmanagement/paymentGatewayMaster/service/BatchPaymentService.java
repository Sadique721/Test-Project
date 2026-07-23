package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.service;

import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.PaginationDetails;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.entity.debitdoc.QDebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.ladger.QCreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.entity.staff.StaffUserService;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.domain.BankManagement;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.domain.QBankManagement;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.repository.BankManagementRepository;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.QServiceArea;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.*;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.*;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPayment;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentAssignment;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentDetails;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentMapping;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.*;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository.BatchPaymentAssignmentRepository;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository.BatchPaymentDetailsRepository;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository.BatchPaymentMappingRepository;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository.BatchPaymentRepository;

import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.CreditDocIdsMessages;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
//import com.querydsl.core.types.Predicate;
//import com.querydsl.core.types.dsl.BooleanExpression;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BatchPaymentService extends AbstractService<BatchPayment, BatchPaymentPojo, Long> {

    @Autowired
    private BatchPaymentRepository batchPaymentRepository;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private BatchPaymentMappingRepository batchPaymentMappingRepository;

    @Autowired
    private BatchPaymentMappingService batchPaymentMappingService;

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private BatchPaymentAssignmentService batchPaymentAssignmentService;

    @Autowired
    private BatchPaymentAssignmentRepository batchPaymentAssignmentRepository;

    @Autowired
    private BankManagementRepository bankManagementRepository;

    @Autowired
    private BatchPaymentDetailsRepository batchPaymentDetailsRepository;

//    @Autowired
//    private MessageSender messageSender;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    public static final String MODULE = "[BatchPaymentService]";

    @Override
    protected JpaRepository<BatchPayment, Long> getRepository() {
        return batchPaymentRepository;
    }


    public boolean isPaymentBatchAlreadyExists(String paymentBatchName) {
//        QBatchPayment batchPayment = QBatchPayment.batchPayment;
//        BooleanExpression expression = batchPayment.isNotNull().and(batchPayment.batchname.eq(paymentBatchName).and(batchPayment.isDeleted.eq(false)));
        Long count=batchPaymentRepository.countByBatchNameAndIsDeleted(paymentBatchName, false);
//        Long count = batchPaymentRepository.count(expression);
        return count > 0;
    }

    public void save(BatchPaymentPojo pojo) throws Exception {
        String SUBMODULE = MODULE + "[save()]";
        BatchPayment obj = convertBatchPaymentPojoToBatchPaymentModel(pojo);
        if (getBUIdsFromCurrentStaff().size() == 1)
//            obj.setBuId(getBUIdsFromCurrentStaff().get(0));
        obj = saveBatchPayment(obj);
        if (pojo.getAssignedStatus().equals(APIConstants.BATCH_PAYMENT_ASSIGNED)) {
//            batchPaymentAssignmentService.assignBatchPayment(obj, getLoggedInUser().getStaffId(), pojo.getAssignedStatus());
        }
    }

    public BatchPayment convertBatchPaymentPojoToBatchPaymentModel(BatchPaymentPojo batchPaymentPojo) throws Exception {
        String SUBMODULE = MODULE + "[covertBatchPaymentPojoToBatchPaymentModel()]";
        List<BatchPaymentMapping> batchPaymentMapping = new ArrayList<>();
        BatchPayment batchPayment = new BatchPayment();
        if (batchPaymentPojo != null) {
            batchPayment.setBatchName(batchPaymentPojo.getBatchName());
            batchPayment.setIsDeleted(false);
            batchPayment.setStatus("Pending");
            if (getLoggedInUser() != null && getLoggedInUser().getStaffId() != null) {
                StaffUser staffUser = staffUserRepository.findById(getLoggedInUser().getStaffId()).orElse(null);
                batchPayment.setCreateBy(staffUser.getUsername());
            }
            if (batchPaymentPojo.getBatchPaymentMappingList() != null && batchPaymentPojo.getBatchPaymentMappingList().size() > 0) {
                List<Integer> allBatchPaymentMappingList = batchPaymentMappingRepository.findAll().stream().filter(y -> y.getIs_deleted().equals(false)).map(x -> x.getCreditDocument().getId()).collect(Collectors.toList());
                List<Integer> creditDocumentIds = creditDocRepository.findAllCreditDocID();

                for (BatchPaymentMappingPojo oldMapping : batchPaymentPojo.getBatchPaymentMappingList()) {
//                    if (creditDocumentIds.contains(Integer.parseInt(oldMapping.getCredit_doc_id().toString()))) {
//                        if (allBatchPaymentMappingList.contains(Integer.parseInt(oldMapping.getCredit_doc_id().toString())))
//                            throw new Exception("Unable save, Found duplicate CreditDocument entry under Batch Payment Mapping.");
//                    } else
//                        throw new Exception("Unable save, No Such a CreditDocument found with Id " + oldMapping.getCredit_doc_id());
                    if (oldMapping.getCredit_doc_id() != null) {
                        if (creditDocumentIds.contains(Integer.parseInt(oldMapping.getCredit_doc_id().toString()))) {
                            if (allBatchPaymentMappingList.contains(Integer.parseInt(oldMapping.getCredit_doc_id().toString()))) {
                                throw new Exception("Unable to save, Found duplicate CreditDocument entry under Batch Payment Mapping.");
                            }
                        } else {
                            throw new Exception("Unable to save, No Such a CreditDocument found with Id " + oldMapping.getCredit_doc_id());
                        }
                    }
                    BatchPaymentMapping mapping = new BatchPaymentMapping();
                    CreditDocument document = new CreditDocument();
                    if (oldMapping.getCredit_doc_id() != null) {
                        document.setId(Integer.parseInt(oldMapping.getCredit_doc_id().toString()));
                    }
                   // document.setId(Integer.parseInt(oldMapping.getCredit_doc_id().toString()));
                    mapping.setCreditDocument(document);
                    mapping.setBatchPayment(batchPayment);
                    batchPaymentMapping.add(mapping);
                }
                batchPayment.setBatchPaymentMappingList(batchPaymentMapping);
            } else
                throw new Exception("No CreditDocument Selected");
        }
        return batchPayment;
    }


    public BatchPayment convertBatchPaymentPojoToBatchPaymentModels(BatchPaymentPojo batchPaymentPojo) throws Exception {
        String SUBMODULE = MODULE + "[covertBatchPaymentPojoToBatchPaymentModel()]";
        List<BatchPaymentMapping> batchPaymentMapping = new ArrayList<>();
        BatchPayment batchPayment = new BatchPayment();
        try{

            if (batchPaymentPojo != null) {
                batchPayment.setBatchName(batchPaymentPojo.getBatchName());
                batchPayment.setIsDeleted(false);
                batchPayment.setStatus("Pending");
                if (getLoggedInUser() != null && getLoggedInUser().getStaffId() != null) {
                    StaffUser staffUser = staffUserRepository.findById(getLoggedInUserId()).get();
                    batchPayment.setCreateBy(staffUser.getUsername());
                }
                if (batchPaymentPojo.getBatchPaymentMappingList() != null && batchPaymentPojo.getBatchPaymentMappingList().size() > 0) {
                    List<Integer> allBatchPaymentMappingList = batchPaymentMappingRepository.findAll().stream().filter(y -> y.getIs_deleted().equals(false)).map(x -> x.getCreditDocument().getId()).collect(Collectors.toList());
                    List<Integer> creditDocumentIds = creditDocRepository.findAllCreditDocIDAndBatchStatus();
                    CreditDocIdsMessages creditDocIdsMessages = new CreditDocIdsMessages();
                    List<Integer> ids = new ArrayList<>();
                    for (BatchPaymentMappingPojo oldMapping : batchPaymentPojo.getBatchPaymentMappingList()) {

                        BatchPaymentMapping mapping = new BatchPaymentMapping();
                        CreditDocument document = new CreditDocument();
                        document.setId(Integer.parseInt(oldMapping.getCredit_doc_id().toString()));
                        mapping.setCreditDocument(document);
                        mapping.setBatchPayment(batchPayment);
                        batchPaymentMapping.add(mapping);
                        CreditDocument creditDocument = creditDocRepository.findById(Integer.parseInt(oldMapping.getCredit_doc_id().toString())).get();
                        creditDocument.setBatchAssigned(true);
                        creditDocRepository.save(creditDocument);
                        ids.add(creditDocument.getId());
                    }
                    creditDocIdsMessages.setAction("Approve");
                    creditDocIdsMessages.setCreditDocumentIds(ids);
                    batchPayment.setBatchPaymentMappingList(batchPaymentMapping);
//                    messageSender.send(creditDocIdsMessages, SharedDataConstants.QUEUE_CREDIT_DOC_IDS_TO_CMS);
                    kafkaMessageSender.send(new KafkaMessageData(creditDocIdsMessages, CreditDocIdsMessages.class.getSimpleName()));

                    return batchPayment;
                } else
                    throw new Exception("No CreditDocument Selected");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

        return null;
    }

    public BatchPayment saveBatchPayment(BatchPayment batchPayment) throws Exception {
        String SUBMODULE = MODULE + "[saveBatchPayment()]";
        try {
            BatchPayment save = batchPaymentRepository.save(batchPayment);
            return save;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);



            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }

    public GenericDataDTO saveBatch(BatchPaymentPojo pojo)  throws Exception{

        String SUBMODULE = MODULE + "[save()]";
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try {
            BatchPayment obj = convertBatchPaymentPojoToBatchPaymentModels(pojo);
            if (getBUIdsFromCurrentStaff().size() == 1)
                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
            obj = saveBatchPayment(obj);
            if (pojo.getAssignedStatus().equals(APIConstants.BATCH_PAYMENT_ASSIGNED)) {
                batchPaymentAssignmentService.assignBatchPayment(obj, getLoggedInUserId(), pojo.getAssignedStatus());
            }
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Success");
        }catch (Exception e){
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage(e.getMessage());
            e.printStackTrace();

        }
        return genericDataDTO;

    }

    public Page<BatchPaymentDetailPojo> searchBatch(SearchBatchPaymentPojo searchBatchPaymentPojo, PaginationRequestDTO requestDTO) {
        Page<BatchPaymentDetailPojo> batchPaymentDetailPojos = null;
        try {
            if (searchBatchPaymentPojo != null) {
                batchPaymentDetailPojos = findBatchPayments(searchBatchPaymentPojo, requestDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return batchPaymentDetailPojos;
    }

    public Page<BatchPaymentDetailPojo> findBatchPayments(SearchBatchPaymentPojo search, PaginationRequestDTO requestDTO) {
        Page<BatchPaymentDetailPojo> batchPaymentPojos = null;
        try {
            QBatchPaymentMapping qBatchPaymentMapping = QBatchPaymentMapping.batchPaymentMapping;
//        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
            QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
            BooleanExpression exp = qBatchPaymentMapping.isNotNull();


            System.out.println("search.getStatus() :::: " + search.getStatus());
            if (search.getStatus() != null && search.getStatus().size() > 0) {
                exp = exp.and(qBatchPaymentMapping.batchPayment.status.in(search.getStatus()));
            }


            if (search.getBranch() != null && !StringUtils.isEmpty(search.getBranch())) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.customer.branch.eq(Long.valueOf(search.getBranch())));
            }

            if (search.getFromDate() != null) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.paymentdate.after(search.getFromDate().minusDays(1)));
            }
            if (search.getToDate() != null) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.paymentdate.before(search.getToDate().plusDays(1)));
            }

            if (search.getPartner() != null) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.customer.partner.eq(search.getPartner()));
            }
            if (search.getDestinationBank() != null) {
                QBankManagement qBankManagement = QBankManagement.bankManagement;
                BooleanExpression be = qBankManagement.isNotNull().and(qBankManagement.isDeleted.eq(false)).and(qBankManagement.id.eq(Long.valueOf(search.getDestinationBank())));
                Optional<BankManagement> bankManagement = bankManagementRepository.findOne(be);

                if (bankManagement != null) {
                    exp = exp.and(qBatchPaymentMapping.creditDocument.destinationBank.eq(bankManagement.get().getId()));
                }
            }

            if (search.getServiceArea() != null) {
                if (qBatchPaymentMapping.creditDocument.customer.serviceAreaId != null)
                    exp = exp.and(qBatchPaymentMapping.creditDocument.customer.serviceAreaId.eq(Long.valueOf(search.getServiceArea())));
            } else {
                if (getLoggedInUserId() != 1) {
                    if (getServiceAreaIdList() != null && !getServiceAreaIdList().isEmpty()) {
                        NumberPath<Long> qServiceArea = qBatchPaymentMapping.creditDocument.customer.serviceAreaId;
                        if (qBatchPaymentMapping.creditDocument.customer.serviceAreaId != null)
                            exp = exp.and(qServiceArea.isNotNull().and(qServiceArea.in(getServiceAreaIdList())));
                    }
                }
            }
            exp = exp.and(qBatchPaymentMapping.creditDocument.isDelete.eq(false)).and(qBatchPaymentMapping.creditDocument.customer.isDeleted.eq(false));
            exp=exp.and(qBatchPaymentMapping.is_deleted.eq(false));
            if (getLoggedInMvnoId() != 1)
                exp = exp.and(qBatchPaymentMapping.creditDocument.mvnoId.eq(getLoggedInMvnoId()));
            if (!CollectionUtils.isEmpty(getBUIdsFromCurrentStaff()) && getBUIdsFromCurrentStaff().size() > 0)
                exp = exp.and(qBatchPaymentMapping.creditDocument.buID.in(getBUIdsFromCurrentStaff()));
            List<BatchPaymentMapping> batchPaymentMappings = (List<BatchPaymentMapping>) batchPaymentMappingRepository.findAll(exp);
            List<Long> batchIds = batchPaymentMappings.stream().map(batchPaymentMapping -> batchPaymentMapping.getBatchPayment().getId()).distinct().collect(Collectors.toList());

            QBatchPaymentDetails qBatchPaymentDetails = QBatchPaymentDetails.batchPaymentDetails;
            BooleanExpression expression = qBatchPaymentDetails.isNotNull();
            if (search.getStaff() != null) {
                expression = expression.and(qBatchPaymentDetails.batchPayment.id.in(batchIds).and(qBatchPaymentDetails.staffUser.id.eq(search.getStaff())));
            } else {
                expression = expression.and(qBatchPaymentDetails.batchPayment.id.in(batchIds).and(qBatchPaymentDetails.staffUser.id.eq(getLoggedInUser().getStaffId())));
            }
            Pageable pageable = generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), "id", 0);

            Page<BatchPaymentDetails> batchPaymentDetails = (Page<BatchPaymentDetails>) batchPaymentDetailsRepository.findAll(expression,pageable);

            List<BatchPaymentDetailPojo> batchPaymentDetailPojos = convetBatchPaymentToPojo(batchPaymentDetails.getContent());
             batchPaymentPojos = new PageImpl<BatchPaymentDetailPojo>(batchPaymentDetailPojos, pageable, batchPaymentDetails.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batchPaymentPojos;
    }

    private List<BatchPaymentAssignment> getAssignment(List<BatchPaymentAssignment> batchPaymentAssignments) {
        List<BatchPaymentAssignment> assignmentList = new ArrayList<>();
        List<Long> batchIdList = batchPaymentAssignments.stream().map(x -> x.getBatchPayment().getId()).distinct().collect(Collectors.toList());
        batchIdList.forEach(x -> {
            List<BatchPaymentAssignment> tmp = batchPaymentAssignments.stream().filter(y -> y.getBatchPayment().getId().equals(x)).collect(Collectors.toList());
            assignmentList.add(tmp.get(tmp.size() - 1));
        });
        return assignmentList;
    }

    private List<BatchPaymentDetailPojo> convetBatchPaymentToPojo(List<BatchPaymentDetails> batchPayments) {
        List<BatchPaymentDetailPojo> list = new ArrayList<>();
        batchPayments.forEach(data -> {
            BatchPaymentDetailPojo detailPojo = new BatchPaymentDetailPojo();
            detailPojo.setBatchId(data.getBatchPayment().getId());
            detailPojo.setBatchName(data.getBatchPayment().getBatchName());
            detailPojo.setAssignee(data.getStaffUser().getUsername());
            if (data.getAssignedStatus().equalsIgnoreCase("AssignedToOtherTeam")) {
                detailPojo.setAssignmentStatus("AssignedToOtherTeam");
            } else {
                detailPojo.setAssignmentStatus(data.getStatus());
            }
            detailPojo.setBatchStatus(data.getBatchPayment().getStatus());
            detailPojo.setCreatedBy(data.getBatchPayment().getCreateBy());
            detailPojo.setStaffId(data.getStaffUser().getId());
            if (data.getRemark() != null) {
                detailPojo.setRemarks(data.getRemark());
            }
            detailPojo.setNextStaffId(data.getNextStaffUser() != null ? data.getNextStaffUser().getId() : null);
            detailPojo.setNextstaffname(data.getNextStaffUser() != null ? data.getNextStaffUser().getUsername() : null);
            List<BatchPaymentMapping> mappings = data.getBatchPayment().getBatchPaymentMappingList().stream().filter(x -> x.getIs_deleted().equals(false)).collect(Collectors.toList());
            detailPojo.setInvoiceCount(String.valueOf(mappings.size()));
            detailPojo.setTotalAmount(Double.toString(mappings.stream().mapToDouble(x -> x.getCreditDocument().getAmount()).sum()));
            detailPojo.setCreditDocumentList(mappings.stream().map(x -> batchPaymentMappingService.convertCreditDocumentIntoCreditPojo(x)).collect(Collectors.toList()));
            list.add(detailPojo);
        });
        return list;
    }

    public List<BatchPaymentDetailPojo> getBatchPaymentDetailListByStaffId(Long staffId) {
        String SUBMODULE = MODULE + "[getBatchPaymentDetailListByStaffId()]";
        List<BatchPaymentDetailPojo> list = new ArrayList<>();
        List<BatchPaymentAssignment> batchPaymentAssignments = new ArrayList<>();
        try {
            if (staffId != null && getLoggedInUser().getStaffId().toString().equals(staffId.toString())) {
                batchPaymentAssignments = batchPaymentAssignmentRepository.findByStaffId(staffId);
                batchPaymentAssignments = getAssignment(batchPaymentAssignments);
                batchPaymentAssignments.forEach(data -> {
                    if (!data.getBatchPayment().getIsDeleted()) {
                        BatchPaymentDetailPojo detailPojo = new BatchPaymentDetailPojo();
                        detailPojo.setBatchId(data.getBatchPayment().getId());
                        detailPojo.setBatchName(data.getBatchPayment().getBatchName());
                        detailPojo.setAssignee(data.getStaffUser().getUsername());
                        if (data.getAssignedStatus().equalsIgnoreCase("AssignedToOtherTeam")) {
                            detailPojo.setAssignmentStatus("AssignedToOtherTeam");
                        } else {
                            detailPojo.setAssignmentStatus(data.getStatus());
                        }
                        detailPojo.setBatchStatus(data.getBatchPayment().getStatus());
                        detailPojo.setCreatedBy(data.getBatchPayment().getCreateBy());
                        detailPojo.setStaffId(data.getStaffUser().getId());
                        if (data.getRemark() != null) {
                            detailPojo.setRemarks(data.getRemark());
                        }
                        detailPojo.setNextStaffId(data.getNextStaffUser() != null ? data.getNextStaffUser().getId() : null);
                        List<BatchPaymentMapping> mappings = data.getBatchPayment().getBatchPaymentMappingList().stream().filter(x -> x.getIs_deleted().equals(false)).collect(Collectors.toList());
                        detailPojo.setInvoiceCount(String.valueOf(mappings.size()));
                        detailPojo.setTotalAmount(Double.toString(mappings.stream().mapToDouble(x -> x.getCreditDocument().getAmount()).sum()));
                        detailPojo.setCreditDocumentList(mappings.stream().map(x -> batchPaymentMappingService.convertCreditDocumentIntoCreditPojo(x)).collect(Collectors.toList()));
                        if(mappings.get(0).getCreditDocument().getFilename() != null && mappings.get(0).getCreditDocument().getFilename().length() > 0){
                            detailPojo.setFilename(mappings.get(0).getCreditDocument().getFilename());
                        }
                        if(mappings.get(0).getCreditDocument() != null){
                            detailPojo.setCreditDocId(mappings.get(0).getCreditDocument().getId());
                            detailPojo.setCustId(mappings.get(0).getCreditDocument().getCustomer().getId());
                        }
                        if(data.getNextStaffUser() != null){
                            // detailPojo.setAssignedName(data.getNextStaffUser().getUsername());
                            detailPojo.setNextstaffname(data.getNextStaffUser().getUsername());
                        }
                        list.add(detailPojo);

                    }
                });
            }
            return list;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<BatchPaymentAuditDetails> getBatchPaymentAuditDetail(Long batchId) throws Exception {
        String SUBMODULE = MODULE + "[getBatchPaymentAuditDetail()]";
        List<BatchPaymentAuditDetails> details = new ArrayList<>();
        if (batchId != null) {
            List<BatchPaymentAssignment> assignmentList = batchPaymentAssignmentService.getBatchPaymentAssignmentByBatchId(batchId);
            assignmentList.forEach(data -> {
                details.add(batchPaymentAssignmentService.convertBatchAssignmentToBatchAssignmentAudit(data));
            });
        } else
            throw new Exception("Batch Payment Id required");
        return details;
    }

    public boolean addBatchPaymentMappingInExistingBatch(BatchPaymentPojo batchPaymentPojo) throws Exception {
        String SUBMODULE = MODULE + "[addBatchPaymentMappingInExistingBatch()]";
        if (batchPaymentPojo != null && batchPaymentPojo.getId() != null) {
            Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchPaymentPojo.getId());
            if (batchPayment.isPresent()) {
                CreditDocIdsMessages creditDocIdsMessages = new CreditDocIdsMessages();
                List<Integer> ids = new ArrayList<>();
                StaffUser staffUser = staffUserRepository.findById(getLoggedInUserId()).get();
                if (staffUser != null && staffUser.getUsername().equals(batchPayment.get().getCreateBy())) {
                    if (batchPaymentPojo.getBatchPaymentMappingList() != null && batchPaymentPojo.getBatchPaymentMappingList().size() > 0) {
                        batchPaymentPojo.getBatchPaymentMappingList().forEach(data ->
                        {
                            BatchPaymentMapping mapping = new BatchPaymentMapping();
                            mapping.setBatchPayment(batchPayment.get());
                            CreditDocument document = new CreditDocument();
                            document.setId(Integer.parseInt(data.getCredit_doc_id().toString()));
                            mapping.setCreditDocument(document);
                            batchPayment.get().getBatchPaymentMappingList().add(mapping);
                            ids.add(Integer.parseInt(data.getCredit_doc_id().toString()));
                        });
                        creditDocIdsMessages.setAction("Approve");
                        creditDocIdsMessages.setCreditDocumentIds(ids);
//                        messageSender.send(creditDocIdsMessages, SharedDataConstants.QUEUE_CREDIT_DOC_IDS_TO_CMS);
                        kafkaMessageSender.send(new KafkaMessageData(creditDocIdsMessages, CreditDocIdsMessages.class.getSimpleName()));
                        batchPaymentRepository.save(batchPayment.get());
                    } else
                        throw new Exception("CreditDoucment List need to be required");
                } else
                    throw new Exception("You are not Authorized user to Add BatchMapping");
            } else
                throw new Exception("Batch Payment Mapping not found");
        }
        return true;
    }

    public List<CreditPojo> getMappingList(Long batchId) throws Exception {
        List<CreditPojo> list = new ArrayList<>();
        if (batchId != null) {
            Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchId);
            if (batchPayment.isPresent()) {
                if (!batchPayment.get().getIsDeleted()) {
                    batchPayment.get().getBatchPaymentMappingList().stream().filter(x -> !x.getIs_deleted()).forEach(data -> {
                        list.add(batchPaymentMappingService.convertCreditDocumentIntoCreditPojo(data));
                    });
                } else
                    throw new Exception("No Batch found with Id " + batchId);
            } else
                throw new Exception("No Batch found with Id " + batchId);
        } else
            throw new Exception("BatchId Required");
        return list;
    }
}
