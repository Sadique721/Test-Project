package com.savbill.radius.services.impl;

import com.savbill.radius.entity.ConfigurationService;
import com.savbill.radius.entity.PlanQosMappingEntity;
import com.savbill.radius.entity.PlanUsagePercentageMapping;
import com.savbill.radius.entity.PostpaidPlan;
import com.savbill.radius.kafka.CustomMessage;
import com.savbill.radius.repository.ConfigurationServiceRepository;
import com.savbill.radius.repository.PlanQosMappingRepository;
import com.savbill.radius.repository.PlanUsagePercentageMappingRepository;
import com.savbill.radius.repository.PostpaidPlanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostpaidPlanServiceImpl {

    @Autowired
    private PostpaidPlanRepository postpaidPlanRepository;

    @Autowired
    private PlanQosMappingRepository planQosMappingRepository;
    @Autowired
    private PlanUsagePercentageMappingRepository planUsagePercentageMappingRepository;
    @Autowired
    private ConfigurationServiceRepository configServiceRepository;

    private static final Logger log = LoggerFactory.getLogger(PostpaidPlanServiceImpl.class);

    public PostpaidPlan save(CustomMessage message) {
        try {
            if (message.getData() != null) {
                PostpaidPlan postpaidPlan = new PostpaidPlan(message);
                PostpaidPlan plan = postpaidPlanRepository.save(postpaidPlan);
                postpaidPlan.getPlanQosMappingEntities().stream().forEach(planQosMapping -> planQosMapping.setIsdelete(false));
                if (postpaidPlan.getPlanQosMappingEntities() != null && postpaidPlan.getPlanQosMappingEntities().size() > 0) {
                    List<PlanQosMappingEntity> previousPlanQosMappingEntityList = planQosMappingRepository.findAllByPlanId(postpaidPlan.getPlanQosMappingEntities().get(0).getPostpaidPlan().longValue());
                    if (!previousPlanQosMappingEntityList.isEmpty()) {
                        previousPlanQosMappingEntityList.stream().forEach(planQosMapping -> planQosMapping.setIsdelete(true));
                        planQosMappingRepository.saveAll(previousPlanQosMappingEntityList);
                    }
                }
                planQosMappingRepository.saveAll(postpaidPlan.getPlanQosMappingEntities());
                //Save Plan Service
                List<PlanUsagePercentageMapping> oldPlansUsage = planUsagePercentageMappingRepository.findAllByPlanId(plan.getId());
                if (!CollectionUtils.isEmpty(oldPlansUsage)) {
                    planUsagePercentageMappingRepository.deleteInBatch(oldPlansUsage);
                }
                List<PlanUsagePercentageMapping> planUsagePercentageMappingList = new ArrayList<>();
                String perValues = "50,70,100"; //TODO: Set configuration for default notification values;
                Optional<ConfigurationService> configuration = configServiceRepository.findByNameAndMvnoId("radius_quota_notification_configuration", plan.getMvnoId());
                if (configuration.isPresent()) {
                    perValues = configuration.get().getValue();
                }
                if (perValues.contains(",")) {
                    List<Integer> percentages = Arrays.stream(perValues.split(",")).filter(s -> !s.isEmpty()).map(Integer::valueOf).collect(Collectors.toList());
                    for (int i = 0; i < percentages.size(); i++) {
                        planUsagePercentageMappingList.add(new PlanUsagePercentageMapping(plan.getId(), percentages.get(i).doubleValue(), i));
                    }
                    planUsagePercentageMappingRepository.saveAll(planUsagePercentageMappingList);
                }
                //update quota in all Active CPR

                return plan;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public PostpaidPlan findByPlanName(String planName) {
        PostpaidPlan postpaidPlan = postpaidPlanRepository.findAllByNameContainsIgnoreCase(planName);
        return postpaidPlan;
    }
}
