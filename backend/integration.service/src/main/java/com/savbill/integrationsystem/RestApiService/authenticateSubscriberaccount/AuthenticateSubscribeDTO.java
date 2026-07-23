package com.savbill.integrationsystem.RestApiService.authenticateSubscriberaccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateSubscribeDTO {
    private String userName;
    private String password;
}
