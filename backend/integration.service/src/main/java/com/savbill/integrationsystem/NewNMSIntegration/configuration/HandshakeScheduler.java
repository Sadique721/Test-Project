//package com.savbill.integrationsystem.NMSConfiguration;
//
//import com.savbill.integrationsystem.nms.service.NMSIntegrationService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//@Configuration
//@EnableScheduling
//public class HandshakeScheduler {
//
//
//
//    @Autowired
//    private NMSIntegrationService nmsIntegrationService;
//
//    private String jwtToken;
//
//    private static final Logger logger = LoggerFactory.getLogger(HandshakeScheduler.class);
//
//    @Scheduled(fixedRate = 1800000)
//    public void maintainAccessToken() {
//        if (jwtToken == null || jwtToken.isEmpty()) {
//            jwtToken = nmsIntegrationService.getJwtTokenfromUrl("8017", "userName", "value", "grantType", "102.209.109.2");
//            if (jwtToken == null) {
//                logger.error("Failed to fetch access token. Handshake skipped.");
//                return;
//            }
//        }
//
////        boolean success = authLogInService.performHandshake(jwtToken);
////        if (!success) {
////          logger.error("Handshake failed. Fetching a new token...");
////            jwtToken = authLogInService.getJwtTokenfromUrl("8017", "userName", "value", "grantType", "102.209.109.2");
////        }
////    }
//    }
//}
