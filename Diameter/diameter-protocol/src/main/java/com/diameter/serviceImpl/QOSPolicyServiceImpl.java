package com.diameter.serviceImpl;

import com.diameter.model.QOSPolicy;
import com.diameter.model.QOSPolicyGatewayMapping;
import com.diameter.model.QOSPolicyGatewayMappingEntity;
import com.diameter.repository.QOSPolicyRepository;
import com.diameter.service.QOSPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.bind.ValidationException;
import java.util.List;

@Service
public class QOSPolicyServiceImpl implements QOSPolicyService {

	private static final Logger logger = LoggerFactory.getLogger(QOSPolicyServiceImpl.class);
	private static final String CLASSNAME = "QOSPolicyServiceImpl";

    private final QOSPolicyRepository repo;

    public QOSPolicyServiceImpl(QOSPolicyRepository repo) {
        this.repo = repo;
    }

    public QOSPolicy create(QOSPolicy policy) throws ValidationException {
    	String methodName = "create";
    	if (logger.isInfoEnabled()) {
    		logger.info(CLASSNAME, methodName, " method started");
        }
        return repo.save(policy);
    }

    public List<QOSPolicy> getAll() {
    	String methodName = "getAll";
    	if (logger.isInfoEnabled()) {
    		logger.info(CLASSNAME, methodName, " method started");
        }
        return repo.findAll();
    }

    public QOSPolicy getById(String id) {
    	String methodName = "getById";
    	if (logger.isInfoEnabled()) {
    		logger.info(CLASSNAME, methodName, " method started");
    		logger.info("Fetching policy by ID: {}", id);
        }
        return repo.findById(id);
    }

    public QOSPolicy getByName(String name) {
    	String methodName = "getByName";
    	if (logger.isInfoEnabled()) {
    		logger.info(CLASSNAME, methodName, " method started");
    		logger.info("Fetching policies by Name: {}", name);
        }
        return repo.findByName(name);
    }

    public List<QOSPolicyGatewayMapping> getGatewayMappingByQosPolicyId(String qosPolicyId) {
        String methodName = "getGatewayMappingByQosPolicyId";
        if (logger.isInfoEnabled()) {
            logger.info(CLASSNAME, methodName, " method started");
            logger.info("Fetching Gateway Mapping by QOS Policy Id: {}", qosPolicyId);
        }
        return repo.getGatewayMappingByQosPolicyId(qosPolicyId);
    }


    @Transactional
    public void createOrUpdateKafka(QOSPolicy incoming) throws ValidationException {

        QOSPolicy existing = null;

        try {
            existing = repo.findById(incoming.getId().toString());
        } catch (EmptyResultDataAccessException ex) {
            existing = null;
        }

        if (existing != null) {

            // 🔵 UPDATE PARENT
            repo.updatePolicy(incoming);

            // 🔵 DELETE OLD CHILDREN
            repo.deleteGatewayMappings(incoming.getId());

        } else {

            // 🔵 INSERT PARENT
            repo.save(incoming);
        }

        // 🔵 INSERT CHILDREN
        if (incoming.getQosPolicyGatewayMappingList() != null
                && !incoming.getQosPolicyGatewayMappingList().isEmpty()) {

            for (QOSPolicyGatewayMappingEntity child :
                    incoming.getQosPolicyGatewayMappingList()) {

                repo.insertGatewayMapping(child, incoming.getId());
            }
        }
    }

}
