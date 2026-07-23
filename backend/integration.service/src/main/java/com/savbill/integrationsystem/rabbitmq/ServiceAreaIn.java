package com.savbill.integrationsystem.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaIn {
        private Long id;
        private String name;
        private String status;
        private Boolean isDeleted = false;
        private String latitude;
        private String longitude;
        private Long areaid;
        private Integer mvnoId;
        //private List<Integer> pincodes;
        private Long cityid;
}
