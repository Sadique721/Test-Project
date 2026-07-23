package com.savbill.cpm.service.common;

import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.custAccountProfile.CustAccountProfile;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Service
public class AcctNumCreationService {

    private final StringRedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CustomersService.class);
    //    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CustomersService.class);
    private static final Logger log = LoggerFactory.getLogger(CustomersService.class);

    @Autowired
    CustomersRepository customersRepository;

    public AcctNumCreationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getNewCustomerAccountNo(CustAccountProfile custAccountProfile, Integer mvnoId) throws Exception {
        String result;
        if (custAccountProfile.getType().equalsIgnoreCase("timestamp")) {
            result = timestampType(custAccountProfile.getPrefix(), custAccountProfile.isYear(), custAccountProfile.isMonth(), custAccountProfile.isDay());
            ApplicationLogger.logger.info("Customer number generated via timestamp : " + result);
        } else if (custAccountProfile.getType().equalsIgnoreCase("number")) {
            result = numberType(custAccountProfile.getPrefix(), custAccountProfile.getStartFrom(), mvnoId);
            ApplicationLogger.logger.info("Customer number generated via numbertype: " + result);
        } else {
            ApplicationLogger.logger.error("Provide Specific Profile Type");
            return null;
        }
        return result;
    }

    public String timestampType(String prefix, boolean year, boolean month, boolean day) {
        try {
            long timestamp = CommonUtils.getUniqueNumber();
            LocalDate local = LocalDate.now();
            int count = (year ? 1 : 0) + (month ? 1 : 0) + (day ? 1 : 0);

            // Convert the year to "yy" format
            String yearPart = String.valueOf(local.getYear()).substring(2);

            switch (count) {
                case 3:
                    return prefix + yearPart + local.getMonthValue() + local.getDayOfMonth() + timestamp;
                case 2:
                    if (year && month) return prefix + yearPart + local.getMonthValue() + timestamp;
                    if (year && day) return prefix + yearPart + local.getDayOfMonth() + timestamp;
                    if (month && day) return prefix + local.getMonthValue() + local.getDayOfMonth() + timestamp;
                case 1:
                    if (year) return prefix + yearPart + timestamp;
                    if (month) return prefix + local.getMonthValue() + timestamp;
                    if (day) return prefix + local.getDayOfMonth() + timestamp;
                default:
                    return "No conditions Matched.";
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error in performing for processTimestamp during account number generation...");
            e.getStackTrace();
            return null;
        }
    }

    public String numberType(String prefix, String startFrom, Integer mvnoId) throws Exception {
        String accountNumber = null;
        String number = null;
        try {
            String redisKey = "customerAccountNumber-" + mvnoId;

            // If key doesn't exist, initialize it safely
            // Step 1: Try to initialize atomically
            Boolean initialized = redisTemplate.opsForValue().setIfAbsent(redisKey, "0"); // store numeric ONLY
            Long nextValue;
            if (Boolean.TRUE.equals(initialized)) {
                // First thread initializes from DB
                String latestAccountNum = customersRepository.findLatestCustomerByMvnoId(mvnoId);
                if (latestAccountNum == null || latestAccountNum.isEmpty()) {
                    nextValue = Long.parseLong(startFrom);
                    ApplicationLogger.logger.error("Last Account Num Null, Starts from configuration");
                } else if (latestAccountNum.startsWith(prefix)) {
                    String numericPart = latestAccountNum.substring(prefix.length());
                    nextValue = Long.parseLong(numericPart) + 1;
                    ApplicationLogger.logger.info("Redis: Last Account Num: {}, Numeric Part: {}, New Value: {}",latestAccountNum,numericPart,nextValue);
                } else {
                    ApplicationLogger.logger.error("Last Account Num Prefix does not match with current profile");
                    nextValue = Long.parseLong(startFrom);
                    ApplicationLogger.logger.info("Redis: Last Account Num prefix not match, New Value: {}",latestAccountNum,nextValue);
                }
                redisTemplate.opsForValue().set(redisKey, String.valueOf(nextValue));
            } else {
                // Atomic increment in Redis
                Long newVal = redisTemplate.opsForValue().increment(redisKey, 1);
                // Format with same length as startFrom
                number = String.format("%0" + startFrom.length() + "d", newVal);
            }
            accountNumber = prefix + number;
            ApplicationLogger.logger.info("Redis: New Account Num " + accountNumber);
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error generating account number due to Redis for mvnoId: " + mvnoId, e);

            try {
                // Fallback to DB if Redis is unavailable
                String latestAccountNum = customersRepository.findLatestCustomerByMvnoId(mvnoId);
                if (latestAccountNum == null || latestAccountNum.isEmpty()) {
                    number = startFrom;
                    ApplicationLogger.logger.error("DB: Last Account Num found null so staring from one" +number);
                } else if (latestAccountNum.startsWith(prefix)) {
                    String numericPart = latestAccountNum.substring(prefix.length());
                    Long nextNum = Long.parseLong(numericPart) + 1;
                    number = String.format("%0" + numericPart.length() + "d", nextNum);
                    ApplicationLogger.logger.info("DB: Last Account Num: {}, Numeric Part: {}, New Value: {} " + latestAccountNum,numericPart,number);
                } else {
                    ApplicationLogger.logger.error("Last Account Num Prefix does not match with current profile");
                    return null;
                }
                accountNumber = prefix + number;
                ApplicationLogger.logger.info("DB: New Account Num "+ accountNumber);
            } catch (Exception e1) {
                ApplicationLogger.logger.error("Error generating account number from DB: ", e1);
            }
        }
        return accountNumber;
    }

}
