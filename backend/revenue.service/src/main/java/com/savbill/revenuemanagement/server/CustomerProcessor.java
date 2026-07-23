package com.savbill.revenuemanagement.server;

import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@NoArgsConstructor
public class CustomerProcessor implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(InvoiceProcessor.class);

    private CustomerBillingMessage msg;
    private MessageReceiverWithThread messageReceiverWithThread;

    private Customers customers;
    @PersistenceContext
    EntityManager entityManager;



    public CustomerProcessor(MessageReceiverWithThread messageReceiverWithThread, CustomerBillingMessage customerBillingMessage, Customers customers)
    {
        this.messageReceiverWithThread=messageReceiverWithThread;
        this.msg=customerBillingMessage;
        this.customers=customers;
    }

    @Override
    public void run() {
        messageReceiverWithThread.processMessage(msg,customers);
    }
}
