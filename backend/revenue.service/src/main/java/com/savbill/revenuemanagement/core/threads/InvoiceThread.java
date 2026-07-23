package com.savbill.revenuemanagement.core.threads;

import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@NoArgsConstructor
@Service
public class InvoiceThread implements Runnable{

    private static final Log logger = LogFactory.getLog(InvoiceThread.class);
    private CustomerBillingMessage message;

    private LocalDateTime currentDate;

    private PrepaidInvoiceService prepaidInvoiceService;

    public InvoiceThread(final CustomerBillingMessage message, PrepaidInvoiceService prepaidInvoiceService, LocalDateTime currentDate) {
        this.message = message;
        this.currentDate = currentDate;
        this.prepaidInvoiceService = prepaidInvoiceService;
    }

    @Override
    public void run() {
        logger.info("----------------------------Message receive for billing invoice: "+message.getMessageId()+" currentDate: "+currentDate);
        prepaidInvoiceService.createPrepaidInvoice(message,null);
    }
}
