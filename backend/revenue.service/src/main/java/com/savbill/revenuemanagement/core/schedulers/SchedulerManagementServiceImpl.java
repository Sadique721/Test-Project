package com.savbill.revenuemanagement.core.schedulers;

import com.savbill.revenuemanagement.core.constants.SearchConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericSearchModel;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service
public class SchedulerManagementServiceImpl extends AbstractService implements SchedulerManagementService {

    @Autowired
    SchedulerManagementRepository schedulerManagementRepository;

    @Autowired
    SchedulerMapper schedulerMapper;

    @Autowired
    SchedulerTaskRegistry taskRegistry;

    private static final Logger logger = LoggerFactory.getLogger(SchedulerManagementServiceImpl.class);

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Override
    public void save(SchedulerManagementDTO schedulerManagementDTO) {
        try {
            SchedulerManagement schedulerManagement = schedulerMapper.toEntity(schedulerManagementDTO);
            validateWithAlreayExists(schedulerManagement);
             schedulerManagementRepository.save(schedulerManagement);
            scheduledTask(schedulerManagement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void validateWithAlreayExists(SchedulerManagement schedulerManagement){
        Boolean exists = schedulerManagementRepository.existsBySchedulerName(schedulerManagement.getSchedulerName());
        if(exists){
            throw new IllegalArgumentException("Scheduler: "+schedulerManagement.getSchedulerName()+" Already Exists");
        }
    }


    @Override
    public void update(SchedulerManagementDTO schedulerManagementDTO, Long id) {
        try {
            SchedulerManagement exisisting = schedulerManagementRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("No record Found for scheduler: " + id));
            schedulerMapper.updateEntityFromDto(schedulerManagementDTO, exisisting);
            schedulerManagementRepository.save(exisisting);
            updateTask(exisisting);
        } catch (Exception e) {
            logger.error("Exception while update scheduler management");
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(Long id) {
        SchedulerManagement exisisting = schedulerManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No record Found for scheduler: " + id));
        ScheduledFuture<?> existingScheduledFuture = scheduledTasks.remove(exisisting.getSchedulerName().toString());
        existingScheduledFuture.cancel(true);
        logger.info(" : {}", exisisting.getSchedulerName().toString());
        schedulerManagementRepository.delete(exisisting);

    }

    @Override
    public SchedulerManagement getById(Long id) {
        SchedulerManagement exisisting = schedulerManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No record Found for scheduler: " + id));
//        SchedulerManagementDTO schedulerManagementDTO = schedulerMapper.toDTO(exisisting);
        return exisisting;
    }


    @Override
    public Page<SchedulerManagement> getAllScedulersWithPagination(PaginationRequestDTO paginationRequestDTO) {
        PageRequest pageRequest = PageRequest.of(paginationRequestDTO.getPage() - 1, paginationRequestDTO.getPageSize(), Sort.by("id").descending());
        try {
            Page<SchedulerManagement> schedulerManagements = search(paginationRequestDTO.getFilters(), pageRequest);
            if (Objects.isNull(schedulerManagements) || schedulerManagements == null) {
                logger.error("SchedulerManagement Details fetch failed — reason: No record found ");
            }

            return schedulerManagements;
        } catch (Exception e) {
            logger.error("SchedulerManagement fetch failed: {}", e);
            throw new RuntimeException("SchedulerManagement fetch failed", e);
        }

    }

    public Page<SchedulerManagement> search(List<GenericSearchModel> filterList, PageRequest pageRequest) {
        try {
            if (filterList != null && !filterList.isEmpty()) {
                for (GenericSearchModel searchModel : filterList) {
                    // Assuming 'ANY' is a special condition
                    if (SearchConstants.ANY.equalsIgnoreCase(searchModel.getFilterColumn().trim())) {
                        return getSchedulers(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Search operation failed: {}", ex.getMessage(), ex);
        }
        return null;
    }


    public Page<SchedulerManagement> getSchedulers(String filterValue, PageRequest pageRequest) {
        QSchedulerManagement qSchedulerManagement = QSchedulerManagement.schedulerManagement;
        BooleanExpression booleanExpression = qSchedulerManagement.isNotNull();
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        // Check if the filter value is not empty
        if (filterValue != null && !filterValue.trim().isEmpty()) {
            List<SchedulerName> matchedEnums = Arrays.stream(SchedulerName.values())
                    .filter(e -> e.name().toLowerCase().contains(filterValue.toString().toLowerCase()))
                    .collect(Collectors.toList());
            // Assuming filterValue is used to match the title or description of the documents
            booleanExpression = booleanExpression.and(qSchedulerManagement.schedulerName.in(matchedEnums)
                    .or(qSchedulerManagement.scheduleType.containsIgnoreCase(filterValue)));
        }

        if (mvnoId == 1) {
            return schedulerManagementRepository.findAll(booleanExpression, pageRequest);
        } else {
            booleanExpression = booleanExpression.and(qSchedulerManagement.mvnoId.in(mvnoId, 1));
            return schedulerManagementRepository.findAll(booleanExpression, pageRequest);
        }
    }

    private void updateTask(SchedulerManagement updatedSchedulerManagement) {
        // Check if a task is already scheduled for the scheduler
        logger.debug("::::::::Call Update Task for Scheduler : "+updatedSchedulerManagement.getSchedulerName().toString());
        try {
            if (!scheduledTasks.containsKey(updatedSchedulerManagement.getSchedulerName().toString())) {
                logger.info("No existing scheduler found for driver : {}", updatedSchedulerManagement.getSchedulerName());
                return;
            }

            // Cancel the existing task
            ScheduledFuture<?> existingScheduledFuture = scheduledTasks.remove(updatedSchedulerManagement.getSchedulerName().toString());
            existingScheduledFuture.cancel(true);
            // Schedule a new task with updated configuration
            scheduledTask(updatedSchedulerManagement);
            logger.debug("::::::::Schedule New Updated task for Scheduler :"+updatedSchedulerManagement.getSchedulerName().toString());
//            log.info("Scheduler updated for driver : {}", updatedPurgSetting.getDriverId());
        }catch (Exception e){
            logger.error(":::::Unable to Update task:::::::::{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void scheduledTask(SchedulerManagement schedulerManagement) {
        if(schedulerManagement.getStatus().equalsIgnoreCase("Active")) {
            String type = schedulerManagement.getScheduleType();
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            if (scheduledTasks.containsKey(schedulerManagement.getSchedulerName().toString())) {
                logger.error("Scheduler already exists for : {}", schedulerManagement.getSchedulerName().toString());
                return;
            }
            switch (type.toUpperCase()) {
                case "DAILY":
                    scheduleDaily(schedulerManagement, scheduler);
                    break;
                case "WEEKLY":
                    scheduleWeekly(schedulerManagement, scheduler);
                    break;
                case "MONTHLY":
                    scheduleMonthly(schedulerManagement, scheduler);
                    break;
                default:
                    throw new IllegalArgumentException("Please Provide Valid Scheduler type");
            }
        }
    }

    public void scheduleDaily(SchedulerManagement schedulerManagement,ScheduledExecutorService scheduler){
        LocalDateTime currentDateTime = LocalDateTime.now();
        String[] dailyValues = schedulerManagement.getSchedulerTime().split(":");
        int scheduledHour = Integer.parseInt(dailyValues[0]);
        int scheduledMinute = Integer.parseInt(dailyValues[1]);
        LocalTime scheduledTime = LocalTime.of(scheduledHour, scheduledMinute);
        Long initialDelay = calculateInitialDelay(currentDateTime, scheduledTime);
        logger.info("Scheduled Daily task going to executed for  {} at: {}", schedulerManagement.getSchedulerName(), initialDelay);
        // Schedule the task to run every day at the specified time
        ScheduledFuture<?> dailyScheduledFuture = scheduler.scheduleWithFixedDelay(() -> {
            logger.info("Daily scheduled task executed for {} at {}", schedulerManagement.getSchedulerName(), LocalDateTime.now());
           taskRegistry.getStrategy(schedulerManagement.getSchedulerName().toString()).execute(schedulerManagement);
//            callDeleteAPI(purgSetting.getDriverId(), purgSetting.getDisposalTime(), purgingLogs, purgSetting.getMvnoId(), purgSetting.getMvnoName());
        }, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);

        // Store the ScheduledFuture in the map for later reference
        scheduledTasks.put(schedulerManagement.getSchedulerName().toString(), dailyScheduledFuture);

    }

    public void scheduleWeekly(SchedulerManagement schedulerManagement,ScheduledExecutorService scheduler){
        LocalDateTime currentDateTime = LocalDateTime.now();
        String[] weeklyValues = schedulerManagement.getSchedulerTime().split(":");
        Weekly schedulerDay = schedulerManagement.getWeekly();
        int weeklyHour = Integer.parseInt(weeklyValues[0]);
        int weeklyMinute = Integer.parseInt(weeklyValues[1]);
        LocalTime weeklyScheduledTime = LocalTime.of(weeklyHour, weeklyMinute);
        // Get the current date and time
        LocalDateTime weeklyDateTime = LocalDateTime.now();
        Long initialDelay = calculateInitialDelay(weeklyDateTime, weeklyScheduledTime, schedulerDay);
        logger.info("Scheduled Weekly task for {} at {}", schedulerManagement.getSchedulerName(), initialDelay);
        ScheduledFuture<?> weeklyScheduledFuture = scheduler.scheduleWithFixedDelay(() -> {
            logger.info("Weekly scheduled task executed for {} at {}", schedulerManagement.getSchedulerName(), LocalDateTime.now());
            taskRegistry.getStrategy(schedulerManagement.getSchedulerName().toString()).execute(schedulerManagement);
        }, initialDelay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS);
        // Store the ScheduledFuture in the map for later reference
        scheduledTasks.put(schedulerManagement.getSchedulerName().toString(), weeklyScheduledFuture);

    }


    public void scheduleMonthly(SchedulerManagement schedulerManagement, ScheduledExecutorService scheduler) {
        String[] monthlyValues = schedulerManagement.getSchedulerTime().split(":");
        int monthlyHour = Integer.parseInt(monthlyValues[0]);
        int monthlyMinute = Integer.parseInt(monthlyValues[1]);
        Long dayOfMonth = schedulerManagement.getDayOfMonth(); // e.g., 15 for 15th

        LocalDateTime now = LocalDateTime.now();

        // Calculate the next valid monthly execution time
        LocalDateTime nextExecutionTime = calculateNextMonthlyExecutionTime(now, dayOfMonth, monthlyHour, monthlyMinute);

        long initialDelay = Duration.between(now, nextExecutionTime).toMillis();
        logger.info("Scheduled monthly task for {} at {}", schedulerManagement.getSchedulerName(), nextExecutionTime);

        ScheduledFuture<?> monthlyScheduledFuture = scheduler.scheduleWithFixedDelay(() -> {
            LocalDateTime executionTime = LocalDateTime.now();
            logger.info("Monthly scheduled task executed for {} at {}", schedulerManagement.getSchedulerName(), executionTime);
            taskRegistry.getStrategy(schedulerManagement.getSchedulerName().toString()).execute(schedulerManagement);
        }, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS); // run daily, but control inside logic

        scheduledTasks.put(schedulerManagement.getSchedulerName().toString(), monthlyScheduledFuture);
    }

    private LocalDateTime calculateNextMonthlyExecutionTime(LocalDateTime now, Long configuredDay, int hour, int minute) {
        int year = now.getYear();
        int month = now.getMonthValue();
        while (true) {
            YearMonth ym = YearMonth.of(year, month);
            int maxDay = ym.lengthOfMonth();

            int targetDay = Math.min(configuredDay.intValue(), maxDay);
            LocalDateTime candidate = LocalDateTime.of(year, month, targetDay, hour, minute);

            if (candidate.isAfter(now)) {
                return candidate;
            }
            // Go to next month
            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
        }
    }


    private static long calculateInitialDelay(LocalDateTime currentDateTime, LocalTime scheduledTime) {
        try{
            LocalDateTime nextScheduledDateTime = LocalDateTime.of(LocalDate.now(), scheduledTime);
            if (currentDateTime.isAfter(nextScheduledDateTime)) {
                // If the scheduled time has already passed for today, calculate delay for the next day
                nextScheduledDateTime = nextScheduledDateTime.plusDays(1);
            }
            return Duration.between(currentDateTime, nextScheduledDateTime).toMillis();
        }catch (Exception e){
            logger.error("::::::::::::::::::Exception while calculate initial delays:::::::::::::");
            throw new RuntimeException(e.getMessage());
        }
    }

    private static long calculateInitialDelay(LocalDateTime weeklyDateTime, LocalTime weeklyScheduledTime, Weekly schedulerDay) {
        // Calculate the initial delay until the next occurrence of the scheduled day and time
        try{
            LocalDateTime nextScheduledDateTime = weeklyDateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(schedulerDay.getValue())))
                    .with(weeklyScheduledTime);
            long initialDelay = Duration.between(weeklyDateTime, nextScheduledDateTime).toMillis();
            if (initialDelay < 0) {
                // If the scheduled time has already passed for this week, calculate delay for the next week
                nextScheduledDateTime = nextScheduledDateTime.plusWeeks(1);
                initialDelay = Duration.between(weeklyDateTime, nextScheduledDateTime).toMillis();
            }
            return initialDelay;
        }catch (Exception e){
            logger.error("Exception while calculation intial delay for weekly");
            throw new RuntimeException(e.getMessage());
        }
    }


    @PostConstruct
    public void scheduleAllSchedulers() {
        List<SchedulerManagement> schedulerManagementList = schedulerManagementRepository.findAllByActiveStatus();
        if(!schedulerManagementList.isEmpty()){
            for (SchedulerManagement schedulerManagement :schedulerManagementList ) {
                scheduledTask(schedulerManagement);
            }
        }
        logger.info("Scheduled for all device drivers on application start-up");
    }


    @Override
    protected JpaRepository getRepository() {
        return null;
    }
}




