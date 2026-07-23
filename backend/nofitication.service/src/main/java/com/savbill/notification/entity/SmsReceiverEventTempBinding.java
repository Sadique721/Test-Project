package com.savbill.notification.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLSMSRECEIVEREVENTTEMPBIND")
public class SmsReceiverEventTempBinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated SmsReceiverEventTempBinding Id")
    @Column (name="smsreceivereventtempbindingid", nullable = false)
    private Long smsReceiverEventTempBindingId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "eventid", nullable = false)
    private Event event;

    @Column(name = "staffid")
    private Integer staffId;

    @ApiModelProperty(notes = "This is Mobile no for sms",required = true)
    @Column (name="mobilenumber", length = 100)
    private String mobileNumber;

    @ApiModelProperty(notes = "This is staff username",required = false)
    @Column (name="staffusername", length = 100)
    private String staffUsername;

    @ApiModelProperty(notes = "This is staff full Name",required = false)
    @Column (name="stafffullname", length = 100)
    private String staffFullName;
}