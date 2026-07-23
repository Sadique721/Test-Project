package com.savbill.revenuemanagement.tasks;

import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.SaveCustomerDataShareMessage;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class InvoiceThread extends CustomMainThread {


    private SubscriberService subscriberService;

    private SaveCustomerDataShareMessage saveCustomerDataShareMessage;

    private MessageReceiverWithThread messageReceiverWithThread;

    public InvoiceThread(SaveCustomerDataShareMessage saveCustomerDataShareMessage, SubscriberService subscriberService, MessageReceiverWithThread messageReceiverWithThread) {
        this.saveCustomerDataShareMessage = saveCustomerDataShareMessage;
        this.subscriberService = subscriberService;
        this.messageReceiverWithThread = messageReceiverWithThread;
    }

    /**
     * <h3>process method is used to process index based search request on given parameter.</h3>
     *
     * @throws ParseException       if error occurred while parsing data.
     * @throws ExecutionException   if error occurred while getting element from future.
     * @throws InterruptedException if error occurred while getting element from future.
     * @throws IOException          if error occurred while writing result in json file.
     */
    @Override
    public void process() throws ParseException, ExecutionException, InterruptedException, IOException {

    }

    /**
     * <h3>getPriority method is used to get task priority.</h3>
     *
     * @return task priority.
     */
    @Override
    public int getPriority() {
        return 0;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
//        log.info("*********message start: " + LocalDateTime.now() + " in milli: " + new Date().getTime());
//        log.info("Load Test Received Message From RabbitMq : <" + saveCustomerDataShareMessage + ">");
//        log.info("*********message customer start: " + LocalDateTime.now() + " in milli: " + new Date().getTime());
        Customers customers = subscriberService.saveCustomersData(saveCustomerDataShareMessage);
//        log.info("*********message customer end: " + LocalDateTime.now() + " in milli: " + new Date().getTime());
        if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.PREPAID) && !customers.getIstrialplan()) {
//            log.info("Load Test saveCustomersmessage : <" + customers + ">");
            CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
            Map<String, Object> data = new HashMap<>();
            data.put(CustomerBillingMessage.CUST_ID, customers.getId());
            data.put("Bullable_CUST_ID",customers.getPlanMappingList().get(0).getBillableCustomerId());
            data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, saveCustomerDataShareMessage.getCreatedById());
            customerBillingMessage.setData(data);
            if (customers.getStatus().equalsIgnoreCase("NewActivation")) {
                customerBillingMessage.setType(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER);
            } else {
                customerBillingMessage.setType(Constants.INVOICE_TYPE.CREATE_CUSTOMER);
            }
            if(saveCustomerDataShareMessage.getRecordPaymentPojo() != null){
                RecordPaymentPojo recordPaymentPojo = saveCustomerDataShareMessage.getRecordPaymentPojo();
                recordPaymentPojo.setCustomerid(customers.getId());
                customerBillingMessage.setRecordPaymentDTO(recordPaymentPojo);
                data.put(CustomerBillingMessage.MVNOID , customers.getMvnoId());
            }
            messageReceiverWithThread.processMessage(customerBillingMessage,null);
        }
//        log.info("*********message end: " + LocalDateTime.now() + " in milli: " + new Date().getTime());

    }
}
