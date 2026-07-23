package com.savbill.integrationsystem.RestApiService.ReauthSessionController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReAuthSessionDto {
    private String subscriberId;
    private String alternateId;
    private String parameter1;
    private String parameter2;
}
