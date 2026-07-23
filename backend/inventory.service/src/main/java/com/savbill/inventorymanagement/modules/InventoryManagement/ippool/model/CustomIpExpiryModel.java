package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class CustomIpExpiryModel {
    public String username;
    public String ip_address;
    public Date enddate;
    public String mobile;
    public String email;
}
