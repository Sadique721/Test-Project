package com.savbill.radius.ippool.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblmippoolmapping")
@ApiModel(value = "tblmippoolmapping",description = "This is IP Pool mapping entity.")
@Data
public class IPPoolMapping {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @ApiModelProperty(notes = "SuspendedProfileMapping attribute id",required = true)
        @Column(name = "ippool_mapping_id", nullable = false)
        private Long ipPoolMappingId;

        @ApiModelProperty(notes = "This is client group id",required = true)
        @Column(name = "clientid", nullable = false)
        private Long clientId;

        @ApiModelProperty(notes = "This is client group id",required = true)
        @Column(name = "ippool_id", nullable = false)
        private Long ipPoolId;

        public Long getIpPoolMappingId() {
                return ipPoolMappingId;
        }

        public void setIpPoolMappingId(Long ipPoolMappingId) {
                this.ipPoolMappingId = ipPoolMappingId;
        }

        public Long getClientId() {
                return clientId;
        }

        public void setClientId(Long clientId) {
                this.clientId = clientId;
        }

        public Long getIpPoolId() {
                return ipPoolId;
        }

        public void setIpPoolId(Long ipPoolId) {
                this.ipPoolId = ipPoolId;
        }
}
