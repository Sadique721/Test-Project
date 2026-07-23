package com.savbill.salescrmsbss.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import com.savbill.salescrmsbss.rabbitMq.ClientServiceMessage;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblclientservice")
public class ClientService {

    @Id
    @Column(name = "serviceid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 40)
    private String value;
    
    @Column(name = "MVNOID",length = 40)
    private Long mvnoId;

    public ClientService(ClientServiceMessage message) {
    	this.id = message.getId();
    	this.name = message.getName();
    	this.value = message.getValue();
    	this.mvnoId = message.getMvnoId();
    }
}
