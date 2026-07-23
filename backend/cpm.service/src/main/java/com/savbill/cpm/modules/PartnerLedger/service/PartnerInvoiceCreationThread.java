package com.savbill.cpm.modules.PartnerLedger.service;

import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.PartnerBillingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class PartnerInvoiceCreationThread implements Runnable{
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    private LocalDate invoiceDate;
    MessageSender messageSender;

    public PartnerInvoiceCreationThread(LocalDate invoiceDate,MessageSender messageSender) {
        this.invoiceDate=invoiceDate;
        this.messageSender=messageSender;
    }

    @Override
    public void run() {
        generateInvoice(this.invoiceDate);
    }

    public void generateInvoice(LocalDate invoiceDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String invoiceDateString = invoiceDate.format(formatter);
            PartnerBillingMessage message = new PartnerBillingMessage(invoiceDateString);
            kafkaMessageSender.send(new KafkaMessageData(message, PartnerBillingMessage.class.getSimpleName()));
//            messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
