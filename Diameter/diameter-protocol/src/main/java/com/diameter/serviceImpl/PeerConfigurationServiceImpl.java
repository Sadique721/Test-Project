package com.diameter.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.diameter.model.PeerConfiguration;
import com.diameter.model.PeerConfiguration.Status;
import com.diameter.repository.PeerConfigurationRepository;
import com.diameter.service.PeerConfigurationService;
import com.diameter.util.DiameterValidator;

@Service
public class PeerConfigurationServiceImpl implements PeerConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(PeerConfigurationServiceImpl.class);
    private final PeerConfigurationRepository repository;
    
    public PeerConfigurationServiceImpl(PeerConfigurationRepository repository) {
        this.repository = repository;
    }

    @Override
    public PeerConfiguration create(PeerConfiguration config) throws ValidationException {
        logger.info("Creating peer configuration: {}", config.getNodeName());
        DiameterValidator.validatePeer(config);
        return repository.save(config);
    }



    @Override
    public List<PeerConfiguration> getAll(Long id, String name, String remoteIpAddress, String realm) {
        logger.info("Fetching peer configurations with filters");
        if (id != null) {
            PeerConfiguration config = repository.getById(id);
            return config != null ? List.of(config) : Collections.emptyList();
        }
        if (name != null && !name.isEmpty()) {
            PeerConfiguration config = repository.getByName(name);
            return config != null ? List.of(config) : Collections.emptyList();
        }
        // -------------------
        // Filter by Remote IP
        // -------------------
        if (remoteIpAddress != null && !remoteIpAddress.trim().isEmpty()) {
            String ip = remoteIpAddress.trim();
            List<PeerConfiguration> list = repository.getByRemoteIp(ip);
            return (list != null) ? list : Collections.emptyList();
        }
        if (realm != null && !realm.trim().isEmpty()) {
            String trimmedRealm = realm.trim();
            List<PeerConfiguration> list = repository.getByRealm(trimmedRealm);
            return (list != null) ? list : Collections.emptyList();
        }
        return repository.findAll();
    }


    @Override
    public Optional<PeerConfiguration> getById(long id) {
        logger.info("Fetching peer configuration by ID: {}", id);
        return repository.findById(id);
    }

    @Override
    public PeerConfiguration update(long id, PeerConfiguration config) throws ValidationException {
        logger.info("Updating peer configuration ID {}: {}", id, config.getNodeName());
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Peer configuration not found with ID " + id);
        }
        return repository.update(id, config);
    }

    @Override
    public void delete(long id) {
        logger.info("Deleting peer configuration ID: {}", id);
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Peer configuration not found with ID " + id);
        }
        repository.delete(id);
    }
    
    @Override
    public List<PeerConfiguration> getPeerConfigByStatus(Status active) {
		logger.info("Fetching peer configurations by status: {}", active.getDbValue());
		return repository.getPeerConfigByStatus(active);
    }
}
