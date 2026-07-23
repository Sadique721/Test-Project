package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.rabbitmq.BusinessUnitMessage;
import lombok.Data;

import javax.persistence.*;
@Data
@Entity
@Table(name = "tblmbusinessunit")
public class BusinessUnit{
        @Id
        @Column(name = "businessunitid")
        private Long id;
        private String buname;

        private String bucode;

        private String status;

        @Column(columnDefinition = "Boolean default false", nullable = false)
        private Boolean isDeleted = false;

        @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
        private Integer mvnoId;

        public BusinessUnit(){

        }

        public BusinessUnit(BusinessUnitMessage message){
                this.id = message.getId();
                this.buname=message.getBuname();
                this.bucode=message.getBucode();
                this.status=message.getStatus();
                this.isDeleted=message.getIsDeleted();
                this.mvnoId=message.getMvnoId();
        }

    }
