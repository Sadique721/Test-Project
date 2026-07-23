package com.savbill.commonGateway.moules.MvnoFeedBackConfig.Service;

import com.savbill.commonGateway.moules.MvnoFeedBackConfig.Domain.FeedbackConfig;
import com.savbill.commonGateway.moules.MvnoFeedBackConfig.Repository.FeedbackConfigRepository;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class FeedbackConfigService {

    @Autowired
    private FeedbackConfigRepository repository;

    public FeedbackConfig create(FeedbackConfig config) {
        try {
            config.setMvnoid(getMvnoIdFromCurrentStaff());
            FeedbackConfig saved = repository.save(config);
            log.info("FeedbackConfig created successfully with ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Error creating FeedbackConfig: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create FeedbackConfig", e);
        }
    }

    public List<FeedbackConfig> getAll(Integer mvnoid) {
        try {
            List<FeedbackConfig> list = repository.findAllByMvnoid(mvnoid);
            log.info("Fetched {} FeedbackConfig(s)", list.size());
            return list;
        } catch (Exception e) {
            log.error("Error fetching FeedbackConfigs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch FeedbackConfigs", e);
        }
    }

    public Optional<FeedbackConfig> getById(Long id) {
        try {
            Optional<FeedbackConfig> config = repository.findById(id);
            if (config.isPresent()) {
                log.info("FeedbackConfig found with ID: {}", id);
            } else {
                log.warn("No FeedbackConfig found with ID: {}", id);
            }
            return config;
        } catch (Exception e) {
            log.error("Error fetching FeedbackConfig by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch FeedbackConfig by ID", e);
        }
    }

    public FeedbackConfig update(Long id, FeedbackConfig updatedConfig) {
        try {
            return repository.findById(id)
                    .map(existing -> {
                        existing.setEvent(updatedConfig.getEvent());
                        existing.setIsActive(updatedConfig.getIsActive());
                        existing.setChannel(updatedConfig.getChannel());
                        existing.setIsMandatory(updatedConfig.getIsMandatory());
                        existing.setFeedBackMessage(updatedConfig.getFeedBackMessage());
                        existing.setRatingScale(updatedConfig.getRatingScale());
                        existing.setRatingDisplayType(updatedConfig.getRatingDisplayType());
                        FeedbackConfig saved = repository.save(existing);
                        log.info("FeedbackConfig updated successfully with ID: {}", id);
                        return saved;
                    })
                    .orElseThrow(() -> {
                        log.warn("FeedbackConfig not found with ID: {}", id);
                        return new RuntimeException("FeedbackConfig not found with ID: " + id);
                    });
        } catch (Exception e) {
            log.error("Error updating FeedbackConfig with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update FeedbackConfig", e);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
            log.info("FeedbackConfig deleted with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting FeedbackConfig with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete FeedbackConfig", e);
        }
    }
    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            log.error("error while getting mvnoId from current staff; {};",e.getMessage(),e);

        }
        return mvnoId;
    }
    public boolean validateIsEventUniqueForSave(FeedbackConfig feedbackConfig){
        if(feedbackConfig.getEvent() != null && !feedbackConfig.getEvent().isEmpty()){
            List<FeedbackConfig> allByEventAndMvnoid = repository.findAllByEventAndMvnoid(feedbackConfig.getEvent(), getMvnoIdFromCurrentStaff());
            if(allByEventAndMvnoid.size() > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean validateForUpdate(FeedbackConfig feedbackConfig,Long id){
        boolean flag = false;
        FeedbackConfig config = repository.findById(id).orElse(null);
        if(config.getEvent().equalsIgnoreCase(feedbackConfig.getEvent())){
            return true;
        }else if(validateIsEventUniqueForSave(feedbackConfig)){
            return true;
        }else {
            return false;
        }
    }
}

