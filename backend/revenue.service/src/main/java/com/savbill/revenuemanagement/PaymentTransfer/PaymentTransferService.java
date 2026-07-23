package com.savbill.revenuemanagement.PaymentTransfer;

import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedgerDtls;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.childcustomer.repo.ChildCustomerRepo;
import com.savbill.revenuemanagement.productmanagement.parentchildmapping.ParentChildMappingRel;
import com.savbill.revenuemanagement.productmanagement.parentchildmapping.ParentChildMappingRepo;
import com.savbill.revenuemanagement.rabbitmq.messages.CreditDocMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.CreditDocMessageList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentTransferService {

    private static Log log = LogFactory.getLog(PaymentTransferService.class);

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PaymentTransferAuditRepository paymentTransferAuditRepository;

    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;

    @Autowired
    private ParentChildMappingRepo parentChildMappingRepo;

    @Autowired
    private ChildCustomerRepo childCustomerRepo;

    @Autowired
    KafkaMessageSender kafkaMessageSender;


    public void transferAmount(PaymentTransferDTO paymentTransferDTO) throws Exception {
        CreditDocument creditDocument = createTransferPayment(paymentTransferDTO);
    }

    public void verifyTransfer(PaymentTransferDTO paymentTransferDTO){
        if(paymentTransferDTO.getAmount() == null || paymentTransferDTO.getAmount() <= 0.1){
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Amount should be more than 0 to proceed.",null);
        }
        if(paymentTransferDTO.getMainCustomerId() == null){
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED,"User Id must be needed for transfer",null);
        }
        if(paymentTransferDTO.getFromParentCustomerId() != null && paymentTransferDTO.getToParentCustomerId() != null){
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED,"Transfers between two parent customers are not allowed.",null);
        }
        if((paymentTransferDTO.fromChildCustomerId != null || paymentTransferDTO.getFromParentCustomerId() != null)
                && paymentTransferDTO.getToParentCustomerId() == null
                && paymentTransferDTO.getToChildCustomerId() == null){
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Please enter receiver customer.",null);
        }

        if((paymentTransferDTO.getToParentCustomerId() != null || paymentTransferDTO.getToChildCustomerId() != null)
                && paymentTransferDTO.getFromChildCustomerId() == null
                && paymentTransferDTO.getFromParentCustomerId() == null){
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Please enter sender customer.",null);
        }

        boolean sameParent = paymentTransferDTO.getFromParentCustomerId() != null &&
                paymentTransferDTO.getFromParentCustomerId().equals(paymentTransferDTO.getToParentCustomerId());

        boolean sameChild = paymentTransferDTO.getFromChildCustomerId() != null &&
                paymentTransferDTO.getFromChildCustomerId().equals(paymentTransferDTO.getToChildCustomerId());


        if(sameParent || sameChild){
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Sender and receiver can't both be same.",null);
        }
        if(paymentTransferDTO.getFromParentCustomerId() != null) {
            Double parentwallet = customerLedgerDtlsRepository.findWalletAmt(paymentTransferDTO.getFromParentCustomerId());
            if(parentwallet <= 0){
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Wallet amount must be greater than 0",null);

            }
            if(parentwallet < paymentTransferDTO.getAmount()){
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Transfer must be less than wallet amount.",null);

            }
        }
        if(paymentTransferDTO.getFromChildCustomerId() != null) {
            Double childwallet = customerLedgerDtlsRepository.findwalletAmountById(paymentTransferDTO.getFromChildCustomerId());
            if(childwallet == null || childwallet <= 0){
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Wallet amount must be greater than 0.",null);

            }
            if(childwallet != null && childwallet < paymentTransferDTO.getAmount()){
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Transfer must be less than wallet amount.",null);
            }
        }
    }

    public CreditDocument createTransferPayment(PaymentTransferDTO paymentTransferDTO) throws Exception {
        String referenceNo = generateTransferRef(paymentTransferDTO);
        RecordPaymentPojo recordPaymentPojo = creditDocService.createPaymentForTransfer(paymentTransferDTO.getMainCustomerId() , referenceNo , paymentTransferDTO.getAmount());
        Customers customers = customersRepository.findCustomerDataByIdForPayment(paymentTransferDTO.getMainCustomerId());
        CreditDocument creditDocument  = creditDocService.saveAuto(recordPaymentPojo, false, false, false, customers.getMvnoId(), customers.getPartner(), null , false , customers.getId(), customers.getUsername(),true);
        creditDocument.setStatus("approved");
        creditDocRepository.save(creditDocument); /**Save status here for approval**/
        CreditDocMessageList creditDocMessageList = new CreditDocMessageList();
        List<CreditDocMessage> creditDocMessage = new ArrayList<>();
        CreditDocMessage creditDoc = new CreditDocMessage(creditDocument);
        creditDocMessage.add(creditDoc);
        creditDocMessageList.setCreditDocMessageList(creditDocMessage);
        kafkaMessageSender.send(new KafkaMessageData(creditDocMessageList, CreditDocMessageList.class.getSimpleName()));
        addLedgerForTransfer(paymentTransferDTO , creditDocument);
        savePaymentTransferAudit(paymentTransferDTO);
        return creditDocument;
    }

    public  String generateTransferRef(PaymentTransferDTO paymentTransferDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "TRF_"+paymentTransferDTO.getMainCustomerId()+"_"+ timestamp;
    }

    public PaymentTransferAudit generatePaymentTransferAuditDTO(PaymentTransferDTO paymentTransferDTO){
        PaymentTransferAudit paymentTransferAudit = new PaymentTransferAudit();
        paymentTransferAudit.setAmount(paymentTransferDTO.getAmount());
        paymentTransferAudit.setMainCustomerId(paymentTransferDTO.getMainCustomerId());
        if(paymentTransferDTO.getFromChildCustomerId() != null){
            paymentTransferAudit.setFromChildCustId(paymentTransferDTO.getFromChildCustomerId());
        }
        if(paymentTransferDTO.getToChildCustomerId() != null){
            paymentTransferAudit.setToChildCustId(paymentTransferDTO.getToChildCustomerId());
        }
        if(paymentTransferDTO.getFromParentCustomerId() != null){
            paymentTransferAudit.setFromParentCustId(paymentTransferDTO.getFromParentCustomerId());
        }
        if(paymentTransferDTO.getToParentCustomerId() != null){
            paymentTransferAudit.setToParentCustId(paymentTransferDTO.getToParentCustomerId());
        }
        return  paymentTransferAudit;
    }

    public void savePaymentTransferAudit(PaymentTransferDTO paymentTransferDTO){
        PaymentTransferAudit paymentTransferAudit = generatePaymentTransferAuditDTO(paymentTransferDTO);
        paymentTransferAuditRepository.save(paymentTransferAudit);
    }

    public void addLedgerForTransfer(PaymentTransferDTO paymentTransferDTO,CreditDocument creditDocument){
        /**For from wallet ledger**/
        CustomerLedgerDtls fromCustomerLedgerDtls = new CustomerLedgerDtls();
        fromCustomerLedgerDtls.setAmount(paymentTransferDTO.getAmount());
     //   fromCustomerLedgerDtls.setCustomer(creditDocument.getCustomer());
        fromCustomerLedgerDtls.setAmount(creditDocument.getAmount());
        if(paymentTransferDTO.getFromChildCustomerId() != null){
            fromCustomerLedgerDtls.setFromId(paymentTransferDTO.getFromChildCustomerId());
        }
        if(paymentTransferDTO.getFromParentCustomerId() != null){
            fromCustomerLedgerDtls.setCustomer(creditDocument.getCustomer());
        }
        fromCustomerLedgerDtls.setCreditdocid(creditDocument.getId());
        fromCustomerLedgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_CATEGORY_TRANSFER);
        fromCustomerLedgerDtls.setTranstype("DR");
        fromCustomerLedgerDtls.setDescription("Outgoing transfer");
        customerLedgerDtlsRepository.save(fromCustomerLedgerDtls);
        /**For To wallet ledger**/
        CustomerLedgerDtls toCustomerLedgerDtls = new CustomerLedgerDtls();
        toCustomerLedgerDtls.setAmount(paymentTransferDTO.getAmount());
     //   toCustomerLedgerDtls.setCustomer(creditDocument.getCustomer());
        toCustomerLedgerDtls.setAmount(creditDocument.getAmount());
        if(paymentTransferDTO.getToChildCustomerId() != null){
            toCustomerLedgerDtls.setToId(paymentTransferDTO.getToChildCustomerId());
        }
        if(paymentTransferDTO.getToParentCustomerId() != null){
            toCustomerLedgerDtls.setCustomer(creditDocument.getCustomer());
        }
        toCustomerLedgerDtls.setCreditdocid(creditDocument.getId());
        toCustomerLedgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_CATEGORY_TRANSFER);
        toCustomerLedgerDtls.setTranstype("CR");
        toCustomerLedgerDtls.setDescription("Incoming transfer");
        customerLedgerDtlsRepository.save(toCustomerLedgerDtls);
        /**Add main ledger entry for transaction**/
        if(paymentTransferDTO.getFromChildCustomerId() != null && paymentTransferDTO.getToChildCustomerId() != null) {
            CustomerLedgerDtls mainCustomerLedgerDtls = new CustomerLedgerDtls();
            mainCustomerLedgerDtls.setCustomer(creditDocument.getCustomer());
            mainCustomerLedgerDtls.setAmount(creditDocument.getAmount());
            mainCustomerLedgerDtls.setCreditdocid(creditDocument.getId());
            mainCustomerLedgerDtls.setTranscategory(Constants.CUSTOMER_LEDGER.TRANS_CATEGORY_TRANSFER);
            mainCustomerLedgerDtls.setTranstype("TR");
            mainCustomerLedgerDtls.setDescription("Transfer between child");
            customerLedgerDtlsRepository.save(mainCustomerLedgerDtls);
        }

    }
    public GenericDataDTO getPaginatedAuditLog(PaginationRequestDTO paginationRequestDTO,Integer custId) {
        if(paginationRequestDTO.getPage() >= 1){
            paginationRequestDTO.setPage(paginationRequestDTO.getPage() - 1);
        }
        Pageable pageable = PageRequest.of(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        Page<PaymentTransferAudit> audits = paymentTransferAuditRepository.getPaymentAuditByCustomerAudit(custId,pageable);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<PaymentAuditDTO> mappedList = audits.stream()
                .map(audit -> PaymentAuditDTO.builder()
                        .fromName(resolveName(audit.getFromParentCustId(), audit.getFromChildCustId(),custId))
                        .toName(resolveName(audit.getToParentCustId(), audit.getToChildCustId(),custId))
                        .amount(audit.getAmount())
                        .createdDate(audit.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
        genericDataDTO = convertPagableResponseToGenericDataDTO(audits , mappedList);
        return genericDataDTO;

    }



    private String resolveName(Integer parentId, Integer childId,Integer mainCustId) {
        if (parentId != null) {
            return customersRepository.findCustomerDataByIdForPayment(parentId)
                    .getCustname();
        } else if (childId != null) {
            return parentChildMappingRepo.findByChildAndParentCustomer(Long.valueOf(childId), mainCustId.longValue())
                    .map(ParentChildMappingRel::getChildFirstName)
                    .orElse("Child#" + childId);
        }
        return "N/A";
    }

    public  GenericDataDTO convertPagableResponseToGenericDataDTO(Page<PaymentTransferAudit> paginationList,List<PaymentAuditDTO> paymentAuditDTOList){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setDataList(paymentAuditDTOList);
        genericDataDTO.setResponseCode(org.springframework.http.HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(org.springframework.http.HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }
}

