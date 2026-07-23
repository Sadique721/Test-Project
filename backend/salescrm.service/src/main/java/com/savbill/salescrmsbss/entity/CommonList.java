package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tblcommonlist")
public class CommonList{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="list_item_id", nullable = false, length = 40)
    private Long id;
    @Column(name="list_text", nullable = false, length = 40)
    private String text;
    @Column(name="list_value", nullable = false, length = 40)
    private String value;
    @Column(name="list_type", nullable = false, length = 40)
    private String type;
    @Column(name="status", nullable = false, length = 1)
    private String status;
    
    @Column(name = "MVNOID", nullable = true, length = 40)
    private Integer mvnoId;
}
