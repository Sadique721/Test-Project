package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceareaLocationMappingMessage {

    private Long id;

    private Long serviceAreaId;

    private Long locationId;


}
