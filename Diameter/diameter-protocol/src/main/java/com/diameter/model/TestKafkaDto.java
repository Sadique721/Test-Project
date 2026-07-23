package com.diameter.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "test_kafka")
public class TestKafkaDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
