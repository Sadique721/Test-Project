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
@Table(name = "tblsmsconfigeventtempbind")
public class SmsConfigEventTempBinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated smsConfigEventTempBinding Id")
    @Column (name="smsconfigeventtempbindid", nullable = false)
    private Long smsConfigEventTempBindingId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "eventid", nullable = false)
    private Event event;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "smsconfigid", nullable = false)
    private SmsConfig smsConfig;
}
