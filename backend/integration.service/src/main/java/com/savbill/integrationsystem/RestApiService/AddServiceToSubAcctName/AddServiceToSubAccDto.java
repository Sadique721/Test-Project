package com.savbill.integrationsystem.RestApiService.AddServiceToSubAcctName;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddServiceToSubAccDto{
    @JsonProperty("String_1")
    private String string1;
    @JsonProperty("String_2")
    private String string2;
}
