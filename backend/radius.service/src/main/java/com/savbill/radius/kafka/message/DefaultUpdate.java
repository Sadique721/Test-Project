package com.savbill.radius.kafka.message;


import com.savbill.radius.aaa.data.CustomerCreateData;
import lombok.Data;

import java.io.Serializable;

@Data
public class DefaultUpdate implements Serializable {
    private CustomerCreateData customerCreateData;
    private String oldUsername;

    public DefaultUpdate(CustomerCreateData customerCreateData, String oldUsername) {
        this.customerCreateData = customerCreateData;
        this.oldUsername = oldUsername;
    }

    public DefaultUpdate() {
    }

    public DefaultUpdate(CustomerCreateData customerCreateData) {
        this.customerCreateData = customerCreateData;
    }
}
