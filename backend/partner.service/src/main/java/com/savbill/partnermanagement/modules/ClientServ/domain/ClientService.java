package com.savbill.partnermanagement.modules.ClientServ.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@ToString
@Table(name = "tblmclientservice")
public class ClientService {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 40)
    private String value;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

}
