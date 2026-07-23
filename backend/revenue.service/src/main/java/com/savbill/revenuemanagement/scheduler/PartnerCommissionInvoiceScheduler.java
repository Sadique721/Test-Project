package com.savbill.revenuemanagement.scheduler;


import com.savbill.revenuemanagement.core.service.partner.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component
public class PartnerCommissionInvoiceScheduler {

    @Autowired
    private PartnerService partnerService;

    @Scheduled(cron ="${cronJobTimeForPartnerCommissionInvoice}}")
    public void generatePartnerCommissionInvoice()
    {
        LocalDate startOfMonth = LocalDate.now().minusMonths(12).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = LocalDate.now();
        LocalDate nextBillDate=LocalDate.now();
        partnerService.generatePartnerCommissionInvoice(nextBillDate,startOfMonth,endOfMonth,null);
    }
}
