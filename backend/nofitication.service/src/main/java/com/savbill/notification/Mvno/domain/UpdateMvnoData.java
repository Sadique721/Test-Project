package com.savbill.notification.Mvno.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateMvnoData {
    private Integer oldmvnoId;
    private Integer newmvnoId;
}

