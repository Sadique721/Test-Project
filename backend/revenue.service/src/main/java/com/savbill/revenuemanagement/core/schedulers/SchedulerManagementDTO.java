package com.savbill.revenuemanagement.core.schedulers;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class SchedulerManagementDTO {
    @NotBlank(message = "Scheduler name is mandatory")
    private String schedulerName;
    @NotBlank(message = "Scheduler time is mandatory")
    private String schedulerTime;

    @NotBlank(message = "Schedule type is mandatory")
    private String scheduleType;

    private Weekly weekly;

    @Min(value = 1, message = "Day of month must be between 1 and 31")
    @Max(value = 31, message = "Day of month must be between 1 and 31")
    private Long dayOfMonth;
    private String status;
    private Long mvnoId;

}
