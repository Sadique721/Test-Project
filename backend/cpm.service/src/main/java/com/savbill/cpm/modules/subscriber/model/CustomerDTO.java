package com.savbill.cpm.modules.subscriber.model;

import com.savbill.cpm.model.postpaid.CustMacMappping;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceArea;
import com.savbill.cpm.pojo.api.CustChargeOverrideDTO;
import com.savbill.cpm.pojo.api.CustNetworkDetailsDTO;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerDTO {
    private Integer id;
    private String username;
    private String firstname;
    private String lastname;
    private String mobile;
    private String email;
    private String acctno;
    private String countryCode;
    private Integer mvnoId;
    private Long serviceAreaId;
    private String serviceAreaName;
    public CustomerDTO(
            Integer id,
            String username,
            String firstname,
            String lastname,
            String email,
            String acctno,
            String mobile,
            String countryCode,
            Integer mvnoId,
            Long serviceAreaId,
            String serviceAreaName
    ) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.acctno = acctno;
        this.mobile = mobile;
        this.countryCode = countryCode;
        this.mvnoId = mvnoId;
        this.serviceAreaId = serviceAreaId;
        this.serviceAreaName = serviceAreaName;
    }
}
