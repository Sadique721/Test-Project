package com.savbill.integrationsystem.utility;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class CommonUtilityService {

    private static final long KB_TO_BYTES = 1024L;
    private static final long MB_TO_BYTES = KB_TO_BYTES * 1024L;
    private static final long GB_TO_BYTES = MB_TO_BYTES * 1024L;

    public long calCulateBytes(Double quota, String quotaUnit) {
        long startTime = System.currentTimeMillis();
        switch (quotaUnit.toUpperCase()) {
            case "KB":
                return (long) (quota * 1024);
            case "MB":
                return (long) (quota * MB_TO_BYTES);
            case "GB":
                return (long) (quota * GB_TO_BYTES);
            default:
                return 0l;
        }
    }

    public long calCulateBytesLong(Long quota, String quotaUnit) {
        long startTime = System.currentTimeMillis();
        switch (quotaUnit.toUpperCase()) {
            case "KB":
                return (long) (quota * 1024);
            case "MB":
                return (long) (quota * MB_TO_BYTES);
            case "GB":
                return (long) (quota * GB_TO_BYTES);
            default:
                return 0l;
        }
    }

    public String convertNumberToWord(long number) {
        long startTime = System.currentTimeMillis();
        log.info("convertNumberToWord Method called In Start Time At: {}",new Date(startTime));
        if (number == 0) {
            return "zero";
        }
        StringBuilder result = new StringBuilder();
        // Define the chunks (1,000,000,000, etc)
        String[] units = {"", "thousand", "million", "billion", "trillion"};
        int unitIndex = 0;
        while (number > 0) {
            if (number % 1000 != 0) {
                String part = convertThreeDigitChunkToWord((int) (number % 1000));
                if (unitIndex > 0) {
                    part += " " + units[unitIndex];
                }
                if (result.length() > 0) {
                    result.insert(0, " " + part + " ");
                } else {
                    result.insert(0, part + " ");
                }
            }
            number /= 1000;
            unitIndex++;
        }
        log.info("Completed convertNumberToWord in {} ms for Number: {}", System.currentTimeMillis() - startTime, number);
        return result.toString().trim();
    }

    private static String convertThreeDigitChunkToWord(int number) {
        long startTime = System.currentTimeMillis();
        log.info("convertThreeDigitChunkToWord Method called In Start Time At: {}",new Date(startTime));
        if (number == 0) {
            return "";
        }
        String[] lessThan20 = {"", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
                "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
                "eighteen", "nineteen"};
        String[] tens = {"", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};
        StringBuilder sb = new StringBuilder();
        if (number >= 100) {
            sb.append(lessThan20[number / 100]).append(" hundred");
            number %= 100;
            if (number > 0) {
                sb.append(" and ");
            }
        }
        if (number >= 20) {
            sb.append(tens[number / 10]);
            if (number % 10 != 0) {
                sb.append("-");
            }
            sb.append(lessThan20[number % 10]);
        } else if (number > 0) {
            sb.append(lessThan20[number]);
        }
        log.info("Completed convertThreeDigitChunkToWord in {} ms for Number: {}", System.currentTimeMillis() - startTime, number);
        return sb.toString().trim();
    }

    public String statusValue(Integer status) {
        long startTime = System.currentTimeMillis();
        log.info("statusValue Method called In Start Time At: {}", new Date(startTime));
        String statusName = "";
        switch (status.toString()) {
            case "0":
                statusName = "Subscribed";
                break;
            case "1":
                statusName = "Start Scheduled";
                break;
            case "2":
                statusName = "Active";
                break;
            case "3":
                statusName = "Expiry Scheduled";
                break;
            case "4":
                statusName = "Expired";
                break;
            case "5":
                statusName = "Unsubscribed";
                break;
            case "6":
                statusName = "Approval Pending";
                break;
            case "7":
                statusName = "Rejected";
                break;
        }
        log.info("Completed statusValue in {} ms for status: {}", System.currentTimeMillis() - startTime, status);
        return statusName;
    }

    public long localStartDateTime(String startDateTimeString) {
        long startTime = System.currentTimeMillis();
        log.info("localStartDateTime Method called In Start Time At: {}", new Date(startTime));
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(startDateTimeString);
            log.info("Completed localStartDateTime in {} ms for startDateTimeString: {}", System.currentTimeMillis() - startTime, startDateTimeString);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception e) {
            log.error("Error parsing end time: {}", startDateTimeString, e);
            return 0;
        }
    }
}
