package com.savbill.revenuemanagement.core.schedulers.autoSchedulersConfig;

import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.schedulers.ScheduledTask;
import com.savbill.revenuemanagement.core.schedulers.SchedulerManagement;
import com.savbill.revenuemanagement.core.service.ExportInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(Constants.SCHEDULERS_NAME.AUTO_INVOICE_NOTIFICATION)
public class InvoiceNotification implements ScheduledTask {

    @Autowired
    private ExportInvoiceService exportInvoiceService;
    @Override
    public void execute(SchedulerManagement schedulerManagement) {
        exportInvoiceService.startInvoiceNotificationThread(true, null);
    }
}
