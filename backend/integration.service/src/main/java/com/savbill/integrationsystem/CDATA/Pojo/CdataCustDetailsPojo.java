package com.savbill.integrationsystem.CDATA.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CdataCustDetailsPojo {

    String customerName;
    String custUserName;
    String custPassword;
    String configName;
    Integer custServMappingId;
    String loggedInUser;
    Integer loggedInUserMvnoId;
    String serialNumber;
    String manufacturer;
    Integer customerId;
    String model;
}
