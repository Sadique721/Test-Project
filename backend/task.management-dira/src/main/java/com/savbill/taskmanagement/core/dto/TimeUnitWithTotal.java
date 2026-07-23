package com.savbill.taskmanagement.core.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeUnitWithTotal {
    private long totalTime;
    private String unit;
}
