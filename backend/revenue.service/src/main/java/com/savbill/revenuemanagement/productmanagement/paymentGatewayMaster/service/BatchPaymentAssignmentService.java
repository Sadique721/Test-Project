package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.service;


import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.entity.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.revenuemanagement.core.entity.TeamUserMapping.TeamsRepository;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.entity.staff.StaffUserService;
import com.savbill.revenuemanagement.core.entity.staff.Teams;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.ledger.DebitDocService;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.*;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPayment;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentAssignment;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentDetails;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentMapping;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.BatchAssignPojo;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.BatchPaymentAssignmentPojo;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.BatchPaymentAuditDetails;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository.BatchPaymentAssignmentRepository;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository.BatchPaymentDetailsRepository;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository.BatchPaymentRepository;
import com.savbill.revenuemanagement.rabbitmq.messages.CreditDocMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.CreditDocIdsMessages;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.ListOfCreditDocForBatch;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchPaymentAssignmentService extends AbstractService<BatchPaymentAssignment, BatchPaymentAssignmentPojo, Long> {

    @Autowired
    private BatchPaymentAssignmentRepository batchPaymentAssignmentRepository;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private BatchPaymentService batchPaymentService;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private BatchPaymentRepository batchPaymentRepository;

    @Autowired
    private ClientServiceSrv clientService;

    @Autowired
    private TeamsRepository  teamsRepository;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private TeamUserMappingsRepocitory teamUserMappingsRepocitory;
    @Autowired
    private BatchPaymentDetailsRepository batchPaymentDetailsRepository;

//    @Autowired
//    MessageSender messageSender;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    @Override
    protected JpaRepository<BatchPaymentAssignment, Long> getRepository() {
        return batchPaymentAssignmentRepository;
    }

    public void assignBatchPayment(BatchPayment batchPayment, Integer staffId, String flag) {
        if (staffId != null) {
            StaffUser staff = staffUserRepository.findById(getLoggedInUserId()).get();
            if (batchPayment != null && staff != null)
                saveBatchPaymentAssignment(batchPayment, staff, flag);
        }
    }

    public void saveBatchPaymentAssignment(BatchPayment batchPayment, StaffUser staffUser, String assignedStatus) {
        BatchPaymentAssignment batchPaymentAssignment = new BatchPaymentAssignment();
        batchPaymentAssignment.setAssignedDate(LocalDate.now());
        if (batchPayment != null && staffUser != null) {
            batchPaymentAssignment.setBatchPayment(batchPayment);
            batchPaymentAssignment.setStaffUser(staffUser);
            String finalTeamAuthority = CommonConstants.TEAMTYPE;
            Set<Long> teamsIds = teamUserMappingsRepocitory.teamIds(staffUser.getId().longValue());
            Set<Teams> teams1 = teamsRepository.findAllByIdIn(teamsIds);

            if (finalTeamAuthority != null && teams1.stream().anyMatch(x -> {
                String teamType = x.getTeamType();
                return teamType != null && teamType.equalsIgnoreCase(finalTeamAuthority);
            })){
                batchPaymentAssignment.setNextStaffUser(null);
            }else {
                batchPaymentAssignment.setNextStaffUser(staffUser);
            }
            batchPaymentAssignment.setStatus("Pending");
//            batchPaymentAssignment.setRemark("Approved by : " + staffUser.getUsername());
            batchPaymentAssignment.setAssignedStatus(assignedStatus);
            batchPaymentAssignmentRepository.save(batchPaymentAssignment);
        }

        saveBatchPaymentDetails(batchPayment,staffUser,assignedStatus);
    }

    public void saveBatchPaymentDetails(BatchPayment batchPayment, StaffUser staffUser, String assignedStatus) {
        BatchPaymentDetails batchPaymentDetails = new BatchPaymentDetails();
        batchPaymentDetails.setAssignedDate(LocalDate.now());
        if (batchPayment != null && staffUser != null) {
            batchPaymentDetails.setBatchPayment(batchPayment);
            batchPaymentDetails.setStaffUser(staffUser);
            String finalTeamAuthority = CommonConstants.TEAMTYPE;
            Set<Long> teamsIds = teamUserMappingsRepocitory.teamIds(staffUser.getId().longValue());
            Set<Teams> teams1 = teamsRepository.findAllByIdIn(teamsIds);

            if (finalTeamAuthority != null && teams1.stream().anyMatch(x -> {
                String teamType = x.getTeamType();
                return teamType != null && teamType.equalsIgnoreCase(finalTeamAuthority);
            })){
                batchPaymentDetails.setNextStaffUser(null);
            }else {
                batchPaymentDetails.setNextStaffUser(staffUser);
            }
            batchPaymentDetails.setStatus("Pending");
//            batchPaymentAssignment.setRemark("Approved by : " + staffUser.getUsername());
            batchPaymentDetails.setAssignedStatus(assignedStatus);
            batchPaymentDetailsRepository.save(batchPaymentDetails);
        }

    }

    public void batchPaymentApprove(BatchAssignPojo batchAssignPojo) throws Exception {
        if (batchAssignPojo == null || batchAssignPojo.getBatchId() == null) {
            throw new Exception("BatchId Required");
        }

        BatchPayment batchPayment = batchPaymentRepository.findById(batchAssignPojo.getBatchId()).orElse(null);
        if (Objects.isNull(batchPayment)) {
            throw new Exception("No BatchPayment found with given id " + batchAssignPojo.getBatchId());
        }

        if (batchAssignPojo.getStaffId() == null) {
            throw new Exception("StaffId Required");
        }

        StaffUser staffUser = staffUserRepository.findById(batchAssignPojo.getStaffId()).orElse(null);
        if (Objects.isNull(staffUser)) {
            throw new Exception("No StaffUser found with given id " + batchAssignPojo.getStaffId());
        }

        StaffUser nextStaffUser = null;
        if(batchAssignPojo.getNextStaffId()!=null){
            nextStaffUser  = staffUserRepository.findById(batchAssignPojo.getNextStaffId()).orElse(null);

        }
        BatchPaymentAssignment lastAssignment = batchPaymentAssignmentRepository.findTopByBatchPaymentAndStaffUserAndStatusOrderByAssignedDateDesc(batchPayment, staffUser,"Pending").orElse(null);
        BatchPaymentDetails lastAssignmentDetails = batchPaymentDetailsRepository.findTopByBatchPaymentAndStaffUserAndStatusOrderByAssignedDateDesc(batchPayment, staffUser,"Pending").orElse(null);

        if (nextStaffUser!=null) {
//            TODO:ticket notification
//            hierarchyService.sendWorkflowAssignActionMessage(nextStaffUser.getCountryCode(), nextStaffUser.getPhone(), nextStaffUser.getEmail(), nextStaffUser.getMvnoId(), nextStaffUser.getUsername(), batchPayment.getBatchname());

            if (lastAssignment != null) {
                lastAssignment.setStatus("Approved");
                lastAssignment.setAssignedStatus("AssignedToOtherTeam");
                lastAssignment.setRemark(batchAssignPojo.getRemark());
                lastAssignment.setNextStaffUser(nextStaffUser);
                lastAssignment.setRemark(batchAssignPojo.getRemark());
                batchPaymentAssignmentRepository.save(lastAssignment);


                BatchPaymentDetails lastAssignmentDetailsDelete = batchPaymentDetailsRepository.findByBatchPaymentAndStaffUserDelete(batchAssignPojo.getBatchId(),nextStaffUser.getId());
                if(lastAssignmentDetailsDelete!=null) {
                    batchPaymentDetailsRepository.delete(lastAssignmentDetailsDelete);
                }

                if (lastAssignment.getNextStaffUser() != null) {
                    BatchPaymentAssignment nextpaymentAssignment = new BatchPaymentAssignment();
                    nextpaymentAssignment.setBatchPayment(batchPayment);
                    nextpaymentAssignment.setAssignedDate(LocalDate.now());
                    nextpaymentAssignment.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
                    nextpaymentAssignment.setStaffUser(nextStaffUser);
                    nextpaymentAssignment.setStatus("Pending");
                    nextpaymentAssignment.setRemark(batchAssignPojo.getRemark());
                    nextpaymentAssignment.setNextStaffUser(null);
                    batchPaymentAssignmentRepository.save(nextpaymentAssignment);

                    BatchPaymentDetails nextpaymentdetails = new BatchPaymentDetails();
                    nextpaymentdetails.setBatchPayment(batchPayment);
                    nextpaymentdetails.setAssignedDate(LocalDate.now());
                    nextpaymentdetails.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
                    nextpaymentdetails.setStaffUser(nextStaffUser);
                    nextpaymentdetails.setStatus("Pending");
                    nextpaymentdetails.setRemark(batchAssignPojo.getRemark());
                    nextpaymentdetails.setNextStaffUser(null);
                    batchPaymentDetailsRepository.save(nextpaymentdetails);
                }
            }
        }

        if (lastAssignment != null) {
            lastAssignment.setStatus("Approved");
            lastAssignment.setAssignedStatus("AssignedToOtherTeam");
            lastAssignment.setRemark(batchAssignPojo.getRemark());
            lastAssignment.setNextStaffUser(nextStaffUser);
            lastAssignment.setRemark(batchAssignPojo.getRemark());
            batchPaymentAssignmentRepository.save(lastAssignment);

            lastAssignmentDetails.setStatus("Approved");
            lastAssignmentDetails.setAssignedStatus("AssignedToOtherTeam");
            lastAssignmentDetails.setRemark(batchAssignPojo.getRemark());
            lastAssignmentDetails.setNextStaffUser(nextStaffUser);
            lastAssignmentDetails.setRemark(batchAssignPojo.getRemark());
            lastAssignmentDetails.setIsDelete(true);
            batchPaymentDetailsRepository.save(lastAssignmentDetails);
        }

        String finalTeamAuthority = CommonConstants.TEAMTYPE;
        Set<Long> teamsIds = teamUserMappingsRepocitory.teamIds(staffUser.getId().longValue());
        Set<Teams> teams1 = teamsRepository.findAllByIdIn(teamsIds);

        if (finalTeamAuthority != null && teams1.stream().anyMatch(x -> {
            String teamType = x.getTeamType();
            return teamType != null && teamType.equalsIgnoreCase(finalTeamAuthority);
        })) {
            batchPayment.setStatus("Approved");

            if (lastAssignment != null) {
                lastAssignment.setAssignedStatus("Approved");
                lastAssignment.setStatus("Approved");
                batchPaymentAssignmentRepository.save(lastAssignment);

                lastAssignmentDetails.setAssignedStatus("Approved");
                lastAssignmentDetails.setStatus("Approved");
                lastAssignmentDetails.setIsDelete(true);
                batchPaymentDetailsRepository.save(lastAssignmentDetails);

                List<CreditDocMessage> newCreditMessageList = new ArrayList<>();
                for (BatchPaymentMapping batchPaymentMapping : batchPayment.getBatchPaymentMappingList()) {
                    if (batchPaymentMapping.getCreditDocument() != null) {
                        Optional<DebitDocument> debitDocumentOpt = getDebitDocByCreditDoc(batchPaymentMapping.getCreditDocument().getId());
                        if (debitDocumentOpt.isPresent()) {
                            DebitDocument debitDocument = debitDocumentOpt.get();
                            CreditDocument creditDocument = batchPaymentMapping.getCreditDocument();

                            if (debitDocument.getTotalamount() - debitDocument.getAdjustedAmount() == 0) {
                                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                            } else if (debitDocument.getTotalamount() - debitDocument.getAdjustedAmount() > 0) {
                                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                            }

                            debitDocRepository.save(debitDocument);
                            creditDocRepository.save(creditDocument);

                            List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocIdAndCreditDocId(debitDocument.getId(), creditDocument.getId());
                            CreditDocMessage creditDocMessage = new CreditDocMessage(creditDocument, IterableUtils.toList(creditDebitDocMappings));

                            CreditDocument newCreditDocument = creditDocService.save(creditDocMessage);
                            CreditDocMessage newCreditDocMessage = new CreditDocMessage(newCreditDocument);
                            newCreditMessageList.add(newCreditDocMessage);

                            Customers customers=customersRepository.findById(creditDocMessage.getCustomer()).orElse(null);
                            customers.setWalletbalance(creditDocMessage.getWalletBalance());
                            customersRepository.save(customers);
                        }
                    }
                }
                ListOfCreditDocForBatch listOfCreditDocForBatch = new ListOfCreditDocForBatch();
                listOfCreditDocForBatch.setCreditDocMessageList(newCreditMessageList);
//                messageSender.send(listOfCreditDocForBatch,SharedDataConstants.QUEUE_CREDIT_DOC_DETAILS_TO_CMS);
                kafkaMessageSender.send(new KafkaMessageData(listOfCreditDocForBatch, ListOfCreditDocForBatch.class.getSimpleName()));
            }
            batchPaymentRepository.save(batchPayment);
        }


    }

    public Optional<DebitDocument> getDebitDocByCreditDoc(Integer creditId) {

        List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(creditId);
        Optional<DebitDocument> debitDocument = debitDocRepository.findById(creditDebitDocMappingList.get(0).getDebtDocId());
        return debitDocument;
    }

    public void batchPaymentReject(BatchAssignPojo batchAssignPojo) throws Exception {
        if (batchAssignPojo != null) {
            if (batchAssignPojo.getBatchId() != null) {
                BatchPayment batchPayment1 = batchPaymentRepository.findById(batchAssignPojo.getBatchId()).get();
//                hierarchyService.sendWorkflowAssignActionMessage(nextStaffUser.get().getCountryCode(), nextStaffUser.get().getPhone(), nextStaffUser.get().getEmail(), nextStaffUser.get().getMvnoId(), nextStaffUser.get().getUsername(), batchPayment1.getBatchname());
                Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchAssignPojo.getBatchId());
                if (batchPayment.isPresent()) {
                    if (batchAssignPojo.getStaffId() != null) {
                        Optional<StaffUser> staffUser = staffUserRepository.findById(batchAssignPojo.getStaffId());
                        if (staffUser.isPresent()) {
                            List<BatchPaymentAssignment> batchPaymentAssignment = batchPaymentAssignmentRepository.findByBatchPaymentAndStaffUser(batchPayment.get().getId(), staffUser.get().getId());
                            List<BatchPaymentDetails> batchPaymentDetails = batchPaymentDetailsRepository.findByBatchPaymentAndStaffUser(batchPayment.get().getId(), staffUser.get().getId());
                            if (batchPaymentAssignment != null) {

                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setStatus("Rejected");
                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setRemark(batchAssignPojo.getRemark());
                                batchPaymentAssignmentRepository.save(batchPaymentAssignment.get(batchPaymentAssignment.size() - 1));

                                batchPaymentDetails.get(batchPaymentDetails.size() - 1).setStatus("Rejected");
                                batchPaymentDetails.get(batchPaymentDetails.size() - 1).setRemark(batchAssignPojo.getRemark());
                                batchPaymentDetails.get(batchPaymentDetails.size() - 1).setIsDelete(true);
                                batchPaymentDetailsRepository.save(batchPaymentDetails.get(batchPaymentDetails.size() - 1));

                                StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                                if (currentStaff != null && currentStaff.getUsername().equalsIgnoreCase(batchPayment.get().getCreateBy())) {
                                    List<Integer> creditDocIds = batchPayment.get().getBatchPaymentMappingList().stream().filter(y -> y.getIs_deleted().equals(false)).map(x -> x.getCreditDocument().getId()).collect(Collectors.toList());
                                    List<CreditDocument> creditDocument = creditDocRepository.findAllByIdIn(creditDocIds);
                                    List<CreditDocument> creditDocumentSaved = creditDocument.stream().peek(i->i.setBatchAssigned(false)).collect(Collectors.toList());
                                    creditDocRepository.saveAll(creditDocumentSaved);
                                    CreditDocIdsMessages creditDocIdsMessages = new CreditDocIdsMessages();
                                    creditDocIdsMessages.setAction("Rejected");
                                    creditDocIdsMessages.setCreditDocumentIds(creditDocIds);
//                                    messageSender.send(creditDocIdsMessages, SharedDataConstants.QUEUE_CREDIT_DOC_IDS_TO_CMS);
                                    kafkaMessageSender.send(new KafkaMessageData(creditDocIdsMessages, CreditDocIdsMessages.class.getSimpleName()));
                                    batchPayment.get().setStatus("Rejected");
                                    batchPaymentRepository.save(batchPayment.get());
                                } else {
                                    List<BatchPaymentAssignment> previousBatchAssignment = batchPaymentAssignmentRepository.findPreviousAssigne(batchPayment.get().getId(), staffUser.get().getId());
                                    BatchPaymentAssignment previousBatchAssignmentForloggedInUser = batchPaymentAssignmentRepository.findPreviousAssignees(batchPayment.get().getId(), getLoggedInUserId());

                                    List<BatchPaymentDetails> previousBatchdetails = batchPaymentDetailsRepository.findPreviousAssigne(batchPayment.get().getId(), staffUser.get().getId());
                                    BatchPaymentDetails previousBatchdetailsForloggedInUser = batchPaymentDetailsRepository.findPreviousAssignees(batchPayment.get().getId(), getLoggedInUserId());

                                    if (previousBatchAssignment != null && previousBatchAssignment.size() > 0) {
                                        BatchPaymentAssignment nextpaymentAssignment = new BatchPaymentAssignment();
                                        nextpaymentAssignment.setBatchPayment(batchPayment.get());
                                        nextpaymentAssignment.setAssignedDate(LocalDate.now());
                                        nextpaymentAssignment.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
                                        nextpaymentAssignment.setStaffUser(previousBatchAssignment.get(previousBatchAssignment.size() - 1).getStaffUser());
                                        nextpaymentAssignment.setStatus("Pending");
                                        nextpaymentAssignment.setNextStaffUser(currentStaff);
                                        previousBatchAssignmentForloggedInUser.setAssignedStatus("AssignedToOtherTeam");
                                        previousBatchAssignmentForloggedInUser.setNextStaffUser(previousBatchAssignment.get(previousBatchAssignment.size() - 1).getStaffUser());
                                        batchPaymentAssignmentRepository.save(nextpaymentAssignment);
                                        batchPaymentAssignmentRepository.save(previousBatchAssignmentForloggedInUser);

                                        batchPaymentDetailsRepository.delete(previousBatchdetails.get(0));

                                        BatchPaymentDetails nextpaymentDetails = new BatchPaymentDetails();
                                        nextpaymentDetails.setBatchPayment(batchPayment.get());
                                        nextpaymentDetails.setAssignedDate(LocalDate.now());
                                        nextpaymentDetails.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
                                        nextpaymentDetails.setStaffUser(previousBatchAssignment.get(previousBatchAssignment.size() - 1).getStaffUser());
                                        nextpaymentDetails.setStatus("Pending");
                                        nextpaymentDetails.setNextStaffUser(currentStaff);
                                        previousBatchdetailsForloggedInUser.setAssignedStatus("AssignedToOtherTeam");
                                        previousBatchdetailsForloggedInUser.setNextStaffUser(previousBatchAssignment.get(previousBatchAssignment.size() - 1).getStaffUser());
                                        previousBatchdetailsForloggedInUser.setIsDelete(true);
                                        batchPaymentDetailsRepository.save(nextpaymentDetails);
                                        batchPaymentDetailsRepository.save(previousBatchdetailsForloggedInUser);
                                    }
                                }
                            }
                        } else
                            throw new Exception("No StaffUser found with given id " + batchAssignPojo.getStaffId());
                    } else
                        throw new Exception("StaffId Required");
                } else
                    throw new Exception("No BatchPayment found with given id " + batchAssignPojo.getBatchId());
            } else
                throw new Exception("BatchId Required");
        }
    }
    public List<BatchPaymentAssignment> getBatchPaymentAssignmentByBatchId(Long batchId) {
        List<BatchPaymentAssignment> assignmentList = new ArrayList<>();
        QBatchPaymentAssignment qBatchPaymentAssignment = QBatchPaymentAssignment.batchPaymentAssignment;
        BooleanExpression expression = qBatchPaymentAssignment.isNotNull();
        expression = expression.and(qBatchPaymentAssignment.batchPayment.id.eq(batchId)).and(qBatchPaymentAssignment.batchPayment.isDeleted.eq(false));
        assignmentList = (List<BatchPaymentAssignment>) batchPaymentAssignmentRepository.findAll(expression);
        assignmentList.forEach(x -> {
            x.getBatchPayment().setBatchPaymentMappingList(x.getBatchPayment().getBatchPaymentMappingList().stream().filter(data -> data.getIs_deleted().equals(false)).collect(Collectors.toList()));
        });
        return assignmentList;
    }

    public BatchPaymentAuditDetails convertBatchAssignmentToBatchAssignmentAudit(BatchPaymentAssignment paymentAssignment) {
        if (paymentAssignment != null) {
            BatchPaymentAuditDetails paymentAuditDetails = new BatchPaymentAuditDetails();
            paymentAuditDetails.setBatchId(paymentAssignment.getBatchPayment().getId());
            paymentAuditDetails.setBatchName(paymentAssignment.getBatchPayment().getBatchName());
            paymentAuditDetails.setStaffName(paymentAssignment.getStaffUser().getUsername());
            if (paymentAssignment.getStaffUser() != null ) {
                Set<Long> teamIds = teamUserMappingsRepocitory.teamIds(paymentAssignment.getStaffUser().getId().longValue());
                if(teamIds!=null && teamIds.size()>0) {
                    Set<Teams> teams = teamsRepository.findAllByIdIn(teamIds);
                    paymentAuditDetails.setTeamName(teams.stream().findFirst().get().getName());
                }else {
                    paymentAuditDetails.setTeamName(null);
                }
            } else {
                paymentAuditDetails.setTeamName(null);
            }

            paymentAuditDetails.setStatus(paymentAssignment.getStatus());
            paymentAuditDetails.setRemark(paymentAssignment.getRemark());
            return paymentAuditDetails;
        }
        return null;
    }
}
