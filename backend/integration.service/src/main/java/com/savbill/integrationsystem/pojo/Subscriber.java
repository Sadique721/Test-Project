package com.savbill.integrationsystem.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Subscriber {

    private Long code;
    private String name;
    private String ph_no;
    private LocalDate active_from;
    private LocalDate expiry_date;
    private Long device_count;
    List<Packages> subPackages = new ArrayList<>();
    List<Devices> devices = new ArrayList<>();

}
