package com.savbill.partnermanagement.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenericRequestDTO {
    @JsonProperty("id")
    private Long id;
    private String name;
}
