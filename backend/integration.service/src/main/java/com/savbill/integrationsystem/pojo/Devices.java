package com.savbill.integrationsystem.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Devices {

    private String device_id;
    private String master;
    private String pack;
    private LocalDate active_from;
    private LocalDate expiry_on;
    private Double last_payment;
    private LocalDate last_paid;
    List<Subscriber> subscriberdata = new ArrayList<>();

}
