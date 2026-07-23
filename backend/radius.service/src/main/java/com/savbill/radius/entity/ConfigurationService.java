package com.savbill.radius.entity;

import com.savbill.radius.kafka.message.SaveClientServMessge;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name = "tblmclientservice")
public class ConfigurationService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceid", nullable = false, length = 40)
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 40)
    private String value;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public ConfigurationService(SaveClientServMessge configurationService) {
        this.name = configurationService.getName();
        this.value = configurationService.getValue();
        this.mvnoId = configurationService.getMvnoId();
    }

    public ConfigurationService(SaveClientServMessge configurationService, Long id) {
        this.id = id;
        this.name = configurationService.getName();
        this.value = configurationService.getValue();
        this.mvnoId = configurationService.getMvnoId();
    }
}
