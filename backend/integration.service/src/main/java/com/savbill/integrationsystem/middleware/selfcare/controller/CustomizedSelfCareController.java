package com.savbill.integrationsystem.middleware.selfcare.controller;


import com.savbill.integrationsystem.middleware.selfcare.model.*;
import com.savbill.integrationsystem.middleware.selfcare.model.*;
import com.savbill.integrationsystem.middleware.selfcare.service.SelfCarePaymentDetailsService;
import com.savbill.integrationsystem.middleware.selfcare.service.SelfCareTicketService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController(value = "CustomizedSelfCareController2")
@RequestMapping()
public class CustomizedSelfCareController {

    @Autowired
    SelfCarePaymentDetailsService selfCarePaymentDetailsService;

    @Autowired
    SelfCareTicketService selfCareTicketService;
 
    String getModuleNameForLog() {
        return "CustomizedSelfCareController[]";
    }

    private static final Logger logger = LoggerFactory.getLogger(CustomizedSelfCareController.class);

    @GetMapping(value = "/api/selfCare/category")
    public Map<String, Object>  selfCareCategory() {
        MDC.put("type", "Fetch");
        System.out.println("================== Request for SelfCare Category ==================\n");
        try {
            String responsePacket="{\"CategoryList\": [ { \"Id\": 10113,\"Name\": \"Technical Support Center\"},{\"Id\": 10132,\"Name\": \"Sales Call Center\"}]}";
            JSONObject jsonObject = new JSONObject(responsePacket);
            return jsonObject.toMap();
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
        }
        MDC.remove("type");
        return null;
    }

    @GetMapping(value = "/api/selfCare/subCategory/{categoryID}")
    public Map<String, Object>  selfCareSubCategory(@PathVariable double categoryID) {
        MDC.put("type", "Fetch");
        System.out.println("================== Request for SelfCare Sub Category ==================\n");
        try {
            String responsePacket="{\n" +
                    "  \"SubCategoryList\": [\n" +
                    "    {\n" +
                    "      \"Id\": 10115,\n" +
                    "      \"Name\": \"FTTH/X\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"Id\": 10116,\n" +
                    "      \"Name\": \"Clear TV\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            JSONObject jsonObject = new JSONObject(responsePacket);
            return jsonObject.toMap();
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
        }
        MDC.remove("type");
        return null;
    }

    @GetMapping(value = "/api/selfCare/case/{username}")
    public ResponseEntity<?> selfCareCase (@PathVariable String username) {
        System.out.println("================== Request for SelfCare Case for User: " + username + " ==================\n");
        OpenAndCloseCaseDetails caseDetails;
        try {
            caseDetails = selfCareTicketService.getOpenAndCloseCaseDetails(username);
            return new ResponseEntity<>(caseDetails, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            return new ResponseEntity<>(e.toString(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/api/selfCare/paymentDetails/{username}")
    public ResponseEntity<?> selfCarePaymentDetails (@PathVariable String username) {
        MDC.put("type", "Fetch");
        System.out.println("================== Request for Payment Details for user: " + username + " ==================\n");
        try {
            List<PaymentDetails> paymentDetailsList;
            paymentDetailsList = selfCarePaymentDetailsService.getPaymentDetailsByUserName(username);
            return new ResponseEntity<>(paymentDetailsList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            return new ResponseEntity<>(e.toString(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/api/selfCare/priorities")
    public ResponseEntity<?> selfCarePriorities () {
        System.out.println("================== Request for Case priorities ==================\n");
        ArrayList<Priorities> listdata = new ArrayList<>();
        try {
            Priorities lowPriority = new Priorities();
            lowPriority.setId(105);
            lowPriority.setName("Low");
            listdata.add(lowPriority);

            Priorities mediumPriority = new Priorities();
            mediumPriority.setId(106);
            mediumPriority.setName("Medium");
            listdata.add(mediumPriority);

            Priorities highPriority = new Priorities();
            highPriority.setId(107);
            highPriority.setName("High");
            listdata.add(highPriority);

            return new ResponseEntity<>(listdata, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            return new ResponseEntity<>(e.toString(), HttpStatus.EXPECTATION_FAILED);
        }

    }

    @GetMapping(value = "/api/selfCare/status")
    public ResponseEntity<?> selfCareStatus () {
        System.out.println("================== Request for Case Status ==================\n");
        ArrayList<Status> listdata = new ArrayList<>();
        try {
            Status open = new Status();
            open.setId(100);
            open.setName("Open");
            listdata.add(open);

            Status inProgress = new Status();
            inProgress.setId(104);
            inProgress.setName("In Progress");
            listdata.add(inProgress);

            Status resolved = new Status();
            resolved.setId(129);
            resolved.setName("Resolved");
            listdata.add(resolved);

            Status reOpen = new Status();
            reOpen.setId(102);
            reOpen.setName("Re-open");
            listdata.add(reOpen);

            Status raiseAndClose = new Status();
            raiseAndClose.setId(509);
            raiseAndClose.setName("Raise and Close");
            listdata.add(raiseAndClose);

            Status closed = new Status();
            closed.setId(101);
            closed.setName("Closed");
            listdata.add(closed);

            Status followup = new Status();
            followup.setId(510);
            followup.setName("Follow Up");
            listdata.add(followup);

            Status outOfDomain = new Status();
            outOfDomain.setId(508);
            outOfDomain.setName("Out of domain");
            listdata.add(outOfDomain);

            Status onHold = new Status();
            onHold.setId(507);
            onHold.setName("On Hold");
            listdata.add(onHold);

            Status pending = new Status();
            pending.setId(103);
            pending.setName("Pending");
            listdata.add(pending);

            return new ResponseEntity<>(listdata, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            return new ResponseEntity<>(e.toString(), HttpStatus.EXPECTATION_FAILED);
        }

    }
    @PostMapping(value = "/api/selfCare/case")
    public ResponseEntity<?> selfCareCase (@RequestBody TicketRequest ticketRequest) {
        System.out.println("================== Request for Case creation ==================\n");
        TicketResponse ticketResponse = new TicketResponse();
        try {
            if (ticketRequest.getUserId() != null) {
                ticketResponse = selfCareTicketService.saveSelfCareTicket(ticketRequest);
                return new ResponseEntity<>(ticketResponse, HttpStatus.OK);
            }
            return new ResponseEntity<>(ticketResponse, HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            return new ResponseEntity<>("Error while saving data.", HttpStatus.EXPECTATION_FAILED);
        }
    }
}
