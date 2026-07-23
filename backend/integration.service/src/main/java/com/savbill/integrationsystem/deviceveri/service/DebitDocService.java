package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.Services.ServicesRepository;
import com.savbill.integrationsystem.billgen.entity.CreditDebitDocMapping;
import com.savbill.integrationsystem.billgen.entity.CreditDocumentData;
import com.savbill.integrationsystem.billgen.entity.QCreditDebitDocMapping;
import com.savbill.integrationsystem.billgen.repository.CreditDebtMappingRepository;
import com.savbill.integrationsystem.billgen.repository.CreditDocRepocitory;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.*;
import com.savbill.integrationsystem.deviceveri.domain.CustomerPackageRelData;
import com.savbill.integrationsystem.deviceveri.domain.DebitDocumentData;
import com.savbill.integrationsystem.deviceveri.dto.PaymentDetail;
import com.savbill.integrationsystem.deviceveri.mapper.DebitDocMapper;
import com.savbill.integrationsystem.deviceveri.model.CustomerPackageRelDTO;
import com.savbill.integrationsystem.deviceveri.model.CustomersDTO;
import com.savbill.integrationsystem.deviceveri.model.DebitDocDTO;
import com.savbill.integrationsystem.deviceveri.repository.CustomerPackckageRelRepo;
import com.savbill.integrationsystem.deviceveri.repository.DebitDocRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebitDocService extends ExBaseAbstractService<DebitDocDTO, DebitDocumentData, Long> {


    @Autowired
    private DebitDocRepo repo;

    @Autowired
    private DebitDocMapper mapper;

    @Autowired
    private CreditDocRepocitory creditDocRepocitory;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    CustomerPackageRelService customerPackageRelService;

    @Autowired
    ServicesRepository servicesRepository;

    @Autowired
    CustomerPackckageRelRepo packckageRelRepo;


    public DebitDocService(DebitDocRepo repo, DebitDocMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "DebitDocumentService[]";
    }
    
    public List<DebitDocDTO> findByCustpackrelidAndIsDelete(Long custpackrelid, Integer isDeleted){
    	List<DebitDocumentData> list = repo.findByCustpackrelidAndIsDelete(custpackrelid, 0);
        List<DebitDocDTO> listDTO = list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    	return listDTO;
    }
    
    public List<DebitDocDTO> findByDebitdocumentidAndIsDelete(Long debitdocumentid, Integer isDeleted){
    	List<DebitDocumentData> list = repo.findByDebitdocumentidAndIsDelete(debitdocumentid, 0);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<DebitDocDTO> findByInventoryMappingIdAndIsDelete(Long inventoryMappingId, Integer isDelete){
    	List<DebitDocumentData> list = repo.findByDebitdocumentidAndIsDelete(inventoryMappingId, 0);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }


    public PaymentDetail getLatestPayment(List<CustomerPackageRelDTO> customerPackageRelDTOS, Long inventoryMappingId, String custId,List<Long> serviceIds) {
        List<Integer> debitDocIds=new ArrayList<>();
        PaymentDetail paymentDetail = new PaymentDetail();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<CustomersDTO> childCustomerList=customersService.findByParentcustid(custId);
        if(!CollectionUtils.isEmpty(childCustomerList))
        {
            List<String> serviceNameList=servicesRepository.findByServiceIds(serviceIds);
            childCustomerList.stream().forEach(record->{
                List<Integer> childDebitDocIds=getAllChildDebitDocIdsForGivenServiceIds(record.getCustid(),serviceNameList);
                if(childDebitDocIds!=null && !CollectionUtils.isEmpty(childDebitDocIds))
                    debitDocIds.addAll(childDebitDocIds);
            });
        }
        try {

            debitDocIds.addAll(customerPackageRelDTOS.stream().filter(x -> x.getDebitdocid() != null).map(x -> x.getDebitdocid().intValue()).distinct().collect(Collectors.toList()));
            List<DebitDocumentData> list = repo.findBySubscriberid(Long.parseLong(custId));
            List<Integer> debitDocIdListForInventory = list.stream().filter(x -> (x.getInventoryMappingId() != null && x.getInventoryMappingId().equals(inventoryMappingId))).map(x -> x.getDebitdocumentid().intValue()).distinct().collect(Collectors.toList());

            debitDocIds.addAll(debitDocIdListForInventory);

            QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
            BooleanExpression expression = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.debtDocId.in(debitDocIds));
            List<CreditDebitDocMapping> creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(expression);
            if (!CollectionUtils.isEmpty(creditDebitDocMappings)) {
                creditDebitDocMappings = creditDebitDocMappings.stream().filter(x -> (x.getAdjustedAmount() != null && x.getAdjustedAmount() > 0)).collect(Collectors.toList());
                List<Integer> creditDocIds = creditDebitDocMappings.stream().map(x -> x.getCreditDocId()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(creditDocIds)) {
                    List<CreditDocumentData> creditDocuments = creditDocRepocitory.findAllByCreditDocIdsInCreatedDateDescOrder(creditDocIds);
                    if (!CollectionUtils.isEmpty(creditDocuments)) {
                        creditDocuments = creditDocuments.stream().filter(x -> x.getType().equalsIgnoreCase("PAYMENT")).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(creditDocuments)) {
                            List<CreditDocumentData> finalCreditDocuments = creditDocuments;
                            creditDebitDocMappings = creditDebitDocMappings.stream().filter(x -> x.getCreditDocId().equals(finalCreditDocuments.get(0).getId())).collect(Collectors.toList());
                            paymentDetail.setLatestPaymentDate(LocalDateTime.parse(creditDocuments.get(0).getCreatedate(), formatter));
                            paymentDetail.setLatestPaymentAmount(creditDebitDocMappings.stream().mapToDouble(x -> x.getAdjustedAmount()).max().getAsDouble());
                        }
                    }
                }
            }
            return paymentDetail;
        }catch (Exception e) {
            return paymentDetail;
        }
    }


    public List<Integer> getAllChildDebitDocIdsForGivenServiceIds(Long childCustomerId, List<String> serviceNameList)
    {
        List<Integer> debitDocIds=new ArrayList<>();
        List<CustomerPackageRelData> packageRelDTOS=packckageRelRepo.findByCustomerIdAndServiceNameListLong(serviceNameList,childCustomerId,"Group");
        List<Integer> ids=packageRelDTOS.stream().filter(x -> x.getDebitdocid() != null).map(x -> x.getDebitdocid().intValue()).distinct().collect(Collectors.toList());
        if(ids!=null && !ids.isEmpty())
            debitDocIds.addAll(ids);
        return debitDocIds;
    }
}
