package com.savbill.cpm.modules.ippool.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class CustomPlanExpiryModel {
    public String username;
    public String planname;
    public String mobile;
    public String email;
}
