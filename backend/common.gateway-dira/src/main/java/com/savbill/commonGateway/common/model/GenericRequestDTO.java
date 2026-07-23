package com.savbill.commonGateway.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenericRequestDTO {
    @JsonProperty("id")
    private Long id;
    private String name;
}
