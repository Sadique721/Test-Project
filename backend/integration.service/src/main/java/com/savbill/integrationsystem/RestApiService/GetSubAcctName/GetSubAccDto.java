package com.savbill.integrationsystem.RestApiService.GetSubAcctName;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetSubAccDto {
    @JsonProperty("String_1")
    private String string1;
}
