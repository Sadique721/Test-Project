package com.diameter.schedulers;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.diameter.repository.CustomerRepository;

@Component
public class QuotaResetScheduler {

    private static final Logger logger = LoggerFactory.getLogger(QuotaResetScheduler.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Scheduled(cron = "${info.app.quotaResetCron:0 0 0 * * *}")
    public void resetDailyQuotas() {
        logger.info(">>> Daily Quota Reset Scheduler triggered at {}", LocalDateTime.now());
        try {
            int rowsUpdated = customerRepository.resetDailyQuotas();
            logger.info("Daily Quota Reset completed. Total records reset: {}", rowsUpdated);
        } catch (Exception e) {
            logger.error("Daily Quota Reset Scheduler failed: {}", e.getMessage(), e);
        }
    }
}
