package com.savbill.radius.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@ToString
@Table(name = "tblmclientservice")
@NoArgsConstructor
public class ClientServiceEntity {

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

    public ClientServiceEntity(String name, String value, Integer mvnoId) {
        this.name = name;
        this.value = value;
        this.mvnoId = mvnoId;
    }
    public String getValue() {
        return value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
