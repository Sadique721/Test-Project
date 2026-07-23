package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveUpdateVendorMessage {


    private Long id;

    private String name;

    private String status;

    private boolean isDeleted;


    private Integer mvnoId;
}
