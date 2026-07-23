package com.savbill.integrationsystem.RestApiService.SubAccountNameIsLoggendIn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubAccNameDto {
    @JsonProperty("String_1")
    private String string1;

}
