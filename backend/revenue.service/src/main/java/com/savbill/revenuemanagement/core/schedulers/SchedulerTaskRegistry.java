package com.savbill.revenuemanagement.core.schedulers;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SchedulerTaskRegistry {

    private final Map<String, ScheduledTask> strategyMap;

    public SchedulerTaskRegistry(List<ScheduledTask> scheduledTasks) {
        this.strategyMap = new HashMap<>();
        for(ScheduledTask task :scheduledTasks){
            String beanName = task.getClass().getAnnotation(Component.class).value();
            strategyMap.put(beanName,task);
        }
    }

    public ScheduledTask getStrategy(String schedulerName) {
        ScheduledTask task = strategyMap.get(schedulerName);
        if (Objects.isNull(task)) {
            throw new IllegalArgumentException("No Scheduler Config found for: " + schedulerName);
        }
        return task;
    }
}
