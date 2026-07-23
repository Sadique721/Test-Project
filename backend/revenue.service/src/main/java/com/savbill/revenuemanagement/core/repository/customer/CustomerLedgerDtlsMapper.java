package com.savbill.revenuemanagement.core.repository.customer;


import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedgerDtls;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.core.mapper.customer.CustomerMapper;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public abstract class CustomerLedgerDtlsMapper implements IBaseMapper<CustomerLedgerDtlsPojo, CustomerLedgerDtls> {

//    @Mapping(source = "customer", target = "custId")
//    @Mapping(source = "data.CREATE_DATE", target = "CREATE_DATE")
//    @Mapping(source = "data.END_DATE", target = "END_DATE")
    public abstract CustomerLedgerDtlsPojo domainToDTO(CustomerLedgerDtls data, @Context CycleAvoidingMappingContext context);

//    @Mapping(source = "custId", target = "customer")
    public abstract CustomerLedgerDtls dtoToDomain(CustomerLedgerDtlsPojo data, @Context CycleAvoidingMappingContext context);
    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;
//    @Autowired
//    private CustomerLedgerInfoPojo infoPojo;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

//    @Autowired
//    private CustomersService customersService;

    Integer fromCustomersToId(Customers entity) {
        return entity == null ? null : entity.getId();
    }

//    Customers fromCustId(Integer custId) {
//        if (custId == null) {
//            return null;
//        }
//        Customers customers = customersService.get(custId);
//        return customers;
//    }

    LocalDate fromCreateDateTimeToCreateDate(LocalDateTime entity) {
        if (entity == null) {
            return null;
        } else {
            return entity.toLocalDate();
        }
    }

    LocalDateTime fromCreateDateToCreateDateTime(LocalDate entity) {
        if (entity == null) {
            return null;
        } else {
            return entity.atStartOfDay();
        }
    }



    @AfterMapping
    public void loadCreditAmount(CustomerLedgerDtls domain, @MappingTarget CustomerLedgerDtlsPojo pojo) {

        if (domain.getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)) {
            if (domain.getCreditdocid() != null) {
                CreditDocument creditDocuments = creditDocRepository.findLightCreditDocumentById(domain.getCreditdocid());
                List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(creditDocuments.getId());
                List<String> invoiceNumbers = new ArrayList<>();
                if(domain.getTranscategory().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.ADJUSTMENT) && domain.getDebitdocid() != null){
                    debitDocRepository.findById(domain.getDebitdocid()).ifPresent(debitDocument -> invoiceNumbers.add(debitDocument.getDocnumber()));
                    pojo.setInvoiceNo(invoiceNumbers);
                }else{
                    if (creditDebitDocMappingList.size() > 0) {
                        for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappingList) {
                            if (creditDebitDocMapping.getDebtDocId() != null) {
                                debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).ifPresent(debitDocument -> invoiceNumbers.add(debitDocument.getDocnumber()));
                                pojo.setInvoiceNo(invoiceNumbers);
                            }
                        }
                    } else {
                        invoiceNumbers.add("Advance");
                        pojo.setInvoiceNo(invoiceNumbers);
                    }
                }
                pojo.setRefNo(creditDocuments.getId());
                pojo.setReceiptNo(creditDocuments.getCreditdocumentno());
                pojo.setRemarks(creditDocuments.getRemarks());
                pojo.setCategory(creditDocuments.getPaytype());
                if(pojo.getPaymentRefNo() == null) {
                    if(creditDocuments.getReciptNo() != null){
                        pojo.setPaymentRefNo(creditDocuments.getReciptNo());
                    }
                }
            }

        }
        if (domain.getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)) {
            if (domain.getDebitdocid() != null) {
                List<String> invoiceNumbers = new ArrayList<>();
                DebitDocument debitDocument = debitDocRepository.findById(domain.getDebitdocid())
                        .orElseThrow(() -> new EntityNotFoundException("DebitDocument not found"));

                invoiceNumbers.add(debitDocument.getDocnumber());
                pojo.setRefNo(debitDocument.getId());
                pojo.setInvoiceNo(invoiceNumbers);
                if(domain.getDescription() != null) {
                    pojo.setRemarks(domain.getDescription());
                }

            } else if (domain.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_REFUND)) {
                CreditDocument creditDocuments = creditDocRepository.getOne(domain.getCreditdocid());
                pojo.setReceiptNo(creditDocuments.getReferenceno());
                pojo.setRemarks(creditDocuments.getRemarks());
                pojo.setCategory(creditDocuments.getPaytype());
            }

        }
        if(!domain.getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT) && !domain.getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)){
            pojo.setRemarks(domain.getDescription());
        }
    }
}
