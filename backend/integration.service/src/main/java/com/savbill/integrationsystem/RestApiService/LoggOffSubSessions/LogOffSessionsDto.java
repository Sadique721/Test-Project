package com.savbill.integrationsystem.RestApiService.LoggOffSubSessions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogOffSessionsDto {
    @JsonProperty("String_1")
    private String string1;
}
