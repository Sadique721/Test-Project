package com.savbill.radius.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMCUSTOMERTIMEBASEPOLICY")
public class CustomerTimeBasePolicyMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated  Id")
    @Column(name = "details_id", nullable = false)
    private Long id;

    @ApiModelProperty(notes = "This is policy from day")
    @Column(name = "from_day", nullable = false, length = 500)
    private String fromDay;

    @ApiModelProperty(notes = "This is policy to day")
    @Column(name = "to_day", nullable = false, length = 500)
    private String toDay;

    @ApiModelProperty(notes = "This is policy from time")
    @Column(name = "from_time", nullable = false, length = 500)
    private String fromTime;

    @ApiModelProperty(notes = "This is policy to time")
    @Column(name = "to_time", nullable = false, length = 500)
    private String toTime;

    @ApiModelProperty(notes = "This is policy speed")
    @Column(name = "speed", nullable = false, length = 500)
    private String speed;

    @ApiModelProperty(notes = "This is policy access")
    @Column(name = "access", nullable = false, length = 500)
    private Boolean access;

    @ApiModelProperty(notes = "This is time base policy refer in table time base policy column name policyId")
    @Column(name = "custid", nullable = false)
    private Long  customerId;
}
