package com.savbill.integrationsystem.RestApiService.LoggOffSubSession;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggOffSubbsessionDto {
    @JsonProperty("String_1")
    private String string1;
}
