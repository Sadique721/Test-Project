package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.core.mapper.customer.CustomerMapper;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper()
public abstract class CreditDocumentMapper implements IBaseMapper<PaymentHistoryDTO, CreditDocument> {

    @Mapping(source = "customer", target = "custId")
    @Mapping(source = "reciptNo", target = "receiptNo")
    @Mapping(source = "type", target="type")
    @Mapping(source = "nextTeamHierarchyMappingId",target = "nextTeamHierarchyMappingId")
    public abstract PaymentHistoryDTO domainToDTO(CreditDocument data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "custId", target = "customer")
    @Mapping(source = "receiptNo", target = "referenceno")
    @Mapping(source = "type", target="type")

    public abstract CreditDocument dtoToDomain(PaymentHistoryDTO dtoData, @Context CycleAvoidingMappingContext context);

    @Autowired
    SubscriberService customersService;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    CreditDocument fromId(Integer id) {
        if (id == null) {
            return null;
        }
        final CreditDocument creditDocument = new CreditDocument();
        creditDocument.setId(id);
        return creditDocument;
    }

    Integer fromCustomers(Customers customers) {
        return customers == null ? null : customers.getId();
    }

    @Autowired
    CustomersRepository customersRepository;

    Customers fromCustomerId(Integer custId) {
        if (custId == null) {
            return null;
        }
        Customers entity = null;
        try {
            entity = customersService.get(custId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    public void loadPaymentHistory(CreditDocument domain, @MappingTarget PaymentHistoryDTO dto) {
//        String status = creditDocRepository.getOne(domain.getId()).getStatus();
//        if (status.equalsIgnoreCase(SubscriberConstants.PAYMENT_STATUS_APPROVED)) {
//            dto.setStatus("complete");
//        }
        StaffUser staffUser = null;
        if (domain.getCreatedById() != null && domain.getCreatedById() != 0) {
            staffUser = staffUserRepository.getOne(domain.getCreatedById());
            dto.setPaymentBy(staffUser.getUsername());
        }
        if (dto.getPaymentBy()==null && domain.getCreatedByName()!=null){
            dto.setPaymentBy(domain.getCreatedByName());
        }

    }
}
