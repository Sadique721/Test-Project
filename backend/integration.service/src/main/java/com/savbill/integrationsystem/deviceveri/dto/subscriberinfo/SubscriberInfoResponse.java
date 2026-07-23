package com.savbill.integrationsystem.deviceveri.dto.subscriberinfo;

import com.savbill.integrationsystem.deviceveri.dto.SubscriberData;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubscriberInfoResponse {

    @JsonProperty("subscriber_data")
    private SubscriberData subscriberData;

}
