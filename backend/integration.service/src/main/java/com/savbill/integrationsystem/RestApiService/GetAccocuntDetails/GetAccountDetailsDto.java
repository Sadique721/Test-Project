package com.savbill.integrationsystem.RestApiService.GetAccocuntDetails;

import com.savbill.integrationsystem.generated.getaccountdetails.WsGetAccountDetailsResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetAccountDetailsDto {
    private List<WsGetAccountDetailsResponse.GetAccountDetails.Item> items;
    private String password;
    private int requestId;
    private int responseCode;
    private String responseMessage;
    private String serviceId;
    private String userName;
}
