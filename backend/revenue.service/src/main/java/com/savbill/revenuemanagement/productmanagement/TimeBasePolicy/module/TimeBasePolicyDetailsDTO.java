package com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.module;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class TimeBasePolicyDetailsDTO {

    private Long detailsid;
    private String fromDay;
    private String toDay;
    private String fromTime;
    private String toTime;
    private Long qqsid;
    private Boolean access;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Long buId;
    private String qos_name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private TimeBasePolicyDTO timeBasePolicy;


}
