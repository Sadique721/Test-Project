package com.savbill.cpm.model.common;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tbltcustomer_caf_reject_file_mapping")
public class CustomerCafImageMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "filename")
    private String filename;

    @Column(name = "uniquename")
    private String uniquename;
}
