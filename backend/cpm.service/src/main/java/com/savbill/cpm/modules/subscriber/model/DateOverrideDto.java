package com.savbill.cpm.modules.subscriber.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateOverrideDto {

    boolean dateOverrideFlag;
    private LocalDateTime changePlanStartDate;
    private LocalDateTime changePlanEndDate;
}
