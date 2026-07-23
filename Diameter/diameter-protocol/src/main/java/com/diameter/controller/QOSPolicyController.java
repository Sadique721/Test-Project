package com.diameter.controller;

import java.util.List;

import javax.xml.bind.ValidationException;

import com.diameter.model.QOSPolicyGatewayMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diameter.model.QOSPolicy;
import com.diameter.service.QOSPolicyService;

@RestController
@RequestMapping("/qos")
public class QOSPolicyController {
	private final QOSPolicyService service;
	private static final Logger logger = LoggerFactory.getLogger(QOSPolicyService.class);
	private static final String CLASSNAME = "QOSPolicyService";

    public QOSPolicyController(QOSPolicyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<QOSPolicy> create(@RequestBody QOSPolicy policy) throws ValidationException {
    	String methodName = "create";
    	if (logger.isInfoEnabled()) {
    		logger.debug(CLASSNAME, methodName, " method started");
        }
    	QOSPolicy created = service.create(policy);
        if (logger.isInfoEnabled()) {
    		logger.debug("Policy created", methodName, " method started");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);        
    }

    
    @GetMapping
    public ResponseEntity<List<QOSPolicy>> getPolicies(@RequestParam(required = false) String id,
                                         @RequestParam(required = false) String name) {
    	String methodName = "getPolicies";
    	if (logger.isInfoEnabled()) {
    		logger.debug(CLASSNAME, methodName, " method started");
        }
    	if (id != null) {
            QOSPolicy policyById = service.getById(id);
            return ResponseEntity.ok(List.of(policyById));
        }
        if (name != null) {
            QOSPolicy policyByName = service.getByName(name);
            return ResponseEntity.ok(List.of(policyByName));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/gateway-mapping")
    public ResponseEntity<List<QOSPolicyGatewayMapping>> getGatewayMapping(@RequestParam("qosPolicyId") String qosPolicyId) {

        String methodName = "getGatewayMapping";

        if (logger.isInfoEnabled()) {
            logger.debug(CLASSNAME, methodName, " method started");
        }

        ResponseEntity<List<QOSPolicyGatewayMapping>> response = ResponseEntity.ok(service.getGatewayMappingByQosPolicyId(qosPolicyId));

        if (logger.isInfoEnabled()) {
            logger.debug(CLASSNAME, methodName, " method completed");
        }
        return response;
    }

}
