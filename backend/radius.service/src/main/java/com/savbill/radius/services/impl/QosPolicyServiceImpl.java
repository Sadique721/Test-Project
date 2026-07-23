package com.savbill.radius.services.impl;

import com.savbill.radius.entity.QOSPolicy;
import com.savbill.radius.entity.QOSPolicyGatewayMapping;
import com.savbill.radius.kafka.CustomMessage;
import com.savbill.radius.repository.QOSGatewayMappingRepository;
import com.savbill.radius.repository.QosPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QosPolicyServiceImpl {

    @Autowired
    private QosPolicyRepository qosPolicyRepository;

    @Autowired
    private QOSGatewayMappingRepository qosGatewayMappingRepository;

    @Transactional
    public QOSPolicy save(CustomMessage message) {
        try {
            if (message.getData() != null) {
                QOSPolicy qosPolicy = new QOSPolicy(message);

                // Save the QOSPolicy entity first to ensure it has an ID generated
                List<QOSPolicyGatewayMapping> qosPolicyGatewayMappingList = qosPolicy.getQosPolicyGatewayMappingList();

                List<QOSPolicyGatewayMapping> updatedMappingList = new ArrayList<>();

                for (QOSPolicyGatewayMapping qosPolicyGatewayMapping : qosPolicyGatewayMappingList) {
                    if (qosPolicyGatewayMapping.getId() != null) {
                        Optional<QOSPolicyGatewayMapping> currentMappingOpt = qosGatewayMappingRepository.findById(qosPolicyGatewayMapping.getId());
                        if (currentMappingOpt.isPresent()) {
                            QOSPolicyGatewayMapping currentMapping = currentMappingOpt.get();
                            currentMapping.setDownloadSpeed(qosPolicyGatewayMapping.getDownloadSpeed());
                            currentMapping.setGatewayName(qosPolicyGatewayMapping.getGatewayName());
                            currentMapping.setUploadSpeed(qosPolicyGatewayMapping.getUploadSpeed());
                            currentMapping.setBaseDownloadSpeed(qosPolicyGatewayMapping.getBaseDownloadSpeed());
                            currentMapping.setBaseUploadSpeed(qosPolicyGatewayMapping.getBaseUploadSpeed());
                            currentMapping.setThrottleDownloadSpeed(qosPolicyGatewayMapping.getThrottleDownloadSpeed());
                            currentMapping.setThrottleUploadSpeed(qosPolicyGatewayMapping.getThrottleUploadSpeed());
                            currentMapping.setQosPolicyId(qosPolicyGatewayMapping.getQosPolicyId());
                            updatedMappingList.add(currentMapping);
                        } else {
                            // If no existing mapping is found by ID, add the new mapping
                            qosPolicyGatewayMapping.setQosPolicyId(qosPolicy.getId());
                            updatedMappingList.add(qosPolicyGatewayMapping);
                        }
                    } else {
                        // If no ID is present, add the new mapping
                        qosPolicyGatewayMapping.setQosPolicyId(qosPolicy.getId());
                        updatedMappingList.add(qosPolicyGatewayMapping);
                    }
                }

                // Update QOSPolicy with the updated mapping list
                qosPolicy.setQosPolicyGatewayMappingList(updatedMappingList);
                return qosPolicyRepository.save(qosPolicy);
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
