package com.savbill.ticketmanagement.core.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class GenericIdModel {
    @Id
    private Long id;
}
