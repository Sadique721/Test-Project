package com.savbill.revenuemanagement.server;

import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMapppingRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.ChangePlanMessage;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class InvoiceProcessor implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(InvoiceProcessor.class);

    private CustomerBillingMessage msg;

    private CustPlanMapppingRepository custPlanMappingRepository;
    private PrepaidInvoiceService prepaidInvoiceService;
    private MessageReceiverWithThread messageReceiverWithThread;
    private LocalDate billDate;
    private CustomersRepository customersRepository;

    @PersistenceContext
    EntityManager entityManager;


    public InvoiceProcessor(CustomerBillingMessage msg, CustPlanMapppingRepository custPlanMappingRepository, PrepaidInvoiceService prepaidInvoiceService, MessageReceiverWithThread messageReceiverWithThread, LocalDate billDate,CustomersRepository customersRepository) {
        this.msg = msg;
        this.custPlanMappingRepository = custPlanMappingRepository;
        this.prepaidInvoiceService = prepaidInvoiceService;
        this.messageReceiverWithThread = messageReceiverWithThread;
        this.billDate = billDate;
        this.customersRepository=customersRepository;
    }

    @Override
    public void run() {
        msg.getData().put(CustomerBillingMessage.POSTPAIDADVANCE,"Both");
        Map<String, Object> datas = msg.getData();
        msg.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
        Integer custId = (Integer) datas.get(CustomerBillingMessage.CUST_ID);
        List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByCustomerIdAndEndDate(custId,billDate.atStartOfDay().minusSeconds(1));
        List<Integer> activeCprIds = custPlanMapppings.stream().map(i->i.getId()).collect(Collectors.toList());
        if (custPlanMapppings.size()>0){
            custPlanMapppings = custPlanMapppings.stream().peek(x->x.setCustPlanStatus("STOP")).collect(Collectors.toList());
            custPlanMappingRepository.saveAll(custPlanMapppings);
            ChangePlanMessage changePlanMessage = new ChangePlanMessage();
            changePlanMessage.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
            prepaidInvoiceService.createInvoiceForPostpaidChangePlanProrate(custId.intValue(),changePlanMessage,activeCprIds, billDate);
        }
        msg.setType("Scheduler");
        msg.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
        msg.setIsEarlyBillDate(true);
        messageReceiverWithThread.receiveBillingInvoiceMessageScheduler(msg,null);
    }
}
