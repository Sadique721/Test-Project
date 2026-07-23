package com.savbill.radius.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.sift.AppenderFactory;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.Context;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Iterator;

public class LogbackConfig {

    SiftingAppender siftingAppender;
    ch.qos.logback.classic.Logger rootLogger;
    LoggerContext context;

    public void configureLogging(String mdcKey, String logLevel, String keyValue) {
        // Get the logger context
        context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();

        // Create SiftingAppender
        siftingAppender = new SiftingAppender();
        siftingAppender.setContext(context);
        siftingAppender.setName(keyValue);

        // Create MDCBasedDiscriminator based on MDC value (for user or thread)
        MDCBasedDiscriminator discriminator = new MDCBasedDiscriminator();
        discriminator.setContext(context);
        discriminator.setKey(mdcKey);  // Key like 'user' or 'thread'
        discriminator.setDefaultValue("ignore"); // Default value for non-matching keys
        discriminator.start();
        siftingAppender.setDiscriminator(discriminator);

        // Add custom filter for keyValue filtering
        siftingAppender.addFilter(new KeyValueFilter(mdcKey, keyValue));

        // Set up AppenderFactory for dynamic RollingFileAppender
        siftingAppender.setAppenderFactory((context1, discriminatingValue) -> {
            // Create a new RollingFileAppender for each discriminating value
            RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
            rollingFileAppender.setContext(context1);
            rollingFileAppender.setName("FILE-" + discriminatingValue);


            // Path and pattern configuration for log files
            String appLogRoot = System.getProperty("APP_LOG_ROOT", "log");
            String logPattern = "{ \"level\":\"%p\",\"user\":\"%X{userName}\", \"trace-Id\":\"%X{traceId}\",\"span-Id\":\"%X{spanId}\", \"type\":\"%X{type}\", \"timestamp\":\"%d{ISO8601}\", \"module\":\"Savbill-RADIUS\", \"thread\":\"%t\",\"sub-module\":\"%F\", \"line\":\"%L\",\"message\":\"%m\"}%n";

            // Dynamically set the file path for logging, using MDC values
            rollingFileAppender.setFile(appLogRoot + "/" + mdcKey + "-" + discriminatingValue + ".log");

            // Create and set the PatternLayoutEncoder
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context1);
            encoder.setPattern(logPattern);
            encoder.start();
            rollingFileAppender.setEncoder(encoder);

            ThresholdFilter thresholdFilter = new ThresholdFilter();
            thresholdFilter.setContext(context1);
            thresholdFilter.setLevel(logLevel); // Set the threshold level dynamically
            thresholdFilter.start();

            // Add the ThresholdFilter to the RollingFileAppender
            rollingFileAppender.addFilter(thresholdFilter);

            // Rolling Policy setup for size and time-based rolling
            SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
            rollingPolicy.setContext(context1);
            rollingPolicy.setFileNamePattern(appLogRoot + "/archived/" + mdcKey + "-" + discriminatingValue + ".%d{yyyy-MM-dd}.%i.log");
            rollingPolicy.setMaxFileSize(FileSize.valueOf("500MB"));
            rollingPolicy.setTotalSizeCap(FileSize.valueOf("1GB"));
            rollingPolicy.setMaxHistory(5);
            rollingPolicy.setParent(rollingFileAppender);
            rollingPolicy.start();
            rollingFileAppender.setRollingPolicy(rollingPolicy);

            // Start the RollingFileAppender
            rollingFileAppender.start();

            return rollingFileAppender;
        });

        // Start SiftingAppender
        siftingAppender.start();

        // Add the SiftingAppender to the root logger
        rootLogger =
                (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(siftingAppender);
        rootLogger.setLevel(Level.INFO);
        rootLogger.setAdditive(false);

    }

    public void stopAllLogging() {
        try {
            Iterator<Appender<ILoggingEvent>> appenderIterator = rootLogger.iteratorForAppenders();
            while (appenderIterator.hasNext()) {
                Appender<ILoggingEvent> next = appenderIterator.next();
                if (!"savbillradiuslog-async".equalsIgnoreCase(next.getName())) {
                    rootLogger.detachAppender(next);
                    next.stop();
                }
            }
        } catch (NullPointerException e) {
            System.out.println("No Active Appender");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (siftingAppender != null) {
//            rootLogger.detachAppender("SIFT");
//            // Stop the appender to release resources
//            String appendName =  siftingAppender.getDiscriminatorKey();
//            System.out.println("SiftingAppender stopped and removed.");
//            return "Logging stopped for appender "+ appendName;
//        } else {
//            System.out.println("SiftingAppender not found.");
//            return "No appender found";
//        }
    }

    public ResponseEntity getAllActiveAppender() {
        ArrayList<String> appNames = new ArrayList<>();
        Iterator<Appender<ILoggingEvent>> appenderIterator;
        try {
            appenderIterator = rootLogger.iteratorForAppenders();
            while (appenderIterator.hasNext()) {
                Appender<ILoggingEvent> next = appenderIterator.next();
                appNames.add(next.getName());
            }
            return ResponseEntity.status(200).body(appNames);
        } catch (NullPointerException e) {
            return ResponseEntity.status(200).body("No Active Appender found");
        } catch (Exception e) {
            return ResponseEntity.status(200).body("Exception Occurred while getting Active appender");
        }
    }

    public String stopLogging(String key) {

        try {
            siftingAppender = (SiftingAppender) rootLogger.getAppender(key);
            if (siftingAppender != null) {
                rootLogger.detachAppender(key);

                // Stop the appender to release resources
                String appendName = siftingAppender.getName();
                siftingAppender.stop();
                System.out.println("SiftingAppender stopped and removed.");
                return "Logging stopped for appender " + appendName;
            } else {
                System.out.println("SiftingAppender not found.");
                return "No appender found";
            }
        } catch (NullPointerException e) {
            System.out.println("No Active Appender");
            return "No appender found";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception Occured:: " + e.getMessage();
        }
    }


    // Custom filter for filtering based on MDC value (keyValue)
    class KeyValueFilter extends Filter<ILoggingEvent> {
        private final String mdcKey;
        private final String keyValue;

        public KeyValueFilter(String mdcKey, String keyValue) {
            this.mdcKey = mdcKey;
            this.keyValue = keyValue;
        }

        @Override
        public FilterReply decide(ILoggingEvent event) {
            String mdcValue = event.getMDCPropertyMap().get(mdcKey);
            if (mdcValue != null && mdcValue.equals(keyValue)) {
                return FilterReply.ACCEPT; // Accept the log event if MDC value matches the keyValue
            } else {
                return FilterReply.DENY; // Deny the log event if MDC value does not match
            }
        }
    }
}