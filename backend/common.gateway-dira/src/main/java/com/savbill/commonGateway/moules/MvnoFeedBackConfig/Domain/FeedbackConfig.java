package com.savbill.commonGateway.moules.MvnoFeedBackConfig.Domain;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.moules.MvnoFeedBackConfig.Enums.ChannelType;
import com.savbill.commonGateway.moules.MvnoFeedBackConfig.Enums.FrequencyType;
import com.savbill.commonGateway.moules.MvnoFeedBackConfig.Enums.RatingDisplayType;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmfeedbackconfig")
@EntityListeners(AuditableListener.class)
public class FeedbackConfig extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="event",nullable = false)
    private String event; // e.g., "Ticket Resolution"

    @Column(name = "is_active",nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel",nullable = false)
    private ChannelType channel;

    @Column(name = "is_madatory",nullable = false)
    private Boolean isMandatory;

    @Column(name="feedback_message",nullable = false)
    private String feedBackMessage;

    @Column(name="rating_scale",nullable = false)
    private Integer ratingScale;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_display_type",nullable = false)
    private RatingDisplayType ratingDisplayType;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency",nullable = true)
    private FrequencyType frequency; // DAILY / WEEKLY / MONTHLY

    @Column(name = "mvnoid",nullable = false)
    private Integer mvnoid;


}
