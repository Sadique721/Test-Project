package com.savbill.radius.utils;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SavbillRadius/userlog")
public class LoggerController {
    LogbackConfig logbackConfig = new LogbackConfig();

    @GetMapping("/startLogging")
    public String startLogging(@RequestParam String key, @RequestParam String value, @RequestParam String logLevel) {
        // Set MDC key-value dynamically
        MDC.put(key, value);

        // Configure logging dynamically based on MDC key-value and log level
        logbackConfig.configureLogging(key, logLevel, value);

        // Return confirmation message
        return "Logging started with MDC key-value: " + key + "=" + value + " and log level: " + logLevel;
    }

    @GetMapping("/getAllLogging")
    public ResponseEntity getAllLogging() {
        return logbackConfig.getAllActiveAppender();
    }


    @GetMapping("/stopLogging")
    public String startLogging(@RequestParam String key) {
        return logbackConfig.stopLogging(key);
    }

    @GetMapping("/stopAllLogging")
    public String startLogging() {
        logbackConfig.stopAllLogging();
        return "Logging stopped for All appender.";
    }

}