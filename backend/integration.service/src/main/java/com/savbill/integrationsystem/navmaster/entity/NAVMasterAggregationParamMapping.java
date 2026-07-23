package com.savbill.integrationsystem.navmaster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tbltnavmasteraggreagtionparammapping")
@AllArgsConstructor
@NoArgsConstructor
public class NAVMasterAggregationParamMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 40)
    private Integer id;

    @Column(name = "nav_master_id")
    private Integer navMasterId;
    @Column(name = "param_name")
    private String paramName;
}
