package com.savbill.integrationsystem.isp;

import lombok.Data;

import java.util.List;

@Data
public class ServicePayload {
    private String service;
    private double amount;
    private List<Item> items;
}
